/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.configuration;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.Role;

/**
 * Contains all PCMM configuration variables. This class is loaded by a
 * configuration file.
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMSpecification {

	/**
	 * the PCMM level colors
	 */
	private Map<Integer, PCMMLevelColor> levelColors;

	/**
	 * the PCMM phases
	 */
	private List<PCMMPhase> phases;

	/**
	 * the PCMM options
	 */
	private List<PCMMOption> options;

	/**
	 * the PCMM roles
	 */
	private List<Role> roles;

	/**
	 * the PCMM elements
	 */
	private List<PCMMElement> elements;

	/**
	 * The PCMM mode
	 */
	private PCMMMode mode;

	/**
	 * The PCMM Planning Fields
	 */
	private List<PCMMPlanningParam> planningFields;

	/**
	 * The map of PCMM Planning questions
	 */
	private Map<IAssessable, List<PCMMPlanningQuestion>> planningQuestions;

	/**
	 * The constructor
	 */
	public PCMMSpecification() {
		mode = PCMMMode.DEFAULT;
	}

	@SuppressWarnings("javadoc")
	public Map<Integer, PCMMLevelColor> getLevelColors() {
		return levelColors;
	}

	@SuppressWarnings("javadoc")
	public void setLevelColors(Map<Integer, PCMMLevelColor> levelColors) {
		this.levelColors = levelColors;
	}

	@SuppressWarnings("javadoc")
	public List<PCMMPhase> getPhases() {
		return phases;
	}

	@SuppressWarnings("javadoc")
	public void setPhases(List<PCMMPhase> phases) {
		this.phases = phases;
	}

	@SuppressWarnings("javadoc")
	public List<Role> getRoles() {
		return roles;
	}

	@SuppressWarnings("javadoc")
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	/**
	 * @return true if the PCMM Aggregate is enabled, otherwise false
	 */
	public boolean isPcmmAggregateEnabled() {
		return phases != null && phases.contains(PCMMPhase.AGGREGATE);
	}

	/**
	 * @return true if the PCMM Assess is enabled, otherwise false
	 */
	public boolean isPcmmAssessEnabled() {
		return phases != null && phases.contains(PCMMPhase.ASSESS);
	}

	/**
	 * @return true if the PCMM Evidence is enabled, otherwise false
	 */
	public boolean isPcmmEvidenceEnabled() {
		return phases != null && phases.contains(PCMMPhase.EVIDENCE);
	}

	/**
	 * @return true if the PCMM stamp is enabled, otherwise false
	 */
	public boolean isPcmmStampEnabled() {
		return phases != null && phases.contains(PCMMPhase.STAMP);
	}

	/**
	 * @return true if the PCMM planning is enabled, otherwise false
	 */
	public boolean isPcmmPlanningEnabled() {
		return phases != null && phases.contains(PCMMPhase.PLANNING);
	}

	@SuppressWarnings("javadoc")
	public List<PCMMElement> getElements() {
		return elements;
	}

	@SuppressWarnings("javadoc")
	public void setElements(List<PCMMElement> elements) {
		this.elements = elements;
	}

	@SuppressWarnings("javadoc")
	public PCMMMode getMode() {
		return mode;
	}

	@SuppressWarnings("javadoc")
	public void setMode(PCMMMode mode) {
		this.mode = mode;
	}

	@SuppressWarnings("javadoc")
	public List<PCMMPlanningParam> getPlanningFields() {
		return planningFields;
	}

	@SuppressWarnings("javadoc")
	public void setPlanningFields(List<PCMMPlanningParam> planningFields) {
		this.planningFields = planningFields;
	}

	@SuppressWarnings("javadoc")
	public Map<IAssessable, List<PCMMPlanningQuestion>> getPlanningQuestions() {
		return planningQuestions;
	}

	@SuppressWarnings("javadoc")
	public void setPlanningQuestions(Map<IAssessable, List<PCMMPlanningQuestion>> planningQuestions) {
		this.planningQuestions = planningQuestions;
	}

	@SuppressWarnings("javadoc")
	public List<PCMMOption> getOptions() {
		return options;
	}

	@SuppressWarnings("javadoc")
	public void setOptions(List<PCMMOption> options) {
		this.options = options;
	}

}
