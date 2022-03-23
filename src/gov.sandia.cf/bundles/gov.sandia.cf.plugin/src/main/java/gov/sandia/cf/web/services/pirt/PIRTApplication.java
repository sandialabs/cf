/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.pirt;

import java.util.List;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.pirt.IPIRTApplication;
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
 * Manage PIRT Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTApplication extends AApplication implements IPIRTApplication {

	/**
	 * PIRTApplication constructor
	 */
	public PIRTApplication() {
		super();
	}

	/**
	 * PIRTApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public PIRTApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public PIRTSpecification loadPIRTConfiguration(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public String getBackgroundColor(PIRTSpecification pirtConfiguration, PIRTLevelImportance expectedLevel,
			PIRTLevelImportance currentLevel) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public QuantityOfInterest resetQoI(QuantityOfInterest qoi) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public QuantityOfInterest tagQoI(QuantityOfInterest qoi, String tagDescriptionn, User currentUser)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public QuantityOfInterest duplicateQoI(QuantityOfInterest qoi, QuantityOfInterest duplicatedQoi, User user)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public List<QuantityOfInterest> getQoIList(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public List<QuantityOfInterest> getRootQoI(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public QuantityOfInterest getQoIById(Integer qoiId) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public boolean existsQoISymbol(Integer[] id, String symbol) throws CredibilityException {
		// TODO to implement
		return false;
	}

	@Override
	public QuantityOfInterest addQoI(QuantityOfInterest qoi, User user) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public QuantityOfInterest addQoI(QuantityOfInterest qoi, User user, List<PIRTDescriptionHeader> headers)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public QuantityOfInterest updateQoI(QuantityOfInterest qoi, User user) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteQoI(QuantityOfInterest qoi) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<QoIHeader> getQoIHeaders() {
		// TODO to implement
		return null;
	}

	@Override
	public QoIHeader addQoIHeader(QoIHeader qoiHeader, User user) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public QoIHeader updateQoIHeader(QoIHeader qoiHeader, User user) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteQoIHeader(QoIHeader qoiHeader) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<PhenomenonGroup> getPhenomenonGroups() {
		// TODO to implement
		return null;
	}

	@Override
	public PhenomenonGroup addPhenomenonGroup(PhenomenonGroup groupToCreate) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public PhenomenonGroup updatePhenomenonGroup(PhenomenonGroup group) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deletePhenomenonGroup(PhenomenonGroup group) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void reorderPhenomenonGroups(PhenomenonGroup groupToMove, int newIndex) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<Phenomenon> getPhenomena() {
		// TODO to implement
		return null;
	}

	@Override
	public Phenomenon addPhenomenon(Phenomenon phenomenonToCreate) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public Phenomenon updatePhenomenon(Phenomenon phenomenon) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deletePhenomenon(Phenomenon phenomenon) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void reorderPhenomena(Phenomenon phenomenonToMove, int newIndex) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<Criterion> getCriterion() {
		// TODO to implement
		return null;
	}

	@Override
	public Criterion addCriterion(Criterion criterion) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public Criterion updateCriterion(Criterion criterion) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteCriterion(Criterion criterion) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<Object> executeQuery(PIRTQuery query, List<String> criteriaInputList) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public List<PIRTAdequacyColumnGuideline> getPIRTAdequacyColumnGuideline() {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTAdequacyColumnGuideline addPIRTAdequacyColumnGuideline(
			PIRTAdequacyColumnGuideline pirtAdequacyColumnGuideline) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTAdequacyColumnGuideline updatePIRTAdequacyColumnGuideline(
			PIRTAdequacyColumnGuideline pirtAdequacyColumnGuideline) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteAllPIRTAdequacyColumnGuideline(List<PIRTAdequacyColumnGuideline> pirtGuidelines)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deletePIRTAdequacyColumnGuideline(PIRTAdequacyColumnGuideline pirtAdequacyColumnGuideline)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<PIRTAdequacyColumn> getPIRTAdequacyColumn() {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTAdequacyColumn addPIRTAdequacyColumn(PIRTAdequacyColumn pirtAdequacyColumn) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTAdequacyColumn updatePIRTAdequacyColumn(PIRTAdequacyColumn pirtAdequacyColumn)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteAllPIRTAdequacyColumn(List<PIRTAdequacyColumn> pirtAdequacyColumns) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deletePIRTAdequacyColumn(PIRTAdequacyColumn pirtAdequacyColumn) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<PIRTAdequacyColumnLevelGuideline> getPIRTAdequacyColumnLevelGuideline() {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTAdequacyColumnLevelGuideline addPIRTAdequacyColumnLevelGuideline(
			PIRTAdequacyColumnLevelGuideline pirtAdequacyLevelGuideline) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTAdequacyColumnLevelGuideline updatePIRTAdequacyColumnLevelGuideline(
			PIRTAdequacyColumnLevelGuideline pirtAdequacyLevelGuideline) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deletePIRTAdequacyColumnLevelGuideline(PIRTAdequacyColumnLevelGuideline pirtAdequacyLevelGuideline)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<PIRTDescriptionHeader> getPIRTDescriptionHeader() {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTDescriptionHeader addPIRTDescriptionHeader(PIRTDescriptionHeader pirtDescHeader)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTDescriptionHeader updatePIRTDescriptionHeader(PIRTDescriptionHeader pirtDescHeader)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteAllPIRTDescriptionHeader(List<PIRTDescriptionHeader> pirtHeaders) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deletePIRTDescriptionHeader(PIRTDescriptionHeader pirtDescHeader) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<PIRTLevelDifferenceColor> getPIRTLevelDifferenceColor() {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTLevelDifferenceColor addPIRTLevelDifferenceColor(PIRTLevelDifferenceColor pirtLevelDiffColor)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTLevelDifferenceColor updatePIRTLevelDifferenceColor(PIRTLevelDifferenceColor pirtLevelDiffColor)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteAllPIRTLevelDifferenceColor(List<PIRTLevelDifferenceColor> pirtColors)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deletePIRTLevelDifferenceColor(PIRTLevelDifferenceColor pirtLevelDiffColor)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<PIRTLevelImportance> getPIRTLevelImportance() {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTLevelImportance addPIRTLevelImportance(PIRTLevelImportance pirtLevelImportance)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public PIRTLevelImportance updatePIRTLevelImportance(PIRTLevelImportance pirtLevelImportance)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteAllPIRTLevelImportance(List<PIRTLevelImportance> pirtLevels) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deletePIRTLevelImportance(PIRTLevelImportance pirtLevelImportance) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public boolean sameConfiguration(PIRTSpecification spec1, PIRTSpecification spec2) {
		// TODO to implement
		return false;
	}

	@Override
	public boolean isPIRTEnabled() {
		// TODO to implement
		return false;
	}

	@Override
	public void refresh(QuantityOfInterest qoi) {
		// TODO to implement

	}

	@Override
	public void refresh(PhenomenonGroup group) {
		// TODO to implement

	}

	@Override
	public void refresh(Phenomenon phenomenon) {
		// TODO to implement

	}

	@Override
	public List<QuantityOfInterest> getQoIByModelAndParent(Model model, QuantityOfInterest parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reorderAllQuantityOfInterest(Model model, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reorderQuantityOfInterestAtSameLevel(QuantityOfInterest toMove, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reorderQuantityOfInterest(QuantityOfInterest toMove, int newIndex, User user)
			throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reorderPhenomenaForGroup(PhenomenonGroup toMove) throws CredibilityException {
		// TODO Auto-generated method stub
		
	}
}
