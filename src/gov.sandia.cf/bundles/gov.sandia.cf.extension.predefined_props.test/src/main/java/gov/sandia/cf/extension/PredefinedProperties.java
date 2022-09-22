/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.extension;

import java.util.Map;

import gov.sandia.cf.services.extensionpoint.IPredefinedProperties;

/**
 * Predefined properties class.
 * 
 * @author Didier Verstraete
 *
 */
public class PredefinedProperties implements IPredefinedProperties {

	@Override
	public Map<String, Object> getProperties() {
		return Map.of(IPredefinedProperties.ARG_EXECUTABLE_PATH,
				"C:\\dev\\git\\nga\\arg_arg\\arg\\Applications\\ARG.py", IPredefinedProperties.ARG_SETENV_SCRIPT_PATH,
				"C:\\dev\\_nga\\cf\\arg_report\\setEnv.bat");
	}
}