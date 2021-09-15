/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.QoIPlanningSelectValueRepository;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the QoIPlanningSelectValueRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class QoIPlanningSelectValueRepositoryTest
		extends AbstractTestRepository<QoIPlanningSelectValue, Integer, QoIPlanningSelectValueRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(QoIPlanningSelectValueRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QoIPlanningSelectValueRepository> getRepositoryClass() {
		return QoIPlanningSelectValueRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QoIPlanningSelectValue> getModelClass() {
		return QoIPlanningSelectValue.class;
	}

	@Override
	QoIPlanningSelectValue getModelFulfilled(QoIPlanningSelectValue model) {
		QoIPlanningParam parameter = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), null);
		fulfillModelStrings(model);
		model.setParameter(parameter);
		return model;
	}
}
