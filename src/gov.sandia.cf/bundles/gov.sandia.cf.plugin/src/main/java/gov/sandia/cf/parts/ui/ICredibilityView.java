/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui;

/**
 * Interface to force the credibility views to implement methods needed for the
 * view managers.
 * 
 * @author Didier Verstraete
 */
public interface ICredibilityView {

	/**
	 * @return the view manager
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
}
