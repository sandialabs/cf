/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

/**
 * The ARG report options.
 * 
 * @author Maxime N.
 *
 */
@SuppressWarnings("javadoc")
public enum ExportOptions {

	// ARG Parameters
	ARG_PARAMETERS,

	// Model
	MODEL,

	// User
	USER_LIST,

	// Planning
	PLANNING_INCLUDE,

	// Intended Purpose
	PLANNING_INTENDEDPURPOSE_INCLUDE, INTENDED_PURPOSE,

	// System requirements
	PLANNING_REQUIREMENT_INCLUDE, PLANNING_REQUIREMENTS,

	// QoI Planner
	PLANNING_QOI_PLANNER_INCLUDE,

	// Uncertainty Inventory
	UNCERTAINTY_INCLUDE, PLANNING_UNCERTAINTY_INCLUDE, PLANNING_UNCERTAINTIES, UNCERTAINTY_PARAMETERS,
	UNCERTAINTY_GROUP_LIST,

	// Analyst Decisions
	PLANNING_DECISION_INCLUDE, PLANNING_DECISIONS,

	// PIRT
	PIRT_INCLUDE, PIRT_SPECIFICATION, PIRT_QOI_LIST, PIRT_QOI_INCLUDE, PIRT_QOI_TAG,

	// PCMM
	PCMM_INCLUDE, PCMM_TAG, PCMM_TAG_LIST, PCMM_PLANNING_INCLUDE, PCMM_ASSESSMENT_INCLUDE, PCMM_EVIDENCE_INCLUDE,
	PCMM_MODE, PCMM_ELEMENTS, PCMM_PLANNING_PARAMETERS, PCMM_PLANNING_PARAMETERS_VALUES,
	PCMM_PLANNING_PARAMETERS_TABLEITEMS, PCMM_PLANNING_QUESTIONS, PCMM_PLANNING_QUESTION_VALUES, PCMM_EVIDENCE_LIST,
	PCMM_ASSESSMENT_LIST,

	// Custom Ending
	CUSTOM_ENDING_INCLUDE;
}
