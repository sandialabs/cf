/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.Tag;

/**
 * the IPCMMPlanningQuestionValueRepository repository interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IPCMMPlanningQuestionValueRepository extends ICRUDRepository<PCMMPlanningQuestionValue, Integer> {

	/**
	 * Get PCMM planning question value by PCMM Planning Question.
	 * 
	 * @param question the question to find
	 * @return the PCMM planning question values matching
	 */
	List<PCMMPlanningQuestionValue> findByQuestion(PCMMPlanningQuestion question);

	/**
	 * Get PCMM planning question value by element in PCMM Planning Question.
	 * 
	 * @param element     the element to find
	 * @param selectedTag the tag to find
	 * @return the PCMM planning question values matching
	 */
	List<PCMMPlanningQuestionValue> findByElement(PCMMElement element, Tag selectedTag);

	/**
	 * Get PCMM planning question value by element in PCMM subelement in PCMM
	 * Planning Question.
	 * 
	 * @param element the element to find
	 * @param selectedTag the tag to find
	 * @return the PCMM planning question values matching
	 */
	List<PCMMPlanningQuestionValue> findByElementInSubelement(PCMMElement element, Tag selectedTag);
}
