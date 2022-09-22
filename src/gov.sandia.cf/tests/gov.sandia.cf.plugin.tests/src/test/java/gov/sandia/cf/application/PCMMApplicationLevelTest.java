/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMAggregationLevel;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the PCMM Application Controller
 */
class PCMMApplicationLevelTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMApplicationLevelTest.class);

	@Test
	void testLevelCRUDWorking() throws CredibilityException {
		// Initialize
		IPCMMApplication app = getAppManager().getService(IPCMMApplication.class);

		// ******************************
		// Create Level
		// ******************************
		PCMMLevel level = new PCMMLevel();
		level.setName("My_Level"); //$NON-NLS-1$
		level.setCode(1);

		// Save
		PCMMLevel addedLevel = app.addLevel(level);
		assertNotNull(addedLevel);

		// ******************************
		// Get Level by id
		// ******************************
		PCMMLevel foundLevel = app.getLevelById(level.getId());
		assertNotNull(foundLevel);
		assertEquals(foundLevel.getId(), level.getId());

		// ******************************
		// Update Level
		// ******************************
		level.setName("My_Level_Updated"); //$NON-NLS-1$
		PCMMLevel updatedLevel = app.updateLevel(level);
		assertNotNull(updatedLevel);
		assertEquals(updatedLevel.getName(), level.getName());

		// ******************************
		// Delete Level
		// ******************************
		app.deleteLevel(level);
		assertNull(app.getLevelById(level.getId()));
	}

	@Test
	void testGetClosestLevelForCode() {

		// PCMMSpecification
		PCMMSpecification PCMMConfiguration = mock(PCMMSpecification.class);
		Map<Integer, PCMMLevelColor> colors = new HashMap<Integer, PCMMLevelColor>();
		colors.put(0, new PCMMLevelColor(1, "My_Color_0", "255, 0, 0")); //$NON-NLS-1$ //$NON-NLS-2$
		colors.put(1, new PCMMLevelColor(1, "My_Color_1", "0, 255, 0")); //$NON-NLS-1$ //$NON-NLS-2$
		colors.put(2, new PCMMLevelColor(2, "My_Color_2", "0, 0, 255")); //$NON-NLS-1$ //$NON-NLS-2$
		when(PCMMConfiguration.getLevelColors()).thenReturn(colors);

		// ******************************
		// Closest Level by code
		// ******************************
		// Empty list
		PCMMAggregationLevel closestLevelEmpty = getPCMMAggregateApp().getClosestLevelForCode(PCMMConfiguration, null,
				1);
		assertNull(closestLevelEmpty);

		PCMMLevel level0 = new PCMMLevel();
		level0.setName("My_Level_0"); //$NON-NLS-1$
		level0.setCode(0);

		PCMMLevel level1 = new PCMMLevel();
		level1.setName("My_Level_1"); //$NON-NLS-1$
		level1.setCode(1);

		PCMMLevel level2 = new PCMMLevel();
		level2.setName("My_Level_2"); //$NON-NLS-1$
		level2.setCode(2);

		PCMMLevel level3 = new PCMMLevel();
		level3.setName("My_Level_3"); //$NON-NLS-1$
		level3.setCode(3);

		// Add them
		List<PCMMLevel> levelList = new ArrayList<PCMMLevel>();
		levelList.add(level0);
		levelList.add(level1);
		levelList.add(level2);
		levelList.add(level3);

		// Find closest
		PCMMAggregationLevel closestLevel5 = getPCMMAggregateApp().getClosestLevelForCode(PCMMConfiguration, levelList,
				5);
		PCMMAggregationLevel closestLevel0 = getPCMMAggregateApp().getClosestLevelForCode(PCMMConfiguration, levelList,
				-1);
		PCMMAggregationLevel closestLevel2 = getPCMMAggregateApp().getClosestLevelForCode(PCMMConfiguration, levelList,
				2);

		// Tests standard
		assertEquals(Integer.valueOf(-1), closestLevel0.getCode());
		assertEquals(Integer.valueOf(2), closestLevel2.getCode());
		assertEquals(Integer.valueOf(5), closestLevel5.getCode());
		closestLevel5 = getPCMMAggregateApp().getClosestLevelForCode(PCMMConfiguration, levelList, 5);
		assertNotNull(closestLevel5);
		assertEquals(Integer.valueOf(5), closestLevel5.getCode());

		// Double Same code
		levelList.add(level2);
		closestLevel2 = getPCMMAggregateApp().getClosestLevelForCode(PCMMConfiguration, levelList, 2);
		assertEquals(Integer.valueOf(2), closestLevel2.getCode());

		// Null cases
		PCMMLevel levelNull = new PCMMLevel();
		level3.setName("My_Level_Null"); //$NON-NLS-1$
		level3.setCode(null);
		levelList.add(levelNull);

		// Double Code null
		levelList.add(levelNull);
		closestLevel2 = getPCMMAggregateApp().getClosestLevelForCode(PCMMConfiguration, levelList, 2);
		assertEquals(closestLevel2.getCode(), Integer.valueOf(2));

		// One null
		levelList.add(null);
		PCMMAggregationLevel closestLevelN = getPCMMAggregateApp().getClosestLevelForCode(PCMMConfiguration, levelList,
				0);
		assertEquals(closestLevelN.getCode(), Integer.valueOf(0));
	}

	/* *********** addLevel *********** */

	@Test
	void testAddLevel_Errors() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).addLevel(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDLEVEL_LEVELNULL), e.getMessage());
	}

	/* *********** getLevelById *********** */

	@Test
	void testGetLevelById_Null() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).getLevelById(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_GETLEVELBYID_IDNULL), e.getMessage());
	}

	/* *********** updateLevel *********** */

	@Test
	void testUpdateLevel_Null() {
		// Check null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).updateLevel(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATELEVEL_LEVELNULL), e.getMessage());
	}

	@Test
	void testUpdateLevel_IdNull() {
		// Check id null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).updateLevel(new PCMMLevel());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATELEVEL_IDNULL), e.getMessage());
	}

	/* *********** deleteLevel *********** */

	@Test
	void testDeleteLevel_Null() {
		// Check null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).deleteLevel(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETELEVEL_LEVELNULL), e.getMessage());
	}

	@Test
	void testDeleteLevel_IdNull() {
		// Check id null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).deleteLevel(new PCMMLevel());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETELEVEL_IDNULL), e.getMessage());
	}
}
