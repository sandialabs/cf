/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.qoiplanning;

import java.util.List;

import gov.sandia.cf.model.QoIPlanningParam;

/**
 * Contains all QoIPlanning configuration variables. This class is loaded by a
 * configuration file.
 * 
 * @author Didier Verstraete
 *
 */
public class QoIPlanningSpecification {
	/**
	 * the QoI Planner parameters
	 */
	private List<QoIPlanningParam> parameters;

	@SuppressWarnings("javadoc")
	public List<QoIPlanningParam> getParameters() {
		return parameters;
	}

	@SuppressWarnings("javadoc")
	public void setParameters(List<QoIPlanningParam> parameters) {
		this.parameters = parameters;
	}
}
