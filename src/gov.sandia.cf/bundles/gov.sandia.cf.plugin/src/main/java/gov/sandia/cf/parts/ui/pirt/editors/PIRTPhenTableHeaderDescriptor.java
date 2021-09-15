/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The PIRT Header table Descriptor class
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTPhenTableHeaderDescriptor {

	/**
	 * Do not instantiate.
	 */
	private PIRTPhenTableHeaderDescriptor() {
	}

	/**
	 * @return the column name
	 */
	public static String getRowNameLabel() {
		return RscTools.getString(RscConst.TABLE_PIRT_HEADER_ROW_NAME);
	}

	/**
	 * @return the column description
	 */
	public static String getRowDescriptionLabel() {
		return RscTools.getString(RscConst.TABLE_PIRT_HEADER_ROW_DESCRIPTION);
	}

	/**
	 * @return the column creation date
	 */
	public static String getRowCreationDateLabel() {
		return RscTools.getString(RscConst.TABLE_PIRT_HEADER_ROW_CREATIONDATE);
	}

	/**
	 * @return the column is tagged
	 */
	public static String getRowIsTaggedLabel() {
		return RscTools.getString(RscConst.TABLE_PIRT_HEADER_ROW_ISTAGGED);
	}

	/**
	 * @return the column tag date
	 */
	public static String getRowTagDateLabel() {
		return RscTools.getString(RscConst.TABLE_PIRT_HEADER_ROW_TAGDATE);
	}

	/**
	 * @return the column tag description
	 */
	public static String getRowTagDescriptionLabel() {
		return RscTools.getString(RscConst.TABLE_PIRT_HEADER_ROW_TAGDESCRIPTION);
	}
}
