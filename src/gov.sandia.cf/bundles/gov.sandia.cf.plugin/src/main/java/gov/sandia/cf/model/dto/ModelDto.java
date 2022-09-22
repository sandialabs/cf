/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto;

import java.io.Serializable;

/**
 * The Class ModelDto.
 *
 * @author Didier Verstraete
 */
public class ModelDto implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	private Integer id;
	
	/** The application. */
	private String application;
	
	/** The contact. */
	private String contact;

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
	 * Gets the application.
	 *
	 * @return the application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * Sets the application.
	 *
	 * @param application the new application
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * Gets the contact.
	 *
	 * @return the contact
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * Sets the contact.
	 *
	 * @param contact the new contact
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

}