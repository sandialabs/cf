/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.qoiplanning;

import java.util.List;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;
import gov.sandia.cf.model.QoIPlanningValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;

/**
 * Interface to manage QoI Planning Application methods
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IQoIPlanningApplication extends IApplication {

	/**
	 * Load the QoI Planning specification
	 * 
	 * @param model the model
	 * @return the QoI Planning specification
	 */
	QoIPlanningSpecification loadQoIPlanningConfiguration(Model model);

	/**
	 * Get QoIPlanning Parameters by Model
	 * 
	 * @param model The model
	 * @return The list of parameters for the model
	 */
	List<QoIPlanningParam> getParameterByModel(Model model);

	/**
	 * @param value the qoi planning value to create or update
	 * @param user  the user to set
	 * @return the newly created or updated qoi planning value
	 * @throws CredibilityException if an error occurs during creation or update
	 */
	QoIPlanningValue createOrUpdateQoIPlanningValue(QoIPlanningValue value, User user) throws CredibilityException;

	/**
	 * Delete All QoIPlanning values
	 * 
	 * @param values the qoi planning values to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllQoIPlanningValue(List<QoIPlanningValue> values) throws CredibilityException;

	/**
	 * Delete QoIPlanning value
	 * 
	 * @param value the qoi planning value to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteQoIPlanningValue(QoIPlanningValue value) throws CredibilityException;

	/**
	 * Delete All QoIPlanning Parameter
	 * 
	 * @param params the list of parameters to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllQoIPlanningParam(List<QoIPlanningParam> params) throws CredibilityException;

	/**
	 * Delete QoIPlanning Parameter
	 * 
	 * @param param the parameter to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteQoIPlanningParam(QoIPlanningParam param) throws CredibilityException;

	/**
	 * Delete All QoIPlanning Select Values
	 * 
	 * @param selectValues the select values to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllQoIPlanningSelectValue(List<QoIPlanningSelectValue> selectValues) throws CredibilityException;

	/**
	 * Delete QoIPlanning Select Value
	 * 
	 * @param select the select value to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteQoIPlanningSelectValue(QoIPlanningSelectValue select) throws CredibilityException;

	/**
	 * Delete QoIPlanning Constraints
	 * 
	 * @param constraints the constraints to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllQoIPlanningConstraint(List<QoIPlanningConstraint> constraints) throws CredibilityException;

	/**
	 * Delete QoIPlanning Constraints
	 * 
	 * @param select the constraint to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteQoIPlanningConstraint(QoIPlanningConstraint select) throws CredibilityException;

	/**
	 * @param spec1 the first specification to check
	 * @param spec2 the second specification to check
	 * @return true if the system QoIPlanning configurations spec1 and spec2 are the
	 *         same
	 */
	boolean sameConfiguration(QoIPlanningSpecification spec1, QoIPlanningSpecification spec2);

	/**
	 * Check if QoIPlanning is enabled
	 * 
	 * @param model the model
	 * @return true if QoIPlanning is enabled
	 */
	boolean isQoIPlanningEnabled(Model model);

}
