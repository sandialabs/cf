/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.constants.configuration;

/**
 * This class contains the PIRT schema constants.
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public class YmlPIRTSchema {
	/**
	 * PIRT attribute
	 */
	public static final String CONF_PIRT = "PIRT"; //$NON-NLS-1$
	/**
	 * Root attributes
	 */
	public static final String CONF_PIRT_HEADER = "Header"; //$NON-NLS-1$
	public static final String CONF_PIRT_ADEQUACY = "Adequacy"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL = "Levels"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_COLOR = "Level Difference Coloring"; //$NON-NLS-1$
	public static final String CONF_PIRT_GUIDELINES = "Ranking Guidelines"; //$NON-NLS-1$
	/**
	 * Subroot attribute
	 */
	public static final String CONF_PIRT_FIELDS = "Fields"; //$NON-NLS-1$
	/**
	 * Adequacy types
	 */
	public static final String CONF_PIRT_ADEQUACY_TYPE = "Type"; //$NON-NLS-1$
	public static final String CONF_PIRT_ADEQUACY_DEFAULT_TYPE_TEXT = "Text"; //$NON-NLS-1$
	/**
	 * Level attributes
	 */
	public static final String CONF_PIRT_LEVEL_NUMERICALVALUE = "NumericalValue"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_LABEL = "Label"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_LABEL_UNKNOWN = "???"; //$NON-NLS-1$
	/**
	 * Level Difference Color attributes
	 */
	public static final String CONF_PIRT_LEVEL_COLOR_POS_OR_ZERO = "Positive or zero"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_COLOR_POS_OR_ZERO_RANGE = "0;1000"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_COLOR_ONE_LEVEL = "One level"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_COLOR_ONE_LEVEL_RANGE = "-1"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_COLOR_TWO_LEVELS_OR_MORE = "Two or more levels"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_COLOR_TWO_LEVELS_OR_MORE_RANGE = "-1000;-2"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_COLOR_NOT_ADDRESSED = "Not Addressed"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_COLOR_NA = "N/A"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_COLOR_RGB = "RGB"; //$NON-NLS-1$
	public static final String CONF_PIRT_LEVEL_COLOR_EXPLANATION = "Description"; //$NON-NLS-1$
	/**
	 * Guidelines attributes
	 */
	public static final String CONF_PIRT_GUIDELINES_GUIDELINESTAG = "Guidelines"; //$NON-NLS-1$
	public static final String CONF_PIRT_GUIDELINES_GUIDELINES_DESCTAG = "Description"; //$NON-NLS-1$
	public static final String CONF_PIRT_GUIDELINES_GUIDELINES_LEVELTAG = "Levels"; //$NON-NLS-1$

	private YmlPIRTSchema() {
		// Do not instantiate
	}
}
