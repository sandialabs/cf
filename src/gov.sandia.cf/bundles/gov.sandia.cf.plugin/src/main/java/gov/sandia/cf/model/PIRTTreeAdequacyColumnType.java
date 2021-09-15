/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum of pirt adequacy column types. Configuration file must have this
 * constants as adequacy columns types to be linked with UI.
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public enum PIRTTreeAdequacyColumnType {

	RICH_TEXT("RichText"), //$NON-NLS-1$
	TEXT("Text"), //$NON-NLS-1$
	LEVELS("Levels"); //$NON-NLS-1$

	/**
	 * the adequacy column type
	 */
	private String type;

	/**
	 * @param type
	 */
	private PIRTTreeAdequacyColumnType(String type) {
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

		for (PIRTTreeAdequacyColumnType credibility : PIRTTreeAdequacyColumnType.values()) {
			types.add(credibility.getType());
		}

		return types.toArray(typesArray);
	}

}
