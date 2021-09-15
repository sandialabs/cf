/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.ARGParametersQoIOptionRepository;
import gov.sandia.cf.model.ARGParametersQoIOption;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the ARGParametersQoIOptionRepository
 */
@RunWith(JUnitPlatform.class)
class ARGParametersQoIOptionRepositoryTest
		extends AbstractTestRepository<ARGParametersQoIOption, Integer, ARGParametersQoIOptionRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ARGParametersQoIOptionRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<ARGParametersQoIOptionRepository> getRepositoryClass() {
		return ARGParametersQoIOptionRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<ARGParametersQoIOption> getModelClass() {
		return ARGParametersQoIOption.class;
	}

	@Override
	ARGParametersQoIOption getModelFulfilled(ARGParametersQoIOption model) {
		fulfillModelStrings(model);
		return model;
	}
}
