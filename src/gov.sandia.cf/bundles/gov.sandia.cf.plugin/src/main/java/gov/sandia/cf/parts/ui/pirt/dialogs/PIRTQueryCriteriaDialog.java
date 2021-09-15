/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.dialogs;

import java.util.ArrayList;
import java.util.List;

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

import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.ui.pirt.PIRTViewManager;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add a new phenomenon
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTQueryCriteriaDialog extends GenericCFSmallDialog<PIRTViewManager> {

	/**
	 * column name property to set criterion name
	 */
	private static final String COLUMN_NAME_PROPERTY = "COLUMN_NAME"; //$NON-NLS-1$
	/**
	 * the criteria input or parameter list
	 */
	private List<String> criteriaInputList;
	/**
	 * the criteria list
	 */
	private List<String> criteriaList;

	/**
	 * the criteria labels
	 */
	private List<Label> criteriaLabels;
	/**
	 * the criteria viewers
	 */
	private List<Text> criteriaText;

	/**
	 * @param viewManager  the view manager
	 * @param parentShell  the parent shell
	 * @param criteriaList the criteria list
	 */
	public PIRTQueryCriteriaDialog(PIRTViewManager viewManager, Shell parentShell, List<String> criteriaList) {
		super(viewManager, parentShell);
		this.criteriaList = criteriaList;
		this.criteriaInputList = new ArrayList<>();
		this.criteriaLabels = new ArrayList<>();
		this.criteriaText = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_PIRT_DLG_QUERY_CRIT_TITLE));
		setMessage(RscTools.getString(RscConst.MSG_PIRT_DLG_QUERY_CRIT_MSG), IMessageProvider.INFORMATION);
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

		// add generated fields according to criteriaList
		if (criteriaList != null && !criteriaList.isEmpty()) {
			for (String criteria : criteriaList) {

				// label
				Label label = new Label(formContainer, SWT.NONE);
				label.setText(criteria + RscTools.COLON);
				criteriaLabels.add(label);

				// text
				Text text = new Text(formContainer, SWT.BORDER);
				GridData data = new GridData();
				data.grabExcessHorizontalSpace = true;
				data.horizontalAlignment = GridData.FILL;
				data.minimumHeight = PartsResourceConstants.DIALOG_TXT_INPUT_HEIGHT;
				text.setLayoutData(data);
				text.setData(COLUMN_NAME_PROPERTY, criteria);
				criteriaText.add(text);
			}

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
		newShell.setText(RscTools.getString(RscConst.MSG_PIRT_DLG_QUERY_CRIT_MSG));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {

		criteriaInputList = new ArrayList<>();

		// criteria list
		for (Text text : criteriaText) {
			criteriaInputList.add(text.getText());
		}

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
	 * @return the phenomenon to create
	 */
	public List<String> openDialog() {
		if (open() == Window.OK) {
			return criteriaInputList;
		}
		return new ArrayList<>();
	}

}
