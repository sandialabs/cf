/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMPlanningTableValueRepository;
import gov.sandia.cf.model.PCMMPlanningTableValue;

/**
 * PCMMPlanningTableValueRepository entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningTableValueRepository extends AbstractCRUDRepository<PCMMPlanningTableValue, Integer>
		implements IPCMMPlanningTableValueRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public PCMMPlanningTableValueRepository() {
		super(PCMMPlanningTableValue.class);
	}

	/**
	 * PCMMPlanningTableValueRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMPlanningTableValueRepository(EntityManager entityManager) {
		super(entityManager, PCMMPlanningTableValue.class);
	}

}