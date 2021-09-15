/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.util.List;

import gov.sandia.cf.application.configuration.uncertainty.UncertaintySpecification;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;

/**
 * Interface to manage Uncertainty Application methods
 * 
 * @author Maxime N.
 *
 */
public interface IUncertaintyApplication extends IApplication {

	/**
	 * Load the uncertainty specification
	 * 
	 * @param model the model
	 * @return the uncertainty specification
	 */
	UncertaintySpecification loadUncertaintyConfiguration(Model model);

	/**
	 * Find all Uncertainty group by model
	 * 
	 * @param model The model
	 * @return The list of UncertaintyGroup found for the Model
	 */
	List<UncertaintyGroup> getUncertaintyGroupByModel(Model model);

	/**
	 * Get Uncertainty Group by id
	 * 
	 * @param id the uncertainty id
	 * @return The UncertaintyGroup found
	 */
	UncertaintyGroup getUncertaintyGroupById(Integer id);

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
	 * Create uncertaintyGroup
	 * 
	 * @param uncertaintyGroup the uncertainty group
	 * @param model            the model to set
	 * @param userCreation     the user that created the uncertainty
	 * @return The uncertaintyGroup created
	 * @throws CredibilityException if an error occurs during creation
	 */
	public UncertaintyGroup addUncertaintyGroup(UncertaintyGroup uncertaintyGroup, Model model, User userCreation)
			throws CredibilityException;

	/**
	 * Create uncertainty
	 * 
	 * @param uncertainty  the uncertainty to create
	 * @param userCreation the user that created the uncertainty
	 * @return The uncertainty created
	 * @throws CredibilityException if an error occurs during creation
	 */
	public Uncertainty addUncertainty(Uncertainty uncertainty, User userCreation) throws CredibilityException;

	/**
	 * Update uncertaintyGroup
	 * 
	 * @param uncertaintyGroup the uncertainty group to update
	 * @return The uncertaintyGroup updated
	 * @throws CredibilityException if an error occurs during update
	 */
	public UncertaintyGroup updateUncertaintyGroup(UncertaintyGroup uncertaintyGroup) throws CredibilityException;

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
	 * Delete Uncertainty Group
	 * 
	 * @param uncertaintyGroup the uncertainty group to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteUncertaintyGroup(UncertaintyGroup uncertaintyGroup) throws CredibilityException;

	/**
	 * Delete Uncertainty
	 * 
	 * @param uncertainty the uncertainty to delete
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteUncertainty(Uncertainty uncertainty) throws CredibilityException;

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
	 * Refresh the uncertainty group
	 * 
	 * @param group the uncertainty group to refresh
	 */
	void refresh(UncertaintyGroup group);

	/**
	 * Refresh the uncertainty group
	 * 
	 * @param newUncertainty the uncertainty to refresh
	 */
	void refresh(Uncertainty newUncertainty);
}
