/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.uncertainty;

import java.util.List;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.uncertainty.IUncertaintyApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyConstraint;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;

/**
 * Manage Uncertainty Application methods
 * 
 * @author Didier Verstraete
 */
public class UncertaintyApplication extends AApplication implements IUncertaintyApplication {

	/**
	 * UncertaintyApplication constructor
	 */
	public UncertaintyApplication() {
		super();
	}

	/**
	 * UncertaintyApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public UncertaintyApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public UncertaintySpecification loadUncertaintyConfiguration(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public List<Uncertainty> getUncertaintyGroupByModel(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public Uncertainty getUncertaintyGroupById(Integer id) {
		// TODO to implement
		return null;
	}

	@Override
	public List<Uncertainty> getUncertaintiesByModel(Model model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Uncertainty> getUncertaintiesByModelAndParent(Model model, Uncertainty parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uncertainty getUncertaintyById(Integer id) {
		// TODO to implement
		return null;
	}

	@Override
	public List<UncertaintyParam> getUncertaintyParameterByModel(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public Uncertainty addUncertainty(Uncertainty uncertainty, Model model, User userCreation)
			throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uncertainty updateUncertainty(Uncertainty uncertainty, User userUpdate) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteAllUncertainties(List<Uncertainty> uncertaintyList) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteUncertainty(Uncertainty uncertainty) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteAllUncertaintyValue(List<UncertaintyValue> values) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteUncertaintyValue(UncertaintyValue value) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteAllUncertaintyParam(List<UncertaintyParam> params) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteUncertaintyParam(UncertaintyParam param) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteAllUncertaintySelectValue(List<UncertaintySelectValue> selectValues) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteUncertaintySelectValue(UncertaintySelectValue select) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public boolean sameConfiguration(UncertaintySpecification spec1, UncertaintySpecification spec2) {
		// TODO to implement
		return false;
	}

	@Override
	public boolean isUncertaintyEnabled(Model model) {
		// TODO to implement
		return false;
	}

	@Override
	public void refresh(Uncertainty newUncertainty) {
		// TODO to implement

	}

	@Override
	public void reorderAll(Model model, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reorderUncertaintyAtSameLevel(Uncertainty toMove, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reorderUncertainty(Uncertainty toMove, int newIndex, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllUncertaintyConstraint(List<UncertaintyConstraint> constraints) throws CredibilityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteUncertaintyConstraint(UncertaintyConstraint constraint) throws CredibilityException {
		// TODO Auto-generated method stub
		
	}
}
