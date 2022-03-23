/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.service;

import gov.sandia.cf.webapp.model.stub.EntityLock;
import gov.sandia.cf.webapp.model.stub.EntityLockInfo;

/**
 * The Interface ILockService.
 * 
 * @author Didier Verstraete
 */
public interface ILockService {

	String lock(EntityLock lock);

	String lock(Class<?> entityClass, Long id);

	String lock(Class<?> entityClass, Long id, String information);

	void unlock(String token, Class<?> entityClass, Long id);

	void unlock(EntityLock lock);

	boolean isLocked(Class<?> entityClass, Long id);

	boolean isWritable(String token, Class<?> entityClass, Long id);

	EntityLockInfo getLockInfo(Class<?> entityClass, Long id);
}
