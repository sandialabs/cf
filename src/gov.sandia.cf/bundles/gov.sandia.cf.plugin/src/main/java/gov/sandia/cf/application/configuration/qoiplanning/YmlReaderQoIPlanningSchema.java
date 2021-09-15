/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.qoiplanning;

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
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;

/**
 * This class loads credibility configuration. The actual implementation is
 * stored in a YML file
 * 
 * @author Maxime N.
 */
public class YmlReaderQoIPlanningSchema implements IYmlReader<QoIPlanningSpecification> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlReaderQoIPlanningSchema.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QoIPlanningSpecification load(File ymlSchema) throws CredibilityException, IOException {
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

			// retrieve parameters
			if (yamlSpecifications != null && yamlSpecifications.containsKey(YmlQoIPlanningSchema.CONF_QOIPLANNING)) {
				return true;
			}
		} catch (Exception e) {
			logger.warn("System Requirements file not valid: {}", ymlSchema, e); //$NON-NLS-1$
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
	private QoIPlanningSpecification read(File ymlSchema) throws CredibilityException, IOException {

		QoIPlanningSpecification specifications = new QoIPlanningSpecification();

		// test
		Map<?, ?> yamlSpecifications = YmlReaderGlobal.loadYmlFile(ymlSchema);

		// log variable
		String errorMessage = "credibility configuration missing {} tag"; //$NON-NLS-1$

		if (yamlSpecifications != null) {

			// Retrieve parameters
			if (yamlSpecifications.get(YmlQoIPlanningSchema.CONF_QOIPLANNING) instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Map<String, Object>> yamlParameters = (Map<String, Map<String, Object>>) yamlSpecifications
						.get(YmlQoIPlanningSchema.CONF_QOIPLANNING);
				try {
					specifications.setParameters(YmlReaderGenericSchema.createParameters(QoIPlanningParam.class,
							QoIPlanningSelectValue.class, QoIPlanningConstraint.class, yamlParameters));
				} catch (InstantiationException | IllegalAccessException e) {
					logger.warn(e.getMessage(), e);
				}
			} else {
				logger.warn(errorMessage, YmlQoIPlanningSchema.CONF_QOIPLANNING);
			}
		}

		return specifications;
	}

}
