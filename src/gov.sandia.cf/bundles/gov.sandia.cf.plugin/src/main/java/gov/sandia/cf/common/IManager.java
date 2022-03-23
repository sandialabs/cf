/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.common;

/**
 * Interface for layer manager
 * 
 * @author Didier Verstraete
 *
 */
public interface IManager {

	/**
	 * Starts the loader
	 */
	void start();

	/**
	 * Stops the loader
	 */
	void stop();

	/**
	 * @return true if loader is started
	 */
	boolean isStarted();
}
