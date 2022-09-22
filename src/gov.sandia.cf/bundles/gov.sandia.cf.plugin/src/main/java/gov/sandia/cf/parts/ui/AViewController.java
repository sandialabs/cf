/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Control;

import gov.sandia.cf.web.WebEvent;

/**
 * The Class AViewController.
 *
 * @author Didier Verstraete
 * @param <M> the generic type
 * @param <V> the view manager
 */
public abstract class AViewController<M extends IViewManager, V extends ICredibilityView> implements IViewController {

	/** The view manager. */
	private M viewManager;

	/** The view. */
	private V view;

	/**
	 * Instantiates a new a view controller.
	 *
	 * @param viewManager the view manager
	 */
	protected AViewController(M viewManager) {
		Assert.isNotNull(viewManager);
		this.viewManager = viewManager;
	}

	/**
	 * Gets the view manager.
	 *
	 * @return the view manager
	 */
	@Override
	public M getViewManager() {
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

	/**
	 * View changed.
	 */
	public void viewChanged() {
		getViewManager().viewChanged();
	}

	/**
	 * Sets the view.
	 *
	 * @param view the new view
	 */
	protected void setView(V view) {
		this.view = view;
	}

	/**
	 * Gets the view.
	 *
	 * @return the view
	 */
	public V getView() {
		return view;
	}

	@Override
	public Control getViewControl() {
		return (Control) getView();
	}

	@Override
	public void quit() {
		// default implement is doing nothing. To override.
	}

	@Override
	public void handleWebEvent(WebEvent e) {
		// default implement is doing nothing. To override.
	}

}
