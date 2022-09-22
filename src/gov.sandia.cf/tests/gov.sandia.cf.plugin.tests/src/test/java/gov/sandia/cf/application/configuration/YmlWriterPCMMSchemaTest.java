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
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.pcmm.YmlReaderPCMMSchema;
import gov.sandia.cf.application.pcmm.YmlWriterPCMMSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * The Class YmlWriterPCMMSchemaTest.
 * 
 * @author Didier Verstraete
 */
class YmlWriterPCMMSchemaTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlWriterPCMMSchemaTest.class);

	@Test
	void testExport_Empty() {

		PCMMSpecification specs = new PCMMSpecification();

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File file = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$
		try {
			YmlWriterPCMMSchema ymlWriter = new YmlWriterPCMMSchema();
			ymlWriter.writeSchema(file, specs, false, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
		assertTrue(file.exists());
	}

	@Test
	void test_writeSchema_Working() throws URISyntaxException, IOException, CredibilityException {

		// construct
		PCMMSpecification specsOrigin = new YmlReaderPCMMSchema().load(new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		new YmlWriterPCMMSchema().writeSchema(exportFile, specsOrigin, false, false);

		assertTrue(exportFile.exists());
		PCMMSpecification specsExported = new YmlReaderPCMMSchema().load(exportFile); // $NON-NLS-1$
		assertNotNull(specsExported);
		assertTrue(getAppManager().getService(IPCMMApplication.class).sameConfiguration(specsOrigin, specsExported));

		// clear
		Files.delete(exportFile.toPath());
	}

	@Test
	void test_writeSchema_FileNull() throws URISyntaxException, IOException, CredibilityException {

		// construct
		PCMMSpecification specsOrigin = new YmlReaderPCMMSchema().load(new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		File exportFile = null;

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			new YmlWriterPCMMSchema().writeSchema(exportFile, specsOrigin, false, false);
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS), e.getMessage());
	}

	@Test
	void test_writeSchema_SpecsNull() {

		// construct
		PCMMSpecification specsOrigin = null;

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		try {
			new YmlWriterPCMMSchema().writeSchema(exportFile, specsOrigin, false, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
	}

	/* ************** PCMM Planning *********** */

	@Test
	void testExport_PCMMPlanning() {

		PCMMPlanningParam param1 = TestEntityFactory.getNewPCMMPlanningParam(null, null);
		param1.setType("Action item"); //$NON-NLS-1$

		PCMMPlanningParam parent1 = TestEntityFactory.getNewPCMMPlanningParam(null, null);
		parent1.setName("Action item"); //$NON-NLS-1$

		PCMMPlanningParam child1 = TestEntityFactory.getNewPCMMPlanningParam(null, null);
		child1.setName("Date"); //$NON-NLS-1$
		child1.setType(FormFieldType.DATE.getType());
		PCMMPlanningParam child2 = TestEntityFactory.getNewPCMMPlanningParam(null, null);
		child2.setName("Richtext"); //$NON-NLS-1$
		child2.setType(FormFieldType.RICH_TEXT.getType());
		PCMMPlanningParam child3 = TestEntityFactory.getNewPCMMPlanningParam(null, null);
		child3.setName("Select field"); //$NON-NLS-1$
		child3.setType(FormFieldType.SELECT.getType());

		parent1.setChildren(Arrays.asList(new PCMMPlanningParam[] { child1, child2, child3 }));

		PCMMSpecification specs = new PCMMSpecification();
		specs.setPlanningFields(Arrays.asList(new PCMMPlanningParam[] { param1 }));

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File file = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$
		try {
			YmlWriterPCMMSchema ymlWriter = new YmlWriterPCMMSchema();
			ymlWriter.writeSchema(file, specs, false, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
		assertTrue(file.exists());
	}
}
