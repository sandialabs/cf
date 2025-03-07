package gov.sandia.cf.application.configuration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.requirement.ISystemRequirementApplication;
import gov.sandia.cf.application.requirement.YmlReaderSystemRequirementSchema;
import gov.sandia.cf.application.requirement.YmlWriterSystemRequirementSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * @author Didier Verstraete
 *
 */
class YmlWriterSystemRequirementSchemaTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlWriterSystemRequirementSchemaTest.class);

	@Test
	void test_writeUncertaintySchema_Empty() {

		SystemRequirementSpecification specs = new SystemRequirementSpecification();

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File file = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$
		try {
			new YmlWriterSystemRequirementSchema().writeSchema(file, specs, false, false);
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
	void test_writeSchema_Working() throws URISyntaxException, IOException, CredibilityException {

		// construct
		SystemRequirementSpecification specsOrigin = new YmlReaderSystemRequirementSchema()
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/Requirement_Parameter-v0.1.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		new YmlWriterSystemRequirementSchema().writeSchema(exportFile, specsOrigin, false, false);

		assertTrue(exportFile.exists());
		SystemRequirementSpecification specsExported = new YmlReaderSystemRequirementSchema().load(exportFile); // $NON-NLS-1$
		assertNotNull(specsExported);
		assertTrue(getAppManager().getService(ISystemRequirementApplication.class).sameConfiguration(specsOrigin,
				specsExported));

		// clear
		Files.delete(exportFile.toPath());
	}

	@Test
	void test_writeSchema_WorkingWithIds() throws URISyntaxException, IOException, CredibilityException {

		// construct
		SystemRequirementSpecification specsOrigin = new YmlReaderSystemRequirementSchema()
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/Requirement_Parameter-v0.1.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		new YmlWriterSystemRequirementSchema().writeSchema(exportFile, specsOrigin, true, false);

		assertTrue(exportFile.exists());
		SystemRequirementSpecification specsExported = new YmlReaderSystemRequirementSchema().load(exportFile); // $NON-NLS-1$
		assertNotNull(specsExported);
		assertTrue(getAppManager().getService(ISystemRequirementApplication.class).sameConfiguration(specsOrigin,
				specsExported));

		// clear
		Files.delete(exportFile.toPath());
	}

	@Test
	void test_writeSchema_FileNull() throws URISyntaxException, IOException, CredibilityException {

		// construct
		SystemRequirementSpecification specsOrigin = new YmlReaderSystemRequirementSchema()
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/Requirement_Parameter-v0.1.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		File exportFile = null;

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			new YmlWriterSystemRequirementSchema().writeSchema(exportFile, specsOrigin, false, false);
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS), e.getMessage());
	}

	@Test
	void test_writeSchema_SpecsNull() {

		// construct
		SystemRequirementSpecification specsOrigin = null;

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		try {
			new YmlWriterSystemRequirementSchema().writeSchema(exportFile, specsOrigin, false, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
	}
}
