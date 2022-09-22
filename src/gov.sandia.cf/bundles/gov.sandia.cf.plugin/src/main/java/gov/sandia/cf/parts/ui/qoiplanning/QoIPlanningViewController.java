/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.qoiplanning;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.parts.ui.pirt.IQoIViewController;
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
public class QoIPlanningViewController extends AViewController<QoIPlanningViewManager, QoIPlanningView>
		implements IQoIViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(QoIPlanningViewController.class);

	/**
	 * Instantiates a new qo I planning view controller.
	 *
	 * @param viewManager the view manager
	 */
	QoIPlanningViewController(QoIPlanningViewManager viewManager) {
		super(viewManager);
		super.setView(new QoIPlanningView(this, SWT.NONE));

		// Refresh
		refresh();
	}

	void reloadData() {
		// Get Model
		Model model = getViewManager().getCache().getModel();
		List<QuantityOfInterest> qoiList = new ArrayList<>();

		// Get data
		if (model != null) {
			// Get list of qoi
			qoiList = this.getViewManager().getAppManager().getService(IPIRTApplication.class).getRootQoI(model);

			// reload qoi planning specification
			getViewManager().getCache().reloadQoIPlanningSpecification();
		}

		// Get expanded elements
		Object[] elements = getView().getTreeExpandedElements();

		// Refresh the table
		getView().refreshMainTable();

		// Set input
		getView().setTreeData(qoiList);

		// Set expanded elements
		getView().setExpandedElements(elements);
	}

	/**
	 * Add QuantityOfInterest
	 */
	void addQuantityOfInterest() {
		// Open dialog in View Mode
		QoIPlanningDialog dialog = new QoIPlanningDialog(getViewManager(), getView().getShell(), null, ViewMode.CREATE);
		QuantityOfInterest qoi = dialog.openDialog();

		if (qoi != null) {
			try {
				// Set User
				qoi.setModel(getViewManager().getCache().getModel());
				qoi.setCreationDate(new Date());

				// Create
				getViewManager().getAppManager().getService(IPIRTApplication.class).addQoI(qoi,
						getViewManager().getCache().getUser());

				// Associate to existing group and refresh view
				getView().reload();

				// Refresh
				getView().refresh();

				// fire view change to save credibility file
				getViewManager().viewChanged();

			} catch (CredibilityException e) {
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_QOIPLANNING_DIALOG_TITLE),
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
			QoIPlanningDialog dialog = new QoIPlanningDialog(getViewManager(), getView().getShell(), qoi,
					ViewMode.VIEW);
			dialog.openDialog();
		}
	}

	/**
	 * Update qoi
	 * 
	 * @param qoi the qoi to update
	 */
	void updateQoIAction(QuantityOfInterest qoi) {

		// Open dialog in View Mode
		QoIPlanningDialog dialog = new QoIPlanningDialog(getViewManager(), getView().getShell(), qoi, ViewMode.UPDATE);
		QuantityOfInterest qoiToUpdate = dialog.openDialog();

		// Update
		try {
			updateQoI(qoiToUpdate);
		} catch (CredibilityException e) {
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_QOIPLANNING_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_QOIPLANNING_DIALOG_UPDATE)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}

		refreshIfChanged();
	}

	/**
	 * Update qoi.
	 *
	 * @param qoi the qoi to update
	 * @return the quantity of interest
	 * @throws CredibilityException the credibility exception
	 */
	QuantityOfInterest updateQoI(QuantityOfInterest qoi) throws CredibilityException {

		if (qoi == null) {
			return null;
		}

		// Update
		QuantityOfInterest qoiUpdated = getViewManager().getAppManager().getService(IPIRTApplication.class)
				.updateQoI(qoi, getViewManager().getCache().getUser());

		// Refresh parent
		QuantityOfInterest newGroup = qoiUpdated.getParent();
		if (newGroup != null) {
			getViewManager().getAppManager().getService(IPIRTApplication.class).refresh(newGroup);
		}

		// fire view change to save credibility file
		getViewManager().viewChanged();

		return qoiUpdated;
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
		boolean confirm = MessageDialog.openConfirm(getView().getShell(),
				RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DELETECONFIRM_TITLE),
				RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DELETECONFIRM_MSG, qoi.getSymbol(), additionalInfo));

		if (confirm) {
			try {
				// Remove
				getViewManager().getAppManager().getService(IPIRTApplication.class).deleteQoI(qoi,
						getViewManager().getCache().getUser());

				// Refresh
				getView().refresh();
				getView().refreshViewer();

				// Set view has changed
				getViewManager().viewChanged();

			} catch (CredibilityException e) {
				// Log
				logger.error("An error occured while deleting qoi: {}", qoi //$NON-NLS-1$
						+ RscTools.carriageReturn() + e.getMessage(), e);
				// Display to user
				MessageDialog.openError(getView().getShell(),
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
			boolean confirm = MessageDialog.openConfirm(getView().getShell(),
					RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DUPLICATECONFIRM_TITLE),
					RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DUPLICATECONFIRM_MSG, qoiSelected.getSymbol()));

			if (confirm) {
				try {

					QoIDialog qoiDialog = new QoIDialog(getViewManager(), getView().getShell(), ViewMode.COPY,
							qoiSelected);
					QuantityOfInterest duplicatedQoi = qoiDialog.openDialog();

					// Not canceled
					if (null != duplicatedQoi) {

						// Duplicate qoi in database
						getViewManager().getAppManager().getService(IPIRTApplication.class).duplicateQoI(qoiSelected,
								duplicatedQoi, getViewManager().getCache().getUser());

						// confirm tag success
						MessageDialog.openInformation(getView().getShell(),
								RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DUPLICATECONFIRM_TITLE),
								RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DUPLICATECONFIRM_SUCCESS));

						// Refresh
						getView().refresh();

						// fire view change to save credibility file
						getViewManager().viewChanged();

						// Refresh
						getView().refreshViewer();
					}

				} catch (CredibilityException e) {
					logger.error("An error occured while duplicating qoi: {}\n{}", qoiSelected, e.getMessage(), e); //$NON-NLS-1$
					MessageDialog.openError(getView().getShell(),
							RscTools.getString(RscConst.ERR_QOIHOMEVIEW_DUPLICATING), e.getMessage());
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
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TITLE),
					RscTools.getString(RscConst.ERR_QOIHOMEVIEW_TAGGING_QOINULL));
		} else if (!(element instanceof QuantityOfInterest)) {
			MessageDialog.openWarning(getView().getShell(),
					RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TAGCONFIRM_TITLE),
					RscTools.getString(RscConst.ERR_QOIHOMEVIEW_TAGGING_NOTQOI));
		} else {

			QuantityOfInterest qoiSelected = (QuantityOfInterest) element;

			// confirm tag dialog
			boolean confirm = MessageDialog.openConfirm(getView().getShell(),
					RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TAGCONFIRM_TITLE),
					RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TAGCONFIRM_QUESTION, qoiSelected.getSymbol()));

			if (confirm) {
				try {
					// tag dialog
					QoITagDialog tagQoIDialog = new QoITagDialog(getViewManager(), getView().getShell(), qoiSelected);
					String tagDescription = tagQoIDialog.openDialog();

					if (tagQoIDialog.getReturnCode() == Window.OK) {

						// tag qoi in database
						getViewManager().getAppManager().getService(IPIRTApplication.class).tagQoI(qoiSelected,
								tagDescription, getViewManager().getCache().getUser());

						// fire view change to save credibility file
						getViewManager().viewChanged();

						// refresh view
						getView().refresh();

						// Refresh
						getView().refreshViewer();

						// confirm tag success
						MessageDialog.openInformation(getView().getShell(),
								RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TAGCONFIRM_TITLE),
								RscTools.getString(RscConst.MSG_QOIHOMEVIEW_TAGCONFIRM_SUCCESS));
					}

				} catch (CredibilityException e) {
					logger.error("An error occured while tagging qoi: {}\n{}", qoiSelected, e.getMessage(), e); //$NON-NLS-1$
					MessageDialog.openError(getView().getShell(),
							RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TAGGING), e.getMessage());
				}
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
			getViewManager().getAppManager().getService(IPIRTApplication.class).reorderAllQuantityOfInterest(
					getViewManager().getCache().getModel(), getViewManager().getCache().getUser());

			// fire view change to save credibility file
			getViewManager().viewChanged();

		} catch (CredibilityException e) {
			logger.error("Impossible to reorder all qois: {}", e.getMessage(), e);//$NON-NLS-1$
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reorder(QuantityOfInterest qoi, int newIndex) throws CredibilityException {

		getViewManager().getAppManager().getService(IPIRTApplication.class).reorderQuantityOfInterest(qoi, newIndex,
				null);

		// fire view change to save credibility file
		getViewManager().viewChanged();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshIfChanged() {

		// Refresh
		if (getViewManager().isDirty()) {
			getView().refresh();
		}
	}
}
