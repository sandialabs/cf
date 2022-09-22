/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.dao.IPCMMLevelColorRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * JUnit test class for the PCMM Application Controller
 * 
 * @author Didier Verstraete
 *
 */
class PCMMApplicationLevelColorTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMApplicationLevelColorTest.class);

	/* *********** addLevelColor *********** */
	@Test
	void test_addLevelColor_Working() throws CredibilityException {

		PCMMLevelColor level = new PCMMLevelColor();
		level.setCode(1);
		level.setFixedColor("125,125,125"); //$NON-NLS-1$
		level.setName("MYCOLOR"); //$NON-NLS-1$

		PCMMLevelColor addedLevelColor = getAppManager().getService(IPCMMApplication.class).addLevelColor(level);

		assertNotNull(addedLevelColor);
		assertNotNull(addedLevelColor.getId());
		assertEquals("125,125,125", addedLevelColor.getFixedColor()); //$NON-NLS-1$
		assertEquals("MYCOLOR", addedLevelColor.getName()); //$NON-NLS-1$
		assertEquals(Integer.valueOf(1), addedLevelColor.getCode());
	}

	@Test
	void testAddLevelColor_Null() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).addLevelColor(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDLEVELCOLOR_NULL), e.getMessage());
	}

	/* *********** updateLevelColor *********** */

	@Test
	void test_updateLevelColor_Working() throws CredibilityException {

		PCMMLevelColor level = TestEntityFactory.getNewPCMMLevelColor(getDaoManager());
		level.setCode(2);
		level.setFixedColor("NEW"); //$NON-NLS-1$
		level.setName("New Name"); //$NON-NLS-1$

		PCMMLevelColor updatedLevelColor = getAppManager().getService(IPCMMApplication.class).updateLevelColor(level);

		assertNotNull(updatedLevelColor);
		assertNotNull(updatedLevelColor.getId());
		assertEquals("NEW", updatedLevelColor.getFixedColor()); //$NON-NLS-1$
		assertEquals("New Name", updatedLevelColor.getName()); //$NON-NLS-1$
		assertEquals(Integer.valueOf(2), updatedLevelColor.getCode());
	}

	@Test
	void test_updateLevelColor_Null() {
		// Check null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).updateLevelColor(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATELEVELCOLOR_NULL), e.getMessage());
	}

	@Test
	void testUpdateLevelColor_IdNull() {
		// Check id null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).updateLevelColor(new PCMMLevelColor());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATELEVELCOLOR_IDNULL), e.getMessage());
	}

	/* *********** deleteLevelColor *********** */

	@Test
	void testDeleteLevelColor_Working() throws CredibilityException {

		PCMMLevelColor level = TestEntityFactory.getNewPCMMLevelColor(getDaoManager());

		getAppManager().getService(IPCMMApplication.class).deleteLevelColor(level);

		List<PCMMLevelColor> allColors = getDaoManager().getRepository(IPCMMLevelColorRepository.class).findAll();
		assertNotNull(allColors);
		assertTrue(allColors.isEmpty());
	}

	@Test
	void testDeleteLevelColor_Null() {
		// Check null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).deleteLevelColor(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETELEVELCOLOR_NULL), e.getMessage());
	}

	@Test
	void testDeleteLevelColor_IdNull() {
		// Check id null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).deleteLevelColor(new PCMMLevelColor());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETELEVELCOLOR_IDNULL), e.getMessage());
	}
}
