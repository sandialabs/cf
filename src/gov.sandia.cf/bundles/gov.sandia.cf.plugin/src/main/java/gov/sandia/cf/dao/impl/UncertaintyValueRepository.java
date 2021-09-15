/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IUncertaintyValueRepository;
import gov.sandia.cf.model.UncertaintyValue;

/**
 * UncertaintyParameterRepository entity repository
 * 
 * @author Maxime N.
 *
 */
public class UncertaintyValueRepository extends AbstractCRUDRepository<UncertaintyValue, Integer>
		implements IUncertaintyValueRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public UncertaintyValueRepository() {
		super(UncertaintyValue.class);
	}

	/**
	 * UncertaintyParameterRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public UncertaintyValueRepository(EntityManager entityManager) {
		super(entityManager, UncertaintyValue.class);
	}

}