/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.status;

/**
 * The listener interface for receiving IConnectionStatus events. The class that
 * is interested in processing a IConnectionStatus event implements this
 * interface, and the object created with that class is registered with a
 * component using the component's <code>addIConnectionStatusListener</code>
 * method. When the IConnectionStatus event occurs, that object's appropriate
 * method is invoked.
 *
 * @author Didier Verstraete
 */
public interface IConnectionStatusListener {

	/**
	 * Connection lost.
	 */
	void connectionLost();

	/**
	 * Connection gained.
	 */
	void connectionGained();
}
