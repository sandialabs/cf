/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IUncertaintySelectValueRepository;
import gov.sandia.cf.model.UncertaintySelectValue;

/**
 * UncertaintySelectValue entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class UncertaintySelectValueRepository extends AbstractCRUDRepository<UncertaintySelectValue, Integer>
		implements IUncertaintySelectValueRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public UncertaintySelectValueRepository() {
		super(UncertaintySelectValue.class);
	}

	/**
	 * UncertaintySelectValueRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public UncertaintySelectValueRepository(EntityManager entityManager) {
		super(entityManager, UncertaintySelectValue.class);
	}

}