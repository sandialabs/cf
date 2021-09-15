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
 * The DecisionSelectValue entity class
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "DECISION_SELECT_VALUE")
public class DecisionSelectValue extends GenericParameterSelectValue<DecisionParam>
		implements IEntity<DecisionSelectValue, Integer>, IImportable<DecisionSelectValue> {

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
	private DecisionParam parameter;

	/**
	 * {@inheritDoc}
	 */
	public DecisionParam getParameter() {
		return parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParameter(DecisionParam parameter) {
		this.parameter = parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameAs(DecisionSelectValue importable) {

		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameParameter = (getParameter() == null && importable.getParameter() == null)
				|| (getParameter() != null && getParameter().sameAs(importable.getParameter()));

		return sameName && sameParameter;
	}

	@Override
	public String getSelectName() {
		return getAbstract();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAbstract() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "DecisionSelectValue [parameter=" + parameter + super.toString() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	@Override
	public DecisionSelectValue copy() {
		DecisionSelectValue entity = new DecisionSelectValue();
		entity.setName(getName());
		entity.setParameter(getParameter());
		return entity;
	}
}