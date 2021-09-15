/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer;

import org.eclipse.swt.widgets.Composite;

import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * 
 * The table viewer class for phenomena description
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTPhenomenaTreePhenomena extends TableViewerHideSelection {

	/**
	 * 
	 * The constructor
	 * 
	 * @param parent the parent composite
	 * @param style  the style of the component
	 */
	public PIRTPhenomenaTreePhenomena(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @return the column id property name
	 */
	public static String getColumnIdProperty() {
		return RscTools.getString(RscConst.MSG_TABLE_COLUMN_ID);
	}

	/**
	 * @return the column phenomena property name
	 */
	public static String getColumnPhenomenaProperty() {
		return RscTools.getString(RscConst.TABLE_PIRT_PHENOMENA_COLUMN_PHENOMENA);
	}

	/**
	 * @return the column importance property name
	 */
	public static String getColumnImportanceProperty() {
		return RscTools.getString(RscConst.TABLE_PIRT_PHENOMENA_COLUMN_IMPORTANCE);
	}

	/**
	 * @return the column action view details property name
	 */
	public static String getColumnActionViewProperty() {
		return RscTools.getString(RscConst.MSG_BTN_VIEW);
	}

	/**
	 * @return the column action edit property name
	 */
	public static String getColumnActionEditProperty() {
		return RscTools.getString(RscConst.MSG_BTN_EDIT);
	}

	/**
	 * @return the column action delete property name
	 */
	public static String getColumnActionDeleteProperty() {
		return RscTools.getString(RscConst.MSG_BTN_DELETE);
	}
}
