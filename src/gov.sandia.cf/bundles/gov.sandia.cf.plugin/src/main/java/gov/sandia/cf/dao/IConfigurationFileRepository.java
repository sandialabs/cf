/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import gov.sandia.cf.model.ConfigurationFile;

/**
 * the Configuration File repository interface
 * 
 * @author Didier Verstraete
 *
 */
@Repository
public interface IConfigurationFileRepository extends ICRUDRepository<ConfigurationFile, Integer> {

}
