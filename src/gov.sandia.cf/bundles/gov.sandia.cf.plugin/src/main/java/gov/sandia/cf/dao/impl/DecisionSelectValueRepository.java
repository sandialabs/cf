/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IDecisionSelectValueRepository;
import gov.sandia.cf.model.DecisionSelectValue;

/**
 * DecisionSelectValue entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionSelectValueRepository extends AbstractCRUDRepository<DecisionSelectValue, Integer>
		implements IDecisionSelectValueRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public DecisionSelectValueRepository() {
		super(DecisionSelectValue.class);
	}

	/**
	 * DecisionSelectValueRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public DecisionSelectValueRepository(EntityManager entityManager) {
		super(entityManager, DecisionSelectValue.class);
	}

}