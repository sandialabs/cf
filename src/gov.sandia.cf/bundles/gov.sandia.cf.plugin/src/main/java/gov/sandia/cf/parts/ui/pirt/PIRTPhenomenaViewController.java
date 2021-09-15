/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPIRTApplication;
import gov.sandia.cf.application.configuration.pirt.PIRTSpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.parts.model.QoIHeaderParts;
import gov.sandia.cf.parts.ui.pirt.dialogs.PIRTPhenomenonDialog;
import gov.sandia.cf.parts.ui.pirt.dialogs.PIRTPhenomenonGroupDialog;
import gov.sandia.cf.parts.ui.pirt.dialogs.QoITagDialog;
import gov.sandia.cf.parts.ui.pirt.editors.PIRTPhenTableHeaderDescriptor;
import gov.sandia.cf.parts.viewer.TableHeader;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * PIRT Phenomena view controller: Used to control the PIRT Phenomena view
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTPhenomenaViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTPhenomenaViewController.class);

	/**
	 * The view
	 */
	private PIRTPhenomenaView view;

	PIRTPhenomenaViewController(PIRTPhenomenaView view) {
		Assert.isNotNull(view);
		this.view = view;
	}

	/**
	 *
	 * Get pirt configuration
	 * <p>
	 * TODO: TO BE UPDATED. Controller has to store itself the PIRT specification
	 * instead of getting it from the view.
	 * 
	 * @return the pirt specification
	 */
	public PIRTSpecification getPirtConfiguration() {
		return view.getPirtConfiguration();
	}

	/**
	 * Refreshes the parent view to update qoi modifications
	 */
	public void refreshQoIView() {
		view.getViewManager().refreshQOIView();
		view.getViewManager().reloadTabName(view.getQoISelected());
		if (view.getQoISelected() != null) {
			view.updateTableHeaderBarName(view.getQoISelected().getSymbol());
		}
	}

	/**
	 * Delete a tree element
	 * 
	 * @param element the element to delete
	 */
	void deleteElement(Object element) {
		// Initialize
		boolean refresh = false;
		if (element instanceof PhenomenonGroup) {
			deletePhenomenonGroup((PhenomenonGroup) element);
			refresh = true;
		} else if (element instanceof Phenomenon) {
			deletePhenomenon((Phenomenon) element);
			refresh = true;
		}

		// Refresh
		if (refresh) {
			// Refresh
			view.refresh();

			// fire view change to save credibility file
			view.getViewManager().viewChanged();
		}
	}

	/**
	 * Delete a Phenomenon Group
	 * 
	 * @param phenomenonGroup the group to delete
	 */
	void deletePhenomenonGroup(PhenomenonGroup phenomenonGroup) {
		// constructs confirm message
		String title = RscTools.getString(RscConst.MSG_PIRT_MODEL_PHENGROUP);
		String message = RscTools.getString(RscConst.MSG_PHENOMENAVIEW_DELETECONFIRM_QUESTIONGROUP,
				phenomenonGroup.getName());

		// confirm dialog
		boolean confirm = MessageDialog.openConfirm(view.getShell(),
				RscTools.getString(RscConst.MSG_PHENOMENAVIEW_DELETECONFIRM_TITLE, title), message);

		if (confirm) {
			try {
				view.getViewManager().getAppManager().getService(IPIRTApplication.class)
						.deletePhenomenonGroup(phenomenonGroup);
				List<PhenomenonGroup> data = view.getPhenomenonGroups();
				data.remove(phenomenonGroup);

			} catch (CredibilityException e) {
				logger.error("An error occured while deleting phenomenon: {}", //$NON-NLS-1$
						phenomenonGroup + RscTools.carriageReturn() + e.getMessage(), e);
			}
		}
	}

	/**
	 * Delete a Phenomenon
	 * 
	 * @param element the element to delete
	 */
	void deletePhenomenon(Phenomenon phenomenon) {
		// constructs confirm message
		String title = RscTools.getString(RscConst.MSG_PIRT_MODEL_PHEN);
		String message = RscTools.getString(RscConst.MSG_PHENOMENAVIEW_DELETECONFIRM_QUESTIONPHENOMENON,
				phenomenon.getName());

		// confirm dialog
		boolean confirm = MessageDialog.openConfirm(view.getShell(),
				RscTools.getString(RscConst.MSG_PHENOMENAVIEW_DELETECONFIRM_TITLE, title), message);

		if (confirm) {
			try {
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).deletePhenomenon(phenomenon);
				List<PhenomenonGroup> data = (view.getPhenomenonGroups());
				int indexOfPhenomenonGroup = data.indexOf(phenomenon.getPhenomenonGroup());
				data.get(indexOfPhenomenonGroup).getPhenomenonList().remove(phenomenon);
			} catch (CredibilityException e) {
				logger.error("An error occured while deleting phenomenon: {}", phenomenon //$NON-NLS-1$
						+ RscTools.carriageReturn() + e.getMessage(), e);
			}
		}
	}

	/**
	 * Add a new phenomenon group: Open the new phenomenon group wizard and set all
	 * attributes needed. Persist entered datas in database
	 */
	public void addPhenomenonGroupAction() {

		if (view.getQoISelected() != null) {

			// open creation dialog
			PIRTPhenomenonGroupDialog dialog = new PIRTPhenomenonGroupDialog(view.getViewManager(), view.getShell(),
					view.getQoISelected(), null, DialogMode.CREATE);
			PhenomenonGroup groupToCreate = dialog.openDialog();

			// create group
			if (groupToCreate != null) {
				try {

					// Set id
					groupToCreate.setIdLabel(view.getIdColumnTextPhenomenonGroup(groupToCreate));

					// set qoi
					groupToCreate.setQoi(view.getQoISelected());

					// create
					view.getViewManager().getAppManager().getService(IPIRTApplication.class)
							.addPhenomenonGroup(groupToCreate);

					// refresh qoi
					view.getViewManager().getAppManager().getService(IPIRTApplication.class)
							.refresh(view.getQoISelected());

					// fire view change to save credibility file
					view.getViewManager().viewChanged();

					// reload
					view.refresh();

				} catch (CredibilityException e) {
					MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
							RscTools.getString(RscConst.ERR_PHENOMENAVIEW_ADDING_PHENGROUP)
									+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
				}
			}
		}
	}

	/**
	 * Add a new phenomenon: Open the new phenomenon wizard and set all attributes
	 * needed and criterion. Persist entered data in database
	 * 
	 * @param groupSelected the phenomenon group to associate the phenomenon to
	 *                      create
	 */
	public void addPhenomenonAction(PhenomenonGroup groupSelected) {

		// Get tree data
		List<PhenomenonGroup> phenGroups = view.getPhenomenonGroups();

		if (phenGroups == null) {
			MessageDialog.openWarning(view.getShell(),
					RscTools.getString(RscConst.WRN_PHENOMENAVIEW_ADDING_PHENOMENON_GROUPNOTPRESENT_TITLE),
					RscTools.getString(RscConst.WRN_PHENOMENAVIEW_ADDING_PHENOMENON_GROUPNOTPRESENT_DESC));
			return;
		}

		// open creation dialog
		PIRTPhenomenonDialog dialog = new PIRTPhenomenonDialog(view.getViewManager(), view.getShell(), phenGroups, null,
				groupSelected, DialogMode.CREATE);
		Phenomenon phenomenonToCreate = dialog.openDialog();

		if (phenomenonToCreate == null) {
			return;
		}

		// persist phenomenon in database
		try {
			// Set id
			phenomenonToCreate.setIdLabel(view.getIdColumnTextPhenomenon(phenomenonToCreate));

			// create phenomenon
			view.getViewManager().getAppManager().getService(IPIRTApplication.class).addPhenomenon(phenomenonToCreate);

			// Refresh parent
			if (groupSelected != null) {
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).refresh(groupSelected);

				// Expand parent
				view.expandElements(groupSelected);
			}

			// fire view change to save credibility file
			view.getViewManager().viewChanged();

			// Refresh
			view.refresh();

		} catch (CredibilityException e) {
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
					RscTools.getString(RscConst.ERR_PHENOMENAVIEW_ADDING_PHENOMENON)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * 
	 * View the cell element
	 * 
	 * @param element the element to view
	 */
	void viewElement(Object element) {

		if (element != null) {
			if (element instanceof PhenomenonGroup) {

				// view the phenomenon group
				viewPhenomenonGroupAction((PhenomenonGroup) element);

			} else if (element instanceof Phenomenon) {

				// view the phenomenon
				viewPhenomenonAction((Phenomenon) element);
			}
		}
	}

	/**
	 * View the phenomenon group in parameter by opening a popup dialog
	 * 
	 * @param group the group to view
	 */
	public void viewPhenomenonGroupAction(PhenomenonGroup group) {

		if (group == null) {
			logger.warn("The phenomenon group to update is null"); //$NON-NLS-1$
		} else {

			// open update dialog
			PIRTPhenomenonGroupDialog dialog = new PIRTPhenomenonGroupDialog(view.getViewManager(), view.getShell(),
					view.getQoISelected(), group, DialogMode.VIEW);
			dialog.openDialog();
		}
	}

	/**
	 * View the phenomenon in parameter by opening a popup dialog
	 * 
	 * @param phenomenon the phenomenon to view
	 */
	public void viewPhenomenonAction(Phenomenon phenomenon) {

		if (phenomenon == null) {
			logger.warn("The phenomenon to update is null"); //$NON-NLS-1$
		} else {

			// open view dialog
			PIRTPhenomenonDialog dialog = new PIRTPhenomenonDialog(view.getViewManager(), view.getShell(),
					view.getQoISelected().getPhenomenonGroupList(), phenomenon, null, DialogMode.VIEW);
			dialog.openDialog();
		}
	}

	/**
	 * 
	 * Update the cell element
	 * 
	 * @param element the element to update
	 */
	void updateElement(Object element) {

		if (element != null) {
			if (element instanceof PhenomenonGroup) {

				// update the phenomenon group
				updatePhenomenonGroupAction((PhenomenonGroup) element);

			} else if (element instanceof Phenomenon) {

				// update the phenomenon
				updatePhenomenonAction((Phenomenon) element);
			}
		}
	}

	/**
	 * Update the phenomenon group in parameter by opening a popup dialog to edit it
	 * 
	 * @param group the group to update
	 */
	public void updatePhenomenonGroupAction(PhenomenonGroup group) {

		// open update dialog
		PIRTPhenomenonGroupDialog dialog = new PIRTPhenomenonGroupDialog(view.getViewManager(), view.getShell(),
				view.getQoISelected(), group, DialogMode.UPDATE);
		PhenomenonGroup groupUpdated = dialog.openDialog();

		if (groupUpdated != null) {
			try {
				// create
				view.getViewManager().getAppManager().getService(IPIRTApplication.class)
						.updatePhenomenonGroup(groupUpdated);

				// Refresh
				view.refresh();

				// fire view change to save credibility file
				view.getViewManager().viewChanged();
			} catch (CredibilityException e) {
				logger.error("An error occured while updating phenomenon group: {}", RscTools.carriageReturn() //$NON-NLS-1$
						+ e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
						RscTools.getString(RscConst.ERR_PHENOMENAVIEW_UPDATING_PHENGROUP) + e.getMessage());
			}
		}
	}

	/**
	 * Update the phenomenon in parameter by opening a popup dialog to edit it
	 * 
	 * @param phenomenon the phenomenon to update
	 */
	public void updatePhenomenonAction(Phenomenon phenomenon) {

		// open update dialog
		Phenomenon phenomenonToUpdate = phenomenon;
		PhenomenonGroup previousGroup = phenomenonToUpdate.getPhenomenonGroup();
		PIRTPhenomenonDialog dialog = new PIRTPhenomenonDialog(view.getViewManager(), view.getShell(),
				view.getQoISelected().getPhenomenonGroupList(), phenomenonToUpdate, null, DialogMode.UPDATE);
		Phenomenon phenomenonUpdated = dialog.openDialog();

		if (phenomenonUpdated != null) {
			updatePhenomenon(phenomenonUpdated);

			PhenomenonGroup newGroup = phenomenonUpdated.getPhenomenonGroup();

			// update phenomenon group lists
			if (previousGroup != null && newGroup != null) {
				previousGroup.getPhenomenonList().remove(phenomenonToUpdate);
				newGroup.getPhenomenonList().add(phenomenonUpdated);
			}

			// reload data model
			view.refresh();

			// fire view change to save credibility file
			view.getViewManager().viewChanged();
		}
	}

	/**
	 * Update Phenomenon entity
	 * 
	 * @param phenomenon the phenomenon to update
	 */
	public void updatePhenomenon(Phenomenon phenomenon) {
		try {
			view.getViewManager().getAppManager().getService(IPIRTApplication.class).updatePhenomenon(phenomenon);

			// Refresh view
			view.getViewManager().viewChanged();
			view.refresh();

		} catch (CredibilityException e) {
			logger.error("An error occured while updating phenomenon: {}", RscTools.carriageReturn() //$NON-NLS-1$
					+ e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
					RscTools.getString(RscConst.ERR_PHENOMENAVIEW_UPDATING_PHENOMENON) + e.getMessage());
		}
	}

	/**
	 * Update Criterion entity
	 * 
	 * @param criterion the criterion to update
	 */
	public void updateCriterion(Criterion criterion) {

		if (criterion == null) {
			return;
		}

		try {
			view.getViewManager().getAppManager().getService(IPIRTApplication.class).updateCriterion(criterion);

			// Refresh parent
			if (criterion.getPhenomenon() != null) {
				view.getViewManager().getAppManager().getService(IPIRTApplication.class)
						.refresh(criterion.getPhenomenon());
			}

			// Refresh view
			view.getViewManager().viewChanged();
			view.refresh();

		} catch (CredibilityException e) {
			logger.error("An error occured while updating criterion: {}", RscTools.carriageReturn() //$NON-NLS-1$
					+ e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
					RscTools.getString(RscConst.ERR_PHENOMENAVIEW_UPDATING_PHENOMENON) + e.getMessage());
		}
	}

	/**
	 * Do tag action. Save and copy the current PIRT state: Phenomenon Groups,
	 * Phenomena, Criteria
	 */
	public void doTagAction() {

		// confirm tag dialog
		boolean confirm = MessageDialog.openConfirm(view.getShell(),
				RscTools.getString(RscConst.MSG_PHENOMENAVIEW_TAGCONFIRM_TITLE),
				RscTools.getString(RscConst.MSG_PHENOMENAVIEW_TAGCONFIRM_QUESTION, view.getQoISelected().getSymbol()));

		if (confirm) {
			try {
				// tag dialog
				QoITagDialog tagQoIDialog = new QoITagDialog(view.getViewManager(), view.getShell(),
						view.getQoISelected());
				String tagDescription = tagQoIDialog.openDialog();

				if (tagQoIDialog.getReturnCode() == Window.OK) {

					// tag qoi in database
					view.getViewManager().getAppManager().getService(IPIRTApplication.class)
							.tagQoI(view.getQoISelected(), tagDescription, view.getViewManager().getCache().getUser());

					// refresh view
					view.getViewManager().reloadQOIView();

					// fire view change to save credibility file
					view.getViewManager().viewChanged();

					// confirm tag success
					MessageDialog.openInformation(view.getShell(),
							RscTools.getString(RscConst.MSG_PHENOMENAVIEW_TAGCONFIRM_TITLE),
							RscTools.getString(RscConst.MSG_PHENOMENAVIEW_TAGCONFIRM_SUCCESS));
				}

			} catch (CredibilityException e) {
				logger.error("An error occured while tagging qoi: {}", view.getQoISelected() + RscTools.carriageReturn() //$NON-NLS-1$
						+ e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TAGGING),
						e.getMessage());
			}
		}

	}

	/**
	 * Reset Phenomena view: qoi headers are set to blank, all phenomenon groups,
	 * phenomenon and criterion are deleted
	 */
	public void resetAction() {

		QuantityOfInterest qoiSelected = view.getQoISelected();

		if (qoiSelected == null) {
			return;
		}

		// get reset confirmation message variables
		int nbGroups = 0;
		int nbPhenomena = 0;

		if (qoiSelected.getPhenomenonGroupList() != null) {
			nbGroups = qoiSelected.getPhenomenonGroupList().size();
			for (PhenomenonGroup group : qoiSelected.getPhenomenonGroupList()) {
				if (group != null && group.getPhenomenonList() != null) {
					nbPhenomena += group.getPhenomenonList().size();
				}
			}
		}

		String resetQuestion = RscTools.getString(RscConst.MSG_PHENOMENAVIEW_RESETCONFIRM_QUESTION,
				qoiSelected.getSymbol(), nbGroups, nbPhenomena);

		// confirm reset
		boolean confirmReset = MessageDialog.openConfirm(view.getShell(),
				RscTools.getString(RscConst.MSG_PHENOMENAVIEW_RESETCONFIRM_TITLE), resetQuestion);

		if (confirmReset) {
			// Reset the selected qoi in database
			try {
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).resetQoI(qoiSelected);
			} catch (CredibilityException e) {
				logger.error("An error occured while resetting qoi: {}", //$NON-NLS-1$
						(view.getQoISelected() != null ? view.getQoISelected() : RscTools.empty())
								+ RscTools.carriageReturn(),
						e);

				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
						RscTools.getString(RscConst.ERR_PHENOMENAVIEW_RESETTING)
								+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}

			// Refresh
			view.refresh();

			// Fire view change to save credibility file
			view.getViewManager().viewChanged();
		}

	}

	/**
	 * Updates the QoI Headers
	 * 
	 * @param qoiHeaderParts the qoi header to update
	 * @param property       the property updated
	 * @param value          the value to set
	 */
	public void updateQoIHeaders(QoIHeaderParts qoiHeaderParts, String property, String value) {

		// qoi fixed attributes
		if (qoiHeaderParts.isFixed()) {
			updateFixedAttributes(qoiHeaderParts, property, value);
		} else if (property.equals(TableHeader.COLUMN_VALUE_PROPERTY)) {
			updateVariableAttributes(qoiHeaderParts, property, value);
		}
	}

	/**
	 * Updates the QoI Header Fixed attributes
	 * 
	 * @param qoiHeaderParts the qoi header to update
	 * @param property       the property updated
	 * @param value          the value to set
	 */
	private void updateFixedAttributes(QoIHeaderParts qoiHeaderParts, String property, String value) {

		boolean toUpdate = false;
		QuantityOfInterest qoi = qoiHeaderParts.getQoi();

		if (PIRTPhenTableHeaderDescriptor.getRowNameLabel().equals(qoiHeaderParts.getName())) {
			toUpdate = setQoIName(qoi, property, value);
		} else if (PIRTPhenTableHeaderDescriptor.getRowDescriptionLabel().equals(qoiHeaderParts.getName())) {
			qoi.setDescription(value);
			toUpdate = true;
		} else if (PIRTPhenTableHeaderDescriptor.getRowTagDescriptionLabel().equals(qoiHeaderParts.getName())) {
			qoi.setTagDescription(value);
			toUpdate = true;
		}

		if (toUpdate) {
			try {
				// persist modifications in database
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).updateQoI(qoi,
						view.getViewManager().getCache().getUser());

				// refresh parent views and tabs
				refreshQoIView();

				// refresh the table header
				view.refresh();

				// fire view change to save credibility file
				view.getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
						RscTools.getString(RscConst.ERR_PHENOMENAVIEW_UPDATING_QOINAME) + property
								+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}
		}
	}

	/**
	 * @param qoiHeaderParts the qoi header to update
	 * @param property       the property updated
	 * @param value          the value to set
	 * @return true if the qoi name is ready to update, otherwise false
	 */
	private boolean setQoIName(QuantityOfInterest qoi, String property, String value) {

		boolean toUpdate = false;

		// check if qoi name already exists if the name changed
		if (!qoi.getSymbol().equals(value) && value != null && !value.equals(RscTools.empty())) {
			boolean existsQoISymbol = false;
			try {
				existsQoISymbol = view.getViewManager().getAppManager().getService(IPIRTApplication.class)
						.existsQoISymbol(new Integer[] { qoi.getId() }, value);
				if (existsQoISymbol) {
					MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
							RscTools.getString(RscConst.ERR_COPYQOI_NAME_DUPLICATED, value));
				} else {
					qoi.setSymbol(value);
					toUpdate = true;
				}
			} catch (CredibilityException e) {
				logger.error("An error occured while retrieving the qoi names:\n{}", e.getMessage(), //$NON-NLS-1$
						e);
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
						RscTools.getString(RscConst.ERR_PHENOMENAVIEW_UPDATING_QOINAME) + property
								+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}
		}

		return toUpdate;
	}

	/**
	 * Updates the QoI Header variable attributes
	 * 
	 * @param qoiHeaderParts the qoi header to update
	 * @param property       the property updated
	 * @param value          the value to set
	 */
	private void updateVariableAttributes(QoIHeaderParts qoiHeaderParts, String property, String value) {

		// qoi variable attributes
		qoiHeaderParts.setValue(value);
		try {
			view.getViewManager().getAppManager().getService(IPIRTApplication.class)
					.updateQoIHeader(qoiHeaderParts.getQoiHeader(), view.getViewManager().getCache().getUser());

			// refresh the table header
			view.refresh();

			// fire view change to save credibility file
			view.getViewManager().viewChanged();

		} catch (CredibilityException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
					RscTools.getString(RscConst.ERR_PHENOMENAVIEW_UPDATING_QOIHEADER) + property
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

}
