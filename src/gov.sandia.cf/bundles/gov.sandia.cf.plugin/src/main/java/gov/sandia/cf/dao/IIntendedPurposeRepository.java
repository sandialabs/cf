/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.Model;

/**
 * the IntendedPurpose repository interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IIntendedPurposeRepository extends ICRUDRepository<IntendedPurpose, Integer> {

	/**
	 * @param model the CF model to search
	 * @return the first intended purpose
	 */
	IntendedPurpose getFirst(Model model);

}
