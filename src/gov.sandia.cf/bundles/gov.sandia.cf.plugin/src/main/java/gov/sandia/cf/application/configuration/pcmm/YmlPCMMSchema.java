/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.pcmm;

/**
 * This class contains the PCMM schema constants.
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public class YmlPCMMSchema {
	/**
	 * Global constants
	 */
	public static final String CONF_PCMM = "PCMM"; //$NON-NLS-1$
	public static final String CONF_SCHEMA = "Schema"; //$NON-NLS-1$

	public static final String CONF_PCMM_PHASES = "Phases"; //$NON-NLS-1$
	public static final String CONF_PCMM_ROLES = "Roles"; //$NON-NLS-1$
	public static final String CONF_PCMM_LEVEL_COLORS = "Levels"; //$NON-NLS-1$
	public static final String CONF_PCMM_PLANNING = "Planning"; //$NON-NLS-1$
	/**
	 * PCMM elements constants
	 */
	public static final String CONF_PCMM_ELEMENTS = "Elements"; //$NON-NLS-1$
	public static final String CONF_PCMM_ELEMENT_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PCMM_ELEMENT_NAME = "Name"; //$NON-NLS-1$
	public static final String CONF_PCMM_ELEMENT_COLOR = "Color"; //$NON-NLS-1$
	public static final String CONF_PCMM_ELEMENT_ABBREV = "Abbreviation"; //$NON-NLS-1$
	/**
	 * PCMM Subelements constants
	 */
	public static final String CONF_PCMM_SUBELEMENTS = "Subelements"; //$NON-NLS-1$
	public static final String CONF_PCMM_SUBELEMENT_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PCMM_SUBELEMENT_NAME = "Name"; //$NON-NLS-1$
	public static final String CONF_PCMM_SUBELEMENT_CODE = "Code"; //$NON-NLS-1$
	/**
	 * PCMM Levels constants
	 */
	public static final String CONF_PCMM_LEVELS = "Levels"; //$NON-NLS-1$
	public static final String CONF_PCMM_LEVEL_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PCMM_LEVEL_CODE = "Code"; //$NON-NLS-1$
	public static final String CONF_PCMM_LEVEL_NAME = "Name"; //$NON-NLS-1$
	public static final String CONF_PCMM_LEVEL_COLOR = "Color"; //$NON-NLS-1$
	/**
	 * PCMM Levels Descriptors constants
	 */
	public static final String CONF_PCMM_LEVEL_DESCRIPTORS = "Descriptors"; //$NON-NLS-1$
	/**
	 * PCMM Planning constants
	 */
	public static final String CONF_PCMM_PLANNING_FIELDS = "Planning Fields"; //$NON-NLS-1$
	public static final String CONF_PCMM_PLANNING_QUESTIONS = "Planning Questions"; //$NON-NLS-1$
	public static final String CONF_PCMM_PLANNING_TYPES = "Planning Types"; //$NON-NLS-1$

	private YmlPCMMSchema() {
		// Do not instantiate
	}
}
