/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.dto.configuration.ParameterLinkGson;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.parts.ui.ACredibilityView;
import gov.sandia.cf.parts.ui.home.HomeView;
import gov.sandia.cf.parts.ui.pcmm.ACredibilityPCMMView;
import gov.sandia.cf.parts.ui.pcmm.PCMMEvidenceView;
import gov.sandia.cf.parts.ui.pcmm.PCMMViewManager;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * The workspace resource listener and manager class.
 * 
 * This class is called when an event occurs outside of the plugin into the
 * workspace. It handles and dispatches the task to do depending of the event
 * type.
 * 
 * @author Didier Verstraete
 *
 */
public class ResourceDeltaModifier implements IResourceDeltaVisitor {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ResourceDeltaModifier.class);

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource res = delta.getResource();

		if (res != null) {

			// if it is a credibility file, change it depending of the update
			if (FileTools.CREDIBILITY_FILE_EXTENSION.equals(res.getFileExtension())) {
				handleCredibilityFileChange(delta);
			}

			// check if it is an evidence of the opened evitor
			handleCredibilityEvidenceChange(delta);
		}
		return true;
	}

	/**
	 * Rename or change the path of the file changed in the Eclipse explorer.
	 * 
	 * @param delta
	 * @throws CoreException
	 */
	private void handleCredibilityFileChange(IResourceDelta delta) throws CoreException {

		IResource res = delta.getResource();

		if (delta.getKind() == IResourceDelta.REMOVED) {

			/** renamed file event */
			if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {

				IPath newPath = delta.getMovedToPath();
				logger.info("Renaming credibility file from: {} to: {}", //$NON-NLS-1$
						res.getFullPath(),
						(newPath != null) ? newPath.toString() : RscTools.getString(RscConst.MSG_OBJECT_NULL));
				renameResourceAndModel(res, newPath);
			} else if (res instanceof IFile) {
				/** deleted file event */
				logger.info("Credibility connection closed"); //$NON-NLS-1$

				// close the opened database connection
				CredibilityEditor credibilityEditor = getOpenedEditorForResource(res);
				if (credibilityEditor != null) {
					credibilityEditor.getAppMgr().stop();
					credibilityEditor.dispose();
				}

				// delete the temporary folder
				deleteCFTemporaryFolder((IFile) res);
			}
		}
	}

	/**
	 * Close the database connection if it exists, and move the temporary folder to
	 * the new path from workspace and file system.
	 *
	 * @param oldFile the old file
	 * @param newFile the new file
	 * @throws CoreException the core exception
	 * @throws IOException   Signals that an I/O exception has occurred.
	 */
	private void moveCFTemporaryFolder(IFile oldFile, IFile newFile) throws CoreException, IOException {

		IFolder cfTmpIFolderOld = CFTmpFolderManager.getTempFolder(oldFile);
		File cfTmpFolderOld = WorkspaceTools.toFile(cfTmpIFolderOld);
		IFolder cfTmpIFolderNew = CFTmpFolderManager.getTempFolder(newFile);
		File cfTmpFolderNew = WorkspaceTools.toFile(cfTmpIFolderNew);

		if (cfTmpFolderOld != null && cfTmpFolderOld.exists() && cfTmpFolderNew != null) {
			// if it exists delete new temporary folder
			if (cfTmpFolderNew.exists()) {
				logger.info("New cf file {} temporary folder already exists. Deleting temporary folder {} ...", //$NON-NLS-1$
						newFile.getLocation(), cfTmpFolderNew.getAbsolutePath());
				runDeleteFolderInWorskpace(cfTmpIFolderNew);
			}

			logger.info("Moving old cf file temporary folder to the new location: {} to {} ...", //$NON-NLS-1$
					cfTmpFolderOld.getAbsolutePath(), cfTmpFolderNew.getAbsolutePath());

			Files.move(cfTmpFolderOld.toPath(), cfTmpFolderNew.toPath(), StandardCopyOption.REPLACE_EXISTING);

			logger.info("Old cf file temporary folder: {} moved to: {}", //$NON-NLS-1$
					cfTmpFolderOld.getAbsolutePath(), cfTmpFolderNew.getAbsolutePath());
		}
	}

	/**
	 * Close the database connection if it exists, and delete the temporary folders
	 * from workspace and file system.
	 * 
	 * @param res
	 * @throws CoreException
	 */
	private void deleteCFTemporaryFolder(IFile res) throws CoreException {

		// deletes database files if there is no other credibility file
		IFolder cfTmpIFolder = CFTmpFolderManager.getTempFolder(res);
		File cfTmpFolder = WorkspaceTools.toFile(cfTmpIFolder);

		if (cfTmpFolder != null && cfTmpFolder.exists()) {

			logger.info("Deleting credibility temporary files..."); //$NON-NLS-1$

			// delete temporary folder in workspace
			if (cfTmpIFolder != null && cfTmpIFolder.exists()) { // delete it if it exists in workspace
				runDeleteFolderInWorskpace(cfTmpIFolder);
			}

			logger.info("Credibility temporary files deleted"); //$NON-NLS-1$
		}
	}

	/**
	 * Delete the folder in the workspace and in the file system.
	 * 
	 * @param cfTmpIFolder
	 * @throws CoreException
	 */
	private void runDeleteFolderInWorskpace(final IFolder cfTmpIFolder) throws CoreException {

		final File cfTmpFolder = WorkspaceTools.toFile(cfTmpIFolder);

		WorkspaceJob deleteJob = new WorkspaceJob(RscTools.getString(RscConst.MSG_EDITOR_DELETE_TMPFOLDER)) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				try {
					logger.debug("Enter delete temporary folder"); //$NON-NLS-1$
					WorkspaceTools.deleteDirectoryRecursively(cfTmpIFolder);
				} catch (CoreException e) {
					logger.error("Impossible to delete temporary folder with Workspace filesystem", //$NON-NLS-1$
							e);

					// delete temporary folder on filesystem
					try {
						FileTools.deleteDirectoryRecursively(cfTmpFolder);
					} catch (IOException e1) {
						logger.error("Impossible to delete temporary folder files: {}", //$NON-NLS-1$
								cfTmpFolder, e1);
					}
				}
				return null;
			}
		};
		deleteJob.runInWorkspace(new NullProgressMonitor());
	}

	/**
	 * Reload the credibility editors if the file changed in the Eclipse explorer is
	 * part of an evidence of the opened credibility editors.
	 * 
	 * @param delta
	 */
	private void handleCredibilityEvidenceChange(IResourceDelta delta) {

		IResource res = delta.getResource();

		List<CredibilityEditor> openedEditors = getOpenedCFEditors();
		List<ACredibilityView<?>> viewsToReload = new ArrayList<>();

		for (CredibilityEditor editor : openedEditors) {
			viewsToReload.addAll(getViewsImpactedInCredibilityEditor(res, editor));
		}

		if (!viewsToReload.isEmpty()) {
			Display.getDefault().asyncExec(() -> viewsToReload.forEach(ACredibilityView::reload));
		}
	}

	/**
	 * @param res
	 * @param editor
	 * @return the views impacted by the change of res in the editor in parameter.
	 */
	private List<ACredibilityView<?>> getViewsImpactedInCredibilityEditor(IResource res, CredibilityEditor editor) {

		List<ACredibilityView<?>> viewsToReload = new ArrayList<>();
		if (editor != null && editor.getAppMgr() != null
				&& editor.getAppMgr().getService(IPCMMApplication.class) != null && res.getFullPath() != null) {
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(PCMMEvidence.Filter.VALUE,
					ParameterLinkGson.toGson(FormFieldType.LINK_FILE, res.getFullPath().toPortableString()));
			List<PCMMEvidence> evidences = editor.getAppMgr().getService(IPCMMEvidenceApp.class).getEvidenceBy(filters);
			if (evidences != null && !evidences.isEmpty()) {
				Composite layoutTop = editor.getViewMgr().getLayoutTop();
				if (layoutTop instanceof PCMMViewManager) {
					ACredibilityPCMMView<?> activeView = ((PCMMViewManager) layoutTop).getActiveView();
					if (activeView instanceof PCMMEvidenceView) {
						viewsToReload.add(activeView);
					}
				} else if (layoutTop instanceof HomeView) {
					viewsToReload.add((HomeView) layoutTop);
				}
			}
		}

		return viewsToReload;
	}

	/**
	 * Update cf database model parameters to match renamed credibility file. If an
	 * editor is currently opened for the renamed file, reset the editor input.
	 * 
	 * @param res     current resource before renaming
	 * @param newPath renamed resource path
	 */
	private void renameResourceAndModel(final IResource res, final IPath newPath) {

		/**
		 * Retrieve the credibility editor
		 */
		CredibilityEditor credibilityEditor = getOpenedEditorForResource(res);

		if (credibilityEditor == null || newPath == null) {
			return;
		}

		// rename editor in GUI Thread to avoid errors
		Display.getDefault().syncExec(() -> {

			IFile newCfFile = WorkspaceTools.getFileInWorkspaceForPath(newPath);

			if (newCfFile != null && newCfFile.exists()) {

				// if the cf project path changed
				if (credibilityEditor.getCfProjectPath() == null
						|| credibilityEditor.getCfProjectPath().equals(newCfFile.getParent().getFullPath())) {

					/**
					 * If the credibility file has just been renamed, rename the input file in the
					 * credibility editor
					 */
					// rename the file in the editor
					credibilityEditor.setRenamedFile(newCfFile);
				}

				/** deleted file event */
				logger.info("Credibility connection closed"); //$NON-NLS-1$

				// close the opened database connection
				if (credibilityEditor != null) {
					credibilityEditor.getAppMgr().stop();
				}

				// delete the temporary folder
				try {
					moveCFTemporaryFolder((IFile) res, newCfFile);
				} catch (CoreException | IOException e) {
					logger.error(e.getMessage(), e);
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_TITLE),
							RscTools.getString(RscConst.ERR_CREDIBILITYEDITOR_MOVE, res, newCfFile)
									+ RscTools.carriageReturn() + e.getMessage());
				}

				/**
				 * If it is a move or renaming, close the editor and open it with the new path
				 */
				closeAndReopenEditor(credibilityEditor, newPath);
			}
		});
	}

	/**
	 * Close the credibility editor in parameter and reopen it in the new cf file.
	 * 
	 * @param credibilityEditor
	 * @param newPath
	 */
	private void closeAndReopenEditor(CredibilityEditor credibilityEditor, IPath newPath) {
		try {
			// close the editor
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(credibilityEditor, true);

			// open it with new path
			IEditorInput input = new FileEditorInput(WorkspaceTools.getFileInWorkspaceForPath(newPath));
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
					CredibilityFrameworkConstants.CREDIBILITY_EDITOR_ID);
		} catch (PartInitException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * @param res
	 * @return the credibility editor opened and associated to the resource in
	 *         parameter
	 */
	private CredibilityEditor getOpenedEditorForResource(IResource res) {

		CredibilityEditor credibilityEditor = null;

		/**
		 * Retrieve the credibility editor
		 */
		// updating opened editor name
		if (PlatformUI.isWorkbenchRunning()) {

			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();

			if (windows != null) {
				// for each workbench window
				for (IWorkbenchWindow window : windows) {

					IWorkbenchPage activePage = window.getActivePage();

					// get credbility editor impacted
					credibilityEditor = getOpenedEditorForResourceInPage(activePage, res);
				}
			}
		}

		return credibilityEditor;
	}

	/**
	 * Gets the opened editor for resource in page.
	 *
	 * @param activePage the active page
	 * @param res        the res
	 * @return the credibility editor opened and associated to the resource in
	 *         parameter
	 */
	private CredibilityEditor getOpenedEditorForResourceInPage(IWorkbenchPage activePage, IResource res) {

		// get active page
		if (activePage != null && activePage.getActiveEditor() instanceof CredibilityEditor) {

			CredibilityEditor activeEditor = (CredibilityEditor) activePage.getActiveEditor();

			// check plugin name corresponding to opened editor
			Bundle cfBundle = CredibilityFrameworkConstants.getBundle();
			IEditorInput input = activeEditor.getEditorInput();

			// check file correspondance
			boolean isCfBundle = (cfBundle != null && cfBundle.getSymbolicName() != null
					&& cfBundle.getSymbolicName().equals(activeEditor.getEditorSite().getPluginId()));
			boolean equalsResourceEditorInput = (input instanceof FileEditorInput
					&& ((FileEditorInput) input).getFile() != null && ((FileEditorInput) input).getFile().equals(res));

			if (isCfBundle && equalsResourceEditorInput) {
				// get the credibility editor found
				return activeEditor;
			}
		}

		return null;
	}

	/**
	 * @param res
	 * @return the credibility editor opened and associated to the resource in
	 *         parameter
	 */
	private List<CredibilityEditor> getOpenedCFEditors() {

		List<CredibilityEditor> credibilityEditorsOpened = new ArrayList<>();

		/**
		 * Retrieve the credibility editors
		 */
		if (PlatformUI.isWorkbenchRunning()) {

			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();

			// for each workbench window
			for (IWorkbenchWindow window : windows) {

				IWorkbenchPage activePage = window.getActivePage();

				// get opened cf editors
				credibilityEditorsOpened.addAll(getOpenedCFEditorsInPage(activePage));
			}
		}

		return credibilityEditorsOpened;
	}

	/**
	 * Gets the opened CF editors in page.
	 *
	 * @param activePage the active page
	 * @return the credibility editor opened and associated to the resource in
	 *         parameter
	 */
	private List<CredibilityEditor> getOpenedCFEditorsInPage(IWorkbenchPage activePage) {

		List<CredibilityEditor> credibilityEditorsOpened = new ArrayList<>();

		// get active page
		if (activePage != null) {

			IEditorReference[] editorReferences = activePage.getEditorReferences();
			if (editorReferences != null) {

				Arrays.stream(editorReferences).filter(Objects::nonNull).forEach(editorRef -> {
					IEditorPart editor = editorRef.getEditor(false);
					if (editor instanceof CredibilityEditor) {
						// check plugin name corresponding to the editor
						Bundle cfBundle = CredibilityFrameworkConstants.getBundle();
						if (cfBundle != null && cfBundle.getSymbolicName() != null
								&& cfBundle.getSymbolicName().equals(editor.getEditorSite().getPluginId())) {
							credibilityEditorsOpened.add((CredibilityEditor) editor);
						}
					}
				});
			}
		}

		return credibilityEditorsOpened;
	}
}
