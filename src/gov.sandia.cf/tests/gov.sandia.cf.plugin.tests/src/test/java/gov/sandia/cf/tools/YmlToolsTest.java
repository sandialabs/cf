/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import gov.sandia.cf.application.imports.YmlCFImportConstructor;
import gov.sandia.cf.model.dto.IntendedPurposeDto;
import gov.sandia.cf.model.dto.yml.YmlAllDataDto;
import gov.sandia.cf.model.dto.yml.YmlUncertaintyDataDto;
import gov.sandia.cf.tests.TestDtoFactory;
import junit.runner.Version;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class YmlToolsTest {
	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlToolsTest.class);

	/**
	 * temporary folder to store files
	 */
	@Rule
	public static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

	@BeforeAll
	public static void initializeAll() {
		try {
			logger.info("JUnit version is: {}", Version.id()); //$NON-NLS-1$

			// Create folder
			TEMP_FOLDER.create();

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private File newFolder;

	@BeforeEach
	public void initialize() {
		try {
			logger.info("Test started"); //$NON-NLS-1$

			newFolder = TEMP_FOLDER.newFolder();
			assertTrue(newFolder.exists());

		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@AfterEach
	public void clean() {
		boolean deleted = newFolder.delete();
		assertTrue(deleted);
		logger.info("Test ending"); //$NON-NLS-1$
	}

	@AfterAll
	public static void cleanAll() {
		try {
			TEMP_FOLDER.delete();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_import_cf() throws ParseException {

		Yaml yaml = new Yaml(new YmlCFImportConstructor(YmlAllDataDto.class));
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("yml/import_cf.yml"); //$NON-NLS-1$
		YmlAllDataDto data = yaml.load(inputStream);
		assertNotNull(data);

		// global data
		assertNotNull(data.getGlobalData());
		assertNotNull(data.getGlobalData().getModel());
		assertNotNull(data.getGlobalData().getRoles());
		assertNotNull(data.getGlobalData().getUsers());

		// intended purpose
		assertNotNull(data.getIntendedPurposeData());
		assertNotNull(data.getIntendedPurposeData().getIntendedPurpose());

		// PCMM
		assertNotNull(data.getPcmmData());
		assertNotNull(data.getPcmmData().getElements());
		assertNotNull(data.getPcmmData().getAssessments());
		assertNotNull(data.getPcmmData().getEvidence());

		assertNotNull(data.getPcmmData().getPlanningFields());
		assertNotNull(data.getPcmmData().getPlanningFieldValues());
		assertNotNull(data.getPcmmData().getPlanningQuestions());
		assertNotNull(data.getPcmmData().getPlanningQuestionValues());
		assertNotNull(data.getPcmmData().getPlanningTableItems());

		// PIRT
		assertNotNull(data.getPirtData());
		assertNotNull(data.getPirtData().getQuantityOfInterestList());

		// System Requirement
		assertNotNull(data.getRequirementData());
		assertNotNull(data.getRequirementData().getRequirementParameters());
		assertNotNull(data.getRequirementData().getRequirementValues());

		// Uncertainty
		assertNotNull(data.getUncertaintyData());
		assertNotNull(data.getUncertaintyData().getUncertaintyParameters());
		assertNotNull(data.getUncertaintyData().getUncertaintyGroups());
	}

	@Test
	void test_export() throws ParseException, IOException {

		IntendedPurposeDto newIntendedPurpose = TestDtoFactory.getNewIntendedPurpose();
		newIntendedPurpose.setId(1);
		newIntendedPurpose.setDescription("test"); //$NON-NLS-1$
		newIntendedPurpose.setReference("Reference"); //$NON-NLS-1$
		newIntendedPurpose.setDateUpdate(java.util.Date.from(LocalDateTime.of(2021, 11, 1, 0, 0).toLocalDate()
				.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

		File newFile = TEMP_FOLDER.newFile("test.yml"); //$NON-NLS-1$

		// YML reader
		Yaml yaml = new Yaml();

		// Write the specifications to the credibility file
		try (Writer writer = new StringWriter()) {

			// Dump map objects to yml string
			yaml.dump(Arrays.asList(newIntendedPurpose), writer);

			FileTools.writeStringInFile(newFile, writer.toString(), false);
		}

		// clean
		assertTrue(newFile.delete());
	}

	@Test
	void test_export_gson() throws ParseException, IOException {

		IntendedPurposeDto newIntendedPurpose = TestDtoFactory.getNewIntendedPurpose();
		newIntendedPurpose.setId(1);
		newIntendedPurpose.setDescription("test"); //$NON-NLS-1$
		newIntendedPurpose.setReference("Reference"); //$NON-NLS-1$
		newIntendedPurpose.setDateUpdate(java.util.Date.from(LocalDateTime.of(2021, 11, 1, 0, 0).toLocalDate()
				.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

		File newFile = TEMP_FOLDER.newFile("test.yml"); //$NON-NLS-1$

		String gson = GsonTools.toGson(Arrays.asList(newIntendedPurpose));

		FileTools.writeStringInFile(newFile, gson, false);

		// clean
		assertTrue(newFile.delete());
	}

	@Test
	void test_import_uncertainty() throws ParseException {

		Representer representer = new Representer();
		representer.getPropertyUtils().setSkipMissingProperties(true);
		Yaml yaml = new Yaml(new YmlCFImportConstructor(YmlUncertaintyDataDto.class), representer);
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("yml/import_uncertainty.yml"); //$NON-NLS-1$

		YmlUncertaintyDataDto data = yaml.load(inputStream);
		assertNotNull(data);
	}

}
