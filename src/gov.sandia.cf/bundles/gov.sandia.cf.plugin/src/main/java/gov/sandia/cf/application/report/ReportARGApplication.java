/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.report;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.requirement.ISystemRequirementApplication;
import gov.sandia.cf.application.tools.GenericParameterTools;
import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.constants.arg.ARGOrientation;
import gov.sandia.cf.constants.arg.ARGVersion;
import gov.sandia.cf.constants.arg.YmlARGParameterSchema;
import gov.sandia.cf.constants.arg.YmlARGStructure;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.IGenericTableItem;
import gov.sandia.cf.model.IGenericTableValue;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.dto.configuration.ParameterLinkGson;
import gov.sandia.cf.parts.widgets.PCMMElementSelectorWidget;
import gov.sandia.cf.tools.CFVariableResolver;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.GsonTools;
import gov.sandia.cf.tools.MathTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * Manage ARG Report Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class ReportARGApplication extends AApplication implements IReportARGApplication {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReportARGApplication.class);

	/**
	 * The report sections by level
	 */
	private static final Map<Integer, String> LEVEL_SECTION;
	static {
		LEVEL_SECTION = new HashMap<>();
		LEVEL_SECTION.put(0, YmlARGStructure.ARG_STRUCTURE_N_CHAPTER);
		LEVEL_SECTION.put(1, YmlARGStructure.ARG_STRUCTURE_N_SECTION);
		LEVEL_SECTION.put(2, YmlARGStructure.ARG_STRUCTURE_N_SUBSECTION);
		LEVEL_SECTION.put(3, YmlARGStructure.ARG_STRUCTURE_N_SUBSUBSECTION);
	}

	/**
	 * The default figure witdh
	 */
	private static final String GENERATED_FIGURE_WIDTH = "145mm"; //$NON-NLS-1$

	/**
	 * The constructor
	 */
	public ReportARGApplication() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ReportARGApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	/** {@inheritDoc} */
	@Override
	public File createReportStructureFile(ARGParameters argParameters) throws IOException, CredibilityException {

		if (argParameters == null || argParameters.getStructureFilePath() == null) {
			return null;
		}

		return createReportFile(argParameters.getStructureFilePath());
	}

	/** {@inheritDoc} */
	@Override
	public File createReportParametersFile(ARGParameters argParameters) throws IOException, CredibilityException {

		if (argParameters == null || argParameters.getParametersFilePath() == null) {
			return null;
		}

		return createReportFile(argParameters.getParametersFilePath());
	}

	/** {@inheritDoc} */
	@Override
	public void generateReportParametersFile(File parametersFile, ARGParameters argParameters)
			throws IOException, CredibilityException {

		if (argParameters != null && parametersFile != null && parametersFile.exists()) {

			// Write data to parameters file
			logger.debug("Writing ARG parameters to yaml file: {}", parametersFile.getAbsolutePath()); //$NON-NLS-1$

			boolean append = false;
			FileTools.writeStringInFile(parametersFile, getParametersContentAsString(argParameters), append);
		}
	}

	/**
	 * Get the arg parameters data as a string
	 * 
	 * @param argParameters the arg parameters data
	 * @return a string containing the arg parameters as a yml file
	 */
	private static String getParametersContentAsString(ARGParameters argParameters) {

		StringBuilder str = new StringBuilder();

		// Command parameters
		str.append("# Command parameters").append(RscTools.CARRIAGE_RETURN); //$NON-NLS-1$

		// Set output and data folders
		File outputFile = FileTools.findFileInWorkspaceOrSystem(argParameters.getOutput());
		if (outputFile != null) {
			String outputStringPath = FileTools.getNormalizedPath(outputFile.toPath());

			// data
			str.append(YmlARGParameterSchema.CONF_ARG_DATA).append(RscTools.COLON).append("\"") //$NON-NLS-1$
					.append(outputStringPath).append("\"") //$NON-NLS-1$
					.append(RscTools.CARRIAGE_RETURN);

			// output
			str.append(YmlARGParameterSchema.CONF_ARG_OUTPUT).append(RscTools.COLON).append("\"") //$NON-NLS-1$
					.append(outputStringPath).append("\"") //$NON-NLS-1$
					.append(RscTools.CARRIAGE_RETURN);
		}

		// set structure file
		File structureFilePath = WorkspaceTools.getFileInWorkspaceOrSystem(argParameters.getStructureFilePath());
		if (structureFilePath != null) {
			String structureFileStringPath = FileTools.getNormalizedPath(structureFilePath.toPath());
			str.append(YmlARGParameterSchema.CONF_ARG_STRUCTURE).append(RscTools.COLON).append("\"") //$NON-NLS-1$
					.append(structureFileStringPath).append("\"").append(RscTools.CARRIAGE_RETURN); //$NON-NLS-1$
		} else {
			str.append(YmlARGParameterSchema.CONF_ARG_STRUCTURE).append(RscTools.COLON).append("\"") //$NON-NLS-1$
					.append(argParameters.getStructureFilePath()).append("\"").append(RscTools.CARRIAGE_RETURN); //$NON-NLS-1$
		}
		str.append(YmlARGParameterSchema.CONF_ARG_FILENAME).append(RscTools.COLON).append(argParameters.getFilename())
				.append(RscTools.CARRIAGE_RETURN);
		str.append(YmlARGParameterSchema.CONF_ARG_TITLE).append(RscTools.COLON).append(argParameters.getTitle())
				.append(RscTools.CARRIAGE_RETURN);

		str.append(RscTools.CARRIAGE_RETURN);

		// General report parameters
		str.append("# General report parameters").append(RscTools.CARRIAGE_RETURN); //$NON-NLS-1$

		str.append(YmlARGParameterSchema.CONF_ARG_BACKEND).append(RscTools.COLON).append(argParameters.getBackendType())
				.append(RscTools.CARRIAGE_RETURN);
		str.append(YmlARGParameterSchema.CONF_ARG_NUMBER).append(RscTools.COLON).append(argParameters.getNumber())
				.append(RscTools.CARRIAGE_RETURN);
		str.append(YmlARGParameterSchema.CONF_ARG_REPORT_TYPE).append(RscTools.COLON)
				.append(argParameters.getReportType()).append(RscTools.CARRIAGE_RETURN);
		if (!StringUtils.isBlank(argParameters.getAuthor())) {
			str.append(YmlARGParameterSchema.CONF_ARG_AUTHOR).append(RscTools.COLON).append(argParameters.getAuthor())
					.append(RscTools.CARRIAGE_RETURN);
		}

		return str.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void copyReportStructureContentIntoFile(File structureFile, Map<String, Object> structure)
			throws CredibilityException, IOException {

		// Check file exists
		if (structureFile == null || !structureFile.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFREPORT_YAML_STRUCTURE_FILE_NOTEXISTS));
		}

		// YML reader
		Yaml yaml = new Yaml();

		if (structure == null) {
			structure = new LinkedHashMap<>();
		}

		// remove non-printable characters
		structure = StringTools.removeNonPrintableChars(structure);

		try (Writer writer = new StringWriter()) {

			// Dump map objects to yml string
			yaml.dump(structure, writer);

			// check for binary content generated
			if (writer.toString().contains("!!" + Tag.BINARY)) { //$NON-NLS-1$
				throw new CredibilityException(RscTools
						.getString(RscConst.ERR_REPORTVIEW_GENERATE_REPORT_ARGPARAM_STRUCTUREFILE_SPECIALCHARS));
			}

			// write in file
			boolean append = false;
			FileTools.writeStringInFile(structureFile, writer.toString(), append);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> generateStructure(Map<ExportOptions, Object> options)
			throws CredibilityException, IOException {
		// Initialize
		Map<String, Object> ymlHashMap = new LinkedHashMap<>();

		// Set version
		ymlHashMap.put(YmlARGStructure.ARG_STRUCTURE_VERSION_KEY, ARGVersion.ARG_VERSION);

		// Generate chapters
		List<Map<String, Object>> chapters = generateStructureChapters(options);

		// Set Chapters
		ymlHashMap.put(YmlARGStructure.ARG_STRUCTURE_CHAPTERS_KEY, chapters);

		return ymlHashMap;
	}

	/**
	 * Generate paragraph section
	 * 
	 * @return The paragraph section
	 */
	private Map<String, Object> generateParagraph() {
		Map<String, Object> paragraph = new LinkedHashMap<>();
		paragraph.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH);
		return paragraph;
	}

	/**
	 * Generate html paragraph section
	 * 
	 * @return The html paragraph section
	 */
	private Map<String, Object> generateHtmlParagraph() {
		Map<String, Object> htmlParagraph = new LinkedHashMap<>();
		htmlParagraph.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_HTML_KEY);
		return htmlParagraph;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> generateParagraph(String text) {
		Map<String, Object> paragraph = generateParagraph();
		paragraph.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY, text);
		return paragraph;
	}

	/** {@inheritDoc} */
	@Override
	public void prefixOrCreateParagraph(String text, Map<String, Object> paragraph) {

		if (paragraph == null || !paragraph.containsKey(YmlARGStructure.ARG_STRUCTURE_N_KEY) || !paragraph
				.get(YmlARGStructure.ARG_STRUCTURE_N_KEY).equals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH)) {
			paragraph = generateParagraph();
		}

		paragraph.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY, StringTools.appendTo(
				(String) paragraph.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY), text, StringTools.PREFIX, true));
	}

	/** {@inheritDoc} */
	@Override
	public void suffixOrCreateParagraph(String text, Map<String, Object> paragraph) {

		if (paragraph == null || !paragraph.containsKey(YmlARGStructure.ARG_STRUCTURE_N_KEY) || !paragraph
				.get(YmlARGStructure.ARG_STRUCTURE_N_KEY).equals(YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH)) {
			paragraph = generateParagraph();
		}

		paragraph.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY, StringTools.appendTo(
				(String) paragraph.get(YmlARGStructure.ARG_STRUCTURE_STRING_KEY), text, StringTools.SUFFIX, true));
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> generateHtmlParagraph(String htmlValue) {
		return generateHtmlParagraph(null, htmlValue);
	}

	/**
	 * Create/Populate a html paragraph with content
	 * 
	 * @param htmlParagraph the existing html paragraph
	 * @param htmlValue     the html value
	 * @return the html paragraph populated
	 */
	private Map<String, Object> generateHtmlParagraph(Map<String, Object> htmlParagraph, String htmlValue) {

		if (htmlParagraph == null) {
			htmlParagraph = generateHtmlParagraph();
		}

		// remove the tabs and new lines to write pure html content
		if (htmlValue != null) {
			htmlValue = htmlValue.replace(RscTools.TAB, RscTools.empty());
			htmlValue = htmlValue.replace(RscTools.CARRIAGE_RETURN, RscTools.empty());
		}
		htmlParagraph.put(YmlARGStructure.ARG_STRUCTURE_HTML_STRING_KEY, htmlValue);

		return htmlParagraph;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> generateHyperlink(String prefix, String suffix, String path, String value) {
		return generateHyperlink(generateParagraph(), prefix, suffix, path, value);
	}

	/**
	 * Generate hyperlink section
	 * 
	 * @param paragraph the paragraph
	 * @param prefix    the link prefix
	 * @param suffix    the link suffix
	 * @param path      the link path
	 * @param value     the link value
	 * @return The hyperlink section
	 */
	private Map<String, Object> generateHyperlink(Map<String, Object> paragraph, String prefix, String suffix,
			String path, String value) {
		if (paragraph == null) {
			paragraph = generateParagraph();
		}
		if (prefix != null) {
			paragraph.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY, prefix);
		}
		paragraph.put(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_PATH_KEY, path != null ? path : RscTools.empty());
		paragraph.put(YmlARGStructure.ARG_STRUCTURE_HYPERLINK_STRING_KEY, value != null ? value : RscTools.empty());
		if (suffix != null) {
			paragraph.put(YmlARGStructure.ARG_STRUCTURE_STRING_SUFFIX_KEY, suffix);
		}
		return paragraph;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> generateInlining(Map<String, Object> section, String path) {
		if (section == null) {
			section = new LinkedHashMap<>();
		}
		section.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_INLINEDOCX);
		section.put(YmlARGStructure.ARG_STRUCTURE_INLINEDOCX_PATH_KEY, path);

		return section;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> generateImage(Map<String, Object> section, String path, String caption, String label) {
		if (section == null) {
			section = new LinkedHashMap<>();
		}
		section.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, YmlARGStructure.ARG_STRUCTURE_N_FIGURE);
		section.put(YmlARGStructure.ARG_STRUCTURE_FIGURE_ARGS_KEY, new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put(YmlARGStructure.ARG_STRUCTURE_FIGURE_WIDTH_KEY, GENERATED_FIGURE_WIDTH);
				put(YmlARGStructure.ARG_STRUCTURE_FIGURE_FILE_KEY, path);
				if (caption != null) {
					put(YmlARGStructure.ARG_STRUCTURE_FIGURE_CAPTION_KEY, caption);
				}
				if (label != null) {
					put(YmlARGStructure.ARG_STRUCTURE_FIGURE_LABEL_KEY, label);
				}
			}
		});

		return section;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> generateSection(String title, String text, List<Map<String, Object>> subsections,
			String sectionType, ARGOrientation orientation) {

		// Initialize
		Map<String, Object> section = new LinkedHashMap<>();

		// Existing level keys section
		if (LEVEL_SECTION.containsValue(sectionType)) {
			section.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, sectionType);
			section.put(YmlARGStructure.ARG_STRUCTURE_TITLE_KEY, title);
			if (orientation != null) {
				section.put(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY, orientation.getOrientation());
			}
			if (text != null) {
				section.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY, text);
			}
			if (subsections != null) {
				section.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, subsections);
			}
		}
		// For recursive methods (e.g SytemRequirement, Decision, ...): section became
		// paragraph
		else {
			section.put(YmlARGStructure.ARG_STRUCTURE_N_KEY, sectionType);
			StringBuilder strContent = new StringBuilder();
			if (!StringUtils.isBlank(title)) {
				strContent.append(title);
			}
			if (!StringUtils.isBlank(text)) {
				if (!StringUtils.isBlank(title)) {
					strContent.append(RscTools.CARRIAGE_RETURN);
				}
				strContent.append(text);
			}
			if (strContent.length() > 0) {
				section.put(YmlARGStructure.ARG_STRUCTURE_STRING_KEY, strContent.toString());
			}
			if (subsections != null) {
				section.put(YmlARGStructure.ARG_STRUCTURE_SECTIONS_KEY, subsections);
			}
			if (orientation != null) {
				section.put(YmlARGStructure.ARG_STRUCTURE_ORIENTATION_KEY, orientation.getOrientation());
			}
		}

		return section;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> generateSection(String title, String text, List<Map<String, Object>> subsections,
			ARGOrientation orientation) {
		return generateSection(title, text, subsections, YmlARGStructure.ARG_STRUCTURE_N_SECTION, orientation);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> generateSubSection(String title, String text, List<Map<String, Object>> subsections,
			ARGOrientation orientation) {
		return generateSection(title, text, subsections, YmlARGStructure.ARG_STRUCTURE_N_SUBSECTION, orientation);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> generateSubsubSection(String title, String text, List<Map<String, Object>> subsections,
			ARGOrientation orientation) {
		return generateSection(title, text, subsections, YmlARGStructure.ARG_STRUCTURE_N_SUBSUBSECTION, orientation);
	}

	/** {@inheritDoc} */
	@Override
	public String getLinkPathRelativeToOutputDir(ARGParameters parameters, String wksLink) throws CredibilityException {

		if (parameters == null || org.apache.commons.lang3.StringUtils.isBlank(wksLink)) {
			return null;
		}

		if (parameters.getOutput() != null) {

			// get output folder path on filesystem
			File outputFolder = FileTools
					.findFileInWorkspaceOrSystem(CFVariableResolver.resolveAll(parameters.getOutput()));

			if (outputFolder != null && outputFolder.exists() && outputFolder.isDirectory()) {

				// get wksLink path on filesystem
				StringBuilder strBld = new StringBuilder(CFVariable.WORKSPACE.get());
				if (!wksLink.startsWith(FileTools.PATH_SEPARATOR)) {
					strBld.append(FileTools.PATH_SEPARATOR);
				}
				strBld.append(wksLink);

				File filePath = new File(CFVariableResolver.resolveAll(strBld.toString()));

				// make it relative to the output folder path
				Path wksRelativeToOutput = outputFolder.toPath().relativize(filePath.toPath());

				if (wksRelativeToOutput != null) {
					return FileTools.getNormalizedPath(wksRelativeToOutput);
				}
			}
		}

		return null;
	}

	/**
	 * Generate the report content.
	 * 
	 * @param options the generation options
	 * @return a list of map containing the report content to the ARG format
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 * @throws IOException
	 */
	private List<Map<String, Object>> generateStructureChapters(Map<ExportOptions, Object> options)
			throws CredibilityException, IOException {
		// Initialize
		List<Map<String, Object>> chapters = new ArrayList<>();

		// Has options
		if (options != null) {

			// Get main features includes
			boolean includePlanning = options.containsKey(ExportOptions.PLANNING_INCLUDE)
					&& (boolean) options.get(ExportOptions.PLANNING_INCLUDE);
			boolean includePIRT = options.containsKey(ExportOptions.PIRT_INCLUDE)
					&& (boolean) options.get(ExportOptions.PIRT_INCLUDE);
			boolean includePCMM = options.containsKey(ExportOptions.PCMM_INCLUDE)
					&& (boolean) options.get(ExportOptions.PCMM_INCLUDE);
			boolean includeCustomEnding = options.containsKey(ExportOptions.CUSTOM_ENDING_INCLUDE)
					&& (boolean) options.get(ExportOptions.CUSTOM_ENDING_INCLUDE);

			// Planning sections
			if (includePlanning) {
				getAppMgr().getService(IReportARGPlanningApp.class).generateStructurePlanning(chapters, options);
			}

			// PIRT sections
			if (includePIRT) {
				getAppMgr().getService(IReportARGPIRTApp.class).generateStructurePIRT(chapters, options);
			}

			// PCMM sections
			if (includePCMM && options.containsKey(ExportOptions.PCMM_TAG)) {
				getAppMgr().getService(IReportARGPCMMApp.class).generateStructurePCMM(chapters, options);
			}

			// Custom ending
			if (includeCustomEnding) {
				appendCustomEndingStructureFile(chapters, options);
			}
		}

		// Result
		return chapters;
	}

	/**
	 * Generate Custom ending Chapter
	 * 
	 * @param chapters the PCMM chapters
	 * @param options  the export options
	 * @throws IOException if an error occurs during custom structure file parsing
	 */
	@SuppressWarnings("unchecked")
	private void appendCustomEndingStructureFile(List<Map<String, Object>> chapters, Map<ExportOptions, Object> options)
			throws IOException {
		ARGParameters argParam = (ARGParameters) options.get(ExportOptions.ARG_PARAMETERS);
		if (argParam != null && !StringUtils.isBlank(argParam.getCustomEndingFilePath())) {

			try (FileReader fileReader = new FileReader(new File(argParam.getCustomEndingFilePath()))) {
				// yaml reader
				Map<?, ?> customEndingMap = new Yaml().load(fileReader);
				if (customEndingMap != null
						&& customEndingMap.get(YmlARGStructure.ARG_STRUCTURE_CHAPTERS_KEY) instanceof List) {
					chapters.addAll((List<Map<String, Object>>) customEndingMap
							.get(YmlARGStructure.ARG_STRUCTURE_CHAPTERS_KEY));
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getSectionTypeByGenericLevel(final Integer level) {

		if (level == null) {
			return YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH;
		}

		// generic parameters are not chapters
		final int levelKey = level + 1;
		if (!LEVEL_SECTION.containsKey(levelKey)) {
			return YmlARGStructure.ARG_STRUCTURE_N_PARAGRAPH;
		}

		return LEVEL_SECTION.get(levelKey);
	}

	/** {@inheritDoc} */
	@Override
	public void generateGenericValues(List<Map<String, Object>> parentSections, IGenericTableItem item,
			List<IGenericTableValue> values, ARGParameters argParameters) throws CredibilityException {

		// Initialize
		List<String> textValues = new ArrayList<>();
		List<IGenericTableValue> richtextValues = new ArrayList<>();
		List<IGenericTableValue> links = new ArrayList<>();

		// Has values
		if (values == null) {
			return;
		}

		// For each value
		for (IGenericTableValue genericValue : GenericParameterTools.sortTableValuesByParameterId(values).stream()
				.filter(Objects::nonNull).filter(v -> v.getParameter() != null)
				.filter(v -> !StringUtils.isBlank(v.getValue())).collect(Collectors.toList())) {

			// Get Link
			if (FormFieldType.LINK.getType().equals(genericValue.getParameter().getType())) {
				links.add(genericValue);
			}

			// Get Richtext
			else if (FormFieldType.RICH_TEXT.getType().equals(genericValue.getParameter().getType())) {
				richtextValues.add(genericValue);
			}

			// Get Credibility Element
			else if (FormFieldType.CREDIBILITY_ELEMENT.getType().equals(genericValue.getParameter().getType())) {
				textValues.add(generateCredibilityElementLine(genericValue));
			}

			// Get System Requirement
			else if (FormFieldType.SYSTEM_REQUIREMENT.getType().equals(genericValue.getParameter().getType())) {
				textValues.add(generateSystemRequirementLine(genericValue));
			}

			// Get Text
			else {
				// Generate parameter/value section
				String textValue = generateGenericValueLine(genericValue);
				if (!org.apache.commons.lang3.StringUtils.isBlank(textValue)) {
					textValues.add(textValue);
				}
			}
		}

		// Append and construct
		computeGenericValues(parentSections, item, textValues, richtextValues, links, argParameters);
	}

	/**
	 * Append generic values sections to the parent sections.
	 *
	 * @param parentSections the parent sections to feed
	 * @param item           the generic table item
	 * @param textValues     the text values to add
	 * @param richtextValues the richtext values
	 * @param links          the links to add
	 * @param argParameters  the arg parameters to compute links
	 * @throws CredibilityException if an error occurs in the process
	 */
	private void computeGenericValues(List<Map<String, Object>> parentSections, IGenericTableItem item,
			List<String> textValues, List<IGenericTableValue> richtextValues, List<IGenericTableValue> links,
			ARGParameters argParameters) throws CredibilityException {

		// Has values
		if (parentSections == null) {
			return;
		}

		// if there is only one link, put the text values in the same paragraph
		if (links.size() == 1) {

			List<String> stringValues = new ArrayList<>();
			if (textValues != null && !textValues.isEmpty()) {
				stringValues.addAll(textValues);
			}
			stringValues.add(getLinkTitle(links.get(0)));
			String linkPath = getLinkPath(links.get(0), argParameters);

			if (!StringUtils.isBlank(linkPath)) {

				// if it's an image, generate image with caption
				if (new File(CFVariableResolver.resolveAll(argParameters.getOutput()), linkPath).isFile()
						&& FileTools.isImage(linkPath)) {
					if (textValues != null) {
						parentSections.add(generateParagraph(String.join(RscTools.CARRIAGE_RETURN, textValues)));
					}

					parentSections.add(generateImage(null, linkPath, getLinkCaption(links.get(0), item), null));
				}

				// otherwise generate a paragraph
				else {
					// workaround to have fields and link in the same paragraph -> append link to
					// values
					parentSections.add(generateHyperlink(String.join(RscTools.CARRIAGE_RETURN, stringValues), null,
							linkPath, linkPath));
				}
			} else {
				parentSections.add(generateParagraph(String.join(RscTools.CARRIAGE_RETURN, textValues)));
			}
		} else {
			// create a paragraph for the string values
			if (textValues != null && !textValues.isEmpty()) {
				parentSections.add(generateParagraph(String.join(RscTools.CARRIAGE_RETURN, textValues)));
			}
			// create a paragraph per link (not possible to add multiple links into a
			// paragraph)
			if (links.size() > 1) {
				computeGenericLinks(parentSections, item, textValues, links, argParameters);
			}
		}

		// add richtext values after
		if (richtextValues != null && !richtextValues.isEmpty()) {
			richtextValues.forEach(richtext -> {

				// Set the answer to the question
				if (!StringUtils.isBlank(richtext.getValue())) {
					// label
					parentSections.add(getAppMgr().getService(IReportARGApplication.class)
							.generateParagraph(richtext.getParameter().getName() + RscTools.COLON));
					// value
					parentSections.add(getAppMgr().getService(IReportARGApplication.class)
							.generateHtmlParagraph(richtext.getValue()));
				}
			});
		}
	}

	/**
	 * Append generic links sections to the parent sections
	 * 
	 * @param parentSections the parent sections to feed
	 * @param item           the generic table item
	 * @param textValues     the text values to add
	 * @param links          the links to add
	 * @param argParameters  the arg parameters to compute links
	 * @throws CredibilityException if an error occurs in the process
	 */
	private void computeGenericLinks(List<Map<String, Object>> parentSections, IGenericTableItem item,
			List<String> textValues, List<IGenericTableValue> links, ARGParameters argParameters) {

		if (parentSections == null || links == null) {
			return;
		}

		links.forEach(link -> computeGenericLink(parentSections, item, textValues, link, argParameters));
	}

	/**
	 * Compute generic link.
	 *
	 * @param parentSections the parent sections
	 * @param item           the item
	 * @param textValues     the text values
	 * @param link           the link
	 * @param argParameters  the arg parameters
	 */
	private void computeGenericLink(List<Map<String, Object>> parentSections, IGenericTableItem item,
			List<String> textValues, IGenericTableValue link, ARGParameters argParameters) {

		if (parentSections == null || link == null) {
			return;
		}

		try {
			String linkPath = getLinkPath(link, argParameters);

			// if it's an image, generate an image
			if (argParameters != null && linkPath != null
					&& new File(CFVariableResolver.resolveAll(argParameters.getOutput()), linkPath).isFile()
					&& FileTools.isImage(linkPath)) {
				if (textValues != null) {
					parentSections.add(generateParagraph(String.join(RscTools.CARRIAGE_RETURN, textValues)));
				}

				// get caption
				parentSections.add(generateImage(null, linkPath, getLinkCaption(link, item), null));
			}

			// otherwise generate a link
			else {

				Map<String, Object> generateGenericValueHyperlink = generateGenericValueHyperlink(link, argParameters);
				if (generateGenericValueHyperlink != null) {
					parentSections.add(generateGenericValueHyperlink);
				}
			}
		} catch (CredibilityException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * @param genericValue the value to display
	 * @return a string containing the credibility element generated value
	 */
	private String generateCredibilityElementLine(IGenericTableValue genericValue) {
		String id = genericValue.getValue();
		String value = RscTools.empty();
		if (id != null && MathTools.isInteger(id)) {
			try {
				PCMMElement element = getAppMgr().getService(IPCMMApplication.class)
						.getElementById(Integer.valueOf(id));
				if (element != null && !org.apache.commons.lang3.StringUtils.isBlank(element.getAbstract())) {
					value = element.getAbstract();
				} else if (PCMMElementSelectorWidget.NOT_APPLICABLE_ID.equals(Integer.valueOf(id))) {
					value = PCMMElementSelectorWidget.NOT_APPLICABLE_VALUE;
				}
			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return generateLabelValue(genericValue.getParameter().getName(), value);
	}

	/**
	 * @param genericValue the value to display
	 * @return a string containing the system requirement generated value
	 */
	public String generateSystemRequirementLine(IGenericTableValue genericValue) {
		String id = genericValue.getValue();
		String value = RscTools.empty();
		if (id != null && MathTools.isInteger(id)) {
			SystemRequirement requirement = getAppMgr().getService(ISystemRequirementApplication.class)
					.getRequirementById(Integer.valueOf(id));
			if (requirement != null && !org.apache.commons.lang3.StringUtils.isBlank(requirement.getAbstract())) {
				value = requirement.getAbstract();
			}
		}
		return generateLabelValue(genericValue.getParameter().getName(), value);
	}

	/**
	 * Generate generic value hyperlink.
	 *
	 * @param value the generic value to display
	 * @param argParameters the arg parameters
	 * @return a map containing the link if the path is not empty, otherwise a
	 *         paragraph with the value parameter name.
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	private Map<String, Object> generateGenericValueHyperlink(IGenericTableValue value, ARGParameters argParameters)
			throws CredibilityException {

		if (value != null && !org.apache.commons.lang3.StringUtils.isBlank(value.getValue())
				&& value.getParameter() != null
				&& FormFieldType.LINK.getType().equals(value.getParameter().getType())) {

			String linkTitle = value.getParameter().getName() + RscTools.COLON;
			String linkPath = null;

			ParameterLinkGson linkData = GsonTools.getFromGson(value.getValue(), ParameterLinkGson.class);

			// do not generate anything if the link value is null or empty
			// (https://gitlab.com/iwf/cf/-/issues/467)
			if (linkData != null && !org.apache.commons.lang3.StringUtils.isBlank(linkData.value)) {

				linkPath = linkData.value;

				if (FormFieldType.LINK_FILE.equals(linkData.type)) {
					// Get relative path
					linkPath = getLinkPathRelativeToOutputDir(argParameters, linkData.value);
				}

				return generateHyperlink(linkTitle, null, linkPath, linkPath);
			}
		}

		return null;
	}

	private String getLinkTitle(IGenericTableValue value, boolean withColon) {

		if (value != null && !org.apache.commons.lang3.StringUtils.isBlank(value.getValue())
				&& value.getParameter() != null
				&& FormFieldType.LINK.getType().equals(value.getParameter().getType())) {

			return value.getParameter().getName() + (withColon ? RscTools.COLON : ""); //$NON-NLS-1$
		}

		return null;
	}

	private String getLinkTitle(IGenericTableValue value) {
		return getLinkTitle(value, true);
	}

	/**
	 * Gets the link path.
	 *
	 * @param value         the value
	 * @param argParameters the arg parameters
	 * @return the link path
	 * @throws CredibilityException the credibility exception
	 */
	private String getLinkPath(IGenericTableValue value, ARGParameters argParameters) throws CredibilityException {

		if (value != null && !org.apache.commons.lang3.StringUtils.isBlank(value.getValue())
				&& value.getParameter() != null
				&& FormFieldType.LINK.getType().equals(value.getParameter().getType())) {

			ParameterLinkGson linkData = GsonTools.getFromGson(value.getValue(), ParameterLinkGson.class);

			// do not generate anything if the link value is null or empty
			// (https://gitlab.com/iwf/cf/-/issues/467)
			if (linkData != null && !org.apache.commons.lang3.StringUtils.isBlank(linkData.value)) {
				if (FormFieldType.LINK_FILE.equals(linkData.type)) {
					// Get relative path
					String linkPathRelativeToOutputDir = getLinkPathRelativeToOutputDir(argParameters, linkData.value);
					return !StringUtils.isBlank(linkPathRelativeToOutputDir) ? linkPathRelativeToOutputDir
							: linkData.value;
				} else {
					return linkData.value;
				}
			}
		}

		return null;
	}

	/**
	 * Gets the link caption.
	 *
	 * @param value the value
	 * @param item  the item
	 * @return the link caption
	 */
	private String getLinkCaption(IGenericTableValue value, IGenericTableItem item) {

		if (value != null && !org.apache.commons.lang3.StringUtils.isBlank(value.getValue())
				&& value.getParameter() != null
				&& FormFieldType.LINK.getType().equals(value.getParameter().getType())) {

			ParameterLinkGson linkData = GsonTools.getFromGson(value.getValue(), ParameterLinkGson.class);

			// do not generate anything if the link value is null or empty
			// (https://gitlab.com/iwf/cf/-/issues/467)
			if (linkData != null && !org.apache.commons.lang3.StringUtils.isBlank(linkData.value)
					&& FormFieldType.LINK_FILE.equals(linkData.type) && FileTools.isImage(linkData.value)) {
				if (StringUtils.isBlank(linkData.caption)) {
					return (item != null ? item.getItemTitle() + RscTools.SPACE + RscTools.HYPHEN + RscTools.SPACE
							: RscTools.empty()) + getLinkTitle(value, false);
				} else {
					return linkData.caption;
				}
			}
		}

		return null;
	}

	/**
	 * Generate GenericValue line.
	 *
	 * @param genericValue The GenericValue
	 * @return the generic value line
	 */
	private String generateGenericValueLine(IGenericTableValue genericValue) {

		// Get parameter
		GenericParameter<?> param = genericValue.getParameter();

		String value = null;
		if (param != null) {
			value = generateLabelValue(param.getName(), genericValue.getReadableValue());
		}

		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateLabelValue(String label, String value) {

		StringBuilder paragraph = new StringBuilder();

		// Create parameter string
		StringBuilder parameterAndValue = new StringBuilder();

		if (!StringUtils.isBlank(label)) {
			parameterAndValue.append(label);
			parameterAndValue.append(RscTools.COLON);
		}

		// Create value string
		if (value != null) {
			parameterAndValue.append(value);
		}

		// Add paragraph section
		paragraph.append(parameterAndValue.toString());

		return paragraph.toString();
	}

	/**
	 * Create a file with CF variable resolved
	 * 
	 * @param file the file to create
	 * @return the created file
	 * @throws IOException          if a reading/writing error occurs
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	private File createReportFile(String file) throws IOException, CredibilityException {

		if (file == null) {
			return null;
		}

		// search file on system
		File fileToCreate = new File(CFVariableResolver.resolveAll(file));

		// search if parent exists to create the file
		if (fileToCreate.getParentFile() != null && fileToCreate.getParentFile().exists()) {

			if (!fileToCreate.exists()) {
				Files.createFile(fileToCreate.toPath());
				logger.debug("Creating ARG structure file: {}", fileToCreate.getAbsolutePath()); //$NON-NLS-1$
			}

			return fileToCreate;
		}

		return null;
	}

}
