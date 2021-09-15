/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * PCMM Evidence view controller: Used to control the PCMM Evidence view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMEvidenceViewController.class);

	/**
	 * The view
	 */
	private PCMMEvidenceView view;

	/**
	 * Constructor
	 * 
	 * @param view the PCMM evidence view to manage
	 */
	PCMMEvidenceViewController(PCMMEvidenceView view) {
		Assert.isNotNull(view);
		this.view = view;
	}

	/**
	 * Add a new PCMM evidence
	 * 
	 * @param element the evidence to add
	 */
	void addEvidence(Object element) {
		/**
		 * Check the PCMM mode
		 */
		if (PCMMMode.DEFAULT.equals(view.getViewManager().getPCMMConfiguration().getMode())) {
			// get the selected sub-element to associate the evidence with
			PCMMSubelement subelementSelected = (PCMMSubelement) element;

			// check the sub-element
			if (subelementSelected == null) {
				MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE),
						RscTools.getString(RscConst.ERR_PCMMEVID_ADD_BADSELECT_NOTSUBELEMENT_MSG));
			} else {
				// add the evidences
				addEvidence(subelementSelected);
			}
		} else if (PCMMMode.SIMPLIFIED.equals(view.getViewManager().getPCMMConfiguration().getMode())) {
			// get the selected element to associate the evidence with
			PCMMElement elementSelectedTemp = (PCMMElement) element;

			// check the element
			if (elementSelectedTemp == null) {
				MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE),
						RscTools.getString(RscConst.ERR_PCMMEVID_ADD_BADSELECT_NOTELEMENT_MSG));
			} else {
				// add the evidences
				addEvidence(elementSelectedTemp);
			}
		}

	}

	/**
	 * Add a new PCMM evidence
	 * 
	 * @param assessable the assessable (PCMM Element or Subelement) to associate
	 *                   the evidence with
	 */
	void addEvidence(IAssessable assessable) {
		addEvidence(assessable, null);
	}

	/**
	 * Add a new PCMM evidence with default file setted
	 * 
	 * @param assessable  the assessable (PCMM Element or Subelement) to associate
	 *                    the evidence with
	 * @param defaultFile the default file to associate
	 */
	public void addEvidence(IAssessable assessable, IFile defaultFile) {

		if (getPcmmElement() == null) {
			String message = "Impossible to add the evidence. There is no PCMM element selected."; //$NON-NLS-1$
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE), message);
			logger.warn(message);
			return;
		} else if (assessable == null) {
			String message = RscTools.getString(RscConst.ERR_PCMMEVID_ADD_BADSELECT_NOTSUBELEMENT_MSG);
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE), message);
			logger.warn(message);
			return;
		} else if (assessable instanceof PCMMElement && !getPcmmElement().equals(assessable)) {
			String message = "Impossible to add the evidence. The selected element is not an authorized PCMM element"; //$NON-NLS-1$
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE), message);
			logger.warn(message);
			return;
		} else if (assessable instanceof PCMMSubelement && (((PCMMSubelement) assessable).getElement() == null
				|| !getPcmmElement().equals(((PCMMSubelement) assessable).getElement()))) {
			String message = "Impossible to add the evidence. The selected subelement is not into the authorized PCMM element"; //$NON-NLS-1$
			MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_ADD_TITLE), message);
			logger.warn(message);
			return;
		}

		// select the resource with the resource dialog
		List<PCMMEvidence> evidenceList = showEvidenceDialog(null, assessable, defaultFile);

		if (evidenceList == null || evidenceList.isEmpty()) {
			return;
		}

		boolean changed = false;

		// add the evidence in database and viewer
		for (PCMMEvidence evidence : evidenceList.stream().filter(Objects::nonNull).collect(Collectors.toList())) {
			try {
				addEvidenceResource(evidence, assessable);
			} catch (CredibilityException e) {
				logger.error("An error occurred while adding new PCMM evidence: \n{}", e.getMessage(), e); //$NON-NLS-1$
				MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
						RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_ADDING_MSG) + e.getMessage());
			}
			changed = true;
		}

		if (changed) {
			// Set view changed
			view.getViewManager().viewChanged();

			// Refresh
			view.refresh();
		}
	}

	/**
	 * Add the evidence in database and associate it with the assessable (PCMM
	 * element or subelement)
	 * 
	 * @param evidence   the evidence to add
	 * @param assessable the assessable (PCMM element or subelement)
	 * @throws CredibilityException
	 */
	void addEvidenceResource(PCMMEvidence evidence, IAssessable assessable) throws CredibilityException {

		if (evidence == null) {
			return;
		}

		// Keep file location
		if (FormFieldType.LINK_FILE.equals(evidence.getType())) {
			// Last selection
			view.setLastSelectedFile(WorkspaceTools.getFileInWorkspaceForPath(new Path(evidence.getPath())));

			// Set last updated date
			if (getLastSelectedFile() != null) {
				long lastUpdated = getLastSelectedFile().getRawLocation().makeAbsolute().toFile().lastModified();
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
		evidence.setUserCreation(view.getViewManager().getCache().getUser());
		evidence.setRoleCreation(view.getViewManager().getCurrentUserRole());

		// Add Evidence in database
		PCMMEvidence evidenceCreated = view.getViewManager().getAppManager().getService(IPCMMApplication.class)
				.addEvidence(evidence);

		// Associate the evidence with the assessable item
		if (assessable instanceof PCMMElement) {
			((PCMMElement) assessable).getEvidenceList().add(evidenceCreated);
		} else if (assessable instanceof PCMMSubelement) {
			((PCMMSubelement) assessable).getEvidenceList().add(evidenceCreated);
		}
	}

	/**
	 * Add a new PCMM evidence
	 * 
	 * This method is called if the PCMM Mode is SIMPLIFIED
	 * 
	 * @param element the element to associate the evidence with
	 */
	void editEvidence(PCMMEvidence editedEvidence) {

		if (editedEvidence == null) {
			logger.warn("The evidence to edit is null"); //$NON-NLS-1$
			return;
		}

		// Item
		IAssessable item = null;
		if (PCMMMode.DEFAULT == view.getViewManager().getCache().getPCMMSpecification().getMode()) {
			item = editedEvidence.getSubelement();
		} else if (PCMMMode.SIMPLIFIED == view.getViewManager().getCache().getPCMMSpecification().getMode()) {
			item = editedEvidence.getElement();
		}

		// select the resource with the resource dialog
		List<PCMMEvidence> evidenceList = showEvidenceDialog(editedEvidence, item, null);

		if (evidenceList != null && !evidenceList.isEmpty()) {

			boolean changed = false;

			// Save
			for (PCMMEvidence evidence : evidenceList.stream().filter(Objects::nonNull).collect(Collectors.toList())) {
				try {
					view.getViewManager().getAppManager().getService(IPCMMApplication.class).updateEvidence(evidence);
					changed |= true;
				} catch (CredibilityException e) {
					logger.error("An error has occurred while updating PCMM evidence: \n{}", e.getMessage(), e); //$NON-NLS-1$
					MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
							RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_ADDING_MSG) + e.getMessage());
				}
			}

			if (changed) {
				// Set view changed
				view.getViewManager().viewChanged();

				// Refresh
				view.refresh();
			}
		}
	}

	/**
	 * Display a new resource selection dialog.
	 * 
	 * @param evidence The edited evidence
	 * @return the list of the selected resources
	 */
	List<PCMMEvidence> showEvidenceDialog(PCMMEvidence evidence, IAssessable item, IFile defaultFile) {
		// Get last selected file
		if (null == getLastSelectedFile()) {
			view.setLastSelectedFile(WorkspaceTools.getActiveCfFile());
		}

		// Evidence dialog to add/edit the evidence
		DialogMode mode = evidence == null ? DialogMode.CREATE : DialogMode.UPDATE;
		if (evidence == null) {
			evidence = new PCMMEvidence();
		}
		if (defaultFile != null) {
			evidence.setFilePath(defaultFile.getFullPath().toPortableString());
		}
		PCMMEvidenceDialog dialog = new PCMMEvidenceDialog(view.getViewManager(), view.getShell(), evidence, item,
				mode);

		// open the dialog
		return Arrays.asList(dialog.openDialog());
	}

	/**
	 * Delete the selected evidence
	 * 
	 * @param evidences the evidence to delete
	 */
	void deleteEvidence(PCMMEvidence evidence) {

		// delete the evidence
		try {

			/** confirm delete **/

			// constructs confirm message
			String deleteDetailMessage = RscTools.empty();
			deleteDetailMessage += evidence != null
					? RscTools.getString(RscConst.MSG_PCMMEVID_DELETE_CONFIRM_QUESTION, evidence.getName())
					: RscTools.empty();

			// confirm dialog
			boolean confirm = MessageDialog.openConfirm(view.getShell(),
					RscTools.getString(RscConst.MSG_PCMMEVID_DELETE_CONFIRM_TITLE), deleteDetailMessage);

			if (confirm && evidence != null) {

				/**
				 * Delete from phenomena viewer
				 */
				if (evidence.getElement() != null) {
					getPcmmElement().getEvidenceList().remove(evidence);
				} else if (evidence.getSubelement() != null) {
					int index = getPcmmElement().getSubElementList().indexOf(evidence.getSubelement());
					if (index >= 0) {
						PCMMSubelement sub = getPcmmElement().getSubElementList().get(index);
						sub.getEvidenceList().remove(evidence);
					}
				}

				/**
				 * Delete from database
				 */
				view.getViewManager().getAppManager().getService(IPCMMApplication.class).deleteEvidence(evidence);

				// Set view changed
				view.getViewManager().viewChanged();

				// Refresh
				view.refresh();
			}
		} catch (CredibilityException e) {
			logger.error("An error has occurred while deleting PCMMevidence:\n{}", e.getMessage(), e); //$NON-NLS-1$
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_DELETING_MSG) + e.getMessage());
		}
	}

	/**
	 * Edit the evidence in database and associate it with the assessable (PCMM
	 * element or subelement)
	 * 
	 * @param evidence      the evidence to add
	 * @param newAssessable the new assessable to set (PCMM element or subelement)
	 */
	public void editEvidenceResource(PCMMEvidence evidence, IAssessable newAssessable) {

		if (evidence == null || newAssessable == null) {
			return;
		}

		boolean changed = false;

		// check
		try {
			view.getViewManager().getAppManager().getService(IPCMMApplication.class)
					.checkEvidenceWithSamePathInAssessable(evidence.getValue(), evidence.getSection(), newAssessable);
		} catch (CredibilityException e) {
			logger.error("An error has occurred while updating PCMM evidence: \n{}", e.getMessage(), e); //$NON-NLS-1$
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_ADDING_MSG) + e.getMessage());
			return;
		}

		// set changes
		if (newAssessable instanceof PCMMElement) {
			evidence.setElement((PCMMElement) newAssessable);
		} else if (newAssessable instanceof PCMMSubelement) {
			evidence.setSubelement((PCMMSubelement) newAssessable);
		}

		// update
		try {
			view.getViewManager().getAppManager().getService(IPCMMApplication.class).updateEvidence(evidence);
			changed |= true;
		} catch (CredibilityException e) {
			logger.error("An error has occurred while updating PCMM evidence: \n{}", e.getMessage(), e); //$NON-NLS-1$
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_ADDING_MSG) + e.getMessage());
		}

		if (changed) {
			// Set view changed
			view.getViewManager().viewChanged();
		}
	}

	/**
	 * {@link PCMMViewManager#isTagMode()}
	 * 
	 * @return true if the selected tag is a real tag, otherwise false
	 */
	public boolean isTagMode() {
		return view.getViewManager().isTagMode();
	}

	/**
	 * @return the pcmm element
	 */
	public PCMMElement getPcmmElement() {
		return view.getPcmmElement();
	}

	/**
	 * @return the last selected file
	 */
	public IFile getLastSelectedFile() {
		return view.getLastSelectedFile();
	}

	/**
	 * @return the pcmm configuration
	 */
	public PCMMSpecification getPCMMConfiguration() {
		return view.getViewManager().getPCMMConfiguration();
	}

	/**
	 * @return the pcmm mode
	 */
	public PCMMMode getPCMMMode() {
		return view.getViewManager().getPCMMConfiguration() != null
				? view.getViewManager().getPCMMConfiguration().getMode()
				: null;
	}
}
