/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.ui.pcmm.PCMMManageTagDialog;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMTagDateLabelProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMTagDescriptionLabelProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMTagNameLabelProvider;
import gov.sandia.cf.parts.ui.pcmm.editors.PCMMTagUserLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoICreationDateLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoIDescriptionLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoINameLabelProvider;
import gov.sandia.cf.parts.ui.pirt.editors.QoITreeContentProvider;
import gov.sandia.cf.parts.viewer.PIRTQoITableQoI;
import gov.sandia.cf.parts.viewer.editors.AutoResizeViewerLayout;
import gov.sandia.cf.parts.viewer.editors.GenericTreeListContentProvider;
import gov.sandia.cf.parts.widgets.CollapsibleWidget;
import gov.sandia.cf.parts.widgets.FancyToolTipSupport;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.ColorTools;
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
public class ExportConfigurationView extends ACredibilitySubView<ExportConfigurationViewController> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExportConfigurationView.class);

	/**
	 * The main composite
	 */
	private Composite mainComposite;

	private Text textPIRTSchemaPath;
	private Text textPCMMSchemaPath;
	private Text textUncertaintySchemaPath;
	private Text textDecisionSchemaPath;
	private Text textExportDataPath;

	private CheckboxTreeViewer pirtQoITree;
	private CheckboxTreeViewer pcmmTagTree;

	private Text textSysRequirementsSchemaPath;
	private Text textQoIPlanningSchemaPath;

	// data
	private Button pirtCheckbox;
	private Button pcmmCheckbox;
	private Button pcmmCheckboxPlanning;
	private Button pcmmCheckboxAssessment;
	private Button pcmmCheckboxEvidence;
	private Button intendedPurposeCheckbox;
	private Button decisionCheckbox;
	private Button systemRequirementCheckbox;
	private Button uncertaintyCheckbox;

	/**
	 * @param viewController the view manager
	 * @param parent         the parent composite
	 * @param style          the view style
	 */
	public ExportConfigurationView(ExportConfigurationViewController viewController, Composite parent, int style) {
		super(viewController, parent, style);

		// create the view
		renderPage();
	}

	/** {@inheritDoc} */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_TITLE);
	}

	/** {@inheritDoc} */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_ITEMTITLE);
	}

	/**
	 * Creates the page
	 */
	private void renderPage() {

		logger.debug("Render Export Configuration page"); //$NON-NLS-1$

		// Render main table
		renderMainComposite();

		// Render footer buttons
		renderFooterButtons();
	}

	/**
	 * Render main table
	 */
	private void renderMainComposite() {

		logger.debug("Render Export Configuration main composite"); //$NON-NLS-1$

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
		mainComposite.setBackground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));

		// Main table composite
		firstScroll.setContent(mainComposite);
		firstScroll.setExpandHorizontal(true);
		firstScroll.setExpandVertical(true);
		firstScroll.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		// Render sub-composites
		renderSysRequirementsSchema();
		renderQoIPlanningSchema();
		renderUncertaintySchema();
		renderDecisionSchema();
		renderPIRTSchema();
		renderPCMMSchema();
		renderData();
	}

	/**
	 * Render QoI Planning composite
	 */
	private void renderQoIPlanningSchema() {

		logger.debug("Render Export Configuration QoI Planning"); //$NON-NLS-1$

		// QoI Planning main composite
		Composite schemaComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		schemaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		schemaComposite.setLayout(gridLayout);
		schemaComposite.setBackground(schemaComposite.getParent().getBackground());

		// QoI Planning collapse
		new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), mainComposite, SWT.FILL | SWT.BORDER,
				schemaComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_QOIPLANNING_TITLE), false, true);

		// label
		GridData gdlabelSchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelSchemaPath.horizontalSpan = 2;
		Label label = FormFactory.createLabel(schemaComposite,
				RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_QOIPLANNING_SCHEMAPATH), gdlabelSchemaPath);
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), label);

		// text path
		textQoIPlanningSchemaPath = FormFactory.createText(schemaComposite, null, SWT.BORDER | SWT.SINGLE);
		textQoIPlanningSchemaPath.setEditable(false);
		textQoIPlanningSchemaPath
				.setText(PrefTools.getPreference(PrefTools.CONF_EXPORT_QOIPLANNING_SCHEMA_FILE_LAST_PATH_KEY));

		// button browse
		Map<String, Object> optionsBtnBrowse = new HashMap<>();
		optionsBtnBrowse.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowse.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowse.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme browseButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				schemaComposite, null, optionsBtnBrowse);

		// button export
		Map<String, Object> optionsBtnExport = new HashMap<>();
		optionsBtnExport.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_EXPORT));
		optionsBtnExport.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_EXPORT);
		optionsBtnExport.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnExport.put(ButtonTheme.OPTION_OUTLINE, false);
		optionsBtnExport.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewController().exportQoIPlanningSchema());
		ButtonTheme exportButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				schemaComposite, null, optionsBtnExport);
		GridData gdBtnExport = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false);
		gdBtnExport.horizontalSpan = 2;
		exportButton.setLayoutData(gdBtnExport);
		exportButton.setEnabled(StringUtils.isNotEmpty(textQoIPlanningSchemaPath.getText()));

		// text input listener
		textQoIPlanningSchemaPath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String path = textQoIPlanningSchemaPath.getText();
				PrefTools.setPreference(PrefTools.CONF_EXPORT_QOIPLANNING_SCHEMA_FILE_LAST_PATH_KEY,
						textQoIPlanningSchemaPath.getText());
				exportButton.setEnabled(StringUtils.isNotEmpty(path));
			}
		});
		// browse button listener
		browseButton.addListener(SWT.Selection, event -> getViewController().browseExportFile(textQoIPlanningSchemaPath,
				exportButton, PrefTools.CONF_EXPORT_QOIPLANNING_SCHEMA_FILE_LAST_PATH_KEY));
	}

	/**
	 * Render PIRT composite
	 */
	private void renderPIRTSchema() {

		logger.debug("Render Export Configuration PIRT"); //$NON-NLS-1$

		// PIRT main composite
		Composite pirtSchemaComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		pirtSchemaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		pirtSchemaComposite.setLayout(gridLayout);
		pirtSchemaComposite.setBackground(pirtSchemaComposite.getParent().getBackground());

		// PIRT collapse
		new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), mainComposite, SWT.FILL | SWT.BORDER,
				pirtSchemaComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_PIRT_TITLE), false, true);

		// label
		GridData gdlabelPIRTSchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelPIRTSchemaPath.horizontalSpan = 2;
		Label label = FormFactory.createLabel(pirtSchemaComposite,
				RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_PIRTSCHEMAPATH), gdlabelPIRTSchemaPath);
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), label);

		// text path
		textPIRTSchemaPath = FormFactory.createText(pirtSchemaComposite, null, SWT.BORDER | SWT.SINGLE);
		textPIRTSchemaPath.setEditable(false);
		textPIRTSchemaPath.setText(PrefTools.getPreference(PrefTools.CONF_EXPORT_PIRT_SCHEMA_FILE_LAST_PATH_KEY));

		// button browse
		Map<String, Object> optionsBtnBrowse = new HashMap<>();
		optionsBtnBrowse.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowse.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowse.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme browseButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				pirtSchemaComposite, null, optionsBtnBrowse);

		// button export
		Map<String, Object> optionsBtnExport = new HashMap<>();
		optionsBtnExport.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_EXPORT));
		optionsBtnExport.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_EXPORT);
		optionsBtnExport.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnExport.put(ButtonTheme.OPTION_OUTLINE, false);
		optionsBtnExport.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> getViewController().exportPIRTSchema());
		ButtonTheme exportButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				pirtSchemaComposite, null, optionsBtnExport);
		GridData gdBtnExport = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false);
		gdBtnExport.horizontalSpan = 2;
		exportButton.setLayoutData(gdBtnExport);
		exportButton.setEnabled(StringUtils.isNotEmpty(textPIRTSchemaPath.getText()));

		// text input listener
		textPIRTSchemaPath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String path = textPIRTSchemaPath.getText();
				PrefTools.setPreference(PrefTools.CONF_EXPORT_PIRT_SCHEMA_FILE_LAST_PATH_KEY,
						textPIRTSchemaPath.getText());
				exportButton.setEnabled(StringUtils.isNotEmpty(path));
			}
		});
		// browse button listener
		browseButton.addListener(SWT.Selection, event -> getViewController().browseExportFile(textPIRTSchemaPath,
				exportButton, PrefTools.CONF_EXPORT_PIRT_SCHEMA_FILE_LAST_PATH_KEY));
	}

	/**
	 * Render PCMM composite
	 */
	private void renderPCMMSchema() {

		logger.debug("Render Export Configuration PCMM"); //$NON-NLS-1$

		// PCMM main composite
		Composite pcmmSchemaComposite = new Composite(mainComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(2, false);
		pcmmSchemaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		pcmmSchemaComposite.setLayout(gridLayout);
		pcmmSchemaComposite.setBackground(pcmmSchemaComposite.getParent().getBackground());

		// PCMM collapse
		new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), mainComposite, SWT.FILL | SWT.BORDER,
				pcmmSchemaComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_PCMM_TITLE), false, true);

		// label
		GridData gdlabelPCMMSchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelPCMMSchemaPath.horizontalSpan = 2;
		Label label = FormFactory.createLabel(pcmmSchemaComposite,
				RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_PCMMSCHEMAPATH), gdlabelPCMMSchemaPath);
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), label);

		// text path
		textPCMMSchemaPath = FormFactory.createText(pcmmSchemaComposite, null, SWT.BORDER | SWT.SINGLE);
		textPCMMSchemaPath.setEditable(false);
		textPCMMSchemaPath.setText(PrefTools.getPreference(PrefTools.CONF_EXPORT_PCMM_SCHEMA_FILE_LAST_PATH_KEY));

		// button browse
		Map<String, Object> optionsBtnBrowse = new HashMap<>();
		optionsBtnBrowse.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowse.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowse.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme browseButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				pcmmSchemaComposite, null, optionsBtnBrowse);

		// button export
		Map<String, Object> optionsBtnExport = new HashMap<>();
		optionsBtnExport.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_EXPORT));
		optionsBtnExport.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_EXPORT);
		optionsBtnExport.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnExport.put(ButtonTheme.OPTION_OUTLINE, false);
		optionsBtnExport.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> getViewController().exportPCMMSchema());
		ButtonTheme exportButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				pcmmSchemaComposite, null, optionsBtnExport);
		GridData gdBtnExport = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false);
		gdBtnExport.horizontalSpan = 2;
		exportButton.setLayoutData(gdBtnExport);
		exportButton.setEnabled(StringUtils.isNotEmpty(textPCMMSchemaPath.getText()));

		// text input listener
		textPCMMSchemaPath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String path = textPCMMSchemaPath.getText();
				PrefTools.setPreference(PrefTools.CONF_EXPORT_PCMM_SCHEMA_FILE_LAST_PATH_KEY,
						textPCMMSchemaPath.getText());
				exportButton.setEnabled(StringUtils.isNotEmpty(path));
			}
		});
		// browse button listener
		browseButton.addListener(SWT.Selection, event -> getViewController().browseExportFile(textPCMMSchemaPath,
				exportButton, PrefTools.CONF_EXPORT_PCMM_SCHEMA_FILE_LAST_PATH_KEY));
	}

	/**
	 * Render Uncertainty composite
	 */
	private void renderUncertaintySchema() {

		logger.debug("Render Export Configuration Uncertainty"); //$NON-NLS-1$

		// Uncertainty main composite
		Composite uncertaintySchemaComposite = new Composite(mainComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(2, false);
		uncertaintySchemaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		uncertaintySchemaComposite.setLayout(gridLayout);
		uncertaintySchemaComposite.setBackground(uncertaintySchemaComposite.getParent().getBackground());

		// Uncertainty collapse
		new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), mainComposite, SWT.FILL | SWT.BORDER,
				uncertaintySchemaComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_UNCERTAINTY_TITLE), false,
				true);

		// label
		GridData gdlabelUncertaintySchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelUncertaintySchemaPath.horizontalSpan = 2;
		Label label = FormFactory.createLabel(uncertaintySchemaComposite,
				RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_UNCERTAINTYSCHEMAPATH), gdlabelUncertaintySchemaPath);
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), label);

		// text path
		textUncertaintySchemaPath = FormFactory.createText(uncertaintySchemaComposite, null, SWT.BORDER | SWT.SINGLE);
		textUncertaintySchemaPath.setEditable(false);
		textUncertaintySchemaPath
				.setText(PrefTools.getPreference(PrefTools.CONF_EXPORT_UNCERTAINTY_SCHEMA_FILE_LAST_PATH_KEY));

		// button browse
		Map<String, Object> optionsBtnBrowse = new HashMap<>();
		optionsBtnBrowse.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowse.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowse.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme browseButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				uncertaintySchemaComposite, null, optionsBtnBrowse);

		// button export
		Map<String, Object> optionsBtnExport = new HashMap<>();
		optionsBtnExport.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_EXPORT));
		optionsBtnExport.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_EXPORT);
		optionsBtnExport.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnExport.put(ButtonTheme.OPTION_OUTLINE, false);
		optionsBtnExport.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewController().exportUncertaintySchema());
		ButtonTheme exportButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				uncertaintySchemaComposite, null, optionsBtnExport);
		GridData gdBtnExport = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false);
		gdBtnExport.horizontalSpan = 2;
		exportButton.setLayoutData(gdBtnExport);
		exportButton.setEnabled(StringUtils.isNotEmpty(textUncertaintySchemaPath.getText()));

		// text input listener
		textUncertaintySchemaPath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String path = textUncertaintySchemaPath.getText();
				PrefTools.setPreference(PrefTools.CONF_EXPORT_UNCERTAINTY_SCHEMA_FILE_LAST_PATH_KEY,
						textUncertaintySchemaPath.getText());
				exportButton.setEnabled(StringUtils.isNotEmpty(path));
			}
		});
		// browse button listener
		browseButton.addListener(SWT.Selection, event -> getViewController().browseExportFile(textUncertaintySchemaPath,
				exportButton, PrefTools.CONF_EXPORT_UNCERTAINTY_SCHEMA_FILE_LAST_PATH_KEY));
	}

	/**
	 * Render System Requirements composite
	 */
	private void renderSysRequirementsSchema() {

		logger.debug("Render Export Configuration System Requirement"); //$NON-NLS-1$

		// System Requirements main composite
		Composite sysRequirementsSchemaComposite = new Composite(mainComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(2, false);
		sysRequirementsSchemaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		sysRequirementsSchemaComposite.setLayout(gridLayout);
		sysRequirementsSchemaComposite.setBackground(sysRequirementsSchemaComposite.getParent().getBackground());

		// System Requirements collapse
		new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), mainComposite, SWT.FILL | SWT.BORDER,
				sysRequirementsSchemaComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_SYSREQUIREMENTS_TITLE),
				false, true);

		// label
		GridData gdlabelSysRequirementsSchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelSysRequirementsSchemaPath.horizontalSpan = 2;
		Label label = FormFactory.createLabel(sysRequirementsSchemaComposite,
				RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_SYSREQUIREMENTS_SCHEMAPATH),
				gdlabelSysRequirementsSchemaPath);
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), label);

		// text path
		textSysRequirementsSchemaPath = FormFactory.createText(sysRequirementsSchemaComposite, null,
				SWT.BORDER | SWT.SINGLE);
		textSysRequirementsSchemaPath.setEditable(false);
		textSysRequirementsSchemaPath
				.setText(PrefTools.getPreference(PrefTools.CONF_EXPORT_REQUIREMENTS_SCHEMA_FILE_LAST_PATH_KEY));

		// button browse
		Map<String, Object> optionsBtnBrowse = new HashMap<>();
		optionsBtnBrowse.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowse.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowse.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme browseButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				sysRequirementsSchemaComposite, null, optionsBtnBrowse);

		// button export
		Map<String, Object> optionsBtnExport = new HashMap<>();
		optionsBtnExport.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_EXPORT));
		optionsBtnExport.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_EXPORT);
		optionsBtnExport.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnExport.put(ButtonTheme.OPTION_OUTLINE, false);
		optionsBtnExport.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewController().exportSysRequirementsSchema());
		ButtonTheme exportButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				sysRequirementsSchemaComposite, null, optionsBtnExport);
		GridData gdBtnExport = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false);
		gdBtnExport.horizontalSpan = 2;
		exportButton.setLayoutData(gdBtnExport);
		exportButton.setEnabled(StringUtils.isNotEmpty(textSysRequirementsSchemaPath.getText()));

		// text input listener
		textSysRequirementsSchemaPath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String path = textSysRequirementsSchemaPath.getText();
				PrefTools.setPreference(PrefTools.CONF_EXPORT_REQUIREMENTS_SCHEMA_FILE_LAST_PATH_KEY,
						textSysRequirementsSchemaPath.getText());
				exportButton.setEnabled(StringUtils.isNotEmpty(path));
			}
		});
		// browse button listener
		browseButton.addListener(SWT.Selection,
				event -> getViewController().browseExportFile(textSysRequirementsSchemaPath, exportButton,
						PrefTools.CONF_EXPORT_REQUIREMENTS_SCHEMA_FILE_LAST_PATH_KEY));
	}

	/**
	 * Render Decision composite
	 */
	private void renderDecisionSchema() {

		logger.debug("Render Export Configuration Decision"); //$NON-NLS-1$

		// Decision main composite
		Composite decisionSchemaComposite = new Composite(mainComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(2, false);
		decisionSchemaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		decisionSchemaComposite.setLayout(gridLayout);
		decisionSchemaComposite.setBackground(decisionSchemaComposite.getParent().getBackground());

		// Decision collapse
		new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), mainComposite, SWT.FILL | SWT.BORDER,
				decisionSchemaComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DECISION_TITLE), false, true);

		// label
		GridData gdlabelDecisionSchemaPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelDecisionSchemaPath.horizontalSpan = 2;
		Label label = FormFactory.createLabel(decisionSchemaComposite,
				RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DECISION_SCHEMAPATH), gdlabelDecisionSchemaPath);
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), label);

		// text path
		textDecisionSchemaPath = FormFactory.createText(decisionSchemaComposite, null, SWT.BORDER | SWT.SINGLE);
		textDecisionSchemaPath.setEditable(false);
		textDecisionSchemaPath
				.setText(PrefTools.getPreference(PrefTools.CONF_EXPORT_DECISION_SCHEMA_FILE_LAST_PATH_KEY));

		// button browse
		Map<String, Object> optionsBtnBrowse = new HashMap<>();
		optionsBtnBrowse.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowse.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowse.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme browseButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				decisionSchemaComposite, null, optionsBtnBrowse);

		// button export
		Map<String, Object> optionsBtnExport = new HashMap<>();
		optionsBtnExport.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_EXPORT));
		optionsBtnExport.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_EXPORT);
		optionsBtnExport.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnExport.put(ButtonTheme.OPTION_OUTLINE, false);
		optionsBtnExport.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewController().exportDecisionSchema());
		ButtonTheme exportButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				decisionSchemaComposite, null, optionsBtnExport);
		GridData gdBtnExport = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false);
		gdBtnExport.horizontalSpan = 2;
		exportButton.setLayoutData(gdBtnExport);
		exportButton.setEnabled(StringUtils.isNotEmpty(textDecisionSchemaPath.getText()));

		// text input listener
		textDecisionSchemaPath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String path = textDecisionSchemaPath.getText();
				PrefTools.setPreference(PrefTools.CONF_EXPORT_DECISION_SCHEMA_FILE_LAST_PATH_KEY,
						textDecisionSchemaPath.getText());
				exportButton.setEnabled(StringUtils.isNotEmpty(path));
			}
		});
		// browse button listener
		browseButton.addListener(SWT.Selection, event -> getViewController().browseExportFile(textDecisionSchemaPath,
				exportButton, PrefTools.CONF_EXPORT_DECISION_SCHEMA_FILE_LAST_PATH_KEY));
	}

	/**
	 * Render export Data
	 */
	private void renderData() {

		logger.debug("Render Export Configuration Data"); //$NON-NLS-1$

		// Data main composite
		Composite dataComposite = new Composite(mainComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, false);
		dataComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		dataComposite.setLayout(gridLayout);
		dataComposite.setBackground(dataComposite.getParent().getBackground());

		// Uncertainty collapse
		new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), mainComposite, SWT.FILL | SWT.BORDER,
				dataComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATA_TITLE), false, true);

		// label
		GridData gdlabelExportPath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelExportPath.horizontalSpan = 2;
		Label label = FormFactory.createLabel(dataComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATA_LBL),
				gdlabelExportPath);
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), label);

		// select export file path
		Composite exportPathComposite = new Composite(dataComposite, SWT.FILL);
		GridLayout gdExportPath = new GridLayout(2, false);
		exportPathComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		exportPathComposite.setLayout(gdExportPath);
		exportPathComposite.setBackground(exportPathComposite.getParent().getBackground());

		// text path
		textExportDataPath = FormFactory.createText(exportPathComposite, null, SWT.BORDER | SWT.SINGLE);
		textExportDataPath.setEditable(false);
		textExportDataPath.setText(PrefTools.getPreference(PrefTools.CONF_EXPORT_DATA_FILE_LAST_PATH_KEY));

		// button browse
		Map<String, Object> optionsBtnBrowse = new HashMap<>();
		optionsBtnBrowse.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowse.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowse.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme browseButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				exportPathComposite, null, optionsBtnBrowse);

		// render Intended Purpose selection
		renderIntendedPurposeData(dataComposite);

		// render Decision selection
		renderDecisionData(dataComposite);

		// render System Requirement selection
		renderSystemRequirementData(dataComposite);

		// render Uncertainty selection
		renderUncertaintyData(dataComposite);

		// render PIRT selection
		renderPIRTData(dataComposite);

		// render PCMM selection
		renderPCMMData(dataComposite);

		// button export
		Map<String, Object> optionsBtnExport = new HashMap<>();
		optionsBtnExport.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_EXPORT));
		optionsBtnExport.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_EXPORT);
		optionsBtnExport.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnExport.put(ButtonTheme.OPTION_OUTLINE, false);
		optionsBtnExport.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> getViewController().exportData());
		ButtonTheme exportButton = FormFactory.createButton(getViewController().getViewManager().getRscMgr(),
				dataComposite, null, optionsBtnExport);
		GridData gdBtnExport = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false);
		gdBtnExport.horizontalSpan = 2;
		exportButton.setLayoutData(gdBtnExport);
		exportButton.setEnabled(StringUtils.isNotEmpty(textExportDataPath.getText()));

		// text input listener
		textExportDataPath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String path = textExportDataPath.getText();
				PrefTools.setPreference(PrefTools.CONF_EXPORT_DATA_FILE_LAST_PATH_KEY, textExportDataPath.getText());
				exportButton.setEnabled(StringUtils.isNotEmpty(path));
			}
		});
		// browse button listener
		browseButton.addListener(SWT.Selection, event -> getViewController().browseExportFile(textExportDataPath,
				exportButton, PrefTools.CONF_EXPORT_DATA_FILE_LAST_PATH_KEY));
	}

	/**
	 * Render PIRT composite.
	 *
	 * @param parent the parent
	 */
	private void renderPIRTData(Composite parent) {

		// PIRT main composite
		Composite pirtDataComposite = new Composite(parent, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		pirtDataComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		pirtDataComposite.setLayout(gridLayout);
		pirtDataComposite.setBackground(pirtDataComposite.getParent().getBackground());

		// PIRT collapse
		CollapsibleWidget pirtCollapse = new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), parent,
				SWT.FILL, pirtDataComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATA_PIRT_LBL), true,
				true);
		pirtCheckbox = pirtCollapse.getCheckbox();

		// PIRT QoI selection - Label
		Label checkboxesLabel = FormFactory.createLabel(pirtDataComposite,
				RscTools.getString(RscConst.MSG_EXPORTVIEW_PIRT_QOI_LBL));
		checkboxesLabel.setBackground(checkboxesLabel.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), checkboxesLabel);

		// Initialize PIRT tree
		renderPIRTQoITree(pirtDataComposite);

		// reload data
		refreshPIRTQoITree();

		// Refresh
		pirtQoITree.refresh(true);
	}

	/**
	 * Initialize QoI selection tree
	 * 
	 * @param parent the parent composite
	 */
	private void renderPIRTQoITree(Composite parent) {

		// Tree - Create
		pirtQoITree = new CheckboxTreeViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		pirtQoITree.getTree().setLayoutData(gdViewer);
		pirtQoITree.getTree().setHeaderVisible(true);
		pirtQoITree.getTree().setLinesVisible(false);

		FancyToolTipSupport.enableFor(pirtQoITree, ToolTip.NO_RECREATE);

		// Tree - Layout
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(pirtQoITree);
		pirtQoITree.getTree().setLayout(viewerLayout);

		// Tree - Hint
		gdViewer.minimumHeight = pirtQoITree.getTree().getItemHeight();
		gdViewer.widthHint = getViewController().getViewManager().getSize().x
				- 2 * ((GridLayout) parent.getLayout()).horizontalSpacing;

		// Tree - Customize
		pirtQoITree.getTree().setHeaderBackground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		pirtQoITree.getTree().setHeaderForeground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));

		// Initialize data
		List<String> columnProperties = new ArrayList<>();

		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) pirtQoITree.getTree().getLayout();

		// Name
		TreeViewerColumn nameColumn = new TreeViewerColumn(pirtQoITree, SWT.LEFT);
		nameColumn.getColumn().setText(PIRTQoITableQoI.getColumnSymbolProperty());
		nameColumn.setLabelProvider(new QoINameLabelProvider(getViewController().getViewManager().getRscMgr()));
		treeViewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.QOIHOME_VIEW_TABLEPHEN_NAMECOLUMN_WEIGHT, true));
		columnProperties.add(PIRTQoITableQoI.getColumnSymbolProperty());

		// Description
		TreeViewerColumn descriptionColumn = new TreeViewerColumn(pirtQoITree, SWT.LEFT);
		descriptionColumn.getColumn().setText(PIRTQoITableQoI.getColumnDescriptionProperty());
		descriptionColumn
				.setLabelProvider(new QoIDescriptionLabelProvider(getViewController().getViewManager().getRscMgr()));
		treeViewerLayout.addColumnData(
				new ColumnWeightData(PartsResourceConstants.QOIHOME_VIEW_TABLEPHEN_NAMECOLUMN_WEIGHT, true));
		columnProperties.add(PIRTQoITableQoI.getColumnDescriptionProperty());

		// Creation Date
		TreeViewerColumn creationDateColumn = new TreeViewerColumn(pirtQoITree, SWT.LEFT);
		creationDateColumn.getColumn().setText(PIRTQoITableQoI.getColumnCreationDateProperty());
		creationDateColumn
				.setLabelProvider(new QoICreationDateLabelProvider(getViewController().getViewManager().getRscMgr()));
		treeViewerLayout.addColumnData(
				new ColumnPixelData(PartsResourceConstants.QOIHOME_VIEW_TABLEPHEN_CREATIONDATECOLUMN_WIDTH, true));
		columnProperties.add(PIRTQoITableQoI.getColumnCreationDateProperty());
		creationDateColumn.getColumn().notifyListeners(SWT.Selection, new Event());

		// Add listeners
		renderTreeAddEvents(pirtQoITree);

		// Tree - Properties
		pirtQoITree.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		pirtQoITree.setContentProvider(new QoITreeContentProvider());

	}

	/**
	 * Add tree viewer events.
	 *
	 * @param treeViewer the tree viewer
	 */
	private void renderTreeAddEvents(TreeViewer treeViewer) {
		// Get tree
		Tree tree = treeViewer.getTree();

		tree.addListener(SWT.MeasureItem, new Listener() {
			private TreeItem previousItem = null;

			@Override
			public void handleEvent(Event event) {
				event.height = PartsResourceConstants.TABLE_ROW_HEIGHT;
				TreeItem item = (TreeItem) event.item;
				if (item != null && !item.equals(previousItem)) {
					previousItem = item;
				}
			}
		});

		// disable selection when clicking on the container
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				treeViewer.setSelection(new StructuredSelection());
			}
		});
	}

	/**
	 * Refresh PIRT Tree data
	 */
	void refreshPIRTQoITree() {

		List<QuantityOfInterest> qoiList = getViewController().getQois();

		if (qoiList != null) {

			List<QuantityOfInterest> qoiParentList = qoiList.stream().filter(qoi -> qoi.getParent() == null)
					.collect(Collectors.toList());

			// Set input
			pirtQoITree.setInput(qoiParentList);

			// auto expand all
			pirtQoITree.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

			// select all
			qoiParentList.forEach(qoi -> pirtQoITree.setSubtreeChecked(qoi, true));

			// reset tree height
			((GridData) pirtQoITree.getTree().getLayoutData()).heightHint = pirtQoITree.getTree().getItemHeight()
					* (qoiList.size() + 2); // +2 for header and margin

		}
	}

	/**
	 * Render PCMM composite.
	 *
	 * @param parent the parent
	 */
	private void renderPCMMData(Composite parent) {
		// PCMM main composite
		Composite pcmmDataComposite = new Composite(parent, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		pcmmDataComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		pcmmDataComposite.setLayout(gridLayout);
		pcmmDataComposite.setBackground(pcmmDataComposite.getParent().getBackground());

		// PCMM collapse
		CollapsibleWidget pcmmCollapse = new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), parent,
				SWT.FILL, pcmmDataComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATA_PCMM_LBL), true,
				true);
		pcmmCheckbox = pcmmCollapse.getCheckbox();

		// PCMM tag selection
		renderPCMMTagSelection(pcmmDataComposite);

		// Divider Label
		CLabel divideLabel = new CLabel(pcmmDataComposite, SWT.LEFT);
		divideLabel.setText(" "); //$NON-NLS-1$
		divideLabel.setBackground(divideLabel.getParent().getBackground());

		// PCMM features selection
		renderPCMMFeatures(pcmmDataComposite);
	}

	/**
	 * Render PCMM tag selection.
	 *
	 * @param parent the parent
	 */
	private void renderPCMMTagSelection(Composite parent) {

		// PCMM Tag selection - Label
		Label checkboxesLabel = FormFactory.createLabel(parent,
				RscTools.getString(RscConst.MSG_EXPORTVIEW_PCMM_TAG_LBL));
		checkboxesLabel.setBackground(checkboxesLabel.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), checkboxesLabel);

		// Initialize PCMM tag tree
		renderPCMMTagTree(parent);

		// reload data
		refreshPCMMTagTree();

		// Refresh
		pcmmTagTree.refresh(true);
	}

	/**
	 * Initialize QoI selection tree
	 * 
	 * @param parent the parent composite
	 */
	private void renderPCMMTagTree(Composite parent) {

		// Tree - Create
		pcmmTagTree = new CheckboxTreeViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		pcmmTagTree.getTree().setLayoutData(gdViewer);
		pcmmTagTree.getTree().setHeaderVisible(true);
		pcmmTagTree.getTree().setLinesVisible(false);

		FancyToolTipSupport.enableFor(pcmmTagTree, ToolTip.NO_RECREATE);

		// Tree - Layout
		final AutoResizeViewerLayout viewerLayout = new AutoResizeViewerLayout(pcmmTagTree);
		pcmmTagTree.getTree().setLayout(viewerLayout);

		// Tree - Hint
		gdViewer.minimumHeight = pcmmTagTree.getTree().getItemHeight();
		gdViewer.widthHint = getViewController().getViewManager().getSize().x
				- 2 * ((GridLayout) parent.getLayout()).horizontalSpacing;

		// Tree - Customize
		pcmmTagTree.getTree().setHeaderBackground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		pcmmTagTree.getTree().setHeaderForeground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));

		// Initialize data
		List<String> columnProperties = new ArrayList<>();

		AutoResizeViewerLayout treeViewerLayout = (AutoResizeViewerLayout) pcmmTagTree.getTree().getLayout();

		// Name
		TreeViewerColumn nameColumn = new TreeViewerColumn(pcmmTagTree, SWT.LEFT);
		nameColumn.getColumn().setText(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_LBL));
		nameColumn.setLabelProvider(new PCMMTagNameLabelProvider(getViewController().getViewManager()));
		treeViewerLayout.addColumnData(new ColumnWeightData(PCMMManageTagDialog.COL_NAME_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_LBL));

		// Tag Date
		TreeViewerColumn tagDateColumn = new TreeViewerColumn(pcmmTagTree, SWT.LEFT);
		tagDateColumn.getColumn().setText(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_DATE));
		tagDateColumn.setLabelProvider(new PCMMTagDateLabelProvider());
		treeViewerLayout.addColumnData(new ColumnWeightData(PCMMManageTagDialog.COL_DATE_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_DATE));
		tagDateColumn.getColumn().notifyListeners(SWT.Selection, new Event());

		// User
		TreeViewerColumn userTagColumn = new TreeViewerColumn(pcmmTagTree, SWT.LEFT);
		userTagColumn.getColumn().setText(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_USER));
		userTagColumn.setLabelProvider(new PCMMTagUserLabelProvider());
		treeViewerLayout.addColumnData(new ColumnWeightData(PCMMManageTagDialog.COL_USER_WIDTH, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_USER));
		userTagColumn.getColumn().notifyListeners(SWT.Selection, new Event());

		// Description
		TreeViewerColumn descriptionColumn = new TreeViewerColumn(pcmmTagTree, SWT.LEFT);
		descriptionColumn.getColumn().setText(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_DESC));
		descriptionColumn.setLabelProvider(new PCMMTagDescriptionLabelProvider());
		int descriptionColWidth = parent.getSize().x == 0 ? PCMMManageTagDialog.COL_DESC_WIDTH
				: parent.getSize().x - PCMMManageTagDialog.COL_NAME_WIDTH;
		treeViewerLayout.addColumnData(new ColumnWeightData(descriptionColWidth, true));
		columnProperties.add(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_DESC));

		// Add listeners
		renderTreeAddEvents(pcmmTagTree);

		// Tree - Properties
		pcmmTagTree.setColumnProperties(columnProperties.stream().toArray(String[]::new));
		pcmmTagTree.setContentProvider(new GenericTreeListContentProvider());

	}

	/**
	 * Reload PCMM tag Tree data
	 */
	void refreshPCMMTagTree() {

		// load data
		List<Tag> tagList = new ArrayList<>();

		// add current tag
		Tag currentVersion = new Tag();
		currentVersion.setName(RscTools.getString(RscConst.MSG_EXPORTVIEW_PCMM_TAG_CURRENT));
		tagList.add(currentVersion);

		// add other tags
		tagList.addAll(getViewController().getTags());

		// Set input
		pcmmTagTree.setInput(tagList);

		// auto expand all
		pcmmTagTree.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

		// select all
		tagList.forEach(qoi -> pcmmTagTree.setSubtreeChecked(qoi, true));

		// reset tree height
		((GridData) pcmmTagTree.getTree().getLayoutData()).heightHint = pcmmTagTree.getTree().getItemHeight()
				* (tagList.size() + 2); // +2 for header and margin
	}

	/**
	 * Render PCMM feature selection.
	 *
	 * @param pcmmComposite the pcmm composite
	 */
	private void renderPCMMFeatures(Composite pcmmComposite) {
		// Check parameter - Label
		Label checkboxesLabel = FormFactory.createLabel(pcmmComposite,
				RscTools.getString(RscConst.MSG_EXPORTVIEW_PCMM_FEATURES_LBL));
		checkboxesLabel.setBackground(checkboxesLabel.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), checkboxesLabel);

		// Check-box Planning
		Composite pcmmCheckboxPlanningComposite = new Composite(pcmmComposite, SWT.FILL);
		GridLayout pcmmCheckboxPlanningCompositeGridLayout = new GridLayout();
		pcmmCheckboxPlanningCompositeGridLayout.marginBottom = 5;
		pcmmCheckboxPlanningComposite.setLayoutData(new GridData());
		pcmmCheckboxPlanningComposite.setLayout(pcmmCheckboxPlanningCompositeGridLayout);
		pcmmCheckboxPlanningComposite.setBackground(pcmmCheckboxPlanningComposite.getParent().getBackground());
		pcmmCheckboxPlanning = new Button(pcmmCheckboxPlanningComposite, SWT.CHECK);
		pcmmCheckboxPlanning.setText(RscTools.getString(RscConst.MSG_EXPORTVIEW_PCMM_CHECKBOX_PLANNING));
		pcmmCheckboxPlanning.setSelection(true);
		pcmmCheckboxPlanning.setBackground(pcmmCheckboxPlanning.getParent().getBackground());

		// Check-box Evidence
		Composite pcmmCheckboxEvidenceComposite = new Composite(pcmmComposite, SWT.FILL);
		GridLayout pcmmCheckboxEvidenceCompositeGridLayout = new GridLayout();
		pcmmCheckboxEvidenceCompositeGridLayout.marginBottom = 5;
		pcmmCheckboxEvidenceComposite.setLayoutData(new GridData());
		pcmmCheckboxEvidenceComposite.setLayout(pcmmCheckboxEvidenceCompositeGridLayout);
		pcmmCheckboxEvidenceComposite.setBackground(pcmmCheckboxEvidenceComposite.getParent().getBackground());
		pcmmCheckboxEvidence = new Button(pcmmCheckboxEvidenceComposite, SWT.CHECK);
		pcmmCheckboxEvidence.setText(RscTools.getString(RscConst.MSG_EXPORTVIEW_PCMM_CHECKBOX_EVIDENCE));
		pcmmCheckboxEvidence.setSelection(true);
		pcmmCheckboxEvidence.setBackground(pcmmCheckboxEvidence.getParent().getBackground());

		// Check-box Assessment
		Composite pcmmCheckboxAssessmentComposite = new Composite(pcmmComposite, SWT.FILL);
		GridLayout pcmmCheckboxAssessmentCompositeGridLayout = new GridLayout();
		pcmmCheckboxAssessmentCompositeGridLayout.marginBottom = 5;
		pcmmCheckboxAssessmentComposite.setLayoutData(new GridData());
		pcmmCheckboxAssessmentComposite.setLayout(pcmmCheckboxAssessmentCompositeGridLayout);
		pcmmCheckboxAssessmentComposite.setBackground(pcmmCheckboxAssessmentComposite.getParent().getBackground());
		pcmmCheckboxAssessment = new Button(pcmmCheckboxAssessmentComposite, SWT.CHECK);
		pcmmCheckboxAssessment.setText(RscTools.getString(RscConst.MSG_EXPORTVIEW_PCMM_CHECKBOX_ASSESSMENT));
		pcmmCheckboxAssessment.setSelection(true);
		pcmmCheckboxAssessment.setBackground(pcmmCheckboxAssessment.getParent().getBackground());
	}

	/**
	 * Render Intended Purpose data composite.
	 *
	 * @param parent the parent
	 */
	private void renderIntendedPurposeData(Composite parent) {
		// main composite
		Composite dataComposite = new Composite(parent, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		dataComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		dataComposite.setLayout(gridLayout);
		dataComposite.setBackground(dataComposite.getParent().getBackground());

		// collapse
		CollapsibleWidget collapse = new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), parent,
				SWT.FILL, dataComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATA_INTPURPOSE_LBL), true,
				true);
		intendedPurposeCheckbox = collapse.getCheckbox();

		// export description
		CLabel label = new CLabel(dataComposite, SWT.NONE);
		label.setText(RscTools.getString(RscConst.MSG_EXPORTVIEW_INTPURPOSE_LBL));
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), label);
	}

	/**
	 * Render Decision data composite.
	 *
	 * @param parent the parent
	 */
	private void renderDecisionData(Composite parent) {
		// main composite
		Composite dataComposite = new Composite(parent, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		dataComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		dataComposite.setLayout(gridLayout);
		dataComposite.setBackground(dataComposite.getParent().getBackground());

		// collapse
		CollapsibleWidget collapse = new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), parent,
				SWT.FILL, dataComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATA_DECISION_LBL), true,
				true);
		decisionCheckbox = collapse.getCheckbox();

		// export description
		CLabel label = new CLabel(dataComposite, SWT.NONE);
		label.setText(RscTools.getString(RscConst.MSG_EXPORTVIEW_DECISION_LBL));
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), label);
	}

	/**
	 * Render System Requirement data composite.
	 *
	 * @param parent the parent
	 */
	private void renderSystemRequirementData(Composite parent) {
		// main composite
		Composite dataComposite = new Composite(parent, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		dataComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		dataComposite.setLayout(gridLayout);
		dataComposite.setBackground(dataComposite.getParent().getBackground());

		// collapse
		CollapsibleWidget collapse = new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), parent,
				SWT.FILL, dataComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATA_SYSREQ_LBL), true, true);
		systemRequirementCheckbox = collapse.getCheckbox();

		// export description
		CLabel label = new CLabel(dataComposite, SWT.NONE);
		label.setText(RscTools.getString(RscConst.MSG_EXPORTVIEW_SYSREQ_LBL));
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), label);
	}

	/**
	 * Render Uncertainty data composite.
	 *
	 * @param parent the parent
	 */
	private void renderUncertaintyData(Composite parent) {
		// main composite
		Composite dataComposite = new Composite(parent, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		dataComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		dataComposite.setLayout(gridLayout);
		dataComposite.setBackground(dataComposite.getParent().getBackground());

		// collapse
		CollapsibleWidget collapse = new CollapsibleWidget(getViewController().getViewManager().getRscMgr(), parent,
				SWT.FILL, dataComposite, RscTools.getString(RscConst.MSG_CONF_EXPORTVIEW_DATA_UNCERTAINTY_LBL), true,
				true);
		uncertaintyCheckbox = collapse.getCheckbox();

		// export description
		CLabel label = new CLabel(dataComposite, SWT.NONE);
		label.setText(RscTools.getString(RscConst.MSG_EXPORTVIEW_UNCERTAINTY_LBL));
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), label);
	}

	/**
	 * Render footer buttons
	 */
	private void renderFooterButtons() {
		// Footer buttons - Composite
		Composite compositeButtonsFooter = new Composite(this, SWT.FILL);
		GridLayout gridLayoutButtonsHeader = new GridLayout(2, false);
		compositeButtonsFooter.setLayout(gridLayoutButtonsHeader);
		compositeButtonsFooter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		// Composite for Footer left buttons
		Composite compositeButtonsFooterLeft = new Composite(compositeButtonsFooter, SWT.NONE);
		compositeButtonsFooterLeft.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		compositeButtonsFooterLeft.setLayout(new RowLayout());

		// Composite for header right buttons
		Composite compositeButtonsFooterRight = new Composite(compositeButtonsFooter, SWT.RIGHT_TO_LEFT);
		compositeButtonsFooterRight.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
		compositeButtonsFooterRight.setLayout(new RowLayout());

		// Footer buttons - Back
		Map<String, Object> btnBackOptions = new HashMap<>();
		btnBackOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BACK));
		btnBackOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnBackOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_BACK);
		btnBackOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		ButtonTheme btnBack = new ButtonTheme(getViewController().getViewManager().getRscMgr(),
				compositeButtonsFooterLeft, SWT.CENTER, btnBackOptions);

		// Footer buttons - Help
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> HelpTools.openContextualHelp());

		ButtonTheme btnHelp = new ButtonTheme(getViewController().getViewManager().getRscMgr(),
				compositeButtonsFooterLeft, SWT.CENTER, btnHelpOptions);
		RowData btnLayoutData = new RowData();
		btnHelp.setLayoutData(btnLayoutData);
		HelpTools.addContextualHelp(compositeButtonsFooterLeft, ContextualHelpId.EXPORT);

		// Footer buttons - Back - plug
		getViewController().getViewManager().plugBackButton(btnBack);

		// layout view
		compositeButtonsFooter.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {
		getViewController().reloadData();
	}

	/**
	 * @return the qoi planning schema path value, null if the widget is null
	 */
	String getTextQoIPlanningSchemaPath() {
		return textQoIPlanningSchemaPath != null ? textQoIPlanningSchemaPath.getText() : null;
	}

	/**
	 * @return the PIRT schema path value, null if the widget is null
	 */
	String getTextPIRTSchemaPath() {
		return textPIRTSchemaPath != null ? textPIRTSchemaPath.getText() : null;
	}

	/**
	 * @return the PCMM schema path value, null if the widget is null
	 */
	String getTextPCMMSchemaPath() {
		return textPCMMSchemaPath != null ? textPCMMSchemaPath.getText() : null;
	}

	/**
	 * @return the Uncertainty schema path value, null if the widget is null
	 */
	String getTextUncertaintySchemaPath() {
		return textUncertaintySchemaPath != null ? textUncertaintySchemaPath.getText() : null;
	}

	/**
	 * @return the system requirements schema path value, null if the widget is null
	 */
	String getTextSysRequirementsSchemaPath() {
		return textSysRequirementsSchemaPath != null ? textSysRequirementsSchemaPath.getText() : null;
	}

	/**
	 * @return the Decision schema path value, null if the widget is null
	 */
	String getTextDecisionSchemaPath() {
		return textDecisionSchemaPath != null ? textDecisionSchemaPath.getText() : null;
	}

	/**
	 * @return the export data path value, null if the widget is null
	 */
	String getTextDataSchemaPath() {
		return textExportDataPath != null ? textExportDataPath.getText() : null;
	}

	/**
	 * @return true if the PIRT checkbox is selected and not null, otherwise false.
	 */
	boolean isPIRTSelected() {
		return pirtCheckbox != null && pirtCheckbox.getSelection();
	}

	/**
	 * @return true if the PCMM is selected and not null, otherwise false.
	 */
	boolean isPCMMSelected() {
		return pcmmCheckbox != null && pcmmCheckbox.getSelection();
	}

	/**
	 * @return true if the PCMM Planning is selected and not null, otherwise false.
	 */
	boolean isPCMMPlanningSelected() {
		return pcmmCheckboxPlanning != null && pcmmCheckboxPlanning.getSelection();
	}

	/**
	 * @return true if the PCMM Evidence is selected and not null, otherwise false.
	 */
	boolean isPCMMEvidenceSelected() {
		return pcmmCheckboxEvidence != null && pcmmCheckboxEvidence.getSelection();
	}

	/**
	 * @return true if the PCMM Assessment checkbox is selected and not null,
	 *         otherwise false.
	 */
	boolean isPCMMAssessmentSelected() {
		return pcmmCheckboxAssessment != null && pcmmCheckboxAssessment.getSelection();
	}

	/**
	 * Checks if is intended purpose selected.
	 *
	 * @return true, if is intended purpose selected
	 */
	boolean isIntendedPurposeSelected() {
		return intendedPurposeCheckbox != null && intendedPurposeCheckbox.getSelection();
	}

	/**
	 * Checks if is decision selected.
	 *
	 * @return true, if is decision selected
	 */
	boolean isDecisionSelected() {
		return decisionCheckbox != null && decisionCheckbox.getSelection();
	}

	/**
	 * Checks if is system requirement selected.
	 *
	 * @return true, if is system requirement selected
	 */
	boolean isSystemRequirementSelected() {
		return systemRequirementCheckbox != null && systemRequirementCheckbox.getSelection();
	}

	/**
	 * @return true if the Uncertainty checkbox is selected and not null, otherwise
	 *         false.
	 */
	boolean isUncertaintySelected() {
		return uncertaintyCheckbox != null && uncertaintyCheckbox.getSelection();
	}

	/**
	 * @return the selected PIRT qoi
	 */
	Object[] getPIRTQoISelected() {
		return pirtQoITree != null ? pirtQoITree.getCheckedElements() : new Object[] {};
	}

	/**
	 * @return the selected PCMM tags
	 */
	Object[] getPCMMTagSelected() {
		return pcmmTagTree != null ? pcmmTagTree.getCheckedElements() : new Object[] {};
	}
}
