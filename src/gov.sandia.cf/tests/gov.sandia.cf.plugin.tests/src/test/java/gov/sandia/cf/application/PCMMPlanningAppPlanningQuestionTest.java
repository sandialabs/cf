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

import gov.sandia.cf.constants.configuration.YmlGenericSchema;
import gov.sandia.cf.dao.IPCMMPlanningQuestionRepository;
import gov.sandia.cf.dao.IPCMMPlanningQuestionValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
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
class PCMMPlanningAppPlanningQuestionTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningAppPlanningQuestionTest.class);

	/* ************ addPlanningQuestion ************* */

	@Test
	void testaddPlanningQuestion_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMPlanningQuestion question = new PCMMPlanningQuestion();
		question.setModel(newModel);
		question.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		question.setName("PARAM"); //$NON-NLS-1$
		question.setType("TYPE PARAM"); //$NON-NLS-1$

		// test
		try {
			getPCMMPlanningApp().addPlanningQuestion(question);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testaddPlanningQuestion_QuestionNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().addPlanningQuestion(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDQUESTION_NULL), e.getMessage());
	}

	/* ************ addAllPCMMPlanningQuestion ************* */

	@Test
	void test_addAllPCMMPlanningQuestion_PCMMPlanningParamWithChildren_Working() throws CredibilityException {

		// construct data
		// model
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// PCMM planning question
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

		// test
		getPCMMPlanningApp().addAllPCMMPlanningQuestion(newModel, Arrays.asList(question, question2));

		List<PCMMPlanningQuestion> findAllQuestions = getDaoManager()
				.getRepository(IPCMMPlanningQuestionRepository.class).findAll();
		assertNotNull(findAllQuestions);
		assertEquals(2, findAllQuestions.size());
	}

	@Test
	void testaddAllPlanningParameter_ParamNull() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			getPCMMPlanningApp().addAllPCMMPlanningQuestion(newModel, null);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testaddAllPlanningParameter_ModelNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().addAllPCMMPlanningQuestion(null, null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_IMPORTCONF_MODELNULL), e.getMessage());
	}

	/* ************ getPlanningQuestionsByElement ************* */

	@Test
	void testgetPlanningQuestionsByElement_PCMMModeDefault() {

		// construct data
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), newPCMMSubelement);
		TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), newPCMMSubelement);

		// test
		List<PCMMPlanningQuestion> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningQuestionsByElement(newPCMMSubelement.getElement(), PCMMMode.DEFAULT);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	@Test
	void testgetPlanningQuestionsByElement_PCMMModeSimplified() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), newPCMMElement);
		TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), newPCMMElement);

		// test
		List<PCMMPlanningQuestion> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningQuestionsByElement(newPCMMElement, PCMMMode.SIMPLIFIED);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	/* ************ getPlanningQuestionsValueByElement ************* */

	@Test
	void testgetPlanningQuestionsValueByElement_PCMMModeDefault_NotTagged() {

		// construct data
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMSubelement);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);
		// the following one is tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null,
				TestEntityFactory.getNewTag(getDaoManager(), null));

		// test
		Tag tag = null;
		List<PCMMPlanningQuestionValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningQuestionsValueByElement(newPCMMSubelement.getElement(), PCMMMode.DEFAULT, tag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	@Test
	void testgetPlanningQuestionsValueByElement_PCMMModeDefault_Tagged() {

		// construct data
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMSubelement);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, newTag);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, newTag);
		// the following one is not tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);

		// test
		List<PCMMPlanningQuestionValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningQuestionsValueByElement(newPCMMSubelement.getElement(), PCMMMode.DEFAULT, newTag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	@Test
	void testgetPlanningQuestionsValueByElement_PCMMModeSimplified_NotTagged() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMElement);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);
		// the following one is tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null,
				TestEntityFactory.getNewTag(getDaoManager(), null));

		// test
		Tag tag = null;
		List<PCMMPlanningQuestionValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningQuestionsValueByElement(newPCMMElement, PCMMMode.SIMPLIFIED, tag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	@Test
	void testgetPlanningQuestionsValueByElement_PCMMModeSimplified_Tagged() {

		// construct data
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMElement);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, newTag);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, newTag);
		// the following one is not tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);

		// test
		List<PCMMPlanningQuestionValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningQuestionsValueByElement(newPCMMElement, PCMMMode.SIMPLIFIED, newTag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	/* ************ getPlanningQuestionsValueByElement ************* */

	@Test
	void testgetPlanningQuestionsValueByElement_PCMMModeDefault_ListTag() {

		// construct data
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMSubelement);
		Tag tag1 = TestEntityFactory.getNewTag(getDaoManager(), null);
		Tag tag2 = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMPlanningQuestionValue value1 = TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(),
				newPCMMPlanningQuestion, null, null);
		PCMMPlanningQuestionValue value2 = TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(),
				newPCMMPlanningQuestion, null, null);
		PCMMPlanningQuestionValue value3 = TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(),
				newPCMMPlanningQuestion, null, tag1);
		// the following one is tagged with tag2 so it should not be retrieved
		PCMMPlanningQuestionValue value4 = TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(),
				newPCMMPlanningQuestion, null, tag2);

		// test
		List<PCMMPlanningQuestionValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningQuestionsValueByElement(newPCMMSubelement.getElement(), PCMMMode.DEFAULT,
						Arrays.asList(null, tag1));

		assertNotNull(planningQuestionsByElement);
		assertEquals(3, planningQuestionsByElement.size());
		assertTrue(planningQuestionsByElement.contains(value1));
		assertTrue(planningQuestionsByElement.contains(value2));
		assertTrue(planningQuestionsByElement.contains(value3));
		assertFalse(planningQuestionsByElement.contains(value4));
	}

	@Test
	void testgetPlanningQuestionsValueByElement_PCMMModeSimplified_ListTag() {

		// construct data
		PCMMElement newElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newElement);
		Tag tag1 = TestEntityFactory.getNewTag(getDaoManager(), null);
		Tag tag2 = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMPlanningQuestionValue value1 = TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(),
				newPCMMPlanningQuestion, null, null);
		PCMMPlanningQuestionValue value2 = TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(),
				newPCMMPlanningQuestion, null, null);
		PCMMPlanningQuestionValue value3 = TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(),
				newPCMMPlanningQuestion, null, tag1);
		// the following one is tagged with tag2 so it should not be retrieved
		PCMMPlanningQuestionValue value4 = TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(),
				newPCMMPlanningQuestion, null, tag2);

		// test
		List<PCMMPlanningQuestionValue> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningQuestionsValueByElement(newElement, PCMMMode.SIMPLIFIED, Arrays.asList(null, tag1));

		assertNotNull(planningQuestionsByElement);
		assertEquals(3, planningQuestionsByElement.size());
		assertTrue(planningQuestionsByElement.contains(value1));
		assertTrue(planningQuestionsByElement.contains(value2));
		assertTrue(planningQuestionsByElement.contains(value3));
		assertFalse(planningQuestionsByElement.contains(value4));
	}
	/* ************ addPlanningQuestionValue ************* */

	@Test
	void testaddPlanningQuestionValue_Working() throws CredibilityException {

		// construct data
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMSubelement);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		PCMMPlanningQuestionValue value = new PCMMPlanningQuestionValue();
		value.setDateCreation(new Date());
		value.setParameter(newPCMMPlanningQuestion);
		value.setUserCreation(newUser);
		value.setValue("VALUE"); //$NON-NLS-1$

		// test
		PCMMPlanningQuestionValue addPlanningQuestionValue = getPCMMPlanningApp().addPlanningQuestionValue(value);

		assertNotNull(addPlanningQuestionValue);
		assertNotNull(addPlanningQuestionValue.getId());
	}

	@Test
	void testaddPlanningQuestionValue_PlanningValueNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().addPlanningQuestionValue(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDQUESTIONVALUE_NULL), e.getMessage());
	}

	/* ************ updatePlanningQuestionValue ************* */

	@Test
	void testupdatePlanningQuestionValue_Working() throws CredibilityException {

		// construct data
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMSubelement);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		PCMMPlanningQuestionValue newPCMMPlanningQuestionValue = TestEntityFactory
				.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);

		newPCMMPlanningQuestionValue.setValue("MY_NEW_VALUE"); //$NON-NLS-1$

		// test
		PCMMPlanningQuestionValue updatedPlanningValue = getPCMMPlanningApp()
				.updatePlanningQuestionValue(newPCMMPlanningQuestionValue, newUser);

		assertNotNull(updatedPlanningValue);
		assertNotNull(updatedPlanningValue.getId());
		assertEquals("MY_NEW_VALUE", updatedPlanningValue.getValue()); //$NON-NLS-1$
	}

	@Test
	void testupdatePlanningQuestionValue_PlanningValueNull() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().updatePlanningQuestionValue(null, newUser);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEQUESTIONVALUE_NULL), e.getMessage());
	}

	@Test
	void testupdatePlanningQuestionValue_IdNull() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().updatePlanningQuestionValue(new PCMMPlanningQuestionValue(), newUser);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEQUESTIONVALUE_IDNULL), e.getMessage());
	}

	/* ************ getPlanningQuestionValueBy ************* */

	@Test
	void testgetPlanningQuestionValueBy_Working() {

		// construct data
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), null, null, null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), null, null, null);

		// test
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericValue.Filter.VALUE, NullParameter.NOT_NULL);
		List<PCMMPlanningQuestionValue> planningFieldsBy = getPCMMPlanningApp().getPlanningQuestionValueBy(filters);
		assertNotNull(planningFieldsBy);
		assertEquals(2, planningFieldsBy.size());
	}

	/* ************ deleteAllPlanningQuestions ************* */

	@Test
	void test_deleteAllPlanningQuestions_Working() throws CredibilityException {

		// construct data
		PCMMPlanningQuestion question1 = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), question1, null, null);

		PCMMPlanningQuestion question2 = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), question2, null, null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), question2, null, null);

		PCMMPlanningQuestion question3 = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), question3, null, null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), question3, null, null);

		// test
		getPCMMPlanningApp().deleteAllPlanningQuestions(Arrays.asList(question1, question2));

		List<PCMMPlanningQuestion> allQuestions = getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class)
				.findAll();
		assertNotNull(allQuestions);
		assertEquals(1, allQuestions.size());
		assertEquals(question3, allQuestions.iterator().next());

		List<PCMMPlanningQuestionValue> allQuestionValues = getDaoManager()
				.getRepository(IPCMMPlanningQuestionValueRepository.class).findAll();
		assertNotNull(allQuestions);
		assertEquals(2, allQuestionValues.size());
		assertEquals(question3, allQuestionValues.iterator().next().getParameter());
	}

	@Test
	void test_deleteAllPlanningQuestions_Null() {

		// test
		try {
			getPCMMPlanningApp().deleteAllPlanningQuestions(null);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	/* ************ deletePlanningQuestion ************* */

	@Test
	void test_deletePlanningQuestion_Working() throws CredibilityException {

		// construct data
		PCMMPlanningQuestion question1 = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), question1, null, null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), question1, null, null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), question1, null, null);

		PCMMPlanningQuestion question2 = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), null);
		PCMMPlanningQuestionValue question2Value = TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(),
				question2, null, null);

		// test
		getPCMMPlanningApp().deletePlanningQuestion(question1);

		List<PCMMPlanningQuestion> allQuestions = getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class)
				.findAll();
		assertNotNull(allQuestions);
		assertEquals(1, allQuestions.size());
		assertEquals(question2, allQuestions.iterator().next());

		List<PCMMPlanningQuestionValue> allQuestionValues = getDaoManager()
				.getRepository(IPCMMPlanningQuestionValueRepository.class).findAll();
		assertNotNull(allQuestionValues);
		assertEquals(1, allQuestionValues.size());
		assertEquals(question2Value, allQuestionValues.iterator().next());
	}

	@Test
	void test_deletePlanningQuestion_Null() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningQuestion(null));
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEQUESTION_NULL), e.getMessage());
	}

	@Test
	void test_deletePlanningQuestion_IdNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningQuestion(new PCMMPlanningQuestion()));
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEQUESTION_IDNULL), e.getMessage());
	}

	/* ************ deletePlanningQuestionValue ************* */

	@Test
	void test_deletePlanningQuestionValue_Working() throws CredibilityException {

		// construct data
		PCMMPlanningQuestion question1 = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), null);
		PCMMPlanningQuestionValue newPCMMPlanningQuestionValue = TestEntityFactory
				.getNewPCMMPlanningQuestionValue(getDaoManager(), question1, null, null);
		PCMMPlanningQuestionValue newPCMMPlanningQuestionValue2 = TestEntityFactory
				.getNewPCMMPlanningQuestionValue(getDaoManager(), question1, null, null);

		// test
		getPCMMPlanningApp().deletePlanningQuestionValue(newPCMMPlanningQuestionValue);

		List<PCMMPlanningQuestion> allQuestions = getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class)
				.findAll();
		assertNotNull(allQuestions);
		assertEquals(1, allQuestions.size());
		assertEquals(question1, allQuestions.iterator().next());

		List<PCMMPlanningQuestionValue> allQuestionValues = getDaoManager()
				.getRepository(IPCMMPlanningQuestionValueRepository.class).findAll();
		assertNotNull(allQuestionValues);
		assertEquals(1, allQuestionValues.size());
		assertEquals(newPCMMPlanningQuestionValue2, allQuestionValues.iterator().next());
	}

	@Test
	void test_deletePlanningQuestionValue_Null() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningQuestionValue(null));
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEQUESTIONVALUE_NULL), e.getMessage());
	}

	@Test
	void test_deletePlanningQuestionValue_IdNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getPCMMPlanningApp().deletePlanningQuestionValue(new PCMMPlanningQuestionValue()));
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEQUESTIONVALUE_IDNULL), e.getMessage());
	}
}
