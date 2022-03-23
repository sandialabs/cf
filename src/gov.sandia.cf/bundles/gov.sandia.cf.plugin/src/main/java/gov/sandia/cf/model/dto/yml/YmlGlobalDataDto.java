/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.yml;

import java.io.Serializable;
import java.util.List;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.User;

/**
 * Contains global data for import/export.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlGlobalDataDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5033573274149749559L;

	/** The model. */
	private Model model;

	/** The users. */
	private List<User> users;

	/** The roles. */
	private List<Role> roles;

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
	 * Gets the users.
	 *
	 * @return the users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * Sets the users.
	 *
	 * @param users the new users
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	/**
	 * Gets the roles.
	 *
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * Sets the roles.
	 *
	 * @param roles the new roles
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}
