/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.model.PhenomenonGroup;

/**
 * the PhenomenonGroup repository interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IPhenomenonGroupRepository extends ICRUDRepository<PhenomenonGroup, Integer> {

	/**
	 * @param qoiId
	 *            the id of the qoi to retrieve
	 * @return the PhenomenonGroup list associated to @param qoiId
	 */
	List<PhenomenonGroup> findByQoiId(Integer qoiId);

	/**
	 * @param qoiIdList
	 *            the id list of the qoi to retrieve
	 * @return the PhenomenonGroup list associated to @param qoiIdList list
	 */
	List<PhenomenonGroup> findByQoiIdList(List<Integer> qoiIdList);

}
