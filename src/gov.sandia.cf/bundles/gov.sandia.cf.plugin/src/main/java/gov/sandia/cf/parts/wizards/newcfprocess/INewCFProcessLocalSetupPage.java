/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import org.eclipse.jface.wizard.IWizardPage;

import gov.sandia.cf.model.dto.configuration.ConfigurationSchema;

/**
 * The CF Setup wizard page interface
 * 
 * @author Didier Verstraete
 *
 */
public interface INewCFProcessLocalSetupPage extends IWizardPage {
	/**
	 * @return the configuration folder path
	 */
	String getConfigurationFolderDefaultPath();

	/**
	 * @return the configuration schema
	 */
	ConfigurationSchema getConfigurationSchema();

	/**
	 * @return true if generate folder structure is checked, otherwise false
	 */
	boolean getGenerateFolderStructure();

}
