/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.qoiplanning;

import java.util.Date;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.parts.ui.pirt.dialogs.QoIDialog;
import gov.sandia.cf.parts.ui.pirt.dialogs.QoITagDialog;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * QoI Planning view controller: Used to control the QoI Planning view
 * 
 * @author Didier Verstraete
 *
 */
public class QoIPlanningViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(QoIPlanningViewController.class);

	/**
	 * The view
	 */
	private QoIPlanningView view;

	QoIPlanningViewController(QoIPlanningView view) {
		Assert.isNotNull(view);
		this.view = view;
	}

	/**
	 * Add QuantityOfInterest
	 */
	void addQuantityOfInterest() {
		// Open dialog in View Mode
		QoIPlanningDialog dialog = new QoIPlanningDialog(view.getViewManager(), view.getShell(), null,
				DialogMode.CREATE);
		QuantityOfInterest qoi = dialog.openDialog();

		if (qoi != null) {
			try {
				// Set User
				qoi.setModel(view.getViewManager().getCache().getModel());
				qoi.setCreationDate(new Date());

				// Create
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).addQoI(qoi,
						view.getViewManager().getCache().getUser());

				// Associate to existing group and refresh view
				view.reload();

				// Refresh
				view.refresh();

				// fire view change to save credibility file
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_QOIPLANNING_DIALOG_TITLE),
						RscTools.getString(RscConst.ERR_QOIPLANNING_DIALOG_ADD)
								+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}
		}
	}

	/**
	 * View the element. Do nothing if not a qoi.
	 * 
	 * @param element the qoi to view
	 */
	void viewElement(Object element) {

		if (element instanceof QuantityOfInterest) {
			// view the qoi
			viewQoIAction((QuantityOfInterest) element);
		}
	}

	/**
	 * View Quantity Of Interest
	 * 
	 * @param qoi the qoi to view
	 */
	void viewQoIAction(QuantityOfInterest qoi) {
		if (qoi == null) {
			logger.warn("The qoi to view is null"); //$NON-NLS-1$
		} else {
			// Open dialog in View Mode
			QoIPlanningDialog dialog = new QoIPlanningDialog(view.getViewManager(), view.getShell(), qoi,
					DialogMode.VIEW);
			dialog.openDialog();
		}
	}

	/**
	 * Update Quantity Of Interest
	 * 
	 * @param qoi the qoi to update
	 */
	void updateQoIAction(QuantityOfInterest qoi) {
		// Open dialog in View Mode
		QoIPlanningDialog dialog = new QoIPlanningDialog(view.getViewManager(), view.getShell(), qoi,
				DialogMode.UPDATE);
		QuantityOfInterest qoiToUpdate = dialog.openDialog();

		if (qoiToUpdate != null) {
			try {
				// Create
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).updateQoI(qoiToUpdate,
						view.getViewManager().getCache().getUser());

				// Associate to existing group and refresh view
				view.reload();

				// Refresh
				view.refresh();

				// fire view change to save credibility file
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_QOIPLANNING_DIALOG_TITLE),
						RscTools.getString(RscConst.ERR_QOIPLANNING_DIALOG_UPDATE)
								+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}
		}
	}

	/**
	 * Update an element. Do nothing if not a qoi.
	 * 
	 * @param element the qoi to update
	 */
	void updateElement(Object element) {
		if (element instanceof QuantityOfInterest) {
			// view the requirement
			updateQoIAction((QuantityOfInterest) element);
		}
	}

	/**
	 * Delete an element. Do nothing if not a qoi.
	 * 
	 * @param element the qoi to delete
	 */
	void deleteElement(Object element) {

		// delete the requirement
		if (element instanceof QuantityOfInterest) {
			deleteQoIAction((QuantityOfInterest) element);
		}
	}

	/**
	 * Delete a Quantity of Interest
	 * 
	 * @param qoi the qoi to delete
	 */
	void deleteQoIAction(QuantityOfInterest qoi) {

		// search for children
		String additionalInfo = RscTools.empty();
		if (qoi.getChildren() != null && !qoi.getChildren().isEmpty()) {
			additionalInfo = RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DELETECONFIRM_MSG_TAGSUFFIX);
		}

		// confirm delete qoi
		boolean confirm = MessageDialog.openConfirm(view.getShell(),
				RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DELETECONFIRM_TITLE),
				RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DELETECONFIRM_MSG, qoi.getSymbol(), additionalInfo));

		if (confirm) {
			try {
				// Remove
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).deleteQoI(qoi);

				// Refresh
				view.refresh();
				view.refreshViewer();

				// Set view has changed
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				// Log
				logger.error("An error occured while deleting qoi: {}", qoi //$NON-NLS-1$
						+ RscTools.carriageReturn() + e.getMessage(), e);
				// Display to user
				MessageDialog.openError(view.getShell(),
						RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DELETECONFIRM_TITLE), e.getMessage());
			}
		}
	}

	/**
	 * Duplicate the quantity of interest
	 * 
	 * @param element the qoi to duplicate
	 */
	void duplicateQuantityOfInterest(Object element) {

		if (element instanceof QuantityOfInterest) {
			// Create QoI
			QuantityOfInterest qoiSelected = (QuantityOfInterest) element;

			// confirm tag dialog
			boolean confirm = MessageDialog.openConfirm(view.getShell(),
					RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DUPLICATECONFIRM_TITLE),
					RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DUPLICATECONFIRM_MSG, qoiSelected.getSymbol()));

			if (confirm) {
				try {

					QoIDialog qoiDialog = new QoIDialog(view.getViewManager(), view.getShell(), DialogMode.COPY,
							qoiSelected);
					QuantityOfInterest duplicatedQoi = qoiDialog.openDialog();

					// Not canceled
					if (null != duplicatedQoi) {

						// Duplicate qoi in database
						view.getViewManager().getAppManager().getService(IPIRTApplication.class)
								.duplicateQoI(qoiSelected, duplicatedQoi, view.getViewManager().getCache().getUser());

						// confirm tag success
						MessageDialog.openInformation(view.getShell(),
								RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DUPLICATECONFIRM_TITLE),
								RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DUPLICATECONFIRM_SUCCESS));

						// Refresh
						view.refresh();

						// fire view change to save credibility file
						view.getViewManager().viewChanged();

						// Refresh
						view.refreshViewer();
					}

				} catch (CredibilityException e) {
					logger.error("An error occured while duplicating qoi: {}\n{}", qoiSelected, e.getMessage(), e); //$NON-NLS-1$
					MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_QOIHOMEVIEW_DUPLICATING),
							e.getMessage());
				}
			}
		}
	}

	/**
	 * Do tag action. Save and copy the current QoI state: Phenomenon Groups,
	 * Phenomena, Criteria
	 * 
	 * @param element the element to tag
	 */
	void doTagAction(Object element) {

		if (element == null) {
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TITLE),
					RscTools.getString(RscConst.ERR_QOIHOMEVIEW_TAGGING_QOINULL));
		} else if (!(element instanceof QuantityOfInterest)) {
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TAGCONFIRM_TITLE),
					RscTools.getString(RscConst.ERR_QOIHOMEVIEW_TAGGING_NOTQOI));
		} else {

			QuantityOfInterest qoiSelected = (QuantityOfInterest) element;

			// confirm tag dialog
			boolean confirm = MessageDialog.openConfirm(view.getShell(),
					RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TAGCONFIRM_TITLE),
					RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TAGCONFIRM_QUESTION, qoiSelected.getSymbol()));

			if (confirm) {
				try {
					// tag dialog
					QoITagDialog tagQoIDialog = new QoITagDialog(view.getViewManager(), view.getShell(), qoiSelected);
					String tagDescription = tagQoIDialog.openDialog();

					if (tagQoIDialog.getReturnCode() == Window.OK) {

						// tag qoi in database
						view.getViewManager().getAppManager().getService(IPIRTApplication.class).tagQoI(qoiSelected,
								tagDescription, view.getViewManager().getCache().getUser());

						// fire view change to save credibility file
						view.getViewManager().viewChanged();

						// refresh view
						view.refresh();

						// Refresh
						view.refreshViewer();

						// confirm tag success
						MessageDialog.openInformation(view.getShell(),
								RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TAGCONFIRM_TITLE),
								RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TAGCONFIRM_SUCCESS));
					}

				} catch (CredibilityException e) {
					logger.error("An error occured while tagging qoi: {}\n{}", qoiSelected, e.getMessage(), e); //$NON-NLS-1$
					MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TAGGING),
							e.getMessage());
				}
			}
		}
	}
}
