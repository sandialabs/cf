/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.global.IGlobalApplication;
import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.dto.configuration.PIRTQuery;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.parts.ui.pirt.dialogs.PIRTQueryCriteriaDialog;
import gov.sandia.cf.parts.ui.pirt.dialogs.QoIDialog;
import gov.sandia.cf.parts.ui.pirt.dialogs.QoITagDialog;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * PIRT QoI view controller: Used to control the PIRT QoI view
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTQoIViewController extends AViewController<PIRTViewManager, PIRTQoIView> implements IQoIViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTQoIViewController.class);

	/**
	 * Instantiates a new PIRT qo I view controller.
	 *
	 * @param viewManager the view manager
	 * @param parent      the parent
	 */
	PIRTQoIViewController(PIRTViewManager viewManager, Composite parent) {
		super(viewManager);
		super.setView(new PIRTQoIView(this, parent, SWT.NONE));

		// Refresh data and Save state
		refresh();
	}

	/**
	 * Reload data.
	 */
	void reloadData() {
		// Get Model
		Model model = getViewManager().getCache().getModel();
		List<QuantityOfInterest> qoiList = new ArrayList<>();

		if (model != null) {
			// set model to table header
			getView().setTableHeaderData(model);

			getView().refreshTableHeader();

			// qoi list
			qoiList = getViewManager().getAppManager().getService(IPIRTApplication.class).getRootQoI(model);
		}

		// Get expanded elements
		Object[] elements = getView().getTreeExpandedElements();

		getView().refreshMainTable();

		getView().setTreeData(qoiList);

		// Set expanded elements
		getView().setTreeExpandedElements(elements);

	}

	/**
	 * Add a quantity of interest: open the qoi dialog to enter the new qoi
	 * information
	 */
	void addQuantityOfInterest() {

		// open qoi dialog
		QoIDialog addQoIDialog = new QoIDialog(getViewManager(), getView().getShell(), ViewMode.CREATE);
		QuantityOfInterest newQoi = addQoIDialog.openDialog();

		if (newQoi != null) {
			newQoi.setModel(getViewManager().getCache().getModel());

			try {
				// add the qoi
				QuantityOfInterest createdQoI = getViewManager().getAppManager().getService(IPIRTApplication.class)
						.addQoI(newQoi, getViewManager().getCache().getUser(), getPIRTDescriptionHeaders());

				// open the new qoi in a new tab
				getViewManager().addPage(createdQoI);
				getViewManager().openPage(createdQoI);

				// Refresh
				getView().refresh();

				// fire view change to save credibility file
				getViewManager().viewChanged();

				// Refresh
				getView().refreshViewer();

			} catch (CredibilityException e) {
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_QOIHOMEVIEW_TITLE),
						e.getMessage());
				logger.error("An error occured while creating new QoI: {}\n{}", newQoi, e.getMessage(), e); //$NON-NLS-1$
			}
		}

	}

	/**
	 * Delete a Quantity of Interest
	 * 
	 * @param element
	 * @param buttonsToDispose
	 */
	void deleteQuantityOfInterest(Object element) {

		if (element instanceof QuantityOfInterest) {

			// Get object
			QuantityOfInterest qoi = (QuantityOfInterest) element;

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
					// close qoi to delete page before deletion
					getViewManager().close(qoi);

					// delete qoi
					getViewManager().getAppManager().getService(IPIRTApplication.class).deleteQoI(qoi,
							getViewManager().getCache().getUser());

				} catch (CredibilityException e) {
					// Log
					logger.error("An error occured while deleting qoi: {}\n{}", qoi.getSymbol(), e.getMessage(), e); //$NON-NLS-1$

					// Display to user
					MessageDialog.openError(getView().getShell(),
							RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DELETECONFIRM_TITLE), e.getMessage());
				}

				// Refresh
				getView().refresh();

				// fire view change to save credibility file
				getViewManager().viewChanged();

				// Refresh
				getView().refreshViewer();
			}
		}
	}

	/**
	 * Open in a new tab the quantity of interest
	 * 
	 * @param element
	 */
	void openQuantityOfInterest(Object element) {
		if (element instanceof QuantityOfInterest) {
			getViewManager().addPage((QuantityOfInterest) element);
			getViewManager().openPage((QuantityOfInterest) element);
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
	 * Execute PIRT query
	 */
	void queryPIRT() {

		PIRTQuery pirtQuery = getView().getPIRTQuerySelected();

		if (pirtQuery != null) {

			List<String> criteriaInputList = new ArrayList<>();

			// open query criteria dialog (if there is criteria)
			if (pirtQuery.getCriteriaList() != null && !pirtQuery.getCriteriaList().isEmpty()) {
				PIRTQueryCriteriaDialog queryCriteriaDialog = new PIRTQueryCriteriaDialog(getViewManager(),
						getView().getShell(), pirtQuery.getCriteriaList());
				criteriaInputList = queryCriteriaDialog.openDialog();
			}

			// execute query and open result view
			try {
				List<Object> result = getViewManager().getAppManager().getService(IPIRTApplication.class)
						.executeQuery(pirtQuery, criteriaInputList);
				if (result == null || result.isEmpty()) {
					MessageDialog.openInformation(getView().getShell(),
							RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DLG_QUERY_TITLE),
							RscTools.getString(RscConst.MSG_QOIHOMEVIEW_DLG_QUERY_EMPTY));
				} else {
					openQueryResult(pirtQuery, result);
				}
			} catch (CredibilityException e) {
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_QOIHOMEVIEW_TITLE),
						RscTools.getString(RscConst.ERR_QOIHOMEVIEW_EXECUTING_QUERY) + e.getMessage());
				logger.error(RscTools.getString(RscConst.ERR_QOIHOMEVIEW_EXECUTING_QUERY), e);
			}
		}
	}

	/**
	 * Open query result
	 * 
	 * @param pirtQuery
	 * @param result
	 */
	void openQueryResult(PIRTQuery pirtQuery, List<Object> result) {
		getViewManager().close(pirtQuery);
		getViewManager().addPage(pirtQuery, result);
		getViewManager().openPage(pirtQuery);
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
	 * Update CF model, change header fields
	 * 
	 * @param model    the model to update
	 * @param property the model field changed
	 */
	public void updateModelHeaders(Model model, String property) {
		try {
			// persist modifications in database
			getViewManager().getAppManager().getService(IGlobalApplication.class).updateModel(model);

			// fire view chnage to save credibility file
			getViewManager().viewChanged();

			// Refresh
			getView().refresh();

		} catch (CredibilityException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_QOIHOMEVIEW_TITLE),
					RscTools.getString(RscConst.ERR_QOIHOMEVIEW_UPDATING_HEADER) + property
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
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

	/**
	 * @return the PIRT description headers from the PIRT configuration
	 */
	private List<PIRTDescriptionHeader> getPIRTDescriptionHeaders() {
		return getViewManager().getCache().getPIRTSpecification() != null
				? getViewManager().getCache().getPIRTSpecification().getHeaders()
				: List.of();
	}
}
