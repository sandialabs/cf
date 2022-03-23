/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.status;

import gov.sandia.cf.common.IManager;

/**
 * The Interface IPingManager.
 *
 * @author Didier Verstraete
 */
public interface IPingManager extends IManager {

	/**
	 * Adds the listener.
	 *
	 * @param listener the listener
	 */
	void addListener(IConnectionStatusListener listener);

	/**
	 * Removes the listener.
	 *
	 * @param listener the listener
	 */
	void removeListener(IConnectionStatusListener listener);

	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	boolean isConnected();
}
