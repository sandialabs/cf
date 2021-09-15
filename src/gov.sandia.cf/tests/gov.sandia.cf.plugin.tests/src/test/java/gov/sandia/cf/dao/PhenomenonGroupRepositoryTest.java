/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PhenomenonGroupRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PhenomenonGroupRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PhenomenonGroupRepositoryTest
		extends AbstractTestRepository<PhenomenonGroup, Integer, PhenomenonGroupRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PhenomenonGroupRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PhenomenonGroupRepository> getRepositoryClass() {
		return PhenomenonGroupRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PhenomenonGroup> getModelClass() {
		return PhenomenonGroup.class;
	}

	@Override
	PhenomenonGroup getModelFulfilled(PhenomenonGroup model) {
		// populate PIRT
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), newModel);

		fulfillModelStrings(model);
		model.setQoi(newQoI);
		return model;
	}

	@Test
	void testFindByQoiId() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// create Qoi
		QuantityOfInterest createdQoi = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		assertNotNull(createdQoi);
		assertNotNull(createdQoi.getId());

		// create phenomenon group
		PhenomenonGroup group = new PhenomenonGroup();
		fulfillModelStrings(group);
		group.setQoi(createdQoi);
		try {
			PhenomenonGroup createdGroup = getRepository().create(group);
			assertNotNull(createdGroup);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		List<PhenomenonGroup> foundList = getRepository().findByQoiId(createdQoi.getId());
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());

		for (PhenomenonGroup found : foundList) {
			assertNotNull(found);
			assertNotNull(found.getId());
			assertEquals(createdQoi, found.getQoi());
		}
	}

	@Test
	void testFindByQoiId_QoiIdDoesNotExist() {

		// test case
		List<PhenomenonGroup> foundList = getRepository().findByQoiId(-10000);
		assertNotNull(foundList);
		assertTrue(foundList.isEmpty());
	}

	@Test
	void testFindByQoiId_QoiIdNull() {

		// test case
		List<PhenomenonGroup> foundList = getRepository().findByQoiId(null);
		assertNotNull(foundList);
		assertTrue(foundList.isEmpty());
	}

	@Test
	void testFindByQoiIdList_OneQoi() {
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// create Qoi
		QuantityOfInterest createdQoi = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		assertNotNull(createdQoi);
		assertNotNull(createdQoi.getId());

		// create phenomenon group
		PhenomenonGroup group = new PhenomenonGroup();
		fulfillModelStrings(group);
		group.setQoi(createdQoi);
		try {
			PhenomenonGroup createdGroup = getRepository().create(group);
			assertNotNull(createdGroup);
			assertNotNull(createdGroup.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		ArrayList<Integer> qoiIdList = new ArrayList<Integer>();
		qoiIdList.add(createdQoi.getId());
		List<PhenomenonGroup> foundList = getRepository().findByQoiIdList(qoiIdList);
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());

		for (PhenomenonGroup found : foundList) {
			assertNotNull(found);
			assertNotNull(found.getId());
			assertEquals(createdQoi, found.getQoi());
		}
	}

	@Test
	void testFindByQoiIdList_MultipleQoi() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// create Qoi id list
		List<Integer> qoiIdList = new ArrayList<Integer>();
		for (int i = 1; i <= 3; i++) {
			// create Qoi
			QuantityOfInterest createdQoi = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
			assertNotNull(createdQoi);
			assertNotNull(createdQoi.getId());

			// create phenomenon group
			PhenomenonGroup group = new PhenomenonGroup();
			fulfillModelStrings(group);
			group.setQoi(createdQoi);
			try {
				PhenomenonGroup createdGroup = getRepository().create(group);
				assertNotNull(createdGroup);
				assertNotNull(createdGroup.getId());
			} catch (CredibilityException e) {
				fail(e.getMessage());
			}

			// add to qoi id list
			qoiIdList.add(createdQoi.getId());
		}
		assertNotNull(qoiIdList);
		assertFalse(qoiIdList.isEmpty());

		// test case
		List<PhenomenonGroup> foundList = getRepository().findByQoiIdList(qoiIdList);
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());

		for (PhenomenonGroup found : foundList) {
			assertNotNull(found);
			assertNotNull(found.getId());
		}

	}

	@Test
	void testFindByQoiIdList_MultipleQoiIncludingNonExisting() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// create Qoi id list
		List<Integer> qoiIdList = new ArrayList<Integer>();
		for (int i = 1; i <= 3; i++) {

			// create Qoi
			QuantityOfInterest createdQoi = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
			assertNotNull(createdQoi);
			assertNotNull(createdQoi.getId());

			// create phenomenon group
			PhenomenonGroup group = new PhenomenonGroup();
			fulfillModelStrings(group);
			group.setQoi(createdQoi);
			try {
				PhenomenonGroup createdGroup = getRepository().create(group);
				assertNotNull(createdGroup);
				assertNotNull(createdGroup.getId());
			} catch (CredibilityException e) {
				fail(e.getMessage());
			}

			// add to qoi id list
			qoiIdList.add(createdQoi.getId());
		}
		assertNotNull(qoiIdList);
		assertFalse(qoiIdList.isEmpty());

		// add non existing id
		qoiIdList.add(-10000);

		// test case
		List<PhenomenonGroup> foundList = getRepository().findByQoiIdList(qoiIdList);
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());

		for (PhenomenonGroup found : foundList) {
			assertNotNull(found);
			assertNotNull(found.getId());
		}

	}

	@Test
	void testFindByQoiIdList_QoiIdListNull() {

		// test case
		List<PhenomenonGroup> foundList = getRepository().findByQoiIdList(null);
		assertNotNull(foundList);
		assertTrue(foundList.isEmpty());

	}

	@Test
	void testFindByQoiIdList_QoiIdListEmpty() {

		// test case
		List<PhenomenonGroup> foundList = getRepository().findByQoiIdList(new ArrayList<Integer>());
		assertNotNull(foundList);
		assertTrue(foundList.isEmpty());
	}

	@Test
	void testValidationOnCreation() {
		PhenomenonGroup group = new PhenomenonGroup();
		PhenomenonGroupRepository repository = getRepository();
		try {
			repository.create(group);
			fail("Expected an IndexOutOfBoundsException to be thrown"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertNotNull(e.getCause());
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertFalse(((ConstraintViolationException) e.getCause()).getConstraintViolations().isEmpty());
		}
	}

	@Test
	void testValidationOnUpdate() {

		PhenomenonGroupRepository repository = getRepository();

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// create Qoi
		QuantityOfInterest createdQoi = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		assertNotNull(createdQoi);
		assertNotNull(createdQoi.getId());

		// create phenomenon group
		PhenomenonGroup group = new PhenomenonGroup();
		group.setIdLabel("ID1"); //$NON-NLS-1$
		group.setName("MyName"); //$NON-NLS-1$
		group.setQoi(createdQoi);
		PhenomenonGroup createdGroup = null;
		try {
			createdGroup = getRepository().create(group);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(createdGroup);
		assertNotNull(createdGroup.getId());

		// update
		createdGroup.setIdLabel(""); //$NON-NLS-1$
		createdGroup.setName(""); //$NON-NLS-1$
		try {
			repository.update(createdGroup);
			fail("Expected an IndexOutOfBoundsException to be thrown"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertFalse(((ConstraintViolationException) e.getCause()).getConstraintViolations().isEmpty());
		}

		// rollbacked
		PhenomenonGroup found = getRepository().findById(createdGroup.getId());
		assertEquals("ID1", found.getIdLabel()); //$NON-NLS-1$
		assertEquals("MyName", found.getName()); //$NON-NLS-1$

	}
}
