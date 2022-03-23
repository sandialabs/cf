package gov.sandia.cf.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.sandia.cf.webapp.model.entity.IntendedPurpose;

@Repository
public interface IntendedPurposeRepository extends JpaRepository<IntendedPurpose, Long> {

	IntendedPurpose findFirstByOrderByIdAsc();

	@Query("SELECT i FROM IntendedPurpose i WHERE i.model.id = :modelId")
	IntendedPurpose findByModelId(@Param("modelId") Long modelId);

}
