/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.sandia.cf.webapp.model.entity.Model;

/**
 * The Interface ModelRepository.
 * 
 * @author Didier Verstraete
 */
@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

}
