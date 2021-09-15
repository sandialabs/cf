/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.requirement.SystemRequirementSpecification;
import gov.sandia.cf.dao.ISystemRequirementParamRepository;
import gov.sandia.cf.dao.ISystemRequirementSelectValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * JUnit test class for the Import System Requirement Application Controller
 * 
 * @author Maxime N.
 *
 */
@RunWith(JUnitPlatform.class)
class ImportSysRequirementAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ImportSysRequirementAppTest.class);

	@Test
	void test_importSystemRequirementSpecification() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Requirement_Parameter-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import
		getAppManager().getService(IImportSysRequirementApp.class).importSysRequirementSpecification(model, confFile);

		// test Uncertainty Parameter
		List<SystemRequirementParam> param = getDaoManager().getRepository(ISystemRequirementParamRepository.class)
				.findAll();
		assertNotNull(param);
		assertEquals(5, param.size());

		// test SysRequirement select values
		List<SystemRequirementSelectValue> selectValues = getDaoManager()
				.getRepository(ISystemRequirementSelectValueRepository.class).findAll();
		assertNotNull(selectValues);
		assertEquals(10, selectValues.size());

	}

	@Test
	void test_import_working() throws CredibilityException {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// create param
		SystemRequirementParam paramParent = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				null);
		assertNotNull(paramParent);
		SystemRequirementParam paramChild1 = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				paramParent);
		assertNotNull(paramChild1);
		SystemRequirementParam paramChild2 = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				paramParent);
		assertNotNull(paramChild2);
		SystemRequirementParam paramRoot1 = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				null);
		assertNotNull(paramRoot1);
		SystemRequirementParam paramRoot2 = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				null);
		assertNotNull(paramRoot2);

		// Refresh
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(paramParent);
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(paramChild1);
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(paramChild2);
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(paramRoot1);
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(paramRoot2);

		// Import
		getAppManager().getService(IImportSysRequirementApp.class).importSysRequirementParam(model,
				Arrays.asList(paramParent, paramRoot1, paramRoot2));

		// Refresh objects
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(paramParent);
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(paramChild1);
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(paramChild2);
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(paramRoot1);
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(paramRoot2);

		// Tests
		assertNull(paramParent.getParent());
		assertNotNull(paramParent.getChildren());
		assertEquals(2, paramParent.getChildren().size());

		List<SystemRequirementParam> all = getDaoManager().getRepository(ISystemRequirementParamRepository.class)
				.findAll();
		assertNotNull(all);
		assertEquals(5, all.size());
	}

	@Test
	void test_import_error_modelNull() {

		// ********************
		// Import with No model
		// ********************
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IImportSysRequirementApp.class).importSysRequirementConfiguration(null, null);
			fail("Import with model null is not possible."); //$NON-NLS-1$
		});
		assertEquals(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL), e.getMessage());
	}

	@Test
	void test_import_error_configurationNull() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// **********************************
		// Import with Nothing
		// **********************************
		try {
			getAppManager().getService(IImportSysRequirementApp.class).importSysRequirementConfiguration(createdModel,
					null);
		} catch (CredibilityException e) {
			fail("Import with nothing should work."); //$NON-NLS-1$
		}
	}

	@Test
	void test_analyzeUpdateSysRequirementConfiguration_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Requirement_Parameter-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager()
				.getService(IImportSysRequirementApp.class)
				.analyzeUpdateRequirementsConfiguration(model, new SystemRequirementSpecification(), confFile);

		// test
		assertNotNull(analysis);

		// test SystemRequirementParam
		assertNotNull(analysis.get(SystemRequirementParam.class));
		assertEquals(5, analysis.get(SystemRequirementParam.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(SystemRequirementParam.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(SystemRequirementParam.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_importSysRequirementChanges_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Requirement_Parameter-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager()
				.getService(IImportSysRequirementApp.class)
				.analyzeUpdateRequirementsConfiguration(model, new SystemRequirementSpecification(), confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// import
		getAppManager().getService(IImportSysRequirementApp.class).importSysRequirementChanges(model, toChange);

		// test SystemRequirementParam
		List<SystemRequirementParam> paramList = getDaoManager().getRepository(ISystemRequirementParamRepository.class)
				.findAll();
		assertNotNull(paramList);
		assertEquals(5, paramList.size());

		// test SystemRequirement select values
		List<SystemRequirementSelectValue> selectValues = getDaoManager()
				.getRepository(ISystemRequirementSelectValueRepository.class).findAll();
		assertNotNull(selectValues);
		assertEquals(10, selectValues.size());
	}
}
