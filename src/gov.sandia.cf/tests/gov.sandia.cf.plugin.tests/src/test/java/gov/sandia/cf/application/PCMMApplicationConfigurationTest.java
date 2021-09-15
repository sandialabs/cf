/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.application.configuration.pcmm.YmlReaderPCMMSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * JUnit test class for the PCMM Application Configuration Controller
 * 
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class PCMMApplicationConfigurationTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMApplicationConfigurationTest.class);

	@Test
	void test_sameConfiguration_spec1Null_spec2New() {
		PCMMSpecification spec1 = null;
		PCMMSpecification spec2 = new PCMMSpecification();

		assertFalse(getAppManager().getService(IPCMMApplication.class).sameConfiguration(spec1, spec2));
	}

	@Test
	void test_sameConfiguration_spec1New_spec2Null() {
		PCMMSpecification spec1 = new PCMMSpecification();
		PCMMSpecification spec2 = null;

		assertFalse(getAppManager().getService(IPCMMApplication.class).sameConfiguration(spec1, spec2));
	}

	@Test
	void test_sameConfiguration_spec1Null_spec2Null() {
		PCMMSpecification spec1 = null;
		PCMMSpecification spec2 = null;

		assertTrue(getAppManager().getService(IPCMMApplication.class).sameConfiguration(spec1, spec2));
	}

	@Test
	void test_sameConfiguration_spec1New_spec2New() {
		PCMMSpecification spec1 = new PCMMSpecification();
		PCMMSpecification spec2 = new PCMMSpecification();

		assertTrue(getAppManager().getService(IPCMMApplication.class).sameConfiguration(spec1, spec2));
	}

	@Test
	void test_sameConfiguration_sameConfFile() throws URISyntaxException, IOException, CredibilityException {

		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		PCMMSpecification conf1 = new YmlReaderPCMMSchema().load(confFile);
		PCMMSpecification conf2 = new YmlReaderPCMMSchema().load(confFile);

		assertTrue(getPCMMApp().sameConfiguration(conf1, conf2));
	}

	@Test
	void test_sameConfiguration_differentPCMMElements() throws URISyntaxException, IOException, CredibilityException {

		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		PCMMSpecification conf1 = new YmlReaderPCMMSchema().load(confFile);
		PCMMSpecification conf2 = new YmlReaderPCMMSchema().load(confFile);
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);
		conf2.setElements(Arrays.asList(newPCMMElement));

		assertFalse(getPCMMApp().sameConfiguration(conf1, conf2));
	}

	@Test
	void test_sameConfiguration_differentPCMMSubelements()
			throws URISyntaxException, IOException, CredibilityException {

		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		PCMMSpecification conf1 = new YmlReaderPCMMSchema().load(confFile);
		PCMMSpecification conf2 = new YmlReaderPCMMSchema().load(confFile);
		PCMMSubelement newPCMMSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), null);
		conf2.getElements().iterator().next().setSubElementList(Arrays.asList(newPCMMSubelement));

		assertFalse(getPCMMApp().sameConfiguration(conf1, conf2));
	}

	@Test
	void test_sameConfiguration_differentPCMMLevels() throws URISyntaxException, IOException, CredibilityException {

		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		PCMMSpecification conf1 = new YmlReaderPCMMSchema().load(confFile);
		PCMMSpecification conf2 = new YmlReaderPCMMSchema().load(confFile);
		PCMMSubelement sub = conf2.getElements().iterator().next().getSubElementList().iterator().next();
		PCMMLevel newLevel = TestEntityFactory.getNewPCMMLevel(getDaoManager(), null, null);
		sub.setLevelList(Arrays.asList(newLevel));

		assertFalse(getPCMMApp().sameConfiguration(conf1, conf2));
	}

	@Test
	void test_sameConfiguration_differentPCMMOptions() throws URISyntaxException, IOException, CredibilityException {

		PCMMSpecification conf1 = new PCMMSpecification();
		PCMMSpecification conf2 = new PCMMSpecification();
		PCMMOption aggreg = TestEntityFactory.getNewPCMMOption(getDaoManager(), PCMMPhase.AGGREGATE);
		PCMMOption planning = TestEntityFactory.getNewPCMMOption(getDaoManager(), PCMMPhase.PLANNING);
		conf1.setOptions(Arrays.asList(aggreg));
		conf2.setOptions(Arrays.asList(planning, new PCMMOption()));

		assertFalse(getPCMMApp().sameConfiguration(conf1, conf2));
	}

	@Test
	void test_sameConfiguration_differentRoles() throws URISyntaxException, IOException, CredibilityException {

		PCMMSpecification conf1 = new PCMMSpecification();
		PCMMSpecification conf2 = new PCMMSpecification();
		Role role1 = TestEntityFactory.getNewRole(getDaoManager());
		Role role2 = TestEntityFactory.getNewRole(getDaoManager());
		Role role3 = TestEntityFactory.getNewRole(getDaoManager());
		role3.setName("NEW ROLE"); //$NON-NLS-1$
		conf1.setRoles(Arrays.asList(role1));
		conf2.setRoles(Arrays.asList(role2, role3));

		assertFalse(getPCMMApp().sameConfiguration(conf1, conf2));
	}

	@Test
	void test_sameConfiguration_differentLevelColors() throws URISyntaxException, IOException, CredibilityException {

		PCMMSpecification conf1 = new PCMMSpecification();
		PCMMSpecification conf2 = new PCMMSpecification();
		PCMMLevelColor level1 = TestEntityFactory.getNewPCMMLevelColor(getDaoManager());
		PCMMLevelColor level2 = TestEntityFactory.getNewPCMMLevelColor(getDaoManager());
		PCMMLevelColor level3 = TestEntityFactory.getNewPCMMLevelColor(getDaoManager());
		level3.setName("LEVEL 3"); //$NON-NLS-1$
		HashMap<Integer, PCMMLevelColor> map1 = new HashMap<Integer, PCMMLevelColor>();
		map1.put(1, level1);
		map1.put(2, level2);
		HashMap<Integer, PCMMLevelColor> map2 = new HashMap<Integer, PCMMLevelColor>();
		map2.put(1, level1);
		map2.put(2, level3);
		conf1.setLevelColors(map1);
		conf2.setLevelColors(map2);

		assertFalse(getPCMMApp().sameConfiguration(conf1, conf2));
	}

	@Test
	void test_sameConfiguration_differentPCMMPlanningFields()
			throws URISyntaxException, IOException, CredibilityException {

		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		PCMMSpecification conf1 = new YmlReaderPCMMSchema().load(confFile);
		PCMMSpecification conf2 = new YmlReaderPCMMSchema().load(confFile);
		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), null);
		conf1.setPlanningFields(Arrays.asList(newPCMMPlanningParam));

		assertFalse(getPCMMApp().sameConfiguration(conf1, conf2));
	}

	@Test
	void test_sameConfiguration_differentPCMMPlanningQuestions()
			throws URISyntaxException, IOException, CredibilityException {

		File confFile = new File(WorkspaceTools
				.getStaticFilePath("configuration/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.yml")); //$NON-NLS-1$
		PCMMSpecification conf1 = new YmlReaderPCMMSchema().load(confFile);
		PCMMSpecification conf2 = new YmlReaderPCMMSchema().load(confFile);
		PCMMPlanningQuestion newPCMMPlanningQuestion = TestEntityFactory.getNewPCMMPlanningQuestion(getDaoManager(),
				null);
		Map<IAssessable, List<PCMMPlanningQuestion>> mapQuestions = new HashMap<>();
		mapQuestions.put(newPCMMPlanningQuestion.getElement(), Arrays.asList(newPCMMPlanningQuestion));
		conf1.setPlanningQuestions(mapQuestions);

		assertFalse(getPCMMApp().sameConfiguration(conf1, conf2));
	}
}
