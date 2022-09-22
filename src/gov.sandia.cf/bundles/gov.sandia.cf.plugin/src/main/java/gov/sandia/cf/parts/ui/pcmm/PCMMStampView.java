/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.widgets.PCMMChartFactory;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The PCMM Stamp (radar plot chart) view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMStampView extends ACredibilityPCMMView<PCMMStampViewController> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMStampView.class);

	/**
	 * the main composite
	 */
	private Composite mainComposite;

	/**
	 * Instantiates a new PCMM stamp view.
	 *
	 * @param viewController the view controller
	 * @param style          the view style
	 */
	public PCMMStampView(PCMMStampViewController viewController, int style) {
		super(viewController, viewController.getViewManager(), style);

		// create the view
		createPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_PCMMSTAMP_TITLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_PCMMSTAMP_ITEMTITLE);
	}

	/**
	 * Creates the phenomena view and all its components
	 * 
	 * @param parent
	 * 
	 */
	private void createPage() {
		// Render filters
		renderFilters();

		// layout
		GridLayout gridLayout = new GridLayout();
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.setLayout(gridLayout);

		/**
		 * Main Composite
		 */
		// main composite
		mainComposite = new Composite(this, SWT.BORDER);
		mainComposite.setBackground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));
		drawMainComposite();

		/**
		 * Footer
		 */
		// Footer buttons - Composite
		Composite compositeButtonsFooter = new Composite(this, SWT.FILL);
		GridLayout gridLayoutButtonsHeader = new GridLayout();
		compositeButtonsFooter.setLayout(gridLayoutButtonsHeader);
		compositeButtonsFooter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		// Composite for Footer left buttons
		Composite compositeButtonsFooterLeft = new Composite(compositeButtonsFooter, SWT.NONE);
		compositeButtonsFooterLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsFooterLeft.setLayout(new RowLayout());

		// Button Back - Create
		Map<String, Object> btnBackOptions = new HashMap<>();
		btnBackOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BACK));
		btnBackOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnBackOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_BACK);
		btnBackOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnBackOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) e -> getViewController().getViewManager().openHome());
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER,
				btnBackOptions);

		// Button - Guidance Level
		Map<String, Object> btnHelpLevelOptions = new HashMap<>();
		btnHelpLevelOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_LVLGUIDANCE));
		btnHelpLevelOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_HELP);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLUE);
		btnHelpLevelOptions.put(ButtonTheme.OPTION_LISTENER,
				(Listener) e -> getViewController().getViewManager().openPCMMHelpLevelView());
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtonsFooterLeft,
				SWT.PUSH | SWT.CENTER, btnHelpLevelOptions);

		// Footer buttons - Help - Create
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) e -> HelpTools.openContextualHelp());
		new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER,
				btnHelpOptions);
		HelpTools.addContextualHelp(compositeButtonsFooter, ContextualHelpId.PCMM_STAMP);

		// layout view
		compositeButtonsFooter.layout();
	}

	/**
	 * Create filters
	 */
	private void renderFilters() {
		// form container
		Composite formFilterContainer = new Composite(this, SWT.NONE);
		formFilterContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gridLayoutFormFilter = new GridLayout(1, false);
		formFilterContainer.setLayout(gridLayoutFormFilter);

		// label Filters
		Label lblFilter = new Label(formFilterContainer, SWT.LEFT);
		lblFilter.setText(RscTools.getString(RscConst.MSG_PCMMAGGREG_FILTER_LABEL));
		lblFilter.setForeground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		GridData lblFilterGridData = new GridData();
		lblFilter.setLayoutData(lblFilterGridData);
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), lblFilter);

		// form container
		Composite formRoleContainer = new Composite(formFilterContainer, SWT.NONE);
		formRoleContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gridLayoutForm = new GridLayout(2, false);
		formRoleContainer.setLayout(gridLayoutForm);

		// label role
		Label lblRole = new Label(formRoleContainer, SWT.RIGHT);
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), lblRole);
		lblRole.setText(RscTools.getString(RscConst.MSG_PCMMAGGREG_FILTER_ROLE_LABEL));
		GridData lblSubtitleGridData = new GridData();
		lblRole.setLayoutData(lblSubtitleGridData);

		// Get roles and the selected one
		List<Role> roles = getViewController().getViewManager().getAppManager().getService(IPCMMApplication.class)
				.getRoles();
		Role roleSelected = new Role();
		roles.add(roleSelected);

		// Combo-box role
		ComboViewer cbxRole = new ComboViewer(formRoleContainer, SWT.LEFT | SWT.READ_ONLY);
		GridData dataImportance = new GridData();
		cbxRole.getCombo().setLayoutData(dataImportance);
		cbxRole.setContentProvider(new ArrayContentProvider());
		cbxRole.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return (null != ((Role) element).getName()) ? ((Role) element).getName() : "All"; //$NON-NLS-1$
			}
		});
		cbxRole.setInput(roles);
		cbxRole.getCombo().addKeyListener(new ComboDropDownKeyListener());

		// Set the role selected
		cbxRole.setSelection(new StructuredSelection(roleSelected));

		// Listener - On change Role
		cbxRole.addSelectionChangedListener(element -> {
			// Initialize
			IStructuredSelection selection = (IStructuredSelection) element.getSelection();
			getViewController().putFilter(PCMMAssessment.Filter.ROLECREATION, selection.getFirstElement());

			// Reload
			getViewController().reloadData(true);
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {
		getViewController().reloadData(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void roleChanged() {
		// not used in PCMM stamp
	}

	/**
	 * Create the main composite with the PCMM wheel
	 * 
	 * @param compositeMain the parent composite to populate
	 */
	void drawMainComposite() {

		// dispose composite children
		ViewTools.disposeChildren(mainComposite);

		// composite layout
		GridData gdMainComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		mainComposite.setLayoutData(gdMainComposite);
		GridLayout layoutMainComp = new GridLayout(1, false);
		mainComposite.setLayout(layoutMainComp);
		
		List<PCMMElement> elements = getViewController().getElements();

		// draw the content
		if (elements == null || elements.isEmpty()) {
			Label lblEmptyPCMM = new Label(mainComposite, SWT.CENTER);
			GridData gdLblEmpty = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
			lblEmptyPCMM.setLayoutData(gdLblEmpty);
			lblEmptyPCMM.setText(RscTools.getString(RscConst.MSG_NO_DATA));
		} else {

			// Label to explain the PCMM Stamp goal
			StyledText txtPIRT = new StyledText(mainComposite, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
			txtPIRT.setText(RscTools.getString(RscConst.MSG_PCMMSTAMP_LBL_DESCRIPTION));
			GridData txtPIRTGridData = new GridData(SWT.CENTER, SWT.NONE, true, false);
			txtPIRT.setLayoutData(txtPIRTGridData);
			FontTools.setImportantTextFont(getViewController().getViewManager().getRscMgr(), txtPIRT);

			// Create PCMM Stamp Chart Composite
			try {
				PCMMChartFactory.createPCMMStampChart(mainComposite,
						getViewController().getViewManager().getPCMMConfiguration(),
						getViewController().getAggregation());
			} catch (CredibilityException e) {
				logger.error("Impossible to create the PCMM Stamp chart", e); //$NON-NLS-1$
			}

		}

		// layout the composite
		mainComposite.layout();
	}
}
