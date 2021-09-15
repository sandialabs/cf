/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.hsqldb.cmdline.SqlToolError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IGlobalApplication;
import gov.sandia.cf.application.IImportApplication;
import gov.sandia.cf.application.configuration.ConfigurationFileType;
import gov.sandia.cf.application.configuration.ConfigurationSchema;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.dao.DaoManager;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;
import gov.sandia.cf.tools.ZipTools;

/**
 * The newWizard extensions point to create a new credibility process from the
 * project explorer
 * 
 * @author Didier Verstraete
 *
 */
public class NewCredibilityProcessWizard extends Wizard implements INewWizard {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(NewCredibilityProcessWizard.class);

	/**
	 * the page to select parent project and create credibility new files
	 */
	private CredibilityProcessNewFileWizardPage pageNewCredibilityFile;

	/**
	 * the page to select configuration file
	 */
	private CredibilityProcessSetupWizardPage pageSetup;

	/**
	 * The constructor
	 */
	public NewCredibilityProcessWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		addPage(pageNewCredibilityFile);
		addPage(pageSetup);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getWindowTitle() {
		return RscTools.getString(RscConst.MSG_NEWCFPROCESSWIZARD_WINDOWTITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		pageNewCredibilityFile = new CredibilityProcessNewFileWizardPage(selection);
		pageSetup = new CredibilityProcessSetupWizardPage(this);
	}

	/**
	 * @return the CredibilityProcessNewFileWizardPage page
	 */
	public CredibilityProcessNewFileWizardPage getPageNewCredibilityFile() {
		return pageNewCredibilityFile;
	}

	/**
	 * @return the CredibilityProcessSetupWizardPage page
	 */
	public CredibilityProcessSetupWizardPage getPageSetup() {
		return pageSetup;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {

		/**
		 * Define variables
		 */
		// new credibility file
		IFile cfFile = WorkspaceTools.getFileInWorkspaceForPath(
				pageNewCredibilityFile.getContainerFullPath().append(pageNewCredibilityFile.getSelectedFilename()));

		try {

			// new credibility temp folder
			IFolder cfTmpIFolder = WorkspaceTools.getTempFolder(cfFile);
			if (cfTmpIFolder == null) {
				logger.error(RscTools.getString(RscConst.ERR_NEWCFPROCESSWIZARD_WORKDIR_MISSING));
				MessageDialog.openError(getShell(), RscTools.getString(RscConst.ERROR_TITLE),
						RscTools.getString(RscConst.ERR_NEWCFPROCESSWIZARD_WORKDIR_MISSING));
				return false;
			}

			// Check error messages
			if (pageNewCredibilityFile.getErrorMessage() != null
					&& !pageNewCredibilityFile.getErrorMessage().isEmpty()) {
				return false;
			}

			// Get Schema files
			ConfigurationSchema confSchema = pageSetup.getConfigurationSchema();

			// create temporary folder (delete it if it exists)
			createAndEraseWorkingDir(cfTmpIFolder);

			// store credibility files in preferences
			PrefTools.setPreference(PrefTools.QOIPLANNING_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.QOIPLANNING));
			PrefTools.setPreference(PrefTools.PIRT_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.PIRT));
			PrefTools.setPreference(PrefTools.PCMM_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.PCMM));
			PrefTools.setPreference(PrefTools.UNCERTAINTY_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.UNCERTAINTY));
			PrefTools.setPreference(PrefTools.SYSTEM_REQUIREMENT_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.SYSTEM_REQUIREMENT));
			PrefTools.setPreference(PrefTools.DECISION_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.DECISION));

			// import the configuration in database
			importConfiguration(cfTmpIFolder, confSchema);

			/**
			 * Zip credibility temporary folder into credibility file (.cf)
			 */
			createCFFile(cfFile, cfTmpIFolder);

			/**
			 * Delete temporary folder
			 */
			FileTools.deleteDirectoryRecursively(WorkspaceTools.toFile(cfTmpIFolder.getFullPath()));

			/**
			 * generate evidence folder structure if the user has checked the option
			 */
			generateEvidenceFolderStructure();

			/**
			 * open credibility editor
			 */
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
					new FileEditorInput(ResourcesPlugin.getWorkspace().getRoot().getFile(cfFile.getFullPath())),
					CredibilityFrameworkConstants.CREDIBILITY_EDITOR_ID);

		} catch (CredibilityException | URISyntaxException | IOException | CoreException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_NEWCFPROCESSWIZARD_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());

			// if an error occured delete the cf file
			if (cfFile != null && cfFile.exists()) {
				try {
					cfFile.delete(true, null);
				} catch (CoreException e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
			return false;
		}

		return true;
	}

	/**
	 * Create CF temporary working directory. If it already exists, erase it first.
	 * 
	 * @param cfTmpIFolder
	 * @throws CoreException
	 * @throws IOException
	 */
	private void createAndEraseWorkingDir(IFolder cfTmpIFolder) throws CoreException, IOException {

		// Delete temporary working directory
		if (cfTmpIFolder.exists()) { // delete it if it exists in workspace
			WorkspaceTools.deleteDirectoryRecursively(cfTmpIFolder);
		}
		File cfTmpFolder = WorkspaceTools.toFile(cfTmpIFolder);
		if (cfTmpFolder.exists()) { // delete it if it always exists on filesystem
			FileTools.deleteDirectoryRecursively(cfTmpFolder);
		}

		// Create new temporary folder
		cfTmpIFolder.create(true, true, new NullProgressMonitor());

	}

	/**
	 * Import the configuration from the CF schema file into the working dir
	 * database.
	 * 
	 * @param cfTmpIFolder the temporary folder containing the database
	 * @param confSchema   the configuration schema class
	 * @throws CredibilityException if a database error occured.
	 * @throws IOException          if reading the schema file triggers an exception
	 */
	private void importConfiguration(IFolder cfTmpIFolder, ConfigurationSchema confSchema)
			throws CredibilityException, IOException {

		if (cfTmpIFolder == null || !cfTmpIFolder.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.ERR_NEWCFPROCESSWIZARD_WORKDIR_MISSING));
		}

		// load managers
		DaoManager daoMgr = new DaoManager();
		ApplicationManager appMgr = new ApplicationManager(daoMgr);
		appMgr.start();

		try {

			// create or load the credibility database
			String projectPath = WorkspaceTools.toOsPath(cfTmpIFolder.getFullPath());
			daoMgr.initialize(projectPath);

			// Import CF Model in database
			logger.info("Importing CF Model..."); //$NON-NLS-1$

			Model model = appMgr.getService(IGlobalApplication.class).importModel(confSchema);

			// Import specification in database
			appMgr.getService(IImportApplication.class).importConfiguration(model, confSchema);

		} catch (CredibilityException | SqlToolError | SQLException | URISyntaxException
				| CredibilityMigrationException e) {
			logger.error(e.getMessage(), e);
		} finally {
			// stop managers
			appMgr.stop();
		}
	}

	/**
	 * Create the cf file as a zip of the working directory cfTmpIFolder into
	 * cfFile.
	 * 
	 * @param cfFile
	 * @param cfTmpIFolder
	 * @throws CoreException
	 * @throws IOException
	 * @throws CredibilityException
	 */
	private void createCFFile(IFile cfFile, IFolder cfTmpIFolder)
			throws CoreException, IOException, CredibilityException {
		// Check CF file not null
		if (cfFile == null) {
			throw new CredibilityException(RscTools.getString(RscConst.ERR_NEWCFPROCESSWIZARD_CFFILE_MISSING));
		}

		// Check CF temporary folder not null
		if (cfTmpIFolder == null || !cfTmpIFolder.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.ERR_NEWCFPROCESSWIZARD_WORKDIR_MISSING));
		}

		// Logger
		logger.info("Creating credibility file at: {}", cfFile.getFullPath()); //$NON-NLS-1$

		// Create credibility file (.cf)
		cfFile.create(new ByteArrayInputStream(new byte[0]), IResource.FORCE, null);

		// Create the archive (.zip)
		File tmpFolder = WorkspaceTools.toFile(cfTmpIFolder.getFullPath());
		if (tmpFolder != null && tmpFolder.exists()) {
			File[] listFiles = tmpFolder.listFiles();
			if (listFiles != null && listFiles.length > 0) {
				ZipTools.zipFile(Arrays.asList(listFiles), cfFile.getFullPath());
			} else {
				logger.warn("The cf working directory is empty. There is nothing to zip."); //$NON-NLS-1$
			}
		} else {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_CREDEDITOR_SAVE_TMPFOLDERNULL, cfFile.getFullPath(), tmpFolder));
		}
	}

	/**
	 * Generate the folder structure if the user asked for it.
	 * 
	 * @throws CredibilityException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private void generateEvidenceFolderStructure() throws CredibilityException, URISyntaxException, IOException {
		// If generate folder check box is checked
		if (pageSetup.getGenerateFolderStructure()) {

			// Initialize path
			IPath containerFullPath = pageNewCredibilityFile.getContainerFullPath();

			// Check is complete
			if (!pageSetup.isPageComplete()) {
				throw new CredibilityException(RscTools.getString(RscConst.ERR_EVIDFOLDERSTRUCT_SETUP_INCOMPLETE));
			}

			// Check path
			if (containerFullPath == null || containerFullPath.isEmpty()) {
				throw new CredibilityException(RscTools.getString(RscConst.ERR_EVIDFOLDERSTRUCT_CONTAINER_NULL));
			}

			// Generate folder
			String strPath = WorkspaceTools.getStaticFilePath(FileTools.FILE_CREDIBILITY_EVIDENCE_FOLDER_STRUCTURE);
			File evidenceStructureFile = new File(strPath);
			WorkspaceTools.createFolderStructure(containerFullPath, evidenceStructureFile);
		}
	}

}
