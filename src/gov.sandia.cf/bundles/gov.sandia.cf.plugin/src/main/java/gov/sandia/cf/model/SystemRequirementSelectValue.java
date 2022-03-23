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
 * The SystemRequirementSelectValue entity class
 * 
 * @author Maxime N.
 *
 */
@Entity
@Table(name = "COM_REQUIREMENT_SELECT_VALUE")
public class SystemRequirementSelectValue extends GenericParameterSelectValue<SystemRequirementParam>
		implements IEntity<SystemRequirementSelectValue, Integer>, IImportable<SystemRequirementSelectValue> {

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
	private SystemRequirementParam parameter;

	/**
	 * {@inheritDoc}
	 */
	public SystemRequirementParam getParameter() {
		return parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParameter(SystemRequirementParam parameter) {
		this.parameter = parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameKey(SystemRequirementSelectValue newImportable) {
		return newImportable != null && StringTools.equals(getName(), newImportable.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameAs(SystemRequirementSelectValue importable) {
		
		if (importable == null) {
			return false;
		}

		boolean sameName = StringTools.equals(getName(), importable.getName());
		boolean sameParameter = (getParameter() == null && importable.getParameter() == null)
				|| (getParameter() != null && getParameter().sameAs(importable.getParameter()));

		return sameName && sameParameter;
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
		return "SystemRequirementSelectValue [parameter=" + parameter + super.toString() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current entity
	 */
	@Override
	public SystemRequirementSelectValue copy() {
		SystemRequirementSelectValue entity = new SystemRequirementSelectValue();
		entity.setName(getName());
		entity.setParameter(getParameter());
		return entity;
	}
}