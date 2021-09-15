/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IQoIHeaderRepository;
import gov.sandia.cf.model.QoIHeader;

/**
 * Model entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class QoIHeaderRepository extends AbstractCRUDRepository<QoIHeader, Integer> implements IQoIHeaderRepository {

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public QoIHeaderRepository() {
		super(QoIHeader.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager
	 *            the entity manager for this repository to execute queries (must
	 *            not be null)
	 * 
	 */
	public QoIHeaderRepository(EntityManager entityManager) {
		super(entityManager, QoIHeader.class);
	}

}