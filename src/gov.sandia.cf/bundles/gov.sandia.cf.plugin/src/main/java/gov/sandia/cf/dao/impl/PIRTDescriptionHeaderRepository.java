/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPIRTDescriptionHeaderRepository;
import gov.sandia.cf.model.PIRTDescriptionHeader;

/**
 * PIRTDescriptionHeader entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTDescriptionHeaderRepository extends AbstractCRUDRepository<PIRTDescriptionHeader, Integer>
		implements IPIRTDescriptionHeaderRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PIRTDescriptionHeaderRepository() {
		super(PIRTDescriptionHeader.class);
	}

	/**
	 * Repository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PIRTDescriptionHeaderRepository(EntityManager entityManager) {
		super(entityManager, PIRTDescriptionHeader.class);
	}

}