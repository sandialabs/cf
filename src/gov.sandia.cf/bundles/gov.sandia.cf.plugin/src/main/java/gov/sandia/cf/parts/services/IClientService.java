/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.services;

/**
 * The main interface to define for each view service.
 * 
 * @author Didier Verstraete
 *
 */
public interface IClientService {

	/**
	 * @return the client service manager
	 */
	IClientServiceManager getClientSrvMgr();

	/**
	 * Set the view manager
	 * 
	 * @param clientSrvMgr the client service manager to set
	 */
	void setClientSrvMgr(IClientServiceManager clientSrvMgr);

}
