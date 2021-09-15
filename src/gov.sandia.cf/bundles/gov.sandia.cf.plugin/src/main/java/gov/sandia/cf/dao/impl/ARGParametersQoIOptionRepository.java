/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IARGParametersQoIOptionRepository;
import gov.sandia.cf.model.ARGParametersQoIOption;

/**
 * ARG Parameters QoI Option entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class ARGParametersQoIOptionRepository extends AbstractCRUDRepository<ARGParametersQoIOption, Integer>
		implements IARGParametersQoIOptionRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public ARGParametersQoIOptionRepository() {
		super(ARGParametersQoIOption.class);
	}

	/**
	 * ARGParametersQoIOptionRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 * 
	 */
	public ARGParametersQoIOptionRepository(EntityManager entityManager) {
		super(entityManager, ARGParametersQoIOption.class);
	}

}