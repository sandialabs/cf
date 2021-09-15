/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum of open link browser options.
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public enum OpenLinkBrowserOption implements ISelectValue {

	EXERTNAL_BROWSER("Use External Browser"), //$NON-NLS-1$
	INTERNAL_BROWSER("Use Internal Browser"), //$NON-NLS-1$
	ECLIPSE_PREFERENCE("Use Platform Preferences"), //$NON-NLS-1$
	CF_PREFERENCE("Use Credibility Framework Preferences"); //$NON-NLS-1$

	/**
	 * the name to display
	 */
	private String label;

	private OpenLinkBrowserOption(String type) {
		this.label = type;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return label;
	}

	/**
	 * @return the option as an array: e.g. : { {name1, value1}, {name2, value2},
	 *         ...}
	 */
	public static String[][] getAllNameValueArray() {
		String[][] nameValueArray = new String[][] {};
		List<String[]> nameValueList = new ArrayList<>();

		for (OpenLinkBrowserOption option : OpenLinkBrowserOption.values()) {
			nameValueList.add(new String[] { option.getLabel(), option.name() });
		}

		return nameValueList.toArray(nameValueArray);
	}

	/**
	 * @param values the link values to add to the array
	 * @return the option as an array: e.g. : { {name1, value1}, {name2, value2},
	 *         ...}
	 */
	public static String[][] getNameValueArray(OpenLinkBrowserOption... values) {
		String[][] nameValueArray = new String[][] {};
		List<String[]> nameValueList = new ArrayList<>();

		for (OpenLinkBrowserOption option : values) {
			nameValueList.add(new String[] { option.getLabel(), option.name() });
		}

		return nameValueList.toArray(nameValueArray);
	}

	public String getSelectName() {
		return label;
	}

	@Override
	public Integer getId() {
		return null;
	}

}
