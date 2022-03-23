/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;
import gov.sandia.cf.tools.ZipTools;

/**
 * The Class CFTmpFolderManager.
 * 
 * @author Didier Verstraete
 */
public class CFTmpFolderManager {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(CFTmpFolderManager.class);

	/** The Constant CREDIBILITY_TMP_FOLDER_ZIPPED_NAME. */
	public static final String CREDIBILITY_TMP_FOLDER_ZIPPED_NAME = ".cftmp"; //$NON-NLS-1$

	/** The Constant OLD_FILENAME_SUFFIX. */
	public static final String OLD_FILENAME_SUFFIX = "-old"; //$NON-NLS-1$

	/** The Constant CREDIBILITY_TMP_FOLDER_DEFAULT_PREFIX. */
	public static final String CREDIBILITY_TMP_FOLDER_DEFAULT_PREFIX = CREDIBILITY_TMP_FOLDER_ZIPPED_NAME
			+ RscTools.HYPHEN;

	/** The Constant CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME. */
	public static final String CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME = "data"; //$NON-NLS-1$

	/** The Constant CREDIBILITY_BACKUP_FOLDER_NAME. */
	public static final String CREDIBILITY_BACKUP_FOLDER_NAME = "backup"; //$NON-NLS-1$

	/** The Constant CREDIBILITY_EXTRACT_FOLDER_NAME. */
	public static final String CREDIBILITY_EXTRACT_FOLDER_NAME = "extract"; //$NON-NLS-1$

	/** The Constant CREDIBILITY_SAVE_FOLDER_NAME. */
	public static final String CREDIBILITY_SAVE_FOLDER_NAME = "save"; //$NON-NLS-1$

	/** The editor. */
	private CredibilityEditor editor;

	/** The data folder. */
	private String dataFolder;

	/**
	 * Instantiates a new CF tmp folder manager.
	 *
	 * @param editor the editor
	 */
	public CFTmpFolderManager(CredibilityEditor editor) {
		Assert.isNotNull(editor);
		this.editor = editor;
		this.dataFolder = getExistingDataFolderName();
	}

	/**
	 * Gets the data folder.
	 *
	 * @return the data folder
	 */
	private String getExistingDataFolderName() {

		String dataFolderName = CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME;
		File tempFolder = getTempFolder();

		if (tempFolder != null && tempFolder.exists()) {

			File[] listFiles = tempFolder.listFiles(pathname -> pathname != null && pathname.getName() != null
					&& pathname.getName().startsWith(CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME));

			if (listFiles != null && listFiles.length > 0) {

				Optional<File> lastDataFolder = Stream.of(listFiles).max(Comparator.comparing(File::getName));
				if (lastDataFolder.isPresent()) {
					dataFolderName = lastDataFolder.get().getName();
				}
			}
		}
		return dataFolderName;
	}

	/**
	 * @return the temporary folder for the specified cf file
	 */
	public IFolder getTempIFolder() {
		if (editor.getInputFile() != null && editor.getInputFile().getParent() != null) {
			IPath cfTmpFolderPath = editor.getInputFile().getParent().getFullPath()
					.append(CREDIBILITY_TMP_FOLDER_DEFAULT_PREFIX + editor.getInputFile().getName());
			return WorkspaceTools.getFolderInWorkspaceForPath(cfTmpFolderPath);
		} else {
			return null;
		}
	}

	/**
	 * Gets the temp I path.
	 *
	 * @return the temporary folder for the specified cf file
	 */
	public IPath getTempIPath() {
		IFolder iFolder = getTempIFolder();
		return iFolder != null ? iFolder.getFullPath() : null;
	}

	/**
	 * @return the temporary folder for the specified cf file
	 */
	public File getTempFolder() {
		if (editor.getInputFile() != null && editor.getInputFile().getParent() != null) {
			return WorkspaceTools.toFile(getTempIFolder());
		} else {
			return null;
		}
	}

	/**
	 * Gets the temp folder path.
	 *
	 * @return the temporary folder for the specified cf file
	 */
	public String getTempFolderPath() {
		File tmpFolder = getTempFolder();
		return tmpFolder != null ? tmpFolder.getAbsolutePath() : null;
	}

	/**
	 * Gets the database path for project.
	 *
	 * @return the database path for project
	 */
	public String getCurrentDatabasePath() {

		File databaseFolder = getCurrentDatabaseFolder();

		return databaseFolder != null ? databaseFolder.getAbsolutePath() : null;
	}

	/**
	 * Gets the current database folder for project.
	 *
	 * @return the current database folder for project
	 */
	public File getCurrentDatabaseFolder() {

		File tempFolder = getTempFolder();

		if (tempFolder == null) {
			return null;
		}

		return new File(tempFolder, dataFolder);
	}

	/**
	 * Exists.
	 *
	 * @return true, if successful
	 */
	public boolean exists() {
		File cfTmpFolder = getTempFolder();
		return cfTmpFolder != null && cfTmpFolder.exists();
	}

	/**
	 * Create the working directory for this cf editor.
	 *
	 * @throws CredibilityException if a functional error occured
	 * @throws IOException          if a file exception occured
	 */
	public void createWorkingDir() throws CredibilityException, IOException {

		if (editor.getInputFile() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_OPEN_TMPFOLDERCREATIONUNSUCCESSFUL,
					"null", RscTools.empty())); //$NON-NLS-1$
		}

		// get cf working dir
		File cfTmpFolder = getTempFolder();

		if (cfTmpFolder == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_OPEN_TMPFOLDERCREATIONUNSUCCESSFUL,
					editor.getInputFile().getFullPath(), RscTools.empty()));
		}

		// create the temporary folder
		if (!cfTmpFolder.exists()) {

			Files.createDirectory(cfTmpFolder.toPath());
			logger.debug("Creating temp directory {}", cfTmpFolder.getAbsolutePath()); //$NON-NLS-1$

			if (!cfTmpFolder.exists()) {
				throw new CredibilityException(
						RscTools.getString(RscConst.EX_CREDEDITOR_OPEN_TMPFOLDERCREATIONUNSUCCESSFUL,
								editor.getInputFile().getFullPath(), cfTmpFolder));
			}
		}

		// unzip the content of the cf file into the temporary folder extract folder
		File extractFolder = new File(cfTmpFolder, CREDIBILITY_EXTRACT_FOLDER_NAME);
		if (extractFolder.exists()) {
			FileTools.deleteDirectoryRecursively(extractFolder);
			logger.debug("Delete existing 'extract' folder"); //$NON-NLS-1$
		}
		logger.debug("Create 'extract' folder"); //$NON-NLS-1$
		Files.createDirectory(extractFolder.toPath());
		logger.debug("Unzip {} content into 'extract' folder", editor.getInputFile()); //$NON-NLS-1$
		ZipTools.unzip(WorkspaceTools.toFile(editor.getInputFile()), extractFolder);

		// #FIX: issue #116: the content of the *.cf file can have a folder called
		// .cftmp or not. If the folder is present, delete it
		File cfTmpZippedFolder = new File(extractFolder, CREDIBILITY_TMP_FOLDER_ZIPPED_NAME);
		if (cfTmpZippedFolder.exists()) {
			logger.info("Apply patch to move '.cftmp' folder content to the temporary folder root"); //$NON-NLS-1$
			FileTools.move(cfTmpZippedFolder, extractFolder);
		}

		// #FIX: #537 test existence of locked "data" folder
		File extractDataFolder = new File(extractFolder, CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME);
		dataFolder = getNewDataFolderName();
		if (extractDataFolder.exists() && !CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME.equals(dataFolder)) {
			logger.warn("The default database folder 'data' seems to be locked by another process." //$NON-NLS-1$
					+ " Using the following database folder '{}'", dataFolder);//$NON-NLS-1$
			// move the extracted data folder name to the new available one
			File newExtractDataFolder = new File(extractFolder, dataFolder);
			Files.move(extractDataFolder.toPath(), newExtractDataFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		// move the extract folder content in the temp folder
		logger.debug("Moving 'extract' folder content to the temporary folder root (replace existing files)"); //$NON-NLS-1$
		FileTools.move(extractFolder, cfTmpFolder);

		logger.info("Unzipping cf file ({}) and creating new temporary folder for cf project at: {}", //$NON-NLS-1$
				editor.getInputFile().getFullPath(), cfTmpFolder.getPath());
	}

	/**
	 * Gets the new database path for project.
	 *
	 * @return the new database path for project
	 */
	private String getNewDataFolderName() {

		File tempFolder = getTempFolder();

		if (tempFolder == null) {
			return null;
		}

		// default data folder
		File defaultDataFolder = new File(tempFolder, CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME);

		// timestamp suffix
		String suffix = DateTools.getDateFormattedDateTimeHash();

		return CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME + (defaultDataFolder.exists() ? suffix : RscTools.empty());
	}

	/**
	 * Save to zip.
	 *
	 * @throws CredibilityException the credibility exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 * @throws CoreException        the core exception
	 */
	public void saveToZip() throws CredibilityException, IOException, CoreException {

		File tmpFolder = getTempFolder();

		if (editor.getInputFile() == null || tmpFolder == null) {
			return;
		}

		// prepare save folder to zip
		File saveFolder = new File(tmpFolder, CREDIBILITY_SAVE_FOLDER_NAME);
		if (saveFolder.exists()) {
			FileTools.deleteDirectoryRecursively(saveFolder);
			logger.debug("Delete existing 'save' directory"); //$NON-NLS-1$
		}
		Files.createDirectory(saveFolder.toPath());
		logger.debug("Create 'save' directory"); //$NON-NLS-1$

		// copy content except "data" and "save"
		File[] listFilesExceptData = tmpFolder.listFiles(pathname -> pathname != null && pathname.getName() != null
				&& !pathname.getName().startsWith(CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME)
				&& !pathname.getName().startsWith(CREDIBILITY_SAVE_FOLDER_NAME));
		if (listFilesExceptData != null && listFilesExceptData.length > 0) {
			logger.debug("Copy extra content into 'save' directory"); //$NON-NLS-1$
			for (File source : listFilesExceptData) {
				FileTools.copyDirectory(source, saveFolder.toPath().resolve(source.toPath().getFileName()).toFile());
			}
		}

		// copy latest data folder to "data" in the save folder
		// the .cf file must have a clean "data" folder
		File currentDatabaseFolder = getCurrentDatabaseFolder();
		if (currentDatabaseFolder != null) {
			logger.debug("Copy data content into 'save' directory"); //$NON-NLS-1$
			FileTools.copyDirectory(currentDatabaseFolder,
					new File(saveFolder, CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME));
		}

		// zip cf content to cf file
		if (tmpFolder.exists()) {
			File[] listFiles = saveFolder.listFiles();
			if (listFiles != null && listFiles.length > 0) {
				logger.debug("Zip 'save' directory content into {}", editor.getInputFile().getFullPath()); //$NON-NLS-1$
				ZipTools.zipFile(Arrays.asList(listFiles), editor.getInputFile().getFullPath());
			} else {
				logger.warn("The cf working directory is empty. There is nothing to save."); //$NON-NLS-1$
			}
		} else {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CREDEDITOR_SAVE_TMPFOLDERNULL,
					editor.getInputFile().getFullPath(), tmpFolder));
		}

		// delete save folder
		FileTools.deleteDirectoryRecursively(saveFolder);
		logger.debug("Delete 'save' directory"); //$NON-NLS-1$
	}

	/**
	 * Delete temp folder.
	 */
	public void deleteTempFolder() {

		// remove cf temporary folder
		IFolder cfTmpIFolder = getTempIFolder();
		if (cfTmpIFolder != null && cfTmpIFolder.exists()) {

			// try to delete with Eclipse Resources Plugin
			try {
				cfTmpIFolder.delete(true, new NullProgressMonitor());
				ResourcesPlugin.getWorkspace().save(true, new NullProgressMonitor());
			} catch (CoreException e) {
				logger.error("Impossible to delete the temporary folder with Eclipse workspace {}", //$NON-NLS-1$
						cfTmpIFolder.getFullPath(), e);
			}
		}

		// if the temp folder is still there delete it recursively with java Files
		// methods
		File tmpFolder = getTempFolder();
		if (tmpFolder != null && tmpFolder.exists()) {
			try {
				FileTools.deleteDirectoryRecursively(tmpFolder);
			} catch (IOException e) {
				logger.error("Impossible to delete the temporary folder {}", tmpFolder.getAbsolutePath(), e);//$NON-NLS-1$
			}
		}
	}

	/**
	 * Gets the temp folder.
	 *
	 * @param inputFile the input file
	 * @return the temp folder
	 */
	public static IFolder getTempFolder(IFile inputFile) {
		if (inputFile != null && inputFile.getParent() != null) {
			IPath cfTmpFolderPath = inputFile.getParent().getFullPath()
					.append(CREDIBILITY_TMP_FOLDER_DEFAULT_PREFIX + inputFile.getName());
			return WorkspaceTools.getFolderInWorkspaceForPath(cfTmpFolderPath);
		} else {
			return null;
		}
	}

	/**
	 * Gets the database path for project.
	 *
	 * @param cfProjectPath the cf project path
	 * @return the database path for project
	 */
	public static String getDefaultDatabasePathForProject(String cfProjectPath) {
		return new File(cfProjectPath, CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME).getAbsolutePath();
	}
}
