/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/

package gov.sandia.cf.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.MigrationLogRepository;
import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.MigrationLog;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the UserRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class MigrationLogRepositoryTest extends AbstractTestRepository<MigrationLog, Integer, MigrationLogRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(MigrationLogRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<MigrationLogRepository> getRepositoryClass() {
		return MigrationLogRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<MigrationLog> getModelClass() {
		return MigrationLog.class;
	}

	@Override
	MigrationLog getModelFulfilled(MigrationLog model) {
		model.setDatabaseVersion("DATABASE_VERSION"); //$NON-NLS-1$
		model.setDateExecution(new Date());
		model.setIsError(false);
		model.setScriptName("SCRIPT_NAME"); //$NON-NLS-1$
		return model;
	}

	@Test
	@Override
	void testCRUD() {

		List<MigrationLog> all = getRepository().findAll();
		assertNotNull(all);
		for (MigrationLog log : all) {
			getRepository().delete(log);
		}

		// instantiate the model with Strings fulfilled
		MigrationLog toCreate = fulfillModelStrings(getModelInstance());
		toCreate.setDateExecution(new Date());
		toCreate.setIsError(false);

		// create
		try {
			toCreate = getRepository().create(toCreate);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// select all
		all = getRepository().findAll();
		assertNotNull(all);
		assertFalse(all.isEmpty());
		assertEquals(MessageFormat.format("The size is: {0} and should be 1.", all.size()), 1, all.size()); //$NON-NLS-1$
		MigrationLog first = all.get(0);
		compareString(first, toCreate);

		// findbyid
		MigrationLog firstFound = getRepository().findById(first.getId());
		assertNotNull(firstFound);
		assertSame(firstFound, first);

		// refresh
		getRepository().refresh(firstFound);

		// update
		String updateSuffix = "UPDATED"; //$NON-NLS-1$
		for (Field field : toCreate.getClass().getDeclaredFields()) {

			// assign only strings
			if (!Modifier.isFinal(field.getModifiers()) && field.getType().isAssignableFrom(String.class)) {
				PropertyDescriptor pd;
				try {
					pd = new PropertyDescriptor(field.getName(), getModelClass());
					pd.getWriteMethod().invoke(toCreate, field.getName() + updateSuffix);
				} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					fail(e.getMessage());
				}
			}
		}
		try {
			MigrationLog updated = getRepository().update(firstFound);
			MigrationLog updatedFound = getRepository().findById(firstFound.getId());
			assertSame(updated, updatedFound);
			compareString(updated, updatedFound);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// delete
		getRepository().delete(first);
		all = getRepository().findAll();
		assertTrue(all.isEmpty());

	}

	@SuppressWarnings("unchecked")
	@Test
	@Override
	void testExecuteQuery() {

		// instantiate the model with Strings fulfilled
		MigrationLog toCreate = fulfillModelStrings(getModelInstance());
		toCreate.setDateExecution(new Date());
		toCreate.setIsError(false);

		// create
		try {
			toCreate = getRepository().create(toCreate);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// list by native query
		EntityManager entityManager = getDaoManager().getEntityManager();

		// get annotation Table on the Model to get the table name
		Table[] annotationsByType = getModelClass().getAnnotationsByType(javax.persistence.Table.class);
		Table table = annotationsByType[0];

		// query
		Query nativeQuery = entityManager.createNativeQuery("SELECT * FROM " + table.name(), getModelClass()); //$NON-NLS-1$
		List<MigrationLog> resultList = nativeQuery.getResultList();

		assertTrue(!resultList.isEmpty());

		// delete
		getRepository().delete(toCreate);

	}

	@Test
	void test_AllMethods() {

		String scriptName = TABLE_MIGRATION_SCRIPT;

		// get log script
		MigrationLog scriptLog = getRepository().getScriptLog(scriptName);
		assertNull(scriptLog);

		// test is last execution in error
		assertNull(getRepository().isLastExecutionInError(scriptName));

		// test mark as done
		String version = "0.2.0-test"; //$NON-NLS-1$
		IMigrationTask task = new IMigrationTask() {

			@Override
			public String getName() {
				return scriptName;
			}

			@Override
			public boolean execute(DaoManager daoManager) throws CredibilityMigrationException {
				return true;
			}
		};

		try {
			getRepository().markTaskAsExecuted(task, version);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		scriptLog = getRepository().getScriptLog(scriptName);
		assertNotNull(scriptLog);
		assertEquals(scriptName, scriptLog.getScriptName());
		assertEquals(version, scriptLog.getDatabaseVersion());
		assertNull(scriptLog.getExecutionLog());
		assertFalse(scriptLog.getIsError());
		assertNotNull(scriptLog.getDateExecution());

		// test is last execution in error
		assertFalse(getRepository().isLastExecutionInError(scriptName));

		// test mark in error
		String version2 = "0.2.0-test2"; //$NON-NLS-1$
		String errorLog = "Error Code: -5504\r\n" //$NON-NLS-1$
				+ "Call: ALTER TABLE QOI ADD CONSTRAINT FK_QOI_MODEL_ID FOREIGN KEY (MODEL_ID) REFERENCES MODEL (ID)\r\n" //$NON-NLS-1$
				+ "Query: DataModifyQuery(sql=\"ALTER TABLE QOI ADD CONSTRAINT FK_QOI_MODEL_ID FOREIGN KEY (MODEL_ID) REFERENCES MODEL (ID)\")\r\n" //$NON-NLS-1$
				+ "[EL Warning]: 2020-07-16 15:54:56.627--ServerSession(1464984894)--Exception [EclipseLink-4002] (Eclipse Persistence Services - 2.7.4.v20190115-ad5b7c6b2a): " //$NON-NLS-1$
				+ "org.eclipse.persistence.exceptions.DatabaseException\r\n" //$NON-NLS-1$
				+ "Internal Exception: java.sql.SQLSyntaxErrorException: object name already exists: FK_TAG_USER_CREATION_ID in statement " //$NON-NLS-1$
				+ "[ALTER TABLE TAG ADD CONSTRAINT FK_TAG_USER_CREATION_ID FOREIGN KEY (USER_CREATION_ID) REFERENCES USER (ID)]\r\n" //$NON-NLS-1$
				+ "Error Code: -5504\r\n" //$NON-NLS-1$
				+ "Call: ALTER TABLE TAG ADD CONSTRAINT FK_TAG_USER_CREATION_ID FOREIGN KEY (USER_CREATION_ID) REFERENCES USER (ID)\r\n" //$NON-NLS-1$
				+ "Query: DataModifyQuery(sql=\"ALTER TABLE TAG ADD CONSTRAINT FK_TAG_USER_CREATION_ID FOREIGN KEY (USER_CREATION_ID) REFERENCES USER (ID)\")\r\n" //$NON-NLS-1$
				+ "[EL Warning]: 2020-07-16 15:54:56.629--ServerSession(1464984894)--Exception [EclipseLink-4002] (Eclipse Persistence Services - 2.7.4.v20190115-ad5b7c6b2a): " //$NON-NLS-1$
				+ "org.eclipse.persistence.exceptions.DatabaseException\r\n" //$NON-NLS-1$
				+ "Internal Exception: java.sql.SQLSyntaxErrorException: object name already exists: FK_USER_CURRENT_ROLE_ID in statement " //$NON-NLS-1$
				+ "[ALTER TABLE USER ADD CONSTRAINT FK_USER_CURRENT_ROLE_ID FOREIGN KEY (CURRENT_ROLE_ID) REFERENCES ROLE (ID)]\r\n"; //$NON-NLS-1$
		try {
			getRepository().markTaskInError(task, version2, errorLog);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		scriptLog = getRepository().getScriptLog(scriptName);
		assertNotNull(scriptLog);
		assertEquals(scriptName, scriptLog.getScriptName());
		assertEquals(version2, scriptLog.getDatabaseVersion());
		assertEquals(errorLog, scriptLog.getExecutionLog());
		assertTrue(scriptLog.getIsError());
		assertNotNull(scriptLog.getDateExecution());

		// test is last execution in error
		assertTrue(getRepository().isLastExecutionInError(scriptName));
	}

	@Test
	void test_getScriptLog_ScriptNull() {
		try {
			getRepository().getScriptLog(null);
			fail("This sould fail."); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			assertEquals(RscTools.getString(RscConst.EX_MIGRATIONLOG_SCRIPTNAME_BLANK), e.getMessage());
		}
	}

	@Test
	void test_insertScriptLog_InfoLog() {

		// test mark as done
		String version = "0.2.0-test"; //$NON-NLS-1$
		String taskName = "task"; //$NON-NLS-1$
		String errorLog = ""; //$NON-NLS-1$
		try {
			getRepository().insertScriptLog(taskName, version, errorLog, false);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		MigrationLog scriptLog = getRepository().getScriptLog(taskName);
		assertNotNull(scriptLog);
		assertEquals(taskName, scriptLog.getScriptName());
		assertEquals(version, scriptLog.getDatabaseVersion());
		assertEquals(errorLog, scriptLog.getExecutionLog());
		assertFalse(scriptLog.getIsError());
		assertNotNull(scriptLog.getDateExecution());

		// test is last execution in error
		assertFalse(getRepository().isLastExecutionInError(taskName));
	}

	@Test
	void test_insertScriptLog_ErrorLog() {

		// test mark as done
		String version = "0.2.0-test"; //$NON-NLS-1$
		String taskName = "task"; //$NON-NLS-1$
		String errorLog = "Error Code: -5504\r\n" //$NON-NLS-1$
				+ "Call: ALTER TABLE QOI ADD CONSTRAINT FK_QOI_MODEL_ID FOREIGN KEY (MODEL_ID) REFERENCES MODEL (ID)\r\n" //$NON-NLS-1$
				+ "Query: DataModifyQuery(sql=\"ALTER TABLE QOI ADD CONSTRAINT FK_QOI_MODEL_ID FOREIGN KEY (MODEL_ID) REFERENCES MODEL (ID)\")\r\n" //$NON-NLS-1$
				+ "[EL Warning]: 2020-07-16 15:54:56.627--ServerSession(1464984894)--Exception [EclipseLink-4002] (Eclipse Persistence Services - 2.7.4.v20190115-ad5b7c6b2a): " //$NON-NLS-1$
				+ "org.eclipse.persistence.exceptions.DatabaseException\r\n" //$NON-NLS-1$
				+ "Internal Exception: java.sql.SQLSyntaxErrorException: object name already exists: FK_TAG_USER_CREATION_ID in statement " //$NON-NLS-1$
				+ "[ALTER TABLE TAG ADD CONSTRAINT FK_TAG_USER_CREATION_ID FOREIGN KEY (USER_CREATION_ID) REFERENCES USER (ID)]\r\n" //$NON-NLS-1$
				+ "Error Code: -5504\r\n" //$NON-NLS-1$
				+ "Call: ALTER TABLE TAG ADD CONSTRAINT FK_TAG_USER_CREATION_ID FOREIGN KEY (USER_CREATION_ID) REFERENCES USER (ID)\r\n" //$NON-NLS-1$
				+ "Query: DataModifyQuery(sql=\"ALTER TABLE TAG ADD CONSTRAINT FK_TAG_USER_CREATION_ID FOREIGN KEY (USER_CREATION_ID) REFERENCES USER (ID)\")\r\n" //$NON-NLS-1$
				+ "[EL Warning]: 2020-07-16 15:54:56.629--ServerSession(1464984894)--Exception [EclipseLink-4002] (Eclipse Persistence Services - 2.7.4.v20190115-ad5b7c6b2a): " //$NON-NLS-1$
				+ "org.eclipse.persistence.exceptions.DatabaseException\r\n" //$NON-NLS-1$
				+ "Internal Exception: java.sql.SQLSyntaxErrorException: object name already exists: FK_USER_CURRENT_ROLE_ID in statement " //$NON-NLS-1$
				+ "[ALTER TABLE USER ADD CONSTRAINT FK_USER_CURRENT_ROLE_ID FOREIGN KEY (CURRENT_ROLE_ID) REFERENCES ROLE (ID)]\r\n"; //$NON-NLS-1$
		try {
			getRepository().insertScriptLog(taskName, version, errorLog, true);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		MigrationLog scriptLog = getRepository().getScriptLog(taskName);
		assertNotNull(scriptLog);
		assertEquals(taskName, scriptLog.getScriptName());
		assertEquals(version, scriptLog.getDatabaseVersion());
		assertEquals(errorLog, scriptLog.getExecutionLog());
		assertTrue(scriptLog.getIsError());
		assertNotNull(scriptLog.getDateExecution());

		// test is last execution in error
		assertTrue(getRepository().isLastExecutionInError(taskName));
	}

}
