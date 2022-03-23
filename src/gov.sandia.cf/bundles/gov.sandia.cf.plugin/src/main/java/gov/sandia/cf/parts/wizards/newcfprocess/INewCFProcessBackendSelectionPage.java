/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * The Interface INewCFProcessBackendSelectionPage.
 * 
 * @author Didier Verstraete
 */
public interface INewCFProcessBackendSelectionPage extends IWizardPage {

	/**
	 * Checks if is local file.
	 *
	 * @return true, if is local file
	 */
	boolean isLocalFile();

	/**
	 * Checks if is web.
	 *
	 * @return true, if is web
	 */
	boolean isWeb();

}
