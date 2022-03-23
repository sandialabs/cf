/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.guidance;

import java.util.Optional;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMSubelement;
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

public class PCMMGuidanceLevelTableLabelProvider extends ATooltipTableLabelProvider {
	/**
	 * Tree
	 */
	private final TreeViewerHideSelection tableViewer;

	/** PCMM GUIDANCE table NAME index */
	public static final int NAME_COLUMN_INDEX = 0;
	/** PCMM GUIDANCE table DESCRIPTION index */
	public static final int DESCRIPTION_COLUMN_INDEX = 1;

	/**
	 * Flag is row or column
	 */
	boolean isTreeWithColumn;
	/**
	 * The view manager
	 */
	private IViewManager viewMgr;

	/**
	 * Construct
	 * 
	 * @param viewMgr          the view manager
	 * @param tableViewer      the table viewer
	 * @param isTreeWithColumn is it a tree with columns
	 */
	public PCMMGuidanceLevelTableLabelProvider(IViewManager viewMgr, TreeViewerHideSelection tableViewer,
			boolean isTreeWithColumn) {
		super(viewMgr, tableViewer);

		Assert.isNotNull(viewMgr);
		this.viewMgr = viewMgr;

		// Set properties
		this.isTreeWithColumn = isTreeWithColumn;
		this.tableViewer = tableViewer;
	}

	/** {@inheritDoc} */
	@Override
	protected ViewerRow getViewerRow(Point point) {
		return tableViewer.getViewerRow(point);
	}

	/** {@inheritDoc} */
	@Override
	protected String getCellText(Object element, int columnIndex) {

		String text = RscTools.empty();

		if (columnIndex == NAME_COLUMN_INDEX) {
			if (element instanceof PCMMElement) {
				text = ((PCMMElement) element).getName();
			} else if (element instanceof PCMMSubelement) {
				StringBuilder strText = new StringBuilder();
				strText.append(((PCMMSubelement) element).getCode());
				strText.append(": "); //$NON-NLS-1$
				strText.append(((PCMMSubelement) element).getName());
				text = strText.toString();
			} else if (element instanceof PCMMLevel) {
				text = ((PCMMLevel) element).getName();
			} else if (element instanceof PCMMLevelDescriptor) {
				text = ((PCMMLevelDescriptor) element).getName();
			}
		} else if (element instanceof PCMMLevel) {
			String[] columnProperties = (String[]) tableViewer.getColumnProperties();
			PCMMLevel level = (PCMMLevel) element;

			// retrieve adequacy columns values
			Optional<PCMMLevelDescriptor> descriptorLevel = level.getLevelDescriptorList().stream()
					.filter(descriptor -> descriptor != null && columnProperties[columnIndex] != null
							&& columnProperties[columnIndex].equals(descriptor.getName()))
					.findFirst();
			text = descriptorLevel.isPresent() ? descriptorLevel.get().getValue() : text;
		} else if (element instanceof PCMMLevelDescriptor) {
			text = ((PCMMLevelDescriptor) element).getValue();
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
		Font defaultFont = tableViewer.getControl().getFont();
		FontData defaultFontData = (defaultFont.getFontData() != null && defaultFont.getFontData().length > 0)
				? defaultFont.getFontData()[0]
				: new FontData();
		if (columnIndex == NAME_COLUMN_INDEX) {
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
	 * @param element the PCMM element
	 * @return the background color value
	 */
	private String getBackgroundColorValue(Object element) {

		if (isTreeWithColumn) {
			if (element instanceof PCMMElement) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY);
			} else if (element instanceof PCMMSubelement) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT);
			}
		} else {
			if (element instanceof PCMMElement) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY);
			} else if (element instanceof PCMMSubelement) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT);
			} else if (element instanceof PCMMLevel) {
				return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT_2);
			}
		}

		return null;
	}
}
