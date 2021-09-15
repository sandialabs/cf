/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer;

import org.eclipse.swt.widgets.Composite;

import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * 
 * The table viewer class for qoi description and listing
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTQoITableQoI extends TableViewerHideSelection {

	/**
	 * The constructor
	 *
	 * @param parent the parent composite
	 * @param style  the style of the component
	 */
	public PIRTQoITableQoI(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @return the column name property symbol
	 */
	public static String getColumnSymbolProperty() {
		return RscTools.getString(RscConst.TABLE_PIRT_QOI_COLUMN_SYMBOL);
	}

	/**
	 * @return the column name property name
	 */
	public static String getColumnDescriptionProperty() {
		return RscTools.getString(RscConst.TABLE_PIRT_QOI_COLUMN_DESCRIPTION);
	}

	/**
	 * @return the column creation date property name
	 */
	public static String getColumnCreationDateProperty() {
		return RscTools.getString(RscConst.TABLE_PIRT_QOI_COLUMN_CREATIONDATE);
	}

	/**
	 * @return the column is tagged property name
	 */
	public static String getColumnIsTaggedProperty() {
		return RscTools.getString(RscConst.TABLE_PIRT_QOI_COLUMN_ISTAGGED);
	}

	/**
	 * @return the column tag date property name
	 */
	public static String getColumnTagDateProperty() {
		return RscTools.getString(RscConst.TABLE_PIRT_QOI_COLUMN_TAGDATE);
	}

	/**
	 * @return the column tag description property name
	 */
	public static String getColumnTagDescriptionProperty() {
		return RscTools.getString(RscConst.TABLE_PIRT_QOI_COLUMN_TAGDESCRIPTION);
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

	/**
	 * 
	 * @return the column action duplicate property name
	 */
	public static String getColumnActionDuplicateProperty() {
		return RscTools.getString(RscConst.MSG_BTN_COPY);
	}

	/**
	 * 
	 * @return the column action tag property name
	 */
	public static String getColumnActionTagProperty() {
		return RscTools.getString(RscConst.MSG_BTN_TAG);
	}

}
