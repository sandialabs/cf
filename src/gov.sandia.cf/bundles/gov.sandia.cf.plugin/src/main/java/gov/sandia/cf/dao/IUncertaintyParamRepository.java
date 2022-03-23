/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import gov.sandia.cf.model.UncertaintyParam;

/**
 * the IUncertaintyParamRepository repository interface
 * 
 * @author Maxime N.
 *
 */
@Repository
public interface IUncertaintyParamRepository extends ICRUDRepository<UncertaintyParam, Integer> {

	/**
	 * Find first by name.
	 *
	 * @param name the name
	 * @return the uncertainty param
	 */
	UncertaintyParam findFirstByName(String name);

}
