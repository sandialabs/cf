/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.SystemRequirementSelectValueRepository;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the SystemRequirementRepository
 */
@RunWith(JUnitPlatform.class)
class SystemRequirementSelectValueRepositoryTest
		extends AbstractTestRepository<SystemRequirementSelectValue, Integer, SystemRequirementSelectValueRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(SystemRequirementSelectValueRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<SystemRequirementSelectValueRepository> getRepositoryClass() {
		return SystemRequirementSelectValueRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<SystemRequirementSelectValue> getModelClass() {
		return SystemRequirementSelectValue.class;
	}

	@Override
	SystemRequirementSelectValue getModelFulfilled(SystemRequirementSelectValue model) {
		// populate
		SystemRequirementParam newSystemRequirementParam = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), null, null);

		fulfillModelStrings(model);
		model.setParameter(newSystemRequirementParam);
		return model;
	}
}
