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
import gov.sandia.cf.tools.StringTools;

/**
 * The PCMMPlanningQuestionConstraint entity class
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMM_PLANNING_QUESTION_CONSTRAINT")
public class PCMMPlanningQuestionConstraint extends GenericParameterConstraint<PCMMPlanningQuestion>
		implements IEntity<PCMMPlanningQuestionConstraint, Integer>, IImportable<PCMMPlanningQuestionConstraint> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parameter field linked to MODEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARAMETER_ID")
	@NotNull(message = RscConst.EX_GENPARAMVALUELIST_GENPARAMETER_NULL)
	private PCMMPlanningQuestion parameter;

	/**
	 * {@inheritDoc}
	 */
	public PCMMPlanningQuestion getParameter() {
		return parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParameter(PCMMPlanningQuestion parameter) {
		this.parameter = parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameAs(PCMMPlanningQuestionConstraint importable) {

		if (importable == null) {
			return false;
		}

		boolean sameRule = StringTools.equals(getRule(), importable.getRule());
		boolean sameParameter = (getParameter() == null && importable.getParameter() == null)
				|| (getParameter() != null && getParameter().sameAs(importable.getParameter()));

		return sameRule && sameParameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAbstract() {
		return getRule();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	@Override
	public PCMMPlanningQuestionConstraint copy() {
		PCMMPlanningQuestionConstraint entity = new PCMMPlanningQuestionConstraint();
		entity.setRule(getRule());
		entity.setParameter(getParameter());
		return entity;
	}
}