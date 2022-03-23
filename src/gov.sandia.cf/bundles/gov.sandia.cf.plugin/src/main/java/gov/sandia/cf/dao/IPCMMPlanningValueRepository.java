/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.Tag;

/**
 * the IPCMMPlanningValueRepository repository interface
 * 
 * @author Didier Verstraete
 *
 */
@Repository
public interface IPCMMPlanningValueRepository extends ICRUDRepository<PCMMPlanningValue, Integer> {

	/**
	 * Get PCMM planning value by PCMM element in PCMM subelement.
	 * 
	 * @param element     the element to find
	 * @param selectedTag the tag to find
	 * @return the PCMM planning values matching
	 */
	List<PCMMPlanningValue> findByElementInSubelement(PCMMElement element, Tag selectedTag);

}
