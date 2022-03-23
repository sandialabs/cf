/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.yml;

import java.io.Serializable;

/**
 * Contains global data for import/export.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlAllDataDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 666458346737819231L;

	/** The global data. */
	private YmlGlobalDataDto globalData;

	/** The intended purpose data. */
	private YmlIntendedPurposeDataDto intendedPurposeData;

	/** The decision data. */
	private YmlDecisionDataDto decisionData;

	/** The requirement data. */
	private YmlRequirementDataDto requirementData;

	/** The uncertainty data. */
	private YmlUncertaintyDataDto uncertaintyData;

	/** The pirt data. */
	private YmlPIRTDataDto pirtData;

	/** The pcmm data. */
	private YmlPCMMDataDto pcmmData;

	/**
	 * Gets the global data.
	 *
	 * @return the global data
	 */
	public YmlGlobalDataDto getGlobalData() {
		return globalData;
	}

	/**
	 * Sets the global data.
	 *
	 * @param globalData the new global data
	 */
	public void setGlobalData(YmlGlobalDataDto globalData) {
		this.globalData = globalData;
	}

	/**
	 * Gets the intended purpose data.
	 *
	 * @return the intended purpose data
	 */
	public YmlIntendedPurposeDataDto getIntendedPurposeData() {
		return intendedPurposeData;
	}

	/**
	 * Sets the intended purpose data.
	 *
	 * @param intendedPurposeData the new intended purpose data
	 */
	public void setIntendedPurposeData(YmlIntendedPurposeDataDto intendedPurposeData) {
		this.intendedPurposeData = intendedPurposeData;
	}

	/**
	 * Gets the decision data.
	 *
	 * @return the decision data
	 */
	public YmlDecisionDataDto getDecisionData() {
		return decisionData;
	}

	/**
	 * Sets the decision data.
	 *
	 * @param decisionData the new decision data
	 */
	public void setDecisionData(YmlDecisionDataDto decisionData) {
		this.decisionData = decisionData;
	}

	/**
	 * Gets the requirement data.
	 *
	 * @return the requirement data
	 */
	public YmlRequirementDataDto getRequirementData() {
		return requirementData;
	}

	/**
	 * Sets the requirement data.
	 *
	 * @param requirementData the new requirement data
	 */
	public void setRequirementData(YmlRequirementDataDto requirementData) {
		this.requirementData = requirementData;
	}

	/**
	 * Gets the uncertainty data.
	 *
	 * @return the uncertainty data
	 */
	public YmlUncertaintyDataDto getUncertaintyData() {
		return uncertaintyData;
	}

	/**
	 * Sets the uncertainty data.
	 *
	 * @param uncertaintyData the new uncertainty data
	 */
	public void setUncertaintyData(YmlUncertaintyDataDto uncertaintyData) {
		this.uncertaintyData = uncertaintyData;
	}

	/**
	 * Gets the pirt data.
	 *
	 * @return the pirt data
	 */
	public YmlPIRTDataDto getPirtData() {
		return pirtData;
	}

	/**
	 * Sets the pirt data.
	 *
	 * @param pirtData the new pirt data
	 */
	public void setPirtData(YmlPIRTDataDto pirtData) {
		this.pirtData = pirtData;
	}

	/**
	 * Gets the pcmm data.
	 *
	 * @return the pcmm data
	 */
	public YmlPCMMDataDto getPcmmData() {
		return pcmmData;
	}

	/**
	 * Sets the pcmm data.
	 *
	 * @param pcmmData the new pcmm data
	 */
	public void setPcmmData(YmlPCMMDataDto pcmmData) {
		this.pcmmData = pcmmData;
	}

}
