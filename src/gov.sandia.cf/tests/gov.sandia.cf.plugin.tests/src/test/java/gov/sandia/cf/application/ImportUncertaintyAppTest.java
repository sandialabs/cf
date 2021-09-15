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

import gov.sandia.cf.application.configuration.uncertainty.UncertaintySpecification;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.dao.IUncertaintySelectValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * JUnit test class for the import Uncertainty Application Controller
 * 
 * @author Maxime N.
 *
 */
@RunWith(JUnitPlatform.class)
class ImportUncertaintyAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ImportUncertaintyAppTest.class);

	@Test
	void test_importUncertaintySpecification() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintySpecification(model, confFile);

		// test Uncertainty Parameter
		List<UncertaintyParam> uncertaintyParam = getDaoManager().getRepository(IUncertaintyParamRepository.class)
				.findAll();
		assertNotNull(uncertaintyParam);
		assertEquals(9, uncertaintyParam.size());

		// test Uncertainty select values
		List<UncertaintySelectValue> uncertaintySelectValues = getDaoManager()
				.getRepository(IUncertaintySelectValueRepository.class).findAll();
		assertNotNull(uncertaintySelectValues);
		assertEquals(10, uncertaintySelectValues.size());

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
		UncertaintyParam paramParent = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), model, null);
		assertNotNull(paramParent);
		UncertaintyParam paramChild1 = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), model, paramParent);
		assertNotNull(paramChild1);
		UncertaintyParam paramChild2 = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), model, paramParent);
		assertNotNull(paramChild2);
		UncertaintyParam paramRoot1 = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), model, null);
		assertNotNull(paramRoot1);
		UncertaintyParam paramRoot2 = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), model, null);
		assertNotNull(paramRoot2);

		// Refresh
		getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(paramParent);
		getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(paramChild1);
		getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(paramChild2);
		getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(paramRoot1);
		getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(paramRoot2);

		// Import
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintyParam(model,
				Arrays.asList(paramParent, paramRoot1, paramRoot2));

		// Refresh objects
		getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(paramParent);
		getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(paramChild1);
		getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(paramChild2);
		getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(paramRoot1);
		getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(paramRoot2);

		// Tests
		assertNull(paramParent.getParent());
		assertNotNull(paramParent.getChildren());
		assertEquals(2, paramParent.getChildren().size());

		List<UncertaintyParam> all = getDaoManager().getRepository(IUncertaintyParamRepository.class).findAll();
		assertNotNull(all);
		assertEquals(5, all.size());

	}

	@Test
	void test_import_error_modelNull() {

		// ********************
		// Import with No model
		// ********************
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IImportUncertaintyApp.class).importUncertaintyConfiguration(null, null);
			fail("Import with model null is not possible."); //$NON-NLS-1$
		});
		assertEquals(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL), e.getMessage());
	}

	@Test
	void test_import_error_configurationNull() throws CredibilityException {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// **********************************
		// Import with nothing
		// **********************************
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintyConfiguration(createdModel, null);
	}

	@Test
	void test_analyzeUpdateUncertaintyConfiguration_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportUncertaintyApp.class)
				.analyzeUpdateUncertaintyConfiguration(model, new UncertaintySpecification(), confFile);

		// test
		assertNotNull(analysis);

		// test UncertaintyAdequacyColumn
		assertNotNull(analysis.get(UncertaintyParam.class));
		assertEquals(9, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_importUncertaintyChanges_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportUncertaintyApp.class)
				.analyzeUpdateUncertaintyConfiguration(model, new UncertaintySpecification(), confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// import
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintyChanges(model, toChange);

		// test UncertaintyParam
		List<UncertaintyParam> paramList = getDaoManager().getRepository(IUncertaintyParamRepository.class).findAll();
		assertNotNull(paramList);
		assertEquals(9, paramList.size());

		// test Uncertainty select values
		List<UncertaintySelectValue> uncertaintySelectValues = getDaoManager()
				.getRepository(IUncertaintySelectValueRepository.class).findAll();
		assertNotNull(uncertaintySelectValues);
		assertEquals(10, uncertaintySelectValues.size());
	}
}
