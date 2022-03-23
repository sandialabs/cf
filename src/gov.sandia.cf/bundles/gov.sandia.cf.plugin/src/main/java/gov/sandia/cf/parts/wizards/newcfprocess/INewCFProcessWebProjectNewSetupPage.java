/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import org.eclipse.jface.wizard.IWizardPage;

import gov.sandia.cf.model.Model;

/**
 * The Interface INewCFProcessWebProjectNewSetupPage.
 *
 * @author Didier Verstraete
 */
public interface INewCFProcessWebProjectNewSetupPage extends IWizardPage {

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	Model getModel();
}
