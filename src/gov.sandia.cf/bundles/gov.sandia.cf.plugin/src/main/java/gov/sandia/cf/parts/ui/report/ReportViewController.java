/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.report;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.decision.IDecisionApplication;
import gov.sandia.cf.application.intendedpurpose.IIntendedPurposeApp;
import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.pcmm.IPCMMAssessmentApp;
import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.application.pcmm.IPCMMPlanningApplication;
import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.application.report.IReportARGExecutionApp;
import gov.sandia.cf.application.requirement.ISystemRequirementApplication;
import gov.sandia.cf.application.uncertainty.IUncertaintyApplication;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.ARGParametersQoIOption;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.dto.arg.ARGType;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.parts.dialogs.NewFileTreeSelectionDialog;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.TextWidget;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.CFVariableResolver;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * Report view controller: Used to control the Report view
 * 
 * @author Didier Verstraete
 *
 */
public class ReportViewController extends AViewController<ReportViewManager, ReportView>
		implements IReportViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReportViewController.class);

	private ARGParameters argParameters;
	private List<QuantityOfInterest> pirtQoIList;
	private Map<QuantityOfInterest, Map<Class<?>, Control>> pirtControlMap;
	private List<Tag> pcmmTagList;
	private ARGType argTypes;

	private String argVersion;

	private boolean asyncDataLoading = false;

	/**
	 * Instantiates a new report view controller.
	 *
	 * @param viewManager the view manager
	 * @param view        the view
	 */
	ReportViewController(ReportViewManager viewManager, ReportView view) {
		super(viewManager);
		super.setView(view);
	}

	/**
	 * Instantiates a new report view controller.
	 *
	 * @param viewManager the view manager
	 */
	ReportViewController(ReportViewManager viewManager) {
		super(viewManager);
		super.setView(new ReportView(this, SWT.NONE));
	}

	/**
	 * Reload data.
	 */
	void reloadData() {
		// reload parameters from database
		argParameters = getViewManager().getAppManager().getService(IReportARGExecutionApp.class).getARGParameters();

		// get default parameters
		if (argParameters == null) {
			try {

				// persist ARG default parameters
				argParameters = getViewManager().getAppManager().getService(IReportARGExecutionApp.class)
						.addDefaultARGParameters(getViewManager().getCredibilityEditor().getCfProjectPath());

				if (argParameters == null) {
					return;
				}
			} catch (CredibilityException e) {
				logger.error("Impossible to load the arg parameters.", e); //$NON-NLS-1$
			}
		}

		// execute asynchronously data loading
		Display.getCurrent().asyncExec(() -> {
			startAsyncDataLoading();

			// reload combo-box parameters
			reloadARGTypes(new NullProgressMonitor());
			reloadARGVersion(new NullProgressMonitor());

			// reload execution environment
			getView().refreshARGSetup();

			// reload
			getView().refreshARGParameters();
			getView().refreshPlanning();
			reloadPIRT();
			reloadPCMM();
			getView().refreshCustomEnding();

			stopAsyncDataLoading();
		});
	}

	/**
	 * Reload ARG types job.
	 */
	void reloadARGTypesJob() {
		Display.getCurrent().asyncExec(() -> {
			startAsyncDataLoading();

			try {
				StringBuilder consoleLog = new StringBuilder();

				argTypes = getViewManager().getAppManager().getService(IReportARGExecutionApp.class).getARGTypes(
						computeARGParametersForExecution(argParameters), consoleLog, consoleLog,
						new NullProgressMonitor());

				getView().logInConsole(consoleLog.toString());

				getView().refreshARGParametersARGTypes();

			} catch (Exception e) {
				logger.warn(e.getMessage());
			}

			stopAsyncDataLoading();
		});
	}

	/**
	 * Reload the ARG parameters : if not present, add default data.
	 *
	 * @param monitor the monitor
	 */
	void reloadARGTypes(IProgressMonitor monitor) {

		try {
			StringBuilder consoleLog = new StringBuilder();

			argTypes = getViewManager().getAppManager().getService(IReportARGExecutionApp.class)
					.getARGTypes(computeARGParametersForExecution(argParameters), consoleLog, consoleLog, monitor);

			getView().logInConsole(consoleLog.toString());

		} catch (Exception e) {
			logger.warn(RscTools.getString(RscConst.EX_ARG_COMMAND_EXCEPTION), e);
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
					RscTools.getString(RscConst.EX_ARG_COMMAND_EXCEPTION));
		}
	}

	/**
	 * Reload ARG version job.
	 */
	void reloadARGVersionJob() {
		Display.getCurrent().asyncExec(() -> {
			startAsyncDataLoading();

			try {
				StringBuilder consoleLog = new StringBuilder();

				argVersion = getViewManager().getAppManager().getService(IReportARGExecutionApp.class).getARGVersion(
						computeARGParametersForExecution(argParameters), consoleLog, consoleLog,
						new NullProgressMonitor());

				getView().logInConsole(consoleLog.toString());

				getView().refreshARGSetupARGVersion();

			} catch (Exception e) {
				logger.warn(e.getMessage());
			}

			stopAsyncDataLoading();
		});
	}

	/**
	 * Reload ARG version.
	 *
	 * @param monitor the monitor
	 */
	void reloadARGVersion(IProgressMonitor monitor) {
		try {
			StringBuilder consoleLog = new StringBuilder();

			argVersion = getViewManager().getAppManager().getService(IReportARGExecutionApp.class)
					.getARGVersion(computeARGParametersForExecution(argParameters), consoleLog, consoleLog, monitor);

			getView().logInConsole(consoleLog.toString());

		} catch (Exception e) {
			logger.warn(RscTools.getString(RscConst.EX_ARG_COMMAND_EXCEPTION), e);
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
					RscTools.getString(RscConst.EX_ARG_COMMAND_EXCEPTION));
		}
	}

	/**
	 * Reload PIRT data
	 */
	void reloadPIRT() {
		// QoI list
		pirtQoIList = new ArrayList<>();
		pirtControlMap = new HashMap<>();

		// Get Model
		Model model = getViewManager().getCache().getModel();
		if (model != null) {
			// Get QoI list
			pirtQoIList = getViewManager().getAppManager().getService(IPIRTApplication.class).getRootQoI(model);
		}

		// load checkbox
		getView().refreshPIRT();

		// Render
		getView().refreshPIRTQoIList();
	}

	/**
	 * Reload PCMM data
	 */
	void reloadPCMM() {
		// Get Tag list
		pcmmTagList = getViewManager().getAppManager().getService(IPCMMApplication.class).getTags();

		getView().refreshPCMMTagList();
		getView().refreshPCMM();
	}

	/**
	 * Start async data loading.
	 */
	private void startAsyncDataLoading() {
		getView().enableView(false);
		asyncDataLoading = true;
	}

	/**
	 * Stop async data loading.
	 */
	private void stopAsyncDataLoading() {
		getView().enableView(true);
		asyncDataLoading = false;
	}

	/**
	 * Checks if is data loading.
	 *
	 * @return true, if is data loading
	 */
	boolean isAsyncDataLoading() {
		return asyncDataLoading;
	}

	/**
	 * @return the ARG version
	 */
	String getARGVersion() {
		return argVersion;
	}

	/**
	 * Gets the pirt control map.
	 *
	 * @return the pirt control map
	 */
	Map<QuantityOfInterest, Map<Class<?>, Control>> getPirtControlMap() {
		return pirtControlMap;
	}

	/**
	 * Gets the pirt qo I list.
	 *
	 * @return the pirt qo I list
	 */
	List<QuantityOfInterest> getPirtQoIList() {
		return pirtQoIList;
	}

	/**
	 * Gets the pcmm tag list.
	 *
	 * @return the pcmm tag list
	 */
	List<Tag> getPcmmTagList() {
		return pcmmTagList;
	}

	/**
	 * @param argExecPath the arg executable path
	 * @return null if the value of the arg executable path is valid, otherwise
	 *         return the associated notification.
	 */
	public Notification checkArgSetupExecutable(String argExecPath) {

		logger.debug("Check ARG executable path"); //$NON-NLS-1$

		// if is blank - set default
		if (StringUtils.isBlank(argExecPath)) {
			return NotificationFactory.getNewError(
					RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGSETUP_ARGEXECPATH_EMPTY));
		}

		return null;
	}

	/**
	 * Check ARG setup validity
	 * 
	 * @return true if the ARG setup are valid, otherwise false.
	 */
	public boolean validateArgSetupExecutable() {

		Notification notifArgSetupExecutable = checkArgSetupExecutable(getView().getTxtArgSetupExecutable().getValue());
		if (notifArgSetupExecutable != null) {
			getView().getTxtArgSetupExecutable().setHelper(notifArgSetupExecutable);
			return false;
		}

		return true;
	}

	/**
	 * @param argParameters the arg parameters
	 * @return null if the value of the parameters file is valid, otherwise return
	 *         the associated notification.
	 */
	public Notification checkARGParametersFile(ARGParameters argParameters) {

		logger.debug("Check ARG parameters file"); //$NON-NLS-1$

		if (argParameters == null) {
			return NotificationFactory
					.getNewError(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_NULL));
		}

		// if is blank - set default
		if (StringUtils.isBlank(argParameters.getParametersFilePath())) {
			return NotificationFactory.getNewError(
					RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_EMPTY));
		}

		// resolve variable if exists
		String pathResolved = RscTools.empty();
		try {
			pathResolved = CFVariableResolver.resolveAll(argParameters.getParametersFilePath());
		} catch (CredibilityException e) {
			return NotificationFactory.getNewError(e.getMessage());
		}

		// check is a file
		File parametersFile = new File(pathResolved);

		if (parametersFile.isDirectory()) {
			return NotificationFactory.getNewError(
					RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_NOTFILE,
							argParameters.getParametersFilePath()));
		}

		// check parent folder exists or is null
		File parametersFileParent = parametersFile.getParentFile();
		File parentFile = FileTools.findFileInWorkspaceOrSystem(parametersFileParent.getPath());

		if (parentFile == null || !parentFile.exists()) {
			return NotificationFactory.getNewError(
					RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_NOTEXIST,
							argParameters.getParametersFilePath()));
		}

		// check same file as structure file
		if (argParameters.getParametersFilePath().equals(argParameters.getStructureFilePath())) {
			return NotificationFactory.getNewError(RscTools
					.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_PARAMETERSFILE_SAMEASSTRUCTUREFILE));
		}

		return null;
	}

	/**
	 * Check ARG parameters file path validity
	 * 
	 * @return true if the ARG parameters file path is valid, otherwise false.
	 */
	public boolean validateArgParametersFilePath() {

		Notification notifParamFile = checkARGParametersFile(argParameters);
		if (notifParamFile != null) {
			getView().getTxtARGParamParametersFile().setHelper(notifParamFile);
			return !notifParamFile.isError();
		}

		return true;
	}

	/**
	 * @param argParameters the arg parameters
	 * @return null if the value of the structure file is valid, otherwise return
	 *         the associated notification.
	 */
	public Notification checkARGStructureFile(ARGParameters argParameters) {

		logger.debug("Check ARG structure file"); //$NON-NLS-1$

		if (argParameters == null) {
			return NotificationFactory
					.getNewError(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_NULL));
		}

		// if is blank - set default
		if (StringUtils.isBlank(argParameters.getStructureFilePath())) {
			return NotificationFactory.getNewError(
					RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_EMPTY));
		}

		// resolve variable if exists
		String pathResolved = RscTools.empty();
		try {
			pathResolved = CFVariableResolver.resolveAll(argParameters.getStructureFilePath());
		} catch (CredibilityException e) {
			return NotificationFactory.getNewError(e.getMessage());
		}

		// check is a file
		File structureFile = new File(pathResolved);

		if (structureFile.isDirectory()) {
			return NotificationFactory.getNewError(
					RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_NOTFILE,
							argParameters.getStructureFilePath()));
		}

		// check parent folder exists or is null
		File structureFileParent = structureFile.getParentFile();
		File parentFile = FileTools.findFileInWorkspaceOrSystem(structureFileParent.getPath());

		if (parentFile == null || !parentFile.exists()) {
			return NotificationFactory.getNewError(
					RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_NOTEXIST,
							argParameters.getStructureFilePath()));
		}

		// check same file as parameters file
		if (argParameters.getStructureFilePath().equals(argParameters.getParametersFilePath())) {
			return NotificationFactory.getNewError(RscTools
					.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_SAMEASPARAMETERSFILE));
		}

		return null;
	}

	/**
	 * Check ARG structure file path validity
	 * 
	 * @return true if the ARG structure file path is valid, otherwise false.
	 */
	public boolean validateArgStructureFilePath() {

		Notification notifStructFile = checkARGStructureFile(argParameters);
		if (notifStructFile != null) {
			getView().getTxtARGParamStructureFile().setHelper(notifStructFile);
			return !notifStructFile.isError();
		}

		return true;
	}

	/**
	 * @param argParameters the arg parameters
	 * @return null if the value of the output folder is valid, otherwise return the
	 *         associated notification.
	 */
	public Notification checkARGParamOutput(ARGParameters argParameters) {

		logger.debug("Check ARG output"); //$NON-NLS-1$

		if (argParameters == null) {
			return NotificationFactory
					.getNewError(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_NULL));
		}

		// if is null or blank
		if (StringUtils.isBlank(argParameters.getOutput())) {
			return NotificationFactory
					.getNewError(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_OUTPUT_EMPTY));
		}

		// resolve variable if exists
		String pathResolved = RscTools.empty();
		try {
			pathResolved = CFVariableResolver.resolveAll(argParameters.getOutput());
		} catch (CredibilityException e) {
			return NotificationFactory.getNewError(e.getMessage());
		}

		File outputFolder = FileTools.findFileInWorkspaceOrSystem(new File(pathResolved).getPath());

		// check is directory
		if (outputFolder != null && outputFolder.exists() && outputFolder.isFile()) {
			return NotificationFactory.getNewError(RscTools.getString(
					RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_OUTPUT_NOTDIRECTORY, argParameters.getOutput()));
		}

		// check output folder exists and is not null
		if (outputFolder == null || !outputFolder.exists()) {
			return NotificationFactory.getNewError(RscTools.getString(
					RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_OUTPUT_NOTEXIST, argParameters.getOutput()));
		}

		return null;
	}

	/**
	 * Check ARG output file path validity
	 * 
	 * @return true if the ARG output file path is valid, otherwise false.
	 */
	public boolean validateArgOutputPath() {

		Notification notifOutput = checkARGParamOutput(argParameters);
		if (notifOutput != null) {
			getView().getTxtARGParamOutput().setHelper(notifOutput);
			return !notifOutput.isError();
		}

		return true;
	}

	/**
	 * @param argParameters the arg parameters
	 * @return null if the value of the custom ending file path is valid, otherwise
	 *         return the associated notification.
	 */
	public Notification checkARGCustomEnding(ARGParameters argParameters) {

		logger.debug("Check ARG custom ending structure file"); //$NON-NLS-1$

		if (argParameters == null) {
			return NotificationFactory
					.getNewError(RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_NULL));
		}

		// if disabled do not check
		if (!Boolean.TRUE.equals(argParameters.getCustomEndingEnabled())) {
			return null;
		}

		// resolve variable if exists
		String pathResolved = RscTools.empty();
		try {
			pathResolved = CFVariableResolver.resolveAll(argParameters.getCustomEndingFilePath());
		} catch (CredibilityException e) {
			return NotificationFactory.getNewError(e.getMessage());
		}

		if (pathResolved == null || pathResolved.isBlank()) {
			return NotificationFactory.getNewError(RscTools.getString(
					RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_CUSTOMENDINGFILE_NOTEXIST, RscTools.empty()));
		}

		// check is a file
		File structureFile = new File(pathResolved);

		if (!structureFile.exists()) {
			return NotificationFactory.getNewError(
					RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_CUSTOMENDINGFILE_NOTEXIST,
							argParameters.getCustomEndingFilePath()));
		}

		if (structureFile.isDirectory()) {
			return NotificationFactory.getNewError(
					RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_CUSTOMENDINGFILE_NOTFILE,
							argParameters.getCustomEndingFilePath()));
		}

		return null;
	}

	/**
	 * Check ARG custom ending file path validity
	 * 
	 * @return true if the ARG custom ending file path is valid, otherwise false.
	 */
	public boolean validateArgCustomEnding() {

		Notification notifOutput = checkARGCustomEnding(argParameters);
		if (notifOutput != null) {
			getView().getTxtCustomEndingFilePath().setHelper(notifOutput);
			return !notifOutput.isError();
		}

		return true;
	}

	/**
	 * @return the ARG backend and report types populated with current ARG
	 *         installation
	 */
	ARGType getARGTypes() {
		return argTypes;
	}

	/**
	 * Gets the ARG parameters.
	 *
	 * @return the ARG parameters
	 */
	ARGParameters getARGParameters() {
		return argParameters;
	}

	/**
	 * Do browse export file for a specific widget
	 * 
	 * @param textWidget the text widget
	 * @param title      the browse dialog title
	 */
	void browseIntoWorkspace(TextWidget textWidget, String title) {

		if (textWidget == null) {
			return;
		}

		NewFileTreeSelectionDialog dialog = FormFactory.getNewResourceTreeDialog(getViewManager().getRscMgr());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setTitle(title);
		if (!StringUtils.isBlank(textWidget.getValue())) {
			IResource rsc = WorkspaceTools
					.getFileInWorkspaceForPath(new Path(CFVariableResolver.removeAll(textWidget.getValue())));

			if (rsc == null || !rsc.exists()) {
				rsc = WorkspaceTools
						.getResourceInWorkspaceForPath(getViewManager().getCredibilityEditor().getCfProjectPath());
			}

			if (rsc != null) {
				dialog.setInitialSelection(rsc);
			}
		}
		dialog.setAllowMultiple(false);

		if (dialog.open() == Window.OK) {
			IResource resource = (IResource) dialog.getFirstResult();
			textWidget.setValue(FileTools.prefixWorkspaceVar(resource.getFullPath().toString()));
			textWidget.notifyListeners(SWT.KeyUp, new Event());
			textWidget.notifyListeners(SWT.Selection, new Event());
		}
	}

	/**
	 * Generate ARG Report with progress dialog
	 */
	void generateReport() {

		// validate ARG parameters
		getView().clearHelpers();

		boolean valid = validateArgSetupExecutable();
		valid &= validateArgParametersFilePath();
		valid &= validateArgStructureFilePath();
		valid &= validateArgOutputPath();
		valid &= validateArgCustomEnding();

		if (!valid) {
			return;
		}

		// ARG console log
		StringBuilder errorLog = new StringBuilder();
		StringBuilder consoleLog = new StringBuilder();

		if (getView().getTxtConsole() != null) {
			consoleLog.append(getView().getTxtConsole().getTextWidget().getText());
		}

		try {
			final IViewManager viewManager = getViewManager();
			final ARGParameters argParametersForExecution = computeARGParametersForExecution(argParameters);
			final Map<ExportOptions, Object> userSelection = getUserSelectionOptions();

			if (argParametersForExecution == null) {
				throw new CredibilityException(
						RscTools.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_NULL));
			}

			String backendType = !StringUtils.isBlank(argParametersForExecution.getBackendType())
					? argParametersForExecution.getBackendType()
					: RscTools.getString(RscConst.MSG_OBJECT_NULL);
			String filename = !StringUtils.isBlank(argParametersForExecution.getFilename())
					? argParametersForExecution.getFilename()
					: RscTools.getString(RscConst.MSG_OBJECT_NULL);

			// execute asynchronously to keep fluid UI
			final Display display = Display.getCurrent();
			Job job = Job.create(
					RscTools.getString(RscConst.MSG_REPORTVIEW_GENERATE_REPORT_JOB_TITLE, backendType, filename),
					monitor -> {
						try {
							new GenerateARGReportRunnable(viewManager, argParametersForExecution, userSelection,
									errorLog, consoleLog).run(monitor);

							// write log in console
							display.asyncExec(() -> {
								if (getView().getTxtConsole() != null) {
									getView().getTxtConsole().getTextWidget().setText(consoleLog.toString());
									getView().getTxtConsole()
											.setTopIndex(getView().getTxtConsole().getTextWidget().getLineCount() - 1);
								}
							});

							// if job is cancelled
							return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;

						} catch (InvocationTargetException e) {
							logger.error(e.getMessage());
							MessageDialog.openError(Display.getCurrent().getActiveShell(),
									RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE), e.getMessage());
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							logger.error(e.getMessage());
							MessageDialog.openError(Display.getCurrent().getActiveShell(),
									RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE), e.getMessage());
						}

						// this return statement is only accessed when an exception is triggered
						return Status.CANCEL_STATUS;
					});
			job.setUser(true);
			job.schedule();

		} catch (CredibilityException e) {
			logger.error(e.getMessage());
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE), e.getMessage());
		}
	}

	/**
	 * Open local ARG Setup preferences.
	 *
	 * @return true, if successful
	 */
	boolean openPreferences() {
		boolean done = false;
		PreferenceDialog pref = PrefTools.getCFPrefDialog(getView().getShell());
		if (pref != null) {
			int open = pref.open();
			done = (open == Window.OK);

			// reload the ARG setup and parameters
			reloadARGTypesJob();
			reloadARGVersionJob();
		}
		return done;
	}

	/**
	 * update if the value of the txtArgSetupExecutable changed and set
	 * argParameters arg executable, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedARGSetupExecutable(String value) {

		if (argParameters == null || value == null || value.equals(argParameters.getArgExecPath())) {
			return;
		}

		// update
		argParameters.setArgExecPath(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();

		// reload ARG types
		reloadARGTypesJob();
		reloadARGVersionJob();
	}

	/**
	 * update if the value of the txtARGParamParametersFile changed and set
	 * argParameters parameters file, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedARGSetupPreScript(String value) {

		if (argParameters == null || value == null || value.equals(argParameters.getArgPreScript())) {
			return;
		}

		// update
		argParameters.setArgPreScript(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();
	}

	/**
	 * update if the value of the chboxUseARGLocalConf changed and set argParameters
	 * use ARG local configuration.
	 *
	 * @param value the value
	 */
	void changedARGSetupUseLocalConf(boolean value) {

		if (argParameters == null) {
			return;
		}

		if (argParameters.getUseArgLocalConf() == null
				|| (value != argParameters.getUseArgLocalConf().booleanValue())) {
			argParameters.setUseArgLocalConf(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the txtARGParamParametersFile changed and set
	 * argParameters parameters file, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedARGParametersFile(String value) {

		if (argParameters == null || value == null || value.equals(argParameters.getParametersFilePath())) {
			return;
		}

		// update
		argParameters.setParametersFilePath(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();
	}

	/**
	 * update if the value of the txtARGParamStructureFile changed and set
	 * argParameters structure file, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedARGStructureFile(String value) {

		if (argParameters == null || value == null || value.equals(argParameters.getStructureFilePath())) {
			return;
		}

		// update
		argParameters.setStructureFilePath(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();
	}

	/**
	 * update if the value of the txtARGParamOutput changed and set argParameters
	 * output, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedARGParamOutput(String value) {
		if (argParameters == null || value == null || value.equals(argParameters.getOutput())) {
			return;
		}

		// update
		argParameters.setOutput(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();
	}

	/**
	 * update if the value of the txtARGParamFilename changed and set argParameters
	 * filename, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedARGParamFilename(String value) {

		if (argParameters == null || value == null || value.equals(argParameters.getFilename())) {
			return;
		}

		// update
		argParameters.setFilename(value);
		updateARGParameters();
	}

	/**
	 * update if the value of the txtARGParamReportTitle changed and set
	 * argParameters report title, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedARGParamReportTitle(String value) {

		if (argParameters == null || value == null || value.equals(argParameters.getTitle())) {
			return;
		}

		// update
		argParameters.setTitle(value);
		updateARGParameters();
	}

	/**
	 * update if the value of the txtARGParamAuthor changed and set argParameters
	 * report author, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedARGParamAuthor(String value) {

		if (argParameters == null || value == null || value.equals(argParameters.getAuthor())) {
			return;
		}

		// update
		argParameters.setAuthor(value);
		updateARGParameters();
	}

	/**
	 * update if the value of the cbxARGParamReportType changed and set
	 * argParameters report type, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedARGParamReportType(String value) {

		if (argParameters == null || value == null || value.equals(argParameters.getReportType())) {
			return;
		}

		// update
		argParameters.setReportType(value);
		updateARGParameters();
	}

	/**
	 * update if the value of the cbxARGParamBackendType changed and set
	 * argParameters backend type, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedARGParamBackendType(String value) {

		if (argParameters == null || value == null || value.equals(argParameters.getBackendType())) {
			return;
		}

		// update
		argParameters.setBackendType(value);
		updateARGParameters();

		// enable/disable inlining
		getView().refreshWordInlining();
	}

	/**
	 * update if the value of the inline word document option changed, otherwise do
	 * nothing.
	 *
	 * @param value the value
	 */
	void changedARGParamInlineWordDoc(boolean value) {

		if (argParameters == null) {
			return;
		}

		// update
		if (argParameters.getInlineWordDoc() == null || (value != argParameters.getInlineWordDoc().booleanValue())) {
			argParameters.setInlineWordDoc(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the planning option changed.
	 *
	 * @param value the value
	 */
	void changedPlanningEnabledOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		// Planning
		if (argParameters.getPlanningEnabled() == null
				|| (value != argParameters.getPlanningEnabled().booleanValue())) {
			argParameters.setPlanningEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the Planning Intended Purpose option changed.
	 *
	 * @param value the value
	 */
	void changedPlanningIntendedPurposeOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		// Planning Intended Purpose
		if (argParameters.getPlanningIntendedPurposeEnabled() == null
				|| (value != argParameters.getPlanningIntendedPurposeEnabled().booleanValue())) {
			argParameters.setPlanningIntendedPurposeEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the Planning System Requirement option changed.
	 *
	 * @param value the value
	 */
	void changedPlanningSysRequirementOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		// Planning System Requirement
		if (argParameters.getPlanningSysReqEnabled() == null
				|| (value != argParameters.getPlanningSysReqEnabled().booleanValue())) {
			argParameters.setPlanningSysReqEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the Planning QoI Planner option changed.
	 *
	 * @param value the value
	 */
	void changedPlanningQoIPlannerOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		// Planning QoI Planner
		if (argParameters.getPlanningQoIPlannerEnabled() == null
				|| (value != argParameters.getPlanningQoIPlannerEnabled().booleanValue())) {
			argParameters.setPlanningQoIPlannerEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the Planning Uncertainty option changed.
	 *
	 * @param value the value
	 */
	void changedPlanningUncertaintyOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		// Planning Uncertainty
		if (argParameters.getPlanningUncertaintyEnabled() == null
				|| (value != argParameters.getPlanningUncertaintyEnabled().booleanValue())) {
			argParameters.setPlanningUncertaintyEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the Planning Decision option changed.
	 *
	 * @param value the value
	 */
	void changedPlanningDecisionOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		// Planning Decision
		if (argParameters.getPlanningDecisionEnabled() == null
				|| (value != argParameters.getPlanningDecisionEnabled().booleanValue())) {
			argParameters.setPlanningDecisionEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PIRT options changed.
	 *
	 * @param value the value
	 */
	void changedPIRTEnabledOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		// PIRT
		if (argParameters.getPirtEnabled() == null || (value != argParameters.getPirtEnabled().booleanValue())) {
			argParameters.setPirtEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the QoI options changed.
	 *
	 * @param qoi     the qoi
	 * @param tag     the tag
	 * @param enabled the enabled
	 */
	void changedQoISelected(QuantityOfInterest qoi, QuantityOfInterest tag, boolean enabled) {

		if (qoi == null) {
			return;
		}

		// if option list is null
		if (argParameters.getQoiSelectedList() == null || argParameters.getQoiSelectedList().isEmpty()) {
			List<ARGParametersQoIOption> options = new ArrayList<>();
			ARGParametersQoIOption option = new ARGParametersQoIOption();
			option.setArgParameter(argParameters);
			option.setQoi(qoi);
			option.setTag(tag);
			option.setEnabled(enabled);
			options.add(option);
			argParameters.setQoiSelectedList(options);

			updateARGParameters();

			return;
		}

		boolean changed = false;
		boolean found = false;

		for (ARGParametersQoIOption opt : argParameters.getQoiSelectedList()) {
			// if found try to update
			if (opt != null && qoi.equals(opt.getQoi())) {
				found = true;
				if (opt.getEnabled() == null || enabled != opt.getEnabled().booleanValue()) {
					opt.setEnabled(enabled);
					changed = true;
				}
				if ((tag == null && opt.getTag() != null) || (tag != null && opt.getTag() == null)
						|| (tag != null && !tag.equals(opt.getTag()))) {
					opt.setTag(tag);
					changed = true;
				}
				break;
			}
		}

		// if not found create it
		if (!found) {
			ARGParametersQoIOption option = new ARGParametersQoIOption();
			option.setArgParameter(argParameters);
			option.setQoi(qoi);
			option.setTag(tag);
			option.setEnabled(enabled);
			argParameters.getQoiSelectedList().add(option);

			changed = true;
		}

		if (changed) {
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PCMM option changed.
	 *
	 * @param value the value
	 */
	void changedPCMMEnabledOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		if (argParameters.getPcmmEnabled() == null || (value != argParameters.getPcmmEnabled().booleanValue())) {
			argParameters.setPcmmEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PCMM Planning option changed.
	 *
	 * @param value the value
	 */
	void changedPCMMPlanningEnabledOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		if (argParameters.getPcmmPlanningEnabled() == null
				|| (value != argParameters.getPcmmPlanningEnabled().booleanValue())) {
			argParameters.setPcmmPlanningEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PCMM Evidence option changed.
	 *
	 * @param value the value
	 */
	void changedPCMMEvidenceEnabledOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		if (argParameters.getPcmmEvidenceEnabled() == null
				|| (value != argParameters.getPcmmEvidenceEnabled().booleanValue())) {
			argParameters.setPcmmEvidenceEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PCMM Assessment option changed.
	 *
	 * @param value the value
	 */
	void changedPCMMAssessmentEnabledOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		if (argParameters.getPcmmAssessmentEnabled() == null
				|| (value != argParameters.getPcmmAssessmentEnabled().booleanValue())) {
			argParameters.setPcmmAssessmentEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PCMM tag selection changed.
	 *
	 * @param tagSelected the tag selected
	 */
	void changedPCMMTagSelected(Tag tagSelected) {

		if (argParameters == null) {
			return;
		}

		boolean changed = false;

		// persisted tag is not null and selected tag is null
		if (argParameters.getPcmmTagSelected() != null && (tagSelected == null || tagSelected.getId() == null)) {
			argParameters.setPcmmTagSelected(null);
			changed = true;
		}

		// persisted tag is null and selected tag is not null
		if (argParameters.getPcmmTagSelected() == null && tagSelected != null && tagSelected.getId() != null) {
			argParameters.setPcmmTagSelected(tagSelected);
			changed = true;
		}

		// persisted tag is not null and selected tag is not null -> compare
		if (argParameters.getPcmmTagSelected() != null && tagSelected != null && tagSelected.getId() != null
				&& !tagSelected.equals(argParameters.getPcmmTagSelected())) {
			argParameters.setPcmmTagSelected(tagSelected);
			changed = true;
		}

		if (changed) {
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the textCustomEndingFilePath changed and set
	 * argParameters structure file, otherwise do nothing.
	 *
	 * @param value the value
	 */
	void changedARGCustomEndingFile(String value) {

		if (argParameters == null || value == null || value.equals(argParameters.getCustomEndingFilePath())) {
			return;
		}

		// update
		argParameters.setCustomEndingFilePath(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();
	}

	/**
	 * update if the value of the ARG custom ending options changed.
	 *
	 * @param value the value
	 */
	void changedARGCustomEndingEnabledOption(boolean value) {

		if (argParameters == null) {
			return;
		}

		// PIRT
		if (argParameters.getCustomEndingEnabled() == null
				|| (value != argParameters.getCustomEndingEnabled().booleanValue())) {
			argParameters.setCustomEndingEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * Update the ARG parameters
	 */
	void updateARGParameters() {

		if (argParameters != null) {
			try {
				// update arg parameters
				argParameters = getViewManager().getAppManager().getService(IReportARGExecutionApp.class)
						.updateARGParameters(argParameters);

				// set save state
				getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error("An error occured while updating the ARG parameters", e); //$NON-NLS-1$
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
						e.getMessage());
			}
		}
	}

	/**
	 * @param argParameters the arg parameters to copy and resolve
	 * @return the arg parameters for execution environment
	 * @throws CredibilityException
	 */
	ARGParameters computeARGParametersForExecution(ARGParameters argParameters) throws CredibilityException {
		if (argParameters == null) {
			return null;
		}

		// copy the arg parameters to do not change the database settings
		ARGParameters copy = argParameters.copy();

		// set execution settings
		if (Boolean.TRUE.equals(copy.getUseArgLocalConf())) {
			copy.setArgExecPath(PrefTools.getARGExecutablePath());
			copy.setArgPreScript(PrefTools.getARGSetEnvScriptPath());
		}

		// resolve CF variables while we are in the main process and GUI
		copy.setOutput(CFVariableResolver.resolveAll(argParameters.getOutput()));
		copy.setParametersFilePath(CFVariableResolver.resolveAll(argParameters.getParametersFilePath()));
		copy.setStructureFilePath(CFVariableResolver.resolveAll(argParameters.getStructureFilePath()));
		copy.setFilename(CFVariableResolver.resolveAll(argParameters.getFilename()));
		copy.setTitle(CFVariableResolver.resolveAll(argParameters.getTitle()));
		copy.setNumber(CFVariableResolver.resolveAll(argParameters.getNumber()));
		copy.setAuthor(CFVariableResolver.resolveAll(argParameters.getAuthor()));
		copy.setCustomEndingFilePath(CFVariableResolver.resolveAll(argParameters.getCustomEndingFilePath()));

		// set inlining
		boolean enableInlining = PrefTools.getPreferenceBoolean(PrefTools.DEVOPTS_REPORT_INLINEWORD_KEY);
		copy.setInlineWordDoc(enableInlining && Boolean.TRUE.equals(copy.getInlineWordDoc()));

		return copy;
	}

	/**
	 * @return the ARG options depending of the user selection
	 * @throws CredibilityException if an error occurs
	 */
	Map<ExportOptions, Object> getUserSelectionOptions() throws CredibilityException {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);
		Model model = getViewManager().getCache().getModel();
		options.put(ExportOptions.MODEL, model);

		getARGOptionsPlanning(options, model);

		getARGOptionsPIRT(options);

		getARGOptionsPCMM(options, model);

		getARGOptionsCustomEnding(options);

		return options;
	}

	/**
	 * Put in the options map the Planning data according to the user selection.
	 * 
	 * @param options the report options map
	 * @param model   the CF model
	 * @throws CredibilityException if an error occurs
	 */
	private void getARGOptionsPlanning(Map<ExportOptions, Object> options, Model model) throws CredibilityException {

		// PLANNING - Get generation parameters
		options.put(ExportOptions.PLANNING_INCLUDE, getView().getChboxPlanning().getSelection());
		options.put(ExportOptions.INTENDEDPURPOSE_INCLUDE, getView().getChboxPlanningIntendedPurpose().getSelection());
		options.put(ExportOptions.SYSTEM_REQUIREMENT_INCLUDE,
				getView().getChboxPlanningSystemRequirement().getSelection());
		options.put(ExportOptions.QOI_PLANNER_INCLUDE, getView().getChboxPlanningQoIPlanner().getSelection());
		options.put(ExportOptions.PLANNING_UNCERTAINTY_INCLUDE, getView().getChboxPlanningUncertainty().getSelection());
		options.put(ExportOptions.DECISION_INCLUDE, getView().getChboxPlanningDecision().getSelection());

		// PLANNING section
		options.put(ExportOptions.INTENDED_PURPOSE,
				getViewManager().getAppManager().getService(IIntendedPurposeApp.class).get(model));
		options.put(ExportOptions.SYSTEM_REQUIREMENT_LIST, getViewManager().getAppManager()
				.getService(ISystemRequirementApplication.class).getRequirementWithChildrenByModel(model));
		options.put(ExportOptions.PLANNING_UNCERTAINTIES, getViewManager().getAppManager()
				.getService(IUncertaintyApplication.class).getUncertaintyGroupByModel(model));
		options.put(ExportOptions.DECISION_LIST,
				getViewManager().getAppManager().getService(IDecisionApplication.class).getDecisionRootByModel(model));
	}

	/**
	 * Put in the options map the PIRT data according to the user selection.
	 * 
	 * @param options the report options map
	 */
	private void getARGOptionsPIRT(Map<ExportOptions, Object> options) {

		// PIRT- Get generation parameters
		options.put(ExportOptions.PIRT_INCLUDE, getView().getChboxPirt().getSelection());
		options.put(ExportOptions.PIRT_SPECIFICATION, getViewManager().getCache().getPIRTSpecification());

		Map<QuantityOfInterest, Map<ExportOptions, Object>> pirtQoIDataList = new HashMap<>();
		if (argParameters != null && argParameters.getQoiSelectedList() != null) {
			argParameters.getQoiSelectedList().stream().filter(Objects::nonNull).forEach(qoi -> {
				Map<ExportOptions, Object> qoiData = new EnumMap<>(ExportOptions.class);
				qoiData.put(ExportOptions.PIRT_QOI_INCLUDE, qoi.getEnabled());
				qoiData.put(ExportOptions.PIRT_QOI_TAG, qoi.getTag());
				pirtQoIDataList.put(qoi.getQoi(), qoiData);
			});
		}
		options.put(ExportOptions.PIRT_QOI_LIST, pirtQoIDataList);
	}

	/**
	 * Put in the options map the PCMM data according to the user selection.
	 * 
	 * @param options the report options map
	 * @param model   the CF model
	 */
	private void getARGOptionsPCMM(Map<ExportOptions, Object> options, Model model) {

		// PCMM - Get generation parameters
		Tag pcmmTag = getView().getCbxSelection(Tag.class, getView().getCbxPcmmTag());
		if (pcmmTag == null || pcmmTag.getId() == null) {
			pcmmTag = null;
		}

		// PCMM - Options
		PCMMMode mode = null;
		PCMMSpecification pcmmConfig = getViewManager().getPCMMConfiguration();

		if (pcmmConfig != null) {

			// Report options
			mode = pcmmConfig.getMode();
			options.put(ExportOptions.PCMM_INCLUDE, getView().getChboxPcmm().getSelection());
			options.put(ExportOptions.PCMM_MODE, mode);
			options.put(ExportOptions.PCMM_TAG, pcmmTag);
			options.put(ExportOptions.PCMM_PLANNING_INCLUDE, getView().getChboxPcmmPlanning().getSelection());
			options.put(ExportOptions.PCMM_EVIDENCE_INCLUDE, getView().getChboxPcmmEvidence().getSelection());
			options.put(ExportOptions.PCMM_ASSESSMENT_INCLUDE, getView().getChboxPcmmAssessment().getSelection());

			// Get planning parameters
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(GenericParameter.Filter.PARENT, null);
			List<PCMMPlanningParam> planningParameters = getViewManager().getAppManager()
					.getService(IPCMMPlanningApplication.class).getPlanningFieldsBy(filters);
			options.put(ExportOptions.PCMM_PLANNING_PARAMETERS, planningParameters);

			// Get planning questions & values
			Map<PCMMElement, List<PCMMPlanningQuestion>> pcmmPlanningQuestions = new HashMap<>();
			Map<PCMMElement, List<PCMMPlanningQuestionValue>> pcmmPlanningQuestionValues = new HashMap<>();
			Map<PCMMElement, List<PCMMPlanningValue>> pcmmPlanningValues = new HashMap<>();
			Map<PCMMElement, List<PCMMEvidence>> pcmmEvidences = new HashMap<>();
			Map<PCMMElement, List<PCMMAssessment>> pcmmAssessments = new HashMap<>();

			try {
				// PCMM Elements
				List<PCMMElement> pcmmElements = getViewManager().getAppManager().getService(IPCMMApplication.class)
						.getElementList(model);
				options.put(ExportOptions.PCMM_ELEMENTS, pcmmElements);

				// PCMM Data
				for (PCMMElement pcmmElement : pcmmElements) {

					// Planning Questions
					pcmmPlanningQuestions.put(pcmmElement,
							getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
									.getPlanningQuestionsByElement(pcmmElement, mode));

					// Planning Question Values
					pcmmPlanningQuestionValues.put(pcmmElement,
							getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
									.getPlanningQuestionsValueByElement(pcmmElement, mode, pcmmTag));

					// Planning Parameter values
					pcmmPlanningValues.put(pcmmElement,
							getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
									.getPlanningValueByElement(pcmmElement, mode, pcmmTag));

					// Evidence
					pcmmEvidences.put(pcmmElement, getViewManager().getAppManager().getService(IPCMMEvidenceApp.class)
							.getEvidenceByTag(pcmmTag));

					// Assessments
					pcmmAssessments.put(pcmmElement, getViewManager().getAppManager()
							.getService(IPCMMAssessmentApp.class).getAssessmentByTag(pcmmTag));
				}

				// Planning
				if (getView().getChboxPcmmPlanning().getSelection()) {
					options.put(ExportOptions.PCMM_PLANNING_QUESTIONS, pcmmPlanningQuestions);
					options.put(ExportOptions.PCMM_PLANNING_QUESTION_VALUES, pcmmPlanningQuestionValues);
					options.put(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES, pcmmPlanningValues);
				}

				// Evidence
				if (getView().getChboxPcmmEvidence().getSelection()) {
					options.put(ExportOptions.PCMM_EVIDENCE_LIST, pcmmEvidences);
				}

				// Assessments
				if (getView().getChboxPcmmAssessment().getSelection()) {
					options.put(ExportOptions.PCMM_ASSESSMENT_LIST, pcmmAssessments);
				}

			} catch (CredibilityException e) {
				logger.error(e.getMessage());
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
						e.getMessage());
			}
		}
	}

	/**
	 * Put in the options map the Custom ending data according to the user
	 * selection.
	 * 
	 * @param options the report options map
	 */
	private void getARGOptionsCustomEnding(Map<ExportOptions, Object> options) {
		options.put(ExportOptions.CUSTOM_ENDING_INCLUDE, getView().getChboxCustomEnding().getSelection());
	}

}
