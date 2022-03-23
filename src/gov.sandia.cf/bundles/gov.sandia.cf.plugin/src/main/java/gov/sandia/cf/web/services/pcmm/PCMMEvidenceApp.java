/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.pcmm;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;

/**
 * Manage PCMM Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceApp extends AApplication implements IPCMMEvidenceApp {
	/**
	 * The constructor
	 */
	public PCMMEvidenceApp() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public PCMMEvidenceApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public List<PCMMEvidence> getActiveEvidenceList() throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMEvidence getEvidenceById(Integer id) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMEvidence> getAllEvidence() throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMEvidence> getEvidenceByTag(Tag tag) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMEvidence> getEvidenceByTag(List<Tag> tagList) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMEvidence> getEvidenceBy(Map<EntityFilter, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMEvidence addEvidence(PCMMEvidence evidence) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMEvidence updateEvidence(PCMMEvidence evidence) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteEvidence(PCMMEvidence evidence) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteEvidence(List<PCMMEvidence> evidenceList) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<PCMMEvidence> findDuplicateEvidenceByPath(PCMMEvidence evidence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMEvidence> findDuplicateEvidenceByPathAndSection(PCMMEvidence evidence) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int findEvidenceErrorNotification() throws CredibilityException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int findEvidenceWarningNotification() throws CredibilityException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<NotificationType, String> getDuplicatedEvidenceNotification(PCMMEvidence evidence, Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<NotificationType, List<String>> getEvidenceNotifications(PCMMEvidence evidence, Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<PCMMEvidence, Map<NotificationType, List<String>>> getAllEvidenceNotifications()
			throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean evidenceChanged(PCMMEvidence evidence) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reorderEvidence(PCMMEvidence toMove, int newIndex, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkEvidenceWithSamePathInAssessable(String value, String section, IAssessable assessable)
			throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public PCMMEvidence moveEvidence(PCMMEvidence evidence, String generatedId, IAssessable newParent)
			throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}
}
