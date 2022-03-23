/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.report;

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
 * The report view.
 * 
 * @author Maxime N.
 *
 */
public class ReportViewManager extends AViewManager implements Listener, IViewManager {
	/**
	 * the Report home view
	 */
	private ReportView reportView;

	/**
	 * the last control opened
	 */
	private Control lastControl;

	/**
	 * the layout manager to hide/show the different views
	 */
	private StackLayout stackLayout;

	/**
	 * CrediblityView constructor
	 * 
	 * @param parentView the parent view
	 * @param parent     the parent composite
	 * @param style      the style
	 */
	public ReportViewManager(MainViewManager parentView, Composite parent, int style) {
		super(parentView, parent, style);
		this.lastControl = null;

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

		// load home view
		this.reportView = new ReportView(this, SWT.NONE);

		// display homeView first
		this.stackLayout.topControl = reportView;
		this.lastControl = reportView;

		this.layout();
	}

	/**
	 * Plug the button to this class to listen back home events
	 * 
	 * @param button the button
	 */
	public void plugBackHomeButton(Button button) {
		this.viewManager.plugBackHomeButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void openHome() {

		// save the last opened view
		saveLastView();

		if (this.reportView == null) {
			this.reportView = new ReportView(this, SWT.NONE);
		}

		// Refresh
		this.stackLayout.topControl = reportView;
		reportView.refresh();

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
		if (reportView != null) {
			bcHomeItemPart.setName(reportView.getItemTitle());
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
		if (item != null && item.getListener().equals(this) && item.getName().equals((reportView).getItemTitle())) {
			openHome();
		}
	}

	@Override
	public void handleEvent(Event event) {
		// TODO to implement
	}

	@Override
	public void reload() {

		// reload views
		if (reportView != null) {
			reportView.reload();
		}
	}

	@Override
	public void reloadActiveView() {
		if (stackLayout.topControl instanceof ICredibilityView) {
			((ICredibilityView) stackLayout.topControl).reload();
		}
	}
}
