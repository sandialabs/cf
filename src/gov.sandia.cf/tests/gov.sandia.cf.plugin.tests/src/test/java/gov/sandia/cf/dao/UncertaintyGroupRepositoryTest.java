/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.UncertaintyGroupRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the UncertaintyGroupRepository
 */
@RunWith(JUnitPlatform.class)
class UncertaintyGroupRepositoryTest
		extends AbstractTestRepository<UncertaintyGroup, Integer, UncertaintyGroupRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(UncertaintyGroupRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UncertaintyGroupRepository> getRepositoryClass() {
		return UncertaintyGroupRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UncertaintyGroup> getModelClass() {
		return UncertaintyGroup.class;
	}

	@Override
	UncertaintyGroup getModelFulfilled(UncertaintyGroup model) {
		// populate
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		fulfillModelStrings(model);
		model.setModel(newModel);
		return model;
	}
}
