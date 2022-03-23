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
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;

/**
 * Interface to manage PCMM Evidence Application methods
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IPCMMEvidenceApp extends IApplication {

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
	 * @param evidence the evidence changed
	 * @return true if the file changed
	 */
	boolean evidenceChanged(PCMMEvidence evidence);

	/**
	 * Reorder evidence.
	 *
	 * @param toMove   the to move
	 * @param newIndex the new index
	 * @param user     the user
	 * @throws CredibilityException the credibility exception
	 */
	void reorderEvidence(PCMMEvidence toMove, int newIndex, User user) throws CredibilityException;

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

	/**
	 * Move evidence.
	 *
	 * @param evidence the evidence
	 * @param generatedId the generated id
	 * @param newParent the new parent
	 * @return the PCMM evidence
	 * @throws CredibilityException the credibility exception
	 */
	PCMMEvidence moveEvidence(PCMMEvidence evidence, String generatedId, IAssessable newParent)
			throws CredibilityException;
}
