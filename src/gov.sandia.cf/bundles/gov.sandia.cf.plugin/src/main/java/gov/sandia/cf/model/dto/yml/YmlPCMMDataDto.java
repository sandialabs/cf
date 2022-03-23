/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.yml;

import java.io.Serializable;
import java.util.List;

import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningValue;

/**
 * Contains PCMM data for import/export.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlPCMMDataDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6270642271197040024L;

	/** The elements. */
	private List<PCMMElement> elements;

	/** The planning fields. */
	private List<PCMMPlanningParam> planningFields;

	/** The planning questions. */
	private List<PCMMPlanningQuestion> planningQuestions;

	/** The evidence. */
	private List<PCMMEvidence> evidence;

	/** The assessments. */
	private List<PCMMAssessment> assessments;

	/** The planning field values. */
	private List<PCMMPlanningValue> planningFieldValues;

	/** The planning table items. */
	private List<PCMMPlanningTableItem> planningTableItems;

	/** The planning question values. */
	private List<PCMMPlanningQuestionValue> planningQuestionValues;

	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public List<PCMMElement> getElements() {
		return elements;
	}

	/**
	 * Sets the elements.
	 *
	 * @param elements the new elements
	 */
	public void setElements(List<PCMMElement> elements) {
		this.elements = elements;
	}

	/**
	 * Gets the planning fields.
	 *
	 * @return the planning fields
	 */
	public List<PCMMPlanningParam> getPlanningFields() {
		return planningFields;
	}

	/**
	 * Sets the planning fields.
	 *
	 * @param planningFields the new planning fields
	 */
	public void setPlanningFields(List<PCMMPlanningParam> planningFields) {
		this.planningFields = planningFields;
	}

	/**
	 * Gets the planning questions.
	 *
	 * @return the planning questions
	 */
	public List<PCMMPlanningQuestion> getPlanningQuestions() {
		return planningQuestions;
	}

	/**
	 * Sets the planning questions.
	 *
	 * @param planningQuestions the new planning questions
	 */
	public void setPlanningQuestions(List<PCMMPlanningQuestion> planningQuestions) {
		this.planningQuestions = planningQuestions;
	}

	/**
	 * Gets the evidence.
	 *
	 * @return the evidence
	 */
	public List<PCMMEvidence> getEvidence() {
		return evidence;
	}

	/**
	 * Sets the evidence.
	 *
	 * @param evidence the new evidence
	 */
	public void setEvidence(List<PCMMEvidence> evidence) {
		this.evidence = evidence;
	}

	/**
	 * Gets the assessments.
	 *
	 * @return the assessments
	 */
	public List<PCMMAssessment> getAssessments() {
		return assessments;
	}

	/**
	 * Sets the assessments.
	 *
	 * @param assessments the new assessments
	 */
	public void setAssessments(List<PCMMAssessment> assessments) {
		this.assessments = assessments;
	}

	/**
	 * Gets the planning field values.
	 *
	 * @return the planning field values
	 */
	public List<PCMMPlanningValue> getPlanningFieldValues() {
		return planningFieldValues;
	}

	/**
	 * Sets the planning field values.
	 *
	 * @param planningFieldValues the new planning field values
	 */
	public void setPlanningFieldValues(List<PCMMPlanningValue> planningFieldValues) {
		this.planningFieldValues = planningFieldValues;
	}

	/**
	 * Gets the planning table items.
	 *
	 * @return the planning table items
	 */
	public List<PCMMPlanningTableItem> getPlanningTableItems() {
		return planningTableItems;
	}

	/**
	 * Sets the planning table items.
	 *
	 * @param planningTableItems the new planning table items
	 */
	public void setPlanningTableItems(List<PCMMPlanningTableItem> planningTableItems) {
		this.planningTableItems = planningTableItems;
	}

	/**
	 * Gets the planning question values.
	 *
	 * @return the planning question values
	 */
	public List<PCMMPlanningQuestionValue> getPlanningQuestionValues() {
		return planningQuestionValues;
	}

	/**
	 * Sets the planning question values.
	 *
	 * @param planningQuestionValues the new planning question values
	 */
	public void setPlanningQuestionValues(List<PCMMPlanningQuestionValue> planningQuestionValues) {
		this.planningQuestionValues = planningQuestionValues;
	}

}
