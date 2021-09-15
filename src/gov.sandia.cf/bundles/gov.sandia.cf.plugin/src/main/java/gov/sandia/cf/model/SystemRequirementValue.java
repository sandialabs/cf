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

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;

/**
 * The SystemRequirementValue entity class linked to table COM_REQUIREMENT_VALUE
 * 
 * @author Maxime N.
 *
 */
@Entity
@Table(name = "COM_REQUIREMENT_VALUE")
public class SystemRequirementValue extends GenericValue<SystemRequirementParam, SystemRequirementValue>
		implements IGenericTableValue {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		REQUIREMENT("requirement"), //$NON-NLS-1$
		PARAMETER("parameter"); //$NON-NLS-1$

		private String field;

		Filter(String field) {
			this.field = field;
		}

		public String getField() {
			return this.field;
		}
	}

	/**
	 * The parameter field linked to COM_PARAMETER_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARAMETER_ID")
	@NotNull(message = RscConst.EX_REQUIREMENTPARAMETER_GENPARAMETER_NULL)
	private SystemRequirementParam parameter;

	/**
	 * The requirement field linked to REQUIREMENT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REQUIREMENT_ID")
	@NotNull(message = RscConst.EX_REQUIREMENTPARAMETER_REQUIREMENT_NULL)
	private SystemRequirement requirement;

	@SuppressWarnings("javadoc")
	public SystemRequirement getRequirement() {
		return requirement;
	}

	@SuppressWarnings("javadoc")
	public void setRequirement(SystemRequirement requirement) {
		this.requirement = requirement;
	}

	@Override
	public SystemRequirementParam getParameter() {
		return parameter;
	}

	@Override
	public void setParameter(SystemRequirementParam parameter) {
		this.parameter = parameter;
	}

	@Override
	public String toString() {
		return "SystemRequirementValue [" + super.toString() + "], requirement=" + requirement + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current SystemRequirementValue
	 */
	@Override
	public SystemRequirementValue copy() {
		SystemRequirementValue value = new SystemRequirementValue();
		value.setParameter(getParameter());
		value.setDateCreation(getDateCreation());
		value.setDateUpdate(getDateUpdate());
		value.setUserCreation(getUserCreation());
		value.setUserUpdate(getUserUpdate());
		value.setValue(getValue());
		return value;
	}
}