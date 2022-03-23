/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt;

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
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.comparator.StringWithNumberAndNullableComparator;
import gov.sandia.cf.tools.IDTools;

/**
 * The QuantityOfInterest View drop support class
 * 
 * @author Didier Verstraete
 *
 */
public class QoIDropSupport extends ViewerDropAdapter {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(QoIDropSupport.class);

	/** The view manager. */
	private IQoIViewController viewCtrl;

	/**
	 * Constructor.
	 *
	 * @param viewCtrl the view ctrl
	 * @param viewer   the viewer
	 */
	public QoIDropSupport(IQoIViewController viewCtrl, Viewer viewer) {
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
			QuantityOfInterest droppedQuantityOfInterest = moveQuantityOfInterest(element, tmpTarget, location);
			dropSuccessful = droppedQuantityOfInterest != null;
			// if there are several items selected, the new target is the latest item
			// reordered
			if (dropSuccessful && target instanceof QuantityOfInterest
					&& (location == LOCATION_AFTER || location == LOCATION_ON)) {
				tmpTarget = droppedQuantityOfInterest;
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
	 * Move QuantityOfInterest.
	 *
	 * @param dragged the dragged object
	 * @param target  the drop target
	 * @param location the location
	 * @return true if the QuantityOfInterest has been changed, otherwise false
	 */
	protected QuantityOfInterest moveQuantityOfInterest(Object dragged, Object target, int location) {

		if (!(dragged instanceof QuantityOfInterest) || ((QuantityOfInterest) dragged).getTagDate() != null
				|| !(target instanceof QuantityOfInterest) || ((QuantityOfInterest) target).getTagDate() != null
				|| Objects.equals(dragged, target) || location == LOCATION_NONE) {
			return null;
		}

		// reorder if the evidence is dropped on an evidence
		reorder((QuantityOfInterest) dragged, (QuantityOfInterest) target, location == LOCATION_BEFORE);

		return (QuantityOfInterest) dragged;
	}

	/**
	 * Reorder QuantityOfInterest.
	 *
	 * @param dragged the QuantityOfInterest to drag
	 * @param target  the QuantityOfInterest target
	 * @param before  the location
	 * @return true if the QuantityOfInterest has been reordered, otherwise false
	 */
	protected boolean reorder(QuantityOfInterest dragged, QuantityOfInterest target, boolean before) {
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
			logger.error("Impossible to reorder QuantityOfInterest {} {} {}: {}", dragged, before ? "before" : "after", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					target, e.getMessage(), e);
			return false;
		}

		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		// it is just permitted to drag and drop QuantityOfInterest
		Object source = getSelectedObject();
		return source instanceof QuantityOfInterest && ((QuantityOfInterest) source).getParent() == null
				&& ((QuantityOfInterest) source).getTagDate() == null;
	}

}
