/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IDecisionValueRepository;
import gov.sandia.cf.model.DecisionValue;

/**
 * DecisionValueRepository entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionValueRepository extends AbstractCRUDRepository<DecisionValue, Integer>
		implements IDecisionValueRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public DecisionValueRepository() {
		super(DecisionValue.class);
	}

	/**
	 * DecisionParameterRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public DecisionValueRepository(EntityManager entityManager) {
		super(entityManager, DecisionValue.class);
	}

}