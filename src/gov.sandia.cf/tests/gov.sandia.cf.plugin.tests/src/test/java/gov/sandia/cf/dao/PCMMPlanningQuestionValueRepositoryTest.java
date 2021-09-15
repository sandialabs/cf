/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMPlanningQuestionValueRepository;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PCMMPlanningQuestionValueRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PCMMPlanningQuestionValueRepositoryTest
		extends AbstractTestRepository<PCMMPlanningQuestionValue, Integer, PCMMPlanningQuestionValueRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningQuestionValueRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningQuestionValueRepository> getRepositoryClass() {
		return PCMMPlanningQuestionValueRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningQuestionValue> getModelClass() {
		return PCMMPlanningQuestionValue.class;
	}

	@Override
	PCMMPlanningQuestionValue getModelFulfilled(PCMMPlanningQuestionValue model) {
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMSubelement);
		fulfillModelStrings(model);
		model.setParameter(newPCMMPlanningQuestion);
		model.setUserCreation(TestEntityFactory.getNewUser(getDaoManager()));
		model.setDateCreation(new Date());
		return model;
	}

	/*
	 * ********************* findByQuestion method *********************
	 */
	@Test
	void testfindByQuestion_EmptyList() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), newPCMMElement);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMSubelement);

		// test
		List<PCMMPlanningQuestionValue> findByQuestion = getRepository().findByQuestion(newPCMMPlanningQuestion);
		assertNotNull(findByQuestion);
		assertTrue(findByQuestion.isEmpty());
	}

	@Test
	void testfindByQuestion_FulfilledList() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), newPCMMElement);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMSubelement);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);
		// the following one is not associated to the planning question
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), null, null, null);

		// test
		List<PCMMPlanningQuestionValue> findByQuestion = getRepository().findByQuestion(newPCMMPlanningQuestion);
		assertNotNull(findByQuestion);
		assertEquals(2, findByQuestion.size());
	}

	@Test
	void testfindByQuestion_ElementNull() {

		// test
		List<PCMMPlanningQuestionValue> findByQuestion = getRepository().findByQuestion(null);
		assertNotNull(findByQuestion);
		assertTrue(findByQuestion.isEmpty());
	}

	/*
	 * ********************* findByElement method *********************
	 */
	@Test
	void testfindByElement_EmptyList() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		// the following one is not linked to the element searched
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), null, null, null);

		// test
		List<PCMMPlanningQuestionValue> findByElement = getRepository().findByElement(newPCMMElement, null);
		assertNotNull(findByElement);
		assertTrue(findByElement.isEmpty());
	}

	@Test
	void testfindByElement_FulfilledList_NoTag() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMElement);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);
		// the following one is associated but is tagged
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null,
				TestEntityFactory.getNewTag(getDaoManager(), null));

		// test
		List<PCMMPlanningQuestionValue> findByElement = getRepository().findByElement(newPCMMElement, null);
		assertNotNull(findByElement);
		assertEquals(2, findByElement.size());
	}

	@Test
	void testfindByElement_FulfilledList_WithTag() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMElement);
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, newTag);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, newTag);
		// the following one is associated but is not tagged
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);

		// test
		List<PCMMPlanningQuestionValue> findByElement = getRepository().findByElement(newPCMMElement, newTag);
		assertNotNull(findByElement);
		assertEquals(2, findByElement.size());
	}

	@Test
	void testfindByElement_ElementNull() {

		// test
		List<PCMMPlanningQuestionValue> findByElement = getRepository().findByElement(null, null);
		assertNotNull(findByElement);
		assertTrue(findByElement.isEmpty());
	}

	/*
	 * ********************* findByElementInSubelement method *********************
	 */
	@Test
	void testfindByElementInSubelement_EmptyList() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		TestEntityFactory.getNewPCMMSubelement(getDaoManager(), newPCMMElement);
		// the following one is not linked to the element searched
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), null, null, null);

		// test
		List<PCMMPlanningQuestionValue> findByElementInSubelement = getRepository()
				.findByElementInSubelement(newPCMMElement, null);
		assertNotNull(findByElementInSubelement);
		assertTrue(findByElementInSubelement.isEmpty());
	}

	@Test
	void testfindByElementInSubelement_FulfilledList_NoTag() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), newPCMMElement);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMSubelement);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);
		// the following one is not associated to the planning question
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), null, null, null);

		// test
		List<PCMMPlanningQuestionValue> findByElementInSubelement = getRepository()
				.findByElementInSubelement(newPCMMElement, null);
		assertNotNull(findByElementInSubelement);
		assertEquals(2, findByElementInSubelement.size());
	}

	@Test
	void testfindByElementInSubelement_FulfilledList_WithTag() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), newPCMMElement);
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMSubelement);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, newTag);
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, newTag);
		// the following one is associated but not tagged
		TestEntityFactory.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);

		// test
		List<PCMMPlanningQuestionValue> findByElementInSubelement = getRepository()
				.findByElementInSubelement(newPCMMElement, newTag);
		assertNotNull(findByElementInSubelement);
		assertEquals(2, findByElementInSubelement.size());
	}

	@Test
	void testfindByElementInSubelement_ElementNull() {

		// test
		List<PCMMPlanningQuestionValue> findByElementInSubelement = getRepository().findByElementInSubelement(null,
				null);
		assertNotNull(findByElementInSubelement);
		assertTrue(findByElementInSubelement.isEmpty());
	}
}