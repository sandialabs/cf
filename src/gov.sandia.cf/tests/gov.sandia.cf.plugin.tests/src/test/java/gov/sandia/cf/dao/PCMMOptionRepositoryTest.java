/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMOptionRepository;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;

/**
 * JUnit class to test the PCMMOptionRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class PCMMOptionRepositoryTest extends AbstractTestRepository<PCMMOption, Integer, PCMMOptionRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMOptionRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMOptionRepository> getRepositoryClass() {
		return PCMMOptionRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMOption> getModelClass() {
		return PCMMOption.class;
	}

	@Override
	PCMMOption getModelFulfilled(PCMMOption model) {
		fulfillModelStrings(model);
		model.setPhase(PCMMPhase.AGGREGATE);
		return model;
	}
}
