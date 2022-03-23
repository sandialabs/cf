/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.awt.BasicStroke;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.plot.RingPlot;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.widgets.ITagAction;
import gov.sandia.cf.parts.widgets.PCMMChartFactory;
import gov.sandia.cf.parts.widgets.PCMMProgressBarPart;
import gov.sandia.cf.parts.widgets.PCMMTagPart;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * An implementation of the phenomena view with a tableViewer
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMHomeView extends ACredibilityPCMMView implements SelectionListener, ITagAction {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMHomeView.class);

	/** Buttons events PIRT */
	public static final String BTN_EVENT_PCMM_CLOSE = "PCMM_CLOSE"; //$NON-NLS-1$

	/**
	 * The pie section attribute
	 */
	public static final String PIE_SECTION_ATTRIBUTE = "PIE_SECTION_ATTRIBUTE"; //$NON-NLS-1$

	/**
	 * The command names for buttons
	 */
	/** PCMM PLANNING event COMMAND */
	public static final String PLANNING_COMMAND = "PLANNING_COMMAND"; //$NON-NLS-1$
	/** PCMM EVIDENCE event COMMAND */
	public static final String EVIDENCE_COMMAND = "EVIDENCE_COMMAND"; //$NON-NLS-1$
	/** PCMM ASSESS event COMMAND */
	public static final String ASSESS_COMMAND = "ASSESS_COMMAND"; //$NON-NLS-1$
	/**
	 * Configuration
	 */
	private PCMMSpecification pcmmConfiguration;

	/**
	 * The main composite
	 */
	private Composite mainComposite;

	/**
	 * the tag part
	 */
	private PCMMTagPart pcmmTagPart;

	/**
	 * the pcmm elements map
	 */
	private Map<String, PCMMElement> elements;

	/**
	 * the tag list
	 */
	private List<Tag> tagList;

	private Composite compositeButtonsFooter;

	/**
	 * The pcmm phases enabled
	 */
	private List<PCMMPhase> phaseEnabled;

	/**
	 * @param viewManager  the parent view
	 * @param style        the view style
	 * @param phaseEnabled the list of PCMM phases enabled for this view
	 */
	public PCMMHomeView(PCMMViewManager viewManager, int style, final List<PCMMPhase> phaseEnabled) {
		super(viewManager, viewManager, style);

		// Initialize
		this.elements = new LinkedHashMap<>();
		this.pcmmConfiguration = getViewManager().getPCMMConfiguration();

		if (phaseEnabled == null) {
			this.phaseEnabled = new ArrayList<>();
		} else {
			this.phaseEnabled = phaseEnabled;
		}

		List<PCMMPhase> enabledAndActivatedPhases = null;
		if (pcmmConfiguration != null) {
			enabledAndActivatedPhases = this.phaseEnabled.stream().filter(this.pcmmConfiguration.getPhases()::contains)
					.collect(Collectors.toList());
		}

		// set title if there is only one phase activated
		if (enabledAndActivatedPhases != null && enabledAndActivatedPhases.size() == 1) {
			String title = MessageFormat.format("{0} - {1}", RscTools.getString(RscConst.MSG_PCMMVIEW_TITLE), //$NON-NLS-1$
					enabledAndActivatedPhases.get(0));
			setTitle(title);
		}

		// create the view
		renderPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		if (getViewManager().getSelectedTag() != null) {
			this.lblTitle.setImage(IconTheme.getIconImage(getViewManager().getRscMgr(), IconTheme.ICON_NAME_TAG,
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BROWN), 30));
		}
		return RscTools.getString(RscConst.MSG_PCMMVIEW_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_PCMMVIEW_ITEMTITLE);
	}

	/**
	 * Render page
	 */
	private void renderPage() {
		// Initialize
		mainComposite = new Composite(this, SWT.BORDER);
		mainComposite.setBackground(ColorTools.toColor(getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));

		// Render the main composite
		renderMain();

		// Render Footer
		renderFooter();

		// Refresh
		refresh();
	}

	/**
	 * Render footer
	 */
	private void renderFooter() {
		// Footer buttons - Composite
		compositeButtonsFooter = new Composite(this, SWT.FILL);
		GridLayout gridLayoutButtonsHeader = new GridLayout(2, false);
		compositeButtonsFooter.setLayout(gridLayoutButtonsHeader);
		compositeButtonsFooter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		reloadFooterButtons();
	}

	/**
	 * Reload footer
	 */
	private void reloadFooterButtons() {
		// Footer buttons - Composite
		ViewTools.disposeChildren(compositeButtonsFooter);

		// Composite for Footer left buttons
		Composite compositeButtonsFooterLeft = new Composite(compositeButtonsFooter, SWT.NONE);
		compositeButtonsFooterLeft.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		compositeButtonsFooterLeft.setLayout(new RowLayout());

		// Composite for header right buttons
		Composite compositeButtonsFooterRight = new Composite(compositeButtonsFooter, SWT.RIGHT_TO_LEFT);
		compositeButtonsFooterRight.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
		compositeButtonsFooterRight.setLayout(new RowLayout());

		// Button Back - Create
		Map<String, Object> btnBackOptions = new HashMap<>();
		btnBackOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BACK));
		btnBackOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnBackOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_BACK);
		btnBackOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		ButtonTheme btnBack = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER,
				btnBackOptions);

		// Button Back - Plug
		getViewManager().plugBackHomeButton(btnBack);

		// Button - Guidance Level
		Map<String, Object> btnHelpLevelOptions = new HashMap<>();
		btnHelpLevelOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_LVLGUIDANCE));
		btnHelpLevelOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_HELP);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) event -> getViewManager().openPCMMHelpLevelView());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.PUSH | SWT.CENTER,
				btnHelpLevelOptions);

		// Footer buttons - Help - Create
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> HelpTools.openContextualHelp());
		new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER, btnHelpOptions);
		HelpTools.addContextualHelp(compositeButtonsFooter, ContextualHelpId.PCMM_HOME);

		if (pcmmConfiguration != null) {

			// button PCMM Stamp
			if (pcmmConfiguration.isPcmmAssessEnabled() && phaseEnabled.contains(PCMMPhase.STAMP)
					&& pcmmConfiguration.isPcmmStampEnabled()) {
				Map<String, Object> btnPCMMStampOptions = new HashMap<>();
				btnPCMMStampOptions.put(ButtonTheme.OPTION_TEXT,
						RscTools.getString(RscConst.MSG_PCMMHOME_BTN_PCMMSTAMP));
				btnPCMMStampOptions.put(ButtonTheme.OPTION_OUTLINE, true);
				btnPCMMStampOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_STAMP);
				btnPCMMStampOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
				ButtonTheme btnPCMMStamp = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterRight,
						SWT.RIGHT | SWT.LEFT_TO_RIGHT, btnPCMMStampOptions); // force normal orientation left_to_right

				// open pcmm stamp view
				btnPCMMStamp.addListener(SWT.Selection, event -> getViewManager().openPCMMStampView());
			}

			// button PCMM Aggregate
			if (pcmmConfiguration.isPcmmAssessEnabled() && phaseEnabled.contains(PCMMPhase.AGGREGATE)
					&& pcmmConfiguration.isPcmmAggregateEnabled()) {

				Map<String, Object> btnAggregateOptions = new HashMap<>();
				btnAggregateOptions.put(ButtonTheme.OPTION_TEXT,
						RscTools.getString(RscConst.MSG_PCMMHOME_BTN_AGGREGATE));
				btnAggregateOptions.put(ButtonTheme.OPTION_OUTLINE, true);
				btnAggregateOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_AGGREGATE);
				btnAggregateOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
				ButtonTheme btnPCMMAggregate = new ButtonTheme(getViewManager().getRscMgr(),
						compositeButtonsFooterRight, SWT.RIGHT | SWT.LEFT_TO_RIGHT, btnAggregateOptions); // force
																											// normal
																											// orientation
																											// left_to_right

				// open pcmm aggregate view
				btnPCMMAggregate.addListener(SWT.Selection, event -> getViewManager().openPCMMAggregateView());
			}
		}

		// layout view
		compositeButtonsFooter.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {
		// Show role selection
		showRoleSelection();

		// Get Model
		Model model = getViewManager().getCache().getModel();
		if (model != null) {
			try {
				// Load the PCMM elements
				List<PCMMElement> elementsList = getViewManager().getAppManager().getService(IPCMMApplication.class)
						.getElementList(model);

				// If element list is not empty
				if (null != elementsList && !elementsList.isEmpty()) {

					// Create not sorted map by name
					elementsList.forEach(element -> this.elements.put(element.getName(), element));

					// load the tags
					tagList = getViewManager().getAppManager().getService(IPCMMApplication.class).getTags();
				}

			} catch (CredibilityException e) {
				MessageDialog.openWarning(getShell(), RscTools.getString(RscConst.MSG_PCMMHOME_DIALOG_TITLE),
						RscTools.getString(RscConst.ERR_PCMMHOME_DIALOG_LOADING_MSG));
				logger.warn("An error occurred while loading the home data:\n{}", e.getMessage(), e); //$NON-NLS-1$
			}
			// repaint the view composites
			renderMain();

			// reload footer
			reloadFooterButtons();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void roleChanged() {
		// not used for home view
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {

		// handle context menu selection
		String command = (String) ((MenuItem) e.getSource()).getData();

		PieSectionEntity pieSectionEntity = (PieSectionEntity) ((MenuItem) e.getSource()).getParent()
				.getData(PIE_SECTION_ATTRIBUTE);

		PCMMElement pcmmElementSelected = this.elements.get(pieSectionEntity.getSectionKey().toString());

		// WARNING: Hardcoded - Check if PIRT have QoI and it's PMMF element
		boolean isAvailabledAccess = true;
		if (null != pcmmElementSelected.getAbbreviation() && pcmmElementSelected.getAbbreviation().equals("PMMF")) { //$NON-NLS-1$
			Model model = getViewManager().getCache().getModel();
			if (null != model) {
				List<QuantityOfInterest> qois = getViewManager().getAppManager().getService(IPIRTApplication.class)
						.getRootQoI(model);
				isAvailabledAccess = (null != qois && !qois.isEmpty());
			}
		}

		// Check access
		if (isAvailabledAccess) {

			// open planning view
			if (command.equals(PLANNING_COMMAND)) {
				getViewManager().openPCMMPlanningView(pcmmElementSelected);
			}
			// open evidence view
			else if (command.equals(EVIDENCE_COMMAND)) {
				getViewManager().openPCMMEvidenceView(pcmmElementSelected);
			}
			// open pcmm assess view
			else if (command.equals(ASSESS_COMMAND)) {
				getViewManager().openPCMMAssessView(pcmmElementSelected);
			}
		} else {
			// Open error dialog
			MessageDialog.openError(getShell(), RscTools.getString(RscConst.ERR_PCMMHOME_PREREQUISITE_TITLE),
					RscTools.getString(RscConst.ERR_PCMMHOME_PREREQUISITE_TXT));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// redirect to widget selected method
		widgetSelected(e);
	}

	/**
	 * Create the main composite with the PCMM wheel
	 */
	private void renderMain() {

		// dispose composite children
		ViewTools.disposeChildren(mainComposite);

		// composite layout
		GridData gdMainComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		mainComposite.setLayoutData(gdMainComposite);
		GridLayout layoutMainComp = new GridLayout(3, true);
		mainComposite.setLayout(layoutMainComp);

		// draw the content
		if (elements == null || elements.isEmpty()) {
			Label lblEmptyPCMM = new Label(mainComposite, SWT.CENTER);
			GridData gdLblEmpty = new GridData(SWT.CENTER, SWT.CENTER, true, true, 2, 1);
			lblEmptyPCMM.setLayoutData(gdLblEmpty);
			lblEmptyPCMM.setText(RscTools.getString(RscConst.MSG_NO_DATA));
		} else {

			/* Main Composite - Left Composite */
			Composite leftComposite = new Composite(mainComposite, SWT.NONE);
			GridData gdLeftComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			leftComposite.setLayoutData(gdLeftComposite);
			leftComposite.setLayout(new GridLayout());
			leftComposite.setBackground(mainComposite.getBackground());

			/* Left Composite - Progress Bar */
			Composite progressBarComposite = new Composite(leftComposite, SWT.NONE);
			GridData gdProgressBar = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
			progressBarComposite.setLayoutData(gdProgressBar);
			progressBarComposite.setLayout(new GridLayout());
			Label progressionLabel = new Label(progressBarComposite, SWT.NONE);
			progressionLabel.setForeground(ColorTools.toColor(getViewManager().getRscMgr(),
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
			progressionLabel.setText(RscTools.getString(RscConst.MSG_PCMMHOME_LBL_PROGRESS));
			FontTools.setSubtitleFont(getViewManager().getRscMgr(), progressionLabel);
			new PCMMProgressBarPart(getViewManager(), progressBarComposite, new ArrayList<>(elements.values()),
					SWT.NONE);

			/* Left Composite - Tag Bar */
			Composite tagBarComposite = new Composite(leftComposite, SWT.NONE);
			GridData gdTagBar = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			tagBarComposite.setLayoutData(gdTagBar);
			tagBarComposite.setLayout(new GridLayout());
			Label tagTitle = new Label(tagBarComposite, SWT.NONE);
			tagTitle.setForeground(ColorTools.toColor(getViewManager().getRscMgr(),
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
			tagTitle.setText(RscTools.getString(RscConst.MSG_TAG_PART_TITLE));
			FontTools.setSubtitleFont(getViewManager().getRscMgr(), tagTitle);
			pcmmTagPart = new PCMMTagPart(getViewManager(), tagBarComposite, this, tagList,
					getViewManager().getSelectedTag(), SWT.NONE);

			/* Main Composite - Pie Chart */
			// Chart main composite to contain chartComposite
			Composite compositeChartMain = new Composite(mainComposite, SWT.NONE);
			GridLayout gCompositeChartMain = new GridLayout();
			gCompositeChartMain.marginHeight = 0;
			gCompositeChartMain.marginWidth = 0;
			compositeChartMain.setLayout(gCompositeChartMain);
			GridData gdMainChart = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
			compositeChartMain.setLayoutData(gdMainChart);
			compositeChartMain.setBackground(mainComposite.getBackground());

			// create chart composite
			createChartComposite(compositeChartMain);

			// disable selection when clicking on the container
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(org.eclipse.swt.events.MouseEvent e) {
					super.mouseDown(e);
				}
			});
		}

		mainComposite.layout();
	}

	/**
	 * @param parent
	 * @return the chart composite
	 */
	private Composite createChartComposite(Composite parent) {

		// Create Pie Chart
		ChartComposite chartComposite = PCMMChartFactory.createPCMMWheelChart(parent, pcmmConfiguration);
		chartComposite.setBackground(mainComposite.getBackground());

		// change chart background depending of the PCMM view
		this.addPaintListener(e -> {
			if (!parent.isDisposed()) {
				Color bgColor = mainComposite.getBackground();
				java.awt.Color bgColorAwt = new java.awt.Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue());
				chartComposite.getChart().setBackgroundPaint(bgColorAwt);
			}
		});

		// add mouse click listener
		chartComposite.addChartMouseListener(new ChartMouseListener() {

			private PCMMElement onHoverElement = null;

			private final List<PCMMPhase> chartMenuPhases = Arrays.asList(PCMMPhase.PLANNING, PCMMPhase.EVIDENCE,
					PCMMPhase.ASSESS);

			/**
			 * Interact with the user click
			 */
			@Override
			public void chartMouseClicked(final ChartMouseEvent event) {

				if (event != null && event.getEntity() != null && event.getEntity() instanceof PieSectionEntity) {

					MouseEvent trigger = event.getTrigger();
					PieSectionEntity pieSectionEntity = (PieSectionEntity) event.getEntity();

					if (/* left click */ trigger.getButton() == MouseEvent.BUTTON1
							|| /* right click */trigger.getButton() == MouseEvent.BUTTON3) {
						if (null != pcmmConfiguration) {

							List<PCMMPhase> enabledAndActivatedChartMenuPhases = chartMenuPhases.stream()
									.filter(phaseEnabled::contains).filter(pcmmConfiguration.getPhases()::contains)
									.collect(Collectors.toList());

							if (enabledAndActivatedChartMenuPhases != null
									&& enabledAndActivatedChartMenuPhases.size() > 1) {

								// enable chart composite context menu on right click
								chartComposite.getMenu().setVisible(true);
								chartComposite.getMenu().setData(PIE_SECTION_ATTRIBUTE, pieSectionEntity);

							} else if (enabledAndActivatedChartMenuPhases != null) {
								// Get element selected
								PCMMElement pcmmElementSelected = elements
										.get(pieSectionEntity.getSectionKey().toString());

								// Open activated phase
								if (enabledAndActivatedChartMenuPhases.contains(PCMMPhase.EVIDENCE)) {
									getViewManager().openPCMMEvidenceView(pcmmElementSelected);
								} else if (enabledAndActivatedChartMenuPhases.contains(PCMMPhase.ASSESS)) {
									getViewManager().openPCMMAssessView(pcmmElementSelected);
								} else if (enabledAndActivatedChartMenuPhases.contains(PCMMPhase.PLANNING)) {
									getViewManager().openPCMMPlanningView(pcmmElementSelected);
								}
							}
						}
					} else {
						// consume event
						event.getTrigger().consume();
					}
				} else {

					// deactivate click outside the chart
					chartComposite.getMenu().setData(PIE_SECTION_ATTRIBUTE, null);
					chartComposite.getMenu().setVisible(false);
				}
			}

			/**
			 * Gray out the chart pie section to indicate the user that the wheel is
			 * clickable
			 */
			@Override
			public void chartMouseMoved(ChartMouseEvent event) {
				if (event != null && event.getEntity() != null && event.getEntity() instanceof PieSectionEntity) {

					PieSectionEntity pieSectionEntity = (PieSectionEntity) event.getEntity();

					// Get element selected
					PCMMElement pcmmElementSelected = elements.get(pieSectionEntity.getSectionKey().toString());

					if (pcmmElementSelected != null && pcmmElementSelected.getName() != null) {
						// paint section
						((RingPlot) chartComposite.getChart().getPlot()).setSectionPaint(pcmmElementSelected.getName(),
								ColorTools.rgbToAwtColor(ColorTools
										.grayedRgb(ColorTools.stringRGBToColor(pcmmElementSelected.getColor()))));
						((RingPlot) chartComposite.getChart().getPlot()).setSectionOutlinePaint(
								pcmmElementSelected.getName(), ColorTools.rgbToAwtColor(ColorTools
										.grayed(ColorTools.stringRGBToColor(pcmmElementSelected.getColor()), 25)));
						((RingPlot) chartComposite.getChart().getPlot())
								.setSectionOutlineStroke(pcmmElementSelected.getName(), new BasicStroke(3.0f));

						// reset the previous hover section if the element changed
						if (!pcmmElementSelected.equals(onHoverElement)) {
							resetOnHoverElement();
						}

						// set the current hover section
						onHoverElement = pcmmElementSelected;
					}
				} else {
					if (onHoverElement != null) {
						resetOnHoverElement();
					}
				}
			}

			/**
			 * Reset the current on hover element to its original color
			 */
			private void resetOnHoverElement() {
				// Get element hovered

				if (onHoverElement != null && onHoverElement.getName() != null) {
					// paint section
					((RingPlot) chartComposite.getChart().getPlot()).setSectionPaint(onHoverElement.getName(),
							ColorTools.toAwtColor(onHoverElement.getColor()));
					((RingPlot) chartComposite.getChart().getPlot()).setSectionOutlinePaint(onHoverElement.getName(),
							ColorTools.toAwtColor(ColorTools.DEFAULT_STRINGRGB_COLOR));
					((RingPlot) chartComposite.getChart().getPlot()).setSectionOutlineStroke(onHoverElement.getName(),
							new BasicStroke(1.0f));

					// set the current hover section to null
					onHoverElement = null;
				}
			}
		});

		// set the PCMM context menu on the chart composite
		chartComposite.setMenu(createChartContextMenu());

		return chartComposite;
	}

	/**
	 * @return a context Menu for the PCMM chart
	 */
	private Menu createChartContextMenu() {

		logger.debug("Creating chart contextual menu"); //$NON-NLS-1$

		// Create context menu
		Menu menu = new Menu(this);
		if (pcmmConfiguration != null && pcmmConfiguration.getPhases() != null) {

			// planning item
			if (phaseEnabled.contains(PCMMPhase.PLANNING) && pcmmConfiguration.isPcmmPlanningEnabled()) {
				MenuItem evidenceItem = new MenuItem(menu, SWT.PUSH);
				evidenceItem.setText(PCMMPhase.PLANNING.getName());
				evidenceItem.setData(PLANNING_COMMAND);
				evidenceItem.addSelectionListener(this);
				evidenceItem.setEnabled(true);
			}

			// evidence item
			if (phaseEnabled.contains(PCMMPhase.EVIDENCE) && pcmmConfiguration.isPcmmEvidenceEnabled()) {
				MenuItem evidenceItem = new MenuItem(menu, SWT.PUSH);
				evidenceItem.setText(PCMMPhase.EVIDENCE.getName());
				evidenceItem.setData(EVIDENCE_COMMAND);
				evidenceItem.addSelectionListener(this);
				evidenceItem.setEnabled(true);
			}

			// assess item
			if (phaseEnabled.contains(PCMMPhase.ASSESS) && pcmmConfiguration.isPcmmAssessEnabled()) {
				MenuItem assessItem = new MenuItem(menu, SWT.PUSH);
				assessItem.setText(PCMMPhase.ASSESS.getName());
				assessItem.setData(ASSESS_COMMAND);
				assessItem.setEnabled(true);
				assessItem.addSelectionListener(this);
			}
		}

		return menu;
	}

	/**
	 * Tag the current PCMM, and prompt the user to enter the tag parameters
	 */
	public void tagCurrentPCMM() {

		boolean canTag = checkEditorSaveNeed();

		if (canTag) {
			// create new tag dialog
			NewTagDialog tagDialog = new NewTagDialog(getViewManager(), getShell());
			Tag newTag = tagDialog.openDialog();

			// if the user entered a tag
			if (newTag != null) {
				newTag.setUserCreation(getViewManager().getCache().getUser());
				try {
					// tag
					getViewManager().getAppManager().getService(IPCMMApplication.class).tagCurrent(newTag);

					// save the credibility process
					getViewManager().viewChanged();

					// alert the user
					MessageDialog.openInformation(getShell(), RscTools.getString(RscConst.MSG_TAG_DIALOG_VIEWTITLE),
							RscTools.getString(RscConst.MSG_TAG_PART_TAG_SUCCESS));

					// reload the view
					tagList = getViewManager().getAppManager().getService(IPCMMApplication.class).getTags();
					pcmmTagPart.setTagList(tagList);

				} catch (CredibilityException e) {
					MessageDialog.openWarning(getShell(), RscTools.getString(RscConst.MSG_PCMMHOME_DIALOG_TITLE),
							RscTools.getString(RscConst.ERR_TAG_DIALOG_TAGGING_MSG));
					logger.warn("An error has occurred while tagging the current PCMM state:\n{}", e.getMessage(), e); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Open a dialog to manage the tags
	 */
	public void manageTags() {

		boolean canTag = checkEditorSaveNeed();

		if (canTag) {
			// open the tag manager dialog
			PCMMManageTagDialog tagDialog = new PCMMManageTagDialog(getViewManager(), getShell());
			tagDialog.open();

			// reload the view
			tagList = getViewManager().getAppManager().getService(IPCMMApplication.class).getTags();
			pcmmTagPart.setTagList(tagList);
		}
	}

	/**
	 * The tag selection changed
	 */
	public void tagSelectionChanged(Tag newTag) {
		// change the view behavior and reload data
		getViewManager().setSelectedTag(newTag);
	}

	/**
	 * If the editor is in a dirty state, this methods asks the user to save it.
	 * 
	 * @return true if the editor is clean to be tagged, otherwise false
	 */
	private boolean checkEditorSaveNeed() {

		boolean canContinue = true;

		// if the editor needs to be saved
		if (getViewManager().getCredibilityEditor().isDirty()) {

			canContinue = false;

			// ask to save before tagging
			boolean confirmSave = MessageDialog.openQuestion(getShell(),
					RscTools.getString(RscConst.MSG_TAG_DIALOG_VIEWTITLE),
					RscTools.getString(RscConst.MSG_EDITOR_SAVE_BEFORE));

			// save the credibility process
			if (confirmSave) {
				getViewManager().doSave();
				canContinue = true;
			}
		}

		return canContinue;
	}
}
