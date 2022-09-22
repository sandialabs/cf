/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.report.ARGParametersFactory;
import gov.sandia.cf.application.report.IReportARGApplication;
import gov.sandia.cf.application.report.IReportARGExecutionApp;
import gov.sandia.cf.application.report.ReportARGApplication;
import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.constants.arg.ARGBackendDefault;
import gov.sandia.cf.constants.arg.ARGOrientation;
import gov.sandia.cf.constants.arg.ARGReportTypeDefault;
import gov.sandia.cf.constants.arg.ARGVersion;
import gov.sandia.cf.constants.arg.YmlARGParameterSchema;
import gov.sandia.cf.constants.arg.YmlARGStructure;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.dao.ISystemRequirementValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.parts.widgets.PCMMElementSelectorWidget;
import gov.sandia.cf.tests.TestDtoFactory;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.CFVariableResolver;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 *
 * JUnit test class for the ARG Report Application Controller
 * 
 * @author Didier Verstraete
 */
class ReportARGApplicationTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ReportARGApplicationTest.class);

	@Test
	void test_copyReportStructureContentIntoFile_fileNull() {
		CredibilityException exception = assertThrows(CredibilityException.class, () -> {
			// copy content into structure file
			getAppManager().getService(IReportARGApplication.class).copyReportStructureContentIntoFile(null, null);
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFREPORT_YAML_STRUCTURE_FILE_NOTEXISTS), exception.getMessage());
	}

	@Test
	void test_copyReportStructureContentIntoFile_fileNotExists() {
		CredibilityException exception = assertThrows(CredibilityException.class, () -> {
			// copy content into structure file
			getAppManager().getService(IReportARGApplication.class)
					.copyReportStructureContentIntoFile(new File("not_existing_structure_file"), null); //$NON-NLS-1$
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFREPORT_YAML_STRUCTURE_FILE_NOTEXISTS), exception.getMessage());
	}

	@Test
	void test_copyReportStructureContentIntoFile_structureNull() throws IOException, CredibilityException {
		// Initialize
		File testTempFolder = getTestTempFolder();
		String structFilename = "structure.yml"; //$NON-NLS-1$
		Path path = Paths.get(testTempFolder.getPath(), structFilename);

		// create structure file
		Path structureFile = null;
		structureFile = Files.createFile(path);
		assertNotNull(structureFile);
		assertTrue(structureFile.toFile().exists());

		// copy content into structure file
		getAppManager().getService(IReportARGApplication.class)
				.copyReportStructureContentIntoFile(structureFile.toFile(), null);

		// assertions
		Map<?, ?> ymlContent = null;
		try (FileReader fileReader = new FileReader(structureFile.toFile())) {

			// load content
			ymlContent = new Yaml().load(fileReader);

		}
		assertNotNull(ymlContent);
	}

	@Test
	void test_copyReportStructureContentIntoFile_WorkingEmpty() throws IOException, CredibilityException {
		// Initialize
		File testTempFolder = getTestTempFolder();
		String structFilename = "structure.yml"; //$NON-NLS-1$
		Path path = Paths.get(testTempFolder.getPath(), structFilename);

		// create structure file
		Path structureFile = Files.createFile(path);
		assertNotNull(structureFile);
		assertTrue(structureFile.toFile().exists());

		// generate content
		Map<String, Object> content = getAppManager().getService(IReportARGApplication.class)
				.generateStructure(new HashMap<>());

		// copy content into structure file
		getAppManager().getService(IReportARGApplication.class)
				.copyReportStructureContentIntoFile(structureFile.toFile(), content);

		// assertions
		Map<?, ?> ymlContent = null;
		try (FileReader fileReader = new FileReader(structureFile.toFile())) {

			// load content
			ymlContent = new Yaml().load(fileReader);

		}
		assertNotNull(ymlContent);
		assertTrue(ymlContent.containsKey(YmlARGStructure.ARG_STRUCTURE_VERSION_KEY));
		assertEquals(ARGVersion.ARG_VERSION, ymlContent.get(YmlARGStructure.ARG_STRUCTURE_VERSION_KEY));
		assertTrue(ymlContent.containsKey(YmlARGStructure.ARG_STRUCTURE_CHAPTERS_KEY));
		assertTrue(((List<?>) ymlContent.get(YmlARGStructure.ARG_STRUCTURE_CHAPTERS_KEY)).isEmpty());
	}

	@Test
	void test_copyReportStructureContentIntoFile_working_full_options()
			throws IOException, CredibilityException, URISyntaxException {
		// Initialize
		File testTempFolder = getTestTempFolder();
		String structFilename = "structure.yml"; //$NON-NLS-1$
		Path path = Paths.get(testTempFolder.getPath(), structFilename);

		// create structure file
		Path structureFile = Files.createFile(path);
		assertNotNull(structureFile);
		assertTrue(structureFile.toFile().exists());

		// Create options
		Map<ExportOptions, Object> options = new HashMap<>();
		options.put(ExportOptions.PCMM_INCLUDE, true);
		options.put(ExportOptions.PIRT_INCLUDE, true);
		options.put(ExportOptions.PLANNING_INCLUDE, true);
		options.put(ExportOptions.CUSTOM_ENDING_INCLUDE, true);
		options.put(ExportOptions.PCMM_TAG, null);
		options.put(ExportOptions.MODEL, TestEntityFactory.getNewModel(getDaoManager()));
		options.put(ExportOptions.PIRT_QOI_LIST, new HashMap<QuantityOfInterest, Map<ExportOptions, Object>>());
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(null);
		argParameters.setCustomEndingEnabled(true);
		File toAppendFile = new File(WorkspaceTools.getStaticFilePath("report/arg/structureToAppend.yml")); //$NON-NLS-1$
		argParameters.setCustomEndingFilePath(toAppendFile.getPath());
		argParameters.setInlineWordDoc(true);
		argParameters.setBackendType(ARGBackendDefault.WORD.getBackend());
		options.put(ExportOptions.ARG_PARAMETERS, argParameters);

		// Generate content
		Map<String, Object> content = getAppManager().getService(IReportARGApplication.class)
				.generateStructure(options);

		// copy content into structure file
		getAppManager().getService(IReportARGApplication.class)
				.copyReportStructureContentIntoFile(structureFile.toFile(), content);

		// assertions
		Map<?, ?> ymlContent = null;
		try (FileReader fileReader = new FileReader(structureFile.toFile())) {

			// load content
			ymlContent = new Yaml().load(fileReader);

		}
		assertNotNull(ymlContent);
		assertTrue(ymlContent.containsKey(YmlARGStructure.ARG_STRUCTURE_VERSION_KEY));
		assertEquals(ARGVersion.ARG_VERSION, ymlContent.get(YmlARGStructure.ARG_STRUCTURE_VERSION_KEY));
		assertTrue(ymlContent.containsKey(YmlARGStructure.ARG_STRUCTURE_CHAPTERS_KEY));
		List<?> list = (List<?>) ymlContent.get(YmlARGStructure.ARG_STRUCTURE_CHAPTERS_KEY);
		assertFalse(list.isEmpty());
		assertTrue(
				list.stream().map(Map.class::cast).anyMatch(e -> e.containsKey(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY)
						&& e.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY).equals("Conclusion")));//$NON-NLS-1$
	}

	@Test
	void test_generateStructure_Null() throws IOException, CredibilityException {
		// Initialize
		Map<String, Object> ymlContent = getAppManager().getService(IReportARGApplication.class)
				.generateStructure(null);

		// Tests
		assertNotNull(ymlContent);
	}

	@Test
	void test_createReportStructureFile_ARGParamNull() throws IOException, CredibilityException {

		// test
		File reportStructureFile = getAppManager().getService(IReportARGApplication.class)
				.createReportStructureFile(null);

		// validate
		assertNull(reportStructureFile);
	}

	@Test
	void test_createReportStructureFile_WorkingFileInWorkspaceNotExists()
			throws IOException, CredibilityException, CoreException {

		// Initialize
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		IFile structureFileToCreate = newCFFile.getProject().getFile("structureFileToCreate.yml"); //$NON-NLS-1$
		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());
		argParam.setOutput(getTestTempFolder().getPath());
		argParam.setStructureFilePath(CFVariable.WORKSPACE.get() + structureFileToCreate.getFullPath().toString());

		// test
		File reportStructureFile = getAppManager().getService(IReportARGApplication.class)
				.createReportStructureFile(argParam);

		// validate
		assertNotNull(reportStructureFile);
		assertTrue(reportStructureFile.exists());
		assertEquals(structureFileToCreate.getName(), reportStructureFile.getName());

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_createReportStructureFile_WorkingFileInWorkspace()
			throws IOException, CredibilityException, CoreException {

		// Initialize
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		IFile structFile = TestEntityFactory.getNewFile("MyProject", "structure.yml"); //$NON-NLS-1$ //$NON-NLS-2$

		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());

		argParam.setOutput(getTestTempFolder().getPath());
		argParam.setStructureFilePath(CFVariable.WORKSPACE.get() + structFile.getFullPath().toString());

		// test
		File reportStructureFile = null;
		reportStructureFile = getAppManager().getService(IReportARGApplication.class)
				.createReportStructureFile(argParam);

		// validate
		assertNotNull(reportStructureFile);
		assertTrue(reportStructureFile.exists());

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_createReportStructureFile_WorkingFileOnFileSystem()
			throws IOException, CredibilityException, CoreException {

		// Initialize
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		File createTempFile = null;
		createTempFile = File.createTempFile("struct", ".yml", getTestTempFolder()); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(createTempFile);

		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());

		argParam.setOutput(getTestTempFolder().getPath());
		argParam.setStructureFilePath(createTempFile.getPath());

		// test
		File reportStructureFile = getAppManager().getService(IReportARGApplication.class)
				.createReportStructureFile(argParam);

		// validate
		assertNotNull(reportStructureFile);
		assertTrue(reportStructureFile.exists());

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_generateGenericValues_Working() throws CredibilityException {
		// Initializes
		List<Map<String, Object>> parentSections = new ArrayList<>();
		List<IGenericTableValue> values = new ArrayList<>();
		ARGParameters argParameters = new ARGParameters();

		// Data
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());
		SystemRequirement sysRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), model, null,
				user);

		// Test data
		assertNotNull(model);
		assertNotNull(user);
		assertNotNull(sysRequirement);

		// Values
		// HYPERLINK - complete URL
		SystemRequirementParam parameterLink = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				null);
		parameterLink.setType(FormFieldType.LINK.getType());
		SystemRequirementValue valueLink = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(),
				sysRequirement, parameterLink, user);
		valueLink.setValue(TestDtoFactory.getParameterLinkGson(FormFieldType.LINK_URL, "http://example.com")); //$NON-NLS-1$
		valueLink = getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(valueLink);
		values.add(valueLink);

		// HYPERLINK - complete FILE
		SystemRequirementParam parameterFile = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				null);
		parameterFile.setType(FormFieldType.LINK.getType());
		SystemRequirementValue valueFile = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(),
				sysRequirement, parameterFile, user);
		valueFile.setValue(TestDtoFactory.getParameterLinkGson(FormFieldType.LINK_FILE, "/home/test.pdf")); //$NON-NLS-1$
		valueFile = getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(valueFile);
		values.add(valueFile);

		// IMAGE - image FILE
		SystemRequirementParam parameterFileImg = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				null);
		parameterFileImg.setType(FormFieldType.LINK.getType());
		SystemRequirementValue valueFileImg = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(),
				sysRequirement, parameterFileImg, user);
		valueFileImg.setValue(
				TestDtoFactory.getParameterLinkGson(FormFieldType.LINK_FILE, "/home/test.jpg", "With caption")); //$NON-NLS-1$ //$NON-NLS-2$
		valueFileImg = getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(valueFile);
		values.add(valueFileImg);

		// HYPERLINK - Null
		SystemRequirementValue valueLink2 = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), null, null,
				null);
		values.add(valueLink2);

		// HYPERLINK - Blank
		SystemRequirementValue valueLinkBlank = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(),
				sysRequirement, parameterLink, user);
		valueLinkBlank.setValue(""); //$NON-NLS-1$
		valueLinkBlank = getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(valueLinkBlank);
		values.add(valueLinkBlank);

		// HYPERLINK - Value Blank
		SystemRequirementValue valueLinkBlankValue = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(),
				sysRequirement, parameterLink, user);
		valueLinkBlankValue.setValue(TestDtoFactory.getParameterLinkGson(FormFieldType.LINK_URL, "")); //$NON-NLS-1$
		valueLinkBlankValue = getDaoManager().getRepository(ISystemRequirementValueRepository.class)
				.update(valueLinkBlankValue);
		values.add(valueLinkBlankValue);

		// Credibility ELEMENT - Complete
		PCMMElement elt = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		SystemRequirementParam parameterElt = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				null);
		parameterElt.setType(FormFieldType.CREDIBILITY_ELEMENT.getType());
		SystemRequirementValue valueElt = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(),
				sysRequirement, parameterElt, user);
		valueElt.setValue(elt.getId().toString());
		values.add(valueElt);

		// Credibility ELEMENT - Not applicable
		PCMMElement eltNotApplicable = new PCMMElement();
		eltNotApplicable.setId(PCMMElementSelectorWidget.NOT_APPLICABLE_ID);
		SystemRequirementParam parameterEltNotApplicable = TestEntityFactory
				.getNewSystemRequirementParam(getDaoManager(), model, null);
		parameterEltNotApplicable.setType(FormFieldType.CREDIBILITY_ELEMENT.getType());
		SystemRequirementValue valueEltNotApplicable = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(),
				sysRequirement, parameterEltNotApplicable, user);
		valueEltNotApplicable.setValue(eltNotApplicable.getId().toString());
		values.add(valueEltNotApplicable);

		// ELEMENT - Null
		SystemRequirementValue valueElt2 = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), null, null,
				null);
		values.add(valueElt2);

		// SYSTEM REQUIREMENT - Complete
		SystemRequirementParam parameterReq = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				null);
		parameterReq.setType(FormFieldType.SYSTEM_REQUIREMENT.getType());
		SystemRequirementValue valueRequirement = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(),
				sysRequirement, parameterReq, user);
		valueRequirement.setValue(sysRequirement.getId().toString());
		values.add(valueRequirement);

		// SYSTEM REQUIREMENT - Null
		SystemRequirementValue valueRequirement2 = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), null,
				null, null);
		values.add(valueRequirement2);

		// TEXT - Complete
		SystemRequirementParam parameterText = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model,
				null);
		parameterText.setType(FormFieldType.TEXT.getType());
		SystemRequirementValue valueText = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(),
				sysRequirement, parameterText, user);
		valueText.setValue("TEXT"); //$NON-NLS-1$
		values.add(valueText);

		// TEXT - Null
		SystemRequirementValue valueText2 = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), null, null,
				null);
		values.add(valueText2);

		// Test
		getAppManager().getService(IReportARGApplication.class).generateGenericValues(parentSections, sysRequirement,
				values, argParameters);
		assertEquals(4, parentSections.size()); // blank hyperlink should not be displayed
	}

	@Test
	void test_generateGenericValues_Working_URL() throws CredibilityException {

		// Initialize
		List<Map<String, Object>> parentSections = new ArrayList<>();
		List<IGenericTableValue> values = new ArrayList<>();
		ARGParameters argParameters = new ARGParameters();

		// Data
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());
		SystemRequirement sysRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), model, null,
				user);
		// HYPERLINK - complete URL
		SystemRequirementParam parameter = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model, null);
		parameter.setType(FormFieldType.LINK.getType());

		SystemRequirementValue value = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), sysRequirement,
				parameter, user);
		value.setValue(TestDtoFactory.getParameterLinkGson(FormFieldType.LINK_URL, "http://example.com")); //$NON-NLS-1$
		value = getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(value);
		values.add(value);

		// Test
		getAppManager().getService(IReportARGApplication.class).generateGenericValues(parentSections, sysRequirement,
				values, argParameters);

		// Validate
		assertEquals(1, parentSections.size());
		Map<String, Object> map = parentSections.get(0);
		assertNotNull(map);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(parameter.getName() + RscTools.COLON, map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals("http://example.com", map.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_PATH_KEY)); //$NON-NLS-1$
		assertEquals("http://example.com", map.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_STRING_KEY)); //$NON-NLS-1$
	}

	@Test
	void test_generateGenericValues_Working_FileNotFound() throws CredibilityException {

		// Initialize
		List<Map<String, Object>> parentSections = new ArrayList<>();
		List<IGenericTableValue> values = new ArrayList<>();
		ARGParameters argParameters = new ARGParameters();

		// Data
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());
		SystemRequirement sysRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), model, null,
				user);
		// HYPERLINK - complete URL
		SystemRequirementParam parameter = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model, null);
		parameter.setType(FormFieldType.LINK.getType());

		SystemRequirementValue value = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), sysRequirement,
				parameter, user);
		value.setValue(TestDtoFactory.getParameterLinkGson(FormFieldType.LINK_FILE, "/home/test.pdf")); //$NON-NLS-1$
		value = getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(value);
		values.add(value);

		// Test
		getAppManager().getService(IReportARGApplication.class).generateGenericValues(parentSections, sysRequirement,
				values, argParameters);

		// Validate
		assertEquals(1, parentSections.size());
		Map<String, Object> map = parentSections.get(0);
		assertNotNull(map);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(parameter.getName() + RscTools.COLON, map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));// $NON-NLS-1$
		assertEquals("/home/test.pdf", //$NON-NLS-1$
				map.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_PATH_KEY));
		assertEquals("/home/test.pdf", //$NON-NLS-1$
				map.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_STRING_KEY));
	}

	@Test
	void test_generateGenericValues_Working_FileFound() throws CredibilityException, CoreException {

		// Initialize
		List<Map<String, Object>> parentSections = new ArrayList<>();
		List<IGenericTableValue> values = new ArrayList<>();
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());
		IFile file = newCFFile.getProject().getFile("Test.tmp"); //$NON-NLS-1$
		file.create(null, true, new NullProgressMonitor());

		// Data
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());
		SystemRequirement sysRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), model, null,
				user);
		SystemRequirementParam parameter = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model, null);
		parameter.setType(FormFieldType.LINK.getType());

		SystemRequirementValue value = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), sysRequirement,
				parameter, user);
		value.setValue(TestDtoFactory.getParameterLinkGson(FormFieldType.LINK_FILE, file.getFullPath().toString()));
		value = getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(value);
		values.add(value);

		// Test
		getAppManager().getService(IReportARGApplication.class).generateGenericValues(parentSections, sysRequirement,
				values, argParam);

		// Validate
		assertEquals(1, parentSections.size());
		Map<String, Object> map = parentSections.get(0);
		assertNotNull(map);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(parameter.getName() + RscTools.COLON, map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(file.getName(), map.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_PATH_KEY)); // $NON-NLS-1$
		assertEquals(file.getName(), map.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_STRING_KEY)); // $NON-NLS-1$

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_generateGenericValues_Working_File_Image()
			throws CredibilityException, CoreException, URISyntaxException, IOException {

		// Initialize
		List<Map<String, Object>> parentSections = new ArrayList<>();
		List<IGenericTableValue> values = new ArrayList<>();
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());
		File imageFile = new File(WorkspaceTools.getStaticFilePath("report/arg/image.png")); //$NON-NLS-1$
		String imagePathRelativeToWks = FileTools.getNormalizedPath(
				new File(WorkspaceTools.getWorkspacePathToString()).toPath().relativize(imageFile.toPath()));

		// Data
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());
		SystemRequirement sysRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), model, null,
				user);
		SystemRequirementParam parameter = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model, null);
		parameter.setType(FormFieldType.LINK.getType());

		SystemRequirementValue value = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), sysRequirement,
				parameter, user);
		value.setValue(TestDtoFactory.getParameterLinkGson(FormFieldType.LINK_FILE, imagePathRelativeToWks, "Caption")); //$NON-NLS-1$
		value = getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(value);
		values.add(value);

		// Test
		getAppManager().getService(IReportARGApplication.class).generateGenericValues(parentSections, sysRequirement,
				values, argParam);

		// Validate
		assertEquals(2, parentSections.size());
		Map<String, Object> map = parentSections.get(1);
		assertNotNull(map);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_FIGURE, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		@SuppressWarnings("unchecked")
		Map<String, String> args = (Map<String, String>) map.get(YmlARGStructure.ARG_STRUCTURE_FIGURE_ARGS_KEY);
		assertNotNull(args);
		assertEquals(imageFile.getName(),
				FileTools.getFileName(args.get(YmlARGStructure.ARG_STRUCTURE_FIGURE_FILE_KEY))); // $NON-NLS-1$
		assertEquals("Caption", args.get(YmlARGStructure.ARG_STRUCTURE_FIGURE_CAPTION_KEY)); // $NON-NLS-1$ //$NON-NLS-1$

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_generateGenericValues_Working_SystemRequirement() throws CredibilityException, CoreException {

		// Initialize
		List<Map<String, Object>> parentSections = new ArrayList<>();
		List<IGenericTableValue> values = new ArrayList<>();
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());

		// Data
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());
		SystemRequirement sysRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), model, null,
				user);

		SystemRequirementParam parameter = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model, null);
		parameter.setType(FormFieldType.SYSTEM_REQUIREMENT.getType());

		SystemRequirementValue value = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), sysRequirement,
				parameter, user);
		value.setValue(sysRequirement.getId().toString());
		value = getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(value);
		values.add(value);

		// Test
		getAppManager().getService(IReportARGApplication.class).generateGenericValues(parentSections, sysRequirement,
				values, argParam);

		// Validate
		assertEquals(1, parentSections.size());
		Map<String, Object> map = parentSections.get(0);
		assertNotNull(map);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(parameter.getName() + RscTools.COLON + sysRequirement.getAbstract(),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_generateGenericValues_Working_CredibilityElement() throws CredibilityException, CoreException {

		// Initialize
		List<Map<String, Object>> parentSections = new ArrayList<>();
		List<IGenericTableValue> values = new ArrayList<>();
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());

		// Data
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		PCMMElement elt = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);

		SystemRequirement sysRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), model, null,
				user);

		SystemRequirementParam parameter = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model, null);
		parameter.setType(FormFieldType.CREDIBILITY_ELEMENT.getType());

		SystemRequirementValue value = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), sysRequirement,
				parameter, user);
		value.setValue(elt.getId().toString());
		value = getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(value);
		values.add(value);

		// Test
		getAppManager().getService(IReportARGApplication.class).generateGenericValues(parentSections, sysRequirement,
				values, argParam);

		// Validate
		assertEquals(1, parentSections.size());
		Map<String, Object> map = parentSections.get(0);
		assertNotNull(map);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(parameter.getName() + RscTools.COLON + elt.getAbstract(),
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_generateGenericValues_Working_CredibilityElementNotApplicable()
			throws CredibilityException, CoreException {

		// Initialize
		List<Map<String, Object>> parentSections = new ArrayList<>();
		List<IGenericTableValue> values = new ArrayList<>();
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());

		// Data
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		User user = TestEntityFactory.getNewUser(getDaoManager());

		SystemRequirement sysRequirement = TestEntityFactory.getNewSystemRequirement(getDaoManager(), model, null,
				user);

		SystemRequirementParam parameter = TestEntityFactory.getNewSystemRequirementParam(getDaoManager(), model, null);
		parameter.setType(FormFieldType.CREDIBILITY_ELEMENT.getType());

		SystemRequirementValue value = TestEntityFactory.getNewSystemRequirementValue(getDaoManager(), sysRequirement,
				parameter, user);
		value.setValue(PCMMElementSelectorWidget.NOT_APPLICABLE_ID.toString());
		value = getDaoManager().getRepository(ISystemRequirementValueRepository.class).update(value);
		values.add(value);

		// Test
		getAppManager().getService(IReportARGApplication.class).generateGenericValues(parentSections, sysRequirement,
				values, argParam);

		// Validate
		assertEquals(1, parentSections.size());
		Map<String, Object> map = parentSections.get(0);
		assertNotNull(map);
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, map.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(parameter.getName() + RscTools.COLON + PCMMElementSelectorWidget.NOT_APPLICABLE_VALUE,
				map.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_generateGenericValues_Working_Null() throws CredibilityException {
		// Initializes
		List<Map<String, Object>> sections = null;
		List<IGenericTableValue> values = null;
		ARGParameters argParameters = null;

		getAppManager().getService(IReportARGApplication.class).generateGenericValues(sections, null, values,
				argParameters);
		assertNull(sections);
	}

	@Test
	void test_generateHyperlink_nullSuffixParagraph() {
		// Launch
		Map<String, Object> paragraph = getAppManager().getService(IReportARGApplication.class)
				.generateHyperlink("TITLE", null, "PATH", "VALUE"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, paragraph.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("TITLE", paragraph.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals("PATH", paragraph.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_PATH_KEY)); //$NON-NLS-1$
		assertEquals("VALUE", paragraph.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_STRING_KEY)); //$NON-NLS-1$
		assertEquals(null, paragraph.get(YmlARGStructure.ARG_STRUCTURE_STRING_SUFFIX_KEY));
	}

	@Test
	void test_generateHyperlink_Working() {
		// Launch
		Map<String, Object> paragraph = getAppManager().getService(IReportARGApplication.class)
				.generateHyperlink("TITLE", "SUFFIX", "PATH", "VALUE"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, paragraph.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("TITLE", paragraph.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals("PATH", paragraph.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_PATH_KEY)); //$NON-NLS-1$
		assertEquals("VALUE", paragraph.get(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_STRING_KEY)); //$NON-NLS-1$
		assertEquals("SUFFIX", paragraph.get(YmlARGStructure.ARG_STRUCTURE_STRING_SUFFIX_KEY)); //$NON-NLS-1$
	}

	@Test
	void test_generateImage_Working() {
		// Launch
		Map<String, Object> paragraph = getAppManager().getService(IReportARGApplication.class).generateImage(null,
				"/path/to/my/image.png", "Image caption", "Image label"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_FIGURE, paragraph.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		@SuppressWarnings("unchecked")
		Map<String, String> args = (Map<String, String>) paragraph.get(YmlARGStructure.ARG_STRUCTURE_FIGURE_ARGS_KEY);
		assertNotNull(args);
		assertNotNull(args.get(YmlARGStructure.ARG_STRUCTURE_FIGURE_WIDTH_KEY));
		assertEquals("/path/to/my/image.png", args.get(YmlARGStructure.ARG_STRUCTURE_FIGURE_FILE_KEY)); //$NON-NLS-1$
		assertEquals("Image caption", args.get(YmlARGStructure.ARG_STRUCTURE_FIGURE_CAPTION_KEY)); //$NON-NLS-1$
		assertEquals("Image label", args.get(YmlARGStructure.ARG_STRUCTURE_FIGURE_LABEL_KEY)); //$NON-NLS-1$
	}

	@Test
	void test_generateLabelValue_Working() {
		String str = getAppManager().getService(IReportARGApplication.class).generateLabelValue("LABEL", "VALUE"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("LABEL" + RscTools.COLON + "VALUE", str); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_generateHtmlParagraph_Working() {
		Map<String, Object> paragraph = getAppManager().getService(IReportARGApplication.class)
				.generateHtmlParagraph("<h1>My title</h1>\n\nMy content"); //$NON-NLS-1$
		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_HTML_KEY, paragraph.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("<h1>My title</h1>My content", paragraph.get(YmlARGStructure.ARG_STRUCTURE_HTML_STRING_KEY)); //$NON-NLS-1$
	}

	@Test
	void test_generateLabelValue_Working_Null() {
		String str = getAppManager().getService(IReportARGApplication.class).generateLabelValue("LABEL", null); //$NON-NLS-1$
		assertEquals("LABEL" + RscTools.COLON, str); //$NON-NLS-1$
	}

	@Test
	void test_generateParagraph_Working() {
		Map<String, Object> paragraph = getAppManager().getService(IReportARGApplication.class)
				.generateParagraph("PARAGRAPH"); //$NON-NLS-1$
		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, paragraph.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("PARAGRAPH", paragraph.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
	}

	@Test
	void test_createReportParametersFile_ARGParamNull() throws IOException, CredibilityException {

		// test
		File reportParametersFile = null;
		reportParametersFile = getAppManager().getService(IReportARGApplication.class).createReportParametersFile(null);

		// validate
		assertNull(reportParametersFile);
	}

	@Test
	void test_createReportParametersFile_WorkingFileNotExists()
			throws IOException, CredibilityException, CoreException {

		// Initialize
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		IFile parametersFileToCreate = newCFFile.getProject().getFile("parametersFileToCreate.yml"); //$NON-NLS-1$
		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());
		argParam.setOutput(getTestTempFolder().getPath());
		argParam.setParametersFilePath(CFVariable.WORKSPACE.get() + parametersFileToCreate.getFullPath().toString());

		// test
		File reportParametersFile = null;
		reportParametersFile = getAppManager().getService(IReportARGApplication.class)
				.createReportParametersFile(argParam);

		// validate
		assertNotNull(reportParametersFile);
		assertTrue(reportParametersFile.exists());
		assertEquals(parametersFileToCreate.getName(), reportParametersFile.getName());

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_createReportParametersFile_WorkingFileInWorkspace()
			throws IOException, CredibilityException, CoreException {

		// Initialize
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		IFile structFile = TestEntityFactory.getNewFile("MyProject", "parameters.yml"); //$NON-NLS-1$ //$NON-NLS-2$

		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());

		argParam.setOutput(getTestTempFolder().getPath());
		argParam.setParametersFilePath(CFVariable.WORKSPACE.get() + structFile.getFullPath().toString());

		// test
		File reportParametersFile = getAppManager().getService(IReportARGApplication.class)
				.createReportParametersFile(argParam);

		// validate
		assertNotNull(reportParametersFile);
		assertTrue(reportParametersFile.exists());

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_createReportParametersFile_WorkingFileOnFileSystem()
			throws IOException, CredibilityException, CoreException {

		// Initialize
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		File createTempFile = null;
		createTempFile = File.createTempFile("param", ".yml", getTestTempFolder()); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(createTempFile);

		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());

		argParam.setOutput(getTestTempFolder().getPath());
		argParam.setParametersFilePath(createTempFile.getPath());

		// test
		File reportParametersFile = getAppManager().getService(IReportARGApplication.class)
				.createReportParametersFile(argParam);

		// validate
		assertNotNull(reportParametersFile);
		assertTrue(reportParametersFile.exists());

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_createReportParametersFile_Null() throws IOException, CredibilityException, CoreException {

		// Initialize
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());
		argParam.setOutput(getTestTempFolder().getPath());
		argParam.setParametersFilePath(null);
		argParam.setAuthor("The author"); //$NON-NLS-1$

		// Test
		File generatedFile = getAppManager().getService(IReportARGApplication.class)
				.createReportParametersFile(argParam);

		// Validate
		assertNull(generatedFile);

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_generateReportParametersFile_WorkingWord() throws CredibilityException, IOException, CoreException {

		// Initialize
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());
		argParam.setOutput(getTestTempFolder().getPath());
		argParam.setBackendType(ARGBackendDefault.WORD.getBackend());
		argParam.setAuthor("The author"); //$NON-NLS-1$
		argParam.setStructureFilePath(CFVariableResolver.resolveAll(argParam.getStructureFilePath()));

		// Tests
		File reportParametersFile = null;
		reportParametersFile = getAppManager().getService(IReportARGApplication.class)
				.createReportParametersFile(argParam);
		getAppManager().getService(IReportARGApplication.class).generateReportParametersFile(reportParametersFile,
				argParam);
		assertNotNull(reportParametersFile);
		assertTrue(reportParametersFile.exists());

		// load content
		Map<?, ?> ymlContent = null;
		try (FileReader fileReader = new FileReader(reportParametersFile)) {
			ymlContent = new Yaml().load(fileReader);
		}

		assertNotNull(ymlContent);
		assertEquals(getTestTempFolder().getPath().replace("\\", "/"), //$NON-NLS-1$ //$NON-NLS-2$
				ymlContent.get(YmlARGParameterSchema.CONF_ARG_OUTPUT));
		assertEquals(WorkspaceTools.toOsPath(
				newCFFile.getProject().getFile(ARGParametersFactory.CF_REPORT_DEFAULT_STRUCTURE_FILE).getFullPath())
				.replace("\\", "/"), ymlContent.get(YmlARGParameterSchema.CONF_ARG_STRUCTURE)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(ARGParametersFactory.CF_REPORT_DEFAULT_NAME,
				ymlContent.get(YmlARGParameterSchema.CONF_ARG_FILENAME));
		assertEquals(ARGParametersFactory.CF_REPORT_DEFAULT_TITLE,
				ymlContent.get(YmlARGParameterSchema.CONF_ARG_TITLE));
		assertEquals(ARGBackendDefault.WORD.getBackend(), ymlContent.get(YmlARGParameterSchema.CONF_ARG_BACKEND));
		assertEquals(ARGParametersFactory.CF_REPORT_DEFAULT_NUMBER,
				ymlContent.get(YmlARGParameterSchema.CONF_ARG_NUMBER));
		assertEquals(ARGReportTypeDefault.REPORT.getType(), ymlContent.get(YmlARGParameterSchema.CONF_ARG_REPORT_TYPE));
		assertEquals("The author", ymlContent.get(YmlARGParameterSchema.CONF_ARG_AUTHOR)); //$NON-NLS-1$

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_generateReportParametersFile_WorkingLatex() throws IOException, CredibilityException, CoreException {

		// Initialize
		IFile newCFFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParam = null;
		argParam = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newCFFile.getParent().getFullPath());
		argParam.setOutput(getTestTempFolder().getPath());
		argParam.setBackendType(ARGBackendDefault.LATEX.getBackend());
		argParam.setAuthor("The author"); //$NON-NLS-1$
		argParam.setStructureFilePath(CFVariableResolver.resolveAll(argParam.getStructureFilePath()));

		// Tests
		File reportParametersFile = getAppManager().getService(IReportARGApplication.class)
				.createReportParametersFile(argParam);
		getAppManager().getService(IReportARGApplication.class).generateReportParametersFile(reportParametersFile,
				argParam);
		assertNotNull(reportParametersFile);
		assertTrue(reportParametersFile.exists());

		// load content
		Map<?, ?> ymlContent = null;
		try (FileReader fileReader = new FileReader(reportParametersFile)) {
			ymlContent = new Yaml().load(fileReader);
		}

		assertNotNull(ymlContent);
		assertEquals(getTestTempFolder().getPath().replace("\\", "/"), //$NON-NLS-1$ //$NON-NLS-2$
				ymlContent.get(YmlARGParameterSchema.CONF_ARG_OUTPUT));
		assertEquals(WorkspaceTools.toOsPath(
				newCFFile.getProject().getFile(ARGParametersFactory.CF_REPORT_DEFAULT_STRUCTURE_FILE).getFullPath())
				.replace("\\", "/"), ymlContent.get(YmlARGParameterSchema.CONF_ARG_STRUCTURE)); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals(ARGParametersFactory.CF_REPORT_DEFAULT_NAME,
				ymlContent.get(YmlARGParameterSchema.CONF_ARG_FILENAME));
		assertEquals(ARGParametersFactory.CF_REPORT_DEFAULT_TITLE,
				ymlContent.get(YmlARGParameterSchema.CONF_ARG_TITLE));
		assertEquals(ARGBackendDefault.LATEX.getBackend(), ymlContent.get(YmlARGParameterSchema.CONF_ARG_BACKEND));
		assertEquals(ARGParametersFactory.CF_REPORT_DEFAULT_NUMBER,
				ymlContent.get(YmlARGParameterSchema.CONF_ARG_NUMBER));
		assertEquals(ARGReportTypeDefault.REPORT.getType(), ymlContent.get(YmlARGParameterSchema.CONF_ARG_REPORT_TYPE));
		assertEquals("The author", ymlContent.get(YmlARGParameterSchema.CONF_ARG_AUTHOR)); //$NON-NLS-1$

		// clear
		newCFFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_generateSection_Empty() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSection(null,
				null, new ArrayList<Map<String, Object>>(), YmlARGStructure.ARG_STRUCTURE_N_SECTION, null);

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SECTION, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertNull(section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertNull(section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(new ArrayList<Map<String, Object>>(), section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
	}

	@Test
	void test_generateSection_Empty_paragraph() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSection(null,
				null, new ArrayList<Map<String, Object>>(), YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, null);

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertNull(section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertNull(section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(new ArrayList<Map<String, Object>>(), section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
	}

	@Test
	void test_generateSection_Working() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSection("TITLE", //$NON-NLS-1$
				"TEXT", new ArrayList<Map<String, Object>>(), YmlARGStructure.ARG_STRUCTURE_N_SECTION, null); //$NON-NLS-1$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SECTION, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("TITLE", section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY)); //$NON-NLS-1$
		assertEquals("TEXT", section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals(new ArrayList<Map<String, Object>>(), section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
		assertFalse(section.containsKey(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY)); // $NON-NLS-1$

		// Launch
		section = getAppManager().getService(IReportARGApplication.class).generateSection("TITLE", "TEXT", null, null); //$NON-NLS-1$ //$NON-NLS-2$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SECTION, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("TITLE", section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY)); //$NON-NLS-1$
		assertEquals("TEXT", section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals(null, section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
		assertFalse(section.containsKey(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY)); // $NON-NLS-1$
	}

	@Test
	void test_generateSection_WorkingOrientationLandscape() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSection("TITLE", //$NON-NLS-1$
				"TEXT", new ArrayList<Map<String, Object>>(), YmlARGStructure.ARG_STRUCTURE_N_SECTION, //$NON-NLS-1$
				ARGOrientation.LANDSCAPE);

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SECTION, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("TITLE", section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY)); //$NON-NLS-1$
		assertEquals("TEXT", section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals(ARGOrientation.LANDSCAPE.getOrientation(),
				section.get(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY)); // $NON-NLS-1$
		assertEquals(new ArrayList<Map<String, Object>>(), section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
	}

	@Test
	void test_generateSection_Working_paragraph_OnlyTitle() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSection("TITLE", //$NON-NLS-1$
				null, new ArrayList<Map<String, Object>>(), YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, null); // $NON-NLS-1$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(null, section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals("TITLE", section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals(new ArrayList<Map<String, Object>>(), section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
	}

	@Test
	void test_generateSection_Working_paragraph_TitleAndContent() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSection("TITLE", //$NON-NLS-1$
				"CONTENT", new ArrayList<Map<String, Object>>(), YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, null); //$NON-NLS-1$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(null, section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals("TITLE\nCONTENT", section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals(new ArrayList<Map<String, Object>>(), section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
	}

	@Test
	void test_generateSection_Working_paragraph_subsections_Null() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSection(null, // $NON-NLS-1$
				null, null, YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, null); // $NON-NLS-1$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals(null, section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY));
		assertEquals(null, section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY));
		assertEquals(null, section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
	}

	@Test
	void test_generateSection_Working_subsections_Null() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSection("TITLE", //$NON-NLS-1$
				"TEXT", null, YmlARGStructure.ARG_STRUCTURE_N_SECTION, null); //$NON-NLS-1$ s

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SECTION, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("TITLE", section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY)); //$NON-NLS-1$
		assertEquals("TEXT", section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals(null, section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
	}

	@Test
	void test_generateStructure_Working() throws CredibilityException, IOException {
		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		options.put(ExportOptions.MODEL, model);

		// Tests
		Map<String, Object> ymlContent = getAppManager().getService(IReportARGApplication.class)
				.generateStructure(options);

		assertNotNull(ymlContent);
		assertTrue(ymlContent.containsKey(YmlARGStructure.ARG_STRUCTURE_VERSION_KEY));
		assertEquals(ARGVersion.ARG_VERSION, ymlContent.get(YmlARGStructure.ARG_STRUCTURE_VERSION_KEY));
		assertTrue(ymlContent.containsKey(YmlARGStructure.ARG_STRUCTURE_CHAPTERS_KEY));
		assertTrue(((List<?>) ymlContent.get(YmlARGStructure.ARG_STRUCTURE_CHAPTERS_KEY)).isEmpty());
	}

	@Test
	void test_generateStructure_Working_Empty() throws CredibilityException, IOException {
		// Generate content
		Map<String, Object> structure = getAppManager().getService(IReportARGApplication.class)
				.generateStructure(new HashMap<>());

		// Tests
		assertEquals(ARGVersion.ARG_VERSION, structure.get(YmlARGStructure.ARG_STRUCTURE_VERSION_KEY));
		assertEquals(new ArrayList<>(), structure.get(YmlARGStructure.ARG_STRUCTURE_CHAPTERS_KEY));
	}

	@Test
	void test_generateSubSection_Working() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSubSection(
				"TITLE", //$NON-NLS-1$
				"TEXT", new ArrayList<Map<String, Object>>(), null); //$NON-NLS-1$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SUBSECTION, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("TITLE", section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY)); //$NON-NLS-1$
		assertEquals("TEXT", section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals(new ArrayList<Map<String, Object>>(), section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
		assertFalse(section.containsKey(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY));
	}

	@Test
	void test_generateSubSection_Orientation_Landscape_Working() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSubSection(
				"TITLE", //$NON-NLS-1$
				"TEXT", new ArrayList<Map<String, Object>>(), ARGOrientation.LANDSCAPE); //$NON-NLS-1$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SUBSECTION, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("TITLE", section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY)); //$NON-NLS-1$
		assertEquals("TEXT", section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals(new ArrayList<Map<String, Object>>(), section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
		assertEquals(ARGOrientation.LANDSCAPE.getOrientation(),
				section.get(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY));
	}

	@Test
	void test_generateSubsubSection_Working() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSubsubSection(
				"TITLE", //$NON-NLS-1$
				"TEXT", new ArrayList<Map<String, Object>>(), null); //$NON-NLS-1$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SUBSUBSECTION, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("TITLE", section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY)); //$NON-NLS-1$
		assertEquals("TEXT", section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals(new ArrayList<Map<String, Object>>(), section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
		assertFalse(section.containsKey(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY)); // $NON-NLS-1$
	}

	@Test
	void test_generateSubsubSection_Orientation_Landscape_Working() {
		// Launch
		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSubsubSection(
				"TITLE", //$NON-NLS-1$
				"TEXT", new ArrayList<Map<String, Object>>(), ARGOrientation.LANDSCAPE); //$NON-NLS-1$

		// Tests
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SUBSUBSECTION, section.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertEquals("TITLE", section.get(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY)); //$NON-NLS-1$
		assertEquals("TEXT", section.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY)); //$NON-NLS-1$
		assertEquals(new ArrayList<Map<String, Object>>(), section.get(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY));
		assertEquals(ARGOrientation.LANDSCAPE.getOrientation(),
				section.get(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY)); // $NON-NLS-1$
	}

	@Test
	void test_generateSystemRequirementLine_Working() {
		// TODO: tests
	}

	@Test
	void test_generateInlining_working_section_exists() {

		Map<String, Object> section = getAppManager().getService(IReportARGApplication.class).generateSection("TITLE", //$NON-NLS-1$
				"TEXT", new ArrayList<Map<String, Object>>(), null); //$NON-NLS-1$

		Map<String, Object> sectionGenerated = getAppManager().getService(IReportARGApplication.class)
				.generateInlining(section, "my/path"); //$NON-NLS-1$

		// test
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_INLINEDOCX,
				sectionGenerated.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertTrue(sectionGenerated.containsKey(YmlARGStructure.ARG_STRUCTURE_INLINEDOCX_PATH_KEY));
		assertEquals("my/path", sectionGenerated.get(YmlARGStructure.ARG_STRUCTURE_INLINEDOCX_PATH_KEY)); //$NON-NLS-1$
	}

	@Test
	void test_generateInlining_working_section_null() {

		Map<String, Object> sectionGenerated = getAppManager().getService(IReportARGApplication.class)
				.generateInlining(null, "my/path"); //$NON-NLS-1$

		// test
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_INLINEDOCX,
				sectionGenerated.get(YmlARGStructure.ARG_STRUCTURE_N_KEY));
		assertTrue(sectionGenerated.containsKey(YmlARGStructure.ARG_STRUCTURE_INLINEDOCX_PATH_KEY));
		assertEquals("my/path", sectionGenerated.get(YmlARGStructure.ARG_STRUCTURE_INLINEDOCX_PATH_KEY)); //$NON-NLS-1$
	}

	@Test
	void test_getLinkRelativePath_Working() throws CoreException, CredibilityException {

		// Initialize
		IFile reportFile = TestEntityFactory.getNewFile("MyProject", "report.yml"); //$NON-NLS-1$ //$NON-NLS-2$
		IProject project = reportFile.getProject();
		IFolder folder = project.getFolder("myFolder"); //$NON-NLS-1$
		folder.create(true, true, new NullProgressMonitor());
		IFile evidenceFile = folder.getFile("myEvidence.docx"); //$NON-NLS-1$
		evidenceFile.create(null, true, new NullProgressMonitor());
		ARGParameters parameters = new ARGParameters();
		parameters.setOutput(reportFile.getParent().getFullPath().toString()); // Need to use the workspace...
		String path = getAppManager().getService(IReportARGApplication.class).getLinkPathRelativeToOutputDir(parameters,
				evidenceFile.getFullPath().toString());

		// Tests
		assertNotNull(path);
		assertEquals("myFolder/myEvidence.docx", path); //$NON-NLS-1$

		// clear
		reportFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_getLinkRelativePath_OtherFolder() throws CoreException, CredibilityException {

		// Initialize
		// create /MyProject/myEvidence.docx
		IFile evidenceFile = TestEntityFactory.getNewFile("MyProject", "myEvidence.docx"); //$NON-NLS-1$ //$NON-NLS-2$
		IProject project = evidenceFile.getProject();

		// create /MyProject/myFolder/report.yml
		IFolder folder = project.getFolder("myFolder"); //$NON-NLS-1$
		folder.create(true, true, new NullProgressMonitor());
		IFile reportFile = folder.getFile("report.yml"); //$NON-NLS-1$
		reportFile.create(null, true, new NullProgressMonitor());

		ARGParameters parameters = new ARGParameters();
		parameters.setOutput(reportFile.getParent().getFullPath().toString()); // Need to use the workspace...
		String path = getAppManager().getService(IReportARGApplication.class).getLinkPathRelativeToOutputDir(parameters,
				evidenceFile.getFullPath().toString());

		// Tests
		assertNotNull(path);
		assertEquals("../myEvidence.docx", path); //$NON-NLS-1$

		// clear
		evidenceFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_getLinkRelativePath_ExternalPathWorking() throws CredibilityException, IOException, CoreException {

		// Initialize
		// create /MyProject/myEvidence.docx
		IFile evidenceFile = TestEntityFactory.getNewFile("MyProject", "myEvidence.docx"); //$NON-NLS-1$ //$NON-NLS-2$

		// create ${tmp_folder}/output
		Path tmpOutput = new File(getTestTempFolder(), "output").toPath(); //$NON-NLS-1$
		Files.createDirectory(tmpOutput);

		ARGParameters parameters = new ARGParameters();
		parameters.setOutput(tmpOutput.toString()); // Need to use the workspace...
		String path = getAppManager().getService(IReportARGApplication.class).getLinkPathRelativeToOutputDir(parameters,
				evidenceFile.getFullPath().toString());

		// Tests
		assertNotNull(path);
		assertTrue(Paths.get(tmpOutput.toString(), path).toFile().exists());

		// clear
		evidenceFile.getProject().delete(true, new NullProgressMonitor());
		Files.delete(tmpOutput);
	}

	@Test
	void test_getLinkRelativePath_NoOutput() throws CredibilityException {
		ARGParameters parameters = new ARGParameters();
		String path = getAppManager().getService(IReportARGApplication.class).getLinkPathRelativeToOutputDir(parameters,
				"TEST"); //$NON-NLS-1$
		assertNull(path);
	}

	@Test
	void test_getLinkRelativePath_Null() throws CredibilityException {
		assertNull(getAppManager().getService(IReportARGApplication.class).getLinkPathRelativeToOutputDir(null, null));
	}

	@Test
	void test_getLinkRelativePath_Blank() throws CredibilityException {
		assertNull(getAppManager().getService(IReportARGApplication.class)
				.getLinkPathRelativeToOutputDir(new ARGParameters(), "")); //$NON-NLS-1$
	}

	@Test
	void test_getSectionTypeByGenericLevel_Working() {
		// level null
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH,
				getAppManager().getService(IReportARGApplication.class).getSectionTypeByGenericLevel(null));

		// level -1
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_CHAPTER,
				getAppManager().getService(IReportARGApplication.class).getSectionTypeByGenericLevel(-1));

		// level 0
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SECTION,
				getAppManager().getService(IReportARGApplication.class).getSectionTypeByGenericLevel(0));

		// level 1
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SUBSECTION,
				getAppManager().getService(IReportARGApplication.class).getSectionTypeByGenericLevel(1));

		// level 2
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_SUBSUBSECTION,
				getAppManager().getService(IReportARGApplication.class).getSectionTypeByGenericLevel(2));

		// level 3
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH,
				getAppManager().getService(IReportARGApplication.class).getSectionTypeByGenericLevel(3));

		// level 9
		assertEquals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH,
				getAppManager().getService(IReportARGApplication.class).getSectionTypeByGenericLevel(9));
	}

	@Test
	void test_ReportARGApplication() {
		ReportARGApplication reportArgApplication = new ReportARGApplication(getAppManager());
		assertNotNull(reportArgApplication);
		assertEquals(getAppManager(), reportArgApplication.getAppMgr());
	}

}
