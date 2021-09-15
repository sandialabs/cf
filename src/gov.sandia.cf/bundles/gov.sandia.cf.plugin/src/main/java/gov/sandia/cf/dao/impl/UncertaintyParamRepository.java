/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.model.UncertaintyParam;

/**
 * UncertaintyParam entity repository
 * 
 * @author Maxime N.
 *
 */
public class UncertaintyParamRepository extends AbstractCRUDRepository<UncertaintyParam, Integer>
		implements IUncertaintyParamRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public UncertaintyParamRepository() {
		super(UncertaintyParam.class);
	}

	/**
	 * UncertaintyParamRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public UncertaintyParamRepository(EntityManager entityManager) {
		super(entityManager, UncertaintyParam.class);
	}

}