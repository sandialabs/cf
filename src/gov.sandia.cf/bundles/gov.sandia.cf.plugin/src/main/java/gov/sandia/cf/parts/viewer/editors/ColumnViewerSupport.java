/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.parts.constants.PartsResourceConstants;

/**
 * This viewer support enable double click to edit cell content and disable
 * simple click edition
 * 
 * @author Didier Verstraete
 *
 */
public class ColumnViewerSupport implements Listener, ICellModifier {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ColumnViewerSupport.class);
	/**
	 * The viewer to edit
	 */
	private final ColumnViewer viewer;
	/**
	 * The cell modifier for the viewer
	 */
	private final ICellModifier modifier;
	/**
	 * By default, editing is not enabled to avoid simple click edition
	 */
	private boolean isEditingEnabled = false;

	/**
	 * Creates a new TableViewerSupport for @param viewer table
	 * 
	 * @param viewer the table viewer associated
	 */
	public static void enableDoubleClickEditing(ColumnViewer viewer) {
		if (viewer instanceof TableViewer || viewer instanceof TreeViewer) {
			new ColumnViewerSupport(viewer);
		} else {
			logger.warn("ColumnViewer format not taken into account"); //$NON-NLS-1$
		}
	}

	/**
	 * The constructor
	 * 
	 * @param viewer
	 */
	private ColumnViewerSupport(ColumnViewer viewer) {
		this.viewer = viewer;
		modifier = viewer.getCellModifier();
		viewer.setCellModifier(this);
		if (viewer instanceof TableViewer) {
			((TableViewer) viewer).getTable().addListener(PartsResourceConstants.TABLE_VIEWER_SUPPORT_LISTENER_EVENTYPE,
					this);
		} else if (viewer instanceof TreeViewer) {
			((TreeViewer) viewer).getTree().addListener(PartsResourceConstants.TABLE_VIEWER_SUPPORT_LISTENER_EVENTYPE,
					this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void handleEvent(Event event) {
		final Point pt = new Point(event.x, event.y);

		if (viewer instanceof TableViewer) {
			enableEditingColumnForTable(pt);
		} else if (viewer instanceof TreeViewer) {
			enableEditingColumnForTree(pt);
		}
	}

	/**
	 * Enable editing support for tables
	 * 
	 * @param pt the point selected
	 */
	private void enableEditingColumnForTable(Point pt) {
		final TableItem item = ((TableViewer) viewer).getTable().getItem(pt);
		if (item != null) {
			for (int column = 0; column < ((TableViewer) viewer).getTable().getColumnCount(); column++) {
				Rectangle rect = item.getBounds(column);
				if (rect.contains(pt)) {
					isEditingEnabled = true;
					viewer.editElement(item.getData(), column);
					isEditingEnabled = false;
					return;
				}
			}
		}

	}

	/**
	 * Enable editing support for trees.
	 * 
	 * @param pt the point selected
	 */
	private void enableEditingColumnForTree(Point pt) {
		final TreeItem item = ((TreeViewer) viewer).getTree().getItem(pt);
		if (item != null) {
			for (int column = 0; column < ((TreeViewer) viewer).getTree().getColumnCount(); column++) {
				Rectangle rect = item.getBounds(column);
				if (rect.contains(pt)) {
					isEditingEnabled = true;
					viewer.editElement(item.getData(), column);
					isEditingEnabled = false;
					return;
				}
			}
		}
	}

	/**
	 * @return the cell modifier
	 */
	public ICellModifier getModifier() {
		return modifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canModify(Object element, String property) {
		return isEditingEnabled && modifier != null && modifier.canModify(element, property);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getValue(Object element, String property) {
		return modifier != null ? modifier.getValue(element, property) : null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void modify(Object element, String property, Object value) {
		if (modifier != null)
			modifier.modify(element, property, value);
	}
}
