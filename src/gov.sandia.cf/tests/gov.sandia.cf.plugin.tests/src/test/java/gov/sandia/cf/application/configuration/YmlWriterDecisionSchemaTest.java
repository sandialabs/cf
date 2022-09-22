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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.decision.IDecisionApplication;
import gov.sandia.cf.application.decision.YmlReaderDecisionSchema;
import gov.sandia.cf.application.decision.YmlWriterDecisionSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * The Class YmlWriterDecisionSchemaTest.
 *
 * @author Didier Verstraete
 */
class YmlWriterDecisionSchemaTest extends AbstractConfigurationTest {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(YmlWriterDecisionSchemaTest.class);

	@Test
	void test_writeSchema_Empty() throws URISyntaxException, IOException, CredibilityException {

		DecisionSpecification specs = new DecisionSpecification();

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File file = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$
		new YmlWriterDecisionSchema().writeSchema(file, specs, false, false);
		assertTrue(file.exists());

		// clear
		Files.delete(file.toPath());
	}

	@Test
	void test_writeSchema_Working() throws URISyntaxException, IOException, CredibilityException {

		// construct
		DecisionSpecification specsOrigin = new YmlReaderDecisionSchema()
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		new YmlWriterDecisionSchema().writeSchema(exportFile, specsOrigin, false, false);

		assertTrue(exportFile.exists());
		DecisionSpecification specsExported = new YmlReaderDecisionSchema().load(exportFile); // $NON-NLS-1$
		assertNotNull(specsExported);
		assertTrue(
				getAppManager().getService(IDecisionApplication.class).sameConfiguration(specsOrigin, specsExported));

		// clear
		Files.delete(exportFile.toPath());
	}

	@Test
	void test_writeSchema_WorkingWithIds() throws CredibilityException, IOException, URISyntaxException {

		// construct
		DecisionSpecification specsOrigin = new YmlReaderDecisionSchema()
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml"))); //$NON-NLS-1$

		assertNotNull(specsOrigin);

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		new YmlWriterDecisionSchema().writeSchema(exportFile, specsOrigin, true, false);

		assertTrue(exportFile.exists());
		DecisionSpecification specsExported = new YmlReaderDecisionSchema().load(exportFile); // $NON-NLS-1$
		assertNotNull(specsExported);
		assertTrue(
				getAppManager().getService(IDecisionApplication.class).sameConfiguration(specsOrigin, specsExported));

		// clear
		Files.delete(exportFile.toPath());
	}

	@Test
	void test_writeSchema_FileNull() throws URISyntaxException, IOException, CredibilityException {

		// construct
		DecisionSpecification specsOrigin = new YmlReaderDecisionSchema()
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml"))); //$NON-NLS-1$
		assertNotNull(specsOrigin);

		File exportFile = null;

		// test
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			new YmlWriterDecisionSchema().writeSchema(exportFile, specsOrigin, false, false);
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS), e.getMessage());
	}

	@Test
	void test_writeSchema_SpecsNull() {

		// construct
		DecisionSpecification specsOrigin = null;

		String createdFileName = "schema.yml"; //$NON-NLS-1$
		File exportFile = new File(TEMP_FOLDER.getRoot() + "/" + createdFileName); //$NON-NLS-1$

		// test
		try {
			new YmlWriterDecisionSchema().writeSchema(exportFile, specsOrigin, false, false);
		} catch (CredibilityException | IOException e) {
			fail(e.getMessage());
		}
	}
}
