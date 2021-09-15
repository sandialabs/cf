/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.pcmm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.configuration.IYmlReader;
import gov.sandia.cf.application.configuration.YmlGenericSchema;
import gov.sandia.cf.application.configuration.YmlReaderGenericSchema;
import gov.sandia.cf.application.configuration.YmlReaderGlobal;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningParamConstraint;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMPlanningSelectValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscTools;

/**
 * This class loads credibility PCMM configuration. The actual implementation is
 * stored in a yaml file.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlReaderPCMMSchema implements IYmlReader<PCMMSpecification> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlReaderPCMMSchema.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMSpecification load(File ymlSchema) throws CredibilityException, IOException {
		return read(ymlSchema);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(File ymlPCMMSchema) {

		try (FileReader fileReader = new FileReader(ymlPCMMSchema)) {

			// yaml reader
			Map<?, ?> yamlSpecifications = new Yaml().load(fileReader);

			if (yamlSpecifications != null) {

				if (yamlSpecifications.get(YmlPCMMSchema.CONF_PCMM) instanceof Map) {
					return true;
				}

				// retrieve level colors
				if (yamlSpecifications.containsKey(YmlPCMMSchema.CONF_PCMM_LEVEL_COLORS)) {
					return true;
				}

				// retrieve phases
				if (yamlSpecifications.containsKey(YmlPCMMSchema.CONF_PCMM_PHASES)) {
					return true;
				}

				// retrieve roles
				if (yamlSpecifications.containsKey(YmlPCMMSchema.CONF_PCMM_ROLES)) {
					return true;
				}

				// retrieve elements
				if (yamlSpecifications.containsKey(YmlPCMMSchema.CONF_PCMM_ELEMENTS)) {
					return true;
				}

				// retrieve Planning
				if (yamlSpecifications.containsKey(YmlPCMMSchema.CONF_PCMM_PLANNING)) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.warn("PCMM file not valid", e); //$NON-NLS-1$
		}

		return false;
	}

	/**
	 * 
	 * @param yamlPCMMSpecifications the credibility project the specifications from
	 *                               the yaml specifications file
	 * @param key                    the key to search for
	 * @param tValue                 the expected type of the value
	 * @return the found value associated to the key, or null if the key does not
	 *         exist or if the value type does not correspond to tValue
	 */
	private Object readYamlPCMMSpecificationsValue(Map<?, ?> yamlPCMMSpecifications, String key, Class<?> tValue) {
		// log variable
		String errorMessageFormat = "Credibility configuration missing {} tag"; //$NON-NLS-1$

		Object v = yamlPCMMSpecifications.get(key);

		if (tValue.isInstance(v)) {
			return v;
		} else {
			logger.warn(errorMessageFormat, key);
			return null;
		}
	}

	/**
	 * @param reader         the reader
	 * @param specifications the credibility project specifications
	 * @return a CredibilityProjectSpecification class loaded with @param reader.
	 * @throws CredibilityException if an error occured while processing the queries
	 * @throws IOException          if a reading exception occurs
	 */
	@SuppressWarnings("unchecked")
	private PCMMSpecification read(File ymlSchema) throws CredibilityException, IOException {

		PCMMSpecification specifications = new PCMMSpecification();

		// read
		Map<String, Object> yamlSpecifications = YmlReaderGlobal.loadYmlFile(ymlSchema);

		if (yamlSpecifications != null) {

			if (yamlSpecifications.get(YmlPCMMSchema.CONF_PCMM) instanceof Map) {

				// read PCMM specifications
				yamlSpecifications = (Map<String, Object>) yamlSpecifications.get(YmlPCMMSchema.CONF_PCMM);
			}

			// retrieve level colors
			Map<?, ?> yamlLevelColors = (Map<?, ?>) readYamlPCMMSpecificationsValue(yamlSpecifications,
					YmlPCMMSchema.CONF_PCMM_LEVEL_COLORS, Map.class);
			if (yamlLevelColors != null) {
				specifications.setLevelColors(createLevelColors(yamlLevelColors));
			}

			// retrieve phases
			List<?> yamlPhases = (List<?>) readYamlPCMMSpecificationsValue(yamlSpecifications,
					YmlPCMMSchema.CONF_PCMM_PHASES, List.class);
			if (yamlPhases != null) {
				specifications.setPhases(createPhases(yamlPhases));
				specifications.setOptions(createOptions(specifications.getPhases()));
			}

			// retrieve roles
			List<?> yamlRoles = (List<?>) readYamlPCMMSpecificationsValue(yamlSpecifications,
					YmlPCMMSchema.CONF_PCMM_ROLES, List.class);
			if (yamlRoles != null) {
				specifications.setRoles(createRoles(yamlRoles));
			}

			// retrieve elements
			Map<?, ?> elements = (Map<?, ?>) readYamlPCMMSpecificationsValue(yamlSpecifications,
					YmlPCMMSchema.CONF_PCMM_ELEMENTS, Map.class);
			if (elements != null) {
				specifications.setElements(createElements(elements));
			}

			// retrieve Planning
			Map<?, ?> planning = (Map<?, ?>) readYamlPCMMSpecificationsValue(yamlSpecifications,
					YmlPCMMSchema.CONF_PCMM_PLANNING, Map.class);
			if (planning != null) {
				populatePlanning(specifications, planning, specifications.getElements());
			}
		}

		// check PCMM Mode:
		specifications.setMode(getPCMMMode(specifications.getElements()));
		return specifications;

	}

	/**
	 * By default the mode is DEFAULT. If there is at least one level associated to
	 * one PCMM Element, the mode returned is SIMPLIFIED.
	 * 
	 * @param elements the PCMM Elements to check the mode
	 * @return the PCMM mode activated
	 */
	private PCMMMode getPCMMMode(List<PCMMElement> elements) {
		PCMMMode mode = PCMMMode.DEFAULT;
		if (elements != null) {
			boolean elementsHaveLevels = false;
			for (PCMMElement element : elements) {
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
	 * Reads a yaml entry containing level color information
	 * 
	 * @param entry the yaml level color entry
	 * @return the color or null if the entry is not relevant.
	 */
	private PCMMLevelColor readLevelColorFromYamlLevelColorEntry(Entry<?, ?> entry) {
		if (entry != null && entry.getKey() instanceof String && entry.getValue() instanceof Map) {
			String levelKey = (String) entry.getKey();
			Map<?, ?> levelProperties = (Map<?, ?>) entry.getValue();

			// retrieve code
			Integer code = null;
			if (levelProperties.containsKey(YmlPCMMSchema.CONF_PCMM_LEVEL_CODE)) {
				code = YmlReaderGlobal
						.getInteger(levelProperties.getOrDefault(YmlPCMMSchema.CONF_PCMM_LEVEL_CODE, null));
			}

			// retrieve color
			String fixedColor = levelProperties.containsKey(YmlPCMMSchema.CONF_PCMM_LEVEL_COLOR)
					? (String) levelProperties.get(YmlPCMMSchema.CONF_PCMM_LEVEL_COLOR)
					: ColorTools.DEFAULT_STRINGRGB_COLOR;

			return new PCMMLevelColor(code, levelKey, fixedColor);
		}
		return null;
	}

	/**
	 * @param yamlLevelColors the yaml level colors
	 * @return a list of PCMMLevelColor classes from parameters definition
	 */
	private Map<Integer, PCMMLevelColor> createLevelColors(Map<?, ?> yamlLevelColors) {

		Map<Integer, PCMMLevelColor> listLevelColor = new HashMap<>();

		if (yamlLevelColors != null) {

			for (Entry<?, ?> entry : yamlLevelColors.entrySet()) {
				PCMMLevelColor levelColor = readLevelColorFromYamlLevelColorEntry(entry);
				if (levelColor != null)
					listLevelColor.put(levelColor.getCode(), levelColor);
			}
		}
		return listLevelColor;
	}

	/**
	 * @param yamlPhases the yaml phases
	 * @return a list of PCMMPhases classes from parameters definition
	 */
	private List<PCMMPhase> createPhases(List<?> yamlPhases) {

		List<PCMMPhase> listPhases = new ArrayList<>();

		if (yamlPhases != null) {
			for (Object phase : yamlPhases) {
				if (phase instanceof String) {
					PCMMPhase pcmmPhase = PCMMPhase.getPhaseFromName((String) phase);
					if (pcmmPhase != null && !listPhases.contains(pcmmPhase)) {
						listPhases.add(pcmmPhase);
					}
				}
			}
		}
		return listPhases;
	}

	/**
	 * @param yamlPhases the yaml phases
	 * @return a list of PCMMOption classes from parameters definition
	 */
	private List<PCMMOption> createOptions(List<PCMMPhase> phases) {

		List<PCMMOption> listOptions = new ArrayList<>();

		if (phases != null) {
			for (PCMMPhase phase : phases) {
				PCMMOption pcmmOption = new PCMMOption();
				pcmmOption.setPhase(phase);
				listOptions.add(pcmmOption);
			}
		}
		return listOptions;
	}

	/**
	 * @param yamlRoles the yaml phases
	 * @return a list of Role classes from parameters definition
	 */
	private List<Role> createRoles(List<?> yamlRoles) {

		List<Role> listRoles = new ArrayList<>();

		if (yamlRoles != null) {
			for (Object roleName : yamlRoles) {
				if (roleName instanceof String) {
					Role pcmmRole = new Role();
					pcmmRole.setName((String) roleName);
					listRoles.add(pcmmRole);
				}
			}
		}
		return listRoles;
	}

	/**
	 * @param elements the hashmap of yaml elements
	 * @return a list of PCMM elements. If the parameter is null, return an empty
	 *         list.
	 */
	@SuppressWarnings("unchecked")
	private List<PCMMElement> createElements(Map<?, ?> elements) {

		List<PCMMElement> listElements = new ArrayList<>();

		if (elements != null) {
			for (Object mapValue : elements.values()) {
				if (mapValue instanceof Map) {
					Map<String, Object> yamlProperties = (Map<String, Object>) mapValue;

					// retrieve name
					String name = (String) yamlProperties.getOrDefault(YmlPCMMSchema.CONF_PCMM_ELEMENT_NAME,
							RscTools.empty());

					// retrieve color
					String color = (String) yamlProperties.getOrDefault(YmlPCMMSchema.CONF_PCMM_ELEMENT_COLOR,
							ColorTools.DEFAULT_STRINGRGB_COLOR);

					// retrieve abbreviation
					String abbreviation = (String) yamlProperties.getOrDefault(YmlPCMMSchema.CONF_PCMM_ELEMENT_ABBREV,
							RscTools.empty());

					// retrieve levels
					Map<String, Object> levels = (Map<String, Object>) yamlProperties
							.getOrDefault(YmlPCMMSchema.CONF_PCMM_LEVELS, null);

					// retrieve subelements
					Map<String, Object> subelements = (Map<String, Object>) yamlProperties
							.getOrDefault(YmlPCMMSchema.CONF_PCMM_SUBELEMENTS, null);

					PCMMElement pcmmElement = new PCMMElement();
					pcmmElement.setName(name);
					pcmmElement.setColor(color);
					pcmmElement.setAbbreviation(abbreviation);
					pcmmElement.setSubElementList(createSubelements(subelements));
					pcmmElement.setLevelList(createLevels(levels));
					listElements.add(pcmmElement);
				}
			}
		}
		return listElements;
	}

	/**
	 * @param elements the hashmap of yaml subelements
	 * @return a list of PCMM subelements. If the parameter is null, return an empty
	 *         list.
	 */
	@SuppressWarnings("unchecked")
	private List<PCMMSubelement> createSubelements(Map<String, Object> elements) {

		List<PCMMSubelement> listElements = new ArrayList<>();

		if (elements != null) {
			for (Object mapValue : elements.values()) {
				if (mapValue instanceof Map) {
					Map<String, Object> yamlProperties = (Map<String, Object>) mapValue;

					// retrieve name
					String name = (String) yamlProperties.getOrDefault(YmlPCMMSchema.CONF_PCMM_SUBELEMENT_NAME,
							RscTools.empty());

					// retrieve code
					String code = (String) yamlProperties.getOrDefault(YmlPCMMSchema.CONF_PCMM_SUBELEMENT_CODE,
							RscTools.empty());

					// retrieve levels
					Map<String, Object> levels = (Map<String, Object>) yamlProperties
							.getOrDefault(YmlPCMMSchema.CONF_PCMM_LEVELS, null);

					PCMMSubelement pcmmSubElement = new PCMMSubelement();
					pcmmSubElement.setName(name);
					pcmmSubElement.setCode(code);
					pcmmSubElement.setLevelList(createLevels(levels));
					listElements.add(pcmmSubElement);
				}
			}
		}
		return listElements;
	}

	/**
	 * @param elements the hashmap of yaml levels
	 * @return a list of PCMM levels. If the parameter is null, return an empty
	 *         list.
	 */
	@SuppressWarnings("unchecked")
	private List<PCMMLevel> createLevels(Map<String, Object> elements) {

		List<PCMMLevel> listElements = new ArrayList<>();

		if (elements != null) {

			for (Object mapValue : elements.values()) {
				if (mapValue instanceof Map) {
					Map<String, Object> yamlProperties = (Map<String, Object>) mapValue;

					// retrieve code
					Integer code = (Integer) yamlProperties.getOrDefault(YmlPCMMSchema.CONF_PCMM_LEVEL_CODE, null);

					// retrieve name
					String name = (String) yamlProperties.getOrDefault(YmlPCMMSchema.CONF_PCMM_LEVEL_NAME,
							RscTools.empty());

					// retrieve descriptors
					Map<String, Object> descriptors = (Map<String, Object>) yamlProperties
							.getOrDefault(YmlPCMMSchema.CONF_PCMM_LEVEL_DESCRIPTORS, null);

					PCMMLevel pcmmLevel = new PCMMLevel();
					pcmmLevel.setName(name);
					pcmmLevel.setCode(code);
					pcmmLevel.setLevelDescriptorList(createLevelDescriptors(descriptors));
					listElements.add(pcmmLevel);
				}
			}
		}
		return listElements;
	}

	/**
	 * @param elements the hashmap of yaml level descriptors
	 * @return a list of PCMM level descriptors. If the parameter is null, return an
	 *         empty list.
	 */
	private List<PCMMLevelDescriptor> createLevelDescriptors(Map<String, Object> elements) {

		List<PCMMLevelDescriptor> listElements = new ArrayList<>();

		if (elements != null) {
			for (Entry<String, Object> entry : elements.entrySet()) {
				if (entry != null) {
					String elementKey = entry.getKey();
					PCMMLevelDescriptor pcmmDescriptor = new PCMMLevelDescriptor();
					pcmmDescriptor.setName(elementKey);
					pcmmDescriptor.setValue((String) entry.getValue());
					listElements.add(pcmmDescriptor);
				}
			}
		}
		return listElements;
	}

	/**
	 * @param specifications the pcmm specifications to populate
	 * @return the PCMM specifications with the planning included
	 */
	private PCMMSpecification populatePlanning(PCMMSpecification specifications, Map<?, ?> planning,
			List<PCMMElement> elements) {

		if (specifications != null) {

			// retrieve Planning Fields
			Object planningFields = planning.get(YmlPCMMSchema.CONF_PCMM_PLANNING_FIELDS);

			// retrieve Planning Types
			Object planningTypes = planning.get(YmlPCMMSchema.CONF_PCMM_PLANNING_TYPES);

			// retrieve Planning Questions
			Object planningQuestions = planning.get(YmlPCMMSchema.CONF_PCMM_PLANNING_QUESTIONS);

			if (planningFields instanceof Map) {
				Map<?, ?> planningTypesMap = new HashMap<>();
				if (planningTypes instanceof Map) {
					planningTypesMap = (Map<?, ?>) planningTypes;
				}
				specifications.setPlanningFields(populatePlanningFields((Map<?, ?>) planningFields, planningTypesMap));
			}
			if (planningQuestions instanceof Map) {
				specifications.setPlanningQuestions(populatePlanningQuestions((Map<?, ?>) planningQuestions, elements));
			}
		}

		return specifications;
	}

	/**
	 * @param planningFields the yml map of planning fields
	 * @return the planning fields.
	 */
	@SuppressWarnings("unchecked")
	private List<PCMMPlanningParam> populatePlanningFields(Map<?, ?> planningFields, Map<?, ?> planningTypes) {
		// Initialize list
		List<PCMMPlanningParam> listFields = new ArrayList<>();

		if (planningFields != null) {
			// For each parameter from configuration yml
			planningFields.entrySet().forEach(entry -> {
				if (entry != null && entry.getKey() instanceof String && entry.getValue() instanceof Map) {

					// Create PCMMPlanningField
					PCMMPlanningParam field = null;
					try {
						field = YmlReaderGenericSchema.createGenericParameter(PCMMPlanningParam.class,
								PCMMPlanningSelectValue.class, PCMMPlanningParamConstraint.class,
								(Entry<String, Map<String, Object>>) entry);
					} catch (InstantiationException | IllegalAccessException e) {
						logger.warn(e.getMessage(), e);
					}

					field = populatePlanningField(field, planningTypes);

					// add field to list
					listFields.add(field);
				}
			});
		}
		return listFields;
	}

	/**
	 * @param field
	 * @param planningTypes
	 * @return the planning field populated with the correct type
	 */
	private PCMMPlanningParam populatePlanningField(PCMMPlanningParam field, Map<?, ?> planningTypes) {

		// Add planning types
		if (planningTypes != null && field != null) {
			// For each parameter from configuration yml
			for (Entry<?, ?> subentry : planningTypes.entrySet()) {
				if (subentry != null && subentry.getKey() instanceof String && subentry.getValue() instanceof Map) {
					String subkey = (String) subentry.getKey();
					Map<?, ?> subvalue = (Map<?, ?>) subentry.getValue();

					if (subkey.equals(field.getType())) {
						// Create PCMMPlanningType
						List<PCMMPlanningParam> populatePlanningFields = populatePlanningFields(subvalue, null);
						field.setChildren(populatePlanningFields.stream().filter(p -> p instanceof PCMMPlanningParam)
								.map(PCMMPlanningParam.class::cast).collect(Collectors.toList()));
					}
				}
			}
		}

		return field;
	}

	/**
	 * @param planningQuestions the yml map of planning questions
	 * @param elements          the pcmm elements used to associate the key to the
	 *                          right pcmm object
	 * @return a map of assessable and questions.
	 */
	private Map<IAssessable, List<PCMMPlanningQuestion>> populatePlanningQuestions(Map<?, ?> planningQuestions,
			List<PCMMElement> elements) {
		// Initialize list
		Map<IAssessable, List<PCMMPlanningQuestion>> mapQuestions = new HashMap<>();

		if (planningQuestions != null && elements != null) {

			// flat map to take off map hierarchy
			Map<String, List<?>> mapQuestionsUnassociated = complexMapToFlatMap(planningQuestions);

			// search for assessable
			for (Entry<String, List<?>> entry : mapQuestionsUnassociated.entrySet()) {
				if (entry != null && entry.getValue() != null) {
					IAssessable assessable = getAssessableFromKey(entry.getKey(), elements);

					mapQuestions.put(assessable, populatePlanningQuestion(assessable, entry.getValue()));
				}
			}
		}
		return mapQuestions;
	}

	/**
	 * @param assessable the assessable to associate the questions with
	 * @param values     the pcmm questions as string
	 * @return a list of pcmm planning questions for the assessable in parameter
	 */
	private List<PCMMPlanningQuestion> populatePlanningQuestion(IAssessable assessable, List<?> values) {
		List<PCMMPlanningQuestion> questions = new ArrayList<>();
		if (assessable != null && values != null) {
			for (String questionString : values.stream().filter(Objects::nonNull).map(Object::toString)
					.collect(Collectors.toList())) {
				PCMMPlanningQuestion question = new PCMMPlanningQuestion();
				if (assessable instanceof PCMMElement) {
					question.setElement((PCMMElement) assessable);
				} else if (assessable instanceof PCMMSubelement) {
					question.setSubelement((PCMMSubelement) assessable);
				}
				question.setName(questionString);
				question.setRequired(YmlGenericSchema.CONF_GENERIC_OPTIONAL_VALUE);
				question.setType(FormFieldType.RICH_TEXT.getType());
				questions.add(question);
			}
		}
		return questions;
	}

	/**
	 * Take off the map hierarchy and add the subkeys at the same level as the root
	 * keys. If a key is already defined, override it.
	 * 
	 * @param complexMap the complex hierachical map to flat
	 * @return a map containing the complex map in the top level
	 */
	private Map<String, List<?>> complexMapToFlatMap(Map<?, ?> complexMap) {

		Map<String, List<?>> flatMap = new HashMap<>();

		if (complexMap != null) {
			for (Entry<?, ?> entry : complexMap.entrySet()) {
				if (entry != null && entry.getKey() != null) {
					String key = entry.getKey().toString();
					if (entry.getValue() instanceof List) {
						flatMap.put(key, (List<?>) entry.getValue());
					} else if (entry.getValue() instanceof Map) {
						flatMap.putAll(complexMapToFlatMap((Map<?, ?>) entry.getValue()));
					}
				}
			}
		}
		return flatMap;
	}

	/**
	 * @param key      the key to find
	 * @param elements the pcmm elements list
	 * @return the assessable pcmm element or subelement if found matching param
	 *         key, otherwise null.
	 */
	private IAssessable getAssessableFromKey(String key, List<PCMMElement> elements) {
		IAssessable assessable = null;

		if (key != null && elements != null) {
			for (PCMMElement element : elements.stream().filter(Objects::nonNull).collect(Collectors.toList())) {
				if (key.equals(element.getAbbreviation())) {
					return element;
				} else if (element.getSubElementList() != null) {
					PCMMSubelement subelement = getSubelementFromKey(key, element.getSubElementList());
					if (subelement != null) {
						subelement.setElement(element);
						return subelement;
					}
				}
			}
		}
		return assessable;
	}

	/**
	 * @param key         the key to find
	 * @param subelements the pcmm subelements list
	 * @return the assessable pcmm subelement if found matching param key, otherwise
	 *         null.
	 */
	private PCMMSubelement getSubelementFromKey(String key, List<PCMMSubelement> subelements) {
		PCMMSubelement assessable = null;

		if (key != null && subelements != null) {
			for (PCMMSubelement subelement : subelements.stream().filter(Objects::nonNull)
					.collect(Collectors.toList())) {
				if (key.equals(subelement.getCode())) {
					return subelement;
				}
			}
		}
		return assessable;
	}
}
