/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.model;

import gov.sandia.cf.model.QoIHeader;
import gov.sandia.cf.model.QuantityOfInterest;

/**
 * The QoI Header Parts class used to display QoI Headers in the phenomena views
 * 
 * @author Didier Verstraete
 *
 */
public class QoIHeaderParts extends HeaderParts<QuantityOfInterest> {

	/**
	 * The qoi header
	 */
	private QoIHeader qoiHeader;

	/**
	 * The constructor
	 */
	public QoIHeaderParts() {
		super();
	}

	/**
	 * 
	 * The constructor
	 * 
	 * @param name  the name of the header
	 * @param value the value of the header
	 * @param qoi   the qoi associated
	 */
	public QoIHeaderParts(String name, String value, QuantityOfInterest qoi) {
		super(name, value, qoi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return qoiHeader == null ? this.name : qoiHeader.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		if (isFixed()) {
			this.name = name;
		} else {
			qoiHeader.setName(name);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue() {
		return qoiHeader == null ? this.value : qoiHeader.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(String value) {
		if (isFixed()) {
			this.value = value;
		} else {
			qoiHeader.setValue(value);
		}
	}

	/**
	 * @return the qoi
	 */
	public QuantityOfInterest getQoi() {
		return super.getData();
	}

	/**
	 * 
	 * Sets the qoi
	 * 
	 * @param qoi the qoi to set
	 */
	public void setQoi(QuantityOfInterest qoi) {
		super.setData(qoi);
	}

	/**
	 * @return true if the qoi is fixed otherwise false
	 */
	public boolean isFixed() {
		return qoiHeader == null;
	}

	/**
	 * @return the qoi header
	 */
	public QoIHeader getQoiHeader() {
		return qoiHeader;
	}

	/**
	 * Sets the qoi header
	 * 
	 * @param qoiHeader the qoi header to set
	 */
	public void setQoiHeader(QoIHeader qoiHeader) {
		this.qoiHeader = qoiHeader;
	}
}
