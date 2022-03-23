/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.application.pirt.IImportPIRTApp;
import gov.sandia.cf.application.pirt.YmlReaderPIRTSchema;
import gov.sandia.cf.application.uncertainty.IUncertaintyApplication;
import gov.sandia.cf.dao.IPCMMPlanningParamRepository;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.model.dto.configuration.ParameterLinkGson;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tests.TestGenericParam;
import gov.sandia.cf.tests.TestGenericParamSelectValue;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * JUnit test class for the Import Application Controller
 * 
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class ImportApplicationTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ImportApplicationTest.class);

	@Test
	void test_sameListContent_ok_sameList() throws URISyntaxException, IOException, CredibilityException {

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		PIRTSpecification conf = new YmlReaderPIRTSchema().load(confFile);
		assertNotNull(conf);

		// same lists
		assertTrue(getImportApp().sameListContent(conf.getColors(), conf.getColors()));
		// same lists content
		assertTrue(getImportApp().sameListContent(conf.getColors(), new ArrayList<>(conf.getColors())));
		// compare with null list
		assertFalse(getImportApp().sameListContent(conf.getColors(), null));
		assertFalse(getImportApp().sameListContent(null, conf.getColors()));
		// compare with empty list
		assertFalse(getImportApp().sameListContent(conf.getColors(), new ArrayList<>()));
		assertFalse(getImportApp().sameListContent(new ArrayList<>(), conf.getColors()));
		// compare with different content
		ArrayList<PIRTLevelDifferenceColor> newColors = new ArrayList<>(conf.getColors());
		newColors.add(new PIRTLevelDifferenceColor("145,145,145", Arrays.asList(-1, 9), "Level1", "New desc")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertFalse(getImportApp().sameListContent(conf.getColors(), newColors));
		assertFalse(getImportApp().sameListContent(newColors, conf.getColors()));
	}

	@Test
	void test_getImportableName() {
		// search a class name for each importable class
		for (Class<? extends IImportable> importableClass : getListImportableClass()) {
			String importableName = getImportApp().getImportableName(importableClass);
			assertFalse("A class implementing IImportable interface must define an importable name: " //$NON-NLS-1$
					+ importableClass.getName(), StringUtils.isBlank(importableName));
		}
	}

	Set<Class<? extends IImportable>> getListImportableClass() {
		Reflections reflections = new Reflections("gov.sandia.cf.model"); //$NON-NLS-1$
		return reflections.getSubTypesOf(IImportable.class).stream().filter(e -> !Modifier.isAbstract(e.getModifiers()))
				.collect(Collectors.toSet());
	}

	@Test
	void test_getListOfImportableFromAnalysis_TO_ADD_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportPIRTApp.class)
				.analyzeUpdatePIRTConfiguration(model, new PIRTSpecification(), confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// test PIRTAdequacyColumn
		assertNotNull(toChange.get(PIRTAdequacyColumn.class));
		assertEquals(5, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTDescriptionHeader
		assertNotNull(toChange.get(PIRTDescriptionHeader.class));
		assertEquals(5, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelDifferenceColor
		assertNotNull(toChange.get(PIRTLevelDifferenceColor.class));
		assertEquals(3, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelImportance
		assertNotNull(toChange.get(PIRTLevelImportance.class));
		assertEquals(5, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTAdequacyColumnGuideline
		assertNotNull(toChange.get(PIRTAdequacyColumnGuideline.class));
		assertEquals(5, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_getListOfImportableFromAnalysis_TO_DELETE_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-empty.yml")); //$NON-NLS-1$
		YmlReaderPIRTSchema reader = new YmlReaderPIRTSchema();
		PIRTSpecification specification = reader
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml"))); //$NON-NLS-1$

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportPIRTApp.class)
				.analyzeUpdatePIRTConfiguration(model, specification, confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// test PIRTAdequacyColumn
		assertNotNull(toChange.get(PIRTAdequacyColumn.class));
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_ADD).size());
		assertEquals(5, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTDescriptionHeader
		assertNotNull(toChange.get(PIRTDescriptionHeader.class));
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_ADD).size());
		assertEquals(5, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelDifferenceColor
		assertNotNull(toChange.get(PIRTLevelDifferenceColor.class));
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_ADD).size());
		assertEquals(3, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelImportance
		assertNotNull(toChange.get(PIRTLevelImportance.class));
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_ADD).size());
		assertEquals(5, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTAdequacyColumnGuideline
		assertNotNull(toChange.get(PIRTAdequacyColumnGuideline.class));
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_ADD).size());
		assertEquals(5, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_getListOfImportableFromAnalysis_TO_UPDATE_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		YmlReaderPIRTSchema reader = new YmlReaderPIRTSchema();
		PIRTSpecification specification = reader
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml"))); //$NON-NLS-1$

		// update
		specification.getColumns().forEach(c -> c.setType("New Type")); //$NON-NLS-1$
		specification.getHeaders().forEach(c -> c.setIdLabel("New Id label")); //$NON-NLS-1$
		specification.getColors().forEach(c -> c.setColor("0,0,0")); //$NON-NLS-1$
		specification.getLevels().values().forEach(c -> c.setLabel("New label")); //$NON-NLS-1$
		specification.getPirtAdequacyGuidelines().forEach(c -> c.setDescription("New explanation")); //$NON-NLS-1$

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportPIRTApp.class)
				.analyzeUpdatePIRTConfiguration(model, specification, confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// test PIRTAdequacyColumn
		assertNotNull(toChange.get(PIRTAdequacyColumn.class));
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(5, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTDescriptionHeader
		assertNotNull(toChange.get(PIRTDescriptionHeader.class));
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(5, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelDifferenceColor
		assertNotNull(toChange.get(PIRTLevelDifferenceColor.class));
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(3, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelImportance
		assertNotNull(toChange.get(PIRTLevelImportance.class));
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(5, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTAdequacyColumnGuideline
		assertNotNull(toChange.get(PIRTAdequacyColumnGuideline.class));
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(5, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_getListOfImportableFromAnalysis_NO_CHANGES_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		YmlReaderPIRTSchema reader = new YmlReaderPIRTSchema();
		PIRTSpecification specification = reader
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml"))); //$NON-NLS-1$

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportPIRTApp.class)
				.analyzeUpdatePIRTConfiguration(model, specification, confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// test PIRTAdequacyColumn
		assertNotNull(toChange.get(PIRTAdequacyColumn.class));
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(5, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTDescriptionHeader
		assertNotNull(toChange.get(PIRTDescriptionHeader.class));
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(5, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelDifferenceColor
		assertNotNull(toChange.get(PIRTLevelDifferenceColor.class));
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(3, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelImportance
		assertNotNull(toChange.get(PIRTLevelImportance.class));
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(5, toChange.get(PIRTLevelImportance.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTAdequacyColumnGuideline
		assertNotNull(toChange.get(PIRTAdequacyColumnGuideline.class));
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(5, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_getListOfImportableFromAnalysis_TO_ADD_DELETE_UPDATE_working()
			throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		YmlReaderPIRTSchema reader = new YmlReaderPIRTSchema();
		PIRTSpecification specification = reader
				.load(new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml"))); //$NON-NLS-1$

		// update specification to have something TO ADD, To Update and TO DELETE
		specification.getColumns().get(0).setName("New Key"); //$NON-NLS-1$
		specification.getColumns().forEach(c -> c.setType("New Type")); //$NON-NLS-1$
		specification.getHeaders().get(0).setName("New Key"); //$NON-NLS-1$
		specification.getHeaders().forEach(c -> c.setIdLabel("New Id label")); //$NON-NLS-1$
		specification.getColors().get(0).setDescription("New Key"); //$NON-NLS-1$
		specification.getColors().forEach(c -> c.setColor("0,0,0")); //$NON-NLS-1$
		specification.getLevels().values().forEach(c -> c.setLabel("New label")); //$NON-NLS-1$
		specification.getPirtAdequacyGuidelines().get(0).setName("New Key"); //$NON-NLS-1$
		specification.getPirtAdequacyGuidelines().forEach(c -> c.setDescription("New explanation")); //$NON-NLS-1$

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportPIRTApp.class)
				.analyzeUpdatePIRTConfiguration(model, specification, confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// test PIRTAdequacyColumn
		assertNotNull(toChange.get(PIRTAdequacyColumn.class));
		assertEquals(1, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_ADD).size());
		assertEquals(1, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(4, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTDescriptionHeader
		assertNotNull(toChange.get(PIRTDescriptionHeader.class));
		assertEquals(1, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_ADD).size());
		assertEquals(1, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(4, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelDifferenceColor
		assertNotNull(toChange.get(PIRTLevelDifferenceColor.class));
		assertEquals(1, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_ADD).size());
		assertEquals(1, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(2, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelImportance
		assertNotNull(toChange.get(PIRTLevelImportance.class));
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(5, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTAdequacyColumnGuideline
		assertNotNull(toChange.get(PIRTAdequacyColumnGuideline.class));
		assertEquals(1, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_ADD).size());
		assertEquals(1, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(4, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_UPDATE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.NO_CHANGES).size());

	}

	@Test
	void test_doNotLinkGenericParameterWithDifferentTypes()
			throws URISyntaxException, IOException, CredibilityException {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		PCMMPlanningParam newPCMMPlanningParam = TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), newModel);
		TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), newModel, newPCMMPlanningParam);
		TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), newModel, newPCMMPlanningParam);
		TestEntityFactory.getNewPCMMPlanningParam(getDaoManager(), newModel, newPCMMPlanningParam);

		UncertaintyParam newUncertaintyParam = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel,
				null);
		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);

		getDaoManager().getRepository(IPCMMPlanningParamRepository.class).refresh(newPCMMPlanningParam);
		getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(newUncertaintyParam);

		assertEquals(newPCMMPlanningParam.getId(), newUncertaintyParam.getId());
		assertEquals(3, newPCMMPlanningParam.getChildren().size());
		assertEquals(0, newUncertaintyParam.getChildren().size());

		try {
			getAppManager().getService(IUncertaintyApplication.class)
					.deleteAllUncertaintyParam(Arrays.asList(newUncertaintyParam));
		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

	////////////////////////////////// getDatabaseValueForGeneric

	@Test
	void test_getDatabaseValueForGeneric_Text() {

		String stringValue = "Text value";//$NON-NLS-1$

		TestGenericParam newParameter = TestEntityFactory.getNewTestGenericParameter();
		newParameter.setType(FormFieldType.TEXT.getType());
		GenericValue<?, ?> newValue = TestEntityFactory.getNewAnonymousGenericValue();
		newValue.setValue(stringValue);

		// Test
		String databaseValueForGeneric = getAppManager().getService(IImportApplication.class)
				.getDatabaseValueForGeneric(newParameter, newValue);
		assertEquals(stringValue, databaseValueForGeneric);
	}

	@Test
	void test_getDatabaseValueForGeneric_RichText() {

		String stringValue = "Text value";//$NON-NLS-1$

		TestGenericParam newParameter = TestEntityFactory.getNewTestGenericParameter();
		newParameter.setType(FormFieldType.RICH_TEXT.getType());
		GenericValue<?, ?> newValue = TestEntityFactory.getNewAnonymousGenericValue();
		newValue.setValue(stringValue);

		// Test
		String databaseValueForGeneric = getAppManager().getService(IImportApplication.class)
				.getDatabaseValueForGeneric(newParameter, newValue);
		assertEquals(stringValue, databaseValueForGeneric);
	}

	@Test
	void test_getDatabaseValueForGeneric_Date() {

		TestGenericParam newParameter = TestEntityFactory.getNewTestGenericParameter();
		newParameter.setType(FormFieldType.DATE.getType());
		GenericValue<?, ?> newValue = TestEntityFactory.getNewAnonymousGenericValue();
		newValue.setValue("2022-01-01"); //$NON-NLS-1$

		// Test
		String databaseValueForGeneric = getAppManager().getService(IImportApplication.class)
				.getDatabaseValueForGeneric(newParameter, newValue);
		assertEquals("2022-01-01", databaseValueForGeneric); //$NON-NLS-1$
	}

	@Test
	void test_getDatabaseValueForGeneric_Float() {

		TestGenericParam newParameter = TestEntityFactory.getNewTestGenericParameter();
		newParameter.setType(FormFieldType.FLOAT.getType());
		GenericValue<?, ?> newValue = TestEntityFactory.getNewAnonymousGenericValue();
		newValue.setValue("1.0"); //$NON-NLS-1$

		// Test
		String databaseValueForGeneric = getAppManager().getService(IImportApplication.class)
				.getDatabaseValueForGeneric(newParameter, newValue);
		assertEquals("1.0", databaseValueForGeneric); //$NON-NLS-1$
	}

	@Test
	void test_getDatabaseValueForGeneric_Link_Url() {

		TestGenericParam newParameter = TestEntityFactory.getNewTestGenericParameter();
		newParameter.setType(FormFieldType.LINK.getType());
		GenericValue<?, ?> newValue = TestEntityFactory.getNewAnonymousGenericValue();
		newValue.setValue("http://myurltest.fr"); //$NON-NLS-1$

		// Test
		String databaseValueForGeneric = getAppManager().getService(IImportApplication.class)
				.getDatabaseValueForGeneric(newParameter, newValue);
		assertEquals(ParameterLinkGson.toGson(FormFieldType.LINK_URL, "http://myurltest.fr"), databaseValueForGeneric); //$NON-NLS-1$
	}

	@Test
	void test_getDatabaseValueForGeneric_Link_File() {

		TestGenericParam newParameter = TestEntityFactory.getNewTestGenericParameter();
		newParameter.setType(FormFieldType.LINK.getType());
		GenericValue<?, ?> newValue = TestEntityFactory.getNewAnonymousGenericValue();
		newValue.setValue("../my/path/to/value.txt"); //$NON-NLS-1$

		// Test
		String databaseValueForGeneric = getAppManager().getService(IImportApplication.class)
				.getDatabaseValueForGeneric(newParameter, newValue);
		assertEquals(ParameterLinkGson.toGson(FormFieldType.LINK_FILE, "../my/path/to/value.txt"), //$NON-NLS-1$
				databaseValueForGeneric);
	}

	@Test
	void test_getDatabaseValueForGeneric_Select() {

		TestGenericParam newParameter = TestEntityFactory.getNewTestGenericParameter();
		newParameter.setType(FormFieldType.SELECT.getType());
		TestGenericParamSelectValue selectValue = TestEntityFactory.getNewTestGenericParamSelectValue("my select value", //$NON-NLS-1$
				newParameter);
		selectValue.setId(1);
		TestGenericParamSelectValue selectValue2 = TestEntityFactory
				.getNewTestGenericParamSelectValue("my select value 2", newParameter); //$NON-NLS-1$
		selectValue2.setId(2);
		newParameter.setParameterValueList(Arrays.asList(selectValue, selectValue2));
		GenericValue<?, ?> newValue = TestEntityFactory.getNewAnonymousGenericValue();
		newValue.setValue("my select value"); //$NON-NLS-1$

		// Test
		String databaseValueForGeneric = getAppManager().getService(IImportApplication.class)
				.getDatabaseValueForGeneric(newParameter, newValue);
		assertEquals("1", databaseValueForGeneric); //$NON-NLS-1$
	}

	@Test
	void test_getDatabaseValueForGeneric_CredibilityElement() {

		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), null);

		TestGenericParam newParameter = TestEntityFactory.getNewTestGenericParameter();
		newParameter.setType(FormFieldType.CREDIBILITY_ELEMENT.getType());
		GenericValue<?, ?> newValue = TestEntityFactory.getNewAnonymousGenericValue();
		newValue.setValue(newPCMMElement.getAbbreviation());

		// Test
		String databaseValueForGeneric = getAppManager().getService(IImportApplication.class)
				.getDatabaseValueForGeneric(newParameter, newValue);
		assertEquals(newPCMMElement.getId().toString(), databaseValueForGeneric);
	}

	@Test
	void test_getDatabaseValueForGeneric_SystemRequirement() {

		SystemRequirement newSystemRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), null, null,
				null);

		TestGenericParam newParameter = TestEntityFactory.getNewTestGenericParameter();
		newParameter.setType(FormFieldType.SYSTEM_REQUIREMENT.getType());
		GenericValue<?, ?> newValue = TestEntityFactory.getNewAnonymousGenericValue();
		newValue.setValue(newSystemRequirement.getStatement());

		// Test
		String databaseValueForGeneric = getAppManager().getService(IImportApplication.class)
				.getDatabaseValueForGeneric(newParameter, newValue);
		assertEquals(newSystemRequirement.getId().toString(), databaseValueForGeneric);

	}
}
