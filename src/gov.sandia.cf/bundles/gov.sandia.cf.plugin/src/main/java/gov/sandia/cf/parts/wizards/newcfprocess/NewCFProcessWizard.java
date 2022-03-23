/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.hsqldb.cmdline.SqlToolError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.global.IGlobalApplication;
import gov.sandia.cf.application.global.IUserApplication;
import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.constants.configuration.ConfigurationFileType;
import gov.sandia.cf.exceptions.CredibilityDatabaseInvalidException;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.launcher.CFBackendConnectionType;
import gov.sandia.cf.launcher.CFClientSetup;
import gov.sandia.cf.launcher.CFClientSetupFactory;
import gov.sandia.cf.launcher.CFTmpFolderManager;
import gov.sandia.cf.launcher.CredibilityEditor;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.ConfigurationSchema;
import gov.sandia.cf.parts.services.setup.YmlWriterClientSetup;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.CFVariableResolver;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;
import gov.sandia.cf.tools.ZipTools;
import gov.sandia.cf.web.services.IWebClientManager;
import gov.sandia.cf.web.services.WebClientManager;
import gov.sandia.cf.web.services.global.IModelWebClient;

/**
 * The newWizard extensions point to create a new credibility process from the
 * project explorer
 * 
 * @author Didier Verstraete
 *
 */
public class NewCFProcessWizard extends Wizard implements INewWizard {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(NewCFProcessWizard.class);

	/** The resource manager. */
	private ResourceManager resourceManager;

	/** The web client manager. */
	private IWebClientManager webClientManager;

	/** The page to select parent project and create credibility new files. */
	private INewCFProcessFileSelectionPage pageNewCredibilityFile;

	/** The page backend selection. */
	private INewCFProcessBackendSelectionPage pageBackendSelection;

	/** The page to select configuration file. */
	private INewCFProcessLocalSetupPage pageLocalSetup;

	/** The page to select configuration file. */
	private NewCFProcessLocalSetupScanPage pageLocalSetupScan;

	/** The page to select configuration file. */
	private NewCFProcessLocalSetupAdvancedPage pageLocalSetupAdvanced;

	/** The page web setup. */
	private INewCFProcessWebSetupPage pageWebSetup;

	/** The page web project type. */
	private INewCFProcessWebProjectTypePage pageWebProjectType;

	/** The page web project new setup. */
	private INewCFProcessWebProjectNewSetupPage pageWebProjectNewSetup;

	/** The page web project existing setup. */
	private INewCFProcessWebProjectExistingSetupPage pageWebProjectExistingSetup;

	/**
	 * Instantiates a new new credibility process wizard.
	 */
	public NewCFProcessWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/** {@inheritDoc} */
	@Override
	public void addPages() {
		addPage(pageNewCredibilityFile);
		addPage(pageBackendSelection);
		addPage(pageWebSetup);
		addPage(pageWebProjectType);
		addPage(pageWebProjectNewSetup);
		addPage(pageWebProjectExistingSetup);
		addPage(pageLocalSetupScan);
		addPage(pageLocalSetupAdvanced);
	}

	/** {@inheritDoc} */
	@Override
	public String getWindowTitle() {
		return RscTools.getString(RscConst.MSG_NEWCFPROCESS_WIZARD_WINDOWTITLE);
	}

	/** {@inheritDoc} */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		pageNewCredibilityFile = new NewCFProcessFileSelectionPage(selection);
		pageBackendSelection = new NewCFProcessBackendSelectionPage(this);
		pageWebSetup = new NewCFProcessWebSetupPage(this);
		pageWebProjectType = new NewCFProcessWebProjectTypePage(this);
		pageWebProjectNewSetup = new NewCFProcessWebProjectNewSetupPage(this);
		pageWebProjectExistingSetup = new NewCFProcessWebProjectExistingSetupPage(this);
		pageLocalSetupScan = new NewCFProcessLocalSetupScanPage(this);
		pageLocalSetupAdvanced = new NewCFProcessLocalSetupAdvancedPage(this);

		// set the default setup page
		pageLocalSetup = pageLocalSetupScan;

		// set resource manager
		this.resourceManager = new LocalResourceManager(JFaceResources.getResources());

		// init web client manager
		this.webClientManager = new WebClientManager();
		this.webClientManager.start();
	}

	/**
	 * Open default setup page.
	 */
	public void openDefaultSetupPage() {
		pageLocalSetup = pageLocalSetupScan;
		getContainer().showPage(pageLocalSetupScan);
	}

	/**
	 * Open advanced setup page.
	 */
	public void openAdvancedSetupPage() {
		pageLocalSetup = pageLocalSetupAdvanced;
		getContainer().showPage(pageLocalSetupAdvanced);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == null) {

			return pageNewCredibilityFile;
		} else if (page.equals(pageNewCredibilityFile)) {

			// if the concurrency support option is activated => open backend selection page
			if (PrefTools.getPreferenceBoolean(PrefTools.DEVOPTS_CONCURRENCY_SUPPORT_KEY).booleanValue()) {
				return pageBackendSelection;
			} else { // => otherwise open local setup
				return pageLocalSetup;
			}
		} else if (page.equals(pageBackendSelection)) {

			// if the user selected web => open the web setup
			if (pageBackendSelection.isWeb()) {
				return pageWebSetup;
			} else { // => otherwise open the local setup
				return pageLocalSetup;
			}
		} else if (page.equals(pageWebSetup)) {

			return pageWebProjectType;

		} else if (page.equals(pageWebProjectType)) {

			// if the user selected new web project => open the new web setup
			if (pageWebProjectType.isNewProject()) {
				return pageWebProjectNewSetup;
			}

			// if the user selected existing web project => open the web project selector
			if (pageWebProjectType.isExistingProject()) {
				return pageWebProjectExistingSetup;
			}

			return pageWebProjectType;

		} else {

			return null;

		}
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
			IFolder cfTmpIFolder = CFTmpFolderManager.getTempFolder(cfFile);
			if (cfTmpIFolder == null) {
				logger.error(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WIZARD_WORKDIR_MISSING));
				MessageDialog.openError(getShell(), RscTools.getString(RscConst.ERROR_TITLE),
						RscTools.getString(RscConst.ERR_NEWCFPROCESS_WIZARD_WORKDIR_MISSING));
				return false;
			}

			// Check error messages
			if (pageNewCredibilityFile.getErrorMessage() != null
					&& !pageNewCredibilityFile.getErrorMessage().isEmpty()) {
				return false;
			}

			// create temporary folder (delete it if it exists)
			createAndEraseWorkingDir(cfTmpIFolder);

			// store credibility files preferences
			storePreferences();

			// if the user selected web => create web setup
			if (pageBackendSelection.isWeb()) {

				// create setup.yml file
				createWebProject(cfTmpIFolder);

			} else { // => otherwise create local setup

				// import the configuration in database
				createLocalFileProject(cfTmpIFolder);
			}

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
					RscTools.getString(RscConst.ERR_NEWCFPROCESS_WIZARD_ERROR_OCCURED)
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
	 * Store the last selected paths peferences
	 */
	private void storePreferences() {

		if (pageBackendSelection.isWeb()) {
			storeWebPreferences();
		} else {
			storeLocalPreferences();
		}
	}

	/**
	 * Store web preferences.
	 */
	private void storeWebPreferences() {

		// set web server url
		if (!StringUtils.isBlank(pageWebSetup.getServerURL()))
			PrefTools.setPreference(PrefTools.WEB_SERVER_URL, pageWebSetup.getServerURL());
	}

	/**
	 * Store local preferences.
	 */
	private void storeLocalPreferences() {

		// configuration folder
		if (pageLocalSetup.getConfigurationFolderDefaultPath() != null)
			PrefTools.setPreference(PrefTools.CONF_SCHEMA_FOLDER_LAST_PATH_KEY,
					pageLocalSetup.getConfigurationFolderDefaultPath());

		// get configuration schemas
		ConfigurationSchema confSchema = pageLocalSetup.getConfigurationSchema();

		if (confSchema.get(ConfigurationFileType.QOIPLANNING) != null)
			PrefTools.setPreference(PrefTools.QOIPLANNING_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.QOIPLANNING));
		if (confSchema.get(ConfigurationFileType.PIRT) != null)
			PrefTools.setPreference(PrefTools.PIRT_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.PIRT));
		if (confSchema.get(ConfigurationFileType.PCMM) != null)
			PrefTools.setPreference(PrefTools.PCMM_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.PCMM));
		if (confSchema.get(ConfigurationFileType.UNCERTAINTY) != null)
			PrefTools.setPreference(PrefTools.UNCERTAINTY_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.UNCERTAINTY));
		if (confSchema.get(ConfigurationFileType.SYSTEM_REQUIREMENT) != null)
			PrefTools.setPreference(PrefTools.SYSTEM_REQUIREMENT_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.SYSTEM_REQUIREMENT));
		if (confSchema.get(ConfigurationFileType.DECISION) != null)
			PrefTools.setPreference(PrefTools.DECISION_SCHEMA_FILE_LAST_PATH_KEY,
					confSchema.get(ConfigurationFileType.DECISION));
	}

	/**
	 * Creates the web setup project.
	 *
	 * @param cfTmpIFolder the cf tmp I folder
	 * @throws CredibilityException the credibility exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	private void createWebProject(IFolder cfTmpIFolder) throws CredibilityException, IOException {

		// check temp folder
		File cfTmpFolder = WorkspaceTools.toFile(cfTmpIFolder);
		if (cfTmpIFolder == null || !cfTmpIFolder.exists() || cfTmpFolder == null) {
			throw new CredibilityException(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WIZARD_WORKDIR_MISSING));
		}

		// load setup
		CFClientSetup cfClientSetup = CFClientSetupFactory.get(CFBackendConnectionType.WEB);
		cfClientSetup.setWebServerURL(pageWebSetup.getServerURL());

		// load model
		Model model = null;
		if (pageWebProjectType.isNewProject()) {

			Model toCreate = pageWebProjectNewSetup.getModel();
			if (toCreate == null) {
				toCreate = new Model();
			}

			// create project
			model = getWebClientManager().getService(IModelWebClient.class).create(toCreate);

		} else if (pageWebProjectType.isExistingProject()) {
			model = pageWebProjectExistingSetup.getSelectedModel();
		} else {
			throw new CredibilityException(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WIZARD_WEB_PROJECT_TYPE_NULL));
		}

		// set model id
		if (model == null || model.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WIZARD_WEB_MODEL_NULL));
		} else {
			cfClientSetup.setModelId(model.getId());
		}

		// create setup file
		File setupFile = new File(cfTmpFolder, CredibilityEditor.CREDIBILITY_SETUP_FILE_NAME);
		YmlWriterClientSetup clientWriter = new YmlWriterClientSetup();
		clientWriter.writeSchema(setupFile, cfClientSetup, false, false);

	}

	/**
	 * Import the configuration from the CF schema file into the working dir
	 * database.
	 * 
	 * @param cfTmpIFolder the temporary folder containing the database
	 * @throws CredibilityException if a database error occured.
	 * @throws IOException          if reading the schema file triggers an exception
	 */
	private void createLocalFileProject(IFolder cfTmpIFolder) throws CredibilityException, IOException {

		if (cfTmpIFolder == null || !cfTmpIFolder.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WIZARD_WORKDIR_MISSING));
		}

		// get configuration schemas
		ConfigurationSchema confSchema = pageLocalSetup.getConfigurationSchema();

		// load managers
		ApplicationManager appMgr = new ApplicationManager();
		appMgr.start();

		try {

			// create or load the credibility database
			String projectPath = WorkspaceTools.toOsPath(cfTmpIFolder.getFullPath());
			appMgr.initializeLocalDB(CFTmpFolderManager.getDefaultDatabasePathForProject(projectPath));

			// Import CF Model in database
			logger.info("Importing CF Model..."); //$NON-NLS-1$

			Model model = appMgr.getService(IGlobalApplication.class).importModel(confSchema);

			// Import current user
			String userID = CFVariableResolver.resolve(CFVariable.USER_NAME);
			logger.info("Importing User {}...", userID); //$NON-NLS-1$
			User user = appMgr.getService(IUserApplication.class).getUserByUserID(userID);

			// Import specification in database
			appMgr.getService(IImportApplication.class).importConfiguration(model, user, confSchema);

		} catch (CredibilityException | SqlToolError | SQLException | URISyntaxException | CredibilityMigrationException
				| CredibilityDatabaseInvalidException e) {
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
			throw new CredibilityException(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WIZARD_CFFILE_MISSING));
		}

		// Check CF temporary folder not null
		if (cfTmpIFolder == null || !cfTmpIFolder.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WIZARD_WORKDIR_MISSING));
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

		boolean generateFolderStructure = false;
		boolean isPageComplete = false;

		// if the user selected web
		if (pageBackendSelection.isWeb()) {
			generateFolderStructure = pageWebSetup.getGenerateFolderStructure();
			isPageComplete = pageWebSetup.isPageComplete();
		} else { // => otherwise load local setup
			generateFolderStructure = pageLocalSetup.getGenerateFolderStructure();
			isPageComplete = pageLocalSetup.isPageComplete();
		}

		// If generate folder check box is checked
		if (generateFolderStructure) {

			// Initialize path
			IPath containerFullPath = pageNewCredibilityFile.getContainerFullPath();

			// Check is complete
			if (!isPageComplete) {
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

	@Override
	public void dispose() {
		super.dispose();
		this.resourceManager.dispose();
		this.webClientManager.stop();
	}

	@Override
	public boolean canFinish() {
		// override to enable page completion even if the alternate setup pages are not
		// complete
		boolean isComplete = pageNewCredibilityFile.isPageComplete();

		// if the concurrency support is enabled
		if (PrefTools.getPreferenceBoolean(PrefTools.DEVOPTS_CONCURRENCY_SUPPORT_KEY).booleanValue()) {

			// get backend type selection complete
			isComplete &= pageBackendSelection.isPageComplete();

			// and the selected backend type page
			if (pageBackendSelection.isLocalFile()) {
				isComplete &= pageLocalSetup.isPageComplete();
			}

			if (pageBackendSelection.isWeb()) {
				isComplete &= pageWebSetup.isPageComplete();

				if (pageWebProjectType.isExistingProject()) {
					isComplete &= pageWebProjectExistingSetup.isPageComplete();
				} else if (pageWebProjectType.isNewProject()) {
					isComplete &= pageWebProjectNewSetup.isPageComplete();
				} else {
					return false;
				}
			}
		} else { // otherwise get the local setup page complete
			isComplete &= pageLocalSetup.isPageComplete();
		}

		return isComplete;
	}

	/**
	 * @return the CredibilityProcessNewFileWizardPage page
	 */
	public INewCFProcessFileSelectionPage getPageNewCredibilityFile() {
		return pageNewCredibilityFile;
	}

	/**
	 * Gets the page backend selection.
	 *
	 * @return the page backend selection
	 */
	public INewCFProcessBackendSelectionPage getPageBackendSelection() {
		return pageBackendSelection;
	}

	/**
	 * Gets the page web setup.
	 *
	 * @return the page web setup
	 */
	public INewCFProcessWebSetupPage getPageWebSetup() {
		return pageWebSetup;
	}

	/**
	 * Gets the page web project type.
	 *
	 * @return the page web project type
	 */
	public INewCFProcessWebProjectTypePage getPageWebProjectType() {
		return pageWebProjectType;
	}

	/**
	 * Gets the page web project new setup.
	 *
	 * @return the page web project new setup
	 */
	public INewCFProcessWebProjectNewSetupPage getPageWebProjectNewSetup() {
		return pageWebProjectNewSetup;
	}

	/**
	 * Gets the page web project existing setup.
	 *
	 * @return the page web project existing setup
	 */
	public INewCFProcessWebProjectExistingSetupPage getPageWebProjectExistingSetup() {
		return pageWebProjectExistingSetup;
	}

	/**
	 * @return the ICFSetupWizardPage page
	 */
	public INewCFProcessLocalSetupPage getPageSetup() {
		return pageLocalSetup;
	}

	/**
	 * @return the resource manager
	 */
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	/**
	 * Gets the web client manager.
	 *
	 * @return the web client manager
	 */
	public IWebClientManager getWebClientManager() {
		return webClientManager;
	}
}
