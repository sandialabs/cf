/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;

/**
 * 
 * The Intended Purpose class.
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "INTENDED_PURPOSE")
public class IntendedPurpose implements Serializable, IEntity<IntendedPurpose, Integer> {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		DESCRIPTION("description"), //$NON-NLS-1$
		REFERENCE_LINK("reference"), //$NON-NLS-1$
		USER_UPDATE("userUpdate"), //$NON-NLS-1$
		DATE_UPDATE("dateUpdate"), //$NON-NLS-1$
		MODEL("model"); //$NON-NLS-1$

		private String field;

		/**
		 * Filter
		 * 
		 * @param field
		 */
		Filter(String field) {
			this.field = field;
		}

		/** {@inheritDoc} */
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
	 * description: the description
	 */
	@Column(name = "DESCRIPTION", columnDefinition = "LONGVARCHAR")
	private String description;

	/**
	 * reference: the intended purpose reference
	 */
	@Column(name = "REFERENCE_LINK", columnDefinition = "LONGVARCHAR")
	private String reference;

	/**
	 * The model field linked to MODEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@NotNull(message = RscConst.EX_INTENDEDPURPOSE_MODEL_NULL)
	private Model model;

	/**
	 * The userUpdate field linked to USER_UPDATE_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_UPDATE_ID")
	private User userUpdate;

	/**
	 * The dateUpdate field linked to DATE_UPDATE column
	 */
	@Column(name = "DATE_UPDATE")
	private Date dateUpdate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getDescription() {
		return description;
	}

	@SuppressWarnings("javadoc")
	public void setDescription(String description) {
		this.description = description;
	}

	@SuppressWarnings("javadoc")
	public String getReference() {
		return reference;
	}

	@SuppressWarnings("javadoc")
	public void setReference(String reference) {
		this.reference = reference;
	}

	@SuppressWarnings("javadoc")
	public Model getModel() {
		return model;
	}

	@SuppressWarnings("javadoc")
	public void setModel(Model model) {
		this.model = model;
	}

	@SuppressWarnings("javadoc")
	public User getUserUpdate() {
		return userUpdate;
	}

	@SuppressWarnings("javadoc")
	public void setUserUpdate(User userUpdate) {
		this.userUpdate = userUpdate;
	}

	@SuppressWarnings("javadoc")
	public Date getDateUpdate() {
		return Optional.ofNullable(dateUpdate).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = Optional.ofNullable(dateUpdate).map(Date::getTime).map(Date::new).orElse(dateUpdate);
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current Intended Purpose
	 */
	public IntendedPurpose copy() {
		IntendedPurpose entity = new IntendedPurpose();
		entity.setDescription(getDescription());
		entity.setReference(getReference());
		entity.setModel(getModel());
		entity.setDateUpdate(getDateUpdate());
		entity.setUserUpdate(getUserUpdate());
		return entity;
	}

}
