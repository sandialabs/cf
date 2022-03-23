/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import gov.sandia.cf.application.IApplicationManager;
import gov.sandia.cf.launcher.CFCache;
import gov.sandia.cf.launcher.CredibilityEditor;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;
import gov.sandia.cf.parts.services.IClientService;
import gov.sandia.cf.web.services.IWebClientManager;

/**
 * The abstract view manager to handle commons view managers methods.
 * 
 * @author Didier Verstraete
 *
 */
public abstract class AViewManager extends Composite implements IViewManager {

	/**
	 * the credibility framework entry point view (parent)
	 */
	protected MainViewManager viewManager;

	/**
	 * Constructor
	 * 
	 * @param parentView the parent view
	 * @param parent     the parent composite
	 * @param style      the view SWT style
	 */
	protected AViewManager(MainViewManager parentView, Composite parent, int style) {
		super(parent, style);
		this.viewManager = parentView;
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
	public IApplicationManager getAppManager() {
		if (getCredibilityEditor() == null) {
			return null;
		}
		return getCredibilityEditor().getAppMgr();
	}

	@Override
	public CFCache getCache() {
		if (getCredibilityEditor() == null) {
			return null;
		}
		return getCredibilityEditor().getCache();
	}

	@Override
	public IWebClientManager getWebClient() {
		if (getCredibilityEditor() == null) {
			return null;
		}
		return getCredibilityEditor().getWebClient();
	}

	/**
	 * @return the PIRT configuration
	 */
	public PIRTSpecification getPIRTConfiguration() {
		return getCache().getPIRTSpecification();
	}

	/**
	 * @return the PCMM configuration
	 */
	public PCMMSpecification getPCMMConfiguration() {
		return getCache().getPCMMSpecification();
	}

	/**
	 * @return the Uncertainty configuration
	 */
	public UncertaintySpecification getUncertaintyConfiguration() {
		return getCache().getUncertaintySpecification();
	}

	/**
	 * @return the Requirement configuration
	 */
	public SystemRequirementSpecification getSystemRequirementConfiguration() {
		return getCache().getSystemRequirementSpecification();
	}

	/** {@inheritDoc} */
	@Override
	public void viewChanged() {
		if (!isWebConnection() && getCredibilityEditor() != null) {
			getCredibilityEditor().setDirty(true);
			viewManager.refreshSaveState();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void doSave() {
		if (getCredibilityEditor() != null) {
			// save just after
			// create asynchronous save job (otherwise it may not be saved)
			Display.getCurrent().asyncExec(() -> getCredibilityEditor().doSave(new NullProgressMonitor()));
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDirty() {
		return getCredibilityEditor() != null && getCredibilityEditor().isDirty();
	}

	/** {@inheritDoc} */
	@Override
	public void plugPIRTButton(Control button) {
		viewManager.plugPIRTButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void plugQoIPlanningButton(Control button) {
		viewManager.plugQoIPlanningButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void plugPCMMPlanningButton(Control button) {
		viewManager.plugPCMMPlanningButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void plugDecisionButton(Control button) {
		viewManager.plugDecisionButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void plugPCMMButton(Control button) {
		viewManager.plugPCMMButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void plugUncertaintyButton(Control button) {
		viewManager.plugUncertaintyButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void plugSystemRequirementsButton(Control button) {
		viewManager.plugSystemRequirementsButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void plugIntendedPurposeButton(Control button) {
		viewManager.plugIntendedPurposeButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void plugReportButton(Control button) {
		viewManager.plugReportButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void plugConfigurationButton(Control button) {
		viewManager.plugConfigurationButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void plugPreviousViewButton(Button button) {
		viewManager.plugPreviousViewButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public void plugBackHomeButton(Button button) {
		viewManager.plugBackHomeButton(button);
	}

	/** {@inheritDoc} */
	@Override
	public ResourceManager getRscMgr() {
		if (viewManager == null) {
			return null;
		}
		return viewManager.getRscMgr();
	}

	/** {@inheritDoc} */
	@Override
	public CredibilityEditor getCredibilityEditor() {
		if (viewManager == null) {
			return null;
		}
		return viewManager.getCredibilityEditor();
	}

	@Override
	public <S extends IClientService> S getClientService(Class<S> interfaceClass) {
		return viewManager.getClientService(interfaceClass);
	}

	/** {@inheritDoc} */
	@Override
	public void quit() {
		// do nothing by default
	}
}
