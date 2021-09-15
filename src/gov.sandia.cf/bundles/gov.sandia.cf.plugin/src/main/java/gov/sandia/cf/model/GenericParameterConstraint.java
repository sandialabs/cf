/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The GenericParameter entity class
 * 
 * @author Didier Verstraete
 * @param <P> The generic parameter inherited class
 *
 */
@MappedSuperclass
public abstract class GenericParameterConstraint<P extends GenericParameter<P>> implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		ID("id"), //$NON-NLS-1$
		RULE("rule"), //$NON-NLS-1$
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
	 * The id field linked to ID column
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;

	/**
	 * The rule field linked to RULE column
	 */
	@Column(name = "RULE")
	@NotNull(message = RscConst.EX_GENPARAMETERCONSTRAINT_RULE_NULL)
	private String rule;

	@SuppressWarnings("javadoc")
	public Integer getId() {
		return id;
	}

	@SuppressWarnings("javadoc")
	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getRule() {
		return rule;
	}

	@SuppressWarnings("javadoc")
	public void setRule(String rule) {
		this.rule = rule;
	}

	/**
	 * @return the parameter
	 */
	public abstract P getParameter();

	/**
	 * Sets the parameter field with @param parameter
	 * 
	 * @param parameter the parameter to set
	 */
	public abstract void setParameter(P parameter);

	@Override
	public String toString() {
		return "GenericParameterConstraint [" + //$NON-NLS-1$
				"id=" + (id != null ? id.toString() : "") + //$NON-NLS-1$ //$NON-NLS-2$
				RscTools.COMMA + "rule=" + (rule != null ? rule : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}