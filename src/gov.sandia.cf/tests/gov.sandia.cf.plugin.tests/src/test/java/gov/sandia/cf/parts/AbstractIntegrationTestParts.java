/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.tools.FileTools;
import junit.runner.Version;

/**
 * Abstract class to define the squeleton of the Parts layer integration tests
 * 
 * @author Didier Verstraete
 *
 */
public class AbstractIntegrationTestParts extends AbstractTestParts {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTestParts.class);

	/**
	 * The application layer manager
	 */
	private static ApplicationManager appMgr;

	/**
	 * Initialize the test
	 */
	@BeforeAll
	public static void initialize() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		try {
			logger.info("Test started"); //$NON-NLS-1$

			TEMP_FOLDER.create();

			// load application layer classes
			appMgr = new ApplicationManager();
			appMgr.getDaoManager().setPersistUnitName(AbstractTestDao.ENTITY_PERSIST_UNIT_NAME_TEST);

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
			setTestTempFolder(TEMP_FOLDER.newFolder());

			appMgr.start();
			getDaoManager().initialize(getTestTempFolder().getPath());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Clean the test
	 */
	@AfterEach
	public void afterTest() {
		appMgr.stop();
		if (getTestTempFolder() != null && getTestTempFolder().exists()) {
			try {
				FileTools.deleteDirectoryRecursively(getTestTempFolder());
				assertFalse("Directory still exists", getTestTempFolder().exists()); //$NON-NLS-1$
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
