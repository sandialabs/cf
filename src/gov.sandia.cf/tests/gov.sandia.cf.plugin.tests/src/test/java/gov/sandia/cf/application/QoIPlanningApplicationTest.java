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
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.qoiplanning.IQoIPlanningApplication;
import gov.sandia.cf.dao.IQoIPlanningConstraintRepository;
import gov.sandia.cf.dao.IQoIPlanningParamRepository;
import gov.sandia.cf.dao.IQoIPlanningSelectValueRepository;
import gov.sandia.cf.dao.IQoIPlanningValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;
import gov.sandia.cf.model.QoIPlanningValue;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Didier Verstraete
 *
 *         JUnit test class for the Global Application Controller
 */
class QoIPlanningApplicationTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(QoIPlanningApplicationTest.class);

	/* ************ loadQoIPlanningConfiguration ************* */

	@Test
	void test_loadQoIPlanningConfiguration_Enabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), newModel);
		TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), newModel);

		QoIPlanningSpecification spec = getAppManager().getService(IQoIPlanningApplication.class)
				.loadQoIPlanningConfiguration(newModel);

		assertNotNull(spec);
		assertNotNull(spec.getParameters());
		assertEquals(2, spec.getParameters().size());

	}

	@Test
	void test_loadQoIPlanningConfiguration_Disabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		QoIPlanningSpecification spec = getAppManager().getService(IQoIPlanningApplication.class)
				.loadQoIPlanningConfiguration(newModel);

		assertNull(spec);

	}

	/* ************ getParameterByModel ************* */

	@Test
	void test_getParameterByModel_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), newModel);
		TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), newModel);

		// test
		List<QoIPlanningParam> parameterByModel = getAppManager().getService(IQoIPlanningApplication.class)
				.getParameterByModel(newModel);
		assertNotNull(parameterByModel);
		assertEquals(2, parameterByModel.size());
	}

	/* ************ createOrUpdateQoIPlanningValue ************* */

	@Test
	void test_createOrUpdateQoIPlanningValue_CreateWorking() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		QoIPlanningParam newQoIPlanningParam = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), newModel);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), newModel);

		QoIPlanningValue value = new QoIPlanningValue();
		value.setDateCreation(new Date());
		value.setParameter(newQoIPlanningParam);
		value.setUserCreation(newUser);
		value.setValue("VALUE"); //$NON-NLS-1$
		value.setQoi(newQoI);

		// test
		try {
			QoIPlanningValue addPlanningValue = getAppManager().getService(IQoIPlanningApplication.class)
					.createOrUpdateQoIPlanningValue(value, newUser);

			assertNotNull(addPlanningValue);
			assertNotNull(addPlanningValue.getId());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_createOrUpdateQoIPlanningValue_UpdateWorking() {

		// construct data
		QoIPlanningParam newQoIPlanningParam = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		QoIPlanningValue newQoIPlanningValue = TestEntityFactory.getNewQoIPlanningValue(getDaoManager(),
				newQoIPlanningParam, null, null);

		newQoIPlanningValue.setValue("MY_NEW_VALUE"); //$NON-NLS-1$

		// test
		try {
			QoIPlanningValue updatedPlanningValue = getAppManager().getService(IQoIPlanningApplication.class)
					.createOrUpdateQoIPlanningValue(newQoIPlanningValue, newUser);

			assertNotNull(updatedPlanningValue);
			assertEquals(newQoIPlanningValue.getId(), updatedPlanningValue.getId());
			assertEquals("MY_NEW_VALUE", updatedPlanningValue.getValue()); //$NON-NLS-1$

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_createOrUpdateQoIPlanningValue_PlanningValueNull() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			QoIPlanningValue createOrUpdateQoIPlanningValue = getAppManager().getService(IQoIPlanningApplication.class)
					.createOrUpdateQoIPlanningValue(null, newUser);
			assertNull(createOrUpdateQoIPlanningValue);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	/* ************ deleteAllQoIPlanningParam ************* */

	@Test
	void test_deleteAllQoIPlanningParam_Working() {

		// construct data
		// keeped
		QoIPlanningParam newQoIPlanningParam = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);
		// to delete
		QoIPlanningParam newQoIPlanningParam2 = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);
		QoIPlanningParam newQoIPlanningParam3 = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);

		// delete
		try {
			getAppManager().getService(IQoIPlanningApplication.class)
					.deleteAllQoIPlanningParam(Arrays.asList(newQoIPlanningParam2, newQoIPlanningParam3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<QoIPlanningParam> findAll = getDaoManager().getRepository(IQoIPlanningParamRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newQoIPlanningParam, findAll.iterator().next());
	}

	/* ************ deleteQoIPlanningParam ************* */

	@Test
	void test_deleteQoIPlanningParam_Working() {

		// construct data
		// keeped
		QoIPlanningParam newQoIPlanningParam = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);
		// to delete
		QoIPlanningParam newQoIPlanningParam2 = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);

		// delete
		try {
			getAppManager().getService(IQoIPlanningApplication.class).deleteQoIPlanningParam(newQoIPlanningParam2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<QoIPlanningParam> findAll = getDaoManager().getRepository(IQoIPlanningParamRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newQoIPlanningParam, findAll.iterator().next());
	}

	@Test
	void test_deleteQoIPlanningParam_WithValuesSelectConstraintsWorking() {

		// construct data
		// to delete
		QoIPlanningParam newQoIPlanningParam = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);

		// select values
		TestEntityFactory.getNewGenericSelectValue(getDaoManager(), QoIPlanningSelectValue.class, newQoIPlanningParam,
				IQoIPlanningSelectValueRepository.class);
		TestEntityFactory.getNewGenericSelectValue(getDaoManager(), QoIPlanningSelectValue.class, newQoIPlanningParam,
				IQoIPlanningSelectValueRepository.class);

		// constraints
		TestEntityFactory.getNewGenericConstraint(getDaoManager(), QoIPlanningConstraint.class, newQoIPlanningParam,
				IQoIPlanningConstraintRepository.class);
		TestEntityFactory.getNewGenericConstraint(getDaoManager(), QoIPlanningConstraint.class, newQoIPlanningParam,
				IQoIPlanningConstraintRepository.class);

		// values
		TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), newQoIPlanningParam, null, null);
		TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), newQoIPlanningParam, null, null);

		// delete
		try {
			getAppManager().getService(IQoIPlanningApplication.class).deleteQoIPlanningParam(newQoIPlanningParam);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<QoIPlanningParam> findAllParam = getDaoManager().getRepository(IQoIPlanningParamRepository.class)
				.findAll();
		assertNotNull(findAllParam);
		assertTrue(findAllParam.isEmpty());

		List<QoIPlanningValue> findAllValues = getDaoManager().getRepository(IQoIPlanningValueRepository.class)
				.findAll();
		assertNotNull(findAllValues);
		assertTrue(findAllValues.isEmpty());

		List<QoIPlanningSelectValue> findAllSelectValues = getDaoManager()
				.getRepository(IQoIPlanningSelectValueRepository.class).findAll();
		assertNotNull(findAllSelectValues);
		assertTrue(findAllSelectValues.isEmpty());

		List<QoIPlanningConstraint> findAllConstraints = getDaoManager()
				.getRepository(IQoIPlanningConstraintRepository.class).findAll();
		assertNotNull(findAllConstraints);
		assertTrue(findAllConstraints.isEmpty());
	}

	@Test
	void test_deleteQoIPlanningParam_Null() {
		try {
			getAppManager().getService(IQoIPlanningApplication.class).deleteQoIPlanningParam(null);
			fail("This should fail."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGPARAM_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteQoIPlanningParam_IdNull() {
		try {
			getAppManager().getService(IQoIPlanningApplication.class).deleteQoIPlanningParam(new QoIPlanningParam());
			fail("This should fail."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGPARAM_IDNULL), e.getMessage());
		}
	}

	/* ************ deleteAllQoIPlanningValue ************* */

	@Test
	void test_deleteAllQoIPlanningValue_Working() {

		// construct data
		// keeped
		QoIPlanningValue newQoIPlanningValue = TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), null, null,
				null);
		// to delete
		QoIPlanningValue newQoIPlanningValue2 = TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), null, null,
				null);
		QoIPlanningValue newQoIPlanningValue3 = TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), null, null,
				null);

		// delete
		try {
			getAppManager().getService(IQoIPlanningApplication.class)
					.deleteAllQoIPlanningValue(Arrays.asList(newQoIPlanningValue2, newQoIPlanningValue3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<QoIPlanningValue> findAll = getDaoManager().getRepository(IQoIPlanningValueRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newQoIPlanningValue, findAll.iterator().next());
	}

	/* ************ deleteQoIPlanningValue ************* */

	@Test
	void test_deleteQoIPlanningValue_Working() {

		// construct data
		// keeped
		QoIPlanningValue newQoIPlanningValue = TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), null, null,
				null);
		// to delete
		QoIPlanningValue newQoIPlanningValue2 = TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), null, null,
				null);

		// delete
		try {
			getAppManager().getService(IQoIPlanningApplication.class).deleteQoIPlanningValue(newQoIPlanningValue2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<QoIPlanningValue> findAll = getDaoManager().getRepository(IQoIPlanningValueRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newQoIPlanningValue, findAll.iterator().next());
	}

	@Test
	void test_deleteQoIPlanningValue_Null() {
		try {
			getAppManager().getService(IQoIPlanningApplication.class).deleteQoIPlanningValue(null);
			fail("This should fail."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGVALUE_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteQoIPlanningValue_IdNull() {
		try {
			getAppManager().getService(IQoIPlanningApplication.class).deleteQoIPlanningValue(new QoIPlanningValue());
			fail("This should fail."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGVALUE_IDNULL), e.getMessage());
		}
	}

	/* ************ deleteAllQoIPlanningSelectValue ************* */

	@Test
	void test_deleteAllQoIPlanningSelectValue_Working() {

		// construct data
		QoIPlanningParam parameter = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);
		// keeped
		QoIPlanningSelectValue newQoIPlanningSelectValue = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				QoIPlanningSelectValue.class, parameter, IQoIPlanningSelectValueRepository.class);
		// to delete
		QoIPlanningSelectValue newQoIPlanningSelectValue2 = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				QoIPlanningSelectValue.class, parameter, IQoIPlanningSelectValueRepository.class);
		QoIPlanningSelectValue newQoIPlanningSelectValue3 = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				QoIPlanningSelectValue.class, parameter, IQoIPlanningSelectValueRepository.class);

		// delete
		try {
			getAppManager().getService(IQoIPlanningApplication.class).deleteAllQoIPlanningSelectValue(
					Arrays.asList(newQoIPlanningSelectValue2, newQoIPlanningSelectValue3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<QoIPlanningSelectValue> findAll = getDaoManager().getRepository(IQoIPlanningSelectValueRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newQoIPlanningSelectValue, findAll.iterator().next());
	}

	/* ************ deleteQoIPlanningSelectValue ************* */

	@Test
	void test_deleteQoIPlanningSelectValue_Working() {

		// construct data
		QoIPlanningParam parameter = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);
		// keeped
		QoIPlanningSelectValue newQoIPlanningSelectValue = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				QoIPlanningSelectValue.class, parameter, IQoIPlanningSelectValueRepository.class);
		// to delete
		QoIPlanningSelectValue newQoIPlanningSelectValue2 = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				QoIPlanningSelectValue.class, parameter, IQoIPlanningSelectValueRepository.class);

		// delete
		try {
			getAppManager().getService(IQoIPlanningApplication.class)
					.deleteQoIPlanningSelectValue(newQoIPlanningSelectValue2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<QoIPlanningSelectValue> findAll = getDaoManager().getRepository(IQoIPlanningSelectValueRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newQoIPlanningSelectValue, findAll.iterator().next());
	}

	@Test
	void test_deleteQoIPlanningSelectValue_Null() {
		try {
			getAppManager().getService(IQoIPlanningApplication.class).deleteQoIPlanningSelectValue(null);
			fail("This should fail."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGSELECTVALUE_NULL),
					e.getMessage());
		}
	}

	@Test
	void test_deleteQoIPlanningSelectValue_IdNull() {
		try {
			getAppManager().getService(IQoIPlanningApplication.class)
					.deleteQoIPlanningSelectValue(new QoIPlanningSelectValue());
			fail("This should fail."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGSELECTVALUE_IDNULL),
					e.getMessage());
		}
	}

	/* ************ deleteAllQoIPlanningConstraint ************* */

	@Test
	void test_deleteAllQoIPlanningConstraint_Working() {

		// construct data
		QoIPlanningParam parameter = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);
		// keeped
		QoIPlanningConstraint newQoIPlanningConstraint = TestEntityFactory.getNewGenericConstraint(getDaoManager(),
				QoIPlanningConstraint.class, parameter, IQoIPlanningConstraintRepository.class);
		// to delete
		QoIPlanningConstraint newQoIPlanningConstraint2 = TestEntityFactory.getNewGenericConstraint(getDaoManager(),
				QoIPlanningConstraint.class, parameter, IQoIPlanningConstraintRepository.class);
		QoIPlanningConstraint newQoIPlanningConstraint3 = TestEntityFactory.getNewGenericConstraint(getDaoManager(),
				QoIPlanningConstraint.class, parameter, IQoIPlanningConstraintRepository.class);

		// delete
		try {
			getAppManager().getService(IQoIPlanningApplication.class).deleteAllQoIPlanningConstraint(
					Arrays.asList(newQoIPlanningConstraint2, newQoIPlanningConstraint3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<QoIPlanningConstraint> findAll = getDaoManager().getRepository(IQoIPlanningConstraintRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newQoIPlanningConstraint, findAll.iterator().next());
	}

	/* ************ deleteQoIPlanningConstraint ************* */

	@Test
	void test_deleteQoIPlanningConstraint_Working() {

		// construct data
		QoIPlanningParam parameter = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);
		// keeped
		QoIPlanningConstraint newQoIPlanningConstraint = TestEntityFactory.getNewGenericConstraint(getDaoManager(),
				QoIPlanningConstraint.class, parameter, IQoIPlanningConstraintRepository.class);
		// to delete
		QoIPlanningConstraint newQoIPlanningConstraint2 = TestEntityFactory.getNewGenericConstraint(getDaoManager(),
				QoIPlanningConstraint.class, parameter, IQoIPlanningConstraintRepository.class);

		// delete
		try {
			getAppManager().getService(IQoIPlanningApplication.class)
					.deleteQoIPlanningConstraint(newQoIPlanningConstraint2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<QoIPlanningConstraint> findAll = getDaoManager().getRepository(IQoIPlanningConstraintRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newQoIPlanningConstraint, findAll.iterator().next());
	}

	@Test
	void test_deleteQoIPlanningConstraint_Null() {
		try {
			getAppManager().getService(IQoIPlanningApplication.class).deleteQoIPlanningConstraint(null);
			fail("This should fail."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGCONSTRAINT_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteQoIPlanningConstraint_IdNull() {
		try {
			getAppManager().getService(IQoIPlanningApplication.class)
					.deleteQoIPlanningConstraint(new QoIPlanningConstraint());
			fail("This should fail."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGCONSTRAINT_IDNULL),
					e.getMessage());
		}
	}

	/* ************ isQoIPlanningEnabled ************* */

	@Test
	void test_isQoIPlanningEnabled_Enabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), newModel);

		boolean qoIPlanningEnabled = getAppManager().getService(IQoIPlanningApplication.class)
				.isQoIPlanningEnabled(newModel);
		assertTrue(qoIPlanningEnabled);
	}

	@Test
	void test_isQoIPlanningEnabled_Disabled() {
		boolean qoIPlanningEnabled = getAppManager().getService(IQoIPlanningApplication.class)
				.isQoIPlanningEnabled(TestEntityFactory.getNewModel(getDaoManager()));
		assertFalse(qoIPlanningEnabled);
	}

	/* ************ sameConfiguration ************* */

	@Test
	void test_sameConfiguration_Spec1Null() {
		assertFalse(getAppManager().getService(IQoIPlanningApplication.class).sameConfiguration(null,
				new QoIPlanningSpecification()));
	}

	@Test
	void test_sameConfiguration_Spec2Null() {
		assertFalse(getAppManager().getService(IQoIPlanningApplication.class)
				.sameConfiguration(new QoIPlanningSpecification(), null));
	}

	@Test
	void test_sameConfiguration_BothNull() {
		assertTrue(getAppManager().getService(IQoIPlanningApplication.class).sameConfiguration(null, null));
	}

	@Test
	void test_sameConfiguration_BothEmpty() {
		assertTrue(getAppManager().getService(IQoIPlanningApplication.class)
				.sameConfiguration(new QoIPlanningSpecification(), new QoIPlanningSpecification()));
	}

	@Test
	void test_sameConfiguration_Same() {
		QoIPlanningParam qoiPlanningParam = Mockito.mock(QoIPlanningParam.class);
		Mockito.when(qoiPlanningParam.sameAs(Mockito.any())).thenReturn(true);
		QoIPlanningSpecification spec = Mockito.mock(QoIPlanningSpecification.class);
		Mockito.when(spec.getParameters()).thenReturn(Arrays.asList(qoiPlanningParam, qoiPlanningParam));
		assertTrue(getAppManager().getService(IQoIPlanningApplication.class).sameConfiguration(spec, spec));
	}

	@Test
	void test_sameConfiguration_DifferentSize() {
		QoIPlanningParam qoiPlanningParam = Mockito.mock(QoIPlanningParam.class);
		Mockito.when(qoiPlanningParam.sameAs(Mockito.any())).thenReturn(false);
		QoIPlanningSpecification spec = Mockito.mock(QoIPlanningSpecification.class);
		Mockito.when(spec.getParameters()).thenReturn(Arrays.asList(qoiPlanningParam, qoiPlanningParam));
		QoIPlanningSpecification spec2 = Mockito.mock(QoIPlanningSpecification.class);
		Mockito.when(spec2.getParameters()).thenReturn(Arrays.asList(qoiPlanningParam));

		assertFalse(getAppManager().getService(IQoIPlanningApplication.class).sameConfiguration(spec, spec2));
	}

	@Test
	void test_sameConfiguration_DifferentQoIPlanning() {
		QoIPlanningParam qoiPlanningParam = Mockito.mock(QoIPlanningParam.class);
		Mockito.when(qoiPlanningParam.sameAs(Mockito.any())).thenReturn(false);
		QoIPlanningSpecification spec = Mockito.mock(QoIPlanningSpecification.class);
		Mockito.when(spec.getParameters()).thenReturn(Arrays.asList(qoiPlanningParam, qoiPlanningParam));
		QoIPlanningSpecification spec2 = Mockito.mock(QoIPlanningSpecification.class);
		Mockito.when(spec2.getParameters()).thenReturn(Arrays.asList(qoiPlanningParam, qoiPlanningParam));

		assertFalse(getAppManager().getService(IQoIPlanningApplication.class).sameConfiguration(spec, spec));
	}
}
