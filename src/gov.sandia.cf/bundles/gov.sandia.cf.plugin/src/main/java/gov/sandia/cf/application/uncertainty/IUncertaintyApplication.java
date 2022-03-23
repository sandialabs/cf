/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.uncertainty;

import java.util.List;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyConstraint;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;

/**
 * Interface to manage Uncertainty Application methods
 * 
 * @author Maxime N.
 *
 */
@Service
public interface IUncertaintyApplication extends IApplication {

	/**
	 * Load the uncertainty specification
	 * 
	 * @param model the model
	 * @return the uncertainty specification
	 */
	UncertaintySpecification loadUncertaintyConfiguration(Model model);

	/**
	 * Find all Uncertainty groups by model
	 * 
	 * @param model The model
	 * @return The list of UncertaintyGroup found for the Model
	 */
	List<Uncertainty> getUncertaintyGroupByModel(Model model);

	/**
	 * Get Uncertainty Group by id
	 * 
	 * @param id the uncertainty id
	 * @return The UncertaintyGroup found
	 */
	Uncertainty getUncertaintyGroupById(Integer id);

	/**
	 * Gets the uncertainties by model.
	 *
	 * @param model the model
	 * @return the uncertainties by model
	 */
	List<Uncertainty> getUncertaintiesByModel(Model model);

	/**
	 * Get Uncertainty by id
	 * 
	 * @param id the uncertainty id
	 * @return The Uncertainty found
	 */
	Uncertainty getUncertaintyById(Integer id);

	/**
	 * Get Uncertainty Parameters by Model
	 * 
	 * @param model The model
	 * @return The list of Uncertainty parameters for the model
	 */
	List<UncertaintyParam> getUncertaintyParameterByModel(Model model);

	/**
	 * Gets the uncertainties by model and parent.
	 *
	 * @param model  the model
	 * @param parent the parent
	 * @return the uncertainties by model and parent
	 */
	List<Uncertainty> getUncertaintiesByModelAndParent(Model model, Uncertainty parent);

	/**
	 * Create uncertainty.
	 *
	 * @param uncertainty  the uncertainty to create
	 * @param model        the model
	 * @param userCreation the user that created the uncertainty
	 * @return The uncertainty created
	 * @throws CredibilityException if an error occurs during creation
	 */
	public Uncertainty addUncertainty(Uncertainty uncertainty, Model model, User userCreation)
			throws CredibilityException;

	/**
	 * Update uncertainty
	 * 
	 * @param uncertainty the uncertainty to update
	 * @param userUpdate  the user that updated the uncertainty
	 * @return The uncertainty updated
	 * @throws CredibilityException if an error occurs during update
	 */
	public Uncertainty updateUncertainty(Uncertainty uncertainty, User userUpdate) throws CredibilityException;

	/**
	 * Delete Uncertainty
	 * 
	 * @param uncertainty the uncertainty to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteUncertainty(Uncertainty uncertainty) throws CredibilityException;

	/**
	 * Delete all uncertainties.
	 *
	 * @param uncertaintyList the uncertainty list
	 * @throws CredibilityException the credibility exception
	 */
	public void deleteAllUncertainties(List<Uncertainty> uncertaintyList) throws CredibilityException;

	/**
	 * Delete All Uncertainty values
	 * 
	 * @param values the uncertainty values to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllUncertaintyValue(List<UncertaintyValue> values) throws CredibilityException;

	/**
	 * Delete Uncertainty value
	 * 
	 * @param value the value to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteUncertaintyValue(UncertaintyValue value) throws CredibilityException;

	/**
	 * Delete All Uncertainty Parameters
	 * 
	 * @param params the list of parameters to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllUncertaintyParam(List<UncertaintyParam> params) throws CredibilityException;

	/**
	 * Delete Uncertainty Parameter
	 * 
	 * @param param the parameter to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteUncertaintyParam(UncertaintyParam param) throws CredibilityException;

	/**
	 * Delete All Uncertainty Select Values
	 * 
	 * @param selectValues the select values to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteAllUncertaintySelectValue(List<UncertaintySelectValue> selectValues) throws CredibilityException;

	/**
	 * Delete Uncertainty Select Value
	 * 
	 * @param select the select value to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void deleteUncertaintySelectValue(UncertaintySelectValue select) throws CredibilityException;

	/**
	 * @param spec1 the first specification to check
	 * @param spec2 the second specification to check
	 * @return true if the uncertainty configurations spec1 and spec2 are the same
	 */
	boolean sameConfiguration(UncertaintySpecification spec1, UncertaintySpecification spec2);

	/**
	 * Check if Uncertainty is enabled
	 * 
	 * @param model the model
	 * @return true if the uncertainty module is enabled
	 */
	boolean isUncertaintyEnabled(Model model);

	/**
	 * Refresh the uncertainty
	 * 
	 * @param newUncertainty the uncertainty to refresh
	 */
	void refresh(Uncertainty newUncertainty);

	/**
	 * Reorder all.
	 *
	 * @param model the model
	 * @param user  the user
	 * @throws CredibilityException the credibility exception
	 */
	void reorderAll(Model model, User user) throws CredibilityException;

	/**
	 * Reorder uncertainty at same level.
	 *
	 * @param toMove the to move
	 * @param user   the user
	 * @throws CredibilityException the credibility exception
	 */
	void reorderUncertaintyAtSameLevel(Uncertainty toMove, User user) throws CredibilityException;

	/**
	 * Reorder uncertainty.
	 *
	 * @param toMove   the to move
	 * @param newIndex the new index
	 * @param user     the user
	 * @throws CredibilityException the credibility exception
	 */
	void reorderUncertainty(Uncertainty toMove, int newIndex, User user) throws CredibilityException;

	/**
	 * Delete all uncertainty constraint.
	 *
	 * @param constraints the constraints
	 * @throws CredibilityException the credibility exception
	 */
	void deleteAllUncertaintyConstraint(List<UncertaintyConstraint> constraints) throws CredibilityException;

	/**
	 * Delete uncertainty constraint.
	 *
	 * @param constraint the constraint
	 * @throws CredibilityException the credibility exception
	 */
	void deleteUncertaintyConstraint(UncertaintyConstraint constraint) throws CredibilityException;

}
