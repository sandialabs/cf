/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.global;

import gov.sandia.cf.model.dto.ModelDto;

/**
 * The Class Intended Purpose Route.
 * 
 * @author Didier Verstraete
 */
public class ModelDtoFactory {

	private ModelDtoFactory() {
		// Do not implement
	}

	/**
	 * Gets the model.
	 *
	 * @param application the application
	 * @param contact     the contact
	 * @return the model
	 */
	public static ModelDto get(String application, String contact) {
		ModelDto model = new ModelDto();
		model.setApplication(application);
		model.setContact(contact);
		return model;
	}
}
