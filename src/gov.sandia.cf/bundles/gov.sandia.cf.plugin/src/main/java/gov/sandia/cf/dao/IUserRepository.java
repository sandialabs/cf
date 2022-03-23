/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import gov.sandia.cf.model.User;

/**
 * the User repository interface
 * 
 * @author Didier Verstraete
 *
 */
@Repository
public interface IUserRepository extends ICRUDRepository<User, Integer> {

	/**
	 * @param userID the user id to search
	 * @return the user associated to the parameter userID
	 */
	User findByUserId(String userID);

}
