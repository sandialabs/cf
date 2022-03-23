/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.services.setup;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.exports.IYmlSchemaWriter;
import gov.sandia.cf.application.imports.YmlReaderGlobal;
import gov.sandia.cf.constants.configuration.YmlClientSetup;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.launcher.CFBackendConnectionType;
import gov.sandia.cf.launcher.CFClientSetup;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The Class YmlWriterClientSetup.
 * 
 * @author Didier Verstraete
 */
public class YmlWriterClientSetup implements IYmlSchemaWriter<CFClientSetup> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlWriterClientSetup.class);

	/** {@inheritDoc} */
	@Override
	public void writeSchema(final File setupFile, final CFClientSetup specification, final boolean withIds,
			final boolean append) throws CredibilityException, IOException {
		if (setupFile == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
		}

		// if append is not desired, delete the existing file
		if (setupFile.exists() && !append) {
			boolean deleted = Files.deleteIfExists(setupFile.toPath());
			if (!deleted || setupFile.exists()) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_DELETION_ERROR));
			}
		}

		// create file if it does not exist
		if (!setupFile.exists()) {
			boolean created = setupFile.createNewFile();
			if (!created || !setupFile.exists()) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
			}
		}

		if (specification == null) {
			logger.warn("Configuration is null. Nothing to write into file {}", setupFile.getAbsolutePath()); //$NON-NLS-1$
			return;
		}

		logger.debug("Write CF client setup to the yml file {}", setupFile.getAbsolutePath()); //$NON-NLS-1$

		// load global specs
		Map<String, Object> mapSpecs = YmlReaderGlobal.loadYmlFile(setupFile);

		// replace
		if (mapSpecs.get(YmlClientSetup.CF_SETUP_BACKEND_CONNECTION) instanceof String) {
			mapSpecs.remove(YmlClientSetup.CF_SETUP_BACKEND_CONNECTION);
		}

		// store backend connection
		String cfBackendValue = specification.getBackendConnectionType() != null
				? specification.getBackendConnectionType().getType()
				: CFBackendConnectionType.FILE.getType();
		mapSpecs.put(YmlClientSetup.CF_SETUP_BACKEND_CONNECTION, cfBackendValue);

		// store web backend server url
		if (CFBackendConnectionType.WEB.equals(specification.getBackendConnectionType())) {
			mapSpecs.put(YmlClientSetup.CF_SETUP_WEB_SERVER_URL, specification.getWebServerURL());
			mapSpecs.put(YmlClientSetup.CF_SETUP_WEB_APP_MODEL_ID, specification.getModelId());
		}

		// YML reader
		Yaml yaml = new Yaml();

		// Write the specifications to the credibility file
		try (Writer writer = new StringWriter()) {

			// Dump map objects to yml string
			yaml.dump(mapSpecs, writer);

			FileTools.writeStringInFile(setupFile, writer.toString(), append);
		}
	}

}
