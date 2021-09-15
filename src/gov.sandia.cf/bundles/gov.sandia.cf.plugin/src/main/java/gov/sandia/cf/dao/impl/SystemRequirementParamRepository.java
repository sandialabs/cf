/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.ISystemRequirementParamRepository;
import gov.sandia.cf.model.SystemRequirementParam;

/**
 * SystemRequirementParam entity repository
 * 
 * @author Maxime N.
 *
 */
public class SystemRequirementParamRepository extends AbstractCRUDRepository<SystemRequirementParam, Integer>
		implements ISystemRequirementParamRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public SystemRequirementParamRepository() {
		super(SystemRequirementParam.class);
	}

	/**
	 * SystemRequirementParamRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public SystemRequirementParamRepository(EntityManager entityManager) {
		super(entityManager, SystemRequirementParam.class);
	}

}