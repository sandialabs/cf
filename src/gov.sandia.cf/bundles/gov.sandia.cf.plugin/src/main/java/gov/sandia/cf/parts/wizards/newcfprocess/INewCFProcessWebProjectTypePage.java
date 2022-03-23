/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * The CF Web Project wizard page interface.
 * 
 * @author Didier Verstraete
 *
 */
public interface INewCFProcessWebProjectTypePage extends IWizardPage {

	/**
	 * Checks if is new project.
	 *
	 * @return true, if is new project
	 */
	boolean isNewProject();

	/**
	 * Checks if is existing project.
	 *
	 * @return true, if is existing project
	 */
	boolean isExistingProject();

	/**
	 * Gets the web project type.
	 *
	 * @return the web project type
	 */
	CFWebProjectType getWebProjectType();

}
