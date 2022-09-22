/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.YmlReaderPCMMSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * The Class YmlReaderPCMMSchemaSimplifiedModeTest.
 *
 * @author Didier Verstraete
 */
class YmlReaderPCMMSchemaSimplifiedModeTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlReaderPCMMSchemaSimplifiedModeTest.class);

	/* ***************** load ************** */

	@Test
	void test_load_Working() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-No_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		PCMMSpecification confLoaded = new YmlReaderPCMMSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNotNull(confLoaded.getElements());
		assertEquals(6, confLoaded.getElements().size());
		assertEquals(0, confLoaded.getElements().stream().mapToInt(e -> e.getSubElementList().size()).sum());
		assertNotNull(confLoaded.getLevelColors());
		assertEquals(4, confLoaded.getLevelColors().size());
		assertEquals(PCMMMode.SIMPLIFIED, confLoaded.getMode());
		assertNotNull(confLoaded.getOptions());
		assertEquals(5, confLoaded.getOptions().size());
		assertNotNull(confLoaded.getPhases());
		assertEquals(5, confLoaded.getPhases().size());
		assertNotNull(confLoaded.getPlanningFields());
		assertEquals(3, confLoaded.getPlanningFields().size());
		assertNotNull(confLoaded.getPlanningQuestions());
		assertEquals(20, confLoaded.getPlanningQuestions().values().stream().mapToInt(List::size).sum());
		assertNotNull(confLoaded.getRoles());
		assertEquals(6, confLoaded.getRoles().size());

	}

	@Test
	void test_load_NotPCMMFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		PCMMSpecification confLoaded = new YmlReaderPCMMSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getElements());
		assertNull(confLoaded.getLevelColors());
		assertEquals(PCMMMode.DEFAULT, confLoaded.getMode());
		assertNull(confLoaded.getOptions());
		assertNull(confLoaded.getPhases());
		assertNull(confLoaded.getPlanningFields());
		assertNull(confLoaded.getPlanningQuestions());
		assertNull(confLoaded.getRoles());
	}

	@Test
	void test_load_NotYmlFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		PCMMSpecification confLoaded = new YmlReaderPCMMSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getElements());
		assertNull(confLoaded.getLevelColors());
		assertEquals(PCMMMode.DEFAULT, confLoaded.getMode());
		assertNull(confLoaded.getOptions());
		assertNull(confLoaded.getPhases());
		assertNull(confLoaded.getPlanningFields());
		assertNull(confLoaded.getPlanningQuestions());
		assertNull(confLoaded.getRoles());
	}

	@Test
	void test_load_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyPCMM.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		try {
			new YmlReaderPCMMSchema().load(confFile);
			fail("This should fail if the file is null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
		}
	}

	@Test
	void test_load_NullFile() throws IOException {

		// get configuration file
		File confFile = null;

		// test
		try {
			new YmlReaderPCMMSchema().load(confFile);
			fail("This should fail if the file is null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
		}
	}

	/* ***************** isValid ************** */

	@Test
	void test_isValidPCMMFile_Working() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-No_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPCMMFile = new YmlReaderPCMMSchema().isValid(confFile);
		assertTrue(validPCMMFile);
	}

	@Test
	void test_isValidPCMMFile_NotPCMMFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPCMMFile = new YmlReaderPCMMSchema().isValid(confFile);
		assertFalse(validPCMMFile);
	}

	@Test
	void test_isValidPCMMFile_NotYmlFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPCMMFile = new YmlReaderPCMMSchema().isValid(confFile);
		assertFalse(validPCMMFile);
	}

	@Test
	void test_isValidPCMMFile_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyPCMM.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPCMMFile = new YmlReaderPCMMSchema().isValid(confFile);
		assertFalse(validPCMMFile);
	}

	@Test
	void test_isValidPCMMFile_NullFile() {

		// get configuration file
		File confFile = null;

		// test
		boolean validPCMMFile = new YmlReaderPCMMSchema().isValid(confFile);
		assertFalse(validPCMMFile);
	}

}
