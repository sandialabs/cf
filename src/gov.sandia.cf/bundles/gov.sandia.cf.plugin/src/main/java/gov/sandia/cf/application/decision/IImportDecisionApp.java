/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.decision;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;

/**
 * Import Application interface for methods that are specific to the import of
 * Decision.
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IImportDecisionApp extends IApplication {

	/**
	 * Check the Decision compatibility with the configuration in the database and
	 * update it.
	 * 
	 * @param <M>          the importable model
	 * @param model        the model
	 * @param currentSpecs the current Decision Specifications
	 * @param schemaFile   the new Decision schema file to import
	 * @return the analysis of import change as a map
	 * @throws CredibilityException if an error occurred
	 * @throws IOException          if a reading exception occurs
	 */
	<M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateDecisionConfiguration(
			Model model, DecisionSpecification currentSpecs, File schemaFile) throws CredibilityException, IOException;

	/**
	 * Import the Decision Specification from the CF schema file into the working
	 * dir database without duplicate.
	 *
	 * @param model      the CF model
	 * @param user the user
	 * @param schemaFile the Decision schema configuration file
	 * @throws CredibilityException if a database error occurred
	 * @throws IOException          if reading the schema file triggers an exception
	 */
	void importDecisionSpecification(Model model, User user, File schemaFile) throws CredibilityException, IOException;

	/**
	 * Import Decision configuration without duplicate.
	 * 
	 * @param model the CF model
	 * @param specs the specs to import
	 * @throws CredibilityException if an error occurred
	 */
	void importDecisionConfiguration(Model model, DecisionSpecification specs) throws CredibilityException;

	/**
	 * Import Decision Parameters without duplicate.
	 * 
	 * @param model     the CF model
	 * @param paramList the Decision parameters to add
	 * @throws CredibilityException if an error occurred
	 */
	void importDecisionParam(Model model, List<DecisionParam> paramList) throws CredibilityException;

	/**
	 * Import Decision parameter select values without duplicate.
	 * 
	 * @param param           the Decision parameter to associate
	 * @param selectValueList the values to import
	 * @throws CredibilityException if an error occurred
	 */
	void importDecisionSelectValue(List<DecisionSelectValue> selectValueList, DecisionParam param)
			throws CredibilityException;

	/**
	 * Import Decision parameter constraints without duplicate.
	 * 
	 * @param param          the Decision parameter to associate
	 * @param constraintList the values to import
	 * @throws CredibilityException if an error occurred
	 */
	void importDecisionConstraint(List<DecisionConstraint> constraintList, DecisionParam param)
			throws CredibilityException;

	/**
	 * Import the Decision changes approved.
	 * 
	 * @param <M>      the importable class
	 * @param model    the CF model
	 * @param toImport the import list
	 * @throws CredibilityException if an error occured
	 */
	<M extends IImportable<M>> void importDecisionChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException;
}
