/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/

package gov.sandia.cf.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.NativeQueryRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;

/**
 * JUnit class to test the UserRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class NativeQueryRepositoryTest extends AbstractTestDao {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(NativeQueryRepositoryTest.class);

	private NativeQueryRepository repository = null;

	NativeQueryRepository getRepository() {
		if (repository == null) {
			repository = new NativeQueryRepository(getDaoManager().getEntityManager());
		}
		return repository;
	}

	@Test
	void testExecute_SelectQuery() {

		String userID = "MY_USERID"; //$NON-NLS-1$

		// create user
		User user = new User();
		user.setUserID(userID);
		User createdUser = null;
		try {
			createdUser = getDaoManager().getRepository(IUserRepository.class).create(user);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(createdUser);
		assertNotNull(createdUser.getId());

		// test case
		@SuppressWarnings("rawtypes")
		List foundList = null;
		try {
			foundList = getRepository().execute("SELECT u.* FROM User u", User.class); //$NON-NLS-1$
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());

		for (Object object : foundList) {
			assertTrue(object instanceof User);
			assertNotNull(object);
			User castedUser = (User) object;
			assertNotNull(castedUser.getId());
		}
	}

	@Test
	void testExecute_badQuery() {

		// test case
		try {
			getRepository().execute("SELECT u FROM THE User u", User.class); //$NON-NLS-1$
			fail("A bad query must raise an exception"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertNotNull(e);
			assertNotNull(e.getMessage());
		}
	}

	@Test
	void testExecute_QueryNull() {

		// test case
		try {
			getRepository().execute(null, User.class);
			fail("A null query must raise an exception"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertNotNull(e);
			assertNotNull(e.getMessage());
		}
	}

	@Test
	void testExecute_ClassNull() {

		// test case
		try {
			getRepository().execute("SELECT u FROM THE User u", null); //$NON-NLS-1$
			fail("A bad query must raise an exception"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertNotNull(e);
			assertNotNull(e.getMessage());
		}
	}

	@Test
	void testExecute_BadEntityClass() {

		// test case
		try {
			getRepository().execute("SELECT u FROM THE User u", Model.class); //$NON-NLS-1$
			fail("A bad query must raise an exception"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertNotNull(e);
			assertNotNull(e.getMessage());
		}
	}
}
