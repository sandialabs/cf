/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.comparator.StringWithNumberAndNullableComparator;
import gov.sandia.cf.parts.ui.pcmm.PCMMEvidenceViewController;
import gov.sandia.cf.tools.IDTools;

/**
 * The PCMM Evidence View drop support class
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceDropSupport extends ViewerDropAdapter {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMEvidenceDropSupport.class);

	private PCMMEvidenceViewController viewCtrl;

	/**
	 * Constructor
	 * 
	 * @param viewCtrl the pcmm evidence view controller
	 * @param viewer   the viewer widget to control
	 */
	public PCMMEvidenceDropSupport(PCMMEvidenceViewController viewCtrl, Viewer viewer) {
		super(viewer);
		Assert.isNotNull(viewCtrl);

		this.viewCtrl = viewCtrl;
	}

	@Override
	public boolean performDrop(Object data) {

		Object target = getCurrentTarget();
		Object source = (data != null) ? data : getViewer().getSelection();

		if (viewCtrl.isTagMode() && source != null && target != null) {
			return false;
		}

		/**
		 * Get the source
		 */
		if (!(source instanceof IStructuredSelection)) {
			return false;
		}

		boolean dropSuccessful = true;
		int location = determineLocation(getCurrentEvent());
		List<?> toDropList = ((IStructuredSelection) source).toList();

		if (location == LOCATION_AFTER || (location == LOCATION_ON && target instanceof PCMMEvidence)) {
			Collections.reverse(toDropList);
		}

		// process drop for each item
		for (Object element : toDropList) {
			PCMMEvidence droppedEvidence = performDropItem(element, target, location);
			dropSuccessful &= droppedEvidence != null;
		}

		if (dropSuccessful) {
			logger.debug("The drop of: {}\nhas been done on the element: {}", source, target);//$NON-NLS-1$

			// refresh the view
			Display.getCurrent().asyncExec(() -> viewCtrl.refreshIfChanged());
		}

		return dropSuccessful;
	}

	/**
	 * Perform drop item.
	 *
	 * @param element  the element
	 * @param target   the target
	 * @param location the location
	 * @return the PCMM evidence dropped
	 */
	private PCMMEvidence performDropItem(Object element, Object target, int location) {

		/**
		 * Get the target
		 */
		IAssessable assessableTarget = getAssessableTarget(target);
		Object newTarget = target;
		PCMMEvidence droppedEvidence = null;

		if (assessableTarget == null) {
			return null;
		}

		/**
		 * Process the drop
		 */
		// if the dragged element is an IResource from the project explorer
		if (element instanceof IAdaptable) {
			droppedEvidence = performDropIFile(element, assessableTarget, newTarget, location);
		}
		// if the dragged element is an Evidence from the current assessable
		else if (element instanceof PCMMEvidence) {
			droppedEvidence = performDropEvidence(element, assessableTarget, target, location);
		}

		return droppedEvidence;
	}

	/**
	 * Gets the assessable target.
	 *
	 * @param target the target
	 * @return the assessable target
	 */
	private IAssessable getAssessableTarget(Object target) {

		IAssessable assessableTarget = null;

		if (PCMMMode.DEFAULT.equals(viewCtrl.getPCMMMode())) {
			// if the file is dropped on a subelement
			if (target instanceof PCMMSubelement) {
				assessableTarget = (PCMMSubelement) target;
			}
			// if the file is dropped on an evidence: get the subelement
			if (target instanceof PCMMEvidence) {
				assessableTarget = ((PCMMEvidence) target).getSubelement();
			}

			// only if the target is an element or subelement of the selected PCMM element
			if (!(assessableTarget != null && ((PCMMSubelement) assessableTarget).getElement() != null
					&& ((PCMMSubelement) assessableTarget).getElement().equals(viewCtrl.getElementSelected()))) {
				return null;
			}
		} else if (PCMMMode.SIMPLIFIED.equals(viewCtrl.getPCMMMode())) {
			// if the file is dropped on a element
			if (target instanceof PCMMElement) {
				assessableTarget = (PCMMElement) target;
			}
			// if the file is dropped on an evidence: get the element
			if (target instanceof PCMMEvidence) {
				assessableTarget = ((PCMMEvidence) target).getElement();
			}

			// only if the target is an element of the selected PCMM element
			if (!(assessableTarget != null && assessableTarget.equals(viewCtrl.getElementSelected()))) {
				return null;
			}
		}

		return assessableTarget;
	}

	/**
	 * Perform drop I file.
	 *
	 * @param element          the element
	 * @param assessableTarget the assessable target
	 * @param target           the target
	 * @param location         the location
	 * @return the PCMM evidence
	 */
	private PCMMEvidence performDropIFile(Object element, IAssessable assessableTarget, Object target, int location) {

		IFile fileDragged = ((IAdaptable) element).getAdapter(IFile.class);

		// add dragged evidence
		PCMMEvidence evidenceCreated = viewCtrl.addEvidence(assessableTarget, fileDragged);

		// reorder if the evidence is dropped on an evidence
		if (evidenceCreated != null && target instanceof PCMMEvidence) {
			reorder(evidenceCreated, (PCMMEvidence) target, location == LOCATION_BEFORE);
		}

		return evidenceCreated;
	}

	/**
	 * Perform drop evidence.
	 *
	 * @param element          the element
	 * @param assessableTarget the assessable target
	 * @param target           the target
	 * @param location         the location
	 * @return the PCMM evidence
	 */
	private PCMMEvidence performDropEvidence(Object element, IAssessable assessableTarget, Object target,
			int location) {

		PCMMEvidence evidenceDragged = (PCMMEvidence) element;
		PCMMEvidence evidenceUpdated = null;
		IAssessable oldAssessable = null;

		if (PCMMMode.DEFAULT.equals(viewCtrl.getPCMMMode())) {
			oldAssessable = evidenceDragged.getSubelement();
		} else if (PCMMMode.SIMPLIFIED.equals(viewCtrl.getPCMMMode())) {
			oldAssessable = evidenceDragged.getElement();
		}

		// if the evidence is not dropped on his current assessable
		if (!assessableTarget.equals(oldAssessable)) {

			// reset generated id
			evidenceDragged.setGeneratedId(null);

			// update dragged evidence
			evidenceUpdated = viewCtrl.moveEvidence(evidenceDragged, assessableTarget);
		}

		// reorder if the evidence is dropped on an evidence
		if (target instanceof PCMMEvidence) {
			reorder(evidenceDragged, (PCMMEvidence) target, location == LOCATION_BEFORE);
			evidenceUpdated = evidenceDragged;
		}

		return evidenceUpdated;
	}

	/**
	 * Reorder.
	 *
	 * @param dragged the dragged
	 * @param target  the target
	 * @param before  the before
	 * @return true, if successful
	 */
	private boolean reorder(PCMMEvidence dragged, PCMMEvidence target, boolean before) {

		if (dragged == null) {
			return false;
		}

		try {

			// offset used to drag the item depending of its location compared to the target
			int offset = 0;

			// affect the dragged item depending of the location
			if (before) {

				if (new StringWithNumberAndNullableComparator().compare(dragged.getGeneratedId(),
						target.getGeneratedId()) <= 0) {
					offset = 1;
				}

				// reorder
				viewCtrl.reorderEvidence(dragged, IDTools.getPositionInSet(target.getGeneratedId()) - offset);
			} else {

				// offset used to drag the item depending of its location compared to the target
				if (new StringWithNumberAndNullableComparator().compare(dragged.getGeneratedId(),
						target.getGeneratedId()) > 0) {
					offset = 1;
				}

				// reorder
				viewCtrl.reorderEvidence(dragged, IDTools.getPositionInSet(target.getGeneratedId()) + offset);
			}

			logger.debug("The drop of {} {} {} was done", dragged, before ? "before" : "after", target); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (CredibilityException e) {
			logger.error("Impossible to reorder uncertainty {} {} {}: {}", dragged, before ? "before" : "after", target, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					e.getMessage(), e);
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {

		if (viewCtrl.isTagMode()) {
			return false;
		}

		boolean validTarget = true;
		PCMMElement element = null;
		Object source = getSelectedObject();

		/*
		 * Check the source
		 */
		validTarget &= source instanceof PCMMEvidence;

		/*
		 * Check the PCMM mode
		 */
		if (PCMMMode.DEFAULT.equals(viewCtrl.getPCMMMode())) {
			// validate target if it is a subelement or an evidence and if this target is in
			// part of the selected PCMM element of the view
			if (target instanceof PCMMSubelement) {
				element = ((PCMMSubelement) target).getElement();
			} else if (target instanceof PCMMEvidence) {
				PCMMSubelement sub = ((PCMMEvidence) target).getSubelement();
				element = sub != null ? sub.getElement() : null;
			}
		} else if (PCMMMode.SIMPLIFIED.equals(viewCtrl.getPCMMMode())) {
			// validate target if it is an element or an evidence and if this target is in
			// part of the selected PCMM element of the view
			if (target instanceof PCMMElement) {
				element = ((PCMMElement) target);
			} else if (target instanceof PCMMEvidence) {
				element = ((PCMMEvidence) target).getElement();
			}
		}

		// check the validation
		validTarget &= element != null && element.equals(viewCtrl.getElementSelected());

		return LocalSelectionTransfer.getTransfer().isSupportedType(transferType) && validTarget;
	}
}
