/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.decision;

import java.util.Objects;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.comparator.StringWithNumberAndNullableComparator;
import gov.sandia.cf.tools.IDTools;

/**
 * The Decision View drop support class
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionDropSupport extends ViewerDropAdapter {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(DecisionDropSupport.class);

	/** The view manager. */
	private DecisionViewController viewCtrl;

	/**
	 * Constructor.
	 *
	 * @param viewCtrl the view ctrl
	 * @param viewer   the viewer
	 */
	public DecisionDropSupport(DecisionViewController viewCtrl, Viewer viewer) {
		super(viewer);
		Assert.isNotNull(viewCtrl);

		this.viewCtrl = viewCtrl;
	}

	@Override
	public boolean performDrop(Object data) {

		Object target = getCurrentTarget();
		Object source = (data != null) ? data : getViewer().getSelection();

		if (target == null || !(source instanceof IStructuredSelection)) {
			return false;
		}

		boolean dropSuccessful = false;
		int location = determineLocation(getCurrentEvent());
		Object tmpTarget = target;

		// process drop for each item
		for (Object element : ((IStructuredSelection) source).toList()) {
			Decision droppedDecision = moveDecision(element, tmpTarget, location);
			dropSuccessful = droppedDecision != null;
			// if there are several items selected, the new target is the latest item
			// reordered
			if (dropSuccessful && target instanceof Decision && location == LOCATION_AFTER) {
				tmpTarget = droppedDecision;
			}
		}

		if (dropSuccessful) {
			logger.debug("The drop of: {}\nhas been done on the element: {}", source, target);//$NON-NLS-1$

			// refresh the view
			Display.getCurrent().asyncExec(() -> viewCtrl.refreshIfChanged());
		}

		return dropSuccessful;
	}

	/**
	 * Move Decision.
	 *
	 * @param dragged the dragged object
	 * @param target  the drop target
	 * @param location the location
	 * @return true if the Decision has been changed, otherwise false
	 */
	protected Decision moveDecision(Object dragged, Object target, int location) {

		if (dragged == null || !(target instanceof Decision) || Objects.equals(dragged, target)
				|| location == LOCATION_NONE) {
			return null;
		}

		Decision decisionDragged = (Decision) dragged;
		Decision decisionUpdated = null;
		Decision newParent = location == LOCATION_ON ? (Decision) target : ((Decision) target).getParent();
		Decision previousParent = decisionDragged.getParent();

		// get the drag location
		if (!Objects.equals(newParent, previousParent)) {

			// set new parent
			decisionDragged.setParent(newParent);

			// reset generated id
			decisionDragged.setGeneratedId(null);

			// update
			try {
				decisionUpdated = viewCtrl.updateDecision(decisionDragged);
			} catch (CredibilityException e) {
				logger.error("Impossible to move Decision {} to {}: {}", decisionDragged, newParent, //$NON-NLS-1$
						e.getMessage(), e);
				return null;
			}

			// refresh previous parent
			viewCtrl.refreshDecision(previousParent);

			logger.debug("The drop of {} was done on {}", decisionDragged, target); //$NON-NLS-1$
		}

		if (location != LOCATION_ON) {
			reorder(decisionUpdated, (Decision) target, location == LOCATION_BEFORE);
		}

		// if the parent changed, reorder all the items with the highest level impacted
		// to have good IDs
		if (newParent == null || previousParent == null || !Objects.equals(newParent, previousParent)) {
			viewCtrl.reorderAll();
		}

		return decisionUpdated;
	}

	/**
	 * Reorder Decision.
	 *
	 * @param dragged the Decision to drag
	 * @param target  the Decision target
	 * @param before  the location
	 * @return true if the Decision has been reordered, otherwise false
	 */
	protected boolean reorder(Decision dragged, Decision target, boolean before) {
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
				viewCtrl.reorder(dragged, IDTools.getPositionInSet(target.getGeneratedId()) - offset);
			} else {

				// offset used to drag the item depending of its location compared to the target
				if (new StringWithNumberAndNullableComparator().compare(dragged.getGeneratedId(),
						target.getGeneratedId()) > 0) {
					offset = 1;
				}

				// reorder
				viewCtrl.reorder(dragged, IDTools.getPositionInSet(target.getGeneratedId()) + offset);
			}

			logger.debug("The drop of {} {} {} was done", dragged, before ? "before" : "after", target); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} catch (CredibilityException e) {
			logger.error("Impossible to reorder Decision {} {} {}: {}", dragged, before ? "before" : "after", target, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					e.getMessage(), e);
			return false;
		}

		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		// it is just permitted to drag and drop Decision
		Object source = getSelectedObject();
		return source instanceof Decision;
	}

}
