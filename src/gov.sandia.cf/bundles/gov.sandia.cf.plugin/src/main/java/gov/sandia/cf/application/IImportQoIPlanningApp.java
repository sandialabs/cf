/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.configuration.qoiplanning.QoIPlanningSpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;

/**
 * Import Application interface for methods that are specific to the import of
 * QoI Planning.
 * 
 * @author Didier Verstraete
 *
 */
public interface IImportQoIPlanningApp extends IApplication {

	/**
	 * Check the QoI Planning compatibility with the configuration in the database
	 * and update it.
	 * 
	 * @param <M>        the importable model
	 * @param schemaFile the new QoI Planning schema file to import
	 * @return the analysis of import change as a map
	 * @throws CredibilityException if an error occurred
	 * @throws IOException          if a reading exception occurs
	 */
	<M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateQoIPlanningConfiguration(
			File schemaFile) throws CredibilityException, IOException;

	/**
	 * Import the QoI Planning Specification from the CF schema file into the
	 * working dir database without duplicate.
	 * 
	 * @param model      the CF model
	 * @param schemaFile the qoi planning schema configuration file
	 * @throws CredibilityException if a database error occurred
	 * @throws IOException          if reading the schema file triggers an exception
	 */
	void importQoIPlanningSpecification(Model model, File schemaFile) throws CredibilityException, IOException;

	/**
	 * Import QoI Planning configuration without duplicate.
	 * 
	 * @param model the CF model
	 * @param specs the specs to import
	 * @throws CredibilityException if an error occurred
	 */
	void importQoIPlanningConfiguration(Model model, QoIPlanningSpecification specs) throws CredibilityException;

	/**
	 * Import QoI Planning Parameters without duplicate.
	 * 
	 * @param model     the CF model
	 * @param paramList the QoI Planning parameters to add
	 * @throws CredibilityException if an error occurred
	 */
	void importQoIPlanningParam(Model model, List<QoIPlanningParam> paramList) throws CredibilityException;

	/**
	 * Import QoI Planning parameter select values without duplicate.
	 * 
	 * @param param           the qoi planning parameter to associate
	 * @param selectValueList the values to import
	 * @throws CredibilityException if an error occurred
	 */
	void importQoIPlanningSelectValue(List<QoIPlanningSelectValue> selectValueList, QoIPlanningParam param)
			throws CredibilityException;

	/**
	 * Import QoI Planning parameter constraints without duplicate.
	 * 
	 * @param param          the qoi planning parameter to associate
	 * @param constraintList the values to import
	 * @throws CredibilityException if an error occurred
	 */
	void importQoIPlanningConstraint(List<QoIPlanningConstraint> constraintList, QoIPlanningParam param)
			throws CredibilityException;

	/**
	 * Import the QoI Planning changes approved.
	 * 
	 * @param <M>      the importable class
	 * @param model    the CF model
	 * @param toImport the import list
	 * @throws CredibilityException if an error occured
	 */
	<M extends IImportable<M>> void importQoIPlanningChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException;
}
