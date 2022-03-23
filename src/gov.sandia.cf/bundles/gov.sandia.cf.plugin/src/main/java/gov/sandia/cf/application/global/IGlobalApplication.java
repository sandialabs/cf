/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.global;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.ConfigurationFile;
import gov.sandia.cf.model.GlobalConfiguration;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.ConfigurationSchema;

/**
 * Global Application interface for methods that are not specific to credibility
 * features (like PIRT, PCMM,...)
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IGlobalApplication extends IApplication {
	/**
	 * Import CF model
	 * 
	 * @param confSchema The list of config type with config file
	 * @return the model for this cf instance. If the model not exists in database,
	 *         it is created
	 * @throws CredibilityException if model can not be loaded
	 */
	Model importModel(ConfigurationSchema confSchema) throws CredibilityException;

	/**
	 * Load the CF model
	 * 
	 * @return the model for this cf instance. If the model not exists in database,
	 *         it is created
	 * @throws CredibilityException if model can not be loaded
	 */
	Model loadModel() throws CredibilityException;

	/**
	 * @return true if one or more models exists, otherwise false
	 */
	Boolean existsModel();

	/**
	 * Update the CF model
	 * 
	 * @param model the model to update
	 * @return the updated model
	 * @throws CredibilityException if an error occured when updating model
	 */
	Model updateModel(Model model) throws CredibilityException;

	/**
	 * Load model class
	 * 
	 * @param modelClass the model entity class type
	 * @return the model class
	 */
	Class<?> loadModelClass(String modelClass);

	/**
	 * Add a configuration file.
	 *
	 * @param model   the model associated
	 * @param user    the user
	 * @param feature the cf feature associated
	 * @param path    the file path
	 * @return the newly created configuration file
	 * @throws CredibilityException if an error occurs
	 */
	ConfigurationFile addConfigurationFile(Model model, User user, CFFeature feature, String path)
			throws CredibilityException;

	/**
	 * Load the global configuration
	 * 
	 * @return the global configuration
	 * @throws CredibilityException if an error occurs
	 */
	GlobalConfiguration loadGlobalConfiguration() throws CredibilityException;

	/**
	 * Update the global configuration
	 * 
	 * @param configuration the global configuration to update
	 * @return the configuration updated
	 * @throws CredibilityException if a user error occurs
	 */
	GlobalConfiguration updateGlobalConfiguration(GlobalConfiguration configuration) throws CredibilityException;

	/**
	 * @return the open link browser option, null if not found
	 */
	OpenLinkBrowserOption getOpenLinkBrowserOpts();

	/**
	 * Refresh the model
	 * 
	 * @param model the model
	 */
	void refresh(Model model);

	/**
	 * Refresh the global configuration
	 * 
	 * @param model the global configuration
	 */
	void refresh(GlobalConfiguration model);

	/**
	 * @return the database version
	 */
	String getDatabaseVersion();

}
