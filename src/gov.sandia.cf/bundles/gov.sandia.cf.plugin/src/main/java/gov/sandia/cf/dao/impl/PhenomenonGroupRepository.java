/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPhenomenonGroupRepository;
import gov.sandia.cf.model.PhenomenonGroup;

/**
 * Model entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PhenomenonGroupRepository extends AbstractCRUDRepository<PhenomenonGroup, Integer>
		implements IPhenomenonGroupRepository {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PhenomenonGroupRepository.class);

	/**
	 * Query find by qoi id
	 */
	public static final String QUERY_FIND_BY_QOIID = "SELECT g FROM PhenomenonGroup g WHERE g.qoi IS NOT NULL AND g.qoi.id = :"; //$NON-NLS-1$

	/**
	 * Query find by qoi id list
	 */
	public static final String QUERY_FIND_BY_QOIIDLIST = "SELECT g FROM PhenomenonGroup g WHERE g.qoi IS NOT NULL AND g.qoi.id IN :"; //$NON-NLS-1$

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PhenomenonGroupRepository() {
		super(PhenomenonGroup.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 * 
	 */
	public PhenomenonGroupRepository(EntityManager entityManager) {
		super(entityManager, PhenomenonGroup.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhenomenonGroup> findByQoiId(Integer qoiId) {

		// check qoiId param
		if (qoiId == null) {
			logger.error("param qoiId is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		String paramQoiID = "qoiID"; //$NON-NLS-1$

		TypedQuery<PhenomenonGroup> query = getEntityManager().createQuery(QUERY_FIND_BY_QOIID + paramQoiID,
				PhenomenonGroup.class);
		query.setParameter(paramQoiID, qoiId);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhenomenonGroup> findByQoiIdList(List<Integer> qoiIdList) {

		// check qoiIdList param
		if (qoiIdList == null || qoiIdList.isEmpty()) {
			logger.error("param qoiIdList is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		String paramQoiIDList = "qoiIdList"; //$NON-NLS-1$

		TypedQuery<PhenomenonGroup> query = getEntityManager().createQuery(QUERY_FIND_BY_QOIIDLIST + paramQoiIDList,
				PhenomenonGroup.class);
		query.setParameter(paramQoiIDList, qoiIdList);
		return query.getResultList();
	}

}