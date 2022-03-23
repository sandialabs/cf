/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.intendedpurpose;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
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
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.dto.EntityLockInfo;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.LinkWidget;
import gov.sandia.cf.parts.widgets.RichTextWidget;
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
public class IntendedPurposeWebView extends ACredibilitySubView<IntendedPurposeViewManager> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(IntendedPurposeWebView.class);

	/** The view ctrl. */
	private IntendedPurposeWebViewController viewCtrl;

	/** The main composite. */
	private Composite mainComposite;

	/** The rich text description. */
	private RichTextWidget richTextDescription;

	/** The link reference. */
	private LinkWidget linkReference;

	/** The view mode. */
	private ViewMode viewMode;

	/** The form composite. */
	private Composite formComposite;

	/** The btn edit. */
	private ButtonTheme btnEdit;

	/** The btn done. */
	private ButtonTheme btnDone;

	/** The btn cancel. */
	private ButtonTheme btnCancel;

	/** The btn back. */
	private ButtonTheme btnBack;

	/**
	 * The constructor.
	 *
	 * @param viewManager The view manager
	 * @param viewCtrl    the view ctrl
	 * @param style       The view style
	 */
	public IntendedPurposeWebView(IntendedPurposeViewManager viewManager, IntendedPurposeWebViewController viewCtrl,
			int style) {
		super(viewManager, viewManager, style);

		Assert.isNotNull(viewCtrl);
		this.viewCtrl = viewCtrl;
		this.viewMode = ViewMode.VIEW; // default behavior view mode

		// create the view
		renderPage();
	}

	/**
	 * Creates the page
	 */
	private void renderPage() {

		logger.debug("Render Intended Purpose page"); //$NON-NLS-1$

		// Main composite
		renderMainComposite();

		// Render sub-composites
		renderForm();
		repaintForm();

		// Render footer buttons
		renderFooterButtons();
		repaintFooterButtons();
	}

	/**
	 * Render main table composite
	 */
	private void renderMainComposite() {
		mainComposite = new Composite(this, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainComposite.setBackground(ColorTools.toColor(getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE)));
	}

	/**
	 * Render Intended Purpose form
	 */
	private void renderForm() {

		// Intended Purpose form composite
		formComposite = new Composite(mainComposite, SWT.FILL | SWT.BORDER);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		formComposite.setLayout(gridLayout);
		formComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		formComposite.setBackground(formComposite.getParent().getBackground());
	}

	/**
	 * Render footer buttons
	 */
	private void renderFooterButtons() {

		logger.debug("Render Intended Purpose footer buttons"); //$NON-NLS-1$

		// Footer buttons - Composite
		Composite compositeButtonsFooter = new Composite(this, SWT.FILL);
		GridLayout gridLayoutButtonsHeader = new GridLayout(2, false);
		compositeButtonsFooter.setLayout(gridLayoutButtonsHeader);
		compositeButtonsFooter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		// Composite for Footer left buttons
		Composite compositeButtonsFooterLeft = new Composite(compositeButtonsFooter, SWT.NONE);
		compositeButtonsFooterLeft.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		compositeButtonsFooterLeft.setLayout(new RowLayout());

		// Composite for header right buttons
		Composite compositeButtonsFooterRight = new Composite(compositeButtonsFooter, SWT.NONE);
		compositeButtonsFooterRight.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));
		compositeButtonsFooterRight.setLayout(new RowLayout());

		/* Left side */
		// Footer button - Back - Create
		Map<String, Object> btnBackOptions = new HashMap<>();
		btnBackOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_BACK));
		btnBackOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnBackOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_BACK);
		btnBackOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnBack = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER, btnBackOptions);
		btnBack.setToolTipText(RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_BTN_BACK_TOOLTIP));
		btnBack.setLayoutData(new RowData());
		btnBack.setVisible(true);
		// Footer buttons - Back - plug
		getViewManager().plugBackHomeButton(btnBack);

		// Footer button - Cancel - Create
		Map<String, Object> btnCancelOptions = new HashMap<>();
		btnCancelOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_CANCEL));
		btnCancelOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnCancelOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_CLOSE);
		btnCancelOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnCancelOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> viewCtrl.doCancelEditAction());
		btnCancel = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER,
				btnCancelOptions);
		btnCancel.setToolTipText(RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_BTN_CANCEL_TOOLTIP));
		btnCancel.setLayoutData(new RowData());
		btnCancel.setVisible(false);

		// Footer button - Help - Create
		Map<String, Object> btnHelpOptions = new HashMap<>();
		btnHelpOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnHelpOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_INFO);
		btnHelpOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_BLACK);
		btnHelpOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> HelpTools.openContextualHelp());
		ButtonTheme btnHelp = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterLeft, SWT.CENTER,
				btnHelpOptions);
		RowData btnLayoutData = new RowData();
		btnHelp.setLayoutData(btnLayoutData);
		HelpTools.addContextualHelp(compositeButtonsFooterLeft, ContextualHelpId.INTENDED_PURPOSE);

		/* Right side */
		// Footer button - Edit
		Map<String, Object> btnEditOptions = new HashMap<>();
		btnEditOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_EDIT));
		btnEditOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnEditOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_EDIT);
		btnEditOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		btnEditOptions.put(ButtonTheme.OPTION_ENABLED, ViewMode.VIEW.equals(viewMode));
		btnEditOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> viewCtrl.doEditAction());
		btnEdit = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterRight, SWT.PUSH | SWT.CENTER,
				btnEditOptions);
		btnEdit.setToolTipText(RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_BTN_EDIT_TOOLTIP));
		btnEdit.setLayoutData(new RowData());
		btnEdit.setVisible(true);

		// Footer button - Done
		Map<String, Object> btnDoneOptions = new HashMap<>();
		btnDoneOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_DONE));
		btnDoneOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnDoneOptions.put(ButtonTheme.OPTION_ICON, IconTheme.ICON_NAME_UPTODATE);
		btnDoneOptions.put(ButtonTheme.OPTION_COLOR, ConstantTheme.COLOR_NAME_GREEN);
		btnDoneOptions.put(ButtonTheme.OPTION_ENABLED, ViewMode.VIEW.equals(viewMode));
		btnDoneOptions.put(ButtonTheme.OPTION_LISTENER, (Listener) event -> viewCtrl.doEditDoneAction());
		btnDone = new ButtonTheme(getViewManager().getRscMgr(), compositeButtonsFooterRight, SWT.PUSH | SWT.CENTER,
				btnDoneOptions);
		btnDone.setToolTipText(RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_BTN_DONE_TOOLTIP));
		btnDone.setLayoutData(new RowData());
		btnDone.setVisible(false);

		// layout view
		compositeButtonsFooter.layout();
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

	/**
	 * Gets the view mode.
	 *
	 * @return the view mode
	 */
	public ViewMode getViewMode() {
		return viewMode;
	}

	/**
	 * Sets the view mode.
	 *
	 * @param viewMode the new view mode
	 */
	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
	}

	/**
	 * Gets the values.
	 *
	 * @return the values wrapped into an IntendedPurpose object
	 */
	IntendedPurpose getViewData() {
		IntendedPurpose toUpdate = new IntendedPurpose();
		toUpdate.setDescription(richTextDescription.getValue());
		toUpdate.setReference(linkReference.getGSONValue());
		return toUpdate;
	}

	/** {@inheritDoc} */
	@Override
	public void reload() {

		logger.debug("Reload Intended Purpose view"); //$NON-NLS-1$

		// repaint the form components
		repaintForm();
		repaintFooterButtons();

		// refresh the data
		IntendedPurpose intendedPurpose = viewCtrl.reloadIntendedPurpose();

		if (intendedPurpose == null) {
			return;
		}

		// set values
		if (richTextDescription != null) {
			richTextDescription.setValue(intendedPurpose.getDescription());
		}
		if (linkReference != null) {
			linkReference.setValue(intendedPurpose.getReference());
		}
	}

	/**
	 * Enable update.
	 */
	void unlock() {
		if (ViewMode.VIEW.equals(getViewMode())) {
			btnEdit.setEnabled(true);
		}

		clearFlashMessage();
	}

	/**
	 * Disable update.
	 */
	void lock() {
		if (ViewMode.VIEW.equals(getViewMode())) {
			btnEdit.setEnabled(false);
		}

		clearFlashMessage();

		EntityLockInfo lockInfo = viewCtrl.getLockInfo();
		if (lockInfo == null || StringUtils.isBlank(lockInfo.getInformation())) {
			setFlashMessage(NotificationFactory.getNewInfo(RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_LOCKED)));
		} else {
			setFlashMessage(NotificationFactory.getNewInfo(RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_LOCKED_BY),
					lockInfo.getInformation()));
		}

	}

	/**
	 * Reload Intended Purpose form components
	 */
	private void repaintForm() {

		// dispose children to repaint the form
		ViewTools.disposeChildren(formComposite);

		boolean isEditable = ViewMode.UPDATE.equals(viewMode);

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
		label.setForeground(ColorTools.toColor(getViewManager().getRscMgr(),
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
		GridData dataLabel = (GridData) label.getLayoutData();
		dataLabel.horizontalAlignment = GridData.FILL;
		dataLabel.verticalAlignment = GridData.FILL;
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.heightHint = dataLabel.heightHint + 10;
		label.setLayoutData(dataLabel);
		FontTools.setSubtitleFont(getViewManager().getRscMgr(), label);

		// Intended Purpose field
		richTextDescription = FormFactory.createRichTextWidget(getViewManager().getRscMgr(), formComposite,
				RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_DESCRIPTION), null, true, isEditable, false);
		richTextDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// References
		Label lblReferences = FormFactory.createLabel(formComposite,
				RscTools.getString(RscConst.MSG_INTENDEDPURPOSE_REFERENCE));
		FontTools.setBoldFont(getViewManager().getRscMgr(), lblReferences);
		linkReference = FormFactory.createLinkWidget(formComposite, getViewManager(), null, isEditable);
		linkReference.addChangedListener(e -> {
			linkReference.clearHelper();
		});
	}

	/**
	 * Repaint footer buttons.
	 */
	private void repaintFooterButtons() {

		/* VIEW view buttons */
		// Footer button - Back
		boolean updateButtonsVisible = ViewMode.UPDATE.equals(viewMode);
		if (btnBack != null) {
			btnBack.setVisible(!updateButtonsVisible);
			((RowData) btnBack.getLayoutData()).width = !updateButtonsVisible ? SWT.DEFAULT : 0;
			btnBack.requestLayout();
		}
		// Footer button - Edit
		if (btnEdit != null) {
			btnEdit.setVisible(!updateButtonsVisible);
			((RowData) btnEdit.getLayoutData()).width = !updateButtonsVisible ? SWT.DEFAULT : 0;
			btnEdit.requestLayout();
		}

		/* UPDATE view buttons */
		// Footer button - Cancel
		if (btnCancel != null) {
			btnCancel.setVisible(updateButtonsVisible);
			((RowData) btnCancel.getLayoutData()).width = updateButtonsVisible ? SWT.DEFAULT : 0;
			btnCancel.requestLayout();
		}
		// Footer button - Done
		if (btnDone != null) {
			btnDone.setVisible(updateButtonsVisible);
			((RowData) btnDone.getLayoutData()).width = updateButtonsVisible ? SWT.DEFAULT : 0;
			btnDone.requestLayout();
		}
	}

}
