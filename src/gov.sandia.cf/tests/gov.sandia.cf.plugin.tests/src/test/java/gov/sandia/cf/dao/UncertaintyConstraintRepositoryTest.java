/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.UncertaintyConstraintRepository;
import gov.sandia.cf.model.UncertaintyConstraint;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the UncertaintyConstraintRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class UncertaintyConstraintRepositoryTest
		extends AbstractTestRepository<UncertaintyConstraint, Integer, UncertaintyConstraintRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(UncertaintyConstraintRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UncertaintyConstraintRepository> getRepositoryClass() {
		return UncertaintyConstraintRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UncertaintyConstraint> getModelClass() {
		return UncertaintyConstraint.class;
	}

	@Override
	UncertaintyConstraint getModelFulfilled(UncertaintyConstraint model) {
		UncertaintyParam parameter = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);
		fulfillModelStrings(model);
		model.setParameter(parameter);
		return model;
	}
}
