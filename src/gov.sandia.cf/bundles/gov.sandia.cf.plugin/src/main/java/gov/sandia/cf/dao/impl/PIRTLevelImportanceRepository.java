/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPIRTLevelImportanceRepository;
import gov.sandia.cf.model.PIRTLevelImportance;

/**
 * PIRTLevelImportance entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTLevelImportanceRepository extends AbstractCRUDRepository<PIRTLevelImportance, Integer>
		implements IPIRTLevelImportanceRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PIRTLevelImportanceRepository() {
		super(PIRTLevelImportance.class);
	}

	/**
	 * Repository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PIRTLevelImportanceRepository(EntityManager entityManager) {
		super(entityManager, PIRTLevelImportance.class);
	}

}