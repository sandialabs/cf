/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.ui.ICredibilityView;
import gov.sandia.cf.parts.viewer.editors.AGenericTableCellEditor;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.GenericTableComboCellEditor;
import gov.sandia.cf.parts.viewer.editors.GenericTableDateCellEditor;
import gov.sandia.cf.parts.viewer.editors.GenericTableLabelProvider;
import gov.sandia.cf.parts.viewer.editors.GenericTableRichTextCellEditor;
import gov.sandia.cf.parts.viewer.editors.GenericTableTextCellEditor;

/**
 * The table components factory
 * 
 * @author Didier Verstraete
 *
 */
public class TableFactory {

	/**
	 * Do not instantiate
	 */
	private TableFactory() {
	}

	/**
	 * Add parameter column
	 * 
	 * @param parameter     the parameter to add in tree
	 * @param treeViewer    the treeviewer
	 * @param labelProvider the label provider
	 * @return the tree column created
	 */
	public static TreeViewerColumn createGenericParamTreeColumn(GenericParameter<?> parameter, TreeViewer treeViewer,
			GenericTableLabelProvider labelProvider) {

		// Get Tree and layout
		Tree tree = treeViewer.getTree();
		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) tree.getLayout();

		// Initialize
		TreeViewerColumn tempColumn = null;

		// Cell editor depending of column type
		if (FormFieldType.DATE.getType().equals(parameter.getType())) {
			tempColumn = new TreeViewerColumn(treeViewer, SWT.CENTER);
			tempColumn.getColumn().setText(parameter.getName());
			treeViewerLayout
					.addColumnData(new ColumnWeightData(PartsResourceConstants.GENPARAM_TABLE_DATE_COLUMN_COEFF, true));
		} else if (FormFieldType.FLOAT.getType().equals(parameter.getType())) {
			tempColumn = new TreeViewerColumn(treeViewer, SWT.RIGHT);
			tempColumn.getColumn().setText(parameter.getName());
			treeViewerLayout.addColumnData(
					new ColumnWeightData(PartsResourceConstants.GENPARAM_TABLE_FLOAT_COLUMN_COEFF, true));
		} else if (FormFieldType.CREDIBILITY_ELEMENT.getType().equals(parameter.getType())
				|| FormFieldType.TEXT.getType().equals(parameter.getType())
				|| FormFieldType.RICH_TEXT.getType().equals(parameter.getType())
				|| FormFieldType.LINK.getType().equals(parameter.getType())
				|| FormFieldType.SYSTEM_REQUIREMENT.getType().equals(parameter.getType())) {
			tempColumn = new TreeViewerColumn(treeViewer, SWT.LEFT);
			tempColumn.getColumn().setText(parameter.getName());
			treeViewerLayout
					.addColumnData(new ColumnWeightData(PartsResourceConstants.GENPARAM_TABLE_TEXT_COLUMN_COEFF, true));
		} else if (FormFieldType.SELECT.getType().equals(parameter.getType())) {
			tempColumn = new TreeViewerColumn(treeViewer, SWT.CENTER);
			tempColumn.getColumn().setText(parameter.getName());
			treeViewerLayout.addColumnData(
					new ColumnWeightData(PartsResourceConstants.GENPARAM_TABLE_SELECT_COLUMN_COEFF, true));
		}

		if (null != tempColumn) {
			tempColumn.setLabelProvider(labelProvider);
		}
		return tempColumn;
	}

	/**
	 * @param parameter  the parameter to edit
	 * @param treeViewer the treeviewer
	 * @param view       the view containing the viewer
	 * @return a new generic table cell editor
	 */
	public static AGenericTableCellEditor createGenericParamTableCellEditor(GenericParameter<?> parameter,
			TreeViewer treeViewer, ICredibilityView view) {

		AGenericTableCellEditor cellEditor = null;

		if (FormFieldType.DATE.getType().equals(parameter.getType())) {
			cellEditor = new GenericTableDateCellEditor(view, treeViewer, parameter);
		} else if (FormFieldType.TEXT.getType().equals(parameter.getType())
				|| FormFieldType.LINK.getType().equals(parameter.getType())) {
			cellEditor = new GenericTableTextCellEditor(view, treeViewer, parameter);
		} else if (FormFieldType.RICH_TEXT.getType().equals(parameter.getType())) {
			cellEditor = new GenericTableRichTextCellEditor(view, treeViewer, parameter);
		} else if (FormFieldType.SELECT.getType().equals(parameter.getType())) {
			cellEditor = new GenericTableComboCellEditor(view, treeViewer, parameter);
		}

		return cellEditor;
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param cell   the parent cell
	 * @return a new "Open" action button for table column
	 */
	public static ButtonTheme createOpenButtonColumnAction(ResourceManager rscMgr, ViewerCell cell) {

		if (cell == null) {
			return null;
		}

		// Initialize
		int iconSize = PartsResourceConstants.TABLE_ACTION_ICON_SIZE;

		Map<String, Object> btnOpenItemOptions = new HashMap<>();
		btnOpenItemOptions.put(ButtonTheme.OPTION_TEXT, ""); //$NON-NLS-1$
		btnOpenItemOptions.put(ButtonTheme.OPTION_OUTLINE, false);
		btnOpenItemOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_OPEN);
		btnOpenItemOptions.put(ButtonTheme.OPTION_ICON_SIZE, iconSize);
		btnOpenItemOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_PRIMARY_LIGHT);
		return new ButtonTheme(rscMgr, (Composite) cell.getViewerRow().getControl(), SWT.CENTER, btnOpenItemOptions);
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param cell   the parent cell
	 * @return a new "View" action button for table column
	 */
	public static ButtonTheme createViewButtonColumnAction(ResourceManager rscMgr, ViewerCell cell) {

		if (cell == null) {
			return null;
		}

		// Initialize
		int iconSize = PartsResourceConstants.TABLE_ACTION_ICON_SIZE;

		Map<String, Object> btnViewItemOptions = new HashMap<>();
		btnViewItemOptions.put(ButtonTheme.OPTION_TEXT, ""); //$NON-NLS-1$
		btnViewItemOptions.put(ButtonTheme.OPTION_OUTLINE, false);
		btnViewItemOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_VIEW);
		btnViewItemOptions.put(ButtonTheme.OPTION_ICON_SIZE, iconSize);
		btnViewItemOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
		return new ButtonTheme(rscMgr, (Composite) cell.getViewerRow().getControl(), SWT.CENTER, btnViewItemOptions);
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param cell   the parent cell
	 * @return a new "Edit" action button for table column
	 */
	public static ButtonTheme createEditButtonColumnAction(ResourceManager rscMgr, ViewerCell cell) {

		if (cell == null) {
			return null;
		}

		// Initialize
		int iconSize = PartsResourceConstants.TABLE_ACTION_ICON_SIZE;

		Map<String, Object> btnEditOptions = new HashMap<>();
		btnEditOptions.put(ButtonTheme.OPTION_TEXT, ""); //$NON-NLS-1$
		btnEditOptions.put(ButtonTheme.OPTION_OUTLINE, false);
		btnEditOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_EDIT);
		btnEditOptions.put(ButtonTheme.OPTION_ICON_SIZE, iconSize);
		btnEditOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		return new ButtonTheme(rscMgr, (Composite) cell.getViewerRow().getControl(), SWT.CENTER, btnEditOptions);
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param cell   the parent cell
	 * @return a new "Delete" action button for table column
	 */
	public static ButtonTheme createDeleteButtonColumnAction(ResourceManager rscMgr, ViewerCell cell) {

		if (cell == null) {
			return null;
		}

		// Initialize
		int iconSize = PartsResourceConstants.TABLE_ACTION_ICON_SIZE;

		Map<String, Object> btnDeleteItemOptions = new HashMap<>();
		btnDeleteItemOptions.put(ButtonTheme.OPTION_TEXT, ""); //$NON-NLS-1$
		btnDeleteItemOptions.put(ButtonTheme.OPTION_OUTLINE, false);
		btnDeleteItemOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_DELETE);
		btnDeleteItemOptions.put(ButtonTheme.OPTION_ICON_SIZE, iconSize);
		btnDeleteItemOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_RED);
		return new ButtonTheme(rscMgr, (Composite) cell.getViewerRow().getControl(), SWT.CENTER, btnDeleteItemOptions);
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param cell   the parent cell
	 * @return a new "Add" action button for table column
	 */
	public static ButtonTheme createAddButtonColumnAction(ResourceManager rscMgr, ViewerCell cell) {

		if (cell == null) {
			return null;
		}

		// Initialize
		int iconSize = PartsResourceConstants.TABLE_ACTION_ICON_SIZE;

		Map<String, Object> btnAddOptions = new HashMap<>();
		btnAddOptions.put(ButtonTheme.OPTION_TEXT, ""); //$NON-NLS-1$
		btnAddOptions.put(ButtonTheme.OPTION_OUTLINE, false);
		btnAddOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_ADD);
		btnAddOptions.put(ButtonTheme.OPTION_ICON_SIZE, iconSize);
		btnAddOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		return new ButtonTheme(rscMgr, (Composite) cell.getViewerRow().getControl(), SWT.CENTER, btnAddOptions);
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param cell   the parent cell
	 * @return a new "Copy" action button for table column
	 */
	public static ButtonTheme createCopyButtonColumnAction(ResourceManager rscMgr, ViewerCell cell) {

		if (cell == null) {
			return null;
		}

		// Initialize
		int iconSize = PartsResourceConstants.TABLE_ACTION_ICON_SIZE;

		Map<String, Object> btnDuplicateQoIOptions = new HashMap<>();
		btnDuplicateQoIOptions.put(ButtonTheme.OPTION_TEXT, ""); //$NON-NLS-1$
		btnDuplicateQoIOptions.put(ButtonTheme.OPTION_OUTLINE, false);
		btnDuplicateQoIOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_DUPLICATE);
		btnDuplicateQoIOptions.put(ButtonTheme.OPTION_ICON_SIZE, iconSize);
		btnDuplicateQoIOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
		return new ButtonTheme(rscMgr, (Composite) cell.getViewerRow().getControl(), SWT.CENTER,
				btnDuplicateQoIOptions);
	}

	/**
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param cell   the parent cell
	 * @return a new "Tag" action button for table column
	 */
	public static ButtonTheme createTagButtonColumnAction(ResourceManager rscMgr, ViewerCell cell) {

		if (cell == null) {
			return null;
		}

		// Initialize
		int iconSize = PartsResourceConstants.TABLE_ACTION_ICON_SIZE;

		Map<String, Object> btnTagQoIOptions = new HashMap<>();
		btnTagQoIOptions.put(ButtonTheme.OPTION_TEXT, ""); //$NON-NLS-1$
		btnTagQoIOptions.put(ButtonTheme.OPTION_OUTLINE, false);
		btnTagQoIOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_TAG);
		btnTagQoIOptions.put(ButtonTheme.OPTION_ICON_SIZE, iconSize);
		btnTagQoIOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BROWN);
		return new ButtonTheme(rscMgr, (Composite) cell.getViewerRow().getControl(), SWT.CENTER, btnTagQoIOptions);
	}
}
