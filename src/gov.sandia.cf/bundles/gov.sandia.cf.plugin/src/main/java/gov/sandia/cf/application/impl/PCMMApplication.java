/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IImportApplication;
import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.application.IPCMMPlanningApplication;
import gov.sandia.cf.application.IReportARGExecutionApp;
import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.dao.IPCMMAssessmentRepository;
import gov.sandia.cf.dao.IPCMMElementRepository;
import gov.sandia.cf.dao.IPCMMEvidenceRepository;
import gov.sandia.cf.dao.IPCMMLevelColorRepository;
import gov.sandia.cf.dao.IPCMMLevelDescRepository;
import gov.sandia.cf.dao.IPCMMLevelRepository;
import gov.sandia.cf.dao.IPCMMOptionRepository;
import gov.sandia.cf.dao.IPCMMPlanningQuestionRepository;
import gov.sandia.cf.dao.IPCMMSubelementRepository;
import gov.sandia.cf.dao.IRoleRepository;
import gov.sandia.cf.dao.ITagRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.model.PCMMAggregation;
import gov.sandia.cf.model.PCMMAggregationLevel;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.NetTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * Manage PCMM Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMApplication extends AApplication implements IPCMMApplication {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMApplication.class);

	/**
	 * PCMM Progress default weight
	 */
	/** PCMM PROGRESS PLANNING DEFAULT WEIGHT */
	public static final int PCMM_PROGRESS_PLANNING_DEFAULT_WEIGHT = 20;
	/** PCMM PROGRESS EVIDENCE DEFAULT WEIGHT */
	public static final int PCMM_PROGRESS_EVIDENCE_DEFAULT_WEIGHT = 60;
	/** PCMM PROGRESS ASSESS DEFAULT WEIGHT */
	public static final int PCMM_PROGRESS_ASSESS_DEFAULT_WEIGHT = 20;

	/**
	 * The constructor
	 */
	public PCMMApplication() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public PCMMApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMSpecification loadPCMMConfiguration(Model model) throws CredibilityException {
		// Initialize
		PCMMSpecification pcmmSpecification = null;

		// Check PCMM is available
		if (isPCMMEnabled(model)) {
			pcmmSpecification = new PCMMSpecification();

			// set elements
			pcmmSpecification.setElements(getElementList(model));

			// set level colors
			List<PCMMLevelColor> colors = getDaoManager().getRepository(IPCMMLevelColorRepository.class).findAll();
			if (colors != null) {
				pcmmSpecification
						.setLevelColors(colors.stream().collect(Collectors.toMap(PCMMLevelColor::getCode, c -> c)));
			}

			// set mode
			pcmmSpecification.setMode(getPCMMMode(model));

			// set phases
			pcmmSpecification.setPhases(getPCMMPhases());

			// set options
			pcmmSpecification.setOptions(getPCMMOptions());

			// set roles
			pcmmSpecification.setRoles(getRoles());

			// set planning fields
			pcmmSpecification.setPlanningFields(
					getAppMgr().getService(IPCMMPlanningApplication.class).getPlanningFieldsBy(null));

			// set planning questions
			List<PCMMPlanningQuestion> questions = getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class)
					.findAll();
			if (questions != null) {
				Map<IAssessable, List<PCMMPlanningQuestion>> collect = new HashMap<>();
				for (PCMMPlanningQuestion question : questions) {
					IAssessable assessable = question.getElement() != null ? question.getElement()
							: question.getSubelement();
					if (!collect.containsKey(assessable)) {
						collect.put(assessable, new ArrayList<>());
					}
					collect.get(assessable).add(question);
				}
				pcmmSpecification.setPlanningQuestions(collect);
			}
		}

		return pcmmSpecification;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameConfiguration(PCMMSpecification spec1, PCMMSpecification spec2) {

		if (spec1 == null) {
			return spec2 == null;
		} else if (spec2 == null) {
			return false;
		}

		// elements
		if (!getAppMgr().getService(IImportApplication.class).sameListContent(spec1.getElements(),
				spec2.getElements())) {
			return false;
		}

		// subelements
		List<PCMMSubelement> sub1 = new ArrayList<>();
		List<PCMMSubelement> sub2 = new ArrayList<>();
		if (spec1.getElements() != null && spec2.getElements() != null) {
			for (PCMMElement elt : spec1.getElements()) {
				if (elt.getSubElementList() != null) {
					sub1.addAll(elt.getSubElementList());
				}
			}
			for (PCMMElement elt : spec2.getElements()) {
				if (elt.getSubElementList() != null) {
					sub2.addAll(elt.getSubElementList());
				}
			}

			if (!getAppMgr().getService(IImportApplication.class).sameListContent(sub1, sub2)) {
				return false;
			}
		}

		// levels
		if (!sub1.isEmpty() && !sub2.isEmpty()) {
			List<PCMMLevel> levels1 = new ArrayList<>();
			for (PCMMSubelement elt : sub1) {
				if (elt.getLevelList() != null) {
					levels1.addAll(elt.getLevelList());
				}
			}
			List<PCMMLevel> levels2 = new ArrayList<>();
			for (PCMMSubelement elt : sub2) {
				if (elt.getLevelList() != null) {
					levels2.addAll(elt.getLevelList());
				}
			}

			if (!getAppMgr().getService(IImportApplication.class).sameListContent(levels1, levels2)) {
				return false;
			}
		}

		// options
		if (!getAppMgr().getService(IImportApplication.class).sameListContent(spec1.getOptions(), spec2.getOptions())) {
			return false;
		}

		// roles
		if (!getAppMgr().getService(IImportApplication.class).sameListContent(spec1.getRoles(), spec2.getRoles())) {
			return false;
		}

		// level colors
		if ((spec1.getLevelColors() == null && spec2.getLevelColors() != null)
				|| (spec1.getLevelColors() != null && spec2.getLevelColors() == null)
				|| (spec1.getLevelColors() != null && spec2.getLevelColors() != null
						&& !getAppMgr().getService(IImportApplication.class).sameListContent(
								new ArrayList<>(spec1.getLevelColors().values()),
								new ArrayList<>(spec2.getLevelColors().values())))) {
			return false;
		}

		// Planning fields
		if ((spec1.getPlanningFields() == null && spec2.getPlanningFields() != null)
				|| (spec1.getPlanningFields() != null && spec2.getPlanningFields() == null)
				|| (spec1.getPlanningFields() != null && spec2.getPlanningFields() != null
						&& !getAppMgr().getService(IImportApplication.class).sameListContent(
								getAppMgr().getService(IPCMMPlanningApplication.class)
										.flatListParamWithChildren(spec1.getPlanningFields()),
								getAppMgr().getService(IPCMMPlanningApplication.class)
										.flatListParamWithChildren(spec2.getPlanningFields())))) {
			return false;
		}

		// Planning questions
		if ((spec1.getPlanningQuestions() == null && spec2.getPlanningQuestions() != null)
				|| (spec1.getPlanningQuestions() != null && spec2.getPlanningQuestions() == null)) {
			return false;
		} else if (spec1.getPlanningQuestions() != null && spec2.getPlanningQuestions() != null) {

			List<PCMMPlanningQuestion> questions1 = new ArrayList<>();
			spec1.getPlanningQuestions().values().forEach(questions1::addAll);
			List<PCMMPlanningQuestion> questions2 = new ArrayList<>();
			spec2.getPlanningQuestions().values().forEach(questions2::addAll);

			if (!getAppMgr().getService(IImportApplication.class).sameListContent(questions1, questions2)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * By default the mode is DEFAULT. If there is at least one level associated to
	 * one PCMM Element, the mode returned is SIMPLIFIED.
	 * 
	 * @param elements the PCMM Elements to check the mode
	 * @return the PCMM mode activated
	 * @throws CredibilityException
	 */
	private PCMMMode getPCMMMode(Model model) throws CredibilityException {
		PCMMMode mode = PCMMMode.DEFAULT;
		List<PCMMElement> elementList = getElementList(model);
		if (elementList != null) {
			boolean elementsHaveLevels = false;
			for (PCMMElement element : elementList) {
				if (element != null && element.getLevelList() != null && !element.getLevelList().isEmpty()) {
					elementsHaveLevels = true;
					break;
				}
			}

			if (elementsHaveLevels) {
				mode = PCMMMode.SIMPLIFIED;
			}
		}
		return mode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMPhase> getPCMMPhases() {
		List<PCMMPhase> phases = new ArrayList<>();

		// get options
		List<PCMMOption> pcmmOptions = getPCMMOptions();

		// transform option to phase
		if (pcmmOptions != null) {
			phases = pcmmOptions.stream().map(PCMMOption::getPhase).collect(Collectors.toList());
		}

		return phases;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMOption> getPCMMOptions() {
		return getDaoManager().getRepository(IPCMMOptionRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMOption addPCMMOption(PCMMOption option) throws CredibilityException {

		if (option == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDPCMMOPTION_NULL));
		}

		return getDaoManager().getRepository(IPCMMOptionRepository.class).create(option);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMOption updatePCMMOption(PCMMOption option) throws CredibilityException {

		if (option == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEPCMMOPTION_NULL));
		} else if (option.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEPCMMOPTION_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMOptionRepository.class).update(option);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePCMMOption(PCMMOption option) throws CredibilityException {

		if (option == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEPCMMOPTION_NULL));
		} else if (option.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEPCMMOPTION_IDNULL));
		}

		getDaoManager().getRepository(IPCMMOptionRepository.class).delete(option);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllPCMMOptions(List<PCMMOption> options) throws CredibilityException {
		if (options != null) {
			for (PCMMOption option : options) {
				deletePCMMOption(option);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCompleteAggregation(Model model, Tag tag) throws CredibilityException {

		boolean isCompleteAggregation = false;

		List<PCMMElement> elements = getElementList(model);

		if (elements != null) {
			isCompleteAggregation = true;
			for (PCMMElement element : elements) {
				for (PCMMSubelement sub : element.getSubElementList()) {
					Map<EntityFilter, Object> filters = new HashMap<>();
					filters.put(PCMMAssessment.Filter.TAG, tag);
					List<PCMMAssessment> assessmentBySubelement = getAssessmentBySubelement(sub, filters);
					if (assessmentBySubelement == null || assessmentBySubelement.isEmpty()) {
						isCompleteAggregation = false;
						break;
					}
				}
			}
		}

		return isCompleteAggregation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCompleteAggregationSimplified(Model model, Tag tag) throws CredibilityException {

		boolean isCompleteAggregation = false;

		List<PCMMElement> elements = getElementList(model);

		if (elements != null) {
			isCompleteAggregation = true;
			for (PCMMElement element : elements) {
				Map<EntityFilter, Object> filters = new HashMap<>();
				filters.put(PCMMAssessment.Filter.TAG, tag);
				List<PCMMAssessment> assessmentByElement = getAssessmentByElement(element, filters);
				if (assessmentByElement == null || assessmentByElement.isEmpty()) {
					isCompleteAggregation = false;
					break;
				}
			}
		}

		return isCompleteAggregation;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateSubelements(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException {

		// check elements
		if (elements == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTLISTNULL));
		}
		if (configuration == null || configuration.getLevelColors() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_CONFLEVELCOLORLISTNULL));
		}

		return aggregateSubelements(configuration, aggregateAssessments(configuration, elements, filters));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateSubelements(PCMMSpecification configuration,
			Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> mapAggregationBySubelement)
			throws CredibilityException {

		Map<PCMMElement, PCMMAggregation<PCMMElement>> aggegationMap = new HashMap<>();

		// check elements
		if (mapAggregationBySubelement == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_AGGREGMAPNULL));
		}
		if (configuration == null || configuration.getLevelColors() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_CONFLEVELCOLORLISTNULL));
		}

		// get the data used to compute the aggregation
		Map<PCMMElement, Integer> mapNbElement = new HashMap<>();
		Map<PCMMElement, Integer> mapSum = new HashMap<>();
		for (PCMMAggregation<PCMMSubelement> aggregSub : mapAggregationBySubelement.values()) {

			if (aggregSub.getItem() != null && aggregSub.getItem().getElement() != null) {
				// Get element
				PCMMElement elementTmp = aggregSub.getItem().getElement();

				// the sum of the level codes
				int sum = 0;
				if (aggregSub.getLevel() != null && aggregSub.getLevel().getCode() != null) {
					sum = aggregSub.getLevel().getCode();
				}

				// the number of element
				int nbElement = 0;
				if (aggregSub.getLevel() != null && aggregSub.getLevel().getCode() != null) {
					nbElement = 1;
				}

				if (!mapNbElement.containsKey(elementTmp)) {

					// initialize element maps
					mapNbElement.put(elementTmp, nbElement);
					mapSum.put(elementTmp, sum);

				} else {

					// add an element level to the maps
					mapNbElement.put(elementTmp, mapNbElement.get(elementTmp) + nbElement);
					mapSum.put(elementTmp, mapSum.get(elementTmp) + sum);

				}
			}
		}

		// aggregate the result
		for (Entry<PCMMElement, Integer> entry : mapNbElement.entrySet()) {

			if (entry != null) {

				PCMMElement elementTmp = entry.getKey();
				int nbAggregation = entry.getValue();

				if (elementTmp != null) {
					// retrieve the sum and the number of elements
					int sum = mapSum.get(elementTmp);

					// create the aggregation result
					PCMMAggregation<PCMMElement> aggregation = new PCMMAggregation<>();
					aggregation.setItem(elementTmp);

					// aggregate
					if (nbAggregation > 0) {
						float average = (((float) sum) / ((float) nbAggregation));
						int code = (int) Math.ceil((double) average); // round to the highest closest int

						// Set level
						List<PCMMLevel> levels = getDaoManager().getRepository(IPCMMLevelRepository.class)
								.findByPCMMElement(elementTmp);
						aggregation.setLevel(getClosestLevelForCode(configuration, levels, code));
					}

					// put the aggregation in the map to return
					aggegationMap.put(elementTmp, aggregation);
				}
			}
		}

		return aggegationMap;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggregateAssessments(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException {

		Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggegation = new HashMap<>();

		// check elements
		if (elements == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTLISTNULL));
		} else {
			for (PCMMElement element : elements) {
				aggegation.putAll(aggregateAssessments(configuration, element, filters));
			}
		}
		return aggegation;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggregateAssessments(PCMMSpecification configuration,
			PCMMElement element, Map<EntityFilter, Object> filters) throws CredibilityException {

		Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggegation = new HashMap<>();

		// check element
		if (element == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTNULL));
		} else {
			if (element.getSubElementList() != null) {
				for (PCMMSubelement subelt : element.getSubElementList()) {
					aggegation.put(subelt,
							aggregateAssessments(configuration, subelt, getAssessmentBySubelement(subelt, filters)));
				}
			}
		}
		return aggegation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregateAssessmentSimplified(PCMMSpecification configuration,
			List<PCMMElement> elements, Map<EntityFilter, Object> filters) throws CredibilityException {

		Map<PCMMElement, PCMMAggregation<PCMMElement>> aggegation = new HashMap<>();

		// check elements
		if (elements == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTLISTNULL));
		} else {
			for (PCMMElement element : elements) {
				aggegation.put(element,
						aggregateAssessments(configuration, element, getAssessmentByElement(element, filters)));
			}
		}
		return aggegation;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T extends IAssessable> PCMMAggregation<T> aggregateAssessments(PCMMSpecification configuration, T item,
			List<PCMMAssessment> assessmentList) throws CredibilityException {

		PCMMAggregation<T> aggregation = null;

		// check item
		if (item == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ITEMNULL));
		}

		if (assessmentList != null) {
			int sum = 0;
			int nbAssessments = 0;
			aggregation = new PCMMAggregation<>();
			ArrayList<String> commentList = new ArrayList<>();
			for (PCMMAssessment assessmt : assessmentList) {
				// compute the aggregation
				PCMMLevel level = assessmt.getLevel();
				if (level != null && level.getCode() != null) {
					sum += level.getCode();
					nbAssessments++;
				}
				commentList.add(StringTools.clearHtml(assessmt.getComment()));
			}

			// fill the aggregation
			aggregation.setCommentList(commentList);
			aggregation.setItem(item);
			if (nbAssessments > 0) {
				double average = (((double) sum) / ((double) nbAssessments));
				int code = (int) Math.ceil(average); // round to the closest int

				// Get fresh levels
				List<PCMMLevel> levels = new ArrayList<>();
				if (item instanceof PCMMElement) {
					levels = getDaoManager().getRepository(IPCMMLevelRepository.class)
							.findByPCMMElement((PCMMElement) item);
				} else if (item instanceof PCMMSubelement) {
					levels = getDaoManager().getRepository(IPCMMLevelRepository.class)
							.findByPCMMSubelement((PCMMSubelement) item);
				}
				aggregation.setLevel(getClosestLevelForCode(configuration, levels, code));
			}
		}
		return aggregation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getActiveAssessmentList() throws CredibilityException {
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findAllActive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByElement(PCMMElement elt, Map<EntityFilter, Object> filters)
			throws CredibilityException {
		if (filters == null) {
			filters = new HashMap<>();
		}
		filters.put(PCMMAssessment.Filter.ELEMENT, elt);
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByElementInSubelement(PCMMElement elt, Tag tag)
			throws CredibilityException {
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findByElementAndTagInSubelement(elt, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByRoleAndUserAndEltAndTag(Role role, User user, PCMMElement elt, Tag tag)
			throws CredibilityException {
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findByRoleAndUserAndEltAndTag(role, user,
				elt, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByRoleAndUserAndSubeltAndTag(Role role, User user, PCMMSubelement subelt,
			Tag tag) throws CredibilityException {
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findByRoleAndUserAndSubeltAndTag(role,
				user, subelt, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentBySubelement(PCMMSubelement subelt, Map<EntityFilter, Object> filters)
			throws CredibilityException {
		if (filters == null) {
			filters = new HashMap<>();
		}
		filters.put(PCMMAssessment.Filter.SUBELEMENT, subelt);
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByTag(Tag tag) throws CredibilityException {
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findByTag(tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByTag(List<Tag> tagList) throws CredibilityException {

		List<PCMMAssessment> pcmmAssessmentList = new ArrayList<>();
		if (tagList != null) {
			for (Tag tag : tagList) {
				pcmmAssessmentList
						.addAll(getDaoManager().getRepository(IPCMMAssessmentRepository.class).findByTag(tag));
			}
		}

		return pcmmAssessmentList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMAssessment getAssessmentById(Integer id) throws CredibilityException {

		// check assessment before update
		if (id == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_GETASSESSTBYID_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMAssessment addAssessment(PCMMAssessment assessment) throws CredibilityException {

		// check assessment before add
		if (assessment == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDASSESSTBYID_ASSESSTNULL));
		}

		// set creation data
		assessment.setDateCreation(DateTools.getCurrentDate());

		// create
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).create(assessment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMAssessment updateAssessment(PCMMAssessment assessment, User user, Role role)
			throws CredibilityException {

		// check assessment before update
		if (assessment == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_ASSESSTNULL));
		} else if (assessment.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_IDNULL));
		} else if (assessment.getUserCreation() != null && !assessment.getUserCreation().equals(user)) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_DIFFUSERNULL));
		} else if (assessment.getRoleCreation() != null && !assessment.getRoleCreation().equals(role)) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_DIFFROLENULL));
		}

		// set the update date
		assessment.setDateUpdate(DateTools.getCurrentDate());

		// update
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).update(assessment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAssessment(PCMMAssessment assessment) throws CredibilityException {

		// check assessment before update
		if (assessment == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEASSESSTBYID_ASSESSTNULL));
		} else if (assessment.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEASSESSTBYID_IDNULL));
		}

		// delete
		getDaoManager().getRepository(IPCMMAssessmentRepository.class).delete(assessment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAssessment(List<PCMMAssessment> assessmentList) throws CredibilityException {
		if (assessmentList != null) {
			for (PCMMAssessment assessment : assessmentList) {
				deleteAssessment(assessment);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<PCMMElement> getElementList(Model model) throws CredibilityException {

		// check parameters
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_GETELTLIST_MODELNULL));
		}

		return getDaoManager().getRepository(IPCMMElementRepository.class).findByModel(model);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMElement getElementById(Integer id) throws CredibilityException {

		// check parameters
		if (id == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_GETELTBYID_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMElementRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMElement addElement(PCMMElement element) throws CredibilityException {

		// check parameters
		if (element == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDELT_ELTNULL));
		}

		return getDaoManager().getRepository(IPCMMElementRepository.class).create(element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMElement updateElement(PCMMElement element) throws CredibilityException {

		// check parameters
		if (element == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEELT_ELTNULL));
		} else if (element.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEELT_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMElementRepository.class).update(element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteElement(PCMMElement element) throws CredibilityException {

		// check parameters
		if (element == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEELT_ELTNULL));
		} else if (element.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEELT_IDNULL));
		}

		getDaoManager().getRepository(IPCMMElementRepository.class).delete(element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> getActiveEvidenceList() throws CredibilityException {
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findAllActive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMEvidence getEvidenceById(Integer id) throws CredibilityException {

		// check parameters
		if (id == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_GETEVIDENCEBYID_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> getAllEvidence() throws CredibilityException {
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> getEvidenceByTag(Tag tag) throws CredibilityException {
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findByTag(tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> getEvidenceByTag(List<Tag> tagList) throws CredibilityException {

		List<PCMMEvidence> pcmmEvidenceList = new ArrayList<>();
		if (tagList != null) {
			for (Tag tag : tagList) {
				pcmmEvidenceList.addAll(getDaoManager().getRepository(IPCMMEvidenceRepository.class).findByTag(tag));
			}
		}

		return pcmmEvidenceList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMEvidence> getEvidenceBy(Map<EntityFilter, Object> filters) {

		// avoid character encoding error
		if (filters != null && filters.containsKey(PCMMEvidence.Filter.VALUE)) {
			String path = (String) filters.get(PCMMEvidence.Filter.VALUE);
			if (path != null) {
				filters.put(PCMMEvidence.Filter.VALUE, path.replace("\\", "/"));//$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMEvidence addEvidence(PCMMEvidence evidence) throws CredibilityException {

		// check parameters
		if (evidence == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDEVIDENCE_EVIDENCENULL));
		} else if (FormFieldType.LINK_FILE.equals(evidence.getType())
				&& !FileTools.isPathValidInWorkspace(evidence.getPath())) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDPATH, evidence.getPath()));
		} else if (FormFieldType.LINK_URL.equals(evidence.getType()) && (evidence.getPath() == null
				|| evidence.getPath().isEmpty() || !NetTools.isValidURL(evidence.getPath()))) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDURL, evidence.getPath()));
		} else if (evidence.getElement() == null && evidence.getSubelement() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_NOASSESSABLE));
		} else if (evidence.getElement() != null && evidence.getSubelement() != null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_MORETHANONEASSESSABLE));
		}

		// check if evidence already exists for the same PCMM element/subelement
		checkEvidenceWithSamePathInAssessable(evidence);

		// set creation data
		evidence.setDateCreation(DateTools.getCurrentDate());

		// set path
		if (evidence.getPath() != null && FormFieldType.LINK_FILE.equals(evidence.getType())) {
			evidence.setFilePath(evidence.getPath().replace("\\", "/")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// set date file
		if (evidence.getDateFile() == null && FormFieldType.LINK_FILE.equals(evidence.getType())) {
			evidence.setDateFile(FileTools.getLastUpdatedDate(new Path(evidence.getPath())));
		}

		// set file name
		if (evidence.getName() == null && FormFieldType.LINK_FILE.equals(evidence.getType())) {
			String[] split = evidence.getPath().split("/"); //$NON-NLS-1$
			evidence.setName(split[split.length - 1]);
		}
		// create
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).create(evidence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkEvidenceWithSamePathInAssessable(String value, String section, IAssessable assessable)
			throws CredibilityException {

		// check if it is a url
		checkEvidenceURLWithSamePathInAssessable(value, section, assessable);

		// check if it is a file
		checkEvidenceFilepathWithSamePathInAssessable(value, section, assessable);
	}

	/**
	 * Check if the evidence does not already exist
	 * 
	 * @param url      the url to find
	 * @param section  the section to find
	 * @param evidence the assessable to check
	 * @throws CredibilityException if the evidence already exists
	 */
	private void checkEvidenceURLWithSamePathInAssessable(String url, String section, IAssessable assessable)
			throws CredibilityException {
		PCMMEvidence evidence = new PCMMEvidence();
		evidence.setURL(url);
		evidence.setSection(section);
		if (assessable instanceof PCMMElement) {
			evidence.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			evidence.setSubelement((PCMMSubelement) assessable);
		}
		checkEvidenceWithSamePathInAssessable(evidence);
	}

	/**
	 * Check if the evidence does not already exist
	 * 
	 * @param filepath the filepath to find
	 * @param section  the section to find
	 * @param evidence the assessable to check
	 * @throws CredibilityException if the evidence already exists
	 */
	private void checkEvidenceFilepathWithSamePathInAssessable(String filepath, String section, IAssessable assessable)
			throws CredibilityException {
		PCMMEvidence evidence = new PCMMEvidence();
		evidence.setFilePath(filepath);
		evidence.setSection(section);
		if (assessable instanceof PCMMElement) {
			evidence.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			evidence.setSubelement((PCMMSubelement) assessable);
		}
		checkEvidenceWithSamePathInAssessable(evidence);
	}

	/**
	 * Check if the evidence does not already exist
	 * 
	 * @param evidence the evidence to check
	 * @throws CredibilityException if the evidence already exists
	 */
	private void checkEvidenceWithSamePathInAssessable(PCMMEvidence evidence) throws CredibilityException {

		// find duplicated evidence
		List<PCMMEvidence> listEvidenceWithSamePath = findDuplicateEvidenceByPathAndSection(evidence);

		if (listEvidenceWithSamePath == null || listEvidenceWithSamePath.isEmpty()) {
			return;
		}

		String strListEvidence = null;
		List<PCMMEvidence> evidenceFound = null;

		if (evidence.getElement() != null) {
			// Get PCMM Evidences with same path in element
			evidenceFound = listEvidenceWithSamePath.stream().filter(Objects::nonNull)
					.filter(evidenceWithSamePath -> evidenceWithSamePath.getElement() != null
							&& evidenceWithSamePath.getElement().getId().equals(evidence.getElement().getId()))
					.filter(evidenceTmp -> evidenceTmp.getId() != null && !evidenceTmp.getId().equals(evidence.getId()))
					.collect(Collectors.toList());

			strListEvidence = evidenceFound.stream().map(evid -> evid.getElement().getName())
					.collect(Collectors.joining(RscTools.COMMA));

		} else if (evidence.getSubelement() != null) {
			// Get PCMM Evidences with same path in sub-element
			evidenceFound = listEvidenceWithSamePath.stream().filter(Objects::nonNull)
					.filter(evidenceWithSamePath -> evidenceWithSamePath.getSubelement() != null
							&& evidenceWithSamePath.getSubelement().getId().equals(evidence.getSubelement().getId()))
					.filter(evidenceTmp -> evidenceTmp.getId() != null && !evidenceTmp.getId().equals(evidence.getId()))
					.collect(Collectors.toList());

			strListEvidence = evidenceFound.stream().map(evid -> evid.getSubelement().getName())
					.collect(Collectors.joining(RscTools.COMMA));
		}

		// Check has PCMM Evidences with same path in sub-element
		if (evidenceFound != null && !evidenceFound.isEmpty()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDEVIDENCE_ALREADYEXISTS,
					evidence.getPath(), strListEvidence));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMEvidence updateEvidence(PCMMEvidence evidence) throws CredibilityException {

		// check parameters
		if (evidence == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEEVIDENCE_ELTNULL));
		} else if (evidence.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEEVIDENCE_IDNULL));
		} else if (FormFieldType.LINK_FILE.equals(evidence.getType())
				&& !FileTools.isPathValidInWorkspace(evidence.getPath())) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDPATH, evidence.getPath()));
		} else if (FormFieldType.LINK_URL.equals(evidence.getType()) && (evidence.getPath() == null
				|| evidence.getPath().isEmpty() || !NetTools.isValidURL(evidence.getPath()))) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PCMM_EVIDENCE_INVALIDURL, evidence.getPath()));
		} else if (evidence.getElement() == null && evidence.getSubelement() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_NOASSESSABLE));
		} else if (evidence.getElement() != null && evidence.getSubelement() != null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_EVIDENCE_MORETHANONEASSESSABLE));
		}

		// check evidence path already exists for the same pcmm element/subelement
		checkEvidenceWithSamePathInAssessable(evidence);

		// set the update date
		evidence.setDateUpdate(DateTools.getCurrentDate());

		// set date file
		if (evidence.getDateFile() == null && FormFieldType.LINK_FILE.equals(evidence.getType())) {
			evidence.setDateFile(FileTools.getLastUpdatedDate(new Path(evidence.getPath())));
		}

		// update
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).update(evidence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEvidence(PCMMEvidence evidence) throws CredibilityException {

		// check parameters
		if (evidence == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEEVIDENCE_ELTNULL));
		} else if (evidence.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEEVIDENCE_IDNULL));
		}

		getDaoManager().getRepository(IPCMMEvidenceRepository.class).delete(evidence);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEvidence(List<PCMMEvidence> evidenceList) throws CredibilityException {
		if (evidenceList != null) {
			for (PCMMEvidence evidence : evidenceList) {
				deleteEvidence(evidence);
			}
		} else {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEEVIDENCE_ELTNULL));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMAggregationLevel getClosestLevelForCode(PCMMSpecification configuration, List<PCMMLevel> levels,
			int code) {

		if (levels == null) {
			return null;
		}

		// search for the closest level
		PCMMLevel closestLevel = null;
		PCMMAggregationLevel aggLevel = null;

		// sort the level list by code
		levels.sort(getLevelComparator());

		for (PCMMLevel level : levels.stream().filter(Objects::nonNull).collect(Collectors.toList())) {
			if (closestLevel == null) {
				closestLevel = level;
			}
			if (level.getCode() != null) {
				// if the level code is exactly the same as the parameter, return it
				if (level.getCode() <= code) {
					closestLevel = level;
				} else {
					break;
				}
			}
		}

		// Get level
		aggLevel = new PCMMAggregationLevel();
		aggLevel.setCode(code);
		if (null != configuration.getLevelColors() && configuration.getLevelColors().get(code) != null) {
			aggLevel.setName(configuration.getLevelColors().get(code).getName());
		}

		return aggLevel;
	}

	/**
	 * @return a level comparator based on the level code
	 */
	private Comparator<PCMMLevel> getLevelComparator() {
		return (o1, o2) -> {
			if (o1 == null && o2 == null)
				return 0;
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			return Comparator.comparing(PCMMLevel::getCode, Comparator.nullsFirst(Comparator.naturalOrder()))
					.compare(o1, o2);
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMLevel getLevelById(Integer id) throws CredibilityException {

		// check parameters
		if (id == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_GETLEVELBYID_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMLevelRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMLevel addLevel(PCMMLevel level) throws CredibilityException {

		// check parameters
		if (level == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDLEVEL_LEVELNULL));
		}

		return getDaoManager().getRepository(IPCMMLevelRepository.class).create(level);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMLevel updateLevel(PCMMLevel level) throws CredibilityException {

		// check parameters
		if (level == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATELEVEL_LEVELNULL));
		} else if (level.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATELEVEL_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMLevelRepository.class).update(level);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteLevel(PCMMLevel level) throws CredibilityException {

		// check parameters
		if (level == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETELEVEL_LEVELNULL));
		} else if (level.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETELEVEL_IDNULL));
		}

		getDaoManager().getRepository(IPCMMLevelRepository.class).delete(level);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMLevelDescriptor getLevelDescriptorById(Integer id) throws CredibilityException {

		// check parameters
		if (id == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETELEVELDESC_LEVELDESCNULL));
		}

		return getDaoManager().getRepository(IPCMMLevelDescRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMLevelDescriptor addLevelDescriptor(PCMMLevelDescriptor levelDescriptor) throws CredibilityException {

		// check parameters
		if (levelDescriptor == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDLEVELDESC_LEVELDESCNULL));
		}

		// the max size of the descriptor
		int maxValueSize = 1500;

		// Check value length
		String finalValue = null;
		if (levelDescriptor.getValue() != null && levelDescriptor.getValue().length() > maxValueSize) {
			finalValue = levelDescriptor.getValue().substring(0, (maxValueSize - 1));
			logger.warn("Level Descriptor value truncated."); //$NON-NLS-1$
		} else {
			finalValue = levelDescriptor.getValue();
		}

		// Set value
		levelDescriptor.setValue(finalValue);

		// Save
		return getDaoManager().getRepository(IPCMMLevelDescRepository.class).create(levelDescriptor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMLevelDescriptor updateLevelDescriptor(PCMMLevelDescriptor levelDescriptor) throws CredibilityException {

		// check parameters
		if (levelDescriptor == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATELEVELDESC_LEVELDESCNULL));
		} else if (levelDescriptor.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATELEVELDESC_IDNULL));
		}

		// the max size of the descriptor
		int maxValueSize = 1500;

		// Check value length
		String finalValue = null;
		if (levelDescriptor.getValue() != null && levelDescriptor.getValue().length() > maxValueSize) {
			finalValue = levelDescriptor.getValue().substring(0, (maxValueSize - 1));
			logger.warn("Level Descriptor value truncated."); //$NON-NLS-1$
		} else {
			finalValue = levelDescriptor.getValue();
		}

		// Set value
		levelDescriptor.setValue(finalValue);

		return getDaoManager().getRepository(IPCMMLevelDescRepository.class).update(levelDescriptor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteLevelDescriptor(PCMMLevelDescriptor levelDescriptor) throws CredibilityException {

		// check parameters
		if (levelDescriptor == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETELEVELDESC_LEVELDESCNULL));
		} else if (levelDescriptor.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETELEVELDESC_IDNULL));
		}

		getDaoManager().getRepository(IPCMMLevelDescRepository.class).delete(levelDescriptor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMSubelement getSubelementById(Integer id) throws CredibilityException {

		// check parameters
		if (id == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_GETSUBELTBYID_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMSubelementRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMSubelement addSubelement(PCMMSubelement subelement) throws CredibilityException {

		// check parameters
		if (subelement == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDSUBELT_SUBELTNULL));
		}

		return getDaoManager().getRepository(IPCMMSubelementRepository.class).create(subelement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMSubelement updateSubelement(PCMMSubelement subelement) throws CredibilityException {

		// check parameters
		if (subelement == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATESUBELT_SUBELTNULL));
		} else if (subelement.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATESUBELT_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMSubelementRepository.class).update(subelement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteSubelement(PCMMSubelement subelement) throws CredibilityException {

		// check parameters
		if (subelement == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETESUBELT_SUBELTNULL));
		} else if (subelement.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETESUBELT_IDNULL));
		}

		getDaoManager().getRepository(IPCMMSubelementRepository.class).delete(subelement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Role> getRoles() {
		return getDaoManager().getRepository(IRoleRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role getRoleById(Integer id) throws CredibilityException {

		// check parameters
		if (id == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_GETROLEBYID_IDNULL));
		}

		return getDaoManager().getRepository(IRoleRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role addRole(Role role) throws CredibilityException {

		// check parameters
		if (role == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDROLE_ROLENULL));
		}

		return getDaoManager().getRepository(IRoleRepository.class).create(role);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role updateRole(Role role) throws CredibilityException {

		// check parameters
		if (role == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEROLE_ROLENULL));
		} else if (role.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEROLE_IDNULL));
		}

		return getDaoManager().getRepository(IRoleRepository.class).update(role);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteRole(Role role) throws CredibilityException {

		// check parameters
		if (role == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEROLE_ROLENULL));
		} else if (role.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEROLE_IDNULL));
		}

		getDaoManager().getRepository(IRoleRepository.class).delete(role);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMLevelColor addLevelColor(PCMMLevelColor levelColor) throws CredibilityException {

		// check parameters
		if (levelColor == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDLEVELCOLOR_NULL));
		}

		return getDaoManager().getRepository(IPCMMLevelColorRepository.class).create(levelColor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMLevelColor updateLevelColor(PCMMLevelColor levelColor) throws CredibilityException {

		// check parameters
		if (levelColor == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATELEVELCOLOR_NULL));
		} else if (levelColor.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATELEVELCOLOR_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMLevelColorRepository.class).update(levelColor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteLevelColor(PCMMLevelColor levelColor) throws CredibilityException {

		// check parameters
		if (levelColor == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETELEVELCOLOR_NULL));
		} else if (levelColor.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETELEVELCOLOR_IDNULL));
		}

		getDaoManager().getRepository(IPCMMLevelColorRepository.class).delete(levelColor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tag tagCurrent(Tag newTag) throws CredibilityException {

		if (newTag == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_TAGCURRENT_TAGNULL));
		} else if (newTag.getUserCreation() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_TAGCURRENT_USERNULL));
		}

		newTag.setDateTag(DateTools.getCurrentDate());

		// create tag
		Tag createdTag = getDaoManager().getRepository(ITagRepository.class).create(newTag);

		// copy and tag active evidence
		for (PCMMEvidence evidence : getActiveEvidenceList()) {
			PCMMEvidence copy = evidence.copy();
			copy = getDaoManager().getRepository(IPCMMEvidenceRepository.class).create(copy);
			copy.setTag(createdTag);
			getDaoManager().getRepository(IPCMMEvidenceRepository.class).update(copy);

			// refresh the evidence in the entity manager
			if (evidence.getElement() != null) {
				getDaoManager().getRepository(IPCMMElementRepository.class).refresh(evidence.getElement());
			}
			if (evidence.getSubelement() != null) {
				getDaoManager().getRepository(IPCMMSubelementRepository.class).refresh(evidence.getSubelement());
			}
		}

		// copy and tag active assessments
		for (PCMMAssessment assessment : getActiveAssessmentList()) {
			PCMMAssessment copy = assessment.copy();
			copy = getDaoManager().getRepository(IPCMMAssessmentRepository.class).create(copy);
			copy.setTag(createdTag);
			getDaoManager().getRepository(IPCMMAssessmentRepository.class).update(copy);
		}

		// copy and tag pcmm planning
		getAppMgr().getService(IPCMMPlanningApplication.class).tagCurrent(createdTag);

		return createdTag;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Tag> getTags() {
		return getDaoManager().getRepository(ITagRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tag updateTag(Tag tag) throws CredibilityException {

		// check parameters
		if (tag == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATETAG_TAGNULL));
		} else if (tag.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATETAG_IDNULL));
		}

		return getDaoManager().getRepository(ITagRepository.class).update(tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteTag(Tag tag) throws CredibilityException {

		// check parameters
		if (tag == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETETAG_TAGNULL));
		} else if (tag.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETETAG_IDNULL));
		}

		// delete the associated evidence
		deleteEvidence(getEvidenceByTag(tag));

		// delete the associated assessments
		deleteAssessment(getAssessmentByTag(tag));

		// delete PCMM Planning items and values associated to this tag
		getAppMgr().getService(IPCMMPlanningApplication.class).deleteTagged(tag);

		// delete ARG Parameters PCMM tag referenced
		ARGParameters argParam = getAppMgr().getService(IReportARGExecutionApp.class).getARGParameters();
		if (argParam != null && tag.equals(argParam.getPcmmTagSelected())) {
			argParam.setPcmmTagSelected(null);
			getAppMgr().getService(IReportARGExecutionApp.class).updateARGParameters(argParam);
		}

		// delete the tag
		getDaoManager().getRepository(ITagRepository.class).delete(tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int computeMaxProgress(PCMMSpecification configuration) {

		int max = 0;

		if (configuration != null) {
			if (configuration.isPcmmPlanningEnabled()) {
				max += PCMM_PROGRESS_PLANNING_DEFAULT_WEIGHT;
			}
			if (configuration.isPcmmEvidenceEnabled()) {
				max += PCMM_PROGRESS_EVIDENCE_DEFAULT_WEIGHT;
			}
			if (configuration.isPcmmAssessEnabled()) {
				max += PCMM_PROGRESS_ASSESS_DEFAULT_WEIGHT;
			}
		}

		return max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int computeEvidenceMaxProgress(PCMMElement element, PCMMMode mode) {

		int max = 0;

		if (element != null) {
			if (PCMMMode.DEFAULT.equals(mode)) {
				int nbSubelements = element.getSubElementList() != null ? element.getSubElementList().size() : 0;
				max += nbSubelements;
			} else if (PCMMMode.SIMPLIFIED.equals(mode)) {
				max += 1;
			}
		}

		return max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int computeAssessMaxProgress(PCMMElement element, PCMMMode mode) {
		// The Assess Max Progress is the same as the Evidence for now.
		return computeEvidenceMaxProgress(element, mode);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int computeCurrentProgress(Model model, PCMMSpecification configuration) throws CredibilityException {

		// check parameters
		if (configuration == null) {
			return 0;
		}

		int cpt = 0;
		Tag tag = null; // the current progress is not for a specific tag
		List<PCMMElement> elementList = getElementList(model);
		if (elementList != null && !elementList.isEmpty()) {
			for (PCMMElement element : elementList) {
				cpt += computeCurrentProgressByElement(element, tag, configuration);
			}
			cpt = (cpt / elementList.size());
		}

		return cpt;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int computeCurrentProgressByElement(PCMMElement element, Tag selectedTag, PCMMSpecification configuration)
			throws CredibilityException {

		// check parameters
		if (element == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_PROGRESS_COMPUTE_ELTNULL));
		}
		if (configuration == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_PROGRESS_COMPUTE_CONFNULL));
		}

		// compute current progress
		float cptGlobal = 0F;

		// evidence progress
		if (Boolean.TRUE.equals(configuration.isPcmmEvidenceEnabled())) {
			int cpt = computeEvidenceProgress(element, selectedTag, configuration.getMode());
			int max = computeEvidenceMaxProgress(element, configuration.getMode());
			cptGlobal += max > 0 ? ((float) cpt / (float) max) * PCMM_PROGRESS_EVIDENCE_DEFAULT_WEIGHT : 0;
		}

		// assess progress
		if (Boolean.TRUE.equals(configuration.isPcmmAssessEnabled())) {
			int cpt = computeAssessProgress(element, selectedTag, configuration.getMode());
			int max = computeAssessMaxProgress(element, configuration.getMode());
			cptGlobal += max > 0 ? ((float) cpt / (float) max) * PCMM_PROGRESS_ASSESS_DEFAULT_WEIGHT : 0;
		}

		// planning progress
		if (Boolean.TRUE.equals(configuration.isPcmmPlanningEnabled())) {
			int cpt = getAppMgr().getService(IPCMMPlanningApplication.class).computePlanningProgress(element,
					selectedTag, configuration.getMode());
			int max = getAppMgr().getService(IPCMMPlanningApplication.class).computePlanningMaxProgress(element,
					configuration.getMode());
			cptGlobal += max > 0 ? ((float) cpt / (float) max) * PCMM_PROGRESS_PLANNING_DEFAULT_WEIGHT : 0;
		}

		return Math.round(cptGlobal);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int computeEvidenceProgress(PCMMElement element, Tag selectedTag, PCMMMode mode) {

		int cpt = 0;

		if (element != null) {

			// depending of the mode
			if (PCMMMode.DEFAULT.equals(mode)) {

				// in this mode we search the evidence for each subelement
				// compute the evidence progress
				for (PCMMSubelement subelement : element.getSubElementList()) {
					if (subelement != null && subelement.getEvidenceList() != null
							&& !subelement.getEvidenceList().isEmpty()) {
						cpt += getCurrentProgressForEvidence(subelement.getEvidenceList(), selectedTag);
					}
				}
			} else if (PCMMMode.SIMPLIFIED.equals(mode) && element.getEvidenceList() != null
					&& !element.getEvidenceList().isEmpty()) {

				// in this mode we just search the evidence at the element level
				// compute the evidence progress
				cpt += getCurrentProgressForEvidence(element.getEvidenceList(), selectedTag);
			}
		}

		return cpt;
	}

	/**
	 * @param evidenceList the evidence list to check
	 * @param selectedTag  the current tag
	 * @return the progress for the evidence list and tag in parameter
	 */
	private int getCurrentProgressForEvidence(List<PCMMEvidence> evidenceList, Tag selectedTag) {

		int max = 0;

		if (evidenceList != null) {
			for (PCMMEvidence evidence : evidenceList) {
				if (((selectedTag == null || selectedTag.getId() == null) && evidence.getTag() == null)
						|| (evidence.getTag() != null && selectedTag != null
								&& evidence.getTag().getId().equals(selectedTag.getId()))) {
					max++;
					break;
				}
			}
		}

		return max;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int computeAssessProgress(PCMMElement element, Tag selectedTag, PCMMMode mode) throws CredibilityException {

		int cpt = 0;
		List<PCMMAssessment> assessments = null;

		if (element != null) {

			// depending of the mode
			if (PCMMMode.DEFAULT.equals(mode)) {

				// in this mode we search the assessments for each subelement
				// compute the evidence progress
				assessments = getAssessmentByElementInSubelement(element, selectedTag);
				if (assessments != null) {
					Set<PCMMSubelement> subSet = new HashSet<>();
					for (PCMMAssessment asst : assessments.stream().filter(asst -> asst.getSubelement() != null)
							.collect(Collectors.toList())) {
						subSet.add(asst.getSubelement());
					}
					cpt += subSet.size();
				}
			} else if (PCMMMode.SIMPLIFIED.equals(mode)) {

				// in this mode we just search the assessments at the element level
				// compute the evidence progress

				Map<EntityFilter, Object> filters = new HashMap<>();
				filters.put(PCMMAssessment.Filter.TAG, selectedTag);
				assessments = getAssessmentByElement(element, filters);
				if (assessments != null && !assessments.isEmpty()) {
					cpt++;
				}
			}
		}

		return cpt;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<NotificationType, String> getDuplicatedEvidenceNotification(PCMMEvidence evidence,
			final Integer evidenceId) {

		// Initialize
		boolean isError = false;
		boolean hasNotification = false;
		StringBuilder evidenceToString = new StringBuilder();
		Map<NotificationType, String> notification = new EnumMap<>(NotificationType.class);

		// check if evidence already exists with same path and/or section
		Set<PCMMEvidence> listEvidenceWithSamePath = new HashSet<>(findDuplicateEvidenceByPathAndSection(evidence));

		// check for error
		isError = listEvidenceWithSamePath.stream().filter(Objects::nonNull)
				.filter(tmp -> !tmp.getId().equals(evidenceId))
				.anyMatch(tmp -> (tmp.getElement() != null && evidence.getElement() != null
						&& tmp.getElement().getId().equals(evidence.getElement().getId()))
						|| (tmp.getSubelement() != null && evidence.getSubelement() != null
								&& tmp.getSubelement().getId().equals(evidence.getSubelement().getId())));

		// build the notification string
		for (PCMMEvidence evidenceWithSamePath : listEvidenceWithSamePath) {
			if (!evidenceWithSamePath.getId().equals(evidenceId)) {
				if (evidenceWithSamePath.getElement() != null) {
					hasNotification = true;
					evidenceToString.append("<li>").append(evidenceWithSamePath.getElement().getName()) //$NON-NLS-1$
							.append("</li>"); //$NON-NLS-1$

				} else if (evidenceWithSamePath.getSubelement() != null) {
					hasNotification = true;
					evidenceToString.append("<li>").append(evidenceWithSamePath.getSubelement().getName()) //$NON-NLS-1$
							.append("</li>"); //$NON-NLS-1$
				}
			}
		}

		// Manage Error / Warning
		String message = org.apache.commons.lang3.StringUtils.isBlank(evidence.getSection())
				? RscTools.getString(RscConst.NOTIFICATION_PCMM_EVIDENCE_WARN_DUPLICATE_PATH_NOSECTION,
						evidence.getPath(), evidenceToString.toString())
				: RscTools.getString(RscConst.NOTIFICATION_PCMM_EVIDENCE_WARN_DUPLICATE_PATH, evidence.getPath(),
						evidence.getSection(), evidenceToString.toString());
		if (isError) {
			notification.put(NotificationType.ERROR, message);
		} else if (hasNotification) {
			notification.put(NotificationType.WARN, message);
		}

		// Return
		return notification;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<NotificationType, List<String>> getEvidenceNotifications(PCMMEvidence evidence,
			final Integer evidenceId) {

		// Initialize
		Map<NotificationType, List<String>> notifications = new EnumMap<>(NotificationType.class);
		notifications.put(NotificationType.ERROR, new ArrayList<>());
		notifications.put(NotificationType.WARN, new ArrayList<>());

		// Get duplicate warnings
		Map<NotificationType, String> duplicateNotification = getDuplicatedEvidenceNotification(evidence, evidenceId);
		if (duplicateNotification.containsKey(NotificationType.ERROR)) {
			notifications.get(NotificationType.ERROR).add(duplicateNotification.get(NotificationType.ERROR));
		} else if (duplicateNotification.containsKey(NotificationType.WARN)) {
			notifications.get(NotificationType.WARN).add(duplicateNotification.get(NotificationType.WARN));
		}

		// File not exists error
		if (FormFieldType.LINK_FILE.equals(evidence.getType())) {
			if (!Boolean.TRUE.equals(FileTools.filePathExist(evidence.getPath()))) {
				notifications.get(NotificationType.ERROR).add(RscTools
						.getString(RscConst.NOTIFICATION_PCMM_EVIDENCE_ERR_FILE_NOT_EXISTS, evidence.getName()));
			} else if (evidenceChanged(evidence)) {
				// Get updated file warnings
				notifications.get(NotificationType.WARN).add(
						RscTools.getString(RscConst.NOTIFICATION_PCMM_EVIDENCE_WARN_UPDATED_FILE, evidence.getName()));
			}
		}

		// Return
		return notifications;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean evidenceChanged(PCMMEvidence evidence) {
		return evidence != null && FormFieldType.LINK_FILE.equals(evidence.getType()) && null != evidence.getDateFile()
				&& !evidence.getDateFile().equals(FileTools.getLastUpdatedDate(new Path(evidence.getPath())));
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<PCMMEvidence, Map<NotificationType, List<String>>> getAllEvidenceNotifications()
			throws CredibilityException {
		// Initialize
		Map<PCMMEvidence, Map<NotificationType, List<String>>> notifications = new HashMap<>();

		// For each evidence
		List<PCMMEvidence> evidences = getAllEvidence();
		if (null != evidences && !evidences.isEmpty()) {
			evidences.stream().filter(evidence -> evidence != null && evidence.getTag() == null).forEach(evidence -> {
				// Get notification for this evidence
				Map<NotificationType, List<String>> evidenceNotifications = getEvidenceNotifications(evidence,
						evidence.getId());
				notifications.put(evidence, evidenceNotifications);
			});
		}

		// Return
		return notifications;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<PCMMEvidence> findDuplicateEvidenceByPath(PCMMEvidence evidence) {

		if (evidence == null) {
			return new ArrayList<>();
		}

		Map<EntityFilter, Object> filter = new HashMap<>();
		filter.put(PCMMEvidence.Filter.TAG, evidence.getTag());
		filter.put(PCMMEvidence.Filter.VALUE, evidence.getValue());
		return getDaoManager().getRepository(IPCMMEvidenceRepository.class).findBy(filter);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<PCMMEvidence> findDuplicateEvidenceByPathAndSection(PCMMEvidence evidence) {

		if (evidence == null) {
			return new ArrayList<>();
		}

		boolean testNull = false;

		String evidenceValue = evidence.getValue();
		Tag tag = evidence.getTag();
		String section = evidence.getSection();
		if (org.apache.commons.lang3.StringUtils.isBlank(section)) {
			section = RscTools.empty();
			testNull = true;
		}

		Map<EntityFilter, Object> filter = new HashMap<>();
		filter.put(PCMMEvidence.Filter.TAG, tag);
		filter.put(PCMMEvidence.Filter.VALUE, evidenceValue);
		filter.put(PCMMEvidence.Filter.SECTION, section);
		List<PCMMEvidence> listEvidenceWithSamePathAndSection = getEvidenceBy(filter);

		// test to get the section with null
		if (testNull) {
			filter = new HashMap<>();
			filter.put(PCMMEvidence.Filter.TAG, tag);
			filter.put(PCMMEvidence.Filter.VALUE, evidenceValue);
			filter.put(PCMMEvidence.Filter.SECTION, null);
			listEvidenceWithSamePathAndSection.addAll(getEvidenceBy(filter));
		}

		return listEvidenceWithSamePathAndSection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public int findEvidenceErrorNotification() throws CredibilityException {
		// Initialize
		int count = 0;

		// Get all notifications
		Map<PCMMEvidence, Map<NotificationType, List<String>>> evidencesNotifications = getAllEvidenceNotifications();
		for (Map.Entry<PCMMEvidence, Map<NotificationType, List<String>>> entry : evidencesNotifications.entrySet()) {

			// Get error notifications
			// Increment count if not empty
			if (entry.getValue().containsKey(NotificationType.ERROR)
					&& !entry.getValue().get(NotificationType.ERROR).isEmpty()) {
				count += entry.getValue().get(NotificationType.ERROR).size();
			}
		}

		// Result
		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	public int findEvidenceWarningNotification() throws CredibilityException {
		// Initialize
		int count = 0;

		// Get all notifications
		Map<PCMMEvidence, Map<NotificationType, List<String>>> evidencesNotifications = getAllEvidenceNotifications();
		for (Map.Entry<PCMMEvidence, Map<NotificationType, List<String>>> entry : evidencesNotifications.entrySet()) {

			// Get error notifications
			// Increment count if not empty
			if (entry.getValue().containsKey(NotificationType.WARN)
					&& !entry.getValue().get(NotificationType.WARN).isEmpty()) {
				count += entry.getValue().get(NotificationType.WARN).size();
			}
		}

		// Result
		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMElement getElementFromKey(String key) {

		if (key != null) {
			List<PCMMElement> findAll = getDaoManager().getRepository(IPCMMElementRepository.class).findAll();

			if (findAll != null) {
				for (PCMMElement tmp : findAll) {
					if (key.equals(tmp.getAbbreviation())) {
						return tmp;
					}
				}
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMSubelement getSubelementFromKey(String key) {
		if (key != null) {
			List<PCMMSubelement> findAll = getDaoManager().getRepository(IPCMMSubelementRepository.class).findAll();

			if (findAll != null) {
				for (PCMMSubelement tmp : findAll) {
					if (key.equals(tmp.getCode())) {
						return tmp;
					}
				}
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPCMMEnabled(Model model) throws CredibilityException {

		// If no elements, PCMM is not available
		List<PCMMElement> elements = getElementList(model);
		return elements != null && !elements.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshElement(PCMMElement element) {
		getDaoManager().getRepository(IPCMMElementRepository.class).refresh(element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshSubelement(PCMMSubelement subelement) {
		getDaoManager().getRepository(IPCMMSubelementRepository.class).refresh(subelement);
	}
}
