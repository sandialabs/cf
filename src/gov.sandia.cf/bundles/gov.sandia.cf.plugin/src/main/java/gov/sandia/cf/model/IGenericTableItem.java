/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.List;

/**
 * The CF generic table item interface for generic fields represented into
 * tables.
 * 
 * @author Didier Verstraete
 *
 */
public interface IGenericTableItem extends Serializable {

	/**
	 * @return the item values list
	 */
	List<IGenericTableValue> getValueList();
}
