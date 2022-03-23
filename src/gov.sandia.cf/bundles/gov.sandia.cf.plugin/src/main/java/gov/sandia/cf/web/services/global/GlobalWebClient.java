/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.global;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.global.IGlobalApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.ConfigurationFile;
import gov.sandia.cf.model.GlobalConfiguration;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.ConfigurationSchema;
import gov.sandia.cf.web.services.WebClientManager;

/**
 * Global Application manager for methods that are not specific to credibility
 * features (like PIRT, PCMM,...)
 * 
 * @author Didier Verstraete
 * 
 */
public class GlobalWebClient extends AApplication implements IGlobalApplication {

	/**
	 * GlobalApplication constructor
	 */
	public GlobalWebClient() {
		super();
	}

	/**
	 * GlobalApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public GlobalWebClient(WebClientManager appMgr) {
		super(appMgr);
	}

	@Override
	public Model importModel(ConfigurationSchema confSchema) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model loadModel() throws CredibilityException {
		// TODO not used by web
		return null;
	}

	@Override
	public Boolean existsModel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Model updateModel(Model model) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> loadModelClass(String modelClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigurationFile addConfigurationFile(Model model, User user, CFFeature feature, String path)
			throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GlobalConfiguration loadGlobalConfiguration() throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GlobalConfiguration updateGlobalConfiguration(GlobalConfiguration configuration)
			throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OpenLinkBrowserOption getOpenLinkBrowserOpts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh(Model model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(GlobalConfiguration model) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDatabaseVersion() {
		// TODO Auto-generated method stub
		return null;
	}
}
