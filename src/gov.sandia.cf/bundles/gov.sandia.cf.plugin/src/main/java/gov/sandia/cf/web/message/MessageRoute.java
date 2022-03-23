/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.message;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.web.WebClientRuntimeException;
import gov.sandia.cf.web.services.CFRoute;

/**
 * The Class MessageRoute.
 *
 * @author Didier Verstraete
 */
public class MessageRoute {

	private static final String WS_REST_CF_MESSAGE = "/message"; //$NON-NLS-1$
	private static final String WS_REST_CF_MESSAGE_CONNECT = "/connect"; //$NON-NLS-1$
	private static final String WS_REST_CF_MESSAGE_DISCONNECT = "/disconnect"; //$NON-NLS-1$
	private static final String WS_REST_CF_MESSAGE_GET = "/get"; //$NON-NLS-1$
	private static final String WS_REST_CF_MESSAGE_SUBSCRIBE = "/subscribe"; //$NON-NLS-1$

	private static final String WS_REST_CF_MESSAGE_SUBSCRIBE_MODEL = "/model"; //$NON-NLS-1$

	private MessageRoute() {
		// Do not implement
	}

	/**
	 * The Root Uri.
	 *
	 * @return the route
	 */
	public static String rootUri() {
		return CFRoute.apiRoot() + WS_REST_CF_MESSAGE;
	}

	/**
	 * Connect.
	 *
	 * @return the string
	 */
	public static String connect() {
		return rootUri() + WS_REST_CF_MESSAGE_CONNECT;
	}

	/**
	 * Gets the.
	 *
	 * @param memberId the member id
	 * @return the string
	 */
	public static String get(String memberId) {
		if (memberId == null) {
			throw new WebClientRuntimeException(RscTools.getString(RscConst.EX_MESSAGEROUTE_URI_MEMBERID_NULL));
		}
		return rootUri() + WS_REST_CF_MESSAGE_GET + "/" + memberId; //$NON-NLS-1$
	}

	/**
	 * Subscribe to model.
	 *
	 * @param memberId the member id
	 * @param model    the model
	 * @return the string
	 */
	public static String subscribeToModel(String memberId, Model model) {
		if (memberId == null) {
			throw new WebClientRuntimeException(RscTools.getString(RscConst.EX_MESSAGEROUTE_URI_MEMBERID_NULL));
		}
		if (model == null) {
			throw new WebClientRuntimeException(RscTools.getString(RscConst.EX_MESSAGEROUTE_URI_MODEL_NULL));
		}
		return rootUri() + WS_REST_CF_MESSAGE_SUBSCRIBE + "/" + memberId + WS_REST_CF_MESSAGE_SUBSCRIBE_MODEL + "/" //$NON-NLS-1$ //$NON-NLS-2$
				+ model.getId();
	}

	/**
	 * Disconnect.
	 *
	 * @param memberId the member id
	 * @return the string
	 */
	public static String disconnect(String memberId) {
		if (memberId == null) {
			throw new WebClientRuntimeException(RscTools.getString(RscConst.EX_MESSAGEROUTE_URI_MEMBERID_NULL));
		}
		return rootUri() + WS_REST_CF_MESSAGE_DISCONNECT + "/" + memberId; //$NON-NLS-1$
	}

}
