/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.GenericCFDialog;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.RichTextWidget;
import gov.sandia.cf.parts.widgets.TextWidget;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to tag the PCMM process
 * 
 * @author Didier Verstraete
 *
 */
public class NewTagDialog extends GenericCFDialog<IViewManager> {

	/**
	 * the name text
	 */
	private TextWidget txtName;
	/**
	 * the description text
	 */
	private RichTextWidget editorDescription;

	/**
	 * the tag to update
	 */
	private Tag tag;

	/**
	 * Use this constructor to create tag
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 */
	public NewTagDialog(IViewManager viewManager, Shell parentShell) {
		super(viewManager, parentShell);
		this.tag = new Tag();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_TAG_NEWTAGDIALOG_TITLE));
		setMessage(RscTools.getString(RscConst.MSG_TAG_NEWTAGDIALOG_MSG), IMessageProvider.INFORMATION);
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
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		// scroll container
		ScrolledComposite scrollContainer = new ScrolledComposite(container, SWT.V_SCROLL);
		GridData scrollScData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrollScData.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
		scrollScData.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
		scrollContainer.setLayoutData(scrollScData);
		scrollContainer.setLayout(new GridLayout());

		// form container
		Composite formContainer = new Composite(scrollContainer, SWT.NONE);
		GridData scData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scData.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
		scData.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
		formContainer.setLayoutData(scData);
		GridLayout gridLayout = new GridLayout(2, false);
		formContainer.setLayout(gridLayout);
		gridLayout.verticalSpacing = PartsResourceConstants.DEFAULT_GRIDDATA_V_INDENT;

		// label info
		new Label(formContainer, SWT.NONE);
		FormFactory.getNotificationLabel(getViewManager().getRscMgr(), formContainer,
				NotificationFactory.getNewInfo(RscTools.getString(RscConst.MSG_TAG_NEWTAGDIALOG_INFO)));

		// label name
		FormFactory.createFormLabel(formContainer, RscTools.getString(RscConst.MSG_TAG_NEWTAGDIALOG_LBL));

		// text name
		txtName = FormFactory.createTextWidget(getViewManager().getRscMgr(), formContainer, true, null);
		txtName.setTextLimit(70);
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				checkName();
			}
		});

		// label description
		FormFactory.createFormLabel(formContainer, RscTools.getString(RscConst.MSG_TAG_NEWTAGDIALOG_DESC));

		// text description
		editorDescription = FormFactory.createRichTextWidget(getViewManager().getRscMgr(), formContainer, true, true);

		// set scroll container size
		scrollContainer.setContent(formContainer);
		scrollContainer.setExpandHorizontal(true);
		scrollContainer.setExpandVertical(true);
		scrollContainer.setMinSize(formContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		formContainer.addListener(SWT.Resize,
				e -> scrollContainer.setMinSize(formContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT)));
		formContainer
				.addPaintListener(e -> scrollContainer.setMinSize(formContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT)));

		// set focus
		txtName.setFocus();

		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Set the new title of the dialog
		newShell.setText(RscTools.getString(RscConst.MSG_TAG_DIALOG_VIEWTITLE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {

		// defines if form is valid or not
		boolean formValid = true;

		// test name
		formValid = checkName();

		// tests form validation
		if (!formValid) {
			return;
		}

		// set fields
		this.tag.setName(txtName.getValue());
		this.tag.setDescription(editorDescription.getValue());

		super.okPressed();
	}

	/**
	 * Check the tag name
	 */
	private boolean checkName() {

		boolean valid = true;

		// test name
		if (txtName.getValue() == null || txtName.getValue().isEmpty()) {
			valid = false;
			txtName.setHelper(
					NotificationFactory.getNewError(RscTools.getString(RscConst.ERR_TAG_NEWTAGDIALOG_NO_NAME)));
		}

		return valid;
	}

	/**
	 * @return the tag to create/update
	 */
	public Tag openDialog() {
		if (open() == Window.OK) {
			return this.tag;
		}

		return null;
	}

}
