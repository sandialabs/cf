/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.dto.IntendedPurposeDto;
import gov.sandia.cf.model.dto.configuration.ParameterLinkGson;
import gov.sandia.cf.tools.GsonTools;

/**
 * This class generates default classes for tests
 * 
 * @author Didier Verstraete
 *
 */
public class TestDtoFactory {
	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(TestDtoFactory.class);

	/**
	 * @return a new generated IntendedPurpose
	 */
	public static IntendedPurposeDto getNewIntendedPurpose() {

		// create IntendedPurpose
		IntendedPurposeDto elt = new IntendedPurposeDto();

		return elt;
	}

	/**
	 * @param type  the link type
	 * @param value the link value
	 * @return a string containing the parameter link in gson
	 */
	public static String getParameterLinkGson(FormFieldType type, String value) {
		ParameterLinkGson linkData = new ParameterLinkGson();
		linkData.type = type;
		linkData.value = value;
		return GsonTools.toGson(linkData);
	}

	/**
	 * Gets the parameter link gson.
	 *
	 * @param type    the link type
	 * @param value   the link value
	 * @param caption the caption
	 * @return a string containing the parameter link in gson
	 */
	public static String getParameterLinkGson(FormFieldType type, String value, String caption) {
		ParameterLinkGson linkData = new ParameterLinkGson();
		linkData.type = type;
		linkData.value = value;
		linkData.caption = caption;
		return GsonTools.toGson(linkData);
	}
}
