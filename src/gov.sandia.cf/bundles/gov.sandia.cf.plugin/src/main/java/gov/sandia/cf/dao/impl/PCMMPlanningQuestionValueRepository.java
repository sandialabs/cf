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
import gov.sandia.cf.dao.IPCMMPlanningQuestionValueRepository;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.Tag;

/**
 * PCMMPlanningQuestionValueRepository entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningQuestionValueRepository extends AbstractCRUDRepository<PCMMPlanningQuestionValue, Integer>
		implements IPCMMPlanningQuestionValueRepository {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMPlanningQuestionValueRepository.class);

	/**
	 * Query find all active by parameter
	 */
	public static final String QUERY_FIND_ALL_BY_PARAMETER = "SELECT q FROM PCMMPlanningQuestionValue q WHERE q.parameter = :{0}"; //$NON-NLS-1$

	/**
	 * Query find all active by element in parameter
	 */
	public static final String QUERY_FIND_ALL_ACTIVE_BY_ELEMENT_IN_PARAMETER = "SELECT q FROM PCMMPlanningQuestionValue q WHERE q.parameter.element = :{0} and q.tag IS NULL"; //$NON-NLS-1$

	/**
	 * Query find all active by element in parameter
	 */
	public static final String QUERY_FIND_ALL_BY_ELEMENT_IN_PARAMETER = "SELECT q FROM PCMMPlanningQuestionValue q WHERE q.parameter.element = :{0} and q.tag = :{1}"; //$NON-NLS-1$

	/**
	 * Query find all active by element in subelement in parameter
	 */
	public static final String QUERY_FIND_ALL_ACTIVE_BY_ELEMENT_IN_SUBELT_IN_PARAMETER = "SELECT q FROM PCMMPlanningQuestionValue q WHERE q.parameter.subelement.element = :{0} and q.tag IS NULL"; //$NON-NLS-1$

	/**
	 * Query find all active by element in subelement in parameter
	 */
	public static final String QUERY_FIND_ALL_BY_ELEMENT_IN_SUBELT_IN_PARAMETER = "SELECT q FROM PCMMPlanningQuestionValue q WHERE q.parameter.subelement.element = :{0} and q.tag = :{1}"; //$NON-NLS-1$

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public PCMMPlanningQuestionValueRepository() {
		super(PCMMPlanningQuestionValue.class);
	}

	/**
	 * PCMMPlanningQuestionValueRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMPlanningQuestionValueRepository(EntityManager entityManager) {
		super(entityManager, PCMMPlanningQuestionValue.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningQuestionValue> findByQuestion(PCMMPlanningQuestion question) {

		// check params
		if (question == null) {
			logger.error("param question is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		List<PCMMPlanningQuestionValue> returnedList = null;

		String param = "question"; //$NON-NLS-1$
		TypedQuery<PCMMPlanningQuestionValue> query = getEntityManager()
				.createQuery(MessageFormat.format(QUERY_FIND_ALL_BY_PARAMETER, param), PCMMPlanningQuestionValue.class);
		query.setParameter(param, question);
		returnedList = query.getResultList();

		return returnedList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningQuestionValue> findByElement(PCMMElement element, Tag selectedTag) {

		// check params
		if (element == null) {
			logger.error("param element is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		List<PCMMPlanningQuestionValue> returnedList = null;

		String param = "element"; //$NON-NLS-1$
		if (selectedTag == null) {
			TypedQuery<PCMMPlanningQuestionValue> query = getEntityManager().createQuery(
					MessageFormat.format(QUERY_FIND_ALL_ACTIVE_BY_ELEMENT_IN_PARAMETER, param),
					PCMMPlanningQuestionValue.class);
			query.setParameter(param, element);
			returnedList = query.getResultList();
		} else {
			String paramTag = "tag"; //$NON-NLS-1$
			TypedQuery<PCMMPlanningQuestionValue> query = getEntityManager().createQuery(
					MessageFormat.format(QUERY_FIND_ALL_BY_ELEMENT_IN_PARAMETER, param, paramTag),
					PCMMPlanningQuestionValue.class);
			query.setParameter(param, element);
			query.setParameter(paramTag, selectedTag);
			returnedList = query.getResultList();
		}

		return returnedList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningQuestionValue> findByElementInSubelement(PCMMElement element, Tag selectedTag) {

		// check params
		if (element == null) {
			logger.error("param element is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		List<PCMMPlanningQuestionValue> returnedList = null;

		String param = "element"; //$NON-NLS-1$
		if (selectedTag == null) {
			TypedQuery<PCMMPlanningQuestionValue> query = getEntityManager().createQuery(
					MessageFormat.format(QUERY_FIND_ALL_ACTIVE_BY_ELEMENT_IN_SUBELT_IN_PARAMETER, param),
					PCMMPlanningQuestionValue.class);
			query.setParameter(param, element);
			returnedList = query.getResultList();
		} else {
			String paramTag = "tag"; //$NON-NLS-1$
			TypedQuery<PCMMPlanningQuestionValue> query = getEntityManager().createQuery(
					MessageFormat.format(QUERY_FIND_ALL_BY_ELEMENT_IN_SUBELT_IN_PARAMETER, param, paramTag),
					PCMMPlanningQuestionValue.class);
			query.setParameter(param, element);
			query.setParameter(paramTag, selectedTag);
			returnedList = query.getResultList();
		}

		return returnedList;
	}
}