/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.parts.theme.ConstantTheme;

/**
 * The PCMM Evidence column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceColumnLabelProvider extends ColumnLabelProvider {

	/**
	 * The evidence view
	 */
	private PCMMEvidenceView view;

	/**
	 * Constructor
	 * 
	 * @param view the evidence view
	 */
	public PCMMEvidenceColumnLabelProvider(PCMMEvidenceView view) {
		super();
		Assert.isNotNull(view);
		Assert.isNotNull(view.getViewManager());

		this.view = view;
	}

	@Override
	public Color getBackground(Object element) {
		return getTreeCellBackground(element);
	}

	@Override
	public Color getForeground(Object element) {
		return getTreeCellForeground(element);
	}

	/**
	 * Get cell background color
	 * 
	 * @param element the element
	 * @return Color the color
	 */
	private Color getTreeCellBackground(Object element) {

		// PCMM Element in light primary
		if (element instanceof PCMMElement) {
			if (!element.equals(view.getPcmmElement())) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY);
			} else {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT);
			}
		}
		// PCMM Sub-element in light gray
		else if (element instanceof PCMMSubelement) {
			if (((PCMMSubelement) element).getElement() == null
					|| !((PCMMSubelement) element).getElement().equals(view.getPcmmElement())) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY_LIGHT);
			} else {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT_2);
			}
		}
		// PCMM Evidence in secondary light gray
		if (element instanceof PCMMEvidence && !view.isFromCurrentPCMMElement((PCMMEvidence) element)) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY_LIGHT);
		}

		return null;
	}

	/**
	 * Get cell foreground color
	 * 
	 * @param element the element
	 * @return Color the color
	 */
	private Color getTreeCellForeground(Object element) {
		if (element == null || element instanceof PCMMElement) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE);
		} else if (element instanceof PCMMSubelement) {
			if (((PCMMSubelement) element).getElement() == null
					|| !((PCMMSubelement) element).getElement().equals(view.getPcmmElement())) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY);
			} else {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK);
			}
		} else if (element instanceof PCMMEvidence) {
			if (!view.isFromCurrentPCMMElement((PCMMEvidence) element)) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY);
			} else {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK);
			}
		}

		return null;
	}

	/**
	 * @return the pcmm evidence view
	 */
	public PCMMEvidenceView getView() {
		return this.view;
	}
}
