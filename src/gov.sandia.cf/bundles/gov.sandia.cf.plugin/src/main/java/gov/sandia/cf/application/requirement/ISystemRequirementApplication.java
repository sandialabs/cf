/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.requirement;

import java.util.List;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.SystemRequirementValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;

/**
 * Interface to manage System Requirements Application methods
 * 
 * @author Maxime N.
 *
 */
@Service
public interface ISystemRequirementApplication extends IApplication {

	/**
	 * Load the System Requirements specification
	 * 
	 * @param model the model
	 * @return the System Requirements specification
	 */
	SystemRequirementSpecification loadSysRequirementConfiguration(Model model);

	/**
	 * Get Requirement by id
	 * 
	 * @param id the requirement id
	 * @return The SystemRequirement found
	 */
	SystemRequirement getRequirementById(Integer id);

	/**
	 * Gets the requirement by statement.
	 *
	 * @param statement the statement
	 * @return the requirement by statement
	 */
	SystemRequirement getRequirementByStatement(String statement);

	/**
	 * Get System Requirements Parameters by Model
	 * 
	 * @param model The model
	 * @return The list of parameters for the model
	 */
	List<SystemRequirementParam> getParameterByModel(Model model);

	/**
	 * Gets the requirement by model and parent.
	 *
	 * @param model  the model
	 * @param parent the parent
	 * @return the requirement by model and parent
	 */
	List<SystemRequirement> getRequirementByModelAndParent(Model model, SystemRequirement parent);

	/**
	 * Update requirement
	 * 
	 * @param requirement the requirement to update
	 * @param userUpdate  the user that updated the requirement
	 * @return The requirement updated
	 * @throws CredibilityException if an error occurs during update
	 */
	public SystemRequirement updateRequirement(SystemRequirement requirement, User userUpdate)
			throws CredibilityException;

	/**
	 * Delete Requirement.
	 *
	 * @param requirement the requirement to delete
	 * @param user the user
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteRequirement(SystemRequirement requirement, User user) throws CredibilityException;

	/**
	 * Delete All Requirement values
	 * 
	 * @param values the values to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllRequirementValue(List<SystemRequirementValue> values) throws CredibilityException;

	/**
	 * Delete Requirement value
	 * 
	 * @param value the value to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteRequirementValue(SystemRequirementValue value) throws CredibilityException;

	/**
	 * Delete All Requirement Parameters
	 * 
	 * @param params the list of parameters to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllRequirementParam(List<SystemRequirementParam> params) throws CredibilityException;

	/**
	 * Delete Requirement Parameter
	 * 
	 * @param param the parameter to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteRequirementParam(SystemRequirementParam param) throws CredibilityException;

	/**
	 * Delete All Requirement Select Values
	 * 
	 * @param selectValues the select values to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllRequirementSelectValue(List<SystemRequirementSelectValue> selectValues) throws CredibilityException;

	/**
	 * Delete Requirement Select Value
	 * 
	 * @param select the select value to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteRequirementSelectValue(SystemRequirementSelectValue select) throws CredibilityException;

	/**
	 * Get requirement for model
	 * 
	 * @param model the model
	 * @return the list of system requirement roots
	 */
	List<SystemRequirement> getRequirementRootByModel(Model model);

	/**
	 * Get all requirements for model (flattened to one map)
	 * 
	 * @param model the model
	 * @return the list of system requirement associated to the model in a list
	 */
	List<SystemRequirement> getRequirementWithChildrenByModel(Model model);

	/**
	 * Create requirement
	 * 
	 * @param requirement  the system requirement to add
	 * @param model        the model to set
	 * @param userCreation the user that created the entity
	 * @return the newly created system requirement
	 * @throws CredibilityException if an error occurs during creation
	 */
	SystemRequirement addRequirement(SystemRequirement requirement, Model model, User userCreation)
			throws CredibilityException;

	/**
	 * Refresh requirement
	 * 
	 * @param requirement the requirement to refresh
	 */
	void refresh(SystemRequirement requirement);

	/**
	 * @param spec1 the first specification to check
	 * @param spec2 the second specification to check
	 * @return true if the system requirement configurations spec1 and spec2 are the
	 *         same
	 */
	boolean sameConfiguration(SystemRequirementSpecification spec1, SystemRequirementSpecification spec2);

	/**
	 * Check if requirement is enabled
	 * 
	 * @param model the model
	 * @return True if requirement is enabled
	 */
	boolean isRequirementEnabled(Model model);

	/**
	 * @param id        the array of id to except from the search
	 * @param statement the statement to check
	 * @return true if the statement already exists in the database
	 * @throws CredibilityException if an error occurs
	 */
	boolean existsRequirementStatement(Integer[] id, String statement) throws CredibilityException;

	/**
	 * Reorder all.
	 *
	 * @param model the model
	 * @param user  the user
	 * @throws CredibilityException the credibility exception
	 */
	void reorderAll(Model model, User user) throws CredibilityException;

	/**
	 * Reorder system requirement.
	 *
	 * @param systemRequirement the system requirement
	 * @param newIndex          the new index
	 * @param user              the user
	 * @throws CredibilityException the credibility exception
	 */
	void reorderSystemRequirement(SystemRequirement systemRequirement, int newIndex, User user)
			throws CredibilityException;

	/**
	 * Reorder system requirement at same level.
	 *
	 * @param toMove the to move
	 * @param user   the user
	 * @throws CredibilityException the credibility exception
	 */
	void reorderSystemRequirementAtSameLevel(SystemRequirement toMove, User user) throws CredibilityException;
}
