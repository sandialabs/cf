/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PIRTLevelImportanceRepository;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.tools.MathTools;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PIRTLevelImportanceRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PIRTLevelImportanceRepositoryTest
		extends AbstractTestRepository<PIRTLevelImportance, Integer, PIRTLevelImportanceRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PIRTLevelImportanceRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTLevelImportanceRepository> getRepositoryClass() {
		return PIRTLevelImportanceRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTLevelImportance> getModelClass() {
		return PIRTLevelImportance.class;
	}

	@Override
	PIRTLevelImportance getModelFulfilled(PIRTLevelImportance model) {
		fulfillModelStrings(model);
		model.setLevel(MathTools.getRandomIntBase10());
		return model;
	}
}
