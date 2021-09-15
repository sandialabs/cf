/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The Yml tools class with static methods
 * 
 * @author Didier Verstraete
 *
 */
public class YmlReaderGlobal {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlReaderGlobal.class);

	/**
	 * Do not instantiate.
	 */
	private YmlReaderGlobal() {
	}

	/**
	 * @param ymlFileToValidate the file to validate
	 * @return true if the yml file can be parsed and loaded, otherwise false
	 */
	public static boolean isValidYmlFile(File ymlFileToValidate) {

		// check the file
		if (ymlFileToValidate == null || !ymlFileToValidate.exists()) {
			return false;
		}

		// yaml reader
		Yaml yaml = new Yaml();

		// file reader
		try (FileReader fileReader = new FileReader(ymlFileToValidate)) {

			// load the yml file
			yaml.load(fileReader);

		} catch (IOException | YAMLException e) {
			logger.error("The file {} is not a valid yml file.", ymlFileToValidate, e); //$NON-NLS-1$
			return false;
		}

		return true;
	}

	/**
	 * @param ymlFile the file to validate
	 * @return true if the yml file can be parsed and loaded, otherwise false
	 * @throws CredibilityException if a parameter is not valid
	 * @throws IOException          if a reading exception occurs
	 */
	public static Map<String, Object> loadYmlFile(File ymlFile) throws CredibilityException, IOException {

		// check the file
		if (ymlFile == null || !ymlFile.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
		}

		Map<String, Object> load = null;

		// file reader
		try (FileReader fileReader = new FileReader(ymlFile)) {

			// load the yml file
			load = loadYmlFile(fileReader);

		}

		return load;
	}

	/**
	 * @param reader the file reader to use
	 * @return true if the yml file can be parsed and loaded, otherwise false
	 * @throws CredibilityException if a parameter is not valid
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> loadYmlFile(FileReader reader) throws CredibilityException {

		// check the file
		if (reader == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLFILEREADER_NULL));
		}

		Map<String, Object> load = new HashMap<>();

		// YML reader
		Object data = new Yaml().load(reader);
		if (data instanceof Map) {
			load = (Map<String, Object>) data;
		}

		return load;
	}

	/**
	 * @param value the value to retrieve
	 * @return an integer if the value is an integer or a string, otherwise null
	 */
	public static Integer getInteger(Object value) {
		Integer toReturn = null;
		if (value instanceof Integer) {
			toReturn = (Integer) value;
		} else if (value instanceof String) {
			toReturn = Integer.parseInt((String) value);
		}
		return toReturn;
	}

	/**
	 * @param value        the value to retrieve
	 * @param defaultValue the default value to return
	 * @return an integer if the value is an integer or a string, otherwise return
	 *         the defaultValue
	 */
	public static Integer getIntegerOrDefault(Object value, Integer defaultValue) {
		Integer toReturn = getInteger(value);
		return toReturn != null ? toReturn : defaultValue;
	}
}
