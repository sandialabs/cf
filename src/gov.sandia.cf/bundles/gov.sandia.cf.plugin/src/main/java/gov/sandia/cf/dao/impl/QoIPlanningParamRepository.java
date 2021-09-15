/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IQoIPlanningParamRepository;
import gov.sandia.cf.model.QoIPlanningParam;

/**
 * QoIPlanningParam entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class QoIPlanningParamRepository extends AbstractCRUDRepository<QoIPlanningParam, Integer>
		implements IQoIPlanningParamRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public QoIPlanningParamRepository() {
		super(QoIPlanningParam.class);
	}

	/**
	 * QoIPlanningParamRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public QoIPlanningParamRepository(EntityManager entityManager) {
		super(entityManager, QoIPlanningParam.class);
	}

}