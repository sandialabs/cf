/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.Tag;

/**
 * the PCMMEvidence repository interface
 * 
 * @author Didier Verstraete
 *
 */
@Repository
public interface IPCMMEvidenceRepository extends ICRUDRepository<PCMMEvidence, Integer> {

	/**
	 * @return the list of all the active evidence (non-tagged)
	 */
	List<PCMMEvidence> findAllActive();

	/**
	 * @param tag the tag to find
	 * @return a list of evidence linked to the tag in parameter. If the tag is
	 *         null, return the list of active evidence (non-tagged)
	 */
	List<PCMMEvidence> findByTag(Tag tag);

	/**
	 * If needed, change the evidence path containing "\\" to "/"
	 * 
	 * @return true if the evidence needs to be cleared, otherwise false.
	 */
	boolean clearEvidencePath();
}
