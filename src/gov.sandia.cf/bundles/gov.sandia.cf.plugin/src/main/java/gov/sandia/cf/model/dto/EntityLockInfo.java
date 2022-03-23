/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto;

/**
 * The Class EntityLockInfo.
 * 
 * @author Didier Verstraete
 */
public class EntityLockInfo {
	private String entityClass;
	private Long id;
	private String information;

	/**
	 * Instantiates a new entity lock info.
	 */
	public EntityLockInfo() {
	}

	/**
	 * Instantiates a new entity lock info.
	 *
	 * @param entityClass the entity class
	 * @param id          the id
	 */
	public EntityLockInfo(String entityClass, Long id) {
		this(entityClass, id, ""); //$NON-NLS-1$
	}

	/**
	 * Instantiates a new entity lock info.
	 *
	 * @param entityClass the entity class
	 * @param id          the id
	 * @param information the information
	 */
	public EntityLockInfo(String entityClass, Long id, String information) {
		this.entityClass = entityClass;
		this.id = id;
		this.information = information;
	}

	/**
	 * Gets the entity class.
	 *
	 * @return the entity class
	 */
	public String getEntityClass() {
		return entityClass;
	}

	/**
	 * Sets the entity class.
	 *
	 * @param entityClass the new entity class
	 */
	public void setEntityClass(String entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the information.
	 *
	 * @return the information
	 */
	public String getInformation() {
		return information;
	}

	/**
	 * Sets the information.
	 *
	 * @param information the new information
	 */
	public void setInformation(String information) {
		this.information = information;
	}

}