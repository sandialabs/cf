/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The descriptor of the qoi view table header
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTQoITableHeaderDescriptor {

	/**
	 * Do not instantiate
	 */
	private PIRTQoITableHeaderDescriptor() {
	}

	/**
	 * @return the column application
	 */
	public static String getApplicationLabel() {
		return RscTools.getString(RscConst.TABLE_QOI_HEADER_ROW_APPLICATION);
	}

	/**
	 * @return the column contact
	 */
	public static String getContactLabel() {
		return RscTools.getString(RscConst.TABLE_QOI_HEADER_ROW_CONTACT);
	}

}
