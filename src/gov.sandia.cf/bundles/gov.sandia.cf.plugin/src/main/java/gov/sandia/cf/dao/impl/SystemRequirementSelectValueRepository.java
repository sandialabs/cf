/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.ISystemRequirementSelectValueRepository;
import gov.sandia.cf.model.SystemRequirementSelectValue;

/**
 * SystemRequirementSelectValue entity repository
 * 
 * @author Maxime N.
 *
 */
public class SystemRequirementSelectValueRepository extends AbstractCRUDRepository<SystemRequirementSelectValue, Integer>
		implements ISystemRequirementSelectValueRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public SystemRequirementSelectValueRepository() {
		super(SystemRequirementSelectValue.class);
	}

	/**
	 * SystemRequirementSelectValueRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public SystemRequirementSelectValueRepository(EntityManager entityManager) {
		super(entityManager, SystemRequirementSelectValue.class);
	}

}