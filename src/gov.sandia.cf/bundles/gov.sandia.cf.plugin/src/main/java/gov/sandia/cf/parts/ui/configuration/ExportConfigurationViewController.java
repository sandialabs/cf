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
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IExportApplication;
import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.application.IPCMMPlanningApplication;
import gov.sandia.cf.application.IUncertaintyApplication;
import gov.sandia.cf.application.IUserApplication;
import gov.sandia.cf.application.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameter;
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
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.preferences.PrefTools;
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
public class ExportConfigurationViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExportConfigurationViewController.class);

	/**
	 * The view
	 */
	private ExportConfigurationView view;

	/**
	 * the extensions needed to filter file browser
	 */
	private String[] confFileDefaultExtensions = new String[] { FileTools.YML_FILTER, FileTools.YAML_FILTER };

	ExportConfigurationViewController(ExportConfigurationView view) {
		Assert.isNotNull(view);
		this.view = view;
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
			defaultPath = WorkspaceTools.toOsPath(view.getViewManager().getCredibilityEditor().getCfProjectPath());
		}

		// select export file
		String selectedPath = selectFileDialog(defaultPath);

		if (selectedPath != null) {

			// check yml extension and append
			if (!FileTools.hasExtension(selectedPath, FileTools.YML)
					&& !FileTools.hasExtension(selectedPath, FileTools.YAML)) {
				selectedPath += FileTools.YML;
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
		FileDialog dialog = new FileDialog(view.getShell());
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

		if (view.getTextQoIPlanningSchemaPath() == null) {
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		// export configuration
		try {
			view.getViewManager().getAppManager().getService(IExportApplication.class).exportQoIPlanningSchema(
					new File(view.getTextQoIPlanningSchemaPath()),
					view.getViewManager().getCache().getQoIPlanningSpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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

		if (view.getTextPIRTSchemaPath() == null) {
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		try {

			// export configuration
			view.getViewManager().getAppManager().getService(IExportApplication.class).exportPIRTSchema(
					new File(view.getTextPIRTSchemaPath()), view.getViewManager().getCache().getPIRTSpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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

		if (view.getTextPCMMSchemaPath() == null) {
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		try {

			// export configuration
			view.getViewManager().getAppManager().getService(IExportApplication.class).exportPCMMSchema(
					new File(view.getTextPCMMSchemaPath()), view.getViewManager().getCache().getPCMMSpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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

		if (view.getTextUncertaintySchemaPath() == null) {
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		try {

			// export configuration
			view.getViewManager().getAppManager().getService(IExportApplication.class).exportUncertaintySchema(
					new File(view.getTextUncertaintySchemaPath()),
					view.getViewManager().getCache().getUncertaintySpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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

		if (view.getTextSysRequirementsSchemaPath() == null) {
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		try {

			// export configuration
			view.getViewManager().getAppManager().getService(IExportApplication.class).exportSysRequirementsSchema(
					new File(view.getTextSysRequirementsSchemaPath()),
					view.getViewManager().getCache().getSystemRequirementSpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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

		if (view.getTextDecisionSchemaPath() == null) {
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_FILE_NULL));
			return;
		}

		try {

			// export configuration
			view.getViewManager().getAppManager().getService(IExportApplication.class).exportDecisionSchema(
					new File(view.getTextDecisionSchemaPath()),
					view.getViewManager().getCache().getDecisionSpecification());

			// refresh project
			WorkspaceTools.refreshProject();

			// Inform success
			MessageDialog.openInformation(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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
		if (view.getTextDataSchemaPath() == null) {
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATAEXPORT_FILE_NULL));
			return;
		}

		try {

			// export data
			view.getViewManager().getAppManager().getService(IExportApplication.class).exportData(
					new File(view.getTextDataSchemaPath()), exportOptions,
					view.getViewManager().getCache().getPCMMSpecification(),
					view.getViewManager().getCOMConfiguration());

			// Inform success
			MessageDialog.openInformation(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATAEXPORT_SUCCESS));

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_EXPORTVIEW_DATAEXPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}

	}

	/**
	 * @return true if the user confirm save need, otherwise false.
	 */
	private boolean checkSaveNeed() {
		if (view.getViewManager().isDirty()) {
			boolean openConfirm = MessageDialog.openConfirm(view.getShell(),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_NEEDSAVE));
			if (openConfirm) {
				view.getViewManager().doSave();
			} else {
				MessageDialog.openInformation(view.getShell(), RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE),
						RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_EXPORT_CANCELLED));
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the export options depending of the user selection
	 */
	private Map<ExportOptions, Object> getExportOptions() {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);
		options.put(ExportOptions.MODEL, view.getViewManager().getCache().getModel());
		options.put(ExportOptions.USER_LIST,
				view.getViewManager().getAppManager().getService(IUserApplication.class).getUsers());

		options.putAll(getPIRTExportOptions());
		options.putAll(getPCMMExportOptions());
		options.putAll(getUncertaintyExportOptions());

		return options;
	}

	/**
	 * @return the PIRT export options depending of the user selection
	 */
	private Map<ExportOptions, Object> getPIRTExportOptions() {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);

		// PIRT- Get generation parameters
		options.put(ExportOptions.PIRT_INCLUDE, view.isPIRTSelected());

		// PIRT - get tree selected QoIs
		List<QuantityOfInterest> qoiList = null;
		Object[] checkedQoIs = view.getPIRTQoISelected();
		if (checkedQoIs != null && checkedQoIs.length > 0) {
			qoiList = Arrays.stream(checkedQoIs).filter(o -> o instanceof QuantityOfInterest)
					.map(QuantityOfInterest.class::cast).collect(Collectors.toList());
		}
		options.put(ExportOptions.PIRT_QOI_LIST, qoiList);

		return options;
	}

	/**
	 * @return the PCMM options depending of the user selection
	 */
	private Map<ExportOptions, Object> getPCMMExportOptions() {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);

		// PCMM - Get generation parameters
		// PCMM - get selected tags
		List<Tag> pcmmTagList = getSelectedTagList();

		// PCMM - Options
		PCMMMode mode = view.getViewManager().getPCMMConfiguration().getMode();
		options.put(ExportOptions.PCMM_INCLUDE, view.isPCMMSelected());
		options.put(ExportOptions.PCMM_MODE, mode);
		options.put(ExportOptions.PCMM_TAG_LIST, pcmmTagList);
		options.put(ExportOptions.PCMM_PLANNING_INCLUDE, view.isPCMMPlanningSelected());
		options.put(ExportOptions.PCMM_ASSESSMENT_INCLUDE, view.isPCMMAssessmentSelected());
		options.put(ExportOptions.PCMM_EVIDENCE_INCLUDE, view.isPCMMEvidenceSelected());

		// Get Elements
		try {
			/**
			 * PCMM Elements
			 */
			List<PCMMElement> pcmmElements = view.getViewManager().getAppManager().getService(IPCMMApplication.class)
					.getElementList(view.getViewManager().getCache().getModel());
			options.put(ExportOptions.PCMM_ELEMENTS, pcmmElements);

			/**
			 * PCMM data
			 */
			IPCMMApplication pcmmApp = view.getViewManager().getAppManager().getService(IPCMMApplication.class);
			IPCMMPlanningApplication pcmmPlanningApp = view.getViewManager().getAppManager()
					.getService(IPCMMPlanningApplication.class);

			// Get planning parameters
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(GenericParameter.Filter.PARENT, null);
			List<PCMMPlanningParam> planningParameters = pcmmPlanningApp.getPlanningFieldsBy(filters);
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
				if (view.isPCMMPlanningSelected()) {

					// Questions
					pcmmPlanningQuestions.put(pcmmElement,
							pcmmPlanningApp.getPlanningQuestionsByElement(pcmmElement, mode));

					// Question Values
					pcmmPlanningQuestionValues.put(pcmmElement,
							pcmmPlanningApp.getPlanningQuestionsValueByElement(pcmmElement, mode, pcmmTagList));

					// Parameter values
					pcmmPlanningValues.put(pcmmElement,
							pcmmPlanningApp.getPlanningValueByElement(pcmmElement, mode, pcmmTagList));

					// Parameter table item values
					pcmmPlanningItems.put(pcmmElement,
							pcmmPlanningApp.getPlanningTableItemByElement(pcmmElement, mode, pcmmTagList));

				}

				// Evidences
				if (view.isPCMMEvidenceSelected()) {
					pcmmEvidence.put(pcmmElement, pcmmApp.getEvidenceByTag(pcmmTagList));
				}

				// Assessments
				if (view.isPCMMAssessmentSelected()) {
					pcmmAssessments.put(pcmmElement, pcmmApp.getAssessmentByTag(pcmmTagList));
				}
			}

			// Planning
			if (view.isPCMMPlanningSelected()) {
				options.put(ExportOptions.PCMM_PLANNING_QUESTIONS, pcmmPlanningQuestions);
				options.put(ExportOptions.PCMM_PLANNING_QUESTION_VALUES, pcmmPlanningQuestionValues);
				options.put(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES, pcmmPlanningValues);
				options.put(ExportOptions.PCMM_PLANNING_PARAMETERS_TABLEITEMS, pcmmPlanningItems);
			}

			// Evidences
			if (view.isPCMMEvidenceSelected()) {
				options.put(ExportOptions.PCMM_EVIDENCE_LIST, pcmmEvidence);
			}

			// Assessments
			if (view.isPCMMAssessmentSelected()) {
				options.put(ExportOptions.PCMM_ASSESSMENT_LIST, pcmmAssessments);
			}

		} catch (CredibilityException e) {
			logger.error(e.getMessage());
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE), e.getMessage());
		}

		return options;
	}

	/**
	 * @return the list of selected tags
	 */
	private List<Tag> getSelectedTagList() {

		List<Tag> pcmmTagList = new ArrayList<>();
		Object[] checkedTags = view.getPCMMTagSelected();
		if (checkedTags != null && checkedTags.length > 0) {
			for (Tag tag : Arrays.stream(checkedTags).filter(o -> o instanceof Tag).map(Tag.class::cast)
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

	/**
	 * @return the Uncertainty export options depending of the user selection
	 */
	private Map<ExportOptions, Object> getUncertaintyExportOptions() {

		// Initialize
		Map<ExportOptions, Object> options = new EnumMap<>(ExportOptions.class);

		// Uncertainty - Get generation parameters
		options.put(ExportOptions.UNCERTAINTY_INCLUDE, view.isUncertaintySelected());

		// Uncertainty - get uncertainty groups and their content
		List<UncertaintyGroup> uncertaintyGroupList = view.getViewManager().getAppManager()
				.getService(IUncertaintyApplication.class)
				.getUncertaintyGroupByModel(view.getViewManager().getCache().getModel());
		options.put(ExportOptions.UNCERTAINTY_GROUP_LIST, uncertaintyGroupList);

		return options;
	}

}
