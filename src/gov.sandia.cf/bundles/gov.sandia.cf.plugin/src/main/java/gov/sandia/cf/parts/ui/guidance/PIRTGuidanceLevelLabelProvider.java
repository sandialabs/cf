/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.guidance;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.tools.RscTools;

/**
 * Defines labels (text, cell colors, images) of the PIRT table cells
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTGuidanceLevelLabelProvider extends ColumnLabelProvider
		implements ITableColorProvider, ITableFontProvider, ITableLabelProvider {

	/**
	 * Constructor
	 */
	public PIRTGuidanceLevelLabelProvider() {
		// just create the label provider
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof PIRTLevelImportance) {
			PIRTLevelImportance level = (PIRTLevelImportance) element;
			switch (columnIndex) {
			case 0:
				return level.getLevel() != null ? level.getLevel().toString() : RscTools.empty();
			case 1:
				return level.getName();
			case 2:
				return level.getLabel();
			default:
				return RscTools.empty();
			}
		}
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		return getBackground(element);
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return getForeground(element);
	}

	@Override
	public Font getFont(Object element, int columnIndex) {
		return null;
	}
}
