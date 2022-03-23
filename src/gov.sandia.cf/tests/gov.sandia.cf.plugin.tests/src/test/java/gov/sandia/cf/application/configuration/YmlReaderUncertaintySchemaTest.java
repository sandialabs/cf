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

import gov.sandia.cf.application.uncertainty.YmlReaderUncertaintySchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class YmlReaderUncertaintySchemaTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlReaderUncertaintySchemaTest.class);

	/* ***************** load ************** */

	@Test
	void test_load_Working() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		UncertaintySpecification confLoaded = new YmlReaderUncertaintySchema().load(confFile);
		assertNotNull(confLoaded);
		assertNotNull(confLoaded.getParameters());
		assertEquals(9, confLoaded.getParameters().size());
	}

	@Test
	void test_load_OldFileWorking() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/oldUncertainty_Parameter.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		UncertaintySpecification confLoaded = new YmlReaderUncertaintySchema().load(confFile);
		assertNotNull(confLoaded);
		assertNotNull(confLoaded.getParameters());
		assertEquals(9, confLoaded.getParameters().size());
	}

	@Test
	void test_load_NotUncertaintyFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		UncertaintySpecification confLoaded = new YmlReaderUncertaintySchema().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getParameters());
	}

	@Test
	void test_load_NotYmlFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		UncertaintySpecification confLoaded = new YmlReaderUncertaintySchema().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getParameters());
	}

	@Test
	void test_load_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyUncertainty.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		try {
			new YmlReaderUncertaintySchema().load(confFile);
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
			new YmlReaderUncertaintySchema().load(confFile);
			fail("This should fail if the file is null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
		}
	}

	/* ***************** isValid ************** */

	@Test
	void test_isValidUncertaintyFile_Working() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintySchema().isValid(confFile);
		assertTrue(validUncertaintyFile);
	}

	@Test
	void test_isValidUncertaintyFile_OldFileWorking() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/oldUncertainty_Parameter.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintySchema().isValid(confFile);
		assertTrue(validUncertaintyFile);
	}

	@Test
	void test_isValidUncertaintyFile_NotUncertaintyFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintySchema().isValid(confFile);
		assertFalse(validUncertaintyFile);
	}

	@Test
	void test_isValidUncertaintyFile_NotYmlFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintySchema().isValid(confFile);
		assertFalse(validUncertaintyFile);
	}

	@Test
	void test_isValidUncertaintyFile_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyUncertainty.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintySchema().isValid(confFile);
		assertFalse(validUncertaintyFile);
	}

	@Test
	void test_isValidUncertaintyFile_NullFile() {

		// get configuration file
		File confFile = null;

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintySchema().isValid(confFile);
		assertFalse(validUncertaintyFile);
	}

}
