/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMLevelDescRepository;
import gov.sandia.cf.model.PCMMLevelDescriptor;

/**
 * PCMMLevelDescriptor entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMLevelDescRepository extends AbstractCRUDRepository<PCMMLevelDescriptor, Integer>
		implements IPCMMLevelDescRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PCMMLevelDescRepository() {
		super(PCMMLevelDescriptor.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager
	 *            the entity manager for this repository to execute queries (must
	 *            not be null)
	 */
	public PCMMLevelDescRepository(EntityManager entityManager) {
		super(entityManager, PCMMLevelDescriptor.class);
	}

}