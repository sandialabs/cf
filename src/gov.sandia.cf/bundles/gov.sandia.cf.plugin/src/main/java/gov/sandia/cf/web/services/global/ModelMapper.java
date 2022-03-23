/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.global;

import gov.sandia.cf.model.dto.ModelDto;

/**
 * The Class ModelMapper.
 * 
 * @author Didier Verstraete
 * @deprecated to replace with mapstruct
 */
@Deprecated
public class ModelMapper {

	/**
	 * To app object.
	 *
	 * @param web the web object
	 * @return the gov.sandia.cf.model. intended purpose
	 */
	public static gov.sandia.cf.model.Model toApp(ModelDto web) {
		if (web == null) {
			return null;
		}

		gov.sandia.cf.model.Model app = new gov.sandia.cf.model.Model();

		app.setId(web.getId());
		app.setApplication(web.getApplication());
		app.setContact(web.getContact());

		return app;
	}

	/**
	 * To web object.
	 *
	 * @param app the app object
	 * @return the intended purpose dto
	 */
	public static ModelDto toWeb(gov.sandia.cf.model.Model app) {
		if (app == null) {
			return null;
		}

		ModelDto web = new ModelDto();

		web.setId(app.getId());
		web.setApplication(app.getApplication());
		web.setContact(app.getContact());

		return web;
	}
}
