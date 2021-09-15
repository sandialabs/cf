/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;

/**
 * Import Application interface for methods that are specific to the import of
 * PCMM.
 * 
 * @author Didier Verstraete
 *
 */
public interface IImportPCMMApp extends IApplication {

	/**
	 * Check the PCMM compatibility with the configuration in the database and
	 * update it.
	 * 
	 * @param <M>            the importable model
	 * @param model          the model
	 * @param currentSpecs   the current PCMM Specifications
	 * @param pcmmSchemaFile the new pcmm schema file to import
	 * @return the analysis of import change as a map
	 * @throws CredibilityException if an error occurred
	 * @throws IOException          if a reading exception occurs
	 */
	<M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdatePCMMConfiguration(Model model,
			PCMMSpecification currentSpecs, File pcmmSchemaFile) throws CredibilityException, IOException;

	/**
	 * Check the PCMM Planning compatibility with the configuration in the database
	 * and update it.
	 * 
	 * @param <M>               the importable model
	 * @param model             the model
	 * @param planningFields    the fields to import
	 * @param planningQuestions the questions
	 * @return the analysis of import change as a map
	 */
	<M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdatePCMMPlanningConfiguration(
			Model model, List<PCMMPlanningParam> planningFields,
			Map<IAssessable, List<PCMMPlanningQuestion>> planningQuestions);

	/**
	 * Import the PCMM Specification from the CF schema file into the working dir
	 * database without duplicate.
	 * 
	 * @param model          the CF model
	 * @param pcmmSchemaFile the pcmm schema configuration file
	 * @throws CredibilityException if a database error occurred
	 * @throws IOException          if reading the schema file triggers an exception
	 */
	void importPCMMSpecification(Model model, File pcmmSchemaFile) throws CredibilityException, IOException;

	/**
	 * Import the elements into the database without duplicate.
	 * 
	 * @param model     the model to associate
	 * @param pcmmSpecs the pcmm specifications
	 * @throws CredibilityException if a parameter is not valid.
	 */
	void importPCMMConfiguration(Model model, PCMMSpecification pcmmSpecs) throws CredibilityException;

	/**
	 * import the PCMM options without duplicate.
	 * 
	 * @param phases the phases to import
	 * @throws CredibilityException if an error occurred
	 */
	void importPCMMOptionsWithPhases(List<PCMMPhase> phases) throws CredibilityException;

	/**
	 * import the PCMM options without duplicate.
	 * 
	 * @param options the options to import
	 * @throws CredibilityException if an error occurred
	 */
	void importPCMMOptions(List<PCMMOption> options) throws CredibilityException;

	/**
	 * import the PCMM level colors without duplicate.
	 * 
	 * @param levelColors the level colors to import
	 * @throws CredibilityException if an error occurred
	 */
	void importPCMMLevelColors(List<PCMMLevelColor> levelColors) throws CredibilityException;

	/**
	 * import the PCMM Elements without duplicate.
	 * 
	 * @param model    the model to associate
	 * @param elements the elements to import
	 * @throws CredibilityException if an error occurred
	 */
	void importPCMMElements(Model model, List<PCMMElement> elements) throws CredibilityException;

	/**
	 * import the PCMM roles
	 * 
	 * @param roles the roles to import
	 * @throws CredibilityException if an error occurred
	 */
	void importPCMMRoles(List<Role> roles) throws CredibilityException;

	/**
	 * Import the subelements into the database without duplicate.
	 * 
	 * @param subelementList the subelements list
	 * @param createdElement the pcmm element to associate with
	 * @throws CredibilityException if a parameter is not valid.
	 */
	void importPCMMSubelements(List<PCMMSubelement> subelementList, PCMMElement createdElement)
			throws CredibilityException;

	/**
	 * Import the level list into the database. WARNING: only associate to a PCMM
	 * element or a PCMM subelement without duplicate.
	 * 
	 * @param levelList  the level list
	 * @param element    the element to associate with
	 * @param subelement the subelement to associate with
	 * @throws CredibilityException if a parameter is not valid.
	 */
	void importPCMMLevels(List<PCMMLevel> levelList, PCMMElement element, PCMMSubelement subelement)
			throws CredibilityException;

	/**
	 * Import the level descriptor list into the database without duplicate.
	 * 
	 * @param levelDescList the level descriptors list
	 * @param level         the level to associate with
	 * @throws CredibilityException if a parameter is not valid.
	 */
	void importPCMMLevelDescriptors(List<PCMMLevelDescriptor> levelDescList, PCMMLevel level)
			throws CredibilityException;

	/**
	 * Import the PCMM Planning content without duplicate.
	 * 
	 * @param model             the CF model
	 * @param planningFields    the planning fields to add
	 * @param planningQuestions the planning questions to add
	 * @throws CredibilityException if an entity is not valid
	 */
	void importPCMMPlanning(Model model, List<PCMMPlanningParam> planningFields,
			Map<IAssessable, List<PCMMPlanningQuestion>> planningQuestions) throws CredibilityException;

	/**
	 * Import the PCMM Planning fields without duplicate.
	 * 
	 * @param model          the CF model
	 * @param planningFields the planning fields to add
	 * @throws CredibilityException if an entity is not valid
	 */
	void importPCMMPlanningParam(Model model, List<PCMMPlanningParam> planningFields) throws CredibilityException;

	/**
	 * Import the PCMM Planning questions without duplicate.
	 * 
	 * @param model             the CF model
	 * @param planningQuestions the planning questions to add
	 * @throws CredibilityException if an entity is not valid
	 */
	void importPCMMPlanningQuestions(Model model, List<PCMMPlanningQuestion> planningQuestions)
			throws CredibilityException;

	/**
	 * Import the PCMM changes approved.
	 * 
	 * @param <M>      the importable class
	 * @param model    the CF model
	 * @param toImport the import list
	 * @throws CredibilityException if an error occured
	 */
	<M extends IImportable<M>> void importPCMMChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException;
}
