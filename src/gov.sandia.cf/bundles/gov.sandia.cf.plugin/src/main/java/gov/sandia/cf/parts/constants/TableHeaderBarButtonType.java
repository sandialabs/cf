/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum of table header button type
 * 
 * @author Maxime N.
 *
 */
@SuppressWarnings("javadoc")
public enum TableHeaderBarButtonType {

	EDIT("edit"), VIEW("view"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * the adequacy column type
	 */
	private String type;

	private TableHeaderBarButtonType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return type;
	}

	/**
	 * @return the types as array
	 */
	public static String[] getTypes() {
		String[] typesArray = new String[] {};
		List<String> types = new ArrayList<>();

		for (TableHeaderBarButtonType credibility : TableHeaderBarButtonType.values()) {
			types.add(credibility.getType());
		}

		return types.toArray(typesArray);
	}

}
