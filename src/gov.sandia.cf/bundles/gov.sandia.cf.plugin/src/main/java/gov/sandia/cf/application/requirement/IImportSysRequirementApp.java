/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.requirement;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirementConstraint;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;

/**
 * Import Application interface for methods that are specific to the import of
 * System Requirement.
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IImportSysRequirementApp extends IApplication {

	/**
	 * Check the System Requirements schema compatibility with the configuration in
	 * the database and update it.
	 * 
	 * @param <M>            the importable model
	 * @param model          the model
	 * @param currentSpecs   the current System Requirements Specifications
	 * @param pcmmSchemaFile the new System Requirements schema file to import
	 * @return the analysis of import change as a map
	 * @throws CredibilityException if an error occurred
	 * @throws IOException          if a reading exception occurs
	 */
	<M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateRequirementsConfiguration(
			Model model, SystemRequirementSpecification currentSpecs, File pcmmSchemaFile)
			throws CredibilityException, IOException;

	/**
	 * Import System Requirement configuration without duplicate.
	 * 
	 * @param model the model
	 * @param specs the specs to import
	 * @throws CredibilityException if an error occurred
	 */
	void importSysRequirementConfiguration(Model model, SystemRequirementSpecification specs)
			throws CredibilityException;

	/**
	 * Import the System Requirement Specification from the CF schema file into the
	 * working dir database without duplicate.
	 *
	 * @param model                 the CF model
	 * @param user the user
	 * @param requirementSchemaFile the schema configuration file
	 * @throws CredibilityException if a database error occurred
	 * @throws IOException          if reading the schema file triggers an exception
	 */
	void importSysRequirementSpecification(Model model, User user, File requirementSchemaFile)
			throws CredibilityException, IOException;

	/**
	 * Import Requirement Parameters without duplicate.
	 * 
	 * @param model                the CF model
	 * @param requirementParamList the Requirement parameters to add
	 * @throws CredibilityException if an error occurred
	 */
	void importSysRequirementParam(Model model, List<SystemRequirementParam> requirementParamList)
			throws CredibilityException;

	/**
	 * Import System Requirement parameter select values without duplicate.
	 * 
	 * @param requirementParam           the Requirement parameter to associate
	 * @param requirementSelectValueList the values to import
	 * @throws CredibilityException if an error occurred
	 */
	void importSysRequirementSelectValue(List<SystemRequirementSelectValue> requirementSelectValueList,
			SystemRequirementParam requirementParam) throws CredibilityException;

	/**
	 * Import System Requirement parameter constraints without duplicate.
	 * 
	 * @param constraintList the values to import
	 * @param param          the Requirement parameter to associate
	 * @throws CredibilityException if an error occurred
	 */
	void importSysRequirementConstraint(List<SystemRequirementConstraint> constraintList, SystemRequirementParam param)
			throws CredibilityException;

	/**
	 * Import the System Requirements changes approved.
	 * 
	 * @param <M>      the importable class
	 * @param model    the CF model
	 * @param toImport the import list
	 * @throws CredibilityException if an error occured
	 */
	<M extends IImportable<M>> void importSysRequirementChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException;
}
