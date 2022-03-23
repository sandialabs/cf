/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.configuration;

import java.util.List;

import gov.sandia.cf.model.SystemRequirementParam;

/**
 * Contains all System Requirement configuration variables. This class is loaded
 * by a configuration file.
 * 
 * @author Maxime N.
 *
 */
public class SystemRequirementSpecification {
	/**
	 * the System Requirements parameters
	 */
	private List<SystemRequirementParam> parameters;

	@SuppressWarnings("javadoc")
	public List<SystemRequirementParam> getParameters() {
		return parameters;
	}

	@SuppressWarnings("javadoc")
	public void setParameters(List<SystemRequirementParam> parameters) {
		this.parameters = parameters;
	}
}
