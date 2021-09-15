/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The configuration file validator
 * 
 * @author Didier Verstraete
 *
 */
public class ConfigurationFileValidator {

	/**
	 * Constants
	 */
	private static final boolean PIRT_REQUIRED = false;
	private static final boolean QOIPLANNING_REQUIRED = false;
	private static final boolean PCMM_REQUIRED = false;
	private static final boolean UNCERTAINTY_REQUIRED = false;
	private static final boolean REQUIREMENT_REQUIRED = false;

	private ConfigurationFileValidator() {
		// do not instantiate
	}

	/**
	 * Checks the configuration schema files
	 * 
	 * @param confSchema the current CF project configuration schema
	 * @param fileType   the file type (Planning, PIRT, PCMM, Communicate)
	 * @return the set of error messages
	 */
	public static Set<String> checkSchemaFile(ConfigurationSchema confSchema, ConfigurationFileType fileType) {

		// Initialize variables
		boolean required = false;
		String fileTypeMsg = RscTools.empty();
		String pathToValidate = null;
		Set<String> otherPath = new HashSet<>();

		// Manage
		if (ConfigurationFileType.PIRT.equals(fileType)) {
			required = PIRT_REQUIRED;
			fileTypeMsg = RscTools.getString(RscConst.MSG_PIRT);
			pathToValidate = confSchema.get(ConfigurationFileType.PIRT);
			otherPath = confSchema.valuesExcept(Arrays.asList(ConfigurationFileType.PIRT));
		} else if (ConfigurationFileType.QOIPLANNING.equals(fileType)) {
			required = QOIPLANNING_REQUIRED;
			fileTypeMsg = RscTools.getString(RscConst.MSG_QOIPLANNING);
			pathToValidate = confSchema.get(ConfigurationFileType.QOIPLANNING);
			otherPath = confSchema.valuesExcept(Arrays.asList(ConfigurationFileType.QOIPLANNING));
		} else if (ConfigurationFileType.PCMM.equals(fileType)) {
			required = PCMM_REQUIRED;
			fileTypeMsg = RscTools.getString(RscConst.MSG_PCMM);
			pathToValidate = confSchema.get(ConfigurationFileType.PCMM);
			otherPath = confSchema.valuesExcept(Arrays.asList(ConfigurationFileType.PCMM));
		} else if (ConfigurationFileType.UNCERTAINTY.equals(fileType)) {
			required = UNCERTAINTY_REQUIRED;
			fileTypeMsg = RscTools.getString(RscConst.MSG_UNCERTAINTY);
			pathToValidate = confSchema.get(ConfigurationFileType.UNCERTAINTY);
			otherPath = confSchema.valuesExcept(Arrays.asList(ConfigurationFileType.UNCERTAINTY));
		} else if (ConfigurationFileType.SYSTEM_REQUIREMENT.equals(fileType)) {
			required = REQUIREMENT_REQUIRED;
			fileTypeMsg = RscTools.getString(RscConst.MSG_SYSREQUIREMENT);
			pathToValidate = confSchema.get(ConfigurationFileType.SYSTEM_REQUIREMENT);
			otherPath = confSchema.valuesExcept(Arrays.asList(ConfigurationFileType.SYSTEM_REQUIREMENT));
		}

		// Check file path and return errors
		return checkFilePath(pathToValidate, fileTypeMsg, required, otherPath);
	}

	/**
	 * Check file path
	 * 
	 * @param pathToValidate the path to validate
	 * @param fileTypeMsg    the file type name
	 * @param required       is required?
	 * @param otherPath      the other file paths
	 * @return a set of error message if present, otherwise an empty set
	 */
	public static Set<String> checkFilePath(String pathToValidate, String fileTypeMsg, boolean required,
			Set<String> otherPath) {
		// Initialize
		Set<String> errMsg = new HashSet<>();

		// Check if path is not empty and not required
		if (pathToValidate == null || pathToValidate.isEmpty()) {
			// Error only if it's required
			if (required) {
				errMsg.add(RscTools.getString(RscConst.ERR_CONFFILEWIZARD_EMPTYFILE, fileTypeMsg));
			}
		} else {

			// check if file exists and is a valid YML file
			File file = new File(pathToValidate);
			if (file.exists() && file.isFile() && YmlReaderGlobal.isValidYmlFile(file)) {

				// check if file is not the same as another one
				if (otherPath.contains(pathToValidate)) {
					errMsg.add(RscTools.getString(RscConst.ERR_CONFFILEWIZARD_SAME_FILES));
				}
			} else {
				// otherwise add error messages
				errMsg.add(RscTools.getString(RscConst.ERR_CONFFILEWIZARD_BAD_FILE, fileTypeMsg));
			}
		}

		return errMsg;
	}
}
