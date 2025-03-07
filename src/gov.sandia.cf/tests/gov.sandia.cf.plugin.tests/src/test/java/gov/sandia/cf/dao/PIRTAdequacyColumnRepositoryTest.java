/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PIRTAdequacyColumnRepository;
import gov.sandia.cf.model.PIRTAdequacyColumn;

/**
 * JUnit class to test the PIRTAdequacyColumnRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class PIRTAdequacyColumnRepositoryTest
		extends AbstractTestRepository<PIRTAdequacyColumn, Integer, PIRTAdequacyColumnRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PIRTAdequacyColumnRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTAdequacyColumnRepository> getRepositoryClass() {
		return PIRTAdequacyColumnRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTAdequacyColumn> getModelClass() {
		return PIRTAdequacyColumn.class;
	}

	@Override
	PIRTAdequacyColumn getModelFulfilled(PIRTAdequacyColumn model) {
		fulfillModelStrings(model);
		return model;
	}
}
