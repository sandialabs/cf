/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.StructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IGenericParameterApplication;
import gov.sandia.cf.application.IReportARGApplication;
import gov.sandia.cf.application.IReportARGPlanningApp;
import gov.sandia.cf.application.configuration.ExportOptions;
import gov.sandia.cf.application.configuration.arg.YmlARGStructure;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.tools.LinkTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * Manage ARG Report for Planning methods
 * 
 * @author Didier Verstraete
 *
 */
public class ReportARGPlanningApp extends AApplication implements IReportARGPlanningApp {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReportARGPlanningApp.class);

	/**
	 * The constructor
	 */
	public ReportARGPlanningApp() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ReportARGPlanningApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	/** {@inheritDoc} */
	@Override
	public void generateStructurePlanning(List<Map<String, Object>> chapters, Map<ExportOptions, Object> options)
			throws CredibilityException {

		logger.debug("Generate Planning Report"); //$NON-NLS-1$

		// Initialize
		boolean includeIntendedPurpose = options.containsKey(ExportOptions.PLANNING_INTENDEDPURPOSE_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PLANNING_INTENDEDPURPOSE_INCLUDE));
		boolean includeRequirements = options.containsKey(ExportOptions.PLANNING_REQUIREMENT_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PLANNING_REQUIREMENT_INCLUDE));
		boolean includeQoIPlanner = options.containsKey(ExportOptions.PLANNING_QOI_PLANNER_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PLANNING_QOI_PLANNER_INCLUDE));
		boolean includeUncertainty = options.containsKey(ExportOptions.PLANNING_UNCERTAINTY_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PLANNING_UNCERTAINTY_INCLUDE));
		boolean includeDecisions = options.containsKey(ExportOptions.PLANNING_DECISION_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PLANNING_DECISION_INCLUDE));

		// Intended Purpose
		if (includeIntendedPurpose) {
			// Generate intended purpose section
			List<Map<String, Object>> intendedPurposeSection = new ArrayList<>();
			generateStructureSectionsIntendedPurpose(intendedPurposeSection, options);

			// Generate intended purpose chapter
			Map<String, Object> requirementsChapter = new LinkedHashMap<>();
			requirementsChapter.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_CHAPTER);
			requirementsChapter.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_INTENDEDPURPOSE_TITLE));
			requirementsChapter.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_INTENDEDPURPOSE_STRING));
			requirementsChapter.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, intendedPurposeSection);
			chapters.add(requirementsChapter);
		}

		// System requirements
		if (includeRequirements) {
			// Generate system requirement sections
			List<Map<String, Object>> requirementsSections = new ArrayList<>();
			generateStructureSectionsPlanningRequirement(requirementsSections, options);

			// Planning system requirement chapter
			Map<String, Object> requirementsChapter = new LinkedHashMap<>();
			requirementsChapter.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_CHAPTER);
			requirementsChapter.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_REQUIREMENT_TITLE));
			requirementsChapter.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_REQUIREMENT_STRING));
			requirementsChapter.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, requirementsSections);
			chapters.add(requirementsChapter);
		}

		// QoI Planner
		if (includeQoIPlanner) {
			// Generate QoI Planner sections
			List<Map<String, Object>> qoiPlannerSections = new ArrayList<>();
			generateStructureSectionsPlanningQoIPlanner(qoiPlannerSections, options);

			// Planning QoI Planner chapter
			Map<String, Object> qoiPlannerChapter = new LinkedHashMap<>();
			qoiPlannerChapter.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_CHAPTER);
			qoiPlannerChapter.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_QOIPLANNER_TITLE));
			qoiPlannerChapter.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_QOIPLANNER_STRING));
			qoiPlannerChapter.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, qoiPlannerSections);
			chapters.add(qoiPlannerChapter);
		}

		// Uncertainty Inventory
		if (includeUncertainty) {
			// Generate uncertainty inventory sections
			List<Map<String, Object>> uncertaintySections = new ArrayList<>();
			generateStructureSectionsPlanningUncertaintyGroup(uncertaintySections, options);

			// Planning uncertainty inventory chapter
			Map<String, Object> planningChapter = new LinkedHashMap<>();
			planningChapter.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_CHAPTER);
			planningChapter.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_UNCERTAINTY_TITLE));
			planningChapter.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_UNCERTAINTY_STRING));
			planningChapter.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, uncertaintySections);
			chapters.add(planningChapter);
		}

		// Decisions
		if (includeDecisions) {
			// Generate decision sections
			List<Map<String, Object>> decisionSections = new ArrayList<>();
			generateStructureSectionsPlanningDecision(decisionSections, options);

			// Planning decision chapter
			Map<String, Object> planningChapter = new LinkedHashMap<>();
			planningChapter.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_CHAPTER);
			planningChapter.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_DECISION_TITLE));
			planningChapter.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_DECISION_STRING));
			planningChapter.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, decisionSections);
			chapters.add(planningChapter);
		}
	}

	/**
	 * Generate intended purpose string
	 * 
	 * @param parentSections the current report section
	 * @param options        the export options
	 */
	private void generateStructureSectionsIntendedPurpose(List<Map<String, Object>> parentSections,
			Map<ExportOptions, Object> options) {
		StringBuilder str = new StringBuilder();

		if (options.get(ExportOptions.INTENDED_PURPOSE) instanceof IntendedPurpose) {
			IntendedPurpose intendedPurpose = (IntendedPurpose) options.get(ExportOptions.INTENDED_PURPOSE);
			boolean first = true;

			if (!StringUtils.isBlank(intendedPurpose.getDescription())) {
				str.append(StringTools.clearHtml(intendedPurpose.getDescription()));
				first = false;
			}

			String linkPath = LinkTools.getPath(intendedPurpose.getReference());
			if (!StringUtils.isBlank(linkPath)) {
				if (!first) {
					str.append(RscTools.CARRIAGE_RETURN);
				}
				parentSections.add(getAppMgr().getService(IReportARGApplication.class).generateHyperlink(str.toString(),
						null, linkPath, linkPath));
			} else {
				parentSections
						.add(getAppMgr().getService(IReportARGApplication.class).generateParagraph(str.toString()));
			}
		}
	}

	/**
	 * Generate Planning System Requirement sections
	 * 
	 * @param parentSections the current report section
	 * @param options        the export options
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	@SuppressWarnings("unchecked")
	private void generateStructureSectionsPlanningRequirement(List<Map<String, Object>> parentSections,
			Map<ExportOptions, Object> options) throws CredibilityException {

		// Get requirements tree list
		List<SystemRequirement> requirements = (List<SystemRequirement>) options
				.get(ExportOptions.PLANNING_REQUIREMENTS);

		// For each requirements
		if (requirements != null && !requirements.isEmpty()) {
			generateStructureSectionsPlanningRequirement(parentSections,
					requirements.stream().filter(r -> r.getLevel() == 0).collect(Collectors.toList()),
					(ARGParameters) options.get(ExportOptions.ARG_PARAMETERS));
		}
	}

	/**
	 * Generate Planning System Requirement sections: recursive method to retrieve
	 * all the children.
	 * 
	 * @param sections        the map used to generate the structure file
	 * @param requirementList the requirement list
	 * @param argParameters   the arg parameters
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	private void generateStructureSectionsPlanningRequirement(List<Map<String, Object>> sections,
			List<SystemRequirement> requirementList, ARGParameters argParameters) throws CredibilityException {

		// Check data not empties
		if (requirementList == null || requirementList.isEmpty()) {
			return;
		}

		// Generate section for each requirement
		for (SystemRequirement requirement : requirementList.stream()
				.sorted(Comparator.comparing(SystemRequirement::getGeneratedId)).collect(Collectors.toList())) {

			// Initialize
			List<Map<String, Object>> subsections = new ArrayList<>();

			// Parameters and values
			List<IGenericTableValue> values = requirement.getValueList();
			if (values != null && !values.isEmpty()) {

				// Generate parameter/value paragraph
				getAppMgr().getService(IReportARGApplication.class).generateGenericValues(subsections,
						getAppMgr().getService(IGenericParameterApplication.class).sortTableValuesByParameterId(values),
						argParameters);
			}

			// recursive call for children
			if (requirement.getChildren() != null && !requirement.getChildren().isEmpty()) {
				generateStructureSectionsPlanningRequirement(subsections, requirement.getChildren(), argParameters);
			}

			// generate section and title
			Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class).generateSection(
					requirement.getStatement(), null, subsections, getAppMgr().getService(IReportARGApplication.class)
							.getSectionTypeByGenericLevel(requirement.getLevel()));

			// add to main section
			sections.add(section);
		}
	}

	/**
	 * Generate Planning QoI Planner sections
	 * 
	 * @param parentSections the current report section
	 * @param options        the export options
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	@SuppressWarnings("unchecked")
	private void generateStructureSectionsPlanningQoIPlanner(List<Map<String, Object>> parentSections,
			Map<ExportOptions, Object> options) throws CredibilityException {

		// Get QoIs requested
		Map<QuantityOfInterest, Map<ExportOptions, Object>> qois = (Map<QuantityOfInterest, Map<ExportOptions, Object>>) options
				.get(ExportOptions.PIRT_QOI_LIST);

		if (qois != null) {
			// For each quantity of interest
			for (Entry<QuantityOfInterest, Map<ExportOptions, Object>> entry : qois.entrySet()) {
				Map<ExportOptions, Object> qoiData = entry.getValue();
				generateStructureSectionPIRTQoIPlanning(qoiData, parentSections,
						(ARGParameters) options.get(ExportOptions.ARG_PARAMETERS));
			}
		}
	}

	/**
	 * Generate PIRT QoI Planning sections
	 * 
	 * @param qoiData            The QoI data list
	 * @param qoiPlannerSections the qoi section list
	 * @param argParameters      the arg parameters
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	private void generateStructureSectionPIRTQoIPlanning(Map<ExportOptions, Object> qoiData,
			List<Map<String, Object>> qoiPlannerSections, ARGParameters argParameters) throws CredibilityException {

		Object qoiObject = qoiData.get(ExportOptions.PIRT_QOI_TAG);
		QuantityOfInterest tag = null;
		if (qoiObject instanceof QuantityOfInterest) {
			tag = (QuantityOfInterest) qoiObject;
		} else if (qoiObject instanceof StructuredSelection) {
			tag = (QuantityOfInterest) ((StructuredSelection) qoiObject).getFirstElement();
		}

		// Add planning
		if (tag != null && tag.getQoiPlanningList() != null && !tag.getQoiPlanningList().isEmpty()) {

			// Initialize
			List<Map<String, Object>> subsections = new ArrayList<>();

			// Parameters and values
			List<IGenericTableValue> values = tag.getQoiPlanningList().stream().map(IGenericTableValue.class::cast)
					.collect(Collectors.toList());

			if (values != null && !values.isEmpty()) {
				// Generate parameter/value paragraph
				getAppMgr().getService(IReportARGApplication.class).generateGenericValues(subsections, values,
						argParameters);
			}

			// QoI Description
			if (!StringUtils.isBlank(tag.getDescription())) {
				Map<String, Object> sectionToAppend = new HashMap<>();
				if (!subsections.isEmpty()) {
					sectionToAppend = subsections.get(0);
				}
				getAppMgr().getService(IReportARGApplication.class)
						.prefixOrCreateParagraph(getAppMgr().getService(IReportARGApplication.class).generateLabelValue(
								RscTools.getString(RscConst.MSG_QOI_FIELD_DESCRIPTION),
								StringTools.clearHtml(tag.getDescription())), sectionToAppend);
				if (subsections.isEmpty()) {
					subsections.add(sectionToAppend);
				}
			}

			// generate section and title
			Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class)
					.generateSection(tag.getSymbol(), null, subsections);

			// add to main section
			qoiPlannerSections.add(section);
		}
	}

	/**
	 * Generate Planning Uncertainty Inventory group sections
	 * 
	 * @param sections the current report section list
	 * @param options  the export options
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	@SuppressWarnings("unchecked")
	private void generateStructureSectionsPlanningUncertaintyGroup(List<Map<String, Object>> sections,
			Map<ExportOptions, Object> options) throws CredibilityException {
		// Initialize
		List<UncertaintyGroup> uncertaintyGroupList = null;

		// Get data
		if (options.get(ExportOptions.PLANNING_UNCERTAINTIES) instanceof List) {
			uncertaintyGroupList = (List<UncertaintyGroup>) options.get(ExportOptions.PLANNING_UNCERTAINTIES);
		}

		// Check data not empties
		if (uncertaintyGroupList != null && !uncertaintyGroupList.isEmpty()) {

			// Generate section for each uncertainty group
			for (UncertaintyGroup uncertaintyGroup : uncertaintyGroupList) {

				// Initialize
				List<Map<String, Object>> subsections = new ArrayList<>();

				// Generate evidences sections
				generateStructureSectionsPlanningUncertainty(subsections, uncertaintyGroup.getUncertainties(),
						(ARGParameters) options.get(ExportOptions.ARG_PARAMETERS));

				// Add subsections
				Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class)
						.generateSection(uncertaintyGroup.getName(), null, subsections);

				// Add section
				sections.add(section);
			}
		}
	}

	/**
	 * Generate Planning Decision sections.
	 * 
	 * @param sections the map used to generate the structure file
	 * @param options  the report option containing the data
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	@SuppressWarnings("unchecked")
	private void generateStructureSectionsPlanningDecision(List<Map<String, Object>> sections,
			Map<ExportOptions, Object> options) throws CredibilityException {
		// Initialize
		List<Decision> decisionList = null;

		// Get data
		if (options.get(ExportOptions.PLANNING_DECISIONS) instanceof List) {
			decisionList = (List<Decision>) options.get(ExportOptions.PLANNING_DECISIONS);
		}

		// Check data not empties
		if (decisionList != null && !decisionList.isEmpty()) {
			generateStructureSectionsPlanningDecision(sections, decisionList,
					(ARGParameters) options.get(ExportOptions.ARG_PARAMETERS));
		}
	}

	/**
	 * Generate Planning Decision sections: recursive method to retrieve all the
	 * children.
	 * 
	 * @param sections      the map used to generate the structure file
	 * @param decisionList  the decision list
	 * @param argParameters the arg parameters
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	private void generateStructureSectionsPlanningDecision(List<Map<String, Object>> sections,
			List<Decision> decisionList, ARGParameters argParameters) throws CredibilityException {

		// Check data not empties
		if (decisionList == null || decisionList.isEmpty()) {
			return;
		}

		// Generate section for each decision
		for (Decision decision : decisionList.stream().sorted(Comparator.comparing(Decision::getGeneratedId))
				.collect(Collectors.toList())) {

			// Initialize
			List<Map<String, Object>> subsections = new ArrayList<>();

			// Parameters and values
			List<IGenericTableValue> values = decision.getValueList();
			if (values != null && !values.isEmpty()) {

				// Generate parameter/value paragraph
				getAppMgr().getService(IReportARGApplication.class).generateGenericValues(subsections,
						getAppMgr().getService(IGenericParameterApplication.class).sortTableValuesByParameterId(values),
						argParameters);
			}

			// recursive call for children
			if (decision.getChildren() != null && !decision.getChildren().isEmpty()) {
				generateStructureSectionsPlanningDecision(subsections, decision.getChildren(), argParameters);
			}

			// generate section and title
			Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class)
					.generateSection(decision.getTitle(), null, subsections, getAppMgr()
							.getService(IReportARGApplication.class).getSectionTypeByGenericLevel(decision.getLevel()));

			// add to main section
			sections.add(section);
		}
	}

	/**
	 * Generate Planning Uncertainty Inventory sections
	 * 
	 * @param subsections   the current subsection list
	 * @param uncertainties the uncertainties to parse
	 * @praram argParameters the arg parameters
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	private void generateStructureSectionsPlanningUncertainty(List<Map<String, Object>> parentSections,
			List<Uncertainty> uncertainties, ARGParameters argParameters) throws CredibilityException {
		// Uncertainties
		if (uncertainties != null && !uncertainties.isEmpty()) {
			for (Uncertainty uncertainty : uncertainties) {

				// Parameters and values
				List<IGenericTableValue> values = uncertainty.getValueList();
				if (values != null && !values.isEmpty()) {

					// Generate parameter/value paragraph
					getAppMgr().getService(IReportARGApplication.class).generateGenericValues(parentSections, values,
							argParameters);
				}
			}
		}
	}

}
