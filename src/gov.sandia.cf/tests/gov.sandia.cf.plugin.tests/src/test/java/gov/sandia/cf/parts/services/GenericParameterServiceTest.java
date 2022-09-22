/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import gov.sandia.cf.constants.configuration.YmlGenericSchema;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericParameterConstraint;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.parts.services.genericparam.IGenericParameterService;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tests.TestGenericParam;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * 
 * JUnit test class for the Generic Parameter Application Controller
 * 
 * @author Didier Verstraete
 *
 */
class GenericParameterServiceTest extends AbstractTestClientService {

	/*******************************
	 * isParameterAvailableForLevel
	 *******************************/

	@Test
	void test_isParameterAvailableForLevel_AllLevels() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel(YmlGenericSchema.LEVEL_COMPARATOR_ALL);

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, 2);

		assertTrue(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_BadOperator() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel(":/3"); //$NON-NLS-1$

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, 2);

		assertFalse(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_Operator_too_many() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel("< 3 >= 2"); //$NON-NLS-1$

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, 2);

		assertFalse(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_EqualsTrue() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel("=2"); //$NON-NLS-1$

		int levelNumber = 2;

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, levelNumber);

		assertTrue(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_EqualsFalse() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel("=2"); //$NON-NLS-1$

		int levelNumber = 1;

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, levelNumber);

		assertFalse(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_OverTrue() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel(">1"); //$NON-NLS-1$

		int levelNumber = 2;

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, levelNumber);

		assertTrue(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_OverFalse() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel(">2"); //$NON-NLS-1$

		int levelNumber = 2;

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, levelNumber);

		assertFalse(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_UnderTrue() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel("<1"); //$NON-NLS-1$

		int levelNumber = 0;

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, levelNumber);

		assertTrue(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_UnderFalse() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel("<2"); //$NON-NLS-1$

		int levelNumber = 2;

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, levelNumber);

		assertFalse(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_OverOrEqualsTrue() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel(">=1"); //$NON-NLS-1$

		int levelNumber = 1;

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, levelNumber);

		assertTrue(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_OverOrEqualsFalse() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel(">=2"); //$NON-NLS-1$

		int levelNumber = 1;

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, levelNumber);

		assertFalse(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_UnderOrEqualsTrue() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel("<=1"); //$NON-NLS-1$

		int levelNumber = 1;

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, levelNumber);

		assertTrue(returned);
	}

	@Test
	void test_isParameterAvailableForLevel_UnderOrEqualsFalse() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setLevel("<=2"); //$NON-NLS-1$

		int levelNumber = 3;

		boolean returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.isParameterAvailableForLevel(parameter, levelNumber);

		assertFalse(returned);
	}

	/*******************************
	 * getParameterNameWithRequiredPrefix
	 *******************************/

	@Test
	void test_getParameterNameWithRequiredPrefix_Required_blank() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setName("Compared"); //$NON-NLS-1$
		parameter.setRequired(RscTools.empty());

		String returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.getParameterNameWithRequiredPrefix(parameter);

		assertEquals(parameter.getName(), returned);
	}

	@Test
	void test_getParameterNameWithRequiredPrefix_Required_optional() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setName("Compared"); //$NON-NLS-1$
		parameter.setRequired(YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE);

		String returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.getParameterNameWithRequiredPrefix(parameter);

		assertEquals(parameter.getName(), returned);
	}

	@Test
	void test_getParameterNameWithRequiredPrefix_Required_desired() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setName("Compared"); //$NON-NLS-1$
		parameter.setRequired(YmlGenericSchema.CONF_GENERIC_DESIRED_VALUE);

		String returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.getParameterNameWithRequiredPrefix(parameter);

		assertEquals(parameter.getName(), returned);
	}

	@Test
	void test_getParameterNameWithRequiredPrefix_Required_false() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setName("Compared"); //$NON-NLS-1$
		parameter.setRequired(YmlGenericSchema.CONF_GENERIC_FALSE_VALUE);

		String returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.getParameterNameWithRequiredPrefix(parameter);

		assertEquals(parameter.getName(), returned);
	}

	@Test
	void test_getParameterNameWithRequiredPrefix_Required_required() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setName("Compared"); //$NON-NLS-1$
		parameter.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);

		String returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.getParameterNameWithRequiredPrefix(parameter);

		assertEquals(RscTools.getString(RscConst.MSG_LBL_REQUIRED, parameter.getName()), returned);
	}

	@Test
	void test_getParameterNameWithRequiredPrefix_Required_true() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setName("Compared"); //$NON-NLS-1$
		parameter.setRequired(YmlGenericSchema.CONF_GENERIC_TRUE_VALUE);

		String returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.getParameterNameWithRequiredPrefix(parameter);

		assertEquals(RscTools.getString(RscConst.MSG_LBL_REQUIRED, parameter.getName()), returned);
	}

	@Test
	void test_getParameterNameWithRequiredPrefix_Required_Conditional() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setName("Compared"); //$NON-NLS-1$
		parameter.setRequired("bla"); //$NON-NLS-1$

		String returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.getParameterNameWithRequiredPrefix(parameter);

		assertEquals(RscTools.getString(RscConst.MSG_LBL_REQUIRED_WITH_CONDITION, parameter.getName()), returned);
	}

	@Test
	void test_getParameterNameWithRequiredPrefix_Name_blank() {

		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setName(RscTools.empty());
		parameter.setRequired(YmlGenericSchema.CONF_GENERIC_TRUE_VALUE);

		String returned = getClientSrvMgr().getService(IGenericParameterService.class)
				.getParameterNameWithRequiredPrefix(parameter);

		assertTrue(returned.isEmpty());
	}

	/*******************************
	 * checkValid
	 *******************************/

	@Test
	void test_checkValid_NoConstraints() {

		GenericValue<?, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		TestGenericParam parameter = TestEntityFactory.getNewTestGenericParameter();
		parameter.setName("Compared"); //$NON-NLS-1$
		GenericValue<TestGenericParam, ?> tableValue = TestEntityFactory.getNewAnonymousGenericValue();
		tableValue.setParameter(parameter);

		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(tableValue));

		assertNull(returned);
	}

	@Test
	void test_checkValid_WithConstraints_NotFloat() {

		// the generic value to test
		GenericParameterConstraint<TestGenericParam> parameter1Constraint = TestEntityFactory
				.getNewAnonymousGenericParameterConstraint();
		parameter1Constraint.setRule("empty Param 2"); //$NON-NLS-1$
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setConstraintList(Arrays.asList(parameter1Constraint));
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue("Not Float"); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.FLOAT.getType());
		GenericValue<TestGenericParam, ?> tableValue = TestEntityFactory.getNewAnonymousGenericValue();
		tableValue.setParameter(parameter2);

		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(tableValue));

		assertNull(returned);

	}

	@Test
	void test_checkValid_Required_Valid() {

		// the generic value to test
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue("2.1"); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value, null);

		assertNull(returned);

	}

	@Test
	void test_checkValid_Required_NotValid() {

		// the generic value to test
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setParameter(parameter1);

		// the generic value to compare with
		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value, null);

		assertNotNull(returned);
		assertTrue(returned.isError());
		assertEquals(RscTools.getString(RscConst.ERR_GENERICPARAM_PARAMETER_REQUIRED, value.getParameter().getName()),
				returned.getMessages().iterator().next());

	}

	@Test
	void test_checkValid_Desired_Valid() {

		// the generic value to test
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setRequired(YmlGenericSchema.CONF_GENERIC_DESIRED_VALUE);
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setParameter(parameter1);

		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value, null);

		assertNotNull(returned);
		assertFalse(returned.isError());

	}

	@Test
	void test_checkValid_NotRequired_Valid() {

		// the generic value to test
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setRequired(YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE);
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setParameter(parameter1);

		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value, null);

		assertNull(returned);

	}

	@Test
	void test_checkValid_Required_LinkedToAnotherFieldNotFulfilled_Valid() {

		// the generic value to test
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setRequired("empty Param 2"); //$NON-NLS-1$
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue("2.1"); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.FLOAT.getType());
		GenericValue<TestGenericParam, ?> tableValue = TestEntityFactory.getNewAnonymousGenericValue();
		tableValue.setParameter(parameter2);
		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(tableValue));

		assertNull(returned);

	}

	@Test
	void test_checkValid_Required_LinkedToAnotherFieldNotFulfilled_NotValid() {

		// the generic value to test
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setRequired("empty Param 2"); //$NON-NLS-1$
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.FLOAT.getType());
		GenericValue<TestGenericParam, ?> tableValue = TestEntityFactory.getNewAnonymousGenericValue();
		tableValue.setParameter(parameter2);
		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(tableValue));

		assertNotNull(returned);
		assertTrue(returned.isError());
		assertEquals(
				RscTools.getString(RscConst.ERR_GENERICPARAM_PARAMETER_REQUIRED_CONDITION_NOTVALID,
						value.getParameter().getName(), value.getParameter().getRequired()),
				returned.getMessages().iterator().next());

	}

	@Test
	void test_checkValid_Required_LinkedToAnotherField_SpecificValue_Fulfilled() {

		// the generic value to test
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.TEXT.getType());
		parameter1.setRequired("Param 2 == 'yes'"); //$NON-NLS-1$
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue("Value 1"); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.TEXT.getType());

		GenericValue<TestGenericParam, ?> value2 = TestEntityFactory.getNewAnonymousGenericValue();
		value2.setValue("yes"); //$NON-NLS-1$
		value2.setParameter(parameter2);

		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(value2));

		assertNull(returned);
	}

	@Test
	void test_checkValid_Required_LinkedToAnotherField_SpecificValue_NotFulfilled() {

		// the generic value to test
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.TEXT.getType());
		parameter1.setRequired("Param 2 == 'yes'"); //$NON-NLS-1$
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue(""); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.TEXT.getType());

		GenericValue<TestGenericParam, ?> value2 = TestEntityFactory.getNewAnonymousGenericValue();
		value2.setValue("yes"); //$NON-NLS-1$
		value2.setParameter(parameter2);

		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(value2));

		assertNotNull(returned);
		assertTrue(returned.isError());
		assertEquals(
				RscTools.getString(RscConst.ERR_GENERICPARAM_PARAMETER_REQUIRED_CONDITION_NOTVALID,
						value.getParameter().getName(), value.getParameter().getRequired()),
				returned.getMessages().iterator().next());
	}

	@Test
	void test_checkValid_Required_LinkedToAnotherField_SpecificValue_NotNeeded() {

		// the generic value to test
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.TEXT.getType());
		parameter1.setRequired("Param 2 == 'yes'"); //$NON-NLS-1$
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue(""); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.TEXT.getType());

		GenericValue<TestGenericParam, ?> value2 = TestEntityFactory.getNewAnonymousGenericValue();
		value2.setValue("no"); //$NON-NLS-1$
		value2.setParameter(parameter2);

		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(value2));

		assertNull(returned);
	}

	@Test
	void test_checkValid_WithMinConstraint_ValidUnder() {

		// the generic value to test
		GenericParameterConstraint<TestGenericParam> parameter1Constraint = TestEntityFactory
				.getNewAnonymousGenericParameterConstraint();
		parameter1Constraint.setRule("Param 1 > Param 2"); //$NON-NLS-1$
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setConstraintList(Arrays.asList(parameter1Constraint));
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue("2.1"); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.FLOAT.getType());
		GenericValue<TestGenericParam, ?> tableValue = TestEntityFactory.getNewAnonymousGenericValue();
		tableValue.setValue("2.0"); //$NON-NLS-1$
		tableValue.setParameter(parameter2);

		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(tableValue));

		assertNull(returned);

	}

	@Test
	void test_checkValid_WithMinConstraint_ValidEquals() {

		// the generic value to test
		GenericParameterConstraint<TestGenericParam> parameter1Constraint = TestEntityFactory
				.getNewAnonymousGenericParameterConstraint();
		parameter1Constraint.setRule("Param 1 >= Param 2"); //$NON-NLS-1$
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setConstraintList(Arrays.asList(parameter1Constraint));
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue("2.1"); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.FLOAT.getType());
		GenericValue<TestGenericParam, ?> tableValue = TestEntityFactory.getNewAnonymousGenericValue();
		tableValue.setValue("2.1"); //$NON-NLS-1$
		tableValue.setParameter(parameter2);
		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(tableValue));

		assertNull(returned);

	}

	@Test
	void test_checkValid_WithMinConstraint_NotValid() {

		// the generic value to test
		GenericParameterConstraint<TestGenericParam> parameter1Constraint = TestEntityFactory
				.getNewAnonymousGenericParameterConstraint();
		parameter1Constraint.setRule("Param 1 > Param 2"); //$NON-NLS-1$
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setConstraintList(Arrays.asList(parameter1Constraint));
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue("2.1"); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.FLOAT.getType());
		GenericValue<TestGenericParam, ?> tableValue = TestEntityFactory.getNewAnonymousGenericValue();
		tableValue.setValue("2.2"); //$NON-NLS-1$
		tableValue.setParameter(parameter2);
		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(tableValue));

		assertNotNull(returned);
		assertTrue(returned.isError());
		assertEquals(RscTools.getString(RscConst.ERR_GENERICPARAM_VALUE_CONSTRAINT_NOTVALID, parameter1.getName(),
				parameter1Constraint.getRule()), returned.getMessages().iterator().next());

	}

	@Test
	void test_checkValid_WithMaxConstraint_ValidOver() {

		// the generic value to test
		GenericParameterConstraint<TestGenericParam> parameter1Constraint = TestEntityFactory
				.getNewAnonymousGenericParameterConstraint();
		parameter1Constraint.setRule("Param 1 < Param 2"); //$NON-NLS-1$
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setConstraintList(Arrays.asList(parameter1Constraint));
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue("2.1"); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.FLOAT.getType());
		GenericValue<TestGenericParam, ?> tableValue = TestEntityFactory.getNewAnonymousGenericValue();
		tableValue.setValue("2.2"); //$NON-NLS-1$
		tableValue.setParameter(parameter2);
		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(tableValue));

		assertNull(returned);

	}

	@Test
	void test_checkValid_WithMaxConstraint_ValidEquals() {

		// the generic value to test
		GenericParameterConstraint<TestGenericParam> parameter1Constraint = TestEntityFactory
				.getNewAnonymousGenericParameterConstraint();
		parameter1Constraint.setRule("Param 1 <= Param 2"); //$NON-NLS-1$
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setConstraintList(Arrays.asList(parameter1Constraint));
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue("2.1"); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.FLOAT.getType());
		GenericValue<TestGenericParam, ?> tableValue = TestEntityFactory.getNewAnonymousGenericValue();
		tableValue.setValue("2.1"); //$NON-NLS-1$
		tableValue.setParameter(parameter2);
		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(tableValue));

		assertNull(returned);

	}

	@Test
	void test_checkValid_WithMaxConstraint_NotValid() {

		// the generic value to test
		GenericParameterConstraint<TestGenericParam> parameter1Constraint = TestEntityFactory
				.getNewAnonymousGenericParameterConstraint();
		parameter1Constraint.setRule("Param 1 < Param 2"); //$NON-NLS-1$
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setConstraintList(Arrays.asList(parameter1Constraint));
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue("2.2"); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.FLOAT.getType());
		GenericValue<TestGenericParam, ?> tableValue = TestEntityFactory.getNewAnonymousGenericValue();
		tableValue.setValue("2.1"); //$NON-NLS-1$
		tableValue.setParameter(parameter2);
		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(tableValue));

		assertNotNull(returned);
		assertTrue(returned.isError());
		assertEquals(RscTools.getString(RscConst.ERR_GENERICPARAM_VALUE_CONSTRAINT_NOTVALID, parameter1.getName(),
				parameter1Constraint.getRule()), returned.getMessages().iterator().next());

	}

	@Test
	void test_checkValid_WithMaxConstraint_Error_bad_EL() {

		// the generic value to test
		GenericParameterConstraint<TestGenericParam> parameter1Constraint = TestEntityFactory
				.getNewAnonymousGenericParameterConstraint();
		parameter1Constraint.setRule("Param 1 <!: Param 2"); //$NON-NLS-1$
		TestGenericParam parameter1 = TestEntityFactory.getNewTestGenericParameter();
		parameter1.setName("Param 1"); //$NON-NLS-1$
		parameter1.setType(FormFieldType.FLOAT.getType());
		parameter1.setConstraintList(Arrays.asList(parameter1Constraint));
		GenericValue<TestGenericParam, ?> value = TestEntityFactory.getNewAnonymousGenericValue();
		value.setValue("2.1"); //$NON-NLS-1$
		value.setParameter(parameter1);

		// the generic value to compare with
		TestGenericParam parameter2 = TestEntityFactory.getNewTestGenericParameter();
		parameter2.setName("Param 2"); //$NON-NLS-1$
		parameter2.setType(FormFieldType.FLOAT.getType());
		GenericValue<TestGenericParam, ?> tableValue = TestEntityFactory.getNewAnonymousGenericValue();
		tableValue.setValue("2.2"); //$NON-NLS-1$
		tableValue.setParameter(parameter2);
		Notification returned = getClientSrvMgr().getService(IGenericParameterService.class).checkValid(value,
				Arrays.asList(tableValue));

		assertNotNull(returned);
		assertTrue(returned.isError());
		String errorMessage = RscTools.getString(RscConst.ERR_GENERICPARAM_VALUE_CONSTRAINT_EXCEPTION,
				parameter1Constraint.getRule());
		int index = errorMessage.indexOf("{1}"); //$NON-NLS-1$
		assertTrue(returned.getMessages().iterator().next().startsWith(errorMessage.substring(0, index - 1)));
	}
}
