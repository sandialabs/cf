/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPIRTAdequacyLevelGuidelineRepository;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;

/**
 * PIRTAdequacyColumnLevelGuideline entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTAdequacyLevelGuidelineRepository
		extends AbstractCRUDRepository<PIRTAdequacyColumnLevelGuideline, Integer>
		implements IPIRTAdequacyLevelGuidelineRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PIRTAdequacyLevelGuidelineRepository() {
		super(PIRTAdequacyColumnLevelGuideline.class);
	}

	/**
	 * Repository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PIRTAdequacyLevelGuidelineRepository(EntityManager entityManager) {
		super(entityManager, PIRTAdequacyColumnLevelGuideline.class);
	}

}