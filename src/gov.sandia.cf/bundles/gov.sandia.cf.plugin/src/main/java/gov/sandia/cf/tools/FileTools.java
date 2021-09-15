/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.program.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.exceptions.CredibilityException;

/**
 * 
 * This class is a file toolbox
 * 
 * @author Didier Verstraete
 *
 */
public class FileTools {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileTools.class);

	/**
	 * The credibility temporary folder name in the zip
	 */
	public static final String CREDIBILITY_TMP_FOLDER_ZIPPED_NAME = ".cftmp"; //$NON-NLS-1$

	/**
	 * the old file suffix to keep a backup during save phase
	 */
	public static final String OLD_FILENAME_SUFFIX = "-old"; //$NON-NLS-1$

	/**
	 * The credibility temporary folder prefix unzipped
	 */
	public static final String CREDIBILITY_TMP_FOLDER_DEFAULT_PREFIX = CREDIBILITY_TMP_FOLDER_ZIPPED_NAME
			+ RscTools.HYPHEN;

	/**
	 * The credibility database folder name
	 */
	public static final String CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME = "data"; //$NON-NLS-1$

	/**
	 * The credibility backup folder name
	 */
	public static final String CREDIBILITY_BACKUP_FOLDER_NAME = "backup"; //$NON-NLS-1$

	/**
	 * The file to link with the database and to open the credibility project
	 */
	/** CF file extension */
	public static final String CREDIBILITY_FILE_EXTENSION = "cf"; //$NON-NLS-1$
	/** CF file extension with DOT */
	public static final String CREDIBILITY_FILE_DOT_EXTENSION = "." + CREDIBILITY_FILE_EXTENSION; //$NON-NLS-1$

	/**
	 * Files Yaml extensions
	 */
	/** yml file extension for filters */
	public static final String YML_FILTER = "*.yml"; //$NON-NLS-1$
	/** yaml file extension for filters */
	public static final String YAML_FILTER = "*.yaml"; //$NON-NLS-1$

	/**
	 * Files extensions
	 */
	/** WORD 1995-2003 extension */
	public static final String WORD_1995 = ".doc"; //$NON-NLS-1$
	/** WORD 2007+ extension */
	public static final String WORD_2007 = ".docx"; //$NON-NLS-1$
	/** LATEX extension */
	public static final String LATEX = ".tex"; //$NON-NLS-1$
	/** PDF extension */
	public static final String PDF = ".pdf"; //$NON-NLS-1$
	/** YML extension */
	public static final String YML = ".yml"; //$NON-NLS-1$
	/** YAML extension */
	public static final String YAML = ".yaml"; //$NON-NLS-1$

	/**
	 * the dot
	 */
	public static final String DOT = "."; //$NON-NLS-1$

	/**
	 * PATH_SEPARATOR constant
	 */
	public static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$

	/**
	 * The resource folders
	 */
	/** ARG command */
	public static final String FILES_ARG = "arg"; //$NON-NLS-1$
	private static final String FILES_DOCUMENTS = "documents"; //$NON-NLS-1$
	private static final String FILES_CONFIGURATION = "configuration"; //$NON-NLS-1$

	/**
	 * The PIRT Reference file
	 */
	public static final String FILE_PIRT_REFERENCE = FILES_DOCUMENTS + PATH_SEPARATOR
			+ "PIRT_WhatIs_SAND2016-6466TR_V1.1_UUR.pdf"; //$NON-NLS-1$

	/**
	 * The PCMM Reference file
	 */
	public static final String FILE_PCMM_REFERENCE = FILES_DOCUMENTS + PATH_SEPARATOR
			+ "PCMM_WhatIs_SAND2016-7399TR_V1.1_UUR.pdf"; //$NON-NLS-1$

	/**
	 * The credibility evidence structure folder yml file
	 */
	public static final String FILE_CREDIBILITY_EVIDENCE_FOLDER_STRUCTURE = FILES_CONFIGURATION + PATH_SEPARATOR
			+ "Credibility_Evidence_Folder_Structure.yml"; //$NON-NLS-1$

	/**
	 * Private constructor to not allow instantiation.
	 */
	private FileTools() {
	}

	/**
	 * @param path0 the first path
	 * @param path  the path array (optional)
	 * @return the file if found in the workspace or on filesystem, otherwise null
	 */
	public static File findFileInWorkspaceOrSystem(String path0, String... path) {

		if (path0 == null && (path == null || path.length <= 0)) {
			return null;
		}

		File rscFile = null;

		// find file in workspace
		IPath rscIPath = new org.eclipse.core.runtime.Path(path0);
		for (int i = 0; i < path.length; i++) {
			rscIPath = rscIPath.append(path[i]);
		}
		IResource resource = WorkspaceTools.findFirstResourceInWorkspace(rscIPath);
		if (resource != null) {
			rscFile = WorkspaceTools.toFile(resource);
		}

		// find file on system
		if (rscFile == null || !rscFile.exists()) {
			try {
				java.nio.file.Path rscPath = Paths.get(path0, path);
				if (rscPath != null && rscPath.toFile().exists()) {
					rscFile = rscPath.toFile();
				}
			} catch (InvalidPathException e) {
				logger.warn("File not found {}", String.join("/", path)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		return rscFile;
	}

	/**
	 * @param path the path to normalize
	 * @return the path string of the path in parameter
	 */
	public static String getNormalizedPath(String path) {
		String pathString = null;
		if (path != null) {
			pathString = path.replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return pathString;
	}

	/**
	 * @param path the path to normalize
	 * @return the path string of the path in parameter
	 */
	public static String getNormalizedPath(java.nio.file.Path path) {
		String pathString = null;
		if (path != null) {
			pathString = path.normalize().toString().replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return pathString;
	}

	/**
	 * @param filepath the filepath to create file
	 * @return a new file on disk from filepath param. If the file already exists,
	 *         the method do not override it, but returns the file
	 * @throws CredibilityException if a parameter is not valid
	 * @throws IOException          if an error occured during file creation
	 */
	public static File createFile(String filepath) throws CredibilityException, IOException {

		if (filepath == null || filepath.isEmpty()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL));
		}

		File file = new File(filepath);

		if (!file.exists()) {
			boolean newFileCreated = file.createNewFile();
			if (!newFileCreated) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_CREATEFILE_UNSUCCESSFUL, file));
			}
			logger.debug("A new file has been created: {}", filepath); //$NON-NLS-1$
		}

		return file;
	}

	/**
	 * @param directory the directory to check
	 * @param extension the file extension to search
	 * @return true if there is files with @param extension in @param directory
	 */
	public static boolean existFilesWithExtensionInFolder(File directory, String extension) {
		if (directory != null && directory.exists()) {
			File[] listFiles = directory.listFiles((dir, filename) -> filename.endsWith(DOT + extension));
			return listFiles != null && listFiles.length > 0;
		}
		return false;
	}

	/**
	 * @param directory the directory to delete
	 * @return true if directory and all files associated deletion is ok, otherwise
	 *         false
	 * @throws IOException if an error occured during file deletion
	 */
	public static boolean deleteDirectoryRecursively(File directory) throws IOException {
		if (directory == null) {
			return false;
		}

		File[] allContents = directory.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectoryRecursively(file);
			}
		}

		Files.delete(directory.toPath());
		logger.debug("deleted: {}", directory); //$NON-NLS-1$

		return true;
	}

	/**
	 * Move the current directory to directory
	 * 
	 * @param target the target
	 * @param source the source to move
	 * @throws CredibilityException if a parameter is not valid.
	 */
	public static void move(File target, File source) throws CredibilityException {

		if (source == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_FILETOOLS_MOVE_SOURCENULL, target));
		}

		File[] listFiles = source.listFiles();
		if (listFiles != null && listFiles.length > 0) {
			for (File file : listFiles) {
				moveResource(target, file);
			}
		}

		if (source.isDirectory() && source.exists()) {
			try {
				Files.delete(source.toPath());
			} catch (IOException e) {
				throw new CredibilityException(
						RscTools.getString(RscConst.EX_FILETOOLS_MOVE_DELETEPREVRSCUNSUCCESSFUL, source, target));
			}
		}
	}

	/**
	 * Move a resource depending of its type (directory or file)
	 * 
	 * @param target the target
	 * @param file   the resource to move
	 * @throws CredibilityException if a parameter is not valid.
	 */
	private static void moveResource(File target, File file) throws CredibilityException {
		if (file != null) {
			if (file.isDirectory()) {
				moveDirectory(target, file);
			} else {
				moveFile(target, file);
			}
		}
	}

	/**
	 * Move a directory and all its content to its target.
	 * 
	 * @param target    the target
	 * @param directory the directory to move
	 * @throws CredibilityException if a parameter is not valid.
	 */
	private static void moveDirectory(File target, File directory) throws CredibilityException {
		boolean created = true;
		File newDirectory = new File(target, directory.getName());
		if (!newDirectory.exists()) {
			created = newDirectory.mkdir();
			if (!created) {
				logger.warn("Impossible to move to {}: directory {} can not be created.", target, directory);//$NON-NLS-1$
			}
		}

		if (created) {
			move(newDirectory, directory);
		}
	}

	/**
	 * Move a file to its target.
	 * 
	 * @param target the target file
	 * @param file   the file to move
	 */
	private static void moveFile(File target, File file) {
		boolean renamed = file.renameTo(new File(target, file.getName()));
		if (!renamed) {
			logger.warn("Impossible to move to {}: file {} can not be renamed.", target, file); //$NON-NLS-1$
		}
	}

	/**
	 * @return the pirt reference file path
	 * @throws URISyntaxException if the file name is not valid.
	 * @throws IOException        if an error occured during file writing.
	 */
	public static String getPIRTReferenceFilePath() throws URISyntaxException, IOException {
		return WorkspaceTools.getStaticFilePath(FILE_PIRT_REFERENCE);
	}

	/**
	 * @return the pirt reference file path
	 * @throws URISyntaxException if the file name is not valid.
	 * @throws IOException        if an error occured during file writing.
	 */
	public static String getPCMMReferenceFilePath() throws URISyntaxException, IOException {
		return WorkspaceTools.getStaticFilePath(FILE_PCMM_REFERENCE);
	}

	/**
	 * Open the file on the filesystem
	 * 
	 * @param file the file to open
	 */
	public static void openFile(File file) {

		// try to find a local program to open the file
		if (file != null && Program.findProgram(getExtensionByFileName(file.getName())) != null) {
			Program.launch(file.getPath());
		}
	}

	/**
	 * @param filename the file name
	 * @return the file extension
	 */
	public static String getExtensionByFileName(String filename) {
		return Optional.ofNullable(filename).filter(f -> f.contains(".")) //$NON-NLS-1$
				.map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse(RscTools.empty()); //$NON-NLS-1$
	}

	/**
	 * @param filename  the file name
	 * @param extension the file extension
	 * @return the file extension
	 */
	public static boolean hasExtension(String filename, String extension) {
		return extension != null && filename != null
				&& extension.replace(RscTools.DOT, RscTools.empty()).equals(getExtensionByFileName(filename));
	}

	/**
	 * @param osPath the os path
	 * @return a string containing the path of the parameter string minus the last
	 *         segment of the path
	 */
	public static String removeFilenameFromPath(String osPath) {

		IPath pathToReturn = null;
		String osString = RscTools.empty();
		// get the parent path of the valid config file
		if (osPath != null) {
			pathToReturn = removeFilenameFromPath(org.eclipse.core.runtime.Path.fromOSString(osPath));
			if (pathToReturn != null) {
				osString = pathToReturn.toPortableString();
			}
		}

		return osString;
	}

	/**
	 * @param path the path
	 * @return a IPath containing the path of the parameter path minus the last
	 *         segment of the path
	 */
	public static IPath removeFilenameFromPath(IPath path) {
		IPath pathToReturn = null;
		if (path != null && path.segmentCount() > 0) {
			pathToReturn = path.removeLastSegments(1);
		}

		return pathToReturn;
	}

	/**
	 * Write (append) the string in parameter into the file in parameter
	 * 
	 * @param file    the file to write
	 * @param toWrite the string to write
	 * @param append  append to the existing file or erase content
	 * @throws IOException if an error occured during file writing.
	 */
	public static void writeStringInFile(File file, String toWrite, boolean append) throws IOException {

		if (file != null && file.exists()) {
			// Open given file in append mode.
			try (BufferedWriter out = new BufferedWriter(new FileWriter(file, append))) {
				out.write(toWrite);
			}
		}
	}

	/**
	 * Get the last updated date for a file
	 * 
	 * @param filePath the file path
	 * @return the last updated date for the file
	 */
	public static boolean isPathValidInWorkspace(String filePath) {

		IFile iFile = null;
		if (filePath != null) {
			// Initialize
			IPath path = new Path(filePath);
			try {
				iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			} catch (IllegalArgumentException e) {
				logger.warn("Path: {} is not a valid path:\n{}", filePath, e.getMessage(), e); //$NON-NLS-1$
				return false;
			}
		}

		// Result
		return iFile != null && iFile.exists();

	}

	/**
	 * Get the last updated date for a file
	 * 
	 * @param path the file path
	 * @return the last updated date for the file
	 */
	public static Date getLastUpdatedDate(IPath path) {
		// Initialize
		Date lastUpdatedDate = null;
		IFile iFile = null;
		try {
			iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		} catch (IllegalArgumentException e) {
			logger.warn("Path: {} is not a valid path:\n{}", path, e.getMessage(), e); //$NON-NLS-1$
		}

		// Check file exists
		if (iFile != null && iFile.exists()) {
			// Get last updated date
			long lastUpdatedTimestamp = iFile.getRawLocation().makeAbsolute().toFile().lastModified();
			lastUpdatedDate = new Date(lastUpdatedTimestamp);
		}

		// Result
		return lastUpdatedDate;
	}

	/**
	 * Check if the filePath exist
	 * 
	 * @param filePath the file path
	 * @return true if exists
	 */
	public static Boolean filePathExist(String filePath) {
		// Initialize
		IPath path = new Path(filePath);
		IFile iFile = null;
		try {
			iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		} catch (IllegalArgumentException e) {
			logger.info("Path: {} is not a valid path:\n{}", filePath, e.getMessage(), e); //$NON-NLS-1$
		}
		// Check file exists
		return iFile != null && iFile.exists();
	}

	/**
	 * @param path the path to prefix
	 * @return the path prefixed by the workspace variable
	 */
	public static String prefixWorkspaceVar(final String path) {

		if (path == null) {
			return null;
		}

		StringBuilder strBld = new StringBuilder(CFVariable.WORKSPACE.get());
		if (!path.startsWith(PATH_SEPARATOR)) {
			strBld.append(PATH_SEPARATOR);
		}
		strBld.append(path);

		return strBld.toString();
	}

	/**
	 * @param path the path to get parent for
	 * @return the parent folder of the path
	 */
	public static String getParentFolder(String path) {
		if (StringUtils.isBlank(path)) {
			return null;
		}

		File file = new File(path);
		return file.getParent();
	}

	/**
	 * @param path the path to get file or directory name
	 * @return the name of the path (directory or file)
	 */
	public static String getName(String path) {
		if (StringUtils.isBlank(path)) {
			return null;
		}

		File file = new File(path);
		return file.getName();
	}

	/**
	 * @param path the path to get file name
	 * @return the name of the path (directory or file)
	 */
	public static String getFileName(String path) {
		if (StringUtils.isBlank(path)) {
			return null;
		}

		File file = new File(path);
		return file.isFile() ? file.getName() : RscTools.empty();
	}

	/**
	 * @param path the path to check
	 * @return true if the file referenced by path is a word document
	 */
	public static boolean isWordDocument(String path) {
		String extension = getExtensionByFileName(path);
		if (extension != null) {
			extension = RscTools.DOT + extension;
		}
		return extension != null && (extension.equals(WORD_1995) || extension.equals(WORD_2007));
	}

	/**
	 * @param toAppend the array of path to append (follow the order)
	 * @return the append path as a string
	 */
	public static String append(String... toAppend) {

		StringBuilder strBld = new StringBuilder();

		if (toAppend != null) {
			for (String path : toAppend) {
				if (!strBld.toString().isEmpty() && !strBld.toString().endsWith(FileTools.PATH_SEPARATOR)
						&& !path.startsWith(FileTools.PATH_SEPARATOR)) {
					strBld.append(FileTools.PATH_SEPARATOR);
				}
				strBld.append(path);
			}
		}
		return strBld.toString();
	}
}
