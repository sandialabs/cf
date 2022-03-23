/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.status;

/**
 * The Class Status Route Parameters.
 * 
 * @author Didier Verstraete
 */
public class StatusRouteParam {

	/**
	 * Parameters and Values.
	 */
	private static final String WS_REST_CF_STATUS_PING_OK_PARAM = "Success"; //$NON-NLS-1$

	private StatusRouteParam() {
		// Do not implement
	}

	/**
	 * Checks if is success ping response.
	 *
	 * @param response the response
	 * @return true, if is success ping response
	 */
	public static boolean isSuccessPingResponse(String response) {
		return WS_REST_CF_STATUS_PING_OK_PARAM.equals(response);
	}
}
