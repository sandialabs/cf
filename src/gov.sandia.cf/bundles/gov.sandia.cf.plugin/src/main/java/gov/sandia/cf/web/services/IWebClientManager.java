/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services;

import org.springframework.web.reactive.function.client.WebClient;

import gov.sandia.cf.common.IManager;

/**
 * The main interface to define the application manager.
 * 
 * @author Didier Verstraete
 *
 */
public interface IWebClientManager extends IManager {

	/**
	 * @param <S>            the service interface inherited IWebClient class
	 * @param interfaceClass the service interface class
	 * @return the associated service if found, otherwise null
	 */
	public <S> S getService(Class<S> interfaceClass);

	/**
	 * Gets the base URI.
	 *
	 * @return the base URI
	 */
	String getBaseURI();

	/**
	 * Sets the base URI.
	 *
	 * @param baseURI the base URI
	 */
	void setBaseURI(String baseURI);

	/**
	 * Gets the web client.
	 *
	 * @return the web client
	 */
	WebClient getWebClient();

}
