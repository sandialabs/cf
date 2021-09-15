/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.home;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IDecisionApplication;
import gov.sandia.cf.application.IGlobalApplication;
import gov.sandia.cf.application.IIntendedPurposeApp;
import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.application.IPCMMPlanningApplication;
import gov.sandia.cf.application.IPIRTApplication;
import gov.sandia.cf.application.IQoIPlanningApplication;
import gov.sandia.cf.application.ISystemRequirementApplication;
import gov.sandia.cf.application.IUncertaintyApplication;
import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.launcher.CredibilityEditor;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.CursorTools;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.ACredibilityView;
import gov.sandia.cf.parts.viewer.TableViewerHideSelection;
import gov.sandia.cf.parts.widgets.CardContainer;
import gov.sandia.cf.parts.widgets.CardWidget;
import gov.sandia.cf.parts.widgets.CustomProgressBar;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.ImageBadget;
import gov.sandia.cf.parts.widgets.Launcher;
import gov.sandia.cf.parts.widgets.LauncherFactory;
import gov.sandia.cf.parts.widgets.LauncherTile;
import gov.sandia.cf.parts.widgets.PCMMChartFactory;
import gov.sandia.cf.parts.widgets.PIRTComponentFactory;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Home view of the credibility framework plugin
 * 
 * @author Didier Verstraete
 *
 */
public class HomeView extends ACredibilityView<HomeViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(HomeView.class);

	/**
	 * The main composite
	 */
	private CardContainer mainComposite;

	/**
	 * The PIRT progress badge
	 */
	private CLabel pirtProgressBadget;

	/**
	 * The PIRT table sample composite
	 */
	private Composite pirtSampleComposite;

	/**
	 * PCMM Warning composite
	 */
	private Composite pcmmCompositeWarnings;
	private Composite pcmmCompositeErrors;

	/**
	 * The PCMM warning badge
	 */
	private CLabel pcmmWarningsBadge;
	private CLabel pcmmErrorsBadge;

	/**
	 * The PCMM progress bar
	 */
	private CustomProgressBar pcmmProgressBar;
	private Map<CFFeature, LauncherTile> planningTiles;
	private Map<CFFeature, LauncherTile> communicateTiles;
	private Composite compositeFooter;

	/**
	 * Constructor
	 * 
	 * @param viewManager the view manager
	 * @param style       the SWT style
	 */
	public HomeView(HomeViewManager viewManager, int style) {
		super(viewManager, viewManager, style);

		renderPage(viewManager);
	}

	/**
	 * Creates the view with all the components
	 * 
	 * @param parent the parent composite
	 */
	public void renderPage(Composite parent) {

		/**
		 * Main composite
		 */
		mainComposite = new CardContainer(this, SWT.NONE);

		// render the Planning card
		renderPlanningComposite();

		// render the PIRT card
		renderPIRTComposite();

		// render the PCMM card
		renderPCMMComposite();

		// render the communicate card
		renderCommunicateComposite();

		// render the footer buttons
		renderFooterButtons();

		/**
		 * Load data
		 */
		refresh();
	}

	/**
	 * Render the Planning card
	 */
	private void renderPlanningComposite() {
		/**
		 * Planning Composite
		 */
		CardWidget planningComposite = mainComposite.addCard(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PLANNING),
				null, null, true);

		// Planning - title
		CLabel lblTitlePlanning = new CLabel(planningComposite, SWT.CENTER);
		GridData gdLblTitleCommunicate = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		lblTitlePlanning.setLayoutData(gdLblTitleCommunicate);
		lblTitlePlanning.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PLANNING));
		lblTitlePlanning.setImage(IconTheme.getIconImage(getViewManager().getRscMgr(), IconTheme.ICON_NAME_PLANNING,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY), 25));
		lblTitlePlanning.setMargins(PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN,
				PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN, PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN,
				PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN);
		FontTools.setSubtitleFont(getViewManager().getRscMgr(), lblTitlePlanning);

		/////////////////////////////
		// Planning tiles
		/////////////////////////////
		Launcher launcher = LauncherFactory.createLauncher(getViewManager(), planningComposite);
		planningTiles = new EnumMap<>(CFFeature.class);

		// Intended Purpose
		LauncherTile tileIntendedPurpose = launcher.addTile(
				RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_INTENDEDPURPOSE),
				RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_INTENDEDPURPOSE), IconTheme.ICON_NAME_OPEN,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PINK), false);
		planningTiles.put(CFFeature.INTENDED_PURPOSE, tileIntendedPurpose);
		getViewManager().plugIntendedPurposeButton(tileIntendedPurpose);

		// System Requirements
		LauncherTile tileRequirement = launcher.addTile(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_REQUIREMENT),
				RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_REQUIREMENT), IconTheme.ICON_NAME_OPEN,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_RED), false);
		planningTiles.put(CFFeature.SYSTEM_REQUIREMENTS, tileRequirement);
		getViewManager().plugSystemRequirementsButton(tileRequirement);

		// QoI Planner
		LauncherTile qoiPlannerTile = launcher.addTile(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_QOIPLANNER),
				RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_QOIPLANNER), IconTheme.ICON_NAME_OPEN,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BROWN_LIGHT), false);
		planningTiles.put(CFFeature.QOI_PLANNER, qoiPlannerTile);
		getViewManager().plugQoIPlanningButton(qoiPlannerTile);

		// Uncertainty
		LauncherTile tileUncertainty = launcher.addTile(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_UNCERTAINTY),
				RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_UNCERTAINTY), IconTheme.ICON_NAME_OPEN,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_YELLOW), false);
		planningTiles.put(CFFeature.UNCERTAINTY, tileUncertainty);
		getViewManager().plugUncertaintyButton(tileUncertainty);

		// PCMM Planning
		LauncherTile pcmmPlanningTile = launcher.addTile(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PCMMPLANNING),
				RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PCMMPLANNING), IconTheme.ICON_NAME_OPEN,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PURPLE), false);
		planningTiles.put(CFFeature.PCMM_PLANNING, pcmmPlanningTile);
		getViewManager().plugPCMMPlanningButton(pcmmPlanningTile);

		// Analyst Decision
		LauncherTile decisionTile = launcher.addTile(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_DECISION),
				RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_DECISION), IconTheme.ICON_NAME_OPEN,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_ORANGE), false);
		planningTiles.put(CFFeature.DECISION, decisionTile);
		getViewManager().plugDecisionButton(decisionTile);

		/////////////////////////////
		// Composite for buttons
		/////////////////////////////
		Composite compositeButtons = new Composite(planningComposite, SWT.NONE);
		GridData gdcompositeButtons = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
		compositeButtons.setLayoutData(gdcompositeButtons);
		compositeButtons.setLayout(new GridLayout(2, false));

		/////////////////////////////
		// Planning - Composite for buttons LEFT
		/////////////////////////////
		Composite compositeButtonsLeft = new Composite(compositeButtons, SWT.NONE);
		compositeButtonsLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsLeft.setLayout(new RowLayout());

		/////////////////////////////
		// Planning - Composite for buttons RIGHT
		/////////////////////////////
		Composite compositeButtonsRight = new Composite(compositeButtons, SWT.NONE);
		compositeButtonsRight.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsRight.setLayout(new RowLayout());

		// Planning - Button Reference - Create
		Map<String, Object> refOptions = new HashMap<>();
		refOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_COM_REF));
		refOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		refOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_REFERENCE);
		refOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_PRIMARY);
		ButtonTheme btnRef = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsLeft, SWT.PUSH, refOptions);
		btnRef.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PCMM_REF));
		// Hide Planning reference button
		btnRef.setVisible(false);

		// Planning - Button Help - Create
		Map<String, Object> helpOptions = new HashMap<>();
		helpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		helpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		helpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		ButtonTheme btnHelp = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsRight, SWT.PUSH,
				helpOptions);

		// Planning - Button Help - Listener
		btnHelp.addListener(SWT.Selection, event -> HelpTools.openContextualHelp());
		HelpTools.addContextualHelp(compositeButtons, ContextualHelpId.PLANNING);

		// disable header controls if height is too small
		planningComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				boolean needLayout = false;

				// composite height
				if (planningComposite.getSize().y < 280) {
					if (lblTitlePlanning.isVisible()) {
						lblTitlePlanning.setVisible(false);
						lblTitlePlanning.setSize(0, 0);
						((GridData) lblTitlePlanning.getLayoutData()).heightHint = 0;
						needLayout = true;
					}
				} else {
					if (!lblTitlePlanning.isVisible()) {
						lblTitlePlanning.setVisible(true);
						lblTitlePlanning.setSize(SWT.DEFAULT, SWT.DEFAULT);
						((GridData) lblTitlePlanning.getLayoutData()).heightHint = lblTitlePlanning
								.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
						needLayout = true;
					}
				}

				if (needLayout) {
					planningComposite.layout();
				}
			}
		});
	}

	/**
	 * Render the PIRT card
	 */
	private void renderPIRTComposite() {

		/**
		 * PIRT Composite
		 */
		CardWidget pirtComposite = mainComposite.addCard(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PIRT), null, null,
				true);

		// PIRT- Title
		CLabel lblTitlePirt = new CLabel(pirtComposite, SWT.CENTER);
		GridData gdLblTitlePIRT = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
		lblTitlePirt.setLayoutData(gdLblTitlePIRT);
		lblTitlePirt.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PIRT));
		lblTitlePirt.setImage(IconTheme.getIconImage(getViewManager().getRscMgr(), IconTheme.ICON_NAME_PIRT,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY),
				PartsResourceConstants.HOME_VIEW_CARD_ICON_SIZE));
		lblTitlePirt.setMargins(PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN,
				PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN, PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN,
				PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN);
		FontTools.setSubtitleFont(getViewManager().getRscMgr(), lblTitlePirt);

		// PIRT- Description text
		StyledText txtPIRT = new StyledText(pirtComposite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		txtPIRT.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_TXT_PIRT));
		GridData txtPIRTGridData = new GridData(SWT.CENTER, SWT.NONE, true, false);
		txtPIRT.setLayoutData(txtPIRTGridData);
		FontTools.setImportantTextFont(getViewManager().getRscMgr(), txtPIRT);

		/////////////////////////////
		// PIRT - Composite for PIRT Table sample
		/////////////////////////////
		pirtSampleComposite = new Composite(pirtComposite, SWT.NONE);
		pirtSampleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gdImage = new GridLayout();
		gdImage.marginWidth = 20;
		gdImage.marginHeight = PartsResourceConstants.HOME_VIEW_CARD_PIRT_CHART_MARGIN;
		pirtSampleComposite.setLayout(gdImage);
		pirtSampleComposite.setBackground(new Color(getDisplay(), ColorTools.DEFAULT_RGB_COLOR));

		// display the table or not depending of the height of the card
		pirtSampleComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				if (pirtSampleComposite.getSize().y < 150) {
					if (pirtSampleComposite.isVisible()) {
						pirtSampleComposite.setVisible(false);
					}
				} else {
					if (!pirtSampleComposite.isVisible()) {
						pirtSampleComposite.setVisible(true);
					}
				}
			}
		});

		///////////////////////////////
		// PIRT- Composite for progress
		///////////////////////////////
		Composite pirtCompositeProgress = new Composite(pirtComposite, SWT.NONE);
		pirtCompositeProgress.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		pirtCompositeProgress.setLayout(new GridLayout(2, false));

		CLabel labelQoI = new CLabel(pirtCompositeProgress, SWT.NONE);
		labelQoI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		labelQoI.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_PIRT_PROGRESS_LABEL));
		FontTools.setButtonFont(getViewManager().getRscMgr(), labelQoI);

		// PIRT- rounded badge
		Color background = ColorTools.stringToColor(getDisplay(), PartsResourceConstants.INACTIVE_BADGET_COLOR);
		Color foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		pirtProgressBadget = new CLabel(pirtCompositeProgress, SWT.NONE);
		Image decoratedIcon = ImageBadget.createBadget(getViewManager().getRscMgr(), 0, background, foreground);
		pirtProgressBadget.setImage(decoratedIcon);

		/////////////////////////////
		// PIRT- Composite for buttons
		/////////////////////////////
		Composite compositeButtons = new Composite(pirtComposite, SWT.NONE);
		GridData gdcompositeButtons = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
		compositeButtons.setLayoutData(gdcompositeButtons);
		compositeButtons.setLayout(new GridLayout(2, false));

		/////////////////////////////
		// PIRT- Composite for buttons LEFT
		/////////////////////////////
		Composite compositeButtonsLeft = new Composite(compositeButtons, SWT.NONE);
		compositeButtonsLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsLeft.setLayout(new RowLayout());

		/////////////////////////////
		// PIRT- Composite for buttons RIGHT
		/////////////////////////////
		Composite compositeButtonsRight = new Composite(compositeButtons, SWT.NONE);
		compositeButtonsRight.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsRight.setLayout(new RowLayout());

		// PIRT- Button Reference - Create
		Map<String, Object> refOptions = new HashMap<>();
		refOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PIRT_REF));
		refOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		refOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_REFERENCE);
		refOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_PRIMARY);
		ButtonTheme btnRef = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsLeft, SWT.PUSH, refOptions);

		// PIRT- Button Reference - Listener
		btnRef.addListener(SWT.Selection, event -> {
			String pirtReferenceFilePath = null;
			try {
				pirtReferenceFilePath = FileTools.getPIRTReferenceFilePath();
			} catch (URISyntaxException | IOException e) {
				logger.error(e.getMessage(), e);
			}
			if (pirtReferenceFilePath != null && !pirtReferenceFilePath.isEmpty()) {
				Program.launch(pirtReferenceFilePath);
			} else {
				MessageDialog.openError(getShell(), RscTools.getString(RscConst.ERR_HOMEVIEW_PIRTREFFILE_TITLE),
						RscTools.getString(RscConst.ERR_HOMEVIEW_PIRTREFFILE_MSG));
			}
		});

		// PIRT- Button Open - Create
		Map<String, Object> options = new HashMap<>();
		options.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_OPEN));
		options.put(ButtonTheme.OPTION_OUTLINE, false);
		options.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_OPEN);
		options.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_PRIMARY);
		options.put(ButtonTheme.OPTION_ENABLED,
				getViewManager().getAppManager().getService(IPIRTApplication.class).isPIRTEnabled());
		ButtonTheme btnOpen = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsRight, SWT.PUSH, options);

		// PIRT- Button Open - Plug
		getViewManager().plugPIRTButton(btnOpen);

		// PIRT- Button Help - Create
		Map<String, Object> helpOptions = new HashMap<>();
		options.put(ButtonTheme.OPTION_TEXT, RscTools.empty());
		helpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		helpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		helpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		ButtonTheme btnHelp = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsRight, SWT.PUSH,
				helpOptions);

		// PIRT - Button Help - Listener
		btnHelp.addListener(SWT.Selection, event -> HelpTools.openContextualHelp());
		HelpTools.addContextualHelp(compositeButtons, ContextualHelpId.PIRT);

		// change the button layout depending of the width of the composite
		compositeButtons.addListener(SWT.Resize, event -> {

			boolean changed = false;

			// size of the button composite
			int currentSize = (compositeButtons.getSize().x
					- (((GridLayout) compositeButtons.getLayout()).marginWidth * 2));

			// check the sub button composite size
			int margin = 10;
			if (currentSize >= PartsResourceConstants.HOME_VIEW_CARD_COMPOSITE_MIN_WIDTH + margin) {
				changed = btnOpen.getText().isEmpty();
				btnRef.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PIRT_REF));
				btnOpen.setText(RscTools.getString(RscConst.MSG_BTN_OPEN));
			} else {
				changed = !btnOpen.getText().isEmpty();
				btnRef.setText(RscTools.empty());
				btnOpen.setText(RscTools.empty());
			}

			if (changed) {
				compositeButtons.layout();
			}
		});

	}

	/**
	 * Render the PCMM card
	 */
	private void renderPCMMComposite() {

		// Parameters
		PCMMSpecification pcmmConfiguration = getViewManager().getCache().getPCMMSpecification();
		boolean isPCMMAvailable = false;
		try {
			isPCMMAvailable = getViewManager().getAppManager().getService(IPCMMApplication.class)
					.isPCMMEnabled(getViewManager().getCache().getModel());
		} catch (CredibilityException e) {
			logger.error(e.getMessage(), e);
		}

		/**
		 * PCMM Composite
		 */
		CardWidget pcmmComposite = mainComposite.addCard(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PCMM), null, null,
				true);

		// PCMM - title
		CLabel lblTitlePcmm = new CLabel(pcmmComposite, SWT.CENTER);
		GridData gdLblTitlePcmm = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
		lblTitlePcmm.setLayoutData(gdLblTitlePcmm);
		lblTitlePcmm.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PCMM));
		lblTitlePcmm.setImage(IconTheme.getIconImage(getViewManager().getRscMgr(), IconTheme.ICON_NAME_PCMM,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY),
				PartsResourceConstants.HOME_VIEW_CARD_ICON_SIZE));
		lblTitlePcmm.setMargins(PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN,
				PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN, PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN,
				PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN);
		FontTools.setSubtitleFont(getViewManager().getRscMgr(), lblTitlePcmm);

		// PCMM - Description text
		StyledText txtPCMM = new StyledText(pcmmComposite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
		txtPCMM.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_TXT_PCMM));
		GridData txtPCMMGridData = new GridData(SWT.CENTER, SWT.NONE, true, false);
		txtPCMM.setLayoutData(txtPCMMGridData);
		FontTools.setImportantTextFont(getViewManager().getRscMgr(), txtPCMM);

		/////////////////////////////
		// PCMM - Composite for Description chart
		/////////////////////////////
		Composite pcmmChartComposite = new Composite(pcmmComposite, SWT.NONE);
		pcmmChartComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gdImage = new GridLayout();
		gdImage.marginWidth = PartsResourceConstants.HOME_VIEW_CARD_PCMM_CHART_MARGIN;
		gdImage.marginHeight = PartsResourceConstants.HOME_VIEW_CARD_PCMM_CHART_MARGIN;
		pcmmChartComposite.setLayout(gdImage);
		pcmmChartComposite.setBackground(new Color(getDisplay(), ColorTools.DEFAULT_RGB_COLOR));

		// PCMM - Description chart
		if (isPCMMAvailable) {
			ChartComposite pcmmWheel = PCMMChartFactory.createPCMMWheelChart(pcmmChartComposite, pcmmConfiguration);

			// make the chart clickable - redirect listener to SWT.Selection
			CursorTools.setCursor(getViewManager().getRscMgr(), pcmmWheel, SWT.CURSOR_HAND);
			pcmmWheel.addChartMouseListener(new ChartMouseListener() {
				@Override
				public void chartMouseClicked(final ChartMouseEvent event) {
					pcmmChartComposite.notifyListeners(SWT.Selection, new Event());
				}

				@Override
				public void chartMouseMoved(ChartMouseEvent arg0) {
					// not used
				}
			});
			// make the chart clickable - plug to the action
			getViewManager().plugPCMMButton(pcmmChartComposite);

			// display the table or not depending of the height of the card
			pcmmWheel.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(final ControlEvent e) {
					if (pcmmWheel.getSize().y <= PCMMChartFactory.PCMMCHART_SIZE_MIN) {
						if (pcmmWheel.isVisible()) {
							pcmmWheel.setVisible(false);
						}
					} else {
						if (!pcmmWheel.isVisible()) {
							pcmmWheel.setVisible(true);
						}
					}
				}
			});
		}

		///////////////////
		// PCMM - Warnings
		///////////////////
		pcmmCompositeErrors = new Composite(pcmmComposite, SWT.NONE);
		GridData gdErrors = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
		gdErrors.heightHint = PartsResourceConstants.HOME_VIEW_PCMM_WARNERROR_HEIGHT;
		pcmmCompositeErrors.setLayoutData(gdErrors);
		pcmmCompositeErrors.setLayout(new GridLayout(2, false));

		CLabel labelPcmmErrors = new CLabel(pcmmCompositeErrors, SWT.NONE);
		labelPcmmErrors.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		labelPcmmErrors.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_PCMM_ERROR_LABEL));
		labelPcmmErrors.setImage(FormFactory.getErrorIcon(getViewManager().getRscMgr()));
		FontTools.setButtonFont(getViewManager().getRscMgr(), labelPcmmErrors);

		Color backgroundError = ColorTools.stringToColor(getDisplay(), PartsResourceConstants.INACTIVE_BADGET_COLOR);
		Color foregroundError = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		pcmmErrorsBadge = new CLabel(pcmmCompositeErrors, SWT.NONE);
		Image decoratedErrorIcon = ImageBadget.createBadget(getViewManager().getRscMgr(), 0, backgroundError,
				foregroundError);
		pcmmErrorsBadge.setImage(decoratedErrorIcon);

		pcmmCompositeWarnings = new Composite(pcmmComposite, SWT.NONE);
		GridData gdWarning = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
		gdWarning.heightHint = PartsResourceConstants.HOME_VIEW_PCMM_WARNERROR_HEIGHT;
		pcmmCompositeWarnings.setLayoutData(gdWarning);
		pcmmCompositeWarnings.setLayout(new GridLayout(2, false));

		CLabel labelPcmmWarnings = new CLabel(pcmmCompositeWarnings, SWT.NONE);
		labelPcmmWarnings.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		labelPcmmWarnings.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_PCMM_WARNING_LABEL));
		labelPcmmWarnings.setImage(FormFactory.getWarningIcon(getViewManager().getRscMgr()));
		FontTools.setButtonFont(getViewManager().getRscMgr(), labelPcmmWarnings);

		Color background = ColorTools.stringToColor(getDisplay(), PartsResourceConstants.INACTIVE_BADGET_COLOR);
		Color foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		pcmmWarningsBadge = new CLabel(pcmmCompositeWarnings, SWT.NONE);
		Image decoratedIcon = ImageBadget.createBadget(getViewManager().getRscMgr(), 0, background, foreground);
		pcmmWarningsBadge.setImage(decoratedIcon);

		/////////////////////////////////
		// PCMM - Composite for progress
		/////////////////////////////////
		Composite pcmmCompositeProgress = new Composite(pcmmComposite, SWT.NONE);
		pcmmCompositeProgress.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		pcmmCompositeProgress.setLayout(new GridLayout(2, false));

		CLabel labelPcmmProgress = new CLabel(pcmmCompositeProgress, SWT.NONE);
		labelPcmmProgress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		labelPcmmProgress.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_PCMM_PROGRESS_LABEL));
		FontTools.setButtonFont(getViewManager().getRscMgr(), labelPcmmProgress);

		// PCMM - progress bar
		pcmmProgressBar = new CustomProgressBar(getViewManager(), pcmmCompositeProgress, SWT.NONE, false);

		/////////////////////////////
		// PCMM - Composite for buttons
		/////////////////////////////
		Composite compositeButtons = new Composite(pcmmComposite, SWT.NONE);
		GridData gdcompositeButtons = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
		compositeButtons.setLayoutData(gdcompositeButtons);
		compositeButtons.setLayout(new GridLayout(2, false));

		/////////////////////////////
		// PCMM - Composite for buttons LEFT
		/////////////////////////////
		Composite compositeButtonsLeft = new Composite(compositeButtons, SWT.NONE);
		compositeButtonsLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsLeft.setLayout(new RowLayout());

		/////////////////////////////
		// PCMM - Composite for buttons RIGHT
		/////////////////////////////
		Composite compositeButtonsRight = new Composite(compositeButtons, SWT.NONE);
		compositeButtonsRight.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsRight.setLayout(new RowLayout());

		// PCMM- Button Reference - Create
		Map<String, Object> refOptions = new HashMap<>();
		refOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PCMM_REF));
		refOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		refOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_REFERENCE);
		refOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_PRIMARY);
		ButtonTheme btnRefPCMM = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsLeft, SWT.PUSH,
				refOptions);

		// PCMM- Button Reference - Listener
		btnRefPCMM.addListener(SWT.Selection, event -> {
			String pcmmReferenceFilePath = null;
			try {
				pcmmReferenceFilePath = FileTools.getPCMMReferenceFilePath();
			} catch (URISyntaxException | IOException e) {
				logger.error(e.getMessage(), e);
			}
			if (pcmmReferenceFilePath != null && !pcmmReferenceFilePath.isEmpty()) {
				Program.launch(pcmmReferenceFilePath);
			} else {
				MessageDialog.openError(getShell(), RscTools.getString(RscConst.ERR_HOMEVIEW_PCMMREFFILE_TITLE),
						RscTools.getString(RscConst.ERR_HOMEVIEW_PCMMREFFILE_MSG));
			}
		});

		// PCMM - Button Open - Create
		Map<String, Object> options = new HashMap<>();
		options.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_OPEN));
		options.put(ButtonTheme.OPTION_OUTLINE, false);
		options.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_OPEN);
		options.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_PRIMARY);
		options.put(ButtonTheme.OPTION_ENABLED, isPCMMAvailable);
		ButtonTheme btnOpen = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsRight, SWT.PUSH, options);

		// PCMM - Button Open - Plug
		getViewManager().plugPCMMButton(btnOpen);

		// PCMM- Button Help - Create
		Map<String, Object> helpOptions = new HashMap<>();
		helpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		helpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		helpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		ButtonTheme btnHelp = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsRight, SWT.PUSH,
				helpOptions);

		// PCMM- Button Help - Listener
		btnHelp.addListener(SWT.Selection, event -> HelpTools.openContextualHelp());
		HelpTools.addContextualHelp(compositeButtons, ContextualHelpId.PCMM);

		// change the button layout depending of the width of the composite
		compositeButtons.addListener(SWT.Resize, event -> {

			boolean changed = false;

			// size of the button composite
			int currentSize = (compositeButtons.getSize().x
					- (((GridLayout) compositeButtons.getLayout()).marginWidth * 2));

			// check the sub button composite size
			int margin = 10;
			if (currentSize >= PartsResourceConstants.HOME_VIEW_CARD_COMPOSITE_MIN_WIDTH + margin) {
				changed = btnOpen.getText().isEmpty();
				btnRefPCMM.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PCMM_REF));
				btnOpen.setText(RscTools.getString(RscConst.MSG_BTN_OPEN));
			} else {
				changed = !btnOpen.getText().isEmpty();
				btnRefPCMM.setText(RscTools.empty());
				btnOpen.setText(RscTools.empty());
			}

			if (changed) {
				compositeButtons.layout();
			}
		});
	}

	/**
	 * Render the Communicate card
	 */
	private void renderCommunicateComposite() {
		/**
		 * Communicate Composite
		 */
		CardWidget communicateComposite = mainComposite
				.addCard(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_COMMUNICATE), null, null, true);

		// Communicate - title
		CLabel lblTitleCommunicate = new CLabel(communicateComposite, SWT.CENTER);
		GridData gdLblTitleCommunicate = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		lblTitleCommunicate.setLayoutData(gdLblTitleCommunicate);
		lblTitleCommunicate.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_COMMUNICATE));
		lblTitleCommunicate.setImage(IconTheme.getIconImage(getViewManager().getRscMgr(),
				IconTheme.ICON_NAME_COMMUNICATE, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY), 25));
		lblTitleCommunicate.setMargins(PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN,
				PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN, PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN,
				PartsResourceConstants.HOME_VIEW_CARD_TITLE_MARGIN);
		FontTools.setSubtitleFont(getViewManager().getRscMgr(), lblTitleCommunicate);

		/////////////////////////////
		// Communicate tiles
		/////////////////////////////
		Launcher launcher = LauncherFactory.createLauncher(getViewManager(), communicateComposite);
		communicateTiles = new EnumMap<>(CFFeature.class);

		// Generate Report
		LauncherTile genReportTile = launcher.addTile(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_GEN_REPORT),
				RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_GEN_REPORT), IconTheme.ICON_NAME_GEN_CF_REPORT,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GRAY_DARK), false);
		communicateTiles.put(CFFeature.GEN_REPORT, genReportTile);
		getViewManager().plugReportButton(genReportTile);

		// fake tiles - see #431
		LauncherTile fakeTile1 = launcher.addTile("Fake 1", RscTools.empty(), IconTheme.ICON_NAME_EMPTY, //$NON-NLS-1$
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), false);
		fakeTile1.setGrayedInactive(false);
		LauncherTile fakeTile2 = launcher.addTile("Fake 2", RscTools.empty(), IconTheme.ICON_NAME_EMPTY, //$NON-NLS-1$
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), false);
		fakeTile2.setGrayedInactive(false);
		LauncherTile fakeTile3 = launcher.addTile("Fake 3", RscTools.empty(), IconTheme.ICON_NAME_EMPTY, //$NON-NLS-1$
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), false);
		fakeTile3.setGrayedInactive(false);
		LauncherTile fakeTile4 = launcher.addTile("Fake 4", RscTools.empty(), IconTheme.ICON_NAME_EMPTY, //$NON-NLS-1$
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), false);
		fakeTile4.setGrayedInactive(false);
		LauncherTile fakeTile5 = launcher.addTile("Fake 5", RscTools.empty(), IconTheme.ICON_NAME_EMPTY, //$NON-NLS-1$
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), false);
		fakeTile5.setGrayedInactive(false);

		/////////////////////////////
		// Composite for buttons
		/////////////////////////////
		Composite compositeButtons = new Composite(communicateComposite, SWT.NONE);
		GridData gdcompositeButtons = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
		compositeButtons.setLayoutData(gdcompositeButtons);
		compositeButtons.setLayout(new GridLayout(2, false));

		/////////////////////////////
		// Communicate - Composite for buttons LEFT
		/////////////////////////////
		Composite compositeButtonsLeft = new Composite(compositeButtons, SWT.NONE);
		compositeButtonsLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsLeft.setLayout(new RowLayout());

		/////////////////////////////
		// Communicate - Composite for buttons RIGHT
		/////////////////////////////
		Composite compositeButtonsRight = new Composite(compositeButtons, SWT.NONE);
		compositeButtonsRight.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsRight.setLayout(new RowLayout());

		// Communicate- Button Reference - Create
		Map<String, Object> refOptions = new HashMap<>();
		refOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_COM_REF));
		refOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		refOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_REFERENCE);
		refOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_PRIMARY);
		ButtonTheme btnRef = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsLeft, SWT.PUSH, refOptions);
		btnRef.setText(RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_PCMM_REF));
		// Hide communicate reference button
		btnRef.setVisible(false);

		// Communicate - Button Help - Create
		Map<String, Object> helpOptions = new HashMap<>();
		helpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		helpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		helpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		ButtonTheme btnHelp = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsRight, SWT.PUSH,
				helpOptions);

		// Communicate - Button Help - Listener
		btnHelp.addListener(SWT.Selection, event -> HelpTools.openContextualHelp());
		HelpTools.addContextualHelp(compositeButtons, ContextualHelpId.COMMUNICATE);

		// disable header controls if height is too small
		communicateComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				boolean needLayout = false;

				// composite height
				if (communicateComposite.getSize().y < 350) {
					if (lblTitleCommunicate.isVisible()) {
						lblTitleCommunicate.setVisible(false);
						lblTitleCommunicate.setSize(0, 0);
						((GridData) lblTitleCommunicate.getLayoutData()).heightHint = 0;
						needLayout = true;
					}
				} else {
					if (!lblTitleCommunicate.isVisible()) {
						lblTitleCommunicate.setVisible(true);
						lblTitleCommunicate.setSize(SWT.DEFAULT, SWT.DEFAULT);
						((GridData) lblTitleCommunicate.getLayoutData()).heightHint = lblTitleCommunicate
								.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
						needLayout = true;
					}
				}

				if (needLayout) {
					communicateComposite.layout();
				}
			}
		});
	}

	/**
	 * Render footer buttons
	 */
	private void renderFooterButtons() {
		/**
		 * Footer
		 */
		/////////////////////////////
		// Composite for buttons
		/////////////////////////////
		compositeFooter = new Composite(this, SWT.NONE);
		GridData gdcompositeButtons = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
		compositeFooter.setLayoutData(gdcompositeButtons);
		GridLayout gdCompositeButtons = new GridLayout(2, false);
		gdCompositeButtons.marginHeight = 0;
		compositeFooter.setLayout(gdCompositeButtons);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {

		// reload Planning
		reloadPlanningCard();

		// reload PIRT
		reloadPIRTCard();

		// reload PCMM
		reloadPCMMCard();

		// reload Communicate
		reloadCommunicateCard();

		// reload Footer
		reloadFooter();
	}

	/**
	 * Reload the Planning Card and features activated
	 */
	private void reloadPlanningCard() {

		/*
		 * Search for feature activation
		 */
		// Intended Purpose
		boolean isIntendedPurposeAvailable = getViewManager().getAppManager().getService(IIntendedPurposeApp.class)
				.isIntendedPurposeEnabled(getViewManager().getCache().getModel());
		planningTiles.get(CFFeature.INTENDED_PURPOSE).setEnabled(isIntendedPurposeAvailable);

		// System Requirements
		boolean isRequirementAvailable = getViewManager().getAppManager()
				.getService(ISystemRequirementApplication.class)
				.isRequirementEnabled(getViewManager().getCache().getModel());
		planningTiles.get(CFFeature.SYSTEM_REQUIREMENTS).setEnabled(isRequirementAvailable);

		// QoI Planner
		boolean isQoIPlannerAvailable = getViewManager().getAppManager().getService(IQoIPlanningApplication.class)
				.isQoIPlanningEnabled(getViewManager().getCache().getModel());
		planningTiles.get(CFFeature.QOI_PLANNER).setEnabled(isQoIPlannerAvailable);

		// Uncertainty
		boolean isUncertaintyAvailable = getViewManager().getAppManager().getService(IUncertaintyApplication.class)
				.isUncertaintyEnabled(getViewManager().getCache().getModel());
		planningTiles.get(CFFeature.UNCERTAINTY).setEnabled(isUncertaintyAvailable);

		// PCMM Planning
		boolean isPCMMPlanningAvailable = getViewManager().getAppManager().getService(IPCMMPlanningApplication.class)
				.isPCMMPlanningEnabled();
		planningTiles.get(CFFeature.PCMM_PLANNING).setEnabled(isPCMMPlanningAvailable);

		// Decision
		boolean isDecisionAvailable = getViewManager().getAppManager().getService(IDecisionApplication.class)
				.isDecisionEnabled(getViewManager().getCache().getModel());
		planningTiles.get(CFFeature.DECISION).setEnabled(isDecisionAvailable);

	}

	/**
	 * Reload the PIRT card and badges
	 */
	private void reloadPIRTCard() {

		Model model = getViewManager().getCache().getModel();

		// compute PIRT progress
		int nbQoI = 0;
		List<QuantityOfInterest> qoIList = getViewManager().getAppManager().getService(IPIRTApplication.class)
				.getRootQoI(model);
		if (qoIList != null) {
			nbQoI = qoIList.size();
		}
		Color background = ColorTools.stringToColor(getDisplay(),
				(nbQoI > 0) ? PartsResourceConstants.ACTIVE_BADGET_COLOR
						: PartsResourceConstants.INACTIVE_BADGET_COLOR);
		Color foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		Image decoratedIcon = ImageBadget.createBadget(getViewManager().getRscMgr(), nbQoI, background, foreground);
		pirtProgressBadget.setImage(decoratedIcon);

		// reload PIRT sample table
		ViewTools.disposeChildren(pirtSampleComposite);

		TableViewerHideSelection pirtTable = PIRTComponentFactory.createPIRTTable(pirtSampleComposite,
				getViewManager().getCache().getPIRTSpecification(), getViewManager().getAppManager());

		// make the PIRT table clickable - redirect listener to SWT.Selection
		if (pirtTable != null) {
			pirtTable.getTable().addListener(SWT.MouseUp,
					event -> pirtTable.getTable().notifyListeners(SWT.Selection, event));

			CursorTools.setCursor(getViewManager().getRscMgr(), pirtTable.getTable(), SWT.CURSOR_HAND);

			// plug table to PIRT action
			getViewManager().plugPIRTButton(pirtTable.getTable());
		}

		pirtSampleComposite.layout();
	}

	/**
	 * Reload the PCMM card and badges
	 */
	private void reloadPCMMCard() {

		// Compute PCMM errors
		try {
			int nbPcmmErrors = getViewManager().getAppManager().getService(IPCMMApplication.class)
					.findEvidenceErrorNotification();
			Color bgErrorsBadge = (nbPcmmErrors > 0) ? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_RED)
					: ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY_LIGHT);
			Color fgErrorsBadge = (nbPcmmErrors > 0) ? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)
					: ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK);
			Image errorsBadget = ImageBadget.createBadget(getViewManager().getRscMgr(), nbPcmmErrors, bgErrorsBadge,
					fgErrorsBadge);
			pcmmErrorsBadge.setImage(errorsBadget);

			// Toggle visibility
			pcmmCompositeErrors.setVisible(0 < nbPcmmErrors);
			if (0 >= nbPcmmErrors) {
				((GridData) pcmmCompositeErrors.getLayoutData()).heightHint = 0;
			} else {
				((GridData) pcmmCompositeErrors
						.getLayoutData()).heightHint = PartsResourceConstants.HOME_VIEW_PCMM_WARNERROR_HEIGHT;
			}
			pcmmCompositeErrors.getParent().layout();

		} catch (CredibilityException e) {
			logger.error(RscTools.getString(RscConst.ERR_HOMEVIEW_PCMM_ERROR_BADGE_ERROR), e);
			MessageDialog.openError(getShell(), RscTools.getString(RscConst.MSG_HOMEVIEW_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_HOMEVIEW_PCMM_ERROR_BADGE_ERROR));
		}

		// Compute PCMM warning
		int nbPcmmWarnings;
		try {
			nbPcmmWarnings = getViewManager().getAppManager().getService(IPCMMApplication.class)
					.findEvidenceWarningNotification();
			Color bgWarningBadge = (nbPcmmWarnings > 0) ? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_YELLOW)
					: ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY_LIGHT);
			Color fgWarningBadge = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
			Image warningBadget = ImageBadget.createBadget(getViewManager().getRscMgr(), nbPcmmWarnings, bgWarningBadge,
					fgWarningBadge);
			pcmmWarningsBadge.setImage(warningBadget);

			// Toggle visibility
			pcmmCompositeWarnings.setVisible(0 < nbPcmmWarnings);
			if (0 >= nbPcmmWarnings) {
				((GridData) pcmmCompositeWarnings.getLayoutData()).heightHint = 0;
			} else {
				((GridData) pcmmCompositeWarnings
						.getLayoutData()).heightHint = PartsResourceConstants.HOME_VIEW_PCMM_WARNERROR_HEIGHT;
			}
			pcmmCompositeWarnings.getParent().layout();
		} catch (CredibilityException e) {
			logger.error(RscTools.getString(RscConst.ERR_HOMEVIEW_PCMM_WARNING_BADGE_ERROR), e);
			MessageDialog.openError(getShell(), RscTools.getString(RscConst.MSG_HOMEVIEW_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_HOMEVIEW_PCMM_WARNING_BADGE_ERROR));
		}

		// compute PCMM progress
		Model model = getViewManager().getCache().getModel();
		PCMMSpecification configuration = getViewManager().getCache().getPCMMSpecification();
		int currentPCMMProgress = 0;
		int maxPCMMProgress = getViewManager().getAppManager().getService(IPCMMApplication.class)
				.computeMaxProgress(configuration);
		try {
			currentPCMMProgress = getViewManager().getAppManager().getService(IPCMMApplication.class)
					.computeCurrentProgress(model, configuration);
		} catch (CredibilityException e) {
			logger.error(RscTools.getString(RscConst.ERR_HOMEVIEW_PCMM_PROGRESS_ERROR), e);
			MessageDialog.openError(getShell(), RscTools.getString(RscConst.MSG_HOMEVIEW_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_HOMEVIEW_PCMM_PROGRESS_ERROR));
		}
		pcmmProgressBar.setMaximum(maxPCMMProgress);
		pcmmProgressBar.setCurrent(currentPCMMProgress);
	}

	/**
	 * Reload the communicate card
	 */
	private void reloadCommunicateCard() {

		/*
		 * Search for feature activation
		 */
		// ARG Report
		boolean isReportingAvailable = true;
		communicateTiles.get(CFFeature.GEN_REPORT).setEnabled(isReportingAvailable);
	}

	/**
	 * Reload the footer informations
	 */
	private void reloadFooter() {

		ViewTools.disposeChildren(compositeFooter);

		/////////////////////////////
		// Communicate - Composite for buttons LEFT
		/////////////////////////////
		Composite compositeButtonsLeft = new Composite(compositeFooter, SWT.NONE);
		compositeButtonsLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		RowLayout gdCompositeButtonsLeft = new RowLayout();
		compositeButtonsLeft.setLayout(gdCompositeButtonsLeft);

		/////////////////////////////
		// Communicate - Composite for buttons RIGHT
		/////////////////////////////
		Composite compositeButtonsRight = new Composite(compositeFooter, SWT.NONE);
		compositeButtonsRight.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));
		RowLayout gdCompositeButtonsRight = new RowLayout();
		compositeButtonsRight.setLayout(gdCompositeButtonsRight);

		// label version
		if (Boolean.TRUE.equals(PrefTools.getGlobalDisplayVersionNumber())) {
			Label lblVersionCurrent = new Label(compositeButtonsLeft, SWT.LEFT);
			lblVersionCurrent
					.setText(RscTools.getString(RscConst.MSG_VERSION_CURRENT_LABEL, CredibilityEditor.getVersion()));
		}

		// label created with version
		if (Boolean.TRUE.equals(PrefTools.getGlobalDisplayVersionOriginNumber())) {
			Label lblVersionOrigin = new Label(compositeButtonsRight, SWT.RIGHT);
			lblVersionOrigin.setText(RscTools.getString(RscConst.MSG_VERSION_ORIGIN_LABEL, getVersionOrigin()));
		}

		compositeFooter.requestLayout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_CREDIBILITYVIEW_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_CREDIBILITYVIEW_ITEMTITLE);
	}

	/**
	 * @return the origin (creation) version of the cf file.
	 */
	private String getVersionOrigin() {

		// Load or create model
		Model model = null;

		// Retrieve the model's version
		try {
			model = getViewManager().getAppManager().getService(IGlobalApplication.class).loadModel();
		} catch (CredibilityException e) {
			logger.error("An error occured while retrieving the version number: {}", e.getMessage(), e); //$NON-NLS-1$
		}

		// get version origin
		String versionOrigin = RscTools.getString(RscConst.MSG_VERSION_ORIGIN_UNDEFINED);
		if (model != null) {
			versionOrigin = RscTools.getString(RscConst.MSG_VERSION_ORIGIN_BEFORE_0_2_0);
			if (null != model.getVersionOrigin()) {
				versionOrigin = model.getVersionOrigin();
			}
		}

		return versionOrigin;
	}
}
