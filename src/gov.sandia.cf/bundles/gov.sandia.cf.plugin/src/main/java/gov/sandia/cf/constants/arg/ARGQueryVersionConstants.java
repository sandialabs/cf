/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.constants.arg;

import gov.sandia.cf.tools.FileTools;

/**
 * The ARG query version constants class
 * 
 * @author Didier Verstraete
 *
 */
public class ARGQueryVersionConstants {
	/**
	 * The ARG query version python script
	 */
	public static final String SCRIPT_PY_QUERY_ARG_VERSION = FileTools.FILES_ARG + FileTools.PATH_SEPARATOR
			+ "query_ARG_version.py"; //$NON-NLS-1$
	/**
	 * The ARG FILE command parameter
	 */
	public static final String ARGFILE_PARAM = "-a"; //$NON-NLS-1$
	/**
	 * The ARG VERSION returned keyword
	 */
	public static final String VERSION_KEYWORD = "ARG_VERSION="; //$NON-NLS-1$

	private ARGQueryVersionConstants() {
		// DO NOT INSTANTIATE
	}
}
