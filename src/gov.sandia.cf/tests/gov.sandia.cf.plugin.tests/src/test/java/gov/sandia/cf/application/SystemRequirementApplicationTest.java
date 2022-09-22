/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.requirement.ISystemRequirementApplication;
import gov.sandia.cf.dao.ISystemRequirementConstraintRepository;
import gov.sandia.cf.dao.ISystemRequirementParamRepository;
import gov.sandia.cf.dao.ISystemRequirementRepository;
import gov.sandia.cf.dao.ISystemRequirementSelectValueRepository;
import gov.sandia.cf.dao.ISystemRequirementValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementConstraint;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.SystemRequirementValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the System Requirement Application
 */
class SystemRequirementApplicationTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(SystemRequirementApplicationTest.class);

	/* ************ loadSysRequirementConfiguration ************* */

	@Test
	void test_loadSystemRequirementConfiguration_Enabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), newModel, null);

		SystemRequirementSpecification spec = getAppManager().getService(ISystemRequirementApplication.class)
				.loadSysRequirementConfiguration(newModel);

		assertNotNull(spec);
		assertNotNull(spec.getParameters());
		assertEquals(2, spec.getParameters().size());

	}

	@Test
	void test_loadSystemRequirementConfiguration_Disabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		SystemRequirementSpecification spec = getAppManager().getService(ISystemRequirementApplication.class)
				.loadSysRequirementConfiguration(newModel);

		assertNull(spec);
	}

	/* ************ getParameterByModel ************* */

	@Test
	void test_getRequirementById_Working() {

		// Construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		SystemRequirement requirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), newModel, null,
				null);

		// test
		SystemRequirement found = getAppManager().getService(ISystemRequirementApplication.class)
				.getRequirementById(requirement.getId());
		assertNotNull(found);
		assertEquals(found.getId(), requirement.getId());
	}

	/* ************ getSystemRequirementRootByModel ************* */
	@Test
	void test_getSystemRequirementRootByModel_Working() {

		// construct data
		// Model
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// kept
		SystemRequirement newSystemRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), newModel,
				null, null);

		// to delete
		SystemRequirement newSystemRequirement2 = TestEntityFactory.getNewSystemRequirement(getDaoManager(), newModel,
				newSystemRequirement, null);

		List<SystemRequirement> roots = getAppManager().getService(ISystemRequirementApplication.class)
				.getRequirementRootByModel(newModel);
		getAppManager().getService(ISystemRequirementApplication.class).refresh(newSystemRequirement);

		// test
		assertNotNull(roots);
		assertEquals(1, roots.size());
		assertEquals(newSystemRequirement, roots.iterator().next());
		assertNotNull(roots.get(0));
		assertNotNull(roots.get(0).getChildren());
		assertEquals(newSystemRequirement2, roots.get(0).getChildren().get(0));
	}

	/* ************ getParameterByModel ************* */

	@Test
	void test_getParameterByModel_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), newModel, null);

		// test
		List<SystemRequirementParam> parameterByModel = getAppManager().getService(ISystemRequirementApplication.class)
				.getParameterByModel(newModel);
		assertNotNull(parameterByModel);
		assertEquals(2, parameterByModel.size());
	}

	/* ************ addRequirement ************* */
	@Test
	void test_addRequirement_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		SystemRequirementParam newSystemRequirementParam = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), newModel, null);
		SystemRequirementParam newSystemRequirementParam2 = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), newModel, null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		SystemRequirement requirement = new SystemRequirement();
		requirement.setStatement("SystemRequirement"); //$NON-NLS-1$
		requirement.setCreationDate(new Date());
		requirement.setModel(newModel);
		requirement.setUserCreation(newUser);

		SystemRequirementValue value1 = new SystemRequirementValue();
		value1.setDateCreation(new Date());
		value1.setRequirement(requirement);
		value1.setParameter(newSystemRequirementParam);
		value1.setUserCreation(newUser);
		value1.setValue("My VALUE"); //$NON-NLS-1$

		SystemRequirementValue value2 = new SystemRequirementValue();
		value2.setDateCreation(null);
		value2.setRequirement(requirement);
		value2.setParameter(newSystemRequirementParam2);
		value2.setUserCreation(null);
		value2.setValue(null);

		requirement.setRequirementParameterList(Arrays.asList(value1, value2));

		// test
		try {
			SystemRequirement added = getAppManager().getService(ISystemRequirementApplication.class)
					.addRequirement(requirement, newModel, newUser);

			assertNotNull(added);
			assertNotNull(added.getId());

			SystemRequirement found = getDaoManager().getRepository(ISystemRequirementRepository.class)
					.findById(added.getId());

			assertNotNull(found.getRequirementParameterList());
			assertEquals(newModel, found.getModel());
			assertEquals(newUser, found.getUserCreation());
			assertEquals(2, found.getRequirementParameterList().size());
			assertTrue(found.getRequirementParameterList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newSystemRequirementParam)));
			assertTrue(found.getRequirementParameterList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newSystemRequirementParam2)));
			assertTrue(found.getRequirementParameterList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getValue().equals("My VALUE"))); //$NON-NLS-1$
			assertTrue(found.getRequirementParameterList().stream().anyMatch(v -> v.getValue() == null));

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_addRequirement_WorkingNoValues() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), newModel, null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		SystemRequirement requirement = new SystemRequirement();
		requirement.setStatement("SystemRequirement"); //$NON-NLS-1$
		requirement.setCreationDate(new Date());
		requirement.setModel(newModel);
		requirement.setUserCreation(newUser);

		// test
		try {
			SystemRequirement added = getAppManager().getService(ISystemRequirementApplication.class)
					.addRequirement(requirement, newModel, newUser);

			assertNotNull(added);
			assertNotNull(added.getId());

			SystemRequirement found = getDaoManager().getRepository(ISystemRequirementRepository.class)
					.findById(added.getId());

			assertNotNull(found.getRequirementParameterList());
			assertEquals(newModel, found.getModel());
			assertEquals(newUser, found.getUserCreation());
			assertEquals(0, found.getRequirementParameterList().size());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_addRequirement_Null() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(ISystemRequirementApplication.class).addRequirement(null, newModel, newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_NULL), e.getMessage());
		}
	}

	@Test
	void test_addRequirement_StatementExists() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), newModel, null);
		SystemRequirement newSystemRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), newModel,
				null, newUser);

		SystemRequirement requirement = newSystemRequirement.copy();

		// test
		try {
			getAppManager().getService(ISystemRequirementApplication.class).addRequirement(requirement, newModel,
					newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_STATEMENTDUPLICATED,
					requirement.getStatement()), e.getMessage());
		}
	}

	@Test
	void test_addRequirement_UserNull() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			getAppManager().getService(ISystemRequirementApplication.class).addRequirement(new SystemRequirement(),
					newModel, null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_USERNULL), e.getMessage());
		}
	}

	@Test
	void test_addRequirement_ModelNull() {

		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(ISystemRequirementApplication.class).addRequirement(new SystemRequirement(),
					null, newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_MODELNULL), e.getMessage());
		}
	}

	/* ************ updateRequirement ************* */

	@Test
	void test_updateRequirement_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		SystemRequirementParam newSystemRequirementParam = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), newModel, null);
		SystemRequirementParam newSystemRequirementParam2 = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), newModel, null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		SystemRequirement requirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), newModel, null,
				newUser);

		SystemRequirementValue value1 = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), requirement,
				newSystemRequirementParam, newUser);
		value1.setValue("My VALUE"); //$NON-NLS-1$
		SystemRequirementValue value2 = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), requirement,
				newSystemRequirementParam2, newUser);
		value2.setValue(null);

		getDaoManager().getRepository(ISystemRequirementRepository.class).refresh(requirement);
		assertEquals(2, requirement.getRequirementParameterList().size());

		// update
		value1.setValue("UPDATED"); //$NON-NLS-1$
		value2.setValue("UPDATED 2"); //$NON-NLS-1$

		// test
		try {
			SystemRequirement updated = getAppManager().getService(ISystemRequirementApplication.class)
					.updateRequirement(requirement, newUser);

			assertNotNull(updated);
			assertNotNull(updated.getId());
			assertEquals(requirement.getId(), updated.getId());
			assertNotNull(updated.getRequirementParameterList());
			assertEquals(requirement.getModel(), updated.getModel());
			assertEquals(requirement.getUserCreation(), updated.getUserCreation());
			assertEquals(2, updated.getRequirementParameterList().size());
			assertTrue(updated.getRequirementParameterList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newSystemRequirementParam)));
			assertTrue(updated.getRequirementParameterList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newSystemRequirementParam2)));
			assertTrue(updated.getRequirementParameterList().stream().anyMatch(v -> "UPDATED".equals(v.getValue()))); //$NON-NLS-1$
			assertTrue(updated.getRequirementParameterList().stream().anyMatch(v -> "UPDATED 2".equals(v.getValue()))); //$NON-NLS-1$

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_updateRequirement_Null() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(ISystemRequirementApplication.class).updateRequirement(null, newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_UPDATE_REQUIREMENTROW_NULL), e.getMessage());
		}
	}

	@Test
	void test_updateRequirement_IdNull() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(ISystemRequirementApplication.class).updateRequirement(new SystemRequirement(),
					newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_UPDATE_REQUIREMENTROW_IDNULL), e.getMessage());
		}
	}

	@Test
	void test_updateRequirement_UserNull() {

		SystemRequirement requirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), null, null, null);

		// test
		try {
			getAppManager().getService(ISystemRequirementApplication.class).updateRequirement(requirement, null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_UPDATE_REQUIREMENTROW_USERNULL), e.getMessage());
		}
	}

	/* ************ existsSystemRequirementTitle ************* */

	@Test
	void test_existsSystemRequirementTitle_Exists() {

		// construct data
		SystemRequirement newSystemRequirement1 = TestEntityFactory.getNewSystemRequirement(getDaoManager(), null, null,
				null);

		// test
		try {
			boolean existsSystemRequirementTitle = getAppManager().getService(ISystemRequirementApplication.class)
					.existsRequirementStatement(null, newSystemRequirement1.getStatement());
			assertTrue(existsSystemRequirementTitle);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_existsSystemRequirementTitle_ExistsButExceptId() {

		// construct data
		SystemRequirement newSystemRequirement1 = TestEntityFactory.getNewSystemRequirement(getDaoManager(), null, null,
				null);

		// test
		try {
			boolean existsSystemRequirementTitle = getAppManager().getService(ISystemRequirementApplication.class)
					.existsRequirementStatement(new Integer[] { newSystemRequirement1.getId() },
							newSystemRequirement1.getStatement());
			assertFalse(existsSystemRequirementTitle);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	/* ************ deleteSystemRequirement************* */

	@Test
	void test_deleteSystemRequirement_Working() {

		// construct data
		// kept
		SystemRequirement newSystemRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), null, null,
				null);
		SystemRequirementValue newSystemRequirementValue = TestEntityFactory
				.getNewSystemRequirementValue(getDaoManager(), newSystemRequirement, null, null);

		// to delete
		SystemRequirement newSystemRequirement2 = TestEntityFactory.getNewSystemRequirement(getDaoManager(), null, null,
				null);
		TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), newSystemRequirement2, null, null);
		TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), newSystemRequirement2, null, null);

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class).deleteRequirement(newSystemRequirement2,
					null);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// Test
		List<SystemRequirement> findAll = getDaoManager().getRepository(ISystemRequirementRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newSystemRequirement, findAll.iterator().next());

		List<SystemRequirementValue> findAllValues = getDaoManager()
				.getRepository(ISystemRequirementValueRepository.class).findAll();
		assertNotNull(findAllValues);
		assertEquals(1, findAllValues.size());
		assertEquals(newSystemRequirementValue, findAllValues.iterator().next());
	}

	@Test
	void test_deleteSystemRequirement_Null() {

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class).deleteRequirement(null, null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTROW_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteSystemRequirement_IdNull() {

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class).deleteRequirement(new SystemRequirement(),
					null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTROW_IDNULL), e.getMessage());
		}
	}

	/* ************ deleteAllRequirementParam ************* */

	@Test
	void test_deleteAllRequirementParam_Working() {

		// construct data
		// Kept
		SystemRequirementParam newSystemRequirementParam = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), null, null);
		// to delete
		SystemRequirementParam newSystemRequirementParam2 = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), null, null);
		SystemRequirementParam newSystemRequirementParam3 = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), null, null);

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class)
					.deleteAllRequirementParam(Arrays.asList(newSystemRequirementParam2, newSystemRequirementParam3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<SystemRequirementParam> findAll = getDaoManager().getRepository(ISystemRequirementParamRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newSystemRequirementParam, findAll.iterator().next());
	}

	/* ************ deleteRequirementParam ************* */

	@Test
	void test_deleteRequirementParam_Working() {

		// construct data
		// Kept
		SystemRequirementParam newSystemRequirementParam = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), null, null);
		// to delete
		SystemRequirementParam newSystemRequirementParam2 = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), null, null);

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class)
					.deleteRequirementParam(newSystemRequirementParam2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<SystemRequirementParam> findAll = getDaoManager().getRepository(ISystemRequirementParamRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newSystemRequirementParam, findAll.iterator().next());
	}

	@Test
	void test_deleteRequirementParamm_Null() {

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class).deleteRequirementParam(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTPARAM_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteRequirementParam_IdNull() {

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class)
					.deleteRequirementParam(new SystemRequirementParam());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTPARAM_IDNULL), e.getMessage());
		}
	}

	@Test
	void test_deleteRequirementParam_WithValuesSelectWorking() {

		// Construct data
		// to delete
		SystemRequirementParam newSystemRequirementParam = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), null, null);

		// select values
		TestEntityFactory.getNewGenericSelectValue(getDaoManager(), SystemRequirementSelectValue.class,
				newSystemRequirementParam, ISystemRequirementSelectValueRepository.class);
		TestEntityFactory.getNewGenericSelectValue(getDaoManager(), SystemRequirementSelectValue.class,
				newSystemRequirementParam, ISystemRequirementSelectValueRepository.class);

		// values
		TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), null, newSystemRequirementParam, null);
		TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), null, newSystemRequirementParam, null);

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class)
					.deleteRequirementParam(newSystemRequirementParam);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<SystemRequirementParam> findAllParam = getDaoManager()
				.getRepository(ISystemRequirementParamRepository.class).findAll();
		assertNotNull(findAllParam);
		assertTrue(findAllParam.isEmpty());

		List<SystemRequirementValue> findAllValues = getDaoManager()
				.getRepository(ISystemRequirementValueRepository.class).findAll();
		assertNotNull(findAllValues);
		assertTrue(findAllValues.isEmpty());

		List<SystemRequirementSelectValue> findAllSelectValues = getDaoManager()
				.getRepository(ISystemRequirementSelectValueRepository.class).findAll();
		assertNotNull(findAllSelectValues);
		assertTrue(findAllSelectValues.isEmpty());

		List<SystemRequirementConstraint> findAllConstraints = getDaoManager()
				.getRepository(ISystemRequirementConstraintRepository.class).findAll();
		assertNotNull(findAllConstraints);
		assertTrue(findAllConstraints.isEmpty());
	}

	/* ************ deleteAllSystemRequirementValue ************* */

	@Test
	void test_deleteAllSystemRequirementValue_Working() {

		// construct data
		// Kept
		SystemRequirementValue newSystemRequirementValue = TestEntityFactory
				.getNewSystemRequirementValue(getDaoManager(), null, null, null);
		// to delete
		SystemRequirementValue newSystemRequirementValue2 = TestEntityFactory
				.getNewSystemRequirementValue(getDaoManager(), null, null, null);
		SystemRequirementValue newSystemRequirementValue3 = TestEntityFactory
				.getNewSystemRequirementValue(getDaoManager(), null, null, null);

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class)
					.deleteAllRequirementValue(Arrays.asList(newSystemRequirementValue2, newSystemRequirementValue3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<SystemRequirementValue> findAll = getDaoManager().getRepository(ISystemRequirementValueRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newSystemRequirementValue, findAll.iterator().next());
	}

	/* ************ deleteRequirementValue ************* */

	@Test
	void test_deleteRequirementValue_Working() {

		// construct data
		// Kept
		SystemRequirementValue newSystemRequirementValue = TestEntityFactory
				.getNewSystemRequirementValue(getDaoManager(), null, null, null);
		// to delete
		SystemRequirementValue newSystemRequirementValue2 = TestEntityFactory
				.getNewSystemRequirementValue(getDaoManager(), null, null, null);

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class)
					.deleteRequirementValue(newSystemRequirementValue2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<SystemRequirementValue> findAll = getDaoManager().getRepository(ISystemRequirementValueRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newSystemRequirementValue, findAll.iterator().next());
	}

	@Test
	void test_deleteRequirementValue_Null() {

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class).deleteRequirementValue(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTVALUE_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteRequirementValue_IdNull() {

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class)
					.deleteRequirementValue(new SystemRequirementValue());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTVALUE_IDNULL), e.getMessage());
		}
	}

	/* ************ deleteAllRequirementSelectValue ************* */

	@Test
	void test_deleteAllRequirementSelectValue_Working() {

		// construct data
		SystemRequirementParam parameter = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), null, null);
		// Kept
		SystemRequirementSelectValue newRequirementSelectValue = TestEntityFactory.getNewGenericSelectValue(
				getDaoManager(), SystemRequirementSelectValue.class, parameter,
				ISystemRequirementSelectValueRepository.class);
		// to delete
		SystemRequirementSelectValue newRequirementSelectValue2 = TestEntityFactory.getNewGenericSelectValue(
				getDaoManager(), SystemRequirementSelectValue.class, parameter,
				ISystemRequirementSelectValueRepository.class);
		SystemRequirementSelectValue newRequirementSelectValue3 = TestEntityFactory.getNewGenericSelectValue(
				getDaoManager(), SystemRequirementSelectValue.class, parameter,
				ISystemRequirementSelectValueRepository.class);

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class).deleteAllRequirementSelectValue(
					Arrays.asList(newRequirementSelectValue2, newRequirementSelectValue3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<SystemRequirementSelectValue> findAll = getDaoManager()
				.getRepository(ISystemRequirementSelectValueRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newRequirementSelectValue, findAll.iterator().next());
	}

	/* ************ deleteRequirementSelectValue ************* */

	@Test
	void test_deleteRequirementSelectValue_Working() {

		// construct data
		SystemRequirementParam parameter = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), null, null);
		// Kept
		SystemRequirementSelectValue newRequirementSelectValue = TestEntityFactory.getNewGenericSelectValue(
				getDaoManager(), SystemRequirementSelectValue.class, parameter,
				ISystemRequirementSelectValueRepository.class);
		// to delete
		SystemRequirementSelectValue newRequirementSelectValue2 = TestEntityFactory.getNewGenericSelectValue(
				getDaoManager(), SystemRequirementSelectValue.class, parameter,
				ISystemRequirementSelectValueRepository.class);

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class)
					.deleteRequirementSelectValue(newRequirementSelectValue2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<SystemRequirementSelectValue> findAll = getDaoManager()
				.getRepository(ISystemRequirementSelectValueRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newRequirementSelectValue, findAll.iterator().next());
	}

	@Test
	void test_deleteRequirementSelectValue_Null() {

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class).deleteRequirementSelectValue(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTSELECTVALUE_NULL),
					e.getMessage());
		}
	}

	@Test
	void test_deleteRequirementSelectValue_IdNull() {

		// delete
		try {
			getAppManager().getService(ISystemRequirementApplication.class)
					.deleteRequirementSelectValue(new SystemRequirementSelectValue());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTSELECTVALUE_IDNULL),
					e.getMessage());
		}
	}

	/* ************ isSystemRequirementEnabled ************* */

	@Test
	void test_isSystemRequirementEnabled_Enabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), newModel, null);

		boolean SystemRequirementEnabled = getAppManager().getService(ISystemRequirementApplication.class)
				.isRequirementEnabled(newModel);
		assertTrue(SystemRequirementEnabled);
	}

	@Test
	void test_isSystemRequirementEnabled_Disabled() {
		boolean SystemRequirementEnabled = getAppManager().getService(ISystemRequirementApplication.class)
				.isRequirementEnabled(TestEntityFactory.getNewModel(getDaoManager()));
		assertFalse(SystemRequirementEnabled);
	}

	@Test
	void test_sameConfiguration() {
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), newModel, null);

		SystemRequirementSpecification spec = getAppManager().getService(ISystemRequirementApplication.class)
				.loadSysRequirementConfiguration(newModel);

		assertFalse(getAppManager().getService(ISystemRequirementApplication.class).sameConfiguration(null, spec));
		assertFalse(getAppManager().getService(ISystemRequirementApplication.class).sameConfiguration(spec, null));
		assertTrue(getAppManager().getService(ISystemRequirementApplication.class).sameConfiguration(spec, spec));
		assertTrue(getAppManager().getService(ISystemRequirementApplication.class).sameConfiguration(null, null));
	}
}
