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

import gov.sandia.cf.application.IQoIPlanningApplication;
import gov.sandia.cf.application.configuration.qoiplanning.QoIPlanningSpecification;
import gov.sandia.cf.application.configuration.qoiplanning.YmlReaderQoIPlanningSchema;
import gov.sandia.cf.application.configuration.qoiplanning.YmlWriterQoIPlanningSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class YmlWriterQoIPlanningSchemaTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlWriterQoIPlanningSchemaTest.class);

	@Test
	void test_writeSchema_Empty() throws CredibilityException, IOException {

		QoIPlanningSpecification specs = new QoIPlanningSpecification();

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File file = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$
		new YmlWriterQoIPlanningSchema().writeSchema(file, specs, false, false);
		assertTrue(file.exists());

		// clear
		Files.delete(file.toPath());
	}

	@Test
	void test_writeSchema_Working() throws CredibilityException, IOException, URISyntaxException {

		// construct
		QoIPlanningSpecification specsOrigin = new YmlReaderQoIPlanningSchema()
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/QoI_Planning-v0.1.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		new YmlWriterQoIPlanningSchema().writeSchema(exportFile, specsOrigin, false, false);

		assertTrue(exportFile.exists());
		QoIPlanningSpecification specsExported = new YmlReaderQoIPlanningSchema().load(exportFile); // $NON-NLS-1$
		assertNotNull(specsExported);
		assertTrue(getAppManager().getService(IQoIPlanningApplication.class).sameConfiguration(specsOrigin,
				specsExported));

		// clear
		Files.delete(exportFile.toPath());
	}

	@Test
	void test_writeSchema_WorkingWithIds() throws IOException, CredibilityException, URISyntaxException {

		// construct
		QoIPlanningSpecification specsOrigin = new YmlReaderQoIPlanningSchema()
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/QoI_Planning-v0.1.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		new YmlWriterQoIPlanningSchema().writeSchema(exportFile, specsOrigin, true, false);

		assertTrue(exportFile.exists());
		QoIPlanningSpecification specsExported = new YmlReaderQoIPlanningSchema().load(exportFile); // $NON-NLS-1$
		assertNotNull(specsExported);
		assertTrue(getAppManager().getService(IQoIPlanningApplication.class).sameConfiguration(specsOrigin,
				specsExported));

		// clear
		Files.delete(exportFile.toPath());
	}

	@Test
	void test_writeSchema_FileNull() throws CredibilityException, IOException, URISyntaxException {

		// construct
		QoIPlanningSpecification specsOrigin = new YmlReaderQoIPlanningSchema()
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/QoI_Planning-v0.1.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		File exportFile = null;

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			new YmlWriterQoIPlanningSchema().writeSchema(exportFile, specsOrigin, false, false);
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS), e.getMessage());
	}

	@Test
	void test_writeSchema_SpecsNull() {

		// construct
		QoIPlanningSpecification specsOrigin = null;

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		try {
			new YmlWriterQoIPlanningSchema().writeSchema(exportFile, specsOrigin, false, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
	}
}
