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

import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.application.qoiplanning.IImportQoIPlanningApp;
import gov.sandia.cf.dao.IQoIPlanningConstraintRepository;
import gov.sandia.cf.dao.IQoIPlanningParamRepository;
import gov.sandia.cf.dao.IQoIPlanningSelectValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * JUnit test class for the Import QoI Planning Application Controller
 * 
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class ImportQoIPlanningAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ImportQoIPlanningAppTest.class);

	@Test
	void test_importQoIPlanningSpecification_working() throws CredibilityException, IOException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = null;
		try {
			confFile = new File(WorkspaceTools.getStaticFilePath("configuration/QoI_Planning-v0.1.yml")); //$NON-NLS-1$
		} catch (URISyntaxException | IOException e) {
			fail(e.getMessage());
		}
		assertNotNull(confFile);

		// import
		getAppManager().getService(IImportQoIPlanningApp.class).importQoIPlanningSpecification(model, user, confFile);

		// test Parameters
		List<QoIPlanningParam> paramList = getDaoManager().getRepository(IQoIPlanningParamRepository.class).findAll();
		assertNotNull(paramList);
		assertEquals(6, paramList.size());

		// test select values
		List<QoIPlanningSelectValue> selectValues = getDaoManager()
				.getRepository(IQoIPlanningSelectValueRepository.class).findAll();
		assertNotNull(selectValues);
		assertEquals(5, selectValues.size());

		// test constraints
		List<QoIPlanningConstraint> constraints = getDaoManager().getRepository(IQoIPlanningConstraintRepository.class)
				.findAll();
		assertNotNull(constraints);
		assertEquals(2, constraints.size());
	}

	@Test
	void test_importQoIPlanningParam_working() throws CredibilityException {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// create param
		QoIPlanningParam paramParent = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), model);
		assertNotNull(paramParent);
		QoIPlanningParam paramChild1 = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), model);
		assertNotNull(paramChild1);
		QoIPlanningParam paramChild2 = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), model);
		assertNotNull(paramChild2);
		QoIPlanningParam paramRoot1 = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), model);
		assertNotNull(paramRoot1);
		QoIPlanningParam paramRoot2 = TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), model);
		assertNotNull(paramRoot2);

		// Refresh
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(paramParent);
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(paramChild1);
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(paramChild2);
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(paramRoot1);
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(paramRoot2);

		// Import
		getAppManager().getService(IImportQoIPlanningApp.class).importQoIPlanningParam(model,
				Arrays.asList(paramParent, paramRoot1, paramRoot2));

		// Refresh objects
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(paramParent);
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(paramChild1);
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(paramChild2);
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(paramRoot1);
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(paramRoot2);

		// Tests
		assertNull(paramParent.getParent());
		assertNotNull(paramParent.getChildren());
		assertEquals(0, paramParent.getChildren().size());

		List<QoIPlanningParam> all = getDaoManager().getRepository(IQoIPlanningParamRepository.class).findAll();
		assertNotNull(all);
		assertEquals(5, all.size());
	}

	@Test
	void test_importQoIPlanningConfiguration_error_modelNull() {

		// ********************
		// Import with No model
		// ********************
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IImportQoIPlanningApp.class).importQoIPlanningConfiguration(null, null);
			fail("Import with model null is not possible."); //$NON-NLS-1$
		});
		assertEquals(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL), e.getMessage());
	}

	@Test
	void test_importQoIPlanningConfiguration_error_configurationNull() throws CredibilityException {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// **********************************
		// Import with Nothing
		// **********************************
		getAppManager().getService(IImportQoIPlanningApp.class).importQoIPlanningConfiguration(createdModel, null);
	}

	@Test
	void test_analyzeUpdateQoIPlanningConfiguration_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/QoI_Planning-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportQoIPlanningApp.class)
				.analyzeUpdateQoIPlanningConfiguration(confFile);

		// test Parameters
		assertNotNull(analysis);
		assertNotNull(analysis.get(QoIPlanningParam.class));
		assertEquals(6, analysis.get(QoIPlanningParam.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(QoIPlanningParam.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(QoIPlanningParam.class).get(ImportActionType.NO_CHANGES).size());
	}

	@Test
	void test_importQoIPlanningChanges_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/QoI_Planning-v0.1.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportQoIPlanningApp.class)
				.analyzeUpdateQoIPlanningConfiguration(confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// import
		getAppManager().getService(IImportQoIPlanningApp.class).importQoIPlanningChanges(model, toChange);

		// test Parameters
		List<QoIPlanningParam> paramList = getDaoManager().getRepository(IQoIPlanningParamRepository.class).findAll();
		assertNotNull(paramList);
		assertEquals(6, paramList.size());

		// test select values
		List<QoIPlanningSelectValue> selectValues = getDaoManager()
				.getRepository(IQoIPlanningSelectValueRepository.class).findAll();
		assertNotNull(selectValues);
		assertEquals(5, selectValues.size());

		// test constraints
		List<QoIPlanningConstraint> constraints = getDaoManager().getRepository(IQoIPlanningConstraintRepository.class)
				.findAll();
		assertNotNull(constraints);
		assertEquals(2, constraints.size());
	}
}
