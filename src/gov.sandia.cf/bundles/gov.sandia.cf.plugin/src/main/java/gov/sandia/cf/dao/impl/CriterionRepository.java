/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.ICriterionRepository;
import gov.sandia.cf.model.Criterion;

/**
 * Criterion entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class CriterionRepository extends AbstractCRUDRepository<Criterion, Integer> implements ICriterionRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public CriterionRepository() {
		super(Criterion.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager
	 *            the entity manager for this repository to execute queries (must
	 *            not be null)
	 */
	public CriterionRepository(EntityManager entityManager) {
		super(entityManager, Criterion.class);
	}

}