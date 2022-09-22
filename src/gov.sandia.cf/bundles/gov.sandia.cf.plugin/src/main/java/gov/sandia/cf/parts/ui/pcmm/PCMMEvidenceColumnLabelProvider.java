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
import gov.sandia.cf.tools.ColorTools;

/**
 * The PCMM Evidence column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceColumnLabelProvider extends ColumnLabelProvider {

	/**
	 * The evidence view controller
	 */
	private PCMMEvidenceViewController viewController;

	/**
	 * Constructor.
	 *
	 * @param viewController the view controller
	 */
	public PCMMEvidenceColumnLabelProvider(PCMMEvidenceViewController viewController) {
		super();
		Assert.isNotNull(viewController);
		Assert.isNotNull(viewController.getViewManager());

		this.viewController = viewController;
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
			if (!element.equals(viewController.getElementSelected())) {
				return ColorTools.toColor(viewController.getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY));
			} else {
				return ColorTools.toColor(viewController.getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT));
			}
		}
		// PCMM Sub-element in light gray
		else if (element instanceof PCMMSubelement) {
			if (((PCMMSubelement) element).getElement() == null
					|| !((PCMMSubelement) element).getElement().equals(viewController.getElementSelected())) {
				return ColorTools.toColor(viewController.getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY_LIGHT));
			} else {
				return ColorTools.toColor(viewController.getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT_2));
			}
		}
		// PCMM Evidence in secondary light gray
		if (element instanceof PCMMEvidence && !viewController.isFromCurrentPCMMElement((PCMMEvidence) element)) {
			return ColorTools.toColor(viewController.getViewManager().getRscMgr(),
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY_LIGHT));
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
			return ColorTools.toColor(viewController.getViewManager().getRscMgr(),
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));
		} else if (element instanceof PCMMSubelement) {
			if (((PCMMSubelement) element).getElement() == null
					|| !((PCMMSubelement) element).getElement().equals(viewController.getElementSelected())) {
				return ColorTools.toColor(viewController.getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY));
			} else {
				return ColorTools.toColor(viewController.getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK));
			}
		} else if (element instanceof PCMMEvidence) {
			if (!viewController.isFromCurrentPCMMElement((PCMMEvidence) element)) {
				return ColorTools.toColor(viewController.getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY));
			} else {
				return ColorTools.toColor(viewController.getViewManager().getRscMgr(),
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK));
			}
		}

		return null;
	}

	/**
	 * @return the pcmm evidence view controller
	 */
	public PCMMEvidenceViewController getViewController() {
		return this.viewController;
	}
}
