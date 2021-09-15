/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IUncertaintyConstraintRepository;
import gov.sandia.cf.model.UncertaintyConstraint;

/**
 * UncertaintyConstraint entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class UncertaintyConstraintRepository extends AbstractCRUDRepository<UncertaintyConstraint, Integer>
		implements IUncertaintyConstraintRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public UncertaintyConstraintRepository() {
		super(UncertaintyConstraint.class);
	}

	/**
	 * UncertaintyConstraintRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public UncertaintyConstraintRepository(EntityManager entityManager) {
		super(entityManager, UncertaintyConstraint.class);
	}

}