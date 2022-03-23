/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import gov.sandia.cf.tools.RscTools;

/**
 * The Enum CFBackendConnectionType.
 * 
 * @author Didier Verstraete
 */
public enum CFBackendConnectionType {

	/** FILE: for local file database connection. */
	FILE("file"), //$NON-NLS-1$

	/** WEB: for web api connection. */
	WEB("web"); //$NON-NLS-1$

	private String type;

	private CFBackendConnectionType(String type) {
		this.type = type;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return type;
	}

	/**
	 * Gets the types.
	 *
	 * @return the types
	 */
	public static String[] getTypes() {
		String[] typesArray = new String[] {};
		List<String> types = new ArrayList<>();

		for (CFBackendConnectionType credibility : CFBackendConnectionType.values()) {
			types.add(credibility.getType());
		}

		return types.toArray(typesArray);
	}

	/**
	 * Gets the type.
	 *
	 * @param type the type
	 * @return the type
	 */
	public static Optional<CFBackendConnectionType> getType(final String type) {
		final String typeToTest = (type == null) ? RscTools.empty() : type.replace(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return Stream.of(CFBackendConnectionType.values()).filter(t -> t.getType().equals(typeToTest)).findFirst();
	}

}
