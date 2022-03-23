/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.message;

import gov.sandia.cf.common.IManager;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.web.IWebEventListener;
import gov.sandia.cf.web.WebClientException;

/**
 * The main interface to define the application manager.
 * 
 * @author Didier Verstraete
 *
 */
public interface IMessageManager extends IManager {

	/**
	 * Adds the listener.
	 *
	 * @param listener the listener
	 */
	void addListener(IWebEventListener listener);

	/**
	 * Removes the listener.
	 *
	 * @param listener the listener
	 */
	void removeListener(IWebEventListener listener);

	/**
	 * Subscribe to model.
	 *
	 * @param model the model
	 * @throws WebClientException the web client exception
	 */
	void subscribeToModel(Model model) throws WebClientException;
}
