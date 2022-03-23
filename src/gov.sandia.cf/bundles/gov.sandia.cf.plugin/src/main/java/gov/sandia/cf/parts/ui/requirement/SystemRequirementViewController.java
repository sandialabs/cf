/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.requirement;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.requirement.ISystemRequirementApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.services.genericparam.IGenericParameterService;
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
				parent, ViewMode.CREATE);
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
			requirement.getRequirementParameterList()
					.forEach(u -> view.getViewManager().getClientService(IGenericParameterService.class)
							.openLinkValue(u, view.getViewManager().getCache().getOpenLinkBrowserOpts()));
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
					requirement, requirement.getParent(), ViewMode.VIEW);
			dialog.openDialog();
		}
	}

	/**
	 * Update SystemRequirement
	 * 
	 * @param systemRequirement the systemRequirement to update
	 */
	void updateRequirementAction(SystemRequirement systemRequirement) {

		// Keep previous group
		SystemRequirement previousGroup = systemRequirement.getParent();

		// Open dialog in View Mode
		SystemRequirementDialog dialog = new SystemRequirementDialog(view.getViewManager(), view.getShell(),
				systemRequirement, previousGroup, ViewMode.UPDATE);
		SystemRequirement systemRequirementToUpdate = dialog.openDialog();

		// Update
		try {
			updateSystemRequirement(systemRequirementToUpdate);
		} catch (CredibilityException e) {
			logger.error("An error occured while updating systemRequirement: {}", RscTools.carriageReturn() //$NON-NLS-1$
					+ e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_DIALOG_SYSREQUIREMENT_TITLE),
					e.getMessage());
		}

		if (previousGroup != null && !previousGroup.equals(systemRequirement.getParent())) {
			view.getViewManager().getAppManager().getService(ISystemRequirementApplication.class)
					.refresh(previousGroup);
		}

		refreshIfChanged();
	}

	/**
	 * Update SystemRequirement.
	 *
	 * @param systemRequirement the systemRequirement to update
	 * @throws CredibilityException the credibility exception
	 */
	SystemRequirement updateSystemRequirement(SystemRequirement systemRequirement) throws CredibilityException {

		if (systemRequirement == null) {
			return null;
		}

		// Update
		SystemRequirement systemRequirementUpdated = view.getViewManager().getAppManager()
				.getService(ISystemRequirementApplication.class)
				.updateRequirement(systemRequirement, view.getViewManager().getCache().getUser());

		// Refresh parent
		SystemRequirement newGroup = systemRequirementUpdated.getParent();
		if (newGroup != null) {
			view.getViewManager().getAppManager().getService(ISystemRequirementApplication.class).refresh(newGroup);
		}

		// fire view change to save credibility file
		view.getViewManager().viewChanged();

		return systemRequirementUpdated;
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

	/**
	 * Reorder all.
	 *
	 * @return true, if successful
	 */
	protected boolean reorderAll() {
		try {

			// reorder
			view.getViewManager().getAppManager().getService(ISystemRequirementApplication.class).reorderAll(
					view.getViewManager().getCache().getModel(), view.getViewManager().getCache().getUser());

			// fire view change to save credibility file
			view.getViewManager().viewChanged();

		} catch (CredibilityException e) {
			logger.error("Impossible to reorder all uncertainties: {}", e.getMessage(), e);//$NON-NLS-1$
			return false;
		}

		return true;
	}

	/**
	 * Reorder systemRequirement.
	 *
	 * @param systemRequirement the systemRequirement
	 * @param newIndex          the new index
	 * @throws CredibilityException the credibility exception
	 */
	void reorder(SystemRequirement systemRequirement, int newIndex) throws CredibilityException {

		view.getViewManager().getAppManager().getService(ISystemRequirementApplication.class)
				.reorderSystemRequirement(systemRequirement, newIndex, view.getViewManager().getCache().getUser());

		// fire view change to save credibility file
		view.getViewManager().viewChanged();
	}

	/**
	 * Refresh systemRequirement.
	 *
	 * @param systemRequirement the systemRequirement
	 */
	void refreshSystemRequirement(SystemRequirement systemRequirement) {
		view.getViewManager().getAppManager().getService(ISystemRequirementApplication.class)
				.refresh(systemRequirement);
	}

	/**
	 * Refresh if changed.
	 */
	public void refreshIfChanged() {

		// Refresh
		if (view.getViewManager().isDirty()) {
			view.refresh();
		}
	}
}
