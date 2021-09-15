/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPIRTAdequacyColumnRepository;
import gov.sandia.cf.model.PIRTAdequacyColumn;

/**
 * PIRTAdequacyColumn entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTAdequacyColumnRepository extends AbstractCRUDRepository<PIRTAdequacyColumn, Integer>
		implements IPIRTAdequacyColumnRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PIRTAdequacyColumnRepository() {
		super(PIRTAdequacyColumn.class);
	}

	/**
	 * Repository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PIRTAdequacyColumnRepository(EntityManager entityManager) {
		super(entityManager, PIRTAdequacyColumn.class);
	}

}