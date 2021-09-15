/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMPlanningTableItemRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PCMMPlanningTableItemRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PCMMPlanningTableItemRepositoryTest
		extends AbstractTestRepository<PCMMPlanningTableItem, Integer, PCMMPlanningTableItemRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningTableItemRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningTableItemRepository> getRepositoryClass() {
		return PCMMPlanningTableItemRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMPlanningTableItem> getModelClass() {
		return PCMMPlanningTableItem.class;
	}

	@Override
	PCMMPlanningTableItem getModelFulfilled(PCMMPlanningTableItem model) {
		fulfillModelStrings(model);
		model.setDateCreation(new Date());
		model.setParameter(TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null));
		model.setUserCreation(TestEntityFactory.getNewUser(getDaoManager()));
		return model;
	}

	/* ***************** findByElementInSubelement ******************* */

	@Test
	void testfindByElementInSubelement_EmptyList() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		// the following one is not associated to the planning question
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), null, null, null, null);

		// test
		List<PCMMPlanningTableItem> findByElementInSubelement = getRepository()
				.findByElementInSubelement(newPCMMElement, null);
		assertNotNull(findByElementInSubelement);
		assertTrue(findByElementInSubelement.isEmpty());
	}

	@Test
	void testfindByElementInSubelement_FulfilledList_NoTag() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), newModel);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), newPCMMElement);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), newModel);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				null);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				null);
		// the following one is not associated to the subelement
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, null, null, null);
		// the following one is associated to the subelement but is tagged
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				TestEntityFactory.getNewTag(getDaoManager(), null));

		// test
		List<PCMMPlanningTableItem> findByElementInSubelement = getRepository()
				.findByElementInSubelement(newPCMMElement, null);
		assertNotNull(findByElementInSubelement);
		assertEquals(2, findByElementInSubelement.size());
	}

	@Test
	void testfindByElementInSubelement_FulfilledList_WithTag() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), newModel);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), newPCMMElement);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), newModel);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				newTag);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				newTag);
		// the following one is not associated to the subelement
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, null, null, newTag);
		// the following one is associated to the subelement but is not tagged
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				null);

		// test
		List<PCMMPlanningTableItem> findByElementInSubelement = getRepository()
				.findByElementInSubelement(newPCMMElement, newTag);
		assertNotNull(findByElementInSubelement);
		assertEquals(2, findByElementInSubelement.size());
	}

	@Test
	void testfindByElementInSubelement_ElementNull() {

		// test
		List<PCMMPlanningTableItem> findByElementInSubelement = getRepository().findByElementInSubelement(null, null);
		assertNotNull(findByElementInSubelement);
		assertTrue(findByElementInSubelement.isEmpty());
	}
}