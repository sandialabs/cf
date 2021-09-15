/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.statushandlers.StatusManager;
import org.hsqldb.cmdline.SqlToolError;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IGlobalApplication;
import gov.sandia.cf.application.IImportApplication;
import gov.sandia.cf.application.IMigrationApplication;
import gov.sandia.cf.application.configuration.ConfigurationFileType;
import gov.sandia.cf.application.configuration.ConfigurationSchema;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.dao.DaoManager;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationCancelledException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.comparator.VersionComparator;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;
import gov.sandia.cf.tools.ZipTools;

/**
 * 
 * Credibility editor to edit .cf files and launch credibility view
 * 
 * @author Didier Verstraete
 *
 */
public class CredibilityEditor extends EditorPart implements Listener {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(CredibilityEditor.class);

	/**
	 * The credibility database folder name
	 */
	private static final String CREDIBILITY_CONF_FILE_NAME = "cf-schema.yml"; //$NON-NLS-1$

	/**
	 * The GUI resource manager
	 */
	private ResourceManager resourceManager;

	/**
	 * The view manager
	 */
	private MainViewManager viewMgr;
	/**
	 * The application layer manager
	 */
	private ApplicationManager appMgr;
	/**
	 * The dao layer manager
	 */
	private DaoManager daoMgr;

	/**
	 * The cf input file
	 */
	private IFile inputFile;

	/**
	 * The cf project path
	 */
	private IPath cfProjectPath;

	/**
	 * Set the state of the cf file (saved or not)
	 */
	private boolean dirty;

	/**
	 * Editor in error
	 */
	private boolean inError;

	/**
	 * the cf cache
	 */
	private CFCache cache;

	/**
	 * The constructor
	 */
	public CredibilityEditor() {
		this.viewMgr = new MainViewManager(this);
		this.daoMgr = new DaoManager();
		this.appMgr = new ApplicationManager(daoMgr);
		this.cache = new CFCache(this);
		this.dirty = false;
		this.inError = false;
	}

	/** {@inheritDoc} */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		this.cache = new CFCache(this);

		// set not dirty at first
		this.dirty = false;

		if (!(input instanceof FileEditorInput)) {
			String message = RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_BADINPUT);
			logger.error(message);
			MessageDialog.openError(getSite().getShell(), RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_TITLE),
					message);

			// stop execution
			return;
		}

		logger.info("Launching plugin: {}, version={}, file={}", //$NON-NLS-1$
				CredibilityFrameworkConstants.CF_PLUGIN_NAME, getVersion(), ((FileEditorInput) input).getFile());

		/**
		 * Initialize variables
		 */
		setSite(site);
		setInput(input);
		setPartName(input.getName());

		// get cf file
		this.inputFile = ((FileEditorInput) input).getFile();

		// the cf project path
		this.cfProjectPath = this.inputFile.getParent().getFullPath();

		// the cf project path
		IFolder cfTmpIFolder = WorkspaceTools.getTempFolder(inputFile);
		IPath cfFolderPath = cfTmpIFolder.getFullPath();

		// the cf working dir
		File cfTmpFolder = WorkspaceTools.toFile(cfFolderPath);

		// test if the user wants to recover previous data or not
		boolean okRecoverPreviousStage = false;

		/**
		 * Start loading process
		 */
		try {
			/*
			 * Check database version and ask for migration if needed
			 */
			checkDatabaseAndMigrate(cfTmpFolder);

			/*
			 * Check CF working directory existence
			 */
			// unzip property to load data in .cf file
			boolean unzipCfFile = true;

			// test if a cf temporary folder is already present or not
			boolean existsCfTmpFolder = (cfTmpFolder != null && cfTmpFolder.exists());
			if (existsCfTmpFolder) {

				// test if the data are recoverable : make a database connection test, execute a
				// query and close the connection
				boolean databaseRecoverable = isDatabaseRecoverable(cfFolderPath);
				boolean deleteExisting = false;

				if (!databaseRecoverable) {
					MessageDialog.openWarning(getSite().getShell(),
							RscTools.getString(RscConst.WRN_CREDIBILITYEDITOR_CFTMPFOLDER_TITLE), RscTools.getString(
									RscConst.WRN_CREDIBILITYEDITOR_CFTMPFOLDER_NOTRECOVERABLE, inputFile.getName()));
					deleteExisting = true;
				} else {
					// ask the user if he wants to recover
					okRecoverPreviousStage = MessageDialog.openQuestion(getSite().getShell(),
							RscTools.getString(RscConst.WRN_CREDIBILITYEDITOR_CFTMPFOLDER_TITLE), RscTools.getString(
									RscConst.WRN_CREDIBILITYEDITOR_CFTMPFOLDER_CONFIRMRECOVER, inputFile.getName()));

					// if the user wants to recover the previous data
					if (okRecoverPreviousStage) {
						unzipCfFile = false;

						// put the editor in a dirty state because .cf file and temporary folder do not
						// have same data
						setDirty(true);
					} else {
						deleteExisting = true;
					}
				}

				if (deleteExisting) {
					FileTools.deleteDirectoryRecursively(cfTmpFolder);
				}
			}

			/*
			 * Unzip the CF file and create the working directory
			 */
			if (unzipCfFile) {
				createWorkingDir(cfTmpFolder);
			}

			/*
			 * Start the database connection, the application managers and the cache
			 */
			// load application layer classes
			appMgr.start();

			// create or load the credibility database
			String projectPath = WorkspaceTools.toOsPath(cfFolderPath);
			logger.debug("Creating database files at: {}", projectPath); //$NON-NLS-1$
			daoMgr.initialize(projectPath);

			/*
			 * Load cache
			 */
			cache.refreshModel();
			cache.refreshGlobalConfiguration();
			cache.refreshUser();

			/*
			 * Database cleaning and migration
			 */
			doDataMigration(cfFolderPath);

			/*
			 * Load the configuration from the CF working directory
			 */
			reloadConfiguration();

			/*
			 * Update the database version with the current plugin version
			 */
			updateDatabaseVersion();

			// the editor needs to be saved?
			if (isDirty() && !okRecoverPreviousStage) {
				// save just after
				// create asynchronous save job (otherwise it may not be saved)
				Display.getCurrent().asyncExec(() -> doSave(new NullProgressMonitor()));
			}

		} catch (CredibilityMigrationException | CredibilityException | SqlToolError | SQLException | IOException
				| URISyntaxException e) {

			// display the error
			Status status = new Status(IStatus.ERROR, CredibilityFrameworkConstants.CF_PLUGIN_NAME,
					RscTools.getString(RscConst.EX_CREDEDITOR_OPENING, getTitle(), e.getMessage()));
			StatusManager.getManager().handle(status, StatusManager.LOG);

			logger.error(e.getMessage(), e);

			MessageDialog.openError(getSite().getShell(), RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_TITLE),
					RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_LOADING) + this.inputFile
							+ RscTools.carriageReturn() + e.getMessage());

			this.inError = true;
		} catch (CredibilityMigrationCancelledException e) {

			// display the error
			Status status = new Status(IStatus.WARNING, CredibilityFrameworkConstants.CF_PLUGIN_NAME,
					RscTools.getString(RscConst.EX_CREDEDITOR_OPENING, getTitle(), e.getMessage()));
			StatusManager.getManager().handle(status, StatusManager.LOG);

			logger.warn(e.getMessage(), e);

			MessageDialog.openWarning(getSite().getShell(), RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_TITLE),
					e.getMessage());

			this.inError = true;
		}
	}

	/**
	 * Check the database consistency and ask for migration if needed.
	 * 
	 * @param cfTmpFolder the database folder
	 * @throws IOException                            if a file IO error occured
	 * @throws SQLException                           if a SQL error occured
	 * @throws CredibilityException                   if a functional error occured
	 * @throws CredibilityMigrationCancelledException if a migration error occured
	 */
	private void checkDatabaseAndMigrate(File cfTmpFolder)
			throws IOException, CredibilityException, SQLException, CredibilityMigrationCancelledException {

		boolean existsCfTmpFolder = (cfTmpFolder != null && cfTmpFolder.exists());
		Path dbCheckCfTmpFolder = null;
		DaoManager daoManagerTmp = new DaoManager();

		try {
			// create temporary folder
			dbCheckCfTmpFolder = Files.createTempDirectory(FileTools.CREDIBILITY_TMP_FOLDER_DEFAULT_PREFIX);

			// copy the database into the temporary folder
			if (existsCfTmpFolder) {
				Files.copy(cfTmpFolder.toPath(), dbCheckCfTmpFolder, StandardCopyOption.COPY_ATTRIBUTES,
						StandardCopyOption.REPLACE_EXISTING);
				// Traverse the file tree and copy each file/directory.
				final Path targetFolder = dbCheckCfTmpFolder;
				try (Stream<Path> walk = Files.walk(cfTmpFolder.toPath())) {
					walk.forEach(sourcePath -> {
						try {
							Path targetPath = targetFolder.resolve(cfTmpFolder.toPath().relativize(sourcePath));
							Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException ex) {
							logger.error("I/O error: %s%n", ex); //$NON-NLS-1$
						}
					});
				}
			} else {
				Files.copy(WorkspaceTools.toFile(this.inputFile.getFullPath()).toPath(),
						Paths.get(dbCheckCfTmpFolder.toFile().getAbsolutePath(), this.inputFile.getName()),
						StandardCopyOption.REPLACE_EXISTING);
				ZipTools.unzip(new File(dbCheckCfTmpFolder.toFile(), this.inputFile.getName()),
						dbCheckCfTmpFolder.toFile());
			}

			// open database connection on temporary database
			daoManagerTmp.start();
			daoManagerTmp.getDbManager().initialize(dbCheckCfTmpFolder.toFile().getAbsolutePath());
			daoManagerTmp.getRepository(IModelRepository.class).setEntityManager(daoManagerTmp.getEntityManager());

			final String pluginVersion = getVersion();
			String databaseVersion = daoManagerTmp.getRepository(IModelRepository.class).getDatabaseVersion();
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
								this.inputFile.getName(), databaseVersion, pluginVersion),
						MessageDialog.WARNING, new String[] { RscTools.getString(RscConst.MSG_BTN_CANCEL),
								RscTools.getString(RscConst.MSG_BTN_CONFIRM) },
						0);
				// Get user selection ([0 => Cancel, 1 => Confirm])
				int result = dialog.open();

				// If the user cancel the migration (stop opening)
				if (1 != Integer.valueOf(result)) {
					throw new CredibilityMigrationCancelledException(
							RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_DBMIGRATION_TXT, this.inputFile.getName(),
									databaseVersion, pluginVersion));
				}
			}
			// (database version > plugin version) -> Impossible to open, update the plugin
			else if (versionComparison < 0) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_VERSION_MISMATCH,
						pluginVersion, databaseVersion, inputFile.getFullPath()));
			}
		} finally {

			// stop the temporary database connection
			if (daoManagerTmp.isStarted()) {
				daoManagerTmp.stop();
			}

			// delete the temporary folder
			if (dbCheckCfTmpFolder != null && dbCheckCfTmpFolder.toFile() != null
					&& dbCheckCfTmpFolder.toFile().exists()) {
				try {
					FileTools.deleteDirectoryRecursively(dbCheckCfTmpFolder.toFile());
				} catch (IOException e) {
					logger.error("Impossible to delete the temporary folder {}", dbCheckCfTmpFolder, e); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * @param cfFolderPath the cf temporary folder to check
	 * @return true if the database is recoverable : 1.database connection succeed,
	 *         2.There is a model in database, otherwise return false
	 */
	private boolean isDatabaseRecoverable(IPath cfFolderPath) {

		// return false if the cf folder is null
		if (cfFolderPath == null) {
			return false;
		}

		// load application layer classes
		appMgr.start();

		try {
			// create or load the credibility database
			String projectPath = WorkspaceTools.toOsPath(cfFolderPath);
			logger.info("create database files at: {}", projectPath); //$NON-NLS-1$

			// initialize the database connection without the database migration
			daoMgr.getDbManager().initialize(projectPath);
			daoMgr.getRepository(IModelRepository.class).setEntityManager(daoMgr.getEntityManager());

			// load or create model
			Model model = appMgr.getService(IGlobalApplication.class).loadModel();

			// if the model does not exist, the database is corrupted
			if (model == null) {
				return false;
			}

		} catch (Exception e) {
			logger.error("Previous database to recover is in a bad state:\n{}", e.getMessage(), e);//$NON-NLS-1$
			return false;
		}

		// stop database connection
		appMgr.stop();

		return true;
	}

	/**
	 * Create the working directory for this cf editor
	 * 
	 * @param cfTmpFolder the temporary folder file
	 * @throws CredibilityException if a functional error occured
	 * @throws IOException          if a file exception occured
	 */
	private void createWorkingDir(File cfTmpFolder) throws CredibilityException, IOException {

		// create the temporary folder
		boolean cfTmpCreated = cfTmpFolder.mkdir();
		if (!cfTmpCreated) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_OPEN_TMPFOLDERCREATIONUNSUCCESSFUL,
					inputFile.getFullPath(), cfTmpFolder));
		}

		// unzip the content of the cf file into the temporary folder
		ZipTools.unzip(WorkspaceTools.toFile(this.inputFile), cfTmpFolder);

		// #FIX: issue #116: the content of the *.cf file can have a folder called
		// .cftmp or not. If the folder is present, delete it
		File cfTmpZippedFolder = new File(cfTmpFolder.getPath(), FileTools.CREDIBILITY_TMP_FOLDER_ZIPPED_NAME);
		if (cfTmpZippedFolder.exists()) {
			FileTools.move(cfTmpFolder, cfTmpZippedFolder);
		}

		logger.info("Unzipping cf file ({}) and creating new temporary folder for cf project at: {}", //$NON-NLS-1$
				this.inputFile.getFullPath(), cfTmpFolder.getPath());
	}

	/**
	 * Reload the configuration from database
	 */
	public void reloadConfiguration() {

		// load PIRT configuration
		getCache().reloadPIRTSpecification();

		// load PCMM configuration
		getCache().reloadPCMMSpecification();

		// load Uncertainty configuration
		getCache().reloadUncertaintySpecification();

		// load System Requirements configuration
		getCache().reloadSystemRequirementSpecification();

		// load PIRT queries
		getCache().reloadPIRTQueries();

		// reload the views
		getViewMgr().reload();
	}

	/**
	 * Update the database with some adjustments.
	 * 
	 * @param cfFolderPath the database folder path
	 * @throws CredibilityException if an error occurred during migration
	 * @throws IOException          if an error occurred during a file parsing
	 */
	private void doDataMigration(IPath cfFolderPath) throws CredibilityException, IOException {

		// Clear multiple assessments for the same user, role and tag
		// (see gitlab issue #199).
		boolean assessmentsCleared = getAppMgr().getService(IMigrationApplication.class)
				.clearMultipleAssessment(getCache().getPCMMSpecification());

		// Clear the evidence path and replace "\\" by "/" to be correctly interpreted
		// (see gitlab issue #262).
		boolean evidencePathCleared = getAppMgr().getService(IMigrationApplication.class).clearEvidencePath();

		// Import the configuration into the database
		// (see gitlab issue #337).
		boolean configurationImported = importYmlSchemaConfiguration(cfFolderPath);

		// save the changes
		if (assessmentsCleared || evidencePathCleared || configurationImported) {
			setDirty(true);
		}
	}

	/**
	 * Import the confiuration into the database (see gitlab issue #337).
	 * 
	 * @return true if the migration was needed
	 * @throws IOException
	 * @throws CredibilityException
	 */
	private boolean importYmlSchemaConfiguration(IPath cfFolderPath) throws CredibilityException, IOException {
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
			getAppMgr().getService(IImportApplication.class).importConfiguration(cache.getModel(), confSchema);
			configurationImported = true;

			// Move the CF schema file to backup folder
			try {

				File cfSchemaFileTargetFolder = WorkspaceTools
						.toFile(cfFolderPath.append(FileTools.CREDIBILITY_BACKUP_FOLDER_NAME));

				// If needed create the backup folder
				if (cfSchemaFileTargetFolder != null && !cfSchemaFileTargetFolder.exists()) {
					Files.createDirectory(cfSchemaFileTargetFolder.toPath());
				}

				// CF schema file destination path
				File cfSchemaFileTarget = WorkspaceTools.toFile(cfFolderPath
						.append(FileTools.CREDIBILITY_BACKUP_FOLDER_NAME).append(CREDIBILITY_CONF_FILE_NAME));

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
	 * @throws CredibilityException if an error occured during version update
	 */
	private void updateDatabaseVersion() throws CredibilityException {

		Model model = cache.getModel();
		String databaseVersion = model.getVersion();
		final String pluginVersion = getVersion();

		// if the database version is not set, it is an old version of CF. We need to
		// set it
		if (databaseVersion == null) {
			databaseVersion = RscTools.empty();
		}
		int versionComparison = new VersionComparator().compare(pluginVersion, databaseVersion);
		if (versionComparison > 0) {
			model.setVersion(getVersion());
			daoMgr.getRepository(IModelRepository.class).update(model);
			cache.refreshModel();

			// save the version and the migration
			setDirty(true);

			logger.info("The cf file ({}) has been migrated to version {}.", //$NON-NLS-1$
					inputFile.getFullPath(), getVersion());

		} else if (versionComparison < 0) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_VERSION_MISMATCH, pluginVersion,
					databaseVersion, inputFile.getName()));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void createPartControl(Composite parent) {

		// set resource manager
		this.resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);

		if (this.inError) {
			// close editor
			WorkspaceTools.closeEditor(this, false);
		} else {
			// create part
			viewMgr.createPartControl(parent);
		}
	}

	/**
	 * Set the new file name and path for the current file, if it modified while the
	 * file is opened
	 * 
	 * @param newFile the new file name
	 */
	public void setRenamedFile(IFile newFile) {

		// the part name
		setPartName(newFile.getName());

		// the input file
		this.inputFile = newFile;
	}

	/** {@inheritDoc} */
	@Override
	public void setPartName(String name) {
		super.setPartName(RscTools.getString(RscConst.MSG_VERSION_TABNAME, name));
	}

	/** {@inheritDoc} */
	@Override
	public void dispose() {

		// stop the managers
		if (viewMgr != null)
			viewMgr.stop();
		if (appMgr != null)
			appMgr.stop();

		if (!inError) {
			// remove cf temporary folder
			IFolder cfTmpIFolder = WorkspaceTools.getTempFolder(inputFile);

			// search file folder
			File tmpFolder = null;
			if (cfTmpIFolder != null) {
				tmpFolder = WorkspaceTools.toFile(cfTmpIFolder.getFullPath());
			}

			// delete recursively
			if (tmpFolder != null && tmpFolder.exists()) {
				try {
					FileTools.deleteDirectoryRecursively(tmpFolder);
				} catch (IOException e) {
					logger.error("Impossible to delete the temporary folder {}", cfTmpIFolder.getFullPath(), e); //$NON-NLS-1$
				}
			}
		}

		// dispose main view manager
		if (viewMgr != null)
			viewMgr.dispose();

		// dispose the resource manager
		if (resourceManager != null)
			resourceManager.dispose();

		// dispose the editor
		super.dispose();
	}

	/**
	 * @return the input file associated to the editor
	 */
	public IFile getInputFile() {
		return this.inputFile;
	}

	/**
	 * @return the credibility project path
	 */
	public IPath getCfProjectPath() {
		return this.cfProjectPath;
	}

	/**
	 * @return the cf cache
	 */
	public CFCache getCache() {
		return cache;
	}

	/**
	 * @return the version of the plugin
	 */
	public static String getVersion() {
		String version = RscTools.empty();

		Bundle cfBundle = CredibilityFrameworkConstants.getBundle();
		if (cfBundle != null && cfBundle.getVersion() != null) {
			version = cfBundle.getVersion().toString();
		}
		return version;
	}

	/**
	 * @return the view manager
	 */
	public MainViewManager getViewMgr() {
		return viewMgr;
	}

	/**
	 * @return the application manager
	 */
	public ApplicationManager getAppMgr() {
		return appMgr;
	}

	/**
	 * @return the resource manager to handle SWT resources binded to the OS (fonts,
	 *         colors, images, cursors...)
	 */
	public ResourceManager getRscMgr() {
		return resourceManager;
	}

	/** {@inheritDoc} */
	@Override
	public void doSave(IProgressMonitor monitor) {

		if (inputFile != null && inputFile.exists()) { // the input file can change if the file has been renamed

			logger.info("{} saved at: {}", inputFile.getFullPath(), DateTools.getDateFormattedDateTime()); //$NON-NLS-1$
			monitor.beginTask(RscTools.getString(RscConst.MSG_EDITOR_SAVE_BEGINTASK, inputFile), 3);

			// save current cf data as a zip file
			String oldCfFileName = inputFile.getName() + FileTools.OLD_FILENAME_SUFFIX
					+ DateTools.getDateFormattedDateTimeHash();
			IPath oldCfFilePath = cfProjectPath.append(oldCfFileName);
			IFolder cfTmpIFolder = WorkspaceTools.getTempFolder(inputFile);

			try {

				// copy current cf file
				monitor.subTask(RscTools.getString(RscConst.MSG_EDITOR_SAVE_COPYTASK));
				inputFile.copy(oldCfFilePath, IResource.FORCE, new NullProgressMonitor());

				// delete the current cf file without workspace method to not trigger delete
				// events that will delete database and close connection
				Files.delete(WorkspaceTools.toFile(inputFile).toPath());
				monitor.worked(1);

				// zip cf content to cf file
				monitor.subTask(RscTools.getString(RscConst.MSG_EDITOR_SAVE_ZIPTASK));
				File tmpFolder = WorkspaceTools.toFile(cfTmpIFolder.getFullPath());
				if (tmpFolder != null && tmpFolder.exists()) {
					File[] listFiles = tmpFolder.listFiles();
					if (listFiles != null && listFiles.length > 0) {
						ZipTools.zipFile(Arrays.asList(listFiles), inputFile.getFullPath());
					} else {
						logger.warn("The cf working directory is empty. There is nothing to zip."); //$NON-NLS-1$
					}
				} else {
					throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_SAVE_TMPFOLDERNULL,
							inputFile.getFullPath(), tmpFolder));
				}
				monitor.worked(2);

				// remove old cf file
				monitor.subTask(RscTools.getString(RscConst.MSG_EDITOR_SAVE_REMOVEOLDTASK));
				WorkspaceTools.getFileInWorkspaceForPath(oldCfFilePath).delete(IResource.FORCE,
						new NullProgressMonitor());
				monitor.worked(3);

				monitor.done();

			} catch (CoreException | IOException | CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(getSite().getShell(), RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_TITLE),
						RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_SAVING) + inputFile
								+ RscTools.carriageReturn() + e.getMessage());
			}

			// set dirty state of the editor
			setDirty(false);
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Set the dirty property with @param dirty
	 * 
	 * @param dirty the dirty state to set
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		firePropertyChange(IEditorPart.PROP_DIRTY);
		getViewMgr().refreshSaveState();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void setFocus() {
		// not used
	}

	/** {@inheritDoc} */
	@Override
	public void handleEvent(Event event) {
		// not used
	}

	/** {@inheritDoc} */
	@Override
	public void doSaveAs() {
		doSave(new NullProgressMonitor());
	}

	/**
	 * @return is CF editor in error
	 */
	public boolean isInError() {
		return inError;
	}

}
