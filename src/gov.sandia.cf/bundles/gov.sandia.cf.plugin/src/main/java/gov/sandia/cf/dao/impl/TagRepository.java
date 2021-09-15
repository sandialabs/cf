/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.ITagRepository;
import gov.sandia.cf.model.Tag;

/**
 * Tag entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class TagRepository extends AbstractCRUDRepository<Tag, Integer> implements ITagRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public TagRepository() {
		super(Tag.class);
	}

	/**
	 * RoleRepository constructor
	 * 
	 * @param entityManager
	 *            the entity manager for this repository to execute queries (must
	 *            not be null)
	 */
	public TagRepository(EntityManager entityManager) {
		super(entityManager, Tag.class);
	}

}