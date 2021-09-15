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
 * The QoIPlanningValue entity class linked to table QOI_PLANNING_VALUE
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "QOI_PLANNING_VALUE")
public class QoIPlanningValue extends GenericValue<QoIPlanningParam, QoIPlanningValue> implements IGenericTableValue {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		QOI("qoi"), //$NON-NLS-1$
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
	 * The parameter field linked to PARAMETER_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARAMETER_ID")
	@NotNull(message = RscConst.EX_QOIPLANNINGVALUE_GENPARAMETER_NULL)
	private QoIPlanningParam parameter;

	/**
	 * The QoIPlanning field linked to QOI_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QOI_ID")
	@NotNull(message = RscConst.EX_QOIPLANNINGVALUE_QOI_NULL)
	private QuantityOfInterest qoi;

	@SuppressWarnings("javadoc")
	public QuantityOfInterest getQoi() {
		return qoi;
	}

	@SuppressWarnings("javadoc")
	public void setQoi(QuantityOfInterest qoi) {
		this.qoi = qoi;
	}

	@Override
	public QoIPlanningParam getParameter() {
		return parameter;
	}

	@Override
	public void setParameter(QoIPlanningParam parameter) {
		this.parameter = parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "QoIPlanningValue:  [" + super.toString() + "], QoI=" + qoi + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current QoIPlanningValue
	 */
	@Override
	public QoIPlanningValue copy() {
		QoIPlanningValue value = new QoIPlanningValue();
		value.setParameter(getParameter());
		value.setDateCreation(getDateCreation());
		value.setDateUpdate(getDateUpdate());
		value.setUserCreation(getUserCreation());
		value.setUserUpdate(getUserUpdate());
		value.setValue(getValue());
		return value;
	}
}