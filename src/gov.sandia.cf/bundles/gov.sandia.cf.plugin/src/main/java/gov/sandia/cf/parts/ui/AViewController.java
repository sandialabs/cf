/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui;

import org.eclipse.core.runtime.Assert;

/**
 * The Class AViewController.
 *
 * @param <V> the view manager
 * 
 * @author Didier Verstraete
 */
public abstract class AViewController<V extends IViewManager> implements IViewController {

	/**
	 * The view manager
	 */
	private V viewManager;

	/**
	 * Instantiates a new a view controller.
	 *
	 * @param viewManager the view manager
	 */
	protected AViewController(V viewManager) {
		Assert.isNotNull(viewManager);
		this.viewManager = viewManager;
	}

	/**
	 * @return the view manager
	 */
	@Override
	public V getViewManager() {
		return viewManager;
	}

	/**
	 * Refresh the view.
	 */
	@Override
	public void refresh() {
		getView().refresh();
	}

	/**
	 * Gets the item title.
	 *
	 * @return the item title
	 */
	@Override
	public String getItemTitle() {
		return getView().getItemTitle();
	}

	/**
	 * Reload.
	 */
	@Override
	public void reload() {
		getView().reload();
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	@Override
	public String getTitle() {
		return getView().getTitle();
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	@Override
	public void setTitle(String title) {
		getView().setTitle(title);
	}

	/**
	 * Refresh save state.
	 */
	@Override
	public void refreshSaveState() {
		getView().refreshStatusComposite();
	}
}
