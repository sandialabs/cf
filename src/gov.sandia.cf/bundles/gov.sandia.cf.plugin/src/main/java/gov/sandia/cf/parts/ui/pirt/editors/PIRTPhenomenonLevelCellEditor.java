/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnViewer;

import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.parts.ui.pirt.PIRTPhenomenaViewController;

/**
 * The PIRT Phenomenon Level cell editor class
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTPhenomenonLevelCellEditor extends PIRTPhenomenonImportanceCellEditor {
	/**
	 * The PIRT adequacy column
	 */
	private PIRTAdequacyColumn adequacy = null;

	/**
	 * Construct
	 * 
	 * @param viewer   The column viewer
	 * @param viewCtrl The phenomena view controller
	 * @param adequacy the adequacy column
	 */
	public PIRTPhenomenonLevelCellEditor(ColumnViewer viewer, PIRTPhenomenaViewController viewCtrl,
			PIRTAdequacyColumn adequacy) {
		super(viewer, viewCtrl);
		this.adequacy = adequacy;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof Phenomenon && value instanceof PIRTLevelImportance) {
			// Set and save phenomenon
			Phenomenon phenomenon = (Phenomenon) element;
			List<Criterion> criterionToUpdate = new ArrayList<>();
			phenomenon.getCriterionList().forEach(c -> {
				if (c != null && c.getName() != null && c.getName().equals(this.adequacy.getName())) {
					c.setValue(((PIRTLevelImportance) value).getName());
					criterionToUpdate.add(c);
				}
			});

			if (!criterionToUpdate.isEmpty()) {
				criterionToUpdate.forEach(c -> getViewCtrl().updateCriterion(c));
			}
		}
	}
}
