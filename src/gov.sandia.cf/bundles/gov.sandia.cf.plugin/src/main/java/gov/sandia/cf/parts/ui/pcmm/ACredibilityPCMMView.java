/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.ACredibilitySubView;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Qn abstract class to force the credibility views to implement methods needed
 * for the view managers
 * 
 * @author Didier Verstraete
 *
 */
public abstract class ACredibilityPCMMView extends ACredibilitySubView<PCMMViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ACredibilityPCMMView.class);

	/**
	 * Form role composite
	 */
	private Composite formRoleContainer;

	/**
	 * the label for the role description
	 */
	private Label lblRole;

	/**
	 * The Role combo-box
	 */
	private ComboViewer cbxRole;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parent      the parent composite
	 * @param style       the style
	 */
	protected ACredibilityPCMMView(PCMMViewManager viewManager, Composite parent, int style) {
		super(viewManager, parent, style);

		// Composite for right items
		Composite compositeButtonsHeaderRight = new Composite(this, SWT.NONE);
		compositeButtonsHeaderRight.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		compositeButtonsHeaderRight.setLayout(gridLayout);

		// form container
		formRoleContainer = new Composite(compositeButtonsHeaderRight, SWT.NONE);
		formRoleContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayoutForm = new GridLayout(2, false);
		formRoleContainer.setLayout(gridLayoutForm);

		// label role
		lblRole = new Label(formRoleContainer, SWT.RIGHT);
		FontTools.setBoldFont(getViewManager().getRscMgr(), lblRole);
		lblRole.setText(RscTools.getString(RscConst.MSG_PCMMSELECTROLE_ROLE_LABEL));
		GridData lblSubtitleGridData = new GridData();
		lblRole.setLayoutData(lblSubtitleGridData);

		// Get roles and the selected one
		List<Role> roles = getViewManager().getAppManager().getService(IPCMMApplication.class).getRoles();
		Role roleSelected = getViewManager().getCache().getCurrentPCMMRole();

		// Combo-box role
		cbxRole = new ComboViewer(formRoleContainer, SWT.LEFT | SWT.READ_ONLY);
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

		// Set the role selected
		if (roleSelected != null) {
			cbxRole.setSelection(new StructuredSelection(roleSelected));
		}

		// Listener - On change Role
		cbxRole.addSelectionChangedListener(this::roleSelectionChanged);

		// set title label tag icone
		if (getViewManager().getSelectedTag() != null) {
			this.lblTitle.setImage(IconTheme.getIconImage(getViewManager().getRscMgr(), IconTheme.ICON_NAME_TAG,
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BROWN), 30));
		} else {
			this.lblTitle.setImage(null);
		}
		this.lblTitle.requestLayout();
	}

	/**
	 * Change the role selection
	 * 
	 * @param element
	 */
	private void roleSelectionChanged(SelectionChangedEvent element) {

		// Initialize
		IStructuredSelection selection = (IStructuredSelection) element.getSelection();
		Role roleSelected = (Role) selection.getFirstElement();
		Role roleInCache = getViewManager().getCache().getCurrentPCMMRole();

		// Check has changed
		boolean hasChanged = true;
		if (null != roleInCache && roleSelected.getId().equals(roleInCache.getId())) {
			hasChanged = false;
		}
		// Update if Role has changed
		if (hasChanged) {
			try {
				// update database
				getViewManager().getCache().updatePCMMRole(roleSelected);

				// refresh views
				getViewManager().refreshRole();

				// trigger a change to save
				getViewManager().viewChanged();

				// fire role changed in the views
				roleChanged();

			} catch (CredibilityException e) {
				logger.error(MessageFormat.format("An error occured while updating PCMM role:\n{0}", e.getMessage()), //$NON-NLS-1$
						e);
				MessageDialog.openError(getShell(), RscTools.getString(RscConst.MSG_PCMMSELECTROLE_TITLE),
						e.getMessage());
			}
		}
	}

	/**
	 * Refresh the role
	 */
	public void refreshRole() {
		// set the role selected
		Role roleSelected = getViewManager().getCache().getCurrentPCMMRole();
		if (roleSelected != null) {
			cbxRole.setSelection(new StructuredSelection(roleSelected));
		}
	}

	/**
	 * Refresh the tag icon
	 */
	public void refreshTag() {
		// set title label tag icone
		if (getViewManager().getSelectedTag() != null) {
			this.lblTitle.setImage(IconTheme.getIconImage(getViewManager().getRscMgr(), IconTheme.ICON_NAME_TAG,
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BROWN), 30));
		} else {
			this.lblTitle.setImage(null);
		}
		this.lblTitle.requestLayout();
	}

	/**
	 * Refresh the role
	 */
	public void hideRoleSelection() {
		formRoleContainer.setVisible(false);
		((GridData) formRoleContainer.getLayoutData()).heightHint = 0;
	}

	/**
	 * Refresh the role
	 */
	public void showRoleSelection() {
		formRoleContainer.setVisible(true);
		((GridData) formRoleContainer.getLayoutData()).heightHint = formRoleContainer.computeSize(SWT.DEFAULT,
				SWT.DEFAULT).y;
	}

	/**
	 * This method is called when the PCMM role has been changed. The inherited
	 * classes can use this method to refresh their components.
	 */
	public abstract void roleChanged();

}
