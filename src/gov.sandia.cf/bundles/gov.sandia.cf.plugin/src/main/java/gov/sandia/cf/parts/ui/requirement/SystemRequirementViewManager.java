/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.requirement;

import java.util.Queue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import gov.sandia.cf.launcher.CredibilityEditor;
import gov.sandia.cf.parts.model.BreadcrumbItemParts;
import gov.sandia.cf.parts.ui.ACredibilityView;
import gov.sandia.cf.parts.ui.AViewManager;
import gov.sandia.cf.parts.ui.ICredibilityView;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.ui.MainViewManager;

/**
 * The System Requirement view.
 * 
 * @author Maxime N.
 *
 */
public class SystemRequirementViewManager extends AViewManager implements Listener, IViewManager {

	/** Buttons events Properties */
	public static final String BTN_EVENT_PROPERTY = "BTN_EVENT"; //$NON-NLS-1$

	/** Buttons events Globals */
	public static final String BTN_EVENT_BACK_HOME = "BACK_HOME"; //$NON-NLS-1$

	/** Buttons events HomeView */
	public static final String BTN_EVENT_HOME = "BTN_EVENT_HOME"; //$NON-NLS-1$

	/** Buttons events ADD SYSTEM REQUIREMENT ROOT */
	public static final String BTN_EVENT_ADD_GROUP = "BTN_EVENT_ADD_GROUP"; //$NON-NLS-1$

	/**
	 * the last control opened
	 */
	private Control lastControl;

	/**
	 * the layout manager to hide/show the different views
	 */
	private StackLayout stackLayout;

	/** The view controller. */
	private SystemRequirementViewController viewController;

	/**
	 * CrediblityView constructor
	 * 
	 * @param parentView the parent view
	 * @param parent     the parent composite
	 * @param style      the style
	 */
	public SystemRequirementViewManager(MainViewManager parentView, Composite parent, int style) {
		super(parentView, parent, style);

		this.lastControl = null;

		this.viewController = new SystemRequirementViewController(this);

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

		// display homeView first
		this.stackLayout.topControl = viewController.getViewControl();
		this.lastControl = viewController.getViewControl();

		this.layout();
	}

	/**
	 * Plug the button to this class to listen back home events
	 * 
	 * @param button the button
	 */
	public void plugPCMMHomeButton(Button button) {
		button.setData(BTN_EVENT_PROPERTY, BTN_EVENT_HOME);
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

		// Home view
		if (event.widget.getData(BTN_EVENT_PROPERTY) != null
				&& event.widget.getData(BTN_EVENT_PROPERTY).equals(BTN_EVENT_HOME)) {
			openHome();
		}

	}

	/** {@inheritDoc} */
	@Override
	public void openHome() {

		// save the last opened view
		saveLastView();

		// Refresh
		this.stackLayout.topControl = viewController.getViewControl();
		viewController.getView().refresh();

		// Layout
		this.layout();
	}

	/**
	 * Refresh save state
	 */
	public void refreshSaveState() {
		if (null != this.stackLayout.topControl) {
			((ACredibilityView<?>) this.stackLayout.topControl).refreshStatusComposite();
		}
	}

	/**
	 * Open the help level view
	 */
	public void openRequirementHelpLevelView() {
		viewManager.openHelpLevelView();
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
	 * {@inheritDoc}
	 */
	@Override
	public Queue<BreadcrumbItemParts> getBreadcrumbItems(ACredibilityView<?> view) {
		Queue<BreadcrumbItemParts> breadcrumbItems = this.viewManager.getBreadcrumbItems(view);

		// add the pcmm home view
		BreadcrumbItemParts bcHomeItemPart = new BreadcrumbItemParts();
		if (viewController != null) {
			bcHomeItemPart.setName(viewController.getView().getItemTitle());
		} else {
			bcHomeItemPart.setName(view.getItemTitle());
		}
		bcHomeItemPart.setListener(this);
		breadcrumbItems.add(bcHomeItemPart);

		return breadcrumbItems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doBreadcrumbAction(BreadcrumbItemParts item) {
		if (item != null && item.getListener().equals(this)
				&& item.getName().equals((viewController.getView()).getItemTitle())) {
			openHome();
		}
	}

	@Override
	public void reload() {

		// reload views
		if (viewController.getView() != null) {
			viewController.getView().reload();
		}
	}

	@Override
	public void reloadActiveView() {
		if (stackLayout.topControl instanceof ICredibilityView) {
			((ICredibilityView) stackLayout.topControl).reload();
		}
	}

	@Override
	public CredibilityEditor getCredibilityEditor() {
		if (viewManager == null) {
			return null;
		}
		return viewManager.getCredibilityEditor();
	}
}
