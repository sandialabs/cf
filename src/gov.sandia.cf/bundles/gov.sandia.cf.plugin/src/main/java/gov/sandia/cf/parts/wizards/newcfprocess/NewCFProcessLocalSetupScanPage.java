/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import gov.sandia.cf.application.configuration.ConfigurationFileValidator;
import gov.sandia.cf.constants.configuration.ConfigurationFileType;
import gov.sandia.cf.model.dto.configuration.ConfigurationSchema;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.preferences.PrefTools;
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
/**
 * @author Didier Verstraete
 *
 */
public class NewCFProcessLocalSetupScanPage extends WizardPage implements INewCFProcessLocalSetupPage {
	/**
	 * the parent wizard
	 */
	private NewCFProcessWizard parent;

	/**
	 * the configuration folder text
	 */
	private Text textConfigurationFolder;
	/**
	 * the PIRT path combobox
	 */
	private ComboViewer cbxPIRTSchemaPath;
	/**
	 * the QoI Planning path combobox
	 */
	private ComboViewer cbxQoIPlanningSchemaPath;
	/**
	 * the PCMM path combobox
	 */
	private ComboViewer cbxPCMMSchemaPath;
	/**
	 * the Uncertainty path combobox
	 */
	private ComboViewer cbxUncertaintySchemaPath;
	/**
	 * the SystemRequirement path combobox
	 */
	private ComboViewer cbxSystemRequirementSchemaPath;
	/**
	 * the Decision path combobox
	 */
	private ComboViewer cbxDecisionSchemaPath;
	/**
	 * the default path for configuration folder
	 */
	private String configurationFolderDefaultPath;
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

	private Map<ConfigurationFileType, Set<File>> mapPossibleConfigurationFile;

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
	public NewCFProcessLocalSetupScanPage(NewCFProcessWizard parent) {
		super(RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_PAGENAME));
		setTitle(RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_TITLE));
		setDescription(RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_DESCRIPTION));

		this.parent = parent;
		this.evidenceFolderStructurePath = new Path(RscTools.empty());

		this.mapErrMsg = new EnumMap<>(ConfigurationFileType.class);

		this.errMsgCommon = new HashSet<>();
	}

	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.numColumns = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		container.setLayout(layout);

		// Render sub-composites
		renderConfigurationFolderScan();
		cbxSystemRequirementSchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_REQUIREMENT_SCHEMA_PATH));
		cbxQoIPlanningSchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_QOIPLANNING_SCHEMA_PATH));
		cbxUncertaintySchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_UNCERTAINTY_SCHEMA_PATH));
		cbxDecisionSchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_DECISION_SCHEMA_PATH));
		cbxPIRTSchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_PIRT_SCHEMA_PATH));
		cbxPCMMSchemaPath = renderSchemaFileContent(
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_PCMM_SCHEMA_PATH));

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
		GridData gdBtnGenEvidSchema = new GridData(GridData.FILL_HORIZONTAL);
		gdBtnGenEvidSchema.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		gdBtnGenEvidSchema.verticalIndent = 15;
		chbxGenerateFolderStructure.setLayoutData(gdBtnGenEvidSchema);

		// label
		lblGenerateEvidenceFolderStructure = new Label(container, SWT.NONE);
		GridData gdLabelGenEvidSchema = new GridData(GridData.FILL_HORIZONTAL);
		gdLabelGenEvidSchema.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		lblGenerateEvidenceFolderStructure.setLayoutData(gdLabelGenEvidSchema);

		// global
		setControl(container);
		confSchema = new ConfigurationSchema();
		reloadAllConfigurationSchemaValues();
		checkSchemaFiles();
		checkPathToGenerateFolderStructure();

		// add Advanced Button
		Button btnAdvanced = new Button(container, SWT.NONE);
		btnAdvanced.setText(RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_BTN_ADVANCED));
		btnAdvanced.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 2, 1));
		btnAdvanced.addListener(SWT.Selection, event -> this.parent.openAdvancedSetupPage());

		// init preferences
		// Get configuration folder default path in preferences
		configurationFolderDefaultPath = PrefTools.getPreference(PrefTools.CONF_SCHEMA_FOLDER_LAST_PATH_KEY);

		// if not exists, set workspace path
		File selectedFolder = new File(configurationFolderDefaultPath);
		if (!selectedFolder.exists()) {
			configurationFolderDefaultPath = WorkspaceTools.getWorkspacePathToString();
		}
		textConfigurationFolder.setText(configurationFolderDefaultPath);
	}

	/**
	 * Render the configuration folder
	 */
	private void renderConfigurationFolderScan() {

		GridData gdlabelPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelPath.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;

		// label
		FormFactory.createLabel(container,
				RscTools.getString(RscConst.MSG_NEWCFPROCESS_LOCALSETUP_PAGE_CONF_FOLDER_PATH), gdlabelPath);

		// text path
		textConfigurationFolder = new Text(container, SWT.BORDER | SWT.SINGLE);
		textConfigurationFolder.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				configurationFolderDefaultPath = textConfigurationFolder.getText();
				// check file validity
				checkSchemaFiles();
			}

		});
		textConfigurationFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// button browse
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.setText(RscTools.getString(RscConst.MSG_BTN_BROWSE));
		btnBrowse.addListener(SWT.Selection, event -> {
			DirectoryDialog dialog = new DirectoryDialog(getShell());
			dialog.setFilterPath(textConfigurationFolder.getText());
			String selectedPath = dialog.open();
			if (selectedPath != null) {
				textConfigurationFolder.setText(selectedPath);
				configurationFolderDefaultPath = textConfigurationFolder.getText();
			}
			checkSchemaFiles();
		});
	}

	/**
	 * Render Schema File content.
	 *
	 * @param title the title
	 * @return the combo viewer
	 */
	private ComboViewer renderSchemaFileContent(String title) {

		GridData gdLabel = new GridData(GridData.FILL_HORIZONTAL);
		gdLabel.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		gdLabel.verticalIndent = 10;

		// label
		FormFactory.createLabel(container, title, gdLabel);

		GridData gdCbx = new GridData(GridData.FILL_HORIZONTAL);
		gdCbx.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;

		// text path
		ComboViewer cbxTemp = FormFactory.createCombo(container, null, new ArrayList<>(), new LabelProvider() {
			@Override
			public String getText(Object element) {
				return element != null ? ((File) element).getName() : RscTools.empty();
			}
		});
		cbxTemp.getCombo().setLayoutData(gdCbx);
		cbxTemp.addSelectionChangedListener(event -> reloadAllConfigurationSchemaValues());

		return cbxTemp;
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
	public boolean getGenerateFolderStructure() {
		return chbxGenerateFolderStructure.getSelection();
	}

	/** {@inheritDoc} */
	@Override
	public String getConfigurationFolderDefaultPath() {
		return textConfigurationFolder.getText();
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
		checkSchemaFiles();
		checkPathToGenerateFolderStructure();
		updateMessages(false);
	}

	/**
	 * Checks the schema file
	 */
	private void checkSchemaFiles() {
		// Remove all the error messages before checking
		mapErrMsg.clear();

		// load possible configuration files
		mapPossibleConfigurationFile = ConfigurationFileValidator
				.parseConfigurationFolder(textConfigurationFolder.getText());

		reloadSchemaFilesCombo();

		// reload values
		reloadAllConfigurationSchemaValues();

		// check configuration folder path validity
		if (isValidConfigurationFolder()) {
			errMsgCommon.remove(RscTools.getString(RscConst.ERR_NEWCFPROCESS_LOCALSETUP_PAGE_BAD_CONF_FOLDER_PATH));
		} else {
			errMsgCommon.add(RscTools.getString(RscConst.ERR_NEWCFPROCESS_LOCALSETUP_PAGE_BAD_CONF_FOLDER_PATH));
		}

		// update the messages
		updateMessages(true);
	}

	/**
	 * Reload the configuration schema values
	 */
	private void reloadAllConfigurationSchemaValues() {
		confSchema.put(ConfigurationFileType.PIRT, getSelectedFile(cbxPIRTSchemaPath));
		confSchema.put(ConfigurationFileType.QOIPLANNING, getSelectedFile(cbxQoIPlanningSchemaPath));
		confSchema.put(ConfigurationFileType.PCMM, getSelectedFile(cbxPCMMSchemaPath));
		confSchema.put(ConfigurationFileType.UNCERTAINTY, getSelectedFile(cbxUncertaintySchemaPath));
		confSchema.put(ConfigurationFileType.SYSTEM_REQUIREMENT, getSelectedFile(cbxSystemRequirementSchemaPath));
		confSchema.put(ConfigurationFileType.DECISION, getSelectedFile(cbxDecisionSchemaPath));
	}

	/**
	 * Reload the schema file combos values
	 */
	private void reloadSchemaFilesCombo() {
		if (mapPossibleConfigurationFile != null) {
			mapPossibleConfigurationFile.forEach((type, listFiles) -> {

				// construct list files
				List<File> values = new ArrayList<>();
				File emptyFile = new File(RscTools.empty());
				ISelection selection = new StructuredSelection(emptyFile);
				values.add(emptyFile);

				if (listFiles != null && !listFiles.isEmpty()) {
					values.addAll(listFiles);

					// get selection from preference first
					File preferenceFileFromSelection = getPreferenceFileFromSelection(type, listFiles);
					if (preferenceFileFromSelection != null) {
						selection = new StructuredSelection(preferenceFileFromSelection);
					} else {
						selection = new StructuredSelection(listFiles.iterator().next());
					}
				}

				// load comboboxes
				loadSchemaFileCombo(type, values, selection);
			});
		}
	}

	/**
	 * @param type      the file type
	 * @param listFiles the file list
	 * @return the preference file if found in the list, otherwise null
	 */
	private File getPreferenceFileFromSelection(ConfigurationFileType type, Set<File> listFiles) {

		File selection = null;

		if (ConfigurationFileType.PIRT.equals(type)) {
			return getPreferenceFileFromSelection(PrefTools.getPreference(PrefTools.PIRT_SCHEMA_FILE_LAST_PATH_KEY),
					listFiles);
		} else if (ConfigurationFileType.QOIPLANNING.equals(type)) {
			return getPreferenceFileFromSelection(
					PrefTools.getPreference(PrefTools.QOIPLANNING_SCHEMA_FILE_LAST_PATH_KEY), listFiles);
		} else if (ConfigurationFileType.PCMM.equals(type)) {
			return getPreferenceFileFromSelection(PrefTools.getPreference(PrefTools.PCMM_SCHEMA_FILE_LAST_PATH_KEY),
					listFiles);
		} else if (ConfigurationFileType.UNCERTAINTY.equals(type)) {
			return getPreferenceFileFromSelection(
					PrefTools.getPreference(PrefTools.UNCERTAINTY_SCHEMA_FILE_LAST_PATH_KEY), listFiles);
		} else if (ConfigurationFileType.SYSTEM_REQUIREMENT.equals(type)) {
			return getPreferenceFileFromSelection(
					PrefTools.getPreference(PrefTools.SYSTEM_REQUIREMENT_SCHEMA_FILE_LAST_PATH_KEY), listFiles);
		} else if (ConfigurationFileType.DECISION.equals(type)) {
			return getPreferenceFileFromSelection(PrefTools.getPreference(PrefTools.DECISION_SCHEMA_FILE_LAST_PATH_KEY),
					listFiles);
		}

		return selection;
	}

	/**
	 * @param preference the preference path
	 * @param listFiles  the file list
	 * @return the preference file if found in the list, otherwise null
	 */
	private File getPreferenceFileFromSelection(String preference, Set<File> listFiles) {

		if (listFiles == null || listFiles.isEmpty()) {
			return null;
		}

		File selection = null;

		if (StringUtils.isBlank(preference)) {
			selection = listFiles.iterator().next();
		} else {
			Optional<File> optFile = listFiles.stream().filter(Objects::nonNull)
					.filter(file -> preference.equals(file.getPath())).findFirst();
			if (optFile.isPresent())
				selection = optFile.get();
		}

		return selection;
	}

	/**
	 * Load the schema files comboboxes
	 * 
	 * @param type      the file type
	 * @param values    the values
	 * @param selection the selection
	 */
	private void loadSchemaFileCombo(ConfigurationFileType type, List<File> values, ISelection selection) {

		// load comboboxes
		if (ConfigurationFileType.PIRT.equals(type)) {
			cbxPIRTSchemaPath.setInput(values);
			cbxPIRTSchemaPath.setSelection(selection);
		} else if (ConfigurationFileType.QOIPLANNING.equals(type)) {
			cbxQoIPlanningSchemaPath.setInput(values);
			cbxQoIPlanningSchemaPath.setSelection(selection);
		} else if (ConfigurationFileType.PCMM.equals(type)) {
			cbxPCMMSchemaPath.setInput(values);
			cbxPCMMSchemaPath.setSelection(selection);
		} else if (ConfigurationFileType.UNCERTAINTY.equals(type)) {
			cbxUncertaintySchemaPath.setInput(values);
			cbxUncertaintySchemaPath.setSelection(selection);
		} else if (ConfigurationFileType.SYSTEM_REQUIREMENT.equals(type)) {
			cbxSystemRequirementSchemaPath.setInput(values);
			cbxSystemRequirementSchemaPath.setSelection(selection);
		} else if (ConfigurationFileType.DECISION.equals(type)) {
			cbxDecisionSchemaPath.setInput(values);
			cbxDecisionSchemaPath.setSelection(selection);
		}
	}

	/**
	 * @return true if at least one valid file is found for the current folder
	 */
	private boolean isValidConfigurationFolder() {
		for (Set<File> listFiles : mapPossibleConfigurationFile.values()) {
			if (listFiles != null && !listFiles.isEmpty()) {
				return true;
			}
		}

		return false;
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
	 * Update the error messages of wizard and the page complete field.
	 *
	 * @param error the error
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

	/**
	 * Gets the selected file.
	 *
	 * @param cbx the cbx
	 * @return the editable field value
	 */
	private File getSelectedFile(ComboViewer cbx) {
		// Get selected value
		IStructuredSelection selection = (IStructuredSelection) cbx.getSelection();
		if (!selection.isEmpty()) {
			// Get parameter value
			return (File) selection.getFirstElement();
		}
		return null;
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
