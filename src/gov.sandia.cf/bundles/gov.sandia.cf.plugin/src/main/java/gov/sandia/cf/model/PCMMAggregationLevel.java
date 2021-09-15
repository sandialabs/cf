/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;

/**
 * The PCMM Aggregate level
 * 
 * @author Maxime N.
 *
 */
public class PCMMAggregationLevel implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name field linked to NAME column
	 */
	private String name;

	/**
	 * The code field linked to CODE column
	 */
	private Integer code;

	/**
	 * The aggregation object
	 */
	private PCMMAggregation<IAssessable> aggreagation;

	/**
	 * Get the name
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name
	 * 
	 * @param name the level name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the code
	 * 
	 * @return The code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * Set the name
	 * 
	 * @param code the level code
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * Get the aggregation
	 * 
	 * @return The aggregation
	 */
	public PCMMAggregation<IAssessable> getAggreagation() {
		return aggreagation;
	}

	/**
	 * Set the aggregation
	 * 
	 * @param aggreagation the pcmm aggregation
	 */
	public void setAggreagation(PCMMAggregation<IAssessable> aggreagation) {
		this.aggreagation = aggreagation;
	}
}
