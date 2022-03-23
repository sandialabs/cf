/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.pcmm;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAggregation;
import gov.sandia.cf.model.PCMMAggregationLevel;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.query.EntityFilter;

/**
 * Interface to manage PCMM Aggregate Application methods
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IPCMMAggregateApp extends IApplication {

	/**
	 * This method is used in DEFAULT assessment MODE.
	 * 
	 * @param model the model
	 * @param tag   the tag
	 * @return true if all the subelements have at least one assessment, otherwise
	 *         return false
	 * @throws CredibilityException if a parameter is not valid
	 */
	boolean isCompleteAggregation(Model model, Tag tag) throws CredibilityException;

	/**
	 * This method is used in SIMPLIFIED assessment MODE.
	 * 
	 * @param model the model
	 * @param tag   the tag
	 * @return true if all the elements have at least one assessment, otherwise
	 *         return false.
	 * 
	 * @throws CredibilityException if a parameter is not valid
	 */
	boolean isCompleteAggregationSimplified(Model model, Tag tag) throws CredibilityException;

	/**
	 * @param configuration the pcmm specification
	 * @param elements      the elements
	 * @param filters       the filters
	 * @return the elements aggregation of the list of elements in parameter
	 * @throws CredibilityException if a parameter is not valid
	 */
	Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateSubelements(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException;

	/**
	 * @param configuration              the pcmm specification
	 * @param mapAggregationBySubelement the aggregation map of subelements
	 * @return the elements aggregation of the list of elements in parameter
	 * @throws CredibilityException if a parameter is not valid
	 */
	Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateSubelements(PCMMSpecification configuration,
			Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> mapAggregationBySubelement)
			throws CredibilityException;

	/**
	 * @param configuration the pcmm specification
	 * @param elements      the elements
	 * @param filters       the additional filters
	 * @return the sub-element aggregation of the list of elements in parameter
	 * @throws CredibilityException if a parameter is not valid
	 */
	Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggregateAssessments(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException;

	/**
	 * @param configuration the pcmm specification
	 * @param elements      the elements
	 * @param filters       the additional filters
	 * @return the element aggregation of the list of elements in parameter
	 * @throws CredibilityException if a parameter is not valid
	 */
	Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateAssessmentSimplified(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException;

	/**
	 * @param configuration the pcmm specification
	 * @param element       the element
	 * @param filters       the filters
	 * @return the sub-element aggregation of the element in parameter
	 * @throws CredibilityException if a parameter is not valid
	 */
	Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggregateAssessments(PCMMSpecification configuration,
			PCMMElement element, Map<EntityFilter, Object> filters) throws CredibilityException;

	/**
	 * @param <T>            the pcmm type
	 * @param configuration  the pcmm specification
	 * @param item           the item to aggregate
	 * @param assessmentList the assessment list
	 * @return the aggregation of the item in parameter and assessments
	 * @throws CredibilityException if a parameter is not valid
	 */
	<T extends IAssessable> PCMMAggregation<T> aggregateAssessments(PCMMSpecification configuration, T item,
			List<PCMMAssessment> assessmentList) throws CredibilityException;

	/**
	 * @param configuration the pcmm specification
	 * @param levels        the pcmm levels
	 * @param code          the level code
	 * @return in the list of levels of the subelement, return the first level that
	 *         code equals the code parameter. If there is no level that equals the
	 *         code parameter, return the first level smallest than the code
	 *         parameter.
	 */
	PCMMAggregationLevel getClosestLevelForCode(PCMMSpecification configuration, List<PCMMLevel> levels, int code);

}
