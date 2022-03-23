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

import gov.sandia.cf.application.requirement.YmlReaderSystemRequirementSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class YmlReaderSystemRequirementSchemaTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlReaderSystemRequirementSchemaTest.class);

	/* ***************** load ************** */

	@Test
	void test_load_Working() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Requirement_Parameter-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		SystemRequirementSpecification confLoaded = new YmlReaderSystemRequirementSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNotNull(confLoaded.getParameters());
		assertEquals(5, confLoaded.getParameters().size());
	}

	@Test
	void test_load_OldFileWorking() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/oldRequirement_Parameter.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		SystemRequirementSpecification confLoaded = new YmlReaderSystemRequirementSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNotNull(confLoaded.getParameters());
		assertEquals(5, confLoaded.getParameters().size());
	}

	@Test
	void test_load_NotRequirementFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		SystemRequirementSpecification confLoaded = new YmlReaderSystemRequirementSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getParameters());
	}

	@Test
	void test_load_NotYmlFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		SystemRequirementSpecification confLoaded = new YmlReaderSystemRequirementSchema().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getParameters());
	}

	@Test
	void test_load_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyRequirement.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		try {
			new YmlReaderSystemRequirementSchema().load(confFile);
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
			new YmlReaderSystemRequirementSchema().load(confFile);
			fail("This should fail if the file is null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
		}
	}

	/* ***************** isValid ************** */

	@Test
	void test_isValidRequirementFile_Working() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Requirement_Parameter-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validRequirementFile = new YmlReaderSystemRequirementSchema().isValid(confFile);
		assertTrue(validRequirementFile);
	}

	@Test
	void test_isValidRequirementFile_OldFileWorking() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/oldRequirement_Parameter.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validRequirementFile = new YmlReaderSystemRequirementSchema().isValid(confFile);
		assertTrue(validRequirementFile);
	}

	@Test
	void test_isValidRequirementFile_NotRequirementFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validRequirementFile = new YmlReaderSystemRequirementSchema().isValid(confFile);
		assertFalse(validRequirementFile);
	}

	@Test
	void test_isValidRequirementFile_NotYmlFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validRequirementFile = new YmlReaderSystemRequirementSchema().isValid(confFile);
		assertFalse(validRequirementFile);
	}

	@Test
	void test_isValidRequirementFile_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyRequirement.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validRequirementFile = new YmlReaderSystemRequirementSchema().isValid(confFile);
		assertFalse(validRequirementFile);
	}

	@Test
	void test_isValidRequirementFile_NullFile() {

		// get configuration file
		File confFile = null;

		// test
		boolean validRequirementFile = new YmlReaderSystemRequirementSchema().isValid(confFile);
		assertFalse(validRequirementFile);
	}

}
