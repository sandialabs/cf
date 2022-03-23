/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services;

/**
 * The abstract class to extend for every web client service.
 * 
 * @author Didier Verstraete
 * 
 */
public abstract class AWebClient implements IWebClient {

	/** The web client mgr. */
	private IWebClientManager webClientMgr;

	/**
	 * Instantiates a new a web client application.
	 */
	protected AWebClient() {
		this.webClientMgr = null;
	}

	/**
	 * Instantiates a new a web client application.
	 *
	 * @param webClientMgr the web client manager
	 */
	protected AWebClient(IWebClientManager webClientMgr) {
		this.webClientMgr = webClientMgr;
	}

	@Override
	public IWebClientManager getWebClientMgr() {
		return webClientMgr;
	}

	@Override
	public void setWebClientMgr(IWebClientManager webClientMgr) {
		this.webClientMgr = webClientMgr;
	}

}
