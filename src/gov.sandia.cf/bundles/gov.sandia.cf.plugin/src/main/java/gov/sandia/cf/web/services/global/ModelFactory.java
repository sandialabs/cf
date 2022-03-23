/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.global;

import gov.sandia.cf.model.Model;

/**
 * The Class Intended Purpose Route.
 * 
 * @author Didier Verstraete
 */
public class ModelFactory {

	private ModelFactory() {
		// Do not implement
	}

	/**
	 * Gets the model.
	 *
	 * @param id the id
	 * @return the model
	 */
	public static Model get(Integer id) {
		Model model = new Model();
		model.setId(id);
		return model;
	}

}
