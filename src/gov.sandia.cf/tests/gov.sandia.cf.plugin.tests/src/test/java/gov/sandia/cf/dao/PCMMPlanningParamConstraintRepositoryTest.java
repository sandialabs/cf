/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMPlanningParamConstraintRepository;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningParamConstraint;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the PCMMPlanningParamConstraintRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class PCMMPlanningParamConstraintRepositoryTest
		extends AbstractTestRepository<PCMMPlanningParamConstraint, Integer, PCMMPlanningParamConstraintRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningParamConstraintRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningParamConstraintRepository> getRepositoryClass() {
		return PCMMPlanningParamConstraintRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningParamConstraint> getModelClass() {
		return PCMMPlanningParamConstraint.class;
	}

	@Override
	PCMMPlanningParamConstraint getModelFulfilled(PCMMPlanningParamConstraint model) {
		PCMMPlanningParam parameter = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		fulfillModelStrings(model);
		model.setParameter(parameter);
		return model;
	}
}
