/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.ARGParametersRepository;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the ARGParametersRepository
 */
@RunWith(JUnitPlatform.class)
class ARGParametersRepositoryTest extends AbstractTestRepository<ARGParameters, Integer, ARGParametersRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ARGParametersRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<ARGParametersRepository> getRepositoryClass() {
		return ARGParametersRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<ARGParameters> getModelClass() {
		return ARGParameters.class;
	}

	@Override
	ARGParameters getModelFulfilled(ARGParameters model) {
		fulfillModelStrings(model);
		return model;
	}

	@Test
	void test_getFirst_NoValues() {
		assertNull(getRepository().getFirst());
	}

	@Test
	void test_getFirst_Exists() {
		ARGParameters newARGParameters = TestEntityFactory.getNewARGParameters(getDaoManager());
		ARGParameters argParamFound = getRepository().getFirst();
		assertNotNull(argParamFound);
		assertEquals(newARGParameters, argParamFound);

		// clear
		getDaoManager().getRepository(IARGParametersRepository.class).delete(newARGParameters);
	}
}
