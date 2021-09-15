/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The Import Selector widget with helper
 * 
 * @author Didier Verstraete
 *
 */
public class ImportSelectorWidget extends Composite {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportSelectorWidget.class);

	// editable fields
	private TextWidget textViewer;

	private String title;
	private String message;
	private String fileType;
	private String preferenceKey;
	/**
	 * The resource manager
	 */
	private ResourceManager rscMgr;

	/**
	 * the extensions needed to filter file browser
	 */
	private String[] confFileDefaultExtensions = new String[] { FileTools.YML_FILTER, FileTools.YAML_FILTER };

	private ButtonTheme importButton;

	/**
	 * The constructor * @param rscMgr the resource manager used to manage the
	 * resources (fonts, colors, images, cursors...)
	 * 
	 * @param rscMgr        the system resource manager
	 * @param parent        the composite parent
	 * @param style         the style
	 * @param title         the title
	 * @param message       the message
	 * @param fileType      the file type
	 * @param preferenceKey the preference key to store the input
	 */
	public ImportSelectorWidget(ResourceManager rscMgr, Composite parent, int style, String title, String message,
			String fileType, String preferenceKey) {
		super(parent, style);

		Assert.isNotNull(rscMgr);
		this.rscMgr = rscMgr;

		this.title = title;
		this.message = message;
		this.fileType = fileType;
		this.preferenceKey = preferenceKey;

		// create control
		createControl();
	}

	/**
	 * Create the form field content
	 */
	private void createControl() {

		// QoI Planning main composite
		GridLayout gridLayout = new GridLayout(2, false);
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.setLayout(gridLayout);
		this.setBackground(this.getParent().getBackground());

		// QoI Planning collapse
		new CollapsibleWidget(rscMgr, getParent(), SWT.FILL | SWT.BORDER, this, title, false, true);

		// label
		GridData gdlabelQoIPlanningSchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelQoIPlanningSchemaPath.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		Label label = FormFactory.createLabel(this, message, gdlabelQoIPlanningSchemaPath);
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(rscMgr, label);

		// text path
		textViewer = FormFactory.createTextWidget(rscMgr, this, true, null);
		((GridData) textViewer.getLayoutData()).verticalAlignment = SWT.CENTER;
		textViewer.setBackground(this.getBackground());

		// button browse
		Map<String, Object> optionsBtnBrowse = new HashMap<>();
		optionsBtnBrowse.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowse.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowse.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme browseButton = FormFactory.createButton(rscMgr, this, null, optionsBtnBrowse);
		((GridData) browseButton.getLayoutData()).verticalAlignment = SWT.CENTER;

		// button import
		Map<String, Object> optionsBtnImport = new HashMap<>();
		optionsBtnImport.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_IMPORT));
		optionsBtnImport.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_IMPORT);
		optionsBtnImport.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnImport.put(ButtonTheme.OPTION_OUTLINE, false);
		optionsBtnImport.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> {

			// clear text helper
			textViewer.clearHelper();

			// check import file path validity
			boolean importFileValid = false;
			try {
				importFileValid = isValidImportFile();
			} catch (CredibilityException e) {
				logger.warn(e.getMessage(), e);
				textViewer.setHelper(NotificationFactory.getNewWarning(e.getMessage()));
			}

			if (importFileValid) {
				// import the selected file
				doImport();
			}
		});
		importButton = FormFactory.createButton(rscMgr, this, null, optionsBtnImport);
		GridData gdBtnImport = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false);
		gdBtnImport.horizontalSpan = 2;
		gdBtnImport.verticalAlignment = GridData.CENTER;
		importButton.setLayoutData(gdBtnImport);
		importButton.setEnabled(StringUtils.isNotEmpty(textViewer.getValue()));

		// text input listener
		textViewer.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent event) {
				checkImportFile();
			}
		});

		// browse button listener
		browseButton.addListener(SWT.Selection, event -> {
			FileDialog dialog = new FileDialog(getShell());
			dialog.setFilterPath(textViewer.getValue());
			dialog.setFilterExtensions(confFileDefaultExtensions);
			String selectedPath = dialog.open();
			textViewer.setValue(selectedPath);
			checkImportFile();
		});

		// set default value
		String pref = PrefTools.getPreference(preferenceKey);
		textViewer.setValue(StringUtils.isNotEmpty(pref) ? pref : RscTools.empty());
		checkImportFile();
	}

	/**
	 * Check the import file and change components behavior
	 */
	private void checkImportFile() {

		// clear text helper
		textViewer.clearHelper();

		// check file path validity
		boolean importFileValid = false;

		try {

			importFileValid = isValidImportFile();

			if (importFileValid) {
				// set preference
				PrefTools.setPreference(preferenceKey, textViewer.getValue());
			}

			// set import button
			importButton.setEnabled(importFileValid);

		} catch (CredibilityException e) {
			logger.warn(e.getMessage(), e);
			textViewer.setHelper(NotificationFactory.getNewWarning(e.getMessage()));
		}

	}

	/**
	 * @return the text value
	 */
	public String getFilePath() {
		return textViewer.getValue();
	}

	/**
	 * @return the text value
	 */
	public File getFile() {
		String schemaPath = textViewer.getValue();
		return !StringUtils.isBlank(schemaPath) ? new File(schemaPath) : null;
	}

	/**
	 * @return the import file type
	 */
	protected String getFileType() {
		return fileType;
	}

	/**
	 * @return true if the import file is a valid file, otherwise false.
	 * @throws CredibilityException if the import file doesn't exist
	 */
	protected boolean isValidImportFile() throws CredibilityException {

		String schemaPath = textViewer.getValue();

		if (StringUtils.isBlank(schemaPath)) {
			return false;
		}

		File schemaFile = new File(schemaPath);

		// check file existence
		if (!schemaFile.exists()) {
			throw new CredibilityException(
					RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_FILE_NOTEXISTS, fileType));
		}

		// check file validity
		isValidImportFileRule();

		return true;
	}

	/**
	 * To be inherited
	 * 
	 * @return true if the file is valid
	 * @throws CredibilityException if an error occurs
	 */
	public boolean isValidImportFileRule() throws CredibilityException {
		return true;
	}

	/**
	 * Do the import action. Please override.
	 */
	public void doImport() {
		// need to be overrided
	}
}
