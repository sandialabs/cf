/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.global.IGlobalApplication;
import gov.sandia.cf.constants.configuration.ConfigurationFileType;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GlobalConfiguration;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.model.dto.configuration.ConfigurationSchema;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Didier Verstraete
 *
 *         JUnit test class for the Global Application Controller
 */
class GlobalApplicationTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(GlobalApplicationTest.class);

	@Test
	void testModelCRUD() {

		// ********************************
		// test exist model before creation
		// ********************************
		Boolean existModel = null;
		existModel = getAppManager().getService(IGlobalApplication.class).existsModel();
		assertFalse(existModel);

		// ********************************
		// testImportModel
		// ********************************
		Model loadedModel = null;
		try {
			// Schema files
			ConfigurationSchema confSchema = new ConfigurationSchema();
			confSchema.put(ConfigurationFileType.PIRT, new File("Path/PIRT")); //$NON-NLS-1$
			confSchema.put(ConfigurationFileType.QOIPLANNING, new File("Path/QoIPlanning")); //$NON-NLS-1$
			confSchema.put(ConfigurationFileType.PCMM, new File("Path/PCMM")); //$NON-NLS-1$
			confSchema.put(ConfigurationFileType.UNCERTAINTY, new File("Path/UNCERTAINTY")); //$NON-NLS-1$
			confSchema.put(ConfigurationFileType.SYSTEM_REQUIREMENT, new File("Path/REQUIREMENT")); //$NON-NLS-1$

			loadedModel = getAppManager().getService(IGlobalApplication.class).importModel(confSchema);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(loadedModel);
		assertNotNull(loadedModel.getId());

		// ********************************
		// testLoadModel
		// ********************************
		loadedModel = null;
		try {
			loadedModel = getAppManager().getService(IGlobalApplication.class).loadModel();
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(loadedModel);
		assertNotNull(loadedModel.getId());

		// ********************************
		// test exist model after creation
		// ********************************
		existModel = getAppManager().getService(IGlobalApplication.class).existsModel();
		assertTrue(existModel);

		// ********************************
		// test update model
		// ********************************
		// load existing model
		try {
			loadedModel = getAppManager().getService(IGlobalApplication.class).loadModel();
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(loadedModel);

		// ********************************
		// test update model
		// ********************************
		loadedModel.setApplication("My application"); //$NON-NLS-1$
		loadedModel.setContact("My Contact"); //$NON-NLS-1$
		loadedModel.setVersion("0.2.0-TEXT"); //$NON-NLS-1$
		loadedModel.setVersionOrigin("0.1.0-TEXT"); //$NON-NLS-1$

		Model updatedModel = null;
		try {
			updatedModel = getAppManager().getService(IGlobalApplication.class).updateModel(loadedModel);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// check model
		assertNotNull(updatedModel);
		assertNotNull(updatedModel.getId());
		assertEquals(loadedModel.getId(), updatedModel.getId());
		assertEquals("My application", updatedModel.getApplication()); //$NON-NLS-1$
		assertEquals("My Contact", updatedModel.getContact()); //$NON-NLS-1$
		assertEquals("0.2.0-TEXT", updatedModel.getVersion()); //$NON-NLS-1$
		assertEquals("0.1.0-TEXT", updatedModel.getVersionOrigin()); //$NON-NLS-1$
	}

	@Test
	void testUpdateModel_ErrorModelNull() {
		try {
			getAppManager().getService(IGlobalApplication.class).updateModel(null);

			fail("The app manager must raise an exception if the model is null."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UPDATEMODEL_MODELNULL), e.getMessage());
		}
	}

	@Test
	void testUpdateModel_ErrorModelIdNull() {
		try {
			getAppManager().getService(IGlobalApplication.class).updateModel(new Model());

			fail("The app manager must raise an exception if the model is null."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UPDATEMODEL_IDNULL), e.getMessage());
		}
	}

	@Test
	void testLoadModelClass() {
		@SuppressWarnings("rawtypes")
		Class loadModelClass = getAppManager().getService(IGlobalApplication.class).loadModelClass("Model"); //$NON-NLS-1$
		assertEquals(Model.class, loadModelClass);
	}

	@Test
	void testLoadModelClass_ErrorClassNotFound() {
		@SuppressWarnings("rawtypes")
		Class loadModelClass = getAppManager().getService(IGlobalApplication.class).loadModelClass("BLABLABLA"); //$NON-NLS-1$
		assertEquals(Object.class, loadModelClass);
	}

	// ###################################################################
	// ##################### GlobalConfiguration #########################
	// ###################################################################

	@Test
	void test_loadGlobalConfiguration_DoesNotExist() throws CredibilityException {
		GlobalConfiguration loadGlobalConfiguration = getAppManager().getService(IGlobalApplication.class)
				.loadGlobalConfiguration();
		assertNotNull(loadGlobalConfiguration);
		assertNotNull(loadGlobalConfiguration.getId());
	}

	@Test
	void test_loadGlobalConfiguration_Exists() throws CredibilityException {

		GlobalConfiguration loadGlobalConfiguration = getAppManager().getService(IGlobalApplication.class)
				.loadGlobalConfiguration();

		GlobalConfiguration loadGlobalConfiguration2 = getAppManager().getService(IGlobalApplication.class)
				.loadGlobalConfiguration();

		assertNotNull(loadGlobalConfiguration2);
		assertNotNull(loadGlobalConfiguration2.getId());
		assertEquals(loadGlobalConfiguration, loadGlobalConfiguration2);
	}

	@Test
	void test_updateGlobalConfiguration_Working() throws CredibilityException {

		GlobalConfiguration loadGlobalConfiguration = getAppManager().getService(IGlobalApplication.class)
				.loadGlobalConfiguration();

		String oldOpenLinkBrowserOpts = loadGlobalConfiguration.getOpenLinkBrowserOpts();
		assertNotEquals(OpenLinkBrowserOption.EXTERNAL_BROWSER.name(), oldOpenLinkBrowserOpts);

		loadGlobalConfiguration.setOpenLinkBrowserOpts(OpenLinkBrowserOption.EXTERNAL_BROWSER.name());

		GlobalConfiguration updatedGlobalConfiguration = getAppManager().getService(IGlobalApplication.class)
				.updateGlobalConfiguration(loadGlobalConfiguration);

		assertNotNull(updatedGlobalConfiguration);
		assertNotNull(updatedGlobalConfiguration.getId());
		assertEquals(OpenLinkBrowserOption.EXTERNAL_BROWSER.name(),
				updatedGlobalConfiguration.getOpenLinkBrowserOpts());
	}

	@Test
	void test_updateGlobalConfiguration_Conf_Null() throws CredibilityException {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IGlobalApplication.class).updateGlobalConfiguration(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_UPDATEGLBCONF_CONFNULL), e.getMessage());
	}

	@Test
	void test_updateGlobalConfiguration_ConfId_Null() throws CredibilityException {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IGlobalApplication.class).updateGlobalConfiguration(new GlobalConfiguration());
		});
		assertEquals(RscTools.getString(RscConst.EX_UPDATEGLBCONF_IDNULL), e.getMessage());
	}

	@Test
	void test_getOpenLinkBrowserOpts_Working() throws CredibilityException {
		GlobalConfiguration loadGlobalConfiguration = getAppManager().getService(IGlobalApplication.class)
				.loadGlobalConfiguration();

		OpenLinkBrowserOption openLinkBrowserOpts = getAppManager().getService(IGlobalApplication.class)
				.getOpenLinkBrowserOpts();
		assertEquals(OpenLinkBrowserOption.valueOf(loadGlobalConfiguration.getOpenLinkBrowserOpts()),
				openLinkBrowserOpts);
	}

}
