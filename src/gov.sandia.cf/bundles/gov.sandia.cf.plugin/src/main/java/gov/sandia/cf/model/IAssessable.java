/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.io.Serializable;
import java.util.List;

/**
 * The CF model Assessable interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IAssessable extends Serializable {

	/**
	 * @return the level list
	 */
	public List<PCMMLevel> getLevelList();

	/**
	 * @return the assessable name
	 */
	public String getName();
}
