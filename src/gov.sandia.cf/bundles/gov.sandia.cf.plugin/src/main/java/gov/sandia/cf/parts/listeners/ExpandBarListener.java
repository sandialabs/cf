/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.listeners;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

/**
 * An expand bar listener to redraw the view depending of the state of the
 * expand bar.
 * 
 * @author Didier Verstraete
 *
 */
public class ExpandBarListener implements ExpandListener {

	private Composite parent;
	private ExpandBar expandBar;
	private boolean autoResize;

	/**
	 * @param parent     the parent composite
	 * @param expandBar  the expand bar
	 * @param autoResize automatically resize the expand bar?
	 */
	public ExpandBarListener(Composite parent, ExpandBar expandBar, boolean autoResize) {
		this.parent = parent;
		this.expandBar = expandBar;
		this.autoResize = autoResize;
		if (this.autoResize) {
			this.parent.addPaintListener(event -> resizeItems());
		}
	}

	/** {@inheritDoc} */
	public void itemCollapsed(ExpandEvent event) {
		if (this.autoResize) {
			resizeItems((ExpandItem) event.item, true);
		}
		asyncShellPack(Display.getCurrent());
	}

	/** {@inheritDoc} */
	public void itemExpanded(ExpandEvent event) {
		if (this.autoResize) {
			resizeItems((ExpandItem) event.item, false);
		}
		asyncShellPack(Display.getCurrent());
	}

	/**
	 * Layout view in a new thread to collapse or expand the bar in the view
	 * (asynchronously to prevent Linux based OS bug because of thread inversion)
	 * 
	 * @param display
	 */
	private void asyncShellPack(final Display display) {
		display.asyncExec(() -> parent.layout());
	}

	/**
	 * Resizes the items with all the space available in the parent
	 * 
	 * @param expandItem
	 * @param collapse
	 */
	private void resizeItems(ExpandItem expandItem, boolean collapse) {

		if (expandItem != null) {

			int globalSize = expandBar.getSize().y - (2 * expandItem.getHeaderHeight()) - 15;

			// count nb expanded
			int nbExpanded = getNbItemExpanded();
			nbExpanded += collapse ? -1 : 1;

			// set item height
			if (nbExpanded > 0) {

				final int height = globalSize / nbExpanded;
				Arrays.stream(expandBar.getItems()).forEach(item -> {
					if (expandItem.equals(item) && !collapse) {
						item.setHeight(height);
					}
					if (item != null && item.getExpanded()) {
						item.setHeight(height);
					}
				});
			}
		}
	}

	/**
	 * Resize the expand bar items depending of the size of the parent
	 */
	private void resizeItems() {

		if (expandBar != null) {

			int globalSize = expandBar.getSize().y;
			if (expandBar.getItemCount() > 0) {
				globalSize = expandBar.getSize().y - (2 * expandBar.getItem(0).getHeaderHeight()) - 15;
			}

			// count nb expanded
			int nbExpanded = getNbItemExpanded();

			// set item height
			if (nbExpanded > 0) {
				final int barSize = globalSize;
				Arrays.stream(expandBar.getItems()).forEach(item -> {
					if (item != null && item.getExpanded()) {
						item.setHeight(barSize / nbExpanded);
					}
				});
			}
		}
	}

	/**
	 * @return the number of items expanded
	 */
	private int getNbItemExpanded() {

		AtomicInteger nbExpanded = new AtomicInteger(0);
		Arrays.stream(expandBar.getItems())
				.forEach(item -> nbExpanded.getAndAdd(item != null && item.getExpanded() ? 1 : 0));

		return nbExpanded.get();
	}

}
