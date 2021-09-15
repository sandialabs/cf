/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMOptionRepository;
import gov.sandia.cf.model.PCMMOption;

/**
 * PCMMOption entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMOptionRepository extends AbstractCRUDRepository<PCMMOption, Integer> implements IPCMMOptionRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PCMMOptionRepository() {
		super(PCMMOption.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMOptionRepository(EntityManager entityManager) {
		super(entityManager, PCMMOption.class);
	}

}