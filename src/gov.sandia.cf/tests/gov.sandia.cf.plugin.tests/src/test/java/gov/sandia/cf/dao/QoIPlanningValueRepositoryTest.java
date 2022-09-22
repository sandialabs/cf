/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.QoIPlanningValueRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningValue;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the QoIPlanningValueRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class QoIPlanningValueRepositoryTest
		extends AbstractTestRepository<QoIPlanningValue, Integer, QoIPlanningValueRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(QoIPlanningValueRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QoIPlanningValueRepository> getRepositoryClass() {
		return QoIPlanningValueRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QoIPlanningValue> getModelClass() {
		return QoIPlanningValue.class;
	}

	@Override
	QoIPlanningValue getModelFulfilled(QoIPlanningValue model) {
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());
		QuantityOfInterest qoi = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		QoIPlanningParam parameter = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), newModel);
		fulfillModelStrings(model);
		model.setParameter(parameter);
		model.setDateCreation(new Date());
		model.setUserCreation(user);
		model.setQoi(qoi);
		return model;
	}
}
