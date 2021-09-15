/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPhenomenonRepository;
import gov.sandia.cf.model.Phenomenon;

/**
 * Model entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PhenomenonRepository extends AbstractCRUDRepository<Phenomenon, Integer> implements IPhenomenonRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PhenomenonRepository() {
		super(Phenomenon.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager
	 *            the entity manager for this repository to execute queries (must
	 *            not be null)
	 * 
	 */
	public PhenomenonRepository(EntityManager entityManager) {
		super(entityManager, Phenomenon.class);
	}

}