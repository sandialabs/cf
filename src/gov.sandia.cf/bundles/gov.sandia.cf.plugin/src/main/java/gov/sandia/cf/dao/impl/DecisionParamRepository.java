/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IDecisionParamRepository;
import gov.sandia.cf.model.DecisionParam;

/**
 * DecisionParam entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionParamRepository extends AbstractCRUDRepository<DecisionParam, Integer>
		implements IDecisionParamRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public DecisionParamRepository() {
		super(DecisionParam.class);
	}

	/**
	 * DecisionParamRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public DecisionParamRepository(EntityManager entityManager) {
		super(entityManager, DecisionParam.class);
	}

}