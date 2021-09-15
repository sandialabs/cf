/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

/**
 * This class contains the Global data constants.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlGlobalData {

	/**
	 * Configuration constants
	 */
	public static final String MAP_KEY_ID = "{0}-{1}"; //$NON-NLS-1$

	/**
	 * Global attributes
	 */
	/** Yml Global Data GLOBAL key */
	public static final String CONF_GLOBAL = "Global"; //$NON-NLS-1$
	/** Yml Global Data DATA key */
	public static final String CONF_DATA = "Data"; //$NON-NLS-1$
	/**
	 * Root attributes
	 */
	/** Yml Global Data MODEL key */
	public static final String CONF_GLB_MODEL = "Model"; //$NON-NLS-1$
	/** Yml Global Data USER key */
	public static final String CONF_GLB_USER = "User"; //$NON-NLS-1$

	/**
	 * Model attributes
	 */
	/** Yml Global Data MODEL ID key */
	public static final String CONF_GLB_MODEL_ID = "Id"; //$NON-NLS-1$
	/** Yml Global Data MODEL APPLICATION key */
	public static final String CONF_GLB_MODEL_APPLICATION = "Application"; //$NON-NLS-1$
	/** Yml Global Data MODEL CONTACT key */
	public static final String CONF_GLB_MODEL_CONTACT = "Contact"; //$NON-NLS-1$
	/** Yml Global Data MODEL VERSION key */
	public static final String CONF_GLB_MODEL_VERSION = "Version"; //$NON-NLS-1$
	/** Yml Global Data MODEL VERSION ORIGIN key */
	public static final String CONF_GLB_MODEL_VERSIONORIGIN = "VersionOrigin"; //$NON-NLS-1$
	/** Yml Global Data MODEL QOI PLANNING SCHEMA PATH key */
	public static final String CONF_GLB_MODEL_QOIPLANNINGSCHEMAPATH = "QoIPlanningschemapath"; //$NON-NLS-1$
	/** Yml Global Data MODEL PIRT SCHEMA PATH key */
	public static final String CONF_GLB_MODEL_PIRTSCHEMAPATH = "PIRTschemapath"; //$NON-NLS-1$
	/** Yml Global Data MODEL PCMM SCHEMA PATH key */
	public static final String CONF_GLB_MODEL_PCMMSCHEMAPATH = "PCMMschemapath"; //$NON-NLS-1$
	/** Yml Global Data MODEL UNCERTAINTY SCHEMA PATH key */
	public static final String CONF_GLB_MODEL_UNCERTAINTYSCHEMAPATH = "Uncertaintyschemapath"; //$NON-NLS-1$
	/** Yml Global Data MODEL SYSTEM REQUIREMENT SCHEMA PATH key */
	public static final String CONF_GLB_MODEL_SYSREQUIREMENTSCHEMAPATH = "SystemRequirementschemapath"; //$NON-NLS-1$
	/** Yml Global Data MODEL DECISION SCHEMA PATH key */
	public static final String CONF_GLB_MODEL_DECISIONSCHEMAPATH = "Decisionschemapath"; //$NON-NLS-1$

	/**
	 * User attributes
	 */
	/** Yml Global Data USER ID key */
	public static final String CONF_GLB_USER_ID = "Id"; //$NON-NLS-1$
	/** Yml Global Data USER USERID key */
	public static final String CONF_GLB_USER_USERID = "UserId"; //$NON-NLS-1$
	/** Yml Global Data USER PCMM ROLE key */
	public static final String CONF_GLB_USER_ROLEPCMM = "RolePCMM"; //$NON-NLS-1$

	private YmlGlobalData() {
		// Do not instantiate
	}
}
