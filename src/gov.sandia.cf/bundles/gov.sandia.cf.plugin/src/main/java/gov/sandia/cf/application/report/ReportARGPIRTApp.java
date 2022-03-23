/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.RGB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.constants.arg.ARGOrientation;
import gov.sandia.cf.constants.arg.YmlARGStructure;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTTreeAdequacyColumnType;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QoIHeader;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.comparator.StringWithNumberAndNullableComparator;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.viewer.PIRTPhenomenaTreePhenomena;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage ARG Report for PIRT methods
 * 
 * @author Didier Verstraete
 *
 */
public class ReportARGPIRTApp extends AApplication implements IReportARGPIRTApp {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReportARGPIRTApp.class);

	/**
	 * The constructor
	 */
	public ReportARGPIRTApp() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ReportARGPIRTApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void generateStructurePIRT(List<Map<String, Object>> chapters, Map<ExportOptions, Object> options) {

		logger.debug("Generate PIRT Report"); //$NON-NLS-1$

		if (chapters == null || options == null) {
			return;
		}

		List<Map<String, Object>> sections = new ArrayList<>();

		// Generate model headers
		Model model = (Model) options.get(ExportOptions.MODEL);
		if (model != null) {
			generateStructureSectionsPIRTModelHeaders(model, sections);
		}

		// Get PIRT specification
		PIRTSpecification pirtSpecs = (PIRTSpecification) options.get(ExportOptions.PIRT_SPECIFICATION);

		// Get QoIs requested
		Map<QuantityOfInterest, Map<ExportOptions, Object>> qois = (Map<QuantityOfInterest, Map<ExportOptions, Object>>) options
				.get(ExportOptions.PIRT_QOI_LIST);

		if (qois != null) {

			// sort keys
			List<QuantityOfInterest> qoiKeys = new ArrayList<>(qois.keySet());
			Collections.sort(qoiKeys, Comparator.comparing(QuantityOfInterest::getGeneratedId,
					new StringWithNumberAndNullableComparator()));

			// For each quantity of interest
			for (QuantityOfInterest qoi : qoiKeys) {
				generateStructureSectionsPIRTQoI(qois.get(qoi), pirtSpecs, sections);
			}
		}

		// PIRT chapter
		Map<String, Object> pirtChapter = new LinkedHashMap<>();
		pirtChapter.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_CHAPTER);
		pirtChapter.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY,
				RscTools.getString(RscConst.MSG_ARG_REPORT_PIRT_TITLE));
		pirtChapter.put(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY, ARGOrientation.LANDSCAPE.getOrientation());
		pirtChapter.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
				RscTools.getString(RscConst.MSG_ARG_REPORT_PIRT_STRING));
		pirtChapter.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, sections);
		chapters.add(pirtChapter);
	}

	/**
	 * Generate PIRT Model Headers sections
	 * 
	 * @param model       The model
	 * @param subsections The sections list
	 */
	private void generateStructureSectionsPIRTModelHeaders(Model model, List<Map<String, Object>> subsections) {

		if (model == null || subsections == null) {
			return;
		}

		// Items
		List<Map<String, Object>> items = new ArrayList<>();

		// Model application
		if (!StringUtils.isBlank(model.getApplication())) {
			Map<String, Object> itemApplication = new LinkedHashMap<>();
			itemApplication.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.TABLE_QOI_HEADER_ROW_APPLICATION) + RscTools.COLON
							+ model.getApplication());
			items.add(itemApplication);
		}

		// Model contact
		if (!StringUtils.isBlank(model.getContact())) {
			Map<String, Object> itemContact = new LinkedHashMap<>();
			itemContact.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY,
					RscTools.getString(RscConst.TABLE_QOI_HEADER_ROW_CONTACT) + RscTools.COLON + model.getContact());
			items.add(itemContact);
		}

		// Add data to item section
		Map<String, Object> itemSection = new LinkedHashMap<>();
		itemSection.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_ITEMIZE);
		itemSection.put(YmlARGStructure.ARG_STRUCTURE_ITEMS_KEY, items);
		itemSection.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY,
				RscTools.getString(RscConst.TABLE_QOI_HEADER_BAR_LABEL));
		itemSection.put(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY, ARGOrientation.LANDSCAPE.getOrientation());

		// Add itemSection
		subsections.add(itemSection);
	}

	/**
	 * Generate PIRT QoI sections
	 * 
	 * @param The      QoI data
	 * @param sections The sections list
	 */
	private void generateStructureSectionsPIRTQoI(Map<ExportOptions, Object> qoiData, PIRTSpecification pirtSpecs,
			List<Map<String, Object>> sections) {

		if (qoiData == null || sections == null) {
			return;
		}

		// Get quantity of interest selected with the good tag
		boolean isSelected = (boolean) qoiData.get(ExportOptions.PIRT_QOI_INCLUDE);

		Object qoiObject = qoiData.get(ExportOptions.PIRT_QOI_TAG);
		QuantityOfInterest tag = null;
		if (qoiObject instanceof QuantityOfInterest) {
			tag = (QuantityOfInterest) qoiObject;
		} else if (qoiObject instanceof StructuredSelection) {
			tag = (QuantityOfInterest) ((StructuredSelection) qoiObject).getFirstElement();
		}

		// If selected we create the section
		if (isSelected && tag != null) {
			// Add sub-sections (group phenomenon)
			List<Map<String, Object>> subsections = new ArrayList<>();

			// add description
			if (!StringUtils.isEmpty(tag.getDescription())) {
				subsections.add(getAppMgr().getService(IReportARGApplication.class)
						.generateHtmlParagraph(tag.getDescription()));
			}

			// Add headers
			String qoiHeaderString = generateStructureSectionsPIRTQoIHeader(tag);

			// generate PIRT table
			generatePIRTTable(tag, pirtSpecs, subsections);

			// generate PIRT Phenomenon, for each phenomenon group
			List<PhenomenonGroup> phenomenonGroupList = tag.getPhenomenonGroupList();
			if (phenomenonGroupList != null && !phenomenonGroupList.isEmpty()) {

				// sort
				Collections.sort(phenomenonGroupList,
						Comparator.comparing(PhenomenonGroup::getIdLabel, new StringWithNumberAndNullableComparator()));

				phenomenonGroupList
						.forEach(phenomenonGroup -> generateStructureSectionsPIRTPhenomenonGroup(phenomenonGroup,
								pirtSpecs, subsections));
			}

			// Create section
			Map<String, Object> section = getAppMgr().getService(IReportARGApplication.class)
					.generateSection(tag.getSymbol(), qoiHeaderString, subsections, ARGOrientation.LANDSCAPE);

			// Add to sections list
			sections.add(section);
		}
	}

	/**
	 * Generate PIRT QoI Headers sections
	 * 
	 * @param qoi the qoi to generate the header for
	 * @return the qoi header description
	 */
	private String generateStructureSectionsPIRTQoIHeader(QuantityOfInterest qoi) {

		if (qoi == null) {
			return RscTools.empty();
		}

		// Add items
		StringBuilder str = new StringBuilder();
		boolean carriageReturnNeeded = false;
		if (qoi.getQoiHeaderList() != null) {
			for (QoIHeader qoiHeader : qoi.getQoiHeaderList().stream().filter(Objects::nonNull)
					.filter(v -> !StringUtils.isBlank(v.getValue())).collect(Collectors.toList())) {
				// Create item
				if (carriageReturnNeeded) {
					str.append(RscTools.CARRIAGE_RETURN);
				}
				str.append(qoiHeader.getName() + RscTools.COLON + qoiHeader.getValue());
				carriageReturnNeeded = true;
			}
		}

		return str.toString();
	}

	/**
	 * Generate PIRT phenomenon group sections
	 * 
	 * @param phenomenonGroup the phenomenon group to display
	 * @param pirtSpecs       the PIRT specification
	 * @param subsections
	 */
	private void generateStructureSectionsPIRTPhenomenonGroup(PhenomenonGroup phenomenonGroup,
			PIRTSpecification pirtSpecs, List<Map<String, Object>> subsections) {

		if (phenomenonGroup == null || subsections == null) {
			return;
		}

		// Add subsection sections (phenomenon)
		List<Map<String, Object>> subsectionsSections = new ArrayList<>();

		// For each phenomenon
		List<Phenomenon> phenomenonList = phenomenonGroup.getPhenomenonList();
		if (phenomenonList != null && !phenomenonList.isEmpty()) {

			// sort
			Collections.sort(phenomenonList,
					Comparator.comparing(Phenomenon::getIdLabel, new StringWithNumberAndNullableComparator()));

			for (Phenomenon phenomenon : phenomenonList) {
				generateStructureSectionsPIRTPhenomenon(phenomenon, pirtSpecs, subsectionsSections);
			}
		}

		Map<String, Object> subsection = new LinkedHashMap<>();
		subsection.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_SUBSECTION);
		subsection.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY, phenomenonGroup.getName());
		subsection.put(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY, ARGOrientation.LANDSCAPE.getOrientation());
		subsection.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, subsectionsSections);
		subsections.add(subsection);
	}

	/**
	 * Generate PIRT phenomenon sections
	 * 
	 * @param phenomenon          the phenomenon to display
	 * @param pirtSpecs           the PIRT specification
	 * @param subsectionsSections
	 */
	private void generateStructureSectionsPIRTPhenomenon(Phenomenon phenomenon, PIRTSpecification pirtSpecs,
			List<Map<String, Object>> subsectionsSections) {

		if (phenomenon == null || subsectionsSections == null) {
			return;
		}

		// Add items (parameters)
		List<Map<String, Object>> items = new ArrayList<>();

		// PIRT Table for this Phenomenon
		generatePIRTTableForPhenomenon(phenomenon, pirtSpecs, items);

		// add RICHTEXT criteria
		for (Criterion c : phenomenon.getCriterionList().stream().filter(Objects::nonNull)
				.filter(criterion -> PIRTTreeAdequacyColumnType.RICH_TEXT.getType().equals(criterion.getType()))
				.collect(Collectors.toList())) {

			// add html paragraph into sub-element section
			items.add(getAppMgr().getService(IReportARGApplication.class).generateHtmlParagraph(c.getValue()));
		}

		// add TEXT criteria
		long nbCriteriaText = phenomenon.getCriterionList().stream().filter(Objects::nonNull)
				.filter(criterion -> PIRTTreeAdequacyColumnType.TEXT.getType().equals(criterion.getType())).count();
		for (Criterion c : phenomenon.getCriterionList().stream().filter(Objects::nonNull)
				.filter(criterion -> PIRTTreeAdequacyColumnType.TEXT.getType().equals(criterion.getType()))
				.collect(Collectors.toList())) {

			// do not display the criterion name if it's alone
			StringBuilder output = new StringBuilder();
			if (nbCriteriaText > 1) {
				output.append(c.getName() + RscTools.COLON + RscTools.CARRIAGE_RETURN);
			}
			output.append(c.getValue());

			Map<String, Object> parameterItems = new LinkedHashMap<>();
			parameterItems.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY, output.toString());

			List<Map<String, Object>> l1 = new ArrayList<>();
			l1.add(parameterItems);

			// Add data to question section
			Map<String, Object> parameterItem = new LinkedHashMap<>();
			parameterItem.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_ITEMIZE);
			parameterItem.put(YmlARGStructure.ARG_STRUCTURE_ITEMS_KEY, l1);

			// add Question section into sub-element section
			items.add(parameterItem);
		}

		// Add parameters
		Map<String, Object> subsubsection = getAppMgr().getService(IReportARGApplication.class)
				.generateSubsubSection(phenomenon.getName(), null, items, ARGOrientation.LANDSCAPE);
		subsectionsSections.add(subsubsection);
	}

	/**
	 * Generate PIRT Table
	 * 
	 * @param qoi         the qoi to display
	 * @param pirtSpecs   the PIRT specification
	 * @param subsections the existing subsections
	 */
	private void generatePIRTTable(QuantityOfInterest qoi, PIRTSpecification pirtSpecs,
			List<Map<String, Object>> subsections) {

		if (qoi != null && pirtSpecs != null && subsections != null && qoi.getPhenomenonGroupList() != null
				&& !qoi.getPhenomenonGroupList().isEmpty()) {

			// generate PIRT table header
			List<Map<String, Object>> tableHeaders = generatePIRTTableHeader(pirtSpecs.getColumns());

			// generate PIRT table rows
			List<List<Map<String, Object>>> tableRows = generatePIRTTableRows(qoi, pirtSpecs);

			// add to the main map
			List<Map<String, Object>> subsubSections = new ArrayList<>();

			Map<String, Object> tableSection = new LinkedHashMap<>();
			tableSection.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_TABLE);
			tableSection.put(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY, ARGOrientation.LANDSCAPE.getOrientation());
			tableSection.put(YmlARGStructure.ARG_STRUCTURE_TABLE_HEADERS_KEY, tableHeaders);
			tableSection.put(YmlARGStructure.ARG_STRUCTURE_TABLE_ROWS_KEY, tableRows);
			tableSection.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CAPTION_KEY,
					RscTools.getString(RscConst.MSG_ARG_REPORT_PIRT_TABLE_CAPTION, qoi.getSymbol()));

			subsubSections.add(tableSection);

			// Add to subsection
			Map<String, Object> subsectionsSection = getAppMgr().getService(IReportARGApplication.class)
					.generateSubSection(RscTools.getString(RscConst.MSG_ARG_REPORT_PIRT_PIRTTABLE_TITLE), null,
							subsubSections, ARGOrientation.LANDSCAPE);

			// Add itemSection
			subsections.add(subsectionsSection);
		}
	}

	/**
	 * Generate PIRT Table for a particular phenomenon
	 * 
	 * @param qoi         the phenomenon to display
	 * @param pirtSpecs   the PIRT specification
	 * @param subsections the existing subsections
	 */
	private void generatePIRTTableForPhenomenon(Phenomenon phenomenon, PIRTSpecification pirtSpecs,
			List<Map<String, Object>> subsections) {

		if (phenomenon == null && subsections == null) {
			return;
		}

		// generate PIRT table header
		List<Map<String, Object>> subsectionsHeaders = generatePIRTTableHeader(pirtSpecs.getColumns());

		// generate PIRT table rows
		List<List<Map<String, Object>>> subsectionsRows = new ArrayList<>();
		subsectionsRows.add(generatePIRTTablePhenomenonRow(phenomenon, pirtSpecs));

		// add to the main map
		Map<String, Object> subsection = new LinkedHashMap<>();
		subsection.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_TABLE);
		subsection.put(YmlARGStructure.ARG_STRUCTURE_TABLE_HEADERS_KEY, subsectionsHeaders);
		subsection.put(YmlARGStructure.ARG_STRUCTURE_TABLE_ROWS_KEY, subsectionsRows);
		subsections.add(subsection);
	}

	/**
	 * Generate PIRT Table Header row
	 * 
	 * @param criterionColumns the criterion columns
	 */
	private List<Map<String, Object>> generatePIRTTableHeader(List<PIRTAdequacyColumn> criterionColumns) {

		List<Map<String, Object>> subsectionsHeaders = new ArrayList<>();

		List<String> columns = new ArrayList<>();
		columns.add(PIRTPhenomenaTreePhenomena.getColumnIdProperty());
		columns.add(PIRTPhenomenaTreePhenomena.getColumnPhenomenaProperty());
		columns.add(PIRTPhenomenaTreePhenomena.getColumnImportanceProperty());

		if (criterionColumns == null) {
			return subsectionsHeaders;
		}

		// retrieve criterion columns of LEVEL type
		columns.addAll(criterionColumns.stream().filter(Objects::nonNull)
				.filter(column -> PIRTTreeAdequacyColumnType.LEVELS.getType().equals(column.getType()))
				.map(PIRTAdequacyColumn::getName).collect(Collectors.toList()));

		for (String column : columns) {
			Map<String, Object> header = new LinkedHashMap<>();
			header.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_VALUE_KEY, column);
			header.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_BACKGROUNDCOLOR_KEY,
					YmlARGStructure.ARG_STRUCTURE_TABLE_HEADER_BACKGROUNDCOLOR);
			header.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_FOREGROUNDCOLOR_KEY,
					YmlARGStructure.ARG_STRUCTURE_TABLE_HEADER_FOREGROUNDCOLOR);
			header.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_KEY,
					column.equals(PIRTPhenomenaTreePhenomena.getColumnPhenomenaProperty())
							? YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_LEFT_KEY
							: YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_CENTER_KEY);
			header.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTVERTICAL_KEY,
					YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTVERTICAL_BOTTOM_KEY);
			subsectionsHeaders.add(header);
		}

		return subsectionsHeaders;
	}

	/**
	 * Generate PIRT Table data rows
	 * 
	 * @param qoi       the qoi to display
	 * @param pirtSpecs the PIRT specification
	 */
	private List<List<Map<String, Object>>> generatePIRTTableRows(QuantityOfInterest qoi, PIRTSpecification pirtSpecs) {

		List<List<Map<String, Object>>> subsectionsRows = new ArrayList<>();

		if (pirtSpecs == null || qoi == null || qoi.getPhenomenonGroupList() == null
				|| qoi.getPhenomenonGroupList().isEmpty()) {
			return subsectionsRows;
		}

		// For each phenomenon group
		for (PhenomenonGroup phenomenonGroup : qoi.getPhenomenonGroupList()) {

			// generate group row
			subsectionsRows.add(generatePIRTTableGroupRow(phenomenonGroup, pirtSpecs));

			// For each phenomenon
			if (phenomenonGroup.getPhenomenonList() != null && !phenomenonGroup.getPhenomenonList().isEmpty()) {
				for (Phenomenon phenomenon : phenomenonGroup.getPhenomenonList()) {
					subsectionsRows.add(generatePIRTTablePhenomenonRow(phenomenon, pirtSpecs));
				}
			}
		}

		return subsectionsRows;

	}

	/**
	 * Generate PIRT Table phenomenon group row
	 * 
	 * @param phenomenonGroup the phenomenon Group to display
	 * @param pirtSpecs       the PIRT specification
	 */
	private List<Map<String, Object>> generatePIRTTableGroupRow(PhenomenonGroup phenomenonGroup,
			PIRTSpecification pirtSpecs) {

		List<Map<String, Object>> subsectionsRowGroup = new ArrayList<>();

		if (phenomenonGroup == null || pirtSpecs == null) {
			return subsectionsRowGroup;
		}

		// Group id
		Map<String, Object> cellGroupId = new LinkedHashMap<>();
		cellGroupId.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_VALUE_KEY, phenomenonGroup.getIdLabel());
		cellGroupId.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_BACKGROUNDCOLOR_KEY,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT));
		cellGroupId.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_FOREGROUNDCOLOR_KEY,
				YmlARGStructure.ARG_STRUCTURE_TABLE_HEADER_FOREGROUNDCOLOR);
		cellGroupId.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_KEY,
				YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_CENTER_KEY);
		subsectionsRowGroup.add(cellGroupId);

		// Group name
		Map<String, Object> cellGroupName = new LinkedHashMap<>();
		cellGroupName.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_VALUE_KEY, phenomenonGroup.getName());
		cellGroupName.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_BACKGROUNDCOLOR_KEY,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT));
		cellGroupName.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_FOREGROUNDCOLOR_KEY,
				YmlARGStructure.ARG_STRUCTURE_TABLE_HEADER_FOREGROUNDCOLOR);
		cellGroupName.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_KEY,
				YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_LEFT_KEY);
		subsectionsRowGroup.add(cellGroupName);

		// Adequacy columns of type LEVEL
		if (pirtSpecs.getColumns() != null) {
			for (int i = 0; i <= pirtSpecs.getColumns().stream().filter(Objects::nonNull)
					.filter(column -> PIRTTreeAdequacyColumnType.LEVELS.getType().equals(column.getType()))
					.count(); i++) {
				Map<String, Object> cellGroup = new LinkedHashMap<>();
				cellGroup.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_VALUE_KEY, RscTools.empty());
				cellGroup.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_BACKGROUNDCOLOR_KEY,
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT));
				cellGroup.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_FOREGROUNDCOLOR_KEY,
						YmlARGStructure.ARG_STRUCTURE_TABLE_HEADER_FOREGROUNDCOLOR);
				cellGroup.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_KEY,
						YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_CENTER_KEY);
				subsectionsRowGroup.add(cellGroup);
			}
		}

		return subsectionsRowGroup;
	}

	/**
	 * Generate PIRT Table phenomenon row
	 * 
	 * @param phenomenon the phenomenon to display
	 * @param pirtSpecs  the PIRT specification
	 */
	private List<Map<String, Object>> generatePIRTTablePhenomenonRow(Phenomenon phenomenon,
			PIRTSpecification pirtSpecs) {

		List<Map<String, Object>> subsectionsRowPhenomenon = new ArrayList<>();

		if (phenomenon == null || pirtSpecs == null) {
			return subsectionsRowPhenomenon;
		}

		// Phenomenon id
		Map<String, Object> cellId = new LinkedHashMap<>();
		cellId.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_VALUE_KEY, phenomenon.getIdLabel());
		cellId.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_KEY,
				YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_CENTER_KEY);
		subsectionsRowPhenomenon.add(cellId);

		// Phenomenon name
		Map<String, Object> cellName = new LinkedHashMap<>();
		cellName.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_VALUE_KEY, phenomenon.getName());
		cellName.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_KEY,
				YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_LEFT_KEY);
		subsectionsRowPhenomenon.add(cellName);

		// Phenomenon importance
		Map<String, Object> cellImportance = new LinkedHashMap<>();
		String importanceStr = pirtSpecs.getLevels() != null
				&& pirtSpecs.getLevels().get(phenomenon.getImportance()) != null
						? pirtSpecs.getLevels().get(phenomenon.getImportance()).getLabel()
						: RscTools.empty();
		cellImportance.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_VALUE_KEY, importanceStr);
		cellImportance.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_KEY,
				YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_CENTER_KEY);
		subsectionsRowPhenomenon.add(cellImportance);

		// Adequacy columns of type LEVEL
		if (pirtSpecs.getColumns() == null || phenomenon.getCriterionList() == null) {
			return subsectionsRowPhenomenon;
		}

		for (PIRTAdequacyColumn column : pirtSpecs.getColumns().stream().filter(Objects::nonNull)
				.filter(column -> PIRTTreeAdequacyColumnType.LEVELS.getType().equals(column.getType()))
				.collect(Collectors.toList())) {

			// search the criterion
			Optional<Criterion> criterionFound = phenomenon.getCriterionList().stream().filter(
					criterion -> criterion != null && column != null && column.getName().equals(criterion.getName()))
					.findFirst();

			if (criterionFound.isPresent()) {
				subsectionsRowPhenomenon
						.add(generatePIRTTableCriterionCell(criterionFound.get(), phenomenon, pirtSpecs));
			}
		}

		return subsectionsRowPhenomenon;
	}

	/**
	 * Generate PIRT Table criterion cell
	 * 
	 * @param criterionFound the criterion to display
	 * @param phenomenon     the phenomenon associated
	 * @param pirtSpecs      the PIRT specification
	 */
	private Map<String, Object> generatePIRTTableCriterionCell(Criterion criterionFound, Phenomenon phenomenon,
			PIRTSpecification pirtSpecs) {

		Map<String, Object> cell = new LinkedHashMap<>();

		if (criterionFound == null || pirtSpecs == null) {
			return cell;
		}

		// criterion value
		String criterionStr = pirtSpecs.getLevels() != null
				&& pirtSpecs.getLevels().get(criterionFound.getValue()) != null
						? pirtSpecs.getLevels().get(criterionFound.getValue()).getLabel()
						: RscTools.empty();
		cell.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_VALUE_KEY, criterionStr);

		// criterion background color
		if (PIRTTreeAdequacyColumnType.LEVELS.getType().equals(criterionFound.getType()) && phenomenon != null) {
			try {

				RGB backgroundColor = ColorTools.stringRGBToColor(getAppMgr().getService(IPIRTApplication.class)
						.getBackgroundColor(pirtSpecs, pirtSpecs.getLevels().get(phenomenon.getImportance()),
								pirtSpecs.getLevels().get(criterionFound.getValue())));

				if (backgroundColor != null) {
					cell.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_BACKGROUNDCOLOR_KEY,
							ColorTools.toStringRGB(backgroundColor));
					cell.put(YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_KEY,
							YmlARGStructure.ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_CENTER_KEY);
				}
			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
			}
		}

		return cell;
	}

}
