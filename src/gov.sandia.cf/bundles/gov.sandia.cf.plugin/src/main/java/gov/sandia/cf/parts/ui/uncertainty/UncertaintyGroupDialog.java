/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.uncertainty;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add a uncertainty group
 * 
 * @author Maxime N.
 *
 */
public class UncertaintyGroupDialog extends GenericCFSmallDialog<UncertaintyViewManager> {

	/**
	 * The uncertaintyGroup to create
	 */
	private UncertaintyGroup uncertaintyGroup;

	/**
	 * Dialog label label
	 */
	private Label lblLabel;
	/**
	 * Dialog label text
	 */
	private Text txtLabel;

	/**
	 * the button name
	 */
	private String buttonName;

	/**
	 * The constructor
	 * 
	 * @param viewManager      the view manager
	 * @param parentShell      the parent shell
	 * @param uncertaintyGroup the uncertainty group to update (if null, put the
	 *                         dialog in create mode)
	 * @param mode             the dialog mode
	 */
	public UncertaintyGroupDialog(UncertaintyViewManager viewManager, Shell parentShell,
			UncertaintyGroup uncertaintyGroup, DialogMode mode) {
		super(viewManager, parentShell);
		if (mode == null) {
			mode = DialogMode.VIEW;
		}

		switch (mode) {
		case CREATE:
			this.uncertaintyGroup = new UncertaintyGroup();
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CREATE);
			this.mode = DialogMode.CREATE;
			break;

		case UPDATE:
			this.uncertaintyGroup = uncertaintyGroup;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_UPDATE);
			this.mode = mode;
			break;

		case VIEW:
			this.uncertaintyGroup = uncertaintyGroup;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CLOSE);
			this.mode = mode;
			break;
		default:
			break;
		}
	}

	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_TITLE));
		if (mode != DialogMode.VIEW) {
			setMessage(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_DESCRIPTION),
					IMessageProvider.INFORMATION);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		// form container
		Composite formContainer = new Composite(container, SWT.NONE);
		formContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout(2, false);
		formContainer.setLayout(gridLayout);

		if (mode == DialogMode.VIEW) {
			renderNonEditableContent(formContainer);
		} else {
			renderEditableContent(formContainer);
		}

		return container;
	}

	/**
	 * Render the dialog content in non editable mode
	 * 
	 * @param parent the parent composite
	 */
	private void renderNonEditableContent(Composite parent) {
		// label text
		lblLabel = new Label(parent, SWT.NONE);
		lblLabel.setText(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_NAME));

		// text Text
		Label txtViewLabel = new Label(parent, SWT.NONE);
		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.horizontalAlignment = GridData.FILL;
		txtViewLabel.setLayoutData(dataLabel);
		txtViewLabel
				.setText((uncertaintyGroup != null && uncertaintyGroup.getName() != null) ? uncertaintyGroup.getName()
						: RscTools.empty());
		txtViewLabel.setFocus();
	}

	/**
	 * Render the dialog content in editable mode
	 * 
	 * @param parent the parent composite
	 */
	private void renderEditableContent(Composite parent) {
		// label text
		lblLabel = new Label(parent, SWT.NONE);
		lblLabel.setText(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_NAME));

		// text Text
		txtLabel = new Text(parent, SWT.BORDER);
		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.horizontalAlignment = GridData.FILL;
		txtLabel.setLayoutData(dataLabel);
		txtLabel.setText((uncertaintyGroup != null && uncertaintyGroup.getName() != null) ? uncertaintyGroup.getName()
				: RscTools.empty());

		txtLabel.setFocus();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		// Sets the new title of the dialog
		switch (mode) {
		case CREATE:
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_PAGENAME_ADD));
			break;
		case UPDATE:
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_PAGENAME_EDIT));
			break;
		case VIEW:
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_PAGENAME_VIEW));
			break;
		default:
			break;
		}
	}

	@Override
	protected void okPressed() {

		if (mode == DialogMode.VIEW) {
			super.okPressed();
		} else {

			boolean formValid = true;
			String errorMessage = RscTools.empty();

			// test Description
			if (txtLabel.getText() == null || txtLabel.getText().isEmpty()) {
				formValid = false;
				errorMessage += RscTools.getString(RscConst.ERR_DIALOG_UNCERTAINTY_GROUP_NAME)
						+ RscTools.getString(RscConst.CARRIAGE_RETURN);
			}

			// tests form validation
			if (formValid) {
				setErrorMessage(null);

				uncertaintyGroup.setName(txtLabel.getText());
				super.okPressed();
			} else {
				setErrorMessage(errorMessage);
			}
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		String okButtonName = (buttonName != null && !buttonName.isEmpty()) ? buttonName : IDialogConstants.OK_LABEL;
		createButton(parent, IDialogConstants.OK_ID, okButtonName, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * @return the uncertainty group created
	 */
	public UncertaintyGroup openDialog() {
		if (open() == Window.OK) {
			return uncertaintyGroup;
		}

		return null;
	}

}
