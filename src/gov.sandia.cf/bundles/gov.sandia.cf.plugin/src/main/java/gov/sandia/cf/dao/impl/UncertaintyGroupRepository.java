/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IUncertaintyGroupRepository;
import gov.sandia.cf.model.UncertaintyGroup;

/**
 * UncertaintyGroup entity repository
 * 
 * @author Maxime N.
 *
 */
public class UncertaintyGroupRepository extends AbstractCRUDRepository<UncertaintyGroup, Integer>
		implements IUncertaintyGroupRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public UncertaintyGroupRepository() {
		super(UncertaintyGroup.class);
	}

	/**
	 * UncertaintyGroupRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public UncertaintyGroupRepository(EntityManager entityManager) {
		super(entityManager, UncertaintyGroup.class);
	}

}