/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * This class reads the evidence folder structure yml file
 * 
 * @author Didier Verstraete
 *
 */
public class YmlReaderEvidenceFolderStructure {

	/**
	 * @param yamlFile the yaml configuration file
	 * @return the credibility evidence folder structure as a map
	 * @throws IOException if a I/O exception occurs
	 */
	public Map<String, Object> readConfigurationFile(File yamlFile) throws IOException {

		Map<String, Object> yamlProjectSpecifications = null;

		try (FileReader fileReader = new FileReader(yamlFile)) {
			yamlProjectSpecifications = new Yaml().load(fileReader);
		}

		return yamlProjectSpecifications;
	}

}
