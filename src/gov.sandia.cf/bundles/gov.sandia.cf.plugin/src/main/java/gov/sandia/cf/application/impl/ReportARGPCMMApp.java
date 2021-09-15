/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IReportARGApplication;
import gov.sandia.cf.application.IReportARGPCMMApp;
import gov.sandia.cf.application.configuration.ExportOptions;
import gov.sandia.cf.application.configuration.arg.ARGBackendDefault;
import gov.sandia.cf.application.configuration.arg.YmlARGStructure;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.tools.CFVariableResolver;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * Manage ARG Report for PCMM methods
 * 
 * @author Didier Verstraete
 *
 */
public class ReportARGPCMMApp extends AApplication implements IReportARGPCMMApp {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReportARGPCMMApp.class);

	/**
	 * The constructor
	 */
	public ReportARGPCMMApp() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ReportARGPCMMApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	/** {@inheritDoc} */
	@Override
	public void generateStructurePCMM(List<Map<String, Object>> chapters, Map<ExportOptions, Object> options)
			throws CredibilityException {

		logger.debug("Generate PCMM Report"); //$NON-NLS-1$

		if (chapters == null || options == null) {
			return;
		}

		// PCMM Planning
		if (options.containsKey(ExportOptions.PCMM_PLANNING_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PCMM_PLANNING_INCLUDE))) {

			// Generate sections
			List<Map<String, Object>> planningSections = new ArrayList<>();
			generateStructureSectionsPCMMPlanning(planningSections, options);

			// Create planning section
			Map<String, Object> planningSection = new LinkedHashMap<>();
			planningSection.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_CHAPTER);
			planningSection.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_PLANNING_TITLE));
			planningSection.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_PLANNING_STRING));
			planningSection.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, planningSections);
			chapters.add(planningSection);
		}

		// PCMM Evidence
		if (options.containsKey(ExportOptions.PCMM_EVIDENCE_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PCMM_EVIDENCE_INCLUDE))) {
			// Generate sections
			List<Map<String, Object>> evidenceSections = new ArrayList<>();
			generateStructureSectionsPCMMEvidence(evidenceSections, options);

			// Create evidence section
			Map<String, Object> evidenceSection = new LinkedHashMap<>();
			evidenceSection.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_CHAPTER);
			evidenceSection.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_EVIDENCE_TITLE));
			evidenceSection.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_EVIDENCE_STRING));
			evidenceSection.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, evidenceSections);
			chapters.add(evidenceSection);
		}

		// PCMM Assessment
		if (options.containsKey(ExportOptions.PCMM_ASSESSMENT_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PCMM_ASSESSMENT_INCLUDE))) {
			// Generate sections
			List<Map<String, Object>> assessmentSections = new ArrayList<>();
			generateStructureSectionsPCMMAssessment(assessmentSections, options);

			// Create assessment section
			Map<String, Object> assessmentSection = new LinkedHashMap<>();
			assessmentSection.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_CHAPTER);
			assessmentSection.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_TITLE));
			assessmentSection.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_STRING));
			assessmentSection.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, assessmentSections);
			chapters.add(assessmentSection);
		}
	}

	/**
	 * Generate PCMM Planning sections
	 * 
	 * @param sections the existing sections to complete
	 */
	@SuppressWarnings({ "unchecked" })
	private void generateStructureSectionsPCMMPlanning(List<Map<String, Object>> sections,
			Map<ExportOptions, Object> options) {

		if (sections == null || options == null) {
			return;
		}

		// Initialize
		List<PCMMElement> elementList = null;
		List<PCMMPlanningParam> pcmmPlanningParametersList = null;
		Map<PCMMElement, PCMMPlanningQuestion> planningQuestionsList = null;
		Map<PCMMElement, PCMMPlanningQuestionValue> planningQuestionValuesList = null;
		Map<PCMMElement, PCMMPlanningValue> planningParameterValuesList = null;

		// Get data
		if (options.get(ExportOptions.PCMM_ELEMENTS) instanceof List) {
			elementList = (List<PCMMElement>) options.get(ExportOptions.PCMM_ELEMENTS);
		}
		if (options.get(ExportOptions.PCMM_PLANNING_PARAMETERS) instanceof List) {
			pcmmPlanningParametersList = (List<PCMMPlanningParam>) options.get(ExportOptions.PCMM_PLANNING_PARAMETERS);
		}
		if (options.get(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES) instanceof Map) {
			planningParameterValuesList = (Map<PCMMElement, PCMMPlanningValue>) options
					.get(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES);
		}
		if (options.get(ExportOptions.PCMM_PLANNING_QUESTIONS) instanceof Map) {
			planningQuestionsList = (Map<PCMMElement, PCMMPlanningQuestion>) options
					.get(ExportOptions.PCMM_PLANNING_QUESTIONS);
		}
		if (options.get(ExportOptions.PCMM_PLANNING_QUESTION_VALUES) instanceof Map) {
			planningQuestionValuesList = (Map<PCMMElement, PCMMPlanningQuestionValue>) options
					.get(ExportOptions.PCMM_PLANNING_QUESTION_VALUES);
		}

		// Check data not empties
		if (elementList != null && pcmmPlanningParametersList != null && planningParameterValuesList != null
				&& planningQuestionsList != null && planningQuestionValuesList != null) {

			// Generate section for each element
			for (PCMMElement pcmmElement : elementList) {

				// Initialize
				List<Map<String, Object>> subsections = new ArrayList<>();

				// Get questions and questions' values
				List<PCMMPlanningQuestion> pcmmElementQuestions = (List<PCMMPlanningQuestion>) planningQuestionsList
						.get(pcmmElement);
				List<PCMMPlanningQuestionValue> pcmmElementQuestionValues = (List<PCMMPlanningQuestionValue>) planningQuestionValuesList
						.get(pcmmElement);

				List<PCMMPlanningValue> pcmmElementParameterValues = (List<PCMMPlanningValue>) planningParameterValuesList
						.get(pcmmElement);

				// Add PCMM element data
				if (options.get(ExportOptions.PCMM_MODE) == PCMMMode.DEFAULT) {
					generateStructureSectionsPCMMPlanningDefault(subsections, pcmmElement, pcmmElementQuestions,
							pcmmElementQuestionValues, pcmmPlanningParametersList, pcmmElementParameterValues);

				} else {
					generateStructureSectionsPCMMPlanningSimplified(subsections, pcmmElement, pcmmElementQuestions,
							pcmmElementQuestionValues, pcmmPlanningParametersList, pcmmElementParameterValues);
				}

				// Add subsections
				StringBuilder sectionTitle = new StringBuilder();
				sectionTitle.append(pcmmElement.getName());
				sectionTitle.append(" (").append(pcmmElement.getAbbreviation()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
				Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class)
						.generateSection(sectionTitle.toString(), null, subsections);

				// Add to sections
				sections.add(section);
			}
		}
	}

	/**
	 * Generate an element section
	 * 
	 * @param subsections                The sections list
	 * @param pcmmElement                The element
	 * @param pcmmElementQuestions       The questions list for the element
	 * @param pcmmElementQuestionValues  The question's values list for the element
	 * @param pcmmPlanningParametersList The parameters list
	 * @param pcmmElementParameterValues The parameter's values list for the element
	 */
	private void generateStructureSectionsPCMMPlanningDefault(List<Map<String, Object>> subsections,
			PCMMElement pcmmElement, List<PCMMPlanningQuestion> pcmmElementQuestions,
			List<PCMMPlanningQuestionValue> pcmmElementQuestionValues,
			List<PCMMPlanningParam> pcmmPlanningParametersList, List<PCMMPlanningValue> pcmmElementParameterValues) {

		if (subsections == null || pcmmElement == null) {
			return;
		}

		// Get sub-elements
		if (pcmmElement.getSubElementList() != null && !pcmmElement.getSubElementList().isEmpty()) {
			for (PCMMSubelement pcmmSubelement : pcmmElement.getSubElementList()) {
				// Sub-element sections
				List<Map<String, Object>> subsubsections = new ArrayList<>();

				// Generate questions
				generateStructureSubsectionDefaultQuestion(subsubsections, pcmmSubelement, pcmmElementQuestions,
						pcmmElementQuestionValues);

				// Generate parameters
				generateStructureSubsectionDefaultParameter(subsubsections, pcmmSubelement, pcmmPlanningParametersList,
						pcmmElementParameterValues);

				// Add sub-section
				Map<String, Object> subsection = getAppMgr().getService(IReportARGApplication.class)
						.generateSubSection(pcmmSubelement.getName(), null, subsubsections);
				subsections.add(subsection);
			}
		}
	}

	/**
	 * Generate element structure (PCMMMode.SIMPLIFIED)
	 * 
	 * @param subsections
	 * @param pcmmElement
	 * @param pcmmElementQuestions
	 * @param pcmmElementQuestionValues
	 * @param pcmmPlanningParametersList
	 * @param pcmmElementParameterValues
	 */
	private void generateStructureSectionsPCMMPlanningSimplified(List<Map<String, Object>> subsections,
			PCMMElement pcmmElement, List<PCMMPlanningQuestion> pcmmElementQuestions,
			List<PCMMPlanningQuestionValue> pcmmElementQuestionValues,
			List<PCMMPlanningParam> pcmmPlanningParametersList, List<PCMMPlanningValue> pcmmElementParameterValues) {

		// Generate questions
		generateStructureSubsectionSimplifiedQuestion(subsections, pcmmElement, pcmmElementQuestions,
				pcmmElementQuestionValues);

		// Generate parameters
		generateStructureSubsectionSimplifiedParameter(subsections, pcmmElement, pcmmPlanningParametersList,
				pcmmElementParameterValues);
	}

	/**
	 * Generate PCMM Evidence sections
	 * 
	 * @param sections the current report sections
	 * @param options  the export options
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	@SuppressWarnings("unchecked")
	private void generateStructureSectionsPCMMEvidence(List<Map<String, Object>> sections,
			Map<ExportOptions, Object> options) throws CredibilityException {

		if (sections == null || options == null) {
			return;
		}

		// Initialize
		List<PCMMElement> elementList = null;

		// Get data
		if (options.get(ExportOptions.PCMM_ELEMENTS) instanceof List) {
			elementList = (List<PCMMElement>) options.get(ExportOptions.PCMM_ELEMENTS);
		}

		// Check data not empties
		if (elementList != null && !elementList.isEmpty()) {

			// Generate section for each element
			for (PCMMElement pcmmElement : elementList) {

				// Initialize
				List<Map<String, Object>> subsections = new ArrayList<>();

				// Get evidences
				List<PCMMEvidence> evidence = null;
				Map<PCMMElement, List<PCMMEvidence>> evidencesByElement = (Map<PCMMElement, List<PCMMEvidence>>) options
						.get(ExportOptions.PCMM_EVIDENCE_LIST);
				if (evidencesByElement != null) {
					evidence = evidencesByElement.get(pcmmElement);
				}
				ARGParameters argParameters = (ARGParameters) options.get(ExportOptions.ARG_PARAMETERS);

				// Generate evidences sections
				if (options.get(ExportOptions.PCMM_MODE) == PCMMMode.DEFAULT) {
					generateStructureSectionsPCMMEvidenceDefault(subsections, pcmmElement, evidence, argParameters);

				} else {
					generateStructureSectionsPCMMEvidenceSimplified(subsections, pcmmElement, evidence, argParameters);
				}

				// Add subsections
				StringBuilder sectionTitle = new StringBuilder();
				sectionTitle.append(pcmmElement.getName());
				sectionTitle.append(" (").append(pcmmElement.getAbbreviation()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
				Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class)
						.generateSection(sectionTitle.toString(), null, subsections);

				// Add section
				sections.add(section);
			}
		}
	}

	/**
	 * Generate sub-element sections evidence (PCMMMode.DEFAULT)
	 * 
	 * @param subsections the current report sections
	 * @param pcmmElement the pcmm element associated
	 * @param evidence    the evidence to generate
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	private void generateStructureSectionsPCMMEvidenceDefault(List<Map<String, Object>> subsections,
			PCMMElement pcmmElement, List<PCMMEvidence> evidence, ARGParameters argParameters)
			throws CredibilityException {

		if (subsections == null || pcmmElement == null || evidence == null) {
			return;
		}

		// Get sub-elements
		if (pcmmElement.getSubElementList() != null && !pcmmElement.getSubElementList().isEmpty()) {
			for (PCMMSubelement pcmmSubelement : pcmmElement.getSubElementList()) {
				// Sub-element sections
				List<Map<String, Object>> evidenceSections = new ArrayList<>();

				// Generate evidences
				for (PCMMEvidence pcmmEvidence : evidence) {
					if (pcmmEvidence.getSubelement().getId().equals(pcmmSubelement.getId())) {
						generateStructureSectionPCMMEvidence(evidenceSections, pcmmEvidence, argParameters);
					}
				}

				// Add sub-section
				Map<String, Object> subsection = getAppMgr().getService(IReportARGApplication.class)
						.generateSubSection(pcmmSubelement.getName(), null, evidenceSections);
				subsections.add(subsection);
			}
		}
	}

	/**
	 * Generate sub-element sections evidence (PCMMMode.SIMPLIFIED)
	 * 
	 * @param subsections the current report sections
	 * @param pcmmElement the pcmm element associated
	 * @param evidence    the evidence to generate
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	private void generateStructureSectionsPCMMEvidenceSimplified(List<Map<String, Object>> evidenceSections,
			PCMMElement pcmmElement, List<PCMMEvidence> evidence, ARGParameters argParameters)
			throws CredibilityException {

		if (evidenceSections == null || pcmmElement == null || evidence == null) {
			return;
		}

		// Generate evidences
		for (PCMMEvidence pcmmEvidence : evidence) {
			if (pcmmEvidence.getElement().getId().equals(pcmmElement.getId())) {
				generateStructureSectionPCMMEvidence(evidenceSections, pcmmEvidence, argParameters);
			}
		}
	}

	/**
	 * Generate evidence section with evidence values
	 * 
	 * @param evidenceSections
	 * @param pcmmEvidence
	 * @param argParameters    the arg parameters
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	private void generateStructureSectionPCMMEvidence(List<Map<String, Object>> evidenceSections,
			PCMMEvidence pcmmEvidence, ARGParameters argParameters) throws CredibilityException {

		if (evidenceSections == null || argParameters == null || pcmmEvidence == null) {
			return;
		}

		List<Map<String, Object>> listSection = new ArrayList<>();

		// Display evidence description
		if (!StringUtils.isBlank(pcmmEvidence.getDescription())) {
			Map<String, Object> evidenceDescription = getAppMgr().getService(IReportARGApplication.class)
					.generateParagraph(StringTools.clearHtml(pcmmEvidence.getDescription()));
			listSection.add(evidenceDescription);
		}

		// Display evidence section

		String linkSection = !StringUtils.isBlank(pcmmEvidence.getSection())
				? MessageFormat.format(" ({0})", pcmmEvidence.getSection()) //$NON-NLS-1$
				: null;

		// Display file path from type
		String linkPath = null;
		if (pcmmEvidence.getType().equals(FormFieldType.LINK_FILE)) {
			// Display relative path
			linkPath = getAppMgr().getService(IReportARGApplication.class).getLinkPathRelativeToOutputDir(argParameters,
					pcmmEvidence.getPath());
		} else {
			// Display URL
			linkPath = pcmmEvidence.getPath();
		}

		if (linkPath != null) {
			// #434: if the inline word doc option is selected and it is a word doc and the
			// word backend is selected, inline the
			// document into the report
			if (new File(CFVariableResolver.resolveAll(argParameters.getOutput()), linkPath).isFile()
					&& FileTools.isWordDocument(linkPath) && Boolean.TRUE.equals(argParameters.getInlineWordDoc())
					&& argParameters.getBackendType().equals(ARGBackendDefault.WORD.getBackend())) {
				listSection.add(getAppMgr().getService(IReportARGApplication.class).generateInlining(null, linkPath));
			} else {
				listSection.add(getAppMgr().getService(IReportARGApplication.class).generateHyperlink(null, linkSection,
						linkPath, linkPath));
			}
		} else {
			listSection.add(getAppMgr().getService(IReportARGApplication.class)
					.generateParagraph(getAppMgr().getService(IReportARGApplication.class).generateLabelValue(null,
							pcmmEvidence.getPath() + " " + linkSection))); //$NON-NLS-1$
		}

		// add it into sub-section
		evidenceSections.addAll(listSection);
	}

	/**
	 * Generate PCMM Assessment sections
	 * 
	 * @param sections
	 * @param options
	 */
	@SuppressWarnings("unchecked")
	private void generateStructureSectionsPCMMAssessment(List<Map<String, Object>> sections,
			Map<ExportOptions, Object> options) {

		if (sections == null || options == null) {
			return;
		}

		// Initialize
		List<PCMMElement> elementList = null;

		// Get data
		if (options.get(ExportOptions.PCMM_ELEMENTS) instanceof List) {
			elementList = (List<PCMMElement>) options.get(ExportOptions.PCMM_ELEMENTS);
		}

		// Check data not empties
		if (elementList != null && !elementList.isEmpty()) {

			// Generate section for each element
			for (PCMMElement pcmmElement : elementList) {

				// Initialize
				List<Map<String, Object>> subsections = new ArrayList<>();

				// Get assessments
				List<PCMMAssessment> assessments = null;
				Map<PCMMElement, List<PCMMAssessment>> assessmentsByElement = (Map<PCMMElement, List<PCMMAssessment>>) options
						.get(ExportOptions.PCMM_ASSESSMENT_LIST);
				if (assessmentsByElement != null) {
					assessments = assessmentsByElement.get(pcmmElement);
				}

				// Generate assessments sections
				if (options.get(ExportOptions.PCMM_MODE) == PCMMMode.DEFAULT) {
					generateStructureSectionsPCMMAssessmentDefault(subsections, pcmmElement, assessments);

				} else {
					generateStructureSectionsPCMMAssessmentSimplified(subsections, pcmmElement, assessments);
				}

				// Add subsections
				StringBuilder sectionTitle = new StringBuilder();
				sectionTitle.append(pcmmElement.getName());
				sectionTitle.append(" (").append(pcmmElement.getAbbreviation()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
				Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class)
						.generateSection(sectionTitle.toString(), null, subsections);

				// Add section
				sections.add(section);
			}
		}
	}

	/**
	 * Generate sub-element sections assessment (PCMMMode.DEFAULT)
	 * 
	 * @param subsections
	 * @param pcmmElement
	 * @param assessments
	 */
	private void generateStructureSectionsPCMMAssessmentDefault(List<Map<String, Object>> subsections,
			PCMMElement pcmmElement, List<PCMMAssessment> assessments) {

		if (subsections == null || pcmmElement == null || assessments == null) {
			return;
		}

		// Get sub-elements
		if (pcmmElement.getSubElementList() != null && !pcmmElement.getSubElementList().isEmpty()) {
			for (PCMMSubelement pcmmSubelement : pcmmElement.getSubElementList()) {
				// Sub-element sections
				List<Map<String, Object>> assessmentSections = new ArrayList<>();

				// Generate assessments
				for (PCMMAssessment pcmmAssessment : assessments) {
					if (pcmmAssessment.getSubelement().getId().equals(pcmmSubelement.getId())) {
						generateStructureSectionPCMMAssessment(assessmentSections, pcmmAssessment);
					}
				}

				// Add sub-section
				Map<String, Object> subsection = getAppMgr().getService(IReportARGApplication.class)
						.generateSubSection(pcmmSubelement.getName(), null, assessmentSections);
				subsections.add(subsection);
			}
		}

	}

	/**
	 * Generate sub-element sections assessment (PCMMMode.SIMPLIFIED)
	 * 
	 * @param subsections
	 * @param pcmmElement
	 * @param assessments
	 */
	private void generateStructureSectionsPCMMAssessmentSimplified(List<Map<String, Object>> assessmentSections,
			PCMMElement pcmmElement, List<PCMMAssessment> assessments) {

		if (assessments == null || pcmmElement == null) {
			return;
		}

		// Generate assessments
		for (PCMMAssessment pcmmAssessment : assessments) {
			if (pcmmAssessment.getElement().getId().equals(pcmmElement.getId())) {
				generateStructureSectionPCMMAssessment(assessmentSections, pcmmAssessment);
			}
		}
	}

	/**
	 * Generate assessment section with assessment values
	 * 
	 * @param assessmentSections
	 * @param pcmmAssessment
	 */
	private void generateStructureSectionPCMMAssessment(List<Map<String, Object>> assessmentSections,
			PCMMAssessment pcmmAssessment) {

		if (assessmentSections == null || pcmmAssessment == null) {
			return;
		}

		// Initialize
		List<Map<String, Object>> assessmentValues = new ArrayList<>();

		// Add assessment description
		if (!StringUtils.isBlank(pcmmAssessment.getComment())) {
			Map<String, Object> assessmentDescription = new LinkedHashMap<>();
			assessmentDescription.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_VALUE_COMMENT,
							StringTools.clearHtml(pcmmAssessment.getComment())));
			assessmentValues.add(assessmentDescription);
		}

		// Add assessment level
		if (pcmmAssessment.getLevel() != null && !StringUtils.isBlank(pcmmAssessment.getLevel().getName())) {
			Map<String, Object> assessmentPath = new LinkedHashMap<>();
			assessmentPath.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY, RscTools.getString(
					RscConst.MSG_ARG_REPORT_PCMM_ASSESSMENT_VALUE_LEVEL, pcmmAssessment.getLevel().getName()));
			assessmentValues.add(assessmentPath);
		}

		// Add data to assessment values to assessment section
		if (!assessmentValues.isEmpty()) {
			Map<String, Object> assessmentItems = new LinkedHashMap<>();
			assessmentItems.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_ITEMIZE);
			assessmentItems.put(YmlARGStructure.ARG_STRUCTURE_ITEMS_KEY, assessmentValues);

			// add Question section into sub-element section
			assessmentSections.add(assessmentItems);
		}
	}

	/**
	 * Generate sub-element section questions (PCMMMode.DEFAULT)
	 * 
	 * @param subsectionSections
	 * @param pcmmElement
	 * @param pcmmSubelement
	 * @param pcmmElementQuestions
	 * @param pcmmElementQuestionValues
	 */
	private void generateStructureSubsectionDefaultQuestion(List<Map<String, Object>> subsectionSections,
			PCMMSubelement pcmmSubelement, List<PCMMPlanningQuestion> pcmmElementQuestions,
			List<PCMMPlanningQuestionValue> pcmmElementQuestionValues) {

		if (subsectionSections == null || pcmmSubelement == null || pcmmElementQuestions == null
				|| pcmmElementQuestionValues == null || pcmmElementQuestionValues.isEmpty()) {
			return;
		}

		// Questions
		List<Map<String, Object>> questionList = new ArrayList<>();
		for (PCMMPlanningQuestion pcmmElementQuestion : pcmmElementQuestions.stream().filter(Objects::nonNull)
				.filter(v -> v.getSubelement() != null)
				.filter(v -> v.getSubelement().getId().equals(pcmmSubelement.getId())).collect(Collectors.toList())) {
			// Set an empty answer to the question
			String answer = RscTools.empty();

			// Get corresponding answer
			for (PCMMPlanningQuestionValue pcmmElementQuestionValue : pcmmElementQuestionValues.stream()
					.filter(Objects::nonNull).filter(v -> !StringUtils.isBlank(v.getValue()))
					.filter(v -> v.getParameter() != null && v.getParameter().getId() != null)
					.filter(v -> v.getParameter().getId().equals(pcmmElementQuestion.getId()))
					.collect(Collectors.toList())) {
				// Set the answer to the question
				answer = StringTools.clearHtml(pcmmElementQuestionValue.getValue());
			}

			// Add question with answers
			if (!StringUtils.isBlank(answer)) {
				Map<String, Object> q = new LinkedHashMap<>();
				q.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
						pcmmElementQuestion.getName() + RscTools.COLON + RscTools.CARRIAGE_RETURN + answer);
				questionList.add(q);
			}
		}

		// Add data to question section
		if (!questionList.isEmpty()) {
			Map<String, Object> questionSection = new LinkedHashMap<>();
			questionSection.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_ITEMIZE);
			questionSection.put(YmlARGStructure.ARG_STRUCTURE_ITEMS_KEY, questionList);

			// add Question section into sub-element section
			subsectionSections.add(questionSection);
		}
	}

	/**
	 * Generate sub-element section parameters (PCMMMode.DEFAULT)
	 * 
	 * @param subsectionSections
	 * @param pcmmElement
	 * @param pcmmSubelement
	 * @param pcmmPlanningParametersList
	 * @param pcmmElementParameterValues
	 */
	private void generateStructureSubsectionDefaultParameter(List<Map<String, Object>> subsectionSections,
			PCMMSubelement pcmmSubelement, List<PCMMPlanningParam> pcmmPlanningParametersList,
			List<PCMMPlanningValue> pcmmElementParameterValues) {

		if (subsectionSections == null || pcmmSubelement == null || pcmmSubelement.getId() == null
				|| pcmmPlanningParametersList == null || pcmmElementParameterValues == null
				|| pcmmElementParameterValues.isEmpty()) {
			return;
		}

		// For each parameters
		for (PCMMPlanningParam planningParameter : pcmmPlanningParametersList) {

			// List values
			List<Map<String, Object>> parameterValueList = new ArrayList<>();

			// For each parameters value
			// Parameter value for the current sub-element
			for (PCMMPlanningValue planningParameterValue : pcmmElementParameterValues.stream().filter(Objects::nonNull)
					.filter(v -> !StringUtils.isBlank(v.getValue()))
					.filter(v -> v.getSubelement() != null && v.getParameter() != null)
					.filter(v -> pcmmSubelement.getId().equals(v.getSubelement().getId())
							&& planningParameter.getId().equals(v.getParameter().getId()))
					.collect(Collectors.toList())) {
				// Add question with answers
				Map<String, Object> parameterValueItem = new LinkedHashMap<>();
				parameterValueItem.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
						StringTools.clearHtml(planningParameterValue.getValue()));
				parameterValueList.add(parameterValueItem);
			}

			// Parameters data
			List<Map<String, Object>> parameterValueItemList = new ArrayList<>();
			if (!parameterValueList.isEmpty()) {
				Map<String, Object> parameterValueItem = new LinkedHashMap<>();
				parameterValueItem.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_ITEMIZE);
				parameterValueItem.put(YmlARGStructure.ARG_STRUCTURE_ITEMS_KEY, parameterValueList);
				parameterValueItemList.add(parameterValueItem);
			}

			// Add section parameter
			if (!parameterValueItemList.isEmpty()) {
				Map<String, Object> parameterSection = new LinkedHashMap<>();
				parameterSection.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH);
				parameterSection.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
						planningParameter.getName() + RscTools.COLON);
				parameterSection.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, parameterValueItemList);

				// Add add to sections
				subsectionSections.add(parameterSection);
			}
		}
	}

	/**
	 * Generate element section questions (PCMMMode.SIMPLIFIED)
	 * 
	 * @param subsectionSections
	 * @param pcmmElement
	 * @param pcmmElementQuestions
	 * @param pcmmElementQuestionValues
	 */
	private static void generateStructureSubsectionSimplifiedQuestion(List<Map<String, Object>> subsectionSections,
			PCMMElement pcmmElement, List<PCMMPlanningQuestion> pcmmElementQuestions,
			List<PCMMPlanningQuestionValue> pcmmElementQuestionValues) {

		if (subsectionSections == null || pcmmElement == null || pcmmElementQuestions == null
				|| pcmmElementQuestionValues == null || pcmmElementQuestionValues.isEmpty()) {
			return;
		}

		// Questions
		List<Map<String, Object>> questionList = new ArrayList<>();
		for (PCMMPlanningQuestion pcmmElementQuestion : pcmmElementQuestions.stream().filter(Objects::nonNull)
				.filter(v -> v.getElement() != null).filter(v -> v.getElement().getId().equals(pcmmElement.getId()))
				.collect(Collectors.toList())) {

			// Set an empty answer to the question
			String answer = RscTools.empty();

			// Get corresponding answer
			for (PCMMPlanningQuestionValue pcmmElementQuestionValue : pcmmElementQuestionValues.stream()
					.filter(Objects::nonNull).filter(v -> !StringUtils.isBlank(v.getValue()))
					.filter(v -> v.getParameter() != null && v.getParameter().getId() != null)
					.filter(v -> v.getParameter().getId().equals(pcmmElementQuestion.getId()))
					.collect(Collectors.toList())) {
				// Set the answer to the question
				answer = StringTools.clearHtml(pcmmElementQuestionValue.getValue());
			}

			// Add question with answers
			if (!StringUtils.isBlank(answer)) {
				Map<String, Object> q = new LinkedHashMap<>();
				q.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
						pcmmElementQuestion.getName() + RscTools.COLON + RscTools.CARRIAGE_RETURN + answer);
				questionList.add(q);
			}
		}

		// Add data to question section
		if (!questionList.isEmpty()) {
			Map<String, Object> questionSection = new LinkedHashMap<>();
			questionSection.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_ITEMIZE);
			questionSection.put(YmlARGStructure.ARG_STRUCTURE_ITEMS_KEY, questionList);

			// add Question section into sub-element section
			subsectionSections.add(questionSection);
		}
	}

	/**
	 * Generate element section parameters (PCMMMode.SIMPLIFIED)
	 * 
	 * @param subsectionSections
	 * @param pcmmElement
	 * @param pcmmPlanningParametersList
	 * @param pcmmElementParameterValues
	 */
	private void generateStructureSubsectionSimplifiedParameter(List<Map<String, Object>> subsectionSections,
			PCMMElement pcmmElement, List<PCMMPlanningParam> pcmmPlanningParametersList,
			List<PCMMPlanningValue> pcmmElementParameterValues) {

		if (subsectionSections == null || pcmmElement == null || pcmmPlanningParametersList == null
				|| pcmmElementParameterValues == null || pcmmElementParameterValues.isEmpty()) {
			return;
		}

		// For each parameters
		for (PCMMPlanningParam planningParameter : pcmmPlanningParametersList) {

			// List values
			List<Map<String, Object>> parameterValueList = new ArrayList<>();

			// For each parameters value
			// Parameter value for the current sub-element
			for (PCMMPlanningValue planningParameterValue : pcmmElementParameterValues.stream().filter(Objects::nonNull)
					.filter(v -> !StringUtils.isBlank(v.getValue()))
					.filter(v -> v.getElement() != null && v.getParameter() != null)
					.filter(v -> pcmmElement.getId().equals(v.getElement().getId())
							&& planningParameter.getId().equals(v.getParameter().getId()))
					.collect(Collectors.toList())) {

				// Add question with answers
				Map<String, Object> parameterValueItem = new LinkedHashMap<>();
				parameterValueItem.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
						StringTools.clearHtml(planningParameterValue.getValue()));
				parameterValueList.add(parameterValueItem);
			}

			// Parameters data
			List<Map<String, Object>> parameterValueItemList = new ArrayList<>();
			if (!parameterValueList.isEmpty()) {
				Map<String, Object> parameterValueItem = new LinkedHashMap<>();
				parameterValueItem.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_ITEMIZE);
				parameterValueItem.put(YmlARGStructure.ARG_STRUCTURE_ITEMS_KEY, parameterValueList);
				parameterValueItemList.add(parameterValueItem);
			}

			// Add section parameter
			if (!parameterValueItemList.isEmpty()) {
				Map<String, Object> parameterSection = new LinkedHashMap<>();
				parameterSection.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH);
				parameterSection.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
						planningParameter.getName() + RscTools.COLON);
				parameterSection.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, parameterValueItemList);

				// Add add to sections
				subsectionSections.add(parameterSection);
			}
		}
	}
}
