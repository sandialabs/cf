/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.uncertainty;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.configuration.ExportOptions;
import gov.sandia.cf.application.configuration.YmlGlobalData;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * This class write credibility Uncertainty data. The actual implementation is
 * stored in a yaml file
 * 
 * @author Didier Verstraete
 *
 */
public class YmlWriterUncertaintyData {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlWriterUncertaintyData.class);

	/**
	 * Write to the file in parameter the uncertainty data.
	 * 
	 * @param cfDataFile    the output file
	 * @param options       the input options
	 * @param specification the Uncertainty specification
	 * @param append        append or erase the output file
	 * @throws CredibilityException if a parameter is not valid
	 * @throws IOException          if a file exception occurs
	 */
	public void writeUncertaintyData(final File cfDataFile, final Map<ExportOptions, Object> options,
			final UncertaintySpecification specification, final boolean append)
			throws CredibilityException, IOException {
		if (cfDataFile == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
		}

		// if append is not desired, delete the existing file
		if (cfDataFile.exists() && !append) {
			boolean deleted = Files.deleteIfExists(cfDataFile.toPath());
			if (!deleted || cfDataFile.exists()) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_DELETION_ERROR));
			}
		}

		// create file if it does not exist
		if (!cfDataFile.exists()) {
			boolean created = cfDataFile.createNewFile();
			if (!created || !cfDataFile.exists()) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
			}
		}

		if (options == null) {
			logger.warn("User selection is null. Nothing to write into file {}", cfDataFile.getAbsolutePath()); //$NON-NLS-1$
			return;
		}

		if (!options.containsKey(ExportOptions.UNCERTAINTY_INCLUDE)
				|| !Boolean.TRUE.equals(options.get(ExportOptions.UNCERTAINTY_INCLUDE))) {
			logger.warn("Uncertainty is not selected to be included. Nothing to write into file {}", //$NON-NLS-1$
					cfDataFile.getAbsolutePath());
			return;
		}

		logger.debug("Write credibility data to the yml file {}", cfDataFile.getAbsolutePath()); //$NON-NLS-1$

		// load global specs
		Map<String, Object> mapRoot = new LinkedHashMap<>();
		Map<String, Object> mapUncertainty = new LinkedHashMap<>();
		Map<String, Object> mapSchema = new LinkedHashMap<>();
		Map<String, Object> mapData = new LinkedHashMap<>();

		mapRoot.put(YmlUncertaintyData.CONF_UNCERTAINTY, mapUncertainty);

		// Uncertainty Schema
		mapUncertainty.put(YmlUncertaintySchema.CONF_SCHEMA, mapSchema);

		// Uncertainty parameters
		boolean withIds = true;
		mapSchema.put(YmlUncertaintySchema.CONF_UNCERTAINTY_PARAMETER,
				new YmlWriterUncertaintySchema().toMapUncertaintyParam(specification, withIds));

		// Uncertainty Data
		mapUncertainty.put(YmlUncertaintyData.CONF_DATA, mapData);

		// Uncertainty groups
		mapData.put(YmlUncertaintyData.CONF_UNCERTAINTYGROUP, toMapUncertaintyGroupsOptions(options));

		// YML reader
		Yaml yaml = new Yaml();

		// Write the specifications to the credibility file
		try (Writer writer = new StringWriter()) {

			// Dump map objects to yml string
			yaml.dump(mapRoot, writer);

			FileTools.writeStringInFile(cfDataFile, writer.toString(), append);
		}
	}

	/**
	 * @param options the Uncertainty data selected to be exported
	 * @return a map of uncertainty groups and their content structured.
	 */
	public Map<String, Object> toMapUncertaintyGroupsOptions(final Map<ExportOptions, Object> options) {
		Map<String, Object> qoiMap = new LinkedHashMap<>();

		if (options != null && options.containsKey(ExportOptions.UNCERTAINTY_GROUP_LIST)
				&& options.get(ExportOptions.UNCERTAINTY_GROUP_LIST) instanceof List) {

			@SuppressWarnings("unchecked")
			List<UncertaintyGroup> uncertaintyGroupList = (List<UncertaintyGroup>) options
					.get(ExportOptions.UNCERTAINTY_GROUP_LIST);

			return toMapUncertaintyGroupList(uncertaintyGroupList);
		}

		return qoiMap;
	}

	/**
	 * @param uncertaintyGroupList the uncertainty groups to export
	 * @return a map of groups
	 */
	private Map<String, Object> toMapUncertaintyGroupList(final List<UncertaintyGroup> uncertaintyGroupList) {

		Map<String, Object> uncertaintyGroupMap = new LinkedHashMap<>();

		if (uncertaintyGroupList == null) {
			return uncertaintyGroupMap;
		}

		for (UncertaintyGroup value : uncertaintyGroupList) {
			uncertaintyGroupMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, value.getId(), value.getName()),
					toMapUncertaintyGroup(value));
		}

		return uncertaintyGroupMap;
	}

	/**
	 * @param value the uncertainty group to export
	 * @return a map of uncertainty groups
	 */
	private Map<String, Object> toMapUncertaintyGroup(final UncertaintyGroup value) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (value == null) {
			return valueMap;
		}

		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTYGROUP_ID, value.getId());
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTYGROUP_NAME, value.getName());
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTYGROUP_UNCERTAINTIES,
				toMapUncertaintyItemList(value.getUncertainties()));

		return valueMap;
	}

	/**
	 * @param uncertaintyItemList the uncertainty items to export
	 * @return a map of uncertainty items
	 */
	private Map<String, Object> toMapUncertaintyItemList(final List<Uncertainty> uncertaintyList) {

		Map<String, Object> uncertaintyMap = new LinkedHashMap<>();

		if (uncertaintyList == null) {
			return uncertaintyMap;
		}

		for (Uncertainty value : uncertaintyList) {
			uncertaintyMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, value.getId(),
					YmlUncertaintyData.CONF_UNCERTAINTY_UNCERTAINTY), toMapUncertaintyItem(value));
		}

		return uncertaintyMap;
	}

	/**
	 * @param value the uncertainty item to export
	 * @return a map of uncertainty item
	 */
	private Map<String, Object> toMapUncertaintyItem(final Uncertainty value) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (value == null) {
			return valueMap;
		}

		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_ITEM_ID, value.getId());
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_ITEM_GROUP,
				value.getGroup() != null ? value.getGroup().getId() : null);
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_ITEM_USER,
				value.getUserCreation() != null ? value.getUserCreation().getId() : null);
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_ITEM_VALUES, toMapUncertaintyValueList(value.getValueList()));

		return valueMap;
	}

	/**
	 * @param uncertaintyItemList the uncertainty items to export
	 * @return a map of uncertainty items
	 */
	private Map<String, Object> toMapUncertaintyValueList(final List<IGenericTableValue> uncertaintyList) {

		Map<String, Object> uncertaintyMap = new LinkedHashMap<>();

		if (uncertaintyList == null) {
			return uncertaintyMap;
		}

		for (UncertaintyValue value : uncertaintyList.stream().filter(o -> o instanceof UncertaintyValue)
				.map(UncertaintyValue.class::cast).collect(Collectors.toList())) {
			uncertaintyMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, value.getId(),
					YmlUncertaintyData.CONF_UNCERTAINTY_VALUE_UNCERTAINTYVALUE), toMapUncertaintyValue(value));
		}

		return uncertaintyMap;
	}

	/**
	 * @param value the uncertainty item to export
	 * @return a map of uncertainty item
	 */
	private Map<String, Object> toMapUncertaintyValue(final UncertaintyValue value) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (value == null) {
			return valueMap;
		}

		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_VALUE_ID, value.getId());
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_VALUE_VALUE, value.getValue());
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_VALUE_ITEM,
				value.getUncertainty() != null ? value.getUncertainty().getId() : null);
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_VALUE_PARAM,
				value.getParameter() != null ? value.getParameter().getId() : null);
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_VALUE_CREATIONDATE, value.getDateCreation());
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_VALUE_CREATIONUSER,
				value.getUserCreation() != null ? value.getUserCreation().getId() : null);
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_VALUE_UPDATEDATE, value.getDateUpdate());
		valueMap.put(YmlUncertaintyData.CONF_UNCERTAINTY_VALUE_UPDATEUSER,
				value.getUserUpdate() != null ? value.getUserUpdate().getId() : null);

		return valueMap;
	}
}
