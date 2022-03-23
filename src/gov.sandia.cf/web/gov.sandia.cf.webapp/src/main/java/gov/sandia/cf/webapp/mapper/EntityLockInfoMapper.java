/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.mapper;

import org.mapstruct.Mapper;

import gov.sandia.cf.webapp.model.stub.EntityLock;
import gov.sandia.cf.webapp.model.stub.EntityLockInfo;

/**
 * The Interface EntityLockInfoMapper.
 * 
 * @author Didier Verstraete
 */
@Mapper(componentModel = "spring")
public interface EntityLockInfoMapper {

	/**
	 * To info.
	 *
	 * @param lock the lock
	 * @return the entity lock info
	 */
	EntityLockInfo toInfo(EntityLock lock);
}
