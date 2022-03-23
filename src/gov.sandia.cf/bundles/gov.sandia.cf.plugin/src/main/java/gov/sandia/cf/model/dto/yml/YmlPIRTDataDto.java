/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.yml;

import java.io.Serializable;
import java.util.List;

import gov.sandia.cf.model.QuantityOfInterest;

/**
 * Contains PIRT data for import/export.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlPIRTDataDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8806731970708078619L;

	/** The quantity of interest list. */
	private List<QuantityOfInterest> quantityOfInterestList;

	/**
	 * Gets the quantity of interest list.
	 *
	 * @return the quantity of interest list
	 */
	public List<QuantityOfInterest> getQuantityOfInterestList() {
		return quantityOfInterestList;
	}

	/**
	 * Sets the quantity of interest list.
	 *
	 * @param quantityOfInterestList the new quantity of interest list
	 */
	public void setQuantityOfInterestList(List<QuantityOfInterest> quantityOfInterestList) {
		this.quantityOfInterestList = quantityOfInterestList;
	}

}
