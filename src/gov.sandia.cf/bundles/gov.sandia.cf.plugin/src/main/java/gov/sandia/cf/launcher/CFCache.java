/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.decision.IDecisionApplication;
import gov.sandia.cf.application.global.IGlobalApplication;
import gov.sandia.cf.application.global.IUserApplication;
import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.application.pirt.YmlReaderPIRTQueries;
import gov.sandia.cf.application.qoiplanning.IQoIPlanningApplication;
import gov.sandia.cf.application.requirement.ISystemRequirementApplication;
import gov.sandia.cf.application.uncertainty.IUncertaintyApplication;
import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GlobalConfiguration;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.dto.configuration.PIRTQuery;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;
import gov.sandia.cf.parts.services.setup.ISetupService;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.CFVariableResolver;
import gov.sandia.cf.web.services.authentication.IAuthenticationService;
import gov.sandia.cf.web.services.global.IModelWebClient;

/**
 * The CF data cache class.
 * 
 * @author Didier Verstraete
 *
 */
public class CFCache {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(CFCache.class);

	/**
	 * the credibility editor
	 */
	private CredibilityEditor credibilityEditor;

	/**
	 * the simulation model to attach credibility with
	 */
	private Model model;

	/**
	 * the current user
	 */
	private User user;

	/**
	 * the PIRT configuration
	 */
	private PIRTSpecification pirtSpecification;

	/**
	 * the Decision configuration
	 */
	private DecisionSpecification decisionSpecification;

	/**
	 * the QoI Planning configuration
	 */
	private QoIPlanningSpecification qoiPlanningSpecification;

	/**
	 * the PCMM configuration
	 */
	private PCMMSpecification pcmmSpecification;

	/**
	 * The uncertainty specification
	 */
	private UncertaintySpecification uncertaintySpecification;

	/**
	 * The system requirement specification
	 */
	private SystemRequirementSpecification sysRequirementSpecification;

	/**
	 * the PIRT queries
	 */
	private List<PIRTQuery> pirtQueries;

	/** The global configuration. */
	private GlobalConfiguration globalConfiguration;

	/** The cf client setup. */
	private CFClientSetup cfClientSetup;

	/**
	 * The constructor
	 * 
	 * @param credibilityEditor the associated credibility editor
	 */
	protected CFCache(CredibilityEditor credibilityEditor) {
		this.credibilityEditor = credibilityEditor;
		this.user = null;
		this.model = null;
		this.pirtSpecification = null;
		this.decisionSpecification = null;
		this.qoiPlanningSpecification = null;
		this.pcmmSpecification = null;
		this.uncertaintySpecification = null;
		this.sysRequirementSpecification = null;
		this.cfClientSetup = null;
	}

	/**
	 * @return the CF model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * Refresh the CF model
	 * 
	 * Web: for the web call, please initialize the client setup with model id to
	 * load.
	 * 
	 * @throws CredibilityException if an error occured
	 */
	public void refreshModel() throws CredibilityException {
		if (credibilityEditor.isWebConnection()) {
			this.model = credibilityEditor.getWebClient().getService(IModelWebClient.class)
					.loadModel(getCFClientSetup().getModelId());
		} else {
			this.model = credibilityEditor.getAppMgr().getService(IGlobalApplication.class).loadModel();
		}
	}

	/**
	 * @return the CF Global Configuration
	 */
	public GlobalConfiguration getGlobalConfiguration() {
		return globalConfiguration;
	}

	/**
	 * Refresh the CF global configuration
	 * 
	 * @throws CredibilityException if an error occured
	 */
	public void refreshGlobalConfiguration() throws CredibilityException {
		if (credibilityEditor.isWebConnection()) {
			// TODO implement
		} else {
			this.globalConfiguration = credibilityEditor.getAppMgr().getService(IGlobalApplication.class)
					.loadGlobalConfiguration();
		}
	}

	/**
	 * @return the open link browser option
	 */
	public OpenLinkBrowserOption getOpenLinkBrowserOpts() {

		OpenLinkBrowserOption option = null;
		GlobalConfiguration glbConfiguration = getGlobalConfiguration();

		if (glbConfiguration != null) {
			try {
				option = OpenLinkBrowserOption.valueOf(glbConfiguration.getOpenLinkBrowserOpts());
			} catch (IllegalArgumentException e) {
				// do nothing
			}
		}

		return option;
	}

	/**
	 * @return the current user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Refresh the user data
	 * 
	 * @throws CredibilityException if an error occured
	 */
	public void refreshUser() throws CredibilityException {
		if (credibilityEditor.isWebConnection()) {
			this.user = credibilityEditor.getWebClient().getService(IAuthenticationService.class)
					.getUser(CFVariableResolver.resolve(CFVariable.USER_NAME));
		} else {
			this.user = credibilityEditor.getAppMgr().getService(IUserApplication.class)
					.getUserByUserID(CFVariableResolver.resolve(CFVariable.USER_NAME));
		}
	}

	/**
	 * @return the current PCMM role for the user
	 */
	public Role getCurrentPCMMRole() {
		if (user == null) {
			return null;
		}
		return user.getRolePCMM();
	}

	/**
	 * Update the current PCMM role
	 * 
	 * @param newRole the new role to apply
	 * @throws CredibilityException if an error occured
	 */
	public void updatePCMMRole(Role newRole) throws CredibilityException {
		if (credibilityEditor.isWebConnection()) {
			// TODO implement
		} else {
			credibilityEditor.getAppMgr().getService(IUserApplication.class).setCurrentPCMMRole(user, newRole);
		}
		refreshUser();
	}

	/**
	 * @return the PIRT configuration
	 */
	public PIRTSpecification getPIRTSpecification() {
		if (pirtSpecification == null) {
			if (credibilityEditor.isWebConnection()) {
				// TODO implement
			} else {
				pirtSpecification = credibilityEditor.getAppMgr().getService(IPIRTApplication.class)
						.loadPIRTConfiguration(model);
			}
		}
		return pirtSpecification;
	}

	/**
	 * @return the PIRT configuration reloaded
	 */
	public PIRTSpecification reloadPIRTSpecification() {
		pirtSpecification = null;
		return getPIRTSpecification();
	}

	/**
	 * @return the Decision configuration
	 */
	public DecisionSpecification getDecisionSpecification() {
		if (decisionSpecification == null) {
			if (credibilityEditor.isWebConnection()) {
				// TODO implement
			} else {
				decisionSpecification = credibilityEditor.getAppMgr().getService(IDecisionApplication.class)
						.loadDecisionConfiguration(model);
			}
		}
		return decisionSpecification;
	}

	/**
	 * @return the Decision configuration reloaded
	 */
	public DecisionSpecification reloadDecisionSpecification() {
		decisionSpecification = null;
		return getDecisionSpecification();
	}

	/**
	 * @return the QoI Planning configuration
	 */
	public QoIPlanningSpecification getQoIPlanningSpecification() {
		if (qoiPlanningSpecification == null) {
			if (credibilityEditor.isWebConnection()) {
				// TODO implement
			} else {
				qoiPlanningSpecification = credibilityEditor.getAppMgr().getService(IQoIPlanningApplication.class)
						.loadQoIPlanningConfiguration(model);
			}
		}
		return qoiPlanningSpecification;
	}

	/**
	 * @return the QoI Planning configuration reloaded
	 */
	public QoIPlanningSpecification reloadQoIPlanningSpecification() {
		qoiPlanningSpecification = null;
		return getQoIPlanningSpecification();
	}

	/**
	 * @return the PCMM configuration
	 */
	public PCMMSpecification getPCMMSpecification() {
		if (pcmmSpecification == null) {
			if (credibilityEditor.isWebConnection()) {
				// TODO implement
			} else {
				try {
					pcmmSpecification = credibilityEditor.getAppMgr().getService(IPCMMApplication.class)
							.loadPCMMConfiguration(model);
				} catch (CredibilityException e) {
					logger.error("An error occured while loading PCMM configuration {}", e.getMessage(), e); //$NON-NLS-1$
				}
			}
		}
		return pcmmSpecification;
	}

	/**
	 * @return the PCMM configuration reloaded
	 */
	public PCMMSpecification reloadPCMMSpecification() {
		pcmmSpecification = null;
		return getPCMMSpecification();
	}

	/**
	 * @return the Uncertainty configuration
	 */
	public UncertaintySpecification getUncertaintySpecification() {
		if (uncertaintySpecification == null) {
			if (credibilityEditor.isWebConnection()) {
				// TODO implement
			} else {
				uncertaintySpecification = credibilityEditor.getAppMgr().getService(IUncertaintyApplication.class)
						.loadUncertaintyConfiguration(model);
			}
		}
		return uncertaintySpecification;
	}

	/**
	 * @return the Uncertainty configuration reloaded
	 */
	public UncertaintySpecification reloadUncertaintySpecification() {
		uncertaintySpecification = null;
		return getUncertaintySpecification();
	}

	/**
	 * @return the System Requirement configuration
	 */
	public SystemRequirementSpecification getSystemRequirementSpecification() {
		if (sysRequirementSpecification == null) {
			if (credibilityEditor.isWebConnection()) {
				// TODO implement
			} else {
				sysRequirementSpecification = credibilityEditor.getAppMgr()
						.getService(ISystemRequirementApplication.class).loadSysRequirementConfiguration(model);
			}
		}
		return sysRequirementSpecification;
	}

	/**
	 * @return the System Requirement configuration reloaded
	 */
	public SystemRequirementSpecification reloadSystemRequirementSpecification() {
		sysRequirementSpecification = null;
		return getSystemRequirementSpecification();
	}

	/**
	 * @return the PIRT queries
	 */
	public List<PIRTQuery> getPIRTQueries() {
		if (pirtQueries == null) {

			// get query file (optional)
			File queryFile = new File(PrefTools.getPreference(PrefTools.PIRT_QUERY_FILE_PATH_KEY));

			// load query file
			if (queryFile.exists()) {
				YmlReaderPIRTQueries queriesLoader = new YmlReaderPIRTQueries();
				try {
					logger.info("Loading PIRT query file: {}", queryFile); //$NON-NLS-1$
					pirtQueries = queriesLoader.load(queryFile);
				} catch (CredibilityException | IOException e) {
					logger.error("Impossible to load the PIRT queries {}", e.getMessage(), e); //$NON-NLS-1$
				}
			} else {
				logger.debug(
						"No PIRT query file has been defined. You can set it in Preferences > Credibility Framework."); //$NON-NLS-1$
			}
		}
		return pirtQueries;
	}

	/**
	 * @return the PIRT queries reloaded
	 */
	public List<PIRTQuery> reloadPIRTQueries() {
		pirtQueries = null;
		return getPIRTQueries();
	}

	/**
	 * Gets the CF client setup.
	 *
	 * @return the CF client setup
	 */
	public CFClientSetup getCFClientSetup() {
		if (cfClientSetup == null) {
			reloadCFClientSetup(null);
		}
		return cfClientSetup;
	}

	/**
	 * Reload CF client setup.
	 *
	 * @param setupFile the setup file
	 * @throws CredibilityException the credibility exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	void reloadCFClientSetup(File setupFile) {

		CFClientSetup cfClientSetupTmp = null;

		// load file if file exists and the option is activated
		if (setupFile != null) {
			try {
				cfClientSetupTmp = credibilityEditor.getClientSrvMgr().getService(ISetupService.class).load(setupFile);
			} catch (CredibilityException | IOException e) {
				logger.warn("An error occured while loading the client setup {}", e.getMessage(), e); //$NON-NLS-1$
			}
		}

		// default behavior load file
		if (cfClientSetupTmp == null) {
			cfClientSetupTmp = CFClientSetupFactory.get();
		}

		this.cfClientSetup = cfClientSetupTmp;
	}
}
