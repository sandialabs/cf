/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * PCMM Evidence view controller: Used to control the PCMM Evidence view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceViewController extends AViewController<PCMMViewManager, PCMMEvidenceView> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMEvidenceViewController.class);

	/**
	 * the pcmm element
	 */
	private PCMMElement elementSelected;

	/**
	 * The last selected file
	 */
	private IFile lastSelectedFile = null;

	/**
	 * Constructor.
	 *
	 * @param viewManager the view manager
	 */
	PCMMEvidenceViewController(PCMMViewManager viewManager) {
		super(viewManager);
		super.setView(new PCMMEvidenceView(this, SWT.NONE));
	}

	/**
	 * Reload data.
	 */
	void reloadData() {
		// Trigger GuidanceLevel View
		getViewManager().getCredibilityEditor().setPartProperty(
				CredibilityFrameworkConstants.PART_PROPERTY_ACTIVEVIEW_PCMM_SELECTED_ASSESSABLE,
				elementSelected != null ? elementSelected.getAbbreviation() : RscTools.empty());

		// Show role selection
		getView().showRoleSelection();

		List<PCMMElement> pcmmElementList = new ArrayList<>();

		// Get Model
		Model model = getViewManager().getCache().getModel();
		if (model != null) {
			try {
				// Load pcmm elements from database
				pcmmElementList = getViewManager().getAppManager().getService(IPCMMApplication.class)
						.getElementList(model);

			} catch (CredibilityException e) {
				MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
						RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_LOADING_MSG));
				logger.warn("An error has occurred while loading the evidence data:\n{}", e.getMessage(), e); //$NON-NLS-1$
			}
		}

		// Get expanded elements
		Object[] elements = getView().getExpandedElements();

		// Refresh the table
		getView().refreshMainTable();

		// set pcmm elements
		getView().setTreeData(pcmmElementList);

		// Set expanded elements
		if (elements == null || elements.length == 0) {
			if (elementSelected != null) {

				// refresh element
				getViewManager().getAppManager().getService(IPCMMApplication.class).refreshElement(elementSelected);

				getView().expandElement(elementSelected);
			}
		} else {
			getView().setExpandedElements(elements);
		}

		// refresh the viewer
		getView().refreshViewer();
	}

	/**
	 * Add a new PCMM evidence
	 * 
	 * @param element the evidence to add
	 */
	void addEvidence(Object element) {

		PCMMEvidence evidenceCreated = null;

		/**
		 * Check the PCMM mode
		 */
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			// get the selected sub-element to associate the evidence with
			PCMMSubelement subelementSelected = (PCMMSubelement) element;

			// check the sub-element
			if (subelementSelected == null) {
				MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE),
						RscTools.getString(RscConst.ERR_PCMMEVID_ADD_BADSELECT_NOTSUBELEMENT_MSG));
			} else {
				// add the evidence
				evidenceCreated = addEvidence(subelementSelected, null);
			}
		} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
			// get the selected element to associate the evidence with
			PCMMElement elementSelectedTemp = (PCMMElement) element;

			// check the element
			if (elementSelectedTemp == null) {
				MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE),
						RscTools.getString(RscConst.ERR_PCMMEVID_ADD_BADSELECT_NOTELEMENT_MSG));
			} else {
				// add the evidence
				evidenceCreated = addEvidence(elementSelectedTemp, null);
			}
		}

		// Refresh
		if (evidenceCreated != null) {

			// refresh
			viewChangedAndRefresh();
		}
	}

	/**
	 * Add a new PCMM evidence with default file setted.
	 *
	 * @param assessable  the assessable (PCMM Element or Subelement) to associate
	 *                    the evidence with
	 * @param defaultFile the default file to associate
	 * @return the PCMM evidence
	 */
	public PCMMEvidence addEvidence(IAssessable assessable, IFile defaultFile) {

		if (elementSelected == null) {
			String message = "Impossible to add the evidence. There is no PCMM element selected."; //$NON-NLS-1$
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE),
					message);
			logger.warn(message);
			return null;
		} else if (assessable == null) {
			String message = RscTools.getString(RscConst.ERR_PCMMEVID_ADD_BADSELECT_NOTSUBELEMENT_MSG);
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE),
					message);
			logger.warn(message);
			return null;
		} else if (assessable instanceof PCMMElement && !elementSelected.equals(assessable)) {
			String message = "Impossible to add the evidence. The selected element is not an authorized PCMM element"; //$NON-NLS-1$
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE),
					message);
			logger.warn(message);
			return null;
		} else if (assessable instanceof PCMMSubelement && (((PCMMSubelement) assessable).getElement() == null
				|| !elementSelected.equals(((PCMMSubelement) assessable).getElement()))) {
			String message = "Impossible to add the evidence. The selected subelement is not into the authorized PCMM element"; //$NON-NLS-1$
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE),
					message);
			logger.warn(message);
			return null;
		}

		// select the resource with the resource dialog
		PCMMEvidence evidence = showEvidenceDialog(null, assessable, defaultFile);

		PCMMEvidence evidenceCreated = null;

		if (evidence == null) {
			return evidenceCreated;
		}

		try {
			// add the evidence in database and viewer
			evidenceCreated = addEvidenceResource(evidence, assessable);

			// expand element
			if (evidenceCreated != null) {
				getView().expandElement(evidenceCreated.getElement());
				getView().expandSubelement(evidenceCreated.getSubelement());
			}
			// view changed
			getViewManager().viewChanged();

		} catch (CredibilityException e) {
			logger.error("An error occurred while adding new PCMM evidence: \n{}", e.getMessage(), e); //$NON-NLS-1$
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_ADDING_MSG) + e.getMessage());
		}

		return evidenceCreated;
	}

	/**
	 * Add the evidence in database and associate it with the assessable (PCMM
	 * element or subelement).
	 *
	 * @param evidence   the evidence to add
	 * @param assessable the assessable (PCMM element or subelement)
	 * @return the PCMM evidence
	 * @throws CredibilityException the credibility exception
	 */
	PCMMEvidence addEvidenceResource(PCMMEvidence evidence, IAssessable assessable) throws CredibilityException {

		if (evidence == null) {
			return null;
		}

		// Keep file location
		if (FormFieldType.LINK_FILE.equals(evidence.getType())) {
			// Last selection
			lastSelectedFile = WorkspaceTools.getFileInWorkspaceForPath(new Path(evidence.getPath()));

			// Set last updated date
			if (lastSelectedFile != null) {
				long lastUpdated = lastSelectedFile.getRawLocation().makeAbsolute().toFile().lastModified();
				evidence.setDateFile(new Date(lastUpdated));
			}
		}

		// Add element/subelement to the evidence
		if (assessable instanceof PCMMElement) {
			evidence.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			evidence.setSubelement((PCMMSubelement) assessable);
		}

		// set user and role creation parameters
		evidence.setUserCreation(getViewManager().getCache().getUser());
		evidence.setRoleCreation(getViewManager().getCurrentUserRole());

		// Add Evidence in database
		PCMMEvidence evidenceCreated = getViewManager().getAppManager().getService(IPCMMEvidenceApp.class)
				.addEvidence(evidence);

		// Associate the evidence with the assessable item
		if (assessable instanceof PCMMElement) {
			((PCMMElement) assessable).getEvidenceList().add(evidenceCreated);
		} else if (assessable instanceof PCMMSubelement) {
			((PCMMSubelement) assessable).getEvidenceList().add(evidenceCreated);
		}

		return evidenceCreated;
	}

	/**
	 * Add a new PCMM evidence
	 * 
	 * This method is called if the PCMM Mode is SIMPLIFIED.
	 *
	 * @param editedEvidence the edited evidence
	 */
	void editEvidence(PCMMEvidence editedEvidence) {

		if (editedEvidence == null) {
			logger.warn("The evidence to edit is null"); //$NON-NLS-1$
			return;
		}

		// Item
		IAssessable item = null;
		if (PCMMMode.DEFAULT == getViewManager().getCache().getPCMMSpecification().getMode()) {
			item = editedEvidence.getSubelement();
		} else if (PCMMMode.SIMPLIFIED == getViewManager().getCache().getPCMMSpecification().getMode()) {
			item = editedEvidence.getElement();
		}

		// select the resource with the resource dialog
		PCMMEvidence evidence = showEvidenceDialog(editedEvidence, item, null);

		if (evidence != null) {

			boolean changed = false;

			// Save
			try {
				getViewManager().getAppManager().getService(IPCMMEvidenceApp.class).updateEvidence(evidence);
				changed |= true;
			} catch (CredibilityException e) {
				logger.error("An error has occurred while updating PCMM evidence: \n{}", e.getMessage(), e); //$NON-NLS-1$
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
						RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_ADDING_MSG) + e.getMessage());
			}

			if (changed) {
				// Set view changed
				getViewManager().viewChanged();

				// Refresh
				getView().refresh();
			}
		}
	}

	/**
	 * Display a new resource selection dialog.
	 *
	 * @param evidence    The edited evidence
	 * @param item        the item
	 * @param defaultFile the default file
	 * @return the list of the selected resources
	 */
	PCMMEvidence showEvidenceDialog(PCMMEvidence evidence, IAssessable item, IFile defaultFile) {
		// Get last selected file
		if (lastSelectedFile == null) {
			lastSelectedFile = WorkspaceTools.getActiveCfFile();
		}

		// Evidence dialog to add/edit the evidence
		ViewMode mode = evidence == null ? ViewMode.CREATE : ViewMode.UPDATE;
		if (evidence == null) {
			evidence = new PCMMEvidence();
		}
		if (defaultFile != null) {
			evidence.setFilePath(defaultFile.getFullPath().toPortableString());
		}
		PCMMEvidenceDialog dialog = new PCMMEvidenceDialog(getViewManager(), getView().getShell(), evidence, item,
				mode);

		// open the dialog
		return dialog.openDialog();
	}

	/**
	 * Delete evidence.
	 *
	 * @param selection the selection
	 */
	void deleteEvidence(IStructuredSelection selection) {

		if (selection != null && !selection.isEmpty()) {

			/** confirm delete **/

			// constructs confirm message
			String deleteDetailMessage = RscTools.getString(RscConst.MSG_PCMMEVID_MULTI_DELETE_CONFIRM_QUESTION);

			// confirm dialog
			boolean confirm = MessageDialog.openConfirm(getView().getShell(),
					RscTools.getString(RscConst.MSG_PCMMEVID_DELETE_CONFIRM_TITLE), deleteDetailMessage);

			if (!confirm) {
				return;
			}

			Iterator<?> iterator = selection.iterator();
			while (iterator.hasNext()) {
				Object next = iterator.next();
				if (next instanceof PCMMEvidence) {
					try {
						deleteEvidence((PCMMEvidence) next);
					} catch (CredibilityException e) {
						logger.error("An error has occurred while deleting PCMMevidence:\n{}", e.getMessage(), e); //$NON-NLS-1$
						MessageDialog.openError(getView().getShell(),
								RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
								RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_DELETING_MSG) + e.getMessage());
						break;
					}
				}
			}

			// Refresh
			getView().refresh();
		}
	}

	/**
	 * Delete the selected evidence.
	 *
	 * @param evidence the evidence
	 */
	void deleteEvidenceWithConfirm(PCMMEvidence evidence) {

		// delete the evidence
		try {

			/** confirm delete **/

			// constructs confirm message
			String deleteDetailMessage = RscTools.empty();
			deleteDetailMessage += evidence != null
					? RscTools.getString(RscConst.MSG_PCMMEVID_DELETE_CONFIRM_QUESTION, evidence.getName())
					: RscTools.empty();

			// confirm dialog
			boolean confirm = MessageDialog.openConfirm(getView().getShell(),
					RscTools.getString(RscConst.MSG_PCMMEVID_DELETE_CONFIRM_TITLE), deleteDetailMessage);

			if (!confirm) {
				return;
			}

			// delete evidence
			deleteEvidence(evidence);

			// Refresh
			getView().refresh();
		} catch (CredibilityException e) {
			logger.error("An error has occurred while deleting PCMMevidence:\n{}", e.getMessage(), e); //$NON-NLS-1$
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_DELETING_MSG) + e.getMessage());
		}
	}

	/**
	 * Delete evidence.
	 *
	 * @param evidence the evidence
	 * @throws CredibilityException the credibility exception
	 */
	void deleteEvidence(PCMMEvidence evidence) throws CredibilityException {

		if (evidence == null || elementSelected == null) {
			return;
		}

		/**
		 * Delete from phenomena viewer
		 */
		if (evidence.getElement() != null) {
			elementSelected.getEvidenceList().remove(evidence);
		} else if (evidence.getSubelement() != null) {
			int index = elementSelected.getSubElementList().indexOf(evidence.getSubelement());
			if (index >= 0) {
				PCMMSubelement sub = elementSelected.getSubElementList().get(index);
				sub.getEvidenceList().remove(evidence);
			}
		}

		/**
		 * Delete from database
		 */
		getViewManager().getAppManager().getService(IPCMMEvidenceApp.class).deleteEvidence(evidence);

		// Set view changed
		getViewManager().viewChanged();
	}

	/**
	 * Move the evidence in database and associate it with the assessable (PCMM
	 * element or subelement).
	 *
	 * @param evidence      the evidence to add
	 * @param newAssessable the new assessable to set (PCMM element or subelement)
	 * @return the PCMM evidence updated
	 */
	public PCMMEvidence moveEvidence(PCMMEvidence evidence, IAssessable newAssessable) {

		if (evidence == null || newAssessable == null) {
			return null;
		}

		PCMMEvidence evidenceUpdated = null;
		boolean changed = false;

		// set changes and get old one to refresh
		IAssessable oldAssessable = null;
		if (newAssessable instanceof PCMMElement) {
			oldAssessable = evidence.getElement();
		} else if (newAssessable instanceof PCMMSubelement) {
			oldAssessable = evidence.getSubelement();
		}

		// update
		try {
			evidenceUpdated = getViewManager().getAppManager().getService(IPCMMEvidenceApp.class).moveEvidence(evidence,
					null, newAssessable);
			changed = true;
		} catch (CredibilityException e) {
			logger.error("An error has occurred while updating PCMM evidence: \n{}", e.getMessage(), e); //$NON-NLS-1$
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_UPDATING_MSG) + e.getMessage());
		}

		if (changed) {

			// refresh
			getViewManager().getAppManager().getService(IPCMMApplication.class).refreshAssessable(oldAssessable);
			getViewManager().getAppManager().getService(IPCMMApplication.class).refreshAssessable(newAssessable);

			// Set view changed
			getViewManager().viewChanged();
		}

		return evidenceUpdated;
	}

	/**
	 * Reorder evidence.
	 *
	 * @param evidence the evidence
	 * @param newIndex the new index
	 * @throws CredibilityException the credibility exception
	 */
	public void reorderEvidence(PCMMEvidence evidence, int newIndex) throws CredibilityException {

		getViewManager().getAppManager().getService(IPCMMEvidenceApp.class).reorderEvidence(evidence, newIndex,
				getViewManager().getCache().getUser());

		// fire view change to save credibility file
		getViewManager().viewChanged();
	}

	/**
	 * View changed.
	 */
	public void viewChangedAndRefresh() {

		// Set view changed
		getViewManager().viewChanged();

		// Refresh
		getView().refresh();
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
	 * Check if the element in parameter is contained in the current selected
	 * element of the parent view
	 * 
	 * @param evidence the evidence to check
	 * @return boolean True if is selected
	 */
	protected boolean isFromCurrentPCMMElement(PCMMEvidence evidence) {
		// Initialize
		boolean isFromCurrentPCMMElement = false;

		// Check the PCMM mode
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			isFromCurrentPCMMElement = elementSelected != null && evidence.getSubelement() != null
					&& elementSelected.equals(evidence.getSubelement().getElement());
		} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
			isFromCurrentPCMMElement = elementSelected != null && elementSelected.equals(evidence.getElement());
		}

		return isFromCurrentPCMMElement;
	}

	/**
	 * {@link PCMMViewManager#isTagMode()}
	 * 
	 * @return true if the selected tag is a real tag, otherwise false
	 */
	public boolean isTagMode() {
		return getViewManager().isTagMode();
	}

	/**
	 * @return the pcmm element
	 */
	public PCMMElement getElementSelected() {
		return elementSelected;
	}

	/**
	 * Sets the element selected.
	 *
	 * @param elementSelected the new element selected
	 */
	public void setElementSelected(PCMMElement elementSelected) {
		PCMMElement oldElement = this.elementSelected;
		this.elementSelected = elementSelected;

		getView().setPcmmElement(oldElement, elementSelected);
	}

	/**
	 * @return the pcmm configuration
	 */
	public PCMMSpecification getPCMMConfiguration() {
		return getViewManager().getPCMMConfiguration();
	}

	/**
	 * @return the pcmm mode
	 */
	public PCMMMode getPCMMMode() {
		return getViewManager().getPCMMConfiguration() != null ? getViewManager().getPCMMConfiguration().getMode()
				: null;
	}

}
