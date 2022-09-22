/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.IntendedPurposeRepository;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the IntendedPurposeRepository
 * 
 * @author Didier Verstraete
 *
 */
class IntendedPurposeRepositoryTest
		extends AbstractTestRepository<IntendedPurpose, Integer, IntendedPurposeRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(IntendedPurposeRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<IntendedPurposeRepository> getRepositoryClass() {
		return IntendedPurposeRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<IntendedPurpose> getModelClass() {
		return IntendedPurpose.class;
	}

	@Override
	IntendedPurpose getModelFulfilled(IntendedPurpose model) {
		// populate
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		fulfillModelStrings(model);
		model.setModel(newModel);
		return model;
	}

	@Test
	void test_getFirst_NoValues_ModelNull() {
		assertNull(getRepository().getFirst(null));
	}

	@Test
	void test_getFirst_NoValues_ModelNotNull() {
		assertNull(getRepository().getFirst(TestEntityFactory.getNewModel(getDaoManager())));
	}

	@Test
	void test_getFirst_Exists_And_ModelMatch() {
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		IntendedPurpose newIntendedPurpose = TestEntityFactory.getNewIntendedPurpose(getDaoManager(), newModel);
		IntendedPurpose found = getRepository().getFirst(newModel);
		assertNotNull(found);
		assertEquals(newIntendedPurpose, found);

		// clear
		getDaoManager().getRepository(IIntendedPurposeRepository.class).delete(newIntendedPurpose);
	}

	@Test
	void test_getFirst_Exists_And_ModelDoesNotMatch() {
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		Model newModel2 = TestEntityFactory.getNewModel(getDaoManager());
		IntendedPurpose newIntendedPurpose = TestEntityFactory.getNewIntendedPurpose(getDaoManager(), newModel);
		IntendedPurpose found = getRepository().getFirst(newModel2);
		assertNull(found);

		// clear
		getDaoManager().getRepository(IIntendedPurposeRepository.class).delete(newIntendedPurpose);
	}
}
