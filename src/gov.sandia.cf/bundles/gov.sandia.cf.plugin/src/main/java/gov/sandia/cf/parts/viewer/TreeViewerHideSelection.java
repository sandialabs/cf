/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

import gov.sandia.cf.tools.RscTools;

/**
 * 
 * The tree viewer class to hide Eclipse default selection especially on Windows
 * 
 * @author Didier Verstraete
 *
 */
public class TreeViewerHideSelection extends TreeViewer {

	/**
	 * 
	 * The constructor
	 * 
	 * @param parent the parent composite
	 * @param style  the style of the component
	 */
	public TreeViewerHideSelection(Composite parent, int style) {
		this(parent, style, true, false);
	}

	/**
	 * 
	 * The constructor
	 * 
	 * @param parent         the parent composite
	 * @param style          the style of the component
	 * @param hideSelection  hide the selection on the table
	 * @param adaptTextWidth adapt the text width
	 */
	public TreeViewerHideSelection(Composite parent, int style, boolean hideSelection, boolean adaptTextWidth) {
		super(parent, style);

		// disable selection when clicking on a row without data
		if (hideSelection) {
			this.getTree().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (getTree().getItem(new Point(e.x, e.y)) == null) {
						setSelection(new StructuredSelection());
					}
				}
			});
		}

		// adapt text width on two columns option
		if (adaptTextWidth) {

			// Repaint the tree to adapt the text
			int margin = 10;
			int maxRow = 2;
			Listener paintListener = event -> {
				switch (event.type) {
				case SWT.MeasureItem:
					setItemSize(event, margin, maxRow);
					break;
				case SWT.EraseItem:
					event.detail &= ~SWT.FOREGROUND;
					break;
				default:
					break;
				}
			};
			getTree().addListener(SWT.MeasureItem, paintListener);
			getTree().addListener(SWT.EraseItem, paintListener);
		}
	}

	/**
	 * Compute item size in the tree
	 * 
	 * @param event  the event
	 * @param margin the item margin
	 * @param maxRow the max number of rows
	 */
	private void setItemSize(Event event, int margin, int maxRow) {
		TreeItem item = (TreeItem) event.item;
		String text = item.getText(event.index).replace(RscTools.carriageReturn(), RscTools.empty());
		Point size = event.gc.textExtent(text);
		int columnWidth = getTree().getColumn(event.index).getWidth();
		int textWidth = size.x + 2 * margin;
		int ratio = 1;
		if (textWidth > columnWidth && columnWidth > 0) {
			ratio = (int) Math.ceil((float) textWidth / (float) columnWidth);
		}
		event.width = size.x;
		event.height = Math.min(event.gc.getFontMetrics().getHeight() * maxRow,
				event.gc.getFontMetrics().getHeight() * ratio);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ViewerRow getViewerRow(Point point) {
		return super.getViewerRow(point);
	}
}
