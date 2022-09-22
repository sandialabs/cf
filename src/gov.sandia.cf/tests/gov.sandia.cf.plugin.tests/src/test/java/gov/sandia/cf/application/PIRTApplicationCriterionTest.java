/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;

import org.eclipse.swt.graphics.RGB;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tests.TestTools;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Didier Verstraete
 *
 *         JUnit test class for the PIRT Application Controller
 */
class PIRTApplicationCriterionTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PIRTApplicationCriterionTest.class);

	@Test
	void testCriterionCRUDWorking() {

		// ********************************
		// test create criterion with empty content
		// ********************************

		// create phenomenon
		Phenomenon phenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), null);
		assertNotNull(phenomenon);

		// new
		String name = "NAME"; //$NON-NLS-1$
		String type = "TYPE"; //$NON-NLS-1$
		String value = "VALUE"; //$NON-NLS-1$

		Criterion newCriterion = new Criterion();
		newCriterion.setName(name);
		newCriterion.setType(type);
		newCriterion.setValue(value);
		newCriterion.setPhenomenon(phenomenon);

		try {
			// create
			Criterion created = getAppManager().getService(IPIRTApplication.class).addCriterion(newCriterion);
			assertNotNull(newCriterion.getId());
			assertEquals(name, created.getName());
			assertEquals(type, created.getType());
			assertEquals(value, created.getValue());
			assertEquals(phenomenon, created.getPhenomenon());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ********************************
		// test select
		// ********************************
		List<Criterion> list = getAppManager().getService(IPIRTApplication.class).getCriterion();
		assertEquals(1, list.size());

		// ********************************
		// test update
		// ********************************
		String newName = "NEWNAME"; //$NON-NLS-1$
		String newType = "NEWTYPE"; //$NON-NLS-1$
		String newValue = "NEWVALUE"; //$NON-NLS-1$

		// get one
		Criterion getted = list.get(0);
		getted.setName(newName);
		getted.setType(newType);
		getted.setValue(newValue);

		try {
			// update
			Criterion updated = getAppManager().getService(IPIRTApplication.class).updateCriterion(getted);
			assertNotNull(updated);
			assertEquals(getted.getId(), updated.getId());
			assertEquals(newName, updated.getName());
			assertEquals(newType, updated.getType());
			assertEquals(newValue, updated.getValue());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ********************************
		// test delete tag
		// ********************************
		list = getAppManager().getService(IPIRTApplication.class).getCriterion();
		assertEquals(1, list.size());
		getted = list.get(0);

		try {
			// delete tag
			getAppManager().getService(IPIRTApplication.class).deleteCriterion(getted, null);

			list = getAppManager().getService(IPIRTApplication.class).getCriterion();
			assertEquals(0, list.size());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

	}

	@Test
	void testAddCriterion_ErrorCriterionNull() {
		try {
			getAppManager().getService(IPIRTApplication.class).addCriterion(null);
			fail("Adding a criterion null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_ADDCRITERION_CRITERIONNULL), e.getMessage());
		}
	}

	@Test
	void testAddCriterion_ErrorNameNull() {

		// create phenomenon
		Phenomenon newPhenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), null);
		assertNotNull(newPhenomenon);

		Criterion criterion = new Criterion();
		criterion.setType("type"); //$NON-NLS-1$
		criterion.setPhenomenon(newPhenomenon);

		try {
			getAppManager().getService(IPIRTApplication.class).addCriterion(criterion);
			fail("Adding a criterion with a name null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_CRITERION_NAME_BLANK));
		}
	}

	@Test
	void testAddCriterion_ErrorNameEmpty() {

		// create phenomenon
		Phenomenon newPhenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), null);
		assertNotNull(newPhenomenon);

		Criterion criterion = new Criterion();
		criterion.setType("type"); //$NON-NLS-1$
		criterion.setPhenomenon(newPhenomenon);
		criterion.setName(""); //$NON-NLS-1$

		try {
			getAppManager().getService(IPIRTApplication.class).addCriterion(criterion);
			fail("Adding a criterion with a name null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_CRITERION_NAME_BLANK));
		}
	}

	@Test
	void testAddCriterion_ErrorTypeNull() {

		// create phenomenon
		Phenomenon newPhenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), null);
		assertNotNull(newPhenomenon);

		Criterion criterion = new Criterion();
		criterion.setName("name"); //$NON-NLS-1$
		criterion.setPhenomenon(newPhenomenon);

		try {
			getAppManager().getService(IPIRTApplication.class).addCriterion(criterion);
			fail("Adding a criterion with a type null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_CRITERION_TYPE_BLANK));
		}
	}

	@Test
	void testAddCriterion_ErrorTypeEmpty() {

		// create phenomenon
		Phenomenon newPhenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), null);
		assertNotNull(newPhenomenon);

		Criterion criterion = new Criterion();
		criterion.setName("name"); //$NON-NLS-1$
		criterion.setPhenomenon(newPhenomenon);
		criterion.setType(""); //$NON-NLS-1$

		try {
			getAppManager().getService(IPIRTApplication.class).addCriterion(criterion);
			fail("Adding a criterion with a name null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_CRITERION_TYPE_BLANK));
		}
	}

	@Test
	void testAddCriterion_ErrorPhenomenonNull() {

		Criterion criterion = new Criterion();
		criterion.setName("name"); //$NON-NLS-1$
		criterion.setType("type"); //$NON-NLS-1$

		try {
			getAppManager().getService(IPIRTApplication.class).addCriterion(criterion);
			fail("Adding a criterion with a phenomenon null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_CRITERION_PHENOMENON_NULL));
		}
	}

	@Test
	void testUpdateCriterion_ErrorCriterionNull() {
		try {
			getAppManager().getService(IPIRTApplication.class).updateCriterion(null);
			fail("Updating a criterion null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_UPDATECRITERION_CRITERIONNULL), e.getMessage());
		}
	}

	@Test
	void testUpdateCriterion_ErrorIdNull() {

		// create phenomenon
		Phenomenon newPhenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), null);
		assertNotNull(newPhenomenon);

		Criterion criterion = new Criterion();
		criterion.setName("name"); //$NON-NLS-1$
		criterion.setType("type"); //$NON-NLS-1$
		criterion.setPhenomenon(newPhenomenon);

		try {
			getAppManager().getService(IPIRTApplication.class).updateCriterion(criterion);
			fail("Updating a criterion with an id null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_UPDATECRITERION_IDNULL), e.getMessage());
		}
	}

	@Test
	void testUpdateCriterion_ErrorNameNull() {

		// create criterion
		Criterion criterion = TestEntityFactory.getNewCriterion(getDaoManager(), null);
		assertNotNull(criterion);

		criterion.setName(null);

		try {
			getAppManager().getService(IPIRTApplication.class).updateCriterion(criterion);
			fail("Updating a criterion with a name null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_CRITERION_NAME_BLANK));
		}
	}

	@Test
	void testUpdateCriterion_ErrorNameEmpty() {

		// create criterion
		Criterion criterion = TestEntityFactory.getNewCriterion(getDaoManager(), null);
		assertNotNull(criterion);

		criterion.setName(""); //$NON-NLS-1$

		try {
			getAppManager().getService(IPIRTApplication.class).updateCriterion(criterion);
			fail("Updating a criterion with a name null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_CRITERION_NAME_BLANK));
		}
	}

	@Test
	void testUpdateCriterion_ErrorTypeNull() {

		// create criterion
		Criterion criterion = TestEntityFactory.getNewCriterion(getDaoManager(), null);
		assertNotNull(criterion);

		criterion.setType(null);

		try {
			getAppManager().getService(IPIRTApplication.class).updateCriterion(criterion);
			fail("Updating a criterion with a type null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_CRITERION_TYPE_BLANK));
		}
	}

	@Test
	void testUpdateCriterion_ErrorTypeEmpty() {

		// create criterion
		Criterion criterion = TestEntityFactory.getNewCriterion(getDaoManager(), null);
		assertNotNull(criterion);

		criterion.setType(""); //$NON-NLS-1$

		try {
			getAppManager().getService(IPIRTApplication.class).updateCriterion(criterion);
			fail("Updating a criterion with a name null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_CRITERION_TYPE_BLANK));
		}
	}

	@Test
	void testUpdateCriterion_ErrorPhenomenonNull() {
		// create criterion
		Criterion criterion = TestEntityFactory.getNewCriterion(getDaoManager(), null);
		assertNotNull(criterion);

		criterion.setPhenomenon(null);

		try {
			getAppManager().getService(IPIRTApplication.class).updateCriterion(criterion);
			fail("Updating a criterion with a phenomenon null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_CRITERION_PHENOMENON_NULL));
		}
	}

	@Test
	void testDeleteCriterion_ErrorCriterionNull() {
		try {
			getAppManager().getService(IPIRTApplication.class).deleteCriterion(null, null);
			fail("Deleting a criterion null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_DELETECRITERION_CRITERIONNULL), e.getMessage());
		}
	}

	@Test
	void testDeleteCriterion_ErrorIdNull() {
		try {
			Criterion criterion = new Criterion();
			criterion.setType("type"); //$NON-NLS-1$
			criterion.setPhenomenon(new Phenomenon());
			getAppManager().getService(IPIRTApplication.class).deleteCriterion(criterion, null);
			fail("Deleting a criterion with an id null must be impossible."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_DELETECRITERION_IDNULL), e.getMessage());
		}
	}

	/* ************ getBackgroundColor ************* */

	@Test
	void testgetBackgroundColor_Working() {

		PIRTLevelImportance expectedLevel = new PIRTLevelImportance("0", "level 0", 0, "LVL 0", null, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		PIRTLevelImportance currentLevel = new PIRTLevelImportance("1", "level 1", 1, "LVL 1", null, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String rgbDiff = "150, 150, 150"; //$NON-NLS-1$

		PIRTSpecification configuration = mock(PIRTSpecification.class);
		when(configuration.getColor(1)).thenReturn(rgbDiff);

		// test
		try {
			String backgroundColor = getPIRTApp().getBackgroundColor(configuration, expectedLevel, currentLevel);
			assertNotNull(backgroundColor);
			assertEquals(rgbDiff, backgroundColor);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testgetBackgroundColor_FixedColor() {

		String rgb0 = "100, 100, 100"; //$NON-NLS-1$
		PIRTLevelImportance expectedLevel = new PIRTLevelImportance("0", "level 0", 0, "LVL 0", rgb0, "Description"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		String rgb1 = "200, 200, 200"; //$NON-NLS-1$
		PIRTLevelImportance currentLevel = new PIRTLevelImportance("1", "level 1", 1, "LVL 1", rgb1, "Description"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		// test
		try {
			RGB backgroundColor = ColorTools.stringRGBToColor(
					getPIRTApp().getBackgroundColor(mock(PIRTSpecification.class), expectedLevel, currentLevel));
			assertNotNull(backgroundColor);
			assertEquals(200, backgroundColor.red);
			assertEquals(200, backgroundColor.blue);
			assertEquals(200, backgroundColor.green);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testgetBackgroundColor_ConfigurationNull() {

		PIRTLevelImportance expectedLevel = new PIRTLevelImportance("0", "level 0", 0, "LVL 0", "100, 100, 100", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"Description"); //$NON-NLS-1$
		PIRTLevelImportance currentLevel = new PIRTLevelImportance("1", "level 1", 1, "LVL 1", "200, 200, 200", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"Description"); //$NON-NLS-1$

		// test
		try {
			getPIRTApp().getBackgroundColor(null, expectedLevel, currentLevel);
			fail("It should fail if the qoi is null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_GETBGCOLOR_CONFNULL), e.getMessage());
		}
	}

	@Test
	void testgetBackgroundColor_ExpectedLevelNull() {

		PIRTLevelImportance currentLevel = new PIRTLevelImportance("1", "level 1", 1, "LVL 1", null, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// test
		try {
			String backgroundColor = getPIRTApp().getBackgroundColor(mock(PIRTSpecification.class), null, currentLevel);
			assertNotNull(backgroundColor);
			assertEquals(ColorTools.DEFAULT_RGB_COLOR, ColorTools.stringRGBToColor(backgroundColor));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testgetBackgroundColor_CurrentLevelNull() {

		PIRTLevelImportance expectedLevel = new PIRTLevelImportance("0", "level 0", 0, "LVL 0", "100, 100, 100", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				"Description"); //$NON-NLS-1$

		// test
		try {
			String backgroundColor = getPIRTApp().getBackgroundColor(mock(PIRTSpecification.class), expectedLevel,
					null);
			assertNotNull(backgroundColor);
			assertEquals(ColorTools.DEFAULT_RGB_COLOR, ColorTools.stringRGBToColor(backgroundColor));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

}
