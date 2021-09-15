/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.tests.TestConstants;
import junit.runner.Version;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class ZipToolsTest {
	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(ZipToolsTest.class);

	/**
	 * temporary folder to store files
	 */
	@Rule
	public static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

	/**
	 * The eclipse workspace project
	 */
	@Rule
	public IProject project;

	private File createdTempFolder;

	private static final String TEXT_FILE = "text.txt"; //$NON-NLS-1$
	private static final String TEXT2_FILE = "text2.txt"; //$NON-NLS-1$
	private static final String MYFOLDER_DIR = "myFolder"; //$NON-NLS-1$
	private static final String TESTZIP_FILE = "test.zip"; //$NON-NLS-1$
	private static final String TEXTMYFOLDER_FILE = "textMyFolder.txt"; //$NON-NLS-1$

	@BeforeAll
	public static void initializeAll() {
		try {
			String junitVersion = Version.id();
			logger.debug("JUnit version is: {}", junitVersion); //$NON-NLS-1$

			// Create folder
			TEMP_FOLDER.create();

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@BeforeEach
	public void initialize() {
		try {
			// Log
			String junitVersion = Version.id();
			logger.debug("JUnit version is: {}", junitVersion); //$NON-NLS-1$

			// Check folder is created
			createdTempFolder = TEMP_FOLDER.newFolder();
			assertTrue(createdTempFolder.exists());

			// Create workspace project
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			project = root.getProject("MyProject"); //$NON-NLS-1$
			if (!project.exists()) {
				project.create(null);
			}
			project.open(null);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@AfterEach
	public void clean() {
		try {
			logger.info("Test ending"); //$NON-NLS-1$
			if (project != null && project.exists()) {
				project.delete(true, null);
			}
			boolean deleted = FileTools.deleteDirectoryRecursively(createdTempFolder);
			assertTrue(deleted);
		} catch (IOException | CoreException e) {
			fail(e.getMessage());
		}
	}

	@AfterAll
	public static void cleanAll() {
		try {
			TEMP_FOLDER.delete();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testZipIPath() {
		// Logs
		logger.info("Test started ZipIPath"); //$NON-NLS-1$

		try {

			// Create file to zip
			String initialString = "text"; //$NON-NLS-1$
			InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
			IFile newFile = project.getFile(TEXT_FILE);
			if (!newFile.exists()) {
				newFile.create(targetStream, true, null);
			}
			IPath newFileIPath = newFile.getFullPath();
			assertTrue(newFile.exists());
			project.refreshLocal(IResource.DEPTH_INFINITE, null);

			// add file to zip list
			List<IPath> listFilePath = new ArrayList<>();
			listFilePath.add(newFileIPath);

			// Create a zip path
			IPath zipPath = project.getFullPath().append(TESTZIP_FILE);

			// Zip
			ZipTools.zipIPath(listFilePath, zipPath);

			// test Zip
			IFile zipFile = project.getFile(TESTZIP_FILE);
			assertTrue(zipFile.exists());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testZipIPathFailListNull() {
		// Logs
		logger.info("Test started ZipIPathFailListNull"); //$NON-NLS-1$

		try {

			// add file to zip list
			List<IPath> listFilePath = null;

			// Create a zip path
			IPath zipPath = project.getFullPath().append(TESTZIP_FILE);

			// Zip
			ZipTools.zipIPath(listFilePath, zipPath);
			fail(TestConstants.FAIL_CRED_EXCEPTION);

		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL), e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testZipIFile() {
		// Logs
		logger.info("Test started ZipIFile"); //$NON-NLS-1$

		try {

			// Create file to zip
			String initialString = "text"; //$NON-NLS-1$
			InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
			IFile newFile = project.getFile(TEXT_FILE);
			if (!newFile.exists()) {
				newFile.create(targetStream, true, null);
			}
			assertTrue(newFile.exists());
			project.refreshLocal(IResource.DEPTH_INFINITE, null);

			// add file to zip list
			List<IFile> listFilePath = new ArrayList<>();
			listFilePath.add(newFile);

			// Create a zip path
			IPath zipPath = project.getFullPath().append(TESTZIP_FILE);

			// Zip
			ZipTools.zipIFile(listFilePath, zipPath);

			// test Zip
			IFile zipFile = project.getFile(TESTZIP_FILE);
			assertTrue(zipFile.exists());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testZipIFileFailListNull() {
		// Logs
		logger.info("Test started ZipIFileFailListNull"); //$NON-NLS-1$

		try {

			// add file to zip list
			List<IFile> listFilePath = null;

			// Create a zip path
			IPath zipPath = project.getFullPath().append(TESTZIP_FILE);

			// Zip
			ZipTools.zipIFile(listFilePath, zipPath);
			fail(TestConstants.FAIL_CRED_EXCEPTION);

		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL), e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testZipIFileFailTargetNull() {
		// Logs
		logger.info("Test started ZipIFileFailTargetNull"); //$NON-NLS-1$

		try {

			// add file to zip list
			List<IFile> listFilePath = new ArrayList<>();

			// Create a zip path
			IPath zipPath = null;

			// Zip
			ZipTools.zipIFile(listFilePath, zipPath);
			fail(TestConstants.FAIL_CRED_EXCEPTION);

		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL), e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testZipFile() {
		// Logs
		logger.info("Test started testZipFile"); //$NON-NLS-1$

		try {
			// Create content to zip
			File createdFileText = FileTools.createFile(createdTempFolder + "/" + TEXT_FILE); //$NON-NLS-1$
			assertTrue(createdFileText.exists());
			File createdFileText2 = FileTools.createFile(createdTempFolder + "/" + TEXT2_FILE); //$NON-NLS-1$
			assertTrue(createdFileText2.exists());
			File newFolder = new File(createdTempFolder, MYFOLDER_DIR);
			assertTrue(newFolder.mkdir());
			assertTrue(newFolder.isDirectory());
			assertTrue(newFolder.exists());
			File createdFileTextMyFolder = FileTools.createFile(newFolder + "/" + TEXTMYFOLDER_FILE); //$NON-NLS-1$
			assertTrue(createdFileTextMyFolder.exists());

			List<File> listSourceFile = new ArrayList<>();
			listSourceFile.add(createdFileText);
			listSourceFile.add(createdFileText2);
			listSourceFile.add(newFolder);

			// zip file not created yet
			File zipFile = new File(createdTempFolder, TESTZIP_FILE);
			assertFalse(zipFile.exists());

			// Zip Path
			ZipTools.zipFile(listSourceFile, zipFile);
			assertTrue(zipFile.exists());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testZipFileAlreadyExists() {
		// Logs
		logger.info("Test started testZipFileAlreadyExists"); //$NON-NLS-1$

		try {
			// Create content to zip
			File createdFileText = FileTools.createFile(createdTempFolder + "/" + TEXT_FILE); //$NON-NLS-1$
			assertTrue(createdFileText.exists());
			File createdFileText2 = FileTools.createFile(createdTempFolder + "/" + TEXT2_FILE); //$NON-NLS-1$
			assertTrue(createdFileText2.exists());
			File newFolder = new File(createdTempFolder, MYFOLDER_DIR);
			assertTrue(newFolder.mkdir());
			assertTrue(newFolder.isDirectory());
			assertTrue(newFolder.exists());
			File createdFileTextMyFolder = FileTools.createFile(newFolder + "/" + TEXTMYFOLDER_FILE); //$NON-NLS-1$
			assertTrue(createdFileTextMyFolder.exists());

			List<File> listSourceFile = new ArrayList<>();
			listSourceFile.add(createdFileText);
			listSourceFile.add(createdFileText2);
			listSourceFile.add(newFolder);

			// Create a zip file before zipping
			File zipFile = new File(createdTempFolder, TESTZIP_FILE);
			Files.createFile(zipFile.toPath());
			assertTrue(zipFile.exists());

			// Zip Path
			ZipTools.zipFile(listSourceFile, zipFile);
			assertTrue(zipFile.exists());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testZipFileFailListNull() {
		// Logs
		logger.info("Test started ZipFileFailListNull"); //$NON-NLS-1$

		try {

			// add file to zip list
			List<File> listFilePath = null;

			// Create a zip path
			IPath zipPath = project.getFullPath().append(TEXT_FILE);

			// Zip
			ZipTools.zipFile(listFilePath, zipPath);
			fail(TestConstants.FAIL_CRED_EXCEPTION);

		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL), e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testZipFileFailTargetNull() {
		// Logs
		logger.info("Test started ZipFileFailTargetNull"); //$NON-NLS-1$

		try {

			// add file to zip list
			List<File> listFilePath = new ArrayList<>();

			// Create a zip path
			IPath zipPath = null;

			// Zip
			ZipTools.zipFile(listFilePath, zipPath);
			fail(TestConstants.FAIL_CRED_EXCEPTION);

		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL), e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testZipFile2FailListNull() {
		// Logs
		logger.info("Test started ZipFile2FailListNull"); //$NON-NLS-1$

		try {

			// add file to zip list
			List<File> listFilePath = null;

			// Create a zip path
			File zipPath = new File(TEXT_FILE);

			// Zip
			ZipTools.zipFile(listFilePath, zipPath);
			fail(TestConstants.FAIL_CRED_EXCEPTION);

		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL), e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testZipFile2FailTargetNull() {
		// Logs
		logger.info("Test started ZipFile2FailTargetNull"); //$NON-NLS-1$

		try {

			// add file to zip list
			List<File> listFilePath = new ArrayList<>();

			// Create a zip path
			File zipPath = null;

			// Zip
			ZipTools.zipFile(listFilePath, zipPath);
			fail(TestConstants.FAIL_CRED_EXCEPTION);

		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL), e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testUnzipIFile() {
		// Logs
		logger.info("Test started UnzipIFile"); //$NON-NLS-1$

		try {

			// Create file to zip
			String initialString = "text"; //$NON-NLS-1$
			InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
			IFile newFile = project.getFile(TEXT_FILE);
			if (!newFile.exists()) {
				newFile.create(targetStream, true, null);
			}
			project.refreshLocal(IResource.DEPTH_INFINITE, null);

			// add file to zip list
			List<IFile> listFilePath = new ArrayList<>();
			listFilePath.add(newFile);

			// Create a zip path
			IPath zipPath = project.getFullPath().append(TESTZIP_FILE);

			// Zip
			ZipTools.zipIFile(listFilePath, zipPath);

			// test Zip
			IFile zipFile = project.getFile(TESTZIP_FILE);
			assertTrue(zipFile.exists());

			// create unzip folder
			IFolder folder = project.getFolder("unzip"); //$NON-NLS-1$
			if (!folder.exists()) {
				folder.create(true, true, null);
			}
			assertTrue(folder.exists());

			// Unzip
			ZipTools.unzip(zipFile, folder);
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			assertTrue(project.getFile(folder.getName() + "/" + TEXT_FILE).exists()); //$NON-NLS-1$

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testUnzipFile() {
		// Logs
		logger.info("Test started UnzipFile"); //$NON-NLS-1$

		try {
			// Create content to zip
			File createdFileText = FileTools.createFile(createdTempFolder + "/" + TEXT_FILE); //$NON-NLS-1$
			assertTrue(createdFileText.exists());
			File createdFileText2 = FileTools.createFile(createdTempFolder + "/" + TEXT2_FILE); //$NON-NLS-1$
			assertTrue(createdFileText2.exists());
			File newFolder = new File(createdTempFolder, MYFOLDER_DIR);
			assertTrue(newFolder.mkdir());
			assertTrue(newFolder.isDirectory());
			assertTrue(newFolder.exists());
			File createdFileTextMyFolder = FileTools.createFile(newFolder + "/" + TEXTMYFOLDER_FILE); //$NON-NLS-1$
			assertTrue(createdFileTextMyFolder.exists());

			List<File> listSourceFile = new ArrayList<>();
			listSourceFile.add(createdFileText);
			listSourceFile.add(newFolder);
			listSourceFile.add(createdFileText2);

			// Create a zip
			File zipFile = new File(createdTempFolder, TESTZIP_FILE);
			assertFalse(zipFile.exists());

			// Zip Path
			ZipTools.zipFile(listSourceFile, zipFile);
			assertTrue(zipFile.exists());

			// Unzip path
			File folderUnzip = new File(createdTempFolder, "unzip"); //$NON-NLS-1$
			assertTrue(folderUnzip.mkdir());
			assertTrue(folderUnzip.isDirectory());
			ZipTools.unzip(zipFile, folderUnzip);

			File unzippedFileText = new File(folderUnzip, TEXT_FILE);
			assertTrue(unzippedFileText.exists());
			File unzippedNewFolder = new File(folderUnzip, MYFOLDER_DIR);
			assertTrue(unzippedNewFolder.exists());
			File unzippedFileTextMyFolder = new File(unzippedNewFolder, TEXTMYFOLDER_FILE);
			assertTrue(unzippedFileTextMyFolder.exists());
			File unzippedFileText2 = new File(folderUnzip, TEXT2_FILE);
			assertTrue(unzippedFileText2.exists());

		} catch (IOException | CredibilityException e) {
			fail(e.getMessage());
		}
	}
}
