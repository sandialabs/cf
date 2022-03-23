/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.exports;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.constants.configuration.YmlPCMMData;
import gov.sandia.cf.constants.configuration.YmlPCMMSchema;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.yml.YmlAllDataDto;
import gov.sandia.cf.model.dto.yml.YmlDecisionDataDto;
import gov.sandia.cf.model.dto.yml.YmlGlobalDataDto;
import gov.sandia.cf.model.dto.yml.YmlIntendedPurposeDataDto;
import gov.sandia.cf.model.dto.yml.YmlPCMMDataDto;
import gov.sandia.cf.model.dto.yml.YmlPIRTDataDto;
import gov.sandia.cf.model.dto.yml.YmlRequirementDataDto;
import gov.sandia.cf.model.dto.yml.YmlUncertaintyDataDto;

/**
 * The Class ExportOptionsMapper.
 * 
 * @author Didier Verstraete
 */
public class ExportOptionsMapper {

	private ExportOptionsMapper() {
		// DO NOT INSTANTIATE
	}

	/**
	 * Gets the yml all data.
	 *
	 * @param exportOptions the export options
	 * @return the yml all data
	 */
	public static YmlAllDataDto getYmlAllData(final Map<ExportOptions, Object> exportOptions) {

		YmlAllDataDto data = new YmlAllDataDto();

		// load data
		data.setGlobalData(getYmlGlobalData(exportOptions));

		if (isIntendedPurposeToExport(exportOptions)) {
			data.setIntendedPurposeData(getYmlIntendedPurposeData(exportOptions));
		}
		if (isDecisionToExport(exportOptions)) {
			data.setDecisionData(getYmlDecisionData(exportOptions));
		}
		if (isSystemRequirementToExport(exportOptions)) {
			data.setRequirementData(getYmlRequirementData(exportOptions));
		}
		if (isUncertaintyToExport(exportOptions)) {
			data.setUncertaintyData(getYmlUncertaintyData(exportOptions));
		}
		if (isPIRTToExport(exportOptions)) {
			data.setPirtData(getYmlPIRTData(exportOptions));
		}
		if (isPCMMToExport(exportOptions)) {
			data.setPcmmData(getYmlPCMMData(exportOptions));
		}

		return data;
	}

	/**
	 * Gets the yml global data.
	 *
	 * @param exportOptions the export options
	 * @return the yml global data
	 */
	public static YmlGlobalDataDto getYmlGlobalData(final Map<ExportOptions, Object> exportOptions) {

		YmlGlobalDataDto data = new YmlGlobalDataDto();

		data.setModel(getModel(exportOptions));
		data.setRoles(getRoleList(exportOptions));
		data.setUsers(getUserList(exportOptions));

		return data;
	}

	/**
	 * Checks if is intended purpose to export.
	 *
	 * @param exportOptions the export options
	 * @return true, if is intended purpose to export
	 */
	public static boolean isIntendedPurposeToExport(final Map<ExportOptions, Object> exportOptions) {
		return exportOptions != null && exportOptions.containsKey(ExportOptions.INTENDEDPURPOSE_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.INTENDEDPURPOSE_INCLUDE));
	}

	/**
	 * Gets the yml intended purpose data.
	 *
	 * @param exportOptions the export options
	 * @return the yml intended purpose data
	 */
	public static YmlIntendedPurposeDataDto getYmlIntendedPurposeData(final Map<ExportOptions, Object> exportOptions) {

		YmlIntendedPurposeDataDto data = new YmlIntendedPurposeDataDto();

		data.setIntendedPurpose(getIntendedPurpose(exportOptions));

		return data;
	}

	/**
	 * Checks if is decision to export.
	 *
	 * @param exportOptions the export options
	 * @return true, if is decision to export
	 */
	public static boolean isDecisionToExport(final Map<ExportOptions, Object> exportOptions) {
		return exportOptions != null && exportOptions.containsKey(ExportOptions.DECISION_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.DECISION_INCLUDE));
	}

	/**
	 * Gets the yml decision data.
	 *
	 * @param exportOptions the export options
	 * @return the yml decision data
	 */
	public static YmlDecisionDataDto getYmlDecisionData(final Map<ExportOptions, Object> exportOptions) {

		YmlDecisionDataDto data = new YmlDecisionDataDto();

		data.setDecisionParameters(getDecisionParameters(exportOptions));
		data.setDecisionValues(getDecisionValues(exportOptions));

		return data;
	}

	/**
	 * Checks if is system requirement to export.
	 *
	 * @param exportOptions the export options
	 * @return true, if is system requirement to export
	 */
	public static boolean isSystemRequirementToExport(final Map<ExportOptions, Object> exportOptions) {
		return exportOptions != null && exportOptions.containsKey(ExportOptions.SYSTEM_REQUIREMENT_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.SYSTEM_REQUIREMENT_INCLUDE));
	}

	/**
	 * Gets the yml requirement data.
	 *
	 * @param exportOptions the export options
	 * @return the yml requirement data
	 */
	public static YmlRequirementDataDto getYmlRequirementData(final Map<ExportOptions, Object> exportOptions) {

		YmlRequirementDataDto data = new YmlRequirementDataDto();

		data.setRequirementParameters(getSystemRequirementParameters(exportOptions));
		data.setRequirementValues(getSystemRequirementValues(exportOptions));

		return data;
	}

	/**
	 * Checks if is uncertainty to export.
	 *
	 * @param exportOptions the export options
	 * @return true, if is uncertainty to export
	 */
	public static boolean isUncertaintyToExport(final Map<ExportOptions, Object> exportOptions) {
		return exportOptions != null && exportOptions.containsKey(ExportOptions.UNCERTAINTY_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.UNCERTAINTY_INCLUDE));
	}

	/**
	 * Gets the yml uncertainty data.
	 *
	 * @param exportOptions the export options
	 * @return the yml uncertainty data
	 */
	public static YmlUncertaintyDataDto getYmlUncertaintyData(final Map<ExportOptions, Object> exportOptions) {

		YmlUncertaintyDataDto data = new YmlUncertaintyDataDto();

		data.setUncertaintyParameters(getUncertaintyParameters(exportOptions));
		data.setUncertaintyGroups(getUncertaintyGroups(exportOptions));

		return data;
	}

	/**
	 * Checks if is PIRT to export.
	 *
	 * @param exportOptions the export options
	 * @return true, if is PIRT to export
	 */
	public static boolean isPIRTToExport(final Map<ExportOptions, Object> exportOptions) {
		return exportOptions != null && exportOptions.containsKey(ExportOptions.PIRT_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.PIRT_INCLUDE));
	}

	/**
	 * Gets the yml PIRT data.
	 *
	 * @param exportOptions the export options
	 * @return the yml PIRT data
	 */
	public static YmlPIRTDataDto getYmlPIRTData(final Map<ExportOptions, Object> exportOptions) {

		YmlPIRTDataDto data = new YmlPIRTDataDto();

		data.setQuantityOfInterestList(getQoiList(exportOptions));

		return data;
	}

	/**
	 * Checks if is PCMM to export.
	 *
	 * @param exportOptions the export options
	 * @return true, if is PCMM to export
	 */
	public static boolean isPCMMToExport(final Map<ExportOptions, Object> exportOptions) {
		return exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.PCMM_INCLUDE));
	}

	/**
	 * Checks if is PCMM planning to export.
	 *
	 * @param exportOptions the export options
	 * @return true, if is PCMM planning to export
	 */
	public static boolean isPCMMPlanningToExport(final Map<ExportOptions, Object> exportOptions) {
		return exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_PLANNING_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.PCMM_PLANNING_INCLUDE));
	}

	/**
	 * Checks if is PCMM evidence to export.
	 *
	 * @param exportOptions the export options
	 * @return true, if is PCMM evidence to export
	 */
	public static boolean isPCMMEvidenceToExport(final Map<ExportOptions, Object> exportOptions) {
		return exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_EVIDENCE_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.PCMM_EVIDENCE_INCLUDE));
	}

	/**
	 * Checks if is PCMM assessment to export.
	 *
	 * @param exportOptions the export options
	 * @return true, if is PCMM assessment to export
	 */
	public static boolean isPCMMAssessmentToExport(final Map<ExportOptions, Object> exportOptions) {
		return exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_ASSESSMENT_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.PCMM_ASSESSMENT_INCLUDE));
	}

	/**
	 * Gets the yml PCMM data.
	 *
	 * @param exportOptions the export options
	 * @return the yml PCMM data
	 */
	public static YmlPCMMDataDto getYmlPCMMData(final Map<ExportOptions, Object> exportOptions) {

		YmlPCMMDataDto data = new YmlPCMMDataDto();

		data.setElements(getPCMMElementList(exportOptions));

		if (isPCMMPlanningToExport(exportOptions)) {
			data.setPlanningFields(getPCMMPlanningParameters(exportOptions));
			data.setPlanningQuestions(getPCMMPlanningQuestions(exportOptions));
			data.setPlanningFieldValues(getPCMMPlanningValues(exportOptions));
			data.setPlanningTableItems(getPCMMPlanningTableItems(exportOptions));
			data.setPlanningQuestionValues(getPCMMPlanningQuestionValues(exportOptions));
		}
		if (isPCMMEvidenceToExport(exportOptions)) {
			data.setEvidence(getPCMMEvidenceList(exportOptions));
		}
		if (isPCMMAssessmentToExport(exportOptions)) {
			data.setAssessments(getPCMMAssessmentList(exportOptions));
		}

		return data;
	}

	/**
	 * Gets the model.
	 *
	 * @param exportOptions the export options
	 * @return the model
	 */
	public static Model getModel(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.MODEL)
				&& exportOptions.get(ExportOptions.MODEL) instanceof Model) {

			return (Model) exportOptions.get(ExportOptions.MODEL);
		}

		return null;
	}

	/**
	 * Gets the user list.
	 *
	 * @param exportOptions the export options
	 * @return the user list
	 */
	public static List<User> getUserList(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.USER_LIST)
				&& exportOptions.get(ExportOptions.USER_LIST) instanceof List) {

			return (List<User>) exportOptions.get(ExportOptions.USER_LIST);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the PCMM role list.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM role list
	 */
	public static List<Role> getRoleList(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_ROLE_LIST)
				&& exportOptions.get(ExportOptions.PCMM_ROLE_LIST) instanceof List) {

			return (List<Role>) exportOptions.get(ExportOptions.PCMM_ROLE_LIST);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the intended purpose.
	 *
	 * @param exportOptions the export options
	 * @return the intended purpose
	 */
	public static IntendedPurpose getIntendedPurpose(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.INTENDED_PURPOSE)
				&& exportOptions.get(ExportOptions.INTENDED_PURPOSE) instanceof IntendedPurpose) {

			return (IntendedPurpose) exportOptions.get(ExportOptions.INTENDED_PURPOSE);
		}

		return null;
	}

	/**
	 * Gets the decision parameters.
	 *
	 * @param exportOptions the export options
	 * @return the decision parameters
	 */
	public static List<DecisionParam> getDecisionParameters(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.DECISION_PARAMETERS)
				&& exportOptions.get(ExportOptions.DECISION_PARAMETERS) instanceof List) {

			return (List<DecisionParam>) exportOptions.get(ExportOptions.DECISION_PARAMETERS);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the decision values.
	 *
	 * @param exportOptions the export options
	 * @return the decision values
	 */
	public static List<Decision> getDecisionValues(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.DECISION_LIST)
				&& exportOptions.get(ExportOptions.DECISION_LIST) instanceof List) {

			return (List<Decision>) exportOptions.get(ExportOptions.DECISION_LIST);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the system requirement parameters.
	 *
	 * @param exportOptions the export options
	 * @return the system requirement parameters
	 */
	public static List<SystemRequirementParam> getSystemRequirementParameters(
			final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.SYSTEM_REQUIREMENT_PARAMETERS)
				&& exportOptions.get(ExportOptions.SYSTEM_REQUIREMENT_PARAMETERS) instanceof List) {

			return (List<SystemRequirementParam>) exportOptions.get(ExportOptions.SYSTEM_REQUIREMENT_PARAMETERS);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the system requirement values.
	 *
	 * @param exportOptions the export options
	 * @return the system requirement values
	 */
	public static List<SystemRequirement> getSystemRequirementValues(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.SYSTEM_REQUIREMENT_LIST)
				&& exportOptions.get(ExportOptions.SYSTEM_REQUIREMENT_LIST) instanceof List) {

			return (List<SystemRequirement>) exportOptions.get(ExportOptions.SYSTEM_REQUIREMENT_LIST);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the uncertainty parameters.
	 *
	 * @param exportOptions the export options
	 * @return the uncertainty parameters
	 */
	public static List<UncertaintyParam> getUncertaintyParameters(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.UNCERTAINTY_PARAMETERS)
				&& exportOptions.get(ExportOptions.UNCERTAINTY_PARAMETERS) instanceof List) {

			return (List<UncertaintyParam>) exportOptions.get(ExportOptions.UNCERTAINTY_PARAMETERS);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the uncertainty groups.
	 *
	 * @param exportOptions the export options
	 * @return the uncertainty groups
	 */
	public static List<Uncertainty> getUncertaintyGroups(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.UNCERTAINTY_GROUP_LIST)
				&& exportOptions.get(ExportOptions.UNCERTAINTY_GROUP_LIST) instanceof List) {

			return (List<Uncertainty>) exportOptions.get(ExportOptions.UNCERTAINTY_GROUP_LIST);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the qoi list.
	 *
	 * @param exportOptions the export options
	 * @return the qoi list
	 */
	public static List<QuantityOfInterest> getQoiList(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PIRT_QOI_LIST)
				&& exportOptions.get(ExportOptions.PIRT_QOI_LIST) instanceof List) {

			return (List<QuantityOfInterest>) exportOptions.get(ExportOptions.PIRT_QOI_LIST);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the PCMM element list.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM element list
	 */
	public static List<PCMMElement> getPCMMElementList(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_ELEMENTS)
				&& exportOptions.get(ExportOptions.PCMM_ELEMENTS) instanceof List) {

			return (List<PCMMElement>) exportOptions.get(ExportOptions.PCMM_ELEMENTS);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the PCMM planning map.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM planning map
	 */
	public static Map<String, Object> getPCMMPlanningMap(final Map<ExportOptions, Object> exportOptions) {

		Map<String, Object> map = new LinkedHashMap<>();
		Map<String, Object> planningMap = new LinkedHashMap<>();

		boolean notEmpty = false;

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_PLANNING_PARAMETERS)
				&& exportOptions.get(ExportOptions.PCMM_PLANNING_PARAMETERS) instanceof List) {

			planningMap.put(YmlPCMMSchema.CONF_PCMM_PLANNING_FIELDS, getPCMMPlanningParameters(exportOptions));
			notEmpty = true;
		}

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_PLANNING_QUESTIONS)
				&& exportOptions.get(ExportOptions.PCMM_PLANNING_QUESTIONS) instanceof List) {

			planningMap.put(YmlPCMMSchema.CONF_PCMM_PLANNING_QUESTIONS, getPCMMPlanningQuestions(exportOptions));
			notEmpty = true;
		}

		if (notEmpty) {
			map.put(YmlPCMMSchema.CONF_PCMM_PLANNING, planningMap);
		}

		return map;
	}

	/**
	 * Gets the PCMM planning parameters.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM planning parameters
	 */
	public static List<PCMMPlanningParam> getPCMMPlanningParameters(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_PLANNING_PARAMETERS)
				&& exportOptions.get(ExportOptions.PCMM_PLANNING_PARAMETERS) instanceof List) {
			return (List<PCMMPlanningParam>) exportOptions.get(ExportOptions.PCMM_PLANNING_PARAMETERS);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the PCMM planning questions.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM planning questions
	 */
	public static List<PCMMPlanningQuestion> getPCMMPlanningQuestions(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_PLANNING_QUESTIONS)
				&& exportOptions.get(ExportOptions.PCMM_PLANNING_QUESTIONS) instanceof List) {
			return (List<PCMMPlanningQuestion>) exportOptions.get(ExportOptions.PCMM_PLANNING_QUESTIONS);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the PCMM tag list.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM tag list
	 */
	public static List<?> getPCMMTagList(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_TAG_LIST)
				&& exportOptions.get(ExportOptions.PCMM_TAG_LIST) instanceof List) {

			return (List<?>) exportOptions.get(ExportOptions.PCMM_TAG_LIST);
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the PCMM evidence list.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM evidence list
	 */
	@SuppressWarnings("unchecked")
	public static List<PCMMEvidence> getPCMMEvidenceList(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_EVIDENCE_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.PCMM_EVIDENCE_INCLUDE))
				&& exportOptions.containsKey(ExportOptions.PCMM_EVIDENCE_LIST)
				&& exportOptions.get(ExportOptions.PCMM_EVIDENCE_LIST) instanceof Map
				&& !((Map<?, ?>) exportOptions.get(ExportOptions.PCMM_EVIDENCE_LIST)).isEmpty()) {

			Map<?, ?> mapEvidence = (Map<?, ?>) exportOptions.get(ExportOptions.PCMM_EVIDENCE_LIST);

			return (List<PCMMEvidence>) mapEvidence.values().stream().filter(List.class::isInstance)
					.map(List.class::cast).flatMap(List::stream).collect(Collectors.toList());
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the PCMM assessment list.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM assessment list
	 */
	@SuppressWarnings("unchecked")
	public static List<PCMMAssessment> getPCMMAssessmentList(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_ASSESSMENT_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.PCMM_ASSESSMENT_INCLUDE))
				&& exportOptions.containsKey(ExportOptions.PCMM_ASSESSMENT_LIST)
				&& exportOptions.get(ExportOptions.PCMM_ASSESSMENT_LIST) instanceof Map
				&& !((Map<?, ?>) exportOptions.get(ExportOptions.PCMM_ASSESSMENT_LIST)).isEmpty()) {

			Map<?, ?> mapAssessment = (Map<?, ?>) exportOptions.get(ExportOptions.PCMM_ASSESSMENT_LIST);

			return (List<PCMMAssessment>) mapAssessment.values().stream().filter(List.class::isInstance)
					.map(List.class::cast).flatMap(List::stream).collect(Collectors.toList());
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the PCMM planning value list.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM planning value list
	 */
	public static Map<String, Object> getPCMMPlanningValueList(final Map<ExportOptions, Object> exportOptions) {
		Map<String, Object> planningMap = new LinkedHashMap<>();

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_PLANNING_INCLUDE)
				&& Boolean.TRUE.equals(exportOptions.get(ExportOptions.PCMM_PLANNING_INCLUDE))) {

			// PCMM Planning question values
			if (exportOptions.containsKey(ExportOptions.PCMM_PLANNING_QUESTION_VALUES)) {
				planningMap.put(YmlPCMMData.CONF_PLANNING_QUESTION_VALUES,
						getPCMMPlanningQuestionValues(exportOptions));
			}

			// PCMM Planning values
			if (exportOptions.containsKey(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES)) {
				planningMap.put(YmlPCMMData.CONF_PLANNING_PARAMETER_VALUES, getPCMMPlanningValues(exportOptions));
			}

			// PCMM Planning table items and values
			if (exportOptions.containsKey(ExportOptions.PCMM_PLANNING_PARAMETERS_TABLEITEMS)) {
				planningMap.put(YmlPCMMData.CONF_PLANNING_TABLE_ITEMS, getPCMMPlanningTableItems(exportOptions));
			}
		}

		return planningMap;
	}

	/**
	 * Gets the PCMM planning values.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM planning values
	 */
	public static List<PCMMPlanningValue> getPCMMPlanningValues(final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES)
				&& exportOptions.get(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES) instanceof Map) {

			Map<?, ?> mapParamValues = (Map<?, ?>) exportOptions.get(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES);

			return (List<PCMMPlanningValue>) mapParamValues.values().stream().filter(List.class::isInstance)
					.map(List.class::cast).flatMap(List::stream).collect(Collectors.toList());
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the PCMM planning question values.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM planning question values
	 */
	public static List<PCMMPlanningQuestionValue> getPCMMPlanningQuestionValues(
			final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_PLANNING_QUESTION_VALUES)
				&& exportOptions.get(ExportOptions.PCMM_PLANNING_QUESTION_VALUES) instanceof Map) {

			Map<?, ?> mapParamValues = (Map<?, ?>) exportOptions.get(ExportOptions.PCMM_PLANNING_QUESTION_VALUES);

			return (List<PCMMPlanningQuestionValue>) mapParamValues.values().stream().filter(List.class::isInstance)
					.map(List.class::cast).flatMap(List::stream).collect(Collectors.toList());
		}

		return new ArrayList<>();
	}

	/**
	 * Gets the PCMM planning table items.
	 *
	 * @param exportOptions the export options
	 * @return the PCMM planning table items
	 */
	public static List<PCMMPlanningTableItem> getPCMMPlanningTableItems(
			final Map<ExportOptions, Object> exportOptions) {

		if (exportOptions != null && exportOptions.containsKey(ExportOptions.PCMM_PLANNING_PARAMETERS_TABLEITEMS)
				&& exportOptions.get(ExportOptions.PCMM_PLANNING_PARAMETERS_TABLEITEMS) instanceof Map) {

			Map<?, ?> mapParamValues = (Map<?, ?>) exportOptions.get(ExportOptions.PCMM_PLANNING_PARAMETERS_TABLEITEMS);

			return (List<PCMMPlanningTableItem>) mapParamValues.values().stream().filter(List.class::isInstance)
					.map(List.class::cast).flatMap(List::stream).collect(Collectors.toList());
		}

		return new ArrayList<>();
	}
}
