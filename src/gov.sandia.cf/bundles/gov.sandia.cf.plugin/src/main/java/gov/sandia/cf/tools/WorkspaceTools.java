/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.imports.YmlReaderEvidenceFolderStructure;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.launcher.CredibilityEditor;
import gov.sandia.cf.tools.NetTools.URLEncodingCharset;

/**
 * The Workspace Tools class
 * 
 * @author Didier Verstraete
 *
 */
public class WorkspaceTools {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(WorkspaceTools.class);

	/** The Constant IPATH_MINIMUM_FILE_SEGMENT_LENGTH. */
	public static final int IPATH_MINIMUM_FILE_SEGMENT_LENGTH = 2;

	private WorkspaceTools() {
		// Do not instantiate
	}

	/**
	 * @return the active cf file
	 */
	public static IFile getActiveCfFile() {
		IFile cfFile = null;

		IWorkbenchPage activePage = getActivePage();

		if (activePage != null) {

			IEditorPart activeEditor = activePage.getActiveEditor();

			if (activeEditor instanceof CredibilityEditor) {
				IEditorInput input = activeEditor.getEditorInput();
				if (input instanceof FileEditorInput) {
					cfFile = ((FileEditorInput) input).getFile();
				}
			}
		}

		return cfFile;
	}

	/**
	 * @return the active cf editor
	 */
	public static CredibilityEditor getActiveEditor() {

		IWorkbenchPage activePage = getActivePage();

		if (activePage != null) {

			IEditorPart activeEditor = activePage.getActiveEditor();

			if (activeEditor instanceof CredibilityEditor) {
				return (CredibilityEditor) activeEditor;
			}
		}

		return null;
	}

	/**
	 * @return the current active CF editors
	 */
	public static List<CredibilityEditor> getActiveCFEditors() {

		// get active pages
		IWorkbenchPage[] activePages = null;

		IWorkbench workbench = PlatformUI.getWorkbench();

		if (workbench != null) {
			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();

			if (activeWorkbenchWindow != null) {
				activePages = activeWorkbenchWindow.getPages();
			}
		}

		List<CredibilityEditor> editors = new ArrayList<>();

		// search for Credibility editors opened
		if (activePages != null) {
			List<IEditorReference> editorRefs = new ArrayList<>();
			Arrays.asList(activePages).forEach(p -> editorRefs.addAll(Arrays.asList(p.getEditorReferences())));
			editors = editorRefs.stream().filter(e -> e.getEditor(false) instanceof CredibilityEditor)
					.map(e -> e.getEditor(false)).map(CredibilityEditor.class::cast).collect(Collectors.toList());
		}

		return editors;
	}

	/**
	 * @return the workspace
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * @return the current project for the active opened window
	 */
	public static IProject getActiveProject() {

		IProject project = null;
		IWorkbenchPage activePage = getActivePage();

		if (activePage != null) {
			IEditorPart activeEditor = activePage.getActiveEditor();

			if (activeEditor instanceof CredibilityEditor) {
				IEditorInput input = activeEditor.getEditorInput();

				project = input.getAdapter(IProject.class);
				if (project == null) {
					IResource resource = input.getAdapter(IResource.class);
					if (resource != null) {
						project = resource.getProject();
					}
				}
			}
		}

		return project;
	}

	/**
	 * @return the current active page
	 * @throws IllegalStateException if the workbench is not active
	 */
	public static IWorkbenchPage getActivePage() {

		IWorkbenchPage activePage = null;

		IWorkbench workbench = PlatformUI.getWorkbench();

		if (workbench != null) {
			IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();

			if (activeWorkbenchWindow != null) {
				activePage = activeWorkbenchWindow.getActivePage();
			}
		}

		return activePage;
	}

	/**
	 * @return the active cf home directory
	 */
	public static String getActiveHomeDirPath() {
		CredibilityEditor editor = getActiveEditor();
		if (editor != null) {
			return editor.getCfProjectPath() != null ? editor.getCfProjectPath().toString() : RscTools.empty();
		}
		return RscTools.empty();
	}

	/**
	 * @return the active cf filename
	 */
	public static String getActiveFilename() {
		CredibilityEditor editor = getActiveEditor();
		if (editor != null) {
			return editor.getInputFile() != null ? editor.getInputFile().getName() : RscTools.empty();
		}
		return RscTools.empty();
	}

	/**
	 * @return the active cf filename without extension
	 */
	public static String getActiveFilenameWithoutExtension() {
		String activeFilename = getActiveFilename();
		if (activeFilename != null && activeFilename.endsWith(FileTools.CREDIBILITY_FILE_DOT_EXTENSION)) {
			return activeFilename.substring(0, activeFilename.length() - 3);
		}
		return RscTools.empty();
	}

	/**
	 * @return the active cf working directory
	 */
	public static String getActiveWorkingDirPath() {
		CredibilityEditor editor = getActiveEditor();
		if (editor != null && editor.getCfTmpFolderMgr() != null) {
			IFolder workDir = editor.getCfTmpFolderMgr().getTempIFolder();
			return workDir != null ? workDir.getFullPath().toString() : RscTools.empty();
		}
		return RscTools.empty();
	}

	/**
	 * @return the active cf project location path as a string path
	 */
	public static String getActiveProjectPathToString() {
		IProject project = getActiveProject();
		return project != null ? project.getFullPath().toString() : RscTools.empty();
	}

	/**
	 * @param path the path to convert to IFile
	 * @return the file referenced by @param path
	 */
	public static IFile getFileInWorkspaceForPath(IPath path) {
		if (path == null) {
			return null;
		}

		if (path.segmentCount() < IPATH_MINIMUM_FILE_SEGMENT_LENGTH) {
			logger.debug("Impossible to get file with path empty or with size < {}", //$NON-NLS-1$
					IPATH_MINIMUM_FILE_SEGMENT_LENGTH);
			return null;
		}

		IFile file = null;
		try {
			file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		} catch (IllegalArgumentException e) {
			logger.warn(e.getMessage());
		}

		return file;
	}

	/**
	 * @param name the project name
	 * @return the project in workspace referenced by the parameter
	 */
	public static IProject getProjectInWorkspaceForPath(String name) {
		if (name == null) {
			return null;
		}

		IProject project = null;
		try {
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		} catch (IllegalArgumentException e) {
			logger.warn(e.getMessage());
		}

		return project;
	}

	/**
	 * @param path the path to convert to IFolder
	 * @return the folder referenced by @param path
	 */
	public static IFolder getFolderInWorkspaceForPath(IPath path) {
		if (path == null) {
			return null;
		}

		IFolder folder = null;
		try {
			folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
		} catch (IllegalArgumentException e) {
			logger.warn(e.getMessage());
		}

		return folder;
	}

	/**
	 * @param path the path to get
	 * @return the resource if found associated to path
	 */
	public static IResource getResourceInWorkspaceForPath(IPath path) {
		IResource rsc = getFileInWorkspaceForPath(path);

		if (rsc == null || !rsc.exists()) {
			rsc = getFolderInWorkspaceForPath(path);
		}

		if (rsc == null || !rsc.exists()) {
			rsc = getProjectInWorkspaceForPath(path.segment(0));
		}

		return rsc;
	}

	/**
	 * @param path the path to convert to IFolder
	 * @return the folder referenced by @param path
	 */
	public static IContainer findFirstContainerInWorkspaceForPath(IPath path) {
		IContainer[] findForLocation = findContainersInWorkspaceForPath(path);
		return (findForLocation != null && findForLocation.length > 0) ? findForLocation[0] : null;
	}

	/**
	 * @param path the path to convert to IFolder
	 * @return the folder referenced by @param path
	 */
	@SuppressWarnings("deprecation")
	public static IContainer[] findContainersInWorkspaceForPath(IPath path) {
		return ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(path);
	}

	/**
	 * @param path the path to convert to IFile
	 * @return the file referenced by @param path
	 */
	public static IFile findFirstFileInWorkspaceForPath(IPath path) {
		IFile[] findForLocation = findFilesInWorkspaceForPath(path);
		return (findForLocation != null && findForLocation.length > 0) ? findForLocation[0] : null;
	}

	/**
	 * @param path the path to convert to IFile
	 * @return the files referenced by @param path
	 */
	@SuppressWarnings("deprecation")
	public static IFile[] findFilesInWorkspaceForPath(IPath path) {
		return ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(path);
	}

	/**
	 * @param path0 the first path
	 * @param path  the path array (optional)
	 * @return the file parent if found in the workspace or on filesystem, otherwise
	 *         null
	 */
	public static File getFileInWorkspaceOrSystem(String path0, String... path) {

		if (path0 == null && (path == null || path.length <= 0)) {
			return null;
		}

		File reportFile = null;
		File parentFile = null;
		String fileName = null;

		// find file in workspace
		IPath reportIPath = new org.eclipse.core.runtime.Path(path0);
		for (int i = 0; i < path.length; i++) {
			reportIPath = reportIPath.append(path[i]);
		}
		fileName = reportIPath.lastSegment();
		IResource resource = findFirstResourceInWorkspace(reportIPath.removeLastSegments(1));
		if (resource != null) {
			parentFile = toFile(resource);
		}

		// find file on system
		if (parentFile == null || !parentFile.exists()) {
			try {
				java.nio.file.Path reportPath = Paths.get(path0, path);
				java.nio.file.Path report = reportPath.getFileName();
				java.nio.file.Path parent = reportPath.getParent();
				if (report != null) {
					fileName = report.toString();
				}
				if (parent != null && parent.toFile().exists()) {
					parentFile = parent.toFile();
				}
			} catch (InvalidPathException e) {
				logger.warn("File not found {}", String.join("/", path)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		if (parentFile != null && parentFile.exists()) {
			reportFile = new File(parentFile, fileName);
		}

		return reportFile;
	}

	/**
	 * @param resource the resource to search
	 * @return true if the resource exists in the workspace, otherwise false
	 */
	public static boolean existsInWorkspace(IResource resource) {
		return resource != null && existsInWorkspace(resource.getFullPath());
	}

	/**
	 * @param resource the resource to search
	 * @return true if the resource exists in the workspace, otherwise false
	 */
	public static IResource findFirstResourceInWorkspace(IPath resource) {

		if (resource == null) {
			return null;
		}

		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			logger.error(e.getMessage(), e);
		}

		IResource exists = null;

		IContainer[] containersInWorkspaceForPath = findContainersInWorkspaceForPath(resource);
		if (containersInWorkspaceForPath != null && containersInWorkspaceForPath.length > 0) {
			return containersInWorkspaceForPath[0];
		}

		IFile[] fileInWorkspaceForPath = findFilesInWorkspaceForPath(resource);
		if (fileInWorkspaceForPath != null && fileInWorkspaceForPath.length > 0) {
			return fileInWorkspaceForPath[0];
		}

		IResource findMember = ResourcesPlugin.getWorkspace().getRoot().findMember(resource);
		if (findMember != null) {
			return findMember;
		}

		return exists;
	}

	/**
	 * @param resource the resource to search
	 * @return true if the resource exists in the workspace, otherwise false
	 */
	public static boolean existsInWorkspace(IPath resource) {
		return findFirstResourceInWorkspace(resource) != null;
	}

	/**
	 * @return the current workspace location path as a string path
	 */
	public static String getWorkspacePathToString() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace != null ? workspace.getRoot().getLocation().toString() : RscTools.empty();
	}

	/**
	 * @param path the path
	 * @return the list of workspace resources that depends of path (children)
	 */
	public static List<IResource> getChildren(IPath path) {
		List<IResource> listChildren = new ArrayList<>();

		if (path != null) {
			IContainer[] folders = findContainersInWorkspaceForPath(path);
			if (folders != null) {
				for (IContainer folder : folders) {
					try {
						IResource[] members = folder.members();
						listChildren = Arrays.asList(members);
					} catch (CoreException e) {
						logger.error("An error occured while getting the children of the path: {}", path //$NON-NLS-1$
								+ RscTools.carriageReturn() + e.getMessage(), e);
					}
				}
			}
		}

		return listChildren;
	}

	/**
	 * @param iResource the ifile to convert to file
	 * @return the @param ifile to file format
	 */
	public static File toFile(IResource iResource) {
		return iResource != null ? new File(toOsPath(iResource.getFullPath())) : null;
	}

	/**
	 * @param ipath the ipath to convert to file
	 * @return the @param ipath to file format
	 */
	public static File toFile(IPath ipath) {
		return ipath != null ? new File(toOsPath(ipath)) : null;
	}

	/**
	 * @param ipath the ipath to convert to IFile
	 * @return the ipath to IFile format
	 */
	public static IFile toIFile(IPath ipath) {
		if (ipath == null || ipath.segmentCount() < IPATH_MINIMUM_FILE_SEGMENT_LENGTH) {
			logger.debug("Impossible to open a file with path empty or with size < {}", //$NON-NLS-1$
					IPATH_MINIMUM_FILE_SEGMENT_LENGTH);
			return null;
		}
		return ResourcesPlugin.getWorkspace().getRoot().getFile(ipath);
	}

	/**
	 * @param ipath the ipath to convert to IFolder
	 * @return the ipath to IFolder format
	 */
	public static IFolder toIFolder(IPath ipath) {
		return ResourcesPlugin.getWorkspace().getRoot().getFolder(ipath);
	}

	/**
	 * @param path the path to convert to OS path
	 * @return the @param path location as an OS path
	 */
	public static String toOsPath(IPath path) {
		if (path != null && path.segmentCount() > 0) {
			// get Eclipse project
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IResource project = root.findMember(path.segment(0));
			if (project != null) {
				// get project location on filesystem
				IPath projectOsLocation = project.getRawLocation();
				if (projectOsLocation == null || projectOsLocation.toFile() == null
						|| !projectOsLocation.toFile().exists()) {
					projectOsLocation = project.getLocation();
				}
				if (projectOsLocation != null) {
					// append file location to project path
					IPath appended = projectOsLocation.append(path.removeFirstSegments(1));
					return appended != null ? appended.toPortableString() : RscTools.empty();
				}
			}
		}
		return RscTools.empty();
	}

	/**
	 * Refresh a resource in the workspace
	 * 
	 * @param pathFileToRefresh the IFile to refresh
	 * @throws CoreException if the file is not present in the workspace
	 */
	public static void refreshPath(IPath pathFileToRefresh) throws CoreException {

		if (pathFileToRefresh == null || pathFileToRefresh.segmentCount() < IPATH_MINIMUM_FILE_SEGMENT_LENGTH) {
			logger.debug("Impossible to refresh a file with path empty or with size < {}", //$NON-NLS-1$
					IPATH_MINIMUM_FILE_SEGMENT_LENGTH);
			return;
		}

		// get resource
		IResource rsc = ResourcesPlugin.getWorkspace().getRoot().getFile(pathFileToRefresh);
		if (rsc == null) {
			rsc = ResourcesPlugin.getWorkspace().getRoot().getFolder(pathFileToRefresh);
		}

		if (rsc != null) {
			rsc.refreshLocal(0, new NullProgressMonitor());
		}
	}

	/**
	 * Refresh the project
	 */
	public static void refreshProject() {

		try {
			// get resource
			IResource rsc = getActiveProject();

			if (rsc != null) {
				rsc.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			}
		} catch (CoreException e) {
			logger.warn(e.getMessage(), e);
		}
	}

	/**
	 * @param directoryPath the directory to delete
	 * @return true if directory and all files associated deletion is ok, otherwise
	 *         false
	 * @throws CoreException if an error occured while deleting @param directory
	 */
	public static boolean deleteDirectoryRecursively(IPath directoryPath) throws CoreException {
		if (directoryPath != null) {
			IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(directoryPath);

			return deleteDirectoryRecursively(folder);
		}

		return false;
	}

	/**
	 * 
	 * @param directory the directory to delete
	 * @return true if directory and all files associated deletion is ok, otherwise
	 *         false
	 * @throws CoreException if an error occured while deleting @param directory
	 */
	public static boolean deleteDirectoryRecursively(IFolder directory) throws CoreException {
		if (directory != null && directory.exists()) {

			IResource[] allContents = directory.members();
			if (allContents != null) {
				for (IResource resource : allContents) {
					if (resource instanceof IFolder) {
						deleteDirectoryRecursively((IFolder) resource);
					} else {
						resource.delete(true, null);
					}
				}
			}

			directory.delete(true, null);

			return true;
		}

		return false;
	}

	/**
	 * @param fileRelativePathToResource the file relative path
	 * @return the full path of @param fileRelativePathToResource
	 * @throws URISyntaxException if the file name is not valid.
	 * @throws IOException        if an error occured during file writing.
	 */
	public static String getStaticFilePath(String fileRelativePathToResource) throws URISyntaxException, IOException {
		String pirtRefFilePath = RscTools.empty();
		Bundle bundle = CredibilityFrameworkConstants.getBundle();
		URL eclipseURL = bundle.getResource(fileRelativePathToResource);
		URL fileURL = FileLocator.toFileURL(eclipseURL);
		if (fileURL != null) {

			// encode blank spaces to UTF-8 for url.toURI() method
			URL encodedFileURL = new URL(fileURL.toString().replace(URLEncodingCharset.BLANK_SPACE.charset(),
					URLEncodingCharset.BLANK_SPACE.encodedCharset()));
			if (encodedFileURL.toURI() != null) {
				java.nio.file.Path filePath = Paths.get(encodedFileURL.toURI());
				if (filePath != null) {
					pirtRefFilePath = filePath.toString();
				}
			}
		}
		return pirtRefFilePath;
	}

	/**
	 * @param fileRelativePathToResource the file relative path
	 * @return the full path of @param fileRelativePathToResource
	 * @throws IOException if an error occured during file writing.
	 */
	public static URL getStaticFileURL(String fileRelativePathToResource) throws IOException {
		URL fileURL = null;
		Bundle bundle = CredibilityFrameworkConstants.getBundle();
		URL eclipseURL = bundle.getResource(fileRelativePathToResource);
		fileURL = FileLocator.toFileURL(eclipseURL);
		return fileURL;
	}

	/**
	 * Copy a file into the eclipse workspace
	 * 
	 * @param fileToCopy      the file to copy
	 * @param destinationPath the destination path
	 * @param newFileName     the new file name
	 * @return the file copied in the workspace.
	 * @throws CredibilityException if a parameter is not valid.
	 * @throws IOException          if an error occured during file writing.
	 * @throws CoreException        if a runtime error occured.
	 */
	public static IFile copyIntoWorkspace(File fileToCopy, IPath destinationPath, String newFileName)
			throws CredibilityException, IOException, CoreException {

		if (fileToCopy == null || destinationPath == null || newFileName == null || newFileName.isEmpty()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL));
		}

		java.nio.file.Path originalPath = fileToCopy.toPath();
		java.nio.file.Path copiedPath = Paths.get(toOsPath(destinationPath.append(newFileName)));

		// copy file
		Files.copy(originalPath, copiedPath, StandardCopyOption.REPLACE_EXISTING);

		IPath destinationFilePath = destinationPath.append(newFileName);

		// get IFile in workspace
		if (destinationFilePath == null || destinationFilePath.segmentCount() < IPATH_MINIMUM_FILE_SEGMENT_LENGTH) {
			logger.debug("Impossible to refresh copied file: path empty or with size < {}", //$NON-NLS-1$
					IPATH_MINIMUM_FILE_SEGMENT_LENGTH);
			return null;
		}
		IFile copiedFile = ResourcesPlugin.getWorkspace().getRoot().getFile(destinationFilePath);

		// refresh workspace
		copiedFile.refreshLocal(0, new NullProgressMonitor());

		return copiedFile;
	}

	/**
	 * Create a new folder in the workspace for @param path
	 * 
	 * @param path the path of the folder to create
	 * @throws CoreException If an error occured
	 */
	public static void createFolder(IPath path) throws CoreException {
		if (path != null) {
			IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
			if (!folder.exists()) {
				folder.create(true, true, null);
			}
		}
	}

	/**
	 * 
	 * Creates a folder structure as defined in @param ymlFolderStructureDescriptor
	 * in @param rootPath folder
	 * 
	 * @param rootPath                     the root path to create folder structure
	 *                                     into
	 * @param ymlFolderStructureDescriptor the yaml file describing folder strucutre
	 *                                     to create
	 * @throws CredibilityException if an error occured
	 */
	public static void createFolderStructure(IPath rootPath, File ymlFolderStructureDescriptor)
			throws CredibilityException {

		// read evidence structure folder description file
		YmlReaderEvidenceFolderStructure ymlReader = new YmlReaderEvidenceFolderStructure();
		if (!ymlFolderStructureDescriptor.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_EVID_FOLDER_STRUCUTURE_NOTEXIST));
		}

		// test root path
		if (rootPath == null || rootPath.isEmpty()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_ROOTPATH_NOTEXIST));
		}

		// test yml folder structure description file
		Map<String, Object> folderStructure = null;
		try {
			folderStructure = ymlReader.readConfigurationFile(ymlFolderStructureDescriptor);
			createFolderStructure(rootPath, folderStructure);
		} catch (IOException e) {
			throw new CredibilityException(e.getMessage());
		}

	}

	/**
	 * The private method containing logic to create folder structure
	 * 
	 * @param rootPath        the root path to create folder structure into
	 * @param folderStructure the folder structure as a map
	 * @throws CredibilityException if an error occured
	 */
	@SuppressWarnings("unchecked")
	private static void createFolderStructure(IPath rootPath, Map<String, Object> folderStructure)
			throws CredibilityException {

		// test root path
		if (rootPath == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_ROOTPATH_NOTEXIST));
		}

		// test folder structure
		if (folderStructure == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_EVID_FOLDER_STRUCUTURE_NULL));
		}

		// create folder structure
		try {
			for (Entry<String, Object> entry : folderStructure.entrySet()) {
				if (entry != null) {
					String key = entry.getKey();
					createFolder(rootPath.append(key));
					Object object = entry.getValue();
					if (object instanceof Map) {
						Map<String, Object> structureToCreate = (Map<String, Object>) object;
						createFolderStructure(rootPath.append(key), structureToCreate);
					}
				}
			}
		} catch (CoreException e) {
			throw new CredibilityException(e.getMessage());
		}
	}

	/**
	 * Open the file relative to workspace
	 * 
	 * @param pathToFile the path for the file to open. The path must be relative to
	 *                   the workspace
	 */
	public static void openFileInWorkspace(String pathToFile) {
		// Retrieve file
		IPath filePath = new Path(pathToFile);

		if (filePath.segmentCount() < IPATH_MINIMUM_FILE_SEGMENT_LENGTH) {
			logger.debug("Impossible to open a file with path empty or with size < {}", //$NON-NLS-1$
					IPATH_MINIMUM_FILE_SEGMENT_LENGTH);
			return;
		}

		IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);

		// Get current page
		IWorkbenchPage page = getActivePage();

		// Open file using IDE
		try {

			// try to find a local program to open the file
			if (Program.findProgram(iFile.getFullPath().getFileExtension()) != null) {
				Program.launch(toOsPath(iFile.getFullPath()));
			} else {
				// else try to open it with an Eclipse editor
				IDE.openEditor(page, iFile);
			}
		} catch (PartInitException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					RscTools.getString(RscConst.ERR_FILETOOLS_OPENFILE_TITLE),
					RscTools.getString(RscConst.ERR_FILETOOLS_OPENFILE_DESC));
		}
	}

	/**
	 * Close the editor
	 * 
	 * @param editor the editor to close
	 * @param save   save the editor before closing?
	 */
	public static void closeEditor(IEditorPart editor, boolean save) {
		Display.getDefault().asyncExec(() -> {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			page.closeEditor(editor, save);
		});
	}
}
