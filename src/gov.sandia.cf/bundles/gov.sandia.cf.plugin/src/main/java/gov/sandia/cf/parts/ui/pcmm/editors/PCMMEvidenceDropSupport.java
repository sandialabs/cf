/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.parts.ui.pcmm.PCMMEvidenceViewController;

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
		 * Check the PCMM Mode
		 */
		if (PCMMMode.DEFAULT.equals(viewCtrl.getPCMMMode())) {
			return performDropDefaultMode(target, source);
		} else if (PCMMMode.SIMPLIFIED.equals(viewCtrl.getPCMMMode())) {
			return performDropSimplifiedMode(target, source);
		}

		return false;
	}

	/**
	 * Perform drop for DEFAULT mode
	 * 
	 * @param target the target
	 * @param source the source
	 * @return true if the drop is successful, otherwise false
	 */
	private boolean performDropDefaultMode(Object target, Object source) {

		/**
		 * Get the target
		 */
		PCMMSubelement subeltTarget = null;

		// if the file is dropped on a subelement
		if (target instanceof PCMMSubelement) {
			subeltTarget = (PCMMSubelement) target;
		}
		// if the file is dropped on an evidence: get the subelement
		if (target instanceof PCMMEvidence) {
			subeltTarget = ((PCMMEvidence) target).getSubelement();
		}

		/**
		 * Get the source
		 */
		// only if the target is an element or subelement of the selected PCMM element
		if (!(subeltTarget != null && subeltTarget.getElement() != null
				&& subeltTarget.getElement().equals(viewCtrl.getPcmmElement()))) {
			return false;
		}

		if (!(source instanceof IStructuredSelection)) {
			return false;
		}

		boolean dropSuccessful = false;
		Object element = ((IStructuredSelection) source).getFirstElement();

		// if the dragged element is an IResource from the project explorer
		if (element instanceof IAdaptable) {

			final Iterator<?> i = ((IStructuredSelection) source).iterator();
			while (i.hasNext()) {
				IFile fileDragged = ((IAdaptable) i.next()).getAdapter(IFile.class);

				// add dragged evidence
				viewCtrl.addEvidence(subeltTarget, fileDragged);

				dropSuccessful = true;
			}
		}
		// if the dragged element is an Evidence from the current PCMM Element
		else if (element instanceof PCMMEvidence) {

			final Iterator<?> i = ((IStructuredSelection) source).iterator();
			while (i.hasNext()) {
				PCMMEvidence evidenceDragged = (PCMMEvidence) i.next();
				PCMMSubelement oldSubeltTarget = evidenceDragged.getSubelement();

				// if the phenomenon is not dropped on his current group
				if (!subeltTarget.equals(oldSubeltTarget)) {

					// update dragged evidence
					viewCtrl.editEvidenceResource(evidenceDragged, subeltTarget);

					dropSuccessful = true;
				}
			}
		}

		if (dropSuccessful) {
			logger.debug("The drop of: {}\nhas been done on the element: {}", source, //$NON-NLS-1$
					target);
		}

		return dropSuccessful;
	}

	/**
	 * Perform drop for SIMPLIFIED mode
	 * 
	 * @param target the target
	 * @param source the source
	 * @return true if the drop is successful, otherwise false
	 */
	private boolean performDropSimplifiedMode(Object target, Object source) {

		/**
		 * Get the target
		 */
		PCMMElement eltTarget = null;

		// if the file is dropped on a element
		if (target instanceof PCMMElement) {
			eltTarget = (PCMMElement) target;
		}
		// if the file is dropped on an evidence: get the element
		if (target instanceof PCMMEvidence) {
			eltTarget = ((PCMMEvidence) target).getElement();
		}

		/**
		 * Get the source
		 */
		// only if the target is an element of the selected PCMM element
		if (!(eltTarget != null && eltTarget.equals(viewCtrl.getPcmmElement()))) {
			return false;
		}

		// if the dragged element is an IResource from the project explorer
		if (!(source instanceof IStructuredSelection)) {
			return false;
		}

		boolean dropSuccessful = false;
		Object element = ((IStructuredSelection) source).getFirstElement();

		if (element instanceof IAdaptable) {

			final Iterator<?> i = ((IStructuredSelection) source).iterator();
			while (i.hasNext()) {
				IFile fileDragged = ((IAdaptable) i.next()).getAdapter(IFile.class);

				// add dragged evidence
				viewCtrl.addEvidence(eltTarget, fileDragged);

				dropSuccessful = true;
			}
		}
		// if the dragged element is an Evidence from the current PCMM Element
		else if (element instanceof PCMMEvidence) {

			final Iterator<?> i = ((IStructuredSelection) source).iterator();
			while (i.hasNext()) {
				PCMMEvidence evidenceDragged = (PCMMEvidence) i.next();
				PCMMElement oldEltTarget = evidenceDragged.getElement();

				// if the evidence is not dropped on his current element
				if (!eltTarget.equals(oldEltTarget)) {

					// update dragged evidence
					viewCtrl.editEvidenceResource(evidenceDragged, eltTarget);

					dropSuccessful = true;
				}
			}
		}

		if (dropSuccessful) {
			logger.debug("The drop of: {}\nhas been done on the element: {}", source, //$NON-NLS-1$
					target);
		}

		return dropSuccessful;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {

		if (viewCtrl.isTagMode()) {
			return false;
		}

		boolean validTarget = true;
		PCMMElement element = null;

		/**
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
		validTarget = element != null && element.equals(viewCtrl.getPcmmElement());

		return LocalSelectionTransfer.getTransfer().isSupportedType(transferType) && validTarget;
	}
}
