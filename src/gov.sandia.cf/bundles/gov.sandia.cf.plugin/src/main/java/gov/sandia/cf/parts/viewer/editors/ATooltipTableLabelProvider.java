/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.widgets.FancyToolTipSupport;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * Manage cell text and cell tooltips text
 * 
 * @author Maxime N
 */

public abstract class ATooltipTableLabelProvider extends StyledCellLabelProvider {
	/**
	 * viewer
	 */
	private final ColumnViewer viewer;

	/**
	 * Mouse Lister
	 */
	private final Listener mouseListener;

	/**
	 * Current ToolType Text to display
	 */
	private String actualToolTipText = null;

	private ResourceManager rscMgr;

	/**
	 * Construct
	 * 
	 * @param viewMgr the view manager
	 * @param viewer  the column viewer
	 */
	protected ATooltipTableLabelProvider(IViewManager viewMgr, ColumnViewer viewer) {
		// Add Fancy ToolTip
		FancyToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);

		Assert.isNotNull(viewMgr);
		Assert.isNotNull(viewMgr.getRscMgr());
		this.rscMgr = viewMgr.getRscMgr();

		// Set properties
		this.viewer = viewer;
		if (viewer != null) {
			if (viewer instanceof TableViewer && ((TableViewer) this.viewer).getTable() != null) {
				((TableViewer) this.viewer).getTable().setToolTipText(RscTools.empty());
			} else if (viewer instanceof TreeViewer && ((TreeViewer) this.viewer).getTree() != null) {
				((TreeViewer) this.viewer).getTree().setToolTipText(RscTools.empty());
			}
		}
		this.mouseListener = this::handleMouseEvent;
	}

	protected abstract ViewerRow getViewerRow(Point point);

	/**
	 * Get cell text
	 * 
	 * @param element     the element
	 * @param columnIndex the column index
	 * @return the cell text.
	 */
	protected abstract String getCellText(Object element, int columnIndex);

	/**
	 * Set Background color
	 * 
	 * @param element     the element
	 * @param columnIndex the column index
	 * @return the color
	 */
	protected abstract Color getBackground(Object element, int columnIndex);

	/**
	 * Set Foreground color
	 * 
	 * @param element     the element
	 * @param columnIndex the column index
	 * @return the color
	 */
	protected Color getForeground(Object element, int columnIndex) {
		return ColorTools.toColor(rscMgr,
				ConstantTheme.getAssociatedColor(ColorTools.toStringRGB(getBackground(element, columnIndex))));
	}

	/**
	 * Set Font
	 * 
	 * @param element     the element
	 * @param columnIndex the column index
	 * @return the font for the element and index.
	 */
	protected abstract Font getFont(Object element, int columnIndex);

	/**
	 * Add listeners
	 */
	public void install() {

		if (this.viewer != null) {
			if (this.viewer instanceof TableViewer && ((TableViewer) this.viewer).getTable() != null) {
				((TableViewer) this.viewer).getTable().addListener(SWT.Dispose, event -> uninstall());
				((TableViewer) this.viewer).getTable().addListener(SWT.MouseMove, mouseListener);
				((TableViewer) this.viewer).getTable().addListener(SWT.MouseHover, mouseListener);
			} else if (this.viewer instanceof TreeViewer && ((TreeViewer) this.viewer).getTree() != null) {
				((TreeViewer) this.viewer).getTree().addListener(SWT.Dispose, event -> uninstall());
				((TreeViewer) this.viewer).getTree().addListener(SWT.MouseMove, mouseListener);
				((TreeViewer) this.viewer).getTree().addListener(SWT.MouseHover, mouseListener);
			}
		}
	}

	/**
	 * Remove listeners
	 */
	void uninstall() {

		if (this.viewer != null) {
			if (this.viewer instanceof TableViewer && ((TableViewer) this.viewer).getTable() != null) {
				((TableViewer) this.viewer).getTable().removeListener(SWT.MouseMove, mouseListener);
				((TableViewer) this.viewer).getTable().removeListener(SWT.MouseHover, mouseListener);
			} else if (this.viewer instanceof TreeViewer && ((TreeViewer) this.viewer).getTree() != null) {
				((TreeViewer) this.viewer).getTree().removeListener(SWT.MouseMove, mouseListener);
				((TreeViewer) this.viewer).getTree().removeListener(SWT.MouseHover, mouseListener);
			}
		}
	}

	/**
	 * Handle mouse listeners
	 * 
	 * @param event the event to handle
	 */
	private void handleMouseEvent(Event event) {
		setToolTipText(event);
	}

	/**
	 * Set ToolTip Text in
	 * 
	 * @param event the event to handle
	 */
	private void setToolTipText(Event event) {
		Point point = new Point(event.x, event.y);
		ViewerRow row = getViewerRow(point);
		if (row != null) {
			ViewerCell cell = row.getCell(point);
			if (cell != null) {
				CellLabelProvider labelProvider = this.viewer.getLabelProvider(cell.getColumnIndex());
				if (labelProvider != null && 0 < cell.getColumnIndex()) {
					actualToolTipText = getCellText(cell.getElement(), cell.getColumnIndex());
				} else {
					actualToolTipText = null;
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToolTipText(Object element) {
		if (null != actualToolTipText && !actualToolTipText.isEmpty()) {
			return "<html><body><p style='font-size: 14px'>" + StringTools.nl2br(actualToolTipText) //$NON-NLS-1$
					+ "</p></body></html>"; //$NON-NLS-1$
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		cell.setText(getCellText(element, cell.getColumnIndex()));
		cell.setFont(getFont(element, cell.getColumnIndex()));
		cell.setBackground(getBackground(element, cell.getColumnIndex()));
		cell.setForeground(getForeground(element, cell.getColumnIndex()));
	}

}
