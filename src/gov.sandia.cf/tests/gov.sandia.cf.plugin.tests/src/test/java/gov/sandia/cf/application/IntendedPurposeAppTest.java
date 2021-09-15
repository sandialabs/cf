/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 *
 * JUnit test class for the Intended Purpose Application Controller
 * 
 * @author Didier Verstraete
 */
@RunWith(JUnitPlatform.class)
class IntendedPurposeAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(IntendedPurposeAppTest.class);

	@Test
	void test_get_Working_AlreadyExists() throws CredibilityException {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		IntendedPurpose newIntendedPurpose = TestEntityFactory.getNewIntendedPurpose(getDaoManager(), newModel);

		IntendedPurpose intendedPurpose = getAppManager().getService(IIntendedPurposeApp.class).get(newModel);

		assertEquals(newIntendedPurpose, intendedPurpose);
	}

	@Test
	void test_get_Working_DoesNotExist() throws CredibilityException {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		IntendedPurpose intendedPurpose = getAppManager().getService(IIntendedPurposeApp.class).get(newModel);

		assertNotNull(intendedPurpose);
		assertNotNull(intendedPurpose.getId());
	}

	@Test
	void test_updateIntendedPurpose_Working() throws CredibilityException {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		IntendedPurpose intendedPurpose = getAppManager().getService(IIntendedPurposeApp.class).get(newModel);

		intendedPurpose.setDescription("New Description"); //$NON-NLS-1$
		intendedPurpose
				.setReference(TestEntityFactory.getParameterLinkGson(FormFieldType.LINK_URL, "http://example.com")); //$NON-NLS-1$
		IntendedPurpose updatedIntendedPurpose = getAppManager().getService(IIntendedPurposeApp.class)
				.updateIntendedPurpose(intendedPurpose, newUser);

		assertNotNull(updatedIntendedPurpose);
		assertNotNull(updatedIntendedPurpose.getId());
		assertNotNull(updatedIntendedPurpose.getDateUpdate());
		assertEquals(newUser, updatedIntendedPurpose.getUserUpdate());
		assertEquals("New Description", updatedIntendedPurpose.getDescription()); //$NON-NLS-1$
		assertEquals(TestEntityFactory.getParameterLinkGson(FormFieldType.LINK_URL, "http://example.com"), //$NON-NLS-1$
				updatedIntendedPurpose.getReference());
	}

	@Test
	void test_updateIntendedPurpose_Null() {
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		CredibilityException e = assertThrows(CredibilityException.class,
				() -> getAppManager().getService(IIntendedPurposeApp.class).updateIntendedPurpose(null, newUser));
		assertEquals(RscTools.getString(RscConst.EX_INTENDEDPURPOSE_UPDATE_INTENDEDPURPOSE_NULL), e.getMessage());
	}

	@Test
	void test_updateIntendedPurpose_UserUpdateNull() throws CredibilityException {
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		IntendedPurpose intendedPurpose = getAppManager().getService(IIntendedPurposeApp.class).get(newModel);

		CredibilityException e = assertThrows(CredibilityException.class, () -> getAppManager()
				.getService(IIntendedPurposeApp.class).updateIntendedPurpose(intendedPurpose, null));
		assertEquals(RscTools.getString(RscConst.EX_INTENDEDPURPOSE_UPDATE_USER_NULL), e.getMessage());
	}
}
