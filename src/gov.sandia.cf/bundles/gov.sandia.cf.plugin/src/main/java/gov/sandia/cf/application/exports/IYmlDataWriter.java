/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.exports;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;

/**
 * The yml writer interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IYmlDataWriter {

	/**
	 * Write data.
	 *
	 * @param yamlEngine    the yaml engine
	 * @param cfDataFile    the cf data file
	 * @param exportOptions the export options
	 * @param append        the append
	 * @throws CredibilityException the credibility exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	public void writeData(final Yaml yamlEngine, final File cfDataFile, final Map<ExportOptions, Object> exportOptions,
			final boolean append) throws CredibilityException, IOException;
}
