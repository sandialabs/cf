/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.yml;

import java.io.Serializable;

import gov.sandia.cf.model.IntendedPurpose;

/**
 * Contains ModSim Intended Purpose data for import/export.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlIntendedPurposeDataDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1878520673188863314L;

	/** The intended purpose. */
	private IntendedPurpose intendedPurpose;

	/**
	 * Gets the intended purpose.
	 *
	 * @return the intended purpose
	 */
	public IntendedPurpose getIntendedPurpose() {
		return intendedPurpose;
	}

	/**
	 * Sets the intended purpose.
	 *
	 * @param intendedPurpose the new intended purpose
	 */
	public void setIntendedPurpose(IntendedPurpose intendedPurpose) {
		this.intendedPurpose = intendedPurpose;
	}

}
