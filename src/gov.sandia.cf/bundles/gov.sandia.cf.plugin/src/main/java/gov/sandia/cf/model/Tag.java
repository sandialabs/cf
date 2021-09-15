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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;

/**
 * The Tag
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "TAG")
public class Tag implements Serializable, IEntity<Tag, Integer> {

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
		NAME("name"), //$NON-NLS-1$
		DESCRIPTION("description"), //$NON-NLS-1$
		DATE_TAG("dateTag"), //$NON-NLS-1$
		USERCREATION("userCreation"); //$NON-NLS-1$

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
	 * The name field linked to NAME column
	 */
	@Column(name = "NAME")
	@NotBlank(message = RscConst.EX_TAG_NAME_BLANK)
	private String name;

	/**
	 * The description field linked to DESCRIPTION column
	 */
	@Column(name = "DESCRIPTION")
	private String description;

	/**
	 * The dateTag field linked to DATETAG column
	 */
	@Column(name = "DATETAG")
	@NotNull(message = RscConst.EX_TAG_DATETAG_NULL)
	private Date dateTag;

	/**
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	@NotNull(message = RscConst.EX_TAG_USERCREATION_NULL)
	private User userCreation;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@SuppressWarnings("javadoc")
	public String getName() {
		return name;
	}

	@SuppressWarnings("javadoc")
	public void setName(String name) {
		this.name = name;
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
	public Date getDateTag() {
		return Optional.ofNullable(dateTag).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setDateTag(Date dateTag) {
		this.dateTag = Optional.ofNullable(dateTag).map(Date::getTime).map(Date::new).orElse(dateTag);
	}

	@SuppressWarnings("javadoc")
	public User getUserCreation() {
		return userCreation;
	}

	@SuppressWarnings("javadoc")
	public void setUserCreation(User userCreation) {
		this.userCreation = userCreation;
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current User
	 */
	@Override
	public Tag copy() {
		Tag tag = new Tag();
		tag.setDateTag(getDateTag());
		tag.setDescription(getDescription());
		tag.setName(getName());
		return tag;
	}

}
