/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.yml;

import java.io.Serializable;
import java.util.List;

import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyParam;

/**
 * Contains uncertainty data for import/export.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlUncertaintyDataDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6028873302744311850L;

	/** The uncertainty parameters. */
	private List<UncertaintyParam> uncertaintyParameters;

	/** The uncertainty groups. */
	private List<Uncertainty> uncertaintyGroups;

	/**
	 * Gets the uncertainty parameters.
	 *
	 * @return the uncertainty parameters
	 */
	public List<UncertaintyParam> getUncertaintyParameters() {
		return uncertaintyParameters;
	}

	/**
	 * Sets the uncertainty parameters.
	 *
	 * @param uncertaintyParameters the new uncertainty parameters
	 */
	public void setUncertaintyParameters(List<UncertaintyParam> uncertaintyParameters) {
		this.uncertaintyParameters = uncertaintyParameters;
	}

	/**
	 * Gets the uncertainty groups.
	 *
	 * @return the uncertainty groups
	 */
	public List<Uncertainty> getUncertaintyGroups() {
		return uncertaintyGroups;
	}

	/**
	 * Sets the uncertainty groups.
	 *
	 * @param uncertaintyGroups the new uncertainty groups
	 */
	public void setUncertaintyGroups(List<Uncertainty> uncertaintyGroups) {
		this.uncertaintyGroups = uncertaintyGroups;
	}

}
