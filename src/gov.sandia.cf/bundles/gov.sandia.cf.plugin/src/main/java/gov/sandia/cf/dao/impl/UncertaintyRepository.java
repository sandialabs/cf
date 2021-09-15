/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IUncertaintyRepository;
import gov.sandia.cf.model.Uncertainty;

/**
 * Uncertainty entity repository
 * 
 * @author Maxime N.
 *
 */
public class UncertaintyRepository extends AbstractCRUDRepository<Uncertainty, Integer>
		implements IUncertaintyRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public UncertaintyRepository() {
		super(Uncertainty.class);
	}

	/**
	 * UncertaintyRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public UncertaintyRepository(EntityManager entityManager) {
		super(entityManager, Uncertainty.class);
	}

}