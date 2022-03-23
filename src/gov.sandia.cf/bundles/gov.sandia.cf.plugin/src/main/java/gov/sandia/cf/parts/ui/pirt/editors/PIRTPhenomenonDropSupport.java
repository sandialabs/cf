/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.Collections;
import java.util.List;
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
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.comparator.StringWithNumberAndNullableComparator;
import gov.sandia.cf.parts.ui.pirt.PIRTPhenomenaViewController;
import gov.sandia.cf.tools.IDTools;
import gov.sandia.cf.tools.RscTools;

/**
 * The PIRT Phenomena View drop support class
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTPhenomenonDropSupport extends ViewerDropAdapter {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTPhenomenonDropSupport.class);

	/** The view ctrl. */
	private PIRTPhenomenaViewController viewCtrl;

	/**
	 * Constructor.
	 *
	 * @param viewCtrl the view ctrl
	 * @param viewer   the viewer
	 */
	public PIRTPhenomenonDropSupport(PIRTPhenomenaViewController viewCtrl, Viewer viewer) {
		super(viewer);
		Assert.isNotNull(viewCtrl);

		this.viewCtrl = viewCtrl;
	}

	@Override
	public boolean performDrop(Object data) {

		Object source = (data != null) ? data : getViewer().getSelection();
		Object target = getCurrentTarget();

		if (target == null || !(source instanceof IStructuredSelection)) {
			return false;
		}

		boolean dropSuccessful = true;
		int location = determineLocation(getCurrentEvent());
		List<?> toDropList = ((IStructuredSelection) source).toList();

		if (location == LOCATION_AFTER || (location == LOCATION_ON && target instanceof Phenomenon)) {
			Collections.reverse(toDropList);
		}

		// process drop for each item
		for (Object element : toDropList) {
			if (element instanceof PhenomenonGroup) {
				dropSuccessful &= movePhenomenonGroups((PhenomenonGroup) element, target, location == LOCATION_BEFORE);
			} else if (element instanceof Phenomenon) {
				dropSuccessful &= movePhenomenon((Phenomenon) element, target, location == LOCATION_BEFORE);
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
	 * Move phenomenon groups.
	 *
	 * @param dragged the group to move
	 * @param target  the target group
	 * @param before  the before
	 * @return true if the phenomenon groups has been changed, otherwise false
	 */
	protected boolean movePhenomenonGroups(PhenomenonGroup dragged, Object target, boolean before) {

		if (dragged == null) {
			return false;
		}

		// get phenomenon group
		PhenomenonGroup groupTarget = null;
		if (target instanceof PhenomenonGroup) {
			groupTarget = (PhenomenonGroup) target;
		} else if (target instanceof Phenomenon) {
			groupTarget = ((Phenomenon) target).getPhenomenonGroup();
		}

		if (groupTarget == null) {
			return false;
		}

		try {

			// affect the dragged item depending of the location
			if (before) {

				// offset used to drag the item depending of its location compared to the target
				int offset = 0;
				if (dragged.getIdLabel() != null &&

						new StringWithNumberAndNullableComparator().compare(dragged.getIdLabel(),
								groupTarget.getIdLabel()) <= 0) {
					offset = 1;
				}

				// reorder
				viewCtrl.reorderPhenomenonGroups(dragged,
						IDTools.reverseGenerateAlphabeticIdRecursive(groupTarget.getIdLabel()) - offset);
			} else {

				// offset used to drag the item depending of its location compared to the target
				int offset = 0;
				if (dragged.getIdLabel() != null && new StringWithNumberAndNullableComparator()
						.compare(dragged.getIdLabel(), groupTarget.getIdLabel()) > 0) {
					offset = 1;
				}

				// reorder
				viewCtrl.reorderPhenomenonGroups(dragged,
						IDTools.reverseGenerateAlphabeticIdRecursive(groupTarget.getIdLabel()) + offset);
			}

		} catch (CredibilityException e) {
			logger.error("Impossible to reorder phenomenon group: {}", e.getMessage(), e); //$NON-NLS-1$
			return false;
		}

		return true;
	}

	/**
	 * Move phenomenon.
	 *
	 * @param dragged the dragged object
	 * @param target  the drop target
	 * @param before  the before
	 * @return true if the phenomenon groups has been changed, otherwise false
	 */
	protected boolean movePhenomenon(Phenomenon dragged, Object target, boolean before) {

		if (dragged == null) {
			return false;
		}

		PhenomenonGroup groupTarget = null;
		Phenomenon phenomenonTarget = null;
		PhenomenonGroup previousGroup = dragged.getPhenomenonGroup();
		boolean needRepositioning = false;
		boolean moved = false;

		// if phenomenon is dropped on a group
		if (target instanceof PhenomenonGroup) {
			groupTarget = (PhenomenonGroup) target;
		}
		// if phenomenon is dropped on a phenomenon: get the group of the phenomenon
		else if (target instanceof Phenomenon) {
			groupTarget = ((Phenomenon) target).getPhenomenonGroup();
			phenomenonTarget = (Phenomenon) target;
			needRepositioning = true;
		}

		if (groupTarget == null) {
			return false;
		}

		if (!Objects.equals(groupTarget, previousGroup)) {

			// reset generated id
			dragged.setIdLabel(null);

			// set new group
			dragged.setPhenomenonGroup(groupTarget);

			try {
				// update dragged phenomenon
				viewCtrl.updatePhenomenon(dragged);

			} catch (CredibilityException e) {
				logger.error("An error occured while updating phenomenon: {}", RscTools.carriageReturn() //$NON-NLS-1$
						+ e.getMessage(), e);
				return false;
			}

			// refresh
			viewCtrl.refreshPhenomenonGroup(groupTarget);
			viewCtrl.refreshPhenomenonGroup(previousGroup);

			logger.debug("The drop of: {} was done on the element: {}", dragged, target); //$NON-NLS-1$
		}

		if (needRepositioning) {
			moved = reorderPhenomenon(dragged, phenomenonTarget, before);
		}

		// if the parent changed, reorder all the items with the highest level impacted
		// to have good IDs
		if (!Objects.equals(groupTarget, previousGroup)) {
			try {
				viewCtrl.reorderPhenomenaForGroup(previousGroup);
				viewCtrl.reorderPhenomenaForGroup(groupTarget);
			} catch (CredibilityException e) {
				logger.error("Impossible to update phenomenon: {}", e.getMessage(), e); //$NON-NLS-1$
				return false;
			}
		}

		return moved;
	}

	/**
	 * @param dragged the phenomenon to drag
	 * @param target  the phenomenon target
	 * @param before  the location
	 * @return true if the phenomenon groups has been reordered, otherwise false
	 */
	protected boolean reorderPhenomenon(Phenomenon dragged, Phenomenon target, boolean before) {
		try {

			// affect the dragged item depending of the location
			if (before) {

				// offset used to drag the item depending of its location compared to the target
				int offset = 0;
				if (dragged.getIdLabel() != null && new StringWithNumberAndNullableComparator()
						.compare(dragged.getIdLabel(), target.getIdLabel()) <= 0) {
					offset = 1;
				}

				// reorder
				viewCtrl.reorderPhenomenon(dragged, IDTools.getPositionInSet(target.getIdLabel()) - offset);
			} else {

				// offset used to drag the item depending of its location compared to the target
				int offset = 0;
				if (dragged.getIdLabel() != null && new StringWithNumberAndNullableComparator()
						.compare(dragged.getIdLabel(), target.getIdLabel()) > 0) {
					offset = 1;
				}

				// reorder
				viewCtrl.reorderPhenomenon(dragged, IDTools.getPositionInSet(target.getIdLabel()) + offset);
			}
		} catch (CredibilityException e) {
			logger.error("Impossible to reorder phenomenon group: {}", e.getMessage(), e); //$NON-NLS-1$
			return false;
		}

		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		// it is just permitted to drag and drop Phenomenon not PhenomenonGroup
		Object source = getSelectedObject();
		boolean valid = source instanceof PhenomenonGroup || source instanceof Phenomenon;
		valid &= target instanceof PhenomenonGroup || target instanceof Phenomenon;
		return valid;
	}

}
