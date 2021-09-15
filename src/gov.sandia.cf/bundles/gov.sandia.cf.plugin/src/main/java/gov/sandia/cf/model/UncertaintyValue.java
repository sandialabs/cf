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
 * The UncertaintyValue entity class linked to table COM_UNCERTAINTY_VALUE
 * 
 * @author Maxime N.
 *
 */
@Entity
@Table(name = "COM_UNCERTAINTY_VALUE")
public class UncertaintyValue extends GenericValue<UncertaintyParam, UncertaintyValue> implements IGenericTableValue {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		UNCERTAINTY("uncertainty"), //$NON-NLS-1$
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
	@NotNull(message = RscConst.EX_UNCERTAINTYPARAMETER_GENPARAMETER_NULL)
	private UncertaintyParam parameter;

	/**
	 * The uncertainty field linked to UNCERTAINTY_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNCERTAINTY_ID")
	@NotNull(message = RscConst.EX_UNCERTAINTYPARAMETER_UNCERTAINTY_NULL)
	private Uncertainty uncertainty;

	@SuppressWarnings("javadoc")
	public Uncertainty getUncertainty() {
		return uncertainty;
	}

	@SuppressWarnings("javadoc")
	public void setUncertainty(Uncertainty uncertainty) {
		this.uncertainty = uncertainty;
	}

	@Override
	public UncertaintyParam getParameter() {
		return parameter;
	}

	@Override
	public void setParameter(UncertaintyParam parameter) {
		this.parameter = parameter;
	}

	@Override
	public String toString() {
		return "UncertaintyParameter [" + super.toString() + "], uncertainty=" + uncertainty + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current UncertaintyValue
	 */
	@Override
	public UncertaintyValue copy() {
		UncertaintyValue value = new UncertaintyValue();
		value.setParameter(getParameter());
		value.setDateCreation(getDateCreation());
		value.setDateUpdate(getDateUpdate());
		value.setUserCreation(getUserCreation());
		value.setUserUpdate(getUserUpdate());
		value.setValue(getValue());
		return value;
	}
}