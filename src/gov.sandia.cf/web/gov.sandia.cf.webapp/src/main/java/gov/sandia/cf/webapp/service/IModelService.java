/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.service;

import java.util.List;

import gov.sandia.cf.webapp.model.entity.Model;

/**
 * The Interface IModelService.
 * 
 * @author Didier Verstraete
 */
public interface IModelService {

	/**
	 * Gets the model.
	 *
	 * @param modelId the model id
	 * @return the model
	 */
	Model get(Long modelId);

	/**
	 * Creates the model.
	 *
	 * @param model the model to create
	 * @return the model
	 */
	Model create(Model model);

	/**
	 * Save the model.
	 *
	 * @param modelId the model id
	 * @param string  the string
	 * @param entity  the entity
	 */
	void save(Long modelId, String string, Model entity);

	/**
	 * List.
	 *
	 * @return the list
	 */
	List<Model> list();

	/**
	 * Delete.
	 *
	 * @param modelId the model id
	 */
	void delete(Long modelId);
}
