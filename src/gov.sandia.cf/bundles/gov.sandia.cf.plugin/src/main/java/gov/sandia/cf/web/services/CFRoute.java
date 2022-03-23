/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services;

/**
 * The Class CFRoute.
 * 
 * @author Didier Verstraete
 */
public class CFRoute {

	private static final String WS_REST_CF_API = "/api"; //$NON-NLS-1$

	private CFRoute() {
		// Do not implement
	}

	/**
	 * Get the Api root.
	 *
	 * @return the string
	 */
	public static String apiRoot() {
		return WS_REST_CF_API;
	}
}
