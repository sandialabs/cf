/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMPlanningValueRepository;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.Tag;

/**
 * PCMMPlanningValueRepository entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningValueRepository extends AbstractCRUDRepository<PCMMPlanningValue, Integer>
		implements IPCMMPlanningValueRepository {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMPlanningValueRepository.class);

	/**
	 * Query find all active by element in subelement
	 */
	public static final String QUERY_FIND_ALL_ACTIVE_BY_ELEMENT_IN_SUBELT = "SELECT v FROM PCMMPlanningValue v WHERE v.subelement.element = :{0} AND v.tag IS NULL"; //$NON-NLS-1$

	/**
	 * Query find all active by element in subelement
	 */
	public static final String QUERY_FIND_ALL_BY_ELEMENT_IN_SUBELT = "SELECT v FROM PCMMPlanningValue v WHERE v.subelement.element = :{0} AND v.tag = :{1}"; //$NON-NLS-1$

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public PCMMPlanningValueRepository() {
		super(PCMMPlanningValue.class);
	}

	/**
	 * PCMMPlanningParameterRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMPlanningValueRepository(EntityManager entityManager) {
		super(entityManager, PCMMPlanningValue.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningValue> findByElementInSubelement(PCMMElement element, Tag selectedTag) {

		// check params
		if (element == null) {
			logger.error("param element is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		List<PCMMPlanningValue> returnedList = null;

		String param = "element"; //$NON-NLS-1$
		if (selectedTag == null) {
			TypedQuery<PCMMPlanningValue> query = getEntityManager().createQuery(
					MessageFormat.format(QUERY_FIND_ALL_ACTIVE_BY_ELEMENT_IN_SUBELT, param), PCMMPlanningValue.class);
			query.setParameter(param, element);
			returnedList = query.getResultList();
		} else {
			String paramTag = "tag"; //$NON-NLS-1$
			TypedQuery<PCMMPlanningValue> query = getEntityManager().createQuery(
					MessageFormat.format(QUERY_FIND_ALL_BY_ELEMENT_IN_SUBELT, param, paramTag),
					PCMMPlanningValue.class);
			query.setParameter(param, element);
			query.setParameter(paramTag, selectedTag);
			returnedList = query.getResultList();
		}
		
		return returnedList;
	}

}