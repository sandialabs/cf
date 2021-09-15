/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.configuration.pirt.PIRTSpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;

/**
 * Import Application interface for methods that are specific to the import of
 * PIRT.
 * 
 * @author Didier Verstraete
 *
 */
public interface IImportPIRTApp extends IApplication {

	/**
	 * Check the PIRT compatibility with the configuration in the database and
	 * update it.
	 * 
	 * @param <M>            the importable model
	 * @param model          the model
	 * @param currentSpecs   the current PIRT Specifications
	 * @param pirtSchemaFile the new pirt schema file to import
	 * @return the analysis of import change as a map
	 * @throws CredibilityException if an error occurred
	 * @throws IOException          if a reading exception occurs
	 */
	<M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdatePIRTConfiguration(Model model,
			PIRTSpecification currentSpecs, File pirtSchemaFile) throws CredibilityException, IOException;

	/**
	 * Import the PIRT Specification from the CF schema file into the working dir
	 * database without duplicate.
	 * 
	 * @param model          the CF model
	 * @param pirtSchemaFile the pirt schema configuration file
	 * @throws CredibilityException if an error occurs during import
	 * @throws IOException          if reading the schema file triggers an exception
	 */
	void importPIRTSpecification(Model model, File pirtSchemaFile) throws CredibilityException, IOException;

	/**
	 * Import the PIRT configuration into the database without duplicate.
	 * 
	 * @param model     the model to associate with
	 * @param pirtSpecs the PIRT specifications
	 * @throws CredibilityException if an error occurs during import
	 */
	void importPIRTConfiguration(Model model, PIRTSpecification pirtSpecs) throws CredibilityException;

	/**
	 * Import the PIRT level difference colors without duplicate.
	 * 
	 * @param model  the CF model
	 * @param colors the PIRT level difference colors
	 * @throws CredibilityException if an error occurs during import
	 */
	void importPIRTColors(Model model, List<PIRTLevelDifferenceColor> colors) throws CredibilityException;

	/**
	 * Import the PIRT adequacy columns without duplicate.
	 * 
	 * @param model the CF model
	 * @param list  the adequacy columns
	 * @throws CredibilityException if an error occurs during import
	 */
	void importPIRTColumns(Model model, List<PIRTAdequacyColumn> list) throws CredibilityException;

	/**
	 * Import the PIRT description headers without duplicate.
	 * 
	 * @param model the CF model
	 * @param list  the PIRT description headers
	 * @throws CredibilityException if an error occurs during import
	 */
	void importPIRTHeaders(Model model, List<PIRTDescriptionHeader> list) throws CredibilityException;

	/**
	 * Import the PIRT Levels without duplicate.
	 * 
	 * @param model the CF model
	 * @param map   the importance levels
	 * @throws CredibilityException if an error occurs during import
	 */
	void importPIRTLevels(Model model, Map<String, PIRTLevelImportance> map) throws CredibilityException;

	/**
	 * Import the PIRT Levels without duplicate.
	 * 
	 * @param model  the CF model
	 * @param levels the importance levels
	 * @throws CredibilityException if an error occurs during import
	 */
	void importPIRTLevels(Model model, List<PIRTLevelImportance> levels) throws CredibilityException;

	/**
	 * Import the PIRT Guidelines without duplicate.
	 * 
	 * @param columnGuidelines the guidelines
	 * @throws CredibilityException if an error occurs during import
	 */
	void importPIRTGuidelines(List<PIRTAdequacyColumnGuideline> columnGuidelines) throws CredibilityException;

	/**
	 * Import the PIRT Column Level Guidelines without duplicate.
	 * 
	 * @param levelGuidelines the column level guidelines
	 * @throws CredibilityException if an error occurs during import
	 */
	void importPIRTLevelGuidelines(List<PIRTAdequacyColumnLevelGuideline> levelGuidelines) throws CredibilityException;

	/**
	 * Import the PIRT changes approved.
	 * 
	 * @param <M>      the importable class
	 * @param model    the CF model
	 * @param toImport the import list
	 * @throws CredibilityException if an error occurs during import
	 */
	<M extends IImportable<M>> void importPIRTChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException;

}
