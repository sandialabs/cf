/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.uncertainty.YmlReaderUncertaintyData;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.dto.yml.YmlUncertaintyDataDto;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class YmlReaderUncertaintyDataTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlReaderUncertaintyDataTest.class);

	/* ***************** load ************** */

	@Test
	void test_load_Working() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_with_data.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		YmlUncertaintyDataDto confLoaded = new YmlReaderUncertaintyData().load(confFile);
		assertNotNull(confLoaded);
		assertNotNull(confLoaded.getUncertaintyGroups());
		assertEquals(4, confLoaded.getUncertaintyGroups().size());

		Optional<Uncertainty> numericalGroup = confLoaded.getUncertaintyGroups().stream()
				.filter(u -> u.getName().equals("Numerical")).findFirst(); //$NON-NLS-1$
		assertTrue(numericalGroup.isPresent());
		List<Uncertainty> children = numericalGroup.get().getChildren();
		assertEquals(2, children.size());

		Optional<Uncertainty> ftyUncertainty = children.stream().filter(u -> u.getName().equals("FTY")).findFirst(); //$NON-NLS-1$
		assertTrue(ftyUncertainty.isPresent());
		List<UncertaintyValue> ftyValues = ftyUncertainty.get().getValues();
		assertEquals(7, ftyValues.size());
		assertTrue(ftyValues.stream().allMatch(v -> !StringUtils.isBlank(v.getValue()) && v.getParameter() != null
				&& !StringUtils.isBlank(v.getParameter().getName())));

	}

	@Test
	void test_load_OldFileWorking() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/oldUncertainty_Parameter.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		YmlUncertaintyDataDto confLoaded = new YmlReaderUncertaintyData().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getUncertaintyGroups());
	}

	@Test
	void test_load_NotUncertaintyFile() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		YmlUncertaintyDataDto confLoaded = new YmlReaderUncertaintyData().load(confFile);
		assertNotNull(confLoaded);
		assertNull(confLoaded.getUncertaintyGroups());
	}

	@Test
	void test_load_NotYmlFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			new YmlReaderUncertaintyData().load(confFile);
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTYML, confFile.getPath()), e.getMessage());
	}

	@Test
	void test_load_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyUncertainty.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			new YmlReaderUncertaintyData().load(confFile);
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS), e.getMessage());
	}

	@Test
	void test_load_NullFile() throws IOException {

		// get configuration file
		File confFile = null;

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			new YmlReaderUncertaintyData().load(confFile);
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS), e.getMessage());
	}

	/* ***************** isValid ************** */
	@Test
	void test_isValidUncertaintyFile_WithData_Working() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_with_data.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintyData().isValid(confFile);
		assertTrue(validUncertaintyFile);
	}

	@Test
	void test_isValidUncertaintyFile_WithoutData_Working() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintyData().isValid(confFile);
		assertFalse(validUncertaintyFile);
	}

	@Test
	void test_isValidUncertaintyFile_OldFileWorking() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/oldUncertainty_Parameter.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintyData().isValid(confFile);
		assertFalse(validUncertaintyFile);
	}

	@Test
	void test_isValidUncertaintyFile_NotUncertaintyFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintyData().isValid(confFile);
		assertFalse(validUncertaintyFile);
	}

	@Test
	void test_isValidUncertaintyFile_NotYmlFile() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/NotYmlFile.txt")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintyData().isValid(confFile);
		assertFalse(validUncertaintyFile);
	}

	@Test
	void test_isValidUncertaintyFile_FileNotFound() throws URISyntaxException, IOException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/MyUncertainty.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintyData().isValid(confFile);
		assertFalse(validUncertaintyFile);
	}

	@Test
	void test_isValidUncertaintyFile_NullFile() {

		// get configuration file
		File confFile = null;

		// test
		boolean validUncertaintyFile = new YmlReaderUncertaintyData().isValid(confFile);
		assertFalse(validUncertaintyFile);
	}

}
