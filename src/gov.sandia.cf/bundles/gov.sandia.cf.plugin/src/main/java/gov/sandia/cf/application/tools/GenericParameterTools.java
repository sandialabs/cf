/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.tools;

import java.util.List;

import gov.sandia.cf.model.IGenericTableValue;

/**
 * The Class GenericParameterTools.
 * 
 * @author Didier Verstraete
 */
public class GenericParameterTools {
	
	private GenericParameterTools() {
		// do not implement
	}

	/**
	 * Sort values list by parameter id
	 * 
	 * @param values the list to sort
	 * @return the sorted list
	 */
	public static List<IGenericTableValue> sortTableValuesByParameterId(List<IGenericTableValue> values) {

		if (values != null && !values.isEmpty()) {

			// sort values
			values.sort((v1, v2) -> {
				if (v1 == null || v1.getParameter() == null) {
					return v2 == null || v2.getParameter() == null ? 0 : 1;
				}

				if (v2 == null || v2.getParameter() == null) {
					return -1;
				}

				return Integer.compare(v1.getParameter().getId(), v2.getParameter().getId());
			});
		}
		return values;
	}
}
