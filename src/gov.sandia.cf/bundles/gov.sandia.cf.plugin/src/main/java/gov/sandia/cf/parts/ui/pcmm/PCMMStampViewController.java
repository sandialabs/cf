/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMAggregateApp;
import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAggregation;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * PCMM Aggregate home controller: Used to control the PCMM Aggregate view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMStampViewController extends AViewController<PCMMViewManager, PCMMStampView> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMStampViewController.class);

	/**
	 * the pcmm elements map
	 */
	private List<PCMMElement> elements;

	/**
	 * the aggregation map
	 */
	private Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregation;

	/**
	 * Filters
	 */
	private Map<EntityFilter, Object> filters;

	/** The warning dialog shown. */
	private boolean warningDialogShown = false;

	/**
	 * Constructor.
	 *
	 * @param viewManager the view manager
	 */
	PCMMStampViewController(PCMMViewManager viewManager) {
		super(viewManager);

		this.filters = new HashMap<>();
		this.elements = new ArrayList<>();

		super.setView(new PCMMStampView(this, SWT.NONE));
	}

	/**
	 * Reload data.
	 *
	 * @param isFilter the is filter
	 */
	void reloadData(boolean isFilter) {

		// Hide Role
		getView().hideRoleSelection();

		// Get Model
		Model model = getViewManager().getCache().getModel();
		if (model == null) {
			return;
		}

		try {
			// Get elements
			this.elements = getViewManager().getAppManager().getService(IPCMMApplication.class).getElementList(model);

			// Manage "all" role filter
			if (filters.containsKey(PCMMAssessment.Filter.ROLECREATION)) {
				Role role = (Role) filters.get(PCMMAssessment.Filter.ROLECREATION);
				if (null == role.getId()) {
					filters.remove(PCMMAssessment.Filter.ROLECREATION);
				}
			}

			// Add tag filter
			filters.put(PCMMAssessment.Filter.TAG, getViewManager().getSelectedTag());

			if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
				this.aggregation = getViewManager().getAppManager().getService(IPCMMAggregateApp.class)
						.aggregateSubelements(getViewManager().getPCMMConfiguration(), elements, filters);

				// check the completeness of the assessments
				if (!warningDialogShown && !isFilter
						&& !getViewManager().getAppManager().getService(IPCMMAggregateApp.class)
								.isCompleteAggregation(model, getViewManager().getSelectedTag())) {
					MessageDialog.openWarning(getView().getShell(),
							RscTools.getString(RscConst.MSG_PCMMSTAMP_DIALOG_TITLE),
							RscTools.getString(RscConst.MSG_PCMM_ASSESSMENT_INCOMPLETE_MSG));
					warningDialogShown = true;
				}
			} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
				this.aggregation = getViewManager().getAppManager().getService(IPCMMAggregateApp.class)
						.aggregateAssessmentSimplified(getViewManager().getPCMMConfiguration(), elements, filters);

				// check the completeness of the assessments
				if (!isFilter && !getViewManager().getAppManager().getService(IPCMMAggregateApp.class)
						.isCompleteAggregationSimplified(model, getViewManager().getSelectedTag())) {
					MessageDialog.openWarning(getView().getShell(),
							RscTools.getString(RscConst.MSG_PCMMSTAMP_DIALOG_TITLE),
							RscTools.getString(RscConst.MSG_PCMM_ASSESSMENT_INCOMPLETE_SIMPLIFIED_MSG));
				}
			}

			// repaint the view composites
			getView().drawMainComposite();

		} catch (CredibilityException e) {
			MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_PCMMSTAMP_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_PCMMSTAMP_DIALOG_LOADING_MSG));
			logger.warn("An error occurred while loading the stamp data:\n{}", e.getMessage(), e); //$NON-NLS-1$
		}
	}

	/**
	 * @return the pcmm mode
	 */
	public PCMMMode getPCMMMode() {
		return getViewManager().getPCMMConfiguration() != null ? getViewManager().getPCMMConfiguration().getMode()
				: null;
	}

	/**
	 * Gets the filters.
	 *
	 * @return the filters
	 */
	public Map<EntityFilter, Object> getFilters() {
		return filters;
	}

	/**
	 * Put filter.
	 *
	 * @param filter the filter
	 * @param object the object
	 */
	public void putFilter(EntityFilter filter, Object object) {
		this.filters.put(filter, object);
	}

	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public List<PCMMElement> getElements() {
		return elements;
	}

	/**
	 * Gets the aggregation.
	 *
	 * @return the aggregation
	 */
	public Map<PCMMElement, PCMMAggregation<PCMMElement>> getAggregation() {
		return aggregation;
	}
}
