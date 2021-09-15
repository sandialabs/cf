/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.ExportOptions;
import gov.sandia.cf.application.configuration.arg.YmlARGStructure;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementValue;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 *
 * JUnit test class for the ARG Report Planning Application Controller
 * 
 * @author Didier Verstraete
 */
@RunWith(JUnitPlatform.class)
class ReportARGPlanningAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ReportARGPlanningAppTest.class);

	@Test
	void test_generateStructurePlanning_3_Empty() throws CredibilityException {

		// construct
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PLANNING_INCLUDE, true);
		options.put(ExportOptions.PLANNING_REQUIREMENT_INCLUDE, true);
		options.put(ExportOptions.PLANNING_UNCERTAINTY_INCLUDE, true);
		options.put(ExportOptions.PLANNING_DECISION_INCLUDE, true);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPlanningApp.class).generateStructurePlanning(chapters, options);

		// validate
		assertEquals(3, chapters.size());
	}

	@Test
	void test_generateStructurePlanning_Include_Requirement_Empty() throws CredibilityException {

		// construct
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PLANNING_INCLUDE, true);
		options.put(ExportOptions.PLANNING_REQUIREMENT_INCLUDE, true);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPlanningApp.class).generateStructurePlanning(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_REQUIREMENT_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_REQUIREMENT_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertTrue(((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).isEmpty());
	}

	@Test
	void test_generateStructurePlanning_Working_Include_Requirements()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Requirement_Parameter-v0.1.yml")); //$NON-NLS-1$
		getAppManager().getService(IImportSysRequirementApp.class).importSysRequirementSpecification(model, confFile);
		SystemRequirementParam newSystemRequirementParam = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), model, null);
		SystemRequirementParam newSystemRequirementParam2 = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), model, null);
		SystemRequirement newSystemRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), model, null,
				null);
		SystemRequirementValue newSystemRequirementValue = TestEntityFactory
				.getNewSystemRequirementValue(getDaoManager(), newSystemRequirement, newSystemRequirementParam, null);
		newSystemRequirementValue.setValue("MyValue"); //$NON-NLS-1$
		TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), newSystemRequirement,
				newSystemRequirementParam2, null);
		getAppManager().getService(ISystemRequirementApplication.class).refresh(newSystemRequirement);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PLANNING_INCLUDE, true);
		options.put(ExportOptions.PLANNING_REQUIREMENT_INCLUDE, true);
		ARGParameters addDefaultARGParameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(null);
		options.put(ExportOptions.ARG_PARAMETERS, addDefaultARGParameters);
		options.put(ExportOptions.PLANNING_REQUIREMENTS, Arrays.asList(newSystemRequirement));
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPlanningApp.class).generateStructurePlanning(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_REQUIREMENT_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_REQUIREMENT_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(newSystemRequirement.getStatement(),
				((Map<?, ?>) ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
						.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(1, ((List<?>) ((Map<?, ?>) ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
				.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
	}

	@Test
	void test_generateStructurePlanning_Include_Uncertainty_Empty() throws CredibilityException {

		// construct
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PLANNING_INCLUDE, true);
		options.put(ExportOptions.PLANNING_UNCERTAINTY_INCLUDE, true);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPlanningApp.class).generateStructurePlanning(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_UNCERTAINTY_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_UNCERTAINTY_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertTrue(((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).isEmpty());
	}

	@Test
	void test_generateStructurePlanning_Working_Include_Uncertainty()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintySpecification(model, confFile);
		UncertaintyParam newUncertaintyParam = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), model, null);
		UncertaintyParam newUncertaintyParam2 = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), model, null);
		UncertaintyGroup newUncertaintyGroup = TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), model);
		Uncertainty newUncertainty = TestEntityFactory.getNewUncertainty(getDaoManager(), newUncertaintyGroup, null);
		UncertaintyValue newUncertaintyValue = TestEntityFactory.getNewUncertaintyValue(getDaoManager(), newUncertainty,
				newUncertaintyParam, null);
		newUncertaintyValue.setValue("MyValue"); //$NON-NLS-1$
		TestEntityFactory.getNewUncertaintyValue(getDaoManager(), newUncertainty, newUncertaintyParam2, null);
		getAppManager().getService(IUncertaintyApplication.class).refresh(newUncertaintyGroup);
		getAppManager().getService(IUncertaintyApplication.class).refresh(newUncertainty);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PLANNING_INCLUDE, true);
		options.put(ExportOptions.PLANNING_UNCERTAINTY_INCLUDE, true);
		ARGParameters addDefaultARGParameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(null);
		options.put(ExportOptions.ARG_PARAMETERS, addDefaultARGParameters);
		options.put(ExportOptions.PLANNING_UNCERTAINTIES, Arrays.asList(newUncertaintyGroup));
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPlanningApp.class).generateStructurePlanning(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_UNCERTAINTY_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_UNCERTAINTY_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(newUncertaintyGroup.getName(),
				((Map<?, ?>) ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
						.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(1, ((List<?>) ((Map<?, ?>) ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
				.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
	}

	@Test
	void test_generateStructurePlanning_IncludeDecision_Empty() throws CredibilityException {

		// construct
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PLANNING_INCLUDE, true);
		options.put(ExportOptions.PLANNING_DECISION_INCLUDE, true);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPlanningApp.class).generateStructurePlanning(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_DECISION_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_DECISION_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertTrue(((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).isEmpty());
	}

	@Test
	void test_generateStructurePlanning_Working_Include_Decision()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		getAppManager().getService(IImportDecisionApp.class).importDecisionSpecification(model, confFile);
		DecisionParam newDecisionParam = TestEntityFactory.getNewDecisionParam(getDaoManager(), model, null);
		DecisionParam newDecisionParam2 = TestEntityFactory.getNewDecisionParam(getDaoManager(), model, null);
		Decision newDecision = TestEntityFactory.getNewDecision(getDaoManager(), model, null, null);
		DecisionValue newDecisionValue = TestEntityFactory.getNewDecisionValue(getDaoManager(), newDecision,
				newDecisionParam, null);
		newDecisionValue.setValue("MyValue"); //$NON-NLS-1$
		TestEntityFactory.getNewDecisionValue(getDaoManager(), newDecision, newDecisionParam2, null);
		getAppManager().getService(IDecisionApplication.class).refresh(newDecision);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PLANNING_INCLUDE, true);
		options.put(ExportOptions.PLANNING_DECISION_INCLUDE, true);
		ARGParameters addDefaultARGParameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(null);
		options.put(ExportOptions.ARG_PARAMETERS, addDefaultARGParameters);
		options.put(ExportOptions.PLANNING_DECISIONS, Arrays.asList(newDecision));
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPlanningApp.class).generateStructurePlanning(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_DECISION_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_DECISION_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(newDecision.getTitle(),
				((Map<?, ?>) ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
						.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(1, ((List<?>) ((Map<?, ?>) ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
				.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
	}

}
