/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.services.setup;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.launcher.CFClientSetup;
import gov.sandia.cf.launcher.CFClientSetupFactory;
import gov.sandia.cf.parts.services.AClientService;
import gov.sandia.cf.parts.services.IClientServiceManager;

/**
 * Manage Generic Parameter Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class SetupService extends AClientService implements ISetupService {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SetupService.class);

	/**
	 * The constructor
	 */
	public SetupService() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param clientSrvMgr the client service manager
	 */
	public SetupService(IClientServiceManager clientSrvMgr) {
		super(clientSrvMgr);
	}

	/** {@inheritDoc} */
	@Override
	public CFClientSetup load(File setupFile) throws CredibilityException, IOException {
		logger.debug("Load client setup from file {}", setupFile); //$NON-NLS-1$

		YmlReaderClientSetup reader = new YmlReaderClientSetup();
		if (reader.isValid(setupFile)) {
			return reader.load(setupFile);
		} else {
			// default setup
			return CFClientSetupFactory.get();
		}
	}

}