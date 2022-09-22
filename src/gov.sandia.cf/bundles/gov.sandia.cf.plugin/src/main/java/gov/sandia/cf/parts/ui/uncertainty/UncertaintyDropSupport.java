/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.uncertainty;

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
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.comparator.StringWithNumberAndNullableComparator;
import gov.sandia.cf.tools.IDTools;

/**
 * The Uncertainty View drop support class
 * 
 * @author Didier Verstraete
 *
 */
public class UncertaintyDropSupport extends ViewerDropAdapter {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(UncertaintyDropSupport.class);

	/** The view manager. */
	private UncertaintyViewController viewCtrl;

	/**
	 * Constructor.
	 *
	 * @param viewCtrl the view ctrl
	 * @param viewer   the viewer
	 */
	public UncertaintyDropSupport(UncertaintyViewController viewCtrl, Viewer viewer) {
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
			Uncertainty droppedUncertainty = moveUncertainty(element, tmpTarget, location);
			dropSuccessful = droppedUncertainty != null;
			// if there are several items selected, the new target is the latest item
			// reordered
			if (dropSuccessful && target instanceof Uncertainty && location == LOCATION_AFTER) {
				tmpTarget = droppedUncertainty;
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
	 * Move uncertainty.
	 *
	 * @param dragged the dragged object
	 * @param target  the drop target
	 * @param location the location
	 * @return true if the uncertainty has been changed, otherwise false
	 */
	protected Uncertainty moveUncertainty(Object dragged, Object target, int location) {

		if (dragged == null || !(target instanceof Uncertainty) || Objects.equals(dragged, target)
				|| location == LOCATION_NONE) {
			return null;
		}

		Uncertainty uncertaintyDragged = (Uncertainty) dragged;
		Uncertainty uncertaintyUpdated = null;
		Uncertainty newParent = location == LOCATION_ON ? (Uncertainty) target : ((Uncertainty) target).getParent();
		Uncertainty previousParent = uncertaintyDragged.getParent();

		// get the drag location
		if (!Objects.equals(newParent, previousParent)) {

			// set new parent
			uncertaintyDragged.setParent(newParent);

			// reset generated id
			uncertaintyDragged.setGeneratedId(null);

			// update
			try {
				uncertaintyUpdated = viewCtrl.updateUncertainty(uncertaintyDragged);
			} catch (CredibilityException e) {
				logger.error("Impossible to move uncertainty {} to {}: {}", uncertaintyDragged, newParent, //$NON-NLS-1$
						e.getMessage(), e);
				return null;
			}

			// refresh previous parent
			viewCtrl.refreshUncertainty(previousParent);

			logger.debug("The drop of {} was done on {}", uncertaintyDragged, target); //$NON-NLS-1$
		} else {
			uncertaintyUpdated = uncertaintyDragged;
		}

		// reorder
		if (location != LOCATION_ON) {
			reorder(uncertaintyUpdated, (Uncertainty) target, location == LOCATION_BEFORE);
		}

		// if the parent changed, reorder all the items with the highest level impacted
		// to have good IDs
		if (newParent == null || previousParent == null || !Objects.equals(newParent, previousParent)) {
			viewCtrl.reorderAll();
		}

		return uncertaintyUpdated;
	}

	/**
	 * Reorder uncertainty.
	 *
	 * @param dragged the Uncertainty to drag
	 * @param target  the Uncertainty target
	 * @param before  the location
	 * @return true if the Uncertainty has been reordered, otherwise false
	 */
	protected boolean reorder(Uncertainty dragged, Uncertainty target, boolean before) {
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
			logger.error("Impossible to reorder uncertainty {} {} {}: {}", dragged, before ? "before" : "after", target, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					e.getMessage(), e);
			return false;
		}

		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		// it is just permitted to drag and drop Uncertainty
		Object source = getSelectedObject();
		return source instanceof Uncertainty;
	}

}
