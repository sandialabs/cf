/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.INativeQueryRepository;
import gov.sandia.cf.exceptions.CredibilityException;

/**
 * Model entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class NativeQueryRepository implements INativeQueryRepository {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(NativeQueryRepository.class);

	/**
	 * the entity manager to execute jpa queries
	 */
	private EntityManager entityManager;

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public NativeQueryRepository() {
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 * 
	 */
	public NativeQueryRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<?> execute(String query, Class<?> resultClass) throws CredibilityException {
		Query nativeQuery = entityManager.createNativeQuery(query, resultClass);
		logger.info("Executing query:{}", query); //$NON-NLS-1$
		List<?> resultList = null;
		try {
			resultList = nativeQuery.getResultList();
		} catch (javax.persistence.PersistenceException e) {
			throw new CredibilityException(e.getMessage());
		}
		return resultList;
	}

}