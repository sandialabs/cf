/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.RichTextWidget;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to tag a qoi
 * 
 * @author Didier Verstraete
 *
 */
public class QoITagDialog extends GenericCFSmallDialog<IViewManager> {

	/**
	 * the qoi to tag
	 */
	private QuantityOfInterest qoiToTag;
	/**
	 * the tag description
	 */
	private String tagDescription;
	/**
	 * the description Text
	 */
	private RichTextWidget editorDescription;

	/**
	 * @param viewManager the view manager
	 * @param parentShell the parent shell @param qoiToTag the qoi to tag
	 * @param qoiToTag    the qoi to tag
	 */
	public QoITagDialog(IViewManager viewManager, Shell parentShell, QuantityOfInterest qoiToTag) {
		super(viewManager, parentShell);
		this.qoiToTag = qoiToTag;
		tagDescription = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_TAGQOI_TITLE));
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
		GridLayout gridLayout = new GridLayout(2, false);
		formContainer.setLayout(gridLayout);

		// label qoi
		FormFactory.createLabel(formContainer, RscTools.getString(RscConst.MSG_TAGQOI_FIELD_QOI));

		// text qoi
		Text txtQoi = new Text(formContainer, SWT.BORDER);
		GridData dataQoi = new GridData();
		dataQoi.grabExcessHorizontalSpace = true;
		dataQoi.horizontalAlignment = GridData.FILL;
		dataQoi.minimumHeight = PartsResourceConstants.DIALOG_TXT_INPUT_HEIGHT;
		txtQoi.setLayoutData(dataQoi);
		txtQoi.setEditable(false);

		String qoiName = qoiToTag != null ? qoiToTag.getSymbol() : RscTools.empty();
		txtQoi.setText(qoiName);

		// label name
		Label lblDescription = FormFactory.createLabel(formContainer,
				RscTools.getString(RscConst.MSG_TAGQOI_FIELD_DESCRIPTION));
		GridData datalBLDescription = new GridData();
		datalBLDescription.verticalAlignment = GridData.BEGINNING;
		lblDescription.setLayoutData(datalBLDescription);

		// text description - editor
		editorDescription = FormFactory.createRichTextWidget(getViewManager().getRscMgr(), formContainer,
				RscTools.getString(RscConst.MSG_TAGQOI_DESCRIPTION, qoiName), null, true, true, true);

		// dialog behavior
		editorDescription.setFocus();

		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Set the new title of the dialog
		newShell.setText(RscTools.getString(RscConst.MSG_TAGQOI_PAGE_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {

		// set qoi tag description
		tagDescription = editorDescription.getValue();

		setErrorMessage(null);
		super.okPressed();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, RscTools.getString(RscConst.MSG_BTN_CREATE), true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * @return the tagged qoi
	 */
	public String openDialog() {
		if (open() == Window.OK) {
			return tagDescription;
		}

		return null;
	}

}
