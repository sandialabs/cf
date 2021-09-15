/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
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
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPIRTApplication;
import gov.sandia.cf.application.configuration.pirt.PIRTSpecification;
import gov.sandia.cf.application.configuration.pirt.YmlReaderPIRTSchema;
import gov.sandia.cf.application.configuration.pirt.YmlWriterPIRTSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

@RunWith(JUnitPlatform.class)
class YmlWriterPIRTSchemaTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlWriterPIRTSchemaTest.class);

	@Test
	void testExport_Empty() {

		PIRTSpecification specs = new PIRTSpecification();

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File file = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$
		try {
			YmlWriterPIRTSchema ymlWriter = new YmlWriterPIRTSchema();
			ymlWriter.writeSchema(file, specs, false, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
		assertTrue(file.exists());
	}

	@Test
	void test_writeSchema_Working() throws URISyntaxException, IOException, CredibilityException {

		// construct
		PIRTSpecification specsOrigin = new YmlReaderPIRTSchema()
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		new YmlWriterPIRTSchema().writeSchema(exportFile, specsOrigin, false, false);

		assertTrue(exportFile.exists());
		PIRTSpecification specsExported = new YmlReaderPIRTSchema().load(exportFile); // $NON-NLS-1$
		assertNotNull(specsExported);
		assertTrue(getAppManager().getService(IPIRTApplication.class).sameConfiguration(specsOrigin, specsExported));

		// clear
		Files.delete(exportFile.toPath());
	}

	@Test
	void test_writeSchema_FileNull() throws CredibilityException, IOException, URISyntaxException {

		// construct
		PIRTSpecification specsOrigin = new YmlReaderPIRTSchema()
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		File exportFile = null;

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			new YmlWriterPIRTSchema().writeSchema(exportFile, specsOrigin, false, false);
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS), e.getMessage());
	}

	@Test
	void test_writeSchema_SpecsNull() {

		// construct
		PIRTSpecification specsOrigin = null;

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		try {
			new YmlWriterPIRTSchema().writeSchema(exportFile, specsOrigin, false, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
	}
}
