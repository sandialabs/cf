/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import java.util.Date;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.nebula.jface.cdatetime.CDateTimeCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.IGenericTableItem;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.parts.ui.ICredibilityView;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscTools;

/**
 * A date cell editing support for tables and trees.
 * 
 * @author Didier Verstraete
 *
 */
public class GenericTableDateCellEditor extends AGenericTableCellEditor {

	private CDateTimeCellEditor cellEditor = null;

	/**
	 * Constructor
	 * 
	 * @param view      the view
	 * @param viewer    the column viewer
	 * @param parameter the generic parameter to edit
	 */
	public GenericTableDateCellEditor(ICredibilityView view, ColumnViewer viewer, GenericParameter<?> parameter) {
		super(view, viewer, parameter);

		cellEditor = new CDateTimeCellEditor((Composite) getViewer().getControl(), SWT.NONE);
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
					return DateTools.parseDate(columnValue.getValue(), DateTools.getDateTimeFormat());
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
		if (element instanceof IGenericTableItem && value instanceof Date) {
			IGenericTableItem item = (IGenericTableItem) element;

			for (IGenericTableValue columnValue : item.getValueList()) {
				if (columnValue != null && columnValue.getParameter() != null
						&& columnValue.getParameter().equals(getParameter())) {
					columnValue.setValue(DateTools.formatDate((Date) value, DateTools.getDateTimeFormat()));
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
