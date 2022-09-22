/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.SystemRequirementValueRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the SystemRequirementValueRepository
 * 
 * @author Didier Verstraete
 *
 */
class SystemRequirementValueRepositoryTest
		extends AbstractTestRepository<SystemRequirementValue, Integer, SystemRequirementValueRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(SystemRequirementValueRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<SystemRequirementValueRepository> getRepositoryClass() {
		return SystemRequirementValueRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<SystemRequirementValue> getModelClass() {
		return SystemRequirementValue.class;
	}

	@Override
	SystemRequirementValue getModelFulfilled(SystemRequirementValue model) {
		// populate
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		SystemRequirementParam newSystemRequirementParam = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), newModel, null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		SystemRequirement newSystemRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), newModel,
				null, newUser);

		fulfillModelStrings(model);
		model.setRequirement(newSystemRequirement);
		model.setParameter(newSystemRequirementParam);
		model.setUserCreation(newUser);
		model.setDateCreation(new Date());
		return model;
	}
}
