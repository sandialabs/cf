/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;

/**
 * the PCMMAssessment repository interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IPCMMAssessmentRepository extends ICRUDRepository<PCMMAssessment, Integer> {

	/**
	 * @return the list of all active assessments (non-tagged)
	 */
	List<PCMMAssessment> findAllActive();

	/**
	 * @param role the role
	 * @param user the user
	 * @param elt  the element
	 * @param tag  the tag
	 * @return the assessment associated to the parameters
	 */
	List<PCMMAssessment> findByRoleAndUserAndEltAndTag(Role role, User user, PCMMElement elt, Tag tag);

	/**
	 * @param role   the role
	 * @param user   the user
	 * @param subelt the subelement
	 * @param tag    the tag
	 * @return the assessment associated to the parameters
	 */
	List<PCMMAssessment> findByRoleAndUserAndSubeltAndTag(Role role, User user, PCMMSubelement subelt, Tag tag);

	/**
	 * @param elt the element filter
	 * @param tag the tag filter
	 * @return the assessment associated to the element parameter searched into the
	 *         subelement
	 */
	List<PCMMAssessment> findByElementAndTagInSubelement(PCMMElement elt, Tag tag);

	/**
	 * @param tag the tag filter
	 * @return the assessments associated to the tag in parameter. If the tag is
	 *         null, return the list of active assessments (non-tagged)
	 */
	List<PCMMAssessment> findByTag(Tag tag);

	/**
	 * Clear multiple assessments peer
	 * 
	 * @param mode the pcmm mode
	 * @return true if the database needed to be cleared.
	 */
	boolean clearMultipleAssessment(PCMMMode mode);

	/**
	 * Clear multiple assessments for a same role/user/element (keep first found)
	 * 
	 * @param role    the role
	 * @param user    the user
	 * @param element the element
	 * @param tag     the tag
	 */
	public void clearAssessment(Role role, User user, PCMMElement element, Tag tag);

	/**
	 * Clear multiple assessments for a same role/user/subelement (keep first found)
	 * 
	 * @param role       the role
	 * @param user       the user
	 * @param subelement the subelement
	 * @param tag        the tag
	 */
	public void clearAssessment(Role role, User user, PCMMSubelement subelement, Tag tag);
}
