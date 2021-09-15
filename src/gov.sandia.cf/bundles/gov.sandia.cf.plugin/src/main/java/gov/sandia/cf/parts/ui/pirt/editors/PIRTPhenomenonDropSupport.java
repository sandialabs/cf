/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.parts.ui.pirt.PIRTPhenomenaView;
import gov.sandia.cf.tools.IDTools;

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

	private PIRTPhenomenaView view;

	/**
	 * Constructor
	 * 
	 * @param view   the pirt view
	 * @param viewer the viewer
	 */
	public PIRTPhenomenonDropSupport(PIRTPhenomenaView view, Viewer viewer) {
		super(viewer);
		Assert.isNotNull(view);
		Assert.isNotNull(view.getViewManager());

		this.view = view;
	}

	@Override
	public boolean performDrop(Object data) {

		Object source = getSelectedObject();
		Object target = getCurrentTarget();

		if (target == null) {
			return false;
		}

		if (source instanceof PhenomenonGroup) {
			return movePhenomenonGroups((PhenomenonGroup) source, target);
		} else if (source instanceof Phenomenon) {
			return movePhenomenon((Phenomenon) source, target);
		}

		return false;
	}

	/**
	 * 
	 * @param dragged the group to move
	 * @param target  the target group
	 * @return true if the phenomenon groups has been changed, otherwise false
	 */
	protected boolean movePhenomenonGroups(PhenomenonGroup dragged, Object target) {

		if (dragged == null) {
			return false;
		}

		// default drag location is after
		boolean before = false;

		// get phenomenon group
		PhenomenonGroup groupTarget = null;
		if (target instanceof PhenomenonGroup) {

			groupTarget = (PhenomenonGroup) target;

			// get the drag location
			if (determineLocation(getCurrentEvent()) == LOCATION_BEFORE) {
				before = true;
			}

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
				if (dragged.getIdLabel() != null && dragged.getIdLabel().compareTo(groupTarget.getIdLabel()) <= 0) {
					offset = 1;
				}

				// reorder
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).reorderPhenomenonGroups(
						dragged, IDTools.reverseGenerateAlphabeticIdRecursive(groupTarget.getIdLabel()) - offset);
			} else {

				// offset used to drag the item depending of its location compared to the target
				int offset = 0;
				if (dragged.getIdLabel() != null && dragged.getIdLabel().compareTo(groupTarget.getIdLabel()) > 0) {
					offset = 1;
				}

				// reorder
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).reorderPhenomenonGroups(
						dragged, IDTools.reverseGenerateAlphabeticIdRecursive(groupTarget.getIdLabel()) + offset);
			}

			// fire view change to save credibility file
			view.getViewManager().viewChanged();

			logger.debug("The drop of: {} was done on the element: {}", dragged, target); //$NON-NLS-1$
		} catch (CredibilityException e) {
			logger.error("Impossible to reorder phenomenon group: {}", e.getMessage(), e); //$NON-NLS-1$
			return false;
		}

		return true;
	}

	/**
	 * @param dragged the dragged object
	 * @param target  the drop target
	 * @return true if the phenomenon groups has been changed, otherwise false
	 */
	protected boolean movePhenomenon(Phenomenon dragged, Object target) {

		if (dragged == null) {
			return false;
		}

		PhenomenonGroup groupTarget = null;
		Phenomenon phenomenonTarget = null;
		boolean needRepositioning = false;
		boolean before = false;

		// if phenomenon is dropped on a group
		if (target instanceof PhenomenonGroup) {
			groupTarget = (PhenomenonGroup) target;
		}
		// if phenomenon is dropped on a phenomenon: get the group of the phenomenon
		else if (target instanceof Phenomenon) {
			groupTarget = ((Phenomenon) target).getPhenomenonGroup();
			phenomenonTarget = (Phenomenon) target;
			needRepositioning = true;

			// get the drag location
			if (determineLocation(getCurrentEvent()) == LOCATION_BEFORE) {
				before = true;
			}
		}

		// if the phenomenon is not dropped on his current group
		PhenomenonGroup previousGroup = dragged.getPhenomenonGroup();

		if (groupTarget == null) {
			return false;
		}

		if (!groupTarget.equals(previousGroup)) {

			try {
				// update dragged phenomenon
				dragged.setPhenomenonGroup(groupTarget);
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).updatePhenomenon(dragged);

				// update phenomenon group lists
				previousGroup.getPhenomenonList().remove(dragged);
				groupTarget.getPhenomenonList().add(dragged);

				// update previous group phenomenon id label
				List<Phenomenon> phenomenonListToUpdate = previousGroup.getPhenomenonList();
				phenomenonListToUpdate.sort(Comparator.comparing(Phenomenon::getIdLabel));
				int count = 1;
				for (Phenomenon phenTemp : phenomenonListToUpdate) {
					phenTemp.setIdLabel(previousGroup.getIdLabel() + count);
					view.getViewManager().getAppManager().getService(IPIRTApplication.class).updatePhenomenon(phenTemp);
					count++;
				}

				// fire view change to save credibility file
				view.getViewManager().viewChanged();

				logger.debug("The drop of: {} was done on the element: {}", dragged, target); //$NON-NLS-1$

			} catch (CredibilityException e) {
				logger.error("Impossible to update phenomenon: {}", e.getMessage(), e); //$NON-NLS-1$
				return false;
			}
		}

		if (needRepositioning) {
			return reorderPhenomenon(dragged, phenomenonTarget, before);
		}

		return true;
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
				if (dragged.getIdLabel() != null && dragged.getIdLabel().compareTo(target.getIdLabel()) <= 0) {
					offset = 1;
				}

				// reorder
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).reorderPhenomena(dragged,
						IDTools.getPositionInSet(target.getIdLabel()) - offset);
			} else {

				// offset used to drag the item depending of its location compared to the target
				int offset = 0;
				if (dragged.getIdLabel() != null && dragged.getIdLabel().compareTo(target.getIdLabel()) > 0) {
					offset = 1;
				}

				// reorder
				view.getViewManager().getAppManager().getService(IPIRTApplication.class).reorderPhenomena(dragged,
						IDTools.getPositionInSet(target.getIdLabel()) + offset);
			}

			// fire view change to save credibility file
			view.getViewManager().viewChanged();

			logger.debug("The drop of: {} was done on the element: {}", dragged, target); //$NON-NLS-1$
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
		return source instanceof PhenomenonGroup || source instanceof Phenomenon;
	}

}
