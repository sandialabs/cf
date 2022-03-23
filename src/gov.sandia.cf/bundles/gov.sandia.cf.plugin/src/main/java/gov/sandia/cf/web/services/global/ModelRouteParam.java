/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.global;

/**
 * The Class Intended Purpose Route Param.
 * 
 * @author Didier Verstraete
 */
public class ModelRouteParam {

	private static final String MODEL_MODEL_VAR = "model"; //$NON-NLS-1$

	private ModelRouteParam() {
		// Do not implement
	}

	/**
	 * Model.
	 *
	 * @return the string
	 */
	public static String model() {
		return MODEL_MODEL_VAR;
	}

}
