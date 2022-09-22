/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.report.IReportARGApplication;
import gov.sandia.cf.application.report.IReportARGExecutionApp;
import gov.sandia.cf.application.report.ReportARGExecutionApp;
import gov.sandia.cf.constants.arg.ARGBackendDefault;
import gov.sandia.cf.dao.IARGParametersRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 *
 * JUnit test class for the ARG Report Application Controller
 * 
 * @author Maxime N.
 */
class ReportARGExecutionAppTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ReportARGExecutionAppTest.class);

	@Test
	void test_ReportARGExecutionApp_Working() {
		ReportARGExecutionApp obj = new ReportARGExecutionApp(getAppManager());
		assertNotNull(obj);
		assertEquals(obj.getAppMgr(), getAppManager());
	}

	@Test
	void test_addDefaultARGParameters_Working() throws CredibilityException, CoreException {

		IFile newFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters addDefaultARGParameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newFile.getFullPath());

		// test
		assertNotNull(addDefaultARGParameters);

		ARGParameters argParameters = getAppManager().getService(IReportARGExecutionApp.class).getARGParameters();
		assertEquals(addDefaultARGParameters, argParameters);

		// clear
		newFile.getParent().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_generateReportARG_ArgSetupNotSetted() throws IOException, CredibilityException, CoreException {

		// Initialize
		StringBuilder errorLog = new StringBuilder();
		StringBuilder infoLog = new StringBuilder();
		IFile newFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters parameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newFile.getParent().getFullPath());
		parameters.setBackendType(ARGBackendDefault.WORD.getBackend());

		// generate parameters file
		File reportParametersFile = getAppManager().getService(IReportARGApplication.class)
				.createReportParametersFile(parameters);
		getAppManager().getService(IReportARGApplication.class).generateReportParametersFile(reportParametersFile,
				parameters);

		// create execution parameters to give to the generator
		parameters
				.setParametersFilePath(FileTools.getNormalizedPath(Paths.get(reportParametersFile.getAbsolutePath())));

		// generate report - generate a python exception
		assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IReportARGExecutionApp.class).generateReportARG(parameters, errorLog, infoLog,
					new NullProgressMonitor());
		});

		assertFalse(infoLog.toString().isEmpty());
		assertFalse(errorLog.toString().isEmpty());

		// clear
		newFile.getParent().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_generateReportARG_Working() {

		// TODO do a real test with ARG installation
		// test
//		File cfReport = new File(FileTools.toOsPath(newFile.getFullPath()),
//				ARGParametersFactory.CF_REPORT_DEFAULT_NAME + FileTools.WORD_2007);
//		assertTrue(cfReport.exists());
	}

	@Test
	void test_generateReportARG_ARGParametersNull() {
		StringBuilder errorLog = new StringBuilder();
		StringBuilder infoLog = new StringBuilder();
		ARGParameters parameters = null;

		CredibilityException exception = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IReportARGExecutionApp.class).generateReportARG(parameters, errorLog, infoLog,
					new NullProgressMonitor());
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFREPORT_ARG_PARAM_NULL), exception.getMessage());
	}

	@Test
	void test_generateReportARG_Parameters_file_null() {
		StringBuilder errorLog = new StringBuilder();
		StringBuilder infoLog = new StringBuilder();
		ARGParameters parameters = new ARGParameters();
		parameters.setParametersFilePath(null);

		CredibilityException exception = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IReportARGExecutionApp.class).generateReportARG(parameters, errorLog, infoLog,
					new NullProgressMonitor());
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFREPORT_YAML_PARAMETERS_FILE_NOTEXISTS, "null"), //$NON-NLS-1$
				exception.getMessage());
	}

	@Test
	void test_generateReportARG_Parameters_file_not_exists() {
		StringBuilder errorLog = new StringBuilder();
		StringBuilder infoLog = new StringBuilder();
		ARGParameters parameters = new ARGParameters();
		parameters.setParametersFilePath("test.yml"); //$NON-NLS-1$

		CredibilityException exception = assertThrows(CredibilityException.class, () -> {
			getAppManager().getService(IReportARGExecutionApp.class).generateReportARG(parameters, errorLog, infoLog,
					new NullProgressMonitor());
		});
		assertEquals(RscTools.getString(RscConst.EX_CONFREPORT_YAML_PARAMETERS_FILE_NOTEXISTS, "test.yml"), //$NON-NLS-1$
				exception.getMessage());
	}

	@Test
	void test_getARGParameters_WorkingGenerated() throws CredibilityException, CoreException {

		IFile newFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters addDefaultARGParameters = getAppManager().getService(IReportARGExecutionApp.class)
				.addDefaultARGParameters(newFile.getFullPath());

		// test
		ARGParameters argParameters = getAppManager().getService(IReportARGExecutionApp.class).getARGParameters();
		assertNotNull(argParameters);
		assertEquals(addDefaultARGParameters, argParameters);

		// clear
		newFile.getParent().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_getARGParameters_NotGenerated() {
		assertNull(getAppManager().getService(IReportARGExecutionApp.class).getARGParameters());
	}

	@Test
	void test_getARGTypes_Working() throws CredibilityException, URISyntaxException, IOException, CoreException {
		// TODO test with an ARG instance
		// Logs
//		StringBuilder errorLog = new StringBuilder();
//		StringBuilder infoLog = new StringBuilder();
//		IFile newFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
//		ARGParameters parameters = getAppManager().getService(IReportARGExecutionApp.class)
//				.addDefaultARGParameters(newFile.getParent().getFullPath());
//		parameters.setArgExecPath(argScriptTempFile.getAbsolutePath());
//
//		assertNotNull(getAppManager().getService(IReportARGExecutionApp.class).getARGTypes(parameters, errorLog,
//				infoLog, new NullProgressMonitor()));
//
//
//		// clear
//		newFile.getParent().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_getARGTypes_Working_With_prescript()
			throws CredibilityException, URISyntaxException, IOException, CoreException {
		// TODO test with an ARG instance
		// Logs
//		StringBuilder errorLog = new StringBuilder();
//		StringBuilder infoLog = new StringBuilder();
//		IFile newFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
//		ARGParameters parameters = getAppManager().getService(IReportARGExecutionApp.class)
//				.addDefaultARGParameters(newFile.getParent().getFullPath());
//		parameters.setArgPreScript(WorkspaceTools.getStaticFilePath("report/arg/setEnvEcho")); //$NON-NLS-1$
//		parameters.setOutput(getTestTempFolder().getPath());
//		assertNotNull(getAppManager().getService(IReportARGExecutionApp.class).getARGTypes(parameters, errorLog,
//				infoLog, new NullProgressMonitor()));
//
//
//		// clear
//		newFile.getParent().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_getARGVersion_Working() throws CredibilityException, URISyntaxException, IOException, CoreException {
		// TODO test with an ARG instance
//		 Logs
//		StringBuilder errorLog = new StringBuilder();
//		StringBuilder infoLog = new StringBuilder();
//		IFile newFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
//		ARGParameters parameters = getAppManager().getService(IReportARGExecutionApp.class)
//				.addDefaultARGParameters(newFile.getParent().getFullPath());
//
//		assertNotNull(getAppManager().getService(IReportARGExecutionApp.class).getARGVersion(parameters, errorLog,
//				infoLog, new NullProgressMonitor()));
//
//
//		// clear
//		newFile.getParent().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_getARGVersion_Working_With_prescript()
			throws CredibilityException, URISyntaxException, IOException, CoreException {
		// TODO test with an ARG instance
		// Logs
//		StringBuilder errorLog = new StringBuilder();
//		StringBuilder infoLog = new StringBuilder();
//		IFile newFile = TestEntityFactory.getNewFile("MyProject", "file.cf"); //$NON-NLS-1$ //$NON-NLS-2$
//		ARGParameters parameters = getAppManager().getService(IReportARGExecutionApp.class)
//				.addDefaultARGParameters(newFile.getParent().getFullPath());
//		parameters.setOutput(getTestTempFolder().getPath());
//		parameters.setArgPreScript(WorkspaceTools.getStaticFilePath("report/arg/setEnvEcho")); //$NON-NLS-1$
//
//		assertNotNull(getAppManager().getService(IReportARGExecutionApp.class).getARGVersion(parameters, errorLog,
//				infoLog, new NullProgressMonitor()));
//
//
//		// clear
//		newFile.getParent().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_updateARGParameters_Working() throws CredibilityException {
		ARGParameters argParameters = TestEntityFactory.getNewARGParameters(getDaoManager());
		ARGParameters copy = argParameters.copy();

		// update 1
		copy.setParametersFilePath("myParametersFile.yml"); //$NON-NLS-1$

		getAppManager().getService(IReportARGExecutionApp.class).updateARGParameters(copy);

		// update 2
		copy.setArgExecPath("Exec path");//$NON-NLS-1$

		getAppManager().getService(IReportARGExecutionApp.class).updateARGParameters(copy);

		// test
		ARGParameters argParametersUpdated = getAppManager().getService(IReportARGExecutionApp.class)
				.getARGParameters();
		assertNotNull(argParametersUpdated);
		assertEquals("myParametersFile.yml", argParametersUpdated.getParametersFilePath()); //$NON-NLS-1$
		assertEquals("Exec path", argParametersUpdated.getArgExecPath()); //$NON-NLS-1$

		List<ARGParameters> findAll = getDaoManager().getRepository(IARGParametersRepository.class).findAll();
		assertEquals(1, findAll.size());
	}

	@Test
	void test_updateARGParameters_NotGenerated() throws CredibilityException {
		ARGParameters argParameters = new ARGParameters();
		assertNull(getAppManager().getService(IReportARGExecutionApp.class).updateARGParameters(argParameters));
	}
}
