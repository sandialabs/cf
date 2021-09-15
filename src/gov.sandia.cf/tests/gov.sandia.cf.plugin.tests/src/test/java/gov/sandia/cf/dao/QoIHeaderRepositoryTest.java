/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.Date;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.QoIHeaderRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIHeader;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the QoIHeaderRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class QoIHeaderRepositoryTest extends AbstractTestRepository<QoIHeader, Integer, QoIHeaderRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(QoIHeaderRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QoIHeaderRepository> getRepositoryClass() {
		return QoIHeaderRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QoIHeader> getModelClass() {
		return QoIHeader.class;
	}

	@Override
	QoIHeader getModelFulfilled(QoIHeader model) {
		// populate PIRT
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		fulfillModelStrings(model);
		model.setQoi(newQoI);
		model.setCreationDate(new Date());
		model.setUserCreation(newUser);
		return model;
	}
}
