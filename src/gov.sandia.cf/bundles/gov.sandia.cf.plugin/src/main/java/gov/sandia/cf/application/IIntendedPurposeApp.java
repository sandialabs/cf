/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;

/**
 * Intended Purpose Application interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IIntendedPurposeApp extends IApplication {

	/**
	 * @param model the CF model
	 * @return if the intended purpose is activated or not
	 */
	boolean isIntendedPurposeEnabled(Model model);

	/**
	 * Get or create the default intended purpose
	 * 
	 * @param model the CF model
	 * @return the intended purpose
	 * @throws CredibilityException if an error occurs during creation
	 */
	IntendedPurpose get(Model model) throws CredibilityException;

	/**
	 * Update intended purpose
	 * 
	 * @param intendedPurpose the intended purpose to update
	 * @param userUpdate      the user that updated the intended purpose
	 * @return The intended purpose updated
	 * @throws CredibilityException if an error occurs during update
	 */
	IntendedPurpose updateIntendedPurpose(IntendedPurpose intendedPurpose, User userUpdate) throws CredibilityException;
}
