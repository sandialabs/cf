/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.decision;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.decision.IDecisionApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.services.genericparam.IGenericParameterService;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Decision view controller: Used to control the Decision view
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionViewController extends AViewController<DecisionViewManager, DecisionView> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(DecisionViewController.class);

	/**
	 * Instantiates a new decision view controller.
	 *
	 * @param viewManager the view manager
	 */
	DecisionViewController(DecisionViewManager viewManager) {
		super(viewManager);
		super.setView(new DecisionView(this, SWT.NONE));
	}

	/**
	 * Reload data.
	 */
	void reloadData() {

		logger.debug("Reload Decision view"); //$NON-NLS-1$

		// Get Model
		Model model = getViewManager().getCache().getModel();
		List<Decision> decisionList = new ArrayList<>();

		// Get data
		if (model != null) {
			// Get list of decision
			decisionList = getViewManager().getAppManager().getService(IDecisionApplication.class)
					.getDecisionRootByModel(model);

			// reload decision spec
			getViewManager().getCache().reloadDecisionSpecification();
		}

		// Get expanded elements
		Object[] elements = getView().getTreeExpandedElements();

		// Refresh the table
		getView().refreshMainTable();

		// Set input
		getView().setTreeData(decisionList);

		// Set expanded elements
		getView().setTreeExpandedElements(elements);
	}

	/**
	 * Add Decision
	 */
	void addDecision() {
		addDecision(null);
	}

	/**
	 * Add Decision with parent
	 * 
	 * @param parent the parent decision to add the new decision under. If null, the
	 *               decision will have no parents.
	 */
	void addDecision(Decision parent) {

		// Open dialog in View Mode
		DecisionDialog dialog = new DecisionDialog(getViewManager(), getView().getShell(), null, parent,
				ViewMode.CREATE);
		Decision decision = dialog.openDialog();

		if (decision != null) {
			try {
				// Set Id
				decision.setGeneratedId(getView().getIdColumnText(decision));

				// Create
				getViewManager().getAppManager().getService(IDecisionApplication.class).addDecision(decision,
						getViewManager().getCache().getModel(), getViewManager().getCache().getUser());

				// Refresh parent
				if (parent != null) {
					getViewManager().getAppManager().getService(IDecisionApplication.class).refresh(parent);

					// Expand parent
					getView().expandElements(parent);
				}

				// Fire view change to save credibility file
				getViewManager().viewChanged();

				// Refresh
				getView().refresh();
			} catch (CredibilityException e) {
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_DIALOG_DECISION_TITLE),
						RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}
		}
	}

	/**
	 * Open all decision values with type LINK for this decision
	 * 
	 * @param decision the decision to open values for
	 */
	void openAllDecisionValues(Decision decision) {
		if (decision != null && decision.getDecisionList() != null) {
			decision.getDecisionList().forEach(u -> getViewManager().getClientService(IGenericParameterService.class)
					.openLinkValue(u, getViewManager().getCache().getOpenLinkBrowserOpts()));
		}
	}

	/**
	 * View the element. Do nothing if it is not a decision.
	 * 
	 * @param element the decision to view
	 */
	void viewElement(Object element) {

		if (element instanceof Decision) {
			// view the decision group
			viewDecisionAction((Decision) element);
		}
	}

	/**
	 * Add Decision
	 * 
	 * @param decision the decision to add
	 */
	void viewDecisionAction(Decision decision) {
		if (decision == null) {
			logger.warn("The decision to view is null"); //$NON-NLS-1$
		} else {
			// Open dialog in View Mode
			DecisionDialog dialog = new DecisionDialog(getViewManager(), getView().getShell(), decision,
					decision.getParent(), ViewMode.VIEW);
			dialog.openDialog();
		}
	}

	/**
	 * Update an element. Do nothing if it is not a decision.
	 * 
	 * @param element the decision to update
	 */
	void updateElement(Object element) {
		if (element instanceof Decision) {
			// view the decision
			updateDecisionAction((Decision) element);
		}
	}

	/**
	 * Update Decision
	 * 
	 * @param decision the decision to update
	 */
	void updateDecisionAction(Decision decision) {

		// Keep previous group
		Decision previousGroup = decision.getParent();

		// Open dialog in View Mode
		DecisionDialog dialog = new DecisionDialog(getViewManager(), getView().getShell(), decision, previousGroup,
				ViewMode.UPDATE);
		Decision decisionToUpdate = dialog.openDialog();

		// Update
		try {
			updateDecision(decisionToUpdate);
		} catch (CredibilityException e) {
			logger.error("An error occured while updating decision: {}", RscTools.carriageReturn() //$NON-NLS-1$
					+ e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_DIALOG_DECISION_TITLE),
					e.getMessage());
		}

		if (previousGroup != null && !previousGroup.equals(decision.getParent())) {
			getViewManager().getAppManager().getService(IDecisionApplication.class).refresh(previousGroup);
		}

		refreshIfChanged();
	}

	/**
	 * Update Decision.
	 *
	 * @param decision the decision to update
	 * @return
	 * @throws CredibilityException the credibility exception
	 */
	Decision updateDecision(Decision decision) throws CredibilityException {

		if (decision == null) {
			return null;
		}
		// Update
		Decision decisionUpdated = getViewManager().getAppManager().getService(IDecisionApplication.class)
				.updateDecision(decision, getViewManager().getCache().getUser());

		// Refresh parent
		Decision newGroup = decisionUpdated.getParent();
		if (newGroup != null) {
			getViewManager().getAppManager().getService(IDecisionApplication.class).refresh(newGroup);
		}

		// fire view change to save credibility file
		getViewManager().viewChanged();

		return decisionUpdated;
	}

	/**
	 * Delete an element. Do nothing if it is not a decision.
	 * 
	 * @param element the decision to delete with its children
	 */
	void deleteElement(Object element) {

		// delete the decision
		if (element instanceof Decision) {
			deleteDecisionAction((Decision) element);
		}
	}

	/**
	 * Delete a decision
	 * 
	 * @param decision the decision to delete with its children
	 */
	void deleteDecisionAction(Decision decision) {

		// constructs confirm message
		String title = null;
		String message = null;
		if (decision.getParent() == null) {
			title = RscTools.getString(RscConst.MSG_DECISION_GROUP);
			message = RscTools.getString(RscConst.MSG_DECISION_GROUP_DELETECONFIRM, decision.getTitle());
		} else {
			title = RscTools.getString(RscConst.MSG_DECISION);
			message = RscTools.getString(RscConst.MSG_DECISION_DELETECONFIRM, decision.getTitle());
		}

		// confirm dialog
		boolean confirm = MessageDialog.openConfirm(getView().getShell(),
				RscTools.getString(RscConst.MSG_DECISION_DELETECONFIRM_TITLE, title), message);

		if (confirm) {
			try {
				// Get Parent
				Decision parent = decision.getParent();

				// Remove
				getViewManager().getAppManager().getService(IDecisionApplication.class).deleteDecision(decision,
						getViewManager().getCache().getUser());

				// Refresh parent's children list
				if (parent != null) {
					getViewManager().getAppManager().getService(IDecisionApplication.class).refresh(parent);
				}

				// Fire view change to save credibility file
				getViewManager().viewChanged();

				// Refresh
				getView().refresh();

			} catch (CredibilityException e) {
				logger.error("An error occured while deleting decision: {}", decision //$NON-NLS-1$
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
			getViewManager().getAppManager().getService(IDecisionApplication.class)
					.reorderAll(getViewManager().getCache().getModel(), getViewManager().getCache().getUser());

			// fire view change to save credibility file
			getViewManager().viewChanged();

		} catch (CredibilityException e) {
			logger.error("Impossible to reorder all uncertainties: {}", e.getMessage(), e);//$NON-NLS-1$
			return false;
		}

		return true;
	}

	/**
	 * Reorder decision.
	 *
	 * @param decision the decision
	 * @param newIndex the new index
	 * @throws CredibilityException the credibility exception
	 */
	void reorder(Decision decision, int newIndex) throws CredibilityException {

		getViewManager().getAppManager().getService(IDecisionApplication.class).reorderDecision(decision, newIndex,
				getViewManager().getCache().getUser());

		// fire view change to save credibility file
		getViewManager().viewChanged();
	}

	/**
	 * Refresh decision.
	 *
	 * @param decision the decision
	 */
	void refreshDecision(Decision decision) {
		getViewManager().getAppManager().getService(IDecisionApplication.class).refresh(decision);
	}

	/**
	 * Refresh if changed.
	 */
	public void refreshIfChanged() {

		// Refresh
		if (getViewManager().isDirty()) {
			getView().refresh();
		}
	}
}
