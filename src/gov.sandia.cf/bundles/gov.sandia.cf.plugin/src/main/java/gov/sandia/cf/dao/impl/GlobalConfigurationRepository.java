/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IGlobalConfigurationRepository;
import gov.sandia.cf.model.GlobalConfiguration;

/**
 * GlobalConfiguration entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class GlobalConfigurationRepository extends AbstractCRUDRepository<GlobalConfiguration, Integer>
		implements IGlobalConfigurationRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public GlobalConfigurationRepository() {
		super(GlobalConfiguration.class);
	}

	/**
	 * 
	 * GlobalConfigurationRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public GlobalConfigurationRepository(EntityManager entityManager) {
		super(entityManager, GlobalConfiguration.class);
	}

}