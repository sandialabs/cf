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
	 * The get view controller.
	 *
	 * @return the view controller
	 */
	IViewController getViewController();

	/**
	 * Gets the title.
	 *
	 * @return the title of the credibility view
	 */
	String getTitle();

	/**
	 * Set the title.
	 *
	 * @param title the view title
	 */
	void setTitle(String title);

	/**
	 * Gets the item title.
	 *
	 * @return the subtitle of the view
	 */
	String getItemTitle();

	/**
	 * Reload the view data.
	 */
	void reload();

	/**
	 * Refresh data and view state elements.
	 */
	void refresh();

	/**
	 * Refresh save state.
	 */
	void refreshStatusComposite();
}
