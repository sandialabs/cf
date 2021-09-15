/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterSelectValue;

/**
 * This class write the generic parameter. The actual implementation is stored
 * in a yaml file.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlWriterGenericSchema {

	private YmlWriterGenericSchema() {
		// Do not instantiate
	}

	/**
	 * @param <G>     the generic parameter class
	 * 
	 * @param param   the generic parameter to write
	 * @param withIds add the id field to the export
	 * @return a map of Generic Parameter values
	 */
	public static <G extends GenericParameter<G>> Map<String, Object> getGenericParamValues(GenericParameter<G> param,
			final boolean withIds) {

		Map<String, Object> values = new LinkedHashMap<>();

		if (withIds) {
			values.put(YmlGenericSchema.CONF_GENERIC_ID, param.getId());
		}
		if (param.getLevel() != null) {
			values.put(YmlGenericSchema.CONF_GENERIC_LEVEL, param.getLevel());
		}
		if (param.getRequired() != null) {
			values.put(YmlGenericSchema.CONF_GENERIC_REQUIRED, param.getRequired());
		}
		if (param.getType() != null) {
			values.put(YmlGenericSchema.CONF_GENERIC_TYPE, param.getType());
		}
		if (param.getParameterValueList() != null && !param.getParameterValueList().isEmpty()) {
			values.put(YmlGenericSchema.CONF_GENERIC_VALUES, param.getParameterValueList().stream()
					.map(GenericParameterSelectValue::getName).collect(Collectors.toList()));
		}

		return values;
	}

}
