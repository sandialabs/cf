/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/

package gov.sandia.cf.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.UserRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.User;

/**
 * JUnit class to test the UserRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class UserRepositoryTest extends AbstractTestRepository<User, Integer, UserRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(UserRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<UserRepository> getRepositoryClass() {
		return UserRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<User> getModelClass() {
		return User.class;
	}

	@Override
	User getModelFulfilled(User model) {
		fulfillModelStrings(model);
		return model;
	}

	@Test
	void testFindByUserId() {

		String userID = "MY_USERID"; //$NON-NLS-1$

		// create user
		User user = new User();
		user.setUserID(userID);
		User createdUser;
		try {
			createdUser = getRepository().create(user);
			assertNotNull(createdUser);
			assertNotNull(createdUser.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		User found = getRepository().findByUserId(userID);
		assertNotNull(found);
		assertNotNull(found.getId());
		assertEquals(userID, found.getUserID());

	}

	@Test
	void testFindByUserId_userIDNull() {

		// test case
		User found = getRepository().findByUserId(null);
		assertNull(found);
	}
}
