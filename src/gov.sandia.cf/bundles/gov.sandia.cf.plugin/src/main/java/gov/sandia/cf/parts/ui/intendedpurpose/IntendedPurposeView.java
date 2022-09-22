/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.intendedpurpose;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.LinkWidget;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.HelpTools;
import gov.sandia.cf.tools.HelpTools.ContextualHelpId;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Intended Purpose view: it is the Intended Purpose home page
 * 
 * @author Didier Verstraete
 *
 */
public class IntendedPurposeView extends ACredibilitySubView<IntendedPurposeViewController> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(IntendedPurposeView.class);

	/**
	 * Composites
	 */
	private Composite mainComposite;

	private RichTextEditor richTextDescription;

	private LinkWidget linkReference;

	/**
	 * The constructor.
	 *
	 * @param viewController The view controller
	 * @param style          The view style
	 */
	public IntendedPurposeView(IntendedPurposeViewController viewController, int style) {
		super(viewController, viewController.getViewManager(), style);

		// create the view
		renderPage();
	}

	/** {@inheritDoc} */
	@Override
	public String getTitle() {
		return RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_TITLE);
	}

	/** {@inheritDoc} */
	@Override
	public String getItemTitle() {
		return RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_ITEM);
	}

	/** {@inheritDoc} */
	@Override
	public void reload() {
		getViewController().reloadData();
	}

	/**
	 * Refresh the view content
	 */
	void refreshContent() {

		IntendedPurpose intendedPurpose = getViewController().getIntendedPurpose();

		if (intendedPurpose == null) {
			return;
		}

		// set values
		if (richTextDescription != null && intendedPurpose.getDescription() != null) {
			richTextDescription.setText(intendedPurpose.getDescription());
		}
		if (linkReference != null && intendedPurpose.getReference() != null) {
			linkReference.setValue(intendedPurpose.getReference());
		}
	}

	/**
	 * Creates the page
	 */
	private void renderPage() {

		logger.debug("Render Intended Purpose page"); //$NON-NLS-1$

		// Main composite
		renderMainComposite();

		// Render footer buttons
		renderFooterButtons();
	}

	/**
	 * Render main table composite
	 */
	private void renderMainComposite() {
		mainComposite = new Composite(this, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainComposite.setBackground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));

		// Render sub-composites
		renderForm();
	}

	/**
	 * Render Intended Purpose form
	 */
	private void renderForm() {

		// Intended Purpose form composite
		Composite formComposite = new Composite(mainComposite, SWT.FILL | SWT.BORDER);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		formComposite.setLayout(gridLayout);
		formComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		formComposite.setBackground(formComposite.getParent().getBackground());

		// Header composite
		Composite headerComposite = new Composite(formComposite, SWT.NONE);
		GridLayout gdHeaderComposite = new GridLayout(2, false);
		headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		headerComposite.setLayout(gdHeaderComposite);
		headerComposite.setBackground(headerComposite.getParent().getBackground());

		// Header label
		Label label = FormFactory.createLabel(headerComposite,
				RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_HEADER_LBL));
		label.setBackground(label.getParent().getBackground());
		label.setForeground(ColorTools.toColor(getViewController().getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		GridData dataLabel = (GridData) label.getLayoutData();
		dataLabel.horizontalAlignment = GridData.FILL;
		dataLabel.verticalAlignment = GridData.FILL;
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.heightHint = dataLabel.heightHint + 10;
		label.setLayoutData(dataLabel);
		FontTools.setSubtitleFont(getViewController().getViewManager().getRscMgr(), label);

		// Intended Purpose field
		Label lblIntendedPurpose = FormFactory.createLabel(formComposite,
				RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_DESCRIPTION));
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), lblIntendedPurpose);
		richTextDescription = FormFactory.createRichText(formComposite, null);
		richTextDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		richTextDescription.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				getViewController().changedDescription(richTextDescription.getText());
			}
		});
		richTextDescription
				.addModifyListener(e -> getViewController().changedDescription(richTextDescription.getText()));

		// References
		Label lblReferences = FormFactory.createLabel(formComposite,
				RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_REFERENCE));
		FontTools.setBoldFont(getViewController().getViewManager().getRscMgr(), lblReferences);
		linkReference = FormFactory.createLinkWidget(formComposite, getViewController().getViewManager(), null, true);
		linkReference.setOnlyWarning(true);
		linkReference.addChangedListener(e -> {
			getViewController().changedReference(linkReference.getGSONValue());
			if (linkReference.isValid()) {
				linkReference.clearHelper();
			}
		});
	}

	/**
	 * Render footer buttons
	 */
	private void renderFooterButtons() {

		logger.debug("Render Intended Purpose footer buttons"); //$NON-NLS-1$

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
		ButtonTheme btnBack = new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtons,
				SWT.CENTER, btnBackOptions);

		// Footer buttons - Help - Create
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> HelpTools.openContextualHelp());

		ButtonTheme btnHelp = new ButtonTheme(getViewController().getViewManager().getRscMgr(), compositeButtons,
				SWT.CENTER, btnHelpOptions);
		RowData btnLayoutData = new RowData();
		btnHelp.setLayoutData(btnLayoutData);
		HelpTools.addContextualHelp(compositeButtons, ContextualHelpId.INTENDED_PURPOSE);

		// Footer buttons - Back - plug
		getViewController().getViewManager().plugBackHomeButton(btnBack);

		// layout view
		compositeButtons.layout();
	}

}
