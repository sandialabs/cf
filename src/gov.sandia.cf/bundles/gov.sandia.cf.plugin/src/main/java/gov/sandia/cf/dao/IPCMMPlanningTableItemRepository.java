/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.Tag;

/**
 * the IPCMMPlanningTableItemRepository repository interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IPCMMPlanningTableItemRepository extends ICRUDRepository<PCMMPlanningTableItem, Integer> {

	/**
	 * Get PCMM planning table item by PCMM element in PCMM subelement.
	 * 
	 * @param element     the element to find
	 * @param selectedTag the tag to find
	 * @return the PCMM planning values matching
	 */
	List<PCMMPlanningTableItem> findByElementInSubelement(PCMMElement element, Tag selectedTag);
}
