/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pirt.YmlReaderPIRTQueries;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.dto.configuration.PIRTQuery;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class YmlReaderPIRTQueriesTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlReaderPIRTQueriesTest.class);

	/* ***************** load ************** */

	@Test
	void test_load_Working() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_queries.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		List<PIRTQuery> confLoaded = new YmlReaderPIRTQueries().load(confFile);
		assertNotNull(confLoaded);
		assertEquals(4, confLoaded.size());
	}

	@Test
	void test_load_NotPIRTQueryFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		List<PIRTQuery> confLoaded = new YmlReaderPIRTQueries().load(confFile);
		assertNotNull(confLoaded);
		assertTrue(confLoaded.isEmpty());
	}

	@Test
	void test_load_NotYmlFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		List<PIRTQuery> confLoaded = new YmlReaderPIRTQueries().load(confFile);
		assertNotNull(confLoaded);
		assertTrue(confLoaded.isEmpty());
	}

	@Test
	void test_load_FileNotFound() throws IOException, URISyntaxException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyPIRTQuery.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		try {
			new YmlReaderPIRTQueries().load(confFile);
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
			new YmlReaderPIRTQueries().load(confFile);
			fail("This should fail if the file is null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
		}
	}

	/* ***************** isValid ************** */

	@Test
	void test_isValidPIRTQueryFile_Working() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_queries.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPIRTQueryFile = new YmlReaderPIRTQueries().isValid(confFile);
		assertTrue(validPIRTQueryFile);
	}

	@Test
	void test_isValidPIRTQueryFile_NotPIRTQueryFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPIRTQueryFile = new YmlReaderPIRTQueries().isValid(confFile);
		assertFalse(validPIRTQueryFile);
	}

	@Test
	void test_isValidPIRTQueryFile_NotYmlFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPIRTQueryFile = new YmlReaderPIRTQueries().isValid(confFile);
		assertFalse(validPIRTQueryFile);
	}

	@Test
	void test_isValidPIRTQueryFile_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyPIRTQuery.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPIRTQueryFile = new YmlReaderPIRTQueries().isValid(confFile);
		assertFalse(validPIRTQueryFile);
	}

	@Test
	void test_isValidPIRTQueryFile_NullFile() {

		// get configuration file
		File confFile = null;

		// test
		boolean validPIRTQueryFile = new YmlReaderPIRTQueries().isValid(confFile);
		assertFalse(validPIRTQueryFile);
	}

}
