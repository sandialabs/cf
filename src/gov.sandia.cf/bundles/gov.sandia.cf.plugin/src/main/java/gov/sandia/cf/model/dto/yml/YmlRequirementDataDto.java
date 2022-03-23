/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.yml;

import java.io.Serializable;
import java.util.List;

import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;

/**
 * Contains System Requirement data for import/export.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlRequirementDataDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7798585262884450982L;

	/** The requirement parameters. */
	private List<SystemRequirementParam> requirementParameters;

	/** The requirement values. */
	private List<SystemRequirement> requirementValues;

	/**
	 * Gets the requirement parameters.
	 *
	 * @return the requirement parameters
	 */
	public List<SystemRequirementParam> getRequirementParameters() {
		return requirementParameters;
	}

	/**
	 * Sets the requirement parameters.
	 *
	 * @param requirementParameters the new requirement parameters
	 */
	public void setRequirementParameters(List<SystemRequirementParam> requirementParameters) {
		this.requirementParameters = requirementParameters;
	}

	/**
	 * Gets the requirement values.
	 *
	 * @return the requirement values
	 */
	public List<SystemRequirement> getRequirementValues() {
		return requirementValues;
	}

	/**
	 * Sets the requirement values.
	 *
	 * @param requirementValues the new requirement values
	 */
	public void setRequirementValues(List<SystemRequirement> requirementValues) {
		this.requirementValues = requirementValues;
	}

}
