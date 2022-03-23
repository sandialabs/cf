/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.authentication;

import gov.sandia.cf.web.services.CFRoute;

/**
 * The Class Authentication Route.
 * 
 * @author Didier Verstraete
 */
public class AuthenticationRoute {

	private static final String WS_REST_CF_AUTH_LOGIN = "/login"; //$NON-NLS-1$

	private AuthenticationRoute() {
		// Do not implement
	}

	/**
	 * Gets the relative uri for login method.
	 *
	 * @return the login route
	 */
	public static String login() {
		return CFRoute.apiRoot() + WS_REST_CF_AUTH_LOGIN;
	}
}
