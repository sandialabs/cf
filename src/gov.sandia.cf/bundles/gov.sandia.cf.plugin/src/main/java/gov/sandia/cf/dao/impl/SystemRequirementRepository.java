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
import gov.sandia.cf.dao.ISystemRequirementRepository;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.Model;

/**
 * SystemRequirement entity repository
 * 
 * @author Maxime N.
 *
 */
public class SystemRequirementRepository extends AbstractCRUDRepository<SystemRequirement, Integer>
		implements ISystemRequirementRepository {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SystemRequirementRepository.class);

	/**
	 * model parameter
	 */
	public static final String PARAM_MODEL = "model"; //$NON-NLS-1$

	/**
	 * Message logged when param model is empty or null
	 */
	public static final String PARAM_MODEL_EMPTY_ERROR = "param model is empty or null"; //$NON-NLS-1$

	/**
	 * Qurey root requirement
	 */
	public static final String QUERY_FIND_ROOT_BY_MODEL = "SELECT r FROM SystemRequirement r WHERE r.parent IS NULL AND r.model = :"; //$NON-NLS-1$

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public SystemRequirementRepository() {
		super(SystemRequirement.class);
	}

	/**
	 * SystemRequirementRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public SystemRequirementRepository(EntityManager entityManager) {
		super(entityManager, SystemRequirement.class);
	}

	@Override
	public List<SystemRequirement> findRootRequirementsByModel(Model model) {
		// Check model
		if (model == null) {
			logger.error(PARAM_MODEL_EMPTY_ERROR);
			return new ArrayList<>();
		}

		// Create query
		TypedQuery<SystemRequirement> query = getEntityManager().createQuery(QUERY_FIND_ROOT_BY_MODEL + PARAM_MODEL,
				SystemRequirement.class);
		query.setParameter(PARAM_MODEL, model);

		List<SystemRequirement> results = query.getResultList();

		results.forEach(super::refresh);

		// Return result list
		return results;
	}

}