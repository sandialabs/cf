/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.tools.RscConst;

/**
 * The PCMMPlanningValue entity class linked to table
 * PCMM_PLANNING_QUESTION_VALUE
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMM_PLANNING_QUESTION_VALUE")
public class PCMMPlanningQuestionValue extends GenericValueTaggable<PCMMPlanningQuestion, PCMMPlanningQuestionValue> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parameter field linked to PARAMETER_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARAMETER_ID")
	@NotNull(message = RscConst.EX_PCMMPLANNINGVALUE_PCMMPLANNINGPARAM_NULL)
	private PCMMPlanningQuestion parameter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningQuestion getParameter() {
		return parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParameter(PCMMPlanningQuestion parameter) {
		this.parameter = parameter;
	}

	@Override
	public String toString() {
		return "PCMMPlanningQuestionValue [" + super.toString() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	public PCMMPlanningQuestionValue copy() {
		PCMMPlanningQuestionValue value = new PCMMPlanningQuestionValue();
		value.setParameter(getParameter());
		value.setDateCreation(getDateCreation());
		value.setDateUpdate(getDateUpdate());
		value.setUserCreation(getUserCreation());
		value.setUserUpdate(getUserUpdate());
		value.setValue(getValue());
		value.setTag(getTag());
		return value;
	}
}