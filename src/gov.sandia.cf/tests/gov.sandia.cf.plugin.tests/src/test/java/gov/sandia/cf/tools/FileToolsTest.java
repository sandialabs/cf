/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import junit.runner.Version;

/**
 * The Class FileToolsTest.
 *
 * @author Didier Verstraete
 */
class FileToolsTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(FileToolsTest.class);

	/**
	 * temporary folder to store files
	 */
	@Rule
	public static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

	@BeforeAll
	public static void initializeAll() {
		try {
			String junitVersion = Version.id();
			logger.info("JUnit version is: {}", junitVersion); //$NON-NLS-1$

			// Create folder
			TEMP_FOLDER.create();

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private File newFolder;

	@BeforeEach
	public void initialize() {
		try {
			logger.info("Test started"); //$NON-NLS-1$

			newFolder = TEMP_FOLDER.newFolder();
			assertTrue(newFolder.exists());

		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@AfterEach
	public void clean() {
		boolean deleted = newFolder.delete();
		assertTrue(deleted);
		logger.info("Test ending"); //$NON-NLS-1$
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
	void testCreateFile() {
		try {

			String createdFileName = "created-file.txt"; //$NON-NLS-1$

			// Create File
			File createdFile = FileTools.createFile(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$
			assertTrue(createdFile.exists());

			// Test already exist
			File createdFileExist = FileTools.createFile(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$
			assertTrue(createdFileExist.exists());
			assertEquals(createdFileExist.getAbsolutePath(), createdFile.getAbsolutePath());
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testMove() {
		try {
			// Create File
			String createdFileName = "created-file.txt"; //$NON-NLS-1$
			File createdFile = FileTools.createFile(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$
			assertTrue(createdFile.exists());

			// Move file

		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testCreateFileErrors() {
		// Test null file path
		try {
			// Create File
			File createdFile = FileTools.createFile((String) null);
			assertTrue(createdFile.exists());
		} catch (CredibilityException | IOException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL));
		}

		// Test empty file path
		try {
			// Create File
			File createdFile = FileTools.createFile(""); //$NON-NLS-1$
			assertTrue(createdFile.exists());
		} catch (CredibilityException | IOException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_FILETOOLS_EMPTYNULL));
		}
	}

	@Test
	void testExistFilesWithExtensionInFolder() {
		try {
			File newFile = TEMP_FOLDER.newFile("Test." + FileTools.CREDIBILITY_FILE_EXTENSION); //$NON-NLS-1$
			assertTrue(newFile.exists());
			assertEquals(newFile.getParentFile(), TEMP_FOLDER.getRoot());

			// Test true
			assertTrue(FileTools.existFilesWithExtensionInFolder(TEMP_FOLDER.getRoot(),
					FileTools.CREDIBILITY_FILE_EXTENSION));

			// Test false
			assertFalse(FileTools.existFilesWithExtensionInFolder(TEMP_FOLDER.getRoot(), ".test")); //$NON-NLS-1$
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testExistFilesWithExtensionInFolderErrors() {
		assertFalse(FileTools.existFilesWithExtensionInFolder(null, FileTools.CREDIBILITY_FILE_EXTENSION));
		assertFalse(FileTools.existFilesWithExtensionInFolder(new File("test-empty"), //$NON-NLS-1$
				FileTools.CREDIBILITY_FILE_EXTENSION));
	}

	@Test
	void testDeleteDirectoryRecursively() {

		String fileExtension = ".file"; //$NON-NLS-1$
		String folderDir = "folder"; //$NON-NLS-1$
		String newFile = "newFile"; //$NON-NLS-1$

		try {
			File rootFolder = TEMP_FOLDER.newFolder(folderDir);
			File rootFile = TEMP_FOLDER.newFile("rootFile.txt"); //$NON-NLS-1$
			File subFolder1 = TEMP_FOLDER.newFolder(folderDir, "subfolder1"); //$NON-NLS-1$
			assertEquals(subFolder1.getParent(), rootFolder.getPath());

			File file1 = File.createTempFile(newFile, fileExtension, subFolder1);
			assertEquals(file1.getParent(), subFolder1.getPath());
			File file2 = File.createTempFile(newFile, fileExtension, subFolder1);
			assertEquals(file2.getParent(), subFolder1.getPath());

			File subFolder2 = TEMP_FOLDER.newFolder(folderDir, "subfolder2"); //$NON-NLS-1$
			assertEquals(subFolder2.getParent(), rootFolder.getPath());
			File file3 = File.createTempFile(newFile, fileExtension, subFolder2);
			assertEquals(file3.getParent(), subFolder2.getPath());

			// delete recursively
			FileTools.deleteDirectoryRecursively(rootFolder);
			assertFalse(rootFolder.exists());
			assertTrue(rootFile.exists()); // this one shouldn't be deleted
			assertFalse(subFolder1.exists());
			assertFalse(file1.exists());
			assertFalse(file2.exists());
			assertFalse(subFolder2.exists());
			assertFalse(file3.exists());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testDeleteDirectoryRecursivelyErrors() {
		// Path null
		IPath path = null;
		try {
			assertFalse(WorkspaceTools.deleteDirectoryRecursively(path));
		} catch (CoreException e) {
			fail(e.getMessage());
		}

		// Folder null
		IFolder folder = null;
		try {
			assertFalse(WorkspaceTools.deleteDirectoryRecursively(folder));
		} catch (CoreException e) {
			fail(e.getMessage());
		}

		// Folder doesn't exist
		try {
			folder = mock(IFolder.class);
			when(folder.exists()).thenReturn(false);
			assertFalse(WorkspaceTools.deleteDirectoryRecursively(folder));
		} catch (CoreException e) {
			fail(e.getMessage());
		}

		// File null
		File file = null;
		try {
			assertFalse(FileTools.deleteDirectoryRecursively(file));
		} catch (IOException e) {
			fail(e);
		}
	}

//	@Test
//	void testCreateFolderStructure() {
	// @TODO doesn't work
//		try {
//			// Initialize
//			IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
//			File ymlStrucutre = FileTools.createFile(rootPath.toString() + "/structure.yml");
//
//			// Write file
//			FileWriter myWriter;
//			myWriter = new FileWriter(ymlStrucutre);
//			myWriter.write("0-System_Requirements-Definition:\r" + "      0-System_Requirements:\r"
//					+ "      1-Quantities_of_Interests_and_Acceptance_Criteria:\r" + "1-PCMM:\r"
//					+ "      0-Code_Verification:\r" + "      1-Physics_and_Material_Fidelity:\r"
//					+ "      2-Representation_and_Geometric_Fidelity:\r" + "      3-Solution_Verification:\r"
//					+ "      4-Validation:\r" + "         0-PhysSim:\r" + "         1-CompSim:\r"
//					+ "      5-Uncertainty_Quantification:\r" + "         0-Uncertainty_Inventory:\r"
//					+ "         1-UQ:\r" + "2-System_Requirements-Verification:\r" + "3-Peer_Reviews:");
//			myWriter.close();
//
//			// Create folder structure
//			FileTools.createFolderStructure(rootPath, ymlStrucutre);
//
//		} catch (CredibilityException | IOException | IllegalStateException e) {
//			fail(e.getMessage());
//		}
//	}

	@Test
	void testGetWorkspace() {
		try {
			assertNotNull(WorkspaceTools.getWorkspace());
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
	}

//	@Test
//	 void testGetActiveProject() {
// 		// TODO doesn't work	
//		try {
//
//			ResourcesPlugin.getWorkspace();
//
//			// No active editor
//			assertNull(FileTools.getActiveProject());
//
//			// TODO with active editor
//
//		} catch (IllegalStateException e) {
//			fail(e.getMessage());
//		}
//	}

//	@Test
//	void testGetActivePage() {
// 		// TODO doesn't work	
//		try {
//
//			ResourcesPlugin.getWorkspace();
//
//			// No active editor
//			assertNotNull(FileTools.getActivePage());
//
//			// TODO with active editor
//
//		} catch (IllegalStateException e) {
//			fail(e.getMessage());
//		}
//	}

//	@Test
//	void testOpenFileInWorkspace() {
// 		// TODO doesn't work		
//		try {
//			IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
//			File rootFile = FileTools.createFile(rootPath.toPortableString() + "/test-open-file.txt");
//			
//			IPath location= Path.fromOSString(rootFile.getAbsolutePath()); 
//			IFile ifile= ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(location);
//			
//			// @TODO active editor
//			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//			IDE.openEditor(page, ifile);
//
//			FileTools.openFileInWorkspace(ifile.getFullPath().toString());
//
//		} catch (CredibilityException | IllegalStateException | PartInitException e) {
//			fail(e.getMessage());
//		}
//	}

	@Test
	void testGetChildren() {
		try {
			// Test path null
			assertNotNull(WorkspaceTools.getChildren(null));
			assertTrue(WorkspaceTools.getChildren(null).isEmpty());

			// Test ok
			IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
			assertNotNull(WorkspaceTools.getChildren(rootPath));
			assertTrue(WorkspaceTools.getChildren(rootPath).isEmpty());
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testToOsPath() {
		try {
			// Null
			assertEquals(WorkspaceTools.toOsPath(null), RscTools.empty());

			// Work
			assertEquals(WorkspaceTools.toOsPath(new Path("/test")), RscTools.empty()); //$NON-NLS-1$
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testToFile() {
		// Initialize
		IResource iResource = null;
		IPath iPath = null;

		// Null
		assertNull(WorkspaceTools.toFile(iResource));
		assertNull(WorkspaceTools.toFile(iPath));

		// Work
		assertNotNull(WorkspaceTools.toFile(ResourcesPlugin.getWorkspace().getRoot().getLocation()));
	}

	@Test
	void testCreateFolderStructureErrors() {
		// Initialize
		IPath rootPath = null;
		File ymlFolderStructureDescriptor = new File("Test"); //$NON-NLS-1$

		// test ymlFolderStructureDescriptor not existing
		try {
			WorkspaceTools.createFolderStructure(rootPath, ymlFolderStructureDescriptor);
			fail("Can not launch FileTools.createFileTools.FolderStructure with a non-existing file"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_FILETOOLS_EVID_FOLDER_STRUCUTURE_NOTEXIST));
		}

		// test rootPath null
		try {
			ymlFolderStructureDescriptor = FileTools.createFile(TEMP_FOLDER.getRoot() + "/description.yml"); //$NON-NLS-1$
		} catch (IOException | CredibilityException e) {
			fail(e.getMessage());
		}
		try {
			WorkspaceTools.createFolderStructure(rootPath, ymlFolderStructureDescriptor);
			fail("Can not launch FileTools.createFileTools.FolderStructure with a null rootPath"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_FILETOOLS_ROOTPATH_NOTEXIST));
		}

		// test rootPath empty
		try {
			rootPath = new Path(""); //$NON-NLS-1$
			WorkspaceTools.createFolderStructure(rootPath, ymlFolderStructureDescriptor);
			fail("Can not launch FileTools.createFileTools.FolderStructure with an empty rootPath"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_FILETOOLS_ROOTPATH_NOTEXIST));
		}

		// Empty file structure
		try {
			rootPath = new Path(TEMP_FOLDER.getRoot().getPath());
			WorkspaceTools.createFolderStructure(rootPath, ymlFolderStructureDescriptor);
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_FILETOOLS_EVID_FOLDER_STRUCUTURE_NULL));
		}
	}

	@Test
	void testRemoveFilenameFromPathErrors() {
		// Initialize
		IPath path = null;
		String pathString = null;

		// Path null
		assertNull(FileTools.removeFilenameFromPath(path));

		// String null
		assertEquals(RscTools.empty(), FileTools.removeFilenameFromPath(pathString));

		// Path empty
		pathString = ""; //$NON-NLS-1$
		assertEquals(RscTools.empty(), FileTools.removeFilenameFromPath(pathString));
	}

	/////////////////////////////////////////
	////////// Test isWordDocument //////////
	/////////////////////////////////////////

	@Test
	void testIsWordDocument_MatchingWord95() {
		assertTrue(FileTools.isWordDocument("/Test/myPath/myDoc.doc")); //$NON-NLS-1$
	}

	@Test
	void testIsWordDocument_MatchingWord2007() {
		assertTrue(FileTools.isWordDocument("/Test/myPath/myDoc.docx")); //$NON-NLS-1$
	}

	@Test
	void testIsWordDocument_MatchingOnlyFile() {
		assertTrue(FileTools.isWordDocument("myDoc.docx")); //$NON-NLS-1$
	}

	@Test
	void testIsWordDocument_NotMatching() {
		assertFalse(FileTools.isWordDocument("/Test/myPath/myDoc.txt")); //$NON-NLS-1$
	}

	@Test
	void testIsWordDocument_NotFile() {
		assertFalse(FileTools.isWordDocument("/Test/myPath/myDoc/")); //$NON-NLS-1$
	}

	@Test
	void testIsWordDocument_Null() {
		assertFalse(FileTools.isWordDocument(null));
	}
}
