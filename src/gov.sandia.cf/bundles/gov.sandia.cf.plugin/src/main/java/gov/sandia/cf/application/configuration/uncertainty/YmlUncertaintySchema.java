/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.uncertainty;

/**
 * This class contains the Uncertainty schema constants.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlUncertaintySchema {

	/**
	 * Uncertainty attribute
	 */
	/* This variable is needed for backward compatibility */
	public static final String CONF_COM = "COM"; //$NON-NLS-1$

	/**
	 * The default SCHEMA key
	 */
	public static final String CONF_SCHEMA = "Schema"; //$NON-NLS-1$

	/**
	 * Root attributes
	 */
	public static final String CONF_UNCERTAINTY_PARAMETER = "Uncertainty Parameters"; //$NON-NLS-1$

	private YmlUncertaintySchema() {
		// Do not instantiate
	}
}
