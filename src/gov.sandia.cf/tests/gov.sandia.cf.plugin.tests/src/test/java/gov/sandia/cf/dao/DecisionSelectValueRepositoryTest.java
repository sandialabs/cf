/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.DecisionSelectValueRepository;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the DecisionRepository
 * 
 * @author Didier Verstraete
 *
 */
class DecisionSelectValueRepositoryTest
		extends AbstractTestRepository<DecisionSelectValue, Integer, DecisionSelectValueRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(DecisionSelectValueRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<DecisionSelectValueRepository> getRepositoryClass() {
		return DecisionSelectValueRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<DecisionSelectValue> getModelClass() {
		return DecisionSelectValue.class;
	}

	@Override
	DecisionSelectValue getModelFulfilled(DecisionSelectValue model) {
		// populate
		DecisionParam newDecisionParam = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);

		fulfillModelStrings(model);
		model.setParameter(newDecisionParam);
		return model;
	}
}
