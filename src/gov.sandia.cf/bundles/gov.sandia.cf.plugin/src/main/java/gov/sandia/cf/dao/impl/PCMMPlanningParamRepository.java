/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMPlanningParamRepository;
import gov.sandia.cf.model.PCMMPlanningParam;

/**
 * PCMMPlanningParamRepository entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningParamRepository extends AbstractCRUDRepository<PCMMPlanningParam, Integer>
		implements IPCMMPlanningParamRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public PCMMPlanningParamRepository() {
		super(PCMMPlanningParam.class);
	}

	/**
	 * PCMMPlanningParamRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMPlanningParamRepository(EntityManager entityManager) {
		super(entityManager, PCMMPlanningParam.class);
	}

}