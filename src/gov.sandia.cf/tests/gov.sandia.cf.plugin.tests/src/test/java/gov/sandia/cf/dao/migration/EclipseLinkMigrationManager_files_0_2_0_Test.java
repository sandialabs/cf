/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.SQLException;

import org.hsqldb.cmdline.SqlToolError;
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

import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.DaoManager;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.exceptions.CredibilityDatabaseInvalidException;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.ZipTools;
import junit.runner.Version;

/**
 * Test migration for an old cf file version = 0.2.0
 * 
 * @author Didier Verstraete
 */
@RunWith(JUnitPlatform.class)
class EclipseLinkMigrationManager_files_0_2_0_Test {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(EclipseLinkMigrationManager_files_0_2_0_Test.class);

	public static final String SELECT_FROM_WHERE_STRING_QUERY = "SELECT {0} FROM {1} WHERE {2}=''{3}''"; //$NON-NLS-1$

	/**
	 * temporary folder to store the hsqldb database
	 */
	@Rule
	private static TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

	/**
	 * the dao manager
	 */
	private static IDaoManager daoManager;

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
	 * Initialize the test
	 */
	@BeforeEach
	public void beforeTest() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		try {
			logger.info("Test started"); //$NON-NLS-1$

			getDaoManager().start();

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
		try {
			File file = new File(getDaoManager().getDatabaseDirectoryPath());
			if (file.exists()) {
				FileTools.deleteDirectoryRecursively(file);
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Clean the test
	 */
	@AfterAll
	public static void clean() {
		daoManager.stop();
		TEMP_FOLDER.delete();
		assertFalse(TEMP_FOLDER.getRoot().exists());
		logger.info("Test ending"); //$NON-NLS-1$
	}

	@Test
	void test_processMigration_Ok() throws CredibilityException, SQLException, SqlToolError, IOException,
			URISyntaxException, CredibilityMigrationException, CredibilityDatabaseInvalidException {

		// copy cf test file
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream("sql/migration/cf_file_0.2.0.cf"); //$NON-NLS-1$
		File oldCfFile = new File(TEMP_FOLDER.getRoot(), "cf_file_0.2.0.cf"); //$NON-NLS-1$
		Files.copy(inputStream, oldCfFile.toPath());

		// unzip cf test file
		File tmpFolder = new File(TEMP_FOLDER.getRoot(), ".cftmp"); //$NON-NLS-1$
		ZipTools.unzip(oldCfFile, TEMP_FOLDER.getRoot());

		// initialize only the database manager and not the dao manager to bypass the
		// database migration
		getDaoManager().getDbManager().initialize(tmpFolder.getPath());

		// set entity manager
		getDaoManager().getRepository(IModelRepository.class).setEntityManager(getDaoManager().getEntityManager());

		// Needed to execute migration
		TestEntityFactory.getNewModel(getDaoManager());

		// process the migration
		getDaoManager().getDbMigrationManager().executeMigration();

		// close db manager
		getDaoManager().getDbManager().close();

		// clean
		assertTrue(oldCfFile.delete());
		FileTools.deleteDirectoryRecursively(tmpFolder);
	}
}
