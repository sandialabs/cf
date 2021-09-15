/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.tools.GsonTools;

/**
 * The Parameter link Gson format used to store link values and types.
 * 
 * This class is generally converted to Gson to keep data and metadata into the
 * database.
 * 
 * @author Maxime N.
 *
 */
public final class ParameterLinkGson {

	/**
	 * The link type
	 */
	public FormFieldType type;
	/**
	 * The link value
	 */
	public String value;

	/**
	 * @param type  the parameter type
	 * @param value the parameter value
	 * @return a path or url to Gson encoding
	 */
	public static String toGson(FormFieldType type, String value) {
		ParameterLinkGson jsonObject = new ParameterLinkGson();
		jsonObject.type = type;
		jsonObject.value = value;

		// Encode JSON
		return GsonTools.toGson(jsonObject);
	}
}
