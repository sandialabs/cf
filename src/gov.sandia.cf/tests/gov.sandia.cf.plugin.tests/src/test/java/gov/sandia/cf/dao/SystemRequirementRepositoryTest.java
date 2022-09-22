/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.SystemRequirementRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the SystemRequirementRepository
 * 
 * @author Didier Verstraete
 *
 */
class SystemRequirementRepositoryTest
		extends AbstractTestRepository<SystemRequirement, Integer, SystemRequirementRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(SystemRequirementRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<SystemRequirementRepository> getRepositoryClass() {
		return SystemRequirementRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<SystemRequirement> getModelClass() {
		return SystemRequirement.class;
	}

	@Override
	SystemRequirement getModelFulfilled(SystemRequirement model) {
		// populate
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		fulfillModelStrings(model);
		model.setCreationDate(new Date());
		model.setModel(newModel);
		model.setUserCreation(newUser);
		return model;
	}

	/*
	 * ****************** findRootRequirementsByModel method ******************
	 */
	@Test
	void testFindRootRequirementsByModel_Null() {
		List<SystemRequirement> findByElementInSubelement = getRepository().findRootRequirementsByModel(null);
		assertNotNull(findByElementInSubelement);
		assertTrue(findByElementInSubelement.isEmpty());
	}

	@Test
	void testFindRootRequirementsByModel_Empty() {
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		List<SystemRequirement> findByElementInSubelement = getRepository().findRootRequirementsByModel(newModel);
		assertNotNull(findByElementInSubelement);
		assertTrue(findByElementInSubelement.isEmpty());
	}

	@Test
	void testFindRootRequirementsByModel_OneRequirement() {

		// create one system requirement
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		SystemRequirement newSystemRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), newModel,
				null, null);
		try {
			getRepository().create(newSystemRequirement);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find it
		List<SystemRequirement> findByElementInSubelement = getRepository().findRootRequirementsByModel(newModel);
		assertNotNull(findByElementInSubelement);
		assertEquals(1, findByElementInSubelement.size());
	}
}
