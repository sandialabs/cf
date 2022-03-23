/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.services;

import gov.sandia.cf.common.IManager;

/**
 * The main interface to define the client service manager.
 * 
 * @author Didier Verstraete
 *
 */
public interface IClientServiceManager extends IManager {

	/**
	 * @param <S>            the service interface inherited IClientService class
	 * @param interfaceClass the service interface class
	 * @return the associated service if found, otherwise null
	 */
	public <S extends IClientService> S getService(Class<S> interfaceClass);
}
