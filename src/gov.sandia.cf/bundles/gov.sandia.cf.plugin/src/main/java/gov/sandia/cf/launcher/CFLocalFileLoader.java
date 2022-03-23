/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.hsqldb.cmdline.SqlToolError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IApplicationManager;
import gov.sandia.cf.application.global.IGlobalApplication;
import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.application.migration.IMigrationApplication;
import gov.sandia.cf.constants.configuration.ConfigurationFileType;
import gov.sandia.cf.exceptions.CredibilityDatabaseInvalidException;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationCancelledException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.comparator.VersionComparator;
import gov.sandia.cf.model.dto.configuration.ConfigurationSchema;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * 
 * The CF local file loader.
 * 
 * @author Didier Verstraete
 *
 */
public class CFLocalFileLoader {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(CFLocalFileLoader.class);

	/**
	 * The Constant CREDIBILITY_CONF_FILE_NAME.
	 * 
	 * @deprecated this schema file is not currently used.
	 */
	@Deprecated
	private static final String CREDIBILITY_CONF_FILE_NAME = "cf-schema.yml"; //$NON-NLS-1$

	/**
	 * Load local file backend.
	 *
	 * @param editor the editor
	 * @throws SqlToolError                           the sql tool error
	 * @throws CredibilityException                   the credibility exception
	 * @throws SQLException                           the SQL exception
	 * @throws IOException                            Signals that an I/O exception
	 *                                                has occurred.
	 * @throws URISyntaxException                     the URI syntax exception
	 * @throws CredibilityMigrationException          the credibility migration
	 *                                                exception
	 * @throws CoreException                          the core exception
	 * @throws CredibilityDatabaseInvalidException    the credibility database
	 *                                                invalid exception
	 * @throws CredibilityMigrationCancelledException the credibility migration
	 *                                                cancelled exception
	 */
	public void load(CredibilityEditor editor) throws SqlToolError, CredibilityException, SQLException, IOException,
			URISyntaxException, CredibilityMigrationException, CoreException, CredibilityDatabaseInvalidException,
			CredibilityMigrationCancelledException {

		if (editor == null) {
			logger.error("Impossible to load a null credibility editor"); //$NON-NLS-1$
			return;
		}

		// initialize application manager
		// TODO replace with Controller manager
		editor.setAppMgr(new ApplicationManager());

		// get cf working dir
		String currentDatabasePathForProject = editor.getCfTmpFolderMgr().getCurrentDatabasePath();

		// ask the user to recover previous data or not
		boolean okRecoverPreviousStage = false;

		/*
		 * 1 - LOAD: check .cftmp folder existence
		 */
		if (editor.getCfTmpFolderMgr().exists() && currentDatabasePathForProject != null) {

			boolean deleteExisting = false;

			try {

				/*
				 * 2 - START: Start the database connection, the application managers and the
				 * cache
				 */
				editor.getAppMgr().start();
				editor.getAppMgr().initializeLocalDB(currentDatabasePathForProject);

				// .cftmp exists, check is recoverable
				checkDatabaseRecoverable(editor.getAppMgr());

				// .cftmp is recoverable, should we use it?
				okRecoverPreviousStage = MessageDialog.openQuestion(editor.getEditorShell(),
						RscTools.getString(RscConst.WRN_CREDIBILITYEDITOR_CFTMPFOLDER_TITLE),
						RscTools.getString(RscConst.WRN_CREDIBILITYEDITOR_CFTMPFOLDER_CONFIRMRECOVER,
								editor.getInputFile().getName()));

				// if the user wants to recover the previous data
				if (okRecoverPreviousStage) {
					// put the editor in a dirty state because .cf file and temporary folder do not
					// have same data
					editor.setDirty(true);
				} else {
					deleteExisting = true;
				}

			} catch (CredibilityDatabaseInvalidException e) {
				deleteExisting = true;
			} finally {
				if (deleteExisting) {
					editor.getAppMgr().stop();
					editor.getCfTmpFolderMgr().deleteTempFolder();
				}
			}
		}

		/*
		 * 1 - LOAD: Unzip the CF file and create the working directory
		 */
		if (!okRecoverPreviousStage) {
			try {
				editor.getCfTmpFolderMgr().createWorkingDir();
			} catch (AccessDeniedException e) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_TMPFOLDER_ACCESSDENIED), e);
			}
		}

		// set derived file
		editor.getCfTmpFolderMgr().getTempIFolder().refreshLocal(0, new NullProgressMonitor());
		editor.getCfTmpFolderMgr().getTempIFolder().setDerived(true, new NullProgressMonitor());

		/*
		 * 2 - START: Start the database connection, the application managers and cache
		 */
		if (!okRecoverPreviousStage) {
			// load application layer classes
			editor.getAppMgr().start();

			try {

				// create or load the credibility database
				logger.debug("Creating database files at: {}", editor.getCfTmpFolderMgr().getTempFolderPath()); //$NON-NLS-1$
				editor.getAppMgr().initializeLocalDB(editor.getCfTmpFolderMgr().getCurrentDatabasePath());

				// check is recoverable
				checkDatabaseRecoverable(editor.getAppMgr());

			} catch (CredibilityDatabaseInvalidException e) {
				editor.getAppMgr().stop();
				editor.getCfTmpFolderMgr().deleteTempFolder();
				throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_CF_FILE_CORRUPTED), e);
			}
		}

		/*
		 * 3 - Check version and ask for migration
		 */
		try {
			checkDatabaseVersion(editor);
		} catch (CredibilityException | CredibilityMigrationCancelledException e) {

			// if an error occurs, stop the app
			editor.getAppMgr().stop();

			// and delete database files if they've been created now
			if (!okRecoverPreviousStage) {
				editor.getCfTmpFolderMgr().deleteTempFolder();
			}
			throw e;
		}

		/*
		 * 4 - Load cache
		 */
		editor.getCache().refreshModel();
		editor.getCache().refreshGlobalConfiguration();
		editor.getCache().refreshUser();

		/*
		 * 5 - Database cleaning and migration
		 */
		doDataMigration(editor, editor.getCfTmpFolderMgr().getTempIPath());

		/*
		 * 6 - Load the configuration from the CF working directory
		 */
		editor.reloadConfiguration();

		/*
		 * 7 - Update the database version with the current plugin version
		 */
		updateDatabaseVersion(editor);

		// the editor needs to be saved?
		// FIX to save the migration
		if (editor.isDirty() && !okRecoverPreviousStage) {
			// save just after
			// create asynchronous save job (otherwise it may not be saved)
			Display.getCurrent().asyncExec(() -> editor.doSave(new NullProgressMonitor()));
		}
	}

	/**
	 * Check database folder.
	 *
	 * @param editor the editor
	 * @throws CredibilityException                   the credibility exception
	 * @throws CredibilityMigrationCancelledException the credibility migration
	 *                                                cancelled exception
	 */
	private void checkDatabaseVersion(CredibilityEditor editor)
			throws CredibilityException, CredibilityMigrationCancelledException {

		final String pluginVersion = CredibilityEditor.getVersion();
		String databaseVersion = editor.getAppMgr().getService(IGlobalApplication.class).getDatabaseVersion();

		// if the database version is not set, it is an old version of CF. We need to
		// set it
		if (databaseVersion == null) {
			databaseVersion = RscTools.empty();
		}
		if (pluginVersion == null || pluginVersion.isEmpty()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_PLUGIN_VERSION_EMPTY));
		}
		int versionComparison = new VersionComparator().compare(pluginVersion, databaseVersion);

		// Check the plugin version vs the database version (last plugin version):
		// (database version < plugin version) -> Needs migration
		if (versionComparison > 0) {

			// Display confirm dialog before doing migration
			Display display = Display.getDefault();
			Shell activeShell = display.getActiveShell();
			MessageDialog dialog = new MessageDialog(activeShell,
					RscTools.getString(RscConst.WRN_CREDIBILITYEDITOR_DBMIGRATION_CONFIRM_TITLE), null,
					RscTools.getString(RscConst.WRN_CREDIBILITYEDITOR_DBMIGRATION_CONFIRM_TXT,
							editor.getInputFile().getName(), databaseVersion, pluginVersion),
					MessageDialog.WARNING, new String[] { RscTools.getString(RscConst.MSG_BTN_CANCEL),
							RscTools.getString(RscConst.MSG_BTN_CONFIRM) },
					0);
			// Get user selection ([0 => Cancel, 1 => Confirm])
			int result = dialog.open();

			// If the user cancel the migration (stop opening)
			if (1 != Integer.valueOf(result)) {
				throw new CredibilityMigrationCancelledException(
						RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_DBMIGRATION_TXT,
								editor.getInputFile().getName(), databaseVersion, pluginVersion));
			}
		}
		// (database version > plugin version) -> Impossible to open, update the plugin
		else if (versionComparison < 0) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_VERSION_MISMATCH, pluginVersion,
					databaseVersion, editor.getInputFile().getFullPath()));
		}
	}

	/**
	 * Checks if is database recoverable.
	 *
	 * @param appMgrTmp the app mgr tmp
	 * @throws CredibilityDatabaseInvalidException the credibility database invalid
	 *                                             exception
	 */
	private void checkDatabaseRecoverable(IApplicationManager appMgrTmp) throws CredibilityDatabaseInvalidException {

		if (appMgrTmp == null || !appMgrTmp.isStarted()) {
			logger.warn("Application manager is null or not started"); //$NON-NLS-1$
			return;
		}

		// load or create model
		Model model = null;
		try {
			model = appMgrTmp.getService(IGlobalApplication.class).loadModel();
		} catch (CredibilityException e) {
			throw new CredibilityDatabaseInvalidException(
					RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_DB_NOT_RECOVERABLE), e);
		}

		// if the model does not exist, the database is corrupted
		if (model == null || model.getId() == null) {
			throw new CredibilityDatabaseInvalidException(
					RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_DB_NOT_RECOVERABLE));
		}
	}

	/**
	 * Update the database with some adjustments.
	 *
	 * @param editor       the editor
	 * @param cfFolderPath the database folder path
	 * @throws CredibilityException if an error occurred during migration
	 * @throws IOException          if an error occurred during a file parsing
	 */
	private void doDataMigration(CredibilityEditor editor, IPath cfFolderPath)
			throws CredibilityException, IOException {

		// Clear multiple assessments for the same user, role and tag
		// (see gitlab issue #199).
		boolean assessmentsCleared = editor.getAppMgr().getService(IMigrationApplication.class)
				.clearMultipleAssessment(editor.getCache().getPCMMSpecification());

		// Clear the evidence path and replace "\\" by "/" to be correctly interpreted
		// (see gitlab issue #262).
		boolean evidencePathCleared = editor.getAppMgr().getService(IMigrationApplication.class).clearEvidencePath();

		// Import the configuration into the database
		// (see gitlab issue #337).
		boolean configurationImported = importYmlSchemaConfiguration(editor, cfFolderPath);

		// save the changes
		if (assessmentsCleared || evidencePathCleared || configurationImported) {
			editor.setDirty(true);
		}
	}

	/**
	 * Import the confiuration into the database (see gitlab issue #337).
	 * 
	 * configuration file is not created in CF version > 0.5.0. Import is only
	 * necessary for old versions.
	 *
	 * @param editor       the editor
	 * @param cfFolderPath the cf folder path
	 * @return true if the migration was needed
	 * @throws CredibilityException the credibility exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	private boolean importYmlSchemaConfiguration(CredibilityEditor editor, IPath cfFolderPath)
			throws CredibilityException, IOException {
		// Initialize
		boolean configurationImported = false;

		// The CF schema file to import and delete
		File cfSchemaFile = WorkspaceTools.toFile(cfFolderPath.append(CREDIBILITY_CONF_FILE_NAME));
		if (cfSchemaFile != null && cfSchemaFile.exists()) {

			// Prepare data files
			ConfigurationSchema confSchema = new ConfigurationSchema();
			confSchema.put(ConfigurationFileType.PIRT, cfSchemaFile);
			confSchema.put(ConfigurationFileType.QOIPLANNING, cfSchemaFile);
			confSchema.put(ConfigurationFileType.PCMM, cfSchemaFile);
			confSchema.put(ConfigurationFileType.UNCERTAINTY, cfSchemaFile);
			confSchema.put(ConfigurationFileType.SYSTEM_REQUIREMENT, cfSchemaFile);

			// Import configuration from the CF schema file
			editor.getAppMgr().getService(IImportApplication.class).importConfiguration(editor.getCache().getModel(),
					editor.getCache().getUser(), confSchema);
			configurationImported = true;

			// Move the CF schema file to backup folder
			try {

				File cfSchemaFileTargetFolder = WorkspaceTools
						.toFile(cfFolderPath.append(CFTmpFolderManager.CREDIBILITY_BACKUP_FOLDER_NAME));

				// If needed create the backup folder
				if (cfSchemaFileTargetFolder != null && !cfSchemaFileTargetFolder.exists()) {
					Files.createDirectory(cfSchemaFileTargetFolder.toPath());
				}

				// CF schema file destination path
				File cfSchemaFileTarget = WorkspaceTools.toFile(cfFolderPath
						.append(CFTmpFolderManager.CREDIBILITY_BACKUP_FOLDER_NAME).append(CREDIBILITY_CONF_FILE_NAME));

				// Move CF schema file
				if (cfSchemaFileTargetFolder != null && cfSchemaFileTargetFolder.exists()) {
					logger.info("Moving cf schema file to {}", cfSchemaFileTarget.getPath()); //$NON-NLS-1$
					Files.move(cfSchemaFile.toPath(), cfSchemaFileTarget.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (IOException e) {
				logger.error("An error occured during cf schema file move: {}", e.getMessage(), e); //$NON-NLS-1$
			}
		}

		return configurationImported;
	}

	/**
	 * Update the database version with the plugin version if the user gives
	 * approval.
	 *
	 * @param editor the editor
	 * @throws CredibilityException if an error occured during version update
	 */
	private void updateDatabaseVersion(CredibilityEditor editor) throws CredibilityException {

		Model model = editor.getCache().getModel();
		String databaseVersion = model.getVersion();
		final String pluginVersion = CredibilityEditor.getVersion();

		// if the database version is not set, it is an old version of CF. We need to
		// set it
		if (databaseVersion == null) {
			databaseVersion = RscTools.empty();
		}
		int versionComparison = new VersionComparator().compare(pluginVersion, databaseVersion);
		if (versionComparison > 0) {
			model.setVersion(CredibilityEditor.getVersion());
			editor.getAppMgr().getService(IGlobalApplication.class).updateModel(model);
			editor.getCache().refreshModel();

			// save the version and the migration
			editor.setDirty(true);

			logger.info("The cf file ({}) has been migrated to version {}.", //$NON-NLS-1$
					editor.getInputFile().getFullPath(), CredibilityEditor.getVersion());

		} else if (versionComparison < 0) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_VERSION_MISMATCH, pluginVersion,
					databaseVersion, editor.getInputFile().getName()));
		}
	}

}
