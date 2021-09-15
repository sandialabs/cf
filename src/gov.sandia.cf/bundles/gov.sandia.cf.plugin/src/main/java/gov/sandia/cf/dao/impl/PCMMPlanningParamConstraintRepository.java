/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMPlanningParamConstraintRepository;
import gov.sandia.cf.model.PCMMPlanningParamConstraint;

/**
 * PCMMPlanningParamConstraintRepository entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningParamConstraintRepository extends AbstractCRUDRepository<PCMMPlanningParamConstraint, Integer>
		implements IPCMMPlanningParamConstraintRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public PCMMPlanningParamConstraintRepository() {
		super(PCMMPlanningParamConstraint.class);
	}

	/**
	 * PCMMPlanningParamConstraintRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMPlanningParamConstraintRepository(EntityManager entityManager) {
		super(entityManager, PCMMPlanningParamConstraint.class);
	}

}