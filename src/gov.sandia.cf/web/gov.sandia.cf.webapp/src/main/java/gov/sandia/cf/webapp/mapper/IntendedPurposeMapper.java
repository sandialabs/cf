/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.mapper;

import org.mapstruct.Mapper;

import gov.sandia.cf.webapp.model.dto.IntendedPurposeDto;
import gov.sandia.cf.webapp.model.entity.IntendedPurpose;

/**
 * The Interface IntendedPurposeMapper.
 * 
 * @author Didier Verstraete
 */
@Mapper(componentModel = "spring")
public interface IntendedPurposeMapper {

	/**
	 * To entity.
	 *
	 * @param dto the dto
	 * @return the intended purpose
	 */
	IntendedPurpose toEntity(IntendedPurposeDto dto);

	/**
	 * To dto.
	 *
	 * @param entity the entity
	 * @return the intended purpose dto
	 */
	IntendedPurposeDto toDto(IntendedPurpose entity);
}
