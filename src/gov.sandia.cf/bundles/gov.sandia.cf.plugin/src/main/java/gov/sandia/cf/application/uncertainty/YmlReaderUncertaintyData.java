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
import org.yaml.snakeyaml.representer.Representer;

import gov.sandia.cf.application.imports.IYmlReader;
import gov.sandia.cf.application.imports.YmlCFImportConstructor;
import gov.sandia.cf.constants.configuration.YmlUncertaintyConstants;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.dto.yml.YmlUncertaintyDataDto;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * This class loads uncertainty data. The actual implementation is stored in a
 * YML file
 * 
 * @author Didier Verstraete
 */
public class YmlReaderUncertaintyData implements IYmlReader<YmlUncertaintyDataDto> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlReaderUncertaintyData.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public YmlUncertaintyDataDto load(File ymlSchema) throws CredibilityException, IOException {
		return read(ymlSchema);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(File ymlFile) {

		try (FileReader fileReader = new FileReader(ymlFile)) {

			// yaml reader
			Map<?, ?> yamlData = new Yaml().load(fileReader);

			if (yamlData != null) {

				// retrieve parameters
				if (yamlData.containsKey(YmlUncertaintyConstants.CONF_UNCERTAINTYGROUPS)) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.warn("Uncertainty file not valid", e); //$NON-NLS-1$
		}

		return false;
	}

	/**
	 * Read.
	 *
	 * @param ymlFile the yml file
	 * @return a YmlUncertaintyDataDto class loaded with @param reader.
	 * @throws IOException          if a reading exception occurs
	 * @throws CredibilityException
	 */
	private YmlUncertaintyDataDto read(File ymlFile) throws IOException, CredibilityException {

		YmlUncertaintyDataDto uncertaintyData = null;

		if (ymlFile == null || !ymlFile.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
		}

		if (!FileTools.isYmlFile(ymlFile.getPath())) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTYML, ymlFile.getPath()));
		}

		try (FileReader fileReader = new FileReader(ymlFile)) {

			// skip non present attribute
			Representer representer = new Representer();
			representer.getPropertyUtils().setSkipMissingProperties(true);

			// read
			uncertaintyData = new Yaml(new YmlCFImportConstructor(YmlUncertaintyDataDto.class), representer)
					.load(fileReader);
		}

		return uncertaintyData;
	}

}
