/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.services;

/**
 * The abstract class to extend for every application service
 * 
 * @author Didier Verstraete
 * 
 */
public abstract class AClientService implements IClientService {

	/**
	 * The client service manager
	 */
	private IClientServiceManager clientSrvMgr;

	/**
	 * AApplication constructor
	 */
	protected AClientService() {
		this.clientSrvMgr = null;
	}

	/**
	 * AApplication constructor
	 * 
	 * @param clientSrvMgr the client service manager
	 */
	protected AClientService(IClientServiceManager clientSrvMgr) {
		this.clientSrvMgr = clientSrvMgr;
	}

	@Override
	public IClientServiceManager getClientSrvMgr() {
		return clientSrvMgr;
	}

	@Override
	public void setClientSrvMgr(IClientServiceManager clientSrvMgr) {
		this.clientSrvMgr = clientSrvMgr;
	}
}
