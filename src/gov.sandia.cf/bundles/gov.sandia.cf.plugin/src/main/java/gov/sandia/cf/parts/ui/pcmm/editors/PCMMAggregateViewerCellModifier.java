/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.jface.viewers.ICellModifier;

import gov.sandia.cf.model.PCMMAggregation;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.parts.ui.pcmm.PCMMAggregateViewController;
import gov.sandia.cf.tools.RscTools;

/**
 * Defines the PCMM assess table cell modifier and all the constants of the
 * table
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAggregateViewerCellModifier implements ICellModifier {

	/**
	 * the column properties
	 */
	private List<String> columnProperties;

	/**
	 * The column indexes
	 */
	private static final int ID_INDEX = 0;
	private static final int NAME_INDEX = 1;
	private static final int LEVEL_INDEX = 2;
	private static final int EVIDENCE_INDEX = 3;
	private static final int COMMENTS_INDEX = 4;

	/**
	 * A table of the modifiable indexes
	 */
	private final int[] modifiableIndexes = {};

	/**
	 * the parent view
	 */
	private PCMMAggregateViewController viewController;

	/**
	 * The constructor.
	 *
	 * @param viewController   the view controller
	 * @param columnProperties the column properties list
	 */
	public PCMMAggregateViewerCellModifier(PCMMAggregateViewController viewController, List<String> columnProperties) {
		this.viewController = viewController;
		this.columnProperties = columnProperties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modify(Object element, String property, Object value) {
		// unused for aggregate. This view is not an editing view.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue(Object element, String property) {
		int index = columnProperties.indexOf(property);

		if (PCMMMode.DEFAULT.equals(viewController.getViewManager().getPCMMConfiguration().getMode())) {
			if (element instanceof PCMMElement && index == NAME_INDEX) {
				return ((PCMMElement) element).getName();
			} else if (element instanceof PCMMSubelement) {

				PCMMSubelement subelt = (PCMMSubelement) element;

				// retrieve the pcmm assessment aggregation for this subelement
				PCMMAggregation<PCMMSubelement> aggregation = viewController.getAggregatedSubelementsMap().get(subelt);

				switch (index) {
				case ID_INDEX:
					return subelt.getCode();
				case NAME_INDEX:
					return subelt.getName();
				case LEVEL_INDEX:
					return aggregation != null && aggregation.getLevel() != null ? aggregation.getLevel().getCode() : 0;
				case EVIDENCE_INDEX:
					return subelt.getEvidenceList();
				case COMMENTS_INDEX:
					return aggregation != null && aggregation.getCommentList() != null ? aggregation.getCommentList()
							: RscTools.empty();
				default:
					return RscTools.empty();
				}
			}
		} else if (PCMMMode.SIMPLIFIED.equals(viewController.getViewManager().getPCMMConfiguration().getMode())
				&& element instanceof PCMMElement) {
			PCMMElement elt = (PCMMElement) element;

			// retrieve the pcmm assessment aggregation for this element
			PCMMAggregation<PCMMElement> aggregation = viewController.getAggregatedElementsMap().get(elt);

			switch (index) {
			case ID_INDEX:
				return RscTools.empty();
			case NAME_INDEX:
				return elt.getName();
			case LEVEL_INDEX:
				return aggregation != null && aggregation.getLevel() != null ? aggregation.getLevel().getCode() : 0;
			case EVIDENCE_INDEX:
				return elt.getEvidenceList();
			case COMMENTS_INDEX:
				return aggregation != null && aggregation.getCommentList() != null ? aggregation.getCommentList()
						: RscTools.empty();
			default:
				return RscTools.empty();
			}
		}
		return RscTools.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canModify(Object element, String property) {
		int currentIndex = columnProperties.indexOf(property);
		return element instanceof PCMMSubelement && isEditableColumnIndex(currentIndex);
	}

	/**
	 * @param columnIndex the column index
	 * @return true if the parameter value is in the modifiable index list,
	 *         otherwise false
	 */
	public boolean isEditableColumnIndex(int columnIndex) {
		return IntStream.of(modifiableIndexes).anyMatch(x -> x == columnIndex);
	}

}
