/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.launcher.CredibilityEditor;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.parts.model.BreadcrumbItemParts;
import gov.sandia.cf.parts.ui.ACredibilityView;
import gov.sandia.cf.parts.ui.AViewManager;
import gov.sandia.cf.parts.ui.ICredibilityView;
import gov.sandia.cf.parts.ui.IPCMMViewManager;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.tools.NetTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * The credibility view that shows credibility views. All the credibility views
 * are instantiated, the StackLayout manager shows in front the appropriate view
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMViewManager extends AViewManager implements Listener, IPCMMViewManager {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMViewManager.class);

	/**
	 * Buttons events Properties
	 */
	public static final String BTN_EVENT_PROPERTY = "BTN_EVENT"; //$NON-NLS-1$

	/**
	 * Buttons events Globals
	 */
	public static final String BTN_EVENT_PCMM_BACK_HOME = "PCMM_BACK_HOME"; //$NON-NLS-1$

	/**
	 * Buttons events HomeView
	 */
	/** PCMM HOME button event name */
	public static final String BTN_EVENT_PCMM_HOME = "BTN_EVENT_PCMM_HOME"; //$NON-NLS-1$
	/** PCMM ASSESS button event name */
	public static final String BTN_EVENT_PCMM_ASSESS = "BTN_EVENT_PCMM_ASSESS"; //$NON-NLS-1$
	/** PCMM AGGREGATE button event name */
	public static final String BTN_EVENT_PCMM_AGGREGATE = "BTN_EVENT_PCMM_AGGREGATE"; //$NON-NLS-1$
	/** PCMM HELP LEVEL button event name */
	public static final String BTN_EVENT_PCMM_HELP_LEVEL = "BTN_EVENT_PCMM_HELP_LEVEL"; //$NON-NLS-1$
	/** PCMM STAMP button event name */
	public static final String BTN_EVENT_PCMM_STAMP = "BTN_EVENT_PCMM_STAMP"; //$NON-NLS-1$
	/** PCMM EVIDENCE button event name */
	public static final String BTN_EVENT_PCMM_EVIDENCE = "BTN_EVENT_PCMM_EVIDENCE"; //$NON-NLS-1$
	/** PCMM BACK button event name */
	public static final String BTN_EVENT_BACK_HOME = "BTN_EVENT_BACK_HOME"; //$NON-NLS-1$

	/**
	 * The current PCMM tag
	 */
	private Tag selectedTag;

	/**
	 * the last control opened
	 */
	private Control lastControl;
	/**
	 * the layout manager to hide/show the different views
	 */
	private StackLayout stackLayout;

	private PCMMHomeViewController homeController;

	private PCMMAggregateViewController aggregateController;

	private PCMMAssessViewController assessController;

	private PCMMEvidenceViewController evidenceController;

	private PCMMPlanningViewController planningController;

	private PCMMStampViewController stampController;

	/**
	 * CrediblityView constructor
	 * 
	 * @param parentView   the parent view
	 * @param parent       the parent composite
	 * @param style        the style
	 * @param phaseEnabled the pcmm phases enabled
	 */
	public PCMMViewManager(MainViewManager parentView, Composite parent, int style,
			final List<PCMMPhase> phaseEnabled) {
		super(parentView, parent, style);
		this.lastControl = null;
		this.selectedTag = null;

		// create controllers
		this.homeController = new PCMMHomeViewController(this, phaseEnabled != null ? phaseEnabled : new ArrayList<>());
		this.aggregateController = new PCMMAggregateViewController(this);
		this.assessController = new PCMMAssessViewController(this);
		this.evidenceController = new PCMMEvidenceViewController(this);
		this.planningController = new PCMMPlanningViewController(this);
		this.stampController = new PCMMStampViewController(this);

		// create the view
		createPartControl(parent);
	}

	/**
	 * Create the view components
	 * 
	 * @param parent the parent composite
	 */
	public void createPartControl(Composite parent) {

		// set layout
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.stackLayout = new StackLayout();
		this.setLayout(stackLayout);

		if (viewManager.getCache().getPCMMSpecification() != null) {
			// display homeView first
			this.stackLayout.topControl = this.homeController.getViewControl();
			this.lastControl = this.homeController.getViewControl();
		}
		this.layout();
	}

	/**
	 * Plug the button to this class to listen evidence events
	 * 
	 * @param button the button
	 */
	public void plugEvidenceButton(Button button) {
		button.setData(BTN_EVENT_PROPERTY, BTN_EVENT_PCMM_EVIDENCE);
		button.addListener(SWT.Selection, this);
	}

	/**
	 * Plug the button to this class to listen assess events
	 * 
	 * @param button the button
	 */
	public void plugAssessButton(Button button) {
		button.setData(BTN_EVENT_PROPERTY, BTN_EVENT_PCMM_ASSESS);
		button.addListener(SWT.Selection, this);
	}

	/**
	 * Plug the button to this class to listen pcmm stamp events
	 * 
	 * @param button the button
	 */
	public void plugPCMMStampButton(Button button) {
		button.setData(BTN_EVENT_PROPERTY, BTN_EVENT_PCMM_STAMP);
		button.addListener(SWT.Selection, this);
	}

	/**
	 * Plug the button to this class to listen pcmm help level events
	 * 
	 * @param button the button
	 */
	public void plugPCMMHelpLevel(Button button) {
		button.setData(BTN_EVENT_PROPERTY, BTN_EVENT_PCMM_HELP_LEVEL);
		button.addListener(SWT.Selection, this);
	}

	/**
	 * Plug the button to this class to listen back pcmm home events
	 * 
	 * @param button the button
	 */
	public void plugPCMMHomeButton(Button button) {
		button.setData(BTN_EVENT_PROPERTY, BTN_EVENT_PCMM_HOME);
		button.addListener(SWT.Selection, this);
	}

	/**
	 * Plug the button to this class to listen back home events
	 * 
	 * @param button the button
	 */
	@Override
	public void plugBackHomeButton(Button button) {
		this.viewManager.plugBackHomeButton(button);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(Event event) {

		// PCMM Home view
		if (event.widget.getData(BTN_EVENT_PROPERTY) != null
				&& event.widget.getData(BTN_EVENT_PROPERTY).equals(BTN_EVENT_PCMM_HOME)) {
			openHome();
		} // PCMM Evidence view
		else if (event.widget.getData(BTN_EVENT_PROPERTY) != null
				&& event.widget.getData(BTN_EVENT_PROPERTY).equals(BTN_EVENT_PCMM_EVIDENCE)) {
			openPCMMEvidenceView((PCMMElement) null);
		}
		// PCMM Assess view
		else if (event.widget.getData(BTN_EVENT_PROPERTY) != null
				&& event.widget.getData(BTN_EVENT_PROPERTY).equals(BTN_EVENT_PCMM_ASSESS)) {
			openPCMMAssessView(null);
		}
		// PCMM Aggregate view
		else if (event.widget.getData(BTN_EVENT_PROPERTY) != null
				&& event.widget.getData(BTN_EVENT_PROPERTY).equals(BTN_EVENT_PCMM_AGGREGATE)) {
			openPCMMAggregateView();
		}
		// PCMM Stamp view
		else if (event.widget.getData(BTN_EVENT_PROPERTY) != null
				&& event.widget.getData(BTN_EVENT_PROPERTY).equals(BTN_EVENT_PCMM_STAMP)) {
			openPCMMStampView();
		}
		// PCMM Help Level view
		else if (event.widget.getData(BTN_EVENT_PROPERTY) != null
				&& event.widget.getData(BTN_EVENT_PROPERTY).equals(BTN_EVENT_PCMM_HELP_LEVEL)) {
			viewManager.openHelpLevelView();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void openHome() {

		// save the last opened view
		saveLastView();

		// Refresh
		this.stackLayout.topControl = this.homeController.getViewControl();
		this.homeController.getView().refresh();

		this.layout();
	}

	/**
	 * Refresh the save button state
	 */
	public void refreshSaveState() {
		if (null != this.stackLayout.topControl) {
			((ACredibilityView<?>) this.stackLayout.topControl).refreshStatusComposite();
		}
	}

	/**
	 * Open the PCMM planning view and select the pcmm element in parameter
	 * 
	 * @param element the element to open
	 */
	public void openPCMMPlanningView(PCMMElement element) {

		// get the role of the user if it is not configured
		if (getCurrentUserRole() != null) {

			// save the last opened view
			saveLastView();

			// open or set the new PCMM element
			planningController.setElementSelected(element);

			// It is not necessary to reload the evidence view because it is done at the
			// creation and when setting new pcmm elements

			// show the view
			this.stackLayout.topControl = planningController.getViewControl();
			this.layout();

		} else {
			MessageDialog.openWarning(getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_PCMMEVID_DESC_ROLE_MANDATORY));
		}
	}

	/**
	 * Open the PCMM evidence view and select the pcmm element in parameter
	 * 
	 * @param element the element to open
	 */
	public void openPCMMEvidenceView(PCMMElement element) {

		// get the role of the user if it is not configured
		if (getCurrentUserRole() != null) {

			// save the last opened view
			saveLastView();

			// open or set the new PCMM element
			evidenceController.setElementSelected(element);

			// It is not necessary to reload the evidence view because it is done at the
			// creation and when setting new pcmm elements

			// show the view
			this.stackLayout.topControl = evidenceController.getViewControl();
			this.layout();

		} else {
			MessageDialog.openWarning(getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
					RscTools.getString(RscConst.ERR_PCMMEVID_DESC_ROLE_MANDATORY));
		}
	}

	/**
	 * Open the help level view
	 */
	public void openPCMMHelpLevelView() {
		viewManager.openHelpLevelView();
	}

	/**
	 * Open the assessment view
	 * 
	 * @param element the section to select in the view
	 */
	public void openPCMMAssessView(PCMMElement element) {

		// get the role of the user if it is not configured
		if (getCurrentUserRole() != null) {

			// save the last opened view
			saveLastView();

			// get the view
			if (this.assessController.getElementSelected() == null
					|| !this.assessController.getElementSelected().equals(element)) {
				this.assessController.setElementSelected(element);
			}

			// Refresh
			assessController.refresh();

			// show the view
			this.stackLayout.topControl = assessController.getViewControl();
			this.layout();

		} else {
			MessageDialog.openWarning(getShell(), RscTools.getString(RscConst.ERR_PCMMASSESS_TITLE_ROLE_MANDATORY),
					RscTools.getString(RscConst.ERR_PCMMASSESS_DESC_ROLE_MANDATORY));
		}
	}

	/**
	 * @return the current role of the user. If no role is defined in the CF
	 *         Session, a dialog prompt the user to select a role and return the
	 *         role selected.
	 */
	public Role getCurrentUserRole() {

		// get the role of the user if it is not configured
		Role roleSelected = null;

		if (getCache().getCurrentPCMMRole() == null) {
			boolean roleChanged = openChangeRoleDialog();
			if (roleChanged) {
				viewChanged();
			}
		}

		roleSelected = getCache().getCurrentPCMMRole();

		return roleSelected;
	}

	/**
	 * This method open a new dialog to select/change the role of the user
	 * 
	 * @return true if the role has changed (Select), otherwise false (Cancel).
	 */
	public boolean openChangeRoleDialog() {

		boolean changed = false;

		// get the roles from database
		List<Role> roles = getAppManager().getService(IPCMMApplication.class).getRoles();

		// open the dialog to select a role
		PCMMSelectRoleDialog dlg = new PCMMSelectRoleDialog(this, getShell(), roles, getCache().getCurrentPCMMRole());
		Role roleSelected = dlg.openDialog();

		// set the role of the user in the session
		if (roleSelected != null) {
			try {
				getCache().updatePCMMRole(roleSelected);
				changed = true;
			} catch (CredibilityException e) {
				logger.error("An error occured while updating PCMM role:\n{}", e.getMessage(), e);//$NON-NLS-1$
				MessageDialog.openError(getShell(), RscTools.getString(RscConst.MSG_PCMMSELECTROLE_TITLE),
						e.getMessage());
			}
		}

		// refresh the role in all the views
		super.refreshPCMMRole();

		return changed;
	}

	/**
	 * Refresh all PCMM role.
	 */
	public void fireRefreshPCMMRole() {
		super.refreshPCMMRole();
	}

	/**
	 * Refresh the role in the views
	 */
	@Override
	public void refreshPCMMRole() {
		if (this.homeController.getView() != null)
			this.homeController.getView().refreshRole();
		if (this.aggregateController.getView() != null)
			this.aggregateController.getView().refreshRole();
		if (this.assessController.getView() != null)
			this.assessController.getView().refreshRole();
		if (this.evidenceController.getView() != null)
			this.evidenceController.getView().refreshRole();
		if (this.planningController.getView() != null)
			this.planningController.getView().refreshRole();
		if (this.stampController.getView() != null)
			this.stampController.getView().refreshRole();
	}

	/**
	 * @return the tag selected
	 */
	public Tag getSelectedTag() {
		return selectedTag;
	}

	/**
	 * Set the tag selected
	 * 
	 * @param selectedTag the selected tag
	 */
	public void setSelectedTag(Tag selectedTag) {
		this.selectedTag = selectedTag;
		// Refresh
		((ACredibilityView<?>) this.stackLayout.topControl).refresh();

		if (this.homeController.getView() != null)
			this.homeController.getView().refreshTag();
		if (this.aggregateController.getView() != null)
			this.aggregateController.getView().refreshTag();
		if (this.assessController.getView() != null)
			this.assessController.getView().refreshTag();
		if (this.evidenceController.getView() != null)
			this.evidenceController.getView().refreshTag();
		if (this.planningController.getView() != null)
			this.planningController.getView().refreshTag();
		if (this.stampController.getView() != null)
			this.stampController.getView().refreshTag();
	}

	/**
	 * @return true if the selected tag is a real tag, otherwise false
	 */
	public boolean isTagMode() {
		return this.selectedTag != null && this.selectedTag.getId() != null;
	}

	/**
	 * Open the aggregation view
	 */
	public void openPCMMAggregateView() {

		// save the last opened view
		saveLastView();

		// Refresh
		aggregateController.getView().refresh();

		// show the view
		this.stackLayout.topControl = aggregateController.getViewControl();
		this.layout();
	}

	/**
	 * Open the pcmm stamp view
	 */
	public void openPCMMStampView() {

		// save the last opened view
		saveLastView();

		// Refresh
		stampController.getView().refresh();
		this.stackLayout.topControl = stampController.getViewControl();

		this.layout();
	}

	/**
	 * Reopen the last view
	 */
	public void openLastView() {

		Control tempControl = this.lastControl;

		// save the last opened view
		saveLastView();

		// if it is a credibility view, reload the view before opening it
		if (tempControl instanceof ACredibilityView)
			// Refresh
			((ACredibilityView<?>) tempControl).refresh();

		// set the current view to the last control
		this.stackLayout.topControl = tempControl;

		this.layout();
	}

	/**
	 * Save the last control opened view
	 */
	private void saveLastView() {
		this.lastControl = this.stackLayout.topControl;
	}

	/**
	 * @return the active view in PCMM context.
	 */
	public ACredibilityPCMMView<?> getActiveView() {
		if (stackLayout.topControl instanceof ACredibilityPCMMView) {
			return (ACredibilityPCMMView<?>) stackLayout.topControl;
		}
		return null;
	}

	/**
	 * @return the credibility Editor
	 */
	@Override
	public CredibilityEditor getCredibilityEditor() {
		if (viewManager == null) {
			return null;
		}
		return viewManager.getCredibilityEditor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Queue<BreadcrumbItemParts> getBreadcrumbItems(ACredibilityView<?> view) {
		Queue<BreadcrumbItemParts> breadcrumbItems = this.viewManager.getBreadcrumbItems(view);

		// add the pcmm home view
		BreadcrumbItemParts bcHomeItemPart = new BreadcrumbItemParts();
		if (homeController != null) {
			bcHomeItemPart.setName(homeController.getView().getItemTitle());
		} else {
			bcHomeItemPart.setName(view.getItemTitle());
		}
		bcHomeItemPart.setListener(this);
		breadcrumbItems.add(bcHomeItemPart);

		// if it exists, add an other PCMM view that depends of this manager
		if (!(view instanceof PCMMHomeView)) {
			BreadcrumbItemParts bcItemPart = new BreadcrumbItemParts();
			bcItemPart.setName(view.getItemTitle());
			bcItemPart.setListener(this);
			breadcrumbItems.add(bcItemPart);
		}
		return breadcrumbItems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doBreadcrumbAction(BreadcrumbItemParts item) {
		if (item != null && item.getListener().equals(this)
				&& item.getName().equals(homeController.getView().getItemTitle())) {
			openHome();
		}
	}

	/**
	 * Open the document associated to the evidence
	 * 
	 * @param evidence the evidence
	 */
	public void openDocument(PCMMEvidence evidence) {
		// check the evidence before opening
		if (evidence != null && evidence.getPath() != null) {
			// Initialize
			boolean isTypeUrl = evidence.getType() != null && evidence.getType().equals(FormFieldType.LINK_URL);

			// Check type - Local File
			if (!isTypeUrl) {
				WorkspaceTools.openFileInWorkspace(evidence.getPath());
			}
			// Check type - URL
			else {
				// Open browser
				try {
					NetTools.openURL(evidence.getPath(), getCache().getOpenLinkBrowserOpts());
				} catch (PartInitException | MalformedURLException e) {
					logger.error("An error occurred while opening the PCMM evidence:\n{}", e.getMessage(), e); //$NON-NLS-1$
					MessageDialog.openError(getShell(), RscTools.getString(RscConst.MSG_PCMMEVID_DIALOG_TITLE),
							RscTools.getString(RscConst.ERR_PCMMEVID_DIALOG_OPENING_MSG) + e.getMessage());
				}
			}
		}
	}

	@Override
	public void reload() {

		// reload views
		if (homeController.getView() != null) {
			homeController.getView().reload();
		}
		if (planningController.getView() != null) {
			planningController.getView().reload();
		}
		if (evidenceController.getView() != null) {
			evidenceController.getView().reload();
		}
		if (assessController.getView() != null) {
			assessController.getView().reload();
		}
		if (aggregateController.getView() != null) {
			aggregateController.getView().reload();
		}
		if (stampController.getView() != null) {
			stampController.getView().reload();
		}
	}

	@Override
	public void reloadActiveView() {
		if (stackLayout.topControl instanceof ICredibilityView) {
			((ICredibilityView) stackLayout.topControl).reload();
		}
	}
}
