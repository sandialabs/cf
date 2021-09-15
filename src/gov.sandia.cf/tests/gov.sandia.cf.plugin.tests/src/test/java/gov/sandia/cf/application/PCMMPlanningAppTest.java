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
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.YmlGenericSchema;
import gov.sandia.cf.dao.IPCMMPlanningParamRepository;
import gov.sandia.cf.dao.IPCMMPlanningQuestionRepository;
import gov.sandia.cf.dao.IPCMMPlanningSelectValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.model.PCMMSubelement;
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
class PCMMPlanningAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningAppTest.class);

	/* ************ isPCMMPlanningEnabled ************* */

	@Test
	void test_isPCMMPlanningEnabled_Working_onePlanningParam() {

		// construct data
		TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);

		// test
		assertTrue(getPCMMPlanningApp().isPCMMPlanningEnabled());
	}

	@Test
	void test_isPCMMPlanningEnabled_Working_onePlanningQuestion() {

		// construct data
		TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), null);

		// test
		assertTrue(getPCMMPlanningApp().isPCMMPlanningEnabled());
	}

	@Test
	void test_isPCMMPlanningEnabled_Working_onePlanningParam_and_Working_onePlanningQuestion() {

		// construct data
		TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), null);

		// test
		assertTrue(getPCMMPlanningApp().isPCMMPlanningEnabled());
	}

	@Test
	void test_isPCMMPlanningEnabled_Disabled() {

		// test
		assertFalse(getPCMMPlanningApp().isPCMMPlanningEnabled());
	}

	/* ************ addAllPCMMPlanning ************* */

	@Test
	void test_addAllPCMMPlanning_PCMMPlanningParamWithChildren_Working() throws CredibilityException {

		// construct data
		// model
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// PCMM planning param
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

		// PCMMPlanningQuestions
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningQuestion question = new PCMMPlanningQuestion();
		question.setModel(newModel);
		question.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		question.setName("Question 1?"); //$NON-NLS-1$
		question.setType("Question Type"); //$NON-NLS-1$
		question.setSubelement(newPCMMSubelement);

		PCMMPlanningQuestion question2 = new PCMMPlanningQuestion();
		question2.setModel(newModel);
		question2.setRequired(YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE);
		question2.setName("Question 2?"); //$NON-NLS-1$
		question2.setType("Question Type"); //$NON-NLS-1$
		question2.setSubelement(newPCMMSubelement);

		HashMap<IAssessable, List<PCMMPlanningQuestion>> mapQuestions = new HashMap<IAssessable, List<PCMMPlanningQuestion>>();
		mapQuestions.put(newPCMMSubelement, Arrays.asList(question, question2));

		// test
		getPCMMPlanningApp().addAllPCMMPlanning(newModel, Arrays.asList(planningParam, planningParam2), mapQuestions);

		List<PCMMPlanningParam> findAllParam = getDaoManager().getRepository(IPCMMPlanningParamRepository.class)
				.findAll();
		assertNotNull(findAllParam);
		assertEquals(4, findAllParam.size());

		List<PCMMPlanningSelectValue> findAllSelect = getDaoManager()
				.getRepository(IPCMMPlanningSelectValueRepository.class).findAll();
		assertNotNull(findAllSelect);
		assertEquals(2, findAllSelect.size());

		List<PCMMPlanningQuestion> findAllQuestions = getDaoManager()
				.getRepository(IPCMMPlanningQuestionRepository.class).findAll();
		assertNotNull(findAllQuestions);
		assertEquals(2, findAllQuestions.size());
	}

	@Test
	void test_addAllPlanning_ListNull() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			getPCMMPlanningApp().addAllPCMMPlanning(newModel, null, null);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testaddAllPlanning_ModelNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().addAllPCMMPlanning(null, new ArrayList<PCMMPlanningParam>(),
					new HashMap<IAssessable, List<PCMMPlanningQuestion>>());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_IMPORTCONF_MODELNULL), e.getMessage());
	}
}
