/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IConfigurationFileRepository;
import gov.sandia.cf.model.ConfigurationFile;

/**
 * Configuration File entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class ConfigurationFileRepository extends AbstractCRUDRepository<ConfigurationFile, Integer>
		implements IConfigurationFileRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public ConfigurationFileRepository() {
		super(ConfigurationFile.class);
	}

	/**
	 * Repository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public ConfigurationFileRepository(EntityManager entityManager) {
		super(entityManager, ConfigurationFile.class);
	}

}