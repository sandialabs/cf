/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMLevelDescRepository;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PCMMLevelDescRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PCMMLevelDescRepositoryTest
		extends AbstractTestRepository<PCMMLevelDescriptor, Integer, PCMMLevelDescRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMLevelDescRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMLevelDescRepository> getRepositoryClass() {
		return PCMMLevelDescRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMLevelDescriptor> getModelClass() {
		return PCMMLevelDescriptor.class;
	}

	@Override
	PCMMLevelDescriptor getModelFulfilled(PCMMLevelDescriptor model) {
		// populate PCMM
		PCMMLevel newLevel = TestEntityFactory.getNewPCMMLevel(getDaoManager(), null, 0);

		fulfillModelStrings(model);
		model.setLevel(newLevel);
		return model;
	}

}
