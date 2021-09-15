/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMPlanningSelectValueRepository;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PCMMPlanningSelectValueRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PCMMPlanningSelectValueRepositoryTest
		extends AbstractTestRepository<PCMMPlanningSelectValue, Integer, PCMMPlanningSelectValueRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningSelectValueRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningSelectValueRepository> getRepositoryClass() {
		return PCMMPlanningSelectValueRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningSelectValue> getModelClass() {
		return PCMMPlanningSelectValue.class;
	}

	@Override
	PCMMPlanningSelectValue getModelFulfilled(PCMMPlanningSelectValue model) {
		fulfillModelStrings(model);
		model.setParameter(TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null));
		return model;
	}
}