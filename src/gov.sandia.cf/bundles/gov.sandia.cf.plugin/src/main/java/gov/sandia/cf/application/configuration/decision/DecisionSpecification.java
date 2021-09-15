/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.decision;

import java.util.List;

import gov.sandia.cf.model.DecisionParam;

/**
 * Contains all Decision configuration variables. This class is loaded by a
 * configuration file.
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionSpecification {
	/**
	 * the PCMM parameters
	 */
	private List<DecisionParam> parameters;

	@SuppressWarnings("javadoc")
	public List<DecisionParam> getParameters() {
		return parameters;
	}

	@SuppressWarnings("javadoc")
	public void setParameters(List<DecisionParam> parameters) {
		this.parameters = parameters;
	}
}
