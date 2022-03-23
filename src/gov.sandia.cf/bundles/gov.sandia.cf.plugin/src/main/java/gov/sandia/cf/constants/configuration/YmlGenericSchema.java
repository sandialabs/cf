/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.constants.configuration;

/**
 * This class contains the Generic Parameters schema constants.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlGenericSchema {

	/**
	 * Root attributes
	 */
	/** Yml Generic Schema ID key */
	public static final String CONF_GENERIC_ID = "Id"; //$NON-NLS-1$
	/** Yml Generic Schema LEVEL key */
	public static final String CONF_GENERIC_LEVEL = "level"; //$NON-NLS-1$
	/** Yml Generic Schema TYPE key */
	public static final String CONF_GENERIC_TYPE = "type"; //$NON-NLS-1$
	/** Yml Generic Schema REQUIRED key */
	public static final String CONF_GENERIC_REQUIRED = "required"; //$NON-NLS-1$
	/** Yml Generic Schema VALUES key */
	public static final String CONF_GENERIC_VALUES = "values"; //$NON-NLS-1$
	/** Yml Generic Schema CONSTRAINTS key */
	public static final String CONF_GENERIC_CONSTRAINTS = "constraints"; //$NON-NLS-1$
	/** Yml Generic Schema DEFAULT VALUE key */
	public static final String CONF_GENERIC_CONSTRAINTS_DEFAULT = "default"; //$NON-NLS-1$

	/**
	 * Values
	 */
	/** Yml Generic Schema TRUE value */
	public static final String CONF_GENERIC_TRUE_VALUE = "true"; //$NON-NLS-1$
	/** Yml Generic Schema FALSE value */
	public static final String CONF_GENERIC_FALSE_VALUE = "false"; //$NON-NLS-1$
	/** Yml Generic Schema REQUIRED value */
	public static final String CONF_GENERIC_REQUIRED_VALUE = "Required"; //$NON-NLS-1$
	/** Yml Generic Schema OPTIONAL value */
	public static final String CONF_GENERIC_OPTIONAL_VALUE = "Optional"; //$NON-NLS-1$
	/** Yml Generic Schema DESIRED value */
	public static final String CONF_GENERIC_DESIRED_VALUE = "Desired"; //$NON-NLS-1$

	/**
	 * Level comparison
	 */
	/** Yml Generic Schema DEFAULT LEVEL value */
	public static final String DEFAULT_LEVEL = "*"; //$NON-NLS-1$
	/** Yml Generic Schema LEVEL COMPARATOR REGEX */
	public static final String LEVEL_COMPARATOR_STRING = "(?<=\\D)(?=\\d)"; //$NON-NLS-1$
	/** Yml Generic Schema LEVEL COMPARATOR ALL value */
	public static final String LEVEL_COMPARATOR_ALL = "*"; //$NON-NLS-1$
	/** Yml Generic Schema LEVEL COMPARATOR OVER value */
	public static final String LEVEL_COMPARATOR_OVER = ">"; //$NON-NLS-1$
	/** Yml Generic Schema LEVEL COMPARATOR UNDER value */
	public static final String LEVEL_COMPARATOR_UNDER = "<"; //$NON-NLS-1$
	/** Yml Generic Schema LEVEL COMPARATOR OVER OR EQUALS value */
	public static final String LEVEL_COMPARATOR_OVER_EQUAL = ">="; //$NON-NLS-1$
	/** Yml Generic Schema LEVEL COMPARATOR UNDER OR EQUALS value */
	public static final String LEVEL_COMPARATOR_UNDER_EQUAL = "<="; //$NON-NLS-1$
	/** Yml Generic Schema LEVEL COMPARATOR EQUALS value */
	public static final String LEVEL_COMPARATOR_EQUAL = "="; //$NON-NLS-1$

	private YmlGenericSchema() {
		// Do not instantiate
	}
}
