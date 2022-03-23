/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.configuration;

import java.io.File;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import gov.sandia.cf.constants.configuration.ConfigurationFileType;

/**
 * Configuration schema model.
 * 
 * @author Didier Verstraete
 *
 */
public class ConfigurationSchema {

	private Map<ConfigurationFileType, String> mapSchemaFilePath;

	/**
	 * Constructor
	 */
	public ConfigurationSchema() {
		this.mapSchemaFilePath = new EnumMap<>(ConfigurationFileType.class);
	}

	/**
	 * Associates a configuration type to a configuration file path
	 * 
	 * @param type the configuration type
	 * @param path the path to associate
	 */
	public void put(ConfigurationFileType type, String path) {
		if (type != null) {
			mapSchemaFilePath.put(type, path);
		}
	}

	/**
	 * Associates a configuration type to a configuration file
	 * 
	 * @param type the configuration type
	 * @param file the file to associate
	 */
	public void put(ConfigurationFileType type, File file) {
		if (type != null) {
			mapSchemaFilePath.put(type, file != null ? file.getPath() : null);
		}
	}

	/**
	 * @param type the configuration type
	 * @return the associated configuration file path
	 */
	public String get(ConfigurationFileType type) {
		return mapSchemaFilePath.get(type);
	}

	/**
	 * @param type the configuration type
	 * @return the associated configuration file
	 */
	public File getFile(ConfigurationFileType type) {
		return mapSchemaFilePath.containsKey(type) ? new File(mapSchemaFilePath.get(type)) : null;
	}

	/**
	 * @return the configuration file path values
	 */
	public Set<String> values() {
		return new HashSet<>(mapSchemaFilePath.values());
	}

	/**
	 * @param except the list of keys to not retrieve
	 * @return the list of path values except the keys in parameter
	 */
	public Set<String> valuesExcept(List<ConfigurationFileType> except) {

		Set<String> hashSet = new HashSet<>();

		if (except != null) {
			hashSet = new HashSet<>(mapSchemaFilePath.entrySet().stream().filter(e -> !except.contains(e.getKey()))
					.map(Entry::getValue).collect(Collectors.toList()));
		}

		return hashSet;
	}
}
