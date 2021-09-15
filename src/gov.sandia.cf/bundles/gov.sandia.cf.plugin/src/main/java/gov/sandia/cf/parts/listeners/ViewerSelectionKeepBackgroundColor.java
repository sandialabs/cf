/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.listeners;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import gov.sandia.cf.tools.SystemTools;

/**
 * This listener keeps the background color of the viewer item which data type
 * is an implementation of classToKeep.
 * 
 * The viewer labelProvider must be an implemenation of
 * org.eclipse.jface.viewers.IColorProvider
 * 
 * @author Didier Verstraete
 *
 */
public abstract class ViewerSelectionKeepBackgroundColor implements Listener {

	/**
	 * The viewer
	 */
	private ColumnViewer viewer;

	/**
	 * The constructor
	 * 
	 * @param viewer the table viewer
	 */
	public ViewerSelectionKeepBackgroundColor(ColumnViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(Event event) {
		if (this.viewer != null && this.viewer.getControl() != null) {

			Object data = null;
			if (this.viewer instanceof TableViewer) {
				TableItem item = (TableItem) event.item;
				data = item.getData();
			} else if (this.viewer instanceof TreeViewer) {
				TreeItem item = (TreeItem) event.item;
				data = item.getData();
			}

			IBaseLabelProvider treeLabelProvider = this.viewer.getLabelProvider(event.index);
			Color background = null;
			if (treeLabelProvider instanceof IColorProvider) {
				IColorProvider columnLabelProvider = (IColorProvider) treeLabelProvider;
				background = columnLabelProvider.getBackground(data);
			}

			// by default swt libraries uses OS native components, so the behavior is
			// different depending of the OS
			event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLACK));
			if (SystemTools.isWindows()) {
				fillWindowsBackground(event, background);
			} else {
				fillDefaultBackground(event, data);
			}
		}
	}

	/**
	 * Fill table background for Windows based Eclipse platforms.
	 * 
	 * @param event      the event
	 * @param background the background color
	 */
	private void fillWindowsBackground(Event event, Color background) {
		if (background != null && background != event.display.getSystemColor(SWT.COLOR_WHITE)) {
			event.gc.setBackground(background);
			int clientWidth = ((Composite) event.widget).getClientArea().width;
			event.gc.fillRectangle(0, event.y, clientWidth, event.height);
		}
	}

	/**
	 * Fill table background for default OS (UNIX) based Eclipse platforms.
	 * 
	 * @param event the event
	 * @param data  the data
	 */
	private void fillDefaultBackground(Event event, Object data) {

		// check data condition
		if (isConditionFulfilled(data)) {

			// get client width
			int clientWidth = 0;
			if (this.viewer.getControl() instanceof Table) {
				clientWidth = ((Table) this.viewer.getControl()).getClientArea().width;
			} else if (this.viewer.getControl() instanceof Tree) {
				clientWidth = ((Tree) this.viewer.getControl()).getClientArea().width;
			}

			event.gc.fillRectangle(0, event.y, clientWidth, event.height);
			event.detail &= ~SWT.SELECTED;
		}

	}

	/**
	 * @param data the data
	 * @return true if the data must not be higilighted by the selection, otherwise
	 *         false.
	 */
	public abstract boolean isConditionFulfilled(Object data);
}
