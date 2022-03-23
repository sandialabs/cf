/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.exports;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;

/**
 * Import Application interface for methods that are specific to the export.
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IExportApplication extends IApplication {

	/**
	 * Export the Decision schema to the file in parameter
	 * 
	 * @param schemaFile    the schema file to write
	 * @param specification the specification
	 * @throws CredibilityException if a parameter is not valid
	 * @throws IOException          if an error occurred while writing the
	 *                              configuration
	 */
	void exportDecisionSchema(File schemaFile, DecisionSpecification specification)
			throws CredibilityException, IOException;

	/**
	 * Export the QoI Planning schema to the file in parameter
	 * 
	 * @param schemaFile    the schema file to write
	 * @param specification the specification
	 * @throws CredibilityException if a parameter is not valid
	 * @throws IOException          if an error occurred while writing the
	 *                              configuration
	 */
	void exportQoIPlanningSchema(File schemaFile, QoIPlanningSpecification specification)
			throws CredibilityException, IOException;

	/**
	 * Export the PIRT schema to the file in parameter
	 * 
	 * @param schemaFile    the schema file to write
	 * @param specification the pirt specification
	 * @throws CredibilityException if a parameter is not valid
	 * @throws IOException          if an error occurred while writing the
	 *                              configuration
	 */
	void exportPIRTSchema(final File schemaFile, final PIRTSpecification specification)
			throws CredibilityException, IOException;

	/**
	 * Export the PCMM schema to the file in parameter
	 * 
	 * @param schemaFile    the schema file to write
	 * @param specification the pcmm specification
	 * @throws CredibilityException if a parameter is not valid
	 * @throws IOException          if an error occured while writing the
	 *                              configuration
	 */
	void exportPCMMSchema(final File schemaFile, final PCMMSpecification specification)
			throws CredibilityException, IOException;

	/**
	 * Export the Uncertainty schema to the file in parameter
	 * 
	 * @param schemaFile    the schema file to write
	 * @param specification the uncertainty specification
	 * @throws CredibilityException if a parameter is not valid
	 * @throws IOException          if an error occurred while writing the
	 *                              configuration
	 */
	void exportUncertaintySchema(final File schemaFile, final UncertaintySpecification specification)
			throws CredibilityException, IOException;

	/**
	 * Export the System Requirements schema to the file in parameter
	 * 
	 * @param schemaFile    the schema file to write
	 * @param specification the system requirements specification
	 * @throws CredibilityException if a parameter is not valid
	 * @throws IOException          if an error occurred while writing the
	 *                              configuration
	 */
	void exportSysRequirementsSchema(final File schemaFile, final SystemRequirementSpecification specification)
			throws CredibilityException, IOException;

	/**
	 * Export the credibility data to the file in parameter.
	 * 
	 * This method is EXPERIMENTAL.
	 *
	 * @param schemaFile    the schema file to write
	 * @param exportOptions the export options
	 * @throws CredibilityException if a parameter is not valid
	 * @throws IOException          if an error occured while writing the
	 *                              configuration
	 */
	void exportData(File schemaFile, Map<ExportOptions, Object> exportOptions) throws CredibilityException, IOException;

}
