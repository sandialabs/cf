/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.requirement;

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

import gov.sandia.cf.application.configuration.IYmlWriter;
import gov.sandia.cf.application.configuration.YmlReaderGlobal;
import gov.sandia.cf.application.configuration.YmlWriterGenericSchema;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * This class write credibility System Requirement configuration. The actual
 * implementation is stored in a yaml file
 * 
 * @author Didier Verstraete
 *
 */
public class YmlWriterSystemRequirementSchema implements IYmlWriter<SystemRequirementSpecification> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlWriterSystemRequirementSchema.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void writeSchema(final File cfSchemaFile, final SystemRequirementSpecification specification,
			final boolean withIds, final boolean append) throws CredibilityException, IOException {
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

		logger.debug("Write System Requirement specification to the yml file {}", cfSchemaFile.getAbsolutePath()); //$NON-NLS-1$

		// load global specs
		Map<String, Object> mapSpecs = YmlReaderGlobal.loadYmlFile(cfSchemaFile);

		if (mapSpecs.get(YmlSystemRequirementSchema.CONF_COM) instanceof Map) {
			mapSpecs = (Map<String, Object>) mapSpecs.get(YmlSystemRequirementSchema.CONF_COM);
		}

		// replace System Requirement Parameters
		if (mapSpecs.get(YmlSystemRequirementSchema.CONF_REQUIREMENTS_PARAMETER) instanceof Map) {
			mapSpecs.remove(YmlSystemRequirementSchema.CONF_REQUIREMENTS_PARAMETER);
		}
		mapSpecs.put(YmlSystemRequirementSchema.CONF_REQUIREMENTS_PARAMETER,
				toMapSystemRequirementParam(specification, withIds));

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
	 * @param specification the System Requirement specification
	 * @param withIds       add the id field to the export
	 * @return a map of System Requirement Parameters
	 */
	public Map<String, Object> toMapSystemRequirementParam(SystemRequirementSpecification specification,
			final boolean withIds) {

		Map<String, Object> map = new LinkedHashMap<>();

		if (specification != null && specification.getParameters() != null) {

			// sort list by id
			List<SystemRequirementParam> parameters = specification.getParameters().stream().sorted(Comparator
					.comparing(SystemRequirementParam::getId, Comparator.nullsLast(Comparator.naturalOrder())))
					.collect(Collectors.toList());

			parameters.forEach(param -> {
				if (param.getParent() == null) {
					map.put(param.getName(), YmlWriterGenericSchema.getGenericParamValues(param, withIds));
				}
			});
		}

		return map;
	}

}
