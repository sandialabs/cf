/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningTableValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;

/**
 * Interface to manage PCMM Application methods
 * 
 * @author Didier Verstraete
 *
 */
/**
 * @author Didier Verstraete
 *
 */
public interface IPCMMPlanningApplication extends IApplication {

	/**
	 * @return true if there is at least one planning parameter or question,
	 *         otherwise false.
	 */
	boolean isPCMMPlanningEnabled();

	/**
	 * @param planningParam the pcmm planning parameter
	 * @return the newly created planning param
	 * @throws CredibilityException if an error occured
	 */
	PCMMPlanningParam addPlanningParameter(PCMMPlanningParam planningParam) throws CredibilityException;

	/**
	 * @param value the value to add
	 * @return a new parameter select value
	 * @throws CredibilityException if an error occured
	 */
	PCMMPlanningSelectValue addPCMMPlanningSelectValue(PCMMPlanningSelectValue value) throws CredibilityException;

	/**
	 * Delete the planning parameter select value.
	 * 
	 * @param value the planning parameter select value to delete
	 * @throws CredibilityException if an error occured
	 */
	void deletePlanningSelectValue(PCMMPlanningSelectValue value) throws CredibilityException;

	/**
	 * Import the parameter select values
	 * 
	 * @param parameter the parameter to associate with the values
	 * @param values    the values to import
	 * @throws CredibilityException if an error occured
	 */
	void addAllPCMMPlanningSelectValue(PCMMPlanningParam parameter, List<PCMMPlanningSelectValue> values)
			throws CredibilityException;

	/**
	 * @param filters the filters to retrieve the planning fields
	 * @return the planning fields according to the filter
	 */
	List<PCMMPlanningParam> getPlanningFieldsBy(Map<EntityFilter, Object> filters);

	/**
	 * Update the planning parameters
	 * 
	 * @param planningParam the parameter to update
	 * @return the updated planning parameter
	 * @throws CredibilityException if an error occured
	 */
	PCMMPlanningParam updatePlanningParameter(PCMMPlanningParam planningParam) throws CredibilityException;

	/**
	 * Import the PCMM Planning configuration into the database
	 * 
	 * @param model             the model
	 * @param planningFields    the fields to import
	 * @param planningQuestions the questions
	 * @throws CredibilityException if an error occured
	 */
	void addAllPCMMPlanning(Model model, List<PCMMPlanningParam> planningFields,
			Map<IAssessable, List<PCMMPlanningQuestion>> planningQuestions) throws CredibilityException;

	/**
	 * Import the PCMM Planning fields into the database
	 * 
	 * @param model          the model
	 * @param planningFields the fields to import
	 * @throws CredibilityException if an error occured
	 */
	void addAllPCMMPlanningParam(Model model, List<PCMMPlanningParam> planningFields) throws CredibilityException;

	/**
	 * Delete the planning parameters and their children and values if present.
	 * 
	 * @param planningParamList the planning parameters to delete
	 * @throws CredibilityException if an error occured
	 */
	void deleteAllPlanningParameter(List<PCMMPlanningParam> planningParamList) throws CredibilityException;

	/**
	 * Delete the planning parameter and its children and values if present.
	 * 
	 * @param planningParam the planning parameter to delete
	 * @throws CredibilityException if an error occured
	 */
	void deletePlanningParameter(PCMMPlanningParam planningParam) throws CredibilityException;

	/**
	 * @param planningQuestion the pcmm planning question
	 * @return the newly created planning question
	 * @throws CredibilityException if an error occured
	 */
	PCMMPlanningQuestion addPlanningQuestion(PCMMPlanningQuestion planningQuestion) throws CredibilityException;

	/**
	 * @param element the pcmm element to filter on
	 * @param mode    the pcmm mode
	 * @return the planning questions by pcmm element
	 */
	List<PCMMPlanningQuestion> getPlanningQuestionsByElement(PCMMElement element, PCMMMode mode);

	/**
	 * Import the PCMM Planning questions into the database
	 * 
	 * @param model             the model
	 * @param planningQuestions the questions to import
	 * @throws CredibilityException if an error occured
	 */
	void addAllPCMMPlanningQuestion(Model model, List<PCMMPlanningQuestion> planningQuestions)
			throws CredibilityException;

	/**
	 * Delete all questions with their values.
	 * 
	 * @param planningQuestionList the questions to delete
	 * @throws CredibilityException if an error occured
	 */
	void deleteAllPlanningQuestions(List<PCMMPlanningQuestion> planningQuestionList) throws CredibilityException;

	/**
	 * Delete the question with its values.
	 * 
	 * @param question the question to delete
	 * @throws CredibilityException if an error occured
	 */
	void deletePlanningQuestion(PCMMPlanningQuestion question) throws CredibilityException;

	/**
	 * Add the planning value
	 * 
	 * @param planningValue the value to update
	 * @return the added planning value
	 * @throws CredibilityException if an error occured
	 */
	PCMMPlanningValue addPlanningValue(PCMMPlanningValue planningValue) throws CredibilityException;

	/**
	 * Update the planning value
	 * 
	 * @param planningValue the value to update
	 * @param userUpdate    the user that updated the value
	 * @return the updated planning value
	 * @throws CredibilityException if an error occured
	 */
	PCMMPlanningValue updatePlanningValue(PCMMPlanningValue planningValue, User userUpdate) throws CredibilityException;

	/**
	 * @param filters the filters to retrieve the planning field values
	 * @return the planning field values according to the filter
	 */
	List<PCMMPlanningValue> getPlanningValueBy(Map<EntityFilter, Object> filters);

	/**
	 * @param element     the element to find
	 * @param mode        the PCMM mode
	 * @param selectedTag the tag to filter on
	 * @return the planning field values by PCMM element.
	 */
	List<PCMMPlanningValue> getPlanningValueByElement(PCMMElement element, PCMMMode mode, Tag selectedTag);

	/**
	 * @param element the element to find
	 * @param mode    the PCMM mode
	 * @param tagList the tag list to filter on
	 * @return the planning field values by PCMM element.
	 */
	List<PCMMPlanningValue> getPlanningValueByElement(PCMMElement element, PCMMMode mode, List<Tag> tagList);

	/**
	 * Delete the planning value
	 * 
	 * @param value the value to delete
	 * @throws CredibilityException if an error occured
	 */
	void deletePlanningValue(PCMMPlanningValue value) throws CredibilityException;

	/**
	 * Add the planning Question value
	 * 
	 * @param questionValue the value to update
	 * @return the added planning question value
	 * @throws CredibilityException if an error occured
	 */
	PCMMPlanningQuestionValue addPlanningQuestionValue(PCMMPlanningQuestionValue questionValue)
			throws CredibilityException;

	/**
	 * Update the planning Question value
	 * 
	 * @param questionValue the value to update
	 * @param userUpdate    the user that updated the value
	 * @return the updated planning value
	 * @throws CredibilityException if an error occured
	 */
	PCMMPlanningQuestionValue updatePlanningQuestionValue(PCMMPlanningQuestionValue questionValue, User userUpdate)
			throws CredibilityException;

	/**
	 * @param filters the filters to retrieve the planning field values
	 * @return the planning Question values according to the filter
	 */
	List<PCMMPlanningQuestionValue> getPlanningQuestionValueBy(Map<EntityFilter, Object> filters);

	/**
	 * @param element     the pcmm element to filter on
	 * @param mode        the pcmm mode
	 * @param selectedTag the tag to filter on
	 * @return the planning question values by pcmm element
	 */
	List<PCMMPlanningQuestionValue> getPlanningQuestionsValueByElement(PCMMElement element, PCMMMode mode,
			Tag selectedTag);

	/**
	 * @param element the pcmm element to filter on
	 * @param mode    the pcmm mode
	 * @param tagList the tag list to filter on
	 * @return the planning question values by pcmm element
	 */
	List<PCMMPlanningQuestionValue> getPlanningQuestionsValueByElement(PCMMElement element, PCMMMode mode,
			List<Tag> tagList);

	/**
	 * Delete the question value
	 * 
	 * @param value the question value to delete
	 * @throws CredibilityException if an error occured
	 */
	void deletePlanningQuestionValue(PCMMPlanningQuestionValue value) throws CredibilityException;

	/**
	 * @param filters the filters to apply
	 * @return the planning table items found
	 */
	List<PCMMPlanningTableItem> getPlanningTableItemBy(Map<EntityFilter, Object> filters);

	/**
	 * @param element     the element to search
	 * @param mode        the PCMM mode
	 * @param selectedTag the tag to filter on
	 * @return the planning table items found
	 */
	List<PCMMPlanningTableItem> getPlanningTableItemByElement(PCMMElement element, PCMMMode mode, Tag selectedTag);

	/**
	 * @param element the element to search
	 * @param mode    the PCMM mode
	 * @param tagList the tag list to filter on
	 * @return the planning table items found
	 */
	List<PCMMPlanningTableItem> getPlanningTableItemByElement(PCMMElement element, PCMMMode mode, List<Tag> tagList);

	/**
	 * @param item the table item to add
	 * @return the newly created table item
	 * @throws CredibilityException if an error occured
	 */
	PCMMPlanningTableItem addPlanningTableItem(PCMMPlanningTableItem item) throws CredibilityException;

	/**
	 * @param item the table item to refresh
	 * @throws CredibilityException if an error occured
	 */
	void refreshPlanningTableItem(PCMMPlanningTableItem item) throws CredibilityException;

	/**
	 * @param item the table item to delete
	 * @throws CredibilityException if an error occured
	 */
	void deletePlanningTableItem(PCMMPlanningTableItem item) throws CredibilityException;

	/**
	 * @param value the table value to add
	 * @return the newly created table value
	 * @throws CredibilityException if an error occured
	 */
	PCMMPlanningTableValue addPlanningTableValue(PCMMPlanningTableValue value) throws CredibilityException;

	/**
	 * Delete the planning table value
	 * 
	 * @param value the value to delete
	 * @throws CredibilityException if an error occured
	 */
	void deletePlanningTableValue(PCMMPlanningTableValue value) throws CredibilityException;

	/**
	 * Update a planning table value.
	 * 
	 * @param value      the table value to update
	 * @param userUpdate the user that updated the value
	 * @return the updated table value
	 * @throws CredibilityException if an error occured
	 */
	PCMMPlanningTableValue updatePlanningTableValue(PCMMPlanningTableValue value, User userUpdate)
			throws CredibilityException;

	/**
	 * Duplicate all active values and items and associate them to the tag in
	 * parameter.
	 * 
	 * @param newTag the new tag to apply
	 * @throws CredibilityException if an eror occured
	 */
	void tagCurrent(Tag newTag) throws CredibilityException;

	/**
	 * Delete the values and items associated to the tag in parameter.
	 * 
	 * @param newTag the tag to delete
	 * @throws CredibilityException if an error occured
	 */
	void deleteTagged(Tag newTag) throws CredibilityException;

	/**
	 * Compute the maximum planning progress by PCMM element depending of the
	 * PCMM*mode:**PCMMMode.DEFAULT:in this mode we search the planning questions
	 * and fields*for each subelement.**PCMMMode.SIMPLIFIED:in this mode we just
	 * search the planning questions and* fields at the element level.**
	 * 
	 * @param element the PCMM element to compute progress for*
	 * @param mode    the PCMM mode*@return the planning progress*@throws
	 *                CredibilityException if an error occurs
	 * @return the PCMM planning max progress
	 * @throws CredibilityException if an error occurs
	 */
	int computePlanningMaxProgress(PCMMElement element, PCMMMode mode) throws CredibilityException;

	/**
	 * Compute the planning progress by PCMM element depending of the PCMM mode:
	 * 
	 * PCMMMode.DEFAULT: in this mode we search the planning answers for each
	 * subelement.
	 * 
	 * PCMMMode.SIMPLIFIED: in this mode we just search the planning answers at the
	 * element level.
	 * 
	 * @param element     the PCMM element to compute progress for
	 * @param selectedTag the current tag
	 * @param mode        the PCMM mode
	 * @return the planning progress
	 * @throws CredibilityException if an error occurs
	 */
	int computePlanningProgress(PCMMElement element, Tag selectedTag, PCMMMode mode) throws CredibilityException;

	/**
	 * @param paramList the pcmm planning parameter list
	 * @return the list of parameters with their children
	 */
	List<PCMMPlanningParam> flatListParamWithChildren(List<PCMMPlanningParam> paramList);

}
