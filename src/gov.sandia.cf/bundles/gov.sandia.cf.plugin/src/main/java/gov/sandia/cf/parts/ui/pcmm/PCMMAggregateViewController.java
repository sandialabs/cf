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
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
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
public class PCMMAggregateViewController extends AViewController<PCMMViewManager, PCMMAggregateView> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMAggregateViewController.class);

	/**
	 * PCMMSpecification
	 */
	private PCMMSpecification pcmmConfiguration;

	/**
	 * the pcmm elements
	 */
	private List<PCMMElement> elements;

	/**
	 * The aggregation of all assessments
	 */
	private Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggregatedSubelementsMap;

	/**
	 * the aggregation of all sub-elements aggregation
	 */
	private Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregatedElementsMap;

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
	PCMMAggregateViewController(PCMMViewManager viewManager) {
		super(viewManager);

		elements = new ArrayList<>();
		filters = new HashMap<>();
		aggregatedSubelementsMap = new HashMap<>();
		aggregatedElementsMap = new HashMap<>();

		// Set PCMM configuration
		pcmmConfiguration = getViewManager().getPCMMConfiguration();

		super.setView(new PCMMAggregateView(this, SWT.NONE));
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
		if (model != null) {

			// Get expanded elements
			Object[] expanded = getView().getExpandedElements();

			try {
				// Get elements
				elements = getViewManager().getAppManager().getService(IPCMMApplication.class).getElementList(model);
				if (elements != null) {
					// Manage "all" role filter
					if (filters.containsKey(PCMMAssessment.Filter.ROLECREATION)) {
						Role role = (Role) filters.get(PCMMAssessment.Filter.ROLECREATION);
						if (null == role.getId()) {
							filters.remove(PCMMAssessment.Filter.ROLECREATION);
						}
					}

					// Add tag filter
					filters.put(PCMMAssessment.Filter.TAG, getViewManager().getSelectedTag());

					if (PCMMMode.DEFAULT.equals(pcmmConfiguration.getMode())) {
						// Aggregate sub-elements
						aggregatedSubelementsMap = getViewManager().getAppManager().getService(IPCMMAggregateApp.class)
								.aggregateAssessments(pcmmConfiguration, elements, filters);

						// Aggregate elements
						aggregatedElementsMap = getViewManager().getAppManager().getService(IPCMMAggregateApp.class)
								.aggregateSubelements(pcmmConfiguration, aggregatedSubelementsMap);

						// Check the completeness of the assessments (don't display when filtering)
						if (!warningDialogShown && !isFilter && !getViewManager().getAppManager().getService(IPCMMAggregateApp.class)
								.isCompleteAggregation(model, getViewManager().getSelectedTag())) {
							MessageDialog.openWarning(getView().getShell(),
									RscTools.getString(RscConst.MSG_PCMMAGGREG_DIALOG_TITLE),
									RscTools.getString(RscConst.MSG_PCMM_ASSESSMENT_INCOMPLETE_MSG));
							warningDialogShown = true;
						}
					} else if (PCMMMode.SIMPLIFIED.equals(pcmmConfiguration.getMode())) {
						aggregatedElementsMap = getViewManager().getAppManager().getService(IPCMMAggregateApp.class)
								.aggregateAssessmentSimplified(pcmmConfiguration, elements, filters);

						// Check the completeness of the assessments (don't display when filtering)
						if (!isFilter && !getViewManager().getAppManager().getService(IPCMMAggregateApp.class)
								.isCompleteAggregationSimplified(model, getViewManager().getSelectedTag())) {
							MessageDialog.openWarning(getView().getShell(),
									RscTools.getString(RscConst.MSG_PCMMAGGREG_DIALOG_TITLE),
									RscTools.getString(RscConst.MSG_PCMM_ASSESSMENT_INCOMPLETE_SIMPLIFIED_MSG));
						}
					}

					// Refresh the table
					getView().refreshMainTable();

					// Set tree data
					getView().setTreeData(elements);

					// Set expanded elements
					getView().setExpandedElements(expanded);
				}
			} catch (CredibilityException e) {
				MessageDialog.openWarning(getView().getShell(),
						RscTools.getString(RscConst.MSG_PCMMAGGREG_DIALOG_TITLE),
						RscTools.getString(RscConst.ERR_PCMMAGGREG_DIALOG_LOADING_MSG));
				logger.error("An error has occurred while loading aggregation data:\n{}", e.getMessage(), e); //$NON-NLS-1$
			}
		}

		// refresh the viewer
		getView().refreshViewer();
	}

	/**
	 * Gets the pcmm configuration.
	 *
	 * @return the pcmm configuration
	 */
	public PCMMSpecification getPcmmConfiguration() {
		return pcmmConfiguration;
	}

	/**
	 * @return the pcmm mode
	 */
	public PCMMMode getPCMMMode() {
		return getViewManager().getPCMMConfiguration() != null ? getViewManager().getPCMMConfiguration().getMode()
				: null;
	}

	/**
	 * @return the aggregation map of assessments
	 */
	public Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> getAggregatedSubelementsMap() {
		return aggregatedSubelementsMap;
	}

	/**
	 * @return the aggregation map of subelements aggregation
	 */
	public Map<PCMMElement, PCMMAggregation<PCMMElement>> getAggregatedElementsMap() {
		return aggregatedElementsMap;
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
}
