/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.constants.configuration;

/**
 * This class contains the PIRT data constants.
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public class YmlPIRTData {
	/**
	 * PIRT attribute
	 */
	public static final String CONF_PIRT = "PIRT"; //$NON-NLS-1$
	public static final String CONF_DATA = "Data"; //$NON-NLS-1$
	/**
	 * Root attributes
	 */
	public static final String CONF_PIRT_QOI = "QuantityOfInterest"; //$NON-NLS-1$
	public static final String CONF_PIRT_PIRTTABLE = "PIRTTable"; //$NON-NLS-1$

	/**
	 * QoI attributes
	 */
	public static final String CONF_PIRT_QOI_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PIRT_QOI_CREATIONDATE = "CreationDate"; //$NON-NLS-1$
	public static final String CONF_PIRT_QOI_NAME = "Name"; //$NON-NLS-1$
	public static final String CONF_PIRT_QOI_DESCRIPTION = "Description"; //$NON-NLS-1$
	public static final String CONF_PIRT_QOI_PARENT = "Parent"; //$NON-NLS-1$
	public static final String CONF_PIRT_QOI_HEADERS = "Headers"; //$NON-NLS-1$
	public static final String CONF_PIRT_QOI_TAG = "Tag"; //$NON-NLS-1$
	public static final String CONF_PIRT_QOI_TAGDATE = "TagDate"; //$NON-NLS-1$
	public static final String CONF_PIRT_QOI_TAGDESCRIPTION = "TagDescription"; //$NON-NLS-1$
	public static final String CONF_PIRT_QOI_TAGUSER = "TagUser"; //$NON-NLS-1$

	/**
	 * Phenomenon group attributes
	 */
	public static final String CONF_PIRT_PHENGROUP_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PIRT_PHENGROUP_IDLABEL = "IdLabel"; //$NON-NLS-1$
	public static final String CONF_PIRT_PHENGROUP_NAME = "Name"; //$NON-NLS-1$
	public static final String CONF_PIRT_PHENGROUP_QOI = "QoI"; //$NON-NLS-1$
	public static final String CONF_PIRT_PHENGROUP_PHENOMENONLIST = "PhenomenonList"; //$NON-NLS-1$

	/**
	 * Phenomenon attributes
	 */
	public static final String CONF_PIRT_PHENOMENON_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PIRT_PHENOMENON_IDLABEL = "IdLabel"; //$NON-NLS-1$
	public static final String CONF_PIRT_PHENOMENON_NAME = "Name"; //$NON-NLS-1$
	public static final String CONF_PIRT_PHENOMENON_IMPORTANCE = "Importance"; //$NON-NLS-1$
	public static final String CONF_PIRT_PHENOMENON_CRITERIONLIST = "CriterionList"; //$NON-NLS-1$

	/**
	 * Criterion attributes
	 */
	public static final String CONF_PIRT_CRITERION_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_PIRT_CRITERION_TYPE = "Type"; //$NON-NLS-1$
	public static final String CONF_PIRT_CRITERION_NAME = "Name"; //$NON-NLS-1$
	public static final String CONF_PIRT_CRITERION_VALUE = "Value"; //$NON-NLS-1$

	private YmlPIRTData() {
		// Do not instantiate
	}
}
