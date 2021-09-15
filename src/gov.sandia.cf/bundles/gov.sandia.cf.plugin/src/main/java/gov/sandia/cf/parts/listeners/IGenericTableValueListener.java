/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.listeners;

import gov.sandia.cf.model.IGenericTableItem;
import gov.sandia.cf.model.IGenericTableValue;

/**
 * Generic table value changed.
 * 
 * @author Didier Verstraete
 *
 */
public interface IGenericTableValueListener {

	/**
	 * Called when the value of a column implementing AGenericTableCellEditor is
	 * changed.
	 * 
	 * @param item  the generic table item changed
	 * @param value the generic table value changed
	 */
	void valueChanged(IGenericTableItem item, IGenericTableValue value);
}
