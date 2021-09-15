/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.Date;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.DecisionValueRepository;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the DecisionValueRepository
 */
@RunWith(JUnitPlatform.class)
class DecisionValueRepositoryTest extends AbstractTestRepository<DecisionValue, Integer, DecisionValueRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(DecisionValueRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<DecisionValueRepository> getRepositoryClass() {
		return DecisionValueRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<DecisionValue> getModelClass() {
		return DecisionValue.class;
	}

	@Override
	DecisionValue getModelFulfilled(DecisionValue model) {
		// populate
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		DecisionParam newDecisionParam = TestEntityFactory.getNewDecisionParam(getDaoManager(), newModel, null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		Decision newDecision = TestEntityFactory.getNewDecision(getDaoManager(), newModel, null, newUser);

		fulfillModelStrings(model);
		model.setDecision(newDecision);
		model.setParameter(newDecisionParam);
		model.setUserCreation(newUser);
		model.setDateCreation(new Date());
		return model;
	}
}
