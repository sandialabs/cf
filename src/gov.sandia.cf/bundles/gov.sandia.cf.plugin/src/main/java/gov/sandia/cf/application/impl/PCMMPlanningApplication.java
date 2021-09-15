/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.application.IPCMMPlanningApplication;
import gov.sandia.cf.dao.IPCMMPlanningParamRepository;
import gov.sandia.cf.dao.IPCMMPlanningQuestionRepository;
import gov.sandia.cf.dao.IPCMMPlanningQuestionValueRepository;
import gov.sandia.cf.dao.IPCMMPlanningSelectValueRepository;
import gov.sandia.cf.dao.IPCMMPlanningTableItemRepository;
import gov.sandia.cf.dao.IPCMMPlanningTableValueRepository;
import gov.sandia.cf.dao.IPCMMPlanningValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.GenericValueTaggable;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.IGenericTableValue;
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
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage PCMM Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMPlanningApplication extends AApplication implements IPCMMPlanningApplication {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMPlanningApplication.class);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPCMMPlanningEnabled() {
		// Initialize
		boolean isEnabled = true;

		// Has Parameters
		List<PCMMPlanningParam> parameters = getDaoManager().getRepository(IPCMMPlanningParamRepository.class)
				.findAll();
		List<PCMMPlanningQuestion> questions = getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class)
				.findAll();
		isEnabled &= (parameters != null && !parameters.isEmpty()) || (questions != null && !questions.isEmpty());

		// The result
		return isEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningParam> flatListParamWithChildren(List<PCMMPlanningParam> paramList) {
		List<PCMMPlanningParam> toReturn = new ArrayList<>();

		if (paramList != null && !paramList.isEmpty()) {
			for (PCMMPlanningParam param : paramList) {
				toReturn.add(param);
				if (param.getChildren() != null && !param.getChildren().isEmpty()) {
					toReturn.addAll(flatListParamWithChildren(param.getChildren().stream()
							.map(PCMMPlanningParam.class::cast).collect(Collectors.toList())));
				}
			}
		}

		return toReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningParam addPlanningParameter(PCMMPlanningParam planningParam) throws CredibilityException {
		if (planningParam == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDPARAM_NULL));
		}

		logger.debug("Add planning parameter"); //$NON-NLS-1$

		List<GenericParameter<PCMMPlanningParam>> children = planningParam.getChildren();
		List<GenericParameterSelectValue<PCMMPlanningParam>> parameterListValues = new ArrayList<>();
		if (planningParam.getParameterValueList() != null) {
			parameterListValues.addAll(planningParam.getParameterValueList());
		}

		// add pcmm planning param
		planningParam.setParameterValueList(null);
		planningParam.setChildren(null);
		PCMMPlanningParam newPlanningParam = getAppMgr().getDaoManager()
				.getRepository(IPCMMPlanningParamRepository.class).create(planningParam);

		// add generic parameter values
		if (!parameterListValues.isEmpty()) {

			logger.debug("Add planning parameter select values"); //$NON-NLS-1$

			addAllPCMMPlanningSelectValue(newPlanningParam,
					parameterListValues.stream().map(PCMMPlanningSelectValue.class::cast).collect(Collectors.toList()));
			getAppMgr().getDaoManager().getRepository(IPCMMPlanningParamRepository.class).refresh(newPlanningParam);
		}

		// add pcmm planning children recursively
		if (newPlanningParam != null && children != null && !children.isEmpty()) {

			logger.debug("Add planning parameter children"); //$NON-NLS-1$

			for (PCMMPlanningParam param : children.stream().filter(Objects::nonNull)
					.filter(PCMMPlanningParam.class::isInstance).map(PCMMPlanningParam.class::cast)
					.collect(Collectors.toList())) {
				param.setParent(newPlanningParam);
				param.setModel(newPlanningParam.getModel());
				addPlanningParameter(param);
			}
			getAppMgr().getDaoManager().getRepository(IPCMMPlanningParamRepository.class).refresh(newPlanningParam);
		}
		return newPlanningParam;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAllPCMMPlanningSelectValue(PCMMPlanningParam parameter, List<PCMMPlanningSelectValue> values)
			throws CredibilityException {
		if (parameter == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_GLB_GENPARAMVALUELIST_IMPORT_PARAMETERNULL));
		}
		if (values != null) {
			for (PCMMPlanningSelectValue selectValue : values.stream().filter(Objects::nonNull)
					.collect(Collectors.toList())) {
				// set element attributes
				selectValue.setParameter(parameter);
				addPCMMPlanningSelectValue(selectValue);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningSelectValue addPCMMPlanningSelectValue(PCMMPlanningSelectValue value)
			throws CredibilityException {
		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_GLB_GENPARAMVALUELIST_ADD_NULL));
		}
		return getAppMgr().getDaoManager().getRepository(IPCMMPlanningSelectValueRepository.class).create(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePlanningSelectValue(PCMMPlanningSelectValue value) throws CredibilityException {
		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_GLB_GENPARAMVALUELIST_DELETE_NULL));
		} else if (value.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_GLB_GENPARAMVALUELIST_DELETE_IDNULL));
		}

		getAppMgr().getDaoManager().getRepository(IPCMMPlanningSelectValueRepository.class).delete(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningParam updatePlanningParameter(PCMMPlanningParam planningParam) throws CredibilityException {

		if (planningParam == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEPARAM_NULL));
		} else if (planningParam.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEPARAM_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMPlanningParamRepository.class).update(planningParam);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAllPCMMPlanning(Model model, List<PCMMPlanningParam> planningFields,
			Map<IAssessable, List<PCMMPlanningQuestion>> planningQuestions) throws CredibilityException {

		List<PCMMPlanningQuestion> questionList = new ArrayList<>();

		// flat planning questions map to list
		if (planningQuestions != null) {
			questionList = planningQuestions.values().stream().flatMap(List::stream).collect(Collectors.toList());
		}

		// import planning fields
		addAllPCMMPlanningParam(model, planningFields);

		// import planning questions
		addAllPCMMPlanningQuestion(model, questionList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAllPCMMPlanningParam(Model model, List<PCMMPlanningParam> planningFields)
			throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_IMPORTCONF_MODELNULL));
		}

		// import Planning fields
		if (planningFields != null) {
			logger.info("Importing PCMM Planning fields into database..."); //$NON-NLS-1$

			for (PCMMPlanningParam param : planningFields) {
				param.setModel(model);
				addPlanningParameter(param);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllPlanningParameter(List<PCMMPlanningParam> planningParamList) throws CredibilityException {

		if (planningParamList != null) {
			for (PCMMPlanningParam planningParam : planningParamList) {
				deletePlanningParameter(planningParam);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePlanningParameter(PCMMPlanningParam planningParam) throws CredibilityException {

		if (planningParam == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEPARAM_NULL));
		} else if (planningParam.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEPARAM_IDNULL));
		}

		// refresh entity
		getDaoManager().getRepository(IPCMMPlanningParamRepository.class).refresh(planningParam);

		// filters
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericValue.Filter.PARAMETER, planningParam);

		// delete values
		List<PCMMPlanningValue> values = getPlanningValueBy(filters);
		if (values != null) {
			for (PCMMPlanningValue value : values) {
				deletePlanningValue(value);
			}
		}

		// delete table items associated
		List<PCMMPlanningTableItem> items = getPlanningTableItemBy(filters);
		if (items != null) {
			for (PCMMPlanningTableItem item : items) {
				deletePlanningTableItem(item);
			}
		}

		// delete select values
		List<GenericParameterSelectValue<PCMMPlanningParam>> parameterValueList = planningParam.getParameterValueList();
		if (parameterValueList != null) {
			for (GenericParameterSelectValue<?> selectValue : parameterValueList.stream()
					.filter(PCMMPlanningSelectValue.class::isInstance).collect(Collectors.toList())) {
				deletePlanningSelectValue((PCMMPlanningSelectValue) selectValue);
			}
		}

		// delete children
		if (planningParam.getChildren() != null && !planningParam.getChildren().isEmpty()) {
			deleteAllPlanningParameter(planningParam.getChildren().stream().map(PCMMPlanningParam.class::cast)
					.collect(Collectors.toList()));
		}

		// delete parameter
		getDaoManager().getRepository(IPCMMPlanningParamRepository.class).delete(planningParam);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningParam> getPlanningFieldsBy(Map<EntityFilter, Object> filters) {
		return getAppMgr().getDaoManager().getRepository(IPCMMPlanningParamRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningQuestion addPlanningQuestion(PCMMPlanningQuestion planningQuestion) throws CredibilityException {
		if (planningQuestion == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDQUESTION_NULL));
		}
		return getAppMgr().getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class)
				.create(planningQuestion);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningQuestion> getPlanningQuestionsByElement(PCMMElement element, PCMMMode mode) {
		if (PCMMMode.SIMPLIFIED.equals(mode)) {
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(PCMMPlanningQuestion.Filter.ELEMENT, element);
			return getAppMgr().getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class).findBy(filters);
		} else if (PCMMMode.DEFAULT.equals(mode)) {
			return getAppMgr().getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class)
					.findByElementInSubelement(element);
		}
		return new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAllPCMMPlanningQuestion(Model model, List<PCMMPlanningQuestion> planningQuestions)
			throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_IMPORTCONF_MODELNULL));
		}

		// import Planning questions
		if (planningQuestions != null) {
			logger.info("Importing PCMM Planning questions into database..."); //$NON-NLS-1$

			for (PCMMPlanningQuestion question : planningQuestions) {

				question.setModel(model);
				populatePCMMPlanningQuestionAssessable(question);

				boolean hasAssessable = question.getElement() != null || question.getSubelement() != null;
				if (hasAssessable) {
					addPlanningQuestion(question);
				} else {
					logger.warn("Question {} not imported, pcmm element or subelement not found", question); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Populate the question with the assessable from the database. If not present,
	 * set to null.
	 * 
	 * @param question the planning question to populate
	 */
	private void populatePCMMPlanningQuestionAssessable(PCMMPlanningQuestion question) {

		if (question != null) {

			// set PCMM element if needed
			if (question.getElement() != null && question.getElement().getId() == null) {
				PCMMElement elementFromKey = getAppMgr().getService(IPCMMApplication.class)
						.getElementFromKey(question.getElement().getAbbreviation());
				question.setElement(elementFromKey);
			}

			// set PCMM subelement if needed
			if (question.getSubelement() != null && question.getSubelement().getId() == null) {
				PCMMSubelement subelementFromKey = getAppMgr().getService(IPCMMApplication.class)
						.getSubelementFromKey(question.getSubelement().getCode());
				question.setSubelement(subelementFromKey);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllPlanningQuestions(List<PCMMPlanningQuestion> planningQuestionList)
			throws CredibilityException {

		if (planningQuestionList != null) {
			for (PCMMPlanningQuestion question : planningQuestionList) {
				deletePlanningQuestion(question);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePlanningQuestion(PCMMPlanningQuestion question) throws CredibilityException {

		if (question == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEQUESTION_NULL));
		} else if (question.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEQUESTION_IDNULL));
		}

		// delete values
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericValue.Filter.PARAMETER, question);
		List<PCMMPlanningQuestionValue> values = getPlanningQuestionValueBy(filters);
		if (values != null) {
			for (PCMMPlanningQuestionValue value : values) {
				deletePlanningQuestionValue(value);
			}
		}

		// delete question
		getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class).delete(question);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningValue addPlanningValue(PCMMPlanningValue planningValue) throws CredibilityException {
		if (planningValue == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDVALUE_NULL));
		}

		return getAppMgr().getDaoManager().getRepository(IPCMMPlanningValueRepository.class).create(planningValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningValue updatePlanningValue(PCMMPlanningValue planningValue, User userUpdate)
			throws CredibilityException {

		if (planningValue == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEVALUE_NULL));
		} else if (planningValue.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEVALUE_IDNULL));
		}

		// set date update
		planningValue.setDateUpdate(DateTools.getCurrentDate());

		return getDaoManager().getRepository(IPCMMPlanningValueRepository.class).update(planningValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningValue> getPlanningValueBy(Map<EntityFilter, Object> filters) {
		return getAppMgr().getDaoManager().getRepository(IPCMMPlanningValueRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningValue> getPlanningValueByElement(PCMMElement element, PCMMMode mode, Tag selectedTag) {
		if (PCMMMode.SIMPLIFIED.equals(mode)) {
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(PCMMPlanningQuestion.Filter.ELEMENT, element);
			filters.put(GenericValueTaggable.Filter.TAG, selectedTag);
			return getAppMgr().getDaoManager().getRepository(IPCMMPlanningValueRepository.class).findBy(filters);
		} else if (PCMMMode.DEFAULT.equals(mode)) {
			return getAppMgr().getDaoManager().getRepository(IPCMMPlanningValueRepository.class)
					.findByElementInSubelement(element, selectedTag);
		}
		return new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningValue> getPlanningValueByElement(PCMMElement element, PCMMMode mode, List<Tag> tagList) {

		List<PCMMPlanningValue> toReturn = new ArrayList<>();
		if (tagList != null) {
			for (Tag tag : tagList) {
				toReturn.addAll(getPlanningValueByElement(element, mode, tag));
			}
		}

		return toReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePlanningValue(PCMMPlanningValue value) throws CredibilityException {

		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEVALUE_NULL));
		} else if (value.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEVALUE_IDNULL));
		}

		// delete value
		getDaoManager().getRepository(IPCMMPlanningValueRepository.class).delete(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningQuestionValue addPlanningQuestionValue(PCMMPlanningQuestionValue questionValue)
			throws CredibilityException {
		if (questionValue == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDQUESTIONVALUE_NULL));
		}

		return getAppMgr().getDaoManager().getRepository(IPCMMPlanningQuestionValueRepository.class)
				.create(questionValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningQuestionValue updatePlanningQuestionValue(PCMMPlanningQuestionValue questionValue,
			User userUpdate) throws CredibilityException {

		if (questionValue == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEQUESTIONVALUE_NULL));
		} else if (questionValue.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATEQUESTIONVALUE_IDNULL));
		}

		// set date update
		questionValue.setDateUpdate(DateTools.getCurrentDate());

		return getDaoManager().getRepository(IPCMMPlanningQuestionValueRepository.class).update(questionValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningQuestionValue> getPlanningQuestionsValueByElement(PCMMElement element, PCMMMode mode,
			Tag selectedTag) {
		if (PCMMMode.SIMPLIFIED.equals(mode)) {
			return getAppMgr().getDaoManager().getRepository(IPCMMPlanningQuestionValueRepository.class)
					.findByElement(element, selectedTag);
		} else if (PCMMMode.DEFAULT.equals(mode)) {
			return getAppMgr().getDaoManager().getRepository(IPCMMPlanningQuestionValueRepository.class)
					.findByElementInSubelement(element, selectedTag);
		}
		return new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningQuestionValue> getPlanningQuestionsValueByElement(PCMMElement element, PCMMMode mode,
			List<Tag> tagList) {

		List<PCMMPlanningQuestionValue> toReturn = new ArrayList<>();
		if (tagList != null) {
			for (Tag tag : tagList) {
				toReturn.addAll(getPlanningQuestionsValueByElement(element, mode, tag));
			}
		}

		return toReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningQuestionValue> getPlanningQuestionValueBy(Map<EntityFilter, Object> filters) {
		return getAppMgr().getDaoManager().getRepository(IPCMMPlanningQuestionValueRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePlanningQuestionValue(PCMMPlanningQuestionValue value) throws CredibilityException {

		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEQUESTIONVALUE_NULL));
		} else if (value.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETEQUESTIONVALUE_IDNULL));
		}

		// delete value
		getDaoManager().getRepository(IPCMMPlanningQuestionValueRepository.class).delete(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningTableItem> getPlanningTableItemBy(Map<EntityFilter, Object> filters) {
		return getAppMgr().getDaoManager().getRepository(IPCMMPlanningTableItemRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningTableItem> getPlanningTableItemByElement(PCMMElement element, PCMMMode mode,
			Tag selectedTag) {
		if (PCMMMode.SIMPLIFIED.equals(mode)) {
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(PCMMPlanningQuestion.Filter.ELEMENT, element);
			filters.put(GenericValueTaggable.Filter.TAG, selectedTag);
			return getAppMgr().getDaoManager().getRepository(IPCMMPlanningTableItemRepository.class).findBy(filters);
		} else if (PCMMMode.DEFAULT.equals(mode)) {
			return getAppMgr().getDaoManager().getRepository(IPCMMPlanningTableItemRepository.class)
					.findByElementInSubelement(element, selectedTag);
		}
		return new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPlanningTableItem> getPlanningTableItemByElement(PCMMElement element, PCMMMode mode,
			List<Tag> tagList) {

		List<PCMMPlanningTableItem> toReturn = new ArrayList<>();
		if (tagList != null) {
			for (Tag tag : tagList) {
				toReturn.addAll(getPlanningTableItemByElement(element, mode, tag));
			}
		}

		return toReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningTableItem addPlanningTableItem(PCMMPlanningTableItem item) throws CredibilityException {
		if (item == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDTABLEITEM_NULL));
		}

		return getAppMgr().getDaoManager().getRepository(IPCMMPlanningTableItemRepository.class).create(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshPlanningTableItem(PCMMPlanningTableItem item) throws CredibilityException {
		if (item == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_REFRESHTABLEITEM_NULL));
		}

		getAppMgr().getDaoManager().getRepository(IPCMMPlanningTableItemRepository.class).refresh(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePlanningTableItem(PCMMPlanningTableItem item) throws CredibilityException {
		if (item == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETETABLEITEM_NULL));
		}

		// refresh
		getAppMgr().getDaoManager().getRepository(IPCMMPlanningTableItemRepository.class).refresh(item);

		// delete planning table values
		for (IGenericTableValue tableValue : item.getValueList().stream()
				.filter(PCMMPlanningTableValue.class::isInstance).collect(Collectors.toList())) {
			deletePlanningTableValue((PCMMPlanningTableValue) tableValue);
		}

		getAppMgr().getDaoManager().getRepository(IPCMMPlanningTableItemRepository.class).delete(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningTableValue addPlanningTableValue(PCMMPlanningTableValue value) throws CredibilityException {
		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_ADDTABLEVALUE_NULL));
		}

		return getAppMgr().getDaoManager().getRepository(IPCMMPlanningTableValueRepository.class).create(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePlanningTableValue(PCMMPlanningTableValue value) throws CredibilityException {
		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETETABLEVALUE_NULL));
		} else if (value.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_DELETETABLEVALUE_IDNULL));
		}

		getAppMgr().getDaoManager().getRepository(IPCMMPlanningTableValueRepository.class).delete(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMPlanningTableValue updatePlanningTableValue(PCMMPlanningTableValue value, User userUpdate)
			throws CredibilityException {

		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATETABLEVALUE_NULL));
		} else if (value.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_UPDATETABLEVALUE_IDNULL));
		}

		// set date update
		value.setDateUpdate(DateTools.getCurrentDate());

		return getDaoManager().getRepository(IPCMMPlanningTableValueRepository.class).update(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tagCurrent(Tag newTag) throws CredibilityException {

		if (newTag == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_TAG_NULL));
		} else if (newTag.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_TAG_IDNULL));
		}

		// copy and tag active Planning Questions
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericValueTaggable.Filter.TAG, null);

		for (PCMMPlanningQuestionValue answer : getPlanningQuestionValueBy(filters)) {
			PCMMPlanningQuestionValue copy = answer.copy();
			copy.setTag(newTag);
			addPlanningQuestionValue(copy);
		}

		// copy and tag active Planning Parameters
		for (PCMMPlanningValue value : getPlanningValueBy(filters)) {
			PCMMPlanningValue copy = value.copy();
			copy.setTag(newTag);
			addPlanningValue(copy);
		}

		// copy and tag active Planning Table Items and Table Values
		for (PCMMPlanningTableItem item : getPlanningTableItemBy(filters)) {
			PCMMPlanningTableItem copy = item.copy();
			copy.setTag(newTag);
			copy = addPlanningTableItem(copy);

			// create a copy of each table value for an item
			for (PCMMPlanningTableValue value : item.getValueList().stream()
					.filter(PCMMPlanningTableValue.class::isInstance).map(PCMMPlanningTableValue.class::cast)
					.collect(Collectors.toList())) {
				PCMMPlanningTableValue valueCopy = value.copy();
				valueCopy.setItem(copy);
				addPlanningTableValue(valueCopy);
			}

			// refresh PCMM planning table item
			getDaoManager().getRepository(IPCMMPlanningTableItemRepository.class).refresh(copy);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteTagged(Tag tagToDelete) throws CredibilityException {

		if (tagToDelete == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_TAG_NULL));
		} else if (tagToDelete.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_TAG_IDNULL));
		}

		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericValueTaggable.Filter.TAG, tagToDelete);

		// delete Planning Questions associated to the tag to delete
		for (PCMMPlanningQuestionValue answer : getPlanningQuestionValueBy(filters)) {
			getDaoManager().getRepository(IPCMMPlanningQuestionValueRepository.class).delete(answer);
		}

		// delete Planning Parameters associated to the tag to delete
		for (PCMMPlanningValue value : getPlanningValueBy(filters)) {
			getDaoManager().getRepository(IPCMMPlanningValueRepository.class).delete(value);
		}

		// delete Planning Table items and values associated to the tag to delete
		for (PCMMPlanningTableItem item : getPlanningTableItemBy(filters)) {

			// delete Planning Table items associated to the tag to delete. The planning
			// table values associated to the item will be deleted automatically because of
			// cascade CascadeType.REMOVE
			deletePlanningTableItem(item);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int computePlanningMaxProgress(PCMMElement element, PCMMMode mode) throws CredibilityException {

		int max = 0;

		if (element != null) {

			// get planning questions
			List<PCMMPlanningQuestion> planningQuestions = getPlanningQuestionsByElement(element, mode);
			int nbQuestions = planningQuestions != null ? planningQuestions.size() : 0;

			// get planning fields
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(GenericParameter.Filter.MODEL, element.getModel());
			List<PCMMPlanningParam> planningFields = getPlanningFieldsBy(filters);
			if (planningFields != null) {
				planningFields = planningFields.stream().filter(param -> param.getParent() == null)
						.collect(Collectors.toList());

			}
			int nbPlanningFields = planningFields != null ? planningFields.size() : 0;

			if (PCMMMode.DEFAULT.equals(mode)) {

				// add planning questions
				max += nbQuestions;

				// add planning fields
				int nbSubelements = element.getSubElementList() != null ? element.getSubElementList().size() : 0;
				max += nbSubelements * nbPlanningFields;

			} else if (PCMMMode.SIMPLIFIED.equals(mode)) {

				// add planning questions
				max += nbQuestions;

				// add planning fields
				max += nbPlanningFields;
			}
		}

		return max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int computePlanningProgress(PCMMElement element, Tag selectedTag, PCMMMode mode)
			throws CredibilityException {

		int cpt = 0;

		if (element != null) {

			// get planning questions
			List<PCMMPlanningQuestionValue> questionValues = getPlanningQuestionsValueByElement(element, mode,
					selectedTag);
			int nbAnswers = questionValues != null ? questionValues.size() : 0;

			// get planning fields
			List<PCMMPlanningValue> planningFieldValues = getPlanningValueByElement(element, mode, selectedTag);
			int nbPlanningFields = planningFieldValues != null ? planningFieldValues.size() : 0;

			// get planning table items
			List<PCMMPlanningTableItem> planningTableItem = getPlanningTableItemByElement(element, mode, selectedTag);
			int nbTableFilled = 0;

			if (planningTableItem != null) {
				// (+1 if there is at least one item in the table)
				// the hashset will delete duplicated parameters
				Set<PCMMPlanningParam> tableParameters = new HashSet<>();
				planningTableItem.forEach(item -> tableParameters.add(item.getParameter()));
				nbTableFilled = tableParameters.size();
			}

			// add planning questions
			cpt += nbAnswers;

			// add planning fields
			cpt += nbPlanningFields;

			// add planning table fields
			cpt += nbTableFilled;
		}

		return cpt;
	}
}
