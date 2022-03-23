/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import gov.sandia.cf.model.Model;

/**
 * the Model repository interface
 * 
 * @author Didier Verstraete
 *
 */
@Repository
public interface IModelRepository extends ICRUDRepository<Model, Integer> {

	/**
	 * @return the first model if it exists, otherwise return false
	 */
	Model getFirst();

	/**
	 * @return the current database version number
	 */
	String getDatabaseVersion();

}
