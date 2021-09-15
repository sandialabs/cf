/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.DecisionConstraintRepository;
import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the DecisionConstraintRepository
 */
@RunWith(JUnitPlatform.class)
class DecisionConstraintRepositoryTest
		extends AbstractTestRepository<DecisionConstraint, Integer, DecisionConstraintRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(DecisionConstraintRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<DecisionConstraintRepository> getRepositoryClass() {
		return DecisionConstraintRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<DecisionConstraint> getModelClass() {
		return DecisionConstraint.class;
	}

	@Override
	DecisionConstraint getModelFulfilled(DecisionConstraint model) {
		// populate
		DecisionParam newParam = TestEntityFactory.getNewDecisionParam(getDaoManager(), null, null);

		fulfillModelStrings(model);
		model.setParameter(newParam);
		return model;
	}
}
