/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services;

/**
 * The main interface to define for each web client service.
 * 
 * @author Didier Verstraete
 *
 */
public interface IWebClient {

	/**
	 * @return the application manager
	 */
	IWebClientManager getWebClientMgr();

	/**
	 * Set the application manager
	 * 
	 * @param webClientMgr the application manager to set
	 */
	void setWebClientMgr(IWebClientManager webClientMgr);

}
