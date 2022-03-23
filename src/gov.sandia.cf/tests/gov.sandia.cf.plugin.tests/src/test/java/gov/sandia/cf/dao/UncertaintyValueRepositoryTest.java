/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.Date;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.UncertaintyValueRepository;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the UncertaintyRepository
 */
@RunWith(JUnitPlatform.class)
class UncertaintyValueRepositoryTest
		extends AbstractTestRepository<UncertaintyValue, Integer, UncertaintyValueRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(UncertaintyValueRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UncertaintyValueRepository> getRepositoryClass() {
		return UncertaintyValueRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UncertaintyValue> getModelClass() {
		return UncertaintyValue.class;
	}

	@Override
	UncertaintyValue getModelFulfilled(UncertaintyValue model) {
		// populate
		UncertaintyParam newUncertaintyParam = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);
		Uncertainty newUncertainty = TestEntityFactory.getNewUncertainty(getDaoManager(), null, null, null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		fulfillModelStrings(model);
		model.setUncertainty(newUncertainty);
		model.setParameter(newUncertaintyParam);
		model.setUserCreation(newUser);
		model.setDateCreation(new Date());
		return model;
	}
}
