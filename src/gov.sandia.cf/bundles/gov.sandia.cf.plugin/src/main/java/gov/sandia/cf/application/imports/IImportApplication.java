/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.imports;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.ConfigurationSchema;

/**
 * Import Application interface for methods that are specific to the import.
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IImportApplication extends IApplication {

	/**
	 * @param <M>                    the importable model
	 * @param newImportableList      the new importable list
	 * @param existingImportableList the existing importable list
	 * @return the analysis map
	 */
	<M extends IImportable<M>> Map<ImportActionType, List<?>> analyzeImport(List<M> newImportableList,
			List<M> existingImportableList);

	/**
	 * Get the changes to add.
	 * 
	 * @param <M>                    the importable model
	 * @param newImportableList      the new importable list
	 * @param existingImportableList the existing importable list
	 * @return the list of elements to add
	 */
	<M extends IImportable<M>> List<?> getChangesToAdd(List<M> newImportableList, List<M> existingImportableList);

	/**
	 * Import into the database the approved changes.
	 *
	 * @param <M>      the importable entity
	 * @param model    the model to associate with
	 * @param user     the user
	 * @param toImport the new objects to import/update/delete
	 * @throws CredibilityException if an error occurred
	 */
	<M extends IImportable<M>> void importChanges(Model model, User user,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException;

	/**
	 * Import the configuration from the CF schema file into the working dir
	 * database without duplicate.
	 *
	 * @param model      The CF model
	 * @param user       the user
	 * @param confSchema The schemas files for each configuration
	 * @throws CredibilityException if a database error occurred.
	 * @throws IOException          if reading the schema file triggers an exception
	 */
	void importConfiguration(Model model, User user, ConfigurationSchema confSchema)
			throws CredibilityException, IOException;

	/**
	 * @param importClass the import class
	 * @return the import class name
	 */
	String getImportableName(Class<?> importClass);

	/**
	 * @param <M>   the importable
	 * @param list1 the first list of importable
	 * @param list2 the secondflist of importable
	 * @return true if the two list contains the same data, otherwise false
	 */
	<M extends IImportable<M>> boolean sameListContent(List<M> list1, List<M> list2);

	/**
	 * Get a list of objects in the import map depending of its class and import
	 * action.
	 * 
	 * @param <M>          the importable model
	 * @param importClass  the importable class
	 * @param importAction the import action
	 * @param toImport     the map of objects to import
	 * @return the changes between toImport parameter and the database content
	 */
	<M extends IImportable<M>> List<M> getChanges(Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport,
			Class<M> importClass, ImportActionType importAction);

	/**
	 * @param analysis the analysis map
	 * @return the analysis map converted to a map of changes implementing
	 *         IImportable. If one element doesn't implement IImportable interface,
	 *         it will not be added to the changes map.
	 */
	Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> getListOfImportableFromAnalysis(
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis);

	/**
	 * Gets the database value for generic.
	 *
	 * @param parameter the parameter
	 * @param value     the value
	 * @return the database value for generic
	 */
	String getDatabaseValueForGeneric(GenericParameter<?> parameter, GenericValue<?, ?> value);

}
