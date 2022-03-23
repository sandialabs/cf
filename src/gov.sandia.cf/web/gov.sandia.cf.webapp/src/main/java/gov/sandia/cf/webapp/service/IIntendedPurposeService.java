/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.service;

import gov.sandia.cf.webapp.model.entity.IntendedPurpose;
import gov.sandia.cf.webapp.model.stub.EntityLockInfo;

/**
 * The Interface IIntendedPurposeService.
 * 
 * @author Didier Verstraete
 */
public interface IIntendedPurposeService {

	/**
	 * Gets the intended purpose.
	 *
	 * @param modelId the model id
	 * @return the intended purpose
	 */
	IntendedPurpose get(Long modelId);

	/**
	 * Save.
	 *
	 * @param modelId         the model id
	 * @param token           the token
	 * @param intendedPurpose the intended purpose
	 */
	void save(Long modelId, String token, IntendedPurpose intendedPurpose);

	/**
	 * Gets the lock info.
	 *
	 * @param modelId the model id
	 * @return the lock info
	 */
	EntityLockInfo getLockInfo(Long modelId);

	/**
	 * Lock.
	 *
	 * @param modelId     the model id
	 * @param information the information
	 * @return the string
	 */
	String lock(Long modelId, String information);

	/**
	 * Unlock.
	 *
	 * @param modelId the model id
	 * @param token   the token
	 */
	void unlock(Long modelId, String token);

}
