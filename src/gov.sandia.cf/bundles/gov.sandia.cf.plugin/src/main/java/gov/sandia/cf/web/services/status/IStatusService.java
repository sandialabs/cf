/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.status;

import gov.sandia.cf.application.Service;
import gov.sandia.cf.web.services.IWebClient;

/**
 * The Interface ISetupService used to initialize the project.
 * 
 * @author Didier Verstraete
 */
@Service
public interface IStatusService extends IWebClient {

	/**
	 * Ping.
	 *
	 * @return true, if successful
	 */
	boolean ping();
}
