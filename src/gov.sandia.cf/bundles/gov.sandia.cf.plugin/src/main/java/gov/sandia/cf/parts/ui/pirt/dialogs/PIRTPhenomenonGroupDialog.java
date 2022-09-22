/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.ui.pirt.PIRTViewManager;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.TextWidget;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add a phenomenon group
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTPhenomenonGroupDialog extends GenericCFSmallDialog<PIRTViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTPhenomenonGroupDialog.class);
	/**
	 * The phenomenonGroup to create
	 */
	private PhenomenonGroup phenomenonGroup;
	/**
	 * Dialog label text
	 */
	private TextWidget txtLabel;

	/**
	 * the button name
	 */
	private String buttonName;

	/**
	 * The constructor
	 * 
	 * @param viewManager     the view manager
	 * @param parentShell     the parent shell
	 * @param qoiSelected     the current qoi
	 * @param phenomenonGroup the phenomenon group to update (if null, put the
	 *                        dialog in create mode)
	 * @param mode            the dialog mode
	 */
	public PIRTPhenomenonGroupDialog(PIRTViewManager viewManager, Shell parentShell, QuantityOfInterest qoiSelected,
			PhenomenonGroup phenomenonGroup, ViewMode mode) {
		super(viewManager, parentShell);
		if (mode == null) {
			mode = ViewMode.VIEW;
		}

		switch (mode) {
		case CREATE:
			this.phenomenonGroup = new PhenomenonGroup();
			this.phenomenonGroup.setQoi(qoiSelected);
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CREATE);
			this.mode = ViewMode.CREATE;
			break;

		case UPDATE:
			this.phenomenonGroup = phenomenonGroup;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_UPDATE);
			this.mode = mode;
			break;

		case VIEW:
			this.phenomenonGroup = phenomenonGroup;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CLOSE);
			this.mode = mode;
			break;
		default:
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_DIALOG_PHENGROUP_TITLE));
		if (mode != ViewMode.VIEW) {
			setMessage(RscTools.getString(RscConst.MSG_DIALOG_PHENGROUP_DESCRIPTION), IMessageProvider.INFORMATION);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		logger.debug("Create phenomenon group dialog"); //$NON-NLS-1$

		Composite container = (Composite) super.createDialogArea(parent);

		// form container
		Composite formContainer = new Composite(container, SWT.NONE);
		formContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout(2, false);
		formContainer.setLayout(gridLayout);

		// label text
		FormFactory.createLabel(formContainer, RscTools.getString(RscConst.MSG_DIALOG_PHENGROUP_LABEL));

		// text Text
		boolean editable = mode != ViewMode.VIEW;
		txtLabel = FormFactory.createTextWidget(getViewManager().getRscMgr(), formContainer, editable, null);
		txtLabel.setValue((phenomenonGroup != null && phenomenonGroup.getName() != null) ? phenomenonGroup.getName()
				: RscTools.empty());

		txtLabel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				isValid();
			}
		});

		txtLabel.setFocus();

		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		// Sets the new title of the dialog
		switch (mode) {
		case CREATE:
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_PHENGROUP_PAGENAME_ADD));
			break;
		case UPDATE:
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_PHENGROUP_PAGENAME_EDIT));
			break;
		case VIEW:
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_PHENGROUP_PAGENAME_VIEW));
			break;
		default:
			break;
		}
	}

	/**
	 * @return a string if no error is detected, otherwise the error message
	 */
	private boolean isValid() {

		if (mode == ViewMode.VIEW) {
			return true;
		}

		// clear message
		txtLabel.clearHelper();

		boolean valid = true;

		// test Description
		if (txtLabel.getValue() == null || txtLabel.getValue().isEmpty()) {
			txtLabel.setHelper(NotificationFactory.getNewError(RscTools.getString(RscConst.ERR_DIALOG_PHEN_LABEL)));
			valid = false;
		}

		// change ok button
		setEnableOkButton(valid);

		return valid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {

		boolean valid = isValid();

		// Check form is valid
		if (valid) {

			// Set data
			phenomenonGroup.setName(txtLabel.getValue());

			super.okPressed();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		String okButtonName = (buttonName != null && !buttonName.isEmpty()) ? buttonName : IDialogConstants.OK_LABEL;
		createButton(parent, IDialogConstants.OK_ID, okButtonName, true);
		if (!ViewMode.VIEW.equals(this.mode)) {
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		}
	}

	/**
	 * @return the phenomenon group created
	 */
	public PhenomenonGroup openDialog() {
		if (open() == Window.OK) {
			return phenomenonGroup;
		}

		return null;
	}

}
