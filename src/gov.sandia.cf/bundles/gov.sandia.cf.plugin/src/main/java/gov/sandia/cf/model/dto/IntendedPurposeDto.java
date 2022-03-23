/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;

/**
 * The Class IntendedPurposeDto.
 *
 * @author Didier Verstraete
 */
public class IntendedPurposeDto implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id field linked to ID column. */
	private Integer id;

	/** description: the description. */
	private String description;

	/** reference: the intended purpose reference. */
	private String reference;

	/** The model field linked to MODEL_ID column. */
	private Model model;

	/** The userUpdate field linked to USER_UPDATE_ID column. */
	private User userUpdate;

	/** The dateUpdate field linked to DATE_UPDATE column. */
	private Date dateUpdate;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the reference.
	 *
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * Sets the reference.
	 *
	 * @param reference the new reference
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * Sets the model.
	 *
	 * @param model the new model
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * Gets the user update.
	 *
	 * @return the user update
	 */
	public User getUserUpdate() {
		return userUpdate;
	}

	/**
	 * Sets the user update.
	 *
	 * @param userUpdate the new user update
	 */
	public void setUserUpdate(User userUpdate) {
		this.userUpdate = userUpdate;
	}

	/**
	 * Gets the date update.
	 *
	 * @return the date update
	 */
	public Date getDateUpdate() {
		return Optional.ofNullable(dateUpdate).map(Date::getTime).map(Date::new).orElse(null);
	}

	/**
	 * Sets the date update.
	 *
	 * @param dateUpdate the new date update
	 */
	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = Optional.ofNullable(dateUpdate).map(Date::getTime).map(Date::new).orElse(dateUpdate);
	}

}
