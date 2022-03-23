/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IPhenomenonRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tests.TestTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Didier Verstraete
 *
 *         JUnit test class for the PIRT Application Controller
 */
@RunWith(JUnitPlatform.class)
class PIRTApplicationPhenomenonTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PIRTApplicationPhenomenonTest.class);

	@Test
	void testPhenomenonCRUDWorking() {

		// ********************************
		// test create with empty content
		// ********************************
		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), null);
		assertNotNull(group);

		// new
		String name = "NAME"; //$NON-NLS-1$
		String idLabel = "TYPE"; //$NON-NLS-1$
		String importance = "VALUE"; //$NON-NLS-1$

		Phenomenon newPhenomenon = new Phenomenon();
		newPhenomenon.setName(name);
		newPhenomenon.setIdLabel(idLabel);
		newPhenomenon.setImportance(importance);
		newPhenomenon.setPhenomenonGroup(group);

		try {
			// create
			Phenomenon created = getPIRTApp().addPhenomenon(newPhenomenon);
			assertNotNull(newPhenomenon.getId());
			assertEquals(name, created.getName());
			assertEquals(idLabel, created.getIdLabel());
			assertEquals(importance, created.getImportance());
			assertEquals(group, created.getPhenomenonGroup());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ********************************
		// test select
		// ********************************
		List<Phenomenon> list = getPIRTApp().getPhenomena();
		assertEquals(1, list.size());

		// ********************************
		// test update
		// ********************************
		String newName = "NEWNAME"; //$NON-NLS-1$
		String newidLabel = "NEWTYPE"; //$NON-NLS-1$
		String newImportance = "NEWVALUE"; //$NON-NLS-1$

		// get one
		Phenomenon getted = list.get(0);
		getted.setName(newName);
		getted.setIdLabel(newidLabel);
		getted.setImportance(newImportance);

		try {
			// update
			Phenomenon updated = getPIRTApp().updatePhenomenon(getted);
			assertNotNull(updated);
			assertEquals(getted.getId(), updated.getId());
			assertEquals(newName, updated.getName());
			assertEquals(newidLabel, updated.getIdLabel());
			assertEquals(newImportance, updated.getImportance());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ********************************
		// test delete
		// ********************************
		list = getPIRTApp().getPhenomena();
		assertEquals(1, list.size());
		getted = list.get(0);

		try {
			// delete
			getPIRTApp().deletePhenomenon(getted);

			list = getPIRTApp().getPhenomena();
			assertTrue(list.isEmpty());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

	}

	@Test
	void testDeletePhenomenon_WithCriterion() {

		// create phenomenon
		Phenomenon newPhenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), null);
		assertNotNull(newPhenomenon);

		// create criterion
		Criterion criterion = TestEntityFactory.getNewCriterion(getDaoManager(), newPhenomenon);
		assertNotNull(criterion);

		// check phenomenon <-> criterion affectation
		getDaoManager().getRepository(IPhenomenonRepository.class).refresh(newPhenomenon);
		assertNotNull(newPhenomenon.getCriterionList());
		assertFalse(newPhenomenon.getCriterionList().isEmpty());

		try {

			// ********************************
			// test delete
			// ********************************
			getPIRTApp().deletePhenomenon(newPhenomenon);

			List<Phenomenon> list = getPIRTApp().getPhenomena();
			assertTrue(list.isEmpty());

			List<Criterion> listCriterion2 = getPIRTApp().getCriterion();
			assertTrue(listCriterion2.isEmpty());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testAddPhenomenon_ErrorPhenomenonNull() {
		try {
			getPIRTApp().addPhenomenon(null);
			fail("Adding a phenomenon null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_ADDPHENOMENON_PHENOMENONNULL), e.getMessage());
		}
	}

	@Test
	void testAddPhenomenon_ErrorPhenomenonGroupNull() {

		// create phenomenon
		Phenomenon phenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), null).copy();

		phenomenon.setPhenomenonGroup(null);
		try {
			getPIRTApp().addPhenomenon(phenomenon);
			fail("Adding a phenomenon with a phenomenon group null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENON_GROUP_NULL));
		}
	}

	@Test
	void testUpdatePhenomenon_ErrorPhenomenonNull() {
		try {
			getPIRTApp().updatePhenomenon(null);
			fail("Updating a phenomenon null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_UPDATEPHENOMENON_PHENOMENONNULL), e.getMessage());
		}
	}

	@Test
	void testUpdatePhenomenon_ErrorIdNull() {
		try {
			getPIRTApp().updatePhenomenon(new Phenomenon());
			fail("Updating a phenomenon with an id null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_UPDATEPHENOMENON_IDNULL), e.getMessage());
		}
	}

	@Test
	void testUpdatePhenomenon_ErrorPhenomenonGroupNull() {

		// create phenomenon
		Phenomenon phenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), null);
		assertNotNull(phenomenon);

		phenomenon.setPhenomenonGroup(null);
		try {
			getPIRTApp().updatePhenomenon(phenomenon);
			fail("Updating a phenomenon with a phenomenon group null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_PHENOMENON_GROUP_NULL));
		}
	}

	@Test
	void testDeletePhenomenon_ErrorPhenomenonNull() {
		try {
			getPIRTApp().deletePhenomenon(null);
			fail("Deleting a phenomenon null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_DELETEPHENOMENON_PHENOMENONNULL), e.getMessage());
		}
	}

	@Test
	void testDeletePhenomenon_ErrorIdNull() {
		try {
			getPIRTApp().deletePhenomenon(new Phenomenon());
			fail("Deleting a phenomenon with an id null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_DELETEPHENOMENON_IDNULL), e.getMessage());
		}
	}
}
