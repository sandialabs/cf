/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the PCMM Application Controller
 */
@RunWith(JUnitPlatform.class)
class PCMMApplicationElementTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMApplicationElementTest.class);

	@Test
	void testElementCRUDWorking() {
		// Initialize
		IPCMMApplication app = getAppManager().getService(IPCMMApplication.class);

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// ******************************
		// Create Element
		// ******************************
		PCMMElement element = new PCMMElement();
		element.setAbbreviation("Abbrev"); //$NON-NLS-1$
		element.setName("My_Element"); //$NON-NLS-1$
		element.setModel(createdModel);

		// Save
		PCMMElement addedElement = null;
		try {
			addedElement = app.addElement(element);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(addedElement);

		// ******************************
		// Get Element list
		// ******************************
		List<PCMMElement> elements;
		try {
			elements = app.getElementList(createdModel);
			assertNotNull(elements);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ******************************
		// Get Element by id
		// ******************************
		PCMMElement foundElement;
		try {
			foundElement = app.getElementById(element.getId());
			assertNotNull(foundElement);
			assertEquals(foundElement.getId(), element.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ******************************
		// Update Element
		// ******************************
		PCMMElement updatedElement;
		element.setName("My_Element_Updated"); //$NON-NLS-1$
		try {
			updatedElement = app.updateElement(element);
			assertNotNull(updatedElement);
			assertEquals(updatedElement.getName(), element.getName());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ******************************
		// Delete Element
		// ******************************
		try {
			app.deleteElement(element);
			assertNull(app.getElementById(element.getId()));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testAddElement_Errors() {
		try {
			getAppManager().getService(IPCMMApplication.class).addElement(null);
			fail("Can create PCMMElement with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDELT_ELTNULL), e.getMessage());
		}
	}

	@Test
	void testGetElementList_Errors() {
		try {
			getAppManager().getService(IPCMMApplication.class).getElementList(null);
			fail("Can get PCMMElement list with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_GETELTLIST_MODELNULL), e.getMessage());
		}
	}

	@Test
	void testGetElementById_Errors() {
		try {
			getAppManager().getService(IPCMMApplication.class).getElementById(null);
			fail("Can get PCMMElement by id with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_GETELTBYID_IDNULL), e.getMessage());
		}
	}

	@Test
	void testUpdateElement_Errors() {
		// Check null
		try {
			getAppManager().getService(IPCMMApplication.class).updateElement(null);
			fail("Can update PCMMElement with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEELT_ELTNULL), e.getMessage());
		}
		// Check id null
		try {
			getAppManager().getService(IPCMMApplication.class).updateElement(new PCMMElement());
			fail("Can update PCMMElement with id null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEELT_IDNULL), e.getMessage());
		}
	}

	@Test
	void testDeleteElement_Errors() {
		// Check null
		try {
			getAppManager().getService(IPCMMApplication.class).deleteElement(null);
			fail("Can delete PCMMElement with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETEELT_ELTNULL), e.getMessage());
		}
		// Check id null
		try {
			getAppManager().getService(IPCMMApplication.class).deleteElement(new PCMMElement());
			fail("Can delete PCMMElement with id null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETEELT_IDNULL), e.getMessage());
		}
	}

	@Test
	void testGetElementFromKey_Found() {
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMElement elementFromKey = getAppManager().getService(IPCMMApplication.class)
				.getElementFromKey(newPCMMElement.getAbbreviation());
		assertNotNull(elementFromKey);
		assertEquals(newPCMMElement, elementFromKey);
	}

	@Test
	void testGetElementFromKey_NotFound() {
		TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		PCMMElement elementFromKey = getAppManager().getService(IPCMMApplication.class).getElementFromKey("OTHER_KEY"); //$NON-NLS-1$
		assertNull(elementFromKey);
	}
}
