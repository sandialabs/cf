/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.decision.IDecisionApplication;
import gov.sandia.cf.application.exports.IExportApplication;
import gov.sandia.cf.application.global.IUserApplication;
import gov.sandia.cf.application.intendedpurpose.IIntendedPurposeApp;
import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.pcmm.IPCMMAssessmentApp;
import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.application.pcmm.IPCMMPlanningApplication;
import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.application.requirement.ISystemRequirementApplication;
import gov.sandia.cf.application.uncertainty.IUncertaintyApplication;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.FileExtension;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * Export Configuration view controller: Used to control the Export
 * Configuration view
 * 
 * @author Didier Verstraete
 *
 */
public class ExportConfigurationViewController
		extends AViewController<ConfigurationViewManager, ExportConfigurationView> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExportConfigurationViewController.class);

	/**
	 * the extensions needed to filter file browser
	 */
	private String[] confFileDefaultExtensions = new String[] { FileTools.YML_FILTER, FileTools.YAML_FILTER };

	/**
	 * Instantiates a new export configuration view controller.
	 *
	 * @param viewManager the view manager
	 * @param parent      the parent
	 */
	ExportConfigurationViewController(ConfigurationViewManager viewManager, Composite parent) {
		super(viewManager);
		super.setView(new ExportConfigurationView(this, parent, SWT.NONE));
	}

	/**
	 * Reload data.
	 */
	void reloadData() {
		getView().refreshPIRTQoITree();
		getView().refreshPCMMTagTree();
	}

	/**
	 * Gets the qois.
	 *
	 * @return the qois
	 */
	List<QuantityOfInterest> getQois() {
		return getViewManager().getAppManager().getService(IPIRTApplication.class)
				.getQoIList(getViewManager().getCache().getModel());
	}

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	List<Tag> getTags() {
		return getViewManager().getAppManager().getService(IPCMMApplication.class).getTags();
	}

	/**
	 * Do browse export file for a specific widget
	 * 
	 * @param textWidget   the text widget
	 * @param exportButton the export button
	 * @param prefKey      the preference key
	 */
	void browseExportFile(Text textWidget, Button exportButton, String prefKey) {

		if (textWidget == null || exportButton == null) {
			return;
		}

		// get default path and set to .cf file folder if blank
		String defaultPath = null;
		if (!StringUtils.isBlank(textWidget.getText())) {
			defaultPath = textWidget.getText();
		} else {
			defaultPath = WorkspaceTools.toOsPath(getViewManager().getCredibilityEditor().getCfProjectPath());
		}

		// select export file
		String selectedPath = selectFileDialog(defaultPath);

		if (selectedPath != null) {

			// check yml extension and append
			if (!FileTools.hasExtension(selectedPath, FileExtension.YML.getExtension())
					&& !FileTools.hasExtension(selectedPath, FileExtension.YAML.getExtension())) {
				selectedPath += FileExtension.YML.getExtension();
			}

			textWidget.setText(selectedPath);

			if (prefKey != null) {
				PrefTools.setPreference(prefKey, textWidget.getText());
			}
		}

		// enable/disable export button
		exportButton.setEnabled(StringUtils.isNotEmpty(textWidget.getText()));
	}

	/**
	 * Open a file dialog selector and return the selected file
	 * 
	 * @param filterPath the default path to set
	 * @return the file selected by the user
	 */
	String selectFileDialog(String filterPath) {
		FileDialog dialog = new FileDialog(getView().getShell());
		dialog.setFilterPath(filterPath);
		dialog.setFileName(filterPath);
		dialog.setFilterExtensions(confFileDefaultExtensions);
		return dialog.open();
	}

	/**
	 * Export the QoI Planning schema.
	 */
	void exportQoIPlanningSchema() {

		// check save need
		if (!checkSaveNeed()) {
			return;
		}

		if (getView().getTextQoIPlanningSchemaPath() == null) {
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		// export configuration
		try {
			getViewManager().getAppManager().getService(IExportApplication.class).exportQoIPlanningSchema(
					new File(getView().getTextQoIPlanningSchemaPath()),
					getViewManager().getCache().getQoIPlanningSpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_EXPORTVIEW_EXPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Export the PIRT schema.
	 */
	void exportPIRTSchema() {

		// check save need
		if (!checkSaveNeed()) {
			return;
		}

		if (getView().getTextPIRTSchemaPath() == null) {
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		try {

			// export configuration
			getViewManager().getAppManager().getService(IExportApplication.class).exportPIRTSchema(
					new File(getView().getTextPIRTSchemaPath()), getViewManager().getCache().getPIRTSpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_EXPORTVIEW_EXPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}

	}

	/**
	 * Export the PCMM Schema.
	 */
	void exportPCMMSchema() {

		// check save need
		if (!checkSaveNeed()) {
			return;
		}

		if (getView().getTextPCMMSchemaPath() == null) {
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		try {

			// export configuration
			getViewManager().getAppManager().getService(IExportApplication.class).exportPCMMSchema(
					new File(getView().getTextPCMMSchemaPath()), getViewManager().getCache().getPCMMSpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_EXPORTVIEW_EXPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Export the Uncertainty parameters
	 */
	void exportUncertaintySchema() {

		// check save need
		if (!checkSaveNeed()) {
			return;
		}

		if (getView().getTextUncertaintySchemaPath() == null) {
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		try {

			// export configuration
			getViewManager().getAppManager().getService(IExportApplication.class).exportUncertaintySchema(
					new File(getView().getTextUncertaintySchemaPath()),
					getViewManager().getCache().getUncertaintySpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_EXPORTVIEW_EXPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Export the System Requirements parameters
	 */
	void exportSysRequirementsSchema() {

		// check save need
		if (!checkSaveNeed()) {
			return;
		}

		if (getView().getTextSysRequirementsSchemaPath() == null) {
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		try {

			// export configuration
			getViewManager().getAppManager().getService(IExportApplication.class).exportSysRequirementsSchema(
					new File(getView().getTextSysRequirementsSchemaPath()),
					getViewManager().getCache().getSystemRequirementSpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_EXPORTVIEW_EXPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Export the Decision parameters
	 */
	void exportDecisionSchema() {

		// check save need
		if (!checkSaveNeed()) {
			return;
		}

		if (getView().getTextDecisionSchemaPath() == null) {
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		try {

			// export configuration
			getViewManager().getAppManager().getService(IExportApplication.class).exportDecisionSchema(
					new File(getView().getTextDecisionSchemaPath()),
					getViewManager().getCache().getDecisionSpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_EXPORTVIEW_EXPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Export the Data
	 */
	void exportData() {

		// check save need
		if (!checkSaveNeed()) {
			return;
		}

		Map<ExportOptions, Object> exportOptions = getExportOptions();
		if (getView().getTextDataSchemaPath() == null) {
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATAEXPORT_FILE_NULL));
			return;
		}

		try {

			// export data
			getViewManager().getAppManager().getService(IExportApplication.class)
					.exportData(new File(getView().getTextDataSchemaPath()), exportOptions);

			// Inform success
			MessageDialog.openInformation(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATAEXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_EXPORTVIEW_DATAEXPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}

	}

	/**
	 * Check save need.
	 *
	 * @return true if the user confirm save need, otherwise false.
	 */
	private boolean checkSaveNeed() {
		if (getViewManager().isDirty()) {
			boolean openConfirm = MessageDialog.openConfirm(getView().getShell(),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_NEEDSAVE));
			if (openConfirm) {
				getViewManager().doSave();
			} else {
				MessageDialog.openInformation(getView().getShell(),
						RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
						RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_CANCELLED));
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the export options.
	 *
	 * @return the export options depending of the user selection
	 */
	private Map<ExportOptions, Object> getExportOptions() {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);
		options.put(ExportOptions.MODEL, getViewManager().getCache().getModel());
		options.put(ExportOptions.USER_LIST,
				getViewManager().getAppManager().getService(IUserApplication.class).getUsers());
		options.put(ExportOptions.PCMM_ROLE_LIST, getViewManager().getCache().getPCMMSpecification().getRoles());

		options.putAll(getIntendedPurposeExportOptions());
		options.putAll(getDecisionExportOptions());
		options.putAll(getSystemRequirementExportOptions());
		options.putAll(getUncertaintyExportOptions());

		options.putAll(getPIRTExportOptions());
		options.putAll(getPCMMExportOptions());

		return options;
	}

	/**
	 * Gets the intended purpose export options.
	 *
	 * @return the intended purpose export options
	 */
	private Map<ExportOptions, Object> getIntendedPurposeExportOptions() {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);

		// Intended Purpose - is selected?
		options.put(ExportOptions.INTENDEDPURPOSE_INCLUDE, getView().isIntendedPurposeSelected());

		// Intended Purpose - get data
		try {
			IntendedPurpose intendedPurpose = getViewManager().getAppManager().getService(IIntendedPurposeApp.class)
					.get(getViewManager().getCache().getModel());
			options.put(ExportOptions.INTENDED_PURPOSE, intendedPurpose);
		} catch (CredibilityException e) {
			logger.error(e.getMessage());
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
					e.getMessage());
		}

		return options;
	}

	/**
	 * Gets the decision export options.
	 *
	 * @return the Decision export options depending of the user selection
	 */
	private Map<ExportOptions, Object> getDecisionExportOptions() {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);

		// Decision - Get generation parameters
		options.put(ExportOptions.DECISION_INCLUDE, getView().isDecisionSelected());

		// Decision specification
		options.put(ExportOptions.DECISION_SPECIFICATION, getViewManager().getCache().getDecisionSpecification());

		// Decision - get Decision parameters
		List<DecisionParam> parameters = getViewManager().getCache().getDecisionSpecification().getParameters();
		options.put(ExportOptions.DECISION_PARAMETERS, parameters);

		// Decision - get Decision values
		List<Decision> values = getViewManager().getAppManager().getService(IDecisionApplication.class)
				.getDecisionRootByModel(getViewManager().getCache().getModel());
		options.put(ExportOptions.DECISION_LIST, values);

		return options;
	}

	/**
	 * Gets the system requirement export options.
	 *
	 * @return the System Requirement export options depending of the user selection
	 */
	private Map<ExportOptions, Object> getSystemRequirementExportOptions() {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);

		// System Requirement - Get generation parameters
		options.put(ExportOptions.SYSTEM_REQUIREMENT_INCLUDE, getView().isSystemRequirementSelected());

		// System Requirement specification
		options.put(ExportOptions.SYSTEM_REQUIREMENT_SPECIFICATION,
				getViewManager().getCache().getSystemRequirementSpecification());

		// System Requirement - get System Requirement parameters
		List<SystemRequirementParam> parameters = getViewManager().getCache().getSystemRequirementSpecification()
				.getParameters();
		options.put(ExportOptions.SYSTEM_REQUIREMENT_PARAMETERS, parameters);

		// System Requirement - get System Requirement values
		List<SystemRequirement> values = getViewManager().getAppManager()
				.getService(ISystemRequirementApplication.class)
				.getRequirementRootByModel(getViewManager().getCache().getModel());
		options.put(ExportOptions.SYSTEM_REQUIREMENT_LIST, values);

		return options;
	}

	/**
	 * Gets the uncertainty export options.
	 *
	 * @return the Uncertainty export options depending of the user selection
	 */
	private Map<ExportOptions, Object> getUncertaintyExportOptions() {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);

		// Uncertainty - Get generation parameters
		options.put(ExportOptions.UNCERTAINTY_INCLUDE, getView().isUncertaintySelected());

		// Uncertainty specification
		options.put(ExportOptions.UNCERTAINTY_SPECIFICATION, getViewManager().getCache().getUncertaintySpecification());

		// Uncertainty - get uncertainty parameters and their content
		List<UncertaintyParam> parameters = getViewManager().getCache().getUncertaintySpecification().getParameters();
		options.put(ExportOptions.UNCERTAINTY_PARAMETERS, parameters);

		// Uncertainty - get uncertainty groups and their content
		List<Uncertainty> uncertaintyGroupList = getViewManager().getAppManager()
				.getService(IUncertaintyApplication.class)
				.getUncertaintyGroupByModel(getViewManager().getCache().getModel());
		options.put(ExportOptions.UNCERTAINTY_GROUP_LIST, uncertaintyGroupList);

		return options;
	}

	/**
	 * Gets the PIRT export options.
	 *
	 * @return the PIRT export options depending of the user selection
	 */
	private Map<ExportOptions, Object> getPIRTExportOptions() {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);

		// PIRT- Get generation parameters
		options.put(ExportOptions.PIRT_INCLUDE, getView().isPIRTSelected());

		// PIRT specification
		options.put(ExportOptions.PIRT_SPECIFICATION, getViewManager().getCache().getPIRTSpecification());

		// PIRT - get tree selected QoIs
		List<QuantityOfInterest> qoiList = null;
		Object[] checkedQoIs = getView().getPIRTQoISelected();
		if (checkedQoIs != null && checkedQoIs.length > 0) {
			qoiList = Arrays.stream(checkedQoIs).filter(QuantityOfInterest.class::isInstance)
					.map(QuantityOfInterest.class::cast).collect(Collectors.toList());
		}
		options.put(ExportOptions.PIRT_QOI_LIST, qoiList);

		return options;
	}

	/**
	 * Gets the PCMM export options.
	 *
	 * @return the PCMM options depending of the user selection
	 */
	private Map<ExportOptions, Object> getPCMMExportOptions() {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);

		// PCMM - Get generation parameters
		// PCMM - get selected tags
		List<Tag> pcmmTagList = getSelectedTagList();

		// PCMM specification
		options.put(ExportOptions.PCMM_SPECIFICATION, getViewManager().getCache().getPCMMSpecification());

		// PCMM - Options
		PCMMMode mode = getViewManager().getPCMMConfiguration().getMode();
		options.put(ExportOptions.PCMM_INCLUDE, getView().isPCMMSelected());
		options.put(ExportOptions.PCMM_MODE, mode);
		options.put(ExportOptions.PCMM_ROLE_LIST, getViewManager().getCache().getPCMMSpecification().getRoles());
		options.put(ExportOptions.PCMM_ELEMENTS, getViewManager().getCache().getPCMMSpecification().getElements());
		options.put(ExportOptions.PCMM_PLANNING_PARAMETERS,
				getViewManager().getCache().getPCMMSpecification().getPlanningFields());
		options.put(ExportOptions.PCMM_PLANNING_QUESTIONS,
				getViewManager().getCache().getPCMMSpecification().getPlanningQuestions());

		options.put(ExportOptions.PCMM_TAG_LIST, pcmmTagList);
		options.put(ExportOptions.PCMM_PLANNING_INCLUDE, getView().isPCMMPlanningSelected());
		options.put(ExportOptions.PCMM_ASSESSMENT_INCLUDE, getView().isPCMMAssessmentSelected());
		options.put(ExportOptions.PCMM_EVIDENCE_INCLUDE, getView().isPCMMEvidenceSelected());

		// Get Elements
		try {
			/**
			 * PCMM Elements
			 */
			List<PCMMElement> pcmmElements = getViewManager().getAppManager().getService(IPCMMApplication.class)
					.getElementList(getViewManager().getCache().getModel());
			options.put(ExportOptions.PCMM_ELEMENTS, pcmmElements);

			/**
			 * PCMM data
			 */
			// Get planning parameters
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(GenericParameter.Filter.PARENT, null);
			List<PCMMPlanningParam> planningParameters = getViewManager().getAppManager()
					.getService(IPCMMPlanningApplication.class).getPlanningFieldsBy(filters);
			options.put(ExportOptions.PCMM_PLANNING_PARAMETERS, planningParameters);

			// Get planning questions & values
			HashMap<PCMMElement, List<PCMMPlanningQuestion>> pcmmPlanningQuestions = new HashMap<>();
			HashMap<PCMMElement, List<PCMMPlanningQuestionValue>> pcmmPlanningQuestionValues = new HashMap<>();
			HashMap<PCMMElement, List<PCMMPlanningValue>> pcmmPlanningValues = new HashMap<>();
			HashMap<PCMMElement, List<PCMMPlanningTableItem>> pcmmPlanningItems = new HashMap<>();
			HashMap<PCMMElement, List<PCMMEvidence>> pcmmEvidence = new HashMap<>();
			HashMap<PCMMElement, List<PCMMAssessment>> pcmmAssessments = new HashMap<>();

			for (PCMMElement pcmmElement : pcmmElements) {

				// Planning
				if (getView().isPCMMPlanningSelected()) {

					// Questions
					pcmmPlanningQuestions.put(pcmmElement,
							getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
									.getPlanningQuestionsByElement(pcmmElement, mode));

					// Question Values
					pcmmPlanningQuestionValues.put(pcmmElement,
							getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
									.getPlanningQuestionsValueByElement(pcmmElement, mode, pcmmTagList));

					// Parameter values
					pcmmPlanningValues.put(pcmmElement,
							getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
									.getPlanningValueByElement(pcmmElement, mode, pcmmTagList));

					// Parameter table item values
					pcmmPlanningItems.put(pcmmElement,
							getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
									.getPlanningTableItemByElement(pcmmElement, mode, pcmmTagList));

				}

				// Evidences
				if (getView().isPCMMEvidenceSelected()) {
					pcmmEvidence.put(pcmmElement, getViewManager().getAppManager().getService(IPCMMEvidenceApp.class)
							.getEvidenceByTag(pcmmTagList));
				}

				// Assessments
				if (getView().isPCMMAssessmentSelected()) {
					pcmmAssessments.put(pcmmElement, getViewManager().getAppManager()
							.getService(IPCMMAssessmentApp.class).getAssessmentByTag(pcmmTagList));
				}
			}

			// Planning
			if (getView().isPCMMPlanningSelected()) {
				options.put(ExportOptions.PCMM_PLANNING_QUESTIONS, pcmmPlanningQuestions);
				options.put(ExportOptions.PCMM_PLANNING_QUESTION_VALUES, pcmmPlanningQuestionValues);
				options.put(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES, pcmmPlanningValues);
				options.put(ExportOptions.PCMM_PLANNING_PARAMETERS_TABLEITEMS, pcmmPlanningItems);
			}

			// Evidences
			if (getView().isPCMMEvidenceSelected()) {
				options.put(ExportOptions.PCMM_EVIDENCE_LIST, pcmmEvidence);
			}

			// Assessments
			if (getView().isPCMMAssessmentSelected()) {
				options.put(ExportOptions.PCMM_ASSESSMENT_LIST, pcmmAssessments);
			}

		} catch (CredibilityException e) {
			logger.error(e.getMessage());
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE),
					e.getMessage());
		}

		return options;
	}

	/**
	 * Gets the selected tag list.
	 *
	 * @return the list of selected tags
	 */
	private List<Tag> getSelectedTagList() {

		List<Tag> pcmmTagList = new ArrayList<>();
		Object[] checkedTags = getView().getPCMMTagSelected();
		if (checkedTags != null && checkedTags.length > 0) {
			for (Tag tag : Arrays.stream(checkedTags).filter(Tag.class::isInstance).map(Tag.class::cast)
					.collect(Collectors.toList())) {

				if (tag != null && tag.getId() != null) {
					pcmmTagList.add(tag);
				} else if (!pcmmTagList.contains(null)) {

					// add tag null for the current version
					pcmmTagList.add(null);
				}
			}
		}

		return pcmmTagList;
	}
}
