/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IDaoManager;
import junit.runner.Version;

/**
 * Abstract Configuration JUnit test class
 * 
 * @author Didier Verstraete
 *
 */
abstract class AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(AbstractConfigurationTest.class);

	/**
	 * temporary folder to store files
	 */
	@Rule
	public static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

	private File newFolder;

	/**
	 * The application layer manager
	 */
	private static ApplicationManager appMgr;

	@BeforeAll
	public static void initializeAll() {
		try {
			String junitVersion = Version.id();
			logger.info("JUnit version is: {}", junitVersion); //$NON-NLS-1$

			// Create folder
			TEMP_FOLDER.create();

			// load application layer classes
			appMgr = new ApplicationManager();
			appMgr.getDaoManager().setPersistUnitName(AbstractTestDao.ENTITY_PERSIST_UNIT_NAME_TEST);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@BeforeEach
	public void initialize() {
		try {
			logger.info("Test started"); //$NON-NLS-1$

			newFolder = TEMP_FOLDER.newFolder();
			assertTrue(newFolder.exists());

			appMgr.start();
			getDaoManager().initialize(newFolder.getPath());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@AfterEach
	public void clean() {
		appMgr.stop();
		if (newFolder != null && newFolder.exists()) {
			try {
				Files.walk(newFolder.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile)
						.forEach(File::delete);
				assertFalse("Directory still exists", newFolder.exists()); //$NON-NLS-1$
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

	@AfterAll
	public static void cleanAll() {
		appMgr.stop();
		TEMP_FOLDER.delete();
		logger.info("Test ending"); //$NON-NLS-1$
	}

	/**
	 * @return the app manager
	 */
	public ApplicationManager getAppManager() {
		return appMgr;
	}

	/**
	 * @return the dao manager
	 */
	public IDaoManager getDaoManager() {
		return appMgr.getDaoManager();
	}
}
