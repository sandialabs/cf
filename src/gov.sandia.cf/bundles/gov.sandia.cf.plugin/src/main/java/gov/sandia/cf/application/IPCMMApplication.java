/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.model.PCMMAggregation;
import gov.sandia.cf.model.PCMMAggregationLevel;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
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
public interface IPCMMApplication extends IApplication {

	/**
	 * @param model the CF model
	 * @return the pcmm configuration loaded
	 * @throws CredibilityException if an error occured
	 */
	PCMMSpecification loadPCMMConfiguration(Model model) throws CredibilityException;

	/**
	 * @return the PCMM phases
	 */
	List<PCMMPhase> getPCMMPhases();

	/**
	 * @return the PCMM options
	 */
	List<PCMMOption> getPCMMOptions();

	/**
	 * @param option the option to add
	 * @return the entity added
	 * @throws CredibilityException if an error occurs during creation
	 */
	PCMMOption addPCMMOption(PCMMOption option) throws CredibilityException;

	/**
	 * @param option the option to update
	 * @return the entity updated
	 * @throws CredibilityException if an error occurs during update
	 */
	PCMMOption updatePCMMOption(PCMMOption option) throws CredibilityException;

	/**
	 * @param option the entity to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deletePCMMOption(PCMMOption option) throws CredibilityException;

	/**
	 * @param options the PCMM options to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllPCMMOptions(List<PCMMOption> options) throws CredibilityException;

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

	/**
	 * @param model the model used to find pcmm element associated (must not be
	 *              null)
	 * @return the pcmm element associated to @param model
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMElement> getElementList(Model model) throws CredibilityException;

	/**
	 * @param id the id to find the object
	 * @return the element associated to the id
	 * @throws CredibilityException if an error occured while retrieving the object
	 */
	PCMMElement getElementById(Integer id) throws CredibilityException;

	/**
	 * @param element the element to add
	 * @return the new element created
	 * @throws CredibilityException if an error occured while adding new element
	 */
	PCMMElement addElement(PCMMElement element) throws CredibilityException;

	/**
	 * @param element the element to update
	 * @return the updated element
	 * @throws CredibilityException if an error occured while updating element
	 */
	PCMMElement updateElement(PCMMElement element) throws CredibilityException;

	/**
	 * Deletes parameter element from database
	 * 
	 * @param element the element to delete
	 * @throws CredibilityException if an error occured while deleting element
	 */
	void deleteElement(PCMMElement element) throws CredibilityException;

	/**
	 * @return a list with all the active evidence
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMEvidence> getActiveEvidenceList() throws CredibilityException;

	/**
	 * @param id the id to find the object
	 * @return the evidence associated to the id
	 * @throws CredibilityException if an error occured while retrieving the object
	 */
	PCMMEvidence getEvidenceById(Integer id) throws CredibilityException;

	/**
	 * @return the list of all the evidences
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMEvidence> getAllEvidence() throws CredibilityException;

	/**
	 * @param tag the tag
	 * @return the list of evidence associated to the tag in parameter. If the tag
	 *         is null, return the list of active evidence (non-tagged).
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMEvidence> getEvidenceByTag(Tag tag) throws CredibilityException;

	/**
	 * @param tagList the tag list
	 * @return the list of evidence associated to the tags in parameter. If the tag
	 *         list is null, return nothing.
	 * @throws CredibilityException if a parameter is not valid.
	 */
	List<PCMMEvidence> getEvidenceByTag(List<Tag> tagList) throws CredibilityException;

	/**
	 * @param filters the entity filters
	 * @return a list of evidence filtered by different evidence fields
	 */
	List<PCMMEvidence> getEvidenceBy(Map<EntityFilter, Object> filters);

	/**
	 * @param evidence the evidence to add
	 * @return the new evidence created
	 * @throws CredibilityException if an error occured while adding new evidence
	 */
	PCMMEvidence addEvidence(PCMMEvidence evidence) throws CredibilityException;

	/**
	 * @param evidence the evidence to update
	 * @return the updated evidence
	 * @throws CredibilityException if an error occured while updating evidence
	 */
	PCMMEvidence updateEvidence(PCMMEvidence evidence) throws CredibilityException;

	/**
	 * Deletes parameter evidence from database
	 * 
	 * @param evidence the evidence to delete
	 * @throws CredibilityException if an error occured while deleting evidence
	 */
	void deleteEvidence(PCMMEvidence evidence) throws CredibilityException;

	/**
	 * Deletes the list of evidences in parameter from database
	 * 
	 * @param evidenceList the evidence list to delete
	 * @throws CredibilityException if a parameter is not valid.
	 */
	void deleteEvidence(List<PCMMEvidence> evidenceList) throws CredibilityException;

	/**
	 * @param id the id to find the object
	 * @return the level associated to the id
	 * @throws CredibilityException if an error occured while retrieving the object
	 */
	PCMMLevel getLevelById(Integer id) throws CredibilityException;

	/**
	 * @param level the level to add
	 * @return the new level created
	 * @throws CredibilityException if an error occured while adding new level
	 */
	PCMMLevel addLevel(PCMMLevel level) throws CredibilityException;

	/**
	 * @param level the level to update
	 * @return the updated level
	 * @throws CredibilityException if an error occured while updating level
	 */
	PCMMLevel updateLevel(PCMMLevel level) throws CredibilityException;

	/**
	 * Deletes parameter level from database
	 * 
	 * @param level the level to delete
	 * @throws CredibilityException if an error occured while deleting level
	 */
	void deleteLevel(PCMMLevel level) throws CredibilityException;

	/**
	 * @param id the id to find the object
	 * @return the levelDescriptor associated to the id
	 * @throws CredibilityException if an error occured while retrieving the object
	 */
	PCMMLevelDescriptor getLevelDescriptorById(Integer id) throws CredibilityException;

	/**
	 * @param levelDescriptor the levelDescriptor to add
	 * @return the new levelDescriptor created
	 * @throws CredibilityException if an error occured while adding new
	 *                              levelDescriptor
	 */
	PCMMLevelDescriptor addLevelDescriptor(PCMMLevelDescriptor levelDescriptor) throws CredibilityException;

	/**
	 * @param levelDescriptor the levelDescriptor to update
	 * @return the updated levelDescriptor
	 * @throws CredibilityException if an error occured while updating
	 *                              levelDescriptor
	 */
	PCMMLevelDescriptor updateLevelDescriptor(PCMMLevelDescriptor levelDescriptor) throws CredibilityException;

	/**
	 * Deletes parameter levelDescriptor from database
	 * 
	 * @param levelDescriptor the levelDescriptor to delete
	 * @throws CredibilityException if an error occured while deleting
	 *                              levelDescriptor
	 */
	void deleteLevelDescriptor(PCMMLevelDescriptor levelDescriptor) throws CredibilityException;

	/**
	 * @param id the id to find subelement
	 * @return the subelement associated to the id
	 * @throws CredibilityException if an error occured while retrieving subelement
	 */
	PCMMSubelement getSubelementById(Integer id) throws CredibilityException;

	/**
	 * @param subelement the subelement to add
	 * @return the new subelement created
	 * @throws CredibilityException if an error occured while adding new subelement
	 */
	PCMMSubelement addSubelement(PCMMSubelement subelement) throws CredibilityException;

	/**
	 * @param subelement the subelement to update
	 * @return the updated subelement
	 * @throws CredibilityException if an error occured while updating subelement
	 */
	PCMMSubelement updateSubelement(PCMMSubelement subelement) throws CredibilityException;

	/**
	 * Deletes subelement from database
	 * 
	 * @param subelement the subelement to delete
	 * @throws CredibilityException if an error occured while deleting subelement
	 */
	void deleteSubelement(PCMMSubelement subelement) throws CredibilityException;

	/**
	 * @return all the roles
	 */
	List<Role> getRoles();

	/**
	 * @param id the id to find role
	 * @return the role associated to the id
	 * @throws CredibilityException if an error occured while retrieving role
	 */
	Role getRoleById(Integer id) throws CredibilityException;

	/**
	 * @param role the role to add
	 * @return the new role created
	 * @throws CredibilityException if an error occured while adding new role
	 */
	Role addRole(Role role) throws CredibilityException;

	/**
	 * @param role the role to update
	 * @return the updated role
	 * @throws CredibilityException if an error occured while updating role
	 */
	Role updateRole(Role role) throws CredibilityException;

	/**
	 * Deletes role from database
	 * 
	 * @param role the role to delete
	 * @throws CredibilityException if an error occured while deleting role
	 */
	void deleteRole(Role role) throws CredibilityException;

	/**
	 * @param levelColor the level color to add
	 * @return the new level color created
	 * @throws CredibilityException if an error occured while adding
	 */
	PCMMLevelColor addLevelColor(PCMMLevelColor levelColor) throws CredibilityException;

	/**
	 * @param levelColor the level color to update
	 * @return the updated level color
	 * @throws CredibilityException if an error occured while updating
	 */
	PCMMLevelColor updateLevelColor(PCMMLevelColor levelColor) throws CredibilityException;

	/**
	 * Deletes level color from database
	 * 
	 * @param levelColor the level color to delete
	 * @throws CredibilityException if an error occured while deleting
	 */
	void deleteLevelColor(PCMMLevelColor levelColor) throws CredibilityException;

	/**
	 * 
	 * Creates a new tag for the current evidence and assessments
	 * 
	 * @param newTag contains the tag information
	 * @return the current tag
	 * @throws CredibilityException if a parameter is not valid.
	 */
	Tag tagCurrent(Tag newTag) throws CredibilityException;

	/**
	 * @return all the PCMM tags
	 */
	List<Tag> getTags();

	/**
	 * @param tag the tag to update
	 * @return the updated tag
	 * @throws CredibilityException if an error occured while updating tag
	 */
	Tag updateTag(Tag tag) throws CredibilityException;

	/**
	 * Deletes tag from database
	 * 
	 * @param tag the tag to delete
	 * @throws CredibilityException if an error occured while deleting tag
	 */
	void deleteTag(Tag tag) throws CredibilityException;

	/**
	 * 
	 * @param model         the model
	 * @param configuration the pcmm specification
	 * @return the current progress for all the PCMM Elements
	 * @throws CredibilityException if a parameter is not valid.
	 */
	int computeCurrentProgress(Model model, PCMMSpecification configuration) throws CredibilityException;

	/**
	 * @param element       the element
	 * @param selectedTag   the tag selected
	 * @param configuration the pcmm specification
	 * 
	 * @return the current progress of the PCMM Element in parameter.
	 * @throws CredibilityException if a parameter is not valid.
	 */
	int computeCurrentProgressByElement(PCMMElement element, Tag selectedTag, PCMMSpecification configuration)
			throws CredibilityException;

	/**
	 * Find duplicate evidence By Path
	 * 
	 * @param evidence the evidence
	 * @return the list of evidence with a same path
	 */
	public List<PCMMEvidence> findDuplicateEvidenceByPath(PCMMEvidence evidence);

	/**
	 * Find duplicate evidence By Path and Section
	 * 
	 * @param evidence the evidence
	 * @return the list of evidence with the same path and section
	 */
	public List<PCMMEvidence> findDuplicateEvidenceByPathAndSection(PCMMEvidence evidence);

	/**
	 * Find number of evidences with error
	 * 
	 * @return the list of evidence with a same path
	 * @throws CredibilityException if an error occured.
	 */
	public int findEvidenceErrorNotification() throws CredibilityException;

	/**
	 * Find number of evidences with warning
	 * 
	 * @return the list of evidence with a same path
	 * @throws CredibilityException if an error occured.
	 */
	public int findEvidenceWarningNotification() throws CredibilityException;

	/**
	 * Get duplicated evidence file association notification
	 * 
	 * @param evidence the evidence
	 * @param id       the evidence id to test (evidence.getId() not used do not
	 *                 merge the object with jpa persistence)
	 * @return a map of notifications for the current evidence
	 */
	public Map<NotificationType, String> getDuplicatedEvidenceNotification(PCMMEvidence evidence, Integer id);

	/**
	 * Get the notifications for an evidence
	 * 
	 * @param evidence The evidence
	 * @param id       the evidence id to test (evidence.getId() not used do not
	 *                 merge the object with jpa persistence)
	 * @return The notifications for an evidence
	 */
	public Map<NotificationType, List<String>> getEvidenceNotifications(PCMMEvidence evidence, Integer id);

	/**
	 * Get all notifications for all evidences group by evidence
	 * 
	 * @return the evidence notifications
	 * @throws CredibilityException if an error occured while getting notifications.
	 */
	public Map<PCMMEvidence, Map<NotificationType, List<String>>> getAllEvidenceNotifications()
			throws CredibilityException;

	/**
	 * Compute the evidence progress by PCMM element depending of the PCMM mode:
	 * 
	 * PCMMMode.DEFAULT: in this mode we search the evidence for each subelement.
	 * 
	 * PCMMMode.SIMPLIFIED: in this mode we just search the evidence at the element
	 * level.
	 * 
	 * @param element     the PCMM element to compute progress for
	 * @param selectedTag the current tag
	 * @param mode        the PCMM mode
	 * @return the assess progress
	 */
	int computeEvidenceProgress(PCMMElement element, Tag selectedTag, PCMMMode mode);

	/**
	 * Compute the assess progress by PCMM element depending of the PCMM mode:
	 * 
	 * PCMMMode.DEFAULT: in this mode we search the assessments for each subelement.
	 * 
	 * PCMMMode.SIMPLIFIED: in this mode we just search the assessments at the
	 * element level.
	 * 
	 * @param element     the PCMM element to compute progress for
	 * @param selectedTag the current tag
	 * @param mode        the PCMM mode
	 * @return the assess progress
	 * @throws CredibilityException if an error occured
	 */
	int computeAssessProgress(PCMMElement element, Tag selectedTag, PCMMMode mode) throws CredibilityException;

	/**
	 * @param configuration the PCMM configuration
	 * @return the max progress value depending of the configuration
	 */
	int computeMaxProgress(PCMMSpecification configuration);

	/**
	 * Compute the maximum evidence progress by PCMM element depending of the PCMM
	 * mode:
	 * 
	 * PCMMMode.DEFAULT: in this mode we search the evidence for each subelement.
	 * 
	 * PCMMMode.SIMPLIFIED: in this mode we just search the pevidence at the element
	 * level.
	 * 
	 * @param element the PCMM element to compute progress for
	 * @param mode    the PCMM mode
	 * @return the max evidence progress
	 */
	int computeEvidenceMaxProgress(PCMMElement element, PCMMMode mode);

	/**
	 * Compute the maximum assess progress by PCMM element depending of the PCMM
	 * mode:
	 * 
	 * PCMMMode.DEFAULT: in this mode we search the assess for each subelement.
	 * 
	 * PCMMMode.SIMPLIFIED: in this mode we just search the assess at the element
	 * level.
	 * 
	 * @param element the PCMM element to compute progress for
	 * @param mode    the PCMM mode
	 * @return the max assess progress
	 */
	int computeAssessMaxProgress(PCMMElement element, PCMMMode mode);

	/**
	 * @param key the key to find
	 * @return the assessable pcmm element or subelement if found matching param
	 *         key, otherwise null.
	 */
	PCMMElement getElementFromKey(String key);

	/**
	 * @param key the key to find
	 * @return the assessable pcmm element or subelement if found matching param
	 *         key, otherwise null.
	 */
	PCMMSubelement getSubelementFromKey(String key);

	/**
	 * @param spec1 the first specification to check
	 * @param spec2 the second specification to check
	 * @return true if the PCMM configuration contains the same
	 */
	boolean sameConfiguration(PCMMSpecification spec1, PCMMSpecification spec2);

	/**
	 * Check if PCMM is available
	 * 
	 * @param model the model
	 * @return boolean True if PCMM is available
	 * @throws CredibilityException if a database error occurs
	 */
	boolean isPCMMEnabled(Model model) throws CredibilityException;

	/**
	 * @param evidence the evidence changed
	 * @return true if the file changed
	 */
	boolean evidenceChanged(PCMMEvidence evidence);

	/**
	 * Refresh the PCMM element
	 * 
	 * @param element the element to refresh
	 */
	void refreshElement(PCMMElement element);

	/**
	 * Refresh the subelement
	 * 
	 * @param subelement the subelement to refresh
	 */
	void refreshSubelement(PCMMSubelement subelement);

	/**
	 * Check if the evidence does not already exist
	 * 
	 * @param value      the evidence value
	 * @param section    the evidence section
	 * @param assessable the assessable
	 * @throws CredibilityException if the evidence already exists
	 */
	void checkEvidenceWithSamePathInAssessable(String value, String section, IAssessable assessable)
			throws CredibilityException;

}
