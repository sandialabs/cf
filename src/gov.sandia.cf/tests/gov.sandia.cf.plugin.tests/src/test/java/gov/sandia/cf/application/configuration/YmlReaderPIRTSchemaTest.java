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

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.pirt.PIRTSpecification;
import gov.sandia.cf.application.configuration.pirt.YmlReaderPIRTSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class YmlReaderPIRTSchemaTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlReaderPIRTSchemaTest.class);

	/* ***************** load ************** */

	@Test
	void test_load_Working() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		PIRTSpecification confLoaded = new YmlReaderPIRTSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNotNull(confLoaded.getColors());
		assertEquals(3, confLoaded.getColors().size());
		assertNotNull(confLoaded.getColumns());
		assertEquals(5, confLoaded.getColumns().size());
		assertNotNull(confLoaded.getHeaders());
		assertEquals(5, confLoaded.getHeaders().size());
		assertNotNull(confLoaded.getLevels());
		assertEquals(5, confLoaded.getLevels().size());
		assertNotNull(confLoaded.getPirtAdequacyGuidelines());
		assertEquals(5, confLoaded.getPirtAdequacyGuidelines().size());
	}

	@Test
	void test_load_NotPIRTFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		PIRTSpecification confLoaded = new YmlReaderPIRTSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getColors());
		assertNull(confLoaded.getColumns());
		assertNull(confLoaded.getHeaders());
		assertNull(confLoaded.getLevels());
		assertNull(confLoaded.getPirtAdequacyGuidelines());
	}

	@Test
	void test_load_NotYmlFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		PIRTSpecification confLoaded = new YmlReaderPIRTSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getColors());
		assertNull(confLoaded.getColumns());
		assertNull(confLoaded.getHeaders());
		assertNull(confLoaded.getLevels());
		assertNull(confLoaded.getPirtAdequacyGuidelines());
	}

	@Test
	void test_load_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyPIRT.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		try {
			new YmlReaderPIRTSchema().load(confFile);
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
			new YmlReaderPIRTSchema().load(confFile);
			fail("This should fail if the file is null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
		}
	}

	/* ***************** isValid ************** */

	@Test
	void test_isValidPIRTFile_Working() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPIRTFile = new YmlReaderPIRTSchema().isValid(confFile);
		assertTrue(validPIRTFile);
	}

	@Test
	void test_isValidPIRTFile_NotPIRTFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPIRTFile = new YmlReaderPIRTSchema().isValid(confFile);
		assertFalse(validPIRTFile);
	}

	@Test
	void test_isValidPIRTFile_NotYmlFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPIRTFile = new YmlReaderPIRTSchema().isValid(confFile);
		assertFalse(validPIRTFile);
	}

	@Test
	void test_isValidPIRTFile_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyPIRT.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validPIRTFile = new YmlReaderPIRTSchema().isValid(confFile);
		assertFalse(validPIRTFile);
	}

	@Test
	void test_isValidPIRTFile_NullFile() {

		// get configuration file
		File confFile = null;

		// test
		boolean validPIRTFile = new YmlReaderPIRTSchema().isValid(confFile);
		assertFalse(validPIRTFile);
	}

}
