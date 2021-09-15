/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.YmlGenericSchema;
import gov.sandia.cf.dao.impl.PCMMPlanningQuestionRepository;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PCMMPlanningQuestionRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PCMMPlanningQuestionRepositoryTest
		extends AbstractTestRepository<PCMMPlanningQuestion, Integer, PCMMPlanningQuestionRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningQuestionRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningQuestionRepository> getRepositoryClass() {
		return PCMMPlanningQuestionRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningQuestion> getModelClass() {
		return PCMMPlanningQuestion.class;
	}

	@Override
	PCMMPlanningQuestion getModelFulfilled(PCMMPlanningQuestion model) {
		model.setModel(TestEntityFactory.getNewModel(getDaoManager()));
		model.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		fulfillModelStrings(model);
		return model;
	}

	@Test
	void testfindByElementInSubelement_EmptyList() {
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		List<PCMMPlanningQuestion> findByElementInSubelement = getRepository()
				.findByElementInSubelement(newPCMMElement);
		assertNotNull(findByElementInSubelement);
		assertTrue(findByElementInSubelement.isEmpty());
	}

	@Test
	void testfindByElementInSubelement_FulfilledList() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), newPCMMElement);
		TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), newPCMMSubelement);
		TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), newPCMMSubelement);

		// test
		List<PCMMPlanningQuestion> findByElementInSubelement = getRepository()
				.findByElementInSubelement(newPCMMElement);
		assertNotNull(findByElementInSubelement);
		assertEquals(2, findByElementInSubelement.size());
	}

	@Test
	void testfindByElementInSubelement_ElementNull() {

		// test
		List<PCMMPlanningQuestion> findByElementInSubelement = getRepository().findByElementInSubelement(null);
		assertNotNull(findByElementInSubelement);
		assertTrue(findByElementInSubelement.isEmpty());
	}
}