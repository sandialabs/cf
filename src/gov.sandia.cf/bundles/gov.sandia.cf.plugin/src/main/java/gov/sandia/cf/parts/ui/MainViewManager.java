/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IApplicationManager;
import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.common.IManager;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.launcher.CFCache;
import gov.sandia.cf.launcher.CredibilityEditor;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.listeners.IBreadCrumbListener;
import gov.sandia.cf.parts.model.BreadcrumbItemParts;
import gov.sandia.cf.parts.services.IClientService;
import gov.sandia.cf.parts.tools.ViewTools;
import gov.sandia.cf.parts.ui.configuration.ConfigurationViewManager;
import gov.sandia.cf.parts.ui.decision.DecisionViewManager;
import gov.sandia.cf.parts.ui.guidance.CFGuidanceViewManager;
import gov.sandia.cf.parts.ui.home.HomeViewManager;
import gov.sandia.cf.parts.ui.intendedpurpose.IntendedPurposeViewManager;
import gov.sandia.cf.parts.ui.pcmm.PCMMViewManager;
import gov.sandia.cf.parts.ui.pirt.PIRTViewManager;
import gov.sandia.cf.parts.ui.qoiplanning.QoIPlanningViewManager;
import gov.sandia.cf.parts.ui.report.ReportViewManager;
import gov.sandia.cf.parts.ui.requirement.SystemRequirementViewManager;
import gov.sandia.cf.parts.ui.uncertainty.UncertaintyViewManager;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.web.services.IWebClientManager;

/**
 * The credibility view that shows credibility views. All the credibility views
 * are instantiated, the StackLayout manager shows in front the appropriate view
 * 
 * @author Didier Verstraete
 *
 */
public class MainViewManager extends ViewPart implements IManager, IViewManager, IBreadCrumbListener {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(MainViewManager.class);

	/** Buttons events Properties */
	public static final String BTN_EVENT_PROPERTY = "BTN_EVENT"; //$NON-NLS-1$

	/** Buttons events VIEW MANAGER */
	public static final String BTN_VIEWMANAGER = "BTN_VIEWMANAGER"; //$NON-NLS-1$

	/**
	 * Defines the state of the loader
	 */
	private boolean isStarted = false;

	/**
	 * The view managers
	 */
	private Map<CFFeature, AViewManager> viewManagers = null;

	/**
	 * the layout manager to hide/show the different views
	 */
	private StackLayout layout;
	/**
	 * the top content panel
	 */
	private Composite contentPanel;

	/**
	 * the previous view
	 */
	private Control previousView;

	/**
	 * the credibility editor
	 */
	private CredibilityEditor credibilityEditor;

	/** The first opening. */
	private boolean firstOpening;

	/**
	 * CrediblityView constructor
	 * 
	 * @param credibilityEditor editor
	 * 
	 */
	public MainViewManager(CredibilityEditor credibilityEditor) {
		Assert.isNotNull(credibilityEditor);
		this.credibilityEditor = credibilityEditor;
		viewManagers = new EnumMap<>(CFFeature.class);
		this.firstOpening = true;
	}

	/** {@inheritDoc} */
	@Inject
	public void createPartControl(Composite parent) {

		contentPanel = new Composite(parent, SWT.BORDER);
		layout = new StackLayout();
		contentPanel.setLayout(layout);

		// display homeView first
		openHome();
	}

	@Override
	public CredibilityEditor getCredibilityEditor() {
		return credibilityEditor;
	}

	@Override
	public boolean isWebConnection() {
		if (getCredibilityEditor() == null) {
			return false;
		}
		return getCredibilityEditor().isWebConnection();
	}

	@Override
	public boolean isLocalFileConnection() {
		return !isWebConnection();
	}

	@Override
	public CFCache getCache() {
		if (credibilityEditor == null) {
			return null;
		}
		return credibilityEditor.getCache();
	}

	@Override
	public IApplicationManager getAppManager() {
		if (credibilityEditor == null) {
			return null;
		}
		return credibilityEditor.getAppMgr();
	}

	@Override
	public IWebClientManager getWebClient() {
		if (credibilityEditor == null) {
			return null;
		}
		return credibilityEditor.getWebClient();
	}

	@Override
	public void viewChanged() {
		this.credibilityEditor.setDirty(true);
	}

	@Override
	public void doSave() {
		// save just after
		// create asynchronous save job (otherwise it may not be saved)
		Display.getCurrent().asyncExec(() -> this.credibilityEditor.doSave(new NullProgressMonitor()));
	}

	/**
	 * Add an open PIRT listener to the button in parameter.
	 * 
	 * @param button the button to plug with PIRT view
	 * 
	 */
	public void plugPIRTButton(Control button) {
		button.addListener(SWT.Selection, e -> openPIRT());
	}

	/**
	 * Add an open QoI Planning listener to the button in parameter.
	 * 
	 * @param button the button to plug with QoI Planning view
	 * 
	 */
	public void plugQoIPlanningButton(Control button) {
		button.addListener(SWT.Selection, e -> openQoIPlanning());
	}

	/**
	 * Add an open PCMM Planning listener to the button in parameter.
	 * 
	 * @param button the button to plug with PCMM Planning view
	 * 
	 */
	public void plugPCMMPlanningButton(Control button) {
		button.addListener(SWT.Selection, e -> openPCMMPlanning());
	}

	/**
	 * Add an open analyst decision listener to the button in parameter.
	 * 
	 * @param button the button to plug with Decision view
	 * 
	 */
	public void plugDecisionButton(Control button) {
		button.addListener(SWT.Selection, e -> openAnalystDecision());
	}

	/**
	 * Add an open PCMM listener to the button in parameter.
	 * 
	 * @param button the button to plug with PCMM view
	 * 
	 */
	public void plugPCMMButton(Control button) {
		button.addListener(SWT.Selection, e -> openPCMM());
	}

	/**
	 * Add an open uncertainty listener to the button in parameter.
	 * 
	 * @param button the button to plug with Communicate view
	 * 
	 */
	public void plugUncertaintyButton(Control button) {
		button.addListener(SWT.Selection, e -> openUncertainty());
	}

	/**
	 * Add an open system requirements listener to the button in parameter.
	 * 
	 * @param button the button to plug with system requirements view
	 * 
	 */
	public void plugSystemRequirementsButton(Control button) {
		button.addListener(SWT.Selection, e -> openSystemRequirement());
	}

	/**
	 * Add an open intended purpose listener to the button in parameter.
	 * 
	 * @param button the button to plug with intended purpose view
	 * 
	 */
	public void plugIntendedPurposeButton(Control button) {
		button.addListener(SWT.Selection, e -> openIntendedPurpose());
	}

	/**
	 * Add an open reporting listener to the button in parameter.
	 * 
	 * @param button the button to plug with report view
	 * 
	 */
	public void plugReportButton(Control button) {
		button.addListener(SWT.Selection, e -> openReporting());
	}

	/**
	 * Add an open configuration listener to the button in parameter.
	 * 
	 * @param button the button to plug with configuration dialog
	 * 
	 */
	public void plugConfigurationButton(Control button) {
		button.addListener(SWT.Selection, e -> openConfiguration());
	}

	/**
	 * Add an open previous listener to the button in parameter.
	 * 
	 * @param button the button to plug with back home view action
	 * 
	 */
	public void plugPreviousViewButton(Button button) {
		button.addListener(SWT.Selection, e -> openPrevious());
	}

	/**
	 * Add a back home listener to the button in parameter.
	 * 
	 * @param button the button to plug with back home view action
	 * 
	 */
	public void plugBackHomeButton(Button button) {
		button.addListener(SWT.Selection, e -> openHome());
	}

	/** {@inheritDoc} */
	@Override
	public void openHome() {
		if (viewManagers.get(CFFeature.HOME) == null) {
			viewManagers.put(CFFeature.HOME, new HomeViewManager(this, contentPanel, SWT.NONE));
		}

		openViewManager(CFFeature.HOME);
	}

	/**
	 * Open QoI Planning view action.
	 */
	public void openQoIPlanning() {
		if (viewManagers.get(CFFeature.QOI_PLANNER) == null) {
			viewManagers.put(CFFeature.QOI_PLANNER, new QoIPlanningViewManager(this, contentPanel, SWT.NONE));
		}

		openViewManager(CFFeature.QOI_PLANNER);
	}

	/**
	 * Open PIRT view action.
	 */
	public void openPIRT() {
		if (viewManagers.get(CFFeature.PIRT) == null) {
			viewManagers.put(CFFeature.PIRT, new PIRTViewManager(this, contentPanel, SWT.NONE));
		}

		openViewManager(CFFeature.PIRT);
	}

	/**
	 * Open PCMM view action.
	 */
	public void openPCMM() {
		Model model = getCache().getModel();
		if (model != null) {
			List<QuantityOfInterest> qois = getAppManager().getService(IPIRTApplication.class).getRootQoI(model);
			if (firstOpening && (null == qois || qois.isEmpty())
					&& getAppManager().getService(IPIRTApplication.class).isPIRTEnabled()) {
				MessageDialog.openWarning(Display.getCurrent().getActiveShell(),
						RscTools.getString(RscConst.MSG_HOMEVIEW_PCMM_PREREQUISITE_TITLE),
						RscTools.getString(RscConst.MSG_HOMEVIEW_PCMM_PREREQUISITE_TXT));

			}
		}

		if (viewManagers.get(CFFeature.PCMM) == null) {
			viewManagers.put(CFFeature.PCMM, new PCMMViewManager(this, contentPanel, SWT.NONE,
					Arrays.asList(PCMMPhase.EVIDENCE, PCMMPhase.ASSESS, PCMMPhase.AGGREGATE, PCMMPhase.STAMP)));
		}

		openViewManager(CFFeature.PCMM);

		firstOpening = false;
	}

	/**
	 * Open PCMM Planning view action.
	 */
	public void openPCMMPlanning() {

		if (viewManagers.get(CFFeature.PCMM_PLANNING) == null) {
			viewManagers.put(CFFeature.PCMM_PLANNING,
					new PCMMViewManager(this, contentPanel, SWT.NONE, Arrays.asList(PCMMPhase.PLANNING)));
		}

		openViewManager(CFFeature.PCMM_PLANNING);
	}

	/**
	 * Open Uncertainty view action.
	 */
	public void openUncertainty() {

		if (viewManagers.get(CFFeature.UNCERTAINTY) == null) {
			viewManagers.put(CFFeature.UNCERTAINTY, new UncertaintyViewManager(this, contentPanel, SWT.NONE));
		}

		openViewManager(CFFeature.UNCERTAINTY);
	}

	/**
	 * Open System Requirement view action.
	 */
	public void openSystemRequirement() {

		if (viewManagers.get(CFFeature.SYSTEM_REQUIREMENTS) == null) {
			viewManagers.put(CFFeature.SYSTEM_REQUIREMENTS,
					new SystemRequirementViewManager(this, contentPanel, SWT.NONE));
		}

		openViewManager(CFFeature.SYSTEM_REQUIREMENTS);
	}

	/**
	 * Open IntendedPurpose view action.
	 */
	public void openIntendedPurpose() {

		if (viewManagers.get(CFFeature.INTENDED_PURPOSE) == null) {
			viewManagers.put(CFFeature.INTENDED_PURPOSE, new IntendedPurposeViewManager(this, contentPanel, SWT.NONE));
		}

		openViewManager(CFFeature.INTENDED_PURPOSE);
	}

	/**
	 * Open Analyst Decision view action.
	 */
	public void openAnalystDecision() {

		if (viewManagers.get(CFFeature.DECISION) == null) {
			viewManagers.put(CFFeature.DECISION, new DecisionViewManager(this, contentPanel, SWT.NONE));
		}

		openViewManager(CFFeature.DECISION);
	}

	/**
	 * Open Report view action.
	 */
	public void openReporting() {

		if (viewManagers.get(CFFeature.GEN_REPORT) == null) {
			viewManagers.put(CFFeature.GEN_REPORT, new ReportViewManager(this, contentPanel, SWT.NONE));
		}

		openViewManager(CFFeature.GEN_REPORT);
	}

	/**
	 * Open Configuration view action.
	 */
	public void openConfiguration() {

		if (viewManagers.get(CFFeature.CONFIGURATION) == null) {
			viewManagers.put(CFFeature.CONFIGURATION, new ConfigurationViewManager(this, contentPanel, SWT.NONE));
		}

		openViewManager(CFFeature.CONFIGURATION);
	}

	/**
	 * Open Previous view action.
	 */
	public void openPrevious() {

		Control previousViewTemp = layout.topControl;
		layout.topControl = previousView;
		previousView = previousViewTemp;

		java.util.Optional<CFFeature> featureOpt = viewManagers.entrySet().stream()
				.filter(entry -> layout.topControl.equals(entry.getValue())).map(Map.Entry::getKey).findFirst();

		// set current part property for the other views
		if (featureOpt.isPresent()) {
			getCredibilityEditor().setPartProperty(CredibilityFrameworkConstants.PART_PROPERTY_ACTIVEVIEW,
					featureOpt.get().toString());
		}

		// layout the main panel
		contentPanel.layout();
	}

	/**
	 * Open the view for the feature in parameter
	 * 
	 * @param feature the feature associated to the view
	 */
	private void openViewManager(CFFeature feature) {

		if (feature != null && viewManagers.get(feature) != null) {

			// trigger quitting previous feature view
			if (layout.topControl instanceof IViewManager && !layout.topControl.equals(viewManagers.get(feature))) {
				((IViewManager) layout.topControl).quit();
			}

			// open home view for the feature
			viewManagers.get(feature).openHome();

			// change top view and save previous view
			previousView = layout.topControl;
			layout.topControl = viewManagers.get(feature);

			// set current part property for the other views
			getCredibilityEditor().setPartProperty(CredibilityFrameworkConstants.PART_PROPERTY_ACTIVEVIEW,
					feature.toString());

			// layout the main panel
			contentPanel.layout();
		}
	}

	/**
	 * Open the help level view
	 */
	public void openHelpLevelView() {
		try {
			// Get active elements
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			activePage.showView(CredibilityFrameworkConstants.GUIDANCE_VIEW_ID);

			// Set this editor in the guidance level view
			CFGuidanceViewManager guidanceViewManager = getGuidanceViewManager();
			if (guidanceViewManager != null) {
				guidanceViewManager.setCredibilityEditor(getCredibilityEditor());
			}

		} catch (PartInitException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	@Focus
	public void setFocus() {
		// unused
	}

	/**
	 * This method is kept for E3 compatiblity.
	 * 
	 * @param s the selection received from JFace (E3 mode)
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) ISelection s) {
		if (s == null || s.isEmpty())
			return;

		if (s instanceof IStructuredSelection) {
			IStructuredSelection iss = (IStructuredSelection) s;
			if (iss.size() == 1)
				setSelection(iss.getFirstElement());
			else
				setSelection(iss.toArray());
		}
	}

	/**
	 * This method manages the selection of your current object.
	 * 
	 * @param o : the current object received
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object o) {

		// Remove the 2 following lines in pure E4 mode, keep them in mixed mode
		if (o instanceof ISelection) { // Already captured
			// unused
		}
	}

	/**
	 * This method manages the multiple selection of your current objects.
	 * 
	 * @param selectedObjects : the current array of objects received in case of
	 *                        multiple selection
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object[] selectedObjects) {
		// unused
	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		isStarted = true;
	}

	/** {@inheritDoc} */
	@Override
	public void stop() {
		isStarted = false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isStarted() {
		return isStarted;
	}

	/** {@inheritDoc} */
	@Override
	public Queue<BreadcrumbItemParts> getBreadcrumbItems(ACredibilityView<?> view) {
		return viewManagers.get(CFFeature.HOME).getBreadcrumbItems(view);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDirty() {
		return getCredibilityEditor() != null && getCredibilityEditor().isDirty();
	}

	/** {@inheritDoc} */
	@Override
	public void doBreadcrumbAction(BreadcrumbItemParts item) {
		viewManagers.get(CFFeature.HOME).doBreadcrumbAction(item);
	}

	/**
	 * Get top control
	 * 
	 * @return the layout top composite
	 */
	public Composite getLayoutTop() {
		if (layout.topControl instanceof Composite) {
			return (Composite) layout.topControl;
		}
		return null;
	}

	/**
	 * Launch global refresh
	 */
	public void refreshSaveState() {
		for (AViewManager viewMgr : viewManagers.values()) {
			viewMgr.refreshSaveState();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void dispose() {

		stop();

		// dispose views
		for (AViewManager viewMgr : viewManagers.values()) {
			ViewTools.disposeControl(viewMgr);
		}

		// dispose the editor
		super.dispose();
	}

	/** {@inheritDoc} */
	@Override
	public void reload() {

		// reload views
		for (AViewManager viewMgr : viewManagers.values()) {
			viewMgr.reload();
		}

		// Reload the guidance view
		CFGuidanceViewManager guidanceViewManager = getGuidanceViewManager();
		if (guidanceViewManager != null) {
			guidanceViewManager.reload(credibilityEditor);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void reloadActiveView() {
		if (layout.topControl instanceof IViewManager) {
			((IViewManager) layout.topControl).reloadActiveView();
		}
	}

	/**
	 * Reload the view with viewID
	 * 
	 * @param viewID the viewID for the view to reload
	 */
	public void reloadView(CFFeature viewID) {
		if (viewID != null && viewManagers.get(viewID) != null) {
			viewManagers.get(viewID).reload();
		}
	}

	/**
	 * @return the guidance view manager
	 */
	private CFGuidanceViewManager getGuidanceViewManager() {
		EPartService partService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(EPartService.class);
		if (partService != null) {
			MPart part = partService.findPart(CredibilityFrameworkConstants.GUIDANCE_VIEW_ID);
			if (part != null && part.getObject() != null && part.getObject() instanceof CFGuidanceViewManager) {
				return (CFGuidanceViewManager) part.getObject();
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public ResourceManager getRscMgr() {
		return credibilityEditor.getRscMgr();
	}

	/** {@inheritDoc} */
	@Override
	public <S extends IClientService> S getClientService(Class<S> interfaceClass) {
		return credibilityEditor.getClientSrvMgr().getService(interfaceClass);
	}

	/** {@inheritDoc} */
	@Override
	public void quit() {
		// do nothing by default
	}
}
