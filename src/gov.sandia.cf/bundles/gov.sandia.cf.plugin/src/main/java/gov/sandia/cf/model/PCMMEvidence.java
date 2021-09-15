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

import gov.sandia.cf.application.configuration.ParameterLinkGson;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.GsonTools;
import gov.sandia.cf.tools.RscConst;

/**
 * The PCMM evidence
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "PCMMEVIDENCE")
public class PCMMEvidence implements Serializable, IEntity<PCMMEvidence, Integer> {

	private static final long serialVersionUID = 1L;

	/**
	 * Field Filter
	 */
	@SuppressWarnings("javadoc")
	public enum Filter implements EntityFilter {
		ID("id"), //$NON-NLS-1$
		NAME("name"), //$NON-NLS-1$
		DESCRIPTION("description"), //$NON-NLS-1$
		ELEMENT("element"), //$NON-NLS-1$
		SUBELEMENT("subelement"), //$NON-NLS-1$
		USERCREATION("userCreation"), //$NON-NLS-1$
		ROLECREATION("roleCreation"), //$NON-NLS-1$
		TAG("tag"), //$NON-NLS-1$
		VALUE("value"), //$NON-NLS-1$
		DATE_CREATION("dateCreation"), //$NON-NLS-1$
		DATE_UPDATE("dateUpdate"), //$NON-NLS-1$
		DATE_FILE("dateFile"), //$NON-NLS-1$
		SECTION("section"); //$NON-NLS-1$

		private String field;

		Filter(String field) {
			this.field = field;
		}

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
	private String name;

	/**
	 * The section field linked to SECTION column
	 */
	@Column(name = "SECTION")
	private String section;

	/**
	 * The path field linked to PATH column
	 */
	@Column(name = "VALUE")
	@NotBlank(message = RscConst.EX_PCMMEVIDENCE_PATH_BLANK)
	private String value;

	/**
	 * The description field linked to DESCRIPTION column
	 */
	@Column(name = "DESCRIPTION", columnDefinition = "LONGVARCHAR")
	private String description;

	/**
	 * The element field linked to PCMMELEMENT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCMMELEMENT_ID")
	private PCMMElement element;

	/**
	 * The subelement field linked to PCMMSUBELEMENT_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCMMSUBELEMENT_ID")
	private PCMMSubelement subelement;

	/**
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	@NotNull(message = RscConst.EX_PCMMEVIDENCE_USER_NULL)
	private User userCreation;

	/**
	 * The roleCreation field linked to ROLE_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROLE_CREATION_ID")
	@NotNull(message = RscConst.EX_PCMMEVIDENCE_ROLE_NULL)
	private Role roleCreation;

	/**
	 * The dateCreation field linked to DATE_CREATION column
	 */
	@Column(name = "DATE_CREATION")
	@NotNull(message = RscConst.EX_PCMMEVIDENCE_CREATIONDATE_NULL)
	private Date dateCreation;

	/**
	 * The dateUpdate field linked to DATE_UPDATE column
	 */
	@Column(name = "DATE_UPDATE")
	private Date dateUpdate;

	/**
	 * The dateFile field linked to DATE_FILE column
	 */
	@Column(name = "DATE_FILE")
	private Date dateFile;

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
	public String getName() {
		return name;
	}

	@SuppressWarnings("javadoc")
	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("javadoc")
	public String getSection() {
		return section;
	}

	@SuppressWarnings("javadoc")
	public void setSection(String section) {
		this.section = section;
	}

	@SuppressWarnings("javadoc")
	public String getValue() {
		return value;
	}

	@SuppressWarnings("javadoc")
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the type
	 */
	public FormFieldType getType() {
		FormFieldType type = null;
		ParameterLinkGson linkGson = GsonTools.getFromGson(value, ParameterLinkGson.class);
		if (linkGson != null) {
			type = linkGson.type;
		}
		return type;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		String path = null;
		ParameterLinkGson linkGson = GsonTools.getFromGson(value, ParameterLinkGson.class);
		if (linkGson != null) {
			path = linkGson.value;
		}
		return path;
	}

	/**
	 * Set the file path. Default type is setted to FILE
	 * 
	 * @param filePath the evidence file path
	 */
	public void setFilePath(String filePath) {
		ParameterLinkGson jsonObject = new ParameterLinkGson();
		jsonObject.type = FormFieldType.LINK_FILE;
		jsonObject.value = filePath;

		// Encode JSON
		setValue(GsonTools.toGson(jsonObject));
	}

	/**
	 * Set the URL. Default type is setted to URL.
	 * 
	 * @param url the evidence url
	 */
	public void setURL(String url) {
		ParameterLinkGson jsonObject = new ParameterLinkGson();
		jsonObject.type = FormFieldType.LINK_URL;
		jsonObject.value = url;

		// Encode JSON
		setValue(GsonTools.toGson(jsonObject));
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
	public Date getDateFile() {
		return Optional.ofNullable(dateFile).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setDateFile(Date dateFile) {
		this.dateFile = Optional.ofNullable(dateFile).map(Date::getTime).map(Date::new).orElse(null);
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
		this.dateUpdate = Optional.ofNullable(dateUpdate).map(Date::getTime).map(Date::new).orElse(null);
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
	public PCMMEvidence copy() {
		PCMMEvidence entity = new PCMMEvidence();
		entity.setName(getName());
		entity.setDateCreation(getDateCreation());
		entity.setDateUpdate(getDateUpdate());
		entity.setDateFile(getDateFile());
		entity.setDescription(getDescription());
		entity.setValue(getValue());
		entity.setUserCreation(getUserCreation());
		entity.setRoleCreation(getRoleCreation());
		entity.setElement(getElement());
		entity.setSubelement(getSubelement());
		return entity;
	}

}
