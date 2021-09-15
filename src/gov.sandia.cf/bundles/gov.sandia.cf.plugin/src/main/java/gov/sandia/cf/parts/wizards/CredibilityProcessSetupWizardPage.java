/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards;

import java.io.File;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import gov.sandia.cf.application.configuration.ConfigurationFileType;
import gov.sandia.cf.application.configuration.ConfigurationFileValidator;
import gov.sandia.cf.application.configuration.ConfigurationSchema;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * A newWizard page to create a new credibility process with a text and a
 * FileDialog to select the credibility link file
 * 
 * @author Didier Verstraete
 *
 */
public class CredibilityProcessSetupWizardPage extends WizardPage {
	/**
	 * the parent wizard
	 */
	private NewCredibilityProcessWizard parent;
	/**
	 * the PIRT path text
	 */
	private Text textPIRTSchemaPath;
	/**
	 * the QoI Planning path text
	 */
	private Text textQoIPlanningSchemaPath;
	/**
	 * the PCMM path text
	 */
	private Text textPCMMSchemaPath;
	/**
	 * the Uncertainty path text
	 */
	private Text textUncertaintySchemaPath;
	/**
	 * the SystemRequirement path text
	 */
	private Text textSystemRequirementSchemaPath;
	/**
	 * the Decision path text
	 */
	private Text textDecisionSchemaPath;
	/**
	 * the default path for the PIRT schema file
	 */
	private String pirtSchemaDefaultPath;
	/**
	 * the default path for the QoIPlanning schema file
	 */
	private String qoiPlanningSchemaDefaultPath;
	/**
	 * the default path for the PCMM schema file
	 */
	private String pcmmSchemaDefaultPath;
	/**
	 * the default path for the Uncertainty schema file
	 */
	private String uncertaintySchemaDefaultPath;
	/**
	 * the default path for the System Requirement schema file
	 */
	private String sysRequirementSchemaDefaultPath;
	/**
	 * the default path for the Decision schema file
	 */
	private String decisionSchemaDefaultPath;
	/**
	 * the extensions needed to filter file browser
	 */
	private String[] confFileDefaultExtensions = new String[] { FileTools.YML_FILTER, FileTools.YAML_FILTER };
	/**
	 * check box to generate evidence folder structure
	 */
	private Button chbxGenerateFolderStructure;
	/**
	 * the path to generate evidence folder structure
	 */
	private IPath evidenceFolderStructurePath;
	/**
	 * check box to generate evidence folder structure
	 */
	private Label lblGenerateEvidenceFolderStructure;

	private ConfigurationSchema confSchema;

	/**
	 * the error message lists
	 */
	private Map<ConfigurationFileType, Set<String>> mapErrMsg;
	private Set<String> errMsgCommon;
	private Composite container;

	/**
	 * The constructor
	 * 
	 * @param parent the parent wizard
	 */
	public CredibilityProcessSetupWizardPage(NewCredibilityProcessWizard parent) {
		super(RscTools.getString(RscConst.MSG_CONFFILEWIZARD_PAGENAME));
		setTitle(RscTools.getString(RscConst.MSG_CONFFILEWIZARD_TITLE));
		setDescription(RscTools.getString(RscConst.MSG_CONFFILEWIZARD_DESCRIPTION));

		this.parent = parent;
		this.evidenceFolderStructurePath = new Path(RscTools.empty());

		this.mapErrMsg = new EnumMap<>(ConfigurationFileType.class);

		this.errMsgCommon = new HashSet<>();

		// Get PIRT schema file paths in preferences
		pirtSchemaDefaultPath = PrefTools.getPreference(PrefTools.PIRT_SCHEMA_FILE_LAST_PATH_KEY);

		// if not exists, set workspace path
		File selectedFile = new File(pirtSchemaDefaultPath);
		if (!selectedFile.exists()) {
			pirtSchemaDefaultPath = WorkspaceTools.getWorkspacePathToString();
		}

		// Get QoI Planning schema file paths in preferences
		qoiPlanningSchemaDefaultPath = PrefTools.getPreference(PrefTools.QOIPLANNING_SCHEMA_FILE_LAST_PATH_KEY);

		// if not exists, set workspace path
		selectedFile = new File(qoiPlanningSchemaDefaultPath);
		if (!selectedFile.exists()) {
			qoiPlanningSchemaDefaultPath = WorkspaceTools.getWorkspacePathToString();
		}

		// Get PCMM schema file paths in preferences
		pcmmSchemaDefaultPath = PrefTools.getPreference(PrefTools.PCMM_SCHEMA_FILE_LAST_PATH_KEY);

		// if not exists, set workspace path
		selectedFile = new File(pcmmSchemaDefaultPath);
		if (!selectedFile.exists()) {
			pcmmSchemaDefaultPath = WorkspaceTools.getWorkspacePathToString();
		}

		// Get Uncertainty schema file paths in preferences
		uncertaintySchemaDefaultPath = PrefTools.getPreference(PrefTools.UNCERTAINTY_SCHEMA_FILE_LAST_PATH_KEY);

		// if not exists, set workspace path
		selectedFile = new File(uncertaintySchemaDefaultPath);
		if (!selectedFile.exists()) {
			uncertaintySchemaDefaultPath = WorkspaceTools.getWorkspacePathToString();
		}

		// Get System Requirement schema file paths in preferences
		sysRequirementSchemaDefaultPath = PrefTools
				.getPreference(PrefTools.SYSTEM_REQUIREMENT_SCHEMA_FILE_LAST_PATH_KEY);

		// if not exists, set workspace path
		selectedFile = new File(sysRequirementSchemaDefaultPath);
		if (!selectedFile.exists()) {
			sysRequirementSchemaDefaultPath = WorkspaceTools.getWorkspacePathToString();
		}

		// Get Decision schema file paths in preferences
		decisionSchemaDefaultPath = PrefTools.getPreference(PrefTools.DECISION_SCHEMA_FILE_LAST_PATH_KEY);

		// if not exists, set workspace path
		selectedFile = new File(decisionSchemaDefaultPath);
		if (!selectedFile.exists()) {
			decisionSchemaDefaultPath = WorkspaceTools.getWorkspacePathToString();
		}

	}

	/** {@inheritDoc} */
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;

		// Render sub-composites
		renderSysRequirement();
		renderQoIPlanning();
		renderUncertainty();
		renderDecision();
		renderPIRT();
		renderPCMM();

		// generate credibility evidence folder structure check-box
		// set by default to true
		chbxGenerateFolderStructure = new Button(container, SWT.CHECK);
		chbxGenerateFolderStructure.setText(RscTools.getString(RscConst.ERR_CONFFILEWIZARD_CHBX_EVID_STRUCT));
		chbxGenerateFolderStructure.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button btn = (Button) event.getSource();
				String text = btn.getSelection()
						? RscTools.getString(RscConst.ERR_CONFFILEWIZARD_EVID_PARENT_PATH, evidenceFolderStructurePath)
						: RscTools.empty();
				lblGenerateEvidenceFolderStructure.setText(text);
				checkPathToGenerateFolderStructure();
			}
		});

		// label
		lblGenerateEvidenceFolderStructure = new Label(container, SWT.NONE);
		GridData gdLabelGenEvidSchema = new GridData(GridData.FILL_HORIZONTAL);
		gdLabelGenEvidSchema.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		lblGenerateEvidenceFolderStructure.setLayoutData(gdLabelGenEvidSchema);

		// global
		setControl(container);
		confSchema = new ConfigurationSchema();
		reloadAllConfigurationSchemaValues();

		// check paths
		Stream.of(ConfigurationFileType.values()).forEach(this::checkSchemaFile);
		checkPathToGenerateFolderStructure();
	}

	/**
	 * Render System Requirement content
	 */
	private void renderSysRequirement() {
		/**
		 * System Requirement schema selection
		 */
		// label
		Label labelSystemRequirementSchemaPath = new Label(container, SWT.NONE);
		labelSystemRequirementSchemaPath
				.setText(RscTools.getString(RscConst.MSG_CONFFILEWIZARD_REQUIREMENT_SCHEMA_PATH));
		GridData gdlabelSystemRequirementSchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelSystemRequirementSchemaPath.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		labelSystemRequirementSchemaPath.setLayoutData(gdlabelSystemRequirementSchemaPath);

		// text path
		textSystemRequirementSchemaPath = new Text(container, SWT.BORDER | SWT.SINGLE);
		textSystemRequirementSchemaPath.setText(sysRequirementSchemaDefaultPath);
		textSystemRequirementSchemaPath.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				// check file validity
				checkSchemaFile(ConfigurationFileType.SYSTEM_REQUIREMENT);
			}

		});
		GridData gdTxtSystemRequirementparentPath = new GridData(GridData.FILL_HORIZONTAL);
		textSystemRequirementSchemaPath.setLayoutData(gdTxtSystemRequirementparentPath);

		// button browse
		Button btnBrowseSystemRequirement = new Button(container, SWT.CENTER);
		btnBrowseSystemRequirement.setText(RscTools.getString(RscConst.MSG_BTN_BROWSE));
		GridData gdBtnBrowseSystemRequirement = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gdBtnBrowseSystemRequirement.minimumWidth = PartsResourceConstants.CREDCONFWIZARD_BTN_WIDTH;
		btnBrowseSystemRequirement.setLayoutData(gdBtnBrowseSystemRequirement);
		btnBrowseSystemRequirement.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(getShell());
			dialog.setFilterPath(textSystemRequirementSchemaPath.getText());
			dialog.setFilterExtensions(confFileDefaultExtensions);
			String selectedPath = dialog.open();
			if (selectedPath != null) {
				textSystemRequirementSchemaPath.setText(selectedPath);
			}
			checkSchemaFile(ConfigurationFileType.SYSTEM_REQUIREMENT);
		});
	}

	/**
	 * Render QoI Planning content
	 */
	private void renderQoIPlanning() {
		/**
		 * QoI Planning schema selection
		 */
		// label
		Label labelQoIPlanningSchemaPath = new Label(container, SWT.NONE);
		labelQoIPlanningSchemaPath.setText(RscTools.getString(RscConst.MSG_CONFFILEWIZARD_QOIPLANNING_SCHEMA_PATH));
		GridData gdLabelQoIPlanningSchema = new GridData(GridData.FILL_HORIZONTAL);
		gdLabelQoIPlanningSchema.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		labelQoIPlanningSchemaPath.setLayoutData(gdLabelQoIPlanningSchema);

		// text path
		textQoIPlanningSchemaPath = new Text(container, SWT.BORDER | SWT.SINGLE);
		textQoIPlanningSchemaPath.setText(qoiPlanningSchemaDefaultPath);
		textQoIPlanningSchemaPath.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				// check file validity
				checkSchemaFile(ConfigurationFileType.QOIPLANNING);
			}

		});
		GridData gdTxtQoIPlanningParentPath = new GridData(GridData.FILL_HORIZONTAL);
		textQoIPlanningSchemaPath.setLayoutData(gdTxtQoIPlanningParentPath);

		// button browse
		Button btnBrowseQoIPlanning = new Button(container, SWT.CENTER);
		btnBrowseQoIPlanning.setText(RscTools.getString(RscConst.MSG_BTN_BROWSE));
		GridData gdBtnBrowsesQoIPlanning = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gdBtnBrowsesQoIPlanning.minimumWidth = PartsResourceConstants.CREDCONFWIZARD_BTN_WIDTH;
		btnBrowseQoIPlanning.setLayoutData(gdBtnBrowsesQoIPlanning);
		btnBrowseQoIPlanning.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(getShell());
			dialog.setFilterPath(textQoIPlanningSchemaPath.getText());
			dialog.setFilterExtensions(confFileDefaultExtensions);
			String selectedPath = dialog.open();
			if (selectedPath != null) {
				textQoIPlanningSchemaPath.setText(selectedPath);
			}
			checkSchemaFile(ConfigurationFileType.QOIPLANNING);
		});
	}

	/**
	 * Render Uncertainty content
	 */
	private void renderUncertainty() {
		/**
		 * Uncertainty schema selection
		 */
		// label
		Label labelUncertaintySchemaPath = new Label(container, SWT.NONE);
		labelUncertaintySchemaPath.setText(RscTools.getString(RscConst.MSG_CONFFILEWIZARD_UNCERTAINTY_SCHEMA_PATH));
		GridData gdlabelUncertaintySchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelUncertaintySchemaPath.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		labelUncertaintySchemaPath.setLayoutData(gdlabelUncertaintySchemaPath);

		// text path
		textUncertaintySchemaPath = new Text(container, SWT.BORDER | SWT.SINGLE);
		textUncertaintySchemaPath.setText(uncertaintySchemaDefaultPath);
		textUncertaintySchemaPath.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				// check file validity
				checkSchemaFile(ConfigurationFileType.UNCERTAINTY);
			}

		});
		GridData gdTxtUncertaintyparentPath = new GridData(GridData.FILL_HORIZONTAL);
		textUncertaintySchemaPath.setLayoutData(gdTxtUncertaintyparentPath);

		// button browse
		Button btnBrowseUncertainty = new Button(container, SWT.CENTER);
		btnBrowseUncertainty.setText(RscTools.getString(RscConst.MSG_BTN_BROWSE));
		GridData gdBtnBrowseUncertainty = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gdBtnBrowseUncertainty.minimumWidth = PartsResourceConstants.CREDCONFWIZARD_BTN_WIDTH;
		btnBrowseUncertainty.setLayoutData(gdBtnBrowseUncertainty);
		btnBrowseUncertainty.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(getShell());
			dialog.setFilterPath(textUncertaintySchemaPath.getText());
			dialog.setFilterExtensions(confFileDefaultExtensions);
			String selectedPath = dialog.open();
			if (selectedPath != null) {
				textUncertaintySchemaPath.setText(selectedPath);
			}
			checkSchemaFile(ConfigurationFileType.UNCERTAINTY);
		});
	}

	/**
	 * Render Analyst Decision content
	 */
	private void renderDecision() {
		/**
		 * Decision schema selection
		 */
		// label
		Label labelDecisionSchemaPath = new Label(container, SWT.NONE);
		labelDecisionSchemaPath.setText(RscTools.getString(RscConst.MSG_CONFFILEWIZARD_DECISION_SCHEMA_PATH));
		GridData gdlabelDecisionSchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelDecisionSchemaPath.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		labelDecisionSchemaPath.setLayoutData(gdlabelDecisionSchemaPath);

		// text path
		textDecisionSchemaPath = new Text(container, SWT.BORDER | SWT.SINGLE);
		textDecisionSchemaPath.setText(decisionSchemaDefaultPath);
		textDecisionSchemaPath.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				// check file validity
				checkSchemaFile(ConfigurationFileType.DECISION);
			}

		});
		GridData gdTxtDecisionPath = new GridData(GridData.FILL_HORIZONTAL);
		textDecisionSchemaPath.setLayoutData(gdTxtDecisionPath);

		// button browse
		Button btnBrowseDecision = new Button(container, SWT.CENTER);
		btnBrowseDecision.setText(RscTools.getString(RscConst.MSG_BTN_BROWSE));
		GridData gdbtnBrowseDecision = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gdbtnBrowseDecision.minimumWidth = PartsResourceConstants.CREDCONFWIZARD_BTN_WIDTH;
		btnBrowseDecision.setLayoutData(gdbtnBrowseDecision);
		btnBrowseDecision.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(getShell());
			dialog.setFilterPath(textDecisionSchemaPath.getText());
			dialog.setFilterExtensions(confFileDefaultExtensions);
			String selectedPath = dialog.open();
			if (selectedPath != null) {
				textDecisionSchemaPath.setText(selectedPath);
			}
			checkSchemaFile(ConfigurationFileType.DECISION);
		});
	}

	/**
	 * Render PIRT content
	 */
	private void renderPIRT() {
		/**
		 * PIRT schema selection
		 */
		// label
		Label labelPIRTSchemaPath = new Label(container, SWT.NONE);
		labelPIRTSchemaPath.setText(RscTools.getString(RscConst.MSG_CONFFILEWIZARD_PIRT_SCHEMA_PATH));
		GridData gdLabelPIRTSchema = new GridData(GridData.FILL_HORIZONTAL);
		gdLabelPIRTSchema.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		labelPIRTSchemaPath.setLayoutData(gdLabelPIRTSchema);

		// text path
		textPIRTSchemaPath = new Text(container, SWT.BORDER | SWT.SINGLE);
		textPIRTSchemaPath.setText(pirtSchemaDefaultPath);
		textPIRTSchemaPath.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				// check file validity
				checkSchemaFile(ConfigurationFileType.PIRT);
			}

		});
		GridData gdTxtparentPath = new GridData(GridData.FILL_HORIZONTAL);
		textPIRTSchemaPath.setLayoutData(gdTxtparentPath);

		// button browse
		Button btnBrowsePIRT = new Button(container, SWT.CENTER);
		btnBrowsePIRT.setText(RscTools.getString(RscConst.MSG_BTN_BROWSE));
		GridData gdBtnBrowse = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gdBtnBrowse.minimumWidth = PartsResourceConstants.CREDCONFWIZARD_BTN_WIDTH;
		btnBrowsePIRT.setLayoutData(gdBtnBrowse);
		btnBrowsePIRT.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(getShell());
			dialog.setFilterPath(textPIRTSchemaPath.getText());
			dialog.setFilterExtensions(confFileDefaultExtensions);
			String selectedPath = dialog.open();
			if (selectedPath != null) {
				textPIRTSchemaPath.setText(selectedPath);
			}
			checkSchemaFile(ConfigurationFileType.PIRT);
		});
	}

	/**
	 * Render PCMM content
	 */
	private void renderPCMM() {
		/**
		 * PCMM schema selection
		 */
		// label
		Label labelPCMMSchemaPath = new Label(container, SWT.NONE);
		labelPCMMSchemaPath.setText(RscTools.getString(RscConst.MSG_CONFFILEWIZARD_PCMM_SCHEMA_PATH));
		GridData gdlabelPCMMSchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelPCMMSchemaPath.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		labelPCMMSchemaPath.setLayoutData(gdlabelPCMMSchemaPath);

		// text path
		textPCMMSchemaPath = new Text(container, SWT.BORDER | SWT.SINGLE);
		textPCMMSchemaPath.setText(pcmmSchemaDefaultPath);
		textPCMMSchemaPath.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				// check file validity
				checkSchemaFile(ConfigurationFileType.PCMM);
			}

		});
		GridData gdTxtPCMMparentPath = new GridData(GridData.FILL_HORIZONTAL);
		textPCMMSchemaPath.setLayoutData(gdTxtPCMMparentPath);

		// button browse
		Button btnBrowsePCMM = new Button(container, SWT.CENTER);
		btnBrowsePCMM.setText(RscTools.getString(RscConst.MSG_BTN_BROWSE));
		GridData gdBtnBrowsePCMM = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gdBtnBrowsePCMM.minimumWidth = PartsResourceConstants.CREDCONFWIZARD_BTN_WIDTH;
		btnBrowsePCMM.setLayoutData(gdBtnBrowsePCMM);
		btnBrowsePCMM.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(getShell());
			dialog.setFilterPath(textPCMMSchemaPath.getText());
			dialog.setFilterExtensions(confFileDefaultExtensions);
			String selectedPath = dialog.open();
			if (selectedPath != null) {
				textPCMMSchemaPath.setText(selectedPath);
			}
			checkSchemaFile(ConfigurationFileType.PCMM);
		});
	}

	/** {@inheritDoc} */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			initDataFromPreviousPage();
		}
	}

	/**
	 * @return the configuration schema
	 */
	public ConfigurationSchema getConfigurationSchema() {
		return confSchema;
	}

	/**
	 * @return true if generate folder structure is checked, otherwise false
	 */
	public boolean getGenerateFolderStructure() {
		return chbxGenerateFolderStructure.getSelection();
	}

	/**
	 * Initializes data from previous page
	 */
	private void initDataFromPreviousPage() {
		// default setup
		evidenceFolderStructurePath = parent.getPageNewCredibilityFile().getContainerFullPath();
		chbxGenerateFolderStructure.setSelection(true);
		chbxGenerateFolderStructure.notifyListeners(SWT.Selection, new Event());

		// validate data
		Stream.of(ConfigurationFileType.values()).forEach(this::checkSchemaFile);
		checkPathToGenerateFolderStructure();
		updateMessages(false);
	}

	/**
	 * Checks the schema file
	 */
	private void checkSchemaFile(ConfigurationFileType type) {
		// Remove all the error messages before checking
		if (!mapErrMsg.containsKey(type) || mapErrMsg.get(type) == null) {
			mapErrMsg.put(type, new HashSet<>());
		}
		mapErrMsg.get(type).clear();

		reloadConfigurationSchemaValue(type);

		// check file path validity
		Set<String> checkSchemaFile = ConfigurationFileValidator.checkSchemaFile(confSchema, type);
		mapErrMsg.get(type).addAll(checkSchemaFile);

		// update the messages
		updateMessages(true);
	}

	/**
	 * Reload the configuration schema values
	 */
	private void reloadAllConfigurationSchemaValues() {
		confSchema.put(ConfigurationFileType.PIRT, textPIRTSchemaPath.getText());
		confSchema.put(ConfigurationFileType.QOIPLANNING, textQoIPlanningSchemaPath.getText());
		confSchema.put(ConfigurationFileType.PCMM, textPCMMSchemaPath.getText());
		confSchema.put(ConfigurationFileType.UNCERTAINTY, textUncertaintySchemaPath.getText());
		confSchema.put(ConfigurationFileType.SYSTEM_REQUIREMENT, textSystemRequirementSchemaPath.getText());
		confSchema.put(ConfigurationFileType.DECISION, textDecisionSchemaPath.getText());
	}

	/**
	 * Reload the configuration schema value for type
	 * 
	 * @param type the type to reload
	 */
	private void reloadConfigurationSchemaValue(ConfigurationFileType type) {
		if (ConfigurationFileType.PIRT.equals(type)) {
			confSchema.put(type, textPIRTSchemaPath.getText());
		} else if (ConfigurationFileType.QOIPLANNING.equals(type)) {
			confSchema.put(type, textQoIPlanningSchemaPath.getText());
		} else if (ConfigurationFileType.PCMM.equals(type)) {
			confSchema.put(type, textPCMMSchemaPath.getText());
		} else if (ConfigurationFileType.UNCERTAINTY.equals(type)) {
			confSchema.put(type, textUncertaintySchemaPath.getText());
		} else if (ConfigurationFileType.SYSTEM_REQUIREMENT.equals(type)) {
			confSchema.put(type, textSystemRequirementSchemaPath.getText());
		} else if (ConfigurationFileType.DECISION.equals(type)) {
			confSchema.put(type, textDecisionSchemaPath.getText());
		}
	}

	/**
	 * Validate the generate folder structure path (from previous page) if
	 * chbxGenerateFolderStructure is checked
	 */
	private void checkPathToGenerateFolderStructure() {
		// Initialize
		boolean valid = false;

		// if chbxGenerateFolderStructure is checked, validate folder path, otherwise do
		// not validate
		if (chbxGenerateFolderStructure.getSelection()) {
			if (evidenceFolderStructurePath != null) {
				IContainer evidenceFolderStructure = null;
				// if it is a project
				if (evidenceFolderStructurePath.segmentCount() == 1) {
					evidenceFolderStructure = WorkspaceTools
							.getProjectInWorkspaceForPath(evidenceFolderStructurePath.segment(0));
				} else if (evidenceFolderStructurePath.segmentCount() > 1) {
					// maybe it is a folder and not a project
					evidenceFolderStructure = WorkspaceTools.getFolderInWorkspaceForPath(evidenceFolderStructurePath);
				}
				valid = evidenceFolderStructure != null && evidenceFolderStructure.exists();
			}
		} else {
			valid = true;
		}

		if (valid) {
			errMsgCommon.remove(RscTools.getString(RscConst.ERR_CONFFILEWIZARD_BAD_FOLDER_PATH));
		} else {
			errMsgCommon.add(RscTools.getString(RscConst.ERR_CONFFILEWIZARD_BAD_FOLDER_PATH));
		}

		updateMessages(true);
	}

	/**
	 * Update the error messages of wizard and the page complete field
	 */
	private void updateMessages(boolean error) {

		final StringBuilder errorString = new StringBuilder();
		mapErrMsg.forEach((type, set) -> errorString
				.append(errorString.length() == 0 ? RscTools.empty() : RscTools.CARRIAGE_RETURN)
				.append(set.stream().map(Object::toString).collect(Collectors.joining(RscTools.CARRIAGE_RETURN))));

		if (!errMsgCommon.isEmpty()) {
			errorString.append(errorString.length() == 0 ? RscTools.empty() : RscTools.CARRIAGE_RETURN);
			errorString.append(
					errMsgCommon.stream().map(Object::toString).collect(Collectors.joining(RscTools.CARRIAGE_RETURN)));
		}

		// if empty, set the message string to null because of the wizard
		// if it is an error message
		boolean emptyMsgError = errorString.length() == 0;
		if (error) {
			setMessage(null);
			setErrorMessage(emptyMsgError ? null : errorString.toString());
		} else { // otherwise
			setErrorMessage(null);
			setMessage(emptyMsgError ? null : errorString.toString());
		}

		// set page complete if there is no messages
		setPageComplete(emptyMsgError);
	}
}
