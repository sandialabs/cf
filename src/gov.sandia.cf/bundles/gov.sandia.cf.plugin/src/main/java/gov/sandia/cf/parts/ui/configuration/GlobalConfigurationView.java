/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.widgets.CollapsibleWidget;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.SelectWidget;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Configuration view: the global configuration view
 * 
 * @author Didier Verstraete
 *
 */
public class GlobalConfigurationView extends ACredibilitySubView<ConfigurationViewManager> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(GlobalConfigurationView.class);

	/**
	 * Controller
	 */
	private GlobalConfigurationViewController viewCtrl;

	/**
	 * The main composite
	 */
	private Composite mainComposite;

	/**
	 * The open link option combobox
	 */
	private SelectWidget<OpenLinkBrowserOption> cbxOpenLinkOpts;

	/**
	 * @param viewManager the view manager
	 * @param parent      the parent composite
	 * @param style       the view style
	 */
	public GlobalConfigurationView(ConfigurationViewManager viewManager, Composite parent, int style) {
		super(viewManager, parent, style);

		this.viewCtrl = new GlobalConfigurationViewController(this);

		// create the view
		renderPage();
	}

	/** {@inheritDoc} */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_CONF_GLOBALVIEW_TITLE);
	}

	/** {@inheritDoc} */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_CONF_GLOBALVIEW_ITEMTITLE);
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
		mainComposite.setBackground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));

		// Main table composite
		firstScroll.setContent(mainComposite);
		firstScroll.setExpandHorizontal(true);
		firstScroll.setExpandVertical(true);
		firstScroll.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		// Render sub-composites
		renderBrowserOption();
	}

	/**
	 * Render Browser option composite
	 */
	private void renderBrowserOption() {

		logger.debug("Render Global Configuration Browser option"); //$NON-NLS-1$

		// QoI Planning main composite
		Composite schemaComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		schemaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		schemaComposite.setLayout(gridLayout);
		schemaComposite.setBackground(schemaComposite.getParent().getBackground());

		// Browser option collapse
		new CollapsibleWidget(getViewManager().getRscMgr(), mainComposite, SWT.FILL | SWT.BORDER, schemaComposite,
				RscTools.getString(RscConst.MSG_CONF_GLOBALVIEW_BROWSER_TITLE), false, true);

		// label
		Label label = FormFactory.createLabel(schemaComposite,
				RscTools.getString(RscConst.PREFS_GLOBAL_OPEN_LINK_BROWSER_OPTION));
		label.setBackground(label.getParent().getBackground());
		FontTools.setBoldFont(getViewManager().getRscMgr(), label);

		// text path
		cbxOpenLinkOpts = FormFactory.createSelectWidget(getViewManager().getRscMgr(),
				schemaComposite, true, null,
				Arrays.asList(OpenLinkBrowserOption.values()));
		cbxOpenLinkOpts.setValue(null);

		// text input listener
		cbxOpenLinkOpts
				.addSelectionChangedListener(e -> viewCtrl.updateGlobalConfigurationAction(cbxOpenLinkOpts.getValue()));
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
		HelpTools.addContextualHelp(compositeButtonsFooterLeft, ContextualHelpId.EXPORT);

		// Footer buttons - Back - plug
		getViewManager().plugBackButton(btnBack);

		// layout view
		compositeButtonsFooter.layout();
	}

	/** {@inheritDoc} */
	@Override
	public void reload() {
		reloadOpenLinkBrowserOptions();
	}

	/**
	 * Reload the open link browser options
	 */
	private void reloadOpenLinkBrowserOptions() {
		if (cbxOpenLinkOpts != null) {
			OpenLinkBrowserOption finalOption = null;
			try {
				finalOption = OpenLinkBrowserOption.valueOf(viewCtrl.getConfiguration().getOpenLinkBrowserOpts());
			} catch (IllegalArgumentException e) {
				finalOption = OpenLinkBrowserOption.ECLIPSE_PREFERENCE;
			}
			cbxOpenLinkOpts.setValue(finalOption);
		}
	}

	OpenLinkBrowserOption getCbxOpenLinkOpts() {
		return cbxOpenLinkOpts.getValue();
	}
}
