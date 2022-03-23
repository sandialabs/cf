/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.intendedpurpose;

import gov.sandia.cf.model.dto.IntendedPurposeDto;

/**
 * The Class IntendedPurposeMapper.
 * 
 * @author Didier Verstraete
 * @deprecated to replace with mapstruct
 */
@Deprecated
public class IntendedPurposeMapper {

	/**
	 * To app object.
	 *
	 * @param web the web object
	 * @return the gov.sandia.cf.model. intended purpose
	 */
	public static gov.sandia.cf.model.IntendedPurpose toApp(IntendedPurposeDto web) {
		if (web == null) {
			return null;
		}

		gov.sandia.cf.model.IntendedPurpose app = new gov.sandia.cf.model.IntendedPurpose();

		app.setId(web.getId());
		app.setDescription(web.getDescription());
		app.setReference(web.getReference());

		return app;
	}

	/**
	 * To web object.
	 *
	 * @param app the app object
	 * @return the intended purpose dto
	 */
	public static IntendedPurposeDto toWeb(gov.sandia.cf.model.IntendedPurpose app) {
		if (app == null) {
			return null;
		}

		IntendedPurposeDto web = new IntendedPurposeDto();

		web.setId(app.getId());
		web.setDescription(app.getDescription());
		web.setReference(app.getReference());

		return web;
	}
}
