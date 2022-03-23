/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.SQLException;

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

import gov.sandia.cf.application.IApplicationManager;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.exceptions.CredibilityDatabaseInvalidException;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationCancelledException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.parts.services.ClientServiceManager;
import gov.sandia.cf.parts.services.IClientServiceManager;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;
import gov.sandia.cf.web.WebClientException;
import gov.sandia.cf.web.WebClientRuntimeException;
import gov.sandia.cf.web.message.IMessageManager;
import gov.sandia.cf.web.services.IWebClientManager;
import gov.sandia.cf.web.services.status.IConnectionStatusListener;
import gov.sandia.cf.web.services.status.IPingManager;

/**
 * 
 * Credibility editor to edit .cf files and launch credibility view
 * 
 * @author Didier Verstraete
 *
 */
public class CredibilityEditor extends EditorPart implements Listener, IConnectionStatusListener {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(CredibilityEditor.class);

	/** The Constant CREDIBILITY_CONF_FILE_NAME. */
	public static final String CREDIBILITY_SETUP_FILE_NAME = "setup.yml"; //$NON-NLS-1$

	/**
	 * The GUI resource manager
	 */
	private ResourceManager resourceManager;

	/** The view mgr. */
	private MainViewManager viewMgr;

	/** The client srv mgr. */
	private IClientServiceManager clientSrvMgr;

	/** The app mgr. */
	private IApplicationManager appMgr;

	/** The web msg mgr. */
	private IMessageManager webMsgMgr;

	/** The ping mgr. */
	private IPingManager pingMgr;

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
	private boolean toClose;

	/**
	 * the cf cache
	 */
	private CFCache cache;

	/** The cf tmp folder mgr. */
	private CFTmpFolderManager cfTmpFolderMgr;

	/**
	 * The constructor
	 */
	public CredibilityEditor() {
		this.dirty = false;
		this.toClose = false;
	}

	/** {@inheritDoc} */
	@Override
	public void createPartControl(Composite parent) {

		if (this.toClose) {
			// close editor
			WorkspaceTools.closeEditor(this, false);
		} else {
			// create part
			viewMgr.createPartControl(parent);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {

		// set not dirty at first
		this.dirty = false;

		/**
		 * 1- Check input
		 */
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
		 * 2- Initialize client
		 */
		setSite(site);
		setInput(input);
		setPartName(input.getName());

		// init managers
		this.viewMgr = new MainViewManager(this);
		this.clientSrvMgr = new ClientServiceManager();
		this.cache = new CFCache(this);

		// set resource manager
		this.resourceManager = new LocalResourceManager(JFaceResources.getResources());

		// start client service
		this.viewMgr.start();
		this.clientSrvMgr.start();

		// get cf file
		this.inputFile = ((FileEditorInput) input).getFile();

		// get cf project path
		this.cfTmpFolderMgr = new CFTmpFolderManager(this);
		this.cfProjectPath = this.inputFile.getParent().getFullPath();

		try {

			/**
			 * 3- Load setup file for backend detection (Local FILE or WEB)
			 */
			loadClientSetup();

			/**
			 * 4- Load process depending of backend type
			 */
			if (isWebConnection()) {
				new CFWebBackendLoader().load(this);
			} else {
				new CFLocalFileLoader().load(this);
			}

		} catch (CredibilityMigrationException | CredibilityException | CredibilityDatabaseInvalidException
				| SqlToolError | SQLException | IOException | URISyntaxException | WebClientException
				| WebClientRuntimeException | CoreException e) {

			// display the error
			Status status = new Status(IStatus.ERROR, CredibilityFrameworkConstants.CF_PLUGIN_NAME,
					RscTools.getString(RscConst.EX_CREDEDITOR_OPENING, getTitle(), e.getMessage()), e);
			StatusManager.getManager().handle(status, StatusManager.LOG);

			logger.error(e.getMessage(), e);

			MessageDialog.openError(getSite().getShell(), RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_TITLE),
					RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_LOADING) + RscTools.carriageReturn()
							+ this.inputFile + RscTools.carriageReturn() + RscTools.carriageReturn() + e.getMessage());

			this.toClose = true;

		} catch (CredibilityMigrationCancelledException e) {

			// display the error
			Status status = new Status(IStatus.WARNING, CredibilityFrameworkConstants.CF_PLUGIN_NAME,
					RscTools.getString(RscConst.EX_CREDEDITOR_OPENING, getTitle(), e.getMessage()), e);
			StatusManager.getManager().handle(status, StatusManager.LOG);

			logger.warn(e.getMessage(), e);

			this.toClose = true;
		}
	}

	/**
	 * Load client setup.
	 *
	 * @return the CF backend connection type
	 * @throws CredibilityException the credibility exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	private CFClientSetup loadClientSetup() throws CredibilityException, IOException {

		// get cf working dir
		IFolder cfTmpIFolder = cfTmpFolderMgr.getTempIFolder();
		IPath cfFolderPath = cfTmpIFolder.getFullPath();
		File cfTmpFolder = WorkspaceTools.toFile(cfFolderPath);
		boolean existAtStartCfTmpFolder = (cfTmpFolder != null && cfTmpFolder.exists());

		if (!existAtStartCfTmpFolder) {
			cfTmpFolderMgr.createWorkingDir();
		}

		// get setup file
		File setupFile = new File(cfTmpFolder, CREDIBILITY_SETUP_FILE_NAME);

		// load setup file
		getCache().reloadCFClientSetup(setupFile);

		// if the tmp folder has just been created for the loading -> delete it
		if (!existAtStartCfTmpFolder) {
			FileTools.deleteDirectoryRecursively(cfTmpFolder);
		}

		return getCache().getCFClientSetup();
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

		// CONCURRENCY SUPPORT: unsubscribe to the message broker
		if (isWebConnection()) {
			if (webMsgMgr != null)
				webMsgMgr.stop();
			if (pingMgr != null)
				pingMgr.stop();
		}

		// stop the managers
		if (clientSrvMgr != null)
			clientSrvMgr.stop();
		if (appMgr != null)
			appMgr.stop();

		if (!toClose) {
			getCfTmpFolderMgr().deleteTempFolder();
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
	 * Gets the cf tmp folder mgr.
	 *
	 * @return the cf tmp folder mgr
	 */
	public CFTmpFolderManager getCfTmpFolderMgr() {
		return cfTmpFolderMgr;
	}

	/**
	 * @return the cf cache
	 */
	public CFCache getCache() {
		return cache;
	}

	/**
	 * @return the view manager
	 */
	public MainViewManager getViewMgr() {
		return viewMgr;
	}

	/**
	 * @return the client service manager
	 */
	public IClientServiceManager getClientSrvMgr() {
		return clientSrvMgr;
	}

	/**
	 * @return the application manager
	 */
	public IApplicationManager getAppMgr() {
		return appMgr;
	}

	/**
	 * Sets the app mgr.
	 *
	 * @param appMgr the new app mgr
	 */
	void setAppMgr(IApplicationManager appMgr) {
		this.appMgr = appMgr;
	}

	/**
	 * Gets the editor shell.
	 *
	 * @return the editor shell
	 */
	public Shell getEditorShell() {
		return getSite().getShell();
	}

	/**
	 * @return the web client manager
	 */
	public IWebClientManager getWebClient() {
		if (appMgr == null || !isWebConnection() && !IWebClientManager.class.isAssignableFrom(appMgr.getClass())) {
			return null;
		}
		return (IWebClientManager) appMgr;
	}

	/**
	 * Gets the web msg mgr.
	 *
	 * @return the web msg mgr
	 */
	public IMessageManager getWebMsgMgr() {
		return webMsgMgr;
	}

	/**
	 * Sets the web msg mgr.
	 *
	 * @param webMsgMgr the new web msg mgr
	 */
	void setWebMsgMgr(IMessageManager webMsgMgr) {
		this.webMsgMgr = webMsgMgr;
	}

	/**
	 * Gets the ping mgr.
	 *
	 * @return the ping mgr
	 */
	public IPingManager getPingMgr() {
		return pingMgr;
	}

	/**
	 * Sets the ping mgr.
	 *
	 * @param pingMgr the new ping mgr
	 */
	void setPingMgr(IPingManager pingMgr) {
		this.pingMgr = pingMgr;
	}

	/**
	 * Sets CF editor in error.
	 *
	 * @param toClose the new to close
	 */
	void setInError() {
		this.toClose = true;
	}

	/**
	 * @return is CF editor in error
	 */
	public boolean isInError() {
		return toClose;
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

			logger.debug("Begin save {} into {}", cfTmpFolderMgr.getTempFolderPath(), inputFile.getFullPath()); //$NON-NLS-1$
			monitor.beginTask(RscTools.getString(RscConst.MSG_EDITOR_SAVE_BEGINTASK, inputFile), 4);
			int cptTask = 1;

			// save current cf data as a zip file
			String oldCfFileName = inputFile.getName() + CFTmpFolderManager.OLD_FILENAME_SUFFIX
					+ DateTools.getDateFormattedDateTimeHash();
			IPath oldCfFilePath = cfProjectPath.append(oldCfFileName);

			try {

				// copy current cf file
				logger.debug("Save current {} file as {}", inputFile.getName(), oldCfFileName); //$NON-NLS-1$
				monitor.subTask(RscTools.getString(RscConst.MSG_EDITOR_SAVE_COPYTASK));
				inputFile.copy(oldCfFilePath, IResource.FORCE, new NullProgressMonitor());

				// delete the current cf file without workspace method to not trigger delete
				// events that will delete database and close connection
				logger.debug("Delete {} file", inputFile.getName()); //$NON-NLS-1$
				Files.delete(WorkspaceTools.toFile(inputFile).toPath());
				monitor.worked(cptTask);

				// zip cf content to cf file
				logger.debug("Save temporary folder {} as a zip into {}", cfTmpFolderMgr.getTempFolderPath(), //$NON-NLS-1$
						inputFile.getName());
				monitor.subTask(RscTools.getString(RscConst.MSG_EDITOR_SAVE_ZIPTASK));
				cfTmpFolderMgr.saveToZip();
				cptTask++;
				monitor.worked(cptTask);

				// remove old cf file
				logger.debug("Delete saved file {}", oldCfFileName); //$NON-NLS-1$
				monitor.subTask(RscTools.getString(RscConst.MSG_EDITOR_SAVE_REMOVEOLDTASK));
				WorkspaceTools.getFileInWorkspaceForPath(oldCfFilePath).delete(IResource.FORCE,
						new NullProgressMonitor());
				cptTask++;
				monitor.worked(cptTask);

				logger.info("{} saved at: {}", inputFile.getFullPath(), DateTools.getDateFormattedDateTime()); //$NON-NLS-1$
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
	 * Checks if is web connection.
	 *
	 * @return true, if is web
	 */
	public boolean isWebConnection() {
		return CFBackendConnectionType.WEB.equals(getCache().getCFClientSetup().getBackendConnectionType());
	}

	/**
	 * Checks if is local file connection.
	 *
	 * @return true, if is local file
	 */
	public boolean isLocalFileConnection() {
		return !isWebConnection();
	}

	/**
	 * Delete the current CF file, close connection and the editor.
	 */
	public void deleteAndCloseFile() {

		if (inputFile != null && inputFile.exists()) { // the input file can change if the file has been renamed

			logger.info("{} deleted at: {}", inputFile.getFullPath(), DateTools.getDateFormattedDateTime()); //$NON-NLS-1$

			try {

				// delete the current cf file without workspace method to not trigger delete
				// events that will delete database and close connection
				Files.delete(WorkspaceTools.toFile(inputFile).toPath());

			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(getSite().getShell(), RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_TITLE),
						RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_SAVING) + inputFile
								+ RscTools.carriageReturn() + e.getMessage());
			}

			// close editor
			WorkspaceTools.closeEditor(this, false);
		}
	}

	@Override
	public void connectionLost() {
		// refresh connection status
		getViewMgr().refreshSaveState();

		// reload active view
		getViewMgr().reloadActiveView();

		// stop message manager
		webMsgMgr.stop();
	}

	@Override
	public void connectionGained() {
		// refresh connection status
		getViewMgr().refreshSaveState();

		// reload active view
		getViewMgr().reloadActiveView();

		// restart message manager
		webMsgMgr.start();

		// resubscribe
		try {
			webMsgMgr.subscribeToModel(getCache().getModel());
		} catch (WebClientException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return pingMgr != null && pingMgr.isConnected();
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
}
