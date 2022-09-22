/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.application.pcmm.IImportPCMMApp;
import gov.sandia.cf.application.pcmm.YmlReaderPCMMSchema;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.IPCMMElementRepository;
import gov.sandia.cf.dao.IPCMMLevelDescRepository;
import gov.sandia.cf.dao.IPCMMLevelRepository;
import gov.sandia.cf.dao.IPCMMOptionRepository;
import gov.sandia.cf.dao.IPCMMPlanningParamRepository;
import gov.sandia.cf.dao.IPCMMPlanningQuestionRepository;
import gov.sandia.cf.dao.IPCMMPlanningSelectValueRepository;
import gov.sandia.cf.dao.IPCMMSubelementRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * JUnit test class for the Import PCMM Application Controller
 * 
 * @author Maxime N.
 *
 */
class ImportPCMMAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ImportPCMMAppTest.class);

	@Test
	void test_importPCMMSpecification() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import
		getAppManager().getService(IImportPCMMApp.class).importPCMMSpecification(model, user, confFile);

		// test PCMM Phases
		List<PCMMOption> pcmmOptions = getPCMMApp().getPCMMOptions();
		assertNotNull(pcmmOptions);
		assertEquals(5, pcmmOptions.size());

		// test PCMM Roles
		List<Role> roles = getPCMMApp().getRoles();
		assertNotNull(roles);
		assertEquals(6, roles.size());

		// test PCMM Levels
		List<PCMMLevel> levels = getDaoManager().getRepository(IPCMMLevelRepository.class).findAll();
		assertNotNull(levels);
		assertEquals(108, levels.size()); // 4 levels * 27

		// test PCMM Elements
		List<PCMMElement> elementList = getDaoManager().getRepository(IPCMMElementRepository.class).findAll();
		assertNotNull(elementList);
		assertEquals(6, elementList.size());

		// test PCMM Subelements
		List<PCMMSubelement> subelementList = getDaoManager().getRepository(IPCMMSubelementRepository.class).findAll();
		assertNotNull(subelementList);
		assertEquals(27, subelementList.size());

		// test PCMM Planning Fields
		List<PCMMPlanningParam> planningFields = getDaoManager().getRepository(IPCMMPlanningParamRepository.class)
				.findAll();
		assertNotNull(planningFields);
		assertEquals(12, planningFields.size());

		// test PCMM Planning select values
		List<PCMMPlanningSelectValue> planningSelectValues = getDaoManager()
				.getRepository(IPCMMPlanningSelectValueRepository.class).findAll();
		assertNotNull(planningSelectValues);
		assertEquals(7, planningSelectValues.size());

		// test PCMM Planning Questions
		List<PCMMPlanningQuestion> planningQuestions = getDaoManager()
				.getRepository(IPCMMPlanningQuestionRepository.class).findAll();
		assertNotNull(planningQuestions);
		assertEquals(39, planningQuestions.size());

	}

	@Test
	void test_import_working() throws CredibilityException {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role
		Role role = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(role);

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// create element
		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		assertNotNull(element);

		// create subelement
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), element);
		assertNotNull(subelement);

		// create level
		PCMMLevel level = TestEntityFactory.getNewPCMMLevel(getDaoManager(), element, 0);
		assertNotNull(level);

		// create level descriptor
		PCMMLevelDescriptor levelDescriptor = TestEntityFactory.getNewPCMMLevelDescriptor(getDaoManager(), level);
		assertNotNull(levelDescriptor);

		// Refresh
		getDaoManager().getRepository(IPCMMLevelRepository.class).refresh(level);
		getDaoManager().getRepository(IPCMMSubelementRepository.class).refresh(subelement);
		getDaoManager().getRepository(IPCMMElementRepository.class).refresh(element);
		getDaoManager().getRepository(IModelRepository.class).refresh(model);

		// Import (element level list)
		getAppManager().getService(IImportPCMMApp.class).importPCMMElements(model, getPCMMApp().getElementList(model));
		getAppManager().getService(IImportPCMMApp.class).importPCMMRoles(getPCMMApp().getRoles());

		// Refresh objects
		getDaoManager().getRepository(IPCMMSubelementRepository.class).refresh(subelement);
		getDaoManager().getRepository(IPCMMElementRepository.class).refresh(element);

		// Tests
		assertFalse(element.getLevelList().isEmpty());
		assertTrue(subelement.getLevelList().isEmpty());

		// Level list on sub-element
		level.setElement(null);
		level.setSubelement(subelement);
		level = getPCMMApp().updateLevel(level);

		// Refresh
		getDaoManager().getRepository(IPCMMLevelDescRepository.class).refresh(levelDescriptor);
		getDaoManager().getRepository(IPCMMLevelRepository.class).refresh(level);
		getDaoManager().getRepository(IPCMMSubelementRepository.class).refresh(subelement);
		getDaoManager().getRepository(IPCMMElementRepository.class).refresh(element);
		getDaoManager().getRepository(IModelRepository.class).refresh(model);

		// Import (sub-element level list)
		getAppManager().getService(IImportPCMMApp.class).importPCMMElements(model, getPCMMApp().getElementList(model));
		getAppManager().getService(IImportPCMMApp.class).importPCMMRoles(getPCMMApp().getRoles());

		// Refresh objects
		getDaoManager().getRepository(IPCMMSubelementRepository.class).refresh(subelement);
		getDaoManager().getRepository(IPCMMElementRepository.class).refresh(element);

		// Tests
		assertTrue(element.getLevelList().isEmpty());
		assertFalse(subelement.getLevelList().isEmpty());
	}

	@Test
	void test_import_error_modelNull() {

		// ********************
		// Import with No model
		// ********************
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IImportPCMMApp.class).importPCMMConfiguration(null, null);
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
		// Import with No roles & No elements
		// **********************************
		getAppManager().getService(IImportPCMMApp.class).importPCMMConfiguration(createdModel, null);
	}

	@Test
	void test_analyzeUpdatePCMMConfiguration_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportPCMMApp.class)
				.analyzeUpdatePCMMConfiguration(model, new PCMMSpecification(), confFile);

		// test
		assertNotNull(analysis);

		// test PCMMPhase
		assertNotNull(analysis.get(PCMMOption.class));
		assertEquals(5, analysis.get(PCMMOption.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(PCMMOption.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(PCMMOption.class).get(ImportActionType.NO_CHANGES).size());

		// test PCMMPlanningParam
		assertNotNull(analysis.get(PCMMPlanningParam.class));
		assertEquals(3, analysis.get(PCMMPlanningParam.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(PCMMPlanningParam.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(PCMMPlanningParam.class).get(ImportActionType.NO_CHANGES).size());

		// test PCMMPlanningQuestion
		assertNotNull(analysis.get(PCMMPlanningQuestion.class));
		assertEquals(39, analysis.get(PCMMPlanningQuestion.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, analysis.get(PCMMPlanningQuestion.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, analysis.get(PCMMPlanningQuestion.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_importPCMMChanges_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// import PCMM Elements before to allow adding questions
		PCMMSpecification newPcmmSpecs = new YmlReaderPCMMSchema().load(confFile);
		getAppManager().getService(IImportPCMMApp.class).importPCMMElements(model, newPcmmSpecs.getElements());
		assertTrue(getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class).findAll().isEmpty());

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportPCMMApp.class)
				.analyzeUpdatePCMMConfiguration(model, new PCMMSpecification(), confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// import
		getAppManager().getService(IImportPCMMApp.class).importPCMMChanges(model, toChange);

		// test PCMMOption
		List<PCMMOption> options = getDaoManager().getRepository(IPCMMOptionRepository.class).findAll();
		assertNotNull(options);
		assertEquals(5, options.size());

		// test PCMMPlanningParam
		List<PCMMPlanningParam> param = getDaoManager().getRepository(IPCMMPlanningParamRepository.class).findAll();
		assertNotNull(param);
		assertEquals(12, param.size());

		// test PCMMPlanningQuestion
		List<PCMMPlanningQuestion> questions = getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class)
				.findAll();
		assertNotNull(questions);
		assertEquals(39, questions.size());

	}

}
