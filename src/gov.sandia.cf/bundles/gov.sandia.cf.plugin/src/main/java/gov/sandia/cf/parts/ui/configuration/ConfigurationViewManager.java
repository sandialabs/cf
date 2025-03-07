/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.configuration;

import java.util.Queue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.launcher.CredibilityEditor;
import gov.sandia.cf.parts.model.BreadcrumbItemParts;
import gov.sandia.cf.parts.ui.ACredibilityView;
import gov.sandia.cf.parts.ui.AViewManager;
import gov.sandia.cf.parts.ui.ICredibilityView;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Configuration view manager: handle view transition and link to the main view
 * 
 * @author Didier Verstraete
 *
 */
public class ConfigurationViewManager extends AViewManager implements IViewManager {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationViewManager.class);

	/** The folder. */
	private CTabFolder folder;

	/** The export configuration view controller. */
	private ExportConfigurationViewController exportConfigurationViewController;

	/** The global configuration view controller. */
	private GlobalConfigurationViewController globalConfigurationViewController;

	/** The import configuration view controller. */
	private ImportConfigurationViewController importConfigurationViewController;

	/**
	 * The constructor
	 * 
	 * @param parent     the parent composite
	 * @param parentView the parent view
	 * @param style      the view style
	 */
	public ConfigurationViewManager(MainViewManager parentView, Composite parent, int style) {
		super(parentView, parent, style);

		this.setLayout(new FillLayout());

		// the layout manager to hide/show the different views
		folder = new CTabFolder(this, SWT.BOTTOM);

		this.exportConfigurationViewController = new ExportConfigurationViewController(this, folder);
		this.globalConfigurationViewController = new GlobalConfigurationViewController(this, folder);
		this.importConfigurationViewController = new ImportConfigurationViewController(this, folder);

		createPage();
	}

	/**
	 * Creates the view with all the components
	 * 
	 * @param parent
	 * 
	 */
	private void createPage() {

		logger.debug("Creating Configuration view page"); //$NON-NLS-1$

		CTabItem tabGlobal = new CTabItem(folder, SWT.NONE);
		tabGlobal.setText(RscTools.getString(RscConst.MSG_CONF_VIEW_TAB_GLOBAL));
		tabGlobal.setControl(globalConfigurationViewController.getViewControl());

		CTabItem tabImport = new CTabItem(folder, SWT.NONE);
		tabImport.setText(RscTools.getString(RscConst.MSG_CONF_VIEW_TAB_IMPORT));
		tabImport.setControl(importConfigurationViewController.getViewControl());

		CTabItem tabExport = new CTabItem(folder, SWT.NONE);
		tabExport.setText(RscTools.getString(RscConst.MSG_CONF_VIEW_TAB_EXPORT));
		tabExport.setControl(exportConfigurationViewController.getViewControl());

		folder.setSelection(tabGlobal);

		this.layout();
	}

	/**
	 * @param button the button to plug on back action
	 */
	public void plugBackButton(Button button) {
		viewManager.plugPreviousViewButton(button);
	}

	/**
	 * Refresh save state
	 */
	public void refreshSaveState() {
		importConfigurationViewController.getView().refreshStatusComposite();
		exportConfigurationViewController.getView().refreshStatusComposite();
		globalConfigurationViewController.getView().refreshStatusComposite();
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

		// add the configuration home view
		BreadcrumbItemParts bcHomeItemPart = new BreadcrumbItemParts();
		if (view != null) {
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
		if (item != null && item.getListener().equals(this)) {
			openConfigurationHomeView(globalConfigurationViewController.getView());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void openHome() {
		openConfigurationHomeView(globalConfigurationViewController.getView());
	}

	/**
	 * Open the configuration home view
	 * 
	 * @param view the credibility view
	 */
	public void openConfigurationHomeView(ICredibilityView view) {

		// Refresh
		if (view != null) {
			view.refresh();
			this.layout();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reload() {
		// reload views
		if (importConfigurationViewController.getView() != null) {
			importConfigurationViewController.getView().reload();
		}
		if (exportConfigurationViewController.getView() != null) {
			exportConfigurationViewController.getView().reload();
		}
		if (globalConfigurationViewController.getView() != null) {
			globalConfigurationViewController.getView().reload();
		}
	}

	@Override
	public void reloadActiveView() {
		if (folder != null && folder.getSelection() != null
				&& folder.getSelection().getControl() instanceof ICredibilityView) {
			((ICredibilityView) folder.getSelection().getControl()).reload();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void plugConfigurationButton(Control button) {
		// Do nothing, we are already on Configuration view
	}
}
