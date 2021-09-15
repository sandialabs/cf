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
import gov.sandia.cf.dao.IPCMMPlanningQuestionRepository;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningQuestion;

/**
 * PCMMPlanningQuestionRepository entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningQuestionRepository extends AbstractCRUDRepository<PCMMPlanningQuestion, Integer>
		implements IPCMMPlanningQuestionRepository {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMPlanningQuestionRepository.class);

	/**
	 * Query find all by element in subelement
	 */
	public static final String QUERY_FIND_ALL_ACTIVE_BY_ELEMENT_IN_SUBELEMENT = "SELECT q FROM PCMMPlanningQuestion q WHERE q.subelement IS NOT NULL AND q.subelement.element = :{0}"; //$NON-NLS-1$

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public PCMMPlanningQuestionRepository() {
		super(PCMMPlanningQuestion.class);
	}

	/**
	 * PCMMPlanningQuestionRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMPlanningQuestionRepository(EntityManager entityManager) {
		super(entityManager, PCMMPlanningQuestion.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningQuestion> findByElementInSubelement(PCMMElement elt) {

		// check params
		if (elt == null) {
			logger.error("param elt is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		List<PCMMPlanningQuestion> returned = null;

		String paramElt = "elt"; //$NON-NLS-1$
		TypedQuery<PCMMPlanningQuestion> query = getEntityManager().createQuery(
				MessageFormat.format(QUERY_FIND_ALL_ACTIVE_BY_ELEMENT_IN_SUBELEMENT, paramElt),
				PCMMPlanningQuestion.class);
		query.setParameter(paramElt, elt);
		returned = query.getResultList();

		return returned;
	}
}