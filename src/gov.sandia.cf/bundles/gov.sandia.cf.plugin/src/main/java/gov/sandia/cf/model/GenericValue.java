/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import gov.sandia.cf.application.configuration.ParameterLinkGson;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.GsonTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * The GenericValue entity class
 * 
 * @author Didier Verstraete
 * @param <P> The generic parameter type class
 * @param <E> The entity class
 *
 */
@MappedSuperclass
public abstract class GenericValue<P extends GenericParameter<P>, E> implements Serializable, IEntity<E, Integer> {

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
		PARAMETER("parameter"), //$NON-NLS-1$
		VALUE("value"), //$NON-NLS-1$
		USER_CREATION("userCreation"), //$NON-NLS-1$
		DATE_CREATION("dateCreation"), //$NON-NLS-1$
		USER_UPDATE("userUpdate"), //$NON-NLS-1$
		DATE_UPDATE("dateUpdate"); //$NON-NLS-1$

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
	 * The value field linked to VALUE column
	 */
	@Column(name = "VALUE", columnDefinition = "LONGVARCHAR")
	private String value;

	/**
	 * The userCreation field linked to USER_CREATION_ID column
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREATION_ID")
	@NotNull(message = RscConst.EX_GENERICVALUE_USERCREATION_NULL)
	private User userCreation;

	/**
	 * The dateCreation field linked to DATE_CREATION column
	 */
	@Column(name = "DATE_CREATION")
	@NotNull(message = RscConst.EX_GENERICVALUE_DATECREATION_NULL)
	private Date dateCreation;

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

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id field with @param id
	 * 
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value field with @param value
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the userCreation
	 */
	public User getUserCreation() {
		return userCreation;
	}

	/**
	 * 
	 * Sets the userCreation field with @param userCreation
	 * 
	 * @param user the userCreation to set
	 */
	public void setUserCreation(User user) {
		this.userCreation = user;
	}

	/**
	 * @return the dateCreation
	 */
	public Date getDateCreation() {
		return Optional.ofNullable(dateCreation).map(Date::getTime).map(Date::new).orElse(null);
	}

	/**
	 * 
	 * Sets the dateCreation field with @param dateCreation
	 * 
	 * @param dateCreation the dateCreation to set
	 */
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = Optional.ofNullable(dateCreation).map(Date::getTime).map(Date::new).orElse(dateCreation);
	}

	/**
	 * @return the userUpdate
	 */
	public User getUserUpdate() {
		return userUpdate;
	}

	/**
	 * 
	 * Sets the userUpdate field with @param userUpdate
	 * 
	 * @param user the userUpdate to set
	 */
	public void setUserUpdate(User user) {
		this.userUpdate = user;
	}

	/**
	 * @return the dateUpdate
	 */
	public Date getDateUpdate() {
		return Optional.ofNullable(dateUpdate).map(Date::getTime).map(Date::new).orElse(null);
	}

	/**
	 * 
	 * Sets the dateUpdate field with @param dateUpdate
	 * 
	 * @param dateUpdate the dateUpdate to set
	 */
	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = Optional.ofNullable(dateUpdate).map(Date::getTime).map(Date::new).orElse(dateUpdate);
	}

	@Override
	public String toString() {
		return "GenericValue [id=" + id + ", value=" + value + ", userCreation=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ userCreation + ", dateCreation=" + dateCreation + ", userUpdate=" + userUpdate + ", dateUpdate=" //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
				+ dateUpdate + "]"; //$NON-NLS-1$
	}

	/**
	 * Get value with a readable format
	 * 
	 * @return The value with a readable format
	 */
	public String getReadableValue() {
		String readableValue = RscTools.empty();
		if (getParameter() == null || getValue() == null) {
			return readableValue;
		}

		if (getParameter().getType().equals(FormFieldType.DATE.toString())) {
			readableValue = StringTools.clearHtml(getValue());
		} else if (getParameter().getType().equals(FormFieldType.LINK.toString())) {
			ParameterLinkGson json = GsonTools.getFromGson(getValue(), ParameterLinkGson.class);
			readableValue = json.value;
		} else if (getParameter().getType().equals(FormFieldType.RICH_TEXT.toString())) {
			readableValue = StringTools.clearHtml(getValue());
		} else if (getParameter().getType().equals(FormFieldType.SELECT.toString())) {
			Optional<GenericParameterSelectValue<P>> parameterValue = getParameter().getParameterValueList().stream()
					.filter(param -> param.getId().toString().equals(getValue())).findFirst();
			if (parameterValue.isPresent()) {
				readableValue = parameterValue.get().getName();
			}
		} else {
			readableValue = getValue();
		}

		return readableValue;
	}

}
