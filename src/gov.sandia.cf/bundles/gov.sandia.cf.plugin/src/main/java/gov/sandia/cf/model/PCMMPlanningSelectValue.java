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
 * The PCMMPlanningSelectValue entity class
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMM_PLANNING_SELECT_VALUE")
public class PCMMPlanningSelectValue extends GenericParameterSelectValue<PCMMPlanningParam>
		implements IEntity<PCMMPlanningSelectValue, Integer> {

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
	private PCMMPlanningParam parameter;

	/**
	 * {@inheritDoc}
	 */
	public PCMMPlanningParam getParameter() {
		return parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParameter(PCMMPlanningParam parameter) {
		this.parameter = parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "PCMMPlanningSelectValue [parameter=" + parameter + super.toString() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	@Override
	public PCMMPlanningSelectValue copy() {
		PCMMPlanningSelectValue entity = new PCMMPlanningSelectValue();
		entity.setName(getName());
		entity.setParameter(getParameter());
		return entity;
	}
}