/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.Date;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMPlanningTableValueRepository;
import gov.sandia.cf.model.PCMMPlanningTableValue;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PCMMPlanningTableValueRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PCMMPlanningTableValueRepositoryTest
		extends AbstractTestRepository<PCMMPlanningTableValue, Integer, PCMMPlanningTableValueRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningTableValueRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningTableValueRepository> getRepositoryClass() {
		return PCMMPlanningTableValueRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningTableValue> getModelClass() {
		return PCMMPlanningTableValue.class;
	}

	@Override
	PCMMPlanningTableValue getModelFulfilled(PCMMPlanningTableValue model) {
		fulfillModelStrings(model);
		model.setDateCreation(new Date());
		model.setItem(TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), null, null, null, null));
		model.setParameter(TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null));
		model.setUserCreation(TestEntityFactory.getNewUser(getDaoManager()));
		return model;
	}
}