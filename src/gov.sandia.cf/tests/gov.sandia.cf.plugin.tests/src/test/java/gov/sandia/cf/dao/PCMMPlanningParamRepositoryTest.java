/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.configuration.YmlGenericSchema;
import gov.sandia.cf.dao.impl.PCMMPlanningParamRepository;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the PCMMPlanningParamRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class PCMMPlanningParamRepositoryTest
		extends AbstractTestRepository<PCMMPlanningParam, Integer, PCMMPlanningParamRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningParamRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningParamRepository> getRepositoryClass() {
		return PCMMPlanningParamRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningParam> getModelClass() {
		return PCMMPlanningParam.class;
	}

	@Override
	PCMMPlanningParam getModelFulfilled(PCMMPlanningParam model) {
		fulfillModelStrings(model);
		model.setRequired(YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE);
		model.setModel(TestEntityFactory.getNewModel(getDaoManager()));
		return model;
	}

}