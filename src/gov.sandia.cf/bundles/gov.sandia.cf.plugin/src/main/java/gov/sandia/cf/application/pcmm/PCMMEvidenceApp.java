/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.pcmm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.dao.IPCMMEvidenceRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.comparator.StringWithNumberAndNullableComparator;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.IDTools;
import gov.sandia.cf.tools.NetTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage PCMM Evidence Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceApp extends AApplication implements IPCMMEvidenceApp {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMEvidenceApp.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> getActiveEvidenceList() throws CredibilityException {
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findAllActive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMEvidence getEvidenceById(Integer id) throws CredibilityException {

		// check parameters
		if (id == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_GETEVIDENCEBYID_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> getAllEvidence() throws CredibilityException {
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> getEvidenceByTag(Tag tag) throws CredibilityException {
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findByTag(tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> getEvidenceByTag(List<Tag> tagList) throws CredibilityException {

		List<PCMMEvidence> pcmmEvidenceList = new ArrayList<>();
		if (tagList != null) {
			for (Tag tag : tagList) {
				pcmmEvidenceList.addAll(getDaoManager().getRepository(IPCMMEvidenceRepository.class).findByTag(tag));
			}
		}

		return pcmmEvidenceList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> getEvidenceBy(Map<EntityFilter, Object> filters) {

		// avoid character encoding error
		if (filters != null && filters.containsKey(PCMMEvidence.Filter.VALUE)) {
			String path = (String) filters.get(PCMMEvidence.Filter.VALUE);
			if (path != null) {
				filters.put(PCMMEvidence.Filter.VALUE, path.replace("\\", "/"));//$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMEvidence addEvidence(PCMMEvidence evidence) throws CredibilityException {

		// check parameters
		if (evidence == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDEVIDENCE_EVIDENCENULL));
		} else if (FormFieldType.LINK_FILE.equals(evidence.getType())
				&& !FileTools.isPathValidInWorkspace(evidence.getPath())) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDPATH, evidence.getPath()));
		} else if (FormFieldType.LINK_URL.equals(evidence.getType()) && (evidence.getPath() == null
				|| evidence.getPath().isEmpty() || !NetTools.isValidURL(evidence.getPath()))) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDURL, evidence.getPath()));
		} else if (evidence.getElement() == null && evidence.getSubelement() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_NOASSESSABLE));
		} else if (evidence.getElement() != null && evidence.getSubelement() != null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_MORETHANONEASSESSABLE));
		}

		// check if evidence already exists for the same PCMM element/subelement
		checkEvidenceWithSamePathInAssessable(evidence);

		// set creation data
		evidence.setDateCreation(DateTools.getCurrentDate());

		// set path
		if (evidence.getPath() != null && FormFieldType.LINK_FILE.equals(evidence.getType())) {
			evidence.setFilePath(evidence.getPath().replace("\\", "/")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// set date file
		if (evidence.getDateFile() == null && FormFieldType.LINK_FILE.equals(evidence.getType())) {
			evidence.setDateFile(FileTools.getLastUpdatedDate(new Path(evidence.getPath())));
		}

		// set file name
		if (evidence.getName() == null && FormFieldType.LINK_FILE.equals(evidence.getType())) {
			String[] split = evidence.getPath().split("/"); //$NON-NLS-1$
			evidence.setName(split[split.length - 1]);
		}
		// create
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).create(evidence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkEvidenceWithSamePathInAssessable(String value, String section, IAssessable assessable)
			throws CredibilityException {

		// check if it is a url
		checkEvidenceURLWithSamePathInAssessable(value, section, assessable);

		// check if it is a file
		checkEvidenceFilepathWithSamePathInAssessable(value, section, assessable);
	}

	/**
	 * Check if the evidence does not already exist.
	 *
	 * @param url      the url to find
	 * @param section  the section to find
	 * @param assessable the assessable
	 * @throws CredibilityException if the evidence already exists
	 */
	private void checkEvidenceURLWithSamePathInAssessable(String url, String section, IAssessable assessable)
			throws CredibilityException {
		PCMMEvidence evidence = new PCMMEvidence();
		evidence.setURL(url);
		evidence.setSection(section);
		if (assessable instanceof PCMMElement) {
			evidence.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			evidence.setSubelement((PCMMSubelement) assessable);
		}
		checkEvidenceWithSamePathInAssessable(evidence);
	}

	/**
	 * Check if the evidence does not already exist.
	 *
	 * @param filepath the filepath to find
	 * @param section  the section to find
	 * @param assessable the assessable
	 * @throws CredibilityException if the evidence already exists
	 */
	private void checkEvidenceFilepathWithSamePathInAssessable(String filepath, String section, IAssessable assessable)
			throws CredibilityException {
		PCMMEvidence evidence = new PCMMEvidence();
		evidence.setFilePath(filepath);
		evidence.setSection(section);
		if (assessable instanceof PCMMElement) {
			evidence.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			evidence.setSubelement((PCMMSubelement) assessable);
		}
		checkEvidenceWithSamePathInAssessable(evidence);
	}

	/**
	 * Check if the evidence does not already exist
	 * 
	 * @param evidence the evidence to check
	 * @throws CredibilityException if the evidence already exists
	 */
	private void checkEvidenceWithSamePathInAssessable(PCMMEvidence evidence) throws CredibilityException {

		// find duplicated evidence
		List<PCMMEvidence> listEvidenceWithSamePath = findDuplicateEvidenceByPathAndSection(evidence);

		if (listEvidenceWithSamePath == null || listEvidenceWithSamePath.isEmpty()) {
			return;
		}

		String strListEvidence = null;
		List<PCMMEvidence> evidenceFound = null;

		if (evidence.getElement() != null) {
			// Get PCMM Evidences with same path in element
			evidenceFound = listEvidenceWithSamePath.stream().filter(Objects::nonNull)
					.filter(evidenceWithSamePath -> evidenceWithSamePath.getElement() != null
							&& evidenceWithSamePath.getElement().getId().equals(evidence.getElement().getId()))
					.filter(evidenceTmp -> evidenceTmp.getId() != null && !evidenceTmp.getId().equals(evidence.getId()))
					.collect(Collectors.toList());

			strListEvidence = evidenceFound.stream().map(evid -> evid.getElement().getName())
					.collect(Collectors.joining(RscTools.COMMA));

		} else if (evidence.getSubelement() != null) {
			// Get PCMM Evidences with same path in sub-element
			evidenceFound = listEvidenceWithSamePath.stream().filter(Objects::nonNull)
					.filter(evidenceWithSamePath -> evidenceWithSamePath.getSubelement() != null
							&& evidenceWithSamePath.getSubelement().getId().equals(evidence.getSubelement().getId()))
					.filter(evidenceTmp -> evidenceTmp.getId() != null && !evidenceTmp.getId().equals(evidence.getId()))
					.collect(Collectors.toList());

			strListEvidence = evidenceFound.stream().map(evid -> evid.getSubelement().getName())
					.collect(Collectors.joining(RscTools.COMMA));
		}

		// Check has PCMM Evidences with same path in sub-element
		if (evidenceFound != null && !evidenceFound.isEmpty()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDEVIDENCE_ALREADYEXISTS,
					evidence.getPath(), strListEvidence));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMEvidence moveEvidence(final PCMMEvidence evidence, String generatedId, IAssessable newParent)
			throws CredibilityException {

		// check parameters
		if (evidence == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEEVIDENCE_ELTNULL));
		} else if (evidence.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEEVIDENCE_IDNULL));
		} else if (evidence.getElement() == null && evidence.getSubelement() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_NOASSESSABLE));
		} else if (evidence.getElement() != null && evidence.getSubelement() != null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_MORETHANONEASSESSABLE));
		}

		PCMMEvidence evidenceFound = getDaoManager().getRepository(IPCMMEvidenceRepository.class)
				.findById(evidence.getId());

		if (evidenceFound == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_NOTFOUND, evidence.getId()));
		}

		// check evidence path already exists for the same pcmm element/subelement
		checkEvidenceWithSamePathInAssessable(evidenceFound.getValue(), evidenceFound.getSection(), newParent);

		// set generated id
		evidenceFound.setGeneratedId(generatedId);

		// set parent
		if (newParent instanceof PCMMElement) {
			evidenceFound.setElement((PCMMElement) newParent);
		} else if (newParent instanceof PCMMSubelement) {
			evidenceFound.setSubelement((PCMMSubelement) newParent);
		}

		// set the update date
		evidenceFound.setDateUpdate(DateTools.getCurrentDate());

		// set date file
		if (evidenceFound.getDateFile() == null && FormFieldType.LINK_FILE.equals(evidenceFound.getType())) {
			evidenceFound.setDateFile(FileTools.getLastUpdatedDate(new Path(evidenceFound.getPath())));
		}

		// update
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).update(evidenceFound);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMEvidence updateEvidence(PCMMEvidence evidence) throws CredibilityException {

		// check parameters
		if (evidence == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEEVIDENCE_ELTNULL));
		} else if (evidence.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEEVIDENCE_IDNULL));
		} else if (FormFieldType.LINK_FILE.equals(evidence.getType())
				&& !FileTools.isPathValidInWorkspace(evidence.getPath())) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDPATH, evidence.getPath()));
		} else if (FormFieldType.LINK_URL.equals(evidence.getType()) && (evidence.getPath() == null
				|| evidence.getPath().isEmpty() || !NetTools.isValidURL(evidence.getPath()))) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDURL, evidence.getPath()));
		} else if (evidence.getElement() == null && evidence.getSubelement() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_NOASSESSABLE));
		} else if (evidence.getElement() != null && evidence.getSubelement() != null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_MORETHANONEASSESSABLE));
		}

		// check evidence path already exists for the same pcmm element/subelement
		checkEvidenceWithSamePathInAssessable(evidence);

		// set the update date
		evidence.setDateUpdate(DateTools.getCurrentDate());

		// set date file
		if (evidence.getDateFile() == null && FormFieldType.LINK_FILE.equals(evidence.getType())) {
			evidence.setDateFile(FileTools.getLastUpdatedDate(new Path(evidence.getPath())));
		}

		// update
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).update(evidence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEvidence(PCMMEvidence evidence) throws CredibilityException {

		// check parameters
		if (evidence == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEEVIDENCE_ELTNULL));
		} else if (evidence.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEEVIDENCE_IDNULL));
		}

		getDaoManager().getRepository(IPCMMEvidenceRepository.class).delete(evidence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEvidence(List<PCMMEvidence> evidenceList) throws CredibilityException {
		if (evidenceList != null) {
			for (PCMMEvidence evidence : evidenceList) {
				deleteEvidence(evidence);
			}
		} else {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEEVIDENCE_ELTNULL));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<NotificationType, String> getDuplicatedEvidenceNotification(PCMMEvidence evidence,
			final Integer evidenceId) {

		// Initialize
		boolean isError = false;
		boolean hasNotification = false;
		StringBuilder evidenceToString = new StringBuilder();
		Map<NotificationType, String> notification = new EnumMap<>(NotificationType.class);

		// check if evidence already exists with same path and/or section
		Set<PCMMEvidence> listEvidenceWithSamePath = new HashSet<>(findDuplicateEvidenceByPathAndSection(evidence));

		// check for error
		isError = listEvidenceWithSamePath.stream().filter(Objects::nonNull)
				.filter(tmp -> !tmp.getId().equals(evidenceId))
				.anyMatch(tmp -> (tmp.getElement() != null && evidence.getElement() != null
						&& tmp.getElement().getId().equals(evidence.getElement().getId()))
						|| (tmp.getSubelement() != null && evidence.getSubelement() != null
								&& tmp.getSubelement().getId().equals(evidence.getSubelement().getId())));

		// build the notification string
		for (PCMMEvidence evidenceWithSamePath : listEvidenceWithSamePath) {
			if (!evidenceWithSamePath.getId().equals(evidenceId)) {
				if (evidenceWithSamePath.getElement() != null) {
					hasNotification = true;
					evidenceToString.append("<li>").append(evidenceWithSamePath.getElement().getName()) //$NON-NLS-1$
							.append("</li>"); //$NON-NLS-1$

				} else if (evidenceWithSamePath.getSubelement() != null) {
					hasNotification = true;
					evidenceToString.append("<li>").append(evidenceWithSamePath.getSubelement().getName()) //$NON-NLS-1$
							.append("</li>"); //$NON-NLS-1$
				}
			}
		}

		// Manage Error / Warning
		String message = org.apache.commons.lang3.StringUtils.isBlank(evidence.getSection())
				? RscTools.getString(RscConst.NOTIFICATION_PCMM_EVIDENCE_WARN_DUPLICATE_PATH_NOSECTION,
						evidence.getPath(), evidenceToString.toString())
				: RscTools.getString(RscConst.NOTIFICATION_PCMM_EVIDENCE_WARN_DUPLICATE_PATH, evidence.getPath(),
						evidence.getSection(), evidenceToString.toString());
		if (isError) {
			notification.put(NotificationType.ERROR, message);
		} else if (hasNotification) {
			notification.put(NotificationType.WARN, message);
		}

		// Return
		return notification;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<NotificationType, List<String>> getEvidenceNotifications(PCMMEvidence evidence,
			final Integer evidenceId) {

		// Initialize
		Map<NotificationType, List<String>> notifications = new EnumMap<>(NotificationType.class);
		notifications.put(NotificationType.ERROR, new ArrayList<>());
		notifications.put(NotificationType.WARN, new ArrayList<>());

		// Get duplicate warnings
		Map<NotificationType, String> duplicateNotification = getDuplicatedEvidenceNotification(evidence, evidenceId);
		if (duplicateNotification.containsKey(NotificationType.ERROR)) {
			notifications.get(NotificationType.ERROR).add(duplicateNotification.get(NotificationType.ERROR));
		} else if (duplicateNotification.containsKey(NotificationType.WARN)) {
			notifications.get(NotificationType.WARN).add(duplicateNotification.get(NotificationType.WARN));
		}

		// File not exists error
		if (FormFieldType.LINK_FILE.equals(evidence.getType())) {
			if (!Boolean.TRUE.equals(FileTools.filePathExist(evidence.getPath()))) {
				notifications.get(NotificationType.ERROR).add(RscTools
						.getString(RscConst.NOTIFICATION_PCMM_EVIDENCE_ERR_FILE_NOT_EXISTS, evidence.getName()));
			} else if (evidenceChanged(evidence)) {
				// Get updated file warnings
				notifications.get(NotificationType.WARN).add(
						RscTools.getString(RscConst.NOTIFICATION_PCMM_EVIDENCE_WARN_UPDATED_FILE, evidence.getName()));
			}
		}

		// Return
		return notifications;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean evidenceChanged(PCMMEvidence evidence) {
		return evidence != null && FormFieldType.LINK_FILE.equals(evidence.getType()) && null != evidence.getDateFile()
				&& !evidence.getDateFile().equals(FileTools.getLastUpdatedDate(new Path(evidence.getPath())));
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<PCMMEvidence, Map<NotificationType, List<String>>> getAllEvidenceNotifications()
			throws CredibilityException {
		// Initialize
		Map<PCMMEvidence, Map<NotificationType, List<String>>> notifications = new HashMap<>();

		// For each evidence
		List<PCMMEvidence> evidences = getAllEvidence();
		if (null != evidences && !evidences.isEmpty()) {
			evidences.stream().filter(evidence -> evidence != null && evidence.getTag() == null).forEach(evidence -> {
				// Get notification for this evidence
				Map<NotificationType, List<String>> evidenceNotifications = getEvidenceNotifications(evidence,
						evidence.getId());
				notifications.put(evidence, evidenceNotifications);
			});
		}

		// Return
		return notifications;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<PCMMEvidence> findDuplicateEvidenceByPath(PCMMEvidence evidence) {

		if (evidence == null) {
			return new ArrayList<>();
		}

		Map<EntityFilter, Object> filter = new HashMap<>();
		filter.put(PCMMEvidence.Filter.TAG, evidence.getTag());
		filter.put(PCMMEvidence.Filter.VALUE, evidence.getValue());
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findBy(filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<PCMMEvidence> findDuplicateEvidenceByPathAndSection(PCMMEvidence evidence) {

		if (evidence == null) {
			return new ArrayList<>();
		}

		boolean testNull = false;

		String evidenceValue = evidence.getValue();
		Tag tag = evidence.getTag();
		String section = evidence.getSection();
		if (org.apache.commons.lang3.StringUtils.isBlank(section)) {
			section = RscTools.empty();
			testNull = true;
		}

		Map<EntityFilter, Object> filter = new HashMap<>();
		filter.put(PCMMEvidence.Filter.TAG, tag);
		filter.put(PCMMEvidence.Filter.VALUE, evidenceValue);
		filter.put(PCMMEvidence.Filter.SECTION, section);
		List<PCMMEvidence> listEvidenceWithSamePathAndSection = getEvidenceBy(filter);

		// test to get the section with null
		if (testNull) {
			filter = new HashMap<>();
			filter.put(PCMMEvidence.Filter.TAG, tag);
			filter.put(PCMMEvidence.Filter.VALUE, evidenceValue);
			filter.put(PCMMEvidence.Filter.SECTION, null);
			listEvidenceWithSamePathAndSection.addAll(getEvidenceBy(filter));
		}

		return listEvidenceWithSamePathAndSection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public int findEvidenceErrorNotification() throws CredibilityException {
		// Initialize
		int count = 0;

		// Get all notifications
		Map<PCMMEvidence, Map<NotificationType, List<String>>> evidencesNotifications = getAllEvidenceNotifications();
		for (Map.Entry<PCMMEvidence, Map<NotificationType, List<String>>> entry : evidencesNotifications.entrySet()) {

			// Get error notifications
			// Increment count if not empty
			if (entry.getValue().containsKey(NotificationType.ERROR)
					&& !entry.getValue().get(NotificationType.ERROR).isEmpty()) {
				count += entry.getValue().get(NotificationType.ERROR).size();
			}
		}

		// Result
		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	public int findEvidenceWarningNotification() throws CredibilityException {
		// Initialize
		int count = 0;

		// Get all notifications
		Map<PCMMEvidence, Map<NotificationType, List<String>>> evidencesNotifications = getAllEvidenceNotifications();
		for (Map.Entry<PCMMEvidence, Map<NotificationType, List<String>>> entry : evidencesNotifications.entrySet()) {

			// Get error notifications
			// Increment count if not empty
			if (entry.getValue().containsKey(NotificationType.WARN)
					&& !entry.getValue().get(NotificationType.WARN).isEmpty()) {
				count += entry.getValue().get(NotificationType.WARN).size();
			}
		}

		// Result
		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reorderEvidence(PCMMEvidence toMove, int newIndex, User user) throws CredibilityException {

		logger.debug("Reordering evidence {} to index {}", toMove, newIndex); //$NON-NLS-1$

		int startPosition = IDTools.reverseGenerateAlphabeticIdRecursive(IDTools.ALPHABET.get(0));

		if (toMove == null) {
			return;
		}

		PCMMElement element = toMove.getElement();
		PCMMSubelement subelement = toMove.getSubelement();

		if (newIndex < startPosition) {
			newIndex = startPosition;
		}

		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.ELEMENT, element);
		filters.put(PCMMEvidence.Filter.SUBELEMENT, subelement);
		List<PCMMEvidence> sameAssessableEvidenceList = getEvidenceBy(filters);

		if (sameAssessableEvidenceList == null) {
			return;
		}

		// construct data
		sameAssessableEvidenceList
				.sort(Comparator.comparing(PCMMEvidence::getGeneratedId, new StringWithNumberAndNullableComparator()));

		// reorder
		List<PCMMEvidence> reorderedList = IDTools.reorderList(sameAssessableEvidenceList, toMove, newIndex);

		// set generated id
		int index = 1;
		for (PCMMEvidence evidence : reorderedList) {
			evidence.setGeneratedId(String.valueOf(index));
			getDaoManager().getRepository(IPCMMEvidenceRepository.class).update(evidence);
			index++;
		}

		// refresh
		getAppMgr().getService(IPCMMApplication.class).refreshElement(element);
		getAppMgr().getService(IPCMMApplication.class).refreshSubelement(subelement);
	}
}
