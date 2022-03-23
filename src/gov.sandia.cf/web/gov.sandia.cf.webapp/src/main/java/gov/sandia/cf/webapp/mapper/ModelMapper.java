/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import gov.sandia.cf.webapp.model.dto.ModelDto;
import gov.sandia.cf.webapp.model.entity.Model;

/**
 * The Interface ModelMapper.
 * 
 * @author Didier Verstraete
 */
@Mapper(componentModel = "spring")
public interface ModelMapper {

	/**
	 * To entity.
	 *
	 * @param dto the dto
	 * @return the model
	 */
	Model toEntity(ModelDto dto);

	/**
	 * To entity.
	 *
	 * @param dto the dto
	 * @return the list
	 */
	List<Model> toEntity(List<ModelDto> dto);

	/**
	 * To dto.
	 *
	 * @param entity the entity
	 * @return the model dto
	 */
	ModelDto toDto(Model entity);

	/**
	 * To dto.
	 *
	 * @param entity the entity
	 * @return the list
	 */
	List<ModelDto> toDto(List<Model> entity);
}
