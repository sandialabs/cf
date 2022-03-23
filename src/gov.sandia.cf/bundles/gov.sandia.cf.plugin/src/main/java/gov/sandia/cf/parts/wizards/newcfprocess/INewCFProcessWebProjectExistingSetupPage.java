/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import org.eclipse.jface.wizard.IWizardPage;

import gov.sandia.cf.model.Model;

/**
 * The Interface INewCFProcessWebProjectExistingSetupPage.
 *
 * @author Didier Verstraete
 */
public interface INewCFProcessWebProjectExistingSetupPage extends IWizardPage {

	/**
	 * Gets the selected model.
	 *
	 * @return the selected model
	 */
	Model getSelectedModel();

}
