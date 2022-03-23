/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.pcmm;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.pcmm.IPCMMAggregateApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAggregation;
import gov.sandia.cf.model.PCMMAggregationLevel;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.query.EntityFilter;

/**
 * Manage PCMM Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAggregateApp extends AApplication implements IPCMMAggregateApp {
	/**
	 * The constructor
	 */
	public PCMMAggregateApp() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public PCMMAggregateApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public boolean isCompleteAggregation(Model model, Tag tag) throws CredibilityException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCompleteAggregationSimplified(Model model, Tag tag) throws CredibilityException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateSubelements(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateSubelements(PCMMSpecification configuration,
			Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> mapAggregationBySubelement)
			throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggregateAssessments(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateAssessmentSimplified(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggregateAssessments(PCMMSpecification configuration,
			PCMMElement element, Map<EntityFilter, Object> filters) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends IAssessable> PCMMAggregation<T> aggregateAssessments(PCMMSpecification configuration, T item,
			List<PCMMAssessment> assessmentList) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMAggregationLevel getClosestLevelForCode(PCMMSpecification configuration, List<PCMMLevel> levels,
			int code) {
		// TODO Auto-generated method stub
		return null;
	}
}
