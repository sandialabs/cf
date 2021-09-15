/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.exceptions.CredibilityException;

/**
 * the Criteria repository interface
 * 
 * @author Didier Verstraete
 *
 */
public interface INativeQueryRepository extends IRepository {

	/**
	 * @param query     the query to execute
	 * @param typeClass the type of the result class
	 * @return the result of the @param query
	 * @throws CredibilityException if query can not be executed or throws an error
	 */
	@SuppressWarnings("rawtypes")
	List execute(String query, Class<?> typeClass) throws CredibilityException;
}
