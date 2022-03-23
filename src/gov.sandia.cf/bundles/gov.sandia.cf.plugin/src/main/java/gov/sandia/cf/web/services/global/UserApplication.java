/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.global;

import java.util.List;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.global.IUserApplication;
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

	@Override
	public User getUserByUserID(String userID) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role getCurrentPCMMRole(String userID) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurrentPCMMRole(User user, Role roleSelected) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<User> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUserById(Integer id) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User addUser(User user) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User updateUser(User user) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUser(User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}
}
