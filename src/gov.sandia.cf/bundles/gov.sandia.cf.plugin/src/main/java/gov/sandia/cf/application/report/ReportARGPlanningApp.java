/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.StructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.tools.GenericParameterTools;
import gov.sandia.cf.constants.arg.YmlARGStructure;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.comparator.StringWithNumberAndNullableComparator;
import gov.sandia.cf.model.dto.configuration.ParameterLinkGson;
import gov.sandia.cf.tools.CFVariableResolver;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.GsonTools;
import gov.sandia.cf.tools.LinkTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

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
		boolean includeIntendedPurpose = options.containsKey(ExportOptions.INTENDEDPURPOSE_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.INTENDEDPURPOSE_INCLUDE));
		boolean includeRequirements = options.containsKey(ExportOptions.SYSTEM_REQUIREMENT_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.SYSTEM_REQUIREMENT_INCLUDE));
		boolean includeQoIPlanner = options.containsKey(ExportOptions.QOI_PLANNER_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.QOI_PLANNER_INCLUDE));
		boolean includeUncertainty = options.containsKey(ExportOptions.PLANNING_UNCERTAINTY_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PLANNING_UNCERTAINTY_INCLUDE));
		boolean includeDecisions = options.containsKey(ExportOptions.DECISION_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.DECISION_INCLUDE));

		// Intended Purpose
		if (includeIntendedPurpose) {
			// Generate intended purpose section
			List<Map<String, Object>> intendedPurposeSection = new ArrayList<>();
			generateStructureSectionsIntendedPurpose(intendedPurposeSection, options);

			// Generate intended purpose chapter
			Map<String, Object> intendedPurposeChapter = new LinkedHashMap<>();
			intendedPurposeChapter.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_CHAPTER);
			intendedPurposeChapter.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_INTENDEDPURPOSE_TITLE));
			intendedPurposeChapter.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_INTENDEDPURPOSE_STRING));
			intendedPurposeChapter.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, intendedPurposeSection);
			chapters.add(intendedPurposeChapter);
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
	 * Generate intended purpose string.
	 *
	 * @param parentSections the current report section
	 * @param options        the export options
	 * @throws CredibilityException the credibility exception
	 */
	private void generateStructureSectionsIntendedPurpose(List<Map<String, Object>> parentSections,
			Map<ExportOptions, Object> options) throws CredibilityException {

		if (options.get(ExportOptions.INTENDED_PURPOSE) instanceof IntendedPurpose) {

			IntendedPurpose intendedPurpose = (IntendedPurpose) options.get(ExportOptions.INTENDED_PURPOSE);

			// add description
			if (!StringUtils.isBlank(intendedPurpose.getDescription())) {
				parentSections.add(getAppMgr().getService(IReportARGApplication.class)
						.generateHtmlParagraph(intendedPurpose.getDescription()));
			}

			// add link
			if (!StringUtils.isBlank(intendedPurpose.getReference())) {
				generateIntendedPurposeLink(parentSections, options);
			}
		}
	}

	/**
	 * Generate intended purpose link.
	 *
	 * @param parentSections the parent sections
	 * @param options        the options
	 * @throws CredibilityException the credibility exception
	 */
	private void generateIntendedPurposeLink(List<Map<String, Object>> parentSections,
			Map<ExportOptions, Object> options) throws CredibilityException {

		IntendedPurpose intendedPurpose = (IntendedPurpose) options.get(ExportOptions.INTENDED_PURPOSE);
		ARGParameters argParameters = (ARGParameters) options.get(ExportOptions.ARG_PARAMETERS);

		if (intendedPurpose == null) {
			return;
		}

		ParameterLinkGson linkData = GsonTools.getFromGson(intendedPurpose.getReference(), ParameterLinkGson.class);

		// do not generate anything if the link value is null or empty
		// (https://gitlab.com/iwf/cf/-/issues/467)
		if (linkData != null && !StringUtils.isBlank(linkData.value)) {

			// if it's a file
			if (FormFieldType.LINK_FILE.equals(linkData.type)) {

				String linkPath = getAppMgr().getService(IReportARGApplication.class).getLinkPathRelativeToOutputDir(
						argParameters, LinkTools.getPath(intendedPurpose.getReference()));

				// if the file is an image, generate image
				if (argParameters != null && !StringUtils.isBlank(linkPath)
						&& new File(CFVariableResolver.resolveAll(argParameters.getOutput()), linkPath).isFile()
						&& FileTools.isImage(linkPath)) {

					parentSections.add(getAppMgr().getService(IReportARGApplication.class).generateImage(null, linkPath,
							getLinkCaption(intendedPurpose), null));
				}

				// otherwise generate paragraph
				else {
					Map<String, Object> generateGenericValueHyperlink = getAppMgr()
							.getService(IReportARGApplication.class).generateHyperlink(null, null, linkPath, linkPath);
					if (linkPath != null) {
						parentSections.add(generateGenericValueHyperlink);
					}
				}
			}

			// otherwise it's an URL
			else {
				parentSections.add(getAppMgr().getService(IReportARGApplication.class).generateHyperlink(null, null,
						linkData.value, linkData.value));
			}

		}
	}

	/**
	 * Gets the link caption.
	 *
	 * @param value the value
	 * @param item  the item
	 * @return the link caption
	 */
	private String getLinkCaption(IntendedPurpose intendedPurpose) {

		if (intendedPurpose == null) {
			return null;
		}

		ParameterLinkGson linkData = GsonTools.getFromGson(intendedPurpose.getReference(), ParameterLinkGson.class);

		if (linkData != null && FormFieldType.LINK_FILE.equals(linkData.type) && FileTools.isImage(linkData.value)) {
			if (StringUtils.isBlank(linkData.caption)) {
				return RscTools.getString(RscConst.MSG_ARG_REPORT_PLANNING_INTENDEDPURPOSE_REFERENCE_CAPTION);
			} else {
				return linkData.caption;
			}
		}

		return null;
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
				.get(ExportOptions.SYSTEM_REQUIREMENT_LIST);

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
		for (SystemRequirement requirement : requirementList.stream().sorted(
				Comparator.comparing(SystemRequirement::getGeneratedId, new StringWithNumberAndNullableComparator()))
				.collect(Collectors.toList())) {

			// Initialize
			List<Map<String, Object>> subsections = new ArrayList<>();

			// Parameters and values
			List<IGenericTableValue> values = requirement.getValueList();
			if (values != null && !values.isEmpty()) {

				// Generate parameter/value paragraph
				values = GenericParameterTools.sortTableValuesByParameterId(values);
				getAppMgr().getService(IReportARGApplication.class).generateGenericValues(subsections, requirement,
						values, argParameters);
			}

			// recursive call for children
			if (requirement.getChildren() != null && !requirement.getChildren().isEmpty()) {
				generateStructureSectionsPlanningRequirement(subsections, requirement.getChildren(), argParameters);
			}

			// generate section and title
			Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class).generateSection(
					requirement.getStatement(), null, subsections, getAppMgr().getService(IReportARGApplication.class)
							.getSectionTypeByGenericLevel(requirement.getLevel()),
					null);

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

			// sort keys
			List<QuantityOfInterest> qoiKeys = new ArrayList<>(qois.keySet());
			Collections.sort(qoiKeys, Comparator.comparing(QuantityOfInterest::getGeneratedId,
					new StringWithNumberAndNullableComparator()));

			// For each quantity of interest
			for (QuantityOfInterest qoi : qoiKeys) {
				Map<ExportOptions, Object> qoiData = qois.get(qoi);
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

			// QoI Description
			if (!StringUtils.isBlank(tag.getDescription())) {
				subsections.add(getAppMgr().getService(IReportARGApplication.class)
						.generateParagraph(RscTools.getString(RscConst.MSG_QOI_FIELD_DESCRIPTION, RscTools.empty())));
				subsections.add(getAppMgr().getService(IReportARGApplication.class)
						.generateHtmlParagraph(tag.getDescription()));
			}

			// Parameters and values
			List<IGenericTableValue> values = tag.getQoiPlanningList().stream().map(IGenericTableValue.class::cast)
					.collect(Collectors.toList());

			if (values != null && !values.isEmpty()) {
				// Generate parameter/value paragraph
				getAppMgr().getService(IReportARGApplication.class).generateGenericValues(subsections, tag, values,
						argParameters);
			}

			// generate section and title
			Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class)
					.generateSection(tag.getSymbol(), null, subsections, null);

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
		List<Uncertainty> uncertaintyGroupList = null;

		// Get data
		if (options.get(ExportOptions.PLANNING_UNCERTAINTIES) instanceof List) {
			uncertaintyGroupList = (List<Uncertainty>) options.get(ExportOptions.PLANNING_UNCERTAINTIES);
		}

		// Check data not empties
		if (uncertaintyGroupList != null && !uncertaintyGroupList.isEmpty()) {

			// Generate section for each uncertainty group
			for (Uncertainty uncertaintyGroup : uncertaintyGroupList.stream().sorted(
					Comparator.comparing(Uncertainty::getGeneratedId, new StringWithNumberAndNullableComparator()))
					.collect(Collectors.toList())) {

				// Initialize
				List<Map<String, Object>> subsections = new ArrayList<>();

				// Generate evidences sections
				generateStructureSectionsPlanningUncertainty(subsections, uncertaintyGroup.getChildren(),
						(ARGParameters) options.get(ExportOptions.ARG_PARAMETERS));

				// Add subsections
				Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class)
						.generateSection(uncertaintyGroup.getName(), null, subsections, null);

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
		if (options.get(ExportOptions.DECISION_LIST) instanceof List) {
			decisionList = (List<Decision>) options.get(ExportOptions.DECISION_LIST);
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
		for (Decision decision : decisionList.stream()
				.sorted(Comparator.comparing(Decision::getGeneratedId, new StringWithNumberAndNullableComparator()))
				.collect(Collectors.toList())) {

			// Initialize
			List<Map<String, Object>> subsections = new ArrayList<>();

			// Parameters and values
			List<IGenericTableValue> values = decision.getValueList();
			if (values != null && !values.isEmpty()) {

				// Generate parameter/value paragraph
				values = GenericParameterTools.sortTableValuesByParameterId(values);
				getAppMgr().getService(IReportARGApplication.class).generateGenericValues(subsections, decision, values,
						argParameters);
			}

			// recursive call for children
			if (decision.getChildren() != null && !decision.getChildren().isEmpty()) {
				generateStructureSectionsPlanningDecision(subsections, decision.getChildren(), argParameters);
			}

			// generate section and title
			Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class)
					.generateSection(decision.getTitle(), null, subsections, getAppMgr()
							.getService(IReportARGApplication.class).getSectionTypeByGenericLevel(decision.getLevel()),
							null);

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
			for (Uncertainty uncertainty : uncertainties.stream().sorted(
					Comparator.comparing(Uncertainty::getGeneratedId, new StringWithNumberAndNullableComparator()))
					.collect(Collectors.toList())) {

				// Parameters and values
				List<IGenericTableValue> values = uncertainty.getValueList();
				if (values != null && !values.isEmpty()) {

					// Generate parameter/value paragraph
					getAppMgr().getService(IReportARGApplication.class).generateGenericValues(parentSections,
							uncertainty, values, argParameters);
				}
			}
		}
	}

}
