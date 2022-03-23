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
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.decision.IDecisionApplication;
import gov.sandia.cf.dao.IDecisionConstraintRepository;
import gov.sandia.cf.dao.IDecisionParamRepository;
import gov.sandia.cf.dao.IDecisionRepository;
import gov.sandia.cf.dao.IDecisionSelectValueRepository;
import gov.sandia.cf.dao.IDecisionValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.DecisionValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Didier Verstraete
 *
 *         JUnit test class for the Global Application Controller
 */
@RunWith(JUnitPlatform.class)
class DecisionApplicationTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(DecisionApplicationTest.class);

	/* ************ loadQoIPlanningConfiguration ************* */

	@Test
	void test_loadDecisionConfiguration_Enabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);

		DecisionSpecification spec = getAppManager().getService(IDecisionApplication.class)
				.loadDecisionConfiguration(newModel);

		assertNotNull(spec);
		assertNotNull(spec.getParameters());
		assertEquals(2, spec.getParameters().size());

	}

	@Test
	void test_loadDecisionConfiguration_Disabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		DecisionSpecification spec = getAppManager().getService(IDecisionApplication.class)
				.loadDecisionConfiguration(newModel);

		assertNull(spec);

	}

	/* ************ getParameterByModel ************* */

	@Test
	void test_getParameterByModel_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);

		// test
		List<DecisionParam> parameterByModel = getAppManager().getService(IDecisionApplication.class)
				.getParameterByModel(newModel);
		assertNotNull(parameterByModel);
		assertEquals(2, parameterByModel.size());
	}

	/* ************ addDecision ************* */
	@Test
	void test_addDecision_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		DecisionParam newDecisionParam = TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);
		DecisionParam newDecisionParam2 = TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		Decision decision = new Decision();
		decision.setTitle("Decision"); //$NON-NLS-1$
		decision.setCreationDate(new Date());
		decision.setModel(newModel);
		decision.setUserCreation(newUser);

		DecisionValue value1 = new DecisionValue();
		value1.setDateCreation(new Date());
		value1.setDecision(decision);
		value1.setParameter(newDecisionParam);
		value1.setUserCreation(newUser);
		value1.setValue("My VALUE"); //$NON-NLS-1$

		DecisionValue value2 = new DecisionValue();
		value2.setDateCreation(new Date());
		value2.setDecision(decision);
		value2.setParameter(newDecisionParam2);
		value2.setUserCreation(newUser);
		value2.setValue(null);

		decision.setDecisionList(Arrays.asList(value1, value2));

		// test
		try {
			Decision added = getAppManager().getService(IDecisionApplication.class).addDecision(decision, newModel,
					newUser);

			assertNotNull(added);
			assertNotNull(added.getId());

			Decision found = getDaoManager().getRepository(IDecisionRepository.class).findById(added.getId());

			assertNotNull(found.getDecisionList());
			assertEquals(newModel, found.getModel());
			assertEquals(newUser, found.getUserCreation());
			assertEquals(2, found.getDecisionList().size());
			assertTrue(found.getDecisionList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newDecisionParam)));
			assertTrue(found.getDecisionList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newDecisionParam2)));
			assertTrue(found.getDecisionList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getValue().equals("My VALUE"))); //$NON-NLS-1$
			assertTrue(found.getDecisionList().stream().anyMatch(v -> v.getValue() == null));

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_addDecision_WorkingNoValues() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		Decision decision = new Decision();
		decision.setTitle("Decision"); //$NON-NLS-1$
		decision.setCreationDate(new Date());
		decision.setModel(newModel);
		decision.setUserCreation(newUser);

		// test
		try {
			Decision added = getAppManager().getService(IDecisionApplication.class).addDecision(decision, newModel,
					newUser);

			assertNotNull(added);
			assertNotNull(added.getId());

			Decision found = getDaoManager().getRepository(IDecisionRepository.class).findById(added.getId());

			assertNotNull(found.getDecisionList());
			assertEquals(newModel, found.getModel());
			assertEquals(newUser, found.getUserCreation());
			assertEquals(0, found.getDecisionList().size());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_addDecision_Null() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(IDecisionApplication.class).addDecision(null, newModel, newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_ADD_DECISIONROW_NULL), e.getMessage());
		}
	}

	@Test
	void test_addDecision_UserNull() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			getAppManager().getService(IDecisionApplication.class).addDecision(new Decision(), newModel, null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_ADD_DECISIONROW_USERNULL), e.getMessage());
		}
	}

	@Test
	void test_addDecision_ModelNull() {

		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(IDecisionApplication.class).addDecision(new Decision(), null, newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_ADD_DECISIONROW_MODELNULL), e.getMessage());
		}
	}

	/* ************ updateDecision ************* */

	@Test
	void test_updateDecision_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		DecisionParam newDecisionParam = TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);
		DecisionParam newDecisionParam2 = TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		Decision decision = TestEntityFactory.getNewDecision(getDaoManager(), newModel, null, newUser);

		DecisionValue value1 = TestEntityFactory.getNewDecisionValue(getDaoManager(), decision, newDecisionParam,
				newUser);
		value1.setValue("My VALUE"); //$NON-NLS-1$
		DecisionValue value2 = TestEntityFactory.getNewDecisionValue(getDaoManager(), decision, newDecisionParam2,
				newUser);
		value2.setValue(null);

		getDaoManager().getRepository(IDecisionRepository.class).refresh(decision);
		assertEquals(2, decision.getDecisionList().size());

		// update
		value1.setValue("UPDATED"); //$NON-NLS-1$
		value2.setValue("UPDATED 2"); //$NON-NLS-1$

		// test
		try {
			Decision updated = getAppManager().getService(IDecisionApplication.class).updateDecision(decision, newUser);

			assertNotNull(updated);
			assertNotNull(updated.getId());
			assertEquals(decision.getId(), updated.getId());
			assertNotNull(updated.getDecisionList());
			assertEquals(decision.getModel(), updated.getModel());
			assertEquals(decision.getUserCreation(), updated.getUserCreation());
			assertEquals(2, updated.getDecisionList().size());
			assertTrue(updated.getDecisionList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newDecisionParam)));
			assertTrue(updated.getDecisionList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newDecisionParam2)));
			assertTrue(updated.getDecisionList().stream().anyMatch(v -> "UPDATED".equals(v.getValue()))); //$NON-NLS-1$
			assertTrue(updated.getDecisionList().stream().anyMatch(v -> "UPDATED 2".equals(v.getValue()))); //$NON-NLS-1$

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_updateDecision_Null() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(IDecisionApplication.class).updateDecision(null, newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_UPDATE_DECISIONROW_NULL), e.getMessage());
		}
	}

	@Test
	void test_updateDecision_IdNull() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(IDecisionApplication.class).updateDecision(new Decision(), newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_UPDATE_DECISIONROW_IDNULL), e.getMessage());
		}
	}

	@Test
	void test_updateDecision_UserNull() {

		Decision decision = TestEntityFactory.getNewDecision(getDaoManager(), null, null, null);

		// test
		try {
			getAppManager().getService(IDecisionApplication.class).updateDecision(decision, null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_UPDATE_DECISIONROW_USERNULL), e.getMessage());
		}
	}

	/* ************ existsDecisionTitle ************* */

	@Test
	void test_existsDecisionTitle_Exists() {

		// construct data
		Decision newDecision1 = TestEntityFactory.getNewDecision(getDaoManager(), null, null, null);

		// test
		try {
			boolean existsDecisionTitle = getAppManager().getService(IDecisionApplication.class)
					.existsDecisionTitle(null, newDecision1.getTitle());
			assertTrue(existsDecisionTitle);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_existsDecisionTitle_ExistsButExceptId() {

		// construct data
		Decision newDecision1 = TestEntityFactory.getNewDecision(getDaoManager(), null, null, null);

		// test
		try {
			boolean existsDecisionTitle = getAppManager().getService(IDecisionApplication.class)
					.existsDecisionTitle(new Integer[] { newDecision1.getId() }, newDecision1.getTitle());
			assertFalse(existsDecisionTitle);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	/* ************ getDecisionById************* */
	@Test
	void test_getDecisionById_Working() {

		// Construct data
		Decision newDecision = TestEntityFactory.getNewDecision(getDaoManager(), null, null, null);

		// Retrieve by ID
		Decision retrieveDecision = getAppManager().getService(IDecisionApplication.class)
				.getDecisionById(newDecision.getId());

		// Test
		assertNotNull(retrieveDecision);
		assertEquals(newDecision.getId(), retrieveDecision.getId());
	}

	/* ************ getDecisionRootByModel ************* */
	@Test
	void test_getDecisionRootByModel_Working() {

		// construct data
		// Model
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// kept
		Decision newDecision = TestEntityFactory.getNewDecision(getDaoManager(), newModel, null, null);

		// to delete
		Decision newDecision2 = TestEntityFactory.getNewDecision(getDaoManager(), newModel, newDecision, null);

		List<Decision> roots = getAppManager().getService(IDecisionApplication.class).getDecisionRootByModel(newModel);
		getAppManager().getService(IDecisionApplication.class).refresh(newDecision);

		// test
		assertNotNull(roots);
		assertEquals(1, roots.size());
		assertEquals(newDecision, roots.iterator().next());
		assertNotNull(roots.get(0));
		assertNotNull(roots.get(0).getChildren());
		assertEquals(newDecision2, roots.get(0).getChildren().get(0));
	}

	/* ************ deleteDecision************* */

	@Test
	void test_deleteDecision_Working() {

		// construct data
		// kept
		Decision newDecision = TestEntityFactory.getNewDecision(getDaoManager(), null, null, null);
		DecisionValue newDecisionValue = TestEntityFactory.getNewDecisionValue(getDaoManager(), newDecision, null,
				null);

		// to delete
		Decision newDecision2 = TestEntityFactory.getNewDecision(getDaoManager(), null, null, null);
		TestEntityFactory.getNewDecisionValue(getDaoManager(), newDecision2, null, null);
		TestEntityFactory.getNewDecisionValue(getDaoManager(), newDecision2, null, null);

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecision(newDecision2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// Test
		List<Decision> findAll = getDaoManager().getRepository(IDecisionRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newDecision, findAll.iterator().next());

		List<DecisionValue> findAllValues = getDaoManager().getRepository(IDecisionValueRepository.class).findAll();
		assertNotNull(findAllValues);
		assertEquals(1, findAllValues.size());
		assertEquals(newDecisionValue, findAllValues.iterator().next());
	}

	@Test
	void test_deleteDecision_Null() {

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecision(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_DELETE_DECISIONROW_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteDecision_IdNull() {

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecision(new Decision());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_DELETE_DECISIONROW_IDNULL), e.getMessage());
		}
	}

	/* ************ deleteAllDecisionParam ************* */

	@Test
	void test_deleteAllDecisionParam_Working() {

		// construct data
		// keeped
		DecisionParam newDecisionParam = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);
		// to delete
		DecisionParam newDecisionParam2 = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);
		DecisionParam newDecisionParam3 = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class)
					.deleteAllDecisionParam(Arrays.asList(newDecisionParam2, newDecisionParam3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<DecisionParam> findAll = getDaoManager().getRepository(IDecisionParamRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newDecisionParam, findAll.iterator().next());
	}

	/* ************ deleteDecisionParam ************* */

	@Test
	void test_deleteDecisionParam_Working() {

		// construct data
		// keeped
		DecisionParam newDecisionParam = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);
		// to delete
		DecisionParam newDecisionParam2 = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecisionParam(newDecisionParam2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<DecisionParam> findAll = getDaoManager().getRepository(IDecisionParamRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newDecisionParam, findAll.iterator().next());
	}

	@Test
	void test_deleteDecisionParam_WithValuesSelectConstraintsWorking() {

		// construct data
		// to delete
		DecisionParam newDecisionParam = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);

		// select values
		TestEntityFactory.getNewGenericSelectValue(getDaoManager(), DecisionSelectValue.class, newDecisionParam,
				IDecisionSelectValueRepository.class);
		TestEntityFactory.getNewGenericSelectValue(getDaoManager(), DecisionSelectValue.class, newDecisionParam,
				IDecisionSelectValueRepository.class);

		// constraints
		TestEntityFactory.getNewGenericConstraint(getDaoManager(), DecisionConstraint.class, newDecisionParam,
				IDecisionConstraintRepository.class);
		TestEntityFactory.getNewGenericConstraint(getDaoManager(), DecisionConstraint.class, newDecisionParam,
				IDecisionConstraintRepository.class);

		// values
		TestEntityFactory.getNewDecisionValue(getDaoManager(), null, newDecisionParam, null);
		TestEntityFactory.getNewDecisionValue(getDaoManager(), null, newDecisionParam, null);

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecisionParam(newDecisionParam);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<DecisionParam> findAllParam = getDaoManager().getRepository(IDecisionParamRepository.class).findAll();
		assertNotNull(findAllParam);
		assertTrue(findAllParam.isEmpty());

		List<DecisionValue> findAllValues = getDaoManager().getRepository(IDecisionValueRepository.class).findAll();
		assertNotNull(findAllValues);
		assertTrue(findAllValues.isEmpty());

		List<DecisionSelectValue> findAllSelectValues = getDaoManager()
				.getRepository(IDecisionSelectValueRepository.class).findAll();
		assertNotNull(findAllSelectValues);
		assertTrue(findAllSelectValues.isEmpty());

		List<DecisionConstraint> findAllConstraints = getDaoManager().getRepository(IDecisionConstraintRepository.class)
				.findAll();
		assertNotNull(findAllConstraints);
		assertTrue(findAllConstraints.isEmpty());
	}

	/* ************ deleteAllDecisionValue ************* */

	@Test
	void test_deleteAllDecisionValue_Working() {

		// construct data
		// keeped
		DecisionValue newDecisionValue = TestEntityFactory.getNewDecisionValue(getDaoManager(), null, null, null);
		// to delete
		DecisionValue newDecisionValue2 = TestEntityFactory.getNewDecisionValue(getDaoManager(), null, null, null);
		DecisionValue newDecisionValue3 = TestEntityFactory.getNewDecisionValue(getDaoManager(), null, null, null);

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class)
					.deleteAllDecisionValue(Arrays.asList(newDecisionValue2, newDecisionValue3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<DecisionValue> findAll = getDaoManager().getRepository(IDecisionValueRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newDecisionValue, findAll.iterator().next());
	}

	/* ************ deleteDecisionValue ************* */

	@Test
	void test_deleteDecisionValue_Working() {

		// construct data
		// keeped
		DecisionValue newDecisionValue = TestEntityFactory.getNewDecisionValue(getDaoManager(), null, null, null);
		// to delete
		DecisionValue newDecisionValue2 = TestEntityFactory.getNewDecisionValue(getDaoManager(), null, null, null);

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecisionValue(newDecisionValue2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<DecisionValue> findAll = getDaoManager().getRepository(IDecisionValueRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newDecisionValue, findAll.iterator().next());
	}

	@Test
	void test_deleteDecisionValue_Null() {

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecisionValue(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_DELETE_DECISIONVALUE_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteDecisionValue_IdNull() {

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecisionValue(new DecisionValue());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_DELETE_DECISIONVALUE_IDNULL), e.getMessage());
		}
	}

	/* ************ deleteAllDecisionSelectValue ************* */

	@Test
	void test_deleteAllDecisionSelectValue_Working() {

		// construct data
		DecisionParam parameter = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);
		// keeped
		DecisionSelectValue newDecisionSelectValue = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				DecisionSelectValue.class, parameter, IDecisionSelectValueRepository.class);
		// to delete
		DecisionSelectValue newDecisionSelectValue2 = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				DecisionSelectValue.class, parameter, IDecisionSelectValueRepository.class);
		DecisionSelectValue newDecisionSelectValue3 = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				DecisionSelectValue.class, parameter, IDecisionSelectValueRepository.class);

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class)
					.deleteAllDecisionSelectValue(Arrays.asList(newDecisionSelectValue2, newDecisionSelectValue3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<DecisionSelectValue> findAll = getDaoManager().getRepository(IDecisionSelectValueRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newDecisionSelectValue, findAll.iterator().next());
	}

	/* ************ deleteDecisionSelectValue ************* */

	@Test
	void test_deleteDecisionSelectValue_Working() {

		// construct data
		DecisionParam parameter = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);
		// keeped
		DecisionSelectValue newDecisionSelectValue = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				DecisionSelectValue.class, parameter, IDecisionSelectValueRepository.class);
		// to delete
		DecisionSelectValue newDecisionSelectValue2 = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				DecisionSelectValue.class, parameter, IDecisionSelectValueRepository.class);

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecisionSelectValue(newDecisionSelectValue2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<DecisionSelectValue> findAll = getDaoManager().getRepository(IDecisionSelectValueRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newDecisionSelectValue, findAll.iterator().next());
	}

	@Test
	void test_deleteDecisionSelectValue_Null() {

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecisionSelectValue(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_DELETE_SELECTVALUE_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteDecisionSelectValue_IdNull() {

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecisionSelectValue(new DecisionSelectValue());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_DELETE_SELECTVALUE_IDNULL), e.getMessage());
		}
	}

	/* ************ deleteAllDecisionConstraint ************* */

	@Test
	void test_deleteAllDecisionConstraint_Working() {

		// construct data
		DecisionParam parameter = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);
		// keeped
		DecisionConstraint newDecisionConstraint = TestEntityFactory.getNewGenericConstraint(getDaoManager(),
				DecisionConstraint.class, parameter, IDecisionConstraintRepository.class);
		// to delete
		DecisionConstraint newDecisionConstraint2 = TestEntityFactory.getNewGenericConstraint(getDaoManager(),
				DecisionConstraint.class, parameter, IDecisionConstraintRepository.class);
		DecisionConstraint newDecisionConstraint3 = TestEntityFactory.getNewGenericConstraint(getDaoManager(),
				DecisionConstraint.class, parameter, IDecisionConstraintRepository.class);

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class)
					.deleteAllDecisionConstraint(Arrays.asList(newDecisionConstraint2, newDecisionConstraint3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<DecisionConstraint> findAll = getDaoManager().getRepository(IDecisionConstraintRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newDecisionConstraint, findAll.iterator().next());
	}

	/* ************ deleteDecisionConstraint ************* */

	@Test
	void test_deleteDecisionConstraint_Working() {

		// construct data
		DecisionParam parameter = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);
		// keeped
		DecisionConstraint newDecisionConstraint = TestEntityFactory.getNewGenericConstraint(getDaoManager(),
				DecisionConstraint.class, parameter, IDecisionConstraintRepository.class);
		// to delete
		DecisionConstraint newDecisionConstraint2 = TestEntityFactory.getNewGenericConstraint(getDaoManager(),
				DecisionConstraint.class, parameter, IDecisionConstraintRepository.class);

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecisionConstraint(newDecisionConstraint2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<DecisionConstraint> findAll = getDaoManager().getRepository(IDecisionConstraintRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newDecisionConstraint, findAll.iterator().next());
	}

	@Test
	void test_deleteDecisionConstraint_Null() {

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecisionConstraint(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_DELETE_CONSTRAINT_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteDecisionConstraint_IdNull() {

		// delete
		try {
			getAppManager().getService(IDecisionApplication.class).deleteDecisionConstraint(new DecisionConstraint());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_DECISION_DELETE_CONSTRAINT_IDNULL), e.getMessage());
		}
	}

	/* ************ isDecisionEnabled ************* */

	@Test
	void test_isDecisionEnabled_Enabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);

		boolean DecisionEnabled = getAppManager().getService(IDecisionApplication.class).isDecisionEnabled(newModel);
		assertTrue(DecisionEnabled);
	}

	@Test
	void test_isDecisionEnabled_Disabled() {
		boolean DecisionEnabled = getAppManager().getService(IDecisionApplication.class)
				.isDecisionEnabled(TestEntityFactory.getNewModel(getDaoManager()));
		assertFalse(DecisionEnabled);
	}

	@Test
	void test_sameConfiguration() {
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);

		DecisionSpecification spec = getAppManager().getService(IDecisionApplication.class)
				.loadDecisionConfiguration(newModel);

		assertFalse(getAppManager().getService(IDecisionApplication.class).sameConfiguration(null, spec));
		assertFalse(getAppManager().getService(IDecisionApplication.class).sameConfiguration(spec, null));
		assertTrue(getAppManager().getService(IDecisionApplication.class).sameConfiguration(spec, spec));
		assertTrue(getAppManager().getService(IDecisionApplication.class).sameConfiguration(null, null));
	}

}
