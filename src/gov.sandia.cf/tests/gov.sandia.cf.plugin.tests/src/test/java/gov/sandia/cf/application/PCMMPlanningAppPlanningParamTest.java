/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.YmlGenericSchema;
import gov.sandia.cf.dao.IPCMMPlanningParamRepository;
import gov.sandia.cf.dao.IPCMMPlanningSelectValueRepository;
import gov.sandia.cf.dao.IPCMMPlanningTableItemRepository;
import gov.sandia.cf.dao.IPCMMPlanningTableValueRepository;
import gov.sandia.cf.dao.IPCMMPlanningValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningTableValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * JUnit test class for the PCMM Application Planning Controller
 * 
 * @author Didier Verstraete.
 *
 */
@RunWith(JUnitPlatform.class)
class PCMMPlanningAppPlanningParamTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningAppPlanningParamTest.class);

	/* ************ addPlanningParameter ************* */

	@Test
	void testaddPlanningParameter_GenericParameter_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMPlanningParam planningParam = new PCMMPlanningParam();
		planningParam.setModel(newModel);
		planningParam.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		planningParam.setName("PARAM"); //$NON-NLS-1$
		planningParam.setType("TYPE PARAM"); //$NON-NLS-1$

		// test
		try {
			getPCMMPlanningApp().addPlanningParameter(planningParam);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testaddPlanningParameter_PCMMPlanningParam_Working() throws CredibilityException {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMPlanningParam planningParam = new PCMMPlanningParam();
		planningParam.setModel(newModel);
		planningParam.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		planningParam.setName("PARAM"); //$NON-NLS-1$
		planningParam.setType("TYPE PARAM"); //$NON-NLS-1$

		// test
		PCMMPlanningParam addPlanningParameter = getPCMMPlanningApp().addPlanningParameter(planningParam);

		assertNotNull(addPlanningParameter);
		assertNotNull(addPlanningParameter.getParameterValueList());
		assertEquals(0, addPlanningParameter.getParameterValueList().size());
	}

	@Test
	void testaddPlanningParameter_PCMMPlanningParamWithSelectValues_Working() throws CredibilityException {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMPlanningParam planningParam = new PCMMPlanningParam();
		planningParam.setModel(newModel);
		planningParam.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		planningParam.setName("PARAM"); //$NON-NLS-1$
		planningParam.setType("TYPE PARAM"); //$NON-NLS-1$

		// with select values
		List<GenericParameterSelectValue<PCMMPlanningParam>> selectValues = new ArrayList<>();
		PCMMPlanningSelectValue select1 = new PCMMPlanningSelectValue();
		select1.setName("Select 1"); //$NON-NLS-1$
		selectValues.add(select1);
		PCMMPlanningSelectValue select2 = new PCMMPlanningSelectValue();
		select1.setName("Select 2"); //$NON-NLS-1$
		selectValues.add(select2);
		planningParam.setParameterValueList(selectValues);

		// test
		PCMMPlanningParam addPlanningParameter = getPCMMPlanningApp().addPlanningParameter(planningParam);

		assertNotNull(addPlanningParameter);
		assertNotNull(addPlanningParameter.getParameterValueList());
		assertEquals(2, addPlanningParameter.getParameterValueList().size());
	}

	@Test
	void testaddPlanningParameter_PCMMPlanningParamWithChildren_Working() throws CredibilityException {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMPlanningParam planningParam = new PCMMPlanningParam();
		planningParam.setModel(newModel);
		planningParam.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		planningParam.setName("PARAM"); //$NON-NLS-1$
		planningParam.setType("TYPE PARAM"); //$NON-NLS-1$

		PCMMPlanningParam child1 = new PCMMPlanningParam();
		child1.setModel(newModel);
		child1.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		child1.setName("PARAM"); //$NON-NLS-1$
		child1.setType("TYPE PARAM"); //$NON-NLS-1$

		PCMMPlanningParam child2 = new PCMMPlanningParam();
		child2.setModel(newModel);
		child2.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		child2.setName("PARAM"); //$NON-NLS-1$
		child2.setType("TYPE PARAM"); //$NON-NLS-1$

		planningParam.setChildren(Arrays.asList(child1, child2));

		// with select values
		List<GenericParameterSelectValue<PCMMPlanningParam>> selectValues = new ArrayList<>();
		PCMMPlanningSelectValue select1 = new PCMMPlanningSelectValue();
		select1.setName("Select 1"); //$NON-NLS-1$
		selectValues.add(select1);
		PCMMPlanningSelectValue select2 = new PCMMPlanningSelectValue();
		select1.setName("Select 2"); //$NON-NLS-1$
		selectValues.add(select2);
		planningParam.setParameterValueList(selectValues);

		// test
		PCMMPlanningParam addPlanningParameter = getPCMMPlanningApp().addPlanningParameter(planningParam);

		assertNotNull(addPlanningParameter);
		assertNotNull(addPlanningParameter.getParameterValueList());
		assertEquals(2, addPlanningParameter.getParameterValueList().size());

		assertNotNull(addPlanningParameter.getChildren());
		assertEquals(2, addPlanningParameter.getChildren().size());
		assertNotNull(addPlanningParameter.getChildren().get(0).getId());
		assertNotNull(addPlanningParameter.getChildren().get(1).getId());
	}

	@Test
	void testaddPlanningParameter_ParamNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().addPlanningParameter(null);
			fail("It should fail if the parameter is null"); //$NON-NLS-1$
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDPARAM_NULL), e.getMessage());
	}

	/* ************ addAllPCMMPlanningParam ************* */

	@Test
	void testaddAllPlanningParameter_PCMMPlanningParamWithChildren_Working() throws CredibilityException {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMPlanningParam planningParam = new PCMMPlanningParam();
		planningParam.setModel(newModel);
		planningParam.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		planningParam.setName("PARAM"); //$NON-NLS-1$
		planningParam.setType("TYPE PARAM"); //$NON-NLS-1$

		PCMMPlanningParam child1 = new PCMMPlanningParam();
		child1.setModel(newModel);
		child1.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		child1.setName("PARAM"); //$NON-NLS-1$
		child1.setType("TYPE PARAM"); //$NON-NLS-1$

		PCMMPlanningParam child2 = new PCMMPlanningParam();
		child2.setModel(newModel);
		child2.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		child2.setName("PARAM"); //$NON-NLS-1$
		child2.setType("TYPE PARAM"); //$NON-NLS-1$

		planningParam.setChildren(Arrays.asList(child1, child2));

		// with select values
		List<GenericParameterSelectValue<PCMMPlanningParam>> selectValues = new ArrayList<>();
		PCMMPlanningSelectValue select1 = new PCMMPlanningSelectValue();
		select1.setName("Select 1"); //$NON-NLS-1$
		selectValues.add(select1);
		PCMMPlanningSelectValue select2 = new PCMMPlanningSelectValue();
		select1.setName("Select 2"); //$NON-NLS-1$
		selectValues.add(select2);
		planningParam.setParameterValueList(selectValues);

		PCMMPlanningParam planningParam2 = new PCMMPlanningParam();
		planningParam2.setModel(newModel);
		planningParam2.setRequired(YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE);
		planningParam2.setName("PARAM2"); //$NON-NLS-1$
		planningParam2.setType("TYPE PARAM"); //$NON-NLS-1$

		// test
		getPCMMPlanningApp().addAllPCMMPlanningParam(newModel, Arrays.asList(planningParam, planningParam2));

		List<PCMMPlanningParam> findAllParam = getDaoManager().getRepository(IPCMMPlanningParamRepository.class)
				.findAll();
		assertNotNull(findAllParam);
		assertEquals(4, findAllParam.size());

		List<PCMMPlanningSelectValue> findAllSelect = getDaoManager()
				.getRepository(IPCMMPlanningSelectValueRepository.class).findAll();
		assertNotNull(findAllSelect);
		assertEquals(2, findAllSelect.size());
	}

	@Test
	void testaddAllPlanningParameter_ParamNull() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			getPCMMPlanningApp().addAllPCMMPlanningParam(newModel, null);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testaddAllPlanningParameter_ModelNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().addAllPCMMPlanningParam(null, null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_IMPORTCONF_MODELNULL), e.getMessage());
	}

	/* ************ addAllPCMMPlanningSelectValue ************* */

	@Test
	void test_addAllPCMMPlanningSelectValue_ParameterNull() {

		// construct data
		List<PCMMPlanningSelectValue> selectValues = new ArrayList<>();
		PCMMPlanningSelectValue select1 = new PCMMPlanningSelectValue();
		select1.setName("Select 1"); //$NON-NLS-1$
		selectValues.add(select1);
		PCMMPlanningSelectValue select2 = new PCMMPlanningSelectValue();
		select1.setName("Select 2"); //$NON-NLS-1$
		selectValues.add(select2);

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().addAllPCMMPlanningSelectValue(null, selectValues);
		});
		assertEquals(RscTools.getString(RscConst.EX_GLB_GENPARAMVALUELIST_IMPORT_PARAMETERNULL), e.getMessage());

	}

	@Test
	void test_addAllPCMMPlanningSelectValue_ValuesNull() {

		// construct data
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);

		// test
		try {
			getPCMMPlanningApp().addAllPCMMPlanningSelectValue(newPCMMPlanningParam, null);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	/* ************ addPCMMPlanningSelectValue ************* */

	@Test
	void testaddPCMMPlanningSelectValue_ValueNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().addPCMMPlanningSelectValue(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_GLB_GENPARAMVALUELIST_ADD_NULL), e.getMessage());
	}

	/* ************ updatePlanningParameter ************* */

	@Test
	void testupdatePlanningParameter_Working() throws CredibilityException {

		// construct data
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		newPCMMPlanningParam.setName("MY_NEW_NAME"); //$NON-NLS-1$

		// test
		PCMMPlanningParam updatePlanningParameter = getPCMMPlanningApp().updatePlanningParameter(newPCMMPlanningParam);

		assertNotNull(updatePlanningParameter);
		assertEquals(newPCMMPlanningParam.getId(), updatePlanningParameter.getId());
		assertEquals("MY_NEW_NAME", updatePlanningParameter.getName()); //$NON-NLS-1$
	}

	@Test
	void testupdatePlanningParameter_ParameterNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().updatePlanningParameter(null);
			fail("It should fail if the parameter is null"); //$NON-NLS-1$
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEPARAM_NULL), e.getMessage());
	}

	@Test
	void testupdatePlanningParameter_IdNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().updatePlanningParameter(new PCMMPlanningParam());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEPARAM_IDNULL), e.getMessage());
	}

	/* ************ getPlanningFieldsBy ************* */

	@Test
	void testgetPlanningFieldsBy_Working() {

		// construct data
		TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);

		// test
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericParameter.Filter.NAME, NullParameter.NOT_NULL);
		List<PCMMPlanningParam> planningFieldsBy = getPCMMPlanningApp().getPlanningFieldsBy(filters);
		assertNotNull(planningFieldsBy);
		assertEquals(2, planningFieldsBy.size());
	}

	/* ************ addPlanningValue ************* */

	@Test
	void testaddPlanningValue_Working() throws CredibilityException {

		// construct data
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		PCMMPlanningValue value = new PCMMPlanningValue();
		value.setDateCreation(new Date());
		value.setSubelement(newPCMMSubelement);
		value.setParameter(newPCMMPlanningParam);
		value.setUserCreation(newUser);
		value.setValue("VALUE"); //$NON-NLS-1$

		// test
		PCMMPlanningValue addPlanningValue = getPCMMPlanningApp().addPlanningValue(value);

		assertNotNull(addPlanningValue);
		assertNotNull(addPlanningValue.getId());
	}

	@Test
	void testaddPlanningValue_PlanningValueNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().addPlanningValue(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDVALUE_NULL), e.getMessage());
	}

	/* ************ updatePlanningValue ************* */

	@Test
	void testupdatePlanningValue_Working() throws CredibilityException {

		// construct data
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		PCMMPlanningValue newPCMMPlanningValue = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(),
				newPCMMPlanningParam, newPCMMSubelement, null, null);

		newPCMMPlanningValue.setValue("MY_NEW_VALUE"); //$NON-NLS-1$

		// test
		PCMMPlanningValue updatedPlanningValue = getPCMMPlanningApp().updatePlanningValue(newPCMMPlanningValue,
				newUser);

		assertNotNull(updatedPlanningValue);
		assertNotNull(updatedPlanningValue.getId());
		assertEquals("MY_NEW_VALUE", updatedPlanningValue.getValue()); //$NON-NLS-1$
	}

	@Test
	void testupdatePlanningValue_PlanningValueNull() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().updatePlanningValue(null, newUser);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEVALUE_NULL), e.getMessage());
	}

	@Test
	void testupdatePlanningValue_IdNull() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().updatePlanningValue(new PCMMPlanningValue(), newUser);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEVALUE_IDNULL), e.getMessage());
	}

	/* ************ getPlanningValueBy ************* */

	@Test
	void testgetPlanningValueBy_Working() {

		// construct data
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), null, null, null, null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), null, null, null, null);

		// test
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericValue.Filter.VALUE, NullParameter.NOT_NULL);
		List<PCMMPlanningValue> planningFieldsBy = getPCMMPlanningApp().getPlanningValueBy(filters);
		assertNotNull(planningFieldsBy);
		assertEquals(2, planningFieldsBy.size());
	}

	/* ************ getPlanningValueByElement ************* */

	@Test
	void testgetPlanningValueByElement_PCMMModeDefault_NotTagged() {

		// construct data
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null, null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null, null);
		// the following one is tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				TestEntityFactory.getNewTag(getDaoManager(), null));

		// test
		Tag tag = null;
		List<PCMMPlanningValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningValueByElement(newPCMMSubelement.getElement(), PCMMMode.DEFAULT, tag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	@Test
	void testgetPlanningValueByElement_PCMMModeDefault_Tagged() {

		// construct data
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				newTag);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				newTag);
		// the following one is not tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null, null);

		// test
		List<PCMMPlanningValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningValueByElement(newPCMMSubelement.getElement(), PCMMMode.DEFAULT, newTag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	@Test
	void testgetPlanningValueByElement_PCMMModeSimplified_NotTagged() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null, null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null, null);
		// the following one is tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null,
				TestEntityFactory.getNewTag(getDaoManager(), null));

		// test
		Tag tag = null;
		List<PCMMPlanningValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningValueByElement(newPCMMElement, PCMMMode.SIMPLIFIED, tag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	@Test
	void testgetPlanningValueByElement_PCMMModeSimplified_Tagged() {

		// construct data
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null, newTag);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null, newTag);
		// the following one is not tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null, null);

		// test
		List<PCMMPlanningValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningValueByElement(newPCMMElement, PCMMMode.SIMPLIFIED, newTag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	/* ************ getPlanningValueByElement ************* */

	@Test
	void testgetPlanningValueByElement_PCMMModeDefault_ListTag() {

		// construct data
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		Tag tag1 = TestEntityFactory.getNewTag(getDaoManager(), null);
		Tag tag2 = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		PCMMPlanningValue value1 = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam,
				newPCMMSubelement, null, null);
		PCMMPlanningValue value2 = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam,
				newPCMMSubelement, null, null);
		PCMMPlanningValue value3 = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam,
				newPCMMSubelement, null, tag1);
		// the following one is tagged so it should not be retrieved
		PCMMPlanningValue value4 = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam,
				newPCMMSubelement, null, tag2);

		// test
		List<PCMMPlanningValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningValueByElement(newPCMMSubelement.getElement(), PCMMMode.DEFAULT, Arrays.asList(null, tag1));

		assertNotNull(planningQuestionsByElement);
		assertEquals(3, planningQuestionsByElement.size());
		assertTrue(planningQuestionsByElement.contains(value1));
		assertTrue(planningQuestionsByElement.contains(value2));
		assertTrue(planningQuestionsByElement.contains(value3));
		assertFalse(planningQuestionsByElement.contains(value4));
	}

	@Test
	void testgetPlanningValueByElement_PCMMModeSimplified_ListTag() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		Tag tag1 = TestEntityFactory.getNewTag(getDaoManager(), null);
		Tag tag2 = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		PCMMPlanningValue value1 = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam,
				newPCMMElement, null, null);
		PCMMPlanningValue value2 = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam,
				newPCMMElement, null, null);
		PCMMPlanningValue value3 = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam,
				newPCMMElement, null, tag1);
		// the following one should not be retrieved
		PCMMPlanningValue value4 = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), newPCMMPlanningParam,
				newPCMMElement, null, tag2);

		// test
		List<PCMMPlanningValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningValueByElement(newPCMMElement, PCMMMode.SIMPLIFIED, Arrays.asList(null, tag1));

		assertNotNull(planningQuestionsByElement);
		assertEquals(3, planningQuestionsByElement.size());
		assertTrue(planningQuestionsByElement.contains(value1));
		assertTrue(planningQuestionsByElement.contains(value2));
		assertTrue(planningQuestionsByElement.contains(value3));
		assertFalse(planningQuestionsByElement.contains(value4));
	}

	/* ************ deleteAllPlanningParameter ************* */

	@Test
	void test_deleteAllPlanningParameter_Working() throws CredibilityException {

		// construct data
		PCMMPlanningParam param1 = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), param1, null, null, null);

		PCMMPlanningParam param2 = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), param2, null, null, null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), param2, null, null, null);

		PCMMPlanningParam param3 = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), param3, null, null, null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), param3, null, null, null);

		// test
		getPCMMPlanningApp().deleteAllPlanningParameter(Arrays.asList(param1, param2));

		List<PCMMPlanningParam> allParams = getDaoManager().getRepository(IPCMMPlanningParamRepository.class).findAll();
		assertNotNull(allParams);
		assertEquals(1, allParams.size());
		assertEquals(param3, allParams.iterator().next());

		List<PCMMPlanningValue> allValues = getDaoManager().getRepository(IPCMMPlanningValueRepository.class).findAll();
		assertNotNull(allParams);
		assertEquals(2, allValues.size());
		assertEquals(param3, allValues.iterator().next().getParameter());
	}

	@Test
	void test_deleteAllPlanningParameter_Null() {

		// test
		try {
			getPCMMPlanningApp().deleteAllPlanningParameter(null);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	/* ************ deletePlanningParameter ************* */

	@Test
	void test_deletePlanningParameter_Working() throws CredibilityException {

		// construct data
		PCMMPlanningParam param1 = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), param1, null, null, null);

		TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), param1.getModel(), param1);

		PCMMPlanningParam param2 = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), param2, null, null, null);
		TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), param2, null, null, null);

		PCMMPlanningTableItem item = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), param1, null, null,
				null);

		TestEntityFactory.getNewPCMMPlanningTableValue(getDaoManager(), param1, item, null);

		TestEntityFactory.getNewPCMMPlanningSelectValue(getDaoManager(), param1);

		// test
		getPCMMPlanningApp().deletePlanningParameter(param1);

		List<PCMMPlanningParam> allParams = getDaoManager().getRepository(IPCMMPlanningParamRepository.class).findAll();
		assertNotNull(allParams);
		assertEquals(1, allParams.size());
		assertEquals(param2, allParams.iterator().next());

		List<PCMMPlanningValue> allValues = getDaoManager().getRepository(IPCMMPlanningValueRepository.class).findAll();
		assertNotNull(allParams);
		assertEquals(2, allValues.size());
		assertEquals(param2, allValues.iterator().next().getParameter());

		List<PCMMPlanningTableItem> allItems = getDaoManager().getRepository(IPCMMPlanningTableItemRepository.class)
				.findAll();
		assertTrue(allItems.isEmpty());

		List<PCMMPlanningTableValue> allTableValues = getDaoManager()
				.getRepository(IPCMMPlanningTableValueRepository.class).findAll();
		assertTrue(allTableValues.isEmpty());

		List<PCMMPlanningSelectValue> allSelectValues = getDaoManager()
				.getRepository(IPCMMPlanningSelectValueRepository.class).findAll();
		assertTrue(allSelectValues.isEmpty());
	}

	@Test
	void test_deletePlanningParameter_Null() throws CredibilityException {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningParameter(null));
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEPARAM_NULL), e.getMessage());
	}

	@Test
	void test_deletePlanningParameter_IdNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningParameter(new PCMMPlanningParam()));
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEPARAM_IDNULL), e.getMessage());
	}

	/* ************ deletePlanningValue ************* */

	@Test
	void test_deletePlanningValue_Working() throws CredibilityException {

		// construct data
		PCMMPlanningParam param = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		PCMMPlanningValue newPCMMPlanningValue = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), param, null,
				null, null);
		PCMMPlanningValue newPCMMPlanningValue2 = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), param,
				null, null, null);

		// test
		getPCMMPlanningApp().deletePlanningValue(newPCMMPlanningValue);

		List<PCMMPlanningParam> allParam = getDaoManager().getRepository(IPCMMPlanningParamRepository.class).findAll();
		assertNotNull(allParam);
		assertEquals(1, allParam.size());
		assertEquals(param, allParam.iterator().next());

		List<PCMMPlanningValue> allValues = getDaoManager().getRepository(IPCMMPlanningValueRepository.class).findAll();
		assertNotNull(allValues);
		assertEquals(1, allValues.size());
		assertEquals(newPCMMPlanningValue2, allValues.iterator().next());
	}

	@Test
	void test_deletePlanningValue_Null() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningValue(null));
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEVALUE_NULL), e.getMessage());
	}

	@Test
	void test_deletePlanningValue_IdNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningValue(new PCMMPlanningValue()));
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEVALUE_IDNULL), e.getMessage());
	}

	/* ************ deletePlanningSelectValue ************* */

	@Test
	void test_deletePlanningSelectValue_Working() throws CredibilityException {

		// construct data
		PCMMPlanningParam param = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		PCMMPlanningSelectValue selectValue = TestEntityFactory.getNewPCMMPlanningSelectValue(getDaoManager(), param);
		PCMMPlanningSelectValue selectValue2 = TestEntityFactory.getNewPCMMPlanningSelectValue(getDaoManager(), param);

		// test
		getPCMMPlanningApp().deletePlanningSelectValue(selectValue);

		List<PCMMPlanningParam> allParam = getDaoManager().getRepository(IPCMMPlanningParamRepository.class).findAll();
		assertNotNull(allParam);
		assertEquals(1, allParam.size());
		assertEquals(param, allParam.iterator().next());

		List<PCMMPlanningSelectValue> allValues = getDaoManager()
				.getRepository(IPCMMPlanningSelectValueRepository.class).findAll();
		assertNotNull(allValues);
		assertEquals(1, allValues.size());
		assertEquals(selectValue2, allValues.iterator().next());
	}

	@Test
	void test_deletePlanningSelectValue_Null() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningSelectValue(null));
		assertEquals(RscTools.getString(RscConst.EX_GLB_GENPARAMVALUELIST_DELETE_NULL), e.getMessage());
	}

	@Test
	void test_deletePlanningSelectValue_IdNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningSelectValue(new PCMMPlanningSelectValue()));
		assertEquals(RscTools.getString(RscConst.EX_GLB_GENPARAMVALUELIST_DELETE_IDNULL), e.getMessage());
	}

	/* ************ deletePlanningTableValue ************* */

	@Test
	void test_deletePlanningTableValue_Working() throws CredibilityException {

		// construct data
		PCMMPlanningTableItem item = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), null, null, null,
				null);
		PCMMPlanningTableValue tableValue = TestEntityFactory.getNewPCMMPlanningTableValue(getDaoManager(), null, item,
				null);
		PCMMPlanningTableValue tableValue2 = TestEntityFactory.getNewPCMMPlanningTableValue(getDaoManager(), null, item,
				null);

		// test
		getPCMMPlanningApp().deletePlanningTableValue(tableValue);

		List<PCMMPlanningTableItem> allItems = getDaoManager().getRepository(IPCMMPlanningTableItemRepository.class)
				.findAll();
		assertNotNull(allItems);
		assertEquals(1, allItems.size());
		assertEquals(item, allItems.iterator().next());

		List<PCMMPlanningTableValue> allValues = getDaoManager().getRepository(IPCMMPlanningTableValueRepository.class)
				.findAll();
		assertNotNull(allValues);
		assertEquals(1, allValues.size());
		assertEquals(tableValue2, allValues.iterator().next());
	}

	@Test
	void test_deletePlanningTableValue_Null() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningTableValue(null));
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETETABLEVALUE_NULL), e.getMessage());
	}

	@Test
	void test_deletePlanningTableValue_IdNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningTableValue(new PCMMPlanningTableValue()));
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETETABLEVALUE_IDNULL), e.getMessage());
	}
}
