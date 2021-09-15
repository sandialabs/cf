/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.decision;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IDecisionApplication;
import gov.sandia.cf.application.IGenericParameterApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Decision view controller: Used to control the Decision view
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(DecisionViewController.class);

	/**
	 * The decision view
	 */
	private DecisionView view;

	DecisionViewController(DecisionView view) {
		Assert.isNotNull(view);
		this.view = view;
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
		DecisionDialog dialog = new DecisionDialog(view.getViewManager(), view.getShell(), null, parent,
				DialogMode.CREATE);
		Decision decision = dialog.openDialog();

		if (decision != null) {
			try {
				// Set User
				decision.setGeneratedId(view.getIdColumnText(decision));

				// Create
				view.getViewManager().getAppManager().getService(IDecisionApplication.class).addDecision(decision,
						view.getViewManager().getCache().getModel(), view.getViewManager().getCache().getUser());

				// Refresh parent
				if (parent != null) {
					view.getViewManager().getAppManager().getService(IDecisionApplication.class).refresh(parent);

					// Expand parent
					view.expandElements(parent);
				}

				// Fire view change to save credibility file
				view.getViewManager().viewChanged();

				// Refresh
				view.refresh();
			} catch (CredibilityException e) {
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_DIALOG_DECISION_TITLE),
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
			decision.getDecisionList().forEach(u -> view.getViewManager().getAppManager()
					.getService(IGenericParameterApplication.class).openLinkValue(u));
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
			DecisionDialog dialog = new DecisionDialog(view.getViewManager(), view.getShell(), decision,
					decision.getParent(), DialogMode.VIEW);
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

		// Open dialog in View Mode
		Decision oldParent = decision.getParent();
		DecisionDialog dialog = new DecisionDialog(view.getViewManager(), view.getShell(), decision, oldParent,
				DialogMode.UPDATE);
		Decision decisionToUpdate = dialog.openDialog();

		if (decisionToUpdate != null) {
			try {
				// Create
				Decision decisionUpdated = view.getViewManager().getAppManager().getService(IDecisionApplication.class)
						.updateDecision(decisionToUpdate, view.getViewManager().getCache().getUser());

				// Refresh parent
				Decision newParent = decisionUpdated.getParent();
				if (newParent != null) {
					view.getViewManager().getAppManager().getService(IDecisionApplication.class).refresh(newParent);
				}
				if (oldParent != null && !oldParent.equals(newParent)) {
					view.getViewManager().getAppManager().getService(IDecisionApplication.class).refresh(oldParent);
				}

				// Fire view change to save credibility file
				view.getViewManager().viewChanged();

				// Refresh
				view.refresh();
			} catch (CredibilityException e) {
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_DIALOG_DECISION_TITLE),
						RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}
		}
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
		boolean confirm = MessageDialog.openConfirm(view.getShell(),
				RscTools.getString(RscConst.MSG_DECISION_DELETECONFIRM_TITLE, title), message);

		if (confirm) {
			try {
				// Get Parent
				Decision parent = decision.getParent();

				// Remove
				view.getViewManager().getAppManager().getService(IDecisionApplication.class).deleteDecision(decision);

				// Refresh parent's children list
				if (parent != null) {
					view.getViewManager().getAppManager().getService(IDecisionApplication.class).refresh(parent);
				}

				// Fire view change to save credibility file
				view.getViewManager().viewChanged();

				// Refresh
				view.refresh();

			} catch (CredibilityException e) {
				logger.error("An error occured while deleting decision: {}", decision //$NON-NLS-1$
						+ RscTools.carriageReturn() + e.getMessage(), e);
			}
		}
	}
}
