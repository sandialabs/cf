/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class StatusApiController.
 * 
 * @author Didier Verstraete
 */
@RestController
@RequestMapping("api/status")
public class StatusApiController {

	Logger logger = LoggerFactory.getLogger(StatusApiController.class);

	/**
	 * Server status.
	 *
	 * @return the string
	 */
	@GetMapping
	public String status() {
		return ApiConstants.STATUS_PING_SUCCESS_VAR;
	}

}
