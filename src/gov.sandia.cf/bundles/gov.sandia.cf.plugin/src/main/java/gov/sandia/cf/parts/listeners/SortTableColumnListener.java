/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.listeners;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sort Listener, sorts a table viewer by clicking on the header of the table
 * 
 * @author Didier Verstraete
 *
 */
public class SortTableColumnListener implements Listener {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SortTableColumnListener.class);

	/**
	 * the table viewer to link with this listener
	 */
	private TableViewer table;

	/**
	 * @param table the table viewer to link with this listener
	 */
	public SortTableColumnListener(TableViewer table) {
		if (table == null || table.getTable() == null || table.getTable().isDisposed()) {
			throw new IllegalArgumentException("The argument table can not be null or disposed."); //$NON-NLS-1$
		}
		this.table = table;
	}

	/**
	 * {@inheritDoc}
	 */
	public void handleEvent(Event e) {

		if (table == null || table.getTable() == null) {
			logger.error("Impossible to sort a null table"); //$NON-NLS-1$
		} else {
			sortTable((TableColumn) e.widget, table.getTable().getSortDirection());
		}
	}

	/**
	 * 
	 * Sorts the table viewer depending of the row header clicked.
	 * 
	 * @param sortedColumn the column to sort
	 * @param direction    the direction for sorting
	 */
	public void sortTable(TableColumn sortedColumn, int direction) {

		// search sorted column index
		int columnIndex = 0;
		for (TableColumn columnTemp : table.getTable().getColumns()) {
			if (columnTemp != null && columnTemp.equals(sortedColumn)) {
				break;
			}
			columnIndex++;
		}

		// SORT TABLE
		sortTable(columnIndex, direction);

		// set sorted column
		table.getTable().setSortColumn(sortedColumn);

		// set sort direction
		int sortDirection = SWT.UP;
		if (table.getTable().getSortDirection() != SWT.None) {
			sortDirection = table.getTable().getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP;
		}
		table.getTable().setSortDirection(sortDirection);

	}

	/**
	 * Sort table by columnIndex and direction.
	 * 
	 * @param columnIndex
	 * @param direction
	 */
	private void sortTable(int columnIndex, int direction) {

		TableItem[] items = table.getTable().getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());

		if (items != null) {
			for (int i = 1; i < items.length; i++) {
				String value1 = items[i].getText(columnIndex);
				for (int j = 0; j < i; j++) {
					String value2 = items[j].getText(columnIndex);
					if ((direction == SWT.UP && collator.compare(value1, value2) < 0)
							|| (collator.compare(value1, value2) > 0)) {
						items = updateItems(items[i], j);
						break;
					}
				}
			}
		}
	}

	/**
	 * @param oldItem
	 * @param oldItemIdx
	 * @return
	 */
	private TableItem[] updateItems(TableItem oldItem, int oldItemIdx) {
		List<String> values = new ArrayList<>();
		int nbColumns = table.getTable().getColumns().length;
		for (int textIndex = 0; textIndex < nbColumns; textIndex++) {
			values.add(oldItem.getText(textIndex));
		}
		TableItem item = new TableItem(table.getTable(), SWT.NONE, oldItemIdx);
		item.setText(values.stream().toArray(String[]::new));
		item.setData(oldItem.getData());
		item.setImage(oldItem.getImage());
		oldItem.dispose();
		return table.getTable().getItems();
	}
}
