/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.model.HeaderParts;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.viewer.TableHeader;
import gov.sandia.cf.tools.RscTools;

/**
 * Defines labels (text, cell colors, images) of the PIRT QoI table cells
 * 
 * @author Didier Verstraete
 *
 */
public class TableHeaderLabelProvider extends LabelProvider
		implements ITableColorProvider, ITableFontProvider, ITableLabelProvider {

	/**
	 * The table to manage
	 */
	private TableViewer tableHeader;
	/**
	 * The view manager
	 */
	private IViewManager viewMgr;

	/**
	 * The constructor
	 * 
	 * @param viewMgr     the view manager
	 * @param tableHeader the table viewer associated
	 */
	public TableHeaderLabelProvider(IViewManager viewMgr, TableViewer tableHeader) {
		this.tableHeader = tableHeader;

		Assert.isNotNull(viewMgr);
		this.viewMgr = viewMgr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof HeaderParts) {
			HeaderParts<?> header = (HeaderParts<?>) element;
			switch (columnIndex) {
			case 0:
				return header.getName();
			case 1:
				return header.getValue();
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
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getBackground(Object element, int columnIndex) {

		// if row is not modifiable set gray background color, otherwise null
		if (tableHeader.getCellModifier() instanceof ColumnViewerSupport
				&& !((ColumnViewerSupport) tableHeader.getCellModifier()).getModifier().canModify(element,
						TableHeader.COLUMN_VALUE_PROPERTY)) {
			return new Color(Display.getCurrent(), PartsResourceConstants.TABLE_NON_EDITABLE_CELL_COLOR);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getForeground(Object element, int columnIndex) {

		// if row is not modifiable set gray background color, otherwise null
		if (tableHeader.getCellModifier() instanceof ColumnViewerSupport
				&& !((ColumnViewerSupport) tableHeader.getCellModifier()).getModifier().canModify(element,
						TableHeader.COLUMN_VALUE_PROPERTY)) {
			return Display.getCurrent().getSystemColor(PartsResourceConstants.TABLE_NON_EDITABLE_TEXT_COLOR);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Font getFont(Object element, int columnIndex) {
		if (columnIndex == 0) {
			FontData fontData = new FontData();
			fontData.setStyle(PartsResourceConstants.PIRT_PHEN_TABLEHEADER_FONT_STYLE);
			fontData.setHeight(PartsResourceConstants.PIRT_PHEN_TABLEHEADER_FONT_HEIGHT);
			return FontTools.getFont(viewMgr.getRscMgr(), fontData);
		}
		return null;
	}

}
