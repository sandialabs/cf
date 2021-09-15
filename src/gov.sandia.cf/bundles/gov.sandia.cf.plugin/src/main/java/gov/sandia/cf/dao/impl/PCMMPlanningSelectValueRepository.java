/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMPlanningSelectValueRepository;
import gov.sandia.cf.model.PCMMPlanningSelectValue;

/**
 * PCMMPlanningSelectValue entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningSelectValueRepository extends AbstractCRUDRepository<PCMMPlanningSelectValue, Integer>
		implements IPCMMPlanningSelectValueRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public PCMMPlanningSelectValueRepository() {
		super(PCMMPlanningSelectValue.class);
	}

	/**
	 * PCMMPlanningSelectValueRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMPlanningSelectValueRepository(EntityManager entityManager) {
		super(entityManager, PCMMPlanningSelectValue.class);
	}

}