/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.util.List;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.User;

/**
 * Interface to manage User Application methods
 * 
 * @author Didier Verstraete
 *
 */
public interface IUserApplication extends IApplication {

	/**
	 * @param userID the user id into the eclipse environment
	 * @return the user from the parameter userID. If the user does not exists, this
	 *         methods creates it.
	 * @throws CredibilityException if an error occured.
	 */
	User getUserByUserID(String userID) throws CredibilityException;

	/**
	 * @param userID the user id
	 * @return the current PCMM role of the userID in parameter. If the user
	 *         associated to the userID is null, return null.
	 * @throws CredibilityException if an error occured.
	 */
	Role getCurrentPCMMRole(String userID) throws CredibilityException;

	/**
	 * Sets the current role to the user
	 * 
	 * @param user         the user
	 * @param roleSelected the role selected
	 * @throws CredibilityException if an error occured.
	 */
	void setCurrentPCMMRole(User user, Role roleSelected) throws CredibilityException;

	/**
	 * @return all the users
	 */
	List<User> getUsers();

	/**
	 * @param id the id to find the object
	 * @return the user associated to the id
	 * @throws CredibilityException if an error occured while retrieving the object
	 */
	User getUserById(Integer id) throws CredibilityException;

	/**
	 * @param user the user to add
	 * @return the new user created
	 * @throws CredibilityException if an error occured while adding new user
	 */
	User addUser(User user) throws CredibilityException;

	/**
	 * @param user the user to update
	 * @return the updated user
	 * @throws CredibilityException if an error occured while updating user
	 */
	User updateUser(User user) throws CredibilityException;

	/**
	 * Deletes parameter user from database
	 * 
	 * @param user the user to delete
	 * @throws CredibilityException if an error occured while deleting user
	 */
	void deleteUser(User user) throws CredibilityException;

}
