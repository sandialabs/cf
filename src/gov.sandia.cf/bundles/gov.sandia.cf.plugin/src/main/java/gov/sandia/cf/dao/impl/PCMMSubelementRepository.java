/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMSubelementRepository;
import gov.sandia.cf.model.PCMMSubelement;

/**
 * PCMMSubelement entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMSubelementRepository extends AbstractCRUDRepository<PCMMSubelement, Integer>
		implements IPCMMSubelementRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PCMMSubelementRepository() {
		super(PCMMSubelement.class);
	}

	/**
	 * PCMMSubelementRepository constructor
	 * 
	 * @param entityManager
	 *            the entity manager for this repository to execute queries (must
	 *            not be null)
	 */
	public PCMMSubelementRepository(EntityManager entityManager) {
		super(entityManager, PCMMSubelement.class);
	}

}