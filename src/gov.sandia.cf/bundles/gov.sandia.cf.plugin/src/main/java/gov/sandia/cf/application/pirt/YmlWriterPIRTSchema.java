/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.pirt;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.exports.IYmlSchemaWriter;
import gov.sandia.cf.application.imports.YmlReaderGlobal;
import gov.sandia.cf.constants.configuration.YmlPIRTSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * This class write credibility PIRT configuration. The actual implementation is
 * stored in a yaml file
 * 
 * @author Didier Verstraete
 *
 */
public class YmlWriterPIRTSchema implements IYmlSchemaWriter<PIRTSpecification> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlWriterPIRTSchema.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void writeSchema(final File cfSchemaFile, final PIRTSpecification specification, final boolean withIds,
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

		logger.debug("Write PIRT specification to the yml file {}", cfSchemaFile.getAbsolutePath()); //$NON-NLS-1$

		// load global specs
		Map<String, Object> mapSpecs = YmlReaderGlobal.loadYmlFile(cfSchemaFile);

		if (mapSpecs.get(YmlPIRTSchema.CONF_PIRT) instanceof Map) {
			mapSpecs = (Map<String, Object>) mapSpecs.get(YmlPIRTSchema.CONF_PIRT);
		}

		// replace PIRT Headers
		if (mapSpecs.get(YmlPIRTSchema.CONF_PIRT_HEADER) instanceof Map) {
			mapSpecs.remove(YmlPIRTSchema.CONF_PIRT_HEADER);
		}
		mapSpecs.put(YmlPIRTSchema.CONF_PIRT_HEADER, toMapPIRTHeaders(specification));

		// replace PIRT Adequacy columns
		if (mapSpecs.get(YmlPIRTSchema.CONF_PIRT_ADEQUACY) instanceof Map) {
			mapSpecs.remove(YmlPIRTSchema.CONF_PIRT_ADEQUACY);
		}
		mapSpecs.put(YmlPIRTSchema.CONF_PIRT_ADEQUACY, toMapPIRTAdequacyColumns(specification));

		// replace PIRT Levels
		if (mapSpecs.get(YmlPIRTSchema.CONF_PIRT_LEVEL) instanceof Map) {
			mapSpecs.remove(YmlPIRTSchema.CONF_PIRT_LEVEL);
		}
		mapSpecs.put(YmlPIRTSchema.CONF_PIRT_LEVEL, toMapPIRTLevels(specification));

		// replace PIRT colors
		if (mapSpecs.get(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR) instanceof Map) {
			mapSpecs.remove(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR);
		}
		mapSpecs.put(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR, toMapPIRTLevelDifferenceColor(specification));

		// replace PIRT Guidelines
		if (mapSpecs.get(YmlPIRTSchema.CONF_PIRT_GUIDELINES) instanceof Map) {
			mapSpecs.remove(YmlPIRTSchema.CONF_PIRT_GUIDELINES);
		}
		mapSpecs.put(YmlPIRTSchema.CONF_PIRT_GUIDELINES, toMapPIRTGuidelines(specification));

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
	 * @param specification the PIRT specification
	 * @return a map of PIRT headers
	 */
	public Map<String, Object> toMapPIRTHeaders(PIRTSpecification specification) {
		Map<String, Object> headers = new LinkedHashMap<>();

		if (specification != null && specification.getHeaders() != null) {
			headers.put(YmlPIRTSchema.CONF_PIRT_FIELDS,
					specification.getHeaders().stream()
							.sorted(Comparator.comparing(PIRTDescriptionHeader::getId,
									Comparator.nullsLast(Comparator.naturalOrder())))
							.map(PIRTDescriptionHeader::getName).collect(Collectors.toList()));
		}

		return headers;
	}

	/**
	 * @param specification the PIRT specification
	 * @return a map of PIRT Adequacy columns
	 */
	public Map<String, Object> toMapPIRTAdequacyColumns(PIRTSpecification specification) {

		// columns map
		Map<String, Object> columns = new LinkedHashMap<>();

		if (specification != null && specification.getColumns() != null) {

			// columns fields
			Map<String, Object> columnName = new LinkedHashMap<>();
			List<PIRTAdequacyColumn> sorted = specification.getColumns().stream().sorted(
					Comparator.comparing(PIRTAdequacyColumn::getId, Comparator.nullsLast(Comparator.naturalOrder())))
					.collect(Collectors.toList());
			sorted.forEach(column -> {

				// column content
				Map<String, Object> columnContent = new LinkedHashMap<>();
				columnContent.put(YmlPIRTSchema.CONF_PIRT_ADEQUACY_TYPE, column.getType());

				columnName.put(column.getName(), columnContent);
			});

			columns.put(YmlPIRTSchema.CONF_PIRT_FIELDS, columnName);
		}

		return columns;
	}

	/**
	 * @param specification the PIRT specification
	 * @return a map of PIRT Levels
	 */
	public Map<String, Object> toMapPIRTLevels(PIRTSpecification specification) {

		// levels map
		Map<String, Object> levels = new LinkedHashMap<>();

		if (specification != null && specification.getLevels() != null) {

			// levels fields reverse ordered by level
			Map<String, Object> levelName = new LinkedHashMap<>();
			List<PIRTLevelImportance> sorted = specification.getLevels().values().stream()
					.sorted((v1, v2) -> Integer.compare(v2.getLevel(), v1.getLevel())).collect(Collectors.toList());
			sorted.forEach(level -> {

				// level content
				Map<String, Object> levelContent = new LinkedHashMap<>();
				levelContent.put(YmlPIRTSchema.CONF_PIRT_LEVEL_NUMERICALVALUE, level.getLevel());
				levelContent.put(YmlPIRTSchema.CONF_PIRT_LEVEL_LABEL, level.getLabel());

				levelName.put(level.getName(), levelContent);
			});

			levels.put(YmlPIRTSchema.CONF_PIRT_FIELDS, levelName);
		}

		return levels;
	}

	/**
	 * @param specification the PIRT specification
	 * @return a map of PIRT Level difference colors
	 */
	public Map<String, Object> toMapPIRTLevelDifferenceColor(PIRTSpecification specification) {
		Map<String, Object> colors = new LinkedHashMap<>();

		if (specification != null) {
			Map<String, Object> colorName = new LinkedHashMap<>();

			// add level difference colors
			if (specification.getColors() != null) {

				List<PIRTLevelDifferenceColor> sorted = specification.getColors().stream().sorted(Comparator
						.comparing(PIRTLevelDifferenceColor::getId, Comparator.nullsLast(Comparator.naturalOrder())))
						.collect(Collectors.toList());
				sorted.forEach(color -> {

					String diffKey = getColorDiffKeyFromRange(color.getMin(), color.getMax());
					if (diffKey != null) {

						Map<String, Object> colorContent = new LinkedHashMap<>();
						colorContent.put(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_RGB, color.getColor());
						colorContent.put(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_EXPLANATION, color.getExplanation());

						colorName.put(diffKey, colorContent);
					}
				});
			}

			// add level fixed colors
			if (specification.getLevels() != null) {

				List<PIRTLevelImportance> sorted = specification.getLevels().values().stream().sorted(Comparator
						.comparing(PIRTLevelImportance::getId, Comparator.nullsLast(Comparator.naturalOrder())))
						.collect(Collectors.toList());
				sorted.forEach(level -> {

					if (level.getFixedColor() != null) {
						Map<String, Object> colorContent = new LinkedHashMap<>();
						colorContent.put(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_RGB, level.getFixedColor());
						colorContent.put(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_EXPLANATION,
								level.getFixedColorDescription());

						colorName.put(level.getName(), colorContent);
					}
				});
			}

			colors.put(YmlPIRTSchema.CONF_PIRT_FIELDS, colorName);
		}

		return colors;
	}

	/**
	 * @param min
	 * @param max
	 * @return the color difference key for the specified range
	 */
	private String getColorDiffKeyFromRange(Integer min, Integer max) {

		if (min != null && max != null) {

			String range = min + ";" + max; //$NON-NLS-1$

			if (min.equals(max)) {
				range = String.valueOf(min);
			}

			if (range.equals(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_POS_OR_ZERO_RANGE)) {
				return YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_POS_OR_ZERO;
			} else if (range.equals(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_ONE_LEVEL_RANGE)) {
				return YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_ONE_LEVEL;
			} else if (range.equals(YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_TWO_LEVELS_OR_MORE_RANGE)) {
				return YmlPIRTSchema.CONF_PIRT_LEVEL_COLOR_TWO_LEVELS_OR_MORE;
			}
		}

		return null;
	}

	/**
	 * @param specification the PIRT specification
	 * @return a map of PIRT Guidelines
	 */
	public Map<String, Object> toMapPIRTGuidelines(PIRTSpecification specification) {

		// ranking guidelines map
		Map<String, Object> guidelines = new LinkedHashMap<>();

		if (specification != null && specification.getPirtAdequacyGuidelines() != null) {

			// guidelines map
			Map<String, Object> guidelineName = new LinkedHashMap<>();
			specification.getPirtAdequacyGuidelines().forEach(guideline -> {

				Map<String, Object> guidelineContent = new LinkedHashMap<>();
				guidelineContent.put(YmlPIRTSchema.CONF_PIRT_GUIDELINES_GUIDELINES_DESCTAG, guideline.getDescription());

				if (guideline.getLevelGuidelines() != null && !guideline.getLevelGuidelines().isEmpty()) {
					Map<String, Object> guidelineLevels = new LinkedHashMap<>();
					List<PIRTAdequacyColumnLevelGuideline> levelGuidelines = guideline.getLevelGuidelines();
					levelGuidelines.forEach(level -> guidelineLevels.put(level.getName(), level.getDescription()));
					guidelineContent.put(YmlPIRTSchema.CONF_PIRT_GUIDELINES_GUIDELINES_LEVELTAG, guidelineLevels);
				}

				guidelineName.put(guideline.getName(), guidelineContent);

			});

			guidelines.put(YmlPIRTSchema.CONF_PIRT_GUIDELINES_GUIDELINESTAG, guidelineName);
		}

		return guidelines;
	}

}
