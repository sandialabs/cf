/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import javax.validation.Validator;

import gov.sandia.cf.dao.DaoManager;
import gov.sandia.cf.launcher.IManager;

/**
 * The main interface to define the application manager.
 * 
 * @author Didier Verstraete
 *
 */
public interface IApplicationManager extends IManager {

	/**
	 * @param <S>            the service interface inherited IApplication class
	 * @param interfaceClass the service interface class
	 * @return the associated service if found, otherwise null
	 */
	public <S extends IApplication> S getService(Class<S> interfaceClass);

	/**
	 * @return the dao manager
	 */
	DaoManager getDaoManager();

	/**
	 * @return the bean validator
	 */
	Validator getValidator();
}
