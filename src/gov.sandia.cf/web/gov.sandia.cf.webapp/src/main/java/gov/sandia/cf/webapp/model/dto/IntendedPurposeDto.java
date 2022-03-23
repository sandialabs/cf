/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class IntendedPurposeDto.
 * 
 * @author Didier Verstraete
 */
@Data
@NoArgsConstructor
public class IntendedPurposeDto {

	private Long id;
	private String description;
	private String reference;
	private ModelDto model;
	private UserDto userCreation;
	private UserDto userUpdate;
	private LocalDateTime dateCreation;
	private LocalDateTime dateUpdate;
}