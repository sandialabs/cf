/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.util.List;

import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.model.Notification;

/**
 * Generic parameter application interface for methods that are specific to the
 * generic parameters and values.
 * 
 * @author Didier Verstraete
 *
 */
public interface IGenericParameterApplication extends IApplication {

	/**
	 * @param value the generic value to display
	 * @return a string representing the value depending of its type
	 */
	String getReadableValue(GenericValue<?, ?> value);

	/**
	 * Open the selected Generic Value if it is a link
	 * 
	 * @param value the generic value
	 */
	void openLinkValue(GenericValue<?, ?> value);

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
	 * @return true if the value is valid for all constraints, otherwise false
	 */
	boolean isValid(GenericValue<?, ?> value, List<? extends GenericValue<?, ?>> items);

	/**
	 * @param value the value to check
	 * @param items the other items to compare with
	 * @return the notification for unfulfilled constraints for the value in
	 *         parameter, otherwise an empty set.
	 */
	Notification checkValid(GenericValue<?, ?> value, List<? extends GenericValue<?, ?>> items);

	/**
	 * @param value the value to check
	 * @param items the other items to compare with
	 * @return notification for required and desired fields
	 */
	Notification checkRequired(GenericValue<?, ?> value, List<? extends GenericValue<?, ?>> items);

	/**
	 * Sort values list by parameter id
	 * 
	 * @param values the list to sort
	 * @return the sorted list
	 */
	List<IGenericTableValue> sortTableValuesByParameterId(List<IGenericTableValue> values);

}
