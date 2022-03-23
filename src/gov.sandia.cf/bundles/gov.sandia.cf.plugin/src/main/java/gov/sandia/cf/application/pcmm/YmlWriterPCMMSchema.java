/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.pcmm;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.exports.IYmlSchemaWriter;
import gov.sandia.cf.application.exports.YmlWriterGenericSchema;
import gov.sandia.cf.application.imports.YmlReaderGlobal;
import gov.sandia.cf.constants.configuration.YmlPCMMSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * This class write credibility PCMM configuration. The actual implementation is
 * stored in a yaml file
 * 
 * @author Didier Verstraete
 *
 */
public class YmlWriterPCMMSchema implements IYmlSchemaWriter<PCMMSpecification> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlWriterPCMMSchema.class);

	@Override
	@SuppressWarnings("unchecked")
	public void writeSchema(final File cfSchemaFile, final PCMMSpecification specification, final boolean withIds,
			final boolean append) throws CredibilityException, IOException {
		if (cfSchemaFile == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
		}

		// if append is not desired, delete the existing file
		if (cfSchemaFile.exists() && !append) {
			boolean deleted = Files.deleteIfExists(cfSchemaFile.toPath());
			if (!deleted || cfSchemaFile.exists()) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_DELETION_ERROR));
			}
		}

		// create file if it does not exist
		if (!cfSchemaFile.exists()) {
			boolean created = cfSchemaFile.createNewFile();
			if (!created || !cfSchemaFile.exists()) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
			}
		}

		if (specification == null) {
			logger.warn("Configuration is null. Nothing to write into file {}", cfSchemaFile.getAbsolutePath()); //$NON-NLS-1$
			return;
		}

		logger.debug("Write PCMM specification to the yml file {}", cfSchemaFile.getAbsolutePath()); //$NON-NLS-1$

		// load global specs
		Map<String, Object> mapSpecs = YmlReaderGlobal.loadYmlFile(cfSchemaFile);

		if (mapSpecs.get(YmlPCMMSchema.CONF_PCMM) instanceof Map) {
			mapSpecs = (Map<String, Object>) mapSpecs.get(YmlPCMMSchema.CONF_PCMM);
		}

		// replace PCMM Phases
		if (mapSpecs.get(YmlPCMMSchema.CONF_PCMM_PHASES) instanceof List) {
			mapSpecs.remove(YmlPCMMSchema.CONF_PCMM_PHASES);
		}
		mapSpecs.put(YmlPCMMSchema.CONF_PCMM_PHASES, toListPCMMPhases(specification));

		// replace PCMM Roles
		if (mapSpecs.get(YmlPCMMSchema.CONF_PCMM_ROLES) instanceof List) {
			mapSpecs.remove(YmlPCMMSchema.CONF_PCMM_ROLES);
		}
		mapSpecs.put(YmlPCMMSchema.CONF_PCMM_ROLES, toListPCMMRoles(specification));

		// replace PCMM Levels
		if (mapSpecs.get(YmlPCMMSchema.CONF_PCMM_LEVELS) instanceof List) {
			mapSpecs.remove(YmlPCMMSchema.CONF_PCMM_LEVELS);
		}
		mapSpecs.put(YmlPCMMSchema.CONF_PCMM_LEVELS, toMapPCMMLevelColors(specification));

		// replace PCMM Elements
		if (mapSpecs.get(YmlPCMMSchema.CONF_PCMM_ELEMENTS) instanceof List) {
			mapSpecs.remove(YmlPCMMSchema.CONF_PCMM_ELEMENTS);
		}
		mapSpecs.put(YmlPCMMSchema.CONF_PCMM_ELEMENTS, toMapPCMMElements(specification, withIds));

		// replace PCMM Planning
		if (mapSpecs.get(YmlPCMMSchema.CONF_PCMM_PLANNING) instanceof List) {
			mapSpecs.remove(YmlPCMMSchema.CONF_PCMM_PLANNING);
		}
		mapSpecs.put(YmlPCMMSchema.CONF_PCMM_PLANNING, toMapPCMMPlanning(specification, withIds));

		// YML reader
		Yaml yaml = new Yaml();

		// Write the specifications to the credibility file
		try (Writer writer = new StringWriter()) {

			// Dump map objects to yml string
			yaml.dump(mapSpecs, writer);

			FileTools.writeStringInFile(cfSchemaFile, writer.toString(), append);
		}
	}

	/**
	 * @param currentSpecs the pcmm specification to populate
	 * @param changes      the changes to apply
	 * @return the pcmm specification populated
	 */
	public PCMMSpecification populatePCMMPhaseChanges(PCMMSpecification currentSpecs,
			Map<ImportActionType, List<IImportable<?>>> changes) {

		if (currentSpecs == null) {
			currentSpecs = new PCMMSpecification();
		}

		if (currentSpecs.getPhases() == null) {
			currentSpecs.setPhases(new ArrayList<>());
		}

		// To add
		if (changes != null && changes.containsKey(ImportActionType.TO_ADD)) {
			for (IImportable<?> importable : changes.get(ImportActionType.TO_ADD).stream()
					.filter(PCMMOption.class::isInstance).collect(Collectors.toList())) {
				if (!currentSpecs.getOptions().contains(importable)) {
					currentSpecs.getOptions().add((PCMMOption) importable);
				}
			}
		}

		// To delete
		if (changes != null && changes.containsKey(ImportActionType.TO_DELETE)) {
			for (IImportable<?> importable : changes.get(ImportActionType.TO_DELETE).stream()
					.filter(PCMMOption.class::isInstance).collect(Collectors.toList())) {
				if (currentSpecs.getOptions().contains(importable)) {
					currentSpecs.getOptions().remove((PCMMOption) importable);
				}
			}
		}

		return currentSpecs;
	}

	/**
	 * @param specification the PCMM specification
	 * @return a list of PCMM Phases
	 */
	public List<String> toListPCMMPhases(PCMMSpecification specification) {
		List<String> phases = new ArrayList<>();

		if (specification != null && specification.getPhases() != null) {
			phases = specification.getPhases().stream().map(PCMMPhase::getName).collect(Collectors.toList());
		}

		return phases;
	}

	/**
	 * @param specification the PCMM specification
	 * @return a list of PCMM Roles
	 */
	public List<String> toListPCMMRoles(PCMMSpecification specification) {
		List<String> roles = new ArrayList<>();

		if (specification != null && specification.getRoles() != null) {
			roles = specification.getRoles().stream().map(Role::getName).collect(Collectors.toList());
		}

		return roles;
	}

	/**
	 * @param specification the PCMM specification
	 * @return a list of PCMM Levels
	 */
	public Map<String, Object> toMapPCMMLevelColors(PCMMSpecification specification) {
		Map<String, Object> levels = new LinkedHashMap<>();

		if (specification != null && specification.getLevelColors() != null
				&& specification.getLevelColors().values() != null) {
			for (PCMMLevelColor level : specification.getLevelColors().values()) {

				String key = level.getName();
				Map<String, Object> values = new LinkedHashMap<>();

				values.put(YmlPCMMSchema.CONF_PCMM_LEVEL_CODE, level.getCode());
				values.put(YmlPCMMSchema.CONF_PCMM_LEVEL_COLOR, level.getFixedColor());
				levels.put(key, values);
			}
		}

		return levels;
	}

	/**
	 * @param specification the PCMM specification
	 * @param withIds       add the id field to the export
	 * @return a list of PCMM Elements
	 */
	public Map<String, Object> toMapPCMMElements(final PCMMSpecification specification, final boolean withIds) {
		Map<String, Object> elements = new LinkedHashMap<>();

		if (specification != null && specification.getElements() != null) {

			specification.getElements().forEach(element -> {
				String key = element.getAbbreviation();

				Map<String, Object> elementAttributes = new LinkedHashMap<>();
				if (withIds) {
					elementAttributes.put(YmlPCMMSchema.CONF_PCMM_ELEMENT_ID, element.getId());
				}
				elementAttributes.put(YmlPCMMSchema.CONF_PCMM_ELEMENT_NAME, element.getName());
				elementAttributes.put(YmlPCMMSchema.CONF_PCMM_ELEMENT_ABBREV, element.getAbbreviation());
				elementAttributes.put(YmlPCMMSchema.CONF_PCMM_ELEMENT_COLOR, element.getColor());
				if (element.getLevelList() != null) {
					elementAttributes.put(YmlPCMMSchema.CONF_PCMM_LEVELS,
							toMapPCMMLevel(element.getLevelList(), withIds));
				}
				if (element.getSubElementList() != null) {
					elementAttributes.put(YmlPCMMSchema.CONF_PCMM_SUBELEMENTS,
							toMapPCMMSubelements(element.getSubElementList(), withIds));
				}
				elements.put(key, elementAttributes);
			});
		}

		return elements;
	}

	/**
	 * @param subelementList the PCMM subelement list
	 * @param withIds        add the id field to the export
	 * @return a list of PCMM Subelements
	 */
	public Map<String, Object> toMapPCMMSubelements(List<PCMMSubelement> subelementList, final boolean withIds) {
		Map<String, Object> subelements = new LinkedHashMap<>();

		if (subelementList != null) {
			subelementList.forEach(subelt -> {
				String key = subelt.getCode();

				Map<String, Object> subeltAttributes = new LinkedHashMap<>();
				if (withIds) {
					subeltAttributes.put(YmlPCMMSchema.CONF_PCMM_SUBELEMENT_ID, subelt.getId());
				}
				subeltAttributes.put(YmlPCMMSchema.CONF_PCMM_SUBELEMENT_NAME, subelt.getName());
				subeltAttributes.put(YmlPCMMSchema.CONF_PCMM_SUBELEMENT_CODE, subelt.getCode());
				if (subelt.getLevelList() != null) {
					subeltAttributes.put(YmlPCMMSchema.CONF_PCMM_LEVELS,
							toMapPCMMLevel(subelt.getLevelList(), withIds));
				}
				subelements.put(key, subeltAttributes);
			});
		}

		return subelements;
	}

	/**
	 * @param levelList the PCMM level list
	 * @param withIds   add the id field to the export
	 * 
	 * @return a list of PCMM Levels
	 */
	public Map<String, Object> toMapPCMMLevel(List<PCMMLevel> levelList, final boolean withIds) {
		Map<String, Object> levels = new LinkedHashMap<>();

		if (levelList != null) {
			levelList.forEach(level -> {
				String key = level.getName();

				Map<String, Object> levelAttributes = new LinkedHashMap<>();
				if (withIds) {
					levelAttributes.put(YmlPCMMSchema.CONF_PCMM_LEVEL_ID, level.getId());
				}
				levelAttributes.put(YmlPCMMSchema.CONF_PCMM_LEVEL_NAME, level.getName());
				levelAttributes.put(YmlPCMMSchema.CONF_PCMM_LEVEL_CODE, level.getCode());
				if (level.getLevelDescriptorList() != null) {
					levelAttributes.put(YmlPCMMSchema.CONF_PCMM_LEVEL_DESCRIPTORS,
							toMapPCMMLevelDescriptor(level.getLevelDescriptorList()));
				}
				levels.put(key, levelAttributes);
			});
		}

		return levels;
	}

	/**
	 * @param levelDescList the PCMM level descriptor List
	 * @return a list of PCMM Level descriptors
	 */
	public Map<String, Object> toMapPCMMLevelDescriptor(List<PCMMLevelDescriptor> levelDescList) {
		Map<String, Object> levelsDesc = new LinkedHashMap<>();

		if (levelDescList != null) {
			levelDescList.forEach(levelDesc -> {
				String key = levelDesc.getName();
				String value = levelDesc.getValue();
				levelsDesc.put(key, value);
			});
		}

		return levelsDesc;
	}

	/**
	 * @param specification the PCMM specification
	 * @param withIds       integrate the objects ids into the export?
	 * @return a list of PCMM Planning
	 */
	public Map<String, Object> toMapPCMMPlanning(final PCMMSpecification specification, final boolean withIds) {
		Map<String, Object> planning = new LinkedHashMap<>();

		if (specification != null) {
			if (specification.getPlanningFields() != null) {
				planning.put(YmlPCMMSchema.CONF_PCMM_PLANNING_FIELDS, toMapPCMMPlanningFields(specification));
				planning.put(YmlPCMMSchema.CONF_PCMM_PLANNING_TYPES, toMapPCMMPlanningTypes(specification));
			}
			if (specification.getPlanningQuestions() != null) {
				planning.put(YmlPCMMSchema.CONF_PCMM_PLANNING_QUESTIONS, toMapPCMMPlanningQuestions(specification));
			}
		}

		return planning;
	}

	/**
	 * @param specification the PCMM specification
	 * @return a list of PCMM Planning
	 */
	public Map<String, Object> toMapPCMMPlanningFields(PCMMSpecification specification) {

		Map<String, Object> fields = new LinkedHashMap<>();

		if (specification != null && specification.getPlanningFields() != null) {

			// sort list by id
			List<PCMMPlanningParam> parameters = specification.getPlanningFields().stream().sorted(
					Comparator.comparing(PCMMPlanningParam::getId, Comparator.nullsLast(Comparator.naturalOrder())))
					.collect(Collectors.toList());

			parameters.forEach(param -> {
				if (param.getParent() == null) {
					fields.put(param.getName(), YmlWriterGenericSchema.getGenericParamValues(param, false));
				}
			});
		}

		return fields;
	}

	/**
	 * @param specification the PCMM specification
	 * @return a list of PCMM Planning
	 */
	public Map<String, Object> toMapPCMMPlanningTypes(PCMMSpecification specification) {
		Map<String, Object> types = new LinkedHashMap<>();

		if (specification != null && specification.getPlanningFields() != null) {

			// scan for custom parameters
			List<PCMMPlanningParam> typeList = getCustomPCMMPlanningParam(specification.getPlanningFields());

			// sort list by id
			typeList = typeList.stream().sorted(
					Comparator.comparing(PCMMPlanningParam::getId, Comparator.nullsLast(Comparator.naturalOrder())))
					.collect(Collectors.toList());

			// add custom parameters to map
			for (PCMMPlanningParam param : typeList) {
				String key = param.getType();
				types.put(key, toMapPCMMPlanningTypeChildren(param.getChildren()));
			}
		}

		return types;
	}

	/**
	 * Recursive method to search for the custom planning parameters
	 * 
	 * @param parameters all the parameters
	 * @return the list of custom parameters
	 */
	private List<PCMMPlanningParam> getCustomPCMMPlanningParam(List<PCMMPlanningParam> parameters) {
		List<PCMMPlanningParam> typeList = new ArrayList<>();

		if (parameters != null) {

			List<String> parameterTypes = new ArrayList<>(
					Stream.of(FormFieldType.values()).map(FormFieldType::getType).collect(Collectors.toList()));

			for (PCMMPlanningParam param : parameters) {
				// add the current param
				if (param.getType() != null && !parameterTypes.contains(param.getType())) {
					typeList.add(param);
				}

				// check the param children
				if (param.getChildren() != null && !param.getChildren().isEmpty()) {
					typeList.addAll(getCustomPCMMPlanningParam((param.getChildren().stream()
							.map(PCMMPlanningParam.class::cast).collect(Collectors.toList()))));
				}
			}
		}

		return typeList;
	}

	/**
	 * @param parameters the children parameters
	 * @return the list of children of a custom parameter
	 */
	private Map<String, Object> toMapPCMMPlanningTypeChildren(List<GenericParameter<PCMMPlanningParam>> parameters) {

		Map<String, Object> types = new LinkedHashMap<>();

		if (parameters != null) {

			// sort list by id
			parameters = parameters.stream().sorted(
					Comparator.comparing(GenericParameter::getId, Comparator.nullsLast(Comparator.naturalOrder())))
					.collect(Collectors.toList());

			for (GenericParameter<PCMMPlanningParam> param : parameters) {
				types.put(param.getName(), YmlWriterGenericSchema.getGenericParamValues(param, false));
			}
		}

		return types;

	}

	/**
	 * @param specification the PCMM specification
	 * @return a list of PCMM Planning
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> toMapPCMMPlanningQuestions(PCMMSpecification specification) {
		Map<String, Object> questionMap = new LinkedHashMap<>();

		if (specification != null && specification.getPlanningQuestions() != null) {

			// create the assessable content
			questionMap = toMapPCMMPlanningQuestionsAssessable(
					new ArrayList<>(specification.getPlanningQuestions().keySet()));

			// insert values in the map
			for (Entry<IAssessable, List<PCMMPlanningQuestion>> entry : specification.getPlanningQuestions()
					.entrySet()) {

				IAssessable assessable = entry.getKey();
				List<PCMMPlanningQuestion> value = entry.getValue();
				if (value != null) {
					if (assessable instanceof PCMMElement) {
						String key = ((PCMMElement) assessable).getAbbreviation();
						List<String> listQuestions = (List<String>) questionMap.get(key);
						listQuestions
								.addAll(value.stream().map(PCMMPlanningQuestion::getName).collect(Collectors.toList()));
						questionMap.put(key, listQuestions);
					} else if (assessable instanceof PCMMSubelement
							&& ((PCMMSubelement) assessable).getElement() != null) {
						String keySubelement = ((PCMMSubelement) assessable).getCode();
						String keyElement = ((PCMMSubelement) assessable).getElement().getAbbreviation();
						List<String> listQuestions = ((Map<String, List<String>>) questionMap.get(keyElement))
								.get(keySubelement);
						listQuestions
								.addAll(value.stream().map(PCMMPlanningQuestion::getName).collect(Collectors.toList()));
						((Map<String, List<String>>) questionMap.get(keyElement)).put(keySubelement, listQuestions);
					}
				}
			}
		}

		return questionMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> toMapPCMMPlanningQuestionsAssessable(List<IAssessable> assessableList) {

		Map<String, Object> questionMap = new LinkedHashMap<>();

		// create the assessable content
		if (assessableList != null) {
			assessableList.stream().forEach(assessable -> {
				if (assessable instanceof PCMMElement) {
					String key = ((PCMMElement) assessable).getAbbreviation();
					if (!questionMap.containsKey(key)) {
						questionMap.put(key, new ArrayList<String>());
					}
				} else if (assessable instanceof PCMMSubelement && ((PCMMSubelement) assessable).getElement() != null) {
					String keyElement = ((PCMMSubelement) assessable).getElement().getAbbreviation();
					if (!questionMap.containsKey(keyElement)) {
						// treemap to sort by name
						questionMap.put(keyElement, new TreeMap<>());
					}
					Map<String, Object> subelementMap = (Map<String, Object>) questionMap.get(keyElement);
					String keySubelement = ((PCMMSubelement) assessable).getCode();
					if (!subelementMap.containsKey(keySubelement)) {
						subelementMap.put(keySubelement, new ArrayList<String>());
					}
					questionMap.put(keyElement, subelementMap);
				}
			});
		}

		return questionMap;
	}

}
