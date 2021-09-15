/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IQoIPlanningConstraintRepository;
import gov.sandia.cf.model.QoIPlanningConstraint;

/**
 * QoIPlanningConstraint entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class QoIPlanningConstraintRepository extends AbstractCRUDRepository<QoIPlanningConstraint, Integer>
		implements IQoIPlanningConstraintRepository {

	/**
	 * Empty constructor: if using, must call setEntityManager later
	 */
	public QoIPlanningConstraintRepository() {
		super(QoIPlanningConstraint.class);
	}

	/**
	 * QoIPlanningConstraintRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public QoIPlanningConstraintRepository(EntityManager entityManager) {
		super(entityManager, QoIPlanningConstraint.class);
	}

}