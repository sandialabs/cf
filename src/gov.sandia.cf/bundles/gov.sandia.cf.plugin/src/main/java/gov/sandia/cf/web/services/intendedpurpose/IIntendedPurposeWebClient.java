/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.intendedpurpose;

import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.web.services.IWebClient;

/**
 * Intended Purpose Web client interface
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IIntendedPurposeWebClient extends IWebClient {

	/**
	 * Lock the intended purpose.
	 *
	 * @param model       the model
	 * @param information the information
	 * @return the string
	 * @throws CredibilityException the credibility exception
	 */
	String lock(Model model, String information) throws CredibilityException;

	/**
	 * Unlock the update methods.
	 *
	 * @param model the model
	 * @param token the token
	 * @throws CredibilityException the credibility exception
	 */
	void unlock(Model model, String token) throws CredibilityException;

	/**
	 * Gets the lock info.
	 *
	 * @param model the model
	 * @return the lock info
	 * @throws CredibilityException the credibility exception
	 */
	String getLockInfo(Model model) throws CredibilityException;

	/**
	 * Checks if is locked.
	 *
	 * @param model the model
	 * @return true, if is locked
	 */
	boolean isLocked(Model model);
}
