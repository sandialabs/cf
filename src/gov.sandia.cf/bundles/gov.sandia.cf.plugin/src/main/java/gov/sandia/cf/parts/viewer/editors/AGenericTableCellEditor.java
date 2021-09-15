/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.IGenericTableItem;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.parts.listeners.IGenericTableValueListener;
import gov.sandia.cf.parts.ui.ICredibilityView;

/**
 * Abstract generic fields cell editor.
 * 
 * @author Didier Verstraete
 *
 */
public abstract class AGenericTableCellEditor extends EditingSupport {

	private ICredibilityView view = null;
	private GenericParameter<?> parameter = null;

	/**
	 * List of cell editor listeners (element type:
	 * <code>IGenericTableValueListener</code>).
	 */
	private ListenerList<IGenericTableValueListener> listeners = new ListenerList<>();

	/**
	 * Constructor
	 * 
	 * @param viewer    the column viewer
	 * @param view      the view
	 * @param parameter the generic parameter to edit
	 */
	protected AGenericTableCellEditor(ICredibilityView view, ColumnViewer viewer, GenericParameter<?> parameter) {
		super(viewer);
		Assert.isNotNull(parameter, "Generic Parameter is not allowed to be null"); //$NON-NLS-1$

		this.view = view;
		this.parameter = parameter;
	}

	protected ICredibilityView getView() {
		return view;
	}

	protected GenericParameter<?> getParameter() {
		return parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		return element instanceof IGenericTableItem;
	}

	/**
	 * @param listener the cell editor change listener
	 */
	public void addValueChangedListener(IGenericTableValueListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener the cell editor change listener
	 */
	public void removeValueChangedListener(IGenericTableValueListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notifies all registered cell editor listeners of a value change.
	 * 
	 * @see IGenericTableValueListener#valueChanged
	 * 
	 * @param item  the table item changed
	 * @param value the new value
	 */
	protected void fireValueChanged(final IGenericTableItem item, final IGenericTableValue value) {
		for (IGenericTableValueListener l : listeners) {
			SafeRunnable.run(new SafeRunnable() {
				@Override
				public void run() {
					l.valueChanged(item, value);
				}
			});
		}
	}
}
