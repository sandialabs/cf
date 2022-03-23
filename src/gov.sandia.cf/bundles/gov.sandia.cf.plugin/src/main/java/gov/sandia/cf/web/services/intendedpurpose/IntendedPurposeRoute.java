/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.intendedpurpose;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.web.services.global.ModelRoute;

/**
 * The Class Intended Purpose Route.
 * 
 * @author Didier Verstraete
 */
public class IntendedPurposeRoute {

	private static final String WS_REST_CF_INTENDEDPURPOSE = "/purpose"; //$NON-NLS-1$
	private static final String WS_REST_CF_INTENDEDPURPOSE_GET = "/get"; //$NON-NLS-1$
	private static final String WS_REST_CF_INTENDEDPURPOSE_LOCK = "/lock"; //$NON-NLS-1$
	private static final String WS_REST_CF_INTENDEDPURPOSE_LOCK_INFO = "/lock/info"; //$NON-NLS-1$
	private static final String WS_REST_CF_INTENDEDPURPOSE_UNLOCK = "/unlock"; //$NON-NLS-1$
	private static final String WS_REST_CF_INTENDEDPURPOSE_EDIT = "/edit"; //$NON-NLS-1$
	private static final String WS_REST_CF_INTENDEDPURPOSE_SUBSCRIBE = "/subscribe"; //$NON-NLS-1$
	private static final String WS_REST_CF_INTENDEDPURPOSE_UNSUBSCRIBE = "/unsubscribe"; //$NON-NLS-1$

	private IntendedPurposeRoute() {
		// Do not implement
	}

	/**
	 * The Root Uri.
	 *
	 * @param model the model
	 * @return the route
	 */
	public static String rootUri(Model model) {
		return ModelRoute.rootUri(model) + WS_REST_CF_INTENDEDPURPOSE;
	}

	/**
	 * Gets the relative uri for get method.
	 *
	 * @param model the model
	 * @return the get route
	 */
	public static String get(Model model) {
		return rootUri(model) + WS_REST_CF_INTENDEDPURPOSE_GET;
	}

	/**
	 * Gets the relative uri for update method.
	 *
	 * @param model the model
	 * @return the update route
	 */
	public static String update(Model model) {
		return rootUri(model) + WS_REST_CF_INTENDEDPURPOSE_EDIT;
	}

	/**
	 * Gets the relative uri for lock method.
	 *
	 * @param model the model
	 * @return the lock route
	 */
	public static String lock(Model model) {
		return rootUri(model) + WS_REST_CF_INTENDEDPURPOSE_LOCK;
	}

	/**
	 * Gets the relative uri for unlock method.
	 *
	 * @param model the model
	 * @return the unlock route
	 */
	public static String unlock(Model model) {
		return rootUri(model) + WS_REST_CF_INTENDEDPURPOSE_UNLOCK;
	}

	/**
	 * Gets the relative uri for lockInfo method.
	 *
	 * @param model the model
	 * @return the lockInfo route
	 */
	public static String lockInfo(Model model) {
		return rootUri(model) + WS_REST_CF_INTENDEDPURPOSE_LOCK_INFO;
	}

	/**
	 * Gets the relative uri for subscribe method.
	 *
	 * @param model the model
	 * @return the subscribe route
	 */
	public static String subscribe(Model model) {
		return rootUri(model) + WS_REST_CF_INTENDEDPURPOSE_SUBSCRIBE;
	}

	/**
	 * Gets the relative uri for unsubscribe method.
	 *
	 * @param model the model
	 * @return the unsubscribe route
	 */
	public static String unsubscribe(Model model) {
		return rootUri(model) + WS_REST_CF_INTENDEDPURPOSE_UNSUBSCRIBE;
	}
}
