/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import gov.sandia.cf.tools.RscTools;

/**
 * Enum of generic parameter types.
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public enum FormFieldType {

	CREDIBILITY_ELEMENT("Credibility_Element"), //$NON-NLS-1$
	DATE("Date"), //$NON-NLS-1$
	FLOAT("Float"), //$NON-NLS-1$
	LINK("Link"), //$NON-NLS-1$
	LINK_FILE("Link_file"), //$NON-NLS-1$
	LINK_URL("Link_url"), //$NON-NLS-1$
	RICH_TEXT("RichText"), //$NON-NLS-1$
	SELECT("Select"), //$NON-NLS-1$
	SYSTEM_REQUIREMENT("System_Requirement"), //$NON-NLS-1$
	TEXT("Text"); //$NON-NLS-1$

	/**
	 * the adequacy column type
	 */
	private String type;

	private FormFieldType(String type) {
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

		for (FormFieldType credibility : FormFieldType.values()) {
			types.add(credibility.getType());
		}

		return types.toArray(typesArray);
	}

	/**
	 * @param type the type to display
	 * @return the form field type associated to type if found
	 */
	public static Optional<FormFieldType> getType(final String type) {
		final String typeToTest = type == null ? RscTools.empty() : type.replace(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return Stream.of(FormFieldType.values()).filter(t -> t.getType().equals(typeToTest)).findFirst();
	}

}
