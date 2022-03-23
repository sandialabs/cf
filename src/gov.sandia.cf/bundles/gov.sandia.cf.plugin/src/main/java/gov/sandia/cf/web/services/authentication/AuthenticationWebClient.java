/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.User;
import gov.sandia.cf.web.services.AWebClient;
import gov.sandia.cf.web.services.IWebClientManager;

/**
 * Manage Web authentication
 * 
 * @author Didier Verstraete
 *
 */
public class AuthenticationWebClient extends AWebClient implements IAuthenticationService {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationWebClient.class);

	/**
	 * Instantiates a new web authentication service.
	 */
	public AuthenticationWebClient() {
	}

	/**
	 * Instantiates a new web authentication service.
	 *
	 * @param webClientMgr the web client mgr
	 */
	public AuthenticationWebClient(IWebClientManager webClientMgr) {
		super(webClientMgr);
	}

	/** {@inheritDoc} */
	@Override
	public User getUser(String userID) throws CredibilityException {
		logger.debug("Calling getUser method"); //$NON-NLS-1$

		// TODO implement with real web service
		User user = new User();
		user.setUserID(userID);
		return user;
	}

	/** {@inheritDoc} */
	@Override
	public boolean connect(String serverURL, String userID, String password) throws CredibilityException {
		// TODO implement real connection
		return true;
	}

}