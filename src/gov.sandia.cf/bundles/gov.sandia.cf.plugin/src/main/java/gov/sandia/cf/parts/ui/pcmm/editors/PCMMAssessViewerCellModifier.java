/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import java.util.stream.IntStream;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.parts.ui.pcmm.PCMMAssessViewController;
import gov.sandia.cf.tools.RscTools;

/**
 * Defines the PCMM assess table cell modifier and all the constants of the
 * table
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAssessViewerCellModifier implements ICellModifier {

	/**
	 * the view controller
	 */
	private PCMMAssessViewController viewCtrl;

	/**
	 * A table of the modifiable indexes
	 */
	private final int[] modifiableIndexes = { PCMMAssessViewController.LEVEL_INDEX,
			PCMMAssessViewController.COMMENTS_INDEX };

	/**
	 * The constructor
	 */
	/**
	 * @param viewCtrl the view controller
	 */
	public PCMMAssessViewerCellModifier(PCMMAssessViewController viewCtrl) {
		this.viewCtrl = viewCtrl;
	}

	/** {@inheritDoc} */
	@Override
	public void modify(Object element, String property, Object value) {
		PCMMSubelement subelt = null;

		if (element != null) {

			// if the object is an item
			if (element instanceof Item) {
				Item item = (Item) element;

				if (item.getData() instanceof PCMMSubelement) {
					subelt = (PCMMSubelement) item.getData();
				}
			}

			// if it is already a subelement
			if (element instanceof PCMMSubelement) {
				subelt = (PCMMSubelement) element;
			}

			// assess
			viewCtrl.assessSubelementFromCellModifier(subelt, property, value);

		}
	}

	/** {@inheritDoc} */
	@Override
	public Object getValue(Object element, String property) {
		int index = viewCtrl.getColumnIndex(property);
		if (element instanceof PCMMElement && index == PCMMAssessViewController.NAME_INDEX) {
			return ((PCMMElement) element).getName();
		} else if (element instanceof PCMMSubelement) {

			// retrieve pcmm assessment from subelement
			PCMMSubelement subelt = (PCMMSubelement) element;
			PCMMAssessment assessment = viewCtrl.getAssessmentsBySubelt().get(subelt);

			switch (index) {
			case PCMMAssessViewController.ID_INDEX:
				return subelt.getCode();
			case PCMMAssessViewController.NAME_INDEX:
				return subelt.getName();
			case PCMMAssessViewController.LEVEL_INDEX:
				return assessment != null && assessment.getLevel() != null ? assessment.getLevel().getId() : 0;
			case PCMMAssessViewController.EVIDENCE_INDEX:
				return subelt.getEvidenceList();
			case PCMMAssessViewController.COMMENTS_INDEX:
				return assessment != null && assessment.getComment() != null ? assessment.getComment()
						: RscTools.empty();
			default:
				return RscTools.empty();
			}
		}
		return RscTools.empty();
	}

	/** {@inheritDoc} */
	@Override
	public boolean canModify(Object element, String property) {
		int currentIndex = viewCtrl.getColumnIndex(property);

		// can not update in tag mode
		boolean isNotTagMode = !viewCtrl.isTagMode();

		// update only non null element and instanceof PCMMSubelement
		boolean isValidElement = element instanceof PCMMSubelement;

		// update pcmm subelements for editable columns
		boolean isEditableColumn = isEditableColumnIndex(currentIndex);

		// update only the selected pcmm element
		boolean isSelectedPCMMElement = false;
		if (isValidElement)
			isSelectedPCMMElement = viewCtrl.getElementSelected() != null
					&& viewCtrl.getElementSelected().equals(((PCMMSubelement) element).getElement());

		return isNotTagMode && isValidElement && isEditableColumn && isSelectedPCMMElement;
	}

	/**
	 * @param columnIndex the column index
	 * @return true if the parameter value is in the modifiable index list,
	 *         otherwise false.
	 */
	public boolean isEditableColumnIndex(int columnIndex) {
		return IntStream.of(modifiableIndexes).anyMatch(x -> x == columnIndex);
	}

	@SuppressWarnings("javadoc")
	public PCMMAssessViewController getViewCtrl() {
		return viewCtrl;
	}

}
