/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMPlanningQuestionConstraintRepository;
import gov.sandia.cf.model.PCMMPlanningQuestionConstraint;

/**
 * PCMMPlanningQuestionRepository entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningQuestionConstraintRepository
		extends AbstractCRUDRepository<PCMMPlanningQuestionConstraint, Integer>
		implements IPCMMPlanningQuestionConstraintRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public PCMMPlanningQuestionConstraintRepository() {
		super(PCMMPlanningQuestionConstraint.class);
	}

	/**
	 * PCMMPlanningQuestionConstraintRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMPlanningQuestionConstraintRepository(EntityManager entityManager) {
		super(entityManager, PCMMPlanningQuestionConstraint.class);
	}

}