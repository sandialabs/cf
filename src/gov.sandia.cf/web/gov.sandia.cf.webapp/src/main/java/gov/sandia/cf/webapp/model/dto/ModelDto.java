/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Model dto
 * 
 * @author Didier Verstraete
 */
@Data
@NoArgsConstructor
public class ModelDto {
	private Integer id;
	private String application;
	private String contact;
	private String versionOrigin;
	private String version;

}