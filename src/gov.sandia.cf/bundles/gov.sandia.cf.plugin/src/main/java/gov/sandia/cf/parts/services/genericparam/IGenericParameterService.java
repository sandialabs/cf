/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.services.genericparam;

import java.util.List;

import gov.sandia.cf.application.Service;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.parts.services.IClientService;

/**
 * Generic parameter application interface for methods that are specific to the
 * generic parameters and values.
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IGenericParameterService extends IClientService {

	/**
	 * Open the selected Generic Value if it is a link.
	 *
	 * @param value the generic value
	 * @param browserOpts the browser opts
	 */
	void openLinkValue(GenericValue<?, ?> value, OpenLinkBrowserOption browserOpts);

	/**
	 * Check if parameter has to be registered or displayed
	 * 
	 * @param parameter   the parameter to check
	 * @param levelNumber the level number to check
	 * @return True if the parameter has to be registered or displayed
	 */
	boolean isParameterAvailableForLevel(GenericParameter<?> parameter, int levelNumber);

	/**
	 * @param parameter the generic parameter to handle
	 * @return the parameter name with its required prefix (none if not required,
	 *         '*' if required, '**' if conditionally required)
	 */
	String getParameterNameWithRequiredPrefix(GenericParameter<?> parameter);

	/**
	 * @param value the value to check
	 * @param items the other items to compare with
	 * @return the notification for unfulfilled constraints for the value in
	 *         parameter, otherwise an empty set.
	 */
	Notification checkValid(GenericValue<?, ?> value, List<? extends GenericValue<?, ?>> items);

}
