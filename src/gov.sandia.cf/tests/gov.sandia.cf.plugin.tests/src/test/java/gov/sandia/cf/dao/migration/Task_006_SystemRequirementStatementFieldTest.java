/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.ISystemRequirementParamRepository;
import gov.sandia.cf.dao.ISystemRequirementRepository;
import gov.sandia.cf.dao.ISystemRequirementValueRepository;
import gov.sandia.cf.dao.migration.tasks.Task_006_SystemRequirementStatementField;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementValue;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * The Class Task_006_SystemRequirementStatementFieldTest.
 *
 * @author Didier Verstraete
 */
class Task_006_SystemRequirementStatementFieldTest extends AbstractTestDao {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(Task_006_SystemRequirementStatementFieldTest.class);

	/* ****** TEST: MigrationTask ***** */

	@Test
	void test_MigrationTask_NotMigrated() {

		// initialize data
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		// param
		SystemRequirementParam newSysReqParam = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				null);
		newSysReqParam.setName(Task_006_SystemRequirementStatementField.PARAM_STATEMENT_VALUE);
		try {
			newSysReqParam = getDaoManager().getRepository(ISystemRequirementParamRepository.class)
					.update(newSysReqParam);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		SystemRequirementParam newSysReqParamNotImpacted = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), model, null);

		// system requirement 1 + value
		SystemRequirement newSysReq1 = TestEntityFactory.getNewSystemRequirement(getDaoManager(), model, null, null);
		SystemRequirementValue newSysReqValue1 = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(),
				newSysReq1, newSysReqParam, null);
		setSystemRequirementValue(newSysReqValue1, "Sys Req 1 Statement Value");//$NON-NLS-1$

		// system requirement impacted
		SystemRequirement newSysReq2 = TestEntityFactory.getNewSystemRequirement(getDaoManager(), model, null, null);
		SystemRequirementValue newSysReqValue2 = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(),
				newSysReq2, newSysReqParam, null);
		setSystemRequirementValue(newSysReqValue2, "Sys Req 2 Statement Value");//$NON-NLS-1$

		// corrupt database
		// add system requirement value duplicate
		SystemRequirementValue newSysReqValue2Duplicate = TestEntityFactory
				.getNewSystemRequirementValue(getDaoManager(), newSysReq2, newSysReqParam, null);
		setSystemRequirementValue(newSysReqValue2Duplicate, "Sys Req 2 Statement Value");//$NON-NLS-1$

		// add system requirement value duplicate 2
		SystemRequirementValue newSysReqValue2Duplicate2 = TestEntityFactory
				.getNewSystemRequirementValue(getDaoManager(), newSysReq2, newSysReqParam, null);
		setSystemRequirementValue(newSysReqValue2Duplicate2, "Sys Req 2 Statement Value");//$NON-NLS-1$

		assertEquals(newSysReqValue2.getParameter(), newSysReqValue2Duplicate.getParameter());
		assertEquals(newSysReqValue2.getRequirement(), newSysReqValue2Duplicate.getRequirement());
		assertNotEquals(newSysReqValue2.getId(), newSysReqValue2Duplicate.getId());
		assertEquals(newSysReqValue2.getParameter(), newSysReqValue2Duplicate2.getParameter());
		assertEquals(newSysReqValue2.getRequirement(), newSysReqValue2Duplicate2.getRequirement());
		assertNotEquals(newSysReqValue2.getId(), newSysReqValue2Duplicate2.getId());

		// system requirement not impacted
		SystemRequirementValue newSysReqValue2NotImpacted = TestEntityFactory
				.getNewSystemRequirementValue(getDaoManager(), newSysReq2, newSysReqParamNotImpacted, null);
		setSystemRequirementValue(newSysReqValue2NotImpacted, "Sys Req 2 Statement Value");//$NON-NLS-1$

		// corrupt database
		// add statement
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		unitOfWork
				.executeNonSelectingSQL(MessageFormat.format("UPDATE COM_REQUIREMENT SET STATEMENT='''' WHERE ID={0};", //$NON-NLS-1$
						newSysReq1.getId()));
		getDaoManager().getRepository(ISystemRequirementRepository.class).refresh(newSysReq1);
		unitOfWork
				.executeNonSelectingSQL(MessageFormat.format("UPDATE COM_REQUIREMENT SET STATEMENT=NULL WHERE ID={0};", //$NON-NLS-1$
						newSysReq2.getId()));
		getDaoManager().getRepository(ISystemRequirementRepository.class).refresh(newSysReq2);

		// test
		try {
			boolean changed = new Task_006_SystemRequirementStatementField().execute(getDaoManager());
			assertTrue(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}

		// test value migration
		List<SystemRequirementValue> findAll = getDaoManager().getRepository(ISystemRequirementValueRepository.class)
				.findAll();
		assertFalse(findAll.contains(newSysReqValue2Duplicate));
		assertFalse(findAll.contains(newSysReqValue2Duplicate2));

		getDaoManager().getRepository(ISystemRequirementRepository.class).refresh(newSysReq1);
		assertEquals("Sys Req 1 Statement Value", newSysReq1.getStatement()); //$NON-NLS-1$

		getDaoManager().getRepository(ISystemRequirementRepository.class).refresh(newSysReq2);
		assertEquals("Sys Req 2 Statement Value", newSysReq2.getStatement()); //$NON-NLS-1$

		// test value entities deletion
		List<SystemRequirementValue> findAllValues = findAll;
		assertNotNull(findAllValues);
		assertEquals(1, findAllValues.size());
		assertEquals(newSysReqValue2NotImpacted, findAllValues.get(0));

		// test statement param deletion
		List<SystemRequirementParam> findAllParam = getDaoManager()
				.getRepository(ISystemRequirementParamRepository.class).findAll();
		assertNotNull(findAllParam);
		assertEquals(1, findAllParam.size());
		assertEquals(newSysReqParamNotImpacted, findAllParam.get(0));

	}

	private void setSystemRequirementValue(SystemRequirementValue sysReqValue, String value) {
		if (sysReqValue == null) {
			return;
		}

		sysReqValue.setValue(value); // $NON-NLS-1$

		try {
			getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(sysReqValue);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_MigrationTask_AlreadyMigrated() {

		// initialize data
		TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			boolean changed = new Task_006_SystemRequirementStatementField().execute(getDaoManager());
			assertFalse(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}
}
