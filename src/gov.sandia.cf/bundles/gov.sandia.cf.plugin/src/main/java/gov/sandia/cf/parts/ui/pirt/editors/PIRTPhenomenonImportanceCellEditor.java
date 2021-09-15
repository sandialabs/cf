/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.parts.ui.pirt.PIRTPhenomenaViewController;
import gov.sandia.cf.tools.RscTools;

/**
 * The PIRT Phenomenon Importance cell editor
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTPhenomenonImportanceCellEditor extends EditingSupport {
	private ComboBoxViewerCellEditor cellEditor = null;
	private PIRTPhenomenaViewController viewCtrl = null;

	/**
	 * Constructor
	 * 
	 * @param viewer   The column viewer
	 * @param viewCtrl The phenomena view controller
	 */
	public PIRTPhenomenonImportanceCellEditor(ColumnViewer viewer, PIRTPhenomenaViewController viewCtrl) {
		super(viewer);

		this.viewCtrl = viewCtrl;

		cellEditor = new ComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);

		// Set content provider
		cellEditor.setContentProvider(
				inputElement -> (inputElement instanceof List) ? ((List<?>) inputElement).toArray() : new Object[] {});

		// Set label provider
		cellEditor.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (element instanceof PIRTLevelImportance)
						? MessageFormat.format("{0} ({1})", ((PIRTLevelImportance) element).getName(), //$NON-NLS-1$
								((PIRTLevelImportance) element).getLabel())
						: null;
			}
		});
		cellEditor.setInput(viewCtrl.getPirtConfiguration().getLevelsListSortedByLevelDescending());
	}

	/** {@inheritDoc} */
	@Override
	protected CellEditor getCellEditor(Object element) {
		if (element instanceof PhenomenonGroup) {
			return new TextCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
		}
		return cellEditor;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean canEdit(Object element) {
		if (element instanceof Phenomenon) {
			return ((Phenomenon) element).getPhenomenonGroup() != null
					&& ((Phenomenon) element).getPhenomenonGroup().getQoi() != null
					&& ((Phenomenon) element).getPhenomenonGroup().getQoi().getTagDate() == null;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	protected Object getValue(Object element) {
		if (element instanceof Phenomenon) {
			Phenomenon phenomenon = (Phenomenon) element;
			return phenomenon.getImportance();
		}
		return RscTools.empty();
	}

	/** {@inheritDoc} */
	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof Phenomenon && value instanceof PIRTLevelImportance) {
			// Set and save phenomenon
			Phenomenon phenomenon = (Phenomenon) element;
			phenomenon.setImportance(((PIRTLevelImportance) value).getIdLabel());
			viewCtrl.updatePhenomenon(phenomenon);
		}
	}

	/**
	 * @return the view controller
	 */
	public PIRTPhenomenaViewController getViewCtrl() {
		return viewCtrl;
	}

}
