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
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
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
public class ReportViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReportViewController.class);

	/**
	 * The view
	 */
	private ReportView view;

	ReportViewController(ReportView view) {
		Assert.isNotNull(view);
		this.view = view;
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

		Notification notifArgSetupExecutable = checkArgSetupExecutable(view.getTxtArgSetupExecutable().getValue());
		if (notifArgSetupExecutable != null) {
			view.getTxtArgSetupExecutable().setHelper(notifArgSetupExecutable);
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

		Notification notifParamFile = checkARGParametersFile(view.getArgParameters());
		if (notifParamFile != null) {
			view.getTxtARGParamParametersFile().setHelper(notifParamFile);
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

		Notification notifStructFile = checkARGStructureFile(view.getArgParameters());
		if (notifStructFile != null) {
			view.getTxtARGParamStructureFile().setHelper(notifStructFile);
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

		Notification notifOutput = checkARGParamOutput(view.getArgParameters());
		if (notifOutput != null) {
			view.getTxtARGParamOutput().setHelper(notifOutput);
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

		Notification notifOutput = checkARGCustomEnding(view.getArgParameters());
		if (notifOutput != null) {
			view.getTxtCustomEndingFilePath().setHelper(notifOutput);
			return !notifOutput.isError();
		}

		return true;
	}

	/**
	 * @return the ARG backend and report types populated with current ARG
	 *         installation
	 */
	ARGType getARGTypes() {

		ARGType argType = null;

		try {

			StringBuilder consoleLog = new StringBuilder(view.getTxtConsole().getTextWidget().getText());
			argType = view.getViewManager().getAppManager().getService(IReportARGExecutionApp.class)
					.getARGTypes(computeARGParametersForExecution(view.getArgParameters()), consoleLog, consoleLog);
			view.getTxtConsole().getTextWidget().setText(consoleLog.toString());
			view.getTxtConsole().setTopIndex(view.getTxtConsole().getTextWidget().getLineCount() - 1);

		} catch (CredibilityException e) {
			logger.warn(e.getMessage());
		}

		return argType;
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

		NewFileTreeSelectionDialog dialog = FormFactory.getNewResourceTreeDialog(view.getViewManager().getRscMgr());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setTitle(title);
		if (!StringUtils.isBlank(textWidget.getValue())) {
			IResource rsc = WorkspaceTools
					.getFileInWorkspaceForPath(new Path(CFVariableResolver.removeAll(textWidget.getValue())));

			if (rsc == null || !rsc.exists()) {
				rsc = WorkspaceTools
						.getResourceInWorkspaceForPath(view.getViewManager().getCredibilityEditor().getCfProjectPath());
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
		view.clearHelpers();

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

		if (view.getTxtConsole() != null) {
			consoleLog.append(view.getTxtConsole().getTextWidget().getText());
		}

		// launch generation process
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(view.getShell());
		try {
			progressDialog.run(true, false,
					new GenerateARGReportRunnable(view.getViewManager(),
							computeARGParametersForExecution(view.getArgParameters()), getUserSelectionOptions(),
							errorLog, consoleLog));
		} catch (CredibilityException | InvocationTargetException e) {
			logger.error(e.getMessage());
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE), e.getMessage());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.error(e.getMessage());
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE), e.getMessage());
		}
		progressDialog.close();

		// write log in console
		if (view.getTxtConsole() != null) {
			view.getTxtConsole().getTextWidget().setText(consoleLog.toString());
			view.getTxtConsole().setTopIndex(view.getTxtConsole().getTextWidget().getLineCount() - 1);
		}
	}

	/**
	 * update if the value of the txtArgSetupExecutable changed and set
	 * argParameters arg executable, otherwise do nothing.
	 */
	void changedARGSetupExecutable(String value) {

		if (view.getArgParameters() == null || value == null
				|| value.equals(view.getArgParameters().getArgExecPath())) {
			return;
		}

		// update
		view.getArgParameters().setArgExecPath(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();

		// reload ARG types
		view.reloadARGTypes();

		// reload ARG Parameters
		view.reloadARGParameters();
	}

	/**
	 * update if the value of the txtARGParamParametersFile changed and set
	 * argParameters parameters file, otherwise do nothing.
	 */
	void changedARGSetupPreScript(String value) {

		if (view.getArgParameters() == null || value == null
				|| value.equals(view.getArgParameters().getArgPreScript())) {
			return;
		}

		// update
		view.getArgParameters().setArgPreScript(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();
	}

	/**
	 * update if the value of the chboxUseARGLocalConf changed and set argParameters
	 * use ARG local configuration
	 */
	void changedARGSetupUseLocalConf(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		if (view.getArgParameters().getUseArgLocalConf() == null
				|| (value != view.getArgParameters().getUseArgLocalConf().booleanValue())) {
			view.getArgParameters().setUseArgLocalConf(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the txtARGParamParametersFile changed and set
	 * argParameters parameters file, otherwise do nothing.
	 */
	void changedARGParametersFile(String value) {

		if (view.getArgParameters() == null || value == null
				|| value.equals(view.getArgParameters().getParametersFilePath())) {
			return;
		}

		// update
		view.getArgParameters().setParametersFilePath(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();
	}

	/**
	 * update if the value of the txtARGParamStructureFile changed and set
	 * argParameters structure file, otherwise do nothing.
	 */
	void changedARGStructureFile(String value) {

		if (view.getArgParameters() == null || value == null
				|| value.equals(view.getArgParameters().getStructureFilePath())) {
			return;
		}

		// update
		view.getArgParameters().setStructureFilePath(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();
	}

	/**
	 * update if the value of the txtARGParamOutput changed and set argParameters
	 * output, otherwise do nothing.
	 */
	void changedARGParamOutput(String value) {
		if (view.getArgParameters() == null || value == null || value.equals(view.getArgParameters().getOutput())) {
			return;
		}

		// update
		view.getArgParameters().setOutput(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();
	}

	/**
	 * update if the value of the txtARGParamFilename changed and set argParameters
	 * filename, otherwise do nothing.
	 */
	void changedARGParamFilename(String value) {

		if (view.getArgParameters() == null || value == null || value.equals(view.getArgParameters().getFilename())) {
			return;
		}

		// update
		view.getArgParameters().setFilename(value);
		updateARGParameters();
	}

	/**
	 * update if the value of the txtARGParamReportTitle changed and set
	 * argParameters report title, otherwise do nothing.
	 */
	void changedARGParamReportTitle(String value) {

		if (view.getArgParameters() == null || value == null || value.equals(view.getArgParameters().getTitle())) {
			return;
		}

		// update
		view.getArgParameters().setTitle(value);
		updateARGParameters();
	}

	/**
	 * update if the value of the txtARGParamAuthor changed and set argParameters
	 * report author, otherwise do nothing.
	 */
	void changedARGParamAuthor(String value) {

		if (view.getArgParameters() == null || value == null || value.equals(view.getArgParameters().getAuthor())) {
			return;
		}

		// update
		view.getArgParameters().setAuthor(value);
		updateARGParameters();
	}

	/**
	 * update if the value of the cbxARGParamReportType changed and set
	 * argParameters report type, otherwise do nothing.
	 */
	void changedARGParamReportType(String value) {

		if (view.getArgParameters() == null || value == null || value.equals(view.getArgParameters().getReportType())) {
			return;
		}

		// update
		view.getArgParameters().setReportType(value);
		updateARGParameters();
	}

	/**
	 * update if the value of the cbxARGParamBackendType changed and set
	 * argParameters backend type, otherwise do nothing.
	 */
	void changedARGParamBackendType(String value) {

		if (view.getArgParameters() == null || value == null
				|| value.equals(view.getArgParameters().getBackendType())) {
			return;
		}

		// update
		view.getArgParameters().setBackendType(value);
		updateARGParameters();

		// enable/disable inlining
		view.reloadWordInlining();
	}

	/**
	 * update if the value of the inline word document option changed, otherwise do
	 * nothing.
	 */
	void changedARGParamInlineWordDoc(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		// update
		if (view.getArgParameters().getInlineWordDoc() == null
				|| (value != view.getArgParameters().getInlineWordDoc().booleanValue())) {
			view.getArgParameters().setInlineWordDoc(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the planning option changed
	 */
	void changedPlanningEnabledOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		// Planning
		if (view.getArgParameters().getPlanningEnabled() == null
				|| (value != view.getArgParameters().getPlanningEnabled().booleanValue())) {
			view.getArgParameters().setPlanningEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the Planning Intended Purpose option changed
	 */
	void changedPlanningIntendedPurposeOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		// Planning Intended Purpose
		if (view.getArgParameters().getPlanningIntendedPurposeEnabled() == null
				|| (value != view.getArgParameters().getPlanningIntendedPurposeEnabled().booleanValue())) {
			view.getArgParameters().setPlanningIntendedPurposeEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the Planning System Requirement option changed
	 */
	void changedPlanningSysRequirementOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		// Planning System Requirement
		if (view.getArgParameters().getPlanningSysReqEnabled() == null
				|| (value != view.getArgParameters().getPlanningSysReqEnabled().booleanValue())) {
			view.getArgParameters().setPlanningSysReqEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the Planning QoI Planner option changed
	 */
	void changedPlanningQoIPlannerOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		// Planning QoI Planner
		if (view.getArgParameters().getPlanningQoIPlannerEnabled() == null
				|| (value != view.getArgParameters().getPlanningQoIPlannerEnabled().booleanValue())) {
			view.getArgParameters().setPlanningQoIPlannerEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the Planning Uncertainty option changed
	 */
	void changedPlanningUncertaintyOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		// Planning Uncertainty
		if (view.getArgParameters().getPlanningUncertaintyEnabled() == null
				|| (value != view.getArgParameters().getPlanningUncertaintyEnabled().booleanValue())) {
			view.getArgParameters().setPlanningUncertaintyEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the Planning Decision option changed
	 */
	void changedPlanningDecisionOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		// Planning Decision
		if (view.getArgParameters().getPlanningDecisionEnabled() == null
				|| (value != view.getArgParameters().getPlanningDecisionEnabled().booleanValue())) {
			view.getArgParameters().setPlanningDecisionEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PIRT options changed
	 */
	void changedPIRTEnabledOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		// PIRT
		if (view.getArgParameters().getPirtEnabled() == null
				|| (value != view.getArgParameters().getPirtEnabled().booleanValue())) {
			view.getArgParameters().setPirtEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the QoI options changed
	 */
	void changedQoISelected(QuantityOfInterest qoi, QuantityOfInterest tag, boolean enabled) {

		if (qoi == null) {
			return;
		}

		// if option list is null
		if (view.getArgParameters().getQoiSelectedList() == null
				|| view.getArgParameters().getQoiSelectedList().isEmpty()) {
			List<ARGParametersQoIOption> options = new ArrayList<>();
			ARGParametersQoIOption option = new ARGParametersQoIOption();
			option.setArgParameter(view.getArgParameters());
			option.setQoi(qoi);
			option.setTag(tag);
			option.setEnabled(enabled);
			options.add(option);
			view.getArgParameters().setQoiSelectedList(options);

			updateARGParameters();

			return;
		}

		boolean changed = false;
		boolean found = false;

		for (ARGParametersQoIOption opt : view.getArgParameters().getQoiSelectedList()) {
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
			option.setArgParameter(view.getArgParameters());
			option.setQoi(qoi);
			option.setTag(tag);
			option.setEnabled(enabled);
			view.getArgParameters().getQoiSelectedList().add(option);

			changed = true;
		}

		if (changed) {
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PCMM option changed
	 */
	void changedPCMMEnabledOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		if (view.getArgParameters().getPcmmEnabled() == null
				|| (value != view.getArgParameters().getPcmmEnabled().booleanValue())) {
			view.getArgParameters().setPcmmEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PCMM Planning option changed
	 */
	void changedPCMMPlanningEnabledOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		if (view.getArgParameters().getPcmmPlanningEnabled() == null
				|| (value != view.getArgParameters().getPcmmPlanningEnabled().booleanValue())) {
			view.getArgParameters().setPcmmPlanningEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PCMM Evidence option changed
	 */
	void changedPCMMEvidenceEnabledOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		if (view.getArgParameters().getPcmmEvidenceEnabled() == null
				|| (value != view.getArgParameters().getPcmmEvidenceEnabled().booleanValue())) {
			view.getArgParameters().setPcmmEvidenceEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PCMM Assessment option changed
	 */
	void changedPCMMAssessmentEnabledOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		if (view.getArgParameters().getPcmmAssessmentEnabled() == null
				|| (value != view.getArgParameters().getPcmmAssessmentEnabled().booleanValue())) {
			view.getArgParameters().setPcmmAssessmentEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the PCMM tag selection changed
	 */
	void changedPCMMTagSelected(Tag tagSelected) {

		if (view.getArgParameters() == null) {
			return;
		}

		boolean changed = false;

		// persisted tag is not null and selected tag is null
		if (view.getArgParameters().getPcmmTagSelected() != null
				&& (tagSelected == null || tagSelected.getId() == null)) {
			view.getArgParameters().setPcmmTagSelected(null);
			changed = true;
		}

		// persisted tag is null and selected tag is not null
		if (view.getArgParameters().getPcmmTagSelected() == null && tagSelected != null
				&& tagSelected.getId() != null) {
			view.getArgParameters().setPcmmTagSelected(tagSelected);
			changed = true;
		}

		// persisted tag is not null and selected tag is not null -> compare
		if (view.getArgParameters().getPcmmTagSelected() != null && tagSelected != null && tagSelected.getId() != null
				&& !tagSelected.equals(view.getArgParameters().getPcmmTagSelected())) {
			view.getArgParameters().setPcmmTagSelected(tagSelected);
			changed = true;
		}

		if (changed) {
			updateARGParameters();
		}
	}

	/**
	 * update if the value of the textCustomEndingFilePath changed and set
	 * argParameters structure file, otherwise do nothing.
	 */
	void changedARGCustomEndingFile(String value) {

		if (view.getArgParameters() == null || value == null
				|| value.equals(view.getArgParameters().getCustomEndingFilePath())) {
			return;
		}

		// update
		view.getArgParameters().setCustomEndingFilePath(FileTools.getNormalizedPath(Paths.get(value)));
		updateARGParameters();
	}

	/**
	 * update if the value of the ARG custom ending options changed
	 */
	void changedARGCustomEndingEnabledOption(boolean value) {

		if (view.getArgParameters() == null) {
			return;
		}

		// PIRT
		if (view.getArgParameters().getCustomEndingEnabled() == null
				|| (value != view.getArgParameters().getCustomEndingEnabled().booleanValue())) {
			view.getArgParameters().setCustomEndingEnabled(value);
			updateARGParameters();
		}
	}

	/**
	 * Update the ARG parameters
	 */
	void updateARGParameters() {

		if (view.getArgParameters() != null) {
			try {
				// update arg parameters
				ARGParameters newArgParameters = view.getViewManager().getAppManager()
						.getService(IReportARGExecutionApp.class).updateARGParameters(view.getArgParameters());

				view.setARGParameters(newArgParameters);

				// set save state
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error("An error occured while updating the ARG parameters", e); //$NON-NLS-1$
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
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
		Model model = view.getViewManager().getCache().getModel();
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
		options.put(ExportOptions.PLANNING_INCLUDE, view.getChboxPlanning().getSelection());
		options.put(ExportOptions.INTENDEDPURPOSE_INCLUDE, view.getChboxPlanningIntendedPurpose().getSelection());
		options.put(ExportOptions.SYSTEM_REQUIREMENT_INCLUDE, view.getChboxPlanningSystemRequirement().getSelection());
		options.put(ExportOptions.QOI_PLANNER_INCLUDE, view.getChboxPlanningQoIPlanner().getSelection());
		options.put(ExportOptions.PLANNING_UNCERTAINTY_INCLUDE, view.getChboxPlanningUncertainty().getSelection());
		options.put(ExportOptions.DECISION_INCLUDE, view.getChboxPlanningDecision().getSelection());

		// PLANNING section
		options.put(ExportOptions.INTENDED_PURPOSE,
				view.getViewManager().getAppManager().getService(IIntendedPurposeApp.class).get(model));
		options.put(ExportOptions.SYSTEM_REQUIREMENT_LIST, view.getViewManager().getAppManager()
				.getService(ISystemRequirementApplication.class).getRequirementWithChildrenByModel(model));
		options.put(ExportOptions.PLANNING_UNCERTAINTIES, view.getViewManager().getAppManager()
				.getService(IUncertaintyApplication.class).getUncertaintyGroupByModel(model));
		options.put(ExportOptions.DECISION_LIST, view.getViewManager().getAppManager()
				.getService(IDecisionApplication.class).getDecisionRootByModel(model));
	}

	/**
	 * Put in the options map the PIRT data according to the user selection.
	 * 
	 * @param options the report options map
	 */
	private void getARGOptionsPIRT(Map<ExportOptions, Object> options) {

		// PIRT- Get generation parameters
		options.put(ExportOptions.PIRT_INCLUDE, view.getChboxPirt().getSelection());
		options.put(ExportOptions.PIRT_SPECIFICATION, view.getViewManager().getCache().getPIRTSpecification());

		Map<QuantityOfInterest, Map<ExportOptions, Object>> pirtQoIDataList = new HashMap<>();
		if (view.getArgParameters() != null && view.getArgParameters().getQoiSelectedList() != null) {
			view.getArgParameters().getQoiSelectedList().stream().filter(Objects::nonNull).forEach(qoi -> {
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
		Tag pcmmTag = view.getCbxSelection(Tag.class, view.getCbxPcmmTag());
		if (pcmmTag == null || pcmmTag.getId() == null) {
			pcmmTag = null;
		}

		// PCMM - Options
		PCMMMode mode = null;
		PCMMSpecification pcmmConfig = view.getViewManager().getPCMMConfiguration();

		if (pcmmConfig != null) {

			// Report options
			mode = pcmmConfig.getMode();
			options.put(ExportOptions.PCMM_INCLUDE, view.getChboxPcmm().getSelection());
			options.put(ExportOptions.PCMM_MODE, mode);
			options.put(ExportOptions.PCMM_TAG, pcmmTag);
			options.put(ExportOptions.PCMM_PLANNING_INCLUDE, view.getChboxPcmmPlanning().getSelection());
			options.put(ExportOptions.PCMM_EVIDENCE_INCLUDE, view.getChboxPcmmEvidence().getSelection());
			options.put(ExportOptions.PCMM_ASSESSMENT_INCLUDE, view.getChboxPcmmAssessment().getSelection());

			// Get planning parameters
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(GenericParameter.Filter.PARENT, null);
			List<PCMMPlanningParam> planningParameters = view.getViewManager().getAppManager()
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
				List<PCMMElement> pcmmElements = view.getViewManager().getAppManager()
						.getService(IPCMMApplication.class).getElementList(model);
				options.put(ExportOptions.PCMM_ELEMENTS, pcmmElements);

				// PCMM Data
				for (PCMMElement pcmmElement : pcmmElements) {

					// Planning Questions
					pcmmPlanningQuestions.put(pcmmElement,
							view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
									.getPlanningQuestionsByElement(pcmmElement, mode));

					// Planning Question Values
					pcmmPlanningQuestionValues.put(pcmmElement,
							view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
									.getPlanningQuestionsValueByElement(pcmmElement, mode, pcmmTag));

					// Planning Parameter values
					pcmmPlanningValues.put(pcmmElement,
							view.getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
									.getPlanningValueByElement(pcmmElement, mode, pcmmTag));

					// Evidence
					pcmmEvidences.put(pcmmElement, view.getViewManager().getAppManager()
							.getService(IPCMMEvidenceApp.class).getEvidenceByTag(pcmmTag));

					// Assessments
					pcmmAssessments.put(pcmmElement, view.getViewManager().getAppManager()
							.getService(IPCMMAssessmentApp.class).getAssessmentByTag(pcmmTag));
				}

				// Planning
				if (view.getChboxPcmmPlanning().getSelection()) {
					options.put(ExportOptions.PCMM_PLANNING_QUESTIONS, pcmmPlanningQuestions);
					options.put(ExportOptions.PCMM_PLANNING_QUESTION_VALUES, pcmmPlanningQuestionValues);
					options.put(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES, pcmmPlanningValues);
				}

				// Evidence
				if (view.getChboxPcmmEvidence().getSelection()) {
					options.put(ExportOptions.PCMM_EVIDENCE_LIST, pcmmEvidences);
				}

				// Assessments
				if (view.getChboxPcmmAssessment().getSelection()) {
					options.put(ExportOptions.PCMM_ASSESSMENT_LIST, pcmmAssessments);
				}

			} catch (CredibilityException e) {
				logger.error(e.getMessage());
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
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
		options.put(ExportOptions.CUSTOM_ENDING_INCLUDE, view.getChboxCustomEnding().getSelection());
	}

}
