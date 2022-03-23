/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.constants.arg;

import gov.sandia.cf.tools.FileTools;

/**
 * The ARG query types constants class
 * 
 * @author Didier Verstraete
 *
 */
public class ARGQueryTypesConstants {
	/**
	 * The ARG query types python script
	 */
	public static final String SCRIPT_PY_QUERY_ARG_TYPES = FileTools.FILES_ARG + FileTools.PATH_SEPARATOR
			+ "query_ARG_types.py"; //$NON-NLS-1$
	/**
	 * The ARG FILE command parameter
	 */
	public static final String ARG_TYPES_SCRIPT_ARGFILE_PARAM = "-a"; //$NON-NLS-1$

	/**
	 * The ARG TYPES returned keyword
	 */
	public static final String ARG_TYPES_DICO_KEYWORD = "ARG_TYPES="; //$NON-NLS-1$
	/**
	 * The ARG TYPES Backend types returned keyword
	 */
	public static final String ARG_BACKENDTYPE_KEYWORD = "BackendTypes"; //$NON-NLS-1$
	/**
	 * The ARG TYPES Report types returned keyword
	 */
	public static final String ARG_REPORTTYPE_KEYWORD = "ReportTypes"; //$NON-NLS-1$

	private ARGQueryTypesConstants() {
		// DO NOT INSTANTIATE
	}
}
