/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.IWizardPage;
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

import gov.sandia.cf.application.configuration.ConfigurationFileValidator;
import gov.sandia.cf.constants.configuration.ConfigurationFileType;
import gov.sandia.cf.model.dto.configuration.ConfigurationSchema;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.widgets.FormFactory;
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
public class NewCFProcessLocalSetupAdvancedPage extends WizardPage implements INewCFProcessLocalSetupPage {
	/**
	 * the parent wizard
	 */
	private NewCFProcessWizard parent;
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
	public NewCFProcessLocalSetupAdvancedPage(NewCFProcessWizard parent) {
		super(RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_PAGENAME));
		setTitle(RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUPADVANCED_PAGE_TITLE));
		setDescription(RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_DESCRIPTION));

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
		textSystemRequirementSchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_REQUIREMENT_SCHEMA_PATH),
				sysRequirementSchemaDefaultPath, ConfigurationFileType.SYSTEM_REQUIREMENT);
		textQoIPlanningSchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_QOIPLANNING_SCHEMA_PATH),
				qoiPlanningSchemaDefaultPath, ConfigurationFileType.QOIPLANNING);
		textUncertaintySchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_UNCERTAINTY_SCHEMA_PATH),
				uncertaintySchemaDefaultPath, ConfigurationFileType.UNCERTAINTY);
		textDecisionSchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_DECISION_SCHEMA_PATH),
				decisionSchemaDefaultPath, ConfigurationFileType.DECISION);
		textPIRTSchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_PIRT_SCHEMA_PATH), pirtSchemaDefaultPath,
				ConfigurationFileType.PIRT);
		textPCMMSchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_PCMM_SCHEMA_PATH), pcmmSchemaDefaultPath,
				ConfigurationFileType.PCMM);

		// generate credibility evidence folder structure check-box
		// set by default to true
		chbxGenerateFolderStructure = new Button(container, SWT.CHECK);
		chbxGenerateFolderStructure
				.setText(RscTools.getString(RscConst.ERR_NEWCFPROCESS_LOCALSETUP_PAGE_CHBX_EVID_STRUCT));
		chbxGenerateFolderStructure.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button btn = (Button) event.getSource();
				String text = btn.getSelection()
						? RscTools.getString(RscConst.ERR_NEWCFPROCESS_LOCALSETUP_PAGE_EVID_PARENT_PATH,
								evidenceFolderStructurePath)
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

		// add Back to default Button
		Button btnDefault = new Button(container, SWT.NONE);
		btnDefault.setText(RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUPADVANCED_PAGE_BTN_BACK_TO_DEFAULT));
		btnDefault.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 2, 1));
		btnDefault.addListener(SWT.Selection, event -> this.parent.openDefaultSetupPage());

		// check paths
		Stream.of(ConfigurationFileType.values()).forEach(this::checkSchemaFile);
		checkPathToGenerateFolderStructure();
	}

	/**
	 * Render schema file content.
	 */
	private Text renderSchemaFileContent(String title, String defaultValue, ConfigurationFileType type) {

		// label
		Label labelSchemaPath = new Label(container, SWT.NONE);
		labelSchemaPath.setText(title);
		GridData gdlabelSchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelSchemaPath.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		labelSchemaPath.setLayoutData(gdlabelSchemaPath);

		// text path
		Text textTmp = new Text(container, SWT.BORDER | SWT.SINGLE);
		textTmp.setText(defaultValue);
		textTmp.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				// check file validity
				checkSchemaFile(type);
			}

		});
		GridData gdTxtPath = new GridData(GridData.FILL_HORIZONTAL);
		textTmp.setLayoutData(gdTxtPath);

		// button browse
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.setText(RscTools.getString(RscConst.MSG_BTN_BROWSE));
		btnBrowse.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(getShell());
			dialog.setFilterPath(FileTools.getParentFolder(textTmp.getText()));
			dialog.setFileName(FileTools.getFileName(textTmp.getText()));
			dialog.setFilterExtensions(new String[] { FileTools.YML_FILTER, FileTools.YAML_FILTER });
			String selectedPath = dialog.open();
			if (selectedPath != null) {
				textTmp.setText(selectedPath);
			}
			checkSchemaFile(type);
		});

		return textTmp;
	}

	/** {@inheritDoc} */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			initDataFromPreviousPage();
		}
	}

	/** {@inheritDoc} */
	@Override
	public ConfigurationSchema getConfigurationSchema() {
		return confSchema;
	}

	/** {@inheritDoc} */
	@Override
	public String getConfigurationFolderDefaultPath() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
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
			errMsgCommon.remove(RscTools.getString(RscConst.ERR_NEWCFPROCESS_LOCALSETUP_PAGE_BAD_STRUCT_FOLDER_PATH));
		} else {
			errMsgCommon.add(RscTools.getString(RscConst.ERR_NEWCFPROCESS_LOCALSETUP_PAGE_BAD_STRUCT_FOLDER_PATH));
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

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public IWizardPage getPreviousPage() {
		if (PrefTools.getPreferenceBoolean(PrefTools.DEVOPTS_CONCURRENCY_SUPPORT_KEY).booleanValue()) {
			return parent.getPageBackendSelection();
		} else {
			return parent.getPageNewCredibilityFile();
		}
	}
}
