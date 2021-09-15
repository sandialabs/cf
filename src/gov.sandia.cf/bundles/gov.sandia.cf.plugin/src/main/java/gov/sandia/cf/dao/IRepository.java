/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import javax.persistence.EntityManager;

/**
 * The default Repository interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IRepository {

	/**
	 * @return the entity manager
	 */
	public EntityManager getEntityManager();

	/**
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public void setEntityManager(EntityManager entityManager);
}
