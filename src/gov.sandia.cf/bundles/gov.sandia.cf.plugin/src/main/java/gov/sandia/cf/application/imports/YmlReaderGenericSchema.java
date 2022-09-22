/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.imports;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.cf.constants.configuration.YmlGenericSchema;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterConstraint;
import gov.sandia.cf.model.GenericParameterSelectValue;

/**
 * This class reads the generic parameter. The actual implementation is stored
 * in a yaml file.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlReaderGenericSchema {

	private YmlReaderGenericSchema() {
		// Do not instantiate
	}

	/**
	 * Create the list of generic parameters.
	 *
	 * @param <G>              the generic parameter class
	 * @param <S>              the generic parameter select value class
	 * @param <C>              the generic parameter constraint class
	 * @param paramClass       the generic parameter class
	 * @param selectValueClass the generic parameter select value class
	 * @param constraintClass  the generic parameter constraint class
	 * @param ymlParameters    the yml data to parse
	 * @return the list of parameters populated
	 * @throws InstantiationException    if an error occurs during object
	 *                                   instantiation
	 * @throws IllegalAccessException    if an error occurs while accessing methods
	 *                                   using reflection
	 * @throws IllegalArgumentException  the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException     the no such method exception
	 * @throws SecurityException         the security exception
	 */
	public static <G extends GenericParameter<G>, S extends GenericParameterSelectValue<G>, C extends GenericParameterConstraint<G>> List<G> createParameters(
			Class<G> paramClass, Class<S> selectValueClass, Class<C> constraintClass,
			Map<String, Map<String, Object>> ymlParameters) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		// Initialize list
		List<G> listParameters = new ArrayList<>();
		if (ymlParameters != null) {
			// For each parameter from configuration yml
			for (Entry<String, Map<String, Object>> entry : ymlParameters.entrySet()) {
				G genericParameter = createGenericParameter(paramClass, selectValueClass, constraintClass, entry);
				if (genericParameter != null) {
					listParameters.add(genericParameter);
				}
			}
		}

		return listParameters;
	}

	/**
	 * Create a new generic parameter.
	 *
	 * @param <G>              the generic parameter class
	 * @param <S>              the generic parameter select value class
	 * @param <C>              the generic parameter constraint class
	 * @param paramClass       the generic parameter class
	 * @param selectValueClass the generic parameter select value class
	 * @param constraintClass  the generic parameter constraint class
	 * @param entry            the yml entry to parse
	 * @return the new parameter populated
	 * @throws InstantiationException    if an error occurs during object
	 *                                   instantiation
	 * @throws IllegalAccessException    if an error occurs while accessing methods
	 *                                   using reflection
	 * @throws IllegalArgumentException  the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException     the no such method exception
	 * @throws SecurityException         the security exception
	 */
	@SuppressWarnings("unchecked")
	public static <G extends GenericParameter<G>, S extends GenericParameterSelectValue<G>, C extends GenericParameterConstraint<G>> G createGenericParameter(
			Class<G> paramClass, Class<S> selectValueClass, Class<C> constraintClass,
			Entry<String, Map<String, Object>> entry) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		G genParameter = null;
		if (entry != null) {
			// Get level
			String level = (String) entry.getValue().get(YmlGenericSchema.CONF_GENERIC_LEVEL);

			// Create genParameter
			genParameter = paramClass.getDeclaredConstructor().newInstance();
			genParameter.setName(entry.getKey());
			genParameter.setLevel((level != null) ? level : YmlGenericSchema.DEFAULT_LEVEL);
			genParameter.setType((String) entry.getValue().get(YmlGenericSchema.CONF_GENERIC_TYPE));
			String requiredValue = YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE; // default required value is optional
			if (!StringUtils.isBlank((String) entry.getValue().get(YmlGenericSchema.CONF_GENERIC_REQUIRED))) {
				requiredValue = (String) entry.getValue().get(YmlGenericSchema.CONF_GENERIC_REQUIRED);
			}
			genParameter.setRequired(requiredValue);
			if (!StringUtils
					.isBlank((String) entry.getValue().get(YmlGenericSchema.CONF_GENERIC_CONSTRAINTS_DEFAULT))) {
				genParameter.setDefaultValue(
						(String) entry.getValue().get(YmlGenericSchema.CONF_GENERIC_CONSTRAINTS_DEFAULT));
			}

			// Create GenericParameterValueList and add it to GenericParameter
			List<String> selectValues = null;
			if (entry.getValue().get(YmlGenericSchema.CONF_GENERIC_VALUES) instanceof List) {
				selectValues = (List<String>) entry.getValue().get(YmlGenericSchema.CONF_GENERIC_VALUES);
			}
			genParameter.setParameterValueList(createParameterSelectValues(selectValueClass, selectValues));

			// Create GenericParameterConstraints and add it to GenericParameter
			List<String> constraints = null;
			if (entry.getValue().get(YmlGenericSchema.CONF_GENERIC_CONSTRAINTS) instanceof List) {
				constraints = (List<String>) entry.getValue().get(YmlGenericSchema.CONF_GENERIC_CONSTRAINTS);
			}
			genParameter.setConstraintList(createParameterConstraints(constraintClass, constraints));
		}

		return genParameter;
	}

	/**
	 * Create the list of generic parameter select values.
	 *
	 * @param <G>              the generic parameter class
	 * @param <S>              the generic parameter select value class
	 * @param selectValueClass the generic parameter select value class
	 * @param values           the yml data to parse
	 * @return the list of parameters populated
	 * @throws InstantiationException    if an error occurs during object
	 *                                   instantiation
	 * @throws IllegalAccessException    if an error occurs while accessing methods
	 *                                   using reflection
	 * @throws IllegalArgumentException  the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException     the no such method exception
	 * @throws SecurityException         the security exception
	 */
	public static <G extends GenericParameter<G>, S extends GenericParameterSelectValue<G>> List<GenericParameterSelectValue<G>> createParameterSelectValues(
			Class<S> selectValueClass, List<String> values) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		// Create GenericParameterValueList and add it to GenericParameter
		List<GenericParameterSelectValue<G>> listParameterValue = new ArrayList<>();
		if (values != null) {
			for (Object value : values) {
				String val = (value.toString());
				S parameterValue = selectValueClass.getDeclaredConstructor().newInstance();
				parameterValue.setName(val);
				listParameterValue.add(parameterValue);
			}
		}

		return listParameterValue;
	}

	/**
	 * Create the list of generic parameter constraints.
	 *
	 * @param <G>             the generic parameter class
	 * @param <C>             the generic parameter constraint class
	 * @param constraintClass the generic parameter constraint class
	 * @param values          the yml data to parse
	 * @return the list of parameters populated
	 * @throws InstantiationException    if an error occurs during object
	 *                                   instantiation
	 * @throws IllegalAccessException    if an error occurs while accessing methods
	 *                                   using reflection
	 * @throws IllegalArgumentException  the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException     the no such method exception
	 * @throws SecurityException         the security exception
	 */
	public static <G extends GenericParameter<G>, C extends GenericParameterConstraint<G>> List<GenericParameterConstraint<G>> createParameterConstraints(
			Class<C> constraintClass, List<String> values) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		// Create GenericParameterConstraint list and add it to GenericParameter
		List<GenericParameterConstraint<G>> listParameterValue = new ArrayList<>();
		if (values != null) {
			for (String rule : values) {
				C constraint = constraintClass.getDeclaredConstructor().newInstance();
				constraint.setRule(rule);
				listParameterValue.add(constraint);
			}
		}

		return listParameterValue;
	}
}
