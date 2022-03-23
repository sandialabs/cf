/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.guidance;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;

import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.viewer.TreeViewerHideSelection;
import gov.sandia.cf.parts.viewer.editors.ATooltipTableLabelProvider;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage cell text and cell tooltips text
 * 
 * @author Maxime N
 */

public class PIRTGuidelinesLabelProvider extends ATooltipTableLabelProvider {
	/**
	 * Tree
	 */
	private final TreeViewerHideSelection viewer;
	/**
	 * The view manager
	 */
	private IViewManager viewMgr;

	/** PIRT Guidelines table NAME index */
	public static final int NAME_COLUMN_INDEX = 0;

	/** PIRT Guidelines table DESCRIPTION index */
	public static final int DESCRIPTION_COLUMN_INDEX = 1;

	/**
	 * Construct
	 * 
	 * @param viewMgr the view manager
	 * @param viewer  the tree viewer
	 */
	public PIRTGuidelinesLabelProvider(IViewManager viewMgr, TreeViewerHideSelection viewer) {
		super(viewMgr, viewer);

		Assert.isNotNull(viewMgr);
		this.viewMgr = viewMgr;

		// Set properties
		this.viewer = viewer;
	}

	/** {@inheritDoc} */
	@Override
	protected ViewerRow getViewerRow(Point point) {
		return viewer.getViewerRow(point);
	}

	/** {@inheritDoc} */
	@Override
	protected String getCellText(Object element, int columnIndex) {

		String text = RscTools.empty();

		switch (columnIndex) {
		case NAME_COLUMN_INDEX:
			if (element instanceof PIRTAdequacyColumnGuideline) {
				text = ((PIRTAdequacyColumnGuideline) element).getName();
			} else if (element instanceof PIRTAdequacyColumnLevelGuideline) {
				text = ((PIRTAdequacyColumnLevelGuideline) element).getName();
			}
			break;
		case DESCRIPTION_COLUMN_INDEX:
			if (element instanceof PIRTAdequacyColumnGuideline) {
				text = ((PIRTAdequacyColumnGuideline) element).getDescription();
			} else if (element instanceof PIRTAdequacyColumnLevelGuideline) {
				text = ((PIRTAdequacyColumnLevelGuideline) element).getDescription();
			}
			break;
		default:
			break;
		}
		return text;
	}

	/** {@inheritDoc} */
	@Override
	public Color getBackground(Object element, int columnIndex) {
		return getBackground(element);
	}

	/** {@inheritDoc} */
	@Override
	protected Font getFont(Object element, int columnIndex) {
		Font defaultFont = viewer.getControl().getFont();
		FontData defaultFontData = (defaultFont.getFontData() != null && defaultFont.getFontData().length > 0)
				? defaultFont.getFontData()[0]
				: new FontData();
		if (columnIndex == NAME_COLUMN_INDEX && (element instanceof PIRTAdequacyColumnGuideline
				|| element instanceof PIRTAdequacyColumnLevelGuideline)) {
			defaultFontData.setStyle(SWT.BOLD);
		}
		return FontTools.getFont(viewMgr.getRscMgr(), defaultFontData);
	}

	/**
	 * Gets the background color.
	 *
	 * @param element the element
	 * @return the background
	 */
	private Color getBackground(Object element) {
		return ColorTools.toColor(viewMgr.getRscMgr(), getBackgroundColorValue(element));
	}

	/**
	 * Gets the background color value.
	 *
	 * @param element the element
	 * @return the background color value
	 */
	private String getBackgroundColorValue(Object element) {
		return (element instanceof PIRTAdequacyColumnGuideline)
				? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)
				: null;
	}

}
