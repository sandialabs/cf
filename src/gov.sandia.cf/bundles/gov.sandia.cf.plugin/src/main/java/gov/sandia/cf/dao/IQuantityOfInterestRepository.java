/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.List;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QuantityOfInterest;

/**
 * the QuantityOfInterest repository interface
 * 
 * @author Didier Verstraete
 * 
 */
@Repository
public interface IQuantityOfInterestRepository extends ICRUDRepository<QuantityOfInterest, Integer> {

	/**
	 * @param model the model associated with qoi to find (model id must be set)
	 * @return the QuantityOfInterest list associated to @param model
	 */
	List<QuantityOfInterest> findByModel(Model model);

	/**
	 * @param model the model associated with qoi to find (model id must be set)
	 * @return the QuantityOfInterest list associated to @param model not tagged
	 */
	List<QuantityOfInterest> findByModelNotTagged(Model model);

	/**
	 * @param modelId the id of the model associated for qoi to retrieve
	 * @return the QuantityOfInterest id list associated to @param modelId
	 */
	List<Integer> findQoiIdByModelId(Integer modelId);

	/**
	 * @param model the model associated with qoi to find (model id must be set)
	 * @return the QuantityOfInterest list associated to @param model
	 */
	List<QuantityOfInterest> findRootQuantityOfInterest(Model model);

}
