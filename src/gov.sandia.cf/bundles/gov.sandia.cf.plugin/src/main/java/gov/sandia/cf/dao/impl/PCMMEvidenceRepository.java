/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMEvidenceRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.Tag;

/**
 * Model entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceRepository extends AbstractCRUDRepository<PCMMEvidence, Integer>
		implements IPCMMEvidenceRepository {

	private static final Logger logger = LoggerFactory.getLogger(PCMMEvidenceRepository.class);
	/**
	 * Query find all active
	 */
	public static final String QUERY_FIND_ALL_ACTIVE = "SELECT e FROM PCMMEvidence e WHERE e.tag IS NULL"; //$NON-NLS-1$

	/**
	 * Query find by subelement
	 */
	public static final String QUERY_FIND_BY_SUBELEMENT = "SELECT e FROM PCMMEvidence e WHERE e.subelement = :"; //$NON-NLS-1$

	/**
	 * Query find by tag
	 */
	public static final String QUERY_FIND_BY_TAG = "SELECT e FROM PCMMEvidence e WHERE e.tag = :"; //$NON-NLS-1$

	/**
	 * Query find number of duplicates
	 */
	public static final String QUERY_FIND_NUMBER_DUPLICATES = "SELECT e.path, count(e.path) FROM PCMMEvidence e GROUP BY e.path HAVING count(e.path) > 1"; //$NON-NLS-1$

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PCMMEvidenceRepository() {
		super(PCMMEvidence.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMEvidenceRepository(EntityManager entityManager) {
		super(entityManager, PCMMEvidence.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> findAllActive() {
		TypedQuery<PCMMEvidence> query = getEntityManager().createQuery(QUERY_FIND_ALL_ACTIVE, PCMMEvidence.class);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> findByTag(Tag tag) {

		List<PCMMEvidence> evidenceList = null;

		if (tag == null) {
			TypedQuery<PCMMEvidence> query = getEntityManager().createQuery(QUERY_FIND_ALL_ACTIVE, PCMMEvidence.class);
			evidenceList = query.getResultList();
		} else {

			String paramTag = "tag"; //$NON-NLS-1$

			TypedQuery<PCMMEvidence> query = getEntityManager().createQuery(QUERY_FIND_BY_TAG + paramTag,
					PCMMEvidence.class);
			query.setParameter(paramTag, tag);
			evidenceList = query.getResultList();
		}
		return evidenceList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean clearEvidencePath() {

		boolean wasDirty = false;

		for (PCMMEvidence evidence : findAll()) {
			if (evidence.getPath() != null && evidence.getPath().contains("\\")) { //$NON-NLS-1$

				// clear path
				String newPath = evidence.getPath().replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
				if (FormFieldType.LINK_FILE.equals(evidence.getType())) {
					evidence.setFilePath(newPath);
				} else if (FormFieldType.LINK_URL.equals(evidence.getType())) {
					evidence.setURL(newPath);
				}

				// update
				try {
					update(evidence);
					wasDirty = true;
				} catch (CredibilityException e) {
					logger.error("Clear evidence path: impossible to update the evidence: {} with path:{}", evidence, //$NON-NLS-1$
							newPath, e);
				}
			}
		}

		return wasDirty;
	}
}
