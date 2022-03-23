/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.constants.configuration;

/**
 * This class contains the Global data constants.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlGlobalData {

	/**
	 * Global attributes
	 */
	/** Yml Global Data GLOBAL key */
	public static final String CONF_GLOBAL = "Global"; //$NON-NLS-1$
	/** The Constant CONF_SCHEMA. */
	public static final String CONF_SCHEMA = "Schema"; //$NON-NLS-1$
	/** Yml Global Data DATA key */
	public static final String CONF_DATA = "Data"; //$NON-NLS-1$
	/**
	 * Root attributes
	 */
	/** Yml Global Data MODEL key */
	public static final String CONF_GLB_MODEL = "Model"; //$NON-NLS-1$
	/** Yml Global Data USER key */
	public static final String CONF_GLB_USER = "Users"; //$NON-NLS-1$

	/** The Constant CONF_GLB_ID. */
	public static final String CONF_GLB_ID = "id"; //$NON-NLS-1$

	private YmlGlobalData() {
		// Do not instantiate
	}
}
