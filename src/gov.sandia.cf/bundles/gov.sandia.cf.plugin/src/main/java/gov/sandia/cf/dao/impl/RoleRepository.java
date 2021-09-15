/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IRoleRepository;
import gov.sandia.cf.model.Role;

/**
 * Role entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class RoleRepository extends AbstractCRUDRepository<Role, Integer> implements IRoleRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public RoleRepository() {
		super(Role.class);
	}

	/**
	 * RoleRepository constructor
	 * 
	 * @param entityManager
	 *            the entity manager for this repository to execute queries (must
	 *            not be null)
	 */
	public RoleRepository(EntityManager entityManager) {
		super(entityManager, Role.class);
	}

}