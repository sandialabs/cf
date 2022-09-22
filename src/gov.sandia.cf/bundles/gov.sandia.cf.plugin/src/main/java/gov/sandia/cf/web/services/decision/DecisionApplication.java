/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.decision;

import java.util.List;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.IApplicationManager;
import gov.sandia.cf.application.decision.IDecisionApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.DecisionValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;

/**
 * Manage Decision Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionApplication extends AApplication implements IDecisionApplication {

	/**
	 * DecisionApplication constructor
	 */
	public DecisionApplication() {
		super();
	}

	/**
	 * DecisionApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public DecisionApplication(IApplicationManager appMgr) {
		super(appMgr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DecisionSpecification loadDecisionConfiguration(Model model) {
		return null;
	}

	@Override
	public Decision getDecisionById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DecisionParam> getParameterByModel(Model model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Decision> getDecisionRootByModel(Model model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Decision addDecision(Decision decision, Model model, User userCreation) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Decision updateDecision(Decision decision, User userUpdate) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteDecision(Decision decision, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllDecisionValue(List<DecisionValue> values) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteDecisionValue(DecisionValue value) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllDecisionParam(List<DecisionParam> params) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteDecisionParam(DecisionParam param) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllDecisionSelectValue(List<DecisionSelectValue> selectValues) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteDecisionSelectValue(DecisionSelectValue select) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllDecisionConstraint(List<DecisionConstraint> constraints) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteDecisionConstraint(DecisionConstraint select) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Decision decision) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean sameConfiguration(DecisionSpecification spec1, DecisionSpecification spec2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDecisionEnabled(Model model) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsDecisionTitle(Integer[] id, String title) throws CredibilityException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Decision> getDecisionByModelAndParent(Model model, Decision parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reorderAll(Model model, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reorderDecisionAtSameLevel(Decision toMove, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reorderDecision(Decision decision, int newIndex, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}

}
