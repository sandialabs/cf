/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.parts.ui.pcmm.PCMMAssessViewController;
import gov.sandia.cf.tools.RscTools;

/**
 * Defines the PCMM assess table cell modifier and all the constants of the
 * table
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAssessViewerSimplifiedCellModifier extends PCMMAssessViewerCellModifier implements ICellModifier {

	/**
	 * The constructor
	 * 
	 * @param viewCtrl the view controller
	 */
	public PCMMAssessViewerSimplifiedCellModifier(PCMMAssessViewController viewCtrl) {
		super(viewCtrl);
	}

	/** {@inheritDoc} */
	@Override
	public void modify(Object element, String property, Object value) {
		PCMMElement elt = null;

		if (element != null) {

			// if the object is an item
			if (element instanceof Item) {
				Item item = (Item) element;

				if (item.getData() instanceof PCMMElement) {
					elt = (PCMMElement) item.getData();
				}
			}

			// if it is already an element
			if (element instanceof PCMMElement) {
				elt = (PCMMElement) element;
			}

			// assess
			getViewCtrl().assessSimplifiedFromCellModifier(elt, property, value);

		}
	}

	/** {@inheritDoc} */
	@Override
	public Object getValue(Object element, String property) {
		if (!(element instanceof PCMMElement)) {
			return RscTools.empty();
		}

		int index = getViewCtrl().getColumnIndex(property);

		// retrieve pcmm assessment from element
		PCMMElement elt = (PCMMElement) element;
		PCMMAssessment assessment = getViewCtrl().getAssessmentsByElt().get(elt);

		switch (index) {
		case PCMMAssessViewController.ID_INDEX:
			return RscTools.empty();
		case PCMMAssessViewController.NAME_INDEX:
			return elt.getName();
		case PCMMAssessViewController.LEVEL_INDEX:
			return assessment != null && assessment.getLevel() != null ? assessment.getLevel().getId() : 0;
		case PCMMAssessViewController.EVIDENCE_INDEX:
			return elt.getEvidenceList();
		case PCMMAssessViewController.COMMENTS_INDEX:
			return assessment != null && assessment.getComment() != null ? assessment.getComment() : RscTools.empty();
		default:
			return RscTools.empty();
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean canModify(Object element, String property) {
		int currentIndex = getViewCtrl().getColumnIndex(property);

		// can not update in tag mode
		boolean isNotTagMode = !getViewCtrl().isTagMode();

		// update only non null element and instanceof PCMMElement
		boolean isValidElement = element instanceof PCMMElement;

		// update pcmm elements for editable columns
		boolean isEditableColumn = isEditableColumnIndex(currentIndex);

		// update only the selected pcmm element
		boolean isSelectedPCMMElement = false;
		if (isValidElement)
			isSelectedPCMMElement = getViewCtrl().getPcmmElement() != null
					&& getViewCtrl().getPcmmElement().equals((element));

		return isNotTagMode && isValidElement && isEditableColumn && isSelectedPCMMElement;
	}

}
