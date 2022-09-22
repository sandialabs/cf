/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

/**
 * The Interface ISortableByIdEntity.
 *
 * @author Didier Verstraete
 */
public interface ISortableByIdEntity {

	/**
	 * Gets the generated id.
	 *
	 * @return the generated id
	 */
	public String getGeneratedId();

	/**
	 * Sets the generated id.
	 *
	 * @param id the new generated id
	 */
	public void setGeneratedId(String id);

}
