/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.uncertainty;

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
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyConstraint;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;

/**
 * Import Application interface for methods that are specific to the import of
 * Uncertainty.
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IImportUncertaintyApp extends IApplication {
	/**
	 * Check the Uncertainty schema compatibility with the configuration in the
	 * database and update it.
	 * 
	 * @param <M>            the importable model
	 * @param model          the model
	 * @param currentSpecs   the current Uncertainty Specifications
	 * @param pcmmSchemaFile the new Uncertainty schema file to import
	 * @return the analysis of import change as a map
	 * @throws CredibilityException if an error occurs during import
	 * @throws IOException          if a reading exception occurs
	 */
	<M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateUncertaintyConfiguration(
			Model model, UncertaintySpecification currentSpecs, File pcmmSchemaFile)
			throws CredibilityException, IOException;

	/**
	 * Import Uncertainty configuration without duplicate.
	 * 
	 * @param model the model
	 * @param specs the specs to import
	 * @throws CredibilityException if an error occurs during import
	 */
	void importUncertaintyConfiguration(Model model, UncertaintySpecification specs) throws CredibilityException;

	/**
	 * Import the Uncertainty Specification from the CF schema file into the working
	 * dir database without duplicate.
	 *
	 * @param model      the CF model
	 * @param user       the user
	 * @param schemaFile the schema configuration file
	 * @throws CredibilityException if an error occurs during import
	 * @throws IOException          if reading the schema file triggers an exception
	 */
	void importUncertaintySpecification(Model model, User user, File schemaFile)
			throws CredibilityException, IOException;

	/**
	 * Import Uncertainty Parameters without duplicate.
	 * 
	 * @param model                the CF model
	 * @param uncertaintyParamList the uncertainty parameters to add
	 * @throws CredibilityException if an error occurs during import
	 */
	void importUncertaintyParam(Model model, List<UncertaintyParam> uncertaintyParamList) throws CredibilityException;

	/**
	 * Import update uncertainty param.
	 *
	 * @param model                the model
	 * @param uncertaintyParamList the uncertainty param list
	 * @throws CredibilityException the credibility exception
	 */
	void importUpdateUncertaintyParam(Model model, List<UncertaintyParam> uncertaintyParamList)
			throws CredibilityException;

	/**
	 * Import Uncertainty parameter select values without duplicate.
	 * 
	 * @param uncertaintyParam           the uncertainty parameter to associate
	 * @param uncertaintySelectValueList the values to import
	 * @throws CredibilityException if an error occurs during import
	 */
	void importUncertaintySelectValue(List<UncertaintySelectValue> uncertaintySelectValueList,
			UncertaintyParam uncertaintyParam) throws CredibilityException;

	/**
	 * Import Uncertainty parameter constraints without duplicate.
	 * 
	 * @param constraintList the values to import
	 * @param param          the uncertainty parameter to associate
	 * @throws CredibilityException if an error occurs during import
	 */
	void importUncertaintyConstraint(List<UncertaintyConstraint> constraintList, UncertaintyParam param)
			throws CredibilityException;

	/**
	 * Import the Uncertainty changes approved.
	 *
	 * @param <M>      the importable class
	 * @param model    the CF model
	 * @param user     the user
	 * @param toImport the import list
	 * @throws CredibilityException if an error occurs during import
	 */
	<M extends IImportable<M>> void importUncertaintyChanges(Model model, User user,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException;

	/**
	 * Import uncertainty data.
	 *
	 * @param model           the model
	 * @param user            the user
	 * @param uncertaintyFile the uncertainty file
	 * @throws CredibilityException the credibility exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	void importUncertaintyData(Model model, User user, File uncertaintyFile) throws CredibilityException, IOException;

	/**
	 * Import uncertainties.
	 *
	 * @param model           the model
	 * @param user            the user
	 * @param parent          the parent
	 * @param uncertaintyList the uncertainty list
	 * @throws CredibilityException the credibility exception
	 */
	void importUncertainties(Model model, User user, Uncertainty parent, List<Uncertainty> uncertaintyList)
			throws CredibilityException;

	/**
	 * Import uncertainties.
	 *
	 * @param uncertainty the uncertainty
	 * @param user        the user
	 * @param values      the values
	 * @throws CredibilityException the credibility exception
	 */
	void importUncertaintyValues(Uncertainty uncertainty, User user, List<UncertaintyValue> values)
			throws CredibilityException;

}
