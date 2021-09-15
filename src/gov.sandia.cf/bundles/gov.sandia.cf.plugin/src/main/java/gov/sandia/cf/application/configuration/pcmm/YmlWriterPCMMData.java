/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.pcmm;

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
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningTableValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * This class write credibility PCMM data. The actual implementation is stored
 * in a yaml file
 * 
 * @author Didier Verstraete
 *
 */
public class YmlWriterPCMMData {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlWriterPCMMData.class);

	/**
	 * Write to the file in parameter the PCMM data.
	 * 
	 * @param cfDataFile    the output file
	 * @param options       the input options
	 * @param specification the pcmm specification
	 * @param append        append or erase the output file
	 * @throws CredibilityException if a parameter is not valid
	 * @throws IOException          if a file exception occurs
	 */
	public void writePCMMData(final File cfDataFile, final Map<ExportOptions, Object> options,
			final PCMMSpecification specification, final boolean append) throws CredibilityException, IOException {
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

		if (!options.containsKey(ExportOptions.PCMM_INCLUDE)
				|| !Boolean.TRUE.equals(options.get(ExportOptions.PCMM_INCLUDE))) {
			logger.warn("PCMM is not selected to be included. Nothing to write into file {}", //$NON-NLS-1$
					cfDataFile.getAbsolutePath());
			return;
		}

		logger.debug("Write credibility data to the yml file {}", cfDataFile.getAbsolutePath()); //$NON-NLS-1$

		// load global specs
		Map<String, Object> mapRoot = new LinkedHashMap<>();
		Map<String, Object> mapPCMM = new LinkedHashMap<>();
		Map<String, Object> mapSchema = new LinkedHashMap<>();
		Map<String, Object> mapData = new LinkedHashMap<>();

		mapRoot.put(YmlPCMMData.CONF_PCMM, mapPCMM);

		/*
		 * Schema
		 */
		mapPCMM.put(YmlPCMMSchema.CONF_SCHEMA, mapSchema);
		YmlWriterPCMMSchema pcmmSchemaWriter = new YmlWriterPCMMSchema();
		boolean withIds = true;

		// Roles
		mapSchema.put(YmlPCMMSchema.CONF_PCMM_ROLES, pcmmSchemaWriter.toListPCMMRoles(specification));

		// Elements and Subelements
		mapSchema.put(YmlPCMMSchema.CONF_PCMM_ELEMENTS, pcmmSchemaWriter.toMapPCMMElements(specification, withIds));

		// Planning
		mapSchema.put(YmlPCMMSchema.CONF_PCMM_PLANNING, pcmmSchemaWriter.toMapPCMMPlanning(specification, withIds));

		/*
		 * Data
		 */
		mapPCMM.put(YmlPCMMData.CONF_DATA, mapData);

		// Tags
		mapData.put(YmlPCMMData.CONF_PCMM_TAG, toMapPCMMTagList(options));

		// Planning
		mapData.put(YmlPCMMData.CONF_PCMM_PLANNING, toMapPCMMPlanning(options));

		// Evidence
		mapData.put(YmlPCMMData.CONF_PCMM_EVIDENCE, toMapPCMMEvidenceOptions(options));

		// Assessment
		mapData.put(YmlPCMMData.CONF_PCMM_ASSESSMENT, toMapPCMMAssessmentOptions(options));

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
	 * @param options the PCMM data selected to be exported
	 * @return a map of PCMM tags
	 */
	public Map<String, Object> toMapPCMMTagList(final Map<ExportOptions, Object> options) {
		Map<String, Object> qoiMap = new LinkedHashMap<>();

		if (options != null && options.containsKey(ExportOptions.PCMM_TAG_LIST)
				&& options.get(ExportOptions.PCMM_TAG_LIST) instanceof List) {

			@SuppressWarnings("unchecked")
			List<Tag> tagList = (List<Tag>) options.get(ExportOptions.PCMM_TAG_LIST);

			for (Tag tag : tagList) {
				if (tag != null) {
					qoiMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, tag.getId(), tag.getName()),
							toMapPCMMTag(tag));
				}
			}
		}

		return qoiMap;
	}

	/**
	 * @param tag the PCMM tag to be exported
	 * @return a map of PCMM tag
	 */
	public Map<String, Object> toMapPCMMTag(final Tag tag) {
		Map<String, Object> tagMap = new LinkedHashMap<>();

		if (tag == null) {
			return tagMap;
		}

		tagMap.put(YmlPCMMData.CONF_PCMM_TAG_ID, tag.getId());
		tagMap.put(YmlPCMMData.CONF_PCMM_TAG_TAGDATE, tag.getDateTag());
		tagMap.put(YmlPCMMData.CONF_PCMM_TAG_DESCRIPTION, tag.getDescription());
		tagMap.put(YmlPCMMData.CONF_PCMM_TAG_NAME, tag.getName());
		tagMap.put(YmlPCMMData.CONF_PCMM_TAG_TAGUSER,
				tag.getUserCreation() != null ? tag.getUserCreation().getId() : null);

		return tagMap;
	}

	/**
	 * @param options the PCMM data selected to be exported
	 * @return a map of PCMM tags
	 */
	public Map<String, Object> toMapPCMMEvidenceOptions(final Map<ExportOptions, Object> options) {
		Map<String, Object> qoiMap = new LinkedHashMap<>();

		if (options != null && options.containsKey(ExportOptions.PCMM_EVIDENCE_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PCMM_EVIDENCE_INCLUDE))
				&& options.containsKey(ExportOptions.PCMM_EVIDENCE_LIST)
				&& options.get(ExportOptions.PCMM_EVIDENCE_LIST) instanceof Map
				&& !((Map<?, ?>) options.get(ExportOptions.PCMM_EVIDENCE_LIST)).isEmpty()) {

			Map<?, ?> mapEvidence = (Map<?, ?>) options.get(ExportOptions.PCMM_EVIDENCE_LIST);

			@SuppressWarnings("unchecked")
			List<PCMMEvidence> evidenceList = (List<PCMMEvidence>) mapEvidence.values().stream()
					.filter(o -> o instanceof List).map(List.class::cast).flatMap(List::stream)
					.collect(Collectors.toList());
			return toMapPCMMEvidenceList(evidenceList);
		}

		return qoiMap;
	}

	/**
	 * @param evidenceList the evidence to export
	 * @return a map of evidence
	 */
	private Map<String, Object> toMapPCMMEvidenceList(final List<PCMMEvidence> evidenceList) {

		Map<String, Object> evidenceMap = new LinkedHashMap<>();

		if (evidenceList == null) {
			return evidenceMap;
		}

		for (PCMMEvidence value : evidenceList) {
			evidenceMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, value.getId(), value.getName()),
					toMapPCMMEvidence(value));
		}

		return evidenceMap;
	}

	/**
	 * @param value the evidence to export
	 * @return a map of evidence
	 */
	private Map<String, Object> toMapPCMMEvidence(final PCMMEvidence value) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (value == null) {
			return valueMap;
		}

		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_ID, value.getId());
		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_PATH, value.getPath());
		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_NAME, value.getName());
		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_DESC, value.getDescription());
		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_ELEMENT,
				value.getElement() != null ? value.getElement().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_SUBELEMENT,
				value.getSubelement() != null ? value.getSubelement().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_DATEFILE, value.getDateFile());
		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_CREATIONDATE, value.getDateCreation());
		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_UPDATEDATE, value.getDateUpdate());
		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_USER,
				value.getUserCreation() != null ? value.getUserCreation().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_ROLE,
				value.getRoleCreation() != null ? value.getRoleCreation().getName() : null);
		valueMap.put(YmlPCMMData.CONF_PCMM_EVIDENCE_TAG, value.getTag() != null ? value.getTag().getId() : null);

		return valueMap;
	}

	/**
	 * @param options the PCMM data selected to be exported
	 * @return a map of PCMM tags
	 */
	public Map<String, Object> toMapPCMMAssessmentOptions(final Map<ExportOptions, Object> options) {
		Map<String, Object> qoiMap = new LinkedHashMap<>();

		if (options != null && options.containsKey(ExportOptions.PCMM_ASSESSMENT_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PCMM_ASSESSMENT_INCLUDE))
				&& options.containsKey(ExportOptions.PCMM_ASSESSMENT_LIST)
				&& options.get(ExportOptions.PCMM_ASSESSMENT_LIST) instanceof Map
				&& !((Map<?, ?>) options.get(ExportOptions.PCMM_ASSESSMENT_LIST)).isEmpty()) {

			Map<?, ?> mapAssessment = (Map<?, ?>) options.get(ExportOptions.PCMM_ASSESSMENT_LIST);

			@SuppressWarnings("unchecked")
			List<PCMMAssessment> assessmentList = (List<PCMMAssessment>) mapAssessment.values().stream()
					.filter(o -> o instanceof List).map(List.class::cast).flatMap(List::stream)
					.collect(Collectors.toList());

			return toMapPCMMAssessmentList(assessmentList);
		}

		return qoiMap;
	}

	/**
	 * @param evidenceList the evidence to export
	 * @return a map of evidence
	 */
	private Map<String, Object> toMapPCMMAssessmentList(final List<PCMMAssessment> evidenceList) {

		Map<String, Object> evidenceMap = new LinkedHashMap<>();

		if (evidenceList == null) {
			return evidenceMap;
		}

		for (PCMMAssessment value : evidenceList) {
			evidenceMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, value.getId(),
					YmlPCMMData.CONF_PCMM_ASSESSMENT_ASSESSMENT), toMapPCMMAssessment(value));
		}

		return evidenceMap;
	}

	/**
	 * @param value the evidence to export
	 * @return a map of evidence
	 */
	private Map<String, Object> toMapPCMMAssessment(final PCMMAssessment value) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (value == null) {
			return valueMap;
		}

		valueMap.put(YmlPCMMData.CONF_PCMM_ASSESSMENT_ID, value.getId());
		valueMap.put(YmlPCMMData.CONF_PCMM_ASSESSMENT_COMMENT, value.getComment());
		valueMap.put(YmlPCMMData.CONF_PCMM_ASSESSMENT_LEVEL,
				value.getLevel() != null ? value.getLevel().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PCMM_ASSESSMENT_ELEMENT,
				value.getElement() != null ? value.getElement().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PCMM_ASSESSMENT_SUBELEMENT,
				value.getSubelement() != null ? value.getSubelement().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PCMM_ASSESSMENT_CREATIONDATE, value.getDateCreation());
		valueMap.put(YmlPCMMData.CONF_PCMM_ASSESSMENT_UPDATEDATE, value.getDateUpdate());
		valueMap.put(YmlPCMMData.CONF_PCMM_ASSESSMENT_USER,
				value.getUserCreation() != null ? value.getUserCreation().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PCMM_ASSESSMENT_ROLE,
				value.getRoleCreation() != null ? value.getRoleCreation().getName() : null);
		valueMap.put(YmlPCMMData.CONF_PCMM_ASSESSMENT_TAG, value.getTag() != null ? value.getTag().getId() : null);

		return valueMap;
	}

	/**
	 * @param options the PCMM data selected to be exported
	 * @return a map of PCMM Planning data
	 */
	private Map<String, Object> toMapPCMMPlanning(final Map<ExportOptions, Object> options) {
		Map<String, Object> planningMap = new LinkedHashMap<>();

		if (options != null && options.containsKey(ExportOptions.PCMM_PLANNING_INCLUDE)
				&& Boolean.TRUE.equals(options.get(ExportOptions.PCMM_PLANNING_INCLUDE))) {

			// PCMM Planning question values
			if (options.containsKey(ExportOptions.PCMM_PLANNING_QUESTION_VALUES)
					&& options.get(ExportOptions.PCMM_PLANNING_QUESTION_VALUES) instanceof Map) {

				Map<?, ?> mapQuestionValues = (Map<?, ?>) options.get(ExportOptions.PCMM_PLANNING_QUESTION_VALUES);

				@SuppressWarnings("unchecked")
				List<PCMMPlanningQuestionValue> questionValues = (List<PCMMPlanningQuestionValue>) mapQuestionValues
						.values().stream().filter(o -> o instanceof List).map(List.class::cast).flatMap(List::stream)
						.collect(Collectors.toList());

				planningMap.put(YmlPCMMData.CONF_PLANNING_QUESTION_VALUES,
						toMapPCMMPlanningQuestionValuesList(questionValues));
			}

			// PCMM Planning values
			if (options.containsKey(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES)
					&& options.get(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES) instanceof Map) {

				Map<?, ?> mapParamValues = (Map<?, ?>) options.get(ExportOptions.PCMM_PLANNING_PARAMETERS_VALUES);

				@SuppressWarnings("unchecked")
				List<PCMMPlanningValue> paramValues = (List<PCMMPlanningValue>) mapParamValues.values().stream()
						.filter(o -> o instanceof List).map(List.class::cast).flatMap(List::stream)
						.collect(Collectors.toList());

				planningMap.put(YmlPCMMData.CONF_PLANNING_PARAMETER_VALUES, toMapPCMMPlanningValuesList(paramValues));
			}

			// PCMM Planning table items and values
			if (options.containsKey(ExportOptions.PCMM_PLANNING_PARAMETERS_TABLEITEMS)
					&& options.get(ExportOptions.PCMM_PLANNING_PARAMETERS_TABLEITEMS) instanceof Map) {

				Map<?, ?> mapTableItems = (Map<?, ?>) options.get(ExportOptions.PCMM_PLANNING_PARAMETERS_TABLEITEMS);

				@SuppressWarnings("unchecked")
				List<PCMMPlanningTableItem> paramItems = (List<PCMMPlanningTableItem>) mapTableItems.values().stream()
						.filter(o -> o instanceof List).map(List.class::cast).flatMap(List::stream)
						.collect(Collectors.toList());

				planningMap.put(YmlPCMMData.CONF_PLANNING_TABLE_ITEMS, toMapPCMMPlanningTableItemList(paramItems));
			}
		}

		return planningMap;
	}

	/**
	 * @param questionValueList the question values to export
	 * @return a map of question values
	 */
	private Map<String, Object> toMapPCMMPlanningQuestionValuesList(
			final List<PCMMPlanningQuestionValue> questionValueList) {

		Map<String, Object> questionValueMap = new LinkedHashMap<>();

		if (questionValueList == null) {
			return questionValueMap;
		}

		for (PCMMPlanningQuestionValue value : questionValueList) {
			questionValueMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, value.getId(),
					YmlPCMMData.CONF_PLANNING_QUESTION_VALUE_QUESTION), toMapPCMMPlanningQuestionValue(value));
		}

		return questionValueMap;
	}

	/**
	 * @param value the question value to export
	 * @return a map of question values
	 */
	private Map<String, Object> toMapPCMMPlanningQuestionValue(final PCMMPlanningQuestionValue value) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (value == null) {
			return valueMap;
		}

		valueMap.put(YmlPCMMData.CONF_PLANNING_QUESTION_VALUE_ID, value.getId());
		valueMap.put(YmlPCMMData.CONF_PLANNING_QUESTION_VALUE_QUESTION,
				value.getParameter() != null ? value.getParameter().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_QUESTION_VALUE_VALUE, value.getValue());
		valueMap.put(YmlPCMMData.CONF_PLANNING_QUESTION_VALUE_CREATIONDATE, value.getDateCreation());
		valueMap.put(YmlPCMMData.CONF_PLANNING_QUESTION_VALUE_CREATIONUSER,
				value.getUserCreation() != null ? value.getUserCreation().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_QUESTION_VALUE_UPDATEDATE, value.getDateUpdate());
		valueMap.put(YmlPCMMData.CONF_PLANNING_QUESTION_VALUE_UPDATEUSER,
				value.getUserUpdate() != null ? value.getUserUpdate().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_QUESTION_VALUE_TAG,
				value.getTag() != null ? value.getTag().getId() : null);

		return valueMap;
	}

	/**
	 * @param valueList the planning value to export
	 * @return a map of planning values
	 */
	private Map<String, Object> toMapPCMMPlanningValuesList(final List<PCMMPlanningValue> valueList) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (valueList == null) {
			return valueMap;
		}

		for (PCMMPlanningValue value : valueList) {
			valueMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, value.getId(),
					YmlPCMMData.CONF_PLANNING_PARAM_VALUE_PARAMETER), toMapPCMMPlanningValue(value));
		}

		return valueMap;
	}

	/**
	 * @param value the question value to export
	 * @return a map of question values
	 */
	private Map<String, Object> toMapPCMMPlanningValue(final PCMMPlanningValue value) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (value == null) {
			return valueMap;
		}

		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_VALUE_ID, value.getId());
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_VALUE_PARAMETER,
				value.getParameter() != null ? value.getParameter().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_VALUE_ELEMENT,
				value.getElement() != null ? value.getElement().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_VALUE_SUBELEMENT,
				value.getSubelement() != null ? value.getSubelement().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_VALUE_VALUE, value.getValue());
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_VALUE_CREATIONDATE, value.getDateCreation());
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_VALUE_CREATIONUSER,
				value.getUserCreation() != null ? value.getUserCreation().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_VALUE_UPDATEDATE, value.getDateUpdate());
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_VALUE_UPDATEUSER,
				value.getUserUpdate() != null ? value.getUserUpdate().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_VALUE_TAG, value.getTag() != null ? value.getTag().getId() : null);

		return valueMap;
	}

	/**
	 * @param itemList the planning table item to export
	 * @return a map of planning table items
	 */
	private Map<String, Object> toMapPCMMPlanningTableItemList(final List<PCMMPlanningTableItem> itemList) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (itemList == null) {
			return valueMap;
		}

		for (PCMMPlanningTableItem value : itemList) {
			valueMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, value.getId(),
					YmlPCMMData.CONF_PLANNING_PARAM_TABLE_VALUE_TABLEITEM), toMapPCMMPlanningTableItem(value));
		}

		return valueMap;
	}

	/**
	 * @param value the question value to export
	 * @return a map of question values
	 */
	private Map<String, Object> toMapPCMMPlanningTableItem(final PCMMPlanningTableItem value) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (value == null) {
			return valueMap;
		}

		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_ITEM_ID, value.getId());
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_ITEM_PARAMETER,
				value.getParameter() != null ? value.getParameter().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_ITEM_ELEMENT,
				value.getElement() != null ? value.getElement().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_ITEM_SUBELEMENT,
				value.getSubelement() != null ? value.getSubelement().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_ITEM_CREATIONDATE, value.getDateCreation());
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_ITEM_CREATIONUSER,
				value.getUserCreation() != null ? value.getUserCreation().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_ITEM_UPDATEDATE, value.getDateUpdate());
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_ITEM_UPDATEUSER,
				value.getUserUpdate() != null ? value.getUserUpdate().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_ITEM_VALUES,
				toMapPCMMPlanningTableValueList(value.getValueList()));
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_ITEM_TAG,
				value.getTag() != null ? value.getTag().getId() : null);

		return valueMap;
	}

	/**
	 * @param tableValueList the planning table value list to export
	 * @return a map of planning table values
	 */
	private Map<String, Object> toMapPCMMPlanningTableValueList(final List<IGenericTableValue> tableValueList) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (tableValueList == null) {
			return valueMap;
		}

		for (PCMMPlanningTableValue value : tableValueList.stream().filter(o -> o instanceof PCMMPlanningTableValue)
				.map(PCMMPlanningTableValue.class::cast).collect(Collectors.toList())) {
			valueMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, value.getId(),
					YmlPCMMData.CONF_PLANNING_PARAM_TABLE_VALUE_TABLEITEM), toMapPCMMPlanningTableValue(value));
		}

		return valueMap;
	}

	/**
	 * @param value the table value to export
	 * @return a map for this table value data
	 */
	private Map<String, Object> toMapPCMMPlanningTableValue(final PCMMPlanningTableValue value) {

		Map<String, Object> valueMap = new LinkedHashMap<>();

		if (value == null) {
			return valueMap;
		}

		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_VALUE_ID, value.getId());
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_VALUE_PARAMETER,
				value.getParameter() != null ? value.getParameter().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_VALUE_TABLEITEM,
				value.getItem() != null ? value.getItem().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_VALUE_VALUE, value.getValue());
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_VALUE_CREATIONDATE, value.getDateCreation());
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_VALUE_CREATIONUSER,
				value.getUserCreation() != null ? value.getUserCreation().getId() : null);
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_VALUE_UPDATEDATE, value.getDateUpdate());
		valueMap.put(YmlPCMMData.CONF_PLANNING_PARAM_TABLE_VALUE_UPDATEUSER,
				value.getUserUpdate() != null ? value.getUserUpdate().getId() : null);

		return valueMap;
	}
}
