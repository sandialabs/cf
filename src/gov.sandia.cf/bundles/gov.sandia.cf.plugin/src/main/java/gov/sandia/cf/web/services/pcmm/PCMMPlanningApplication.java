/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.pcmm;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.pcmm.IPCMMPlanningApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningTableValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;

/**
 * Manage PCMM Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningApplication extends AApplication implements IPCMMPlanningApplication {

	/**
	 * The constructor
	 */
	public PCMMPlanningApplication() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public PCMMPlanningApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public boolean isPCMMPlanningEnabled() {
		// TODO to implement
		return false;
	}

	@Override
	public PCMMPlanningParam addPlanningParameter(PCMMPlanningParam planningParam) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public PCMMPlanningSelectValue addPCMMPlanningSelectValue(PCMMPlanningSelectValue value)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deletePlanningSelectValue(PCMMPlanningSelectValue value) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void addAllPCMMPlanningSelectValue(PCMMPlanningParam parameter, List<PCMMPlanningSelectValue> values)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<PCMMPlanningParam> getPlanningFieldsBy(Map<EntityFilter, Object> filters) {
		// TODO to implement
		return null;
	}

	@Override
	public PCMMPlanningParam updatePlanningParameter(PCMMPlanningParam planningParam) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void addAllPCMMPlanning(Model model, List<PCMMPlanningParam> planningFields,
			Map<IAssessable, List<PCMMPlanningQuestion>> planningQuestions) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void addAllPCMMPlanningParam(Model model, List<PCMMPlanningParam> planningFields)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteAllPlanningParameter(List<PCMMPlanningParam> planningParamList) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deletePlanningParameter(PCMMPlanningParam planningParam) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public PCMMPlanningQuestion addPlanningQuestion(PCMMPlanningQuestion planningQuestion) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public List<PCMMPlanningQuestion> getPlanningQuestionsByElement(PCMMElement element, PCMMMode mode) {
		// TODO to implement
		return null;
	}

	@Override
	public void addAllPCMMPlanningQuestion(Model model, List<PCMMPlanningQuestion> planningQuestions)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteAllPlanningQuestions(List<PCMMPlanningQuestion> planningQuestionList)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deletePlanningQuestion(PCMMPlanningQuestion question) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public PCMMPlanningValue addPlanningValue(PCMMPlanningValue planningValue) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public PCMMPlanningValue updatePlanningValue(PCMMPlanningValue planningValue, User userUpdate)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public List<PCMMPlanningValue> getPlanningValueBy(Map<EntityFilter, Object> filters) {
		// TODO to implement
		return null;
	}

	@Override
	public List<PCMMPlanningValue> getPlanningValueByElement(PCMMElement element, PCMMMode mode, Tag selectedTag) {
		// TODO to implement
		return null;
	}

	@Override
	public List<PCMMPlanningValue> getPlanningValueByElement(PCMMElement element, PCMMMode mode, List<Tag> tagList) {
		// TODO to implement
		return null;
	}

	@Override
	public void deletePlanningValue(PCMMPlanningValue value) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public PCMMPlanningQuestionValue addPlanningQuestionValue(PCMMPlanningQuestionValue questionValue)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public PCMMPlanningQuestionValue updatePlanningQuestionValue(PCMMPlanningQuestionValue questionValue,
			User userUpdate) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public List<PCMMPlanningQuestionValue> getPlanningQuestionValueBy(Map<EntityFilter, Object> filters) {
		// TODO to implement
		return null;
	}

	@Override
	public List<PCMMPlanningQuestionValue> getPlanningQuestionsValueByElement(PCMMElement element, PCMMMode mode,
			Tag selectedTag) {
		// TODO to implement
		return null;
	}

	@Override
	public List<PCMMPlanningQuestionValue> getPlanningQuestionsValueByElement(PCMMElement element, PCMMMode mode,
			List<Tag> tagList) {
		// TODO to implement
		return null;
	}

	@Override
	public void deletePlanningQuestionValue(PCMMPlanningQuestionValue value) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<PCMMPlanningTableItem> getPlanningTableItemBy(Map<EntityFilter, Object> filters) {
		// TODO to implement
		return null;
	}

	@Override
	public List<PCMMPlanningTableItem> getPlanningTableItemByElement(PCMMElement element, PCMMMode mode,
			Tag selectedTag) {
		// TODO to implement
		return null;
	}

	@Override
	public List<PCMMPlanningTableItem> getPlanningTableItemByElement(PCMMElement element, PCMMMode mode,
			List<Tag> tagList) {
		// TODO to implement
		return null;
	}

	@Override
	public PCMMPlanningTableItem addPlanningTableItem(PCMMPlanningTableItem item) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void refreshPlanningTableItem(PCMMPlanningTableItem item) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deletePlanningTableItem(PCMMPlanningTableItem item) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public PCMMPlanningTableValue addPlanningTableValue(PCMMPlanningTableValue value) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deletePlanningTableValue(PCMMPlanningTableValue value) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public PCMMPlanningTableValue updatePlanningTableValue(PCMMPlanningTableValue value, User userUpdate)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void tagCurrent(Tag newTag) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteTagged(Tag newTag) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public int computePlanningMaxProgress(PCMMElement element, PCMMMode mode) throws CredibilityException {
		// TODO to implement
		return 0;
	}

	@Override
	public int computePlanningProgress(PCMMElement element, Tag selectedTag, PCMMMode mode)
			throws CredibilityException {
		// TODO to implement
		return 0;
	}

	@Override
	public List<PCMMPlanningParam> flatListParamWithChildren(List<PCMMPlanningParam> paramList) {
		// TODO to implement
		return null;
	}

}
