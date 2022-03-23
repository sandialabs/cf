/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.configuration.YmlGenericSchema;
import gov.sandia.cf.dao.impl.UncertaintyParamRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the UncertaintyParamRepository
 */
@RunWith(JUnitPlatform.class)
class UncertaintyParamRepositoryTest
		extends AbstractTestRepository<UncertaintyParam, Integer, UncertaintyParamRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(UncertaintyParamRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UncertaintyParamRepository> getRepositoryClass() {
		return UncertaintyParamRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UncertaintyParam> getModelClass() {
		return UncertaintyParam.class;
	}

	@Override
	UncertaintyParam getModelFulfilled(UncertaintyParam model) {
		// populate
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		fulfillModelStrings(model);
		model.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		model.setModel(newModel);
		return model;
	}
}
