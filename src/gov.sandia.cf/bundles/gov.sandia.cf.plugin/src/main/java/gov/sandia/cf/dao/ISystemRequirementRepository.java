/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.Model;

/**
 * the SystemRequirement repository interface
 * 
 * @author Maxime N.
 *
 */
@Repository
public interface ISystemRequirementRepository extends ICRUDRepository<SystemRequirement, Integer> {

	/**
	 * @param model the model filter
	 * @return the list of system requirements by model
	 */
	List<SystemRequirement> findRootRequirementsByModel(Model model);

}
