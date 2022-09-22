/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.report;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.report.IReportARGApplication;
import gov.sandia.cf.application.report.IReportARGExecutionApp;
import gov.sandia.cf.constants.arg.ARGBackendDefault;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.tools.FileExtension;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * The generate ARG Report runnable task
 * 
 * @author Didier Verstraete
 *
 */
public class GenerateARGReportRunnable implements IRunnableWithProgress {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(GenerateARGReportRunnable.class);

	private IViewManager viewManager;
	private ARGParameters argParameters;
	private Map<ExportOptions, Object> options;
	private StringBuilder errorLog;
	private StringBuilder infoLog;

	/**
	 * @param viewManager   the view manager
	 * @param argParameters the ARG parameters
	 * @param options       the ARG options to construct the structure file
	 * @param errorLog      the error log
	 * @param infoLog       the info log
	 */
	public GenerateARGReportRunnable(IViewManager viewManager, ARGParameters argParameters,
			Map<ExportOptions, Object> options, StringBuilder errorLog, StringBuilder infoLog) {
		this.viewManager = viewManager;
		this.argParameters = argParameters;
		this.options = options;
		this.errorLog = errorLog;
		this.infoLog = infoLog;
	}

	/**
	 * Run the generation
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		generateReport(monitor);
	}

	/**
	 * Generate the Credibility Framework
	 * 
	 * @param progressMonitor the progress monitor
	 * @throws InterruptedException
	 * 
	 */
	private void generateReport(IProgressMonitor progressMonitor) {

		progressMonitor.beginTask(RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_PROCESSING), 100);
		progressMonitor.worked(10);

		try {
			// Generate structure file
			progressMonitor.subTask(RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_TASK_STRUCTUREFILE));
			File structFile = generateStructureFile(options, argParameters, progressMonitor);
			boolean structFileOk = structFile != null && structFile.exists() && structFile.getParentFile() != null
					&& structFile.getParentFile().exists();
			progressMonitor.worked(20);

			// check for user cancellation
			if (progressMonitor.isCanceled()) {
				return;
			}

			// Generate parameters file
			progressMonitor.subTask(RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_TASK_PARAMETERSFILE));
			File parametersFile = generateParametersFile(argParameters, progressMonitor);
			boolean paramFileOk = parametersFile != null && parametersFile.exists()
					&& parametersFile.getParentFile() != null && parametersFile.getParentFile().exists();
			progressMonitor.worked(20);

			// check for user cancellation
			if (progressMonitor.isCanceled()) {
				return;
			}

			if (argParameters != null && paramFileOk && structFileOk) {

				// create execution parameters to give to the generator
				ARGParameters executionParameters = argParameters.copy();
				executionParameters.setParametersFilePath(
						FileTools.getNormalizedPath(Paths.get(parametersFile.getAbsolutePath())));

				// Call ARG by giving files to generate the report
				String argExecutablePath = executionParameters.getArgExecPath();
				if (argExecutablePath != null && !argExecutablePath.isEmpty()) {
					generateReportARG(executionParameters, progressMonitor);
				} else {
					Display.getDefault()
							.syncExec(() -> MessageDialog.openError(Display.getDefault().getActiveShell(),
									RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
									RscTools.getString(RscConst.ERR_REPORTVIEW_ARG_PATH)));
				}

				progressMonitor.done();
			}
		} catch (CredibilityException e) {
			logger.error(e.getMessage());
			Display.getDefault().syncExec(() -> MessageDialog.openError(Display.getDefault().getActiveShell(),
					RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE), e.getMessage()));
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Generate the structure file path.
	 *
	 * @param options         the options
	 * @param argParameters   the arg parameters
	 * @param progressMonitor the progress monitor
	 * @return The structure file path
	 * @throws CredibilityException the credibility exception
	 */
	private File generateStructureFile(Map<ExportOptions, Object> options, ARGParameters argParameters,
			IProgressMonitor progressMonitor) throws CredibilityException {

		if (options == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFREPORT_OPTIONS_NULL));
		}

		try {

			// Generate structure data
			progressMonitor
					.subTask(RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_TASK_STRUCTUREFILE_SUB1));
			options.put(ExportOptions.ARG_PARAMETERS, argParameters);
			Map<String, Object> structure = viewManager.getAppManager().getService(IReportARGApplication.class)
					.generateStructure(options);

			// Create file
			progressMonitor
					.subTask(RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_TASK_STRUCTUREFILE_SUB2));
			File structureFile = viewManager.getAppManager().getService(IReportARGApplication.class)
					.createReportStructureFile(argParameters);

			// put data into YML
			progressMonitor
					.subTask(RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_TASK_STRUCTUREFILE_SUB3));
			viewManager.getAppManager().getService(IReportARGApplication.class)
					.copyReportStructureContentIntoFile(structureFile, structure);

			// Return if file exist
			return structureFile;

		} catch (IOException e) {
			throw new CredibilityException("An error occurs while generating the structure file: " + e.getMessage() //$NON-NLS-1$
					+ RscTools.CARRIAGE_RETURN + e.toString(), e);
		}
	}

	/**
	 * Generate the parameters file.
	 *
	 * @param argParameters   the arg parameters
	 * @param progressMonitor the progress monitor
	 * @return true if the parameters file has been generated
	 * @throws CredibilityException if an error occured
	 */
	private File generateParametersFile(ARGParameters argParameters, IProgressMonitor progressMonitor)
			throws CredibilityException {

		if (argParameters == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFREPORT_ARG_PARAM_NULL));
		}

		try {

			// Create file
			progressMonitor
					.subTask(RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_TASK_PARAMETERSFILE_SUB1));
			File parametersFile = viewManager.getAppManager().getService(IReportARGApplication.class)
					.createReportParametersFile(argParameters);

			// put data into YML
			progressMonitor
					.subTask(RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_TASK_PARAMETERSFILE_SUB2));
			viewManager.getAppManager().getService(IReportARGApplication.class)
					.generateReportParametersFile(parametersFile, argParameters);

			// Return if file exist
			return parametersFile;

		} catch (IOException e) {
			throw new CredibilityException("An error occurs while generating the structure file: " + e.getMessage() //$NON-NLS-1$
					+ RscTools.CARRIAGE_RETURN + e.toString(), e);
		}
	}

	/**
	 * Launch generation ARG command
	 * 
	 * @param parameters      the arg parameters
	 * @param progressMonitor the progress monitor
	 */
	private void generateReportARG(ARGParameters parameters, IProgressMonitor progressMonitor) {
		try {
			// delete previous report before generation
			progressMonitor.subTask(RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_TASK_DELETEPREVIOUS));
			deletePreviousReport(parameters);

			// check for user cancellation
			if (progressMonitor.isCanceled()) {
				return;
			}

			// Run process
			progressMonitor.subTask(RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_TASK_GENREPORT));
			viewManager.getAppManager().getService(IReportARGExecutionApp.class).generateReportARG(parameters, errorLog,
					infoLog, progressMonitor);
			progressMonitor.worked(50);

			// check for user cancellation
			if (progressMonitor.isCanceled()) {
				return;
			}

			// open ARG report
			progressMonitor.subTask(RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_TASK_OPENREPORT));
			openReport(parameters);

			// refresh project
			WorkspaceTools.refreshProject();

		} catch (CredibilityException e) {
			logger.error(e.getMessage());
			Display.getDefault().syncExec(() -> MessageDialog.openError(Display.getCurrent().getActiveShell(),
					RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE), e.getMessage()));
		}
	}

	/**
	 * Delete the ARG report before generation.
	 *
	 * @param parameters the parameters
	 * @throws CredibilityException the credibility exception
	 */
	private void deletePreviousReport(ARGParameters parameters) throws CredibilityException {

		// find report
		if (parameters != null && parameters.getBackendType() != null && parameters.getOutput() != null
				&& parameters.getFilename() != null) {

			if (ARGBackendDefault.WORD.getBackend().equals(parameters.getBackendType())) {

				// try to find word file
				deleteGeneratedFile(parameters, FileExtension.WORD_2007.getExtension());

			} else if (ARGBackendDefault.LATEX.getBackend().equals(parameters.getBackendType())) {

				// try to find PDF file if it has been generated
				deleteGeneratedFile(parameters, FileExtension.PDF.getExtension());

				// try to find latex file
				deleteGeneratedFile(parameters, FileExtension.LATEX.getExtension());
			}
		}
	}

	/**
	 * Delete the generated files before new generation.
	 *
	 * @param parameters the ARG parameters
	 * @param extension  the file extension
	 * @throws CredibilityException the credibility exception
	 */
	private void deleteGeneratedFile(ARGParameters parameters, String extension) throws CredibilityException {

		// find file in workspace
		IPath reportIPath = new org.eclipse.core.runtime.Path(parameters.getOutput())
				.append(parameters.getFilename() + extension);
		if (WorkspaceTools.existsInWorkspace(reportIPath)) {
			try {
				IResource ifile = WorkspaceTools.findFirstResourceInWorkspace(reportIPath);
				if (ifile != null) {
					ifile.delete(true, new NullProgressMonitor());
				}
			} catch (CoreException e) {
				logger.error(RscTools.getString(RscConst.EX_REPORTVIEW_DELETE_REPORT, parameters.getFilename()), e);
			}
		}

		try {
			// find file on system
			Path reportPath = Paths.get(parameters.getOutput(), parameters.getFilename() + extension);

			// delete file
			if (reportPath != null && reportPath.toFile().exists()) {
				Files.delete(reportPath);
			}

		} catch (FileSystemException e) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_REPORTVIEW_REPORT_ALREADYOPENED, parameters.getFilename()), e);
		} catch (InvalidPathException | IOException e) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_REPORTVIEW_DELETE_REPORT, parameters.getFilename()), e);
		}
	}

	/**
	 * Open the ARG report after generation
	 * 
	 * @param parameters the arg parameters
	 */
	private void openReport(ARGParameters parameters) {

		// find report
		File reportFile = findGeneratedReport(parameters);

		if (reportFile != null && reportFile.exists()) {

			// Open confirm dialog
			Display.getDefault()
					.syncExec(() -> MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
							RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
							RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATED)));

			// Open the report
			FileTools.openFile(reportFile);

		} else {
			logger.error(RscTools.getString(RscConst.EX_REPORTVIEW_OPEN_REPORT, parameters.getFilename()));
			Display.getDefault()
					.syncExec(() -> MessageDialog.openError(Display.getCurrent().getActiveShell(),
							RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
							RscTools.getString(RscConst.EX_REPORTVIEW_OPEN_REPORT, parameters.getFilename())));
		}
	}

	/**
	 * Find the report generated depending of the Backend type
	 * 
	 * @param parameters the arg parameters
	 * @return the generated file depending of the Backend type
	 */
	private File findGeneratedReport(ARGParameters parameters) {

		if (parameters != null && parameters.getBackendType() != null && parameters.getOutput() != null
				&& parameters.getFilename() != null) {

			if (ARGBackendDefault.WORD.getBackend().equals(parameters.getBackendType())) {
				return findWordReport(parameters.getOutput(), parameters.getFilename());
			} else if (ARGBackendDefault.LATEX.getBackend().equals(parameters.getBackendType())) {
				return findLatexReport(parameters.getOutput(), parameters.getFilename());
			}
		}

		return null;
	}

	/**
	 * @param folder   the folder name
	 * @param filename the filename
	 * @return the word document generated
	 */
	private File findWordReport(String folder, String filename) {
		return FileTools.findFileInWorkspaceOrSystem(folder, filename + FileExtension.WORD_2007.getExtension());
	}

	/**
	 * @param folder   the folder name
	 * @param filename the filename
	 * @return the pdf if present or latex document generated
	 */
	private File findLatexReport(String folder, String filename) {

		File reportFile = null;

		/* try to find PDF file if it has been generated */
		reportFile = FileTools.findFileInWorkspaceOrSystem(folder, filename + FileExtension.PDF.getExtension());

		/* otherwise return latex file */
		if (reportFile == null || !reportFile.exists()) {
			reportFile = FileTools.findFileInWorkspaceOrSystem(folder, filename + FileExtension.LATEX.getExtension());
		}

		return reportFile;
	}
}
