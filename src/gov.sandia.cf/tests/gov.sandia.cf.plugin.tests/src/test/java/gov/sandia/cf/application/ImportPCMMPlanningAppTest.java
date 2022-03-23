/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IImportPCMMApp;
import gov.sandia.cf.constants.configuration.YmlGenericSchema;
import gov.sandia.cf.dao.IPCMMPlanningQuestionRepository;
import gov.sandia.cf.dao.IPCMMPlanningSelectValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * JUnit test class for the Import PCMM Planning Application Controller
 * 
 * @author Maxime N.
 *
 */
@RunWith(JUnitPlatform.class)
class ImportPCMMPlanningAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ImportPCMMPlanningAppTest.class);

	@Test
	void testImportWorking() {

		// construct data
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// PCMM Planning Fields
		List<PCMMPlanningParam> planningFields = new ArrayList<>();

		PCMMPlanningParam param1 = new PCMMPlanningParam();
		param1.setRequired(YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE);
		param1.setName("PARAM 1"); //$NON-NLS-1$
		param1.setType("TEXT"); //$NON-NLS-1$

		PCMMPlanningParam param2 = new PCMMPlanningParam();
		param2.setRequired(YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE);
		param2.setName("PARAM 2"); //$NON-NLS-1$
		param2.setType("TEXT"); //$NON-NLS-1$
		PCMMPlanningSelectValue selectValue1 = new PCMMPlanningSelectValue();
		selectValue1.setName("Select 1"); //$NON-NLS-1$
		PCMMPlanningSelectValue selectValue2 = new PCMMPlanningSelectValue();
		selectValue2.setName("Select 2"); //$NON-NLS-1$
		param2.setParameterValueList(Arrays.asList(selectValue1, selectValue2));

		PCMMPlanningParam param3 = new PCMMPlanningParam();
		param3.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		param3.setName("PARAM 3"); //$NON-NLS-1$
		param3.setType("RICHTEXT"); //$NON-NLS-1$
		PCMMPlanningParam child1 = new PCMMPlanningParam();
		child1.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		child1.setName("CHILD 1"); //$NON-NLS-1$
		child1.setType("SELECT"); //$NON-NLS-1$
		PCMMPlanningParam child2 = new PCMMPlanningParam();
		child2.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		child2.setName("CHILD 2"); //$NON-NLS-1$
		child2.setType("RICHTEXT"); //$NON-NLS-1$
		param3.setChildren(Arrays.asList(child1, child2));

		planningFields.addAll(Arrays.asList(param1, param2, param3));

		// PCMM Planning Questions
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		assertNotNull(subelement);

		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		assertNotNull(element);

		Map<IAssessable, List<PCMMPlanningQuestion>> planningQuestions = new HashMap<>();
		PCMMPlanningQuestion question1 = new PCMMPlanningQuestion();
		question1.setSubelement(subelement);
		question1.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		question1.setName("QUESTION 1"); //$NON-NLS-1$
		question1.setType("RICHTEXT"); //$NON-NLS-1$

		PCMMPlanningQuestion question2 = new PCMMPlanningQuestion();
		question2.setSubelement(subelement);
		question2.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		question2.setName("QUESTION 2"); //$NON-NLS-1$
		question2.setType("RICHTEXT"); //$NON-NLS-1$

		PCMMPlanningQuestion question3 = new PCMMPlanningQuestion();
		question3.setElement(element);
		question3.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		question3.setName("QUESTION 3"); //$NON-NLS-1$
		question3.setType("RICHTEXT"); //$NON-NLS-1$

		planningQuestions.put(subelement, Arrays.asList(question1, question2));
		planningQuestions.put(element, Arrays.asList(question3));

		// Test
		try {

			// Import (sub-element level list)
			getAppManager().getService(IImportPCMMApp.class).importPCMMPlanning(model, planningFields,
					planningQuestions);

			// Test PCMM Planning Fields
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(GenericParameter.Filter.MODEL, model);
			filters.put(GenericParameter.Filter.PARENT, NullParameter.NULL);
			List<PCMMPlanningParam> planningFieldsBy = getPCMMPlanningApp().getPlanningFieldsBy(filters);

			assertNotNull(planningFieldsBy);
			assertEquals(3, planningFieldsBy.size());

			filters.put(GenericParameter.Filter.PARENT, NullParameter.NOT_NULL);
			List<PCMMPlanningParam> planningChildrenBy = getPCMMPlanningApp().getPlanningFieldsBy(filters);

			assertNotNull(planningChildrenBy);
			assertEquals(2, planningChildrenBy.size());

			List<PCMMPlanningSelectValue> findAllSelectValues = getDaoManager()
					.getRepository(IPCMMPlanningSelectValueRepository.class).findAll();

			assertNotNull(findAllSelectValues);
			assertEquals(2, findAllSelectValues.size());

			// Test PCMM Planning Questions
			filters = new HashMap<>();
			filters.put(GenericParameter.Filter.MODEL, model);
			filters.put(PCMMPlanningQuestion.Filter.SUBELEMENT, subelement);
			List<PCMMPlanningQuestion> planningQuestionBySubelement = getDaoManager()
					.getRepository(IPCMMPlanningQuestionRepository.class).findBy(filters);

			assertNotNull(planningQuestionBySubelement);
			assertEquals(2, planningQuestionBySubelement.size());

			filters = new HashMap<>();
			filters.put(GenericParameter.Filter.MODEL, model);
			filters.put(PCMMPlanningQuestion.Filter.ELEMENT, element);
			List<PCMMPlanningQuestion> planningQuestionByElement = getDaoManager()
					.getRepository(IPCMMPlanningQuestionRepository.class).findBy(filters);

			assertNotNull(planningQuestionByElement);
			assertEquals(1, planningQuestionByElement.size());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testImportPCMMPlanning_Error_ModelNull() {

		// ********************
		// Import with No model
		// ********************
		try {
			getAppManager().getService(IImportPCMMApp.class).importPCMMPlanning(null, null, null);
			fail("Import with model null is not possible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_IMPORTCONF_MODELNULL), e.getMessage());
		}
	}

	@Test
	void testImport_Error_DataNull() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// **********************************
		// Import with No Planning Parameters
		// **********************************
		try {
			getAppManager().getService(IImportPCMMApp.class).importPCMMPlanning(createdModel, null, null);
		} catch (CredibilityException e) {
			fail("Import with no roles and no elements should work."); //$NON-NLS-1$
		}
	}

	@Test
	void testImport_Error_DataEmpty() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// **********************************
		// Import with No Planning Parameters
		// **********************************
		try {
			getAppManager().getService(IImportPCMMApp.class).importPCMMPlanning(createdModel, new ArrayList<>(),
					new HashMap<>());
		} catch (CredibilityException e) {
			fail("Import with no roles and no elements should work."); //$NON-NLS-1$
		}
	}

	//////////////////////////////////////////////////
	/////// Test importPCMMPlanningQuestion //////////
	//////////////////////////////////////////////////

	@Test
	void testImportPCMMPlanningQuestions_Error_ModelNull() {

		// ********************
		// Import with No model
		// ********************
		try {
			getAppManager().getService(IImportPCMMApp.class).importPCMMPlanningQuestions(null, null);
			fail("Import with model null is not possible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_IMPORTCONF_MODELNULL), e.getMessage());
		}
	}

	//////////////////////////////////////////////////
	/////// Test importPCMMPlanningParam //////////
	//////////////////////////////////////////////////

	@Test
	void testImportPCMMPlanningParam_Error_ModelNull() {

		// ********************
		// Import with No model
		// ********************
		try {
			getAppManager().getService(IImportPCMMApp.class).importPCMMPlanningParam(null, null);
			fail("Import with model null is not possible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_IMPORTCONF_MODELNULL), e.getMessage());
		}
	}

	//////////////////////////////////////////////////
	/////// Test getImportableName //////////
	//////////////////////////////////////////////////

	@Test
	void testgetImportableName_Ok() {
		String importableName = getImportApp().getImportableName(PCMMPlanningQuestion.class);
		assertEquals(RscTools.getString(RscConst.MSG_IMPORTAPP_IMPORTNAME_PCMMPLANNINGQUESTION), importableName);
	}

	@Test
	void testgetImportableName_NotFound() {
		String importableName = getImportApp().getImportableName(Object.class);
		assertEquals(RscTools.empty(), importableName);
	}
}
