/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.IGenericTableItem;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.parts.ui.ICredibilityView;
import gov.sandia.cf.tools.RscTools;

/**
 * Generic text cell editing support.
 * 
 * @author Didier Verstraete
 *
 */
public class GenericTableTextCellEditor extends AGenericTableCellEditor {

	private TextCellEditor cellEditor = null;

	/**
	 * Constructor
	 * 
	 * @param view      the view
	 * @param viewer    the column viewer
	 * @param parameter the generic parameter to edit
	 */
	public GenericTableTextCellEditor(ICredibilityView view, ColumnViewer viewer, GenericParameter<?> parameter) {
		super(view, viewer, parameter);

		cellEditor = new TextCellEditor((Composite) getViewer().getControl(), SWT.NONE);
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
					return columnValue.getValue();
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
		if (element instanceof IGenericTableItem && value instanceof String) {
			IGenericTableItem item = (IGenericTableItem) element;

			for (IGenericTableValue columnValue : item.getValueList()) {
				if (columnValue != null && columnValue.getParameter() != null
						&& columnValue.getParameter().equals(getParameter())) {
					columnValue.setValue((String) value);
					getViewer().refresh();
					if (getView() != null) {
						fireValueChanged((IGenericTableItem) element, columnValue);
						getView().getViewController().viewChanged();
					}
					break;
				}
			}
		}
	}
}
