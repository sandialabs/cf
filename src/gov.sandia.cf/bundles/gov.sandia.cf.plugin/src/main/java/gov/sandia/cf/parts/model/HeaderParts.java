/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.model;

import org.eclipse.swt.widgets.Listener;

import gov.sandia.cf.parts.constants.TableHeaderBarButtonType;

/**
 * The Header Parts stub class
 * 
 * @author Didier Verstraete
 *
 * @param <T> the entity class
 */
public class HeaderParts<T> {

	/**
	 * The name field
	 */
	protected String name;

	/**
	 * The value field
	 */
	protected String value;

	/**
	 * Btn listener
	 */
	private Listener btnListener;

	/**
	 * Btn listener
	 */
	private TableHeaderBarButtonType btnType;

	/**
	 * The data field
	 */
	protected T data;

	/**
	 * The constructor
	 */
	public HeaderParts() {
		this.btnType = TableHeaderBarButtonType.EDIT;
	}

	/**
	 * 
	 * The constructor
	 * 
	 * @param name  the name of the header
	 * @param value the value of the header
	 * @param data  the data of the header
	 */
	public HeaderParts(String name, String value, T data) {
		super();
		this.name = name;
		this.value = value;
		this.data = data;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * 
	 * Sets the data
	 * 
	 * @param data the data to set
	 */
	public void setData(T data) {
		this.data = data;
	}

	/**
	 * @return the btnListener
	 */
	public Listener getBtnListener() {
		return btnListener;
	}

	/**
	 * @param btnListener the btnListener to set
	 */
	public void setBtnListener(Listener btnListener) {
		this.btnListener = btnListener;
	}

	/**
	 * @return the btnType
	 */
	public TableHeaderBarButtonType getBtnType() {
		return btnType;
	}

	/**
	 * @param btnType the btnType to set
	 */
	public void setBtnType(TableHeaderBarButtonType btnType) {
		this.btnType = btnType;
	}

}
