/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;

/**
 * the PCMMElement repository interface
 * 
 * @author Didier Verstraete
 *
 */
@Repository
public interface IPCMMElementRepository extends ICRUDRepository<PCMMElement, Integer> {

	/**
	 * @param model
	 *            the model to find elements for
	 * @return the list of elements for the parameter model
	 */
	List<PCMMElement> findByModel(Model model);

}
