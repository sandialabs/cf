/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.hsqldb.lib.StringInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.configuration.YmlGenericSchema;
import gov.sandia.cf.dao.IARGParametersQoIOptionRepository;
import gov.sandia.cf.dao.IARGParametersRepository;
import gov.sandia.cf.dao.ICRUDRepository;
import gov.sandia.cf.dao.ICriterionRepository;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.dao.IDecisionParamRepository;
import gov.sandia.cf.dao.IDecisionRepository;
import gov.sandia.cf.dao.IDecisionValueRepository;
import gov.sandia.cf.dao.IIntendedPurposeRepository;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.IPCMMAssessmentRepository;
import gov.sandia.cf.dao.IPCMMElementRepository;
import gov.sandia.cf.dao.IPCMMEvidenceRepository;
import gov.sandia.cf.dao.IPCMMLevelColorRepository;
import gov.sandia.cf.dao.IPCMMLevelDescRepository;
import gov.sandia.cf.dao.IPCMMLevelRepository;
import gov.sandia.cf.dao.IPCMMOptionRepository;
import gov.sandia.cf.dao.IPCMMPlanningParamRepository;
import gov.sandia.cf.dao.IPCMMPlanningQuestionRepository;
import gov.sandia.cf.dao.IPCMMPlanningQuestionValueRepository;
import gov.sandia.cf.dao.IPCMMPlanningSelectValueRepository;
import gov.sandia.cf.dao.IPCMMPlanningTableItemRepository;
import gov.sandia.cf.dao.IPCMMPlanningTableValueRepository;
import gov.sandia.cf.dao.IPCMMPlanningValueRepository;
import gov.sandia.cf.dao.IPCMMSubelementRepository;
import gov.sandia.cf.dao.IPIRTAdequacyColumnGuidelineRepository;
import gov.sandia.cf.dao.IPIRTAdequacyLevelGuidelineRepository;
import gov.sandia.cf.dao.IPhenomenonGroupRepository;
import gov.sandia.cf.dao.IPhenomenonRepository;
import gov.sandia.cf.dao.IQoIHeaderRepository;
import gov.sandia.cf.dao.IQoIPlanningParamRepository;
import gov.sandia.cf.dao.IQoIPlanningValueRepository;
import gov.sandia.cf.dao.IQuantityOfInterestRepository;
import gov.sandia.cf.dao.IRoleRepository;
import gov.sandia.cf.dao.ISystemRequirementParamRepository;
import gov.sandia.cf.dao.ISystemRequirementRepository;
import gov.sandia.cf.dao.ISystemRequirementValueRepository;
import gov.sandia.cf.dao.ITagRepository;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.dao.IUncertaintyRepository;
import gov.sandia.cf.dao.IUncertaintyValueRepository;
import gov.sandia.cf.dao.IUserRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.ARGParametersQoIOption;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionValue;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterConstraint;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningTableValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QoIHeader;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningValue;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementValue;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tools.MathTools;

/**
 * This class generates default classes for tests
 * 
 * @author Didier Verstraete
 *
 */
public class TestEntityFactory {
	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(TestEntityFactory.class);

	/**
	 * @param daoManager the dao manager
	 * @return a new generated model
	 */
	public static Model getNewModel(IDaoManager daoManager) {

		assertNotNull(daoManager);

		// create model
		Model model = new Model();
		model.setVersion("VERSION"); //$NON-NLS-1$
		model.setVersionOrigin("VERSION_ORIGIN"); //$NON-NLS-1$
		try {
			model = daoManager.getRepository(IModelRepository.class).create(model);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(model);

		return model;
	}

	/**
	 * @param daoManager the dao manager
	 * @return a new generated PIRT level difference color
	 */
	public static PIRTLevelDifferenceColor getNewPIRTLevelDifferenceColors(IDaoManager daoManager) {

		assertNotNull(daoManager);

		// create
		PIRTLevelDifferenceColor color = new PIRTLevelDifferenceColor();
		color.setColor("192, 168, 124"); //$NON-NLS-1$
		color.setDescription("Description"); //$NON-NLS-1$
		color.setExplanation("Explanation"); //$NON-NLS-1$
		color.setMin(1);
		color.setMax(3);

		return color;
	}

	/**
	 * @param daoManager the dao manager
	 * @return a new generated PIRT adequacy column
	 */
	public static PIRTAdequacyColumn getNewPIRTAdequacyColumn(IDaoManager daoManager) {

		assertNotNull(daoManager);

		// create
		PIRTAdequacyColumn column = new PIRTAdequacyColumn();
		column.setIdLabel("A1"); //$NON-NLS-1$
		column.setName("Column"); //$NON-NLS-1$
		column.setType("Text"); //$NON-NLS-1$

		return column;
	}

	/**
	 * @param daoManager the dao manager
	 * @return a new generated PIRT description header
	 */
	public static PIRTDescriptionHeader getNewPIRTDescriptionHeader(IDaoManager daoManager) {

		assertNotNull(daoManager);

		// create
		PIRTDescriptionHeader column = new PIRTDescriptionHeader();
		column.setIdLabel("A1"); //$NON-NLS-1$
		column.setName("Header"); //$NON-NLS-1$

		return column;
	}

	/**
	 * @param daoManager the dao manager
	 * @return a new generated PIRT description header
	 */
	public static PIRTLevelImportance getNewPIRTLevelImportance(IDaoManager daoManager) {

		assertNotNull(daoManager);

		// create
		PIRTLevelImportance level = new PIRTLevelImportance();
		level.setIdLabel("A1"); //$NON-NLS-1$
		level.setName("Level 1"); //$NON-NLS-1$
		level.setFixedColor("125, 145, 25"); //$NON-NLS-1$
		level.setFixedColorDescription("Color description"); //$NON-NLS-1$
		level.setLabel("L1"); //$NON-NLS-1$
		level.setLevel(1);

		return level;
	}

	/**
	 * @param daoManager the dao manager
	 * @return a new generated PIRT description header
	 */
	public static PIRTAdequacyColumnGuideline getNewPIRTAdequacyColumnGuideline(IDaoManager daoManager) {

		assertNotNull(daoManager);

		// create
		PIRTAdequacyColumnGuideline columnGuideline = new PIRTAdequacyColumnGuideline();
		columnGuideline.setDescription("description"); //$NON-NLS-1$
		columnGuideline.setName("Level 1"); //$NON-NLS-1$

		try {
			columnGuideline = daoManager.getRepository(IPIRTAdequacyColumnGuidelineRepository.class)
					.create(columnGuideline);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		return columnGuideline;
	}

	/**
	 * @return a new generated PIRT description header
	 */
	public static PIRTAdequacyColumnGuideline getNewStubPIRTAdequacyColumnGuideline() {

		// create
		PIRTAdequacyColumnGuideline columnGuideline = new PIRTAdequacyColumnGuideline();
		columnGuideline.setDescription("description"); //$NON-NLS-1$
		columnGuideline.setName("Level 1"); //$NON-NLS-1$

		return columnGuideline;
	}

	/**
	 * @param daoManager the dao manager
	 * @param guideline  the PIRT guideline to associate
	 * @return a new generated PIRT description header
	 */
	public static PIRTAdequacyColumnLevelGuideline getNewPIRTAdequacyColumnLevelGuideline(IDaoManager daoManager,
			PIRTAdequacyColumnGuideline guideline) {

		assertNotNull(daoManager);

		// create
		PIRTAdequacyColumnLevelGuideline levelGuideline = new PIRTAdequacyColumnLevelGuideline();
		levelGuideline.setDescription("description"); //$NON-NLS-1$
		levelGuideline.setName("Level 1"); //$NON-NLS-1$
		levelGuideline.setAdequacyColumnGuideline(guideline);
		if (levelGuideline.getAdequacyColumnGuideline() == null) {
			levelGuideline.setAdequacyColumnGuideline(getNewStubPIRTAdequacyColumnGuideline());
		}

		try {
			levelGuideline = daoManager.getRepository(IPIRTAdequacyLevelGuidelineRepository.class)
					.create(levelGuideline);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		return levelGuideline;
	}

	/**
	 * @param guideline the PIRT guideline to associate
	 * @return a new generated PIRT description header
	 */
	public static PIRTAdequacyColumnLevelGuideline getNewStubPIRTAdequacyColumnLevelGuideline(
			PIRTAdequacyColumnGuideline guideline) {

		// create
		PIRTAdequacyColumnLevelGuideline levelGuideline = new PIRTAdequacyColumnLevelGuideline();
		levelGuideline.setDescription("description"); //$NON-NLS-1$
		levelGuideline.setName("Level 1"); //$NON-NLS-1$
		levelGuideline.setAdequacyColumnGuideline(guideline);
		if (levelGuideline.getAdequacyColumnGuideline() == null) {
			levelGuideline.setAdequacyColumnGuideline(getNewStubPIRTAdequacyColumnGuideline());
		}

		return levelGuideline;
	}

	/**
	 * @param daoManager the dao manager
	 * @param model      the model to associate
	 * @return a new generated qoi
	 */
	public static QuantityOfInterest getNewQoI(IDaoManager daoManager, Model model) {

		assertNotNull(daoManager);

		// create qoi
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setSymbol("QOI"); //$NON-NLS-1$
		if (model != null) {
			qoi.setModel(model);
		} else {
			qoi.setModel(getNewModel(daoManager));
		}
		qoi.setCreationDate(new Date());
		try {
			qoi = daoManager.getRepository(IQuantityOfInterestRepository.class).create(qoi);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(qoi);

		return qoi;
	}

	/**
	 * @param daoManager the dao manager
	 * @param parent     the qoi parent to associate
	 * @return a new generated qoi
	 */
	public static QuantityOfInterest getNewQoIWithParent(IDaoManager daoManager, QuantityOfInterest parent) {

		assertNotNull(daoManager);

		// create qoi
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setSymbol("QOI"); //$NON-NLS-1$
		if (parent != null && parent.getModel() != null) {
			qoi.setModel(parent.getModel());
		} else {
			qoi.setModel(getNewModel(daoManager));
		}
		if (parent != null) {
			qoi.setParent(parent);
		}
		qoi.setCreationDate(new Date());
		try {
			qoi = daoManager.getRepository(IQuantityOfInterestRepository.class).create(qoi);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(qoi);

		return qoi;
	}

	/**
	 * @param daoManager the dao manager
	 * @param model      the model to associate
	 * @return a new generated qoi planning parameter
	 */
	public static QoIPlanningParam getNewQoIPlanningParam(IDaoManager daoManager, Model model) {

		assertNotNull(daoManager);

		// create qoi planning param
		QoIPlanningParam qoiPlanningParam = new QoIPlanningParam();
		if (model != null) {
			qoiPlanningParam.setModel(model);
		} else {
			qoiPlanningParam.setModel(getNewModel(daoManager));
		}
		qoiPlanningParam.setName("ParamQOI"); //$NON-NLS-1$
		qoiPlanningParam.setRequired(YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE);
		qoiPlanningParam.setLevel("0"); //$NON-NLS-1$
		qoiPlanningParam.setType("ParamType"); //$NON-NLS-1$
		try {
			qoiPlanningParam = daoManager.getRepository(IQoIPlanningParamRepository.class).create(qoiPlanningParam);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(qoiPlanningParam);

		return qoiPlanningParam;
	}

	/**
	 * @param daoManager the dao manager
	 * @param param      the param to associate
	 * @param qoi        the qoi to associate
	 * @param user       the creation user to associate
	 * @return a new generated qoi planning value
	 */
	public static QoIPlanningValue getNewQoIPlanningValue(IDaoManager daoManager, QoIPlanningParam param,
			QuantityOfInterest qoi, User user) {

		assertNotNull(daoManager);

		// create qoi planning value
		QoIPlanningValue qoiPlanningValue = new QoIPlanningValue();
		if (param != null) {
			qoiPlanningValue.setParameter(param);
		} else {
			qoiPlanningValue.setParameter(getNewQoIPlanningParam(daoManager, null));
		}
		if (qoi != null) {
			qoiPlanningValue.setQoi(qoi);
		} else {
			qoiPlanningValue.setQoi(getNewQoI(daoManager, (Model) null));
		}
		if (user != null) {
			qoiPlanningValue.setUserCreation(user);
		} else {
			qoiPlanningValue.setUserCreation(getNewUser(daoManager));
		}
		qoiPlanningValue.setDateCreation(new Date());
		qoiPlanningValue.setValue("Value"); //$NON-NLS-1$
		try {
			qoiPlanningValue = daoManager.getRepository(IQoIPlanningValueRepository.class).create(qoiPlanningValue);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(qoiPlanningValue);

		return qoiPlanningValue;
	}

	/**
	 * @param daoManager the dao manager
	 * @param qoi        the qoi to associate
	 * @param user       the user creation to set
	 * @return a new generated qoi
	 */
	public static QoIHeader getNewQoIHeader(IDaoManager daoManager, QuantityOfInterest qoi, User user) {

		assertNotNull(daoManager);

		// create qoi
		QoIHeader qoiHeader = new QoIHeader();
		qoiHeader.setName("QOI"); //$NON-NLS-1$
		if (qoi != null) {
			qoiHeader.setQoi(qoi);
		} else {
			qoiHeader.setQoi(getNewQoI(daoManager, (Model) null));
		}
		qoiHeader.setCreationDate(new Date());
		if (user == null) {
			qoiHeader.setUserCreation(getNewUser(daoManager));
		} else {
			qoiHeader.setUserCreation(user);
		}
		try {
			qoiHeader = daoManager.getRepository(IQoIHeaderRepository.class).create(qoiHeader);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(qoiHeader);

		return qoiHeader;
	}

	/**
	 * @param <C>              the generic select value type
	 * @param <P>              the generic parameter type
	 * @param daoManager       the dao manager
	 * @param classSelectValue the generic select value class
	 * @param parameter        the parameter to associate
	 * @param classRepository  the repository class
	 * @return a new generated generic select value
	 */
	public static <C extends GenericParameterSelectValue<P>, P extends GenericParameter<P>> C getNewGenericSelectValue(
			IDaoManager daoManager, Class<C> classSelectValue, P parameter,
			Class<? extends ICRUDRepository<C, ?>> classRepository) {

		assertNotNull(daoManager);

		// create select value
		C selectValue = null;
		try {
			selectValue = classSelectValue.newInstance();
			selectValue.setParameter(parameter);
			selectValue.setName("ParamQOI"); //$NON-NLS-1$
		} catch (InstantiationException | IllegalAccessException e) {
			fail(e.getMessage());
		}

		try {
			selectValue = daoManager.getRepository(classRepository).create(selectValue);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(selectValue);

		return selectValue;
	}

	/**
	 * @param <C>             the generic select value type
	 * @param <P>             the generic parameter type
	 * @param daoManager      the dao manager
	 * @param classConstraint the generic select value class
	 * @param parameter       the parameter to associate
	 * @param classRepository the repository class
	 * @return a new generated generic constraint
	 */
	public static <C extends GenericParameterConstraint<P>, P extends GenericParameter<P>> C getNewGenericConstraint(
			IDaoManager daoManager, Class<C> classConstraint, P parameter,
			Class<? extends ICRUDRepository<C, ?>> classRepository) {

		assertNotNull(daoManager);

		// create constraint
		C constraint = null;
		try {
			constraint = classConstraint.newInstance();
			constraint.setParameter(parameter);
			constraint.setRule("1.0"); //$NON-NLS-1$
		} catch (InstantiationException | IllegalAccessException e) {
			fail(e.getMessage());
		}

		try {
			constraint = daoManager.getRepository(classRepository).create(constraint);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(constraint);

		return constraint;
	}

	/**
	 * @param daoManager the dao manager
	 * @param qoi        the qoi to associate
	 * @return a new generated phenomenon
	 */
	public static PhenomenonGroup getNewPhenomenonGroup(IDaoManager daoManager, QuantityOfInterest qoi) {

		assertNotNull(daoManager);

		// create phenomenon group
		PhenomenonGroup phenomenonGroup = new PhenomenonGroup();
		if (qoi != null) {
			phenomenonGroup.setQoi(qoi);
		} else {
			phenomenonGroup.setQoi(getNewQoI(daoManager, (Model) null));
		}
		phenomenonGroup.setIdLabel("IDLABEL"); //$NON-NLS-1$
		phenomenonGroup.setName("NAME"); //$NON-NLS-1$
		try {
			phenomenonGroup = daoManager.getRepository(IPhenomenonGroupRepository.class).create(phenomenonGroup);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(phenomenonGroup);

		return phenomenonGroup;
	}

	/**
	 * @param daoManager      the dao manager
	 * @param phenomenonGroup the phenomenon group to associate
	 * @return a new generated phenomenon
	 */
	public static Phenomenon getNewPhenomenon(IDaoManager daoManager, PhenomenonGroup phenomenonGroup) {
		return getNewPhenomenon(daoManager, phenomenonGroup, null);
	}

	/**
	 * @param daoManager      the dao manager
	 * @param phenomenonGroup the phenomenon group to associate
	 * @param importance      the phenomenon importance
	 * @return a new generated phenomenon
	 */
	public static Phenomenon getNewPhenomenon(IDaoManager daoManager, PhenomenonGroup phenomenonGroup,
			String importance) {

		assertNotNull(daoManager);

		// create phenomenon
		Phenomenon phenomenon = new Phenomenon();
		if (phenomenonGroup != null) {
			phenomenon.setPhenomenonGroup(phenomenonGroup);
		} else {
			phenomenon.setPhenomenonGroup(getNewPhenomenonGroup(daoManager, null));
		}
		phenomenon.setIdLabel("IDLABEL"); //$NON-NLS-1$
		phenomenon.setName("NAME"); //$NON-NLS-1$
		if (importance != null) {
			phenomenon.setImportance(importance);
		}
		try {
			phenomenon = daoManager.getRepository(IPhenomenonRepository.class).create(phenomenon);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(phenomenon);

		return phenomenon;
	}

	/**
	 * @param daoManager the dao manager
	 * @param phenomenon the phenomenon to associate
	 * @return a new generated criterion
	 */
	public static Criterion getNewCriterion(IDaoManager daoManager, Phenomenon phenomenon) {
		return getNewCriterion(daoManager, phenomenon, null, null, null);
	}

	/**
	 * @param daoManager the dao manager
	 * @param phenomenon the phenomenon to associate
	 * @param name       the criterion name
	 * @param type       the criterion type
	 * @param value      the criterion value
	 * @return a new generated criterion
	 */
	public static Criterion getNewCriterion(IDaoManager daoManager, Phenomenon phenomenon, String name, String type,
			String value) {

		assertNotNull(daoManager);

		// create criterion
		Criterion criterion = new Criterion();
		if (phenomenon != null) {
			criterion.setPhenomenon(phenomenon);
		} else {
			criterion.setPhenomenon(getNewPhenomenon(daoManager, null));
		}
		if (name != null) {
			criterion.setName(name);
		} else {
			criterion.setName("NAME"); //$NON-NLS-1$
		}
		if (name != null) {
			criterion.setType(type);
		} else {
			criterion.setType("TYPE"); //$NON-NLS-1$
		}
		if (name != null) {
			criterion.setValue(value);
		} else {
			criterion.setValue("VALUE"); //$NON-NLS-1$
		}
		try {
			criterion = daoManager.getRepository(ICriterionRepository.class).create(criterion);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(criterion);

		return criterion;
	}

	/**
	 * @param daoManager the dao manager
	 * @return a new generated user
	 */
	public static User getNewUser(IDaoManager daoManager) {

		assertNotNull(daoManager);

		// create user
		User user = new User();
		user.setUserID("USERID"); //$NON-NLS-1$
		try {
			user = daoManager.getRepository(IUserRepository.class).create(user);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(user);

		return user;
	}

	/**
	 * Gets the new user.
	 *
	 * @param daoManager the dao manager
	 * @param userID     the user ID
	 * @return a new generated user
	 */
	public static User getNewUser(IDaoManager daoManager, String userID) {

		assertNotNull(daoManager);

		// create user
		User user = new User();
		user.setUserID(userID);
		try {
			user = daoManager.getRepository(IUserRepository.class).create(user);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(user);

		return user;
	}

	/**
	 * @param daoManager the dao manager
	 * @return a new generated role
	 */
	public static Role getNewRole(IDaoManager daoManager) {

		assertNotNull(daoManager);

		// create role
		Role role = new Role();
		role.setName("ROLE"); //$NON-NLS-1$
		try {
			role = daoManager.getRepository(IRoleRepository.class).create(role);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(role);

		return role;
	}

	/**
	 * @param daoManager the dao manager
	 * @param model      the model to associate
	 * @return a new generated pcmm element
	 */
	public static PCMMElement getNewPCMMElement(IDaoManager daoManager, Model model) {

		assertNotNull(daoManager);

		// create pcmm element
		PCMMElement elt = new PCMMElement();
		if (model != null) {
			elt.setModel(model);
		} else {
			elt.setModel(getNewModel(daoManager));
		}
		elt.setName("ELEMENT"); //$NON-NLS-1$
		elt.setAbbreviation("ABBREV"); //$NON-NLS-1$
		try {
			elt = daoManager.getRepository(IPCMMElementRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager the dao manager
	 * @param element    the pcmm element to associate
	 * @return a new generated pcmm subelement
	 */
	public static PCMMSubelement getNewPCMMSubelement(IDaoManager daoManager, PCMMElement element) {

		assertNotNull(daoManager);

		PCMMElement newElement = element;
		if (element == null) {
			newElement = getNewPCMMElement(daoManager, null);
		}

		// create pcmm subelement
		PCMMSubelement elt = new PCMMSubelement();
		elt.setCode("CODE"); //$NON-NLS-1$
		elt.setName("SUBELEMENT"); //$NON-NLS-1$
		elt.setElement(newElement);
		try {
			elt = daoManager.getRepository(IPCMMSubelementRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		// refresh
		daoManager.getRepository(IPCMMElementRepository.class).refresh(newElement);

		return elt;
	}

	/**
	 * @param daoManager the dao manager
	 * @return a new generated pcmm level color
	 */
	public static PCMMLevelColor getNewPCMMLevelColor(IDaoManager daoManager) {

		assertNotNull(daoManager);

		// create pcmm level Color
		PCMMLevelColor level = new PCMMLevelColor();
		level.setName("LEVEL 1"); //$NON-NLS-1$
		level.setFixedColor("125,125,125"); //$NON-NLS-1$
		level.setCode(1);

		try {
			level = daoManager.getRepository(IPCMMLevelColorRepository.class).create(level);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(level);

		return level;
	}

	/**
	 * @param daoManager the dao manager
	 * @param phase      the pcmm phase
	 * @return a new generated pcmm option
	 */
	public static PCMMOption getNewPCMMOption(IDaoManager daoManager, PCMMPhase phase) {

		assertNotNull(daoManager);

		// create pcmm option
		PCMMOption option = new PCMMOption();
		option.setPhase(phase);
		if (option.getPhase() == null) {
			option.setPhase(PCMMPhase.ASSESS);
		}

		try {
			option = daoManager.getRepository(IPCMMOptionRepository.class).create(option);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(option);

		return option;
	}

	/**
	 * @param daoManager the dao manager
	 * @param assessable the pcmm element or subelement to associate
	 * @param code       the level code
	 * @return a new generated pcmm level
	 */
	public static PCMMLevel getNewPCMMLevel(IDaoManager daoManager, IAssessable assessable, Integer code) {

		assertNotNull(daoManager);

		// create pcmm level
		PCMMLevel level = new PCMMLevel();
		level.setName("LEVEL 1"); //$NON-NLS-1$
		level.setCode(code);
		if (level.getCode() == null) {
			level.setCode(1);
		}
		if (assessable instanceof PCMMElement) {
			level.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			level.setSubelement((PCMMSubelement) assessable);
		}
		try {
			level = daoManager.getRepository(IPCMMLevelRepository.class).create(level);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(level);

		return level;
	}

	/**
	 * @param daoManager the dao manager
	 * @param level      the pcmm level to associate
	 * @return a new generated pcmm level descriptor
	 */
	public static PCMMLevelDescriptor getNewPCMMLevelDescriptor(IDaoManager daoManager, PCMMLevel level) {

		assertNotNull(daoManager);

		// create pcmm level descriptor
		PCMMLevelDescriptor levelDesc = new PCMMLevelDescriptor();
		levelDesc.setName("LEVEL 1"); //$NON-NLS-1$
		if (level != null) {
			levelDesc.setLevel(level);
		} else {
			levelDesc.setLevel(getNewPCMMLevel(daoManager, null, null));
		}
		try {
			levelDesc = daoManager.getRepository(IPCMMLevelDescRepository.class).create(levelDesc);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(levelDesc);

		return levelDesc;
	}

	/**
	 * @param daoManager   the dao manager
	 * @param userCreation the user to associate
	 * @return a new generated pcmm level
	 */
	public static Tag getNewTag(IDaoManager daoManager, User userCreation) {

		assertNotNull(daoManager);

		// create pcmm level
		Tag tag = new Tag();
		tag.setName("LEVEL 1"); //$NON-NLS-1$
		tag.setDateTag(new Date());
		if (userCreation != null) {
			tag.setUserCreation(userCreation);
		} else {
			tag.setUserCreation(getNewUser(daoManager));
		}
		try {
			tag = daoManager.getRepository(ITagRepository.class).create(tag);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(tag);

		return tag;
	}

	/**
	 * @param daoManager the dao manager
	 * @param role       the creation role
	 * @param user       the creation user
	 * @param assessable the pcmm element or subelement
	 * @param level      the pcmm level to set
	 * @return a new generated pcmm assessment
	 */
	public static PCMMAssessment getNewPCMMAssessment(IDaoManager daoManager, Role role, User user,
			IAssessable assessable, PCMMLevel level) {

		assertNotNull(daoManager);

		// create pcmm assessment
		PCMMAssessment elt = new PCMMAssessment();
		if (role != null) {
			elt.setRoleCreation(role);
		} else {
			elt.setRoleCreation(getNewRole(daoManager));
		}
		if (user != null) {
			elt.setUserCreation(user);
		} else {
			elt.setUserCreation(getNewUser(daoManager));
		}
		elt.setLevel(level);
		elt.setDateCreation(new Date());
		if (assessable instanceof PCMMElement) {
			elt.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			elt.setSubelement((PCMMSubelement) assessable);
		}
		try {
			elt = daoManager.getRepository(IPCMMAssessmentRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager the dao manager
	 * @param role       the creation role
	 * @param user       the creation user
	 * @param assessable the pcmm element or subelement
	 * @return a new generated pcmm evidence
	 */
	public static PCMMEvidence getNewPCMMEvidence(IDaoManager daoManager, Role role, User user,
			IAssessable assessable) {
		return getNewPCMMEvidence(daoManager, role, user, assessable, (String) null);
	}

	/**
	 * @param daoManager the dao manager
	 * @param role       the creation role
	 * @param user       the creation user
	 * @param assessable the pcmm element or subelement
	 * @param filePath   the evidence file path
	 * @return a new generated pcmm evidence
	 */
	public static PCMMEvidence getNewPCMMEvidence(IDaoManager daoManager, Role role, User user, IAssessable assessable,
			IFile filePath) {

		assertNotNull(daoManager);

		// create pcmm evidence
		PCMMEvidence elt = new PCMMEvidence();
		if (role != null) {
			elt.setRoleCreation(role);
		} else {
			elt.setRoleCreation(getNewRole(daoManager));
		}
		if (user != null) {
			elt.setUserCreation(user);
		} else {
			elt.setUserCreation(getNewUser(daoManager));
		}
		elt.setDateCreation(new Date());
		if (assessable instanceof PCMMElement) {
			elt.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			elt.setSubelement((PCMMSubelement) assessable);
		}
		if (filePath != null) {
			elt.setFilePath(filePath.getFullPath().toString());
		}
		if (elt.getValue() == null) {
			elt.setURL("http://default.com"); //$NON-NLS-1$
		}
		try {
			elt = daoManager.getRepository(IPCMMEvidenceRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager the dao manager
	 * @param role       the creation role
	 * @param user       the creation user
	 * @param assessable the pcmm element or subelement
	 * @param url        the evidence url to set
	 * @return a new generated pcmm evidence
	 */
	public static PCMMEvidence getNewPCMMEvidence(IDaoManager daoManager, Role role, User user, IAssessable assessable,
			String url) {

		assertNotNull(daoManager);

		// create pcmm evidence
		PCMMEvidence elt = new PCMMEvidence();
		if (role != null) {
			elt.setRoleCreation(role);
		} else {
			elt.setRoleCreation(getNewRole(daoManager));
		}
		if (user != null) {
			elt.setUserCreation(user);
		} else {
			elt.setUserCreation(getNewUser(daoManager));
		}
		elt.setDateCreation(new Date());
		if (assessable instanceof PCMMElement) {
			elt.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			elt.setSubelement((PCMMSubelement) assessable);
		}
		if (url != null) {
			elt.setURL(url);
		}
		if (elt.getValue() == null) {
			elt.setURL("http://default.com"); //$NON-NLS-1$
		}
		try {
			elt = daoManager.getRepository(IPCMMEvidenceRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * Gets the new PCMM evidence.
	 *
	 * @param daoManager the dao manager
	 * @param role       the role
	 * @param user       the user
	 * @param assessable the assessable
	 * @param url        the url
	 * @param tag        the tag
	 * @return the new PCMM evidence
	 */
	public static PCMMEvidence getNewPCMMEvidence(IDaoManager daoManager, Role role, User user, IAssessable assessable,
			String url, Tag tag) {

		assertNotNull(daoManager);

		// create pcmm evidence
		PCMMEvidence elt = new PCMMEvidence();
		if (role != null) {
			elt.setRoleCreation(role);
		} else {
			elt.setRoleCreation(getNewRole(daoManager));
		}
		if (user != null) {
			elt.setUserCreation(user);
		} else {
			elt.setUserCreation(getNewUser(daoManager));
		}
		elt.setDateCreation(new Date());
		if (assessable instanceof PCMMElement) {
			elt.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			elt.setSubelement((PCMMSubelement) assessable);
		}
		if (url != null) {
			elt.setURL(url);
		}
		if (elt.getValue() == null) {
			elt.setURL("http://default.com"); //$NON-NLS-1$
		}

		if (tag != null) {
			elt.setTag(tag);
		}

		try {
			elt = daoManager.getRepository(IPCMMEvidenceRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager the dao manager
	 * @param assessable the pcmm element or subelement
	 * @return a new generated pcmm planning question
	 */
	public static PCMMPlanningQuestion getNewPCMMPlanningQuestion(IDaoManager daoManager, IAssessable assessable) {

		assertNotNull(daoManager);

		// create pcmm planning question
		PCMMPlanningQuestion question = new PCMMPlanningQuestion();

		if (assessable instanceof PCMMElement) {
			question.setElement((PCMMElement) assessable);
			question.setModel(((PCMMElement) assessable).getModel());
		} else if (assessable instanceof PCMMSubelement) {
			question.setSubelement((PCMMSubelement) assessable);
			question.setModel(((PCMMSubelement) assessable).getElement().getModel());
		}
		question.setRequired(YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE);
		question.setName("Question?"); //$NON-NLS-1$
		question.setType("RichText"); //$NON-NLS-1$
		if (question.getModel() == null) {
			question.setModel(getNewModel(daoManager));
		}

		try {
			question = daoManager.getRepository(IPCMMPlanningQuestionRepository.class).create(question);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(question);

		return question;
	}

	/**
	 * @param daoManager the dao manager
	 * @param question   the pcmm planning question to associate
	 * @param user       the creation user
	 * @param tag        the tag to associate
	 * @return a new generated pcmm planning question value
	 */
	public static PCMMPlanningQuestionValue getNewPCMMPlanningQuestionValue(IDaoManager daoManager,
			PCMMPlanningQuestion question, User user, Tag tag) {

		assertNotNull(daoManager);

		// create pcmm planning question value
		PCMMPlanningQuestionValue value = new PCMMPlanningQuestionValue();

		value.setDateCreation(new Date());
		value.setParameter(question);
		if (value.getParameter() == null) {
			value.setParameter(getNewPCMMPlanningQuestion(daoManager, null));
		}
		value.setTag(tag);
		value.setUserCreation(user);
		if (value.getUserCreation() == null) {
			value.setUserCreation(getNewUser(daoManager));
		}
		value.setValue("Question answer"); //$NON-NLS-1$

		try {
			value = daoManager.getRepository(IPCMMPlanningQuestionValueRepository.class).create(value);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(value);

		return value;
	}

	/**
	 * @param daoManager the dao manager
	 * @param model      the model
	 * @return a new generated pcmm planning parameter
	 */
	public static PCMMPlanningParam getNewPCMMPlanningParam(IDaoManager daoManager, Model model) {
		return getNewPCMMPlanningParam(daoManager, model, null);
	}

	/**
	 * @param daoManager the dao manager
	 * @param model      the model
	 * @param parent     the parent to associate
	 * @return a new generated pcmm planning parameter
	 */
	public static PCMMPlanningParam getNewPCMMPlanningParam(IDaoManager daoManager, Model model,
			PCMMPlanningParam parent) {

		// create pcmm planning parameter
		PCMMPlanningParam parameter = new PCMMPlanningParam();

		parameter.setRequired(YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE);
		parameter.setModel(model);
		parameter.setName("PARAM"); //$NON-NLS-1$
		parameter.setType("TYPE PARAM"); //$NON-NLS-1$
		if (parameter.getModel() == null && daoManager != null) {
			parameter.setModel(getNewModel(daoManager));
		}
		parameter.setParent(parent);

		if (daoManager != null) {
			try {
				parameter = daoManager.getRepository(IPCMMPlanningParamRepository.class).create(parameter);
			} catch (CredibilityException e) {
				fail(e.getMessage());
			}
			assertNotNull(parameter);
		}

		return parameter;
	}

	/**
	 * @param daoManager the dao manager
	 * @param param      the param to associate
	 * @return a new generated pcmm planning select value
	 */
	public static PCMMPlanningSelectValue getNewPCMMPlanningSelectValue(IDaoManager daoManager,
			PCMMPlanningParam param) {

		// create pcmm planning select value
		PCMMPlanningSelectValue select = new PCMMPlanningSelectValue();

		select.setName("SELECTVALUE"); //$NON-NLS-1$
		select.setParameter(param);
		if (select.getParameter() == null && daoManager != null) {
			select.setParameter(getNewPCMMPlanningParam(daoManager, null));
		}

		if (daoManager != null) {
			try {
				select = daoManager.getRepository(IPCMMPlanningSelectValueRepository.class).create(select);
			} catch (CredibilityException e) {
				fail(e.getMessage());
			}
			assertNotNull(select);
		}

		return select;
	}

	/**
	 * @param daoManager the dao manager
	 * @param param      the pcmm planning question to associate
	 * @param assessable the pcmm element or subelement
	 * @param user       the creation user
	 * @param tag        the tag to associate
	 * @return a new generated pcmm planning value
	 */
	public static PCMMPlanningValue getNewPCMMPlanningValue(IDaoManager daoManager, PCMMPlanningParam param,
			IAssessable assessable, User user, Tag tag) {

		assertNotNull(daoManager);

		// create pcmm planning value
		PCMMPlanningValue value = new PCMMPlanningValue();

		value.setDateCreation(new Date());
		value.setParameter(param);
		if (value.getParameter() == null) {
			value.setParameter(getNewPCMMPlanningParam(daoManager, null));
		}
		if (assessable instanceof PCMMElement) {
			value.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			value.setSubelement((PCMMSubelement) assessable);
		}
		value.setTag(tag);
		value.setUserCreation(user);
		if (value.getUserCreation() == null) {
			value.setUserCreation(getNewUser(daoManager));
		}
		value.setValue("Question answer"); //$NON-NLS-1$

		try {
			value = daoManager.getRepository(IPCMMPlanningValueRepository.class).create(value);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(value);

		return value;
	}

	/**
	 * @param daoManager the dao manager
	 * @param param      the pcmm planning question to associate
	 * @param assessable the pcmm element or subelement
	 * @param user       the creation user
	 * @param tag        the tag to associate
	 * @return a new generated pcmm planning table item
	 */
	public static PCMMPlanningTableItem getNewPCMMPlanningTableItem(IDaoManager daoManager, PCMMPlanningParam param,
			IAssessable assessable, User user, Tag tag) {

		assertNotNull(daoManager);

		// create pcmm planning table item
		PCMMPlanningTableItem value = new PCMMPlanningTableItem();

		value.setDateCreation(new Date());
		value.setParameter(param);
		if (value.getParameter() == null) {
			value.setParameter(getNewPCMMPlanningParam(daoManager, null));
		}
		if (assessable instanceof PCMMElement) {
			value.setElement((PCMMElement) assessable);
		} else if (assessable instanceof PCMMSubelement) {
			value.setSubelement((PCMMSubelement) assessable);
		}
		value.setTag(tag);
		value.setUserCreation(user);
		if (value.getUserCreation() == null) {
			value.setUserCreation(getNewUser(daoManager));
		}
		value.setValue("Question answer"); //$NON-NLS-1$

		try {
			value = daoManager.getRepository(IPCMMPlanningTableItemRepository.class).create(value);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(value);

		return value;
	}

	/**
	 * @param daoManager the dao manager
	 * @param param      the pcmm planning question to associate
	 * @param item       the item to associate
	 * @param user       the creation user
	 * @return a new generated pcmm planning table value
	 */
	public static PCMMPlanningTableValue getNewPCMMPlanningTableValue(IDaoManager daoManager, PCMMPlanningParam param,
			PCMMPlanningTableItem item, User user) {

		assertNotNull(daoManager);

		// create pcmm planning table item
		PCMMPlanningTableValue value = new PCMMPlanningTableValue();

		value.setDateCreation(new Date());
		value.setParameter(param);
		if (value.getParameter() == null) {
			value.setParameter(getNewPCMMPlanningParam(daoManager, null));
		}
		value.setItem(item);
		if (value.getItem() == null) {
			value.setItem(TestEntityFactory.getNewPCMMPlanningTableItem(daoManager, null, null, null, null));
		}
		value.setUserCreation(user);
		if (value.getUserCreation() == null) {
			value.setUserCreation(getNewUser(daoManager));
		}
		value.setValue("Question answer"); //$NON-NLS-1$

		try {
			value = daoManager.getRepository(IPCMMPlanningTableValueRepository.class).create(value);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(value);

		return value;
	}

	/**
	 * @param daoManager the dao manager
	 * @param model      the model to associate
	 * @return a new generated IntendedPurpose
	 */
	public static IntendedPurpose getNewIntendedPurpose(IDaoManager daoManager, Model model) {

		assertNotNull(daoManager);

		// create IntendedPurpose
		IntendedPurpose elt = new IntendedPurpose();
		elt.setModel(model);
		if (elt.getModel() == null) {
			elt.setModel(getNewModel(daoManager));
		}
		try {
			elt = daoManager.getRepository(IIntendedPurposeRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager  the dao manager
	 * @param uncertainty the uncertainty to associate
	 * @param parameter   the uncertainty parameter to associate
	 * @param user        the user to associate
	 * @return a new generated SystemRequirementValue
	 */
	public static UncertaintyValue getNewUncertaintyValue(IDaoManager daoManager, Uncertainty uncertainty,
			UncertaintyParam parameter, User user) {

		assertNotNull(daoManager);

		// create SystemRequirement Value
		UncertaintyValue elt = new UncertaintyValue();
		elt.setUserCreation(user);
		if (elt.getUserCreation() == null) {
			elt.setUserCreation(getNewUser(daoManager));
		}
		elt.setDateCreation(new Date());
		if (uncertainty == null) {
			uncertainty = getNewUncertainty(daoManager, null, null, user);
		}
		elt.setUncertainty(uncertainty);
		if (parameter == null) {
			parameter = getNewUncertaintyParam(daoManager, null, null);
		}
		elt.setParameter(parameter);
		try {
			elt = daoManager.getRepository(IUncertaintyValueRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager the dao manager
	 * @param model      the model to associate
	 * @param parent     the parent to associate
	 * @return a new generated uncertainty parameter
	 */
	public static UncertaintyParam getNewUncertaintyParam(IDaoManager daoManager, Model model,
			UncertaintyParam parent) {

		assertNotNull(daoManager);

		// create uncertainty parameter
		UncertaintyParam elt = new UncertaintyParam();
		elt.setModel(model);
		if (elt.getModel() == null) {
			elt.setModel(TestEntityFactory.getNewModel(daoManager));
		}
		elt.setParent(parent);
		elt.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		elt.setLevel("2"); //$NON-NLS-1$
		elt.setName("Parameter"); //$NON-NLS-1$
		elt.setType("Type"); //$NON-NLS-1$
		try {
			elt = daoManager.getRepository(IUncertaintyParamRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * Gets the new uncertainty.
	 *
	 * @param daoManager the dao manager
	 * @param model      the model
	 * @param group      the uncertainty group to associate
	 * @param user       the user to associate
	 * @return a new generated uncertainty
	 */
	public static Uncertainty getNewUncertainty(IDaoManager daoManager, Model model, Uncertainty group, User user) {

		assertNotNull(daoManager);

		// create uncertainty
		Uncertainty elt = new Uncertainty();
		if (model == null) {
			elt.setModel(getNewModel(daoManager));
		} else {
			elt.setModel(model);
		}
		elt.setParent(group);
		elt.setName("Name"); //$NON-NLS-1$
		elt.setCreationDate(new Date());
		elt.setUserCreation(user);
		if (elt.getUserCreation() == null) {
			elt.setUserCreation(getNewUser(daoManager));
		}
		try {
			elt = daoManager.getRepository(IUncertaintyRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager the dao manager
	 * @param model      the model to associate
	 * @param parent     the parent to associate
	 * @return a new generated SystemRequirement parameter
	 */
	public static SystemRequirementParam getNewSystemRequirementParam(IDaoManager daoManager, Model model,
			SystemRequirementParam parent) {

		assertNotNull(daoManager);

		// create SystemRequirement parameter
		SystemRequirementParam elt = new SystemRequirementParam();
		elt.setModel(model);
		if (elt.getModel() == null) {
			elt.setModel(TestEntityFactory.getNewModel(daoManager));
		}
		elt.setParent(parent);
		elt.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		elt.setLevel("2"); //$NON-NLS-1$
		elt.setName("Parameter"); //$NON-NLS-1$
		elt.setType("Type"); //$NON-NLS-1$
		try {
			elt = daoManager.getRepository(ISystemRequirementParamRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager the dao manager
	 * @param model      the model to associate
	 * @param parent     the SystemRequirement to associate
	 * @param user       the user to associate
	 * @return a new generated SystemRequirement
	 */
	public static SystemRequirement getNewSystemRequirement(IDaoManager daoManager, Model model,
			SystemRequirement parent, User user) {

		assertNotNull(daoManager);

		// create System Requirement
		SystemRequirement elt = new SystemRequirement();
		elt.setStatement("Statement A" + MathTools.getRandomInt(10000)); //$NON-NLS-1$
		elt.setParent(parent);
		elt.setUserCreation(user);
		if (elt.getUserCreation() == null) {
			elt.setUserCreation(getNewUser(daoManager));
		}
		elt.setCreationDate(new Date());
		elt.setModel(model);
		if (elt.getModel() == null) {
			elt.setModel(getNewModel(daoManager));
		}
		try {
			elt = daoManager.getRepository(ISystemRequirementRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager     the dao manager
	 * @param sysRequirement the SystemRequirement to associate
	 * @param parameter      the system requirement to associate
	 * @param user           the user to associate
	 * @return a new generated SystemRequirementValue
	 */
	public static SystemRequirementValue getNewSystemRequirementValue(IDaoManager daoManager,
			SystemRequirement sysRequirement, SystemRequirementParam parameter, User user) {

		assertNotNull(daoManager);

		// create SystemRequirement Value
		SystemRequirementValue elt = new SystemRequirementValue();
		elt.setUserCreation(user);
		if (elt.getUserCreation() == null) {
			elt.setUserCreation(getNewUser(daoManager));
		}
		elt.setDateCreation(new Date());
		if (sysRequirement == null) {
			sysRequirement = getNewSystemRequirement(daoManager, null, null, user);
		}
		elt.setRequirement(sysRequirement);
		if (parameter == null) {
			parameter = getNewSystemRequirementParam(daoManager, null, null);
		}
		elt.setParameter(parameter);
		try {
			elt = daoManager.getRepository(ISystemRequirementValueRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @return a new IFile in the workspace
	 */
	public static IFile getNewFile() {
		return getNewFile(null, null, ""); //$NON-NLS-1$
	}

	/**
	 * @param projectName the project name
	 * @param fileName    the file name
	 * @return a new IFile in the workspace
	 */
	public static IFile getNewFile(String projectName, String fileName) {
		return getNewFile(projectName, fileName, ""); //$NON-NLS-1$
	}

	/**
	 * @param projectName the project name
	 * @param fileName    the file name
	 * @param content     the file content
	 * @return a new IFile in the workspace
	 */
	public static IFile getNewFile(String projectName, String fileName, String content) {
		String projectNameDefault = "PROJECT"; //$NON-NLS-1$
		if (projectName != null) {
			projectNameDefault = projectName;
		}
		String fileNameDefault = "FILE.txt"; //$NON-NLS-1$
		if (fileName != null) {
			fileNameDefault = fileName;
		}

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectNameDefault);
		IFile file = null;
		try {
			if (!project.exists()) {
				project.create(new NullProgressMonitor());
			}
			project.open(new NullProgressMonitor());
			file = project.getFile(fileNameDefault);
			if (!file.exists()) {
				file.create(new StringInputStream("test"), true, new NullProgressMonitor()); //$NON-NLS-1$
			}
		} catch (CoreException e) {
			fail(e.getMessage());
		}
		return file;
	}

	/**
	 * @return an anonymous and stub generic parameter
	 */
	public static TestGenericParam getNewTestGenericParameter() {
		return new TestGenericParam();
	}

	/**
	 * Gets the new test generic param select value.
	 *
	 * @return the new test generic param select value
	 */
	public static TestGenericParamSelectValue getNewTestGenericParamSelectValue() {
		return new TestGenericParamSelectValue();
	}

	/**
	 * Gets the new test generic param select value.
	 *
	 * @param name the name
	 * @param parameter the parameter
	 * @return the new test generic param select value
	 */
	public static TestGenericParamSelectValue getNewTestGenericParamSelectValue(String name,
			TestGenericParam parameter) {
		TestGenericParamSelectValue selectValue = new TestGenericParamSelectValue();
		selectValue.setName(name);
		selectValue.setParameter(parameter);
		return selectValue;
	}

	/**
	 * @return an anonymous and stub generic value
	 */
	public static <P extends GenericParameter<P>> GenericValue<P, ?> getNewAnonymousGenericValue() {
		return new GenericValue<P, Object>() {
			private static final long serialVersionUID = 1L;

			private P parameter;

			@Override
			public Object copy() {
				return null;
			}

			@Override
			public P getParameter() {
				return parameter;
			}

			@Override
			public void setParameter(P parameter) {
				this.parameter = parameter;
			}
		};
	}

	/**
	 * @return an anonymous and stub generic parameter constraint
	 */
	public static <P extends GenericParameter<P>> GenericParameterConstraint<P> getNewAnonymousGenericParameterConstraint() {
		return new GenericParameterConstraint<P>() {
			private static final long serialVersionUID = 1L;

			private P parameter;

			@Override
			public P getParameter() {
				return parameter;
			}

			@Override
			public void setParameter(P parameter) {
				this.parameter = parameter;
			}
		};
	}

	/**
	 * @param daoManager the dao manager
	 * @param model      the model to associate
	 * @param parent     the parent to associate
	 * @return a new generated Decision parameter
	 */
	public static DecisionParam getNewDecisionParam(IDaoManager daoManager, Model model, DecisionParam parent) {

		assertNotNull(daoManager);

		// create Decision parameter
		DecisionParam elt = new DecisionParam();
		elt.setModel(model);
		if (elt.getModel() == null) {
			elt.setModel(TestEntityFactory.getNewModel(daoManager));
		}
		elt.setParent(parent);
		elt.setRequired(YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE);
		elt.setLevel("2"); //$NON-NLS-1$
		elt.setName("Parameter"); //$NON-NLS-1$
		elt.setType("Type"); //$NON-NLS-1$
		try {
			elt = daoManager.getRepository(IDecisionParamRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager the dao manager
	 * @param model      the model to associate
	 * @param parent     the Decision to associate
	 * @param user       the user to associate
	 * @return a new generated Decision
	 */
	public static Decision getNewDecision(IDaoManager daoManager, Model model, Decision parent, User user) {

		assertNotNull(daoManager);

		// create Decision
		Decision elt = new Decision();
		elt.setParent(parent);
		elt.setTitle("Decision " + MathTools.getRandomInt(10000)); //$NON-NLS-1$
		elt.setUserCreation(user);
		if (elt.getUserCreation() == null) {
			elt.setUserCreation(getNewUser(daoManager));
		}
		elt.setCreationDate(new Date());
		elt.setModel(model);
		if (elt.getModel() == null) {
			elt.setModel(getNewModel(daoManager));
		}
		try {
			elt = daoManager.getRepository(IDecisionRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager the dao manager
	 * @param parameter  the decision parameter to associate
	 * @param decision   the Decision to associate
	 * @param user       the user to associate
	 * @return a new generated Decision
	 */
	public static DecisionValue getNewDecisionValue(IDaoManager daoManager, Decision decision, DecisionParam parameter,
			User user) {

		assertNotNull(daoManager);

		// create Decision Value
		DecisionValue elt = new DecisionValue();
		elt.setUserCreation(user);
		if (elt.getUserCreation() == null) {
			elt.setUserCreation(getNewUser(daoManager));
		}
		elt.setDateCreation(new Date());
		if (decision == null) {
			decision = getNewDecision(daoManager, null, null, user);
		}
		elt.setDecision(decision);
		if (parameter == null) {
			parameter = getNewDecisionParam(daoManager, null, null);
		}
		elt.setParameter(parameter);
		try {
			elt = daoManager.getRepository(IDecisionValueRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager the dao manager
	 * @return a new generated ARGParameters entity
	 */
	public static ARGParameters getNewARGParameters(IDaoManager daoManager) {

		assertNotNull(daoManager);

		// create ARGParameters Value
		ARGParameters elt = new ARGParameters();
		elt.setBackendType("backendType"); //$NON-NLS-1$
		elt.setFilename("filename"); //$NON-NLS-1$
		elt.setNumber("number"); //$NON-NLS-1$
		elt.setOutput("output"); //$NON-NLS-1$
		elt.setParametersFilePath("parametersFilePath"); //$NON-NLS-1$
		elt.setReportType("reportType"); //$NON-NLS-1$
		elt.setStructureFilePath("structureFilePath"); //$NON-NLS-1$
		elt.setTitle("title"); //$NON-NLS-1$
		try {
			elt = daoManager.getRepository(IARGParametersRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}

	/**
	 * @param daoManager   the dao manager
	 * @param argParameter the arg Parameter to set
	 * @param qoi          the qoi to set
	 * @param tag          the tag to set
	 * @return a new generated ARGParametersQoIOption entity
	 */
	public static ARGParametersQoIOption getNewARGParametersQoIOption(IDaoManager daoManager,
			ARGParameters argParameter, QuantityOfInterest qoi, QuantityOfInterest tag) {

		assertNotNull(daoManager);

		// create ARGParametersQoIOption Value
		ARGParametersQoIOption elt = new ARGParametersQoIOption();
		if (argParameter == null) {
			argParameter = getNewARGParameters(daoManager);
		}
		elt.setArgParameter(argParameter);
		elt.setEnabled(true);
		if (qoi == null) {
			qoi = getNewQoI(daoManager, null);
		}
		elt.setQoi(qoi);
		elt.setTag(tag);
		try {
			elt = daoManager.getRepository(IARGParametersQoIOptionRepository.class).create(elt);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNotNull(elt);

		return elt;
	}
}
