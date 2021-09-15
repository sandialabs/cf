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
 * The QoIPlanningParamConstraint entity class
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "QOI_PLANNING_PARAM_CONSTRAINT")
public class QoIPlanningConstraint extends GenericParameterConstraint<QoIPlanningParam>
		implements IEntity<QoIPlanningConstraint, Integer>, IImportable<QoIPlanningConstraint> {

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
	private QoIPlanningParam parameter;

	/**
	 * {@inheritDoc}
	 */
	public QoIPlanningParam getParameter() {
		return parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setParameter(QoIPlanningParam parameter) {
		this.parameter = parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameAs(QoIPlanningConstraint importable) {

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
	public QoIPlanningConstraint copy() {
		QoIPlanningConstraint entity = new QoIPlanningConstraint();
		entity.setRule(getRule());
		entity.setParameter(getParameter());
		return entity;
	}
}