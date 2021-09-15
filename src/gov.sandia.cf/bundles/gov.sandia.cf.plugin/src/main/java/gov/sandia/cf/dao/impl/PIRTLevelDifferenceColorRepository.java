/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPIRTLevelDifferenceColorRepository;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;

/**
 * PIRTLevelDifferenceColor entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTLevelDifferenceColorRepository extends AbstractCRUDRepository<PIRTLevelDifferenceColor, Integer>
		implements IPIRTLevelDifferenceColorRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PIRTLevelDifferenceColorRepository() {
		super(PIRTLevelDifferenceColor.class);
	}

	/**
	 * Repository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PIRTLevelDifferenceColorRepository(EntityManager entityManager) {
		super(entityManager, PIRTLevelDifferenceColor.class);
	}

}