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

import gov.sandia.cf.application.configuration.decision.DecisionSpecification;
import gov.sandia.cf.dao.IDecisionConstraintRepository;
import gov.sandia.cf.dao.IDecisionParamRepository;
import gov.sandia.cf.dao.IDecisionSelectValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * JUnit test class for the Import Decision Application Controller
 * 
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class ImportDecisionAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ImportDecisionAppTest.class);

	@Test
	void test_importDecisionSpecification_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import
		getAppManager().getService(IImportDecisionApp.class).importDecisionSpecification(model, confFile);

		// test Parameters
		List<DecisionParam> paramList = getDaoManager().getRepository(IDecisionParamRepository.class).findAll();
		assertNotNull(paramList);
		assertEquals(9, paramList.size());

		// test select values
		List<DecisionSelectValue> selectValues = getDaoManager().getRepository(IDecisionSelectValueRepository.class)
				.findAll();
		assertNotNull(selectValues);
		assertEquals(20, selectValues.size());

		// test constraints
		List<DecisionConstraint> constraints = getDaoManager().getRepository(IDecisionConstraintRepository.class)
				.findAll();
		assertNotNull(constraints);
		assertEquals(0, constraints.size());
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
		DecisionParam paramParent = TestEntityFactory.getNewDecisionParam(getDaoManager(), model, null);
		assertNotNull(paramParent);
		DecisionParam paramChild1 = TestEntityFactory.getNewDecisionParam(getDaoManager(), model, paramParent);
		assertNotNull(paramChild1);
		DecisionParam paramChild2 = TestEntityFactory.getNewDecisionParam(getDaoManager(), model, paramParent);
		assertNotNull(paramChild2);
		DecisionParam paramRoot1 = TestEntityFactory.getNewDecisionParam(getDaoManager(), model, null);
		assertNotNull(paramRoot1);
		DecisionParam paramRoot2 = TestEntityFactory.getNewDecisionParam(getDaoManager(), model, null);
		assertNotNull(paramRoot2);

		// Refresh
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(paramParent);
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(paramChild1);
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(paramChild2);
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(paramRoot1);
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(paramRoot2);

		// Import
		getAppManager().getService(IImportDecisionApp.class).importDecisionParam(model,
				Arrays.asList(paramParent, paramRoot1, paramRoot2));

		// Refresh objects
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(paramParent);
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(paramChild1);
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(paramChild2);
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(paramRoot1);
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(paramRoot2);

		// Tests
		assertNull(paramParent.getParent());
		assertNotNull(paramParent.getChildren());
		assertEquals(2, paramParent.getChildren().size());

		List<DecisionParam> all = getDaoManager().getRepository(IDecisionParamRepository.class).findAll();
		assertNotNull(all);
		assertEquals(5, all.size());
	}

	@Test
	void test_import_error_modelNull() {

		// ********************
		// Import with No model
		// ********************
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IImportDecisionApp.class).importDecisionConfiguration(null, null);
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
		// Import with Nothing
		// **********************************
		getAppManager().getService(IImportDecisionApp.class).importDecisionConfiguration(createdModel, null);
	}

	@Test
	void test_analyzeUpdateDecisionConfiguration_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportDecisionApp.class)
				.analyzeUpdateDecisionConfiguration(model, new DecisionSpecification(), confFile);

		// test
		assertNotNull(analysis);

		// test DecisionParam
		assertNotNull(analysis.get(DecisionParam.class));
		assertEquals(9, analysis.get(DecisionParam.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(DecisionParam.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(DecisionParam.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_importDecisionChanges_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/ModSim_Decision-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportDecisionApp.class)
				.analyzeUpdateDecisionConfiguration(model, new DecisionSpecification(), confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// import
		getAppManager().getService(IImportDecisionApp.class).importDecisionChanges(model, toChange);

		// test DecisionParam
		List<DecisionParam> paramList = getDaoManager().getRepository(IDecisionParamRepository.class).findAll();
		assertNotNull(paramList);
		assertEquals(9, paramList.size());

		// test Decision select values
		List<DecisionSelectValue> selectValues = getDaoManager().getRepository(IDecisionSelectValueRepository.class)
				.findAll();
		assertNotNull(selectValues);
		assertEquals(20, selectValues.size());
	}
}
