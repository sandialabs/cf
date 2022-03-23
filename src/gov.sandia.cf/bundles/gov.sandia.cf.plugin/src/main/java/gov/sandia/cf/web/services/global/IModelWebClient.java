/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.global;

import java.util.List;

import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.web.services.IWebClient;

/**
 * The Interface IModelWebClient.
 * 
 * @author Didier Verstraete
 */
@Service
public interface IModelWebClient extends IWebClient {

	/**
	 * Load model.
	 *
	 * @param modelId the model id
	 * @return the model
	 * @throws CredibilityException the credibility exception
	 */
	Model loadModel(Integer modelId) throws CredibilityException;

	/**
	 * List the existing models.
	 *
	 * @return the list
	 */
	List<Model> list();

	/**
	 * Creates the model.
	 *
	 * @param model the model
	 * @return the model
	 * @throws CredibilityException the credibility exception
	 */
	Model create(Model model) throws CredibilityException;

	/**
	 * Delete.
	 *
	 * @param modelId the model id
	 * @throws CredibilityException the credibility exception
	 */
	void delete(Integer modelId) throws CredibilityException;
}
