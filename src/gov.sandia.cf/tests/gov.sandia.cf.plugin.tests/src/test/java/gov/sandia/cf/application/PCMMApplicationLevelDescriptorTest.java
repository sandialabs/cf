/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the PCMM Application Controller
 */
class PCMMApplicationLevelDescriptorTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMApplicationLevelDescriptorTest.class);

	private Random random = new Random();

	@Test
	void testLevelDescriptorCRUDWorking() {

		// create level
		PCMMLevel level = TestEntityFactory.getNewPCMMLevel(getDaoManager(), null, 0);
		assertNotNull(level);

		// ******************************
		// Create Level Descriptor
		// ******************************
		PCMMLevelDescriptor levelDescriptor = new PCMMLevelDescriptor();
		levelDescriptor.setName("My_LevelDescriptor"); //$NON-NLS-1$
		levelDescriptor.setValue("My_Value"); //$NON-NLS-1$
		levelDescriptor.setLevel(level);

		// Save
		PCMMLevelDescriptor addedLevelDescriptor = null;
		try {
			addedLevelDescriptor = getPCMMApp().addLevelDescriptor(levelDescriptor);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(addedLevelDescriptor);

		// **********************************
		// Create Level Descriptor long value
		// **********************************
		PCMMLevelDescriptor levelDescriptorL = new PCMMLevelDescriptor();
		levelDescriptorL.setName("My_LevelDescriptor_Long"); //$NON-NLS-1$
		levelDescriptorL.setValue(randomString());
		levelDescriptorL.setLevel(level);

		// Save
		PCMMLevelDescriptor addedLevelDescriptorL = null;
		try {
			addedLevelDescriptorL = getPCMMApp().addLevelDescriptor(levelDescriptorL);
			assertEquals(1499, addedLevelDescriptorL.getValue().length());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(addedLevelDescriptorL);

		// ******************************
		// Get Level Descriptor by id
		// ******************************
		PCMMLevelDescriptor foundLevelDescriptor;
		try {
			foundLevelDescriptor = getPCMMApp().getLevelDescriptorById(levelDescriptor.getId());
			assertNotNull(foundLevelDescriptor);
			assertEquals(foundLevelDescriptor.getId(), levelDescriptor.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ******************************
		// Update Level Descriptor
		// ******************************
		PCMMLevelDescriptor updatedLevelDescriptor;
		levelDescriptor.setName("My_LevelDescriptor_Updated"); //$NON-NLS-1$
		levelDescriptor.setValue("My Value updated"); //$NON-NLS-1$
		try {
			updatedLevelDescriptor = getPCMMApp().updateLevelDescriptor(levelDescriptor);
			assertNotNull(updatedLevelDescriptor);
			assertEquals(updatedLevelDescriptor.getName(), levelDescriptor.getName());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ******************************
		// Update Level Descriptor
		// ******************************
		PCMMLevelDescriptor updatedLevelDescriptorL;
		levelDescriptorL.setName("My_LevelDescriptor_Updated_Long"); //$NON-NLS-1$
		levelDescriptorL.setValue(randomString());
		try {
			updatedLevelDescriptorL = getPCMMApp().updateLevelDescriptor(levelDescriptorL);
			assertNotNull(updatedLevelDescriptorL);
			assertEquals(1499, addedLevelDescriptorL.getValue().length());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ******************************
		// Delete Level Descriptor
		// ******************************
		try {
			getPCMMApp().deleteLevelDescriptor(levelDescriptor);
			assertNull(getPCMMApp().getLevelDescriptorById(levelDescriptor.getId()));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testAddLevelDescriptor_Errors() {
		try {
			getAppManager().getService(IPCMMApplication.class).addLevelDescriptor(null);
			fail("Can create PCMMLevelDescriptor with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDLEVELDESC_LEVELDESCNULL), e.getMessage());
		}
	}

	@Test
	void testGetLevelDescriptorById_Errors() {
		try {
			getAppManager().getService(IPCMMApplication.class).getLevelDescriptorById(null);
			fail("Can get PCMMLevelDescriptor by id with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETELEVELDESC_LEVELDESCNULL), e.getMessage());
		}
	}

	@Test
	void testUpdateLevelDescriptor_Errors() {
		// Check null
		try {
			getAppManager().getService(IPCMMApplication.class).updateLevelDescriptor(null);
			fail("Can update PCMMLevelDescriptor with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATELEVELDESC_LEVELDESCNULL), e.getMessage());
		}
		// Check id null
		try {
			getAppManager().getService(IPCMMApplication.class).updateLevelDescriptor(new PCMMLevelDescriptor());
			fail("Can update PCMMLevelDescriptor with id null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATELEVELDESC_IDNULL), e.getMessage());
		}
	}

	@Test
	void testDeleteLevelDescriptor_Errors() {
		// Check null
		try {
			getAppManager().getService(IPCMMApplication.class).deleteLevelDescriptor(null);
			fail("Can delete PCMMLevelDescriptor with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETELEVELDESC_LEVELDESCNULL), e.getMessage());
		}
		// Check id null
		try {
			getAppManager().getService(IPCMMApplication.class).deleteLevelDescriptor(new PCMMLevelDescriptor());
			fail("Can delete PCMMLevelDescriptor with id null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETELEVELDESC_IDNULL), e.getMessage());
		}
	}

	private String randomString() {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 1501;

		String generatedString = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
		return generatedString;
	}
}
