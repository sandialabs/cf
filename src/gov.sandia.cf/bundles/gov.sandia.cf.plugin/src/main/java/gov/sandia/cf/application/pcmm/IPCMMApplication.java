/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.pcmm;

import java.util.List;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;

/**
 * Interface to manage PCMM Application methods
 * 
 * @author Didier Verstraete
 *
 */
@Service
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
	 * Refresh assessable.
	 *
	 * @param element the element to refresh
	 */
	void refreshAssessable(IAssessable element);

}
