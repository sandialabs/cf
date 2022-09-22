/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.hsqldb.cmdline.SqlToolError;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.common.ServiceLoader;
import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.DaoManager;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.dao.IMigrationLogRepository;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import junit.runner.Version;

/**
 * JUnit tests to check the abstract DAO and hsqldb connection and querying.
 *
 * @author Didier Verstraete
 */
class EclipseLinkMigrationManagerTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(EclipseLinkMigrationManagerTest.class);

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
	 * Initialize the test
	 */
	@BeforeEach
	public void beforeTest() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		try {
			logger.info("Test started"); //$NON-NLS-1$

			createdFolder = TEMP_FOLDER.newFolder();
			getDaoManager().start();

			// initialize only the database manager and not the dao manager to bypass the
			// database migration
			getDaoManager().getDbManager().initialize(createdFolder.getPath());

			getDaoManager().getRepository(IModelRepository.class).setEntityManager(getDaoManager().getEntityManager());

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
				fail(e.getMessage());
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
			fail(e.getMessage());
		}
		TEMP_FOLDER.delete();
		logger.info("Test ending"); //$NON-NLS-1$
	}

	@Test
	void test_processMigration_ModelNull() {

		// process the migration
		try {
			getDaoManager().getDbMigrationManager().executeMigration();
		} catch (SqlToolError | CredibilityException | SQLException | IOException | URISyntaxException
				| CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_processMigration_Ok() {

		// Needed to execute migration
		TestEntityFactory.getNewModel(getDaoManager());

		// process the migration
		try {
			getDaoManager().getDbMigrationManager().executeMigration();
		} catch (SqlToolError | CredibilityException | SQLException | IOException | URISyntaxException
				| CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_getTasks_All() throws CredibilityMigrationException {
		SortedMap<Integer, IMigrationTask> migrationTasks = new EclipseLinkMigrationManager(getDaoManager())
				.getMigrationTasks();
		List<Class<?>> taskClasses = ServiceLoader.load(MigrationTask.class,
				EclipseLinkMigrationManager.class.getPackage().getName());

		assertEquals(taskClasses.size(), migrationTasks.size());

		for (Entry<Integer, IMigrationTask> entry : migrationTasks.entrySet()) {
			Integer key = entry.getKey();
			IMigrationTask value = entry.getValue();
			int taskId = value.getClass().getAnnotation(MigrationTask.class).id();
			assertEquals(Integer.valueOf(taskId), key);
		}
	}

	@Test
	void test_getTasksToExecute_All() throws CredibilityMigrationException {

		// get unit of work
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		assertNotNull(unitOfWork);

		SortedMap<Integer, IMigrationTask> tasksToExecute = null;
		try {
			tasksToExecute = ((EclipseLinkMigrationManager) getDaoManager().getDbMigrationManager())
					.getTasksToExecute(unitOfWork);
		} catch (CredibilityMigrationException | URISyntaxException | IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(tasksToExecute);
		assertEquals(new EclipseLinkMigrationManager(getDaoManager()).getMigrationTasks().size(),
				tasksToExecute.size());
	}

	@Test
	void test_getSqlScriptsToExecute_TwoExecution() {

		TestEntityFactory.getNewModel(getDaoManager());

		// get unit of work
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		assertNotNull(unitOfWork);

		try {
			// execute all scripts
			getDaoManager().getDbMigrationManager().executeMigration();

			// there should be no tasks to execute after one execution
			SortedMap<Integer, IMigrationTask> tasksToExecute = ((EclipseLinkMigrationManager) getDaoManager()
					.getDbMigrationManager()).getTasksToExecute(unitOfWork);
			assertNotNull(tasksToExecute);
			assertTrue(tasksToExecute.isEmpty());

		} catch (IOException | CredibilityException | SqlToolError | SQLException | URISyntaxException
				| CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_isValidScript_TaskNull() {
		try {
			((EclipseLinkMigrationManager) getDaoManager().getDbMigrationManager()).isValidScript(null, null);
			fail("This should fail."); //$NON-NLS-1$
		} catch (CredibilityMigrationException e) {
			assertEquals(RscTools.getString(RscConst.EX_MIGRATIONDAO_TASK_NULL), e.getMessage());
		}
	}

	@Test
	void test_isValidScript_TaskNameNull() {
		try {
			IMigrationTask task = new IMigrationTask() {

				@Override
				public String getName() {
					return null;
				}

				@Override
				public boolean execute(IDaoManager daoManager) throws CredibilityMigrationException {
					return false;
				}
			};
			((EclipseLinkMigrationManager) getDaoManager().getDbMigrationManager()).isValidScript(task, null);
			fail("This should fail."); //$NON-NLS-1$
		} catch (CredibilityMigrationException e) {
			assertEquals(RscTools.getString(RscConst.EX_MIGRATIONDAO_TASKNAME_BLANK), e.getMessage());
		}
	}

	@Test
	void test_isValidScript_TaskNameEmpty() {
		try {
			IMigrationTask task = new IMigrationTask() {

				@Override
				public String getName() {
					return RscTools.empty();
				}

				@Override
				public boolean execute(IDaoManager daoManager) throws CredibilityMigrationException {
					return false;
				}
			};
			((EclipseLinkMigrationManager) getDaoManager().getDbMigrationManager()).isValidScript(task, null);
			fail("This should fail."); //$NON-NLS-1$
		} catch (CredibilityMigrationException e) {
			assertEquals(RscTools.getString(RscConst.EX_MIGRATIONDAO_TASKNAME_BLANK), e.getMessage());
		}
	}

	@Test
	void test_isValidScript_NoPreviousExecution() {

		// get unit of work
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		assertNotNull(unitOfWork);

		try {
			IMigrationTask task = new IMigrationTask() {

				@Override
				public String getName() {
					return "my-custom-task"; //$NON-NLS-1$
				}

				@Override
				public boolean execute(IDaoManager daoManager) throws CredibilityMigrationException {
					return false;
				}
			};
			boolean validScript = ((EclipseLinkMigrationManager) getDaoManager().getDbMigrationManager())
					.isValidScript(task, unitOfWork);
			assertTrue(validScript);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_isValidScript_PreviousExecutionInError() {

		// get unit of work
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		assertNotNull(unitOfWork);

		// variables
		String taskName = "my-custom-task"; //$NON-NLS-1$
		String databaseVersion = "0.1.0"; //$NON-NLS-1$
		String errorLog = "An error has occured"; //$NON-NLS-1$
		IMigrationTask task = new IMigrationTask() {

			@Override
			public String getName() {
				return taskName;
			}

			@Override
			public boolean execute(IDaoManager daoManager) throws CredibilityMigrationException {
				return false;
			}
		};

		try {
			// insert the script in error
			getDaoManager().getRepository(IMigrationLogRepository.class).markTaskInError(task, databaseVersion,
					errorLog);
			boolean validScript = ((EclipseLinkMigrationManager) getDaoManager().getDbMigrationManager())
					.isValidScript(task, unitOfWork);
			assertTrue(validScript);
		} catch (CredibilityException | CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_isValidScript_NotValid_BecauseSuccessful() {

		// get unit of work
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		assertNotNull(unitOfWork);

		// variables
		String taskName = "0.2.0.sql"; //$NON-NLS-1$
		String databaseVersion = "0.1.0"; //$NON-NLS-1$
		IMigrationTask task = new IMigrationTask() {

			@Override
			public String getName() {
				return taskName;
			}

			@Override
			public boolean execute(IDaoManager daoManager) throws CredibilityMigrationException {
				return false;
			}
		};

		try {
			// insert the script success
			getDaoManager().getRepository(IMigrationLogRepository.class).markTaskAsExecuted(task, databaseVersion);

			boolean validScript = ((EclipseLinkMigrationManager) getDaoManager().getDbMigrationManager())
					.isValidScript(task, unitOfWork);
			assertFalse(validScript);
		} catch (CredibilityException | CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_isValidScript_BeforeDatabaseGeneration() {

		// get unit of work
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		assertNotNull(unitOfWork);

		// variables
		String taskName = "0.2.0.sql"; //$NON-NLS-1$
		IMigrationTask task = new IMigrationTask() {

			@Override
			public String getName() {
				return taskName;
			}

			@Override
			public boolean execute(IDaoManager daoManager) throws CredibilityMigrationException {
				return false;
			}
		};

		try {
			boolean validScript = ((EclipseLinkMigrationManager) getDaoManager().getDbMigrationManager())
					.isValidScript(task, unitOfWork);
			assertTrue(validScript);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_getDatabaseVersion() {

		String databaseVersion = "0.2.0-20200601"; //$NON-NLS-1$

		// get unit of work
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		assertNotNull(unitOfWork);

		// update the model version
		unitOfWork.executeNonSelectingSQL("INSERT INTO MODEL (VERSION) VALUES ('" + databaseVersion + "')"); //$NON-NLS-1$ //$NON-NLS-2$

		String databaseVersionRetrieved = getDaoManager().getRepository(IModelRepository.class).getDatabaseVersion();
		assertNotNull(databaseVersionRetrieved);

		// at this point all the files
		assertEquals(databaseVersionRetrieved, databaseVersion);
	}

	@Test
	void test_markScriptAsDone_Ok() {

		// get unit of work
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		assertNotNull(unitOfWork);

		// execute sql script
		try {
			EclipseLinkMigrationManager.executeSQLScript(unitOfWork, AbstractTestDao.TABLE_MIGRATION_SCRIPT);
		} catch (SqlToolError | SQLException | IOException | URISyntaxException e) {
			fail(e.getMessage());
		}
		assertNull(getDaoManager().getRepository(IMigrationLogRepository.class)
				.isLastExecutionInError(AbstractTestDao.TABLE_MIGRATION_SCRIPT));
	}

	@Test
	void test_markScriptInError_Ok() {

		// get unit of work
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		assertNotNull(unitOfWork);

		try {
			// execute the script in error
			EclipseLinkMigrationManager.executeSQLScript(unitOfWork.acquireUnitOfWork(),
					AbstractTestDao.SCRIPT_IN_ERROR);
			fail("This script execution should generate an exception."); //$NON-NLS-1$

		} catch (SqlToolError | IOException | URISyntaxException e) {
			fail("It should generate a SQL Exception"); //$NON-NLS-1$
		} catch (SQLException e) {
		}
	}

	@Test
	void test_splitSqlQueries() {
		try {

			String sqlQueriesFromScript = "--Drop table TAG_PCMM\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "DROP TABLE IF EXISTS TAG_PCMM;\r\n\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "--Create table TAG_PCMM\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "CREATE TABLE IF NOT EXISTS TAG_PCMM (\r\n   ID INT NOT NULL,\r\n" //$NON-NLS-1$
					+ "   NAME VARCHAR(200) NULL,\r\n   DESCRIPTION VARCHAR(2000) NULL,\r\n" //$NON-NLS-1$
					+ "   DATE_TAG DATE,\r\n   USER_CREATION_ID INT NOT NULL,\r\n   DATE_CREATION DATE,\r\n" //$NON-NLS-1$
					+ "   DATE_DELETE DATE,\r\n   DATE_UPDATE DATE\r\n);\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "\r\n--Insert values from TAG to TAG_PCMM table\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "INSERT INTO TAG_PCMM (ID, NAME, DESCRIPTION, DATE_TAG, USER_CREATION_ID)\r\n" //$NON-NLS-1$
					+ "SELECT ID, NAME, DESCRIPTION, DATETAG, USER_CREATION_ID \r\nFROM TAG\r\nWHERE\r\n" //$NON-NLS-1$
					+ "NOT EXISTS \r\n  (SELECT ID, NAME, DESCRIPTION, DATE_TAG, USER_CREATION_ID \r\n" //$NON-NLS-1$
					+ "   FROM TAG_PCMM);\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "\r\n\r\n--Drop Foreign keys\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "ALTER TABLE TAG DROP CONSTRAINT FK_PCMMASSESSMENT_TAG_ID;\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "ALTER TABLE TAG DROP CONSTRAINT FK_PCMMEVIDENCE_TAG_ID;\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "\r\n--Drop old table TAG\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "DROP TABLE IF EXISTS TAG;\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "\r\n\r\n\r\n--Alter table TAG, rename to TAG_PCMM\r\n"; //$NON-NLS-1$
			sqlQueriesFromScript += "--ALTER TABLE TAG RENAME TO TAG_PCMM;\r\n"; //$NON-NLS-1$
			File scriptFile = FileTools.createFile(createdFolder + "/test"); //$NON-NLS-1$

			// Open given file in append mode.
			BufferedWriter out = new BufferedWriter(new FileWriter(scriptFile, true));
			out.write(sqlQueriesFromScript);
			out.close();

			List<String> listSqlQueries = ((EclipseLinkMigrationManager) getDaoManager().getDbMigrationManager())
					.splitSqlQueries(scriptFile.toPath());
			assertNotNull(listSqlQueries);
			assertEquals(6, listSqlQueries.size());

			boolean deleted = scriptFile.delete();
			assertTrue(deleted);

		} catch (IOException | CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_sqlVariableQuote() {
		String variable = "my variable with 'quotes'"; //$NON-NLS-1$
		String variableQuoted = ((EclipseLinkMigrationManager) getDaoManager().getDbMigrationManager())
				.sqlVariableQuote(variable);
		assertEquals("my variable with ''quotes''", variableQuoted); //$NON-NLS-1$
	}
}
