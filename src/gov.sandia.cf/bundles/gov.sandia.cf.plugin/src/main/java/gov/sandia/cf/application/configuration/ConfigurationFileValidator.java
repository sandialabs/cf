/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import java.io.File;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import gov.sandia.cf.application.decision.YmlReaderDecisionSchema;
import gov.sandia.cf.application.imports.YmlReaderGlobal;
import gov.sandia.cf.application.pcmm.YmlReaderPCMMSchema;
import gov.sandia.cf.application.pirt.YmlReaderPIRTSchema;
import gov.sandia.cf.application.qoiplanning.YmlReaderQoIPlanningSchema;
import gov.sandia.cf.application.requirement.YmlReaderSystemRequirementSchema;
import gov.sandia.cf.application.uncertainty.YmlReaderUncertaintySchema;
import gov.sandia.cf.constants.configuration.ConfigurationFileType;
import gov.sandia.cf.model.dto.configuration.ConfigurationSchema;
import gov.sandia.cf.tools.FileExtension;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The configuration file validator
 * 
 * @author Didier Verstraete
 *
 */
public class ConfigurationFileValidator {

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

		// Manage
		if (ConfigurationFileType.PIRT.equals(fileType)) {
			required = false;
			fileTypeMsg = RscTools.getString(RscConst.MSG_PIRT);
			pathToValidate = confSchema.get(ConfigurationFileType.PIRT);
		} else if (ConfigurationFileType.QOIPLANNING.equals(fileType)) {
			required = false;
			fileTypeMsg = RscTools.getString(RscConst.MSG_QOIPLANNING);
			pathToValidate = confSchema.get(ConfigurationFileType.QOIPLANNING);
		} else if (ConfigurationFileType.PCMM.equals(fileType)) {
			required = false;
			fileTypeMsg = RscTools.getString(RscConst.MSG_PCMM);
			pathToValidate = confSchema.get(ConfigurationFileType.PCMM);
		} else if (ConfigurationFileType.UNCERTAINTY.equals(fileType)) {
			required = false;
			fileTypeMsg = RscTools.getString(RscConst.MSG_UNCERTAINTY);
			pathToValidate = confSchema.get(ConfigurationFileType.UNCERTAINTY);
		} else if (ConfigurationFileType.SYSTEM_REQUIREMENT.equals(fileType)) {
			required = false;
			fileTypeMsg = RscTools.getString(RscConst.MSG_SYSREQUIREMENT);
			pathToValidate = confSchema.get(ConfigurationFileType.SYSTEM_REQUIREMENT);
		} else if (ConfigurationFileType.DECISION.equals(fileType)) {
			required = false;
			fileTypeMsg = RscTools.getString(RscConst.MSG_DECISION);
			pathToValidate = confSchema.get(ConfigurationFileType.DECISION);
		}

		// Check file path and return errors
		return checkFilePath(fileType, pathToValidate, fileTypeMsg, required);
	}

	/**
	 * Check file path.
	 *
	 * @param fileType       the file type
	 * @param pathToValidate the path to validate
	 * @param fileTypeMsg    the file type name
	 * @param required       is required?
	 * @return a set of error message if present, otherwise an empty set
	 */
	public static Set<String> checkFilePath(ConfigurationFileType fileType, String pathToValidate, String fileTypeMsg,
			boolean required) {
		// Initialize
		Set<String> errMsg = new HashSet<>();

		// Check if path is not empty and not required
		if (pathToValidate == null || pathToValidate.isEmpty()) {
			// Error only if it's required
			if (required) {
				errMsg.add(RscTools.getString(RscConst.ERR_NEWCFPROCESS_LOCALSETUP_PAGE_EMPTYFILE, fileTypeMsg));
			}
		} else {

			// check if file exists and is a valid YML file
			File file = new File(pathToValidate);
			if (file.exists() && file.isFile() && YmlReaderGlobal.isValidYmlFile(file)) {
				// check file validity
				if (!isOfConfigurationFileType(fileType, file)) {
					errMsg.add(RscTools.getString(RscConst.ERR_NEWCFPROCESS_LOCALSETUP_PAGE_BAD_FILE, fileTypeMsg));
				}
			} else {
				// otherwise add error messages
				errMsg.add(RscTools.getString(RscConst.ERR_NEWCFPROCESS_LOCALSETUP_PAGE_BAD_FILE, fileTypeMsg));
			}
		}

		return errMsg;
	}

	/**
	 * @param path the path to scan
	 * @return the map of found compatible schema files in the path
	 */
	public static Map<ConfigurationFileType, Set<File>> parseConfigurationFolder(String path) {

		Map<ConfigurationFileType, Set<File>> mapFiles = new EnumMap<>(ConfigurationFileType.class);
		mapFiles.put(ConfigurationFileType.SYSTEM_REQUIREMENT, new HashSet<>());
		mapFiles.put(ConfigurationFileType.UNCERTAINTY, new HashSet<>());
		mapFiles.put(ConfigurationFileType.QOIPLANNING, new HashSet<>());
		mapFiles.put(ConfigurationFileType.DECISION, new HashSet<>());
		mapFiles.put(ConfigurationFileType.PIRT, new HashSet<>());
		mapFiles.put(ConfigurationFileType.PCMM, new HashSet<>());

		if (path == null) {
			return mapFiles;
		}

		File confFolderFile = new File(path);

		if (confFolderFile.isDirectory()) {
			File[] ymlFiles = confFolderFile.listFiles(pathname -> pathname != null && pathname.isFile()
					&& FileExtension.getYmlExtensions().stream().anyMatch(ext -> ext.getExtensionWithoutDot()
							.equals(FileTools.getExtensionByFileName(pathname.getPath()))));

			if (ymlFiles != null) {
				Stream.of(ymlFiles)
						.forEach(file -> getConfigurationType(file).forEach(type -> mapFiles.get(type).add(file)));
			}
		}

		return mapFiles;
	}

	/**
	 * @param ymlFile the file to check
	 * @return the list of compatible configuration types with the file
	 */
	public static Set<ConfigurationFileType> getConfigurationType(File ymlFile) {

		Set<ConfigurationFileType> types = new HashSet<>();

		for (ConfigurationFileType type : ConfigurationFileType.values()) {
			if (isOfConfigurationFileType(type, ymlFile)) {
				types.add(type);
			}
		}

		return types;
	}

	/**
	 * @param type    the type to check
	 * @param ymlFile the file to check
	 * @return true if the configuration file is of type
	 */
	public static boolean isOfConfigurationFileType(ConfigurationFileType type, File ymlFile) {
		if (ConfigurationFileType.PIRT.equals(type)) {
			return new YmlReaderPIRTSchema().isValid(ymlFile);
		} else if (ConfigurationFileType.QOIPLANNING.equals(type)) {
			return new YmlReaderQoIPlanningSchema().isValid(ymlFile);
		} else if (ConfigurationFileType.PCMM.equals(type)) {
			return new YmlReaderPCMMSchema().isValid(ymlFile);
		} else if (ConfigurationFileType.UNCERTAINTY.equals(type)) {
			return new YmlReaderUncertaintySchema().isValid(ymlFile);
		} else if (ConfigurationFileType.SYSTEM_REQUIREMENT.equals(type)) {
			return new YmlReaderSystemRequirementSchema().isValid(ymlFile);
		} else if (ConfigurationFileType.DECISION.equals(type)) {
			return new YmlReaderDecisionSchema().isValid(ymlFile);
		}
		return false;
	}
}
