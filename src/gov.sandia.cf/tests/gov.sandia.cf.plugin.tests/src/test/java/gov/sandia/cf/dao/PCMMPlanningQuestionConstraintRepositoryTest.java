/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMPlanningQuestionConstraintRepository;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionConstraint;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PCMMPlanningQuestionConstraintRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PCMMPlanningQuestionConstraintRepositoryTest extends
		AbstractTestRepository<PCMMPlanningQuestionConstraint, Integer, PCMMPlanningQuestionConstraintRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningQuestionConstraintRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningQuestionConstraintRepository> getRepositoryClass() {
		return PCMMPlanningQuestionConstraintRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningQuestionConstraint> getModelClass() {
		return PCMMPlanningQuestionConstraint.class;
	}

	@Override
	PCMMPlanningQuestionConstraint getModelFulfilled(PCMMPlanningQuestionConstraint model) {
		PCMMPlanningQuestion parameter = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(), null);
		fulfillModelStrings(model);
		model.setParameter(parameter);
		return model;
	}
}
