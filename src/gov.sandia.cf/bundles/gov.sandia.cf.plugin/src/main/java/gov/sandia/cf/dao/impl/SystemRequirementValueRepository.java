/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.ISystemRequirementValueRepository;
import gov.sandia.cf.model.SystemRequirementValue;

/**
 * SystemRequirementParameterRepository entity repository
 * 
 * @author Maxime N.
 *
 */
public class SystemRequirementValueRepository extends AbstractCRUDRepository<SystemRequirementValue, Integer>
		implements ISystemRequirementValueRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public SystemRequirementValueRepository() {
		super(SystemRequirementValue.class);
	}

	/**
	 * SystemRequirementParameterRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public SystemRequirementValueRepository(EntityManager entityManager) {
		super(entityManager, SystemRequirementValue.class);
	}

}