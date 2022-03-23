/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.application.uncertainty.IImportUncertaintyApp;
import gov.sandia.cf.application.uncertainty.YmlReaderUncertaintySchema;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.dao.IUncertaintyRepository;
import gov.sandia.cf.dao.IUncertaintySelectValueRepository;
import gov.sandia.cf.dao.IUncertaintyValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;
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
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintySpecification(model, user, confFile);

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
	void test_importUncertaintySpecification_WithData() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_with_data.yml")); //$NON-NLS-1$

		// import
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintySpecification(model, user, confFile);

		// test Uncertainty Parameter
		List<UncertaintyParam> uncertaintyParam = getDaoManager().getRepository(IUncertaintyParamRepository.class)
				.findAll();
		assertNotNull(uncertaintyParam);
		assertEquals(8, uncertaintyParam.size());

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
	void test_analyzeUpdateUncertaintyConfiguration_TO_ADD_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportUncertaintyApp.class)
				.analyzeUpdateUncertaintyConfiguration(model, new UncertaintySpecification(), confFile);

		// test
		assertNotNull(analysis);

		// test UncertaintyAdequacyColumn
		assertNotNull(analysis.get(UncertaintyParam.class));
		assertEquals(9, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_analyzeUpdateUncertaintyConfiguration_TO_DELETE_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_empty.yml")); //$NON-NLS-1$
		YmlReaderUncertaintySchema reader = new YmlReaderUncertaintySchema();
		UncertaintySpecification specification = reader
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml"))); //$NON-NLS-1$

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportUncertaintyApp.class)
				.analyzeUpdateUncertaintyConfiguration(model, specification, confFile);

		// test
		assertNotNull(analysis);

		// test UncertaintyAdequacyColumn
		assertNotNull(analysis.get(UncertaintyParam.class));
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_ADD).size());
		assertEquals(9, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_analyzeUpdateUncertaintyConfiguration_TO_UPDATE_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		YmlReaderUncertaintySchema reader = new YmlReaderUncertaintySchema();
		UncertaintySpecification specification = reader
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml"))); //$NON-NLS-1$
		specification.getParameters().forEach(p -> p.setLevel("=150")); //$NON-NLS-1$

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportUncertaintyApp.class)
				.analyzeUpdateUncertaintyConfiguration(model, specification, confFile);

		// test
		assertNotNull(analysis);

		// test UncertaintyAdequacyColumn
		assertNotNull(analysis.get(UncertaintyParam.class));
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(9, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_analyzeUpdateUncertaintyConfiguration_NO_CHANGES_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		YmlReaderUncertaintySchema reader = new YmlReaderUncertaintySchema();
		UncertaintySpecification specification = reader
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml"))); //$NON-NLS-1$

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportUncertaintyApp.class)
				.analyzeUpdateUncertaintyConfiguration(model, specification, confFile);

		// test
		assertNotNull(analysis);

		// test UncertaintyAdequacyColumn
		assertNotNull(analysis.get(UncertaintyParam.class));
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(UncertaintyParam.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(9, analysis.get(UncertaintyParam.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_importUncertaintyChanges_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportUncertaintyApp.class)
				.analyzeUpdateUncertaintyConfiguration(model, new UncertaintySpecification(), confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// import
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintyChanges(model, newUser, toChange);

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

	@Test
	void test_importUpdateUncertaintyParam_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_v0.0.yml")); //$NON-NLS-1$
		YmlReaderUncertaintySchema readerUncertaintySchema = new YmlReaderUncertaintySchema();
		UncertaintySpecification specification = readerUncertaintySchema.load(confFile);
		// made some changes
		specification.getParameters().forEach(p -> p.setLevel("=150")); //$NON-NLS-1$
		// first import with updated
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintyConfiguration(model, specification);
		assertTrue(getDaoManager().getRepository(IUncertaintyParamRepository.class).findAll().stream()
				.allMatch(p -> p.getLevel().equals("=150"))); //$NON-NLS-1$

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportUncertaintyApp.class)
				.analyzeUpdateUncertaintyConfiguration(model, specification, confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);
		assertEquals(9, toChange.get(UncertaintyParam.class).get(ImportActionType.TO_UPDATE).size());

		// import update
		getAppManager().getService(IImportUncertaintyApp.class).importUpdateUncertaintyParam(model,
				toChange.get(UncertaintyParam.class).get(ImportActionType.TO_UPDATE).stream()
						.filter(UncertaintyParam.class::isInstance).map(UncertaintyParam.class::cast)
						.collect(Collectors.toList()));

		// test UncertaintyParam
		List<UncertaintyParam> paramList = getDaoManager().getRepository(IUncertaintyParamRepository.class).findAll();
		assertNotNull(paramList);
		assertEquals(9, paramList.size());
		assertFalse(paramList.stream().anyMatch(p -> p.getLevel().equals("=150"))); //$NON-NLS-1$

		// test Uncertainty select values
		List<UncertaintySelectValue> uncertaintySelectValues = getDaoManager()
				.getRepository(IUncertaintySelectValueRepository.class).findAll();
		assertNotNull(uncertaintySelectValues);
		assertEquals(10, uncertaintySelectValues.size());
	}

	/////////////////////////// Uncertainty data

	@Test
	void test_importUncertaintyData() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/Uncertainty_Parameter_with_data.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// need to import uncertainty parameters first
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintySpecification(model, user, confFile);

		// import data
		getAppManager().getService(IImportUncertaintyApp.class).importUncertaintyData(model, user, confFile);

		// test Uncertainty Parameter
		List<UncertaintyParam> uncertaintyParamList = getDaoManager().getRepository(IUncertaintyParamRepository.class)
				.findAll();
		assertNotNull(uncertaintyParamList);
		assertEquals(8, uncertaintyParamList.size());

		// test Uncertainty select values
		List<UncertaintySelectValue> uncertaintySelectValues = getDaoManager()
				.getRepository(IUncertaintySelectValueRepository.class).findAll();
		assertNotNull(uncertaintySelectValues);
		assertEquals(10, uncertaintySelectValues.size());

		// test Uncertainty
		List<Uncertainty> uncertaintyList = getDaoManager().getRepository(IUncertaintyRepository.class).findAll();
		assertNotNull(uncertaintyList);
		assertEquals(6, uncertaintyList.size());
		List<Uncertainty> parentList = uncertaintyList.stream().filter(u -> u.getParent() == null)
				.collect(Collectors.toList());
		List<Uncertainty> childrenList = uncertaintyList.stream().filter(u -> u.getParent() != null)
				.collect(Collectors.toList());
		assertEquals(4, parentList.size());
		assertEquals(2, childrenList.size());

		// test Uncertainty values
		List<UncertaintyValue> uncertaintyValueList = getDaoManager().getRepository(IUncertaintyValueRepository.class)
				.findAll();
		assertNotNull(uncertaintyValueList);
		assertEquals(14, uncertaintyValueList.size());
		assertTrue(uncertaintyValueList.stream().allMatch(u -> uncertaintyParamList.contains(u.getParameter())));
		assertTrue(uncertaintyValueList.stream().allMatch(u -> childrenList.contains(u.getUncertainty())));
		assertTrue(uncertaintyValueList.stream().allMatch(u -> !StringUtils.isBlank(u.getValue())));
	}

}
