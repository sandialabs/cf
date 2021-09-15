/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningQuestion;

/**
 * the IPCMMPlanningQuestionRepository repository interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IPCMMPlanningQuestionRepository extends ICRUDRepository<PCMMPlanningQuestion, Integer> {

	/**
	 * @param elt the element to find
	 * @return the list of pcmm planning question by element in subelement
	 */
	List<PCMMPlanningQuestion> findByElementInSubelement(PCMMElement elt);

}
