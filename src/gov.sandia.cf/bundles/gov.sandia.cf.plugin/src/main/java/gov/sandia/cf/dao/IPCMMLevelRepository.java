/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMSubelement;

/**
 * the PCMMLevel repository interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IPCMMLevelRepository extends ICRUDRepository<PCMMLevel, Integer> {
	/**
	 * Find levels by PCMMElement
	 * 
	 * @param element the element filter
	 * @return the list of levels found for the element in parameter.
	 */
	public List<PCMMLevel> findByPCMMElement(PCMMElement element);

	/**
	 * Find levels by PCMMSubelement
	 * 
	 * @param subelement the subelement filter
	 * @return the list of levels found for the subelement in parameter.
	 */
	public List<PCMMLevel> findByPCMMSubelement(PCMMSubelement subelement);

}
