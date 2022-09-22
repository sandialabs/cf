/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import gov.sandia.cf.model.Tag;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to display Quantity of Interest description
 * 
 * @author Maxime N
 */
public class ManagePCMMTagViewDialog extends GenericCFSmallDialog<PCMMViewManager> {

	/**
	 * the Quantity of Interest to create
	 */
	private Tag tag;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 */
	public ManagePCMMTagViewDialog(PCMMViewManager viewManager, Shell parentShell) {
		super(viewManager, parentShell);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_TAGVIEWDIALOG_TITLE, this.tag.getName()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		// form container
		Composite formContainer = new Composite(container, SWT.NONE);
		GridData scData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scData.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
		scData.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
		formContainer.setLayoutData(scData);
		GridLayout gridLayout = new GridLayout();
		formContainer.setLayout(gridLayout);

		// label Description
		Label lblDescription = new Label(formContainer, SWT.TOP);
		GridData dataDescriptionLbl = new GridData();
		lblDescription.setLayoutData(dataDescriptionLbl);
		lblDescription.setText(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_DESC));

		// text description - editor
		Browser txtCommentsBrowser = new Browser(formContainer, SWT.LEFT | SWT.WRAP);
		GridData dataComments = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataComments.grabExcessHorizontalSpace = true;
		dataComments.grabExcessVerticalSpace = true;
		dataComments.heightHint = PartsResourceConstants.DIALOG_TXT_INPUT_HEIGHT * 15;
		dataComments.horizontalSpan = 2;
		txtCommentsBrowser.setLayoutData(dataComments);
		String header = "<!DOCTYPE html>\r\n<html>\r\n<head>\r\n" + //$NON-NLS-1$
				"<style>\r\n" + //$NON-NLS-1$
				"body {overflow: scroll;}\r\n" + //$NON-NLS-1$
				"</style>\r\n" + //$NON-NLS-1$
				"</head>\r\n" + //$NON-NLS-1$
				"<body>"; //$NON-NLS-1$
		String footer = "</body>\r\n</html>"; //$NON-NLS-1$
		txtCommentsBrowser
				.setText(header + ((null == tag.getDescription()) ? RscTools.empty() : tag.getDescription()) + footer);

		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Set the new title of the dialog
		newShell.setText(RscTools.getString(RscConst.MSG_TAG_MGRTAGDIALOG_TITLE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, false);
	}

	/**
	 * @param tag the tag to update (null otherwise)
	 * @return the tag updated/created
	 */
	public Tag openDialog(Tag tag) {
		this.tag = tag;
		if (open() == Window.OK) {
			return this.tag;
		}
		return null;
	}

}
