/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.authentication;

import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.User;
import gov.sandia.cf.web.services.IWebClient;

/**
 * The Interface ISetupService used to initialize the project.
 * 
 * @author Didier Verstraete
 */
@Service
public interface IAuthenticationService extends IWebClient {

	/**
	 * Gets the user.
	 *
	 * @param userID the user ID
	 * @return the user
	 * @throws CredibilityException the credibility exception
	 */
	User getUser(String userID) throws CredibilityException;

	/**
	 * Connect.
	 *
	 * @param serverURL the server URL
	 * @param userID    the user ID
	 * @param password  the password
	 * @return true, if successful
	 * @throws CredibilityException the credibility exception
	 */
	boolean connect(String serverURL, String userID, String password) throws CredibilityException;
}
