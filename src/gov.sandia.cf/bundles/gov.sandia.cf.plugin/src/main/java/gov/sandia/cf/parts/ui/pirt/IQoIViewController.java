/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.QuantityOfInterest;

/**
 * The Interface IQoIViewController.
 * 
 * @author Didier Verstraete
 */
public interface IQoIViewController {

	/**
	 * Refresh if changed.
	 */
	void refreshIfChanged();

	/**
	 * Reorder.
	 *
	 * @param dragged the dragged
	 * @param index the index
	 * @throws CredibilityException the credibility exception
	 */
	void reorder(QuantityOfInterest dragged, int index) throws CredibilityException;

}
