/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.QoIPlanningConstraintRepository;
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the QoIPlanningConstraintRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class QoIPlanningConstraintRepositoryTest
		extends AbstractTestRepository<QoIPlanningConstraint, Integer, QoIPlanningConstraintRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(QoIPlanningConstraintRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QoIPlanningConstraintRepository> getRepositoryClass() {
		return QoIPlanningConstraintRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QoIPlanningConstraint> getModelClass() {
		return QoIPlanningConstraint.class;
	}

	@Override
	QoIPlanningConstraint getModelFulfilled(QoIPlanningConstraint model) {
		QoIPlanningParam parameter = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);
		fulfillModelStrings(model);
		model.setParameter(parameter);
		return model;
	}
}
