/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.uncertainty;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.imports.IYmlReader;
import gov.sandia.cf.application.imports.YmlReaderGenericSchema;
import gov.sandia.cf.application.imports.YmlReaderGlobal;
import gov.sandia.cf.constants.configuration.YmlUncertaintyConstants;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.UncertaintyConstraint;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;

/**
 * This class loads credibility configuration. The actual implementation is
 * stored in a YML file
 * 
 * @author Maxime N.
 */
public class YmlReaderUncertaintySchema implements IYmlReader<UncertaintySpecification> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlReaderUncertaintySchema.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UncertaintySpecification load(File ymlSchema) throws CredibilityException, IOException {
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

				if (yamlSpecifications.get(YmlUncertaintyConstants.CONF_COM) instanceof Map) {
					return true;
				}

				// retrieve parameters
				if (yamlSpecifications.containsKey(YmlUncertaintyConstants.CONF_UNCERTAINTY_PARAMETER)) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.warn("Uncertainty file not valid", e); //$NON-NLS-1$
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
	private UncertaintySpecification read(File ymlSchema) throws CredibilityException, IOException {

		UncertaintySpecification specifications = new UncertaintySpecification();

		// read
		Map<String, Object> yamlSpecifications = YmlReaderGlobal.loadYmlFile(ymlSchema);

		// log variable
		String errorMessage = "credibility configuration missing {} tag"; //$NON-NLS-1$

		if (yamlSpecifications != null) {

			// read uncertainty specifications
			Map<?, ?> yamlUncertaintySpecifications = yamlSpecifications;
			if (yamlSpecifications.get(YmlUncertaintyConstants.CONF_COM) instanceof Map) {
				yamlUncertaintySpecifications = (Map<?, ?>) yamlSpecifications.get(YmlUncertaintyConstants.CONF_COM);
			}

			if (yamlUncertaintySpecifications != null) {

				// Retrieve parameters
				if (yamlUncertaintySpecifications.get(YmlUncertaintyConstants.CONF_UNCERTAINTY_PARAMETER) instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<String, Map<String, Object>> yamlParameters = (Map<String, Map<String, Object>>) yamlUncertaintySpecifications
							.get(YmlUncertaintyConstants.CONF_UNCERTAINTY_PARAMETER);
					try {
						specifications.setParameters(YmlReaderGenericSchema.createParameters(UncertaintyParam.class,
								UncertaintySelectValue.class, UncertaintyConstraint.class, yamlParameters));
					} catch (InstantiationException | IllegalAccessException e) {
						logger.warn(e.getMessage(), e);
					}
				} else {
					logger.warn(errorMessage, YmlUncertaintyConstants.CONF_UNCERTAINTY_PARAMETER);
				}
			}
		}

		return specifications;
	}

}
