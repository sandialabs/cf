/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

/**
 * The CF model Entity interface
 * 
 * @author Didier Verstraete
 **
 * @param <M> the model type
 * @param <K> the id type
 */
public interface IEntity<M, K> {

	/**
	 * @return the id
	 */
	public K getId();

	/**
	 * 
	 * Sets the id field with @param id
	 * 
	 * @param id the id to set
	 */
	public void setId(K id);

	/**
	 * Create a copy of current MODEL with id null and referenced entity or entity
	 * list null
	 * 
	 * @return a copy of the current MODEL
	 */
	public M copy();
}
