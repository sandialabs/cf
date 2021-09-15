/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.hsqldb.HSQLDBDaoManager;
import junit.runner.Version;

/**
 * Abstract class to define the squeleton of the Parts layer tests
 * 
 * @author Didier Verstraete
 *
 */
public class AbstractTestParts {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractTestParts.class);
	/**
	 * temporary folder to store the hsqldb database
	 */
	@Rule
	protected final static TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

	/**
	 * A temporary folder under the main temporary folder
	 */
	private File createdFolder;

	/**
	 * Initialize the test
	 */
	@BeforeAll
	public static void initialize() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		logger.info("Test started"); //$NON-NLS-1$

		try {
			TEMP_FOLDER.create();
		} catch (IOException e) {
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
			createdFolder = TEMP_FOLDER.newFolder();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Clean the test
	 */
	@AfterEach
	public void afterTest() {
		if (createdFolder != null && createdFolder.exists()) {
			try {
				HSQLDBDaoManager.dropDatabaseFiles(createdFolder.getPath());
				Files.walk(createdFolder.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile)
						.forEach(File::delete);
				assertFalse("Directory still exists", createdFolder.exists()); //$NON-NLS-1$
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}

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
		logger.info("Test ending"); //$NON-NLS-1$
	}

	/**
	 * @return the test temp folder
	 */
	public File getTestTempFolder() {
		return this.createdFolder;
	}

	protected void setTestTempFolder(File createdFolder) {
		this.createdFolder = createdFolder;
	}

}
