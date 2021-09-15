/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.pirt;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.configuration.ExportOptions;
import gov.sandia.cf.application.configuration.YmlGlobalData;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QoIHeader;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * This class write credibility PIRT data. The actual implementation is stored
 * in a yaml file
 * 
 * @author Didier Verstraete
 *
 */
public class YmlWriterPIRTData {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlWriterPIRTData.class);

	/**
	 * @param cfDataFile the export file to write
	 * @param options    the export options
	 * @param append     append to the export file?
	 * @throws IOException          if a file read/write error occurs
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void writePIRTData(final File cfDataFile, final Map<ExportOptions, Object> options, final boolean append)
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

		if (!options.containsKey(ExportOptions.PIRT_INCLUDE)
				|| !Boolean.TRUE.equals(options.get(ExportOptions.PIRT_INCLUDE))) {
			logger.warn("PIRT is not selected to be included. Nothing to write into file {}", //$NON-NLS-1$
					cfDataFile.getAbsolutePath());
			return;
		}

		logger.debug("Write credibility data to the yml file {}", cfDataFile.getAbsolutePath()); //$NON-NLS-1$

		// load global specs
		Map<String, Object> mapRoot = new LinkedHashMap<>();
		Map<String, Object> mapPIRT = new LinkedHashMap<>();
		Map<String, Object> mapData = new LinkedHashMap<>();

		mapRoot.put(YmlPIRTData.CONF_PIRT, mapPIRT);
		mapPIRT.put(YmlPIRTData.CONF_DATA, mapData);

		// Quantities of Interest
		mapData.put(YmlPIRTData.CONF_PIRT_QOI, toMapPIRTQoIList(options));

		// Phenomenon Groups
		mapData.put(YmlPIRTData.CONF_PIRT_PIRTTABLE, toMapPhenomenonGroupOptions(options));

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
	 * @param options the PIRT data selected to be exported
	 * @return a map of PIRT Quantities of Interest
	 */
	public Map<String, Object> toMapPIRTQoIList(final Map<ExportOptions, Object> options) {
		Map<String, Object> qoiMap = new LinkedHashMap<>();

		if (options != null && options.containsKey(ExportOptions.PIRT_QOI_LIST)
				&& options.get(ExportOptions.PIRT_QOI_LIST) instanceof List) {

			@SuppressWarnings("unchecked")
			List<QuantityOfInterest> qoiList = (List<QuantityOfInterest>) options.get(ExportOptions.PIRT_QOI_LIST);

			for (QuantityOfInterest qoi : qoiList) {
				qoiMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, qoi.getId(), qoi.getSymbol()),
						toMapPIRTQoI(qoi));
			}
		}

		return qoiMap;
	}

	/**
	 * @param qoi the qoi to convert to a map object
	 * @return a map of PIRT Quantity of Interest
	 */
	public Map<String, Object> toMapPIRTQoI(final QuantityOfInterest qoi) {
		Map<String, Object> qoiMap = new LinkedHashMap<>();

		if (qoi == null) {
			return qoiMap;
		}

		qoiMap.put(YmlPIRTData.CONF_PIRT_QOI_ID, qoi.getId());
		qoiMap.put(YmlPIRTData.CONF_PIRT_QOI_CREATIONDATE, qoi.getCreationDate());
		qoiMap.put(YmlPIRTData.CONF_PIRT_QOI_DESCRIPTION, qoi.getDescription());
		qoiMap.put(YmlPIRTData.CONF_PIRT_QOI_NAME, qoi.getSymbol());
		if (qoi.getParent() != null) {
			qoiMap.put(YmlPIRTData.CONF_PIRT_QOI_PARENT, qoi.getParent());
		}
		if (qoi.getTag() != null) {
			qoiMap.put(YmlPIRTData.CONF_PIRT_QOI_TAG, qoi.getTag());
		}
		if (qoi.getTagDate() != null) {
			qoiMap.put(YmlPIRTData.CONF_PIRT_QOI_TAGDATE, qoi.getTagDate());
		}
		if (qoi.getTagDescription() != null) {
			qoiMap.put(YmlPIRTData.CONF_PIRT_QOI_TAGDESCRIPTION, qoi.getTagDescription());
		}
		if (qoi.getTagUserCreation() != null) {
			qoiMap.put(YmlPIRTData.CONF_PIRT_QOI_TAGUSER,
					qoi.getTagUserCreation() != null ? qoi.getTagUserCreation().getId() : null);
		}
		if (qoi.getParent() != null) {
			qoiMap.put(YmlPIRTData.CONF_PIRT_QOI_PARENT, qoi.getParent().getId());
		}
		if (qoi.getQoiHeaderList() != null) {
			qoiMap.put(YmlPIRTData.CONF_PIRT_QOI_HEADERS, toMapQoIHeader(qoi.getQoiHeaderList()));
		}

		return qoiMap;
	}

	/**
	 * @param qoiHeaderList the QoI headers to export
	 * @return a map of PIRT Quantities of Interest headers
	 */
	private Map<String, Object> toMapQoIHeader(final List<QoIHeader> qoiHeaderList) {

		Map<String, Object> qoiHeaderMap = new LinkedHashMap<>();

		if (qoiHeaderList == null) {
			return qoiHeaderMap;
		}

		for (QoIHeader header : qoiHeaderList) {
			qoiHeaderMap.put(header.getName(), header.getValue());
		}

		return qoiHeaderMap;
	}

	/**
	 * @param phenGroupList the phenomenon groups to export
	 * @return a map of PIRT Quantities of Interest headers
	 */
	private Map<String, Object> toMapPhenomenonGroupOptions(final Map<ExportOptions, Object> options) {
		Map<String, Object> phenGroupMap = new LinkedHashMap<>();

		if (options != null && options.containsKey(ExportOptions.PIRT_QOI_LIST)
				&& options.get(ExportOptions.PIRT_QOI_LIST) instanceof List) {

			List<PhenomenonGroup> phenGroupList = new ArrayList<>();

			@SuppressWarnings("unchecked")
			List<QuantityOfInterest> qoiList = (List<QuantityOfInterest>) options.get(ExportOptions.PIRT_QOI_LIST);
			qoiList.forEach(qoi -> phenGroupList.addAll(qoi.getPhenomenonGroupList()));

			return toMapPhenomenonGroupList(phenGroupList);
		}

		return phenGroupMap;
	}

	/**
	 * @param phenGroupList the phenomenon groups to export
	 * @return a map of Phenomenon Groups
	 */
	private Map<String, Object> toMapPhenomenonGroupList(final List<PhenomenonGroup> phenGroupList) {

		Map<String, Object> phenGroupMap = new LinkedHashMap<>();

		if (phenGroupList == null) {
			return phenGroupMap;
		}

		for (PhenomenonGroup group : phenGroupList) {
			if (group != null && group.getId() != null) {
				phenGroupMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, group.getId(), group.getName()),
						toMapPhenomenonGroup(group));
			}
		}

		return phenGroupMap;
	}

	/**
	 * @param phenGroupList the phenomenon groups to export
	 * @return a map of Phenomenon Groups
	 */
	private Map<String, Object> toMapPhenomenonGroup(final PhenomenonGroup phenGroup) {

		Map<String, Object> phenGroupMap = new LinkedHashMap<>();

		if (phenGroup == null) {
			return phenGroupMap;
		}

		phenGroupMap.put(YmlPIRTData.CONF_PIRT_PHENGROUP_ID, phenGroup.getId());
		phenGroupMap.put(YmlPIRTData.CONF_PIRT_PHENGROUP_IDLABEL, phenGroup.getIdLabel());
		phenGroupMap.put(YmlPIRTData.CONF_PIRT_PHENGROUP_NAME, phenGroup.getName());
		phenGroupMap.put(YmlPIRTData.CONF_PIRT_PHENGROUP_QOI,
				phenGroup.getQoi() != null ? phenGroup.getQoi().getId() : null);
		phenGroupMap.put(YmlPIRTData.CONF_PIRT_PHENGROUP_PHENOMENONLIST,
				toMapPhenomenonList(phenGroup.getPhenomenonList()));

		return phenGroupMap;
	}

	/**
	 * @param phenomenonList the phenomenon list to export
	 * @return a map of Phenomena
	 */
	private Map<String, Object> toMapPhenomenonList(final List<Phenomenon> phenomenonList) {

		Map<String, Object> phenomenonMap = new LinkedHashMap<>();

		if (phenomenonList == null) {
			return phenomenonMap;
		}

		for (Phenomenon phenomenon : phenomenonList) {
			if (phenomenon != null && phenomenon.getId() != null) {
				phenomenonMap.put(
						MessageFormat.format(YmlGlobalData.MAP_KEY_ID, phenomenon.getId(), phenomenon.getName()),
						toMapPhenomenon(phenomenon));
			}
		}

		return phenomenonMap;
	}

	/**
	 * @param phenomenon the phenomenon list to export
	 * @return a map of Phenomena
	 */
	private Map<String, Object> toMapPhenomenon(final Phenomenon phenomenon) {

		Map<String, Object> phenomenonMap = new LinkedHashMap<>();

		if (phenomenon == null) {
			return phenomenonMap;
		}

		phenomenonMap.put(YmlPIRTData.CONF_PIRT_PHENOMENON_ID, phenomenon.getId());
		phenomenonMap.put(YmlPIRTData.CONF_PIRT_PHENOMENON_IDLABEL, phenomenon.getIdLabel());
		phenomenonMap.put(YmlPIRTData.CONF_PIRT_PHENOMENON_NAME, phenomenon.getName());
		phenomenonMap.put(YmlPIRTData.CONF_PIRT_PHENOMENON_IMPORTANCE, phenomenon.getImportance());
		phenomenonMap.put(YmlPIRTData.CONF_PIRT_PHENOMENON_CRITERIONLIST,
				toMapCriterionList(phenomenon.getCriterionList()));

		return phenomenonMap;
	}

	/**
	 * @param criterionList the criterion list to export
	 * @return a map of Criteria
	 */
	private Map<String, Object> toMapCriterionList(final List<Criterion> criterionList) {

		Map<String, Object> criterionMap = new LinkedHashMap<>();

		if (criterionList == null) {
			return criterionMap;
		}

		for (Criterion criterion : criterionList) {
			if (criterion != null && criterion.getId() != null) {
				criterionMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, criterion.getId(), criterion.getName()),
						toMapCriterion(criterion));
			}
		}

		return criterionMap;
	}

	/**
	 * @param criterionList the criterion to export
	 * @return a map of Criterion data
	 */
	private Map<String, Object> toMapCriterion(final Criterion criterion) {

		Map<String, Object> criterionMap = new LinkedHashMap<>();

		if (criterion == null) {
			return criterionMap;
		}

		criterionMap.put(YmlPIRTData.CONF_PIRT_CRITERION_ID, criterion.getId());
		criterionMap.put(YmlPIRTData.CONF_PIRT_CRITERION_TYPE, criterion.getType());
		criterionMap.put(YmlPIRTData.CONF_PIRT_PHENOMENON_NAME, criterion.getName());
		criterionMap.put(YmlPIRTData.CONF_PIRT_CRITERION_VALUE, criterion.getValue());

		return criterionMap;
	}
}
