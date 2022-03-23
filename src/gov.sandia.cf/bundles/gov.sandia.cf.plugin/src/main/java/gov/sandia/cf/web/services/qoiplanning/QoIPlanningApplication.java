/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.qoiplanning;

import java.util.List;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.qoiplanning.IQoIPlanningApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;
import gov.sandia.cf.model.QoIPlanningValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;

/**
 * Manage QoI Planning Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class QoIPlanningApplication extends AApplication implements IQoIPlanningApplication {

	/**
	 * QoIPlanningApplication constructor
	 */
	public QoIPlanningApplication() {
		super();
	}

	/**
	 * QoIPlanningApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public QoIPlanningApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public QoIPlanningSpecification loadQoIPlanningConfiguration(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public List<QoIPlanningParam> getParameterByModel(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public QoIPlanningValue createOrUpdateQoIPlanningValue(QoIPlanningValue value, User user)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteAllQoIPlanningValue(List<QoIPlanningValue> values) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteQoIPlanningValue(QoIPlanningValue value) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteAllQoIPlanningParam(List<QoIPlanningParam> params) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteQoIPlanningParam(QoIPlanningParam param) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteAllQoIPlanningSelectValue(List<QoIPlanningSelectValue> selectValues) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteQoIPlanningSelectValue(QoIPlanningSelectValue select) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteAllQoIPlanningConstraint(List<QoIPlanningConstraint> constraints) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteQoIPlanningConstraint(QoIPlanningConstraint select) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public boolean sameConfiguration(QoIPlanningSpecification spec1, QoIPlanningSpecification spec2) {
		// TODO to implement
		return false;
	}

	@Override
	public boolean isQoIPlanningEnabled(Model model) {
		// TODO to implement
		return false;
	}

}
