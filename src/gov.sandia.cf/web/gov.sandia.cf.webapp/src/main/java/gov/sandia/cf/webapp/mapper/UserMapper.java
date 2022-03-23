/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.mapper;

import org.mapstruct.Mapper;

import gov.sandia.cf.webapp.model.dto.UserDto;
import gov.sandia.cf.webapp.model.entity.User;

/**
 * The Interface UserMapper.
 * 
 * @author Didier Verstraete
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

	/**
	 * To entity.
	 *
	 * @param dto the dto
	 * @return the user
	 */
	User toEntity(UserDto dto);

	/**
	 * To dto.
	 *
	 * @param entity the entity
	 * @return the user dto
	 */
	UserDto toDto(User entity);
}
