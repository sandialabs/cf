/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PIRTLevelDifferenceColorRepository;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.tools.MathTools;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PIRTLevelDifferenceColorRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PIRTLevelDifferenceColorRepositoryTest
		extends AbstractTestRepository<PIRTLevelDifferenceColor, Integer, PIRTLevelDifferenceColorRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PIRTLevelDifferenceColorRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTLevelDifferenceColorRepository> getRepositoryClass() {
		return PIRTLevelDifferenceColorRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTLevelDifferenceColor> getModelClass() {
		return PIRTLevelDifferenceColor.class;
	}

	@Override
	PIRTLevelDifferenceColor getModelFulfilled(PIRTLevelDifferenceColor model) {
		fulfillModelStrings(model);
		model.setMin(MathTools.getRandomIntBase10());
		model.setMax(MathTools.getRandomIntBase10());
		return model;
	}
}
