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

import gov.sandia.cf.application.configuration.pirt.PIRTSpecification;
import gov.sandia.cf.application.configuration.pirt.YmlReaderPIRTSchema;
import gov.sandia.cf.dao.IPCMMPlanningParamRepository;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.tests.TestEntityFactory;
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
	void test_getListOfImportableFromAnalysis_working() throws URISyntaxException, IOException, CredibilityException {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// get configuration file
		File confFile = new File(WorkspaceTools.getStaticFilePath("configuration/PIRT_schema-V0.3.yml")); //$NON-NLS-1$
		assertNotNull(confFile);

		// analyze
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getAppManager().getService(IImportPIRTApp.class)
				.analyzeUpdatePIRTConfiguration(model, new PIRTSpecification(), confFile);

		// to change map
		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = getAppManager()
				.getService(IImportApplication.class).getListOfImportableFromAnalysis(analysis);

		// test
		assertNotNull(toChange);

		// test PIRTAdequacyColumn
		assertNotNull(toChange.get(PIRTAdequacyColumn.class));
		assertEquals(5, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumn.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTDescriptionHeader
		assertNotNull(toChange.get(PIRTDescriptionHeader.class));
		assertEquals(5, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTDescriptionHeader.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelDifferenceColor
		assertNotNull(toChange.get(PIRTLevelDifferenceColor.class));
		assertEquals(3, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTLevelDifferenceColor.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTLevelImportance
		assertNotNull(toChange.get(PIRTLevelImportance.class));
		assertEquals(5, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.TO_DELETE).size());
		assertEquals(0, toChange.get(PIRTLevelImportance.class).get(ImportActionType.NO_CHANGES).size());

		// test PIRTAdequacyColumnGuideline
		assertNotNull(toChange.get(PIRTAdequacyColumnGuideline.class));
		assertEquals(5, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_ADD).size());
		assertEquals(0, toChange.get(PIRTAdequacyColumnGuideline.class).get(ImportActionType.TO_DELETE).size());
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

}
