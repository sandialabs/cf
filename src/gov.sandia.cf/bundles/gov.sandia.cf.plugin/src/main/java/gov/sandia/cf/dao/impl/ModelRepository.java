/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.model.Model;

/**
 * Model entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class ModelRepository extends AbstractCRUDRepository<Model, Integer> implements IModelRepository {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ModelRepository.class);

	/**
	 * Query select all Model
	 */
	public static final String QUERY_SELECT_ALL_MODEL = "SELECT m FROM Model m"; //$NON-NLS-1$

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public ModelRepository() {
		super(Model.class);
	}

	/**
	 * 
	 * ModelRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public ModelRepository(EntityManager entityManager) {
		super(entityManager, Model.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Model getFirst() {
		logger.debug("Calling getFirst() method"); //$NON-NLS-1$

		TypedQuery<Model> query = getEntityManager().createQuery(QUERY_SELECT_ALL_MODEL, Model.class);
		return (query.getResultList() != null && !query.getResultList().isEmpty()) ? query.getResultList().get(0)
				: null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDatabaseVersion() {
		Model model = getFirst();
		if (model != null) {
			refresh(model);
		}
		return model != null ? model.getVersion() : null;
	}
}