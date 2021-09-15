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
 * The PCMM Assessment
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMMASSESSMENT")
public class PCMMAssessment implements Serializable, IEntity<PCMMAssessment, Integer> {

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
		ELEMENT("element"), //$NON-NLS-1$
		SUBELEMENT("subelement"), //$NON-NLS-1$
		USERCREATION("userCreation"), //$NON-NLS-1$
		ROLECREATION("roleCreation"), //$NON-NLS-1$
		TAG("tag"), //$NON-NLS-1$
		LEVEL("level"), //$NON-NLS-1$
		COMMENT("comment"), //$NON-NLS-1$
		DATE_UPDATE("dateUpdate"), //$NON-NLS-1$
		DATE_CREATION("dateCreation"); //$NON-NLS-1$

		private String field;

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
	 * The comment field linked to COMMENT column
	 */
	@Column(name = "COMMENT", columnDefinition = "LONGVARCHAR")
	private String comment;

	/**
	 * The roleCreation field linked to ROLE_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROLE_CREATION_ID")
	@NotNull(message = RscConst.EX_PCMMASSESSMENT_ROLE_NULL)
	private Role roleCreation;

	/**
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	@NotNull(message = RscConst.EX_PCMMASSESSMENT_USER_NULL)
	private User userCreation;

	/**
	 * The level field linked to PCMMLEVEL_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCMMLEVEL_ID")
	private PCMMLevel level;

	/**
	 * The element field linked to PCMMELEMENT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCMMELEMENT_ID")
	private PCMMElement element;

	/**
	 * The PCMMSubelement field linked to PCMMSUBELEMENT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCMMSUBELEMENT_ID")
	private PCMMSubelement subelement;

	/**
	 * The dateCreation field linked to DATE_CREATION column
	 */
	@Column(name = "DATE_CREATION")
	@NotNull(message = RscConst.EX_PCMMASSESSMENT_CREATIONDATE_NULL)
	private Date dateCreation;

	/**
	 * The dateUpdate field linked to DATE_UPDATE column
	 */
	@Column(name = "DATE_UPDATE")
	private Date dateUpdate;

	/**
	 * The tag field linked to TAG_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "TAG_ID")
	private Tag tag;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getComment() {
		return comment;
	}

	@SuppressWarnings("javadoc")
	public void setComment(String comment) {
		this.comment = comment;
	}

	@SuppressWarnings("javadoc")
	public Role getRoleCreation() {
		return roleCreation;
	}

	@SuppressWarnings("javadoc")
	public void setRoleCreation(Role role) {
		this.roleCreation = role;
	}

	@SuppressWarnings("javadoc")
	public User getUserCreation() {
		return userCreation;
	}

	@SuppressWarnings("javadoc")
	public void setUserCreation(User user) {
		this.userCreation = user;
	}

	@SuppressWarnings("javadoc")
	public PCMMElement getElement() {
		return element;
	}

	@SuppressWarnings("javadoc")
	public void setElement(PCMMElement element) {
		this.element = element;
	}

	@SuppressWarnings("javadoc")
	public PCMMSubelement getSubelement() {
		return subelement;
	}

	@SuppressWarnings("javadoc")
	public void setSubelement(PCMMSubelement subelement) {
		this.subelement = subelement;
	}

	@SuppressWarnings("javadoc")
	public PCMMLevel getLevel() {
		return level;
	}

	@SuppressWarnings("javadoc")
	public void setLevel(PCMMLevel level) {
		this.level = level;
	}

	@SuppressWarnings("javadoc")
	public Date getDateCreation() {
		return Optional.ofNullable(dateCreation).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = Optional.ofNullable(dateCreation).map(Date::getTime).map(Date::new).orElse(dateCreation);
	}

	@SuppressWarnings("javadoc")
	public Date getDateUpdate() {
		return Optional.ofNullable(dateUpdate).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = Optional.ofNullable(dateUpdate).map(Date::getTime).map(Date::new).orElse(dateUpdate);
	}

	@SuppressWarnings("javadoc")
	public Tag getTag() {
		return tag;
	}

	@SuppressWarnings("javadoc")
	public void setTag(Tag tag) {
		this.tag = tag;
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current PCMMLevel
	 */
	public PCMMAssessment copy() {
		PCMMAssessment entity = new PCMMAssessment();
		entity.setDateCreation(getDateCreation());
		entity.setDateUpdate(getDateUpdate());
		entity.setComment(getComment());
		entity.setElement(getElement());
		entity.setSubelement(getSubelement());
		entity.setRoleCreation(getRoleCreation());
		entity.setUserCreation(getUserCreation());
		entity.setLevel(getLevel());
		return entity;
	}
}
