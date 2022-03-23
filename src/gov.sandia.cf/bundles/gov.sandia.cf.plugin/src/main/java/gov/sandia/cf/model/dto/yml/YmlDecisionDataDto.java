/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.yml;

import java.io.Serializable;
import java.util.List;

import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionParam;

/**
 * Contains Decision data for import/export.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlDecisionDataDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7846963565056355579L;

	/** The decision parameters. */
	private List<DecisionParam> decisionParameters;

	/** The decision values. */
	private List<Decision> decisionValues;

	/**
	 * Gets the decision parameters.
	 *
	 * @return the decision parameters
	 */
	public List<DecisionParam> getDecisionParameters() {
		return decisionParameters;
	}

	/**
	 * Sets the decision parameters.
	 *
	 * @param decisionParameters the new decision parameters
	 */
	public void setDecisionParameters(List<DecisionParam> decisionParameters) {
		this.decisionParameters = decisionParameters;
	}

	/**
	 * Gets the decision values.
	 *
	 * @return the decision values
	 */
	public List<Decision> getDecisionValues() {
		return decisionValues;
	}

	/**
	 * Sets the decision values.
	 *
	 * @param decisionValues the new decision values
	 */
	public void setDecisionValues(List<Decision> decisionValues) {
		this.decisionValues = decisionValues;
	}

}
