/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * The Interface INewCFProcessFileSelectionPage.
 *
 * @author Didier Verstraete
 */
public interface INewCFProcessFileSelectionPage extends IWizardPage {

	/**
	 * Gets the container full path.
	 *
	 * @return the container full path
	 */
	IPath getContainerFullPath();

	/**
	 * Gets the selected filename.
	 *
	 * @return the selected filename
	 */
	String getSelectedFilename();

}
