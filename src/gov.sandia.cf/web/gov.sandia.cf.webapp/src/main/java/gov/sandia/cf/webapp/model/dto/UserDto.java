/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class UserDto.
 * 
 * @author Didier Verstraete
 */
@Data
@NoArgsConstructor
public class UserDto {
	private Long id;
	private String userID;
	private String name;
	private String firstName;
}
