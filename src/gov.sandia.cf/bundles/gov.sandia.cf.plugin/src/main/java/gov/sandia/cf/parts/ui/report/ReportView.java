/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.report;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.application.IPIRTApplication;
import gov.sandia.cf.application.IReportARGExecutionApp;
import gov.sandia.cf.application.configuration.arg.ARGBackendDefault;
import gov.sandia.cf.application.configuration.arg.ARGReportTypeDefault;
import gov.sandia.cf.application.configuration.arg.ARGType;
import gov.sandia.cf.application.configuration.arg.YmlARGStructure;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.ARGParametersQoIOption;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.comparator.QoiTaggedComparator;
import gov.sandia.cf.model.comparator.TagComparatorByDateTag;
import gov.sandia.cf.model.comparator.VersionComparator;
import gov.sandia.cf.parts.dialogs.ContainerPickerDialog;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.widgets.CollapsibleWidget;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.TextWidget;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.CFVariableResolver;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;
import gov.sandia.cf.tools.SystemTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * Report view: Use to generate Credibility Framework Report
 * 
 * @author Maxime N.
 *
 */
public class ReportView extends ACredibilitySubView<ReportViewManager> {
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReportView.class);

	/**
	 * Controller
	 */
	private ReportViewController viewCtrl;

	/**
	 * Composites
	 */
	private Composite mainComposite;
	private Composite pirtQoIComposite;
	private Composite pcmmComposite;
	private Composite planningComposite;

	/**
	 * ARG Setup
	 */
	private TextWidget txtArgSetupPython;
	private TextWidget txtArgSetupExecutable;
	private TextWidget txtArgSetupPreScript;
	private CLabel textARGVersion;
	private Button chboxUseARGLocalConf;

	private boolean askFirstTimeForMigration;

	/**
	 * ARG Parameters
	 */
	private ARGParameters argParameters;
	private TextWidget txtARGParamOutput;
	private TextWidget txtARGParamParametersFile;
	private TextWidget txtARGParamStructureFile;
	private TextWidget txtARGParamFilename;
	private TextWidget txtARGParamReportTitle;
	private TextWidget txtARGParamAuthor;

	private Label lblReportType;
	private ComboViewer cbxARGParamReportType;
	private ComboViewer cbxARGParamBackendType;

	private Button chboxARGParamInlineWordDoc;

	/**
	 * Planning Elements
	 */
	private Button chboxPlanning;
	private Button chboxPlanningIntendedPurpose;
	private Button chboxPlanningUncertainty;
	private Button chboxPlanningSystemRequirement;
	private Button chboxPlanningQoIPlanner;
	private Button chboxPlanningDecision;

	/**
	 * PIRT Elements
	 */
	private Button chboxPirt;
	private List<QuantityOfInterest> pirtQoIList;
	private Map<QuantityOfInterest, Map<Class<?>, Control>> pirtControlMap;

	/**
	 * PCMM Elements
	 */
	private ComboViewer cbxPcmmTag;
	private Button chboxPcmm;
	private Button chboxPcmmPlanning;
	private Button chboxPcmmEvidence;
	private Button chboxPcmmAssessment;
	private List<Tag> pcmmTagList;

	/**
	 * Custom ending file
	 */
	private Button chboxCustomEnding;
	private TextWidget txtCustomEndingFilePath;

	/**
	 * ARG console
	 */
	private TextViewer txtConsole;

	private ARGType argTypes;

	/**
	 * The constructor
	 * 
	 * @param viewManager The view manager
	 * @param style       The view style
	 */
	public ReportView(ReportViewManager viewManager, int style) {
		super(viewManager, viewManager, style);

		this.viewCtrl = new ReportViewController(this);
		this.askFirstTimeForMigration = true;

		// create the view
		renderPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {

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

		// reload execution environment
		reloadARGSetup();

		// reload combo-box parameters
		reloadARGTypes();

		// reload
		reloadARGParameters();
		reloadPlanning();
		reloadPIRT();
		reloadPCMM();
		reloadCustomEnding();
	}

	/**
	 * Reload the ARG parameters : if not present, add default data.
	 */
	void reloadARGTypes() {

		// reload combo-box parameters
		argTypes = viewCtrl.getARGTypes();
	}

	/**
	 * Reload the ARG parameters : if not present, add default data.
	 */
	void reloadARGParameters() {

		// ARG Back-end type - text input
		if (argTypes != null && argTypes.getBackendTypes() != null) {
			List<Object> backend = argTypes.getBackendTypes().stream().map(Object.class::cast)
					.collect(Collectors.toList());
			cbxARGParamBackendType.setInput(backend);
		}

		// ARG Report type - text input
		if (argTypes != null && argTypes.getReportTypes() != null) {
			List<Object> reportTypes = argTypes.getReportTypes().stream().map(Object.class::cast)
					.collect(Collectors.toList());
			cbxARGParamReportType.setInput(reportTypes);
			lblReportType.setVisible(reportTypes.size() > 1);
			cbxARGParamReportType.getControl().setVisible(reportTypes.size() > 1);
		}

		if (argParameters != null) {
			txtARGParamOutput.setValue(argParameters.getOutput());
			txtARGParamParametersFile.setValue(argParameters.getParametersFilePath());
			txtARGParamStructureFile.setValue(argParameters.getStructureFilePath());
			txtARGParamFilename.setValue(argParameters.getFilename());
			txtARGParamReportTitle.setValue(argParameters.getTitle());
			txtARGParamAuthor
					.setValue(argParameters.getAuthor() != null ? argParameters.getAuthor() : RscTools.empty());
			cbxARGParamBackendType.setSelection(new StructuredSelection(argParameters.getBackendType()));

			if (chboxARGParamInlineWordDoc != null) {

				boolean enableInlining = PrefTools.getPreferenceBoolean(PrefTools.DEVOPTS_REPORT_INLINEWORD_KEY);
				if (enableInlining) {
					// force to true if the value is null
					chboxARGParamInlineWordDoc.setSelection(argParameters.getInlineWordDoc() == null
							|| Boolean.TRUE.equals(argParameters.getInlineWordDoc()));
					chboxARGParamInlineWordDoc.notifyListeners(SWT.Selection, new Event());
				}

				// reload widget display
				reloadWordInlining();
			}
			cbxARGParamReportType.setSelection(new StructuredSelection(argParameters.getReportType()));
		}
	}

	/**
	 * @return the ARG version populated with current ARG installation
	 */
	private String getARGVersion() {

		String argVersion = null;

		try {

			StringBuilder consoleLog = new StringBuilder(txtConsole.getTextWidget().getText());
			argVersion = getViewManager().getAppManager().getService(IReportARGExecutionApp.class)
					.getARGVersion(viewCtrl.computeARGParametersForExecution(argParameters), consoleLog, consoleLog);
			txtConsole.getTextWidget().setText(consoleLog.toString());
			txtConsole.setTopIndex(txtConsole.getTextWidget().getLineCount() - 1);

		} catch (URISyntaxException | IOException | CredibilityException e) {
			logger.warn(e.getMessage());
			MessageDialog.openWarning(getShell(), RscTools.getString(RscConst.MSG_REPORTVIEW_TITLE), e.getMessage());
		}

		return argVersion;
	}

	/**
	 * Reload ARG Setup data
	 */
	void reloadARGSetup() {

		// set default conf
		if (argParameters != null) {
			if (chboxUseARGLocalConf != null) {
				chboxUseARGLocalConf.setSelection(Boolean.TRUE.equals(argParameters.getUseArgLocalConf()));
				chboxUseARGLocalConf.notifyListeners(SWT.Selection, new Event());
			}
			if (txtArgSetupPython != null) {
				if (Boolean.TRUE.equals(argParameters.getUseArgLocalConf())) {
					txtArgSetupPython.setValue(PrefTools.getPythonExecutablePath());
				} else {
					txtArgSetupPython.setValue(argParameters.getPythonExecPath());
				}
			}
			if (txtArgSetupExecutable != null) {
				if (Boolean.TRUE.equals(argParameters.getUseArgLocalConf())) {
					txtArgSetupExecutable.setValue(FileTools.getNormalizedPath(PrefTools.getARGExecutablePath()));
				} else {
					txtArgSetupExecutable.setValue(argParameters.getArgExecPath());
				}
			}
			if (txtArgSetupPreScript != null) {
				if (Boolean.TRUE.equals(argParameters.getUseArgLocalConf())) {
					txtArgSetupPreScript.setValue(FileTools.getNormalizedPath(PrefTools.getARGSetEnvScriptPath()));
				} else {
					txtArgSetupPreScript.setValue(argParameters.getArgPreScript());
				}
			}
		}

		// check and ask for prefs migration to .cf file
		if (askFirstTimeForMigration && argParameters != null
				&& !Boolean.TRUE.equals(argParameters.getUseArgLocalConf()) && txtArgSetupExecutable != null
				&& StringUtils.isBlank(txtArgSetupExecutable.getValue())
				&& !StringUtils.isBlank(PrefTools.getARGExecutablePath())) {

			boolean migrationQuestion = MessageDialog.openQuestion(getShell(),
					RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_TITLE),
					RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_QUESTION_ASKTOPERSISTPREFS));
			if (migrationQuestion) {
				txtArgSetupExecutable.setValue(PrefTools.getARGExecutablePath());
				viewCtrl.changedARGSetupExecutable(txtArgSetupExecutable.getValue());
				if (StringUtils.isBlank(txtArgSetupPreScript.getValue())) {
					txtArgSetupPreScript.setValue(PrefTools.getARGSetEnvScriptPath());
					viewCtrl.changedARGSetupPreScript(txtArgSetupPreScript.getValue());
				}
			}

			askFirstTimeForMigration = false;
		}

		// Get ARG version
		reloadARGSetupARGVersion();
	}

	/**
	 * Reload ARG Setup ARG version information
	 */
	void reloadARGSetupARGVersion() {

		// Get ARG version
		String argVersion = getARGVersion();
		int versionComparison = new VersionComparator().compare(YmlARGStructure.ARG_VERSION, argVersion);
		if (versionComparison > 0) {
			textARGVersion.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_VERSION_WARN, argVersion,
					YmlARGStructure.ARG_VERSION));
			textARGVersion.setForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_ORANGE));
			textARGVersion.setVisible(true);
			textARGVersion.setImage(FormFactory.getWarningIcon(getViewManager().getRscMgr()));
			((GridData) textARGVersion.getLayoutData()).heightHint = textARGVersion.computeSize(SWT.DEFAULT,
					SWT.DEFAULT).y;

			textARGVersion.requestLayout();
			textARGVersion.getParent().requestLayout();
		} else {
			textARGVersion.setText(null);
			textARGVersion.setForeground(null);
			textARGVersion.setVisible(false);
			textARGVersion.setImage(null);
			((GridData) textARGVersion.getLayoutData()).heightHint = 0;

			textARGVersion.requestLayout();
			textARGVersion.getParent().requestLayout();
		}
	}

	/**
	 * Reload Planning data
	 */
	void reloadPlanning() {
		// load checkboxes
		if (argParameters != null) {
			if (chboxPlanning != null) {
				chboxPlanning.setSelection(Boolean.TRUE.equals(argParameters.getPlanningEnabled()));
			}

			if (chboxPlanningIntendedPurpose != null) {
				chboxPlanningIntendedPurpose
						.setSelection(Boolean.TRUE.equals(argParameters.getPlanningIntendedPurposeEnabled()));
				chboxPlanningIntendedPurpose.setEnabled(Boolean.TRUE.equals(argParameters.getPlanningEnabled()));
			}
			if (chboxPlanningSystemRequirement != null) {
				chboxPlanningSystemRequirement
						.setSelection(Boolean.TRUE.equals(argParameters.getPlanningSysReqEnabled()));
				chboxPlanningSystemRequirement.setEnabled(Boolean.TRUE.equals(argParameters.getPlanningEnabled()));
			}
			if (chboxPlanningQoIPlanner != null) {
				chboxPlanningQoIPlanner.setSelection(Boolean.TRUE.equals(argParameters.getPlanningQoIPlannerEnabled()));
				chboxPlanningQoIPlanner.setEnabled(Boolean.TRUE.equals(argParameters.getPlanningEnabled()));
			}
			if (chboxPlanningUncertainty != null) {
				chboxPlanningUncertainty
						.setSelection(Boolean.TRUE.equals(argParameters.getPlanningUncertaintyEnabled()));
				chboxPlanningUncertainty.setEnabled(Boolean.TRUE.equals(argParameters.getPlanningEnabled()));
			}
			if (chboxPlanningDecision != null) {
				chboxPlanningDecision.setSelection(Boolean.TRUE.equals(argParameters.getPlanningDecisionEnabled()));
				chboxPlanningDecision.setEnabled(Boolean.TRUE.equals(argParameters.getPlanningEnabled()));
			}
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

		// Render
		refreshPIRTQoIList();

		// load checkbox
		if (argParameters != null && chboxPirt != null) {
			chboxPirt.setSelection(Boolean.TRUE.equals(argParameters.getPirtEnabled()));
		}
	}

	/**
	 * Reload PCMM data
	 */
	void reloadPCMM() {
		// Get Tag list
		pcmmTagList = getViewManager().getAppManager().getService(IPCMMApplication.class).getTags();
		refreshPCMMTagList();

		// load checkboxes
		if (argParameters != null) {
			if (chboxPcmm != null) {
				chboxPcmm.setSelection(Boolean.TRUE.equals(argParameters.getPcmmEnabled()));
			}
			if (chboxPcmmPlanning != null) {
				chboxPcmmPlanning.setSelection(Boolean.TRUE.equals(argParameters.getPcmmPlanningEnabled()));
				chboxPcmmPlanning.setEnabled(Boolean.TRUE.equals(argParameters.getPcmmEnabled()));
			}
			if (chboxPcmmEvidence != null) {
				chboxPcmmEvidence.setSelection(Boolean.TRUE.equals(argParameters.getPcmmEvidenceEnabled()));
				chboxPcmmEvidence.setEnabled(Boolean.TRUE.equals(argParameters.getPcmmEnabled()));
			}
			if (chboxPcmmAssessment != null) {
				chboxPcmmAssessment.setSelection(Boolean.TRUE.equals(argParameters.getPcmmAssessmentEnabled()));
				chboxPcmmAssessment.setEnabled(Boolean.TRUE.equals(argParameters.getPcmmEnabled()));
			}
		}
	}

	/**
	 * Reload Custom ending
	 */
	void reloadCustomEnding() {
		// load checkboxes
		if (argParameters != null) {
			if (chboxCustomEnding != null) {
				chboxCustomEnding.setSelection(Boolean.TRUE.equals(argParameters.getCustomEndingEnabled()));
				chboxCustomEnding.notifyListeners(SWT.Selection, new Event());
			}
			if (txtCustomEndingFilePath != null) {
				txtCustomEndingFilePath.setValue(argParameters.getCustomEndingFilePath());
			}
		}
	}

	/**
	 * Creates the page
	 */
	private void renderPage() {
		// Main composite
		renderMainComposite();

		// Render footer buttons
		renderFooterButtons();
	}

	/**
	 * Render main table composite
	 */
	private void renderMainComposite() {
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
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainComposite.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));

		// Main table composite
		firstScroll.setContent(mainComposite);
		firstScroll.setExpandHorizontal(true);
		firstScroll.setExpandVertical(true);
		firstScroll.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		// Render sub-composites
		renderARGSetup();
		renderARGReportParameters();
		renderPlanning();
		renderPIRT();
		renderPCMM();
		renderCustomEnding();
		renderARGConsole();
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
		ButtonTheme btnBack = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER,
				btnBackOptions);

		// Footer buttons - Help
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> HelpTools.openContextualHelp());

		ButtonTheme btnHelp = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER,
				btnHelpOptions);
		RowData btnLayoutData = new RowData();
		btnHelp.setLayoutData(btnLayoutData);
		HelpTools.addContextualHelp(compositeButtonsFooterLeft, ContextualHelpId.REPORTING);

		// Footer buttons - Generate
		Map<String, Object> btngenOptions = new HashMap<>();
		btngenOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_REPORTVIEW_BTN_GENERATE));
		btngenOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btngenOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_GEN_CF_REPORT);
		btngenOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
		btngenOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> viewCtrl.generateReport());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterRight, SWT.RIGHT | SWT.LEFT_TO_RIGHT,
				btngenOptions);

		// Footer buttons - Back - plug
		getViewManager().plugBackHomeButton(btnBack);

		// layout view
		compositeButtonsFooter.layout();
	}

	/**
	 * Render ARG setup composite
	 */
	private void renderARGSetup() {

		// ARG setup composite
		Composite argSetupComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		argSetupComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		argSetupComposite.setLayout(gridLayout);
		argSetupComposite.setBackground(argSetupComposite.getParent().getBackground());

		// PIRT collapse
		CollapsibleWidget pirtCollapse = new CollapsibleWidget(getViewManager().getRscMgr(), mainComposite,
				SWT.FILL | SWT.BORDER, argSetupComposite);
		pirtCollapse.setLabel(RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_TITLE));

		// Python executable - Label
		CLabel argPythonLabel = new CLabel(argSetupComposite, SWT.NONE);
		argPythonLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		argPythonLabel.setText(RscTools.getString(RscConst.PREFS_GLOBAL_PYTHON_EXECUTABLE));
		argPythonLabel.setBackground(argPythonLabel.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), argPythonLabel);

		// Python executable - Label Content
		txtArgSetupPython = FormFactory.createTextWidget(getViewManager().getRscMgr(), argSetupComposite, true, null);
		txtArgSetupPython.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		txtArgSetupPython.setEnabled(true);
		txtArgSetupPython.setBackground(txtArgSetupPython.getParent().getBackground());
		txtArgSetupPython.addListener(SWT.KeyUp, e -> viewCtrl.changedARGSetupPython(txtArgSetupPython.getValue()));

		// ARG executable - Label
		CLabel argExecLabel = new CLabel(argSetupComposite, SWT.NONE);
		argExecLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		argExecLabel.setText(RscTools.getString(RscConst.PREFS_GLOBAL_ARG_EXECUTABLE));
		argExecLabel.setBackground(argExecLabel.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), argExecLabel);

		// Browse composite
		Composite browseCompositeARGExecutable = new Composite(argSetupComposite, SWT.FILL);
		GridLayout gdbrowseCompositeARGExecutable = new GridLayout(2, false);
		gdbrowseCompositeARGExecutable.marginWidth = 0;
		browseCompositeARGExecutable.setLayout(gdbrowseCompositeARGExecutable);
		browseCompositeARGExecutable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		browseCompositeARGExecutable.setBackground(browseCompositeARGExecutable.getParent().getBackground());

		// ARG executable - Label Content
		txtArgSetupExecutable = FormFactory.createTextWidget(getViewManager().getRscMgr(), browseCompositeARGExecutable,
				true, null);
		txtArgSetupExecutable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		txtArgSetupExecutable.setEnabled(true);
		txtArgSetupExecutable.setBackground(txtArgSetupExecutable.getParent().getBackground());
		txtArgSetupExecutable.addListener(SWT.KeyUp, e -> {
			viewCtrl.changedARGSetupExecutable(txtArgSetupExecutable.getValue());
			txtArgSetupExecutable.clearHelper();
		});

		// button browse
		Map<String, Object> optionsBtnBrowseARGExecutable = new HashMap<>();
		optionsBtnBrowseARGExecutable.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowseARGExecutable.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowseARGExecutable.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme btnBrowseARGExecutable = FormFactory.createButton(getViewManager().getRscMgr(),
				browseCompositeARGExecutable, null, optionsBtnBrowseARGExecutable);

		btnBrowseARGExecutable.addListener(SWT.Selection, event -> {
			FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
			dlg.setFilterPath(FileTools.getParentFolder(StringTools.getOrEmpty(txtArgSetupExecutable.getValue())));
			dlg.setFileName(FileTools.getFileName(StringTools.getOrEmpty(txtArgSetupExecutable.getValue())));
			dlg.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_TITLE));
			String path = dlg.open();

			if (!StringUtils.isBlank(path)) {
				txtArgSetupExecutable.setValue(FileTools.getNormalizedPath(path));
				viewCtrl.changedARGSetupExecutable(txtArgSetupExecutable.getValue());
				txtArgSetupExecutable.clearHelper();
			}
		});

		// ARG setenv - Label
		CLabel argSetEnvLabel = new CLabel(argSetupComposite, SWT.NONE);
		argSetEnvLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		argSetEnvLabel.setText(RscTools.getString(RscConst.PREFS_GLOBAL_ARG_SETENV));
		argSetEnvLabel.setBackground(argSetEnvLabel.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), argSetEnvLabel);

		// Browse composite
		Composite browseCompositeARGPreScript = new Composite(argSetupComposite, SWT.FILL);
		GridLayout gdbrowseCompositeARGPreScript = new GridLayout(2, false);
		gdbrowseCompositeARGPreScript.marginWidth = 0;
		browseCompositeARGPreScript.setLayout(gdbrowseCompositeARGPreScript);
		browseCompositeARGPreScript.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		browseCompositeARGPreScript.setBackground(browseCompositeARGPreScript.getParent().getBackground());

		// ARG setenv - Label Content
		txtArgSetupPreScript = FormFactory.createTextWidget(getViewManager().getRscMgr(), browseCompositeARGPreScript,
				true, null);
		txtArgSetupPreScript.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		txtArgSetupPreScript.setEnabled(true);
		txtArgSetupPreScript.setBackground(txtArgSetupPreScript.getParent().getBackground());
		txtArgSetupPreScript.addListener(SWT.KeyUp,
				e -> viewCtrl.changedARGSetupPreScript(txtArgSetupPreScript.getValue()));

		// button browse
		Map<String, Object> optionsBtnBrowseARGPreScript = new HashMap<>();
		optionsBtnBrowseARGPreScript.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowseARGPreScript.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowseARGPreScript.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme btnBrowseARGPreScript = FormFactory.createButton(getViewManager().getRscMgr(),
				browseCompositeARGPreScript, null, optionsBtnBrowseARGPreScript);

		btnBrowseARGPreScript.addListener(SWT.Selection, event -> {
			FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
			dlg.setFilterPath(FileTools.getParentFolder(StringTools.getOrEmpty(txtArgSetupPreScript.getValue())));
			dlg.setFileName(FileTools.getFileName(StringTools.getOrEmpty(txtArgSetupPreScript.getValue())));
			dlg.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_TITLE));
			String path = dlg.open();

			if (!StringUtils.isBlank(path)) {
				txtArgSetupPreScript.setValue(FileTools.getNormalizedPath(path));
				viewCtrl.changedARGSetupPreScript(txtArgSetupPreScript.getValue());
			}
		});

		// ARG use local conf - Label
		CLabel lblUseArgLocalConf = new CLabel(argSetupComposite, SWT.NONE);
		lblUseArgLocalConf.setBackground(lblUseArgLocalConf.getParent().getBackground());

		// ARG use local conf - Label Content
		chboxUseARGLocalConf = FormFactory.createCheckbox(argSetupComposite,
				new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1),
				RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_CHBOX_LOCAL_CONF));
		chboxUseARGLocalConf.setBackground(chboxUseARGLocalConf.getParent().getBackground());
		chboxUseARGLocalConf.setSelection(false);

		// ARG setup bottom composite
		Composite argSetupBottomComposite = new Composite(argSetupComposite, SWT.NONE);
		GridLayout gdargSetupBtnComposite = new GridLayout(2, false);
		gdargSetupBtnComposite.marginWidth = 0;
		argSetupBottomComposite.setLayout(gdargSetupBtnComposite);
		argSetupBottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		argSetupBottomComposite.setBackground(argSetupBottomComposite.getParent().getBackground());

		// ARG version - Label
		textARGVersion = FormFactory.getNotificationLabel(getViewManager().getRscMgr(), argSetupBottomComposite,
				NotificationFactory.getNewWarning());

		// ARG setup buttons - Open Preferences
		Map<String, Object> btnOpenPrefsOptions = new HashMap<>();
		btnOpenPrefsOptions.put(ButtonTheme.OPTION_TEXT,
				RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_BTN_OPENPREFS));
		btnOpenPrefsOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnOpenPrefsOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_OPEN);
		btnOpenPrefsOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnOpenPrefsOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> {

			boolean done = openPreferences();

			if (done && !StringUtils.isBlank(PrefTools.getARGExecutablePath())
					&& !chboxUseARGLocalConf.getSelection()) {
				// ask to set local configuration
				boolean replaceQuestion = MessageDialog.openQuestion(getShell(),
						RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_TITLE),
						RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_QUESTION_LOCAL_CONF_BYDEFAULT));
				if (replaceQuestion) {
					chboxUseARGLocalConf.setSelection(true);
					chboxUseARGLocalConf.notifyListeners(SWT.Selection, event);
				}
			}
		});
		ButtonTheme prefButton = FormFactory.createButton(getViewManager().getRscMgr(), argSetupBottomComposite, null,
				btnOpenPrefsOptions);
		GridData gdBtnOpenPrefs = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false);
		prefButton.setLayoutData(gdBtnOpenPrefs);

		// listeners
		chboxUseARGLocalConf.addListener(SWT.Selection, event -> {
			if (chboxUseARGLocalConf.getSelection()) {
				// check if local configuration is setted
				if (StringUtils.isBlank(PrefTools.getARGExecutablePath())) {
					boolean openQuestion = MessageDialog.openQuestion(getShell(),
							RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_TITLE),
							RscTools.getString(RscConst.MSG_REPORTVIEW_ARGSETUP_QUESTION_NOTSETTED_OPENPREFS));
					if (openQuestion) {
						openPreferences();
					}
				}
				txtArgSetupPython.setValue(PrefTools.getPythonExecutablePath());
				txtArgSetupExecutable.setValue(PrefTools.getARGExecutablePath());
				txtArgSetupPreScript.setValue(PrefTools.getARGSetEnvScriptPath());
			} else {
				txtArgSetupPython.setValue(StringTools.getOrEmpty(argParameters.getPythonExecPath()));
				txtArgSetupExecutable.setValue(StringTools.getOrEmpty(argParameters.getArgExecPath()));
				txtArgSetupPreScript.setValue(StringTools.getOrEmpty(argParameters.getArgPreScript()));
			}

			// enable/disable widgets
			txtArgSetupPython.setEnabled(!chboxUseARGLocalConf.getSelection());
			txtArgSetupExecutable.setEnabled(!chboxUseARGLocalConf.getSelection());
			txtArgSetupPreScript.setEnabled(!chboxUseARGLocalConf.getSelection());
			btnBrowseARGExecutable.setEnabled(!chboxUseARGLocalConf.getSelection());
			btnBrowseARGPreScript.setEnabled(!chboxUseARGLocalConf.getSelection());

			// update use local conf
			viewCtrl.changedARGSetupUseLocalConf(chboxUseARGLocalConf.getSelection());

			// reload if ARG is setted
			if (!StringUtils.isBlank(txtArgSetupExecutable.getValue())) {
				reloadARGSetupARGVersion();
				reloadARGTypes();
				reloadARGParameters();
				txtArgSetupExecutable.clearHelper();
			}
		});
	}

	/**
	 * Open local ARG Setup preferences
	 */
	private boolean openPreferences() {
		boolean done = false;
		PreferenceDialog pref = PrefTools.getCFPrefDialog(getShell());
		if (pref != null) {
			int open = pref.open();
			done = (open == Window.OK);

			// reload the ARG setup and parameters
			reloadARGSetup();
			reloadARGTypes();
			reloadARGParameters();
		}
		return done;
	}

	/**
	 * Render ARG Report Parameters
	 */
	private void renderARGReportParameters() {

		// Main configuration composite
		Composite parametersComposite = new Composite(mainComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(2, false);
		parametersComposite.setLayout(gridLayout);
		parametersComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		parametersComposite.setBackground(parametersComposite.getParent().getBackground());

		// collapse
		CollapsibleWidget argParamCollapse = new CollapsibleWidget(getViewManager().getRscMgr(), mainComposite,
				SWT.FILL | SWT.BORDER, parametersComposite);
		argParamCollapse.setLabel(RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_TITLE));

		/*
		 * Parameters file
		 */
		// ARG parameters file - Label
		Label lblParametersFile = FormFactory.createLabel(parametersComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_PARAMFILE));
		lblParametersFile.setBackground(lblParametersFile.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), lblParametersFile);

		// Browse composite
		Composite browseCompositeARGParamFile = new Composite(parametersComposite, SWT.FILL);
		GridLayout gdbrowseCompositeARGParamFile = new GridLayout(2, false);
		gdbrowseCompositeARGParamFile.marginWidth = 0;
		browseCompositeARGParamFile.setLayout(gdbrowseCompositeARGParamFile);
		browseCompositeARGParamFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		browseCompositeARGParamFile.setBackground(browseCompositeARGParamFile.getParent().getBackground());

		// ARG Parameters file - text input
		txtARGParamParametersFile = FormFactory.createTextWidget(getViewManager().getRscMgr(),
				browseCompositeARGParamFile, true, null);
		txtARGParamParametersFile.setBackground(txtARGParamParametersFile.getParent().getBackground());
		txtARGParamParametersFile.addListener(SWT.KeyUp,
				e -> viewCtrl.changedARGParametersFile(txtARGParamParametersFile.getValue()));

		// button browse
		Map<String, Object> optionsBtnBrowseARGParamFile = new HashMap<>();
		optionsBtnBrowseARGParamFile.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowseARGParamFile.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowseARGParamFile.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme btnBrowseARGParamFile = FormFactory.createButton(getViewManager().getRscMgr(),
				browseCompositeARGParamFile, null, optionsBtnBrowseARGParamFile);

		btnBrowseARGParamFile.addListener(SWT.Selection,
				event -> viewCtrl.browseIntoWorkspace(txtARGParamParametersFile,
						RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_PARAMFILE)));

		/*
		 * Structure file
		 */
		// ARG structure file - Label
		Label lblStructureFile = FormFactory.createLabel(parametersComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_STRUCTFILE));
		lblStructureFile.setBackground(lblStructureFile.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), lblStructureFile);

		// Browse composite
		Composite browseCompositeStructureFile = new Composite(parametersComposite, SWT.FILL);
		GridLayout gdbrowseCompositeStructureFile = new GridLayout(2, false);
		gdbrowseCompositeStructureFile.marginWidth = 0;
		browseCompositeStructureFile.setLayout(gdbrowseCompositeStructureFile);
		browseCompositeStructureFile.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		browseCompositeStructureFile.setBackground(browseCompositeStructureFile.getParent().getBackground());

		// ARG structure file - text input
		txtARGParamStructureFile = FormFactory.createTextWidget(getViewManager().getRscMgr(),
				browseCompositeStructureFile, true, null);
		txtARGParamStructureFile.setBackground(txtARGParamStructureFile.getParent().getBackground());
		txtARGParamStructureFile.addListener(SWT.KeyUp,
				e -> viewCtrl.changedARGStructureFile(txtARGParamStructureFile.getValue()));

		// button browse
		Map<String, Object> optionsBtnBrowseARGStructureFile = new HashMap<>();
		optionsBtnBrowseARGStructureFile.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowseARGStructureFile.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowseARGStructureFile.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme btnBrowseARGStructureFile = FormFactory.createButton(getViewManager().getRscMgr(),
				browseCompositeStructureFile, null, optionsBtnBrowseARGStructureFile);

		btnBrowseARGStructureFile.addListener(SWT.Selection,
				event -> viewCtrl.browseIntoWorkspace(txtARGParamStructureFile,
						RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_STRUCTFILE)));

		/*
		 * Output folder
		 */
		// ARG output folder - Label
		Label lblOutput = FormFactory.createLabel(parametersComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_OUTPUTFOLDER));
		lblOutput.setBackground(lblOutput.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), lblOutput);

		// Browse output composite
		Composite browseOutputComposite = new Composite(parametersComposite, SWT.FILL);
		GridLayout gdbrowseOutputComposite = new GridLayout(2, false);
		gdbrowseOutputComposite.marginWidth = 0;
		browseOutputComposite.setLayout(gdbrowseOutputComposite);
		browseOutputComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		browseOutputComposite.setBackground(browseOutputComposite.getParent().getBackground());

		// ARG output folder - text input
		txtARGParamOutput = FormFactory.createTextWidget(getViewManager().getRscMgr(), browseOutputComposite, true,
				null);
		txtARGParamOutput.setBackground(txtARGParamOutput.getParent().getBackground());
		txtARGParamOutput.addListener(SWT.KeyUp, e -> viewCtrl.changedARGParamOutput(txtARGParamOutput.getValue()));

		// button browse
		Map<String, Object> optionsBtnBrowseARGOutput = new HashMap<>();
		optionsBtnBrowseARGOutput.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowseARGOutput.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowseARGOutput.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme btnBrowseARGOutput = FormFactory.createButton(getViewManager().getRscMgr(), browseOutputComposite,
				null, optionsBtnBrowseARGOutput);

		btnBrowseARGOutput.addListener(SWT.Selection, event -> {
			IContainer initialSelection = WorkspaceTools
					.getFolderInWorkspaceForPath(new Path(CFVariableResolver.removeAll(txtARGParamOutput.getValue())));
			ContainerPickerDialog dialog = new ContainerPickerDialog(getShell(), initialSelection, true,
					RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_OUTPUTFOLDER_MSG));
			dialog.setTitle(RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_OUTPUTFOLDER));

			if (dialog.open() == Window.OK) {
				Path resource = (Path) dialog.getFirstResult();
				txtARGParamOutput.setValue(FileTools.prefixWorkspaceVar(resource.toString()));
				viewCtrl.changedARGParamOutput(txtARGParamOutput.getValue());
			}
		});

		/*
		 * Filename
		 */
		// ARG filename - Label
		Label lblFilename = FormFactory.createLabel(parametersComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_FILENAME));
		lblFilename.setBackground(lblFilename.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), lblFilename);

		// ARG filename - text input
		txtARGParamFilename = FormFactory.createTextWidget(getViewManager().getRscMgr(), parametersComposite, true,
				null);
		txtARGParamFilename.setBackground(txtARGParamFilename.getParent().getBackground());
		txtARGParamFilename.addListener(SWT.KeyUp,
				e -> viewCtrl.changedARGParamFilename(txtARGParamFilename.getValue()));

		/*
		 * Title
		 */
		// ARG Report Title - Label
		Label lblReportTitle = FormFactory.createLabel(parametersComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_REPORTTITLE));
		lblReportTitle.setBackground(lblReportTitle.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), lblReportTitle);

		// ARG Report Title - text input
		txtARGParamReportTitle = FormFactory.createTextWidget(getViewManager().getRscMgr(), parametersComposite, true,
				null);
		txtARGParamReportTitle.setBackground(txtARGParamReportTitle.getParent().getBackground());
		txtARGParamReportTitle.addListener(SWT.KeyUp,
				e -> viewCtrl.changedARGParamReportTitle(txtARGParamReportTitle.getValue()));

		/*
		 * Author
		 */
		// ARG Author - Label
		Label lblARGParamAuthor = FormFactory.createLabel(parametersComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_AUTHOR));
		lblARGParamAuthor.setBackground(lblARGParamAuthor.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), lblARGParamAuthor);

		// ARG Author - text input
		txtARGParamAuthor = FormFactory.createTextWidget(getViewManager().getRscMgr(), parametersComposite, true, null);
		txtARGParamAuthor.setBackground(txtARGParamAuthor.getParent().getBackground());
		txtARGParamAuthor.addListener(SWT.KeyUp, e ->

		viewCtrl.changedARGParamAuthor(txtARGParamAuthor.getValue()));

		/*
		 * Back-end type
		 */
		// ARG Back-end type - Label
		Label lblBackendType = FormFactory.createLabel(parametersComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_BACKENDTYPE));
		lblBackendType.setBackground(lblBackendType.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), lblBackendType);

		// ARG Back-end type - text input
		List<Object> backend = ARGBackendDefault.getValues().stream().map(ARGBackendDefault::getBackend)
				.collect(Collectors.toList());
		cbxARGParamBackendType = FormFactory.createCombo(parametersComposite, null, backend);
		cbxARGParamBackendType.getControl().addListener(SWT.Selection,
				event -> viewCtrl.changedARGParamBackendType(getCbxSelection(String.class, cbxARGParamBackendType)));

		/*
		 * Inline Word document
		 */
		// Inline Word document - Label
		CLabel lblUseArgLocalConf = new CLabel(parametersComposite, SWT.NONE);
		lblUseArgLocalConf.setBackground(lblUseArgLocalConf.getParent().getBackground());

		// Inline Word document - checkbox ********** Experimental *********
		boolean enableInlining = PrefTools.getPreferenceBoolean(PrefTools.DEVOPTS_REPORT_INLINEWORD_KEY);
		chboxARGParamInlineWordDoc = FormFactory.createCheckbox(parametersComposite,
				new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1),
				RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_CHBOX_INLINE_WORD_DOC));
		chboxARGParamInlineWordDoc.setBackground(chboxARGParamInlineWordDoc.getParent().getBackground());
		if (enableInlining) {
			chboxARGParamInlineWordDoc.setSelection(true);
		}
		chboxARGParamInlineWordDoc.addListener(SWT.Selection,
				event -> viewCtrl.changedARGParamInlineWordDoc(chboxARGParamInlineWordDoc.getSelection()));
		reloadWordInlining();

		/*
		 * Report type
		 */
		// ARG Report type - Label
		lblReportType = FormFactory.createLabel(parametersComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_ARGPARAM_REPORTTYPE));
		lblReportType.setBackground(lblReportType.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), lblReportType);
		lblReportType.setVisible(false); // deactivated
											// if
											// there
											// is
											// only
											// one
											// report
											// type

		// ARG Report type - text input
		List<Object> reportTypes = ARGReportTypeDefault.getValues().stream().map(ARGReportTypeDefault::getType)
				.collect(Collectors.toList());
		cbxARGParamReportType = FormFactory.createCombo(parametersComposite, null, reportTypes);
		cbxARGParamReportType.getControl().addListener(SWT.Selection, event ->

		viewCtrl.changedARGParamReportType(getCbxSelection(String.class, cbxARGParamReportType)));
		cbxARGParamReportType.getControl().setVisible(false); // deactivated if there is only one report type
	}

	/**
	 * Reload the word inlining checkbox
	 */
	void reloadWordInlining() {
		if (chboxARGParamInlineWordDoc != null) {
			boolean enableInlining = PrefTools.getPreferenceBoolean(PrefTools.DEVOPTS_REPORT_INLINEWORD_KEY);
			String backendSelection = (String) ((IStructuredSelection) cbxARGParamBackendType.getSelection())
					.getFirstElement();
			chboxARGParamInlineWordDoc
					.setEnabled(ARGBackendDefault.WORD.getBackend().equals(backendSelection) && enableInlining);
			chboxARGParamInlineWordDoc
					.setVisible(ARGBackendDefault.WORD.getBackend().equals(backendSelection) && enableInlining);
		}
	}

	/**
	 * Render PIRT composite
	 */
	private void renderPIRT() {

		// PIRT main composite
		Composite pirtComposite = new Composite(mainComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		pirtComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		pirtComposite.setLayout(gridLayout);
		pirtComposite.setBackground(pirtComposite.getParent().getBackground());

		// PIRT collapse
		CollapsibleWidget pirtCollapse = new CollapsibleWidget(getViewManager().getRscMgr(), mainComposite,
				SWT.FILL | SWT.BORDER, pirtComposite, RscTools.getString(RscConst.MSG_REPORTVIEW_PIRT_TITLE), true,
				true);
		chboxPirt = pirtCollapse.getCheckbox();

		// Select tag - Label
		CLabel qoiListLabel = new CLabel(pirtComposite, SWT.LEFT);
		qoiListLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		qoiListLabel.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_PIRT_QOI_LIST));
		qoiListLabel.setBackground(qoiListLabel.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), qoiListLabel);

		// QoI composite
		pirtQoIComposite = new Composite(pirtComposite, SWT.FILL);
		GridLayout gdpirtQoIComposite = new GridLayout(2, false);
		pirtQoIComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		pirtQoIComposite.setLayout(gdpirtQoIComposite);
		pirtQoIComposite.setBackground(pirtQoIComposite.getParent().getBackground());

		// QoI
		refreshPIRTQoIList();

		// add listener
		chboxPirt.addListener(SWT.Selection, event -> {

			// update ARG parameters
			viewCtrl.changedPIRTEnabledOption(chboxPirt.getSelection());

			// disable/enable the associated qoi
			pirtControlMap.forEach((qoi, controlMap) -> {
				Control button = controlMap.get(Button.class);
				Control select = controlMap.get(Combo.class);
				if (button instanceof Button) {
					button.setEnabled(chboxPirt.getSelection());
					select.setEnabled(chboxPirt.getSelection() && ((Button) button).getSelection());
				}
			});
		});
	}

	/**
	 * Refresh the PIRT QoI List widgets
	 */
	private void refreshPIRTQoIList() {
		// Dispose pirtComposite children
		if (pirtQoIComposite != null && !pirtQoIComposite.isDisposed()) {
			for (Control child : pirtQoIComposite.getChildren()) {
				if (!child.isDisposed()) {
					child.dispose();
				}
			}
			pirtQoIComposite.layout();
		}

		// Render QoIs
		if (pirtQoIList != null && !pirtQoIList.isEmpty()) {
			for (QuantityOfInterest qoi : pirtQoIList) {
				renderPIRTQoI(pirtQoIComposite, qoi);
			}
		}

		// Layout the table composite
		if (pirtQoIComposite != null && !pirtQoIComposite.isDisposed()) {
			pirtQoIComposite.layout();
		}
	}

	/**
	 * Render PIRT QuantityOfInterest
	 * 
	 * @param pirtQoIComposite the pirt composite
	 * @param qoi              The Quantity Of Interest to render
	 */
	private void renderPIRTQoI(Composite pirtQoIComposite, QuantityOfInterest quantityOfInterest) {

		Map<Class<?>, Control> controlMap = new HashMap<>();

		// load preselected data
		boolean selected = true;
		QuantityOfInterest tagSelection = quantityOfInterest;
		if (argParameters != null && argParameters.getQoiSelectedList() != null) {
			Optional<ARGParametersQoIOption> qoiOption = argParameters.getQoiSelectedList().stream()
					.filter(opt -> quantityOfInterest.equals(opt.getQoi())).findFirst();
			if (qoiOption.isPresent()) {
				selected = qoiOption.get().getEnabled();
				tagSelection = qoiOption.get().getTag();
			}
		}

		// Label
		Button qoiButton = new Button(pirtQoIComposite, SWT.CHECK);
		qoiButton.setText(quantityOfInterest.getSymbol() + RscTools.COLON);
		qoiButton.setBackground(qoiButton.getParent().getBackground());
		controlMap.put(Button.class, qoiButton);

		// QoI tagged list - Add current version
		List<Object> tempsPirtQoiTagged = new ArrayList<>();
		// add qoi current version if it contains PIRT table
		if (quantityOfInterest.getPhenomenonGroupList() != null
				&& !quantityOfInterest.getPhenomenonGroupList().isEmpty()) {
			tempsPirtQoiTagged.add(quantityOfInterest);
		}

		// QoI tagged list - Add tagged version
		List<QuantityOfInterest> pirtQoiTagged = quantityOfInterest.getChildren();
		if (pirtQoiTagged != null) {
			// sort tag list by date tag
			pirtQoiTagged.sort(new QoiTaggedComparator());
			Collections.reverse(pirtQoiTagged);

			// add tag if it contains PIRT table
			pirtQoiTagged.forEach(tag -> {
				if (tag.getPhenomenonGroupList() != null && !tag.getPhenomenonGroupList().isEmpty()) {
					tempsPirtQoiTagged.add(tag);
				}
			});
		}

		// enable/disable depending of the available qoi/tag list
		qoiButton.setSelection(selected && !tempsPirtQoiTagged.isEmpty());
		qoiButton.setEnabled(chboxPirt.getSelection() && !tempsPirtQoiTagged.isEmpty());
		if (!tempsPirtQoiTagged.contains(tagSelection) && !tempsPirtQoiTagged.isEmpty()) {
			tagSelection = (QuantityOfInterest) tempsPirtQoiTagged.get(0);
		}
		viewCtrl.changedQoISelected(quantityOfInterest, tagSelection, qoiButton.getSelection());

		// Select - Combo
		ComboViewer qoiSelect = FormFactory.createCombo(pirtQoIComposite, null, tempsPirtQoiTagged);
		qoiSelect.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof QuantityOfInterest) {
					QuantityOfInterest qoi = (QuantityOfInterest) element;
					if (qoi.equals(quantityOfInterest)) {
						return RscTools.getString(RscConst.MSG_REPORTVIEW_PIRT_QOI_CURRENT);
					} else {
						String tagLabelPattern = "{0}"; //$NON-NLS-1$
						return MessageFormat.format(tagLabelPattern,
								DateTools.formatDate(qoi.getTagDate(), DateTools.getDateTimeFormat()));
					}
				}
				return null;
			}
		});
		qoiSelect.getControl().setEnabled(chboxPirt.getSelection() && qoiButton.getSelection());
		controlMap.put(Combo.class, qoiSelect.getControl());

		pirtControlMap.put(quantityOfInterest, controlMap);

		// QoI tagged list - set data
		qoiSelect.setSelection(new StructuredSelection(tagSelection));

		// listeners
		qoiButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean btnSelected = qoiButton.getSelection();
				viewCtrl.changedQoISelected(quantityOfInterest, quantityOfInterest, btnSelected);
				qoiSelect.getControl().setEnabled(chboxPirt.getSelection() && btnSelected);
			}
		});

		qoiSelect.getControl().addListener(SWT.Selection,
				selectionChangedEvent -> viewCtrl.changedQoISelected(quantityOfInterest,
						getCbxSelection(QuantityOfInterest.class, qoiSelect), qoiButton.getSelection()));

		// Generate
		pirtQoIComposite.layout();
	}

	/**
	 * Render PCMM composite
	 */
	private void renderPCMM() {
		// PCMM main composite
		pcmmComposite = new Composite(mainComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		pcmmComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		pcmmComposite.setLayout(gridLayout);
		pcmmComposite.setBackground(pcmmComposite.getParent().getBackground());

		// PCMM collapse
		CollapsibleWidget pcmmCollapse = new CollapsibleWidget(getViewManager().getRscMgr(), mainComposite,
				SWT.FILL | SWT.BORDER, pcmmComposite, RscTools.getString(RscConst.MSG_REPORTVIEW_PCMM_TITLE), true,
				true);
		chboxPcmm = pcmmCollapse.getCheckbox();

		// Elements
		renderPCMMTagSelect();
		renderPCMMFeatures();

		// add listener
		chboxPcmm.addListener(SWT.Selection, event -> {
			if (chboxPcmmPlanning != null) {
				chboxPcmmPlanning.setEnabled(chboxPcmm.getSelection());
			}
			if (chboxPcmmEvidence != null) {
				chboxPcmmEvidence.setEnabled(chboxPcmm.getSelection());
			}
			if (chboxPcmmAssessment != null) {
				chboxPcmmAssessment.setEnabled(chboxPcmm.getSelection());
			}
			if (cbxPcmmTag != null) {
				cbxPcmmTag.getControl().setEnabled(chboxPcmm.getSelection());
			}

			// Update ARG parameters
			viewCtrl.changedPCMMEnabledOption(chboxPcmm.getSelection());
		});

		chboxPcmmPlanning.addListener(SWT.Selection,
				event -> viewCtrl.changedPCMMPlanningEnabledOption(chboxPcmmPlanning.getSelection()));
		chboxPcmmEvidence.addListener(SWT.Selection,
				event -> viewCtrl.changedPCMMEvidenceEnabledOption(chboxPcmmEvidence.getSelection()));
		chboxPcmmAssessment.addListener(SWT.Selection,
				event -> viewCtrl.changedPCMMAssessmentEnabledOption(chboxPcmmAssessment.getSelection()));
		cbxPcmmTag.getControl().addListener(SWT.Selection,
				event -> viewCtrl.changedPCMMTagSelected(getCbxSelection(Tag.class, cbxPcmmTag)));
	}

	/**
	 * Render PCMM tag selection
	 */
	private void renderPCMMTagSelect() {

		Composite pcmmTagSelectComposite = new Composite(pcmmComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		pcmmTagSelectComposite.setLayout(gridLayout);
		pcmmTagSelectComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		pcmmTagSelectComposite.setBackground(pcmmTagSelectComposite.getParent().getBackground());

		// Select tag - Label
		Label selectTagLabel = FormFactory.createLabel(pcmmTagSelectComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_PCMM_SELECT_TAG));
		selectTagLabel.setBackground(selectTagLabel.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), selectTagLabel);

		// Select tag - Combo
		renderPCMMTagList(pcmmTagSelectComposite);

		// Divider Label
		CLabel divideLabel = new CLabel(pcmmComposite, SWT.LEFT);
		divideLabel.setText(" "); //$NON-NLS-1$
		divideLabel.setBackground(divideLabel.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), divideLabel);
	}

	/**
	 * Create PCMM tags Combo list
	 * 
	 * @param comboComposite The parent composite
	 */
	private void renderPCMMTagList(Composite comboComposite) {
		// Create
		cbxPcmmTag = FormFactory.createCombo(comboComposite, null, new ArrayList<>());

		// Set label provider
		cbxPcmmTag.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (((Tag) element).getId() == null) {
					return RscTools.getString(RscConst.MSG_TAG_PART_COMBO_DEFAULTTAG);
				} else {
					String tagLabelPattern = "{0} ({1})"; //$NON-NLS-1$
					return MessageFormat.format(tagLabelPattern, ((Tag) element).getName(),
							DateTools.formatDate(((Tag) element).getDateTag(), DateTools.getDateTimeFormat()));
				}
			}
		});
	}

	/**
	 * Render PCMM feature selection
	 */
	private void renderPCMMFeatures() {

		// Check parameter - Label
		Label checkboxesLabel = FormFactory.createLabel(pcmmComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_PCMM_SELECT_CHECKBOXES));
		checkboxesLabel.setBackground(checkboxesLabel.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), checkboxesLabel);

		// Check-box Planning
		Composite pcmmCheckboxPlanningComposite = new Composite(pcmmComposite, SWT.FILL);
		GridLayout pcmmCheckboxPlanningCompositeGridLayout = new GridLayout();
		pcmmCheckboxPlanningComposite.setLayoutData(new GridData());
		pcmmCheckboxPlanningComposite.setLayout(pcmmCheckboxPlanningCompositeGridLayout);
		pcmmCheckboxPlanningComposite.setBackground(pcmmCheckboxPlanningComposite.getParent().getBackground());
		chboxPcmmPlanning = new Button(pcmmCheckboxPlanningComposite, SWT.CHECK);
		chboxPcmmPlanning.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_PCMM_CHECKBOX_PLANNING));
		chboxPcmmPlanning.setSelection(true);
		chboxPcmmPlanning.setBackground(chboxPcmmPlanning.getParent().getBackground());

		// Check-box Evidence
		Composite pcmmCheckboxEvidenceComposite = new Composite(pcmmComposite, SWT.FILL);
		GridLayout pcmmCheckboxEvidenceCompositeGridLayout = new GridLayout();
		pcmmCheckboxEvidenceComposite.setLayoutData(new GridData());
		pcmmCheckboxEvidenceComposite.setLayout(pcmmCheckboxEvidenceCompositeGridLayout);
		pcmmCheckboxEvidenceComposite.setBackground(pcmmCheckboxEvidenceComposite.getParent().getBackground());
		chboxPcmmEvidence = new Button(pcmmCheckboxEvidenceComposite, SWT.CHECK);
		chboxPcmmEvidence.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_PCMM_CHECKBOX_EVIDENCE));
		chboxPcmmEvidence.setSelection(true);
		chboxPcmmEvidence.setBackground(chboxPcmmEvidence.getParent().getBackground());

		// Check-box Assessment
		Composite pcmmCheckboxAssessmentComposite = new Composite(pcmmComposite, SWT.FILL);
		GridLayout pcmmCheckboxAssessmentCompositeGridLayout = new GridLayout();
		pcmmCheckboxAssessmentComposite.setLayoutData(new GridData());
		pcmmCheckboxAssessmentComposite.setLayout(pcmmCheckboxAssessmentCompositeGridLayout);
		pcmmCheckboxAssessmentComposite.setBackground(pcmmCheckboxAssessmentComposite.getParent().getBackground());
		chboxPcmmAssessment = new Button(pcmmCheckboxAssessmentComposite, SWT.CHECK);
		chboxPcmmAssessment.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_PCMM_CHECKBOX_ASSESSMENT));
		chboxPcmmAssessment.setSelection(true);
		chboxPcmmAssessment.setBackground(chboxPcmmAssessment.getParent().getBackground());
	}

	/**
	 * Refresh the pcmm tag list
	 */
	private void refreshPCMMTagList() {

		List<Tag> tempsTagList = new ArrayList<>();

		// add the first empty element to select the non-tagged state
		Tag noTag = new Tag();
		tempsTagList.add(noTag);

		if (pcmmTagList != null) {
			// sort tag list by date tag
			pcmmTagList.sort(new TagComparatorByDateTag());
			Collections.reverse(pcmmTagList);
			tempsTagList.addAll(pcmmTagList);
		}

		// set input
		cbxPcmmTag.setInput(tempsTagList);

		// set selection
		if (argParameters != null && argParameters.getPcmmTagSelected() != null) {
			cbxPcmmTag.setSelection(new StructuredSelection(argParameters.getPcmmTagSelected()));
		} else {
			cbxPcmmTag.setSelection(new StructuredSelection(noTag));
		}

		cbxPcmmTag.refresh();
		this.requestLayout();
	}

	/**
	 * Render Custom Report Ending composite (optional)
	 */
	private void renderCustomEnding() {

		// ARG Ending composite
		Composite argEndingComposite = new Composite(mainComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		argEndingComposite.setLayout(gridLayout);
		argEndingComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		argEndingComposite.setBackground(argEndingComposite.getParent().getBackground());

		// PCMM collapse
		CollapsibleWidget customEndingCollapse = new CollapsibleWidget(getViewManager().getRscMgr(), mainComposite,
				SWT.FILL | SWT.BORDER, argEndingComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_CUSTOMENDING_TITLE), true, true);
		chboxCustomEnding = customEndingCollapse.getCheckbox();

		// label
		GridData gdlabelFilePath = new GridData(GridData.FILL_HORIZONTAL);
		gdlabelFilePath.horizontalSpan = 2;
		Label label = FormFactory.createLabel(argEndingComposite,
				RscTools.getString(RscConst.MSG_REPORTVIEW_CUSTOMENDING_LABEL), gdlabelFilePath);
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), label);

		// text path
		txtCustomEndingFilePath = FormFactory.createTextWidget(getViewManager().getRscMgr(), argEndingComposite, true,
				null);
		txtCustomEndingFilePath.setBackground(txtCustomEndingFilePath.getParent().getBackground());

		// button browse
		Map<String, Object> optionsBtnBrowse = new HashMap<>();
		optionsBtnBrowse.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BROWSE));
		optionsBtnBrowse.put(ButtonTheme.OPTION_ENABLED, true);
		optionsBtnBrowse.put(ButtonTheme.OPTION_OUTLINE, true);
		ButtonTheme browseButton = FormFactory.createButton(getViewManager().getRscMgr(), argEndingComposite, null,
				optionsBtnBrowse);

		// add listener
		chboxCustomEnding.addListener(SWT.Selection, event -> {

			if (txtCustomEndingFilePath != null) {
				txtCustomEndingFilePath.clearHelper();
				txtCustomEndingFilePath.setEnabled(chboxCustomEnding.getSelection());
			}
			if (browseButton != null) {
				browseButton.setEnabled(chboxCustomEnding.getSelection());
			}

			// Update ARG parameters
			viewCtrl.changedARGCustomEndingEnabledOption(chboxCustomEnding.getSelection());
		});

		// text input listener
		txtCustomEndingFilePath.addListener(SWT.KeyUp,
				e -> viewCtrl.changedARGCustomEndingFile(txtCustomEndingFilePath.getValue()));

		// browse button listener
		browseButton.addListener(SWT.Selection, event -> viewCtrl.browseIntoWorkspace(txtCustomEndingFilePath,
				RscTools.getString(RscConst.MSG_REPORTVIEW_CUSTOMENDING_TITLE)));

	}

	/**
	 * Render ARG console composite
	 */
	private void renderARGConsole() {

		// ARG Console composite
		Composite argConsoleComposite = new Composite(mainComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		argConsoleComposite.setLayout(gridLayout);
		argConsoleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		argConsoleComposite.setBackground(argConsoleComposite.getParent().getBackground());

		// collapse
		CollapsibleWidget collapse = new CollapsibleWidget(getViewManager().getRscMgr(), mainComposite,
				SWT.FILL | SWT.BORDER, argConsoleComposite);
		collapse.setLabel(RscTools.getString(RscConst.MSG_REPORTVIEW_ARGCONSOLE_TITLE));

		// Console buttons composite
		Composite argConsoleButtonsComposite = new Composite(argConsoleComposite, SWT.RIGHT_TO_LEFT);
		RowLayout rlArgConsoleButton = new RowLayout();
		argConsoleButtonsComposite.setLayout(rlArgConsoleButton);
		argConsoleButtonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		argConsoleButtonsComposite.setBackground(argConsoleButtonsComposite.getParent().getBackground());

		// Console buttons - Clear
		Map<String, Object> btnClearOptions = new HashMap<>();
		btnClearOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_CLEAR));
		btnClearOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnClearOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_DELETE);
		btnClearOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnClearOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> {
			if (txtConsole != null) {
				txtConsole.getTextWidget().setText(RscTools.empty());
			}
		});
		new ButtonTheme(getViewManager().getRscMgr(), argConsoleButtonsComposite, SWT.CENTER, btnClearOptions);

		// Console buttons - Copy
		Map<String, Object> btnCopyOptions = new HashMap<>();
		btnCopyOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_COPY));
		btnCopyOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnCopyOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_COPY);
		btnCopyOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnCopyOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> {
			if (txtConsole != null) {
				SystemTools.copyToClipboard(txtConsole.getTextWidget().getText());
			}
		});
		new ButtonTheme(getViewManager().getRscMgr(), argConsoleButtonsComposite, SWT.CENTER, btnCopyOptions);

		// ARG Console
		txtConsole = new TextViewer(argConsoleComposite, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		txtConsole.setEditable(false);
		GridData gdConsole = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gdConsole.heightHint = 200;
		txtConsole.getTextWidget().setLayoutData(gdConsole);
	}

	/**
	 * Render Planning
	 * 
	 */
	private void renderPlanning() {
		// Planning main composite
		planningComposite = new Composite(mainComposite, SWT.FILL);
		GridLayout gridLayout = new GridLayout(1, true);
		planningComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		planningComposite.setLayout(gridLayout);
		planningComposite.setBackground(planningComposite.getParent().getBackground());

		// PLANNING collapse
		CollapsibleWidget planningCollapse = new CollapsibleWidget(getViewManager().getRscMgr(), mainComposite,
				SWT.FILL | SWT.BORDER, planningComposite, RscTools.getString(RscConst.MSG_REPORTVIEW_PLANNING_TITLE),
				true, true);
		chboxPlanning = planningCollapse.getCheckbox();

		// Render features
		renderPlanningFeatures();

		// add listener
		chboxPlanning.addListener(SWT.Selection, event -> {
			if (chboxPlanningIntendedPurpose != null) {
				chboxPlanningIntendedPurpose.setEnabled(chboxPlanning.getSelection());
			}
			if (chboxPlanningSystemRequirement != null) {
				chboxPlanningSystemRequirement.setEnabled(chboxPlanning.getSelection());
			}
			if (chboxPlanningQoIPlanner != null) {
				chboxPlanningQoIPlanner.setEnabled(chboxPlanning.getSelection());
			}
			if (chboxPlanningUncertainty != null) {
				chboxPlanningUncertainty.setEnabled(chboxPlanning.getSelection());
			}
			if (chboxPlanningDecision != null) {
				chboxPlanningDecision.setEnabled(chboxPlanning.getSelection());
			}

			// update ARG Parameters
			viewCtrl.changedPlanningEnabledOption(chboxPlanning.getSelection());
		});

		chboxPlanningIntendedPurpose.addListener(SWT.Selection,
				event -> viewCtrl.changedPlanningIntendedPurposeOption(chboxPlanningIntendedPurpose.getSelection()));
		chboxPlanningSystemRequirement.addListener(SWT.Selection,
				event -> viewCtrl.changedPlanningSysRequirementOption(chboxPlanningSystemRequirement.getSelection()));
		chboxPlanningQoIPlanner.addListener(SWT.Selection,
				event -> viewCtrl.changedPlanningQoIPlannerOption(chboxPlanningQoIPlanner.getSelection()));
		chboxPlanningUncertainty.addListener(SWT.Selection,
				event -> viewCtrl.changedPlanningUncertaintyOption(chboxPlanningUncertainty.getSelection()));
		chboxPlanningDecision.addListener(SWT.Selection,
				event -> viewCtrl.changedPlanningDecisionOption(chboxPlanningDecision.getSelection()));
	}

	/**
	 * Render Planning features
	 */
	private void renderPlanningFeatures() {

		// Check-box intended purpose
		Composite intendedPurposeCheckboxComposite = new Composite(planningComposite, SWT.FILL);
		GridLayout intendedPurposeCheckboxCompositeGridLayout = new GridLayout();
		intendedPurposeCheckboxComposite.setLayoutData(new GridData());
		intendedPurposeCheckboxComposite.setLayout(intendedPurposeCheckboxCompositeGridLayout);
		intendedPurposeCheckboxComposite.setBackground(intendedPurposeCheckboxComposite.getParent().getBackground());
		chboxPlanningIntendedPurpose = new Button(intendedPurposeCheckboxComposite, SWT.CHECK);
		chboxPlanningIntendedPurpose
				.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_PLANNING_CHECKBOX_INTENDEDPURPOSE));
		chboxPlanningIntendedPurpose.setSelection(true);
		chboxPlanningIntendedPurpose.setBackground(chboxPlanning.getParent().getBackground());

		// Check-box System Requirement
		Composite requirementCheckboxComposite = new Composite(planningComposite, SWT.FILL);
		GridLayout requirementCheckboxCompositeGridLayout = new GridLayout();
		requirementCheckboxComposite.setLayoutData(new GridData());
		requirementCheckboxComposite.setLayout(requirementCheckboxCompositeGridLayout);
		requirementCheckboxComposite.setBackground(requirementCheckboxComposite.getParent().getBackground());
		chboxPlanningSystemRequirement = new Button(requirementCheckboxComposite, SWT.CHECK);
		chboxPlanningSystemRequirement
				.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_PLANNING_CHECKBOX_REQUIREMENT));
		chboxPlanningSystemRequirement.setSelection(true);
		chboxPlanningSystemRequirement.setBackground(chboxPlanning.getParent().getBackground());

		// Check-box QoI Planner
		Composite qoiPlannerCheckboxComposite = new Composite(planningComposite, SWT.FILL);
		GridLayout qoiPlannerCheckboxCompositeGridLayout = new GridLayout();
		qoiPlannerCheckboxComposite.setLayoutData(new GridData());
		qoiPlannerCheckboxComposite.setLayout(qoiPlannerCheckboxCompositeGridLayout);
		qoiPlannerCheckboxComposite.setBackground(qoiPlannerCheckboxComposite.getParent().getBackground());
		chboxPlanningQoIPlanner = new Button(qoiPlannerCheckboxComposite, SWT.CHECK);
		chboxPlanningQoIPlanner.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_PLANNING_CHECKBOX_QOIPLANNER));
		chboxPlanningQoIPlanner.setSelection(true);
		chboxPlanningQoIPlanner.setBackground(chboxPlanning.getParent().getBackground());

		// Check-box Uncertainty Inventory
		Composite uncertaintyCheckboxComposite = new Composite(planningComposite, SWT.FILL);
		GridLayout uncertaintyCheckboxCompositeGridLayout = new GridLayout();
		uncertaintyCheckboxComposite.setLayoutData(new GridData());
		uncertaintyCheckboxComposite.setLayout(uncertaintyCheckboxCompositeGridLayout);
		uncertaintyCheckboxComposite.setBackground(uncertaintyCheckboxComposite.getParent().getBackground());
		chboxPlanningUncertainty = new Button(uncertaintyCheckboxComposite, SWT.CHECK);
		chboxPlanningUncertainty.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_PLANNING_CHECKBOX_UNCERTAINTY));
		chboxPlanningUncertainty.setSelection(true);
		chboxPlanningUncertainty.setBackground(chboxPlanning.getParent().getBackground());

		// Check-box Decisions
		Composite decisionCheckboxComposite = new Composite(planningComposite, SWT.FILL);
		GridLayout decisionCheckboxCompositeGridLayout = new GridLayout();
		decisionCheckboxComposite.setLayoutData(new GridData());
		decisionCheckboxComposite.setLayout(decisionCheckboxCompositeGridLayout);
		decisionCheckboxComposite.setBackground(decisionCheckboxComposite.getParent().getBackground());
		chboxPlanningDecision = new Button(decisionCheckboxComposite, SWT.CHECK);
		chboxPlanningDecision.setText(RscTools.getString(RscConst.MSG_REPORTVIEW_PLANNING_CHECKBOX_DECISION));
		chboxPlanningDecision.setSelection(true);
		chboxPlanningDecision.setBackground(chboxPlanning.getParent().getBackground());

	}

	/**
	 * Clear the helpers for required fields
	 */
	void clearHelpers() {
		txtArgSetupExecutable.clearHelper();
		txtARGParamStructureFile.clearHelper();
		txtARGParamParametersFile.clearHelper();
		txtARGParamOutput.clearHelper();
		txtCustomEndingFilePath.clearHelper();
	}

	/**
	 * @param <T>       the class to get type
	 * @param typeClass the type class
	 * @param cbx       the combo viewer to get selection for
	 * 
	 * @return the value selected in the combobox in parameter. If null or empty
	 *         selection, return null.
	 */
	@SuppressWarnings("unchecked")
	<T> T getCbxSelection(Class<T> typeClass, ComboViewer cbx) {
		T selection = null;

		if (cbx != null && cbx.getStructuredSelection() != null
				&& cbx.getStructuredSelection().getFirstElement() != null
				&& typeClass.isAssignableFrom(cbx.getStructuredSelection().getFirstElement().getClass())) {
			selection = (T) cbx.getStructuredSelection().getFirstElement();
		}

		return selection;
	}

	ARGParameters getArgParameters() {
		return argParameters;
	}

	/**
	 * Set the arg parameter
	 * 
	 * @param parameters
	 */
	void setARGParameters(ARGParameters parameters) {
		argParameters = parameters;
	}

	TextWidget getTxtArgSetupExecutable() {
		return txtArgSetupExecutable;
	}

	TextWidget getTxtARGParamParametersFile() {
		return txtARGParamParametersFile;
	}

	TextWidget getTxtARGParamStructureFile() {
		return txtARGParamStructureFile;
	}

	TextWidget getTxtARGParamOutput() {
		return txtARGParamOutput;
	}

	TextWidget getTxtCustomEndingFilePath() {
		return txtCustomEndingFilePath;
	}

	TextViewer getTxtConsole() {
		return txtConsole;
	}

	ComboViewer getCbxARGParamReportType() {
		return cbxARGParamReportType;
	}

	ComboViewer getCbxARGParamBackendType() {
		return cbxARGParamBackendType;
	}

	Button getChboxARGParamInlineWordDoc() {
		return chboxARGParamInlineWordDoc;
	}

	Button getChboxPlanning() {
		return chboxPlanning;
	}

	Button getChboxPlanningUncertainty() {
		return chboxPlanningUncertainty;
	}

	Button getChboxPlanningIntendedPurpose() {
		return chboxPlanningIntendedPurpose;
	}

	Button getChboxPlanningSystemRequirement() {
		return chboxPlanningSystemRequirement;
	}

	Button getChboxPlanningQoIPlanner() {
		return chboxPlanningQoIPlanner;
	}

	Button getChboxPlanningDecision() {
		return chboxPlanningDecision;
	}

	Button getChboxPirt() {
		return chboxPirt;
	}

	ComboViewer getCbxPcmmTag() {
		return cbxPcmmTag;
	}

	Button getChboxPcmm() {
		return chboxPcmm;
	}

	Button getChboxPcmmPlanning() {
		return chboxPcmmPlanning;
	}

	Button getChboxPcmmEvidence() {
		return chboxPcmmEvidence;
	}

	Button getChboxPcmmAssessment() {
		return chboxPcmmAssessment;
	}

	Button getChboxCustomEnding() {
		return chboxCustomEnding;
	}

}
