/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.TableItem;

import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.constants.TableHeaderBarButtonType;
import gov.sandia.cf.parts.listeners.ExpandBarListener;
import gov.sandia.cf.parts.model.HeaderParts;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.ExpandBarTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.viewer.editors.ColumnViewerSupport;

/**
 * Widget that provides a table with two columns (key, value) displayed into an
 * expandbar
 * 
 * @author Didier Verstraete
 *
 */
public class TableHeaderBar {

	/**
	 * The expand bar header
	 */
	private ExpandBar barHeader;

	/**
	 * The table header
	 */
	private TableHeader tableHeader;

	/**
	 * The parent control
	 */
	private Composite parentControl;

	/**
	 * Butons
	 */
	private Map<TableItem, TableEditor> viewEditors;

	/**
	 * The constructor
	 * 
	 * @param viewMgr       the view manager
	 * @param parentControl the parentControl composite
	 * @param barName       the bar name
	 */
	public TableHeaderBar(IViewManager viewMgr, Composite parentControl, String barName) {
		Assert.isNotNull(viewMgr);
		Assert.isNotNull(parentControl);
		this.parentControl = parentControl;
		this.viewEditors = new HashMap<>();

		// Expand bar
		barHeader = ExpandBarTheme.createExpandBar(this.parentControl, viewMgr.getRscMgr());
		FontTools.setBoldFont(viewMgr.getRscMgr(), barHeader);

		// Expand bar - Title
		ExpandItem item = new ExpandItem(barHeader, SWT.FILL, 0);
		item.setText(barName);
		item.setExpanded(true);

		// Table - Create
		tableHeader = new TableHeader(barHeader, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		GridData gdTableHeader = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		tableHeader.getTable().setLayoutData(gdTableHeader);
		tableHeader.getTable().setHeaderVisible(false);
		tableHeader.getTable().setLinesVisible(true);
		final TableLayout tableHeaderLayout = new TableLayout();
		tableHeader.getTable().setLayout(tableHeaderLayout);

		// Table - Create columns
		TableViewerColumn nameC = new TableViewerColumn(tableHeader, SWT.LEFT);
		tableHeaderLayout
				.addColumnData(new ColumnPixelData(PartsResourceConstants.PHEN_VIEW_TABLEHEADER_KEYCOLUMN_WIDTH, true));
		nameC.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (element instanceof HeaderParts) ? ((HeaderParts<?>) element).getName() : null;
			}
		});

		TableViewerColumn valueC = new TableViewerColumn(tableHeader, SWT.LEFT);
		tableHeaderLayout
				.addColumnData(new ColumnPixelData(PartsResourceConstants.PHEN_VIEW_TABLEHEADER_VALCOLUMN_WIDTH, true));
		valueC.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (element instanceof HeaderParts) ? ((HeaderParts<?>) element).getValue() : null;
			}
		});

		TableViewerColumn btnC = new TableViewerColumn(tableHeader, SWT.LEFT);
		tableHeaderLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.PHEN_VIEW_TABLEHEADER_BTNCOLUMN_WIDTH, false));
		btnC.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				// Get item
				TableItem item = (TableItem) cell.getItem();
				Object element = cell.getElement();
				HeaderParts<?> header = (HeaderParts<?>) element;

				// View editor
				TableEditor editor = null;

				if (!viewEditors.containsKey(element) && null != header.getBtnListener()) {
					// Initialize
					int iconSize = PartsResourceConstants.TABLE_ACTION_ICON_SIZE;

					// View buttons
					Map<String, Object> btnViewItemOptions = new HashMap<>();
					btnViewItemOptions.put(ButtonTheme.OPTION_TEXT, ""); //$NON-NLS-1$
					btnViewItemOptions.put(ButtonTheme.OPTION_OUTLINE, false);
					if (TableHeaderBarButtonType.VIEW.equals(header.getBtnType())) {
						btnViewItemOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_VIEW);
						btnViewItemOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
					} else {
						btnViewItemOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_EDIT);
						btnViewItemOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
					}

					btnViewItemOptions.put(ButtonTheme.OPTION_ICON_SIZE, iconSize);
					ButtonTheme btnViewItem = new ButtonTheme(viewMgr.getRscMgr(),
							(Composite) cell.getViewerRow().getControl(), SWT.CENTER, btnViewItemOptions);

					// View buttons Listener
					btnViewItem.addListener(SWT.Selection, header.getBtnListener());

					// Draw cell
					editor = new TableEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(btnViewItem, item, cell.getColumnIndex());

					viewEditors.put(item, editor);
				}

			}
		});

		// Expend bar - Title resize
		item.setHeight(tableHeader.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item.setControl(tableHeader.getControl());

		// table editors, modifiers, providers
		String[] columnProperties = new String[TableHeader.COLUMNS_PROPERTIES.size()];
		columnProperties = TableHeader.COLUMNS_PROPERTIES.toArray(columnProperties);
		tableHeader.setColumnProperties(columnProperties);

		tableHeader.setCellEditors(new CellEditor[] { new TextCellEditor(tableHeader.getTable()),
				new TextCellEditor(tableHeader.getTable()) });

		// layout current view depending of the bar state (collapsed/expanded)
		barHeader.addExpandListener(new ExpandBarListener(parentControl, barHeader, false));
	}

	/**
	 * @return the table header
	 */
	public TableHeader getTableHeader() {
		return tableHeader;
	}

	/**
	 * @return the parent control
	 */
	public Composite getParentControl() {
		return parentControl;
	}

	/**
	 * Sets table input
	 * 
	 * @param input the data input to set
	 */
	public void setInput(Object input) {
		getTableHeader().setInput(input);

		// resize bar header items
		computeItemSize();
	}

	/**
	 * Sets personalized cell modifier
	 * 
	 * @param cellModifier the cell modifier to set
	 */
	public void setCellModifier(ICellModifier cellModifier) {

		// set cell modifier
		getTableHeader().setCellModifier(cellModifier);

		// table modifications on double click
		ColumnViewerSupport.enableDoubleClickEditing(tableHeader);
	}

	/**
	 * Sets personalized content provider
	 * 
	 * @param contentProvider the content provider to set
	 */
	public void setContentProvider(IContentProvider contentProvider) {
		getTableHeader().setContentProvider(contentProvider);
	}

	/**
	 * Update the bar name
	 * 
	 * @param newName the new name of the bar to set
	 */
	public void updateHeaderBarName(String newName) {
		barHeader.getItem(0).setText(newName);
	}

	/**
	 * Resizes bar header items depending of the content
	 */
	private void computeItemSize() {
		int tableHeight = tableHeader.getInput() != null
				? tableHeader.getTable().getItemHeight() * tableHeader.getTable().getItemCount()
				: SWT.DEFAULT;
		barHeader.getItem(0).setHeight(tableHeader.getControl().computeSize(SWT.DEFAULT, tableHeight).y);
	}

	/**
	 * Refresh the table viewer
	 */
	public void refreshViewer() {
		tableHeader.refresh();
	}

}
