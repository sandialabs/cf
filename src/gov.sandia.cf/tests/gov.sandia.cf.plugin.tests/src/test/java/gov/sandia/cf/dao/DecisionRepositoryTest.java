/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.DecisionRepository;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the DecisionRepository
 * 
 * @author Didier Verstraete
 *
 */
class DecisionRepositoryTest extends AbstractTestRepository<Decision, Integer, DecisionRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(DecisionRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<DecisionRepository> getRepositoryClass() {
		return DecisionRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<Decision> getModelClass() {
		return Decision.class;
	}

	@Override
	Decision getModelFulfilled(Decision model) {
		// populate
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		fulfillModelStrings(model);
		model.setCreationDate(new Date());
		model.setModel(newModel);
		model.setUserCreation(newUser);
		return model;
	}

}
