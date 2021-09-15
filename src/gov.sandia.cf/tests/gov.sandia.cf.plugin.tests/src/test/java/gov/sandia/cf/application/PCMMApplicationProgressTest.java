/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.application.impl.PCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Didier Verstraete
 *
 *         JUnit test class for the PCMM Application Controller
 */
@RunWith(JUnitPlatform.class)
class PCMMApplicationProgressTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMApplicationProgressTest.class);

	/* ************* computeMaxProgress ************** */

	@Test
	void test_computeMaxProgress_OK() {

		PCMMSpecification configuration = mock(PCMMSpecification.class);
		when(configuration.isPcmmPlanningEnabled()).thenReturn(true);
		when(configuration.isPcmmEvidenceEnabled()).thenReturn(true);
		when(configuration.isPcmmAssessEnabled()).thenReturn(true);

		int progress = getAppManager().getService(IPCMMApplication.class).computeMaxProgress(configuration);
		assertEquals(PCMMApplication.PCMM_PROGRESS_PLANNING_DEFAULT_WEIGHT
				+ PCMMApplication.PCMM_PROGRESS_EVIDENCE_DEFAULT_WEIGHT
				+ PCMMApplication.PCMM_PROGRESS_ASSESS_DEFAULT_WEIGHT, progress);
	}

	@Test
	void test_computeMaxProgress_OnlyPlanning() {

		PCMMSpecification configuration = mock(PCMMSpecification.class);
		when(configuration.isPcmmPlanningEnabled()).thenReturn(true);
		when(configuration.isPcmmEvidenceEnabled()).thenReturn(false);
		when(configuration.isPcmmAssessEnabled()).thenReturn(false);

		int progress = getAppManager().getService(IPCMMApplication.class).computeMaxProgress(configuration);
		assertEquals(PCMMApplication.PCMM_PROGRESS_PLANNING_DEFAULT_WEIGHT, progress);
	}

	@Test
	void test_computeMaxProgress_OnlyEvidence() {

		PCMMSpecification configuration = mock(PCMMSpecification.class);
		when(configuration.isPcmmPlanningEnabled()).thenReturn(false);
		when(configuration.isPcmmEvidenceEnabled()).thenReturn(true);
		when(configuration.isPcmmAssessEnabled()).thenReturn(false);

		int progress = getAppManager().getService(IPCMMApplication.class).computeMaxProgress(configuration);
		assertEquals(PCMMApplication.PCMM_PROGRESS_EVIDENCE_DEFAULT_WEIGHT, progress);
	}

	@Test
	void test_computeMaxProgress_OnlyAssess() {

		PCMMSpecification configuration = mock(PCMMSpecification.class);
		when(configuration.isPcmmPlanningEnabled()).thenReturn(false);
		when(configuration.isPcmmEvidenceEnabled()).thenReturn(false);
		when(configuration.isPcmmAssessEnabled()).thenReturn(true);

		int progress = getAppManager().getService(IPCMMApplication.class).computeMaxProgress(configuration);
		assertEquals(PCMMApplication.PCMM_PROGRESS_ASSESS_DEFAULT_WEIGHT, progress);
	}

	@Test
	void test_computeMaxProgress_ConfigurationNull() {

		PCMMSpecification configuration = null;

		int progress = getAppManager().getService(IPCMMApplication.class).computeMaxProgress(configuration);
		assertEquals(0, progress);
	}

	/* ************ computeEvidenceMaxProgress ************ */

	@Test
	void test_computeEvidenceMaxProgress_OK_ModeDefault() {

		List<PCMMSubelement> listSub = new ArrayList<>();
		listSub.add(mock(PCMMSubelement.class));
		listSub.add(mock(PCMMSubelement.class));
		listSub.add(mock(PCMMSubelement.class));

		PCMMElement element = mock(PCMMElement.class);
		when(element.getSubElementList()).thenReturn(listSub);

		int progress = getAppManager().getService(IPCMMApplication.class).computeEvidenceMaxProgress(element,
				PCMMMode.DEFAULT);
		assertEquals(element.getSubElementList().size(), progress);
	}

	@Test
	void test_computeEvidenceMaxProgress_OK_ModeSimplified() {

		PCMMElement element = mock(PCMMElement.class);

		int progress = getAppManager().getService(IPCMMApplication.class).computeEvidenceMaxProgress(element,
				PCMMMode.SIMPLIFIED);
		assertEquals(1, progress);
	}

	@Test
	void test_computeEvidenceMaxProgress_ModeNull() {

		PCMMElement element = mock(PCMMElement.class);

		int progress = getAppManager().getService(IPCMMApplication.class).computeEvidenceMaxProgress(element, null);
		assertEquals(0, progress);
	}

	@Test
	void test_computeEvidenceMaxProgress_ElementNull() {

		int progress = getAppManager().getService(IPCMMApplication.class).computeEvidenceMaxProgress(null,
				PCMMMode.DEFAULT);
		assertEquals(0, progress);
	}

	/* ************* computeAssessMaxProgress ************* */

	@Test
	void test_computeAssessMaxProgress_OK_ModeDefault() {

		List<PCMMSubelement> listSub = new ArrayList<>();
		listSub.add(mock(PCMMSubelement.class));
		listSub.add(mock(PCMMSubelement.class));
		listSub.add(mock(PCMMSubelement.class));

		PCMMElement element = mock(PCMMElement.class);
		when(element.getSubElementList()).thenReturn(listSub);

		int progress = getAppManager().getService(IPCMMApplication.class).computeAssessMaxProgress(element,
				PCMMMode.DEFAULT);
		assertEquals(element.getSubElementList().size(), progress);
	}

	@Test
	void test_computeAssessMaxProgress_OK_ModeSimplified() {

		PCMMElement element = mock(PCMMElement.class);

		int progress = getAppManager().getService(IPCMMApplication.class).computeAssessMaxProgress(element,
				PCMMMode.SIMPLIFIED);
		assertEquals(1, progress);
	}

	@Test
	void test_computeAssessMaxProgress_ModeNull() {

		PCMMElement element = mock(PCMMElement.class);

		int progress = getAppManager().getService(IPCMMApplication.class).computeAssessMaxProgress(element, null);
		assertEquals(0, progress);
	}

	@Test
	void test_computeAssessMaxProgress_ElementNull() {

		int progress = getAppManager().getService(IPCMMApplication.class).computeAssessMaxProgress(null,
				PCMMMode.DEFAULT);
		assertEquals(0, progress);
	}

	/* ************* computePlanningMaxProgress ************* */

	@Test
	void test_computePlanningMaxProgress_OK_ModeDefault() throws CredibilityException {

		// List PCMMSubelement
		List<PCMMSubelement> listSub = new ArrayList<>();
		listSub.add(mock(PCMMSubelement.class));
		listSub.add(mock(PCMMSubelement.class));
		listSub.add(mock(PCMMSubelement.class));

		// PCMMElement
		PCMMElement element = mock(PCMMElement.class);
		when(element.getSubElementList()).thenReturn(listSub);

		// PCMM Planning Question
		List<PCMMPlanningQuestion> planningQuestions = new ArrayList<>();
		planningQuestions.add(mock(PCMMPlanningQuestion.class));
		planningQuestions.add(mock(PCMMPlanningQuestion.class));
		planningQuestions.add(mock(PCMMPlanningQuestion.class));

		// PCMM Planning Parameter
		List<PCMMPlanningParam> planningParam = new ArrayList<>();
		planningParam.add(mock(PCMMPlanningParam.class));
		planningParam.add(mock(PCMMPlanningParam.class));
		planningParam.add(mock(PCMMPlanningParam.class));

		// PCMMPlanningApplication
		IPCMMPlanningApplication pcmmPlanApp = Mockito.spy(getAppManager().getService(IPCMMPlanningApplication.class));
		when(pcmmPlanApp.getPlanningQuestionsByElement(element, PCMMMode.DEFAULT)).thenReturn(planningQuestions);
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericParameter.Filter.MODEL, element.getModel());
		when(pcmmPlanApp.getPlanningFieldsBy(filters)).thenReturn(planningParam);

		int progress = pcmmPlanApp.computePlanningMaxProgress(element, PCMMMode.DEFAULT);
		int expectedProgress = planningQuestions.size() + (listSub.size() * planningParam.size());
		assertEquals(expectedProgress, progress);
	}

	@Test
	void test_computePlanningMaxProgress_OK_ModeSimplified() throws CredibilityException {

		// PCMMElement
		PCMMElement element = mock(PCMMElement.class);

		// PCMM Planning Question
		List<PCMMPlanningQuestion> planningQuestions = new ArrayList<>();
		planningQuestions.add(mock(PCMMPlanningQuestion.class));
		planningQuestions.add(mock(PCMMPlanningQuestion.class));
		planningQuestions.add(mock(PCMMPlanningQuestion.class));

		// PCMM Planning Parameter
		List<PCMMPlanningParam> planningParam = new ArrayList<>();
		planningParam.add(mock(PCMMPlanningParam.class));
		planningParam.add(mock(PCMMPlanningParam.class));
		planningParam.add(mock(PCMMPlanningParam.class));

		// PCMMPlanningApplication
		IPCMMPlanningApplication pcmmPlanApp = Mockito.spy(getAppManager().getService(IPCMMPlanningApplication.class));
		when(pcmmPlanApp.getPlanningQuestionsByElement(element, PCMMMode.SIMPLIFIED)).thenReturn(planningQuestions);
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericParameter.Filter.MODEL, element.getModel());
		when(pcmmPlanApp.getPlanningFieldsBy(filters)).thenReturn(planningParam);

		int progress = pcmmPlanApp.computePlanningMaxProgress(element, PCMMMode.SIMPLIFIED);
		int expectedProgress = planningQuestions.size() + planningParam.size();
		assertEquals(expectedProgress, progress);
	}

	@Test
	void test_computePlanningMaxProgress_ModeNull() throws CredibilityException {

		PCMMElement element = mock(PCMMElement.class);

		int progress = getAppManager().getService(IPCMMPlanningApplication.class).computePlanningMaxProgress(element,
				null);
		assertEquals(0, progress);
	}

	@Test
	void test_computePlanningMaxProgress_ElementNull() throws CredibilityException {

		int progress = getAppManager().getService(IPCMMPlanningApplication.class).computePlanningMaxProgress(null,
				PCMMMode.DEFAULT);
		assertEquals(0, progress);
	}

	/* ************* computeEvidenceProgress ************ */

	@Test
	void test_computeEvidenceProgress_ModeDefault_3Subelements_2EvidenceFor1Subelement() {

		// List PCMMEvidence : 2 Evidence
		List<PCMMEvidence> listEvidence = new ArrayList<>();
		listEvidence.add(mock(PCMMEvidence.class));
		listEvidence.add(mock(PCMMEvidence.class));

		// List PCMMSubelement : only for 1 PCMMSubelement
		List<PCMMSubelement> listSub = new ArrayList<>();
		PCMMSubelement sub = mock(PCMMSubelement.class);
		when(sub.getEvidenceList()).thenReturn(listEvidence);
		listSub.add(sub);
		listSub.add(mock(PCMMSubelement.class));
		listSub.add(mock(PCMMSubelement.class));

		PCMMElement element = mock(PCMMElement.class);
		when(element.getSubElementList()).thenReturn(listSub);

		IPCMMApplication pcmmPlanApp = Mockito.spy(getAppManager().getService(IPCMMApplication.class));

		int progress = pcmmPlanApp.computeEvidenceProgress(element, null, PCMMMode.DEFAULT);
		int expectedProgress = 1;
		assertEquals(expectedProgress, progress);
	}

	@Test
	void test_computeEvidenceProgress_ModeSimplified_2EvidenceFor1Element() {

		// List PCMMEvidence : 2 Evidence
		List<PCMMEvidence> listEvidence = new ArrayList<>();
		listEvidence.add(mock(PCMMEvidence.class));
		listEvidence.add(mock(PCMMEvidence.class));

		PCMMElement element = mock(PCMMElement.class);
		when(element.getEvidenceList()).thenReturn(listEvidence);

		IPCMMApplication pcmmPlanApp = Mockito.spy(getAppManager().getService(IPCMMApplication.class));

		int progress = pcmmPlanApp.computeEvidenceProgress(element, null, PCMMMode.SIMPLIFIED);
		int expectedProgress = 1;
		assertEquals(expectedProgress, progress);
	}

	@Test
	void test_computeEvidenceProgress_ModeNull() {

		PCMMElement element = mock(PCMMElement.class);

		int progress = getAppManager().getService(IPCMMApplication.class).computeEvidenceProgress(element, null, null);
		assertEquals(0, progress);
	}

	@Test
	void test_computeEvidenceProgress_ElementNull() {

		int progress = getAppManager().getService(IPCMMApplication.class).computeEvidenceProgress(null, null,
				PCMMMode.DEFAULT);
		assertEquals(0, progress);
	}

	/* ************* computeAssessProgress ************* */

	@Test
	void test_computeAssessProgress_ModeDefault_3Subelements_1Assessment() {

		// List PCMMSubelement
		List<PCMMSubelement> listSub = new ArrayList<>();
		PCMMSubelement sub = mock(PCMMSubelement.class);
		listSub.add(sub);
		listSub.add(mock(PCMMSubelement.class));
		listSub.add(mock(PCMMSubelement.class));

		// List PCMMAssessment : 1 Assessement only for 1 PCMMSubelement
		List<PCMMAssessment> listAssesst = new ArrayList<>();
		PCMMAssessment assesst = mock(PCMMAssessment.class);
		when(assesst.getSubelement()).thenReturn(sub);
		listAssesst.add(assesst);

		// PCMMElement
		PCMMElement element = mock(PCMMElement.class);
		when(element.getSubElementList()).thenReturn(listSub);

		// PCMMApplication
		IPCMMApplication pcmmPlanApp = Mockito.spy(getAppManager().getService(IPCMMApplication.class));

		int progress;
		try {
			when(pcmmPlanApp.getAssessmentByElementInSubelement(element, null)).thenReturn(listAssesst);
			progress = pcmmPlanApp.computeAssessProgress(element, null, PCMMMode.DEFAULT);
			int expectedProgress = 1;
			assertEquals(expectedProgress, progress);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	void test_computeAssessProgress_ModeSimplified_2EvidenceFor1Element() throws CredibilityException {

		// PCMMElement
		PCMMElement element = mock(PCMMElement.class);

		// List PCMMAssessment : 1 Assessement only for 1 PCMMSubelement
		List<PCMMAssessment> listAssesst = new ArrayList<>();
		PCMMAssessment assesst = mock(PCMMAssessment.class);
		when(assesst.getElement()).thenReturn(element);
		listAssesst.add(assesst);

		// PCMMApplication
		IPCMMApplication pcmmPlanApp = Mockito.spy(getAppManager().getService(IPCMMApplication.class));

		when(pcmmPlanApp.getAssessmentByElement(Mockito.any(PCMMElement.class), Mockito.any(Map.class)))
				.thenReturn(listAssesst);
		int progress = pcmmPlanApp.computeAssessProgress(element, null, PCMMMode.SIMPLIFIED);
		int expectedProgress = 1;
		assertEquals(expectedProgress, progress);
	}

	@Test
	void test_computeAssessProgress_ModeNull() throws CredibilityException {

		PCMMElement element = mock(PCMMElement.class);

		int progress = getAppManager().getService(IPCMMApplication.class).computeAssessProgress(element, null, null);
		assertEquals(0, progress);
	}

	@Test
	void test_computeAssessProgress_ElementNull() throws CredibilityException {

		int progress = getAppManager().getService(IPCMMApplication.class).computeAssessProgress(null, null,
				PCMMMode.DEFAULT);
		assertEquals(0, progress);
	}

	/* ************* computePlanningProgress ************* */

	@Test
	void test_computePlanningProgress_ModeDefault_3Subelements_2Answers_1TableItem_2Questions()
			throws CredibilityException {

		// List PCMMPlanningQuestionValue
		List<PCMMPlanningQuestionValue> listQuestionValues = new ArrayList<>();
		listQuestionValues.add(mock(PCMMPlanningQuestionValue.class));
		listQuestionValues.add(mock(PCMMPlanningQuestionValue.class));

		// List PCMMPlanningValue
		List<PCMMPlanningValue> listParamValues = new ArrayList<>();
		listParamValues.add(mock(PCMMPlanningValue.class));
		listParamValues.add(mock(PCMMPlanningValue.class));

		// List PCMMPlanningTableItem
		List<PCMMPlanningTableItem> listTableItems = new ArrayList<>();
		listTableItems.add(mock(PCMMPlanningTableItem.class));

		// PCMMElement
		PCMMElement element = mock(PCMMElement.class);

		// IPCMMPlanningApplication
		IPCMMPlanningApplication pcmmPlanApp = Mockito.spy(getAppManager().getService(IPCMMPlanningApplication.class));

		Tag tag = null;
		when(pcmmPlanApp.getPlanningQuestionsValueByElement(element, PCMMMode.DEFAULT, tag))
				.thenReturn(listQuestionValues);
		when(pcmmPlanApp.getPlanningValueByElement(element, PCMMMode.DEFAULT, tag)).thenReturn(listParamValues);
		when(pcmmPlanApp.getPlanningTableItemByElement(element, PCMMMode.DEFAULT, tag)).thenReturn(listTableItems);
		int progress = pcmmPlanApp.computePlanningProgress(element, null, PCMMMode.DEFAULT);
		int expectedProgress = listQuestionValues.size() + listParamValues.size() + listTableItems.size();
		assertEquals(expectedProgress, progress);
	}

	@Test
	void test_computePlanningProgress_ModeSimplified_3Subelements_2Answers_1TableItem_2Questions()
			throws CredibilityException {

		// List PCMMPlanningQuestionValue
		List<PCMMPlanningQuestionValue> listQuestionValues = new ArrayList<>();
		listQuestionValues.add(mock(PCMMPlanningQuestionValue.class));
		listQuestionValues.add(mock(PCMMPlanningQuestionValue.class));

		// List PCMMPlanningValue
		List<PCMMPlanningValue> listParamValues = new ArrayList<>();
		listParamValues.add(mock(PCMMPlanningValue.class));
		listParamValues.add(mock(PCMMPlanningValue.class));

		// List PCMMPlanningTableItem
		List<PCMMPlanningTableItem> listTableItems = new ArrayList<>();
		listTableItems.add(mock(PCMMPlanningTableItem.class));

		// PCMMElement
		PCMMElement element = mock(PCMMElement.class);

		// IPCMMPlanningApplication
		IPCMMPlanningApplication pcmmPlanApp = Mockito.spy(getAppManager().getService(IPCMMPlanningApplication.class));

		Tag tag = null;
		when(pcmmPlanApp.getPlanningQuestionsValueByElement(element, PCMMMode.SIMPLIFIED, tag))
				.thenReturn(listQuestionValues);
		when(pcmmPlanApp.getPlanningValueByElement(element, PCMMMode.SIMPLIFIED, tag)).thenReturn(listParamValues);
		when(pcmmPlanApp.getPlanningTableItemByElement(element, PCMMMode.SIMPLIFIED, tag)).thenReturn(listTableItems);
		int progress = pcmmPlanApp.computePlanningProgress(element, null, PCMMMode.SIMPLIFIED);
		int expectedProgress = listQuestionValues.size() + listParamValues.size() + listTableItems.size();
		assertEquals(expectedProgress, progress);
	}

	@Test
	void test_computePlanningProgress_ModeNull() throws CredibilityException {

		PCMMElement element = mock(PCMMElement.class);

		int progress = getAppManager().getService(IPCMMPlanningApplication.class).computePlanningProgress(element, null,
				null);
		assertEquals(0, progress);
	}

	@Test
	void test_computePlanningProgress_ElementNull() throws CredibilityException {

		int progress = getAppManager().getService(IPCMMPlanningApplication.class).computePlanningProgress(null, null,
				PCMMMode.DEFAULT);
		assertEquals(0, progress);
	}

	/* ************* computeCurrentProgress ************* */

	@Test
	void test_computeCurrentProgress_AllFeatures() throws CredibilityException {

		// PCMMElement
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);

		// PCMMSpecification
		PCMMSpecification configuration = mock(PCMMSpecification.class);
		when(configuration.isPcmmEvidenceEnabled()).thenReturn(true);
		when(configuration.isPcmmAssessEnabled()).thenReturn(true);
		when(configuration.isPcmmPlanningEnabled()).thenReturn(true);
//		when(configuration.getMode()).thenReturn(PCMMMode.DEFAULT);

		// IPCMMApplication
		PCMMApplication pcmmApp = (PCMMApplication) Mockito.spy(getAppManager().getService(IPCMMApplication.class));

		int progress;
		when(pcmmApp.computeEvidenceProgress(element, null, configuration.getMode())).thenReturn(2);
		when(pcmmApp.computeEvidenceMaxProgress(element, configuration.getMode())).thenReturn(4);
		when(pcmmApp.computeAssessProgress(element, null, configuration.getMode())).thenReturn(3);
		when(pcmmApp.computeAssessMaxProgress(element, configuration.getMode())).thenReturn(6);
//			when(pcmmPlanApp.computePlanningProgress(element, null, configuration.getMode())).thenReturn(2);
//			when(pcmmPlanApp.computePlanningMaxProgress(element, configuration.getMode())).thenReturn(2);
		progress = pcmmApp.computeCurrentProgress(model, configuration);
		float expectedProgress = ((2F / 4F) * PCMMApplication.PCMM_PROGRESS_EVIDENCE_DEFAULT_WEIGHT)
				+ ((3F / 6F) * PCMMApplication.PCMM_PROGRESS_ASSESS_DEFAULT_WEIGHT);
//					+ ((2F / 2F) * PCMMApplication.PCMM_PROGRESS_PLANNING_DEFAULT_WEIGHT);
		assertEquals(Math.round(expectedProgress), progress);
	}

	/* ************* computeCurrentProgressByElement ************* */

	@Test
	void test_computeCurrentProgressByElement_AllFeatures() throws CredibilityException {

		// TODO mock pcmm planning progress

		// PCMMElement
		PCMMElement element = mock(PCMMElement.class);

		// PCMMSpecification
		PCMMSpecification configuration = mock(PCMMSpecification.class);
		when(configuration.isPcmmEvidenceEnabled()).thenReturn(true);
		when(configuration.isPcmmAssessEnabled()).thenReturn(true);
		when(configuration.isPcmmPlanningEnabled()).thenReturn(true);
//		when(configuration.getMode()).thenReturn(PCMMMode.DEFAULT);

		// IPCMMApplication
		PCMMApplication pcmmApp = (PCMMApplication) Mockito.spy(getAppManager().getService(IPCMMApplication.class));

		when(pcmmApp.computeEvidenceProgress(element, null, configuration.getMode())).thenReturn(2);
		when(pcmmApp.computeEvidenceMaxProgress(element, configuration.getMode())).thenReturn(4);
		when(pcmmApp.computeAssessProgress(element, null, configuration.getMode())).thenReturn(3);
		when(pcmmApp.computeAssessMaxProgress(element, configuration.getMode())).thenReturn(6);
//			when(pcmmPlanApp.computePlanningProgress(element, null, configuration.getMode())).thenReturn(2);
//			when(pcmmPlanApp.computePlanningMaxProgress(element, configuration.getMode())).thenReturn(2);
		int progress = pcmmApp.computeCurrentProgressByElement(element, null, configuration);
		float expectedProgress = ((2F / 4F) * PCMMApplication.PCMM_PROGRESS_EVIDENCE_DEFAULT_WEIGHT)
				+ ((3F / 6F) * PCMMApplication.PCMM_PROGRESS_ASSESS_DEFAULT_WEIGHT);
//					+ ((2F / 2F) * PCMMApplication.PCMM_PROGRESS_PLANNING_DEFAULT_WEIGHT);
		assertEquals(Math.round(expectedProgress), progress);
	}

	@Test
	void test_computeCurrentProgressByElement_EvidenceFeature() throws CredibilityException {

		// PCMMElement
		PCMMElement element = mock(PCMMElement.class);

		// PCMMSpecification
		PCMMSpecification configuration = mock(PCMMSpecification.class);
		when(configuration.isPcmmEvidenceEnabled()).thenReturn(true);
		when(configuration.isPcmmAssessEnabled()).thenReturn(false);
		when(configuration.isPcmmPlanningEnabled()).thenReturn(false);

		// IPCMMApplication
		PCMMApplication pcmmApp = (PCMMApplication) Mockito.spy(getAppManager().getService(IPCMMApplication.class));

		when(pcmmApp.computeEvidenceProgress(element, null, configuration.getMode())).thenReturn(3);
		when(pcmmApp.computeEvidenceMaxProgress(element, configuration.getMode())).thenReturn(6);
		int progress = pcmmApp.computeCurrentProgressByElement(element, null, configuration);
		float expectedProgress = ((3F / 6F) * PCMMApplication.PCMM_PROGRESS_EVIDENCE_DEFAULT_WEIGHT);
		assertEquals(Math.round(expectedProgress), progress);
	}

	@Test
	void test_computeCurrentProgressByElement_AssessFeature() throws CredibilityException {

		// PCMMElement
		PCMMElement element = mock(PCMMElement.class);

		// PCMMSpecification
		PCMMSpecification configuration = mock(PCMMSpecification.class);
		when(configuration.isPcmmEvidenceEnabled()).thenReturn(false);
		when(configuration.isPcmmAssessEnabled()).thenReturn(true);
		when(configuration.isPcmmPlanningEnabled()).thenReturn(false);

		// IPCMMApplication
		PCMMApplication pcmmApp = (PCMMApplication) Mockito.spy(getAppManager().getService(IPCMMApplication.class));

		when(pcmmApp.computeAssessProgress(element, null, configuration.getMode())).thenReturn(3);
		when(pcmmApp.computeAssessMaxProgress(element, configuration.getMode())).thenReturn(6);
		int progress = pcmmApp.computeCurrentProgressByElement(element, null, configuration);
		float expectedProgress = ((3F / 6F) * PCMMApplication.PCMM_PROGRESS_ASSESS_DEFAULT_WEIGHT);
		assertEquals(Math.round(expectedProgress), progress);
	}

	@Test
	void test_computeCurrentProgressByElement_ElementNull() {

		// PCMMSpecification
		PCMMSpecification configuration = mock(PCMMSpecification.class);
		when(configuration.isPcmmEvidenceEnabled()).thenReturn(false);
		when(configuration.isPcmmAssessEnabled()).thenReturn(true);
		when(configuration.isPcmmPlanningEnabled()).thenReturn(false);

		// IPCMMApplication
		PCMMApplication pcmmApp = (PCMMApplication) Mockito.spy(getAppManager().getService(IPCMMApplication.class));

		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			pcmmApp.computeCurrentProgressByElement(null, null, configuration);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_PROGRESS_COMPUTE_ELTNULL), e.getMessage());
	}

	@Test
	void test_computeCurrentProgressByElement_ConfigurationNull() {

		// PCMMElement
		PCMMElement element = mock(PCMMElement.class);

		// IPCMMApplication
		PCMMApplication pcmmApp = (PCMMApplication) Mockito.spy(getAppManager().getService(IPCMMApplication.class));

		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			pcmmApp.computeCurrentProgressByElement(element, null, null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_PROGRESS_COMPUTE_CONFNULL), e.getMessage());
	}

}
