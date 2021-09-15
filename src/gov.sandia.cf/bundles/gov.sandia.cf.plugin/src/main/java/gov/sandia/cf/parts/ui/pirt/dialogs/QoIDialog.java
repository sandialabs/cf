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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.RichTextWidget;
import gov.sandia.cf.parts.widgets.TextWidget;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add a new Quantity of Interest
 * 
 * @author Didier Verstraete
 *
 */
public class QoIDialog extends GenericCFSmallDialog<IViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(QoIDialog.class);

	/**
	 * the name Text
	 */
	private TextWidget txtSymbol;

	/**
	 * the description Text
	 */
	private RichTextWidget editorDescription;

	/**
	 * the Quantity of Interest
	 */
	private QuantityOfInterest qoi;

	/**
	 * The simplified Constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 * @param mode        the dialog mode
	 */
	public QoIDialog(IViewManager viewManager, Shell parentShell, DialogMode mode) {
		this(viewManager, parentShell, mode, null);
	}

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 * @param mode        the dialog mode
	 * @param qoiToCopy   the qoi to copy
	 */
	public QoIDialog(IViewManager viewManager, Shell parentShell, DialogMode mode, QuantityOfInterest qoiToCopy) {
		super(viewManager, parentShell);

		this.mode = mode;
		this.qoi = new QuantityOfInterest();

		if (this.mode == DialogMode.COPY) {

			String qoiToCopyName = RscTools.getString(RscConst.MSG_OBJECT_NULL);

			if (qoiToCopy != null) {

				this.qoi = qoiToCopy.copy();

				// set the qoi name
				if (qoiToCopy.getSymbol() != null) {
					qoiToCopyName = qoiToCopy.getSymbol();
				}
				this.qoi.setSymbol(qoiToCopyName + " " + RscTools.getString(RscConst.MSG_COPYQOI_NAME_SUFFIX)); //$NON-NLS-1$
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_ADDQOI_TITLE));
		setMessage(RscTools.getString(RscConst.MSG_ADDQOI_DESCRIPTION), IMessageProvider.INFORMATION);
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

		// label name
		FormFactory.createLabel(formContainer, RscTools.getString(RscConst.MSG_QOI_FIELD_SYMBOL));

		// text symbol
		txtSymbol = FormFactory.createTextWidget(getViewManager().getRscMgr(), formContainer, true, null);
		txtSymbol.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// change ok button
				setEnableOkButton(isValid());
			}
		});

		// label description
		Label lblDescription = FormFactory.createLabel(formContainer,
				RscTools.getString(RscConst.MSG_QOI_FIELD_DESCRIPTION));
		GridData datalBLDescription = new GridData();
		datalBLDescription.verticalAlignment = GridData.BEGINNING;
		lblDescription.setLayoutData(datalBLDescription);

		// text description - editor
		editorDescription = FormFactory.createRichTextWidget(getViewManager().getRscMgr(), formContainer, true, true);

		// Set data
		if (mode == DialogMode.COPY) {
			txtSymbol.setValue(this.qoi.getSymbol());
			editorDescription.setValue(qoi.getDescription());
		}

		// dialog behavior
		txtSymbol.setFocus();

		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		// Set the new title of the dialog
		if (mode == DialogMode.COPY) {
			newShell.setText(RscTools.getString(RscConst.MSG_COPYQOI_PAGE_NAME));
		} else {
			newShell.setText(RscTools.getString(RscConst.MSG_ADDQOI_PAGE_NAME));
		}
	}

	/**
	 * @return a string if no error is detected, otherwise the error message
	 */
	private boolean isValid() {

		// clear message
		txtSymbol.clearHelper();

		boolean valid = true;

		// tests Name
		if (txtSymbol.getValue() == null || txtSymbol.getValue().isEmpty()) {
			txtSymbol.setHelper(
					NotificationFactory.getNewError(RscTools.getString(RscConst.ERR_ADDQOI_SYMBOL_MANDATORY)));
			valid = false;
		} else {

			// check if qoi name already exists
			try {
				boolean existsQoISymbol = getViewManager().getAppManager().getService(IPIRTApplication.class)
						.existsQoISymbol(new Integer[] { qoi.getId() }, txtSymbol.getValue());
				if (existsQoISymbol) {
					txtSymbol.setHelper(NotificationFactory.getNewError(
							RscTools.getString(RscConst.ERR_COPYQOI_NAME_DUPLICATED, txtSymbol.getValue())));
					valid = false;
				}
			} catch (CredibilityException e) {
				logger.error("An error occured while retrieving the qoi names", e); //$NON-NLS-1$
				txtSymbol.setHelper(NotificationFactory.getNewError(e.getMessage()));
				valid = false;
			}
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

		// defines if form is valid or not
		boolean valid = isValid();

		// Check form is valid
		if (valid) {

			// Set data
			this.qoi.setCreationDate(DateTools.getCurrentDate());
			this.qoi.setSymbol(txtSymbol.getValue());
			this.qoi.setDescription(editorDescription.getValue());

			// Call super
			super.okPressed();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if (mode == DialogMode.COPY) {
			createButton(parent, IDialogConstants.OK_ID, RscTools.getString(RscConst.MSG_BTN_COPY), true);
		} else {
			createButton(parent, IDialogConstants.OK_ID, RscTools.getString(RscConst.MSG_BTN_CREATE), true);
		}
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * @return the quantity of interest to create
	 */
	public QuantityOfInterest openDialog() {
		if (open() == Window.OK) {
			return this.qoi;
		}

		return null;
	}

}
