/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.decision.YmlReaderDecisionSchema;
import gov.sandia.cf.application.configuration.pcmm.YmlReaderPCMMSchema;
import gov.sandia.cf.application.configuration.pirt.YmlReaderPIRTSchema;
import gov.sandia.cf.application.configuration.qoiplanning.YmlReaderQoIPlanningSchema;
import gov.sandia.cf.application.configuration.requirement.YmlReaderSystemRequirementSchema;
import gov.sandia.cf.application.configuration.uncertainty.YmlReaderUncertaintySchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.widgets.ImportSelectorWidget;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Configuration view: the configuration view
 * 
 * @author Didier Verstraete
 *
 */
public class ImportConfigurationView extends ACredibilitySubView<ConfigurationViewManager> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportConfigurationView.class);

	/**
	 * Controller
	 */
	private ImportConfigurationViewController viewCtrl;

	/**
	 * The main composite
	 */
	private Composite mainComposite;

	private ImportSelectorWidget textQoIPlanningSchemaPath;
	private ImportSelectorWidget textPIRTSchemaPath;
	private ImportSelectorWidget textPCMMSchemaPath;
	private ImportSelectorWidget textUncertaintySchemaPath;
	private ImportSelectorWidget textRequirementsSchemaPath;
	private ImportSelectorWidget textDecisionSchemaPath;

	/**
	 * @param viewManager the view manager
	 * @param parent      the parent composite
	 * @param style       the view style
	 */
	public ImportConfigurationView(ConfigurationViewManager viewManager, Composite parent, int style) {
		super(viewManager, parent, style);

		this.viewCtrl = new ImportConfigurationViewController(this);

		// create the view
		renderPage();
	}

	/** {@inheritDoc} */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_TITLE);
	}

	/** {@inheritDoc} */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_ITEMTITLE);
	}

	/**
	 * Creates the page
	 */
	private void renderPage() {

		// Render main table
		renderMainComposite();

		// Render footer buttons
		renderFooterButtons();

		// Refresh data and Save state
		refresh();
	}

	/**
	 * Render main composite
	 */
	private void renderMainComposite() {

		logger.debug("Creating Import Configuration view content"); //$NON-NLS-1$

		Composite first = new Composite(this, SWT.NONE);
		first.setLayout(new GridLayout(1, false));
		GridData firstData = new GridData(SWT.FILL, SWT.FILL, true, true);
		first.setLayoutData(firstData);

		ScrolledComposite firstScroll = new ScrolledComposite(first, SWT.V_SCROLL);
		firstScroll.setLayout(new GridLayout(1, false));
		firstScroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		firstScroll.addListener(SWT.Resize, event -> {
			int width = firstScroll.getClientArea().width;
			firstScroll.setMinSize(firstScroll.getParent().computeSize(width, SWT.DEFAULT));
		});

		mainComposite = new Composite(firstScroll, SWT.NONE);
		GridLayout gdMainComposite = new GridLayout(1, false);
		mainComposite.setLayout(gdMainComposite);
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainComposite.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));

		// Main table composite
		firstScroll.setContent(mainComposite);
		firstScroll.setExpandHorizontal(true);
		firstScroll.setExpandVertical(true);
		firstScroll.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		// Render sub-composites
		renderSystemRequirement();
		renderQoIPlanning();
		renderUncertainty();
		renderDecision();
		renderPIRT();
		renderPCMM();
	}

	/**
	 * Render QoI Planning Composite
	 */
	private void renderQoIPlanning() {
		String title = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_QOIPLANNING_TITLE);
		String message = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_QOIPLANNING_SCHEMAPATH);
		String fileType = RscTools.getString(RscConst.MSG_QOIPLANNING);
		String preferenceKey = PrefTools.CONF_IMPORT_QOIPLANNING_SCHEMA_FILE_LAST_PATH_KEY;
		textQoIPlanningSchemaPath = new ImportSelectorWidget(getViewManager().getRscMgr(), mainComposite, SWT.NONE, title, message, fileType,
				preferenceKey) {
			@Override
			public boolean isValidImportFileRule() throws CredibilityException {
				// check file validity
				YmlReaderQoIPlanningSchema qoiPlanningSchemaReader = new YmlReaderQoIPlanningSchema();
				if (!qoiPlanningSchemaReader.isValid(getFile())) {
					throw new CredibilityException(
							RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_FILE_NOTVALID, getFileType()));
				}
				return true;
			}

			@Override
			public void doImport() {
				// import new QoI Planning Schema
				viewCtrl.importQoIPlanningSchema();
			}
		};
	}

	/**
	 * Render PIRT Composite
	 */
	private void renderPIRT() {
		String title = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_PIRT_TITLE);
		String message = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_PIRT_SCHEMAPATH);
		String fileType = RscTools.getString(RscConst.MSG_PIRT);
		String preferenceKey = PrefTools.CONF_IMPORT_PIRT_SCHEMA_FILE_LAST_PATH_KEY;
		textPIRTSchemaPath = new ImportSelectorWidget(getViewManager().getRscMgr(), mainComposite, SWT.NONE, title, message, fileType,
				preferenceKey) {
			@Override
			public boolean isValidImportFileRule() throws CredibilityException {
				// check file validity
				YmlReaderPIRTSchema schemaReader = new YmlReaderPIRTSchema();
				if (!schemaReader.isValid(getFile())) {
					throw new CredibilityException(
							RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_FILE_NOTVALID, getFileType()));
				}
				return true;
			}

			@Override
			public void doImport() {
				// import new PIRT Schema
				viewCtrl.importPIRTSchema();
			}
		};
	}

	/**
	 * Render PCMM Composite
	 */
	private void renderPCMM() {
		String title = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_PCMM_TITLE);
		String message = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_PCMM_SCHEMAPATH);
		String fileType = RscTools.getString(RscConst.MSG_PCMM);
		String preferenceKey = PrefTools.CONF_IMPORT_PCMM_SCHEMA_FILE_LAST_PATH_KEY;
		textPCMMSchemaPath = new ImportSelectorWidget(getViewManager().getRscMgr(), mainComposite, SWT.NONE, title, message, fileType,
				preferenceKey) {
			@Override
			public boolean isValidImportFileRule() throws CredibilityException {
				// check file validity
				YmlReaderPCMMSchema schemaReader = new YmlReaderPCMMSchema();
				if (!schemaReader.isValid(getFile())) {
					throw new CredibilityException(
							RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_FILE_NOTVALID, getFileType()));
				}
				return true;
			}

			@Override
			public void doImport() {
				// import new PCMM Schema
				viewCtrl.importPCMMSchema();
			}
		};
	}

	/**
	 * Render Uncertainty composite
	 */
	private void renderUncertainty() {
		String title = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_UNCERTAINTY_TITLE);
		String message = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_UNCERTAINTY_SCHEMAPATH);
		String fileType = RscTools.getString(RscConst.MSG_UNCERTAINTY);
		String preferenceKey = PrefTools.CONF_IMPORT_UNCERTAINTY_SCHEMA_FILE_LAST_PATH_KEY;
		textUncertaintySchemaPath = new ImportSelectorWidget(getViewManager().getRscMgr(), mainComposite, SWT.NONE, title, message, fileType,
				preferenceKey) {
			@Override
			public boolean isValidImportFileRule() throws CredibilityException {
				// check file validity
				YmlReaderUncertaintySchema schemaReader = new YmlReaderUncertaintySchema();
				if (!schemaReader.isValid(getFile())) {
					throw new CredibilityException(
							RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_FILE_NOTVALID, getFileType()));
				}
				return true;
			}

			@Override
			public void doImport() {
				// import new Uncertainty Schema
				viewCtrl.importUncertaintySchema();
			}
		};

	}

	/**
	 * Render System Requirements composite
	 */
	private void renderSystemRequirement() {
		String title = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_REQUIREMENTS_TITLE);
		String message = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_REQUIREMENTS_SCHEMAPATH);
		String fileType = RscTools.getString(RscConst.MSG_SYSREQUIREMENT);
		String preferenceKey = PrefTools.CONF_IMPORT_REQUIREMENTS_SCHEMA_FILE_LAST_PATH_KEY;
		textRequirementsSchemaPath = new ImportSelectorWidget(getViewManager().getRscMgr(), mainComposite, SWT.NONE, title, message, fileType,
				preferenceKey) {
			@Override
			public boolean isValidImportFileRule() throws CredibilityException {
				// check file validity
				YmlReaderSystemRequirementSchema schemaReader = new YmlReaderSystemRequirementSchema();
				if (!schemaReader.isValid(getFile())) {
					throw new CredibilityException(
							RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_FILE_NOTVALID, getFileType()));
				}
				return true;
			}

			@Override
			public void doImport() {
				// import new SystemRequirement Schema
				viewCtrl.importRequirementSchema();
			}
		};
	}

	/**
	 * Render Decision Composite
	 */
	private void renderDecision() {
		String title = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_DECISION_TITLE);
		String message = RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_DECISION_SCHEMAPATH);
		String fileType = RscTools.getString(RscConst.MSG_DECISION);
		String preferenceKey = PrefTools.CONF_IMPORT_DECISION_SCHEMA_FILE_LAST_PATH_KEY;
		textDecisionSchemaPath = new ImportSelectorWidget(getViewManager().getRscMgr(), mainComposite, SWT.NONE, title, message, fileType,
				preferenceKey) {
			@Override
			public boolean isValidImportFileRule() throws CredibilityException {
				// check file validity
				YmlReaderDecisionSchema schemaReader = new YmlReaderDecisionSchema();
				if (!schemaReader.isValid(getFile())) {
					throw new CredibilityException(
							RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_FILE_NOTVALID, getFileType()));
				}
				return true;
			}

			@Override
			public void doImport() {
				// import new Decision Schema
				viewCtrl.importDecisionSchema();
			}
		};
	}

	/**
	 * Render footer buttons
	 */
	private void renderFooterButtons() {
		// Footer buttons - Composite
		Composite compositeButtons = new Composite(this, SWT.NONE);
		compositeButtons.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtons.setLayout(new RowLayout());

		// Footer buttons - Back - Create
		Map<String, Object> btnBackOptions = new HashMap<>();
		btnBackOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BACK));
		btnBackOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnBackOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_BACK);
		btnBackOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		Button btnBack = new ButtonTheme(getViewManager().getRscMgr(), compositeButtons, SWT.CENTER, btnBackOptions);

		// Footer buttons - Help - Create
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) e -> HelpTools.openContextualHelp());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtons, SWT.CENTER, btnHelpOptions);

		// conextual help
		HelpTools.addContextualHelp(compositeButtons, ContextualHelpId.IMPORT);

		// Footer buttons - Back - plug
		getViewManager().plugBackButton(btnBack);

		// layout view
		compositeButtons.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {
		// Not used
	}

	/**
	 * @return the QoI Planning schema widget
	 */
	String getTextQoIPlanningSchemaPath() {
		return textQoIPlanningSchemaPath != null ? textQoIPlanningSchemaPath.getFilePath() : null;
	}

	/**
	 * @return the PIRT schema widget
	 */
	String getTextPIRTSchemaPath() {
		return textPIRTSchemaPath != null ? textPIRTSchemaPath.getFilePath() : null;
	}

	/**
	 * @return the PCMM schema widget
	 */
	String getTextPCMMSchemaPath() {
		return textPCMMSchemaPath != null ? textPCMMSchemaPath.getFilePath() : null;
	}

	/**
	 * @return the Uncertainty schema widget
	 */
	String getTextUncertaintySchemaPath() {
		return textUncertaintySchemaPath != null ? textUncertaintySchemaPath.getFilePath() : null;
	}

	/**
	 * @return the System Requirement schema widget
	 */
	String getTextRequirementsSchemaPath() {
		return textRequirementsSchemaPath != null ? textRequirementsSchemaPath.getFilePath() : null;
	}

	/**
	 * @return the Decision schema widget
	 */
	String getTextDecisionSchemaPath() {
		return textDecisionSchemaPath != null ? textDecisionSchemaPath.getFilePath() : null;
	}

	/**
	 * @return the QoI Planning schema widget
	 */
	File getTextQoIPlanningSchemaFile() {
		return textQoIPlanningSchemaPath != null ? textQoIPlanningSchemaPath.getFile() : null;
	}

	/**
	 * @return the PIRT schema widget
	 */
	File getTextPIRTSchemaFile() {
		return textPIRTSchemaPath != null ? textPIRTSchemaPath.getFile() : null;
	}

	/**
	 * @return the PCMM schema widget
	 */
	File getTextPCMMSchemaFile() {
		return textPCMMSchemaPath != null ? textPCMMSchemaPath.getFile() : null;
	}

	/**
	 * @return the Uncertainty schema widget
	 */
	File getTextUncertaintySchemaFile() {
		return textUncertaintySchemaPath != null ? textUncertaintySchemaPath.getFile() : null;
	}

	/**
	 * @return the System Requirement schema widget
	 */
	File getTextRequirementsSchemaFile() {
		return textRequirementsSchemaPath != null ? textRequirementsSchemaPath.getFile() : null;
	}

	/**
	 * @return the Decision schema widget
	 */
	File getTextDecisionSchemaFile() {
		return textDecisionSchemaPath != null ? textDecisionSchemaPath.getFile() : null;
	}
}
