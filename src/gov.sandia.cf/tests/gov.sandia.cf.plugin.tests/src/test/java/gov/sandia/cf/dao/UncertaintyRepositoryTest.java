/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.UncertaintyRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the UncertaintyRepository
 * 
 * @author Didier Verstraete
 *
 */
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
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		fulfillModelStrings(model);
		model.setModel(newModel);
		model.setUserCreation(newUser);
		model.setCreationDate(new Date());
		return model;
	}
}
