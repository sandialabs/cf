/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.IGenericTableItem;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.parts.ui.ICredibilityView;
import gov.sandia.cf.tools.RscTools;

/**
 * Generic field combo cell editor.
 * 
 * @author Didier Verstraete
 *
 */
public class GenericTableComboCellEditor extends AGenericTableCellEditor {

	private ComboBoxViewerCellEditor cellEditor = null;

	/**
	 * Constructor
	 * 
	 * @param view      the view
	 * @param viewer    the column viewer
	 * @param parameter the generic parameter to edit
	 */
	public GenericTableComboCellEditor(ICredibilityView view, ColumnViewer viewer, GenericParameter<?> parameter) {
		super(view, viewer, parameter);

		cellEditor = new ComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.NONE);

		// Set content provider
		cellEditor.setContentProvider(
				inputElement -> (inputElement instanceof List) ? ((List<?>) inputElement).toArray() : new Object[] {});

		// Set label provider
		cellEditor.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (element instanceof GenericParameterSelectValue)
						? ((GenericParameterSelectValue<?>) element).getName()
						: null;
			}
		});
		cellEditor.setInput(parameter.getParameterValueList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return cellEditor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		return element instanceof IGenericTableItem;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		if (element instanceof IGenericTableItem) {
			IGenericTableItem item = (IGenericTableItem) element;

			for (IGenericTableValue columnValue : item.getValueList()) {
				if (columnValue != null && columnValue.getParameter() != null
						&& columnValue.getParameter().equals(getParameter())) {
					return columnValue;
				}
			}
		}
		return RscTools.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof IGenericTableItem && value instanceof GenericParameterSelectValue) {
			IGenericTableItem item = (IGenericTableItem) element;

			for (IGenericTableValue columnValue : item.getValueList()) {
				if (columnValue != null && columnValue.getParameter() != null
						&& columnValue.getParameter().equals(getParameter())) {
					columnValue.setValue(((GenericParameterSelectValue<?>) value).getId().toString());
					getViewer().refresh();
					if (getView() != null) {
						fireValueChanged((IGenericTableItem) element, columnValue);
						getView().getViewManager().viewChanged();
					}
					break;
				}
			}
		}
	}
}
