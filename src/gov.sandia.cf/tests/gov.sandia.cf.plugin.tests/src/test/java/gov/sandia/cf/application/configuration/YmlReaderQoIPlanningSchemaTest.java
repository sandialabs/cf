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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.qoiplanning.YmlReaderQoIPlanningSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * The Class YmlReaderQoIPlanningSchemaTest.
 *
 * @author Didier Verstraete
 */
class YmlReaderQoIPlanningSchemaTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlReaderQoIPlanningSchemaTest.class);

	/* ***************** load ************** */

	@Test
	void test_load_Working() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/QoI_Planning-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		QoIPlanningSpecification confLoaded = new YmlReaderQoIPlanningSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNotNull(confLoaded.getParameters());
		assertEquals(6, confLoaded.getParameters().size());
	}

	@Test
	void test_load_OldFileWorking() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/oldQoI_Planning.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		QoIPlanningSpecification confLoaded = new YmlReaderQoIPlanningSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNotNull(confLoaded.getParameters());
		assertEquals(6, confLoaded.getParameters().size());
	}

	@Test
	void test_load_NotQoIPlanningFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		QoIPlanningSpecification confLoaded = new YmlReaderQoIPlanningSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getParameters());
	}

	@Test
	void test_load_NotYmlFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		QoIPlanningSpecification confLoaded = new YmlReaderQoIPlanningSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getParameters());
	}

	@Test
	void test_load_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyQoIPlanning.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		try {
			new YmlReaderQoIPlanningSchema().load(confFile);
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
			new YmlReaderQoIPlanningSchema().load(confFile);
			fail("This should fail if the file is null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
		}
	}

	/* ***************** isValid ************** */

	@Test
	void test_isValidQoIPlanningFile_Working() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/QoI_Planning-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validQoIPlanningFile = new YmlReaderQoIPlanningSchema().isValid(confFile);
		assertTrue(validQoIPlanningFile);
	}

	@Test
	void test_isValidQoIPlanningFile_OldFileWorking() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/oldQoI_Planning.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validQoIPlanningFile = new YmlReaderQoIPlanningSchema().isValid(confFile);
		assertTrue(validQoIPlanningFile);
	}

	@Test
	void test_isValidQoIPlanningFile_NotQoIPlanningFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validQoIPlanningFile = new YmlReaderQoIPlanningSchema().isValid(confFile);
		assertFalse(validQoIPlanningFile);
	}

	@Test
	void test_isValidQoIPlanningFile_NotYmlFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validQoIPlanningFile = new YmlReaderQoIPlanningSchema().isValid(confFile);
		assertFalse(validQoIPlanningFile);
	}

	@Test
	void test_isValidQoIPlanningFile_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyQoIPlanning.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validQoIPlanningFile = new YmlReaderQoIPlanningSchema().isValid(confFile);
		assertFalse(validQoIPlanningFile);
	}

	@Test
	void test_isValidQoIPlanningFile_NullFile() {

		// get configuration file
		File confFile = null;

		// test
		boolean validQoIPlanningFile = new YmlReaderQoIPlanningSchema().isValid(confFile);
		assertFalse(validQoIPlanningFile);
	}

}
