/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.uncertainty;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.uncertainty.IUncertaintyApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.services.genericparam.IGenericParameterService;
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
		addUncertainty(null);
	}

	/**
	 * Add Uncertainty.
	 *
	 * @param parentSelected the parent selected
	 * @param groupSelected  the group to add the uncertainty into
	 */
	void addUncertainty(Uncertainty groupSelected) {

		// Open dialog in View Mode
		UncertaintyDialog dialog = new UncertaintyDialog(view.getViewManager(), view.getShell(), null, groupSelected,
				ViewMode.CREATE);
		Uncertainty uncertainty = dialog.openDialog();

		if (uncertainty != null) {
			try {
				// Set Id
				uncertainty.setGeneratedId(view.getIdColumnText(uncertainty));

				// Create
				view.getViewManager().getAppManager().getService(IUncertaintyApplication.class).addUncertainty(
						uncertainty, view.getViewManager().getCache().getModel(),
						view.getViewManager().getCache().getUser());

				// Refresh parent
				if (groupSelected != null) {
					this.view.getViewManager().getAppManager().getService(IUncertaintyApplication.class)
							.refresh(uncertainty.getParent());

					// Expand parent
					view.expandElements(uncertainty.getParent());
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
		if (uncertainty != null && uncertainty.getValues() != null) {
			uncertainty.getValues().forEach(u -> view.getViewManager().getClientService(IGenericParameterService.class)
					.openLinkValue(u, view.getViewManager().getCache().getOpenLinkBrowserOpts()));
		}
	}

	/**
	 * View the element. Do nothing if not an Uncertainty nor an Uncertainty.
	 * 
	 * @param element the element to view
	 */
	void viewElement(Object element) {

		if (element instanceof Uncertainty) {

			// view the uncertainty
			viewUncertaintyAction((Uncertainty) element);
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
					uncertainty.getParent(), ViewMode.VIEW);
			dialog.openDialog();
		}
	}

	/**
	 * Update an element. Do nothing if not an Uncertainty.
	 * 
	 * @param element the uncertainty or uncertainty group to update
	 */
	void updateElement(Object element) {
		if (element instanceof Uncertainty) {
			// view the uncertainty
			updateUncertaintyAction((Uncertainty) element);
		}
	}

	/**
	 * Update Uncertainty
	 * 
	 * @param uncertainty the uncertainty to update
	 */
	void updateUncertaintyAction(Uncertainty uncertainty) {

		// Keep previous group
		Uncertainty previousGroup = uncertainty.getParent();

		// Open dialog in View Mode
		UncertaintyDialog dialog = new UncertaintyDialog(view.getViewManager(), view.getShell(), uncertainty,
				previousGroup, ViewMode.UPDATE);
		Uncertainty uncertaintyToUpdate = dialog.openDialog();

		// Update
		try {
			updateUncertainty(uncertaintyToUpdate);
		} catch (CredibilityException e) {
			logger.error("An error occured while updating uncertainty: {}", RscTools.carriageReturn() //$NON-NLS-1$
					+ e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_DIALOG_UNCERTAINTY_TITLE),
					e.getMessage());
		}

		if (previousGroup != null && !previousGroup.equals(uncertainty.getParent())) {
			view.getViewManager().getAppManager().getService(IUncertaintyApplication.class).refresh(previousGroup);
		}

		refreshIfChanged();
	}

	/**
	 * Update Uncertainty.
	 *
	 * @param uncertainty the uncertainty to update
	 * @throws CredibilityException the credibility exception
	 */
	Uncertainty updateUncertainty(Uncertainty uncertainty) throws CredibilityException {

		if (uncertainty == null) {
			return null;
		}

		// Update
		Uncertainty uncertaintyUpdated = view.getViewManager().getAppManager().getService(IUncertaintyApplication.class)
				.updateUncertainty(uncertainty, view.getViewManager().getCache().getUser());

		// Refresh parent
		Uncertainty newGroup = uncertaintyUpdated.getParent();
		if (newGroup != null) {
			view.getViewManager().getAppManager().getService(IUncertaintyApplication.class).refresh(newGroup);
		}

		// fire view change to save credibility file
		view.getViewManager().viewChanged();

		return uncertaintyUpdated;
	}

	/**
	 * Delete an element. Do nothing if not an Uncertainty nor an UncertaintyGroup.
	 * 
	 * @param element the uncertainty or uncertainty group to delete
	 */
	void deleteElement(Object element) {

		// delete the uncertainty
		if (element instanceof Uncertainty) {
			deleteUncertaintyAction((Uncertainty) element);
		}
	}

	/**
	 * Delete an Uncertainty
	 * 
	 * @param uncertainty the uncertainty to delete
	 */
	void deleteUncertaintyAction(Uncertainty uncertainty) {

		// constructs confirm message
		String title = null;
		String message = null;
		if (uncertainty.getParent() == null) {
			title = RscTools.getString(RscConst.MSG_UNCERTAINTY_GROUP);
			message = RscTools.getString(RscConst.MSG_UNCERTAINTY_GROUP_DELETECONFIRM, uncertainty.getName());
		} else {
			title = RscTools.getString(RscConst.MSG_UNCERTAINTY);
			message = RscTools.getString(RscConst.MSG_UNCERTAINTY_DELETECONFIRM, uncertainty.getName());
		}

		// confirm dialog
		boolean confirm = MessageDialog.openConfirm(view.getShell(),
				RscTools.getString(RscConst.MSG_UNCERTAINTY_DELETECONFIRM_TITLE, title), message);

		if (confirm) {
			try {

				// Get Parent
				Uncertainty parent = uncertainty.getParent();

				// delete
				view.getViewManager().getAppManager().getService(IUncertaintyApplication.class)
						.deleteUncertainty(uncertainty);

				// Refresh parent's children list
				if (parent != null) {
					view.getViewManager().getAppManager().getService(IUncertaintyApplication.class).refresh(parent);
				}

				// Set view has changed
				view.getViewManager().viewChanged();

				// Refresh
				view.refresh();

			} catch (CredibilityException e) {
				logger.error("An error occured while deleting uncertainty: {}", uncertainty //$NON-NLS-1$
						+ RscTools.carriageReturn() + e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_DIALOG_UNCERTAINTY_TITLE),
						e.getMessage());
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
			view.getViewManager().getAppManager().getService(IUncertaintyApplication.class).reorderAll(
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
	 * Reorder uncertainty.
	 *
	 * @param uncertainty the uncertainty
	 * @param newIndex    the new index
	 * @throws CredibilityException the credibility exception
	 */
	void reorder(Uncertainty uncertainty, int newIndex) throws CredibilityException {

		view.getViewManager().getAppManager().getService(IUncertaintyApplication.class).reorderUncertainty(uncertainty,
				newIndex, view.getViewManager().getCache().getUser());

		// fire view change to save credibility file
		view.getViewManager().viewChanged();
	}

	/**
	 * Refresh uncertainty.
	 *
	 * @param uncertainty the uncertainty
	 */
	void refreshUncertainty(Uncertainty uncertainty) {
		view.getViewManager().getAppManager().getService(IUncertaintyApplication.class).refresh(uncertainty);
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
