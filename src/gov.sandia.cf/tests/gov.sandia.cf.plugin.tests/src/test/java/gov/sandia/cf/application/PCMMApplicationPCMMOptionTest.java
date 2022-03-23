/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.dao.IPCMMOptionRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * JUnit test class for the PCMM Application Controller
 * 
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class PCMMApplicationPCMMOptionTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMApplicationPCMMOptionTest.class);

	/* *********** addPCMMOption *********** */
	@Test
	void test_addPCMMOption_Working() throws CredibilityException {

		PCMMOption option = new PCMMOption();
		option.setPhase(PCMMPhase.ASSESS);

		PCMMOption addedOption = getAppManager().getService(IPCMMApplication.class).addPCMMOption(option);

		assertNotNull(addedOption);
		assertNotNull(addedOption.getId());
		assertEquals(PCMMPhase.ASSESS, addedOption.getPhase());
	}

	@Test
	void testAddPCMMOption_Null() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).addPCMMOption(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDPCMMOPTION_NULL), e.getMessage());
	}

	/* *********** updatePCMMOption *********** */

	@Test
	void test_updateOption_Working() throws CredibilityException {

		PCMMOption option = TestEntityFactory.getNewPCMMOption(getDaoManager(), PCMMPhase.AGGREGATE);
		option.setPhase(PCMMPhase.EVIDENCE);

		PCMMOption updatedOption = getAppManager().getService(IPCMMApplication.class).updatePCMMOption(option);

		assertNotNull(updatedOption);
		assertNotNull(updatedOption.getId());
		assertEquals(PCMMPhase.EVIDENCE, updatedOption.getPhase());
	}

	@Test
	void test_updatePCMMOption_Null() {
		// Check null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).updatePCMMOption(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEPCMMOPTION_NULL), e.getMessage());
	}

	@Test
	void test_updatePCMMOption_IdNull() {
		// Check id null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).updatePCMMOption(new PCMMOption());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEPCMMOPTION_IDNULL), e.getMessage());
	}

	/* *********** deletePCMMOption *********** */

	@Test
	void test_deletePCMMOption_Working() throws CredibilityException {

		PCMMOption level = TestEntityFactory.getNewPCMMOption(getDaoManager(), null);

		getAppManager().getService(IPCMMApplication.class).deletePCMMOption(level);

		List<PCMMOption> allColors = getDaoManager().getRepository(IPCMMOptionRepository.class).findAll();
		assertNotNull(allColors);
		assertTrue(allColors.isEmpty());
	}

	@Test
	void test_deletePCMMOption_Null() {
		// Check null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).deletePCMMOption(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETEPCMMOPTION_NULL), e.getMessage());
	}

	@Test
	void test_deletePCMMOption_IdNull() {
		// Check id null
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IPCMMApplication.class).deletePCMMOption(new PCMMOption());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETEPCMMOPTION_IDNULL), e.getMessage());
	}

	/* *********** deleteAllPCMMOptions *********** */

	@Test
	void test_deleteAllPCMMOptions_Working() throws CredibilityException {

		PCMMOption opt1 = TestEntityFactory.getNewPCMMOption(getDaoManager(), PCMMPhase.AGGREGATE);
		PCMMOption opt2 = TestEntityFactory.getNewPCMMOption(getDaoManager(), PCMMPhase.ASSESS);
		PCMMOption opt3 = TestEntityFactory.getNewPCMMOption(getDaoManager(), PCMMPhase.PLANNING);
		PCMMOption opt4 = TestEntityFactory.getNewPCMMOption(getDaoManager(), PCMMPhase.STAMP);

		getAppManager().getService(IPCMMApplication.class).deleteAllPCMMOptions(Arrays.asList(opt1, opt2, opt3));

		List<PCMMOption> allOptions = getDaoManager().getRepository(IPCMMOptionRepository.class).findAll();
		assertNotNull(allOptions);
		assertEquals(1, allOptions.size());
		assertEquals(opt4, allOptions.iterator().next());
	}

	@Test
	void test_deleteAllPCMMOptions_Null() {
		// Check null
		try {
			getAppManager().getService(IPCMMApplication.class).deleteAllPCMMOptions(null);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

}
