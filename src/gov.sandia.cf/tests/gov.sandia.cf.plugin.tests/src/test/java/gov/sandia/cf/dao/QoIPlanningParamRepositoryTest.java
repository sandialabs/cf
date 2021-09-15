/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.QoIPlanningParamRepository;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the QoIPlanningParamRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class QoIPlanningParamRepositoryTest
		extends AbstractTestRepository<QoIPlanningParam, Integer, QoIPlanningParamRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(QoIPlanningParamRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QoIPlanningParamRepository> getRepositoryClass() {
		return QoIPlanningParamRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QoIPlanningParam> getModelClass() {
		return QoIPlanningParam.class;
	}

	@Override
	QoIPlanningParam getModelFulfilled(QoIPlanningParam model) {
		fulfillModelStrings(model);
		model.setModel(TestEntityFactory.getNewModel(getDaoManager()));
		return model;
	}
}
