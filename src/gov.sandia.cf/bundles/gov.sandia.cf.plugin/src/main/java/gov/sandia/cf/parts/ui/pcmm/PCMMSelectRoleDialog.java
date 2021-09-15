/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import gov.sandia.cf.model.Role;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add a new qoi
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMSelectRoleDialog extends GenericCFSmallDialog<PCMMViewManager> {

	/**
	 * the role combobox
	 */
	private ComboViewer cbxRole;

	/**
	 * the list of roles configured
	 */
	private List<Role> roles;

	/**
	 * the role to select and return
	 */
	private Role roleSelected;

	/**
	 * The constructor
	 * 
	 * @param viewManager  the view manager
	 * @param parentShell  the parent shell
	 * @param roles        the list of available roles
	 * @param roleSelected the role selected by default
	 */
	public PCMMSelectRoleDialog(PCMMViewManager viewManager, Shell parentShell, List<Role> roles, Role roleSelected) {
		super(viewManager, parentShell);
		this.roles = roles;
		this.roleSelected = roleSelected;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_PCMMSELECTROLE_TITLE));
		setMessage(RscTools.getString(RscConst.MSG_PCMMSELECTROLE_DESCRIPTION), IMessageProvider.INFORMATION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		// form container
		Composite formContainer = new Composite(container, SWT.NONE);
		formContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout(2, false);
		formContainer.setLayout(gridLayout);

		// label name
		Label lblRole = new Label(formContainer, SWT.NONE);
		lblRole.setText(RscTools.getString(RscConst.MSG_PCMMSELECTROLE_ROLE));

		// combobox role
		cbxRole = new ComboViewer(formContainer, SWT.LEFT);
		GridData dataImportance = new GridData();
		dataImportance.grabExcessHorizontalSpace = true;
		dataImportance.horizontalAlignment = GridData.FILL;
		cbxRole.getCombo().setLayoutData(dataImportance);
		cbxRole.setContentProvider(new ArrayContentProvider());
		cbxRole.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Role) element).getName();
			}
		});
		cbxRole.setInput(roles);
		cbxRole.getCombo().addKeyListener(new ComboDropDownKeyListener());

		// set the role selected
		if (roleSelected != null) {
			cbxRole.setSelection(new StructuredSelection(roleSelected));
		}

		// dialog behavior
		cbxRole.getCombo().setFocus();

		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Set the new title of the dialog
		newShell.setText(RscTools.getString(RscConst.MSG_PCMMSELECTROLE_PAGE_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {

		// defines if form is valid or not
		boolean formValid = true;
		String errorMessage = RscTools.empty();

		ISelection selection = null;

		// test combobox role
		selection = cbxRole.getSelection();
		Role role = null;
		if (!selection.isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			role = (Role) structuredSelection.getFirstElement();
		} else {
			formValid = false;
			errorMessage += RscTools.getString(RscConst.ERR_PCMMSELECTROLE_ROLE_MANDATORY)
					+ RscTools.getString(RscConst.CARRIAGE_RETURN);
		}

		// tests form validation
		if (formValid) {
			setErrorMessage(null);

			// set fields
			this.roleSelected = role;

			super.okPressed();
		} else {
			setErrorMessage(errorMessage);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, RscTools.getString(RscConst.MSG_BTN_SELECT), true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * @return the role selected
	 */
	public Role openDialog() {
		if (open() == Window.OK) {
			return this.roleSelected;
		}

		return null;
	}

}
