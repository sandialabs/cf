/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.requirement;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.configuration.IYmlReader;
import gov.sandia.cf.application.configuration.YmlReaderGenericSchema;
import gov.sandia.cf.application.configuration.YmlReaderGlobal;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.SystemRequirementConstraint;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;

/**
 * This class loads credibility configuration. The actual implementation is
 * stored in a YML file
 * 
 * @author Maxime N.
 */
public class YmlReaderSystemRequirementSchema implements IYmlReader<SystemRequirementSpecification> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlReaderSystemRequirementSchema.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SystemRequirementSpecification load(File ymlSchema) throws CredibilityException, IOException {
		return read(ymlSchema);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(File ymlSchema) {

		try (FileReader fileReader = new FileReader(ymlSchema)) {

			// yaml reader
			Map<?, ?> yamlSpecifications = new Yaml().load(fileReader);

			if (yamlSpecifications != null) {

				if (yamlSpecifications.get(YmlSystemRequirementSchema.CONF_COM) instanceof Map) {
					return true;
				}

				// retrieve parameters
				if (yamlSpecifications.containsKey(YmlSystemRequirementSchema.CONF_REQUIREMENTS_PARAMETER)) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.warn("System Requirements file not valid", e); //$NON-NLS-1$
		}

		return false;
	}

	/**
	 * @param reader         the reader
	 * @param specifications the credibility project specifications
	 * @return a CredibilityProjectSpecification class loaded with @param reader.
	 * @throws CredibilityException if an error occurred while processing the
	 *                              queries
	 * @throws IOException          if a reading exception occurs
	 */
	private SystemRequirementSpecification read(File ymlSchema) throws CredibilityException, IOException {

		SystemRequirementSpecification specifications = new SystemRequirementSpecification();

		// read
		Map<String, Object> yamlSpecifications = YmlReaderGlobal.loadYmlFile(ymlSchema);

		// log variable
		String errorMessage = "credibility configuration missing {} tag"; //$NON-NLS-1$

		if (yamlSpecifications != null) {

			// read requirement specifications
			Map<?, ?> yamlRequirementSpecifications = yamlSpecifications;
			if (yamlSpecifications.get(YmlSystemRequirementSchema.CONF_COM) instanceof Map) {
				yamlRequirementSpecifications = (Map<?, ?>) yamlSpecifications.get(YmlSystemRequirementSchema.CONF_COM);
			}

			if (yamlRequirementSpecifications != null) {

				// Retrieve parameters
				if (yamlRequirementSpecifications
						.get(YmlSystemRequirementSchema.CONF_REQUIREMENTS_PARAMETER) instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<String, Map<String, Object>> yamlParameters = (Map<String, Map<String, Object>>) yamlRequirementSpecifications
							.get(YmlSystemRequirementSchema.CONF_REQUIREMENTS_PARAMETER);
					try {
						specifications.setParameters(YmlReaderGenericSchema.createParameters(
								SystemRequirementParam.class, SystemRequirementSelectValue.class,
								SystemRequirementConstraint.class, yamlParameters));
					} catch (InstantiationException | IllegalAccessException e) {
						logger.warn(e.getMessage(), e);
					}
				} else {
					logger.warn(errorMessage, YmlSystemRequirementSchema.CONF_REQUIREMENTS_PARAMETER);
				}
			}
		}

		return specifications;
	}

}
