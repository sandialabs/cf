/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IPCMMPlanningTableItemRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningTableValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * JUnit test class for the PCMM Application Planning Controller
 * 
 * @author Didier Verstraete.
 *
 */
class PCMMPlanningAppPlanningPlanningTableTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMPlanningAppPlanningPlanningTableTest.class);
	/* ************ getPlanningTableItemBy ************* */

	@Test
	void testgetPlanningTableItemBy_Working() {

		// construct data
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), null, null, null, null);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), null, null, null, null);

		// test
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericValue.Filter.VALUE, NullParameter.NOT_NULL);
		List<PCMMPlanningTableItem> planningFieldsBy = getPCMMPlanningApp().getPlanningTableItemBy(filters);
		assertNotNull(planningFieldsBy);
		assertEquals(2, planningFieldsBy.size());
	}

	/* ************ getPlanningTableItemByElement ************* */

	@Test
	void testgetPlanningTableItemByElement_PCMMModeDefault_NotTagged() {

		// construct data
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				null);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				null);
		// the following one is tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				TestEntityFactory.getNewTag(getDaoManager(), null));

		// test
		Tag tag = null;
		List<PCMMPlanningTableItem> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningTableItemByElement(newPCMMSubelement.getElement(), PCMMMode.DEFAULT, tag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	@Test
	void testgetPlanningTableItemByElement_PCMMModeDefault_Tagged() {

		// construct data
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				newTag);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				newTag);
		// the following one is not tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMSubelement, null,
				null);

		// test
		List<PCMMPlanningTableItem> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningTableItemByElement(newPCMMSubelement.getElement(), PCMMMode.DEFAULT, newTag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	@Test
	void testgetPlanningTableItemByElement_PCMMModeSimplified_NotTagged() {

		// construct data
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null,
				null);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null,
				null);
		// the following one is tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null,
				TestEntityFactory.getNewTag(getDaoManager(), null));

		// test
		Tag tag = null;
		List<PCMMPlanningTableItem> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningTableItemByElement(newPCMMElement, PCMMMode.SIMPLIFIED, tag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	@Test
	void testgetPlanningTableItemByElement_PCMMModeSimplified_Tagged() {

		// construct data
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null,
				newTag);
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null,
				newTag);
		// the following one is not tagged so it should not be retrieved
		TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(), newPCMMPlanningParam, newPCMMElement, null,
				null);

		// test
		List<PCMMPlanningTableItem> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningTableItemByElement(newPCMMElement, PCMMMode.SIMPLIFIED, newTag);

		assertNotNull(planningQuestionsByElement);
		assertEquals(2, planningQuestionsByElement.size());
	}

	/* ************ getPlanningTableItemByElement ************* */

	@Test
	void test_getPlanningTableItemByElement_PCMMModeSimplified_ListTag() {

		// construct data
		Tag tag1 = TestEntityFactory.getNewTag(getDaoManager(), null);
		Tag tag2 = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		PCMMPlanningTableItem item1 = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				newPCMMPlanningParam, newPCMMElement, null, null);
		PCMMPlanningTableItem item2 = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				newPCMMPlanningParam, newPCMMElement, null, tag1);
		PCMMPlanningTableItem item3 = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				newPCMMPlanningParam, newPCMMElement, null, tag1);
		// the following one is not to retrieve
		PCMMPlanningTableItem item4 = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				newPCMMPlanningParam, newPCMMElement, null, tag2);

		// test
		List<PCMMPlanningTableItem> planningQuestionsByElement = getPCMMPlanningApp()
				.getPlanningTableItemByElement(newPCMMElement, PCMMMode.SIMPLIFIED, Arrays.asList(null, tag1));

		assertNotNull(planningQuestionsByElement);
		assertEquals(3, planningQuestionsByElement.size());

		assertTrue(planningQuestionsByElement.contains(item1));
		assertTrue(planningQuestionsByElement.contains(item2));
		assertTrue(planningQuestionsByElement.contains(item3));
		assertFalse(planningQuestionsByElement.contains(item4));
	}

	@Test
	void test_getPlanningTableItemByElement_PCMMModeDefault_ListTag() {

		// construct data
		Tag tag1 = TestEntityFactory.getNewTag(getDaoManager(), null);
		Tag tag2 = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		PCMMPlanningTableItem item1 = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				newPCMMPlanningParam, newPCMMSubelement, null, null);
		PCMMPlanningTableItem item2 = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				newPCMMPlanningParam, newPCMMSubelement, null, tag1);
		PCMMPlanningTableItem item3 = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				newPCMMPlanningParam, newPCMMSubelement, null, tag1);
		// the following one is not to retrieve
		PCMMPlanningTableItem item4 = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				newPCMMPlanningParam, newPCMMSubelement, null, tag2);

		// test
		List<PCMMPlanningTableItem> planningQuestionsByElement = getPCMMPlanningApp().getPlanningTableItemByElement(
				newPCMMSubelement.getElement(), PCMMMode.DEFAULT, Arrays.asList(null, tag1));

		assertNotNull(planningQuestionsByElement);
		assertEquals(3, planningQuestionsByElement.size());

		assertTrue(planningQuestionsByElement.contains(item1));
		assertTrue(planningQuestionsByElement.contains(item2));
		assertTrue(planningQuestionsByElement.contains(item3));
		assertFalse(planningQuestionsByElement.contains(item4));
	}

	/* ************ addPlanningTableItem ************* */

	@Test
	void testaddPlanningTableItem_Working() throws CredibilityException {

		// construct data
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		PCMMPlanningTableItem value = new PCMMPlanningTableItem();
		value.setDateCreation(new Date());
		value.setParameter(newPCMMPlanningParam);
		value.setSubelement(newPCMMSubelement);
		value.setUserCreation(newUser);
		value.setValue("VALUE"); //$NON-NLS-1$

		// test
		PCMMPlanningTableItem addPlanningTableItem = getPCMMPlanningApp().addPlanningTableItem(value);

		assertNotNull(addPlanningTableItem);
		assertNotNull(addPlanningTableItem.getId());
	}

	@Test
	void testaddPlanningTableItem_PlanningValueNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().addPlanningTableItem(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDTABLEITEM_NULL), e.getMessage());
	}

	/* ************ refreshPlanningTableItem ************* */

	@Test
	void testrefreshPlanningTableItem_Working() throws CredibilityException {

		PCMMPlanningTableItem newPCMMPlanningTableItem = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				null, null, null, null);

		// test
		getPCMMPlanningApp().refreshPlanningTableItem(newPCMMPlanningTableItem);

		assertNotNull(newPCMMPlanningTableItem);
		assertNotNull(newPCMMPlanningTableItem.getId());
	}

	@Test
	void testrefreshPlanningTableItem_itemNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().refreshPlanningTableItem(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_REFRESHTABLEITEM_NULL), e.getMessage());
	}

	/* ************ deletePlanningTableItem ************* */

	@Test
	void testdeletePlanningTableItem_Working() throws CredibilityException {

		PCMMPlanningTableItem newPCMMPlanningTableItem = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				null, null, null, null);
		Integer id = newPCMMPlanningTableItem.getId();

		// test
		getPCMMPlanningApp().deletePlanningTableItem(newPCMMPlanningTableItem);

		PCMMPlanningTableItem findById = getDaoManager().getRepository(IPCMMPlanningTableItemRepository.class)
				.findById(id);
		assertNull(findById);
	}

	@Test
	void testdeletePlanningTableItem_itemNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().deletePlanningTableItem(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETETABLEITEM_NULL), e.getMessage());
	}

	/* ************ addPlanningTableValue ************* */

	@Test
	void testaddPlanningTableValue_Working() throws CredibilityException {

		// construct data
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		PCMMPlanningTableItem newPCMMPlanningTableItem = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				null, null, null, null);

		PCMMPlanningTableValue value = new PCMMPlanningTableValue();
		value.setDateCreation(new Date());
		value.setParameter(newPCMMPlanningParam);
		value.setItem(newPCMMPlanningTableItem);
		value.setUserCreation(newUser);
		value.setValue("VALUE"); //$NON-NLS-1$

		// test
		PCMMPlanningTableValue addPlanningTableValue = getPCMMPlanningApp().addPlanningTableValue(value);

		assertNotNull(addPlanningTableValue);
		assertNotNull(addPlanningTableValue.getId());
	}

	@Test
	void testaddPlanningTableValue_PlanningValueNull() {

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().addPlanningTableValue(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDTABLEVALUE_NULL), e.getMessage());
	}

	/* ************ updatePlanningTableValue ************* */

	@Test
	void testupdatePlanningTableValue_Working() throws CredibilityException {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		PCMMPlanningTableValue newPCMMPlanningTableValue = TestEntityFactory
				.getNewPCMMPlanningTableValue(getDaoManager(), null, null, null);

		newPCMMPlanningTableValue.setValue("NEW VALUE"); //$NON-NLS-1$

		// test
		PCMMPlanningTableValue updatePlanningTableValue = getPCMMPlanningApp()
				.updatePlanningTableValue(newPCMMPlanningTableValue, newUser);

		assertNotNull(updatePlanningTableValue);
		assertNotNull(updatePlanningTableValue.getId());
		assertEquals("NEW VALUE", updatePlanningTableValue.getValue()); //$NON-NLS-1$
	}

	@Test
	void testupdatePlanningTableValue_PlanningValueNull() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().updatePlanningTableValue(null, newUser);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATETABLEVALUE_NULL), e.getMessage());
	}

	@Test
	void testupdatePlanningTableValue_IdNull() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().updatePlanningTableValue(new PCMMPlanningTableValue(), newUser);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATETABLEVALUE_IDNULL), e.getMessage());
	}
}
