/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IUserRepository;
import gov.sandia.cf.model.User;

/**
 * User entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class UserRepository extends AbstractCRUDRepository<User, Integer> implements IUserRepository {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

	/**
	 * Query find by userID
	 */
	public static final String QUERY_FIND_BY_USERID = "SELECT u FROM User u WHERE u.userID = :"; //$NON-NLS-1$

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public UserRepository() {
		super(User.class);
	}

	/**
	 * UserRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public UserRepository(EntityManager entityManager) {
		super(entityManager, User.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User findByUserId(String userID) {

		// check qoiId param
		if (userID == null) {
			logger.error("param userID is empty or null"); //$NON-NLS-1$
			return null;
		}

		String paramUserID = "userID"; //$NON-NLS-1$

		TypedQuery<User> query = getEntityManager().createQuery(QUERY_FIND_BY_USERID + paramUserID, User.class);
		query.setParameter(paramUserID, userID);
		return query.getResultList() != null && !query.getResultList().isEmpty() ? query.getResultList().get(0) : null;
	}
}