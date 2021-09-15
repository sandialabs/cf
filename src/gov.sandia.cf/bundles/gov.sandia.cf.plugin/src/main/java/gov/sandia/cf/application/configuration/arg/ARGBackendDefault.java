/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.arg;

import java.util.Arrays;
import java.util.List;

/**
 * The ARG Backend types.
 * 
 * @author Didier Verstraete
 *
 */
public enum ARGBackendDefault {

	/**
	 * WORD Backend
	 */
	WORD("Word"), //$NON-NLS-1$
	/**
	 * LATEX Backend
	 */
	LATEX("LaTeX"); //$NON-NLS-1$

	private String backend;

	/**
	 * @param backend
	 */
	private ARGBackendDefault(String backend) {
		this.backend = backend;
	}

	/**
	 * @return the backend string
	 */
	public String getBackend() {
		return this.backend;
	}

	/**
	 * @param backend the backend name
	 * @return the backend associated
	 */
	public static ARGBackendDefault getBackendEnum(String backend) {
		return Arrays.asList(values()).stream().filter(v -> v.getBackend().equals(backend)).findFirst().orElse(null);
	}

	/**
	 * @return the backend list
	 */
	public static List<ARGBackendDefault> getValues() {
		return Arrays.asList(values());
	}

}
