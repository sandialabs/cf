/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.util.List;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IGlobalApplication;
import gov.sandia.cf.application.configuration.ConfigurationSchema;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.dao.IConfigurationFileRepository;
import gov.sandia.cf.dao.IGlobalConfigurationRepository;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.ConfigurationFile;
import gov.sandia.cf.model.GlobalConfiguration;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Global Application manager for methods that are not specific to credibility
 * features (like PIRT, PCMM,...)
 * 
 * @author Didier Verstraete
 * 
 */
public class GlobalApplication extends AApplication implements IGlobalApplication {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(GlobalApplication.class);

	private static final String CF_MODEL_PACKAGE_PREFIX = "gov.sandia.cf.model."; //$NON-NLS-1$

	/**
	 * GlobalApplication constructor
	 */
	public GlobalApplication() {
		super();
	}

	/**
	 * GlobalApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public GlobalApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	/** {@inheritDoc} */
	@Override
	public Model importModel(ConfigurationSchema confSchema) throws CredibilityException {
		// Get Model
		Model model = getDaoManager().getRepository(IModelRepository.class).getFirst();

		// create or update model
		if (model == null) {
			Model modelToCreate = new Model();
			modelToCreate.setVersion(getVersion());
			modelToCreate.setVersionOrigin(getVersion());
			model = getDaoManager().getRepository(IModelRepository.class).create(modelToCreate);
		} else {
			model.setVersion(getVersion());
			if (model.getVersionOrigin() == null) {
				model.setVersionOrigin(getVersion());
			}
			model = updateModel(model);
		}

		// refresh
		getDaoManager().getRepository(IModelRepository.class).refresh(model);

		return model;
	}

	/** {@inheritDoc} */
	@Override
	public Model loadModel() throws CredibilityException {

		Model model = getDaoManager().getRepository(IModelRepository.class).getFirst();

		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_GLB_LOAD_MODEL_NULL));
		}

		return model;
	}

	/** {@inheritDoc} */
	@Override
	public Boolean existsModel() {
		Model model = getDaoManager().getRepository(IModelRepository.class).getFirst();
		return model != null;
	}

	/** {@inheritDoc} */
	@Override
	public Model updateModel(Model model) throws CredibilityException {

		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UPDATEMODEL_MODELNULL));
		} else if (model.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UPDATEMODEL_IDNULL));
		}

		return getDaoManager().getRepository(IModelRepository.class).update(model);
	}

	/** {@inheritDoc} */
	@Override
	public Class<?> loadModelClass(String modelClass) {
		Class<?> resultClass = Object.class;
		try {
			resultClass = Class.forName(CF_MODEL_PACKAGE_PREFIX + modelClass);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		return resultClass;
	}

	/** {@inheritDoc} */
	@Override
	public ConfigurationFile addConfigurationFile(Model model, CFFeature feature, String path)
			throws CredibilityException {

		if (model == null || feature == null) {
			return null;
		}

		// create new
		ConfigurationFile confFile = new ConfigurationFile();
		confFile.setModel(model);
		confFile.setFeature(feature);
		confFile.setPath(path);
		confFile.setDateImport(DateTools.getCurrentDate());
		ConfigurationFile created = getDaoManager().getRepository(IConfigurationFileRepository.class).create(confFile);

		// refresh model
		getDaoManager().getRepository(IModelRepository.class).refresh(model);

		return created;
	}

	/**
	 * @return the bundle version
	 */
	private static String getVersion() {
		String version = RscTools.empty();

		Bundle cfBundle = CredibilityFrameworkConstants.getBundle();
		if (cfBundle != null && cfBundle.getVersion() != null) {
			version = cfBundle.getVersion().toString();
		}
		return version;
	}

	/** {@inheritDoc} */
	@Override
	public GlobalConfiguration loadGlobalConfiguration() throws CredibilityException {

		List<GlobalConfiguration> listConfiguration = getDaoManager()
				.getRepository(IGlobalConfigurationRepository.class).findAll();

		GlobalConfiguration actualConfiguration = null;

		if (listConfiguration == null || listConfiguration.isEmpty()) {
			actualConfiguration = getDaoManager().getRepository(IGlobalConfigurationRepository.class)
					.create(new GlobalConfiguration());
		} else {
			actualConfiguration = listConfiguration.iterator().next();
		}

		return actualConfiguration;
	}

	/** {@inheritDoc} */
	@Override
	public GlobalConfiguration updateGlobalConfiguration(GlobalConfiguration configuration)
			throws CredibilityException {

		if (configuration == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UPDATEGLBCONF_CONFNULL));
		} else if (configuration.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UPDATEGLBCONF_IDNULL));
		}

		return getDaoManager().getRepository(IGlobalConfigurationRepository.class).update(configuration);
	}

	/** {@inheritDoc} */
	@Override
	public OpenLinkBrowserOption getOpenLinkBrowserOpts() {

		OpenLinkBrowserOption option = null;
		GlobalConfiguration glbConfiguration = null;

		try {
			glbConfiguration = getAppMgr().getService(IGlobalApplication.class).loadGlobalConfiguration();
		} catch (CredibilityException e) {
			// do nothing
		}

		if (glbConfiguration != null) {
			try {
				option = OpenLinkBrowserOption.valueOf(glbConfiguration.getOpenLinkBrowserOpts());
			} catch (IllegalArgumentException e) {
				// do nothing
			}
		}

		return option;
	}

	/** {@inheritDoc} */
	@Override
	public void refresh(Model model) {
		getDaoManager().getRepository(IModelRepository.class).refresh(model);
	}

	/** {@inheritDoc} */
	@Override
	public void refresh(GlobalConfiguration model) {
		getDaoManager().getRepository(IGlobalConfigurationRepository.class).refresh(model);
	}
}
