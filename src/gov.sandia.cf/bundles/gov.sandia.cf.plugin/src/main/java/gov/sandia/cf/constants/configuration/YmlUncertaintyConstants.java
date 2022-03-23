/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.constants.configuration;

/**
 * This class contains the Uncertainty schema constants.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlUncertaintyConstants {

	/**
	 * Uncertainty attribute
	 * 
	 * @deprecated This variable is needed for backward compatibility
	 */
	@Deprecated
	public static final String CONF_COM = "COM"; //$NON-NLS-1$

	/**
	 * Root attributes
	 */
	public static final String CONF_UNCERTAINTY_PARAMETER = "Uncertainty Parameters"; //$NON-NLS-1$
	/**
	 * Uncertainty attributes
	 */
	public static final String CONF_UNCERTAINTYGROUPS = "uncertaintyGroups"; //$NON-NLS-1$
	

	private YmlUncertaintyConstants() {
		// Do not instantiate
	}
}
