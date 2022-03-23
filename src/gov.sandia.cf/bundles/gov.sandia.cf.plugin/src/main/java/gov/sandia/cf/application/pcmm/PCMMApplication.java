/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.pcmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.application.report.IReportARGExecutionApp;
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
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
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
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
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
		for (PCMMEvidence evidence : getAppMgr().getService(IPCMMEvidenceApp.class).getActiveEvidenceList()) {
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
		for (PCMMAssessment assessment : getAppMgr().getService(IPCMMAssessmentApp.class).getActiveAssessmentList()) {
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
		getAppMgr().getService(IPCMMEvidenceApp.class)
				.deleteEvidence(getAppMgr().getService(IPCMMEvidenceApp.class).getEvidenceByTag(tag));

		// delete the associated assessments
		getAppMgr().getService(IPCMMAssessmentApp.class)
				.deleteAssessment(getAppMgr().getService(IPCMMAssessmentApp.class).getAssessmentByTag(tag));

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
				assessments = getAppMgr().getService(IPCMMAssessmentApp.class)
						.getAssessmentByElementInSubelement(element, selectedTag);
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
				assessments = getAppMgr().getService(IPCMMAssessmentApp.class).getAssessmentByElement(element, filters);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refreshAssessable(IAssessable assessable) {
		if (assessable instanceof PCMMElement) {
			refreshElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			refreshSubelement((PCMMSubelement) assessable);
		}
	}
}
