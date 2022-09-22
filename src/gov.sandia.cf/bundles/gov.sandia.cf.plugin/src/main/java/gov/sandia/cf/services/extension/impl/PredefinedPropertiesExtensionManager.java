/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.services.extension.impl;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.services.extensionpoint.IPredefinedProperties;

/**
 * The Class PredefinedPropertiesExtensionManager.
 *
 * @author Didier Verstraete
 */
public class PredefinedPropertiesExtensionManager {

	private static final Logger logger = LoggerFactory.getLogger(PredefinedPropertiesExtensionManager.class);

	private PredefinedPropertiesExtensionManager() {
	}

	/** The extension point id. */
	private static String extensionPointId = CredibilityFrameworkConstants.CF_EXTENSIONPOINT_PREDEFINEDPROPERTIES_ID;

	/** The extension point class attribute. */
	private static String extensionPointClass = CredibilityFrameworkConstants.CF_EXTENSIONPOINT_PREDEFINEDPROPERTIES_CLASS;

	/** The extension interface class. */
	private static Class<?> extensionInterfaceClass = IPredefinedProperties.class;

	/**
	 * Gets the arg executable file.
	 *
	 * @return the arg executable file
	 */
	public static File getArgExecutableFile() {
		return getPropertyFile(IPredefinedProperties.ARG_EXECUTABLE_PATH);
	}

	/**
	 * Gets the arg set environment file.
	 *
	 * @return the arg set environment file
	 */
	public static File getArgSetEnvironmentFile() {
		return getPropertyFile(IPredefinedProperties.ARG_SETENV_SCRIPT_PATH);
	}

	/**
	 * Gets the property file.
	 *
	 * @param property the property
	 * @return the property file
	 */
	private static File getPropertyFile(String property) {
		Map<String, Object> properties = getProperties();
		if (properties != null && properties.containsKey(property)) {
			Object argExecPath = properties.get(property);
			if (argExecPath instanceof String) {
				return new File((String) argExecPath);
			}
		}
		logger.warn("Impossible to get the file for property {}", property); //$NON-NLS-1$
		return null;

	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public static Map<String, Object> getProperties() {
		return ExtensionTool.execute(extensionInterfaceClass, extensionPointId, extensionPointClass, "getProperties"); //$NON-NLS-1$
	}
}
