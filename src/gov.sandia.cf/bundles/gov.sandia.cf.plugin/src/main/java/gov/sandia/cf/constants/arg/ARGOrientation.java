/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.constants.arg;

import java.util.Arrays;
import java.util.List;

/**
 * The ARG orientation types.
 * 
 * @author Didier Verstraete
 *
 */
public enum ARGOrientation {

	/** The portrait. */
	PORTRAIT("portrait"), //$NON-NLS-1$
	/** The landscape. */
	LANDSCAPE("landscape"); //$NON-NLS-1$

	private String orientation;

	/**
	 * @param backend
	 */
	private ARGOrientation(String backend) {
		this.orientation = backend;
	}

	/**
	 * Gets the orientation.
	 *
	 * @return the orientation
	 */
	public String getOrientation() {
		return this.orientation;
	}

	/**
	 * Gets the orientation enum.
	 *
	 * @param backend the backend
	 * @return the orientation enum
	 */
	public static ARGOrientation getOrientationEnum(String backend) {
		return Arrays.asList(values()).stream().filter(v -> v.getOrientation().equals(backend)).findFirst()
				.orElse(null);
	}

	/**
	 * @return the backend list
	 */
	public static List<ARGOrientation> getValues() {
		return Arrays.asList(values());
	}

}
