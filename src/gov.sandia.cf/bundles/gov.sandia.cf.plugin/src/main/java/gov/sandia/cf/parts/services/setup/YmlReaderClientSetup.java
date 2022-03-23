/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.services.setup;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.imports.IYmlReader;
import gov.sandia.cf.application.imports.YmlReaderGlobal;
import gov.sandia.cf.constants.configuration.YmlClientSetup;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.launcher.CFBackendConnectionType;
import gov.sandia.cf.launcher.CFClientSetup;

/**
 * This class loads credibility configuration. The actual implementation is
 * stored in a YML file
 * 
 * @author Didier Verstraete
 */
public class YmlReaderClientSetup implements IYmlReader<CFClientSetup> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlReaderClientSetup.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CFClientSetup load(File setupFile) throws CredibilityException, IOException {
		return read(setupFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(File setupFile) {

		if (setupFile == null) {
			return false;
		}

		try (FileReader fileReader = new FileReader(setupFile)) {

			// yaml reader
			Map<?, ?> yamlSpecifications = new Yaml().load(fileReader);

			if (yamlSpecifications != null
					&& yamlSpecifications.containsKey(YmlClientSetup.CF_SETUP_BACKEND_CONNECTION)) {
				return true;
			}
		} catch (Exception e) {
			logger.debug("Setup file not valid", e); //$NON-NLS-1$
		}

		return false;
	}

	/**
	 * Read the client setup.
	 *
	 * @param setupFile the setup file
	 * @return the CF client setup
	 * @throws CredibilityException the credibility exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	private CFClientSetup read(File setupFile) throws CredibilityException, IOException {

		CFClientSetup setup = new CFClientSetup();

		// read
		Map<String, Object> yamlSpecifications = YmlReaderGlobal.loadYmlFile(setupFile);

		if (yamlSpecifications != null) {

			// read specifications
			Map<?, ?> yamlSetupMap = yamlSpecifications;

			// Retrieve Backend Connection Type
			Optional<CFBackendConnectionType> type = CFBackendConnectionType
					.getType(get(yamlSetupMap, YmlClientSetup.CF_SETUP_BACKEND_CONNECTION, String.class, true));
			if (type.isPresent()) {
				setup.setBackendConnectionType(type.get());
			}

			// Retrieve web configuration
			if (CFBackendConnectionType.WEB.equals(setup.getBackendConnectionType())) {

				// Retrieve Web Server URL
				setup.setWebServerURL(get(yamlSetupMap, YmlClientSetup.CF_SETUP_WEB_SERVER_URL, String.class, true));

				// Retrieve model id
				setup.setModelId(get(yamlSetupMap, YmlClientSetup.CF_SETUP_WEB_APP_MODEL_ID, Integer.class, true));
			}
		}

		return setup;
	}

	@SuppressWarnings("unchecked")
	private <T> T get(Map<?, ?> yamlMap, String key, Class<T> type, boolean warn) {

		if (!yamlMap.containsKey(key)) {
			if (warn) {
				logger.warn("credibility configuration missing {} tag", YmlClientSetup.CF_SETUP_BACKEND_CONNECTION); //$NON-NLS-1$
			}
			return null;
		}

		Object value = yamlMap.get(key);

		if (type.isInstance(value)) {
			return (T) value;
		} else {
			return null;
		}
	}
}
