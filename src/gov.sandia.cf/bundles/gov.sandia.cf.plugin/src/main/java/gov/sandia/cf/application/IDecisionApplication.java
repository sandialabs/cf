/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.util.List;

import gov.sandia.cf.application.configuration.decision.DecisionSpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.DecisionValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;

/**
 * Interface to manage System Decisions Application methods
 * 
 * @author Didier Verstraete
 *
 */
public interface IDecisionApplication extends IApplication {

	/**
	 * Load the Decisions specification
	 * 
	 * @param model the model
	 * @return the Decisions specification
	 */
	DecisionSpecification loadDecisionConfiguration(Model model);

	/**
	 * Get Decision by id
	 * 
	 * @param id the decision id
	 * @return The Decision found
	 */
	Decision getDecisionById(Integer id);

	/**
	 * Get Decisions Parameters by Model
	 * 
	 * @param model The model
	 * @return The list of parameters for the model
	 */
	List<DecisionParam> getParameterByModel(Model model);

	/**
	 * Get decision for model
	 * 
	 * @param model the model associated
	 * @return the root (top level) decision list
	 */
	List<Decision> getDecisionRootByModel(Model model);

	/**
	 * Create Decision
	 * 
	 * @param decision     the decision to add
	 * @param model        the model to set
	 * @param userCreation the user that created the entity
	 * @return the newly created decision
	 * @throws CredibilityException if an error occurs during creation
	 */
	Decision addDecision(Decision decision, Model model, User userCreation) throws CredibilityException;

	/**
	 * Update Decision
	 * 
	 * @param decision   the decision to update
	 * @param userUpdate the user that updated the decision
	 * @return The decision updated
	 * @throws CredibilityException if an error occurs during update
	 */
	public Decision updateDecision(Decision decision, User userUpdate) throws CredibilityException;

	/**
	 * Delete Decision
	 * 
	 * @param decision the decision to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteDecision(Decision decision) throws CredibilityException;

	/**
	 * Delete All Decision values
	 * 
	 * @param values the decision values to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllDecisionValue(List<DecisionValue> values) throws CredibilityException;

	/**
	 * Delete Decision value
	 * 
	 * @param value the decision value to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteDecisionValue(DecisionValue value) throws CredibilityException;

	/**
	 * Delete Decision Parameter
	 * 
	 * @param params the list of parameters to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllDecisionParam(List<DecisionParam> params) throws CredibilityException;

	/**
	 * Delete Decision Parameter
	 * 
	 * @param param the parameter to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteDecisionParam(DecisionParam param) throws CredibilityException;

	/**
	 * Delete All Decision Select Values
	 * 
	 * @param selectValues the select values to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllDecisionSelectValue(List<DecisionSelectValue> selectValues) throws CredibilityException;

	/**
	 * Delete Decision Select Value
	 * 
	 * @param select the select value to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteDecisionSelectValue(DecisionSelectValue select) throws CredibilityException;

	/**
	 * Delete All Decision Constraints
	 * 
	 * @param constraints the constraints to delete
	 * @throws CredibilityException if an error occurs during decision constraint
	 *                              deletion
	 */
	void deleteAllDecisionConstraint(List<DecisionConstraint> constraints) throws CredibilityException;

	/**
	 * Delete Decision Constraints
	 * 
	 * @param select the constraint to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteDecisionConstraint(DecisionConstraint select) throws CredibilityException;

	/**
	 * Refresh decision
	 * 
	 * @param decision the decision to refresh
	 */
	void refresh(Decision decision);

	/**
	 * @param spec1 the first specification to compare
	 * @param spec2 the second specification to compare
	 * @return true if the system decision configurations spec1 and spec2 are the
	 *         same
	 */
	boolean sameConfiguration(DecisionSpecification spec1, DecisionSpecification spec2);

	/**
	 * Check if decision is enabled
	 * 
	 * @param model the model
	 * @return True if decision is enabled
	 */
	boolean isDecisionEnabled(Model model);

	/**
	 * @param id    the id to do not search
	 * @param title the decision title
	 * @return true if there is a decision with the same title, otherwise false.
	 * @throws CredibilityException if a parameter is not valid.
	 */
	boolean existsDecisionTitle(Integer[] id, String title) throws CredibilityException;
}
