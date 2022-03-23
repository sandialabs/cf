/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.application.pirt.IImportPIRTApp;
import gov.sandia.cf.dao.IPIRTAdequacyColumnGuidelineRepository;
import gov.sandia.cf.dao.IPIRTAdequacyColumnRepository;
import gov.sandia.cf.dao.IPIRTAdequacyLevelGuidelineRepository;
import gov.sandia.cf.dao.IPIRTDescriptionHeaderRepository;
import gov.sandia.cf.dao.IPIRTLevelDifferenceColorRepository;
import gov.sandia.cf.dao.IPIRTLevelImportanceRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * JUnit test class for the Import PIRT Application Controller
 * 
 * @author Maxime N.
 *
 */
@RunWith(JUnitPlatform.class)
class ImportPIRTAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ImportPIRTAppTest.class);

	@Test
	void test_importPIRTSpecification_working() throws CredibilityException, IOException, URISyntaxException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import
		getAppManager().getService(IImportPIRTApp.class).importPIRTSpecification(model, user, confFile);

		// test PIRT headers
		List<PIRTDescriptionHeader> pirtDescriptionHeader = getPIRTApp().getPIRTDescriptionHeader();
		assertNotNull(pirtDescriptionHeader);
		assertEquals(5, pirtDescriptionHeader.size());

		// test PIRT adequacy columns
		List<PIRTAdequacyColumn> pirtAdequacyColumn = getPIRTApp().getPIRTAdequacyColumn();
		assertNotNull(pirtAdequacyColumn);
		assertEquals(5, pirtAdequacyColumn.size());

		// test PIRT levels
		List<PIRTLevelImportance> pirtLevelImportance = getPIRTApp().getPIRTLevelImportance();
		assertNotNull(pirtLevelImportance);
		assertEquals(5, pirtLevelImportance.size());

		// test PIRT level difference colors
		List<PIRTLevelDifferenceColor> pirtLevelDifferenceColor = getPIRTApp().getPIRTLevelDifferenceColor();
		assertNotNull(pirtLevelDifferenceColor);
		assertEquals(3, pirtLevelDifferenceColor.size());

		// test PIRT ranking guidelines
		List<PIRTAdequacyColumnGuideline> pirtAdequacyColumnGuideline = getPIRTApp().getPIRTAdequacyColumnGuideline();
		assertNotNull(pirtAdequacyColumnGuideline);
		assertEquals(5, pirtAdequacyColumnGuideline.size());

		// test PIRT ranking level guidelines
		List<PIRTAdequacyColumnLevelGuideline> pirtAdequacyColumnLevelGuideline = getDaoManager()
				.getRepository(IPIRTAdequacyLevelGuidelineRepository.class).findAll();
		assertNotNull(pirtAdequacyColumnLevelGuideline);
		assertEquals(16, pirtAdequacyColumnLevelGuideline.size());
	}

	@Test
	void test_import_working() throws CredibilityException {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// create colors
		PIRTLevelDifferenceColor color1 = TestEntityFactory.getNewPIRTLevelDifferenceColors(getDaoManager());
		PIRTLevelDifferenceColor color2 = TestEntityFactory.getNewPIRTLevelDifferenceColors(getDaoManager());
		color2.setColor("145, 125, 140"); //$NON-NLS-1$

		// create columns
		PIRTAdequacyColumn column1 = TestEntityFactory.getNewPIRTAdequacyColumn(getDaoManager());
		PIRTAdequacyColumn column2 = TestEntityFactory.getNewPIRTAdequacyColumn(getDaoManager());
		column2.setName(column2.getName() + "1"); //$NON-NLS-1$

		// create headers
		PIRTDescriptionHeader header1 = TestEntityFactory.getNewPIRTDescriptionHeader(getDaoManager());
		PIRTDescriptionHeader header2 = TestEntityFactory.getNewPIRTDescriptionHeader(getDaoManager());
		header2.setName(header2.getName() + "1"); //$NON-NLS-1$

		// create level importance
		PIRTLevelImportance level1 = TestEntityFactory.getNewPIRTLevelImportance(getDaoManager());
		PIRTLevelImportance level2 = TestEntityFactory.getNewPIRTLevelImportance(getDaoManager());
		level2.setName(level2.getName() + "2"); //$NON-NLS-1$
		level2.setLevel(2);

		// create level guidelines
		PIRTAdequacyColumnGuideline columnGuideline1 = TestEntityFactory.getNewStubPIRTAdequacyColumnGuideline();
		PIRTAdequacyColumnLevelGuideline levelGuideline1 = TestEntityFactory
				.getNewStubPIRTAdequacyColumnLevelGuideline(columnGuideline1);
		PIRTAdequacyColumnLevelGuideline levelGuideline2 = TestEntityFactory
				.getNewStubPIRTAdequacyColumnLevelGuideline(columnGuideline1);
		levelGuideline2.setName("Name 2"); //$NON-NLS-1$
		columnGuideline1.setLevelGuidelines(Arrays.asList(levelGuideline1, levelGuideline2));
		PIRTAdequacyColumnGuideline columnGuideline2 = TestEntityFactory.getNewStubPIRTAdequacyColumnGuideline();
		columnGuideline2.setName(columnGuideline2.getName() + "2"); //$NON-NLS-1$

		// Import
		getAppManager().getService(IImportPIRTApp.class).importPIRTColors(model, Arrays.asList(color1, color2));
		getAppManager().getService(IImportPIRTApp.class).importPIRTColumns(model, Arrays.asList(column1, column2));
		getAppManager().getService(IImportPIRTApp.class).importPIRTHeaders(model, Arrays.asList(header1, header2));
		getAppManager().getService(IImportPIRTApp.class).importPIRTLevels(model, Arrays.asList(level1, level2));
		getAppManager().getService(IImportPIRTApp.class)
				.importPIRTGuidelines(Arrays.asList(columnGuideline1, columnGuideline2));

		// Tests
		List<PIRTLevelDifferenceColor> colors = getDaoManager().getRepository(IPIRTLevelDifferenceColorRepository.class)
				.findAll();
		assertNotNull(colors);
		assertEquals(2, colors.size());

		List<PIRTAdequacyColumn> columns = getDaoManager().getRepository(IPIRTAdequacyColumnRepository.class).findAll();
		assertNotNull(columns);
		assertEquals(2, columns.size());

		List<PIRTDescriptionHeader> headers = getDaoManager().getRepository(IPIRTDescriptionHeaderRepository.class)
				.findAll();
		assertNotNull(headers);
		assertEquals(2, headers.size());

		List<PIRTLevelImportance> levels = getDaoManager().getRepository(IPIRTLevelImportanceRepository.class)
				.findAll();
		assertNotNull(levels);
		assertEquals(2, levels.size());

		List<PIRTAdequacyColumnGuideline> guidelines = getDaoManager()
				.getRepository(IPIRTAdequacyColumnGuidelineRepository.class).findAll();
		assertNotNull(guidelines);
		assertEquals(2, guidelines.size());

		List<PIRTAdequacyColumnLevelGuideline> levelGuidelines = getDaoManager()
				.getRepository(IPIRTAdequacyLevelGuidelineRepository.class).findAll();
		assertNotNull(levelGuidelines);
		assertEquals(2, levelGuidelines.size());
	}

	@Test
	void test_importPIRTConfiguration_error_modelNull() {

		// ********************
		// Import with No model
		// ********************
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IImportPIRTApp.class).importPIRTConfiguration(null, null);
			fail("Import with model null is not possible."); //$NON-NLS-1$
		});
		assertEquals(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL), e.getMessage());
	}

	@Test
	void test_importPIRTConfiguration_error_configurationNull() throws CredibilityException {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// **********************************
		// Import with Nothing
		// **********************************
		getAppManager().getService(IImportPIRTApp.class).importPIRTConfiguration(createdModel, null);
	}

	@Test
	void test_analyzeUpdatePIRTConfiguration_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportPIRTApp.class)
				.analyzeUpdatePIRTConfiguration(model, new PIRTSpecification(), confFile);

		// test
		assertNotNull(analysis);

		// test PIRTAdequacyColumn
		assertNotNull(analysis.get(PIRTAdequacyColumn.class));
		assertEquals(5, analysis.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(PIRTAdequacyColumn.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTDescriptionHeader
		assertNotNull(analysis.get(PIRTDescriptionHeader.class));
		assertEquals(5, analysis.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(PIRTDescriptionHeader.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelDifferenceColor
		assertNotNull(analysis.get(PIRTLevelDifferenceColor.class));
		assertEquals(3, analysis.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(PIRTLevelDifferenceColor.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelImportance
		assertNotNull(analysis.get(PIRTLevelImportance.class));
		assertEquals(5, analysis.get(PIRTLevelImportance.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(PIRTLevelImportance.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(PIRTLevelImportance.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTAdequacyColumnGuideline
		assertNotNull(analysis.get(PIRTAdequacyColumnGuideline.class));
		assertEquals(5, analysis.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.NO_CHANGES).size());
	}

	@Test
	void test_importPIRTChanges_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportPIRTApp.class)
				.analyzeUpdatePIRTConfiguration(model, new PIRTSpecification(), confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// import
		getAppManager().getService(IImportPIRTApp.class).importPIRTChanges(model, toChange);

		// test PIRTAdequacyColumn
		List<PIRTAdequacyColumn> adeqCol = getDaoManager().getRepository(IPIRTAdequacyColumnRepository.class).findAll();
		assertNotNull(adeqCol);
		assertEquals(5, adeqCol.size());

		// test PIRTDescriptionHeader
		List<PIRTDescriptionHeader> descHeaderList = getDaoManager()
				.getRepository(IPIRTDescriptionHeaderRepository.class).findAll();
		assertNotNull(descHeaderList);
		assertEquals(5, descHeaderList.size());

		// test PIRTLevelDifferenceColor
		List<PIRTLevelDifferenceColor> levelDiffColor = getDaoManager()
				.getRepository(IPIRTLevelDifferenceColorRepository.class).findAll();
		assertNotNull(levelDiffColor);
		assertEquals(3, levelDiffColor.size());

		// test PIRTLevelImportance
		List<PIRTLevelImportance> levelImportance = getDaoManager().getRepository(IPIRTLevelImportanceRepository.class)
				.findAll();
		assertNotNull(levelImportance);
		assertEquals(5, levelImportance.size());

		// test PIRTAdequacyColumnGuideline
		List<PIRTAdequacyColumnGuideline> adeqColGuid = getDaoManager()
				.getRepository(IPIRTAdequacyColumnGuidelineRepository.class).findAll();
		assertNotNull(adeqColGuid);
		assertEquals(5, adeqColGuid.size());

	}

}
