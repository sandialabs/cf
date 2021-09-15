/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.SystemRequirementConstraintRepository;
import gov.sandia.cf.model.SystemRequirementConstraint;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the SystemRequirementConstraintRepository
 */
@RunWith(JUnitPlatform.class)
class SystemRequirementConstraintRepositoryTest
		extends AbstractTestRepository<SystemRequirementConstraint, Integer, SystemRequirementConstraintRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(SystemRequirementConstraintRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<SystemRequirementConstraintRepository> getRepositoryClass() {
		return SystemRequirementConstraintRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<SystemRequirementConstraint> getModelClass() {
		return SystemRequirementConstraint.class;
	}

	@Override
	SystemRequirementConstraint getModelFulfilled(SystemRequirementConstraint model) {
		// populate
		SystemRequirementParam newParam = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), null, null);

		fulfillModelStrings(model);
		model.setParameter(newParam);
		return model;
	}
}
