/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.decision;

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
import gov.sandia.cf.constants.configuration.YmlDecisionSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;

/**
 * This class loads credibility configuration. The actual implementation is
 * stored in a YML file
 * 
 * @author Didier Verstraete
 */
public class YmlReaderDecisionSchema implements IYmlReader<DecisionSpecification> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlReaderDecisionSchema.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DecisionSpecification load(File ymlSchema) throws CredibilityException, IOException {
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

			if (yamlSpecifications != null
					&& yamlSpecifications.containsKey(YmlDecisionSchema.CONF_DECISION_PARAMETER)) {
				return true;
			}
		} catch (Exception e) {
			logger.warn("Decision file not valid", e); //$NON-NLS-1$
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
	private DecisionSpecification read(File ymlSchema) throws CredibilityException, IOException {

		DecisionSpecification specifications = new DecisionSpecification();

		// read
		Map<String, Object> yamlSpecifications = YmlReaderGlobal.loadYmlFile(ymlSchema);

		// log variable
		String errorMessage = "credibility configuration missing {} tag"; //$NON-NLS-1$

		if (yamlSpecifications != null) {

			// read decision specifications
			Map<?, ?> yamlDecisionSpecifications = yamlSpecifications;

			// Retrieve parameters
			if (yamlDecisionSpecifications.get(YmlDecisionSchema.CONF_DECISION_PARAMETER) instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Map<String, Object>> yamlParameters = (Map<String, Map<String, Object>>) yamlDecisionSpecifications
						.get(YmlDecisionSchema.CONF_DECISION_PARAMETER);
				try {
					specifications.setParameters(YmlReaderGenericSchema.createParameters(DecisionParam.class,
							DecisionSelectValue.class, DecisionConstraint.class, yamlParameters));
				} catch (InstantiationException | IllegalAccessException e) {
					logger.warn(e.getMessage(), e);
				}
			} else {
				logger.warn(errorMessage, YmlDecisionSchema.CONF_DECISION_PARAMETER);
			}
		}

		return specifications;
	}

}
