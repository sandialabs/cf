/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import gov.sandia.cf.application.pcmm.IImportPCMMApp;
import gov.sandia.cf.application.pcmm.YmlReaderPCMMSchema;
import gov.sandia.cf.application.report.IReportARGExecutionApp;
import gov.sandia.cf.application.report.IReportARGPCMMApp;
import gov.sandia.cf.constants.arg.YmlARGStructure;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 *
 * JUnit test class for the ARG Report PCMM Application Controller
 * 
 * @author Didier Verstraete
 */
@RunWith(JUnitPlatform.class)
class ReportARGPCMMAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ReportARGPCMMAppTest.class);

	@Test
	void test_generateStructurePCMM_Include_planning_Empty() throws CredibilityException {

		// construct
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_PLANNING_INCLUDE, true);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_PLANNING_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_PLANNING_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertTrue(((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).isEmpty());
	}

	@Test
	void test_generateStructurePCMM_Include_planning_Working()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		PCMMSpecification pcmmSpecs = new YmlReaderPCMMSchema().load(confFile);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_PLANNING_INCLUDE, true);
		options.put(ExportOptions.PCMM_ELEMENTS, pcmmSpecs.getElements());
		options.put(ExportOptions.PCMM_PLANNING_PARAMETERS, pcmmSpecs.getPlanningFields());
		options.put(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES, new HashMap<>());
		options.put(ExportOptions.PCMM_PLANNING_QUESTIONS, pcmmSpecs.getPlanningQuestions());
		options.put(ExportOptions.PCMM_PLANNING_QUESTION_VALUES, new HashMap<>());
		options.put(ExportOptions.PCMM_MODE, PCMMMode.DEFAULT);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_PLANNING_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_PLANNING_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(pcmmSpecs.getElements().size(),
				((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
	}

	@Test
	void test_generateStructurePCMM_Include_planning_Working_database_Default_mode()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), newPCMMElement);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), model);
		PCMMPlanningValue newPCMMPlanningValue = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(),
				newPCMMPlanningParam, newPCMMSubelement, null, null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMSubelement);
		PCMMPlanningQuestionValue newPCMMPlanningQuestionValue = TestEntityFactory
				.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_PLANNING_INCLUDE, true);
		options.put(ExportOptions.PCMM_ELEMENTS, Arrays.asList(newPCMMElement));
		options.put(ExportOptions.PCMM_PLANNING_PARAMETERS, Arrays.asList(newPCMMPlanningParam));
		options.put(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES, new HashMap<PCMMElement, List<PCMMPlanningValue>>() {
			private static final long serialVersionUID = 1L;
			{
				put(newPCMMElement, Arrays.asList(newPCMMPlanningValue));
			}
		});
		options.put(ExportOptions.PCMM_PLANNING_QUESTIONS, new HashMap<PCMMElement, List<PCMMPlanningQuestion>>() {
			private static final long serialVersionUID = 1L;
			{
				put(newPCMMElement, Arrays.asList(newPCMMPlanningQuestion));
			}
		});
		options.put(ExportOptions.PCMM_PLANNING_QUESTION_VALUES,
				new HashMap<PCMMElement, List<PCMMPlanningQuestionValue>>() {
					private static final long serialVersionUID = 1L;
					{
						put(newPCMMElement, Arrays.asList(newPCMMPlanningQuestionValue));
					}
				});
		ARGParameters addDefaultARGParameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(null);
		options.put(ExportOptions.ARG_PARAMETERS, addDefaultARGParameters);
		options.put(ExportOptions.PCMM_MODE, PCMMMode.DEFAULT);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_PLANNING_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_PLANNING_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(1, ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
		assertEquals(2,
				((List<?>) ((Map<?, ?>) ((List<?>) ((Map<?, ?>) ((List<?>) map
						.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
								.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
										.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
	}

	@Test
	void test_generateStructurePCMM_Include_planning_Working_database_Simplified_mode()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), model);
		PCMMPlanningValue newPCMMPlanningValue = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(),
				newPCMMPlanningParam, newPCMMElement, null, null);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				newPCMMElement);
		PCMMPlanningQuestionValue newPCMMPlanningQuestionValue = TestEntityFactory
				.getNewPCMMPlanningQuestionValue(getDaoManager(), newPCMMPlanningQuestion, null, null);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_PLANNING_INCLUDE, true);
		options.put(ExportOptions.PCMM_ELEMENTS, Arrays.asList(newPCMMElement));
		options.put(ExportOptions.PCMM_PLANNING_PARAMETERS, Arrays.asList(newPCMMPlanningParam));
		options.put(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES, new HashMap<PCMMElement, List<PCMMPlanningValue>>() {
			private static final long serialVersionUID = 1L;
			{
				put(newPCMMElement, Arrays.asList(newPCMMPlanningValue));
			}
		});
		options.put(ExportOptions.PCMM_PLANNING_QUESTIONS, new HashMap<PCMMElement, List<PCMMPlanningQuestion>>() {
			private static final long serialVersionUID = 1L;
			{
				put(newPCMMElement, Arrays.asList(newPCMMPlanningQuestion));
			}
		});
		options.put(ExportOptions.PCMM_PLANNING_QUESTION_VALUES,
				new HashMap<PCMMElement, List<PCMMPlanningQuestionValue>>() {
					private static final long serialVersionUID = 1L;
					{
						put(newPCMMElement, Arrays.asList(newPCMMPlanningQuestionValue));
					}
				});
		ARGParameters addDefaultARGParameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(null);
		options.put(ExportOptions.ARG_PARAMETERS, addDefaultARGParameters);
		options.put(ExportOptions.PCMM_MODE, PCMMMode.SIMPLIFIED);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_PLANNING_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_PLANNING_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(1, ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
		assertEquals(2, ((List<?>) ((Map<?, ?>) ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
				.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
	}

	@Test
	void test_generateStructurePCMM_Include_evidence_Empty() throws CredibilityException {

		// construct
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_EVIDENCE_INCLUDE, true);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_EVIDENCE_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_EVIDENCE_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertTrue(((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).isEmpty());
	}

	@Test
	void test_generateStructurePCMM_Include_evidence_Working()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());
		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		PCMMSpecification pcmmSpecs = new YmlReaderPCMMSchema().load(confFile);
		getAppManager().getService(IImportPCMMApp.class).importPCMMSpecification(model, user, confFile);

		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_EVIDENCE_INCLUDE, true);
		options.put(ExportOptions.PCMM_ELEMENTS, pcmmSpecs.getElements());
		options.put(ExportOptions.PCMM_MODE, PCMMMode.DEFAULT);

		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_EVIDENCE_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_EVIDENCE_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(pcmmSpecs.getElements().size(),
				((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
	}

	@Test
	void test_generateStructurePCMM_Include_evidence_Working_database_Default_mode()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), newPCMMElement);
		PCMMEvidence newPCMMEvidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null,
				newPCMMSubelement);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_EVIDENCE_INCLUDE, true);
		options.put(ExportOptions.PCMM_ELEMENTS, Arrays.asList(newPCMMElement));
		options.put(ExportOptions.PCMM_EVIDENCE_LIST, new HashMap<PCMMElement, List<PCMMEvidence>>() {
			private static final long serialVersionUID = 1L;
			{
				put(newPCMMElement, Arrays.asList(newPCMMEvidence));
			}
		});
		ARGParameters addDefaultARGParameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(null);
		options.put(ExportOptions.ARG_PARAMETERS, addDefaultARGParameters);
		options.put(ExportOptions.PCMM_MODE, PCMMMode.DEFAULT);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_EVIDENCE_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_EVIDENCE_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(1, ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
		assertEquals(newPCMMEvidence.getPath(),
				((Map<?, ?>) ((List<?>) ((Map<?, ?>) ((List<?>) ((Map<?, ?>) ((List<?>) map
						.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
								.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
										.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
												.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_PATH_KEY));
	}

	@Test
	void test_generateStructurePCMM_Include_evidence_Working_database_Simplified_mode()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMEvidence newPCMMEvidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null,
				newPCMMElement);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_EVIDENCE_INCLUDE, true);
		options.put(ExportOptions.PCMM_ELEMENTS, Arrays.asList(newPCMMElement));
		options.put(ExportOptions.PCMM_EVIDENCE_LIST, new HashMap<PCMMElement, List<PCMMEvidence>>() {
			private static final long serialVersionUID = 1L;
			{
				put(newPCMMElement, Arrays.asList(newPCMMEvidence));
			}
		});
		ARGParameters addDefaultARGParameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(null);
		options.put(ExportOptions.ARG_PARAMETERS, addDefaultARGParameters);
		options.put(ExportOptions.PCMM_MODE, PCMMMode.SIMPLIFIED);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_EVIDENCE_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_EVIDENCE_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(1, ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
		assertEquals(newPCMMEvidence.getPath(),
				((Map<?, ?>) ((List<?>) ((Map<?, ?>) ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY))
						.get(0)).get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
								.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_PATH_KEY));
	}

	@Test
	void test_generateStructurePCMM_Include_assessment_Empty() throws CredibilityException {

		// construct
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_ASSESSMENT_INCLUDE, true);
		options.put(ExportOptions.PCMM_MODE, PCMMMode.DEFAULT);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertTrue(((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).isEmpty());
	}

	@Test
	void test_generateStructurePCMM_Include_assessment_Working()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);
		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		PCMMSpecification pcmmSpecs = new YmlReaderPCMMSchema().load(confFile);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_ASSESSMENT_INCLUDE, true);
		options.put(ExportOptions.PCMM_ELEMENTS, pcmmSpecs.getElements());
		options.put(ExportOptions.PCMM_MODE, PCMMMode.DEFAULT);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(pcmmSpecs.getElements().size(),
				((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
	}

	@Test
	void test_generateStructurePCMM_Include_assessment_Working_database_Default_mode()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), newPCMMElement);
		PCMMLevel newPCMMLevel = TestEntityFactory.getNewPCMMLevel(getDaoManager(), newPCMMSubelement, 2);
		PCMMAssessment newPCMMAssessment = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), null, null,
				newPCMMSubelement, newPCMMLevel);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_ASSESSMENT_INCLUDE, true);
		options.put(ExportOptions.PCMM_ELEMENTS, Arrays.asList(newPCMMElement));
		options.put(ExportOptions.PCMM_ASSESSMENT_LIST, new HashMap<PCMMElement, List<PCMMAssessment>>() {
			private static final long serialVersionUID = 1L;
			{
				put(newPCMMElement, Arrays.asList(newPCMMAssessment));
			}
		});
		ARGParameters addDefaultARGParameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(null);
		options.put(ExportOptions.ARG_PARAMETERS, addDefaultARGParameters);
		options.put(ExportOptions.PCMM_MODE, PCMMMode.DEFAULT);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(1, ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
		assertEquals(1,
				((List<?>) ((Map<?, ?>) ((List<?>) ((Map<?, ?>) ((List<?>) ((Map<?, ?>) ((List<?>) map
						.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
								.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
										.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
												.get(YmlARGStructure.ARG_STRUCTURE_ITEMS_KEY)).size());
	}

	@Test
	void test_generateStructurePCMM_Include_assessment_Working_database_Simplified_mode()
			throws CredibilityException, URISyntaxException, IOException {

		// construct database
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMLevel newPCMMLevel = TestEntityFactory.getNewPCMMLevel(getDaoManager(), newPCMMElement, 2);
		PCMMAssessment newPCMMAssessment = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), null, null,
				newPCMMElement, newPCMMLevel);

		// construct options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_ASSESSMENT_INCLUDE, true);
		options.put(ExportOptions.PCMM_ELEMENTS, Arrays.asList(newPCMMElement));
		options.put(ExportOptions.PCMM_ASSESSMENT_LIST, new HashMap<PCMMElement, List<PCMMAssessment>>() {
			private static final long serialVersionUID = 1L;
			{
				put(newPCMMElement, Arrays.asList(newPCMMAssessment));
			}
		});
		ARGParameters addDefaultARGParameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(null);
		options.put(ExportOptions.ARG_PARAMETERS, addDefaultARGParameters);
		options.put(ExportOptions.PCMM_MODE, PCMMMode.SIMPLIFIED);
		List<Map<String, Object>> chapters = new ArrayList<>();

		// test
		getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);

		// validate
		assertEquals(1, chapters.size());
		Map<String, Object> map = chapters.get(0);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_TITLE),
				map.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_STRING),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(1, ((List<?>) map.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).size());
		assertEquals(1,
				((List<?>) ((Map<?, ?>) ((List<?>) ((Map<?, ?>) ((List<?>) map
						.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
								.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY)).get(0))
										.get(YmlARGStructure.ARG_STRUCTURE_ITEMS_KEY)).size());
	}

	@Test
	void test_generateStructurePCMM_Chapters_null() {
		try {
			getAppManager().getService(IReportARGPCMMApp.class).generateStructurePCMM(null,
					new HashMap<ExportOptions, Object>());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_generateStructurePCMM_Options_null() {
		try {
			getAppManager().getService(IReportARGPCMMApp.class)
					.generateStructurePCMM(new ArrayList<Map<String, Object>>(), null);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}
}
