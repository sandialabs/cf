/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.model.QoIHeaderParts;
import gov.sandia.cf.parts.ui.AViewController;
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
public class PIRTPhenomenaViewController extends AViewController<PIRTViewManager, PIRTPhenomenaView> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTPhenomenaViewController.class);

	/**
	 * the current quantity of interest
	 */
	private QuantityOfInterest qoiSelected;

	/**
	 * Instantiates a new PIRT phenomena view controller.
	 *
	 * @param viewManager the view manager
	 * @param parent      the parent
	 * @param qoi         the qoi
	 */
	PIRTPhenomenaViewController(PIRTViewManager viewManager, Composite parent, QuantityOfInterest qoi) {
		super(viewManager);
		this.qoiSelected = qoi;
		super.setView(new PIRTPhenomenaView(this, parent, SWT.NONE));

		// Refresh
		refresh();
	}

	/**
	 * Reload data.
	 */
	void reloadData() {

		// retrieve qoi from database
		try {
			qoiSelected = getViewManager().getAppManager().getService(IPIRTApplication.class)
					.getQoIById(qoiSelected.getId());
		} catch (CredibilityException e) {
			logger.error("An error occured while reloading qoi data and PIRT tree", e); //$NON-NLS-1$
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
					RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}

		getView().setTableHeaderData(qoiSelected);

		List<PhenomenonGroup> phenomenaGroups = new ArrayList<>();
		if (qoiSelected != null) {
			phenomenaGroups = qoiSelected.getPhenomenonGroupList();
			phenomenaGroups.sort(Comparator.comparing(PhenomenonGroup::getIdLabel));
		}

		/**
		 * Refresh the table
		 */
		// Get expanded elements
		Object[] elements = getView().getTreeExpandedElements();

		getView().setTableHeaderData(qoiSelected);

		// Refresh the table
		getView().refreshMainTable();

		// Set input
		getView().setTreeData(phenomenaGroups);

		// Set expanded elements
		getView().setTreeExpandedElements(elements);
	}

	/**
	 * Refreshes the parent view to update qoi modifications
	 */
	public void refreshQoIView() {
		getViewManager().refreshQOIView();
		getViewManager().reloadTabName(qoiSelected);
		if (qoiSelected != null) {
			getView().updateTableHeaderBarName(qoiSelected.getSymbol());
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
			getView().refresh();

			// fire view change to save credibility file
			getViewManager().viewChanged();
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
		boolean confirm = MessageDialog.openConfirm(getView().getShell(),
				RscTools.getString(RscConst.MSG_PHENOMENAVIEW_DELETECONFIRM_TITLE, title), message);

		if (confirm) {
			try {
				getViewManager().getAppManager().getService(IPIRTApplication.class)
						.deletePhenomenonGroup(phenomenonGroup, getViewManager().getCache().getUser());
				List<PhenomenonGroup> data = getPhenomenonGroups();
				data.remove(phenomenonGroup);

			} catch (CredibilityException e) {
				logger.error("An error occured while deleting phenomenon: {}", //$NON-NLS-1$
						phenomenonGroup + RscTools.carriageReturn() + e.getMessage(), e);
			}
		}
	}

	/**
	 * Delete a Phenomenon.
	 *
	 * @param phenomenon the phenomenon
	 */
	void deletePhenomenon(Phenomenon phenomenon) {
		// constructs confirm message
		String title = RscTools.getString(RscConst.MSG_PIRT_MODEL_PHEN);
		String message = RscTools.getString(RscConst.MSG_PHENOMENAVIEW_DELETECONFIRM_QUESTIONPHENOMENON,
				phenomenon.getName());

		// confirm dialog
		boolean confirm = MessageDialog.openConfirm(getView().getShell(),
				RscTools.getString(RscConst.MSG_PHENOMENAVIEW_DELETECONFIRM_TITLE, title), message);

		if (confirm) {
			try {
				getViewManager().getAppManager().getService(IPIRTApplication.class).deletePhenomenon(phenomenon,
						getViewManager().getCache().getUser());
				List<PhenomenonGroup> data = getPhenomenonGroups();
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

		if (qoiSelected != null) {

			// open creation dialog
			PIRTPhenomenonGroupDialog dialog = new PIRTPhenomenonGroupDialog(getViewManager(), getView().getShell(),
					qoiSelected, null, ViewMode.CREATE);
			PhenomenonGroup groupToCreate = dialog.openDialog();

			// create group
			if (groupToCreate != null) {
				try {

					// Set id
					groupToCreate.setIdLabel(getView().getIdColumnTextPhenomenonGroup(groupToCreate));

					// set qoi
					groupToCreate.setQoi(qoiSelected);

					// create
					getViewManager().getAppManager().getService(IPIRTApplication.class)
							.addPhenomenonGroup(groupToCreate);

					// refresh qoi
					getViewManager().getAppManager().getService(IPIRTApplication.class).refresh(qoiSelected);

					// fire view change to save credibility file
					getViewManager().viewChanged();

					// reload
					getView().refresh();

				} catch (CredibilityException e) {
					MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
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
		List<PhenomenonGroup> phenGroups = getPhenomenonGroups();

		if (phenGroups == null) {
			MessageDialog.openWarning(getView().getShell(),
					RscTools.getString(RscConst.WRN_PHENOMENAVIEW_ADDING_PHENOMENON_GROUPNOTPRESENT_TITLE),
					RscTools.getString(RscConst.WRN_PHENOMENAVIEW_ADDING_PHENOMENON_GROUPNOTPRESENT_DESC));
			return;
		}

		// open creation dialog
		PIRTPhenomenonDialog dialog = new PIRTPhenomenonDialog(getViewManager(), getView().getShell(), phenGroups, null,
				groupSelected, ViewMode.CREATE);
		Phenomenon phenomenonToCreate = dialog.openDialog();

		if (phenomenonToCreate == null) {
			return;
		}

		// persist phenomenon in database
		try {
			// Set id
			phenomenonToCreate.setIdLabel(getView().getIdColumnTextPhenomenon(phenomenonToCreate));

			// create phenomenon
			getViewManager().getAppManager().getService(IPIRTApplication.class).addPhenomenon(phenomenonToCreate);

			// Refresh parent
			if (groupSelected != null) {
				getViewManager().getAppManager().getService(IPIRTApplication.class).refresh(groupSelected);

				// Expand parent
				getView().expandElements(groupSelected);
			}

			// fire view change to save credibility file
			getViewManager().viewChanged();

			// Refresh
			getView().refresh();

		} catch (CredibilityException e) {
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
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
			PIRTPhenomenonGroupDialog dialog = new PIRTPhenomenonGroupDialog(getViewManager(), getView().getShell(),
					qoiSelected, group, ViewMode.VIEW);
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
			PIRTPhenomenonDialog dialog = new PIRTPhenomenonDialog(getViewManager(), getView().getShell(),
					qoiSelected.getPhenomenonGroupList(), phenomenon, null, ViewMode.VIEW);
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
		PIRTPhenomenonGroupDialog dialog = new PIRTPhenomenonGroupDialog(getViewManager(), getView().getShell(),
				qoiSelected, group, ViewMode.UPDATE);
		PhenomenonGroup groupUpdated = dialog.openDialog();

		if (groupUpdated != null) {
			try {
				// create
				getViewManager().getAppManager().getService(IPIRTApplication.class).updatePhenomenonGroup(groupUpdated);

				// Refresh
				getView().refresh();

				// fire view change to save credibility file
				getViewManager().viewChanged();
			} catch (CredibilityException e) {
				logger.error("An error occured while updating phenomenon group: {}", RscTools.carriageReturn() //$NON-NLS-1$
						+ e.getMessage(), e);
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
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
		PIRTPhenomenonDialog dialog = new PIRTPhenomenonDialog(getViewManager(), getView().getShell(),
				qoiSelected.getPhenomenonGroupList(), phenomenonToUpdate, null, ViewMode.UPDATE);
		Phenomenon phenomenonUpdated = dialog.openDialog();

		if (phenomenonUpdated != null) {
			try {
				updatePhenomenon(phenomenonUpdated);

				PhenomenonGroup newGroup = phenomenonUpdated.getPhenomenonGroup();

				// update phenomenon group lists
				if (previousGroup != null && newGroup != null) {
					previousGroup.getPhenomenonList().remove(phenomenonToUpdate);
					newGroup.getPhenomenonList().add(phenomenonUpdated);
				}

				// reload data model
				getView().refresh();
			} catch (CredibilityException e) {
				logger.error("An error occured while updating phenomenon: {}", RscTools.carriageReturn() //$NON-NLS-1$
						+ e.getMessage(), e);
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
						RscTools.getString(RscConst.ERR_PHENOMENAVIEW_UPDATING_PHENOMENON) + e.getMessage());
			}

		}
	}

	/**
	 * Update Phenomenon entity.
	 *
	 * @param phenomenon the phenomenon to update
	 * @return the phenomenon
	 * @throws CredibilityException the credibility exception
	 */
	public Phenomenon updatePhenomenon(Phenomenon phenomenon) throws CredibilityException {

		if (phenomenon == null) {
			return null;
		}

		Phenomenon updatePhenomenon = getViewManager().getAppManager().getService(IPIRTApplication.class)
				.updatePhenomenon(phenomenon);

		// Refresh parent
		PhenomenonGroup newGroup = updatePhenomenon.getPhenomenonGroup();
		if (newGroup != null) {
			getViewManager().getAppManager().getService(IPIRTApplication.class).refresh(newGroup);
		}

		// Refresh view
		getViewManager().viewChanged();

		return updatePhenomenon;
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
			getViewManager().getAppManager().getService(IPIRTApplication.class).updateCriterion(criterion);

			// Refresh parent
			if (criterion.getPhenomenon() != null) {
				getViewManager().getAppManager().getService(IPIRTApplication.class).refresh(criterion.getPhenomenon());
			}

			// Refresh view
			getViewManager().viewChanged();
			getView().refresh();

		} catch (CredibilityException e) {
			logger.error("An error occured while updating criterion: {}", RscTools.carriageReturn() //$NON-NLS-1$
					+ e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
					RscTools.getString(RscConst.ERR_PHENOMENAVIEW_UPDATING_PHENOMENON) + e.getMessage());
		}
	}

	/**
	 * Do tag action. Save and copy the current PIRT state: Phenomenon Groups,
	 * Phenomena, Criteria
	 */
	public void doTagAction() {

		// confirm tag dialog
		boolean confirm = MessageDialog.openConfirm(getView().getShell(),
				RscTools.getString(RscConst.MSG_PHENOMENAVIEW_TAGCONFIRM_TITLE),
				RscTools.getString(RscConst.MSG_PHENOMENAVIEW_TAGCONFIRM_QUESTION, qoiSelected.getSymbol()));

		if (confirm) {
			try {
				// tag dialog
				QoITagDialog tagQoIDialog = new QoITagDialog(getViewManager(), getView().getShell(), qoiSelected);
				String tagDescription = tagQoIDialog.openDialog();

				if (tagQoIDialog.getReturnCode() == Window.OK) {

					// tag qoi in database
					getViewManager().getAppManager().getService(IPIRTApplication.class).tagQoI(qoiSelected,
							tagDescription, getViewManager().getCache().getUser());

					// refresh view
					getViewManager().reloadQOIView();

					// fire view change to save credibility file
					getViewManager().viewChanged();

					// confirm tag success
					MessageDialog.openInformation(getView().getShell(),
							RscTools.getString(RscConst.MSG_PHENOMENAVIEW_TAGCONFIRM_TITLE),
							RscTools.getString(RscConst.MSG_PHENOMENAVIEW_TAGCONFIRM_SUCCESS));
				}

			} catch (CredibilityException e) {
				logger.error("An error occured while tagging qoi: {}", qoiSelected + RscTools.carriageReturn() //$NON-NLS-1$
						+ e.getMessage(), e);
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TAGGING),
						e.getMessage());
			}
		}

	}

	/**
	 * Reset Phenomena view: qoi headers are set to blank, all phenomenon groups,
	 * phenomenon and criterion are deleted
	 */
	public void resetAction() {

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
		boolean confirmReset = MessageDialog.openConfirm(getView().getShell(),
				RscTools.getString(RscConst.MSG_PHENOMENAVIEW_RESETCONFIRM_TITLE), resetQuestion);

		if (confirmReset) {
			// Reset the selected qoi in database
			try {
				getViewManager().getAppManager().getService(IPIRTApplication.class).resetQoI(qoiSelected,
						getViewManager().getCache().getUser());
			} catch (CredibilityException e) {
				logger.error("An error occured while resetting qoi: {}", //$NON-NLS-1$
						(qoiSelected != null ? qoiSelected : RscTools.empty()) + RscTools.carriageReturn(), e);

				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
						RscTools.getString(RscConst.ERR_PHENOMENAVIEW_RESETTING)
								+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}

			// Refresh
			getView().refresh();

			// Fire view change to save credibility file
			getViewManager().viewChanged();
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
				getViewManager().getAppManager().getService(IPIRTApplication.class).updateQoI(qoi,
						getViewManager().getCache().getUser());

				// refresh parent views and tabs
				refreshQoIView();

				// refresh the table header
				getView().refresh();

				// fire view change to save credibility file
				getViewManager().viewChanged();

			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
						RscTools.getString(RscConst.ERR_PHENOMENAVIEW_UPDATING_QOINAME) + property
								+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			}
		}
	}

	/**
	 * Sets the qo I name.
	 *
	 * @param qoi      the qoi
	 * @param property the property updated
	 * @param value    the value to set
	 * @return true if the qoi name is ready to update, otherwise false
	 */
	private boolean setQoIName(QuantityOfInterest qoi, String property, String value) {

		boolean toUpdate = false;

		// check if qoi name already exists if the name changed
		if (!qoi.getSymbol().equals(value) && value != null && !value.equals(RscTools.empty())) {
			boolean existsQoISymbol = false;
			try {
				existsQoISymbol = getViewManager().getAppManager().getService(IPIRTApplication.class)
						.existsQoISymbol(new Integer[] { qoi.getId() }, value);
				if (existsQoISymbol) {
					MessageDialog.openWarning(getView().getShell(),
							RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
							RscTools.getString(RscConst.ERR_COPYQOI_NAME_DUPLICATED, value));
				} else {
					qoi.setSymbol(value);
					toUpdate = true;
				}
			} catch (CredibilityException e) {
				logger.error("An error occured while retrieving the qoi names:\n{}", e.getMessage(), //$NON-NLS-1$
						e);
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
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
			getViewManager().getAppManager().getService(IPIRTApplication.class)
					.updateQoIHeader(qoiHeaderParts.getQoiHeader(), getViewManager().getCache().getUser());

			// refresh the table header
			getView().refresh();

			// fire view change to save credibility file
			getViewManager().viewChanged();

		} catch (CredibilityException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_PHENOMENAVIEW_TITLE),
					RscTools.getString(RscConst.ERR_PHENOMENAVIEW_UPDATING_QOIHEADER) + property
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Reorder phenomenon groups.
	 *
	 * @param group    the group
	 * @param newIndex the new index
	 * @throws CredibilityException the credibility exception
	 */
	public void reorderPhenomenonGroups(PhenomenonGroup group, int newIndex) throws CredibilityException {

		getViewManager().getAppManager().getService(IPIRTApplication.class).reorderPhenomenonGroups(group, newIndex);

		// fire view change to save credibility file
		getViewManager().viewChanged();
	}

	/**
	 * Reorder phenomena for group.
	 *
	 * @param group the group
	 * @throws CredibilityException the credibility exception
	 */
	public void reorderPhenomenaForGroup(PhenomenonGroup group) throws CredibilityException {

		getViewManager().getAppManager().getService(IPIRTApplication.class).reorderPhenomenaForGroup(group);

		// fire view change to save credibility file
		getViewManager().viewChanged();
	}

	/**
	 * Reorder phenomenon.
	 *
	 * @param phenomenon the phenomenon
	 * @param newIndex   the new index
	 * @throws CredibilityException the credibility exception
	 */
	public void reorderPhenomenon(Phenomenon phenomenon, int newIndex) throws CredibilityException {

		getViewManager().getAppManager().getService(IPIRTApplication.class).reorderPhenomena(phenomenon, newIndex);

		// fire view change to save credibility file
		getViewManager().viewChanged();
	}

	/**
	 * Refresh phenomenon group.
	 *
	 * @param group the group
	 */
	public void refreshPhenomenonGroup(PhenomenonGroup group) {
		getViewManager().getAppManager().getService(IPIRTApplication.class).refresh(group);
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

	/**
	 * Get pirt configuration
	 * 
	 * @return the pirt specification
	 */
	public PIRTSpecification getPirtConfiguration() {
		return getViewManager().getPIRTConfiguration();
	}

	/**
	 *
	 * Get the qoi selected
	 * 
	 * @return the qoi selected
	 */
	QuantityOfInterest getQoISelected() {
		return qoiSelected;
	}

	/**
	 * Checks if is tagged.
	 *
	 * @return true, if is tagged
	 */
	boolean isTagged() {
		return qoiSelected != null && qoiSelected.getTagDate() != null;
	}

	/**
	 * @return the PIRT description headers from the PIRT configuration
	 */
	@SuppressWarnings("unchecked")
	List<PhenomenonGroup> getPhenomenonGroups() {
		return (List<PhenomenonGroup>) getView().getTreeData();
	}
}
