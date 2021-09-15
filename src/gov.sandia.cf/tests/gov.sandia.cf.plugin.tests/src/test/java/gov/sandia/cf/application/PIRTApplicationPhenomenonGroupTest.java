/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IPhenomenonGroupRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tests.TestTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the PIRT Application Controller
 */
@RunWith(JUnitPlatform.class)
class PIRTApplicationPhenomenonGroupTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PIRTApplicationPhenomenonGroupTest.class);

	@Test
	void testPhenomenonGroupCRUDWorking() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create QoI
		QuantityOfInterest qoi = TestEntityFactory.getNewQoI(getDaoManager(), createdModel);
		assertNotNull(qoi);
		try {

			// *******************
			// Add PhenomenonGroup
			// *******************
			// Create
			PhenomenonGroup group = new PhenomenonGroup();
			group.setName("My_Group"); //$NON-NLS-1$
			group.setIdLabel("My_Id_Label_1"); //$NON-NLS-1$
			group.setQoi(qoi);
			PhenomenonGroup createdGroup = getPIRTApp().addPhenomenonGroup(group);

			// Create Phenomenon
			Phenomenon phenomenon = new Phenomenon();
			phenomenon.setName("My_Phenomenon"); //$NON-NLS-1$
			phenomenon.setIdLabel("My_Id_Label"); //$NON-NLS-1$
			phenomenon.setPhenomenonGroup(group);
			phenomenon = getPIRTApp().addPhenomenon(phenomenon);
			assertNotNull(phenomenon);
			assertEquals("My_Phenomenon", phenomenon.getName()); //$NON-NLS-1$
			assertEquals("My_Id_Label", phenomenon.getIdLabel()); //$NON-NLS-1$
			assertEquals(group, phenomenon.getPhenomenonGroup());

			getDaoManager().getRepository(IPhenomenonGroupRepository.class).refresh(group);

			// Test create
			assertNotNull(createdGroup);
			assertEquals(createdGroup.getName(), group.getName());

			// ********************************
			// Get all
			// ********************************
			List<PhenomenonGroup> list = getPIRTApp().getPhenomenonGroups();
			assertEquals(1, list.size());

			// ********************************
			// Update PhenomenonGroup
			// ********************************
			group.setName("My_Goup_Updated"); //$NON-NLS-1$
			PhenomenonGroup updatedGroup = getPIRTApp().updatePhenomenonGroup(group);
			assertNotNull(updatedGroup);
			assertEquals(updatedGroup.getName(), group.getName());

			// ********************************
			// Delete
			// ********************************
			getPIRTApp().deletePhenomenonGroup(group);
			list = getPIRTApp().getPhenomenonGroups();
			assertTrue(list.isEmpty());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testAddPhenomenonGroup_Error_Null() {
		// *************************
		// With PhenomenonGroup null
		// *************************
		try {
			getPIRTApp().addPhenomenonGroup(null);
			fail("Adding a phenomenon group null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_ADDPHENGROUP_GROUPNULL), e.getMessage());
		}
	}

	@Test
	void testAddPhenomenonGroup_Error_QoINull() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null).copy();

		// *****************************
		// With PhenomenonGroup qoi null
		// *****************************
		group.setQoi(null);
		try {
			getPIRTApp().addPhenomenonGroup(group);
			fail("Adding a phenomenon group with no quantity of interest must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENONGROUP_QOI_NULL));
		}
	}

	@Test
	void testAddPhenomenonGroup_Error_IdLabelNull() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null).copy();

		// **********************************
		// With PhenomenonGroup id label null
		// **********************************
		group.setIdLabel(null);
		try {
			getPIRTApp().addPhenomenonGroup(group);
			fail("Adding a phenomenon group with id label null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENONGROUP_IDLABEL_BLANK));
		}
	}

	@Test
	void testAddPhenomenonGroup_Error_IdLabelEmpty() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null).copy();

		// ***********************************
		// With PhenomenonGroup id label empty
		// ***********************************
		group.setIdLabel(""); //$NON-NLS-1$
		try {
			getPIRTApp().addPhenomenonGroup(group);
			fail("Adding a phenomenon group with id label empty must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENONGROUP_IDLABEL_BLANK));
		}
	}

	@Test
	void testAddPhenomenonGroup_Error_NameNull() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null).copy();

		// **********************************
		// With PhenomenonGroup name null
		// **********************************
		group.setName(null);
		try {
			getPIRTApp().addPhenomenonGroup(group);
			fail("Adding a phenomenon group with name null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENONGROUP_NAME_BLANK));
		}
	}

	@Test
	void testAddPhenomenonGroup_Error_NameEmpty() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null).copy();

		// ***********************************
		// With PhenomenonGroup name empty
		// ***********************************
		group.setName(""); //$NON-NLS-1$
		try {
			getPIRTApp().addPhenomenonGroup(group);
			fail("Adding a phenomenon group with name empty must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENONGROUP_NAME_BLANK));
		}
	}

	@Test
	void testUpdatePhenomenonGroup_Error_Null() {
		// *************************
		// With PhenomenonGroup null
		// *************************
		try {
			getPIRTApp().updatePhenomenonGroup(null);
			fail("Updating a phenomenon group null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_UPDATEPHENGROUP_GROUPNULL), e.getMessage());
		}
	}

	@Test
	void testUpdatePhenomenonGroup_Error_IdNull() {

		// *****************************
		// With PhenomenonGroup id null
		// *****************************
		try {
			getPIRTApp().updatePhenomenonGroup(new PhenomenonGroup());
			fail("Updating a phenomenon group with no id must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_UPDATEPHENGROUP_IDNULL), e.getMessage());
		}
	}

	@Test
	void testUpdatePhenomenonGroup_Error_QoINull() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null);
		assertNotNull(group);

		// *****************************
		// With PhenomenonGroup qoi null
		// *****************************
		group.setQoi(null);
		try {
			getPIRTApp().updatePhenomenonGroup(group);
			fail("Updating a phenomenon group with no quantity of interest must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENONGROUP_QOI_NULL));
		}
	}

	@Test
	void testUpdatePhenomenonGroup_Error_IdLabelNull() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null);
		assertNotNull(group);

		// **********************************
		// With PhenomenonGroup id label null
		// **********************************
		group.setIdLabel(null);
		try {
			getPIRTApp().updatePhenomenonGroup(group);
			fail("Updating a phenomenon group with id label null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENONGROUP_IDLABEL_BLANK));
		}
	}

	@Test
	void testUpdatePhenomenonGroup_Error_IdLabelEmpty() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null);
		assertNotNull(group);

		// ***********************************
		// With PhenomenonGroup id label empty
		// ***********************************
		group.setIdLabel(""); //$NON-NLS-1$
		try {
			getPIRTApp().updatePhenomenonGroup(group);
			fail("Updating a phenomenon group with id label empty must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENONGROUP_IDLABEL_BLANK));
		}
	}

	@Test
	void testUpdatePhenomenonGroup_Error_NameNull() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null);
		assertNotNull(group);

		// **********************************
		// With PhenomenonGroup name null
		// **********************************
		group.setName(null);
		try {
			getPIRTApp().updatePhenomenonGroup(group);
			fail("Updating a phenomenon group with name null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENONGROUP_NAME_BLANK));
		}
	}

	@Test
	void testUpdatePhenomenonGroup_Error_NameEmpty() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null);
		assertNotNull(group);

		// ***********************************
		// With PhenomenonGroup name empty
		// ***********************************
		group.setName(""); //$NON-NLS-1$
		try {
			getPIRTApp().updatePhenomenonGroup(group);
			fail("Updating a phenomenon group with name empty must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENONGROUP_NAME_BLANK));
		}
	}

	@Test
	void testDeletePhenomenonGroup_Error_Null() {

		// *************************
		// With PhenomenonGroup null
		// *************************
		try {
			getPIRTApp().deletePhenomenonGroup(null);
			fail("Delete a phenomenon group null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_DELETEPHENGROUP_GROUPNULL), e.getMessage());
		}
	}

	@Test
	void testDeletePhenomenonGroup_Error_IdNull() {

		// *****************************
		// With PhenomenonGroup id null
		// *****************************
		try {
			getPIRTApp().deletePhenomenonGroup(new PhenomenonGroup());
			fail("Delete a phenomenon group with no id must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_DELETEPHENGROUP_IDNULL), e.getMessage());
		}
	}

	@Test
	void testDeletePhenomenonGroup_Error_PhenomenonListNull() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null);
		assertNotNull(group);

		// ****************************************
		// With PhenomenonGroup PhenomenonList null
		// ****************************************
		group.setPhenomenonList(null);
		try {
			getPIRTApp().deletePhenomenonGroup(group);
		} catch (CredibilityException e) {
			fail("Delete a phenomenon group with PhenomenonList null is possible."); //$NON-NLS-1$
		}
	}

	@Test
	void testDeletePhenomenonGroup_Errors() {

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null);
		assertNotNull(group);

		// ***************************************************
		// With PhenomenonGroup PhenomenonList containing null
		// ***************************************************
		ArrayList<Phenomenon> list = new ArrayList<Phenomenon>();
		list.add(null);
		group.setPhenomenonList(list);
		try {
			getPIRTApp().deletePhenomenonGroup(group);
		} catch (CredibilityException e) {
			fail("Delete a phenomenon group with PhenomenonList containing null will be possible."); //$NON-NLS-1$
		}
	}

	@Test
	void test_reorderPhenomenonGroups() {

		// construct data
		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), null);
		PhenomenonGroup group1 = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI);
		group1.setIdLabel("A"); //$NON-NLS-1$
		try {
			getPIRTApp().updatePhenomenonGroup(group1);
			getPIRTApp().refresh(group1);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		TestEntityFactory.getNewPhenomenon(getDaoManager(), group1);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), group1);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), group1);
		PhenomenonGroup group2 = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI);
		group2.setIdLabel("B"); //$NON-NLS-1$
		try {
			getPIRTApp().updatePhenomenonGroup(group2);
			getPIRTApp().refresh(group2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		TestEntityFactory.getNewPhenomenon(getDaoManager(), group2);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), group2);
		PhenomenonGroup group3 = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI);
		group3.setIdLabel("C"); //$NON-NLS-1$
		try {
			getPIRTApp().updatePhenomenonGroup(group3);
			getPIRTApp().refresh(group3);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		TestEntityFactory.getNewPhenomenon(getDaoManager(), group3);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), group3);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), group3);
		PhenomenonGroup group4 = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI);
		group4.setIdLabel("D"); //$NON-NLS-1$
		try {
			getPIRTApp().updatePhenomenonGroup(group4);
			getPIRTApp().refresh(group4);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		TestEntityFactory.getNewPhenomenon(getDaoManager(), group4);
		PhenomenonGroup group5 = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI);
		group5.setIdLabel("E"); //$NON-NLS-1$
		try {
			getPIRTApp().updatePhenomenonGroup(group5);
			getPIRTApp().refresh(group5);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		TestEntityFactory.getNewPhenomenon(getDaoManager(), group5);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), group5);

		// process
		try {
			getPIRTApp().reorderPhenomenonGroups(group3, 4);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		List<PhenomenonGroup> phenomenonGroups = getPIRTApp().getPhenomenonGroups();
		phenomenonGroups.sort(Comparator.comparing(PhenomenonGroup::getIdLabel));

		// test
		// group order
		assertEquals(group1, phenomenonGroups.get(0));
		assertEquals(group2, phenomenonGroups.get(1));
		assertEquals(group4, phenomenonGroups.get(2));
		assertEquals(group5, phenomenonGroups.get(3));
		assertEquals(group3, phenomenonGroups.get(4));

		// group id label
		assertEquals("A", group1.getIdLabel()); //$NON-NLS-1$
		assertEquals("B", group2.getIdLabel()); //$NON-NLS-1$
		assertEquals("E", group3.getIdLabel()); //$NON-NLS-1$
		assertEquals("C", group4.getIdLabel()); //$NON-NLS-1$
		assertEquals("D", group5.getIdLabel()); //$NON-NLS-1$

		// phenomenon labels
		assertEquals("A", group1.getPhenomenonList().get(0).getIdLabel().substring(0, 1)); //$NON-NLS-1$
		assertEquals("A", group1.getPhenomenonList().get(1).getIdLabel().substring(0, 1)); //$NON-NLS-1$
		assertEquals("A", group1.getPhenomenonList().get(2).getIdLabel().substring(0, 1)); //$NON-NLS-1$

		assertEquals("B", group2.getPhenomenonList().get(0).getIdLabel().substring(0, 1)); //$NON-NLS-1$
		assertEquals("B", group2.getPhenomenonList().get(1).getIdLabel().substring(0, 1)); //$NON-NLS-1$

		assertEquals("E", group3.getPhenomenonList().get(0).getIdLabel().substring(0, 1)); //$NON-NLS-1$
		assertEquals("E", group3.getPhenomenonList().get(1).getIdLabel().substring(0, 1)); //$NON-NLS-1$
		assertEquals("E", group3.getPhenomenonList().get(2).getIdLabel().substring(0, 1)); //$NON-NLS-1$

		assertEquals("C", group4.getPhenomenonList().get(0).getIdLabel().substring(0, 1)); //$NON-NLS-1$

		assertEquals("D", group5.getPhenomenonList().get(0).getIdLabel().substring(0, 1)); //$NON-NLS-1$
		assertEquals("D", group5.getPhenomenonList().get(1).getIdLabel().substring(0, 1)); //$NON-NLS-1$
	}
}
