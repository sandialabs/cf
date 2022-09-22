/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.QuantityOfInterestRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.DateTools;

/**
 * JUnit class to test the QuantityOfInterestRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class QuantityOfInterestRepositoryTest
		extends AbstractTestRepository<QuantityOfInterest, Integer, QuantityOfInterestRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(QuantityOfInterestRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QuantityOfInterestRepository> getRepositoryClass() {
		return QuantityOfInterestRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<QuantityOfInterest> getModelClass() {
		return QuantityOfInterest.class;
	}

	@Override
	QuantityOfInterest getModelFulfilled(QuantityOfInterest model) {
		// populate PIRT
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		fulfillModelStrings(model);
		model.setCreationDate(new Date());
		model.setModel(newModel);
		return model;
	}

	@Test
	void testFindByModelNotTagged() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);
		assertNotNull(createdModel.getId());

		// create Qoi
		QuantityOfInterest createdQoi = TestEntityFactory.getNewQoI(getDaoManager(), createdModel);
		assertNotNull(createdQoi);

		// create tag
		QuantityOfInterest tag = createdQoi.copy();
		tag.setTag("HASHTAG"); //$NON-NLS-1$
		tag.setTagDate(DateTools.getCurrentDate());
		tag.setParent(createdQoi);
		QuantityOfInterest createdTag = null;
		try {
			createdTag = getRepository().create(tag);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		List<QuantityOfInterest> foundList = getRepository().findByModelNotTagged(createdModel);
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());
		assertEquals(1, foundList.size());

		QuantityOfInterest found = foundList.get(0);
		assertNotNull(found);
		assertNotNull(found.getId());
		assertEquals(createdQoi, found);

		assertTrue(foundList.contains(createdQoi));
		assertFalse(foundList.contains(createdTag));
	}

	@Test
	void testFindByModelNotTagged_modelNull() {

		// test case
		List<QuantityOfInterest> foundList = getRepository().findByModelNotTagged(null);
		assertNotNull(foundList);
		assertTrue(foundList.isEmpty());
	}

	@Test
	void testFindByModel() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);
		assertNotNull(createdModel.getId());

		// create Qoi
		QuantityOfInterest createdQoi = TestEntityFactory.getNewQoI(getDaoManager(), createdModel);
		assertNotNull(createdQoi);

		// test case
		List<QuantityOfInterest> foundList = getRepository().findByModel(createdModel);
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());
		assertEquals(1, foundList.size());

		QuantityOfInterest found = foundList.get(0);
		assertNotNull(found);
		assertEquals(createdQoi, found);
		assertNotNull(found.getId());
		assertEquals(createdModel, found.getModel());
	}

	@Test
	void testFindByModel_MultipleQoi() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);
		assertNotNull(createdModel.getId());

		// create Qoi 1
		QuantityOfInterest createdQoi = TestEntityFactory.getNewQoI(getDaoManager(), createdModel);
		assertNotNull(createdQoi);

		// create Qoi 2
		QuantityOfInterest createdQoi2 = TestEntityFactory.getNewQoI(getDaoManager(), createdModel);
		assertNotNull(createdQoi2);

		// create Qoi 3
		QuantityOfInterest createdQoi3 = TestEntityFactory.getNewQoI(getDaoManager(), createdModel);
		assertNotNull(createdQoi3);

		// test case
		List<QuantityOfInterest> foundList = getRepository().findByModel(createdModel);
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());
		assertEquals(3, foundList.size());

		assertTrue(foundList.contains(createdQoi));
		assertTrue(foundList.contains(createdQoi2));
		assertTrue(foundList.contains(createdQoi3));
	}

	@Test
	void testFindByModel_WithTag() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);
		assertNotNull(createdModel.getId());

		// create Qoi
		QuantityOfInterest createdQoi = TestEntityFactory.getNewQoI(getDaoManager(), createdModel);
		assertNotNull(createdQoi);

		// create tag
		QuantityOfInterest tag = createdQoi.copy();
		tag.setTag("HASHTAG"); //$NON-NLS-1$
		tag.setTagDate(DateTools.getCurrentDate());
		tag.setParent(createdQoi);
		QuantityOfInterest createdTag = null;
		try {
			createdTag = getRepository().create(tag);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		List<QuantityOfInterest> foundList = getRepository().findByModel(createdModel);
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());
		assertEquals(2, foundList.size());

		assertTrue(foundList.contains(createdQoi));
		assertTrue(foundList.contains(createdTag));
	}

	@Test
	void testFindByModel_ModelNull() {

		// test case
		List<QuantityOfInterest> foundList = getRepository().findByModel(null);
		assertNotNull(foundList);
		assertTrue(foundList.isEmpty());
	}

	@Test
	void testFindRootQuantityOfInterest() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create Qoi
		QuantityOfInterest createdQoi = TestEntityFactory.getNewQoI(getDaoManager(), createdModel);
		assertNotNull(createdQoi);

		// create Qoi2
		QuantityOfInterest createdQoi2 = TestEntityFactory.getNewQoI(getDaoManager(), createdModel);
		assertNotNull(createdQoi2);

		// create tag1
		QuantityOfInterest tag = createdQoi.copy();
		tag.setTag("HASHTAG"); //$NON-NLS-1$
		tag.setTagDate(DateTools.getCurrentDate());
		tag.setParent(createdQoi);
		QuantityOfInterest createdTag1 = null;
		try {
			createdTag1 = getRepository().create(tag);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// create tag2
		QuantityOfInterest tag2 = createdQoi.copy();
		tag2.setTag("HASHTAG"); //$NON-NLS-1$
		tag2.setTagDate(DateTools.getCurrentDate());
		tag2.setParent(createdQoi);
		QuantityOfInterest createdTag2 = null;
		try {
			createdTag2 = getRepository().create(tag2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		List<QuantityOfInterest> foundList = getRepository().findRootQuantityOfInterest(createdModel);
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());
		assertEquals(2, foundList.size());

		assertTrue(foundList.contains(createdQoi));
		assertTrue(foundList.contains(createdQoi2));
		assertFalse(foundList.contains(createdTag1));
		assertFalse(foundList.contains(createdTag2));
	}

	@Test
	void testFindRootQuantityOfInterest_modelNull() {

		// test case
		List<QuantityOfInterest> foundList = getRepository().findRootQuantityOfInterest(null);
		assertNotNull(foundList);
		assertTrue(foundList.isEmpty());
	}

	@Test
	void testFindQoiIdByModelId() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);
		assertNotNull(createdModel.getId());

		// create Qoi
		QuantityOfInterest createdQoi = TestEntityFactory.getNewQoI(getDaoManager(), createdModel);
		assertNotNull(createdQoi);

		// test case
		List<Integer> foundList = getRepository().findQoiIdByModelId(createdModel.getId());
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());

		for (Integer found : foundList) {
			assertNotNull(found);
		}
	}

	@Test
	void testFindQoiIdByModelId_ModelIdNull() {

		// test case
		List<Integer> foundList = getRepository().findQoiIdByModelId(null);
		assertNotNull(foundList);
		assertTrue(foundList.isEmpty());
	}

}
