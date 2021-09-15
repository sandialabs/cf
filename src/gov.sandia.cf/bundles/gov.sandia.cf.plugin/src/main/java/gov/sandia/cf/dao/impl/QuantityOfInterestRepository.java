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
import gov.sandia.cf.dao.IQuantityOfInterestRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QuantityOfInterest;

/**
 * Model entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class QuantityOfInterestRepository extends AbstractCRUDRepository<QuantityOfInterest, Integer>
		implements IQuantityOfInterestRepository {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(QuantityOfInterestRepository.class);

	/**
	 * Query find by model
	 */
	public static final String QUERY_FIND_BY_MODEL = "SELECT q FROM QuantityOfInterest q WHERE q.model = :"; //$NON-NLS-1$

	/**
	 * Query find by model
	 */
	public static final String QUERY_FIND_ROOT_BY_MODEL = "SELECT q FROM QuantityOfInterest q WHERE q.parent IS NULL AND q.model = :"; //$NON-NLS-1$

	/**
	 * Query find by model id
	 */
	public static final String QUERY_FIND_BY_MODELID = "SELECT q.id FROM QuantityOfInterest q WHERE q.model IS NOT NULL AND q.model.id = :"; //$NON-NLS-1$

	/**
	 * model parameter
	 */
	public static final String PARAM_MODEL = "model"; //$NON-NLS-1$

	/**
	 * Message logged when param model is empty or null
	 */
	public static final String PARAM_MODEL_EMPTY_ERROR = "param model is empty or null"; //$NON-NLS-1$

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public QuantityOfInterestRepository() {
		super(QuantityOfInterest.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 * 
	 */
	public QuantityOfInterestRepository(EntityManager entityManager) {
		super(entityManager, QuantityOfInterest.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QuantityOfInterest> findByModelNotTagged(Model model) {

		// check model parameter
		if (model == null) {
			logger.error(PARAM_MODEL_EMPTY_ERROR);
			return new ArrayList<>();
		}

		TypedQuery<QuantityOfInterest> query = getEntityManager().createQuery(
				"SELECT q FROM QuantityOfInterest q WHERE q.tagDate IS NULL AND q.model = :" + PARAM_MODEL, //$NON-NLS-1$
				QuantityOfInterest.class);
		query.setParameter(PARAM_MODEL, model);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QuantityOfInterest> findByModel(Model model) {

		// check model param
		if (model == null) {
			logger.error(PARAM_MODEL_EMPTY_ERROR);
			return new ArrayList<>();
		}

		TypedQuery<QuantityOfInterest> query = getEntityManager().createQuery(QUERY_FIND_BY_MODEL + PARAM_MODEL,
				QuantityOfInterest.class);
		query.setParameter(PARAM_MODEL, model);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QuantityOfInterest> findRootQuantityOfInterest(Model model) {
		// Check model
		if (model == null) {
			logger.error(PARAM_MODEL_EMPTY_ERROR);
			return new ArrayList<>();
		}

		// Create query
		TypedQuery<QuantityOfInterest> query = getEntityManager().createQuery(QUERY_FIND_ROOT_BY_MODEL + PARAM_MODEL,
				QuantityOfInterest.class);
		query.setParameter(PARAM_MODEL, model);

		List<QuantityOfInterest> results = query.getResultList();

		results.forEach(super::refresh);

		// Return result list
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Integer> findQoiIdByModelId(Integer modelId) {

		// check modelId param
		if (modelId == null) {
			logger.error("param modelId is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		String paramModelId = "modelId"; //$NON-NLS-1$

		TypedQuery<Integer> query = getEntityManager().createQuery(QUERY_FIND_BY_MODELID + paramModelId, Integer.class);
		query.setParameter(paramModelId, modelId);
		return query.getResultList();
	}
}