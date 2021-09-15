/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMLevelColorRepository;
import gov.sandia.cf.model.PCMMLevelColor;

/**
 * PCMMAssessLevel entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMLevelColorRepository extends AbstractCRUDRepository<PCMMLevelColor, Integer>
		implements IPCMMLevelColorRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PCMMLevelColorRepository() {
		super(PCMMLevelColor.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMLevelColorRepository(EntityManager entityManager) {
		super(entityManager, PCMMLevelColor.class);
	}

}