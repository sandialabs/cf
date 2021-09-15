/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PIRTAdequacyLevelGuidelineRepository;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PIRTAdequacyLevelGuidelineRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PIRTAdequacyLevelGuidelineRepositoryTest extends
		AbstractTestRepository<PIRTAdequacyColumnLevelGuideline, Integer, PIRTAdequacyLevelGuidelineRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PIRTAdequacyLevelGuidelineRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTAdequacyLevelGuidelineRepository> getRepositoryClass() {
		return PIRTAdequacyLevelGuidelineRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTAdequacyColumnLevelGuideline> getModelClass() {
		return PIRTAdequacyColumnLevelGuideline.class;
	}

	@Override
	PIRTAdequacyColumnLevelGuideline getModelFulfilled(PIRTAdequacyColumnLevelGuideline model) {
		fulfillModelStrings(model);
		PIRTAdequacyColumnGuideline newPIRTAdequacyColumns = TestEntityFactory.getNewPIRTAdequacyColumnGuideline(getDaoManager());
		model.setAdequacyColumnGuideline(newPIRTAdequacyColumns);
		return model;
	}
}
