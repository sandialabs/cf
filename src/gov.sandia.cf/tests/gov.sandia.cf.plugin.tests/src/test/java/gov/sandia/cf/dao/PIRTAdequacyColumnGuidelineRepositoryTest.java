/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PIRTAdequacyColumnGuidelineRepository;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PIRTAdequacyColumnGuidelineRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PIRTAdequacyColumnGuidelineRepositoryTest
		extends AbstractTestRepository<PIRTAdequacyColumnGuideline, Integer, PIRTAdequacyColumnGuidelineRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PIRTAdequacyColumnGuidelineRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTAdequacyColumnGuidelineRepository> getRepositoryClass() {
		return PIRTAdequacyColumnGuidelineRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTAdequacyColumnGuideline> getModelClass() {
		return PIRTAdequacyColumnGuideline.class;
	}

	@Override
	PIRTAdequacyColumnGuideline getModelFulfilled(PIRTAdequacyColumnGuideline model) {
		fulfillModelStrings(model);
		return model;
	}
}
