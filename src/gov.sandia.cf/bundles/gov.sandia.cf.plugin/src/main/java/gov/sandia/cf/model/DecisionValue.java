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
 * The DecisionValue entity class linked to table DECISION_VALUE
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "DECISION_VALUE")
public class DecisionValue extends GenericValue<DecisionParam, DecisionValue> implements IGenericTableValue {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		DECISION("decision"), //$NON-NLS-1$
		PARAMETER("parameter"); //$NON-NLS-1$

		private String field;

		/**
		 * Filter
		 * 
		 * @param field
		 */
		Filter(String field) {
			this.field = field;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getField() {
			return this.field;
		}
	}

	/**
	 * The parameter field linked to PARAMETER_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARAMETER_ID")
	@NotNull(message = RscConst.EX_DECISIONVALUE_GENPARAMETER_NULL)
	private DecisionParam parameter;

	/**
	 * The Decision field linked to DECISION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DECISION_ID")
	@NotNull(message = RscConst.EX_DECISIONVALUE_DECISION_NULL)
	private Decision decision;

	@SuppressWarnings("javadoc")
	public Decision getDecision() {
		return decision;
	}

	@SuppressWarnings("javadoc")
	public void setDecision(Decision decision) {
		this.decision = decision;
	}

	@Override
	public DecisionParam getParameter() {
		return parameter;
	}

	@Override
	public void setParameter(DecisionParam parameter) {
		this.parameter = parameter;
	}

	@Override
	public String toString() {
		return "DecisionValue:  [" + super.toString() + "], Decision=" + decision + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Creates a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current DecisionValue
	 */
	@Override
	public DecisionValue copy() {
		DecisionValue value = new DecisionValue();
		value.setDateCreation(getDateCreation());
		value.setDateUpdate(getDateUpdate());
		value.setDecision(getDecision());
		value.setParameter(getParameter());
		value.setUserCreation(getUserCreation());
		value.setUserUpdate(getUserUpdate());
		value.setValue(getValue());
		return value;
	}
}