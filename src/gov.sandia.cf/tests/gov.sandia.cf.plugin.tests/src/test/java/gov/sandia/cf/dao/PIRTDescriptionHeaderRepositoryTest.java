/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PIRTDescriptionHeaderRepository;
import gov.sandia.cf.model.PIRTDescriptionHeader;

/**
 * JUnit class to test the PIRTDescriptionHeaderRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class PIRTDescriptionHeaderRepositoryTest
		extends AbstractTestRepository<PIRTDescriptionHeader, Integer, PIRTDescriptionHeaderRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PIRTDescriptionHeaderRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTDescriptionHeaderRepository> getRepositoryClass() {
		return PIRTDescriptionHeaderRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PIRTDescriptionHeader> getModelClass() {
		return PIRTDescriptionHeader.class;
	}

	@Override
	PIRTDescriptionHeader getModelFulfilled(PIRTDescriptionHeader model) {
		fulfillModelStrings(model);
		return model;
	}
}
