/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.pirt;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.imports.IYmlReader;
import gov.sandia.cf.application.imports.YmlReaderGlobal;
import gov.sandia.cf.constants.configuration.YmlPIRTSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscTools;

/**
 * This class loads credibility configuration. The actual implementation is
 * stored in a yaml file
 * 
 * @author Didier Verstraete
 *
 */
public class YmlReaderPIRTSchema implements IYmlReader<PIRTSpecification> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlReaderPIRTSchema.class);

	/**
	 * Separators
	 */
	/** PIRT range separator */
	public static final String CONF_PIRT_RANGE_SEPARATOR = ";"; //$NON-NLS-1$
	/** PIRT RGB separator */
	public static final String CONF_PIRT_RGB_SEPARATOR = ","; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PIRTSpecification load(File ymlPIRTSchema) throws CredibilityException, IOException {
		return read(ymlPIRTSchema);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid(File ymlPIRTSchema) {

		try (FileReader fileReader = new FileReader(ymlPIRTSchema)) {

			// yaml reader
			Map<?, ?> yamlSpecifications = new Yaml().load(fileReader);

			if (yamlSpecifications != null) {

				if (yamlSpecifications.get(YmlPIRTSchema.CONF_PIRT) instanceof Map) {
					return true;
				}

				// retrieve level colors
				if (yamlSpecifications.containsKey(YmlPIRTSchema.CONF_PIRT_HEADER)) {
					return true;
				}

				// retrieve phases
				if (yamlSpecifications.containsKey(YmlPIRTSchema.CONF_PIRT_ADEQUACY)) {
					return true;
				}

				// retrieve roles
				if (yamlSpecifications.containsKey(YmlPIRTSchema.CONF_PIRT_LEVEL)) {
					return true;
				}

				// retrieve elements
				if (yamlSpecifications.containsKey(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR)) {
					return true;
				}

				// retrieve Planning
				if (yamlSpecifications.containsKey(YmlPIRTSchema.CONF_PIRT_GUIDELINES)) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.warn("PIRT file not valid", e); //$NON-NLS-1$
		}

		return false;
	}

	/**
	 * @param reader the file reader
	 * 
	 * @return a CredibilityProjectSpecification class loaded with @param reader.
	 * @throws CredibilityException if an error occurs during reading process
	 * @throws IOException          if a reading exception occurs
	 * 
	 */
	private PIRTSpecification read(File ymlSchema) throws CredibilityException, IOException {

		PIRTSpecification specifications = new PIRTSpecification();

		// read
		Map<String, Object> yamlSpecifications = YmlReaderGlobal.loadYmlFile(ymlSchema);

		Map<?, ?> yamlPIRTSpecifications = yamlSpecifications;
		if (yamlSpecifications.get(YmlPIRTSchema.CONF_PIRT) instanceof Map) {

			// read PCMM specifications
			yamlPIRTSpecifications = (Map<?, ?>) yamlSpecifications.get(YmlPIRTSchema.CONF_PIRT);
		}

		// read PIRT specifications
		readPIRT(specifications, yamlPIRTSpecifications);

		return specifications;
	}

	/**
	 * Populates the pirt specification with the map from the yml file
	 * 
	 * @param specifications         the pirt specification to populate
	 * @param yamlPIRTSpecifications the yml pirt file specification to copy
	 * @return the pirt specifications populated
	 */
	@SuppressWarnings("unchecked")
	private PIRTSpecification readPIRT(PIRTSpecification specifications, Map<?, ?> yamlPIRTSpecifications) {

		String warnMessage = "credibility configuration missing {} tag"; //$NON-NLS-1$

		// check pirt specifications
		if (yamlPIRTSpecifications == null) {
			logger.warn(warnMessage, YmlPIRTSchema.CONF_PIRT);
			return specifications;
		}

		// retrieve headers specifications
		if (yamlPIRTSpecifications.containsKey(YmlPIRTSchema.CONF_PIRT_HEADER)) {
			List<String> yamlHeaders = (List<String>) ((Map<String, Object>) yamlPIRTSpecifications
					.get(YmlPIRTSchema.CONF_PIRT_HEADER)).get(YmlPIRTSchema.CONF_PIRT_FIELDS);
			specifications.setHeaders(createPIRTDescriptionHeader(yamlHeaders));
		} else {
			logger.warn(warnMessage, YmlPIRTSchema.CONF_PIRT_HEADER);
		}

		// retrieve adequacy columns specifications
		if (yamlPIRTSpecifications.containsKey(YmlPIRTSchema.CONF_PIRT_ADEQUACY)) {
			Map<String, Map<String, Object>> yamlPIRTAdequacyColumns = (Map<String, Map<String, Object>>) ((Map<String, Object>) yamlPIRTSpecifications
					.get(YmlPIRTSchema.CONF_PIRT_ADEQUACY)).get(YmlPIRTSchema.CONF_PIRT_FIELDS);
			specifications.setAdequacyColumns(createPIRTTableAdequacyColumns(yamlPIRTAdequacyColumns));
		} else {
			logger.warn(warnMessage, YmlPIRTSchema.CONF_PIRT_ADEQUACY);
		}

		// retrieve levels specifications
		if (yamlPIRTSpecifications.containsKey(YmlPIRTSchema.CONF_PIRT_LEVEL)) {
			Map<String, Map<String, Object>> yamlLevels = (Map<String, Map<String, Object>>) ((Map<String, Object>) yamlPIRTSpecifications
					.get(YmlPIRTSchema.CONF_PIRT_LEVEL)).get(YmlPIRTSchema.CONF_PIRT_FIELDS);
			specifications.setLevels(createPIRTImportanceLevels(yamlLevels).stream()
					.collect(Collectors.toMap(PIRTLevelImportance::getIdLabel, o -> o)));
		} else {
			logger.warn(warnMessage, YmlPIRTSchema.CONF_PIRT_LEVEL);
		}

		// retrieve level differences colors specifications
		if (yamlPIRTSpecifications.containsKey(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR)) {
			Map<Object, Map<String, Object>> yamlLevelColoring = (Map<Object, Map<String, Object>>) ((Map<String, Object>) yamlPIRTSpecifications
					.get(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR)).get(YmlPIRTSchema.CONF_PIRT_FIELDS);
			specifications.setColors(createPIRTLevelColorDifference(yamlLevelColoring, specifications.getLevels()));
		} else {
			logger.warn(warnMessage, YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR);
		}

		// retrieve level differences colors specifications
		if (yamlPIRTSpecifications.containsKey(YmlPIRTSchema.CONF_PIRT_GUIDELINES)) {
			Map<String, Map<String, Object>> yamlAdequacyGuidelines = (Map<String, Map<String, Object>>) ((Map<String, Object>) yamlPIRTSpecifications
					.get(YmlPIRTSchema.CONF_PIRT_GUIDELINES)).get(YmlPIRTSchema.CONF_PIRT_GUIDELINES_GUIDELINESTAG);
			specifications.setPirtAdequacyGuidelines(createPIRTAdequacyColumnGuideline(yamlAdequacyGuidelines));
		} else {
			logger.warn(warnMessage, YmlPIRTSchema.CONF_PIRT_GUIDELINES);
		}

		return specifications;
	}

	/**
	 * @param yamlHeaderProperties the yaml header properties in a list
	 * @return a PIRTDescriptionHeader list from @param yamlHeaderProperties list
	 */
	private List<PIRTDescriptionHeader> createPIRTDescriptionHeader(List<String> yamlHeaderProperties) {

		List<PIRTDescriptionHeader> listDescriptionHeader = new ArrayList<>();

		if (yamlHeaderProperties != null) {
			for (String yamlHeader : yamlHeaderProperties) {
				PIRTDescriptionHeader header = new PIRTDescriptionHeader(yamlHeader, yamlHeader);
				listDescriptionHeader.add(header);
			}
		}

		return listDescriptionHeader;
	}

	/**
	 * @param yamlPIRTAdequacyColumns the yaml pirt adequacy columns in a map
	 * @return a PIRTTableAdequacyColumn list from @param yamlPIRTAdequacyColumns
	 *         list
	 */
	private List<PIRTAdequacyColumn> createPIRTTableAdequacyColumns(
			Map<String, Map<String, Object>> yamlPIRTAdequacyColumns) {

		List<PIRTAdequacyColumn> listAdequacyColumns = new ArrayList<>();

		if (yamlPIRTAdequacyColumns != null) {
			for (Entry<String, Map<String, Object>> entry : yamlPIRTAdequacyColumns.entrySet()) {
				if (entry != null) {
					String yamlColumnName = entry.getKey();
					Map<String, Object> yamlProperties = entry.getValue();
					// retrieve properties
					String yamlType = (String) yamlProperties.getOrDefault(YmlPIRTSchema.CONF_PIRT_ADEQUACY_TYPE,
							YmlPIRTSchema.CONF_PIRT_ADEQUACY_DEFAULT_TYPE_TEXT);

					// create new adequacy column
					PIRTAdequacyColumn adequacyColumn = new PIRTAdequacyColumn(yamlColumnName, yamlColumnName,
							yamlType);
					listAdequacyColumns.add(adequacyColumn);
				}
			}
		}

		return listAdequacyColumns;
	}

	/**
	 * @param yamlPIRTImportanceLevels the yaml pirt importance levels in a map
	 * @return a PIRTImportanceLevel list from @param yamlPIRTImportanceLevels list
	 */
	private List<PIRTLevelImportance> createPIRTImportanceLevels(
			Map<String, Map<String, Object>> yamlPIRTImportanceLevels) {

		List<PIRTLevelImportance> listImportanceLevels = new ArrayList<>();

		if (yamlPIRTImportanceLevels != null) {
			for (Entry<String, Map<String, Object>> mapEntry : yamlPIRTImportanceLevels.entrySet()) {

				if (mapEntry != null) {

					String yamlLevelName = mapEntry.getKey();
					Map<String, Object> yamlProperties = mapEntry.getValue();

					// retrieve properties
					Integer yamlNumericalValue = YmlReaderGlobal.getIntegerOrDefault(
							yamlProperties.getOrDefault(YmlPIRTSchema.CONF_PIRT_LEVEL_NUMERICALVALUE, 0), 0);

					String yamlLabel = (String) yamlProperties.getOrDefault(YmlPIRTSchema.CONF_PIRT_LEVEL_LABEL,
							YmlPIRTSchema.CONF_PIRT_LEVEL_LABEL_UNKNOWN);

					// create new importance level
					PIRTLevelImportance importanceLevel = new PIRTLevelImportance(yamlLevelName, yamlLevelName,
							yamlNumericalValue, yamlLabel, null, null);
					listImportanceLevels.add(importanceLevel);
				}
			}
		}

		return listImportanceLevels;
	}

	/**
	 * @param yamlPIRTLevelColor  the pirt level colors in a map
	 * @param pirtLevelImportance the pirt level importances
	 * @return a PIRTColorDifference list from @param yamlPIRTLevelColor list
	 */
	private List<PIRTLevelDifferenceColor> createPIRTLevelColorDifference(
			Map<Object, Map<String, Object>> yamlPIRTLevelColor, Map<String, PIRTLevelImportance> pirtLevelImportance) {

		List<PIRTLevelDifferenceColor> colors = new ArrayList<>();

		if (yamlPIRTLevelColor != null) {

			// difference positive or equals to zero
			String rgbPositiveOrZero = getPIRTLevelColor(yamlPIRTLevelColor,
					YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_POS_OR_ZERO);
			String explanationPositiveOrZero = (String) yamlPIRTLevelColor
					.get(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_POS_OR_ZERO)
					.getOrDefault(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_EXPLANATION, RscTools.empty());

			PIRTLevelDifferenceColor colorPositiveOrZero = new PIRTLevelDifferenceColor(rgbPositiveOrZero,
					stringRangeToMinMax(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_POS_OR_ZERO_RANGE),
					YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_POS_OR_ZERO, explanationPositiveOrZero);

			// one level of difference
			String rgbOneLevel = getPIRTLevelColor(yamlPIRTLevelColor, YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_ONE_LEVEL);
			String explanationOneLevel = (String) yamlPIRTLevelColor.get(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_ONE_LEVEL)
					.getOrDefault(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_EXPLANATION, RscTools.empty());

			PIRTLevelDifferenceColor colorOneLevel = new PIRTLevelDifferenceColor(rgbOneLevel,
					stringRangeToMinMax(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_ONE_LEVEL_RANGE),
					YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_ONE_LEVEL, explanationOneLevel);

			// two levels of difference or more
			String rgbTwoOrMoreLevels = getPIRTLevelColor(yamlPIRTLevelColor,
					YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_TWO_LEVELS_OR_MORE);
			String explanationTwoOrMoreLevels = (String) yamlPIRTLevelColor
					.get(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_TWO_LEVELS_OR_MORE)
					.getOrDefault(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_EXPLANATION, RscTools.empty());

			PIRTLevelDifferenceColor colorTwoOrMoreLevels = new PIRTLevelDifferenceColor(rgbTwoOrMoreLevels,
					stringRangeToMinMax(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_TWO_LEVELS_OR_MORE_RANGE),
					YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_TWO_LEVELS_OR_MORE, explanationTwoOrMoreLevels);

			// Not Adressed
			if (pirtLevelImportance.containsKey(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_NOT_ADDRESSED)) {

				String rgbNotAdressed = getPIRTLevelColor(yamlPIRTLevelColor,
						YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_NOT_ADDRESSED);
				String explanationNotAdressed = (String) yamlPIRTLevelColor
						.get(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_NOT_ADDRESSED)
						.getOrDefault(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_EXPLANATION, RscTools.empty());

				// set pirt level importance fixed color
				pirtLevelImportance.get(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_NOT_ADDRESSED)
						.setFixedColor(rgbNotAdressed);
				pirtLevelImportance.get(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_NOT_ADDRESSED)
						.setFixedColorDescription(explanationNotAdressed);
			}

			// Not Applicable
			if (pirtLevelImportance.containsKey(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_NA)) {

				String rgbNotApplicable = getPIRTLevelColor(yamlPIRTLevelColor, YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_NA);
				String explanationNotApplicable = (String) yamlPIRTLevelColor
						.get(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_NA)
						.getOrDefault(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_EXPLANATION, RscTools.empty());

				// set pirt level importance fixed color
				pirtLevelImportance.get(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_NA).setFixedColor(rgbNotApplicable);
				pirtLevelImportance.get(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_NA)
						.setFixedColorDescription(explanationNotApplicable);

			}

			colors.add(colorPositiveOrZero);
			colors.add(colorOneLevel);
			colors.add(colorTwoOrMoreLevels);
		}
		return colors;
	}

	/**
	 * @param yamlPIRTLevelColor the pirt specification map containing colors
	 * @param colorLevelTag      the color level tag to get
	 * @return the pirt level color for the specified tag as a RGB
	 */
	private String getPIRTLevelColor(Map<Object, Map<String, Object>> yamlPIRTLevelColor, String colorLevelTag) {
		return yamlPIRTLevelColor.containsKey(colorLevelTag) ? (String) yamlPIRTLevelColor.get(colorLevelTag)
				.getOrDefault(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_RGB, ColorTools.DEFAULT_STRINGRGB_COLOR)
				: ColorTools.DEFAULT_STRINGRGB_COLOR;
	}

	/**
	 * @param yamlPIRTPIRTAdequacyGuideline the yaml pirt adequacy columns
	 *                                      guidelines in a map
	 * @return a yamlPIRTPIRTAdequacyGuideline list from @param
	 *         yamlPIRTPIRTAdequacyGuideline list
	 */
	@SuppressWarnings("unchecked")
	private List<PIRTAdequacyColumnGuideline> createPIRTAdequacyColumnGuideline(
			Map<String, Map<String, Object>> yamlPIRTPIRTAdequacyGuideline) {

		List<PIRTAdequacyColumnGuideline> listAdequacyColumnGuidelines = new ArrayList<>();

		if (yamlPIRTPIRTAdequacyGuideline != null) {
			for (Entry<String, Map<String, Object>> entry : yamlPIRTPIRTAdequacyGuideline.entrySet()) {
				if (entry != null) {

					String name = entry.getKey();
					Map<String, Object> yamlProperties = entry.getValue();

					if (name != null && !name.isEmpty()) {

						// retrieve properties
						String description = (String) yamlProperties.getOrDefault(
								YmlPIRTSchema.CONF_PIRT_GUIDELINES_GUIDELINES_DESCTAG,
								YmlPIRTSchema.CONF_PIRT_LEVEL_LABEL_UNKNOWN);

						Map<String, String> levelGuidelines = (Map<String, String>) yamlProperties
								.getOrDefault(YmlPIRTSchema.CONF_PIRT_GUIDELINES_GUIDELINES_LEVELTAG, null);

						// create new adequacy guideline
						PIRTAdequacyColumnGuideline adequacyGuideline = new PIRTAdequacyColumnGuideline(name,
								description, levelGuidelines);

						listAdequacyColumnGuidelines.add(adequacyGuideline);
					}
				}
			}
		}

		return listAdequacyColumnGuidelines;
	}

	/**
	 * @param range the color range in a string
	 * @return RGB class from String @param rgb. If @param rgb is null, empty or
	 *         does not contains valid rgb description, return null
	 * 
	 *         Example: valid: 0,255,0 invalid: 0255,55
	 */
	private List<Integer> stringRangeToMinMax(String range) {

		List<Integer> minMax = new ArrayList<>();

		if (range != null) {
			// trim to delete white spaces
			range = range.trim();

			// split range to retrieve int min and max
			String[] splittedRange = range.split(YmlReaderPIRTSchema.CONF_PIRT_RANGE_SEPARATOR);

			Integer min = null;
			Integer max = null;

			// splitted minMax must have 2 values
			if (splittedRange != null) {
				if (splittedRange.length == 2) {
					min = Integer.valueOf(splittedRange[0]);
					minMax.add(min);
					max = Integer.valueOf(splittedRange[1]);
					minMax.add(max);
				} else if (splittedRange.length == 1) {
					minMax.add(Integer.valueOf(range));
					minMax.add(Integer.valueOf(range));
				}
			}
		}
		return minMax;
	}
}
