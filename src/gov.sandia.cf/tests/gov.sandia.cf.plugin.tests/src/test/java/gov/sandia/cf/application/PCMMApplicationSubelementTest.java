/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the PCMM Application Controller
 */
@RunWith(JUnitPlatform.class)
class PCMMApplicationSubelementTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMApplicationSubelementTest.class);

	@Test
	void testSubelementCRUDWorking() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create element
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		assertNotNull(createdElement);

		// ******************************
		// Create Sub-element
		// ******************************
		PCMMSubelement subelement = new PCMMSubelement();
		subelement.setCode("My_Code"); //$NON-NLS-1$
		subelement.setName("My_Subelement"); //$NON-NLS-1$
		subelement.setElement(createdElement);

		// Save
		PCMMSubelement addedSubelement = null;
		try {
			addedSubelement = getPCMMApp().addSubelement(subelement);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(addedSubelement);

		// ******************************
		// Get Sub-element by id
		// ******************************
		PCMMSubelement foundSubelement;
		try {
			foundSubelement = getPCMMApp().getSubelementById(subelement.getId());
			assertNotNull(foundSubelement);
			assertEquals(foundSubelement.getId(), subelement.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ******************************
		// Update Sub-element
		// ******************************
		PCMMSubelement updatedSubelement;
		subelement.setName("My_Subelement_Updated"); //$NON-NLS-1$
		try {
			updatedSubelement = getPCMMApp().updateSubelement(subelement);
			assertNotNull(updatedSubelement);
			assertEquals(updatedSubelement.getName(), subelement.getName());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ******************************
		// Delete Sub-element
		// ******************************
		try {
			getPCMMApp().deleteSubelement(subelement);
			assertNull(getPCMMApp().getSubelementById(subelement.getId()));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testAddSubelement_Errors() {
		try {
			getPCMMApp().addSubelement(null);
			fail("Can create PCMMSubelement with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDSUBELT_SUBELTNULL), e.getMessage());
		}
	}

	@Test
	void testGetSubelementById_Errors() {
		try {
			getPCMMApp().getSubelementById(null);
			fail("Can get PCMMSubelement by id with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_GETSUBELTBYID_IDNULL), e.getMessage());
		}
	}

	@Test
	void testUpdateSubelement_Errors() {
		// Check null
		try {
			getPCMMApp().updateSubelement(null);
			fail("Can update PCMMSubelement with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATESUBELT_SUBELTNULL), e.getMessage());
		}
		// Check id null
		try {
			getPCMMApp().updateSubelement(new PCMMSubelement());
			fail("Can update PCMMSubelement with id null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATESUBELT_IDNULL), e.getMessage());
		}
	}

	@Test
	void testDeleteSubelement_Errors() {
		// Check null
		try {
			getPCMMApp().deleteSubelement(null);
			fail("Can delete PCMMSubelement with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETESUBELT_SUBELTNULL), e.getMessage());
		}
		// Check id null
		try {
			getPCMMApp().deleteSubelement(new PCMMSubelement());
			fail("Can delete PCMMSubelement with id null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETESUBELT_IDNULL), e.getMessage());
		}
	}

	@Test
	void testGetElementFromKey_Found() {
		PCMMSubelement newPCMMElement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMSubelement elementFromKey = getAppManager().getService(IPCMMApplication.class)
				.getSubelementFromKey(newPCMMElement.getCode());
		assertNotNull(elementFromKey);
		assertEquals(newPCMMElement, elementFromKey);
	}

	@Test
	void testGetElementFromKey_NotFound() {
		TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		PCMMSubelement elementFromKey = getAppManager().getService(IPCMMApplication.class)
				.getSubelementFromKey("OTHER_KEY"); //$NON-NLS-1$
		assertNull(elementFromKey);
	}
}
