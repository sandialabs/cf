/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

/**
 * The CF generic table value interface for generic fields represented into
 * tables.
 * 
 * @author Didier Verstraete
 *
 */
public interface IGenericTableValue {

	/**
	 * @return the generic parameter
	 */
	GenericParameter<?> getParameter();

	/**
	 * @return the value
	 */
	String getValue();

	/**
	 * Sets the value field with @param value
	 * 
	 * @param value the value to set
	 */
	void setValue(String value);

	/**
	 * 
	 * @return The value with a readable format
	 */
	String getReadableValue();
}
