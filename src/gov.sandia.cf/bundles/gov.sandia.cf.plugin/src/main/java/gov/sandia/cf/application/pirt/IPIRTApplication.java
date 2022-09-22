/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.pirt;

import java.util.List;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QoIHeader;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.PIRTQuery;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;

/**
 * Interface to manage PIRT Application methods
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IPIRTApplication extends IApplication {

	/**
	 * @param model the model to load
	 * @return the PIRT configuration loaded
	 */
	PIRTSpecification loadPIRTConfiguration(Model model);

	/**
	 * Get the background color
	 * 
	 * @param pirtConfiguration the pirt specification
	 * @param expectedLevel     : credibility level importance of the phenomenon
	 * @param currentLevel      : current level of the parameter
	 * @return cell color depending of the credibility level of the phenomenon
	 *         currentLevel compared to the expectedLevel
	 * @throws CredibilityException if an error occurs.
	 */
	String getBackgroundColor(PIRTSpecification pirtConfiguration, PIRTLevelImportance expectedLevel,
			PIRTLevelImportance currentLevel) throws CredibilityException;

	/**
	 * Reset qo I.
	 *
	 * @param qoi quantity of interest concerned
	 * 
	 *            reset pirt datas:
	 * 
	 *            - qoi is cleared but not deleted
	 * 
	 *            - qoiHeaders are cleared but not deleted
	 * 
	 *            - all phenomenon groups are deleted
	 * 
	 *            - all phenomenon are deleted
	 * 
	 *            - all criterion are deleted
	 * @param user the user
	 * @return the @param qoi resetted
	 * @throws CredibilityException if an error occurs while resetting qoi
	 */
	QuantityOfInterest resetQoI(QuantityOfInterest qoi, User user) throws CredibilityException;

	/**
	 * Makes a copy of @param qoi and marks the copied qoi as tagged. The @param
	 * tagDescription is used to set a description for the tag
	 * 
	 * @param qoi             the qoi to tag
	 * @param tagDescriptionn the tag description
	 * @param currentUser     the current user
	 * @return the tagged qoi
	 * @throws CredibilityException if an error occurs while tagging qoi
	 */
	QuantityOfInterest tagQoI(QuantityOfInterest qoi, String tagDescriptionn, User currentUser)
			throws CredibilityException;

	/**
	 * Makes a copy of @param qoi
	 * 
	 * @param qoi           the qoi to duplicate
	 * @param duplicatedQoi the qoi duplicated
	 * @param user          the duplicate action user
	 * @return the copied qoi
	 * @throws CredibilityException if an error occurs while duplicating qoi
	 */
	QuantityOfInterest duplicateQoI(QuantityOfInterest qoi, QuantityOfInterest duplicatedQoi, User user)
			throws CredibilityException;

	/**
	 * @param model the model used to find qoi associated (must not be null)
	 * @return the qoi associated to @param model
	 */
	List<QuantityOfInterest> getQoIList(Model model);

	/**
	 * @param model the model used to find qoi associated (must not be null)
	 * @return the qoi not tagged associated to @param model
	 */
	List<QuantityOfInterest> getRootQoI(Model model);

	/**
	 * Gets the qo I by model and parent.
	 *
	 * @param model  the model
	 * @param parent the parent
	 * @return the qo I by model and parent
	 */
	List<QuantityOfInterest> getQoIByModelAndParent(Model model, QuantityOfInterest parent);

	/**
	 * @param qoiId the qoi id to find qoi
	 * @return the qoi associated to @param qoiId
	 * @throws CredibilityException if an error occurs while retrieving qoi
	 */
	QuantityOfInterest getQoIById(Integer qoiId) throws CredibilityException;

	/**
	 * @param id     the id to do not search
	 * @param symbol the qoi symbol
	 * @return true if there is a qoi with the name symbol, otherwise false.
	 * @throws CredibilityException if a parameter is not valid.
	 */
	boolean existsQoISymbol(Integer[] id, String symbol) throws CredibilityException;

	/**
	 * @param qoi  the qoi to add
	 * @param user the creation user
	 * @return the new qoi created
	 * @throws CredibilityException if an error occurs while adding new qoi
	 */
	QuantityOfInterest addQoI(QuantityOfInterest qoi, User user) throws CredibilityException;

	/**
	 * Creates a new qoi and creates qoi headers associated with @param headers
	 * 
	 * @param qoi     the qoi to add
	 * @param user    the creation user
	 * @param headers description of headers to create and to associate to @param
	 *                qoi
	 * @return the new qoi created with qoi headers associated
	 * @throws CredibilityException if an error occurs while adding new qoi
	 */
	QuantityOfInterest addQoI(QuantityOfInterest qoi, User user, List<PIRTDescriptionHeader> headers)
			throws CredibilityException;

	/**
	 * @param qoi  the qoi to update
	 * @param user the update user
	 * @return the updated qoi
	 * @throws CredibilityException if an error occurs while updating qoi
	 */
	QuantityOfInterest updateQoI(QuantityOfInterest qoi, User user) throws CredibilityException;

	/**
	 * Deletes @param qoi from database.
	 *
	 * @param qoi  the qoi to delete
	 * @param user the user
	 * @throws CredibilityException if an error occurs while deleting qoi
	 */
	void deleteQoI(QuantityOfInterest qoi, User user) throws CredibilityException;

	/**
	 * @return the list of qoi headers
	 */
	List<QoIHeader> getQoIHeaders();

	/**
	 * @param qoiHeader the qoi header to add
	 * @param user      the creation user
	 * @return the new qoiheader created
	 * @throws CredibilityException if an error occurs while adding new qoi header
	 */
	QoIHeader addQoIHeader(QoIHeader qoiHeader, User user) throws CredibilityException;

	/**
	 * @param qoiHeader the qoi header to update
	 * @param user      the update user
	 * @return the updated qoi header
	 * @throws CredibilityException if an error occurs while updating qoi header
	 */
	QoIHeader updateQoIHeader(QoIHeader qoiHeader, User user) throws CredibilityException;

	/**
	 * Deletes @param qoiHeader from database
	 * 
	 * @param qoiHeader the qoi header to delete
	 * @throws CredibilityException if an error occurs while deleting qoi header
	 */
	void deleteQoIHeader(QoIHeader qoiHeader) throws CredibilityException;

	/**
	 * @return the list of phenomenon groups
	 */
	List<PhenomenonGroup> getPhenomenonGroups();

	/**
	 * @param groupToCreate the group to create
	 * 
	 * @return the new phenomenon group created
	 * @throws CredibilityException if an error occurs while adding new phenomenon
	 *                              group
	 */
	PhenomenonGroup addPhenomenonGroup(PhenomenonGroup groupToCreate) throws CredibilityException;

	/**
	 * @param group the phenomenon group to update
	 * @return updated phenomenon group
	 * @throws CredibilityException if an error occurs while updating phenomenon
	 *                              group
	 */
	PhenomenonGroup updatePhenomenonGroup(PhenomenonGroup group) throws CredibilityException;

	/**
	 * Deletes @param phenomenon group from database.
	 *
	 * @param group the group to delete
	 * @param user  the user
	 * @throws CredibilityException if an error occurs while deleting phenomenon
	 *                              group
	 */
	void deletePhenomenonGroup(PhenomenonGroup group, User user) throws CredibilityException;

	/**
	 * Change the phenomenon group order
	 * 
	 * @param groupToMove the phenomenon group to move
	 * @param newIndex    the id label index
	 * @throws CredibilityException if an error occurs
	 */
	void reorderPhenomenonGroups(PhenomenonGroup groupToMove, int newIndex) throws CredibilityException;

	/**
	 * @return the list of phenomena
	 */
	List<Phenomenon> getPhenomena();

	/**
	 * @param phenomenonToCreate the phenomenon to create
	 * 
	 * @return the new phenomenon created
	 * @throws CredibilityException if an error occurs while adding new phenomenon
	 */
	Phenomenon addPhenomenon(Phenomenon phenomenonToCreate) throws CredibilityException;

	/**
	 * @param phenomenon the phenomenon to update
	 * @return the updated phenomenon
	 * @throws CredibilityException if an error occurs while updating phenomenon
	 */
	Phenomenon updatePhenomenon(Phenomenon phenomenon) throws CredibilityException;

	/**
	 * Deletes @param phenomenon from database.
	 *
	 * @param phenomenon the phenomenon to delete
	 * @param user       the user
	 * @throws CredibilityException if an error occurs while deleting new phenomenon
	 */
	void deletePhenomenon(Phenomenon phenomenon, User user) throws CredibilityException;

	/**
	 * Change the phenomenon order
	 * 
	 * @param phenomenonToMove the phenomenon group to move
	 * @param newIndex         the id label index
	 * @throws CredibilityException if an error occurs
	 */
	void reorderPhenomena(Phenomenon phenomenonToMove, int newIndex) throws CredibilityException;

	/**
	 * @return the list of criterion
	 */
	List<Criterion> getCriterion();

	/**
	 * @param criterion the criterion to add
	 * @return the created Criterion
	 * @throws CredibilityException if an error occurs while adding new criterion
	 */
	Criterion addCriterion(Criterion criterion) throws CredibilityException;

	/**
	 * @param criterion the criterion to update
	 * @return the updated criterion
	 * @throws CredibilityException if an error occurs while updating criterion
	 */
	Criterion updateCriterion(Criterion criterion) throws CredibilityException;

	/**
	 * Deletes @param criterion from database.
	 *
	 * @param criterion the criterion to delete
	 * @param user      the user
	 * @throws CredibilityException if an error occurs while deleting criterion
	 */
	void deleteCriterion(Criterion criterion, User user) throws CredibilityException;

	/**
	 * @param query             the pirt query to execute
	 * @param criteriaInputList the criteria parameters list
	 * @return the executedQuery
	 * @throws CredibilityException if an error occurs while executing pirt query
	 */
	List<Object> executeQuery(PIRTQuery query, List<String> criteriaInputList) throws CredibilityException;

	/**
	 * @return the list of PIRT adequacy column guidelines
	 */
	List<PIRTAdequacyColumnGuideline> getPIRTAdequacyColumnGuideline();

	/**
	 * @param pirtAdequacyColumnGuideline the PIRT adequacy column guideline to add
	 * @return the created PIRTAdequacyColumnGuideline
	 * @throws CredibilityException if an error occurs
	 */
	PIRTAdequacyColumnGuideline addPIRTAdequacyColumnGuideline(PIRTAdequacyColumnGuideline pirtAdequacyColumnGuideline)
			throws CredibilityException;

	/**
	 * @param pirtAdequacyColumnGuideline the entity to update
	 * @return the updated entity
	 * @throws CredibilityException if an error occurs while updating
	 */
	PIRTAdequacyColumnGuideline updatePIRTAdequacyColumnGuideline(
			PIRTAdequacyColumnGuideline pirtAdequacyColumnGuideline) throws CredibilityException;

	/**
	 * Deletes all @param pirtAdequacyColumnGuideline from database
	 * 
	 * @param pirtGuidelines the list of entities to delete
	 * @throws CredibilityException if an error occurs while deleting
	 */
	void deleteAllPIRTAdequacyColumnGuideline(List<PIRTAdequacyColumnGuideline> pirtGuidelines)
			throws CredibilityException;

	/**
	 * Deletes @param pirtAdequacyColumnGuideline from database
	 * 
	 * @param pirtAdequacyColumnGuideline the entity to delete
	 * @throws CredibilityException if an error occurs while deleting
	 */
	void deletePIRTAdequacyColumnGuideline(PIRTAdequacyColumnGuideline pirtAdequacyColumnGuideline)
			throws CredibilityException;

	/**
	 * @return the list of PIRT adequacy columns
	 */
	List<PIRTAdequacyColumn> getPIRTAdequacyColumn();

	/**
	 * @param pirtAdequacyColumn the PIRT adequacy column to add
	 * @return the newly created entity
	 * @throws CredibilityException if an error occurs while adding
	 */
	PIRTAdequacyColumn addPIRTAdequacyColumn(PIRTAdequacyColumn pirtAdequacyColumn) throws CredibilityException;

	/**
	 * @param pirtAdequacyColumn the entity to update
	 * @return the updated entity
	 * @throws CredibilityException if an error occurs while updating
	 */
	PIRTAdequacyColumn updatePIRTAdequacyColumn(PIRTAdequacyColumn pirtAdequacyColumn) throws CredibilityException;

	/**
	 * Deletes all @param pirtAdequacyColumns from database
	 * 
	 * @param pirtAdequacyColumns the list of entities to delete
	 * @throws CredibilityException if an error occurs while deleting
	 */
	void deleteAllPIRTAdequacyColumn(List<PIRTAdequacyColumn> pirtAdequacyColumns) throws CredibilityException;

	/**
	 * Deletes @param pirtAdequacyColumn from database
	 * 
	 * @param pirtAdequacyColumn the entity to delete
	 * @throws CredibilityException if an error occurs while deleting
	 */
	void deletePIRTAdequacyColumn(PIRTAdequacyColumn pirtAdequacyColumn) throws CredibilityException;

	/**
	 * @return the list of PIRT adequacy column level guidelines
	 */
	List<PIRTAdequacyColumnLevelGuideline> getPIRTAdequacyColumnLevelGuideline();

	/**
	 * @param pirtAdequacyLevelGuideline the PIRT adequacy column level guideline to
	 *                                   add
	 * @return the newly created entity
	 * @throws CredibilityException if an error occurs while adding
	 */
	PIRTAdequacyColumnLevelGuideline addPIRTAdequacyColumnLevelGuideline(
			PIRTAdequacyColumnLevelGuideline pirtAdequacyLevelGuideline) throws CredibilityException;

	/**
	 * @param pirtAdequacyLevelGuideline the entity to update
	 * @return the updated entity
	 * @throws CredibilityException if an error occurs while updating
	 */
	PIRTAdequacyColumnLevelGuideline updatePIRTAdequacyColumnLevelGuideline(
			PIRTAdequacyColumnLevelGuideline pirtAdequacyLevelGuideline) throws CredibilityException;

	/**
	 * Deletes @param pirtAdequacyLevelGuideline from database
	 * 
	 * @param pirtAdequacyLevelGuideline the entity to delete
	 * @throws CredibilityException if an error occurs while deleting
	 */
	void deletePIRTAdequacyColumnLevelGuideline(PIRTAdequacyColumnLevelGuideline pirtAdequacyLevelGuideline)
			throws CredibilityException;

	/**
	 * @return the list of PIRT description headers
	 */
	List<PIRTDescriptionHeader> getPIRTDescriptionHeader();

	/**
	 * @param pirtDescHeader the PIRT description header to add
	 * @return the newly created entity
	 * @throws CredibilityException if an error occurs while adding
	 */
	PIRTDescriptionHeader addPIRTDescriptionHeader(PIRTDescriptionHeader pirtDescHeader) throws CredibilityException;

	/**
	 * @param pirtDescHeader the entity to update
	 * @return the updated entity
	 * @throws CredibilityException if an error occurs while updating
	 */
	PIRTDescriptionHeader updatePIRTDescriptionHeader(PIRTDescriptionHeader pirtDescHeader) throws CredibilityException;

	/**
	 * Deletes all pirt description Headers in parameter from database
	 * 
	 * @param pirtHeaders the list of entities to delete
	 * @throws CredibilityException if an error occurs while deleting
	 */
	void deleteAllPIRTDescriptionHeader(List<PIRTDescriptionHeader> pirtHeaders) throws CredibilityException;

	/**
	 * Deletes pirtDescHeader from database
	 * 
	 * @param pirtDescHeader the entity to delete
	 * @throws CredibilityException if an error occurs while deleting
	 */
	void deletePIRTDescriptionHeader(PIRTDescriptionHeader pirtDescHeader) throws CredibilityException;

	/**
	 * @return the list of PIRT level difference colors
	 */
	List<PIRTLevelDifferenceColor> getPIRTLevelDifferenceColor();

	/**
	 * @param pirtLevelDiffColor the PIRT level difference color to add
	 * @return the newly created entity
	 * @throws CredibilityException if an error occurs while adding
	 */
	PIRTLevelDifferenceColor addPIRTLevelDifferenceColor(PIRTLevelDifferenceColor pirtLevelDiffColor)
			throws CredibilityException;

	/**
	 * @param pirtLevelDiffColor the entity to update
	 * @return the updated entity
	 * @throws CredibilityException if an error occurs while updating
	 */
	PIRTLevelDifferenceColor updatePIRTLevelDifferenceColor(PIRTLevelDifferenceColor pirtLevelDiffColor)
			throws CredibilityException;

	/**
	 * Deletes all @param pirtLevelDiffColor from database
	 * 
	 * @param pirtColors the list of entities to delete
	 * @throws CredibilityException if an error occurs while deleting
	 */
	void deleteAllPIRTLevelDifferenceColor(List<PIRTLevelDifferenceColor> pirtColors) throws CredibilityException;

	/**
	 * Deletes @param pirtLevelDiffColor from database
	 * 
	 * @param pirtLevelDiffColor the entity to delete
	 * @throws CredibilityException if an error occurs while deleting
	 */
	void deletePIRTLevelDifferenceColor(PIRTLevelDifferenceColor pirtLevelDiffColor) throws CredibilityException;

	/**
	 * @return the list of PIRT level importance
	 */
	List<PIRTLevelImportance> getPIRTLevelImportance();

	/**
	 * @param pirtLevelImportance the PIRT level importance to add
	 * @return the newly created entity
	 * @throws CredibilityException if an error occurs while adding
	 */
	PIRTLevelImportance addPIRTLevelImportance(PIRTLevelImportance pirtLevelImportance) throws CredibilityException;

	/**
	 * @param pirtLevelImportance the entity to update
	 * @return the updated entity
	 * @throws CredibilityException if an error occurs while updating
	 */
	PIRTLevelImportance updatePIRTLevelImportance(PIRTLevelImportance pirtLevelImportance) throws CredibilityException;

	/**
	 * Deletes all @param pirtLevelImportance from database
	 * 
	 * @param pirtLevels the levels to delete
	 * @throws CredibilityException if an error occurs while deleting
	 */
	void deleteAllPIRTLevelImportance(List<PIRTLevelImportance> pirtLevels) throws CredibilityException;

	/**
	 * Deletes @param pirtLevelImportance from database
	 * 
	 * @param pirtLevelImportance the entity to delete
	 * @throws CredibilityException if an error occurs while deleting
	 */
	void deletePIRTLevelImportance(PIRTLevelImportance pirtLevelImportance) throws CredibilityException;

	/**
	 * @param spec1 the first specification to check
	 * @param spec2 the second specification to check
	 * @return true if spec1 content is the same as spec2 content
	 */
	boolean sameConfiguration(PIRTSpecification spec1, PIRTSpecification spec2);

	/**
	 * Check if PIRT is available
	 * 
	 * @return True if PIRT is available
	 */
	boolean isPIRTEnabled();

	/**
	 * Refresh the qoi
	 * 
	 * @param qoi the qoi to refresh
	 */
	void refresh(QuantityOfInterest qoi);

	/**
	 * Refresh the group
	 * 
	 * @param group the phenomena group to refresh
	 */
	void refresh(PhenomenonGroup group);

	/**
	 * Refresh the phenomenon
	 * 
	 * @param phenomenon the phenomenon to refresh
	 */
	void refresh(Phenomenon phenomenon);

	/**
	 * Reorder all.
	 *
	 * @param model the model
	 * @param user  the user
	 * @throws CredibilityException the credibility exception
	 */
	void reorderAllQuantityOfInterest(Model model, User user) throws CredibilityException;

	/**
	 * Reorder quantity of interest at same level.
	 *
	 * @param toMove the to move
	 * @param user   the user
	 * @throws CredibilityException the credibility exception
	 */
	void reorderQuantityOfInterestAtSameLevel(QuantityOfInterest toMove, User user) throws CredibilityException;

	/**
	 * Reorder quantity of interest.
	 *
	 * @param toMove   the to move
	 * @param newIndex the new index
	 * @param user     the user
	 * @throws CredibilityException the credibility exception
	 */
	void reorderQuantityOfInterest(QuantityOfInterest toMove, int newIndex, User user) throws CredibilityException;

	/**
	 * Reorder phenomena for group.
	 *
	 * @param toMove the to move
	 * @throws CredibilityException the credibility exception
	 */
	void reorderPhenomenaForGroup(PhenomenonGroup toMove) throws CredibilityException;

	/**
	 * Reorder groups for quantity of interest.
	 *
	 * @param qoi  the qoi
	 * @param user the user
	 * @throws CredibilityException the credibility exception
	 */
	void reorderGroupsForQuantityOfInterest(QuantityOfInterest qoi, User user) throws CredibilityException;
}
