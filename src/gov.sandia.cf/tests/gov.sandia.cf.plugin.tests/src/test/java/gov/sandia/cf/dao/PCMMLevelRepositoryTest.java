/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMLevelRepository;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.tools.MathTools;

/**
 * JUnit class to test the PCMMLevelRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class PCMMLevelRepositoryTest extends AbstractTestRepository<PCMMLevel, Integer, PCMMLevelRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMLevelRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMLevelRepository> getRepositoryClass() {
		return PCMMLevelRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMLevel> getModelClass() {
		return PCMMLevel.class;
	}

	@Override
	PCMMLevel getModelFulfilled(PCMMLevel model) {
		fulfillModelStrings(model);
		model.setCode(MathTools.getRandomIntBase10());
		return model;
	}
}
