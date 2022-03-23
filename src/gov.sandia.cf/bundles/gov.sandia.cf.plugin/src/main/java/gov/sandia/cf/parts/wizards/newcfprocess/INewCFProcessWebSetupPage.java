/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * The CF Web Setup wizard page interface
 * 
 * @author Didier Verstraete
 *
 */
public interface INewCFProcessWebSetupPage extends IWizardPage {

	/**
	 * Gets the generate folder structure.
	 *
	 * @return true if the generate folder structure is to generate, otherwise
	 *         false.
	 */
	boolean getGenerateFolderStructure();

	/**
	 * Gets the server URL.
	 *
	 * @return the server URL
	 */
	String getServerURL();

}
