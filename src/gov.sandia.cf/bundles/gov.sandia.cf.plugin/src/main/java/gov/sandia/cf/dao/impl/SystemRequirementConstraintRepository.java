/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.ISystemRequirementConstraintRepository;
import gov.sandia.cf.model.SystemRequirementConstraint;

/**
 * SystemRequirementConstraint entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class SystemRequirementConstraintRepository extends AbstractCRUDRepository<SystemRequirementConstraint, Integer>
		implements ISystemRequirementConstraintRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public SystemRequirementConstraintRepository() {
		super(SystemRequirementConstraint.class);
	}

	/**
	 * SystemRequirementConstraintRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public SystemRequirementConstraintRepository(EntityManager entityManager) {
		super(entityManager, SystemRequirementConstraint.class);
	}

}