/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IGenericParameterApplication;
import gov.sandia.cf.application.IGlobalApplication;
import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.application.ISystemRequirementApplication;
import gov.sandia.cf.application.configuration.YmlGenericSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterConstraint;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.tools.ELTools;
import gov.sandia.cf.tools.LinkTools;
import gov.sandia.cf.tools.MathTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage Generic Parameter Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class GenericParameterApplication extends AApplication implements IGenericParameterApplication {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(GenericParameterApplication.class);

	/**
	 * The constructor
	 */
	public GenericParameterApplication() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public GenericParameterApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getReadableValue(GenericValue<?, ?> value) {
		logger.debug("Return Generic Parameter readable value"); //$NON-NLS-1$

		if (value == null || value.getParameter() == null || StringUtils.isBlank(value.getParameter().getType())) {
			return RscTools.empty();
		}

		if (FormFieldType.CREDIBILITY_ELEMENT.getType().equals(value.getParameter().getType())) {
			Integer id = MathTools.isInteger(value.getValue()) ? Integer.parseInt(value.getValue()) : 0;
			try {
				PCMMElement elementFound = getAppMgr().getService(IPCMMApplication.class).getElementById(id);
				return elementFound != null ? elementFound.getAbstract() : RscTools.empty();
			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
			}
		} else if (FormFieldType.SYSTEM_REQUIREMENT.getType().equals(value.getParameter().getType())) {
			Integer id = MathTools.isInteger(value.getValue()) ? Integer.parseInt(value.getValue()) : 0;
			SystemRequirement requirementFound = getAppMgr().getService(ISystemRequirementApplication.class)
					.getRequirementById(id);
			return requirementFound != null ? requirementFound.getAbstract() : RscTools.empty();
		}

		return gov.sandia.cf.tools.StringTools.clearHtml(value.getReadableValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void openLinkValue(GenericValue<?, ?> value) {

		// check the evidence before opening
		if (value != null && value.getValue() != null && value.getParameter() != null
				&& FormFieldType.LINK.getType().equals(value.getParameter().getType())) {
			LinkTools.openLinkValue(value.getValue(),
					getAppMgr().getService(IGlobalApplication.class).getOpenLinkBrowserOpts());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isParameterAvailableForLevel(GenericParameter<?> parameter, int levelNumber) {
		// All
		if (parameter.getLevel().equals(YmlGenericSchema.LEVEL_COMPARATOR_ALL)) {
			return true;
		}

		// Split level to get the comparator and the level value
		String[] parts = parameter.getLevel().split(YmlGenericSchema.LEVEL_COMPARATOR_STRING);

		// Check match
		if (parts.length >= 2 && MathTools.isInteger(parts[1])) {

			// Get data for the comparison
			String comparatorCondition = parts[0];
			int comparatorLevel = Integer.parseInt(parts[1]);

			// Compare
			switch (comparatorCondition) {
			case YmlGenericSchema.LEVEL_COMPARATOR_EQUAL:
				return levelNumber == comparatorLevel;
			case YmlGenericSchema.LEVEL_COMPARATOR_OVER:
				return levelNumber > comparatorLevel;
			case YmlGenericSchema.LEVEL_COMPARATOR_UNDER:
				return levelNumber < comparatorLevel;
			case YmlGenericSchema.LEVEL_COMPARATOR_OVER_EQUAL:
				return levelNumber >= comparatorLevel;
			case YmlGenericSchema.LEVEL_COMPARATOR_UNDER_EQUAL:
				return levelNumber <= comparatorLevel;
			default:
				return false;
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParameterNameWithRequiredPrefix(GenericParameter<?> parameter) {

		if (parameter == null || StringUtils.isBlank(parameter.getName())) {
			return RscTools.empty();
		}

		if (StringUtils.isBlank(parameter.getRequired())
				|| YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE.equals(parameter.getRequired())
				|| YmlGenericSchema.CONF_GENERIC_DESIRED_VALUE.equals(parameter.getRequired())
				|| YmlGenericSchema.CONF_GENERIC_FALSE_VALUE.equals(parameter.getRequired())) {
			return parameter.getName();
		}

		if (YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE.equals(parameter.getRequired())
				|| YmlGenericSchema.CONF_GENERIC_TRUE_VALUE.equals(parameter.getRequired())) {
			return RscTools.getString(RscConst.MSG_LBL_REQUIRED, parameter.getName());
		}

		return RscTools.getString(RscConst.MSG_LBL_REQUIRED_WITH_CONDITION, parameter.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(GenericValue<?, ?> value, List<? extends GenericValue<?, ?>> items) {
		Notification notification = checkValid(value, items);
		return notification == null || !notification.isError();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Notification checkValid(GenericValue<?, ?> value, List<? extends GenericValue<?, ?>> items) {

		if (value == null || value.getParameter() == null) {
			return null;
		}

		// check required
		Notification notification = checkRequired(value, items);
		if (notification != null) {
			return notification;
		}

		// check constraints after that point. If value is blank or if there is no
		// constraints stop here
		if (org.apache.commons.lang3.StringUtils.isBlank(value.getReadableValue())
				|| value.getParameter().getConstraintList() == null
				|| value.getParameter().getConstraintList().isEmpty()) {
			return null;
		}

		// check constraints validity
		for (GenericParameterConstraint<?> constraint : value.getParameter().getConstraintList().stream()
				.filter(Objects::nonNull).collect(Collectors.toList())) {

			String eval = constraint.getRule();

			// get attributes values
			Map<String, Object> varMapper = new LinkedHashMap<>();
			if (items != null && !items.isEmpty()) {

				// get variable list from eval
				List<String> fields = new ArrayList<>();
				fields.add(value.getParameter().getName());
				fields.addAll(items.stream().map(var -> var.getParameter().getName()).collect(Collectors.toList()));
				Set<String> associatedFields = ELTools.getVariableSet(eval, fields);

				// map variable value
				associatedFields.forEach(var -> varMapper.put(var, getAssociatedValueString(var, items)));
				varMapper.put(value.getParameter().getName(), value.getReadableValue());
			}

			// compute eval
			try {
				boolean valid = ELTools.eval(Boolean.class, eval, varMapper);
				if (!valid) {
					notification = NotificationFactory.getNewError(RscTools.getString(
							RscConst.ERR_GENERICPARAM_VALUE_CONSTRAINT_NOTVALID, value.getParameter().getName(), eval));
				}
			} catch (Exception e) {
				notification = NotificationFactory.getNewError(
						RscTools.getString(RscConst.ERR_GENERICPARAM_VALUE_CONSTRAINT_EXCEPTION, eval, e.getMessage()));
				logger.error(
						RscTools.getString(RscConst.ERR_GENERICPARAM_VALUE_CONSTRAINT_EXCEPTION, eval, e.getMessage()),
						e);
			}
		}

		return notification;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Notification checkRequired(GenericValue<?, ?> value, List<? extends GenericValue<?, ?>> items) {

		// not required
		if (value == null || value.getParameter() == null || StringUtils.isBlank(value.getParameter().getRequired())
				|| YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE.equals(value.getParameter().getRequired())
				|| YmlGenericSchema.CONF_GENERIC_FALSE_VALUE.equals(value.getParameter().getRequired())) {
			return null;
		}

		// check if value is required
		boolean desired = false;
		boolean required = false;
		Set<String> associatedFields = new HashSet<>();

		// desired
		if (YmlGenericSchema.CONF_GENERIC_DESIRED_VALUE.equals(value.getParameter().getRequired())) {
			desired = true;
		}
		// directly required
		else if (YmlGenericSchema.CONF_GENERIC_REQUIRED_VALUE.equals(value.getParameter().getRequired())
				|| YmlGenericSchema.CONF_GENERIC_TRUE_VALUE.equals(value.getParameter().getRequired())) {
			required = true;
		}
		// compute required
		else {
			String eval = value.getParameter().getRequired();

			// get attributes values
			Map<String, Object> varMapper = new LinkedHashMap<>();
			if (items != null && !items.isEmpty()) {

				// get variable list from eval
				List<String> fields = new ArrayList<>();
				fields.addAll(items.stream().map(var -> var.getParameter().getName()).collect(Collectors.toList()));
				associatedFields = ELTools.getVariableSet(eval, fields);

				// map variable value
				associatedFields.forEach(var -> varMapper.put(var, getAssociatedValueString(var, items)));
			}

			// compute eval
			required = ELTools.eval(Boolean.class, eval, varMapper);
		}

		Notification notification = null;

		// if required, check if value is blank
		if (required && StringUtils.isBlank(value.getReadableValue())) {
			if (associatedFields.isEmpty()) {
				notification = NotificationFactory.getNewError(RscTools
						.getString(RscConst.ERR_GENERICPARAM_PARAMETER_REQUIRED, value.getParameter().getName()));
			} else {
				notification = NotificationFactory
						.getNewError(RscTools.getString(RscConst.ERR_GENERICPARAM_PARAMETER_REQUIRED_CONDITION_NOTVALID,
								value.getParameter().getName(), value.getParameter().getRequired()));
			}
		}

		// if desired, check if value is blank
		if (desired && StringUtils.isBlank(value.getReadableValue())) {
			if (associatedFields.isEmpty()) {
				notification = NotificationFactory.getNewWarning(RscTools
						.getString(RscConst.ERR_GENERICPARAM_PARAMETER_DESIRED, value.getParameter().getName()));
			} else {
				notification = NotificationFactory.getNewWarning(
						RscTools.getString(RscConst.ERR_GENERICPARAM_PARAMETER_DESIRED_CONDITION_NOTVALID,
								value.getParameter().getName(), value.getParameter().getRequired()));
			}
		}

		return notification;
	}

	/**
	 * @param var   the variable to map
	 * @param items the items to find
	 * @return the value if found as string, otherwise null
	 */
	private String getAssociatedValueString(String var, List<? extends GenericValue<?, ?>> items) {

		// get associated value
		Optional<? extends GenericValue<?, ?>> valueAssociated = items.stream()
				.filter(v -> v != null && v.getParameter() != null && var.equals(v.getParameter().getName()))
				.findFirst();

		// set associated value string
		String valueAssociatedStr = null;
		if (valueAssociated.isPresent()) {
			valueAssociatedStr = valueAssociated.get().getReadableValue();
		}
		return valueAssociatedStr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IGenericTableValue> sortTableValuesByParameterId(List<IGenericTableValue> values) {

		if (values != null && !values.isEmpty()) {

			// sort values
			values.sort((v1, v2) -> {
				if (v1 == null || v1.getParameter() == null) {
					return v2 == null || v2.getParameter() == null ? 0 : 1;
				}

				if (v2 == null || v2.getParameter() == null) {
					return -1;
				}

				return Integer.compare(v1.getParameter().getId(), v2.getParameter().getId());
			});
		}
		return values;
	}
}