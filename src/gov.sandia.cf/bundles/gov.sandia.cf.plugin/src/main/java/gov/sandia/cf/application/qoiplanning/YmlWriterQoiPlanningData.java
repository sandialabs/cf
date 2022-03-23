/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.qoiplanning;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.exports.ExportOptionsMapper;
import gov.sandia.cf.application.exports.IYmlDataWriter;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * This class write credibility QoI Planning data. The actual implementation is
 * stored in a yaml file
 * 
 * @author Didier Verstraete
 *
 */
public class YmlWriterQoiPlanningData implements IYmlDataWriter {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlWriterQoiPlanningData.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeData(final Yaml yamlEngine, final File cfDataFile, final Map<ExportOptions, Object> exportOptions,
			final boolean append) throws CredibilityException, IOException {
		if (cfDataFile == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
		}

		// if append is not desired, delete the existing file
		if (cfDataFile.exists() && !append) {
			boolean deleted = Files.deleteIfExists(cfDataFile.toPath());
			if (!deleted || cfDataFile.exists()) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_DELETION_ERROR));
			}
		}

		// create file if it does not exist
		if (!cfDataFile.exists()) {
			boolean created = cfDataFile.createNewFile();
			if (!created || !cfDataFile.exists()) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
			}
		}

		if (exportOptions == null) {
			logger.warn("User selection is null. Nothing to write into file {}", cfDataFile.getAbsolutePath()); //$NON-NLS-1$
			return;
		}

		if (!exportOptions.containsKey(ExportOptions.QOI_PLANNER_INCLUDE)
				|| !Boolean.TRUE.equals(exportOptions.get(ExportOptions.QOI_PLANNER_INCLUDE))) {
			logger.warn("QoI Planning is not selected to be included. Nothing to write into file {}", //$NON-NLS-1$
					cfDataFile.getAbsolutePath());
			return;
		}

		logger.debug("Write credibility data to the yml file {}", cfDataFile.getAbsolutePath()); //$NON-NLS-1$

		// Write the specifications to the credibility file
		try (Writer writer = new StringWriter()) {

			// Dump map objects to yml string
			yamlEngine.dump(ExportOptionsMapper.getYmlPIRTData(exportOptions), writer);

			FileTools.writeStringInFile(cfDataFile, writer.toString(), append);
		}
	}
}
