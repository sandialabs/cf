/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.global.IUserApplication;
import gov.sandia.cf.dao.IRoleRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.User;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the PCMM Application Controller
 */
class UserApplicationTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(UserApplicationTest.class);

	@Test
	void testUserCRUDWorking() {
		// Initialize
		IUserApplication app = getAppManager().getService(IUserApplication.class);

		try {
			// Check is started
			Boolean isStarted = getAppManager().isStarted();
			assertTrue(isStarted);

			// Create Role
			Role role = new Role();
			role.setName("My_Role"); //$NON-NLS-1$
			role = getDaoManager().getRepository(IRoleRepository.class).create(role);

			// ***********
			// Create user
			// ***********
			User user = new User();
			user.setUserID("My_UserId"); //$NON-NLS-1$
			user.setRolePCMM(role);
			app.addUser(user);
			assertNotNull(user);
			assertNotNull(user.getId());
			assertEquals("My_UserId", user.getUserID()); //$NON-NLS-1$

			// *************
			// Retrieve user
			// *************
			// By Id
			User userFoundId = app.getUserById(user.getId());
			assertNotNull(userFoundId);
			assertEquals(userFoundId.getId(), user.getId());

			// By User Id
			User userFoundUserId = app.getUserByUserID(user.getUserID());
			assertNotNull(userFoundUserId);
			assertEquals(userFoundUserId.getId(), user.getId());

			// By User Id not found
			User userFoundUserIdNotFound = app.getUserByUserID("My_UserId_Not_Found"); //$NON-NLS-1$
			assertNotNull(userFoundUserIdNotFound);
			assertNotNull(userFoundUserIdNotFound.getId());
			assertEquals("My_UserId_Not_Found", userFoundUserIdNotFound.getUserID()); //$NON-NLS-1$

			// *********************
			// Retrieve Current Role
			// *********************
			Role currentRole = app.getCurrentPCMMRole(user.getUserID());
			assertNotNull(currentRole);
			assertEquals(currentRole.getId(), user.getRolePCMM().getId());

			// ****************
			// Set Current Role
			// ****************
			app.setCurrentPCMMRole(user, role);
			currentRole = app.getCurrentPCMMRole(user.getUserID());
			assertNotNull(currentRole);
			assertEquals(currentRole.getId(), user.getRolePCMM().getId());

			// *********
			// Get users
			// *********
			List<User> users = app.getUsers();
			assertNotNull(users);
			assertEquals(users.isEmpty(), false);

			// ***********
			// Update user
			// ***********
			user.setUserID("My_Updated_User_Id"); //$NON-NLS-1$
			User userUpdated = app.updateUser(user);
			assertNotNull(userUpdated);
			assertEquals(userUpdated.getId(), user.getId());
			assertEquals("My_Updated_User_Id", userUpdated.getUserID()); //$NON-NLS-1$

			// ***********
			// Delete user
			// ***********
			app.deleteUser(user);
			app.deleteUser(userFoundUserIdNotFound);
			users = app.getUsers();
			assertNotNull(users);
			assertEquals(users.isEmpty(), true);

			// Test with null
			app.setCurrentPCMMRole(null, null);

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

	}
}
