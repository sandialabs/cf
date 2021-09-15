/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IDecisionConstraintRepository;
import gov.sandia.cf.model.DecisionConstraint;

/**
 * DecisionConstraint entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionConstraintRepository extends AbstractCRUDRepository<DecisionConstraint, Integer>
		implements IDecisionConstraintRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public DecisionConstraintRepository() {
		super(DecisionConstraint.class);
	}

	/**
	 * DecisionConstraintRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public DecisionConstraintRepository(EntityManager entityManager) {
		super(entityManager, DecisionConstraint.class);
	}

}