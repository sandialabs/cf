/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.pcmm;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;

/**
 * Interface to manage PCMM Assessment Application methods
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IPCMMAssessmentApp extends IApplication {

	/**
	 * @return a list with all the active assessments
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMAssessment> getActiveAssessmentList() throws CredibilityException;

	/**
	 * @param role the role
	 * @param user the user
	 * @param elt  the element
	 * @param tag  the tag
	 * @return the assessment list for the parameters
	 * @throws CredibilityException if a parameter is not valid
	 */
	List<PCMMAssessment> getAssessmentByRoleAndUserAndEltAndTag(Role role, User user, PCMMElement elt, Tag tag)
			throws CredibilityException;

	/**
	 * @param role   the role
	 * @param user   the user
	 * @param subelt the subelement
	 * @param tag    the tag
	 * @return the assessment list for the parameters
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMAssessment> getAssessmentByRoleAndUserAndSubeltAndTag(Role role, User user, PCMMSubelement subelt, Tag tag)
			throws CredibilityException;

	/**
	 * @param id the id to find the object
	 * @return the assessment associated to the id
	 * @throws CredibilityException if an error occured while retrieving the object
	 */
	PCMMAssessment getAssessmentById(Integer id) throws CredibilityException;

	/**
	 * @param elt     the element to find
	 * @param filters the additional filters
	 * @return the assessments associated to the element in parameter
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMAssessment> getAssessmentByElement(PCMMElement elt, Map<EntityFilter, Object> filters)
			throws CredibilityException;

	/**
	 * @param elt the element
	 * @param tag the tag
	 * @return the assessments associated to the element in parameter searched into
	 *         the subelement
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMAssessment> getAssessmentByElementInSubelement(PCMMElement elt, Tag tag) throws CredibilityException;

	/**
	 * @param subelt  the subelement
	 * @param filters the filter
	 * @return the assessments associated to the sub-element in parameter
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMAssessment> getAssessmentBySubelement(PCMMSubelement subelt, Map<EntityFilter, Object> filters)
			throws CredibilityException;

	/**
	 * @param tag the tag
	 * @return the assessments associated to the tag in parameter. If the tag is
	 *         null, return the list of active assessments (non-tagged)
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMAssessment> getAssessmentByTag(Tag tag) throws CredibilityException;

	/**
	 * @param tagList the tag list
	 * @return the assessments associated to the tags in parameter. If the tag list
	 *         is null, return empty list.
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMAssessment> getAssessmentByTag(List<Tag> tagList) throws CredibilityException;

	/**
	 * @param assessment the assessment to add
	 * @return the new assessment created
	 * @throws CredibilityException if an error occured while adding new assessment
	 */
	PCMMAssessment addAssessment(PCMMAssessment assessment) throws CredibilityException;

	/**
	 * @param assessment the assessment to update
	 * @param userUpdate the user who updates
	 * @param roleUpdate the role taken to update
	 * @return the updated assessment
	 * @throws CredibilityException if an error occured while updating assessment
	 */
	PCMMAssessment updateAssessment(PCMMAssessment assessment, User userUpdate, Role roleUpdate)
			throws CredibilityException;

	/**
	 * Deletes parameter assessment from database
	 * 
	 * @param assessment the assessment to delete
	 * @throws CredibilityException if an error occured while deleting assessment
	 */
	void deleteAssessment(PCMMAssessment assessment) throws CredibilityException;

	/**
	 * Deletes the list of assessments in parameter from database
	 * 
	 * @param assessmentList the assessments to delete
	 * @throws CredibilityException if a parameter is not valid.
	 */
	void deleteAssessment(List<PCMMAssessment> assessmentList) throws CredibilityException;
}
