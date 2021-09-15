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
import gov.sandia.cf.dao.IPCMMElementRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;

/**
 * PCMMEvidence entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMElementRepository extends AbstractCRUDRepository<PCMMElement, Integer>
		implements IPCMMElementRepository {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMElementRepository.class);

	/**
	 * Query find by model
	 */
	public static final String QUERY_FIND_BY_MODEL = "SELECT e FROM PCMMElement e WHERE e.model = :"; //$NON-NLS-1$

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PCMMElementRepository() {
		super(PCMMElement.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMElementRepository(EntityManager entityManager) {
		super(entityManager, PCMMElement.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMElement> findByModel(Model model) {

		// check model param
		if (model == null) {
			logger.error("param model is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		String parameterModel = "model"; //$NON-NLS-1$

		TypedQuery<PCMMElement> query = getEntityManager().createQuery(QUERY_FIND_BY_MODEL + parameterModel,
				PCMMElement.class);
		query.setParameter(parameterModel, model);
		return query.getResultList();
	}

}