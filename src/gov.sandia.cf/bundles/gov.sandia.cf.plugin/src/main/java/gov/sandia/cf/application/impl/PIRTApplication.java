/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.swt.graphics.RGB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IGlobalApplication;
import gov.sandia.cf.application.IImportApplication;
import gov.sandia.cf.application.IPIRTApplication;
import gov.sandia.cf.application.IQoIPlanningApplication;
import gov.sandia.cf.application.configuration.pirt.PIRTQuery;
import gov.sandia.cf.application.configuration.pirt.PIRTSpecification;
import gov.sandia.cf.dao.IARGParametersQoIOptionRepository;
import gov.sandia.cf.dao.IARGParametersRepository;
import gov.sandia.cf.dao.ICriterionRepository;
import gov.sandia.cf.dao.INativeQueryRepository;
import gov.sandia.cf.dao.IPIRTAdequacyColumnGuidelineRepository;
import gov.sandia.cf.dao.IPIRTAdequacyColumnRepository;
import gov.sandia.cf.dao.IPIRTAdequacyLevelGuidelineRepository;
import gov.sandia.cf.dao.IPIRTDescriptionHeaderRepository;
import gov.sandia.cf.dao.IPIRTLevelDifferenceColorRepository;
import gov.sandia.cf.dao.IPIRTLevelImportanceRepository;
import gov.sandia.cf.dao.IPhenomenonGroupRepository;
import gov.sandia.cf.dao.IPhenomenonRepository;
import gov.sandia.cf.dao.IQoIHeaderRepository;
import gov.sandia.cf.dao.IQoIPlanningValueRepository;
import gov.sandia.cf.dao.IQuantityOfInterestRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParametersQoIOption;
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
import gov.sandia.cf.model.QoIPlanningValue;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.IDTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage PIRT Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTApplication extends AApplication implements IPIRTApplication {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTApplication.class);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTSpecification loadPIRTConfiguration(Model model) {
		// Initialize
		PIRTSpecification pirtSpecification = null;

		// Check PIRT is enabled
		if (isPIRTEnabled()) {

			pirtSpecification = new PIRTSpecification();

			// set adequacy columns
			pirtSpecification.setAdequacyColumns(getPIRTAdequacyColumn());

			// set level difference colors
			pirtSpecification.setColors(getPIRTLevelDifferenceColor());

			// set description headers
			pirtSpecification.setHeaders(getPIRTDescriptionHeader());

			// set importance levels
			List<PIRTLevelImportance> pirtLevelImportance = getPIRTLevelImportance();
			if (pirtLevelImportance != null) {
				Map<String, PIRTLevelImportance> levels = new HashMap<>();
				for (PIRTLevelImportance level : pirtLevelImportance) {
					levels.put(level.getIdLabel(), level);
				}
				pirtSpecification.setLevels(levels);
			}

			// set guidelines
			pirtSpecification.setPirtAdequacyGuidelines(getPIRTAdequacyColumnGuideline());
		}

		return pirtSpecification;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameConfiguration(PIRTSpecification spec1, PIRTSpecification spec2) {

		if (spec1 == null) {
			return spec2 == null;
		} else if (spec2 == null) {
			return false;
		}
		// adequacy columns
		if (!getAppMgr().getService(IImportApplication.class).sameListContent(spec1.getColors(), spec2.getColors())) {
			return false;
		}

		// adequacy columns
		if (!getAppMgr().getService(IImportApplication.class).sameListContent(spec1.getColumns(), spec2.getColumns())) {
			return false;
		}

		// PIRT headers
		if (!getAppMgr().getService(IImportApplication.class).sameListContent(spec1.getHeaders(), spec2.getHeaders())) {
			return false;
		}

		// PIRT Guidelines
		if (!getAppMgr().getService(IImportApplication.class).sameListContent(spec1.getPirtAdequacyGuidelines(),
				spec2.getPirtAdequacyGuidelines())) {
			return false;
		}

		// PIRT Levels
		return getAppMgr().getService(IImportApplication.class).sameListContent(
				new ArrayList<>(spec1.getLevels().values()), new ArrayList<>(spec2.getLevels().values()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RGB getBackgroundColor(PIRTSpecification pirtConfiguration, PIRTLevelImportance expectedLevel,
			PIRTLevelImportance currentLevel) throws CredibilityException {

		if (pirtConfiguration == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_GETBGCOLOR_CONFNULL));
		}

		// set default color to the table background color
		RGB color = ColorTools.stringRGBToColor(ColorTools.DEFAULT_STRINGRGB_COLOR);

		// if there is a fixed color for the level, take it
		if (currentLevel != null && currentLevel.getFixedColor() != null) {
			color = ColorTools.stringRGBToColor(currentLevel.getFixedColor());
		} else if (expectedLevel != null && currentLevel != null) {
			// otherwise do the comparison with the expected color
			int differenceLevel = currentLevel.getLevel() - expectedLevel.getLevel();
			color = pirtConfiguration.getColor(differenceLevel);
		}

		return color;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuantityOfInterest resetQoI(QuantityOfInterest qoi) throws CredibilityException {

		if (qoi == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEQOI_QOINULL));
		} else if (qoi.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEQOI_IDNULL));
		}

		// refresh qoi
		refresh(qoi);

		// delete all phenomena groups
		if (qoi.getPhenomenonGroupList() != null) {
			for (PhenomenonGroup group : qoi.getPhenomenonGroupList()) {
				if (group != null) {
					deletePhenomenonGroup(group);
				}
			}

			// refresh qoi
			refresh(qoi);
		}

		return qoi;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuantityOfInterest tagQoI(QuantityOfInterest qoi, String tagDescription, User currentUser)
			throws CredibilityException {

		if (qoi == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_TAG_QOINULL));
		} else if (qoi.getModel() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_TAG_MODELNULL));
		}

		// Create a QoI copy
		QuantityOfInterest taggedQoi = qoi.copy();

		// Set parent
		taggedQoi.setParent(qoi);

		// Set QoI tagged fields
		Date currentDateTime = DateTools.getCurrentDate();
		String hashtag = DateTools.formatDate(currentDateTime, DateTools.DATE_TIME_FORMAT_HASH) + taggedQoi.hashCode();
		taggedQoi.setDescription(qoi.getDescription());
		taggedQoi.setModel(qoi.getModel());
		taggedQoi.setTag(hashtag);
		taggedQoi.setTagDate(currentDateTime);
		taggedQoi.setTagDescription(tagDescription);
		taggedQoi.setTagUserCreation(currentUser);

		// Copy QoI Planning values before save
		if (qoi.getQoiPlanningList() != null) {
			List<QoIPlanningValue> qoiPlanningListCopied = new ArrayList<>();
			for (QoIPlanningValue qoiPlanningValue : qoi.getQoiPlanningList()) {
				QoIPlanningValue qoiPlanningCopied = qoiPlanningValue.copy();
				qoiPlanningCopied.setQoi(taggedQoi);
				qoiPlanningListCopied.add(qoiPlanningCopied);
			}
			taggedQoi.setQoiPlanningList(qoiPlanningListCopied);
		}

		// Save
		taggedQoi = addQoI(taggedQoi, qoi.getUserCreation());

		// Copy QoI headers
		if (qoi.getQoiHeaderList() != null) {
			List<QoIHeader> qoiHeaderListCopied = new ArrayList<>();
			for (QoIHeader qoiHeader : qoi.getQoiHeaderList()) {
				QoIHeader qoiHeaderCopied = qoiHeader.copy();
				qoiHeaderCopied.setQoi(taggedQoi);
				qoiHeaderCopied = addQoIHeader(qoiHeaderCopied, qoiHeader.getUserCreation());
				qoiHeaderListCopied.add(qoiHeaderCopied);
			}
			taggedQoi.setQoiHeaderList(qoiHeaderListCopied);
		}

		// Copy phenomena groups
		if (qoi.getPhenomenonGroupList() != null) {
			List<PhenomenonGroup> groupListCopied = new ArrayList<>();
			for (PhenomenonGroup group : qoi.getPhenomenonGroupList()) {
				groupListCopied.add(copyPhenomenonGroup(group, taggedQoi));
			}
			taggedQoi.setPhenomenonGroupList(groupListCopied);
		}

		return taggedQoi;
	}

	/**
	 * @param group     the group to copy
	 * @param qoiParent the qoi to associate
	 * @return the phenomenon group copied
	 * @throws CredibilityException if an error occured
	 */
	private PhenomenonGroup copyPhenomenonGroup(PhenomenonGroup group, QuantityOfInterest qoiParent)
			throws CredibilityException {

		if (group != null) {
			PhenomenonGroup groupCopied = group.copy();
			groupCopied.setQoi(qoiParent);
			groupCopied = addPhenomenonGroup(groupCopied);

			// copy phenomenon
			if (group.getPhenomenonList() != null) {
				List<Phenomenon> phenomenonListCopied = new ArrayList<>();
				for (Phenomenon phenomenon : group.getPhenomenonList()) {
					phenomenonListCopied.add(copyPhenomenon(phenomenon, groupCopied));
				}
				groupCopied.setPhenomenonList(phenomenonListCopied);
			}

			return groupCopied;
		}
		return null;
	}

	/**
	 * @param phenomenon  the phenomenon to copy
	 * @param groupParent the group to associate
	 * @return the phenomenon copied
	 * @throws CredibilityException if an error occured
	 */
	private Phenomenon copyPhenomenon(Phenomenon phenomenon, PhenomenonGroup groupParent) throws CredibilityException {

		if (phenomenon != null) {

			Phenomenon phenomenonCopied = phenomenon.copy();
			phenomenonCopied.setPhenomenonGroup(groupParent);
			phenomenonCopied = addPhenomenon(phenomenonCopied);

			// copy criterion
			if (phenomenon.getCriterionList() != null) {
				List<Criterion> criterionListCopied = new ArrayList<>();
				for (Criterion criterion : phenomenon.getCriterionList()) {
					if (criterion != null) {
						Criterion criterionCopied = criterion.copy();
						criterionCopied.setPhenomenon(phenomenonCopied);
						criterionCopied = addCriterion(criterionCopied);
						criterionListCopied.add(criterionCopied);
					}
				}
				phenomenonCopied.setCriterionList(criterionListCopied);
			}

			return phenomenonCopied;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuantityOfInterest duplicateQoI(QuantityOfInterest qoi, QuantityOfInterest duplicatedQoi, User user)
			throws CredibilityException {

		// No Quantity of Interest given
		if (null == qoi) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DUPLICATEQOI_QOINULL));
		} else if (user == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDQOI_USERNULL));
		}

		// Hydrate Quantity of Interest
		if (null == duplicatedQoi) {
			duplicatedQoi = qoi.copy();
			duplicatedQoi.setSymbol(qoi.getSymbol() + " " + RscTools.getString(RscConst.MSG_COPYQOI_NAME_SUFFIX)); //$NON-NLS-1$
		}
		duplicatedQoi.setModel(qoi.getModel());
		duplicatedQoi.setCreationDate(DateTools.getCurrentDate());
		duplicatedQoi.setUserCreation(user);

		// Copy QoI Planning values before save
		if (qoi.getQoiPlanningList() != null) {
			List<QoIPlanningValue> qoiPlanningListCopied = new ArrayList<>();
			for (QoIPlanningValue qoiPlanningValue : qoi.getQoiPlanningList()) {
				QoIPlanningValue qoiPlanningCopied = qoiPlanningValue.copy();
				qoiPlanningCopied.setQoi(duplicatedQoi);
				qoiPlanningListCopied.add(qoiPlanningCopied);
			}
			duplicatedQoi.setQoiPlanningList(qoiPlanningListCopied);
		}

		// Save
		duplicatedQoi = addQoI(duplicatedQoi, user);

		// Copy qoi headers
		if (qoi.getQoiHeaderList() != null) {
			List<QoIHeader> qoiHeaderListCopied = new ArrayList<>();
			for (QoIHeader qoiHeader : qoi.getQoiHeaderList()) {
				QoIHeader qoiHeaderCopied = qoiHeader.copy();
				qoiHeaderCopied.setQoi(duplicatedQoi);
				qoiHeaderCopied = addQoIHeader(qoiHeaderCopied, user);
				qoiHeaderListCopied.add(qoiHeaderCopied);
			}
			duplicatedQoi.setQoiHeaderList(qoiHeaderListCopied);
		}

		// copy phenomena groups
		if (qoi.getPhenomenonGroupList() != null) {
			List<PhenomenonGroup> groupListCopied = new ArrayList<>();
			for (PhenomenonGroup group : qoi.getPhenomenonGroupList()) {
				groupListCopied.add(copyPhenomenonGroup(group, duplicatedQoi));
			}
			duplicatedQoi.setPhenomenonGroupList(groupListCopied);
		}

		return duplicatedQoi;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QuantityOfInterest> getQoIList(Model model) {

		List<QuantityOfInterest> findQuantityOfInterest = getDaoManager()
				.getRepository(IQuantityOfInterestRepository.class).findByModel(model);

		// sort qoi by id (creation order)
		if (findQuantityOfInterest != null) {
			findQuantityOfInterest.sort(Comparator.comparing(QuantityOfInterest::getId));
		}

		return findQuantityOfInterest;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QuantityOfInterest> getRootQoI(Model model) {

		List<QuantityOfInterest> findRootQuantityOfInterest = getDaoManager()
				.getRepository(IQuantityOfInterestRepository.class).findRootQuantityOfInterest(model);

		// sort qoi by id (creation order)
		if (findRootQuantityOfInterest != null) {
			findRootQuantityOfInterest.sort(Comparator.comparing(QuantityOfInterest::getId));
		}

		return findRootQuantityOfInterest;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuantityOfInterest getQoIById(Integer qoiId) throws CredibilityException {

		if (qoiId == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_GETQOIBYID_IDNULL));
		}
		return getDaoManager().getRepository(IQuantityOfInterestRepository.class).findById(qoiId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean existsQoISymbol(Integer[] id, String symbol) throws CredibilityException {

		if (symbol == null || symbol.isEmpty()) {
			return false;
		}

		return !getDaoManager().getRepository(IQuantityOfInterestRepository.class)
				.isUniqueExcept(QuantityOfInterest.Filter.SYMBOL, id, symbol);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuantityOfInterest addQoI(QuantityOfInterest qoi, User user) throws CredibilityException {

		if (qoi == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDQOI_QOINULL));
		} else if (qoi.getParent() == null && existsQoISymbol((Integer[]) null, qoi.getSymbol())) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDQOI_NAMEDUPLICATED));
		} else if (user == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDQOI_USERNULL));
		}

		// set the current date for creation date
		if (qoi.getCreationDate() == null) {
			Date currentDate = DateTools.getCurrentDate();
			qoi.setCreationDate(currentDate);
		}

		// set the creation user
		qoi.setUserCreation(user);

		// set the qoi name with the parent name
		if (qoi.getParent() != null) {
			qoi.setSymbol(qoi.getParent().getSymbol());
		}

		// Get list
		List<QoIPlanningValue> planningValues = qoi.getQoiPlanningList();
		qoi.setQoiPlanningList(new ArrayList<>());

		QuantityOfInterest qoiCreated = getDaoManager().getRepository(IQuantityOfInterestRepository.class).create(qoi);

		// add qoi planning
		if (planningValues != null) {
			for (QoIPlanningValue value : planningValues) {

				// set values
				value.setQoi(qoiCreated);
				value.setDateCreation(DateTools.getCurrentDate());
				value.setUserCreation(user);

				// create value
				QoIPlanningValue createdValue = getAppMgr().getDaoManager()
						.getRepository(IQoIPlanningValueRepository.class).create(value);
				qoi.getQoiPlanningList().add(createdValue);
			}
		}

		return qoiCreated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuantityOfInterest addQoI(QuantityOfInterest qoi, User user, List<PIRTDescriptionHeader> headers)
			throws CredibilityException {

		QuantityOfInterest newQoi = addQoI(qoi, user);

		// then create new qoiHeaders if configuration exists
		if (newQoi != null && headers != null && !headers.isEmpty()) {
			List<QoIHeader> qoiHeaderList = new ArrayList<>();
			for (PIRTDescriptionHeader header : headers) {

				// create new qoiHeader
				QoIHeader qoiHeader = new QoIHeader();
				qoiHeader.setName(header.getName());
				qoiHeader.setValue(RscTools.empty());
				qoiHeader.setQoi(newQoi);

				// persist qoiHeader
				QoIHeader qoiHeaderCreated = addQoIHeader(qoiHeader, user);

				// add to header list
				qoiHeaderList.add(qoiHeaderCreated);
			}

			// add qoi headers to new qoi
			newQoi.setQoiHeaderList(qoiHeaderList);
		}

		return newQoi;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QuantityOfInterest updateQoI(QuantityOfInterest qoi, User user) throws CredibilityException {

		if (qoi == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEQOI_QOINULL));
		} else if (qoi.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEQOI_IDNULL));
		}

		// check qoi name change in id and children tags
		List<Integer> ids = new ArrayList<>();
		QuantityOfInterest qoIById = getQoIById(qoi.getId());
		if (qoIById != null) {
			ids.add(qoIById.getId());
			if (qoIById.getChildren() != null) {
				qoIById.getChildren().forEach(tag -> {
					if (tag != null) {
						ids.add(tag.getId());
					}
				});
			}

			if (existsQoISymbol(ids.toArray(new Integer[] {}), qoi.getSymbol())) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEQOI_NAMEDUPLICATED));
			}
		} else {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEQOI_QOINOTFOUND));
		}

		// set the current date for update date
		if (qoi.getUpdateDate() == null) {
			Date currentDate = DateTools.getCurrentDate();
			qoi.setUpdateDate(currentDate);
		}

		// set the update user
		qoi.setUserUpdate(user);

		// Get list
		List<QoIPlanningValue> planningValues = qoi.getQoiPlanningList();
		qoi.setQoiPlanningList(new ArrayList<>());

		QuantityOfInterest qoiUpdated = getDaoManager().getRepository(IQuantityOfInterestRepository.class).update(qoi);

		// Update Planning
		if (planningValues != null) {
			for (QoIPlanningValue value : planningValues) {

				// set values
				value.setQoi(qoiUpdated);

				// create value
				QoIPlanningValue changedValue = getAppMgr().getService(IQoIPlanningApplication.class)
						.createOrUpdateQoIPlanningValue(value, user);
				qoi.getQoiPlanningList().add(changedValue);
			}
		}

		return qoiUpdated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteQoI(QuantityOfInterest qoi) throws CredibilityException {

		if (qoi == null || qoi.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEQOI_QOINULL));
		}

		// refresh before deletion
		getDaoManager().getRepository(IQuantityOfInterestRepository.class).refresh(qoi);

		// delete ARG Parameters QoI referenced
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(ARGParametersQoIOption.Filter.QOI, qoi);
		List<ARGParametersQoIOption> foundARGParamReferences = getDaoManager()
				.getRepository(IARGParametersQoIOptionRepository.class).findBy(filters);
		if (foundARGParamReferences != null) {
			foundARGParamReferences.forEach(opt -> {
				getDaoManager().getRepository(IARGParametersQoIOptionRepository.class).delete(opt);
				getDaoManager().getRepository(IARGParametersRepository.class).refresh(opt.getArgParameter());
			});
		}

		// delete ARG Parameters QoI tag referenced
		filters = new HashMap<>();
		filters.put(ARGParametersQoIOption.Filter.TAG, qoi);
		foundARGParamReferences = getDaoManager().getRepository(IARGParametersQoIOptionRepository.class)
				.findBy(filters);
		if (foundARGParamReferences != null) {
			foundARGParamReferences.forEach(opt -> {
				getDaoManager().getRepository(IARGParametersQoIOptionRepository.class).delete(opt);
				getDaoManager().getRepository(IARGParametersRepository.class).refresh(opt.getArgParameter());
			});
		}

		// REMOVE QoI - headers, children and planning values associated will be
		// automatically
		// deleted by cascade REMOVE
		getDaoManager().getRepository(IQuantityOfInterestRepository.class).delete(qoi);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QoIHeader> getQoIHeaders() {
		return getDaoManager().getRepository(IQoIHeaderRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QoIHeader addQoIHeader(QoIHeader qoiHeader, User user) throws CredibilityException {

		if (qoiHeader == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDQOIHEADER_QOIHEADERNULL));
		}

		// set the current date for creation date
		if (qoiHeader.getCreationDate() == null) {
			Date currentDate = DateTools.getCurrentDate();
			qoiHeader.setCreationDate(currentDate);
		}

		// set the creation user
		qoiHeader.setUserCreation(user);

		return getDaoManager().getRepository(IQoIHeaderRepository.class).create(qoiHeader);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QoIHeader updateQoIHeader(QoIHeader qoiHeader, User user) throws CredibilityException {

		if (qoiHeader == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEQOIHEADER_QOIHEADERNULL));
		} else if (qoiHeader.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEQOIHEADER_IDNULL));
		}

		// set the current date for creation date
		if (qoiHeader.getUpdateDate() == null) {
			Date currentDate = DateTools.getCurrentDate();
			qoiHeader.setUpdateDate(currentDate);
		}

		// set the update user
		qoiHeader.setUserUpdate(user);

		return getDaoManager().getRepository(IQoIHeaderRepository.class).update(qoiHeader);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteQoIHeader(QoIHeader qoiHeader) throws CredibilityException {

		if (qoiHeader == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEQOIHEADER_QOIHEADERNULL));
		} else if (qoiHeader.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEQOIHEADER_IDNULL));
		}

		getDaoManager().getRepository(IQoIHeaderRepository.class).delete(qoiHeader);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PhenomenonGroup> getPhenomenonGroups() {
		return getDaoManager().getRepository(IPhenomenonGroupRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PhenomenonGroup addPhenomenonGroup(PhenomenonGroup group) throws CredibilityException {
		if (group == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDPHENGROUP_GROUPNULL));
		}
		return getDaoManager().getRepository(IPhenomenonGroupRepository.class).create(group);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PhenomenonGroup updatePhenomenonGroup(PhenomenonGroup group) throws CredibilityException {

		if (group == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPHENGROUP_GROUPNULL));
		} else if (group.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPHENGROUP_IDNULL));
		}

		return getDaoManager().getRepository(IPhenomenonGroupRepository.class).update(group);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePhenomenonGroup(PhenomenonGroup group) throws CredibilityException {

		if (group == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPHENGROUP_GROUPNULL));
		} else if (group.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPHENGROUP_IDNULL));
		}

		// refresh before deletion
		getDaoManager().getRepository(IPhenomenonGroupRepository.class).refresh(group);

		// REMOVE phenomenon group - phenomena will be deleted by cascade REMOVE
		getDaoManager().getRepository(IPhenomenonGroupRepository.class).delete(group);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reorderPhenomenonGroups(PhenomenonGroup groupToMove, int newIndex) throws CredibilityException {

		logger.debug("Reorder phenomenon groups"); //$NON-NLS-1$

		int startPosition = IDTools.reverseGenerateAlphabeticIdRecursive(IDTools.ALPHABET.get(0));

		if (groupToMove == null || groupToMove.getQoi() == null || newIndex < startPosition) {
			return;
		}

		List<PhenomenonGroup> phenomenonGroups = getPhenomenonGroups().stream()
				.filter(g -> groupToMove.getQoi().equals(g.getQoi())).collect(Collectors.toList());

		if (phenomenonGroups == null) {
			return;
		}

		// construct data
		phenomenonGroups.sort(Comparator.comparing(PhenomenonGroup::getIdLabel));

		// reorder
		List<PhenomenonGroup> reorderedList = IDTools.reorderList(phenomenonGroups, groupToMove, newIndex);

		// set id for parents
		for (PhenomenonGroup group : reorderedList) {
			updatePhenomenonGroupId(group, IDTools.generateAlphabeticId(startPosition));
			startPosition++;
		}
	}

	/**
	 * @param groupToMove
	 * @param idLabel
	 * @throws CredibilityException
	 */
	private void updatePhenomenonGroupId(PhenomenonGroup groupToMove, String idLabel) throws CredibilityException {

		if (groupToMove == null || idLabel == null) {
			return;
		}

		// set id for parent
		groupToMove.setIdLabel(idLabel);
		updatePhenomenonGroup(groupToMove);
		refresh(groupToMove);

		List<Phenomenon> phenomenonList = groupToMove.getPhenomenonList();
		if (phenomenonList == null) {
			return;
		}

		// sort
		phenomenonList.sort(Comparator.comparing(Phenomenon::getIdLabel));

		// set id for children
		int index = 1;
		for (Phenomenon phenomenon : phenomenonList) {
			phenomenon.setIdLabel(idLabel + index);
			updatePhenomenon(phenomenon);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Phenomenon> getPhenomena() {
		return getDaoManager().getRepository(IPhenomenonRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Phenomenon addPhenomenon(Phenomenon phenomenon) throws CredibilityException {
		if (phenomenon == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDPHENOMENON_PHENOMENONNULL));
		}

		return getDaoManager().getRepository(IPhenomenonRepository.class).create(phenomenon);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Phenomenon updatePhenomenon(Phenomenon phenomenon) throws CredibilityException {

		if (phenomenon == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPHENOMENON_PHENOMENONNULL));
		} else if (phenomenon.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPHENOMENON_IDNULL));
		}

		return getDaoManager().getRepository(IPhenomenonRepository.class).update(phenomenon);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePhenomenon(Phenomenon phenomenon) throws CredibilityException {

		if (phenomenon == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPHENOMENON_PHENOMENONNULL));
		} else if (phenomenon.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPHENOMENON_IDNULL));
		}

		// refresh before deletion
		getDaoManager().getRepository(IPhenomenonRepository.class).delete(phenomenon);

		// REMOVE Phenomenon - criterion will be deleted by cascade REMOVE
		getDaoManager().getRepository(IPhenomenonRepository.class).delete(phenomenon);
	}

	@Override
	public void reorderPhenomena(Phenomenon phenomenonToMove, int newIndex) throws CredibilityException {

		logger.debug("Reorder phenomena"); //$NON-NLS-1$

		int startPosition = IDTools.reverseGenerateAlphabeticIdRecursive(IDTools.ALPHABET.get(0));

		if (phenomenonToMove == null || phenomenonToMove.getPhenomenonGroup() == null || newIndex < startPosition) {
			return;
		}

		List<Phenomenon> phenomenonList = getPhenomena().stream()
				.filter(p -> phenomenonToMove.getPhenomenonGroup().equals(p.getPhenomenonGroup()))
				.collect(Collectors.toList());

		if (phenomenonList == null) {
			return;
		}

		// construct data
		phenomenonList.sort(Comparator.comparing(Phenomenon::getIdLabel));

		// reorder
		List<Phenomenon> reorderedList = IDTools.reorderList(phenomenonList, phenomenonToMove, newIndex);

		// set id for parents
		int index = 1;
		final String groupIdLabel = phenomenonToMove.getPhenomenonGroup().getIdLabel();
		for (Phenomenon phenomenon : reorderedList) {
			phenomenon.setIdLabel(groupIdLabel + index);
			updatePhenomenon(phenomenon);
			index++;
		}

		// refresh group
		refresh(phenomenonToMove.getPhenomenonGroup());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Criterion> getCriterion() {
		return getDaoManager().getRepository(ICriterionRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Criterion addCriterion(Criterion criterion) throws CredibilityException {

		if (criterion == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDCRITERION_CRITERIONNULL));
		}

		return getDaoManager().getRepository(ICriterionRepository.class).create(criterion);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Criterion updateCriterion(Criterion criterion) throws CredibilityException {

		if (criterion == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATECRITERION_CRITERIONNULL));
		} else if (criterion.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATECRITERION_IDNULL));
		}

		Criterion criterionUpdated = getDaoManager().getRepository(ICriterionRepository.class).update(criterion);

		// refresh parent Phenomenon
		if (criterion.getPhenomenon() != null) {
			getDaoManager().getRepository(IPhenomenonRepository.class).refresh(criterionUpdated.getPhenomenon());
		}

		return criterionUpdated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteCriterion(Criterion criterion) throws CredibilityException {

		if (criterion == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETECRITERION_CRITERIONNULL));
		} else if (criterion.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETECRITERION_IDNULL));
		}

		getDaoManager().getRepository(ICriterionRepository.class).delete(criterion);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> executeQuery(PIRTQuery query, List<String> criteriaInputList) throws CredibilityException {
		// Initialize
		String stringQuery = query.getQuery();
		List<String> criticalList = query.getCriteriaList();
		IGlobalApplication globalApplication = getAppMgr().getService(IGlobalApplication.class);
		INativeQueryRepository nativeQueryRepo = getDaoManager().getNativeQueryRepository();

		// Check
		if (criticalList != null && !criticalList.isEmpty() && criteriaInputList != null
				&& !criteriaInputList.isEmpty()) {
			stringQuery = MessageFormat.format(stringQuery, criteriaInputList.toArray());
		}

		// Result
		return nativeQueryRepo.execute(stringQuery, globalApplication.loadModelClass(query.getResultType()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PIRTAdequacyColumnGuideline> getPIRTAdequacyColumnGuideline() {
		return getDaoManager().getRepository(IPIRTAdequacyColumnGuidelineRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTAdequacyColumnGuideline addPIRTAdequacyColumnGuideline(
			PIRTAdequacyColumnGuideline pirtAdequacyColumnGuideline) throws CredibilityException {

		if (pirtAdequacyColumnGuideline == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDPIRTADEQCOLUMNGUIDELINE_COLUMNNULL));
		}

		return getDaoManager().getRepository(IPIRTAdequacyColumnGuidelineRepository.class)
				.create(pirtAdequacyColumnGuideline);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTAdequacyColumnGuideline updatePIRTAdequacyColumnGuideline(
			PIRTAdequacyColumnGuideline pirtAdequacyColumnGuideline) throws CredibilityException {

		if (pirtAdequacyColumnGuideline == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTADEQCOLUMNGUIDELINE_COLUMNNULL));
		} else if (pirtAdequacyColumnGuideline.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTADEQCOLUMNGUIDELINE_IDNULL));
		}

		return getDaoManager().getRepository(IPIRTAdequacyColumnGuidelineRepository.class)
				.update(pirtAdequacyColumnGuideline);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllPIRTAdequacyColumnGuideline(List<PIRTAdequacyColumnGuideline> pirtGuidelines)
			throws CredibilityException {
		if (pirtGuidelines != null) {
			for (PIRTAdequacyColumnGuideline guideline : pirtGuidelines) {
				deletePIRTAdequacyColumnGuideline(guideline);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePIRTAdequacyColumnGuideline(PIRTAdequacyColumnGuideline pirtAdequacyColumnGuideline)
			throws CredibilityException {

		if (pirtAdequacyColumnGuideline == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PIRT_DELETEPIRTADEQCOLUMNGUIDELINE_COLUMNNULL));
		} else if (pirtAdequacyColumnGuideline.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPIRTADEQCOLUMNGUIDELINE_IDNULL));
		}

		getDaoManager().getRepository(IPIRTAdequacyColumnGuidelineRepository.class).delete(pirtAdequacyColumnGuideline);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PIRTAdequacyColumnLevelGuideline> getPIRTAdequacyColumnLevelGuideline() {
		return getDaoManager().getRepository(IPIRTAdequacyLevelGuidelineRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTAdequacyColumnLevelGuideline addPIRTAdequacyColumnLevelGuideline(
			PIRTAdequacyColumnLevelGuideline pirtAdequacyColumnGuideline) throws CredibilityException {

		if (pirtAdequacyColumnGuideline == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PIRT_ADDPIRTADEQCOLUMNLEVELGUIDELINE_COLUMNNULL));
		}

		return getDaoManager().getRepository(IPIRTAdequacyLevelGuidelineRepository.class)
				.create(pirtAdequacyColumnGuideline);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTAdequacyColumnLevelGuideline updatePIRTAdequacyColumnLevelGuideline(
			PIRTAdequacyColumnLevelGuideline pirtAdequacyColumnGuideline) throws CredibilityException {

		if (pirtAdequacyColumnGuideline == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTADEQCOLUMNLEVELGUIDELINE_COLUMNNULL));
		} else if (pirtAdequacyColumnGuideline.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTADEQCOLUMNLEVELGUIDELINE_IDNULL));
		}

		return getDaoManager().getRepository(IPIRTAdequacyLevelGuidelineRepository.class)
				.update(pirtAdequacyColumnGuideline);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePIRTAdequacyColumnLevelGuideline(PIRTAdequacyColumnLevelGuideline pirtAdequacyColumnGuideline)
			throws CredibilityException {

		if (pirtAdequacyColumnGuideline == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PIRT_DELETEPIRTADEQCOLUMNLEVELGUIDELINE_COLUMNNULL));
		} else if (pirtAdequacyColumnGuideline.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_PIRT_DELETEPIRTADEQCOLUMNLEVELGUIDELINE_IDNULL));
		}

		getDaoManager().getRepository(IPIRTAdequacyLevelGuidelineRepository.class).delete(pirtAdequacyColumnGuideline);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PIRTAdequacyColumn> getPIRTAdequacyColumn() {
		return getDaoManager().getRepository(IPIRTAdequacyColumnRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTAdequacyColumn addPIRTAdequacyColumn(PIRTAdequacyColumn pirtAdequacyColumn) throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDPIRTADEQCOLUMN_COLUMNNULL));
		}

		return getDaoManager().getRepository(IPIRTAdequacyColumnRepository.class).create(pirtAdequacyColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTAdequacyColumn updatePIRTAdequacyColumn(PIRTAdequacyColumn pirtAdequacyColumn)
			throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTADEQCOLUMN_COLUMNNULL));
		} else if (pirtAdequacyColumn.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTADEQCOLUMN_IDNULL));
		}

		return getDaoManager().getRepository(IPIRTAdequacyColumnRepository.class).update(pirtAdequacyColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllPIRTAdequacyColumn(List<PIRTAdequacyColumn> pirtAdequacyColumns) throws CredibilityException {
		if (pirtAdequacyColumns != null) {
			for (PIRTAdequacyColumn column : pirtAdequacyColumns) {
				deletePIRTAdequacyColumn(column);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePIRTAdequacyColumn(PIRTAdequacyColumn pirtAdequacyColumn) throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPIRTADEQCOLUMN_COLUMNNULL));
		} else if (pirtAdequacyColumn.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPIRTADEQCOLUMN_IDNULL));
		}

		getDaoManager().getRepository(IPIRTAdequacyColumnRepository.class).delete(pirtAdequacyColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PIRTDescriptionHeader> getPIRTDescriptionHeader() {
		return getDaoManager().getRepository(IPIRTDescriptionHeaderRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTDescriptionHeader addPIRTDescriptionHeader(PIRTDescriptionHeader pirtAdequacyColumn)
			throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDPIRTDESCHEADER_COLUMNNULL));
		}

		return getDaoManager().getRepository(IPIRTDescriptionHeaderRepository.class).create(pirtAdequacyColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTDescriptionHeader updatePIRTDescriptionHeader(PIRTDescriptionHeader pirtAdequacyColumn)
			throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTDESCHEADER_COLUMNNULL));
		} else if (pirtAdequacyColumn.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTDESCHEADER_IDNULL));
		}

		return getDaoManager().getRepository(IPIRTDescriptionHeaderRepository.class).update(pirtAdequacyColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllPIRTDescriptionHeader(List<PIRTDescriptionHeader> pirtHeaders) throws CredibilityException {
		if (pirtHeaders != null) {
			for (PIRTDescriptionHeader header : pirtHeaders) {
				deletePIRTDescriptionHeader(header);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePIRTDescriptionHeader(PIRTDescriptionHeader pirtAdequacyColumn) throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPIRTDESCHEADER_COLUMNNULL));
		} else if (pirtAdequacyColumn.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPIRTDESCHEADER_IDNULL));
		}

		getDaoManager().getRepository(IPIRTDescriptionHeaderRepository.class).delete(pirtAdequacyColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PIRTLevelDifferenceColor> getPIRTLevelDifferenceColor() {
		return getDaoManager().getRepository(IPIRTLevelDifferenceColorRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTLevelDifferenceColor addPIRTLevelDifferenceColor(PIRTLevelDifferenceColor pirtAdequacyColumn)
			throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDPIRTLEVELDIFFCOLOR_COLUMNNULL));
		}

		return getDaoManager().getRepository(IPIRTLevelDifferenceColorRepository.class).create(pirtAdequacyColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTLevelDifferenceColor updatePIRTLevelDifferenceColor(PIRTLevelDifferenceColor pirtAdequacyColumn)
			throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTLEVELDIFFCOLOR_COLUMNNULL));
		} else if (pirtAdequacyColumn.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTLEVELDIFFCOLOR_IDNULL));
		}

		return getDaoManager().getRepository(IPIRTLevelDifferenceColorRepository.class).update(pirtAdequacyColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllPIRTLevelDifferenceColor(List<PIRTLevelDifferenceColor> pirtColors)
			throws CredibilityException {
		if (pirtColors != null) {
			for (PIRTLevelDifferenceColor color : pirtColors) {
				deletePIRTLevelDifferenceColor(color);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePIRTLevelDifferenceColor(PIRTLevelDifferenceColor pirtAdequacyColumn)
			throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPIRTLEVELDIFFCOLOR_COLUMNNULL));
		} else if (pirtAdequacyColumn.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPIRTLEVELDIFFCOLOR_IDNULL));
		}

		getDaoManager().getRepository(IPIRTLevelDifferenceColorRepository.class).delete(pirtAdequacyColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PIRTLevelImportance> getPIRTLevelImportance() {
		return getDaoManager().getRepository(IPIRTLevelImportanceRepository.class).findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTLevelImportance addPIRTLevelImportance(PIRTLevelImportance pirtAdequacyColumn)
			throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_ADDPIRTLEVELIMPORTANCE_COLUMNNULL));
		}

		return getDaoManager().getRepository(IPIRTLevelImportanceRepository.class).create(pirtAdequacyColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTLevelImportance updatePIRTLevelImportance(PIRTLevelImportance pirtAdequacyColumn)
			throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTLEVELIMPORTANCE_COLUMNNULL));
		} else if (pirtAdequacyColumn.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_UPDATEPIRTLEVELIMPORTANCE_IDNULL));
		}

		return getDaoManager().getRepository(IPIRTLevelImportanceRepository.class).update(pirtAdequacyColumn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllPIRTLevelImportance(List<PIRTLevelImportance> pirtLevels) throws CredibilityException {
		if (pirtLevels != null) {
			for (PIRTLevelImportance level : pirtLevels) {
				deletePIRTLevelImportance(level);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePIRTLevelImportance(PIRTLevelImportance pirtAdequacyColumn) throws CredibilityException {

		if (pirtAdequacyColumn == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPIRTLEVELIMPORTANCE_COLUMNNULL));
		} else if (pirtAdequacyColumn.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PIRT_DELETEPIRTLEVELIMPORTANCE_IDNULL));
		}

		getDaoManager().getRepository(IPIRTLevelImportanceRepository.class).delete(pirtAdequacyColumn);
	}

	/**
	 * Check if PIRT is available
	 * 
	 * @return True if PIRT is available
	 */
	@Override
	public boolean isPIRTEnabled() {
		// Initialize
		boolean isEnabled = true;

		// Adequacy
		List<PIRTAdequacyColumn> adequacyColumn = getPIRTAdequacyColumn();
		isEnabled &= adequacyColumn != null && !adequacyColumn.isEmpty();

		// set level difference colors
		List<PIRTLevelDifferenceColor> levelDifferenceColor = getPIRTLevelDifferenceColor();
		isEnabled &= levelDifferenceColor != null && !levelDifferenceColor.isEmpty();

		// set importance levels
		List<PIRTLevelImportance> pirtLevelImportance = getPIRTLevelImportance();
		isEnabled &= pirtLevelImportance != null && !pirtLevelImportance.isEmpty();

		// Result
		return isEnabled;
	}

	@Override
	public void refresh(QuantityOfInterest qoi) {
		getDaoManager().getRepository(IQuantityOfInterestRepository.class).refresh(qoi);
	}

	@Override
	public void refresh(PhenomenonGroup group) {
		if (group != null)
			getDaoManager().getRepository(IPhenomenonGroupRepository.class).refresh(group);
	}

	@Override
	public void refresh(Phenomenon phenomenon) {
		if (phenomenon != null)
			getDaoManager().getRepository(IPhenomenonRepository.class).refresh(phenomenon);
	}
}
