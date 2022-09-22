/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.uncertainty.IUncertaintyApplication;
import gov.sandia.cf.application.uncertainty.YmlReaderUncertaintySchema;
import gov.sandia.cf.application.uncertainty.YmlWriterUncertaintySchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * @author Didier Verstraete
 *
 */
class YmlWriterUncertaintySchemaTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlWriterUncertaintySchemaTest.class);

	@Test
	void test_writeSchema_Empty() {

		UncertaintySpecification specs = new UncertaintySpecification();

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File file = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$
		try {
			new YmlWriterUncertaintySchema().writeSchema(file, specs, false, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
		assertTrue(file.exists());

		// clear
		try {
			Files.delete(file.toPath());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_writeSchema_Working() throws CredibilityException, IOException {

		// construct
		UncertaintySpecification specsOrigin = null;
		try {
			specsOrigin = new YmlReaderUncertaintySchema()
					.load(new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml"))); //$NON-NLS-1$
		} catch (CredibilityException | URISyntaxException | IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(specsOrigin);

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		try {
			new YmlWriterUncertaintySchema().writeSchema(exportFile, specsOrigin, false, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}

		assertTrue(exportFile.exists());
		UncertaintySpecification specsExported = new YmlReaderUncertaintySchema().load(exportFile); // $NON-NLS-1$
		assertNotNull(specsExported);
		assertTrue(getAppManager().getService(IUncertaintyApplication.class).sameConfiguration(specsOrigin,
				specsExported));

		// clear
		try {
			Files.delete(exportFile.toPath());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_writeSchema_WorkingWithIds() throws CredibilityException, IOException {

		// construct
		UncertaintySpecification specsOrigin = null;
		try {
			specsOrigin = new YmlReaderUncertaintySchema()
					.load(new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml"))); //$NON-NLS-1$
		} catch (CredibilityException | URISyntaxException | IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(specsOrigin);

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		try {
			new YmlWriterUncertaintySchema().writeSchema(exportFile, specsOrigin, true, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}

		assertTrue(exportFile.exists());
		UncertaintySpecification specsExported = new YmlReaderUncertaintySchema().load(exportFile); // $NON-NLS-1$
		assertNotNull(specsExported);
		assertTrue(getAppManager().getService(IUncertaintyApplication.class).sameConfiguration(specsOrigin,
				specsExported));

		// clear
		try {
			Files.delete(exportFile.toPath());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_writeSchema_FileNull() {

		// construct
		UncertaintySpecification specsOrigin = null;
		try {
			specsOrigin = new YmlReaderUncertaintySchema()
					.load(new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml"))); //$NON-NLS-1$
		} catch (CredibilityException | URISyntaxException | IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(specsOrigin);

		File exportFile = null;

		// test
		try {
			new YmlWriterUncertaintySchema().writeSchema(exportFile, specsOrigin, false, false);
		} catch (CredibilityException | IOException e) {
			assertEquals(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS), e.getMessage());
		}
	}

	@Test
	void test_writeSchema_SpecsNull() {

		// construct
		UncertaintySpecification specsOrigin = null;

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		try {
			new YmlWriterUncertaintySchema().writeSchema(exportFile, specsOrigin, false, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
	}
}
