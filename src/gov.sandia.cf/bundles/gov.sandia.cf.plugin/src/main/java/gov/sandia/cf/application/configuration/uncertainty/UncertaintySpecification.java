/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.uncertainty;

import java.util.List;

import gov.sandia.cf.model.UncertaintyParam;

/**
 * Contains all Uncertaincy configuration variables. This class is loaded by a
 * configuration file.
 * 
 * @author Maxime N.
 *
 */
public class UncertaintySpecification {
	/**
	 * the Uncertainty parameters
	 */
	private List<UncertaintyParam> parameters;

	@SuppressWarnings("javadoc")
	public List<UncertaintyParam> getParameters() {
		return parameters;
	}

	@SuppressWarnings("javadoc")
	public void setParameters(List<UncertaintyParam> parameters) {
		this.parameters = parameters;
	}
}
