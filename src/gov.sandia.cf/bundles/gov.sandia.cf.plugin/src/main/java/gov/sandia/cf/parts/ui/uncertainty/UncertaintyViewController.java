/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.uncertainty;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IGenericParameterApplication;
import gov.sandia.cf.application.IGlobalApplication;
import gov.sandia.cf.application.IUncertaintyApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Uncertainty view controller: Used to control the Uncertainty view
 * 
 * @author Didier Verstraete
 *
 */
public class UncertaintyViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(UncertaintyViewController.class);

	/**
	 * The decision view
	 */
	private UncertaintyView view;

	UncertaintyViewController(UncertaintyView view) {
		Assert.isNotNull(view);
		this.view = view;
	}

	/**
	 * Add UncertaintyGroup
	 */
	void addUncertaintyGroup() {

		// Open dialog in View Mode
		UncertaintyGroupDialog dialog = new UncertaintyGroupDialog(view.getViewManager(), view.getShell(), null,
				DialogMode.CREATE);
		UncertaintyGroup group = dialog.openDialog();
		Model model = view.getViewManager().getCache().getModel();

		if (group != null && model != null) {
			try {
				// create
				view.getViewManager().getAppManager().getService(IUncertaintyApplication.class)
						.addUncertaintyGroup(group, model, view.getViewManager().getCache().getUser());

				// refresh qoi
				view.getViewManager().getAppManager().getService(IGlobalApplication.class).refresh(model);

				// fire view change to save credibility file
				view.getViewManager().viewChanged();

				// reload
				view.refresh();

			} catch (CredibilityException e) {
				logger.error("An error occured while updating uncertainty group: {}", RscTools.carriageReturn() //$NON-NLS-1$
						+ e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_DIALOG_UNCERTAINTY_TITLE),
						e.getMessage());
			}
		}
	}

	/**
	 * Add Uncertainty
	 * 
	 * @param groupSelected the group to add the uncertainty into
	 */
	void addUncertainty(UncertaintyGroup groupSelected) {

		// Open dialog in View Mode
		UncertaintyDialog dialog = new UncertaintyDialog(view.getViewManager(), view.getShell(), null, groupSelected,
				DialogMode.CREATE);
		Uncertainty uncertainty = dialog.openDialog();

		if (uncertainty != null) {
			try {

				// Create
				Uncertainty uncertaintyCreated = view.getViewManager().getAppManager()
						.getService(IUncertaintyApplication.class)
						.addUncertainty(uncertainty, view.getViewManager().getCache().getUser());

				// Associate to existing group and refresh view
				List<UncertaintyGroup> groups = view.getTreeInput();
				if (groups != null) {
					for (UncertaintyGroup group : groups) {
						if (group.equals(uncertaintyCreated.getGroup())) {
							if (group.getUncertainties() == null) {
								group.setUncertainties(new ArrayList<>());
							}
							group.getUncertainties().add(uncertaintyCreated);
						}
					}
				}

				// Refresh parent
				if (uncertainty.getGroup() != null) {
					this.view.getViewManager().getAppManager().getService(IUncertaintyApplication.class)
							.refresh(uncertainty.getGroup());

					// Expand parent
					view.expandElements(uncertainty.getGroup());
				}

				// refresh view
				view.reload();

				// fire view change to save credibility file
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error("An error occured while adding uncertainty: {}", RscTools.carriageReturn() //$NON-NLS-1$
						+ e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_DIALOG_UNCERTAINTY_TITLE),
						e.getMessage());
			}
		}
	}

	/**
	 * Open all uncertainty values with type LINK for this uncertainty
	 * 
	 * @param uncertainty the uncertainty to open values for
	 */
	void openAllUncertaintyValues(Uncertainty uncertainty) {
		if (uncertainty != null && uncertainty.getUncertaintyParameterList() != null) {
			uncertainty.getUncertaintyParameterList().forEach(u -> view.getViewManager().getAppManager()
					.getService(IGenericParameterApplication.class).openLinkValue(u));
		}
	}

	/**
	 * View the element. Do nothing if not an Uncertainty nor an UncertaintyGroup.
	 * 
	 * @param element the element to view
	 */
	void viewElement(Object element) {

		if (element != null) {
			if (element instanceof UncertaintyGroup) {

				// view the uncertainty group
				viewUncertaintyGroupAction((UncertaintyGroup) element);

			} else if (element instanceof Uncertainty) {

				// view the uncertainty
				viewUncertaintyAction((Uncertainty) element);
			}
		}
	}

	/**
	 * View UncertaintyGroup
	 * 
	 * @param group the group to view
	 */
	void viewUncertaintyGroupAction(UncertaintyGroup group) {
		if (group == null) {
			logger.warn("The uncertainty group to view is null"); //$NON-NLS-1$
		} else {
			// Open dialog in View Mode
			UncertaintyGroupDialog dialog = new UncertaintyGroupDialog(view.getViewManager(), view.getShell(), group,
					DialogMode.VIEW);
			dialog.openDialog();
		}
	}

	/**
	 * Add Uncertainty
	 * 
	 * @param uncertainty the uncertainty to add
	 */
	void viewUncertaintyAction(Uncertainty uncertainty) {
		if (uncertainty == null) {
			logger.warn("The uncertainty to view is null"); //$NON-NLS-1$
		} else {
			// Open dialog in View Mode
			UncertaintyDialog dialog = new UncertaintyDialog(view.getViewManager(), view.getShell(), uncertainty,
					uncertainty.getGroup(), DialogMode.VIEW);
			dialog.openDialog();
		}
	}

	/**
	 * Update UncertaintyGroup
	 * 
	 * @param group the uncertainty group to update
	 */
	void updateUncertaintyGroupAction(UncertaintyGroup group) {

		// Open dialog in View Mode
		UncertaintyGroupDialog dialog = new UncertaintyGroupDialog(view.getViewManager(), view.getShell(), group,
				DialogMode.UPDATE);
		UncertaintyGroup groupToUpdate = dialog.openDialog();

		if (groupToUpdate != null) {
			try {
				// create
				view.getViewManager().getAppManager().getService(IUncertaintyApplication.class)
						.updateUncertaintyGroup(groupToUpdate);

				// Refresh
				view.refresh();

				// fire view change to save credibility file
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error("An error occured while updating uncertainty group: {}", RscTools.carriageReturn() //$NON-NLS-1$
						+ e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_DIALOG_UNCERTAINTY_TITLE),
						e.getMessage());
			}
		}
	}

	/**
	 * Update Uncertainty
	 * 
	 * @param uncertainty the uncertainty to update
	 */
	void updateUncertaintyAction(Uncertainty uncertainty) {

		// Keep previous group
		UncertaintyGroup previousGroup = uncertainty.getGroup();

		// Open dialog in View Mode
		UncertaintyDialog dialog = new UncertaintyDialog(view.getViewManager(), view.getShell(), uncertainty,
				uncertainty.getGroup(), DialogMode.UPDATE);
		Uncertainty uncertaintyToUpdate = dialog.openDialog();

		if (uncertaintyToUpdate != null) {
			try {
				// Create
				Uncertainty uncertaintyUpdated = view.getViewManager().getAppManager()
						.getService(IUncertaintyApplication.class)
						.updateUncertainty(uncertaintyToUpdate, view.getViewManager().getCache().getUser());

				UncertaintyGroup newGroup = uncertaintyUpdated.getGroup();

				// update uncertainty group lists
				if (previousGroup != null && newGroup != null) {
					previousGroup.getUncertainties().remove(uncertaintyToUpdate);
					newGroup.getUncertainties().add(uncertaintyUpdated);
				}

				// Refresh
				view.refresh();

				// fire view change to save credibility file
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error("An error occured while updating uncertainty: {}", RscTools.carriageReturn() //$NON-NLS-1$
						+ e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_DIALOG_UNCERTAINTY_TITLE),
						e.getMessage());
			}
		}
	}

	/**
	 * Update an element. Do nothing if not an Uncertainty nor an UncertaintyGroup.
	 * 
	 * @param element the uncertainty or uncertainty group to update
	 */
	void updateElement(Object element) {
		if (element != null) {
			if (element instanceof UncertaintyGroup) {
				// view the uncertainty group
				updateUncertaintyGroupAction((UncertaintyGroup) element);
			} else if (element instanceof Uncertainty) {
				// view the uncertainty
				updateUncertaintyAction((Uncertainty) element);
			}
		}
	}

	/**
	 * Delete an element. Do nothing if not an Uncertainty nor an UncertaintyGroup.
	 * 
	 * @param element the uncertainty or uncertainty group to delete
	 */
	void deleteElement(Object element) {

		// delete the uncertainty group
		if (element instanceof UncertaintyGroup) {
			deleteUncertaintyGroupAction((UncertaintyGroup) element);
		}
		// delete the uncertainty
		else if (element instanceof Uncertainty) {
			deleteUncertaintyAction((Uncertainty) element);
		}
	}

	/**
	 * Delete a Group
	 * 
	 * @param group the uncertainty group to delete
	 */
	void deleteUncertaintyGroupAction(UncertaintyGroup group) {

		// constructs confirm message
		String title = RscTools.getString(RscConst.MSG_UNCERTAINTY_GROUP);
		String message = RscTools.getString(RscConst.MSG_UNCERTAINTY_GROUP_DELETECONFIRM, group.getName());

		// confirm dialog
		boolean confirm = MessageDialog.openConfirm(view.getShell(),
				RscTools.getString(RscConst.MSG_UNCERTAINTY_DELETECONFIRM_TITLE, title), message);

		if (confirm) {
			try {
				// delete
				view.getViewManager().getAppManager().getService(IUncertaintyApplication.class)
						.deleteUncertaintyGroup(group);

				// Refresh
				view.refresh();

				// Set view has changed
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error("An error occured while deleting uncertainty: {}", //$NON-NLS-1$
						group + RscTools.carriageReturn() + e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_DIALOG_UNCERTAINTY_TITLE),
						e.getMessage());
			}
		}
	}

	/**
	 * Delete an Uncertainty
	 * 
	 * @param uncertainty the uncertainty to delete
	 */
	void deleteUncertaintyAction(Uncertainty uncertainty) {
		// constructs confirm message
		String title = RscTools.getString(RscConst.MSG_UNCERTAINTY);
		String message = RscTools.getString(RscConst.MSG_UNCERTAINTY_DELETECONFIRM, view.getIdColumnText(uncertainty));

		// confirm dialog
		boolean confirm = MessageDialog.openConfirm(view.getShell(),
				RscTools.getString(RscConst.MSG_UNCERTAINTY_DELETECONFIRM_TITLE, title), message);

		if (confirm) {
			try {
				// delete
				view.getViewManager().getAppManager().getService(IUncertaintyApplication.class)
						.deleteUncertainty(uncertainty);

				// Refresh
				view.refresh();

				// Set view has changed
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error("An error occured while deleting uncertainty: {}", uncertainty //$NON-NLS-1$
						+ RscTools.carriageReturn() + e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_DIALOG_UNCERTAINTY_TITLE),
						e.getMessage());
			}
		}
	}

}
