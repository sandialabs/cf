/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.util.List;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IUserApplication;
import gov.sandia.cf.dao.IUserRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.User;

/**
 * Manage User Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class UserApplication extends AApplication implements IUserApplication {

	/**
	 * The constructor
	 */
	public UserApplication() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public UserApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getUserByUserID(String userID) throws CredibilityException {

		User userFound = getDaoManager().getRepository(IUserRepository.class).findByUserId(userID);

		if (userFound == null) {
			User user = new User();
			user.setUserID(userID);
			userFound = addUser(user);
		}

		return userFound;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role getCurrentPCMMRole(String userID) throws CredibilityException {
		// Get user by User ID (created if not exist)
		User user = getUserByUserID(userID);

		Role roleToReturn = null;

		if (user != null) {
			roleToReturn = user.getRolePCMM();
		}

		return roleToReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCurrentPCMMRole(final User user, final Role roleSelected) throws CredibilityException {
		if (user != null) {
			User userTemp = user;
			userTemp.setRolePCMM(roleSelected);
			updateUser(userTemp);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> getUsers() {
		return getDaoManager().getRepository(IUserRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getUserById(Integer id) throws CredibilityException {
		return getDaoManager().getRepository(IUserRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User addUser(User user) throws CredibilityException {
		return getDaoManager().getRepository(IUserRepository.class).create(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User updateUser(User user) throws CredibilityException {
		return getDaoManager().getRepository(IUserRepository.class).update(user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUser(User user) throws CredibilityException {
		getDaoManager().getRepository(IUserRepository.class).delete(user);
	}
}
