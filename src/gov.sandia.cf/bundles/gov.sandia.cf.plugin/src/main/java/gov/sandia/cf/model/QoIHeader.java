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
import gov.sandia.cf.tools.RscTools;

/**
 * 
 * The QoIHeader entity class linked to table QOIHEADER
 * 
 * @author Didier Verstraete
 *
 */
@Entity
@Table(name = "QOIHEADER")
public class QoIHeader implements Serializable, IEntity<QoIHeader, Integer> {

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
		VALUE("value"), //$NON-NLS-1$
		DATE_CREATION("creationDate"), //$NON-NLS-1$
		USER_CREATION("userCreation"), //$NON-NLS-1$
		DATE_UPDATE("updateDate"), //$NON-NLS-1$
		USER_UPDATE("userUpdate"), //$NON-NLS-1$
		QOI("qoi"); //$NON-NLS-1$

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
	@NotBlank(message = RscConst.EX_QOIHEADER_NAME_BLANK)
	private String name;

	/**
	 * The value field linked to VALUE column
	 */
	@Column(name = "VALUE")
	private String value;

	/**
	 * The creationDate field linked to CREATION_DATE column
	 */
	@Column(name = "CREATION_DATE")
	@NotNull(message = RscConst.EX_QOIHEADER_CREATIONDATE_NULL)
	private Date creationDate;

	/**
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	@NotNull(message = RscConst.EX_QOIHEADER_USERCREATION_NULL)
	private User userCreation;

	/**
	 * The creationDate field linked to UPDATE_DATE column
	 */
	@Column(name = "UPDATE_DATE")
	private Date updateDate;

	/**
	 * The userCreation field linked to USER_UPDATE_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_UPDATE_ID")
	private User userUpdate;

	/**
	 * The qoi field linked to QOI_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QOI_ID")
	@NotNull(message = RscConst.EX_QOIHEADER_QOI_NULL)
	private QuantityOfInterest qoi;

	/**
	 * QoIHeader constructor
	 */
	public QoIHeader() {
	}

	/**
	 * QoIHeader constructor with all fields
	 */
	/**
	 * @param id    the id
	 * @param name  the name
	 * @param value the value
	 * @param qoi   the qoi to associate
	 */
	public QoIHeader(Integer id, String name, String value, QuantityOfInterest qoi) {
		this.id = id;
		this.name = name;
		this.value = value;
		this.qoi = qoi;
		this.creationDate = new Date();
	}

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
	public String getValue() {
		return value;
	}

	@SuppressWarnings("javadoc")
	public void setValue(String value) {
		this.value = value;
	}

	@SuppressWarnings("javadoc")
	public Date getCreationDate() {
		return Optional.ofNullable(creationDate).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setCreationDate(Date creationDate) {
		this.creationDate = Optional.ofNullable(creationDate).map(Date::getTime).map(Date::new).orElse(creationDate);
	}

	@SuppressWarnings("javadoc")
	public User getUserCreation() {
		return userCreation;
	}

	@SuppressWarnings("javadoc")
	public void setUserCreation(User userCreation) {
		this.userCreation = userCreation;
	}

	@SuppressWarnings("javadoc")
	public Date getUpdateDate() {
		return Optional.ofNullable(updateDate).map(Date::getTime).map(Date::new).orElse(null);
	}

	@SuppressWarnings("javadoc")
	public void setUpdateDate(Date updateDate) {
		this.updateDate = Optional.ofNullable(updateDate).map(Date::getTime).map(Date::new).orElse(updateDate);
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
	public QuantityOfInterest getQoi() {
		return qoi;
	}

	@SuppressWarnings("javadoc")
	public void setQoi(QuantityOfInterest qoi) {
		this.qoi = qoi;
	}

	@Override
	public String toString() {
		return "QoIHeader [" + "id=" + (id != null ? id.toString() : "") + RscTools.COMMA + "name=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ (name != null ? name : "") + RscTools.COMMA + "value=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (value != null ? value : "") + RscTools.COMMA + "qoi=" //$NON-NLS-1$ //$NON-NLS-2$
				+ (qoi != null ? qoi.toString() : "") + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Create a copy of current entity with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current QoiHeader
	 */
	public QoIHeader copy() {
		QoIHeader entity = new QoIHeader();
		entity.setName(getName());
		entity.setValue(getValue());
		entity.setCreationDate(getCreationDate());
		entity.setUserCreation(getUserCreation());
		entity.setUpdateDate(getUpdateDate());
		entity.setUserUpdate(getUserUpdate());
		entity.setQoi(getQoi());
		return entity;
	}

}