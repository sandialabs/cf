/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

/**
 * 
 * The select value for an entity
 * 
 * @author Didier Verstraete
 *
 */
public interface ISelectValue {

	/**
	 * @return the id
	 */
	Integer getId();

	/**
	 * @return the name
	 */
	String getSelectName();
}
