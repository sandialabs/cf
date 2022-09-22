/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.WorkspaceTools;
import gov.sandia.cf.tools.ZipTools;
import junit.runner.Version;

/**
 * The Class CFTmpFolderManagerTest.
 *
 * @author Didier Verstraete
 */
class CFTmpFolderManagerTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(CFTmpFolderManagerTest.class);

	/**
	 * temporary folder to store the hsqldb database
	 */
	@Rule
	private static TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

	/**
	 * Initialize the test
	 */
	@BeforeAll
	public static void initialize() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		try {
			logger.info("Test started"); //$NON-NLS-1$

			TEMP_FOLDER.create();

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Initialize the test
	 */
	@BeforeEach
	public void beforeTest() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		try {
			logger.info("Test started"); //$NON-NLS-1$
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Clean the test
	 */
	@AfterEach
	public void afterTest() {
		// delete workspace files
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (project.exists()) {
				try {
					project.delete(true, new NullProgressMonitor());
				} catch (CoreException e) {
					logger.warn(e.getMessage());
				}
			}
		}
	}

	/**
	 * Clean the test
	 */
	@AfterAll
	public static void clean() {
		TEMP_FOLDER.delete();
		assertFalse("Directory still exists", TEMP_FOLDER.getRoot().exists()); //$NON-NLS-1$
		logger.info("Test ending"); //$NON-NLS-1$
	}

	@Test
	void test_constructor_ok() throws CoreException {

		IFile newFile = TestEntityFactory.getNewFile("Project", "myFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$

		CredibilityEditor credibilityEditor = mock(CredibilityEditor.class);
		when(credibilityEditor.getInputFile()).thenReturn(newFile);

		CFTmpFolderManager cfTmpMgr = new CFTmpFolderManager(credibilityEditor);
		assertNotNull(cfTmpMgr.getTempFolder());
		assertEquals(CFTmpFolderManager.CREDIBILITY_TMP_FOLDER_DEFAULT_PREFIX + "myFile.cf", //$NON-NLS-1$
				cfTmpMgr.getTempFolder().getName());
		assertFalse(cfTmpMgr.exists());

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_createWorkingDir_ok() throws CoreException, CredibilityException, IOException {

		IFile newFile = TestEntityFactory.getNewFile("Project", "myFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$

		CredibilityEditor credibilityEditor = mock(CredibilityEditor.class);
		when(credibilityEditor.getInputFile()).thenReturn(newFile);

		CFTmpFolderManager cfTmpMgr = new CFTmpFolderManager(credibilityEditor);
		assertFalse(cfTmpMgr.exists());

		// create
		cfTmpMgr.createWorkingDir();
		assertEquals(CFTmpFolderManager.CREDIBILITY_TMP_FOLDER_DEFAULT_PREFIX + "myFile.cf", //$NON-NLS-1$
				cfTmpMgr.getTempFolder().getName());
		assertTrue(cfTmpMgr.exists());

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_deleteTempFolder_ok() throws CoreException, CredibilityException, IOException {

		IFile newFile = TestEntityFactory.getNewFile("Project", "myFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$

		CredibilityEditor credibilityEditor = mock(CredibilityEditor.class);
		when(credibilityEditor.getInputFile()).thenReturn(newFile);

		CFTmpFolderManager cfTmpMgr = new CFTmpFolderManager(credibilityEditor);
		assertFalse(cfTmpMgr.exists());
		cfTmpMgr.createWorkingDir();
		assertEquals(CFTmpFolderManager.CREDIBILITY_TMP_FOLDER_DEFAULT_PREFIX + "myFile.cf", //$NON-NLS-1$
				cfTmpMgr.getTempFolder().getName());
		assertTrue(cfTmpMgr.exists());

		// delete
		cfTmpMgr.deleteTempFolder();
		assertFalse(cfTmpMgr.exists());

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_getCurrentDatabasePath_ok() throws CoreException, CredibilityException, IOException {
		IFile newFile = TestEntityFactory.getNewFile("Project", "myFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$

		CredibilityEditor credibilityEditor = mock(CredibilityEditor.class);
		when(credibilityEditor.getInputFile()).thenReturn(newFile);

		CFTmpFolderManager cfTmpMgr = new CFTmpFolderManager(credibilityEditor);
		String currentDatabasePath = cfTmpMgr.getCurrentDatabasePath();
		assertEquals(new File(cfTmpMgr.getTempFolder(), CFTmpFolderManager.CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME)
				.getAbsolutePath(), currentDatabasePath);

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_getCurrentDatabasePath_existingDataFolder() throws CoreException, CredibilityException, IOException {

		IFile newFile = TestEntityFactory.getNewFile("Project", "myFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$

		CredibilityEditor credibilityEditor = mock(CredibilityEditor.class);
		when(credibilityEditor.getInputFile()).thenReturn(newFile);

		CFTmpFolderManager cfTmpMgr = new CFTmpFolderManager(credibilityEditor);

		// create data folder first
		File tmpFolder = WorkspaceTools.toFile(CFTmpFolderManager.getTempFolder(newFile));
		Files.createDirectory(tmpFolder.toPath());
		File dataFolder = new File(cfTmpMgr.getTempFolder(),
				CFTmpFolderManager.CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME);
		Files.createDirectory(dataFolder.toPath());
		File credibilityLockFile = new File(dataFolder, "credibility.lck"); //$NON-NLS-1$
		Files.createFile(credibilityLockFile.toPath());
		File credibilityTxtFile = new File(dataFolder, "credibility.txt"); //$NON-NLS-1$
		Files.createFile(credibilityTxtFile.toPath());

		cfTmpMgr.createWorkingDir();
		String currentDatabasePath = cfTmpMgr.getCurrentDatabasePath();

		// not the default one
		assertNotEquals(new File(cfTmpMgr.getTempFolder(), CFTmpFolderManager.CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME)
				.getAbsolutePath(), currentDatabasePath);
		assertTrue(cfTmpMgr.getCurrentDatabaseFolder().getName()
				.startsWith(CFTmpFolderManager.CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_saveToZip_ok() throws CoreException, CredibilityException, IOException {

		IFile newFile = TestEntityFactory.getNewFile("Project", "myFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$

		CredibilityEditor credibilityEditor = mock(CredibilityEditor.class);
		when(credibilityEditor.getInputFile()).thenReturn(newFile);

		CFTmpFolderManager cfTmpMgr = new CFTmpFolderManager(credibilityEditor);
		assertFalse(cfTmpMgr.exists());
		cfTmpMgr.createWorkingDir();

		File dataFolder = new File(cfTmpMgr.getTempFolder(),
				CFTmpFolderManager.CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME);
		Files.createDirectory(dataFolder.toPath());
		File credibilityScriptFile = new File(dataFolder, "credibility.script"); //$NON-NLS-1$
		Files.createFile(credibilityScriptFile.toPath());
		File credibilityLogFile = new File(dataFolder, "credibility.log"); //$NON-NLS-1$
		Files.createFile(credibilityLogFile.toPath());

		// save to zip
		cfTmpMgr.saveToZip();

		File inputFile = WorkspaceTools.toFile(newFile);
		assertTrue(inputFile.exists());

		// extract zip
		File extractFolder = new File(cfTmpMgr.getTempFolder(), CFTmpFolderManager.CREDIBILITY_EXTRACT_FOLDER_NAME);
		Files.createDirectory(extractFolder.toPath());
		ZipTools.unzip(inputFile, extractFolder);
		assertEquals(1, extractFolder.listFiles().length);
		assertTrue(Stream.of(extractFolder.listFiles())
				.allMatch(f -> f.getName().equals(CFTmpFolderManager.CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME)));
		File extractDataFolder = new File(extractFolder, CFTmpFolderManager.CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME);
		assertEquals(2, extractDataFolder.listFiles().length);
		assertTrue(Stream.of(extractDataFolder.listFiles()).anyMatch(f -> f.getName().equals("credibility.script"))); //$NON-NLS-1$
		assertTrue(Stream.of(extractDataFolder.listFiles()).anyMatch(f -> f.getName().equals("credibility.log"))); //$NON-NLS-1$

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_saveToZip_existingDataFolder() throws CoreException, CredibilityException, IOException {

		IFile newFile = TestEntityFactory.getNewFile("Project", "myFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$

		CredibilityEditor credibilityEditor = mock(CredibilityEditor.class);
		when(credibilityEditor.getInputFile()).thenReturn(newFile);

		CFTmpFolderManager cfTmpMgr = new CFTmpFolderManager(credibilityEditor);
		assertFalse(cfTmpMgr.exists());

		// create data folder first
		File tmpFolder = WorkspaceTools.toFile(CFTmpFolderManager.getTempFolder(newFile));
		Files.createDirectory(tmpFolder.toPath());
		File dataFolder = new File(cfTmpMgr.getTempFolder(),
				CFTmpFolderManager.CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME);
		Files.createDirectory(dataFolder.toPath());
		File credibilityLockFile = new File(dataFolder, "credibility.lck"); //$NON-NLS-1$
		Files.createFile(credibilityLockFile.toPath());
		File credibilityTxtFile = new File(dataFolder, "credibility.txt"); //$NON-NLS-1$
		Files.createFile(credibilityTxtFile.toPath());

		// create
		cfTmpMgr.createWorkingDir();

		File dataFolder2 = cfTmpMgr.getCurrentDatabaseFolder();
		Files.createDirectory(dataFolder2.toPath());
		File credibilityScriptFile = new File(dataFolder2, "credibility.script"); //$NON-NLS-1$
		Files.createFile(credibilityScriptFile.toPath());
		File credibilityLogFile = new File(dataFolder2, "credibility.log"); //$NON-NLS-1$
		Files.createFile(credibilityLogFile.toPath());

		// save to zip
		cfTmpMgr.saveToZip();

		File inputFile = WorkspaceTools.toFile(newFile);
		assertTrue(inputFile.exists());

		// extract zip
		File extractFolder = new File(cfTmpMgr.getTempFolder(), CFTmpFolderManager.CREDIBILITY_EXTRACT_FOLDER_NAME);
		Files.createDirectory(extractFolder.toPath());
		ZipTools.unzip(inputFile, extractFolder);
		// zip extract folder contains one data folder matching dataXXXXX folder (get
		// the latest data folder)
		assertEquals(1, extractFolder.listFiles().length);
		assertTrue(Stream.of(extractFolder.listFiles())
				.allMatch(f -> f.getName().equals(CFTmpFolderManager.CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME)));
		// zip data in extract folder contains two files credibility.script and
		// credibility.log
		assertEquals(2, cfTmpMgr.getCurrentDatabaseFolder().listFiles().length);
		assertTrue(Stream.of(cfTmpMgr.getCurrentDatabaseFolder().listFiles())
				.anyMatch(f -> f.getName().equals("credibility.script"))); //$NON-NLS-1$
		assertTrue(Stream.of(cfTmpMgr.getCurrentDatabaseFolder().listFiles())
				.anyMatch(f -> f.getName().equals("credibility.log"))); //$NON-NLS-1$

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}
}
