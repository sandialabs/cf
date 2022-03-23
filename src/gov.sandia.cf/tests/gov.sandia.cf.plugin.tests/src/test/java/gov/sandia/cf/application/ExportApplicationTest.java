/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.decision.IDecisionApplication;
import gov.sandia.cf.application.decision.IImportDecisionApp;
import gov.sandia.cf.application.decision.YmlReaderDecisionSchema;
import gov.sandia.cf.application.pcmm.IImportPCMMApp;
import gov.sandia.cf.application.pcmm.YmlReaderPCMMSchema;
import gov.sandia.cf.application.pirt.IImportPIRTApp;
import gov.sandia.cf.application.pirt.YmlReaderPIRTSchema;
import gov.sandia.cf.application.qoiplanning.IImportQoIPlanningApp;
import gov.sandia.cf.application.qoiplanning.IQoIPlanningApplication;
import gov.sandia.cf.application.qoiplanning.YmlReaderQoIPlanningSchema;
import gov.sandia.cf.application.requirement.IImportSysRequirementApp;
import gov.sandia.cf.application.requirement.ISystemRequirementApplication;
import gov.sandia.cf.application.requirement.YmlReaderSystemRequirementSchema;
import gov.sandia.cf.application.uncertainty.IImportUncertaintyApp;
import gov.sandia.cf.application.uncertainty.IUncertaintyApplication;
import gov.sandia.cf.application.uncertainty.YmlReaderUncertaintySchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * JUnit test class for the Export Application Controller
 * 
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class ExportApplicationTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ExportApplicationTest.class);

	@Test
	void testExportDecisionSpecification() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import configuration
		getAppManager().getService(IImportDecisionApp.class).importDecisionSpecification(model, user, confFile);

		// export
		File exportConfFile = File.createTempFile("ModSim_Decision", ".yml", getTestTempFolder()); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(exportConfFile);
		assertTrue(exportConfFile.exists());

		DecisionSpecification configuration = getAppManager().getService(IDecisionApplication.class)
				.loadDecisionConfiguration(model);
		getExportApp().exportDecisionSchema(exportConfFile, configuration);

		DecisionSpecification exportedConfiguration = new YmlReaderDecisionSchema().load(exportConfFile);

		// compare configuration
		boolean sameConfiguration = getAppManager().getService(IDecisionApplication.class)
				.sameConfiguration(configuration, exportedConfiguration);
		assertTrue(sameConfiguration);
	}

	@Test
	void testExportQoIPlanningSpecification() throws CredibilityException, IOException, URISyntaxException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/QoI_Planning-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import configuration
		getAppManager().getService(IImportQoIPlanningApp.class).importQoIPlanningSpecification(model, user, confFile);

		// export
		File exportConfFile = File.createTempFile("QoI_Planning", ".yml", getTestTempFolder()); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(exportConfFile);
		assertTrue(exportConfFile.exists());

		QoIPlanningSpecification configuration = getAppManager().getService(IQoIPlanningApplication.class)
				.loadQoIPlanningConfiguration(model);
		getExportApp().exportQoIPlanningSchema(exportConfFile, configuration);

		QoIPlanningSpecification exportedConfiguration = null;
		exportedConfiguration = new YmlReaderQoIPlanningSchema().load(exportConfFile);

		// compare configuration
		boolean sameConfiguration = getAppManager().getService(IQoIPlanningApplication.class)
				.sameConfiguration(configuration, exportedConfiguration);
		assertTrue(sameConfiguration);
	}

	@Test
	void testExportPIRTSpecification() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import configuration
		getAppManager().getService(IImportPIRTApp.class).importPIRTSpecification(model, user, confFile);

		// export
		File exportConfFile = File.createTempFile("PIRT-schema", ".yml", getTestTempFolder()); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(exportConfFile);
		assertTrue(exportConfFile.exists());

		PIRTSpecification pirtConfiguration = getPIRTApp().loadPIRTConfiguration(model);
		getExportApp().exportPIRTSchema(exportConfFile, pirtConfiguration);

		PIRTSpecification exportedConfiguration = null;
		exportedConfiguration = new YmlReaderPIRTSchema().load(exportConfFile);

		// compare configuration
		boolean sameConfiguration = getPIRTApp().sameConfiguration(pirtConfiguration, exportedConfiguration);
		assertTrue(sameConfiguration);
	}

	@Test
	void testExportPCMMSpecification() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import configuration
		getAppManager().getService(IImportPCMMApp.class).importPCMMSpecification(model, user, confFile);

		// export
		File exportConfFile = File.createTempFile("PCMM-schema", ".yml", getTestTempFolder()); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(exportConfFile);
		assertTrue(exportConfFile.exists());

		PCMMSpecification configuration = getPCMMApp().loadPCMMConfiguration(model);

		getExportApp().exportPCMMSchema(exportConfFile, configuration);

		PCMMSpecification exportedConfiguration = new YmlReaderPCMMSchema().load(exportConfFile);

		// compare configuration
		boolean sameConfiguration = getPCMMApp().sameConfiguration(configuration, exportedConfiguration);
		assertTrue(sameConfiguration);
	}

	@Test
	void testExportUncertaintySpecification() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import configuration
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintySpecification(model, user, confFile);

		// export
		File exportConfFile = File.createTempFile("Uncertainty-schema", ".yml", getTestTempFolder()); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(exportConfFile);
		assertTrue(exportConfFile.exists());

		UncertaintySpecification configuration = getAppManager().getService(IUncertaintyApplication.class)
				.loadUncertaintyConfiguration(model);

		getExportApp().exportUncertaintySchema(exportConfFile, configuration);

		UncertaintySpecification exportedConfiguration = new YmlReaderUncertaintySchema().load(exportConfFile);

		// compare configuration
		boolean sameConfiguration = getAppManager().getService(IUncertaintyApplication.class)
				.sameConfiguration(configuration, exportedConfiguration);
		assertTrue(sameConfiguration);
	}

	@Test
	void testExportSysRequirementSpecification() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Requirement_Parameter-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import configuration
		getAppManager().getService(IImportSysRequirementApp.class).importSysRequirementSpecification(model, user,
				confFile);

		// export
		File exportConfFile = File.createTempFile("SysRequirement-schema", ".yml", getTestTempFolder()); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(exportConfFile);
		assertTrue(exportConfFile.exists());

		SystemRequirementSpecification configuration = getAppManager().getService(ISystemRequirementApplication.class)
				.loadSysRequirementConfiguration(model);

		getExportApp().exportSysRequirementsSchema(exportConfFile, configuration);

		SystemRequirementSpecification exportedConfiguration = new YmlReaderSystemRequirementSchema()
				.load(exportConfFile);

		// compare configuration
		boolean sameConfiguration = getAppManager().getService(ISystemRequirementApplication.class)
				.sameConfiguration(configuration, exportedConfiguration);
		assertTrue(sameConfiguration);
	}

}
