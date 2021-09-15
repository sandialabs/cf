/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.nebula.widgets.richtext.RichTextEditorConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.ui.pirt.PIRTViewManager;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to display Quantity of Interest description
 * 
 * @author Maxime N
 */
public class PIRTQoIDescriptionDialog extends GenericCFSmallDialog<PIRTViewManager> {

	/**
	 * the Quantity of Interest to create
	 */
	private QuantityOfInterest qoi;

	/**
	 * Editor Description
	 */
	private RichTextEditor editorDescription;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 * @param mode        the dialog mode
	 */
	public PIRTQoIDescriptionDialog(PIRTViewManager viewManager, Shell parentShell, DialogMode mode) {
		super(viewManager, parentShell);
		this.mode = mode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_QOIDESC_TITLE));
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

		if (this.mode.equals(DialogMode.UPDATE)) {
			// text description - configure editor
			RichTextEditorConfiguration editConfig = new RichTextEditorConfiguration();
			editConfig.setToolbarCollapsible(true);
			editConfig.setOption(RichTextEditorConfiguration.TOOLBAR_GROUPS,
					PartsResourceConstants.RICH_EDITOR_TOOLBAR);

			// text description - editor
			editorDescription = new RichTextEditor(formContainer, editConfig, SWT.BORDER);
			GridData dataDescription = new GridData(SWT.FILL, SWT.FILL, true, true);
			dataDescription.minimumHeight = PartsResourceConstants.RICHTEXTEDITOR_MINHEIGHT;
			dataDescription.heightHint = PartsResourceConstants.RICHTEXTEDITOR_MINHEIGHT;
			editorDescription.setLayoutData(dataDescription);
			editorDescription.setText(qoi.getDescription());
		} else {
			// text description - editor
			Browser txtCommentsBrowser = new Browser(formContainer, SWT.LEFT | SWT.WRAP);
			GridData dataComments = new GridData(SWT.FILL, SWT.FILL, true, true);
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
			txtCommentsBrowser.setText(
					header + ((null == qoi.getDescription()) ? RscTools.empty() : qoi.getDescription()) + footer);
		}

		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Set the new title of the dialog
		newShell.setText(RscTools.getString(RscConst.MSG_QOIDESC_PAGE_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {
		// Set text
		qoi.setDescription(editorDescription.getText());
		super.okPressed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if (this.mode.equals(DialogMode.UPDATE)) {
			createButton(parent, IDialogConstants.OK_ID, RscTools.getString(RscConst.MSG_BTN_UPDATE), true);
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		} else {
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, false);
		}
	}

	/**
	 * @param qoi the qoi to update.
	 * @return the quantity of interest to create
	 */
	public QuantityOfInterest openDialog(QuantityOfInterest qoi) {
		this.qoi = qoi;
		if (open() == Window.OK) {
			return qoi;
		}
		return null;
	}

}
