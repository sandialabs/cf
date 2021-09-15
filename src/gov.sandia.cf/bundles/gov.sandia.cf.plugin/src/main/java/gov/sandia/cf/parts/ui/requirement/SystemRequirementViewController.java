/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.requirement;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IGenericParameterApplication;
import gov.sandia.cf.application.ISystemRequirementApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * System Requirement view controller: Used to control the System Requirement
 * view
 * 
 * @author Didier Verstraete
 *
 */
public class SystemRequirementViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SystemRequirementViewController.class);

	/**
	 * The view
	 */
	private SystemRequirementView view;

	SystemRequirementViewController(SystemRequirementView view) {
		Assert.isNotNull(view);
		this.view = view;
	}

	/**
	 * Add SystemRequirement
	 */
	void addRequirement() {
		addRequirement(null);
	}

	/**
	 * Add SystemRequirement in tree
	 */
	void addRequirement(SystemRequirement parent) {
		// Open dialog in View Mode
		SystemRequirementDialog dialog = new SystemRequirementDialog(view.getViewManager(), view.getShell(), null,
				parent, DialogMode.CREATE);
		SystemRequirement requirement = dialog.openDialog();

		if (requirement != null) {
			try {
				// Set id
				requirement.setGeneratedId(view.getIdColumnText(requirement));

				// Create
				view.getViewManager().getAppManager().getService(ISystemRequirementApplication.class).addRequirement(
						requirement, view.getViewManager().getCache().getModel(),
						view.getViewManager().getCache().getUser());

				// Refresh parent
				if (parent != null) {
					view.getViewManager().getAppManager().getService(ISystemRequirementApplication.class)
							.refresh(parent);

					// Expand parent
					view.expandElements(parent);
				}

				// Fire view change to save credibility file
				view.getViewManager().viewChanged();

				// Refresh
				view.refresh();
				
			} catch (CredibilityException e) {
				MessageDialog.openError(view.getShell(),
						RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_GROUP_NAME),
						RscTools.getString(RscConst.ERR_PHENOMENAVIEW_ADDING_PHENGROUP)
								+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}
		}
	}

	/**
	 * Open all system requirement values with type LINK for this decision
	 * 
	 * @param requirement the system requirement to open values for
	 */
	void openAll(SystemRequirement requirement) {
		if (requirement != null && requirement.getRequirementParameterList() != null) {
			requirement.getRequirementParameterList().forEach(u -> view.getViewManager().getAppManager()
					.getService(IGenericParameterApplication.class).openLinkValue(u));
		}
	}

	/**
	 * View the element
	 * 
	 * @param element the system requirement to view
	 */
	void viewElement(Object element) {

		if (element instanceof SystemRequirement) {
			// view the requirement group
			viewRequirementAction((SystemRequirement) element);
		}
	}

	/**
	 * Add SystemRequirement
	 * 
	 * @param requirement the system requirement to add
	 */
	void viewRequirementAction(SystemRequirement requirement) {
		if (requirement == null) {
			logger.warn("The requirement to view is null"); //$NON-NLS-1$
		} else {
			// Open dialog in View Mode
			SystemRequirementDialog dialog = new SystemRequirementDialog(view.getViewManager(), view.getShell(),
					requirement, requirement.getParent(), DialogMode.VIEW);
			dialog.openDialog();
		}
	}

	/**
	 * Update SystemRequirement
	 * 
	 * @param requirement the system requirement to update
	 */
	void updateRequirementAction(SystemRequirement requirement) {
		// Open dialog in View Mode
		SystemRequirement oldParent = requirement.getParent();
		SystemRequirementDialog dialog = new SystemRequirementDialog(view.getViewManager(), view.getShell(),
				requirement, oldParent, DialogMode.UPDATE);
		SystemRequirement requirementToUpdate = dialog.openDialog();

		if (requirementToUpdate != null) {
			try {
				// Create
				SystemRequirement requirementUpdated = view.getViewManager().getAppManager()
						.getService(ISystemRequirementApplication.class)
						.updateRequirement(requirementToUpdate, view.getViewManager().getCache().getUser());

				// Refresh parent
				SystemRequirement newParent = requirementUpdated.getParent();
				if (newParent != null) {
					view.getViewManager().getAppManager().getService(ISystemRequirementApplication.class)
							.refresh(newParent);
				}
				if (oldParent != null && !oldParent.equals(newParent)) {
					view.getViewManager().getAppManager().getService(ISystemRequirementApplication.class)
							.refresh(oldParent);
				}

				// Fire view change to save credibility file
				view.getViewManager().viewChanged();

				// Refresh
				view.refresh();
			} catch (CredibilityException e) {
				MessageDialog.openError(view.getShell(),
						RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_GROUP_NAME),
						RscTools.getString(RscConst.ERR_PHENOMENAVIEW_ADDING_PHENGROUP)
								+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}
		}
	}

	/**
	 * Update an element
	 * 
	 * @param element the system requirement to update
	 */
	void updateElement(Object element) {
		if (element instanceof SystemRequirement) {
			// view the requirement
			updateRequirementAction((SystemRequirement) element);
		}
	}

	/**
	 * Delete an element
	 * 
	 * @param element the system requirement to delete
	 */
	void deleteElement(Object element) {

		// delete the requirement
		if (element instanceof SystemRequirement) {
			deleteRequirementAction((SystemRequirement) element);
		}
	}

	/**
	 * Delete an Requirement
	 * 
	 * @param requirement the system requirement to delete
	 */
	void deleteRequirementAction(SystemRequirement requirement) {
		// constructs confirm message
		String title = null;
		String message = null;
		if (requirement.getParent() == null) {
			title = RscTools.getString(RscConst.MSG_SYSREQUIREMENT_GROUP);
			message = RscTools.getString(RscConst.MSG_SYSREQUIREMENT_GROUP_DELETECONFIRM, requirement.getStatement());
		} else {
			title = RscTools.getString(RscConst.MSG_SYSREQUIREMENT);
			message = RscTools.getString(RscConst.MSG_SYSREQUIREMENT_DELETECONFIRM, requirement.getStatement());
		}

		// confirm dialog
		boolean confirm = MessageDialog.openConfirm(view.getShell(),
				RscTools.getString(RscConst.MSG_SYSREQUIREMENT_DELETECONFIRM_TITLE, title), message);

		if (confirm) {
			try {
				// Get Parent
				SystemRequirement parent = requirement.getParent();

				// Remove
				view.getViewManager().getAppManager().getService(ISystemRequirementApplication.class)
						.deleteRequirement(requirement);

				// Refresh parent's children list
				if (parent != null) {
					view.getViewManager().getAppManager().getService(ISystemRequirementApplication.class)
							.refresh(parent);
				}

				// Fire view change to save credibility file
				view.getViewManager().viewChanged();

				// Refresh
				view.refresh();

			} catch (CredibilityException e) {
				logger.error("An error occured while deleting requirement: {}", requirement //$NON-NLS-1$
						+ RscTools.carriageReturn() + e.getMessage(), e);
			}
		}
	}

}
