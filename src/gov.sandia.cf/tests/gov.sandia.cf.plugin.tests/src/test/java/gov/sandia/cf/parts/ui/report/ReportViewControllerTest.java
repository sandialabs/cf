/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.report;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.report.ARGParametersFactory;
import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.parts.AbstractTestParts;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * JUnit test class for the Report View Controller
 * 
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class ReportViewControllerTest extends AbstractTestParts {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(AbstractTestParts.class);

	/**
	 * Parameters file
	 */

	@Test
	void test_checkARGParametersFile_ArgParam_Null() {
		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGParametersFile(null);

		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages()
				.contains(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_NULL)));
	}

	@Test
	void test_checkARGParametersFile_Empty() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());
		argParameters.setParametersFilePath(RscTools.empty());

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGParametersFile(argParameters);

		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages()
				.contains(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_EMPTY)));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkARGParametersFile_NotFile() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());
		argParameters.setParametersFilePath(CFVariable.WORKSPACE.get() + newFile.getParent().getFullPath().toString());

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGParametersFile(argParameters);

		// test
		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages()
				.contains(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_NOTFILE,
						argParameters.getParametersFilePath())));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkARGParametersFile_Parent_Not_Exist() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());
		argParameters.setParametersFilePath(CFVariable.WORKSPACE.get() + FileTools.PATH_SEPARATOR + "directory" //$NON-NLS-1$
				+ FileTools.PATH_SEPARATOR + "newFile.yml"); //$NON-NLS-1$

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGParametersFile(argParameters);

		// test
		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages()
				.contains(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_NOTEXIST,
						argParameters.getParametersFilePath())));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkARGParametersFile_SameAsStructure() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());
		argParameters.setParametersFilePath(argParameters.getStructureFilePath());

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGParametersFile(argParameters);

		// test
		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages().contains(RscTools
				.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_SAMEASSTRUCTUREFILE)));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkARGParametersFile_Working() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGParametersFile(argParameters);

		// test
		assertNull(notif);

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	/**
	 * Structure file
	 */

	@Test
	void test_checkARGStructureFile_ArgParam_Null() {
		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGStructureFile(null);

		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages()
				.contains(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_NULL)));
	}

	@Test
	void test_checkARGStructureFile_Empty() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());
		argParameters.setStructureFilePath(RscTools.empty());

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGStructureFile(argParameters);

		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages()
				.contains(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_EMPTY)));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkARGStructureFile_NotFile() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());
		argParameters.setStructureFilePath(CFVariable.WORKSPACE.get() + newFile.getParent().getFullPath().toString());

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGStructureFile(argParameters);

		// test
		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages()
				.contains(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_NOTFILE,
						argParameters.getStructureFilePath())));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkARGStructureFile_Parent_Not_Exist() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());
		argParameters.setStructureFilePath(CFVariable.WORKSPACE.get() + FileTools.PATH_SEPARATOR + "directory" //$NON-NLS-1$
				+ FileTools.PATH_SEPARATOR + "newFile.yml"); //$NON-NLS-1$

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGStructureFile(argParameters);

		// test
		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages()
				.contains(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_NOTEXIST,
						argParameters.getStructureFilePath())));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkARGStructureFile_SameAsStructure() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());
		argParameters.setStructureFilePath(argParameters.getParametersFilePath());

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGStructureFile(argParameters);

		// test
		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages().contains(RscTools
				.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_SAMEASPARAMETERSFILE)));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkARGStructureFile_Working() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGStructureFile(argParameters);

		// test
		assertNull(notif);

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	/**
	 * Output folder
	 */

	@Test
	void test_checkARGOutput_ArgParam_Null() {
		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGParamOutput(null);

		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages()
				.contains(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_NULL)));
	}

	@Test
	void test_checkARGOutput_Empty() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());
		argParameters.setOutput(RscTools.empty());

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGParamOutput(argParameters);

		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages()
				.contains(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_OUTPUT_EMPTY)));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkARGOutput_NotDirectory() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());
		argParameters.setOutput(CFVariable.WORKSPACE.get() + newFile.getFullPath().toString());

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGParamOutput(argParameters);

		// test
		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages().contains(RscTools.getString(
				RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_OUTPUT_NOTDIRECTORY, argParameters.getOutput())));
		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkARGOutput_Not_Exist() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());
		argParameters.setOutput(CFVariable.WORKSPACE.get() + FileTools.PATH_SEPARATOR + "directory"); //$NON-NLS-1$

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGParamOutput(argParameters);

		// test
		assertNotNull(notif);
		assertEquals(NotificationType.ERROR, notif.getType());
		assertTrue(notif.getMessages()
				.contains(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_OUTPUT_NOTEXIST,
						CFVariable.WORKSPACE.get() + FileTools.PATH_SEPARATOR + "directory"))); //$NON-NLS-1$
		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_checkARGOutput_Working() throws CoreException {

		// construct
		IFile newFile = TestEntityFactory.getNewFile("Project", "cfFile.cf"); //$NON-NLS-1$ //$NON-NLS-2$
		ARGParameters argParameters = ARGParametersFactory.getDefaultParameters(newFile.getParent().getFullPath());

		ReportView view = Mockito.mock(ReportView.class);
		ReportViewController ctrl = new ReportViewController(view);
		Notification notif = ctrl.checkARGParamOutput(argParameters);

		// test
		assertNull(notif);

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
	}
}
