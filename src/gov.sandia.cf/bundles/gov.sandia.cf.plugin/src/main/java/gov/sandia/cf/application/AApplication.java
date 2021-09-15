/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import gov.sandia.cf.dao.DaoManager;

/**
 * The abstract class to extend for every application service
 * 
 * @author Didier Verstraete
 * 
 */
public abstract class AApplication implements IApplication {

	/**
	 * The application manager
	 */
	private IApplicationManager appMgr;

	/**
	 * AApplication constructor
	 */
	public AApplication() {
		this.appMgr = null;
	}

	/**
	 * AApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public AApplication(IApplicationManager appMgr) {
		this.appMgr = appMgr;
	}

	/**
	 * @return the application manager
	 */
	public IApplicationManager getAppMgr() {
		return appMgr;
	}

	public void setAppMgr(IApplicationManager appMgr) {
		this.appMgr = appMgr;
	}

	/**
	 * @return the dao manager, null if appMgr is null
	 */
	public DaoManager getDaoManager() {
		if (appMgr == null) {
			return null;
		}
		return appMgr.getDaoManager();
	}

}
