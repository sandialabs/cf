/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPIRTAdequacyColumnGuidelineRepository;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;

/**
 * PIRTAdequacyColumnGuideline entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTAdequacyColumnGuidelineRepository extends AbstractCRUDRepository<PIRTAdequacyColumnGuideline, Integer>
		implements IPIRTAdequacyColumnGuidelineRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PIRTAdequacyColumnGuidelineRepository() {
		super(PIRTAdequacyColumnGuideline.class);
	}

	/**
	 * Repository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PIRTAdequacyColumnGuidelineRepository(EntityManager entityManager) {
		super(entityManager, PIRTAdequacyColumnGuideline.class);
	}

}