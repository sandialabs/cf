/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IQoIPlanningValueRepository;
import gov.sandia.cf.model.QoIPlanningValue;

/**
 * QoIPlanningParameterRepository entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class QoIPlanningValueRepository extends AbstractCRUDRepository<QoIPlanningValue, Integer>
		implements IQoIPlanningValueRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public QoIPlanningValueRepository() {
		super(QoIPlanningValue.class);
	}

	/**
	 * QoIPlanningParameterRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public QoIPlanningValueRepository(EntityManager entityManager) {
		super(entityManager, QoIPlanningValue.class);
	}

}