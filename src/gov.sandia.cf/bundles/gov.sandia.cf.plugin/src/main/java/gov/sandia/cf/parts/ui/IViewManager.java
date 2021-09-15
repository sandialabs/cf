/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui;

import java.util.Queue;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.launcher.CFCache;
import gov.sandia.cf.launcher.CredibilityEditor;
import gov.sandia.cf.parts.model.BreadcrumbItemParts;

/**
 * 
 * The view manager interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IViewManager {

	/**
	 * @return the credibility editor
	 */
	CredibilityEditor getCredibilityEditor();

	/**
	 * @return the application manager
	 */
	ApplicationManager getAppManager();

	/**
	 * @return the cf cache
	 */
	CFCache getCache();

	/**
	 * @return the resource manager to handle SWT resources binded to the OS (fonts,
	 *         colors, images, cursors...)
	 */
	ResourceManager getRscMgr();

	/**
	 * @param view the view to get the items
	 * @return the set of items
	 */
	Queue<BreadcrumbItemParts> getBreadcrumbItems(ACredibilityView<?> view);

	/**
	 * Do the breadcrumb action once the item associated is clicked
	 * 
	 * @param item the item clicked
	 */
	void doBreadcrumbAction(BreadcrumbItemParts item);

	/**
	 * Set the view changed properties and mechanisms
	 */
	void viewChanged();

	/**
	 * Do save the credibility editor
	 */
	void doSave();

	/**
	 * Reload the view manager
	 */
	void reload();

	/**
	 * @return true if the credibility editor needs to be saved, otherwise false.
	 */
	boolean isDirty();

	/**
	 * Open the view manager home view
	 */
	void openHome();

	/**
	 * Refresh save state
	 */
	void refreshSaveState();

	/**
	 * Plug the button to the PIRT view
	 * 
	 * @param button the button to set
	 */
	void plugPIRTButton(Control button);

	/**
	 * Plug the button to the QoI Planning view
	 * 
	 * @param button the button to set
	 */
	void plugQoIPlanningButton(Control button);

	/**
	 * Plug the button to the PCMM Planning view
	 * 
	 * @param button the button to set
	 */
	void plugPCMMPlanningButton(Control button);

	/**
	 * Plug the button to the Analyst Decision view
	 * 
	 * @param button the button to set
	 */
	void plugDecisionButton(Control button);

	/**
	 * Plug the button to the PCMM view
	 * 
	 * @param button the button to set
	 */
	void plugPCMMButton(Control button);

	/**
	 * Plug the button to the Uncertainty view
	 * 
	 * @param button the button to set
	 */
	void plugUncertaintyButton(Control button);

	/**
	 * Plug the button to the System Requirement view
	 * 
	 * @param button the button to set
	 */
	void plugSystemRequirementsButton(Control button);

	/**
	 * Plug the button to the Intended purpose view
	 * 
	 * @param button the button to set
	 */
	void plugIntendedPurposeButton(Control button);

	/**
	 * Plug the button to the Report view
	 * 
	 * @param button the button to set
	 */
	void plugReportButton(Control button);

	/**
	 * Plug the button to the configuration view
	 * 
	 * @param button the button to set
	 */
	void plugConfigurationButton(Control button);

	/**
	 * Plug the button to the Previous view
	 * 
	 * @param button the button to set
	 */
	void plugPreviousViewButton(Button button);

	/**
	 * Plug the button to the Home view
	 * 
	 * @param button the button to set
	 */
	void plugBackHomeButton(Button button);

}
