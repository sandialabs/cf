/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.uncertainty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.dao.IUncertaintyConstraintRepository;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.dao.IUncertaintyRepository;
import gov.sandia.cf.dao.IUncertaintySelectValueRepository;
import gov.sandia.cf.dao.IUncertaintyValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyConstraint;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.comparator.StringWithNumberAndNullableComparator;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.IDTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage Uncertainty Application methods
 * 
 * @author Maxime N.
 *
 */
public class UncertaintyApplication extends AApplication implements IUncertaintyApplication {
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(UncertaintyApplication.class);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UncertaintySpecification loadUncertaintyConfiguration(Model model) {
		// Initialize
		UncertaintySpecification specs = null;

		// Check if uncertainty is enabled
		if (isUncertaintyEnabled(model)) {
			specs = new UncertaintySpecification();

			// get the Uncertainty Parameters
			specs.setParameters(getUncertaintyParameterByModel(model));
		}

		return specs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameConfiguration(UncertaintySpecification spec1, UncertaintySpecification spec2) {

		if (spec1 == null) {
			return spec2 == null;
		} else if (spec2 == null) {
			return false;
		}

		// parameters
		return getAppMgr().getService(IImportApplication.class).sameListContent(spec1.getParameters(),
				spec2.getParameters());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Uncertainty> getUncertaintyGroupByModel(Model model) {
		// Logger
		UncertaintyApplication.logger.debug("UncertaintyApplication: getUncertaintyGroupByModel"); //$NON-NLS-1$

		// Filter
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(Uncertainty.Filter.MODEL, model);
		filters.put(Uncertainty.Filter.PARENT, NullParameter.NULL);

		// Query
		return this.getDaoManager().getRepository(IUncertaintyRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Uncertainty> getUncertaintiesByModel(Model model) {

		UncertaintyApplication.logger.debug("UncertaintyApplication: getUncertaintiesByModel"); //$NON-NLS-1$

		// Filter
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(Uncertainty.Filter.MODEL, model);

		// Query
		return this.getDaoManager().getRepository(IUncertaintyRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Uncertainty> getUncertaintiesByModelAndParent(Model model, Uncertainty parent) {

		UncertaintyApplication.logger.debug("UncertaintyApplication: getUncertaintiesByModelAndParent"); //$NON-NLS-1$

		// Filter
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(Uncertainty.Filter.MODEL, model);
		filters.put(Uncertainty.Filter.PARENT, (parent == null ? NullParameter.NULL : parent));

		// Query
		return this.getDaoManager().getRepository(IUncertaintyRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uncertainty getUncertaintyGroupById(Integer id) {
		UncertaintyApplication.logger.debug("UncertaintyApplication: getUncertaintyGroupById"); //$NON-NLS-1$
		return this.getDaoManager().getRepository(IUncertaintyRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uncertainty getUncertaintyById(Integer id) {
		UncertaintyApplication.logger.debug("UncertaintyApplication: getUncertaintyById"); //$NON-NLS-1$
		return this.getDaoManager().getRepository(IUncertaintyRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UncertaintyParam> getUncertaintyParameterByModel(Model model) {
		UncertaintyApplication.logger.debug("UncertaintyApplication: getParameterByModel"); //$NON-NLS-1$
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(Uncertainty.Filter.MODEL, model);
		return this.getDaoManager().getRepository(IUncertaintyParamRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uncertainty addUncertainty(Uncertainty uncertainty, Model model, User userCreation)
			throws CredibilityException {

		if (uncertainty == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTY_NULL));
		} else if (userCreation == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTYROW_USERNULL));
		} else if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTY_MODELNULL));
		}

		// Get list
		List<UncertaintyValue> uncertaintyParameters = uncertainty.getValues();
		uncertainty.setValues(new ArrayList<>());

		// Create
		uncertainty.setUserCreation(userCreation);
		uncertainty.setCreationDate(DateTools.getCurrentDate());
		uncertainty.setModel(model);

		Uncertainty uncertaintyCreated = getAppMgr().getDaoManager().getRepository(IUncertaintyRepository.class)
				.create(uncertainty);

		if (uncertaintyParameters != null) {
			for (UncertaintyValue uncertaintyValue : uncertaintyParameters) {

				// set values
				uncertaintyValue.setUncertainty(uncertaintyCreated);
				uncertaintyValue.setDateCreation(DateTools.getCurrentDate());
				uncertaintyValue.setUserCreation(userCreation);

				// create value
				UncertaintyValue createdUncertaintyValue = getAppMgr().getDaoManager()
						.getRepository(IUncertaintyValueRepository.class).create(uncertaintyValue);
				uncertainty.getValues().add(createdUncertaintyValue);
			}
		}

		return uncertaintyCreated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uncertainty updateUncertainty(Uncertainty uncertainty, User userUpdate) throws CredibilityException {

		if (uncertainty == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_NULL));
		} else if (uncertainty.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_IDNULL));
		} else if (userUpdate == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_USERNULL));
		}

		if (uncertainty.getValueList() != null) {
			for (UncertaintyValue value : uncertainty.getValueList().stream().filter(UncertaintyValue.class::isInstance)
					.map(UncertaintyValue.class::cast).collect(Collectors.toList())) {
				value.setDateUpdate(DateTools.getCurrentDate());
				value.setUserUpdate(userUpdate);
			}
		}

		return getAppMgr().getDaoManager().getRepository(IUncertaintyRepository.class).update(uncertainty);
	}

	@Override
	public void deleteAllUncertainties(List<Uncertainty> uncertaintyList, User user) throws CredibilityException {
		if (uncertaintyList != null) {
			for (Uncertainty value : uncertaintyList) {
				deleteUncertainty(value, user);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUncertainty(Uncertainty uncertainty, User user) throws CredibilityException {

		if (uncertainty == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYROW_NULL));
		} else if (uncertainty.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYROW_IDNULL));
		}

		// refresh Uncertainty
		getAppMgr().getDaoManager().getRepository(IUncertaintyRepository.class).refresh(uncertainty);

		Uncertainty parent = uncertainty.getParent();
		Model model = uncertainty.getModel();

		// Remove Uncertainty - values associated will be automatically
		// deleted by cascade REMOVE
		getAppMgr().getDaoManager().getRepository(IUncertaintyRepository.class).delete(uncertainty);

		if (parent == null) {
			reorderAll(model, user);
		} else {
			reorderUncertaintyAtSameLevel(uncertainty, user);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllUncertaintyValue(List<UncertaintyValue> values) throws CredibilityException {
		if (values != null) {
			for (UncertaintyValue value : values) {
				deleteUncertaintyValue(value);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUncertaintyValue(UncertaintyValue value) throws CredibilityException {

		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYVALUE_NULL));
		} else if (value.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYVALUE_IDNULL));
		}

		getAppMgr().getDaoManager().getRepository(IUncertaintyValueRepository.class).delete(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllUncertaintyParam(List<UncertaintyParam> params) throws CredibilityException {
		if (params != null) {
			for (UncertaintyParam param : params) {
				deleteUncertaintyParam(param);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUncertaintyParam(UncertaintyParam param) throws CredibilityException {

		if (param == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYPARAM_NULL));
		} else if (param.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYPARAM_IDNULL));
		}

		// retrieve the uncertainty values associated to this parameter
		Map<EntityFilter, Object> filtersValue = new HashMap<>();
		filtersValue.put(UncertaintyValue.Filter.PARAMETER, param);
		deleteAllUncertaintyValue(
				getDaoManager().getRepository(IUncertaintyValueRepository.class).findBy(filtersValue));

		// retrieve the uncertainty parameter select values
		Map<EntityFilter, Object> filtersSelectValues = new HashMap<>();
		filtersSelectValues.put(GenericParameterSelectValue.Filter.PARAMETER, param);
		deleteAllUncertaintySelectValue(
				getDaoManager().getRepository(IUncertaintySelectValueRepository.class).findBy(filtersSelectValues));

		getDaoManager().getRepository(IUncertaintyParamRepository.class).delete(param);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllUncertaintySelectValue(List<UncertaintySelectValue> selectValues) throws CredibilityException {
		if (selectValues != null) {
			for (UncertaintySelectValue select : selectValues) {
				deleteUncertaintySelectValue(select);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUncertaintySelectValue(UncertaintySelectValue select) throws CredibilityException {

		if (select == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYSELECTVALUE_NULL));
		} else if (select.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYSELECTVALUE_IDNULL));
		}

		getDaoManager().getRepository(IUncertaintySelectValueRepository.class).delete(select);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllUncertaintyConstraint(List<UncertaintyConstraint> constraints) throws CredibilityException {
		if (constraints != null) {
			for (UncertaintyConstraint select : constraints) {
				deleteUncertaintyConstraint(select);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUncertaintyConstraint(UncertaintyConstraint constraint) throws CredibilityException {

		if (constraint == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYCONSTRAINT_NULL));
		} else if (constraint.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYCONSTRAINT_IDNULL));
		}

		getDaoManager().getRepository(IUncertaintyConstraintRepository.class).delete(constraint);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUncertaintyEnabled(Model model) {
		// Initialize
		boolean isEnabled = true;

		// Has Parameter
		List<UncertaintyParam> parameters = getUncertaintyParameterByModel(model);
		isEnabled &= parameters != null && !parameters.isEmpty();

		// The result
		return isEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(Uncertainty uncertainty) {
		if (uncertainty != null)
			getDaoManager().getRepository(IUncertaintyRepository.class).refresh(uncertainty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reorderAll(Model model, User user) throws CredibilityException {

		if (model == null) {
			return;
		}

		List<Uncertainty> sameGroupUncertaintyList = getUncertaintyGroupByModel(model);

		if (sameGroupUncertaintyList == null) {
			return;
		}

		// construct data
		sameGroupUncertaintyList
				.sort(Comparator.comparing(Uncertainty::getGeneratedId, new StringWithNumberAndNullableComparator()));

		// set id for parents
		int index = 1;
		for (Uncertainty uncertainty : sameGroupUncertaintyList) {

			String elementId = IDTools.generateAlphabeticId(index - 1);
			uncertainty.setGeneratedId(elementId);
			updateUncertainty(uncertainty, user);

			if (uncertainty.getChildren() != null && !uncertainty.getChildren().isEmpty()) {
				reorderUncertaintyAtSameLevel(uncertainty.getChildren().get(0), user);
			}

			// refresh group
			refresh(uncertainty);

			index++;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reorderUncertaintyAtSameLevel(Uncertainty toMove, User user) throws CredibilityException {

		if (toMove == null || toMove.getModel() == null) {
			return;
		}

		List<Uncertainty> sameGroupUncertaintyList = getUncertaintiesByModelAndParent(toMove.getModel(),
				toMove.getParent());

		if (sameGroupUncertaintyList == null) {
			return;
		}

		// construct data
		sameGroupUncertaintyList
				.sort(Comparator.comparing(Uncertainty::getGeneratedId, new StringWithNumberAndNullableComparator()));

		// apply reordering to elements
		applyReorderFromList(sameGroupUncertaintyList, toMove, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reorderUncertainty(Uncertainty toMove, int newIndex, User user) throws CredibilityException {

		int startPosition = IDTools.reverseGenerateAlphabeticIdRecursive(IDTools.ALPHABET.get(0));

		if (toMove == null || toMove.getModel() == null) {
			return;
		}

		if (newIndex < startPosition) {
			newIndex = startPosition;
		}

		List<Uncertainty> sameGroupUncertaintyList = getUncertaintiesByModelAndParent(toMove.getModel(),
				toMove.getParent());

		if (sameGroupUncertaintyList == null) {
			return;
		}

		// construct data
		sameGroupUncertaintyList
				.sort(Comparator.comparing(Uncertainty::getGeneratedId, new StringWithNumberAndNullableComparator()));

		// reorder
		List<Uncertainty> reorderedList = IDTools.reorderList(sameGroupUncertaintyList, toMove, newIndex);

		// apply reordering to elements
		applyReorderFromList(reorderedList, toMove, user);
	}

	/**
	 * Apply reorder from list.
	 *
	 * @param orderedList the ordered list
	 * @param toMove      the to move
	 * @param user        the user
	 * @throws CredibilityException the credibility exception
	 */
	private void applyReorderFromList(List<Uncertainty> orderedList, Uncertainty toMove, User user)
			throws CredibilityException {

		// set id for parents
		int index = 1;
		final String groupIdLabel = toMove.getParent() == null ? RscTools.empty() : toMove.getParent().getGeneratedId();
		for (Uncertainty uncertainty : orderedList) {

			String elementId = null;
			if (toMove.getParent() == null) {
				elementId = IDTools.generateAlphabeticId(index - 1);
			} else if (toMove.getLevel() % 2 == 0) {
				elementId = groupIdLabel + IDTools.generateAlphabeticId(index - 1);
			} else {
				elementId = groupIdLabel + index;
			}

			uncertainty.setGeneratedId(elementId);
			updateUncertainty(uncertainty, user);

			if (uncertainty.getChildren() != null && !uncertainty.getChildren().isEmpty()) {
				reorderUncertaintyAtSameLevel(uncertainty.getChildren().get(0), user);
			}

			index++;
		}

		// refresh group
		refresh(toMove.getParent());
	}
}
