/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.UncertaintySelectValueRepository;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the UncertaintyRepository
 */
@RunWith(JUnitPlatform.class)
class UncertaintySelectValueRepositoryTest
		extends AbstractTestRepository<UncertaintySelectValue, Integer, UncertaintySelectValueRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(UncertaintySelectValueRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UncertaintySelectValueRepository> getRepositoryClass() {
		return UncertaintySelectValueRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UncertaintySelectValue> getModelClass() {
		return UncertaintySelectValue.class;
	}

	@Override
	UncertaintySelectValue getModelFulfilled(UncertaintySelectValue model) {
		// populate
		UncertaintyParam newUncertaintyParam = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);

		fulfillModelStrings(model);
		model.setParameter(newUncertaintyParam);
		return model;
	}
}
