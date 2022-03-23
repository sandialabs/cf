/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.status;

import gov.sandia.cf.web.services.CFRoute;

/**
 * The Class Status Route.
 * 
 * @author Didier Verstraete
 */
public class StatusRoute {

	/**
	 * Routes.
	 */
	private static final String WS_REST_CF_STATUS_PING = "/status"; //$NON-NLS-1$

	private StatusRoute() {
		// Do not implement
	}

	/**
	 * Gets the relative uri for ping method.
	 *
	 * @return the ping route
	 */
	public static String ping() {
		return CFRoute.apiRoot() + WS_REST_CF_STATUS_PING;
	}
}
