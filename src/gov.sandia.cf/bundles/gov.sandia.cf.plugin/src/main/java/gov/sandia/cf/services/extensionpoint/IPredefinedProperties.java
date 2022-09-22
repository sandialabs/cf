/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.services.extensionpoint;

import java.util.Map;

/**
 * The Interface IPredefinedProperties.
 *
 * @author Didier Verstraete
 */
public interface IPredefinedProperties {

	/**
	 * The Constant ARG_EXECUTABLE_PATH.
	 * 
	 * The ARG executable path is used by CF to determine the ARG context.
	 * 
	 * It is the 'ARG.py' file path.
	 * 
	 * For a normal installation with {@code python -m pip install pyARG}, the ARG
	 * executable path will be in the
	 * '${python_install_path}/Libs/site-packages/arg/Applications/ARG.py'
	 * 
	 */
	public static final String ARG_EXECUTABLE_PATH = "ARG_EXECUTABLE_PATH"; //$NON-NLS-1$

	/** 
	 * The Constant ARG_SETENV_SCRIPT_PATH.
	 * 
	 * Typically a '.sh' or '.bat' file.
	 * 
	 * It is used to override default platform commands and execute environment commands.
	 * 
	 * This script is executed with the python command and its scope is only the executed command.
	 * 
	 * This property is not mandatory.
	 * 
	 */
	public static final String ARG_SETENV_SCRIPT_PATH = "ARG_SETENV_SCRIPT_PATH"; //$NON-NLS-1$

	/**
	 * Gets the properties map.
	 *
	 * @return the properties
	 */
	public Map<String, Object> getProperties();

}
