/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import gov.sandia.cf.model.query.EntityFilter;

/**
 * 
 * The ARG Parameters QoI options selected class.
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "ARG_PARAMETERS_QOI_OPTIONS")
public class ARGParametersQoIOption implements Serializable, IEntity<ARGParametersQoIOption, Integer> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		ARG_PARAMETER("argParameter"), //$NON-NLS-1$
		QOI("qoi"), //$NON-NLS-1$
		TAG("tag"), //$NON-NLS-1$
		ENABLED("enabled"); //$NON-NLS-1$

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
	 * argParameter: indicates which ARG Parameter is selected (only one must exist)
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "ARG_PARAM_ID")
	private ARGParameters argParameter;

	/**
	 * qoi: indicates which QoI is selected
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "QOI_ID")
	private QuantityOfInterest qoi;

	/**
	 * tag: indicates which QoI tag is selected
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "QOI_TAG_ID")
	private QuantityOfInterest tag;

	/**
	 * enabled: indicates if the QoI selected is enabled
	 */
	@Column(name = "ENABLED")
	private Boolean enabled;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public ARGParameters getArgParameter() {
		return argParameter;
	}

	@SuppressWarnings("javadoc")
	public void setArgParameter(ARGParameters argParameter) {
		this.argParameter = argParameter;
	}

	@SuppressWarnings("javadoc")
	public QuantityOfInterest getQoi() {
		return qoi;
	}

	@SuppressWarnings("javadoc")
	public void setQoi(QuantityOfInterest qoi) {
		this.qoi = qoi;
	}

	@SuppressWarnings("javadoc")
	public Boolean getEnabled() {
		return enabled;
	}

	@SuppressWarnings("javadoc")
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@SuppressWarnings("javadoc")
	public QuantityOfInterest getTag() {
		return tag;
	}

	@SuppressWarnings("javadoc")
	public void setTag(QuantityOfInterest tag) {
		this.tag = tag;
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current ARGParametersQoIOption
	 */
	public ARGParametersQoIOption copy() {
		ARGParametersQoIOption entity = new ARGParametersQoIOption();
		entity.setArgParameter(getArgParameter());
		entity.setQoi(getQoi());
		entity.setTag(getTag());
		entity.setEnabled(getEnabled());
		return entity;
	}

}
