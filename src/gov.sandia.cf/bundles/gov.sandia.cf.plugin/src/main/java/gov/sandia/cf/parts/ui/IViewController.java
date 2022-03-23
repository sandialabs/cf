/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui;

import org.eclipse.swt.widgets.Control;

import gov.sandia.cf.web.WebEvent;

/**
 * Interface to force the credibility views to implement methods needed for the
 * view managers.
 * 
 * @author Didier Verstraete
 */
public interface IViewController {

	/**
	 * @return the view manager
	 * 
	 */
	IViewManager getViewManager();

	/**
	 * @return the title of the credibility view
	 */
	String getTitle();

	/**
	 * Set the title
	 * 
	 * @param title the view title
	 */
	void setTitle(String title);

	/**
	 * @return the subtitle of the view
	 */
	String getItemTitle();

	/**
	 * Reload the data of the view
	 */
	void reload();

	/**
	 * Refresh data and view state elements
	 */
	void refresh();

	/**
	 * Refresh save state
	 */
	void refreshSaveState();

	/**
	 * Gets the view.
	 *
	 * @return the view
	 */
	ICredibilityView getView();

	/**
	 * Gets the view control.
	 *
	 * @return the view control
	 */
	Control getViewControl();

	/**
	 * Quit.
	 */
	void quit();
	
	/**
	 * Handle web event.
	 *
	 * @param e the e
	 */
	void handleWebEvent(WebEvent e);
}
