/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IDecisionRepository;
import gov.sandia.cf.model.Decision;

/**
 * Decision entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionRepository extends AbstractCRUDRepository<Decision, Integer> implements IDecisionRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public DecisionRepository() {
		super(Decision.class);
	}

	/**
	 * DecisionRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public DecisionRepository(EntityManager entityManager) {
		super(entityManager, Decision.class);
	}

}