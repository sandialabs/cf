/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.services.setup;

import java.io.File;
import java.io.IOException;

import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.launcher.CFClientSetup;
import gov.sandia.cf.parts.services.IClientService;

/**
 * The Interface ISetupService used to initialize the project.
 * 
 * @author Didier Verstraete
 */
@Service
public interface ISetupService extends IClientService {

	/**
	 * Load CF client setup.
	 *
	 * @param setupFile the setup file
	 * @return the CF client setup
	 * @throws CredibilityException the credibility exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 */
	CFClientSetup load(File setupFile) throws CredibilityException, IOException;
}
