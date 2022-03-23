/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import gov.sandia.cf.dao.IDaoManager;

/**
 * The main interface to define for each application service.
 * 
 * @author Didier Verstraete
 *
 */
public interface IApplication {

	/**
	 * @return the application manager
	 */
	IApplicationManager getAppMgr();

	/**
	 * Set the application manager
	 * 
	 * @param appMgr the application manager to set
	 */
	void setAppMgr(IApplicationManager appMgr);

	/**
	 * @return the dao manager
	 */
	IDaoManager getDaoManager();
}
