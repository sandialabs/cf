/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PIRTAdequacyColumnGuidelineRepository;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;

/**
 * JUnit class to test the PIRTAdequacyColumnGuidelineRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
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
