/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.DaoManager;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.tools.FileTools;
import junit.runner.Version;

/**
 * JUnit tests to check the abstract DAO and hsqldb connection and querying.
 *
 * @author Didier Verstraete
 */
class EclipseLinkMigrationManagerExecutionTest {

	/**
	 * f the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(EclipseLinkMigrationManagerExecutionTest.class);

	/**
	 * temporary folder to store the hsqldb database
	 */
	@Rule
	private static TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

	/**
	 * the dao manager
	 */
	private static IDaoManager daoManager;

	private File createdFolder;

	/**
	 * Initialize the test
	 */
	@BeforeAll
	public static void initialize() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		try {
			logger.info("Test started"); //$NON-NLS-1$

			TEMP_FOLDER.create();

			daoManager = new DaoManager(AbstractTestDao.ENTITY_PERSIST_UNIT_NAME_TEST);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * @return the dao manager
	 */
	public IDaoManager getDaoManager() {
		return daoManager;
	}

	/**
	 * @return the temporary folder
	 */
	public TemporaryFolder getTempFolder() {
		return TEMP_FOLDER;
	}

	/**
	 * Initialize the test
	 */
	@BeforeEach
	public void beforeTest() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		try {
			logger.info("Test started"); //$NON-NLS-1$

			createdFolder = getTempFolder().newFolder();
			getDaoManager().start();

			// initialize only the database manager and not the dao manager to bypass the
			// database migration
			getDaoManager().getDbManager().initialize(createdFolder.getPath());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Clean the test
	 */
	@AfterEach
	public void afterTest() {
		getDaoManager().stop();
		if (createdFolder != null && createdFolder.exists()) {
			try {
				FileTools.deleteDirectoryRecursively(createdFolder);
			} catch (IOException e) {
				fail("Exception during temporary folders deletion", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Clean the test
	 */
	@AfterAll
	public static void clean() {
		daoManager.stop();
		try {
			FileTools.deleteDirectoryRecursively(TEMP_FOLDER.getRoot());
		} catch (IOException e) {
			fail("Exception during temporary folders deletion", e); //$NON-NLS-1$
		}
		TEMP_FOLDER.delete();
		logger.info("Test ending"); //$NON-NLS-1$
	}

	@Test
	void test_AllTasksExecution() {

		// get unit of work
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		assertNotNull(unitOfWork);

		String currentScript = null;

		try {

			// get scripts to execute
			SortedMap<Integer, IMigrationTask> sqlScriptsToExecute = null;
			try {
				sqlScriptsToExecute = ((EclipseLinkMigrationManager) getDaoManager().getDbMigrationManager())
						.getTasksToExecute(unitOfWork);
			} catch (CredibilityMigrationException e) {
				fail(e.getMessage());
			}
			assertNotNull(sqlScriptsToExecute);

			// execute all scripts
			for (Entry<Integer, IMigrationTask> script : sqlScriptsToExecute.entrySet()) {
				if (script != null && script.getValue() != null) {
					IMigrationTask task = script.getValue();

					// execute the script
					task.execute(getDaoManager());
				}
			}
		} catch (CredibilityMigrationException | IOException | URISyntaxException e) {
			fail(MessageFormat.format("Script: [{0}] in error:\n", currentScript) + e.getMessage()); //$NON-NLS-1$
		}
	}
}
