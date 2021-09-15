/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMLevelColorRepository;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.tools.MathTools;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PCMMLevelColorRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PCMMLevelColorRepositoryTest extends AbstractTestRepository<PCMMLevelColor, Integer, PCMMLevelColorRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMLevelColorRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMLevelColorRepository> getRepositoryClass() {
		return PCMMLevelColorRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMLevelColor> getModelClass() {
		return PCMMLevelColor.class;
	}

	@Override
	PCMMLevelColor getModelFulfilled(PCMMLevelColor model) {
		fulfillModelStrings(model);
		model.setCode(MathTools.getRandomIntBase10());
		return model;
	}
}
