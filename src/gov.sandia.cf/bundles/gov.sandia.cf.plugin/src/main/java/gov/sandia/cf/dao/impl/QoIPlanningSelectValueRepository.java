/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IQoIPlanningSelectValueRepository;
import gov.sandia.cf.model.QoIPlanningSelectValue;

/**
 * QoIPlanningSelectValue entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class QoIPlanningSelectValueRepository extends AbstractCRUDRepository<QoIPlanningSelectValue, Integer>
		implements IQoIPlanningSelectValueRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public QoIPlanningSelectValueRepository() {
		super(QoIPlanningSelectValue.class);
	}

	/**
	 * QoIPlanningSelectValueRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public QoIPlanningSelectValueRepository(EntityManager entityManager) {
		super(entityManager, QoIPlanningSelectValue.class);
	}

}