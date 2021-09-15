/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Auto Resize layout for Table and Tree
 * 
 * @author Maxime N
 */
public class AutoResizeViewerLayout extends TableLayout implements ControlListener {
	/**
	 * The table
	 */
	private final ColumnViewer viewer;

	/**
	 * The columns list
	 */
	private List<ColumnLayoutData> columns = new ArrayList<>();

	/**
	 * The auto resize flag
	 */
	private boolean autosizing = false;

	private int firstWidth;

	/**
	 * Construct
	 * 
	 * @param viewer the column viewer
	 */
	public AutoResizeViewerLayout(ColumnViewer viewer) {
		this.viewer = viewer;
		viewer.getControl().addControlListener(this);
		this.firstWidth = -1000;
	}

	/**
	 * Add column data
	 * 
	 * @param data the column layout data
	 */
	@Override
	public void addColumnData(ColumnLayoutData data) {
		columns.add(data);
		super.addColumnData(data);
	}

	/**
	 * Move control
	 * 
	 * @param e The control event
	 */
	public void controlMoved(ControlEvent e) {
		// not used
	}

	/**
	 * Resize control
	 * 
	 * @param e The control event
	 */
	public void controlResized(ControlEvent e) {

		if (autosizing)
			return;
		if (firstWidth != ((Composite) viewer.getControl()).getClientArea().width) {
			firstWidth = ((Composite) viewer.getControl()).getClientArea().width;
			autosizing = true;
			try {
				autoSizeColumns();
			} finally {
				autosizing = false;
			}
		}
	}

	/**
	 * Auto resize
	 */
	private void autoSizeColumns() {

		int width = ((Composite) viewer.getControl()).getClientArea().width;

		// Layout is being called with an invalid value the first time it is being
		// called on Linux.
		// This method resets the layout to null, so we run it only when the value is
		// OK.
		if (width <= 1)
			return;

		int nbColumnsViewer = 0;
		if (viewer.getControl() instanceof Table) {
			nbColumnsViewer = ((Table) viewer.getControl()).getColumns().length;
		} else if (viewer.getControl() instanceof Tree) {
			nbColumnsViewer = ((Tree) viewer.getControl()).getColumns().length;
		}

		int size = Math.min(columns.size(), nbColumnsViewer);
		int[] widths = new int[size];
		int fixedWidth = 0;
		int numberOfWeightColumns = 0;
		int totalWeight = 0;

		// First compute space occupied by fixed columns
		for (int i = 0; i < size; i++) {
			ColumnLayoutData col = columns.get(i);
			if (col instanceof ColumnPixelData) {
				int pixels = ((ColumnPixelData) col).width;
				widths[i] = pixels;
				fixedWidth += pixels;
			} else if (col instanceof ColumnWeightData) {
				ColumnWeightData cw = (ColumnWeightData) col;
				numberOfWeightColumns++;
				int weight = cw.weight;
				totalWeight += weight;
			} else {
				throw new IllegalStateException("Unknown column layout data"); //$NON-NLS-1$
			}
		}

		// Do we have columns that have a weight?
		if (numberOfWeightColumns > 0) {
			// Now, distribute the rest to the columns with weight.
			// Make sure there's enough room, even if we have to scroll.
			if (width < fixedWidth + totalWeight)
				width = fixedWidth + totalWeight;
			int rest = width - fixedWidth;
			int totalDistributed = 0;
			for (int i = 0; i < size; i++) {
				ColumnLayoutData col = columns.get(i);
				if (col instanceof ColumnWeightData) {
					ColumnWeightData cw = (ColumnWeightData) col;
					int weight = cw.weight;
					int pixels = totalWeight == 0 ? 0 : weight * rest / totalWeight;
					if (pixels < cw.minimumWidth)
						pixels = cw.minimumWidth;
					totalDistributed += pixels;
					widths[i] = pixels;
				}
			}

			// Distribute any remaining pixels to columns with the correct weight
			int diff = rest - totalDistributed;
			for (int i = 0; diff > 0; i++) {
				if (i == size)
					i = 0;
				ColumnLayoutData col = columns.get(i);
				if (col instanceof ColumnWeightData) {
					++widths[i];
					--diff;
				}
			}
		}

		// resize the viewer columns
		if (viewer.getControl() instanceof Table) {
			TableColumn[] tableColumns = ((Table) viewer.getControl()).getColumns();
			for (int i = 0; i < size; i++) {
				if (tableColumns[i].getWidth() != widths[i])
					tableColumns[i].setWidth(widths[i]);
			}
		} else if (viewer.getControl() instanceof Tree) {
			TreeColumn[] treeColumns = ((Tree) viewer.getControl()).getColumns();
			for (int i = 0; i < size; i++) {
				if (treeColumns[i].getWidth() != widths[i])
					treeColumns[i].setWidth(widths[i]);
			}
		}
	}
}