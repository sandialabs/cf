/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.YmlGenericSchema;
import gov.sandia.cf.dao.impl.DecisionParamRepository;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the DecisionParamRepository
 */
@RunWith(JUnitPlatform.class)
class DecisionParamRepositoryTest extends AbstractTestRepository<DecisionParam, Integer, DecisionParamRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(DecisionParamRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<DecisionParamRepository> getRepositoryClass() {
		return DecisionParamRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<DecisionParam> getModelClass() {
		return DecisionParam.class;
	}

	@Override
	DecisionParam getModelFulfilled(DecisionParam model) {
		// populate
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		fulfillModelStrings(model);
		model.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		model.setModel(newModel);
		return model;
	}
}
