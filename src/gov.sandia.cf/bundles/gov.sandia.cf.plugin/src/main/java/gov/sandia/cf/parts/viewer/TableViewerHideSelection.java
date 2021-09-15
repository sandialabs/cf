/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * 
 * The table viewer class for phenomena description
 * 
 * @author Didier Verstraete
 *
 */
public class TableViewerHideSelection extends TableViewer {

	/**
	 * 
	 * The constructor
	 * 
	 * @param parent the parent composite
	 * @param style  the style of the component
	 */
	public TableViewerHideSelection(Composite parent, int style) {
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
	public TableViewerHideSelection(Composite parent, int style, boolean hideSelection, boolean adaptTextWidth) {
		super(parent, style);

		// disable selection when clicking on a row without data
		if (hideSelection) {
			this.getTable().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (getTable().getItem(new Point(e.x, e.y)) == null) {
						setSelection(new StructuredSelection());
					}
				}
			});
		}

		// adapt text width on two columns option
		if (adaptTextWidth) {

			/*
			 * Repaint the tree to adapt the text
			 */
			int margin = 10;
			int maxRow = 2;
			Listener paintListener = event -> {
				switch (event.type) {
				case SWT.MeasureItem:
					setItemSize(event, margin, maxRow);
					break;
				case SWT.PaintItem:
					paintItem(event, margin, maxRow);
					break;
				case SWT.EraseItem:
					event.detail &= ~SWT.FOREGROUND;
					break;
				default:
					break;
				}
			};
			getTable().addListener(SWT.MeasureItem, paintListener);
			getTable().addListener(SWT.PaintItem, paintListener);
			getTable().addListener(SWT.EraseItem, paintListener);
		}

	}

	/**
	 * Compute item size in the table
	 * 
	 * @param event  the event
	 * @param margin the item margin
	 * @param maxRow the max number of rows
	 */
	private void setItemSize(Event event, int margin, int maxRow) {
		TreeItem item = (TreeItem) event.item;
		String text = item.getText(event.index).replace(RscTools.carriageReturn(), RscTools.empty());
		Point size = event.gc.textExtent(text);
		int columnWidth = getTable().getColumn(event.index).getWidth();
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
	 * Paint the item in the table
	 * 
	 * @param event  the event
	 * @param margin the margin
	 * @param maxRow the max row
	 */
	private void paintItem(Event event, int margin, int maxRow) {
		TreeItem item = (TreeItem) event.item;
		String text = item.getText(event.index).replace(RscTools.carriageReturn(), RscTools.empty());
		Point size = event.gc.textExtent(text);
		int columnWidth = getTable().getColumn(event.index).getWidth();
		int textWidth = size.x + 2 * margin;
		if (textWidth > columnWidth && columnWidth > 0) {
			float ratio = (float) textWidth / (float) columnWidth;
			int period = (int) Math.floor((float) text.length() / ratio);
			text = StringTools.insertPeriodically(text, RscTools.carriageReturn(), period);
			if (text != null && (period * maxRow) - 3 > 0 && text.length() > period * maxRow) {
				text = text.substring(0, (period * maxRow) - 3) + RscTools.THREE_DOTS;
			}
			size = event.gc.textExtent(text);
		}
		int offset2 = Math.max(0, (event.height - size.y) / 2);
		event.gc.drawText(text, event.x, event.y + offset2, true);
	}
}
