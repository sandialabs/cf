/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.UncertaintyRepository;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the UncertaintyRepository
 */
@RunWith(JUnitPlatform.class)
class UncertaintyRepositoryTest extends AbstractTestRepository<Uncertainty, Integer, UncertaintyRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(UncertaintyRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UncertaintyRepository> getRepositoryClass() {
		return UncertaintyRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<Uncertainty> getModelClass() {
		return Uncertainty.class;
	}

	@Override
	Uncertainty getModelFulfilled(Uncertainty model) {
		// populate
		UncertaintyGroup newGroup = TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		fulfillModelStrings(model);
		model.setGroup(newGroup);
		model.setUserCreation(newUser);
		return model;
	}
}
