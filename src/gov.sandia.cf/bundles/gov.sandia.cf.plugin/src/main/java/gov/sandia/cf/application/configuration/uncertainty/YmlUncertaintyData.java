/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.uncertainty;

/**
 * This class contains the Uncertainty data constants.
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public class YmlUncertaintyData {
	/**
	 * Root attributes
	 */
	public static final String CONF_UNCERTAINTY = "UncertaintyInventory"; //$NON-NLS-1$
	public static final String CONF_DATA = "Data"; //$NON-NLS-1$

	/**
	 * Uncertainty attributes
	 */
	public static final String CONF_UNCERTAINTYGROUP = "UncertaintyGroups"; //$NON-NLS-1$

	/**
	 * Uncertainty groups attributes
	 */
	public static final String CONF_UNCERTAINTYGROUP_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTYGROUP_NAME = "Name"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTYGROUP_UNCERTAINTIES = "Uncertainties"; //$NON-NLS-1$

	/**
	 * Uncertainty items attributes
	 */
	public static final String CONF_UNCERTAINTY_UNCERTAINTY = "Uncertainty"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_ITEM_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_ITEM_USER = "User"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_ITEM_GROUP = "Group"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_ITEM_VALUES = "Values"; //$NON-NLS-1$

	/**
	 * Uncertainty values attributes
	 */
	public static final String CONF_UNCERTAINTY_VALUE_UNCERTAINTYVALUE = "UncertaintyValue"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_VALUE_ID = "Id"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_VALUE_VALUE = "Value"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_VALUE_ITEM = "Item"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_VALUE_PARAM = "Parameter"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_VALUE_CREATIONDATE = "CreationDate"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_VALUE_CREATIONUSER = "CreationUser"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_VALUE_UPDATEDATE = "UpdateDate"; //$NON-NLS-1$
	public static final String CONF_UNCERTAINTY_VALUE_UPDATEUSER = "UpdateUser"; //$NON-NLS-1$

	private YmlUncertaintyData() {
		// Do not instantiate
	}
}
