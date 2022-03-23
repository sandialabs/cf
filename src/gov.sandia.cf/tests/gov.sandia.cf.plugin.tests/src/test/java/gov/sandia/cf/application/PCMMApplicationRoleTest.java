/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the PCMM Application Controller
 */
@RunWith(JUnitPlatform.class)
class PCMMApplicationRoleTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMApplicationRoleTest.class);

	@Test
	void testRoleCRUDWorking() {
		// Get Application
		IPCMMApplication app = getAppManager().getService(IPCMMApplication.class);

		// ****************
		// Test role
		// ****************
		Role role = new Role();
		role.setName("My_Role"); //$NON-NLS-1$
		try {
			role = app.addRole(role);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(role);

		// ****************
		// Test role list
		// ****************
		List<Role> roles = app.getRoles();
		assertFalse(roles.isEmpty());

		// ******************
		// Test get role
		// ******************
		try {
			Role roleGet = app.getRoleById(roles.get(0).getId());
			assertNotNull(roleGet);
			assertEquals(roleGet.getId(), roles.get(0).getId());
		} catch (CredibilityException e) {
			fail("PCMMApplication.getRoleById doesn't work."); //$NON-NLS-1$
		}

		// ******************
		// Update role
		// ******************
		Role updatedRole;
		role.setName("My_Updated_Role"); //$NON-NLS-1$
		try {
			updatedRole = app.updateRole(role);
			assertNotNull(updatedRole);
			assertEquals(updatedRole.getName(), "My_Updated_Role"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			fail("PCMMApplication.updateRole doesn't work."); //$NON-NLS-1$
		}

		// ******************
		// Delete role
		// ******************
		try {
			app.deleteRole(role);
			assertNull(app.getRoleById(role.getId()));
		} catch (CredibilityException e) {
			fail("PCMMApplication.deleteRole doesn't work."); //$NON-NLS-1$
		}
	}

	@Test
	void testAddRole_ErrorIdNull() {
		try {
			getAppManager().getService(IPCMMApplication.class).addRole(null);
			fail("Can create Role with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDROLE_ROLENULL), e.getMessage());
		}
	}

	@Test
	void testGetRole_ErrorIdNull() {
		// ********************
		// Test get role null
		// ********************
		try {
			getAppManager().getService(IPCMMApplication.class).getRoleById(null);
			fail("Can get Role with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_GETROLEBYID_IDNULL), e.getMessage());
		}
	}

	@Test
	void testUpdateRole_ErrorIdNull() {
		// **********************
		// Test update role null
		// **********************
		try {
			getAppManager().getService(IPCMMApplication.class).updateRole(null);
			fail("Can update Role with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEROLE_ROLENULL), e.getMessage());
		}

		// ************************
		// Test update role id null
		// ************************
		try {
			getAppManager().getService(IPCMMApplication.class).updateRole(new Role());
			fail("Can update Role with id null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEROLE_IDNULL), e.getMessage());
		}
	}

	@Test
	void testDeleteRole_ErrorIdNull() {
		// **********************
		// Test update role null
		// **********************
		try {
			getAppManager().getService(IPCMMApplication.class).deleteRole(null);
			fail("Can delete Role with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETEROLE_ROLENULL), e.getMessage());
		}

		// ************************
		// Test update role id null
		// ************************
		try {
			getAppManager().getService(IPCMMApplication.class).deleteRole(new Role());
			fail("Can delete Role with id null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETEROLE_IDNULL), e.getMessage());
		}
	}
}
