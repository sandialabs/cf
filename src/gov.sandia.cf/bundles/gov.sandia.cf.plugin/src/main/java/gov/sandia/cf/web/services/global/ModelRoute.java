/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.global;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.web.WebClientRuntimeException;
import gov.sandia.cf.web.services.CFRoute;

/**
 * The Class Intended Purpose Route.
 * 
 * @author Didier Verstraete
 */
public class ModelRoute {

	private static final String WS_REST_CF_MODEL = "/model"; //$NON-NLS-1$
	private static final String WS_REST_CF_MODEL_GET = "/get"; //$NON-NLS-1$
	private static final String WS_REST_CF_MODEL_LIST = "/list"; //$NON-NLS-1$
	private static final String WS_REST_CF_MODEL_NEW = "/new"; //$NON-NLS-1$
	private static final String WS_REST_CF_MODEL_EDIT = "/edit"; //$NON-NLS-1$
	private static final String WS_REST_CF_MODEL_DELETE = "/delete"; //$NON-NLS-1$

	private ModelRoute() {
		// Do not implement
	}

	/**
	 * Model root route.
	 *
	 * @param model the model
	 * @return the string
	 */
	public static String rootUri(Model model) {
		if (model == null) {
			throw new WebClientRuntimeException(RscTools.getString(RscConst.EX_MODELROUTE_URI_MODEL_NULL));
		}
		return CFRoute.apiRoot() + WS_REST_CF_MODEL + "/" + model.getId(); //$NON-NLS-1$
	}

	/**
	 * Gets the relative uri for get method.
	 *
	 * @param model the model
	 * @return the get route
	 */
	public static String get(Model model) {
		return rootUri(model) + WS_REST_CF_MODEL_GET;
	}

	/**
	 * Gets the relative uri for list method.
	 *
	 * @return the list route
	 */
	public static String list() {
		return CFRoute.apiRoot() + WS_REST_CF_MODEL + "/" + WS_REST_CF_MODEL_LIST; //$NON-NLS-1$
	}

	/**
	 * Gets the relative uri for new method.
	 *
	 * @return the new route
	 */
	public static String create() {
		return CFRoute.apiRoot() + WS_REST_CF_MODEL + "/" + WS_REST_CF_MODEL_NEW; //$NON-NLS-1$
	}

	/**
	 * Gets the relative uri for update method.
	 *
	 * @param model the model
	 * @return the update route
	 */
	public static String update(Model model) {
		return rootUri(model) + WS_REST_CF_MODEL_EDIT;
	}

	/**
	 * Gets the relative uri for delete method.
	 *
	 * @param model the model
	 * @return the string
	 */
	public static String delete(Model model) {
		return rootUri(model) + WS_REST_CF_MODEL_DELETE;
	}
}
