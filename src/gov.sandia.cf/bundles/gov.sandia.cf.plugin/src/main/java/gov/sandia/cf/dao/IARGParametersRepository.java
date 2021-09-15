/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import gov.sandia.cf.model.ARGParameters;

/**
 * the ARG Parameters repository interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IARGParametersRepository extends ICRUDRepository<ARGParameters, Integer> {

	/**
	 * @return the first arg parameters
	 */
	ARGParameters getFirst();

}
