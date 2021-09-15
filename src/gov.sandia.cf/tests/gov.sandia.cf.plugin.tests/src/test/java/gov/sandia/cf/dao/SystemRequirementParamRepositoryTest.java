/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.YmlGenericSchema;
import gov.sandia.cf.dao.impl.SystemRequirementParamRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the SystemRequirementParamRepository
 */
@RunWith(JUnitPlatform.class)
class SystemRequirementParamRepositoryTest
		extends AbstractTestRepository<SystemRequirementParam, Integer, SystemRequirementParamRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(SystemRequirementParamRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<SystemRequirementParamRepository> getRepositoryClass() {
		return SystemRequirementParamRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<SystemRequirementParam> getModelClass() {
		return SystemRequirementParam.class;
	}

	@Override
	SystemRequirementParam getModelFulfilled(SystemRequirementParam model) {
		// populate
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		fulfillModelStrings(model);
		model.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		model.setModel(newModel);
		return model;
	}
}
