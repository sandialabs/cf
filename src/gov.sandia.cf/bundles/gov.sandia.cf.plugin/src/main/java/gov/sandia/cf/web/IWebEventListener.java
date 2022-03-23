/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web;

/**
 * The listener interface for receiving IWebEvent events. The class that is
 * interested in processing a IWebEvent event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addIWebEventListener</code> method. When the IWebEvent event
 * occurs, that object's appropriate method is invoked.
 *
 * @see WebEvent
 * 
 * @author Didier Verstraete
 */
public interface IWebEventListener {
	/**
	 * Handle the web event.
	 *
	 * @param e the event
	 */
	void handle(WebEvent e);

	/**
	 * Handle the web event error.
	 *
	 * @param error the error
	 */
	void handleError(Throwable error);
}
