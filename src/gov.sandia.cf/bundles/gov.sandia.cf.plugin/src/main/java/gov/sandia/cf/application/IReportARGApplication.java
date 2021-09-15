/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.IGenericTableValue;

/**
 * Interface to manage ARG Report Application methods
 * 
 * @author Didier Verstraete
 *
 */
public interface IReportARGApplication extends IApplication {

	/**
	 * Create Credibility Report structure file
	 * 
	 * @param argParameters the arg parameters
	 * @return the file created
	 * @throws IOException          if a file read/write error occurs
	 * @throws CredibilityException if an error occurs during deletion or variable
	 *                              resolving
	 */
	File createReportStructureFile(ARGParameters argParameters) throws IOException, CredibilityException;

	/**
	 * Copy structure object into structure YML file
	 * 
	 * @param structureFile the structure file to populate
	 * @param structure     the map containing the structure content
	 * @throws IOException          if a file read/write error occurs
	 * @throws CredibilityException if an error occurs during deletion
	 */
	void copyReportStructureContentIntoFile(File structureFile, Map<String, Object> structure)
			throws CredibilityException, IOException;

	/**
	 * Create Credibility Report parameters file
	 * 
	 * @param argParameters the arg parameters
	 * @return the file created
	 * @throws IOException          if a file read/write error occurs
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	File createReportParametersFile(ARGParameters argParameters) throws IOException, CredibilityException;

	/**
	 * Generate a File that contains the ARG parameters (a YML file).
	 * 
	 * @param parametersFile the parameters file
	 * @param argParameters  the ARG parameters to generate
	 * @throws IOException          if a file read/write error occurs
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	void generateReportParametersFile(File parametersFile, ARGParameters argParameters)
			throws IOException, CredibilityException;

	/**
	 * Generate Credibility Report structure data
	 * 
	 * @param options the options containing the data
	 * @return a map containing the data for ARG reporting
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 * @throws IOException          if an error occurs during structure file parsing
	 *                              and construction
	 */
	Map<String, Object> generateStructure(Map<ExportOptions, Object> options) throws CredibilityException, IOException;

	/**
	 * @param level the level to get section for
	 * @return the section type associated to the level specified. If out of limits,
	 *         return paragraph
	 */
	String getSectionTypeByGenericLevel(Integer level);

	/**
	 * Generate GenericValues paragraph
	 * 
	 * @param parentSections The parent section
	 * @param values         The generic values
	 * @param argParameters  the ARG parameters
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	void generateGenericValues(List<Map<String, Object>> parentSections, List<IGenericTableValue> values,
			ARGParameters argParameters) throws CredibilityException;

	/**
	 * Generate section
	 * 
	 * @param title       the section title
	 * @param text        the section text
	 * @param subsections the subsections
	 * @return a map containing a new ARG section
	 */
	Map<String, Object> generateSection(String title, String text, List<Map<String, Object>> subsections);

	/**
	 * Generate subsection
	 * 
	 * @param title       the subsection title
	 * @param text        the subsection text
	 * @param subsections the subsections
	 * @return a map containing a new ARG subsection
	 */
	Map<String, Object> generateSubSection(String title, String text, List<Map<String, Object>> subsections);

	/**
	 * Generate a subsubsection
	 * 
	 * @param title       the subsubsection title
	 * @param text        the subsubsection text
	 * @param subsections the subsections
	 * @return a map containing a new ARG subsubsection
	 */
	Map<String, Object> generateSubsubSection(String title, String text, List<Map<String, Object>> subsections);

	/**
	 * @param parameters the ARG parameters
	 * @param link       the link path
	 * @return the link path relative to the report output folder
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	String getLinkPathRelativeToOutputDir(ARGParameters parameters, String link) throws CredibilityException;

	/**
	 * Generate hyperlink section
	 * 
	 * @param prefix the link prefix
	 * @param suffix the link suffix
	 * @param path   the link path
	 * @param value  the link value
	 * @return a map containing the hyperlink section
	 */
	Map<String, Object> generateHyperlink(String prefix, String suffix, String path, String value);

	/**
	 * Generate a section depending of the type
	 * 
	 * @param title       the subsubsection title
	 * @param text        the subsubsection text
	 * @param subsections the subsections
	 * @param sectionType the section type (chapter, section, subsection,
	 *                    subsubsection...)
	 * @return a map containing a new ARG section
	 */
	Map<String, Object> generateSection(String title, String text, List<Map<String, Object>> subsections,
			String sectionType);

	/**
	 * Generate Label value line
	 * 
	 * @param label the label
	 * @param value the value
	 * @return return the label value
	 */
	String generateLabelValue(String label, String value);

	/**
	 * Generate paragraph section
	 * 
	 * @param text the paragraph text
	 * @return The paragraph section
	 */
	Map<String, Object> generateParagraph(String text);

	/**
	 * Prefix (if exists) or create a new paragraph section with the text param as a
	 * prefix
	 * 
	 * @param text      the paragraph text
	 * @param paragraph the paragraph map
	 */
	void prefixOrCreateParagraph(String text, Map<String, Object> paragraph);

	/**
	 * Append (if exists) or create a new paragraph section with the text param as a
	 * suffix
	 * 
	 * @param text      the paragraph text
	 * @param paragraph the paragraph map
	 */
	void suffixOrCreateParagraph(String text, Map<String, Object> paragraph);

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
	Map<String, Object> generateHyperlink(Map<String, Object> paragraph, String prefix, String suffix, String path,
			String value);

	/**
	 * Inline a docx document into an existing docx document
	 * 
	 * @param section the section to add the inlined document into
	 * @param path    the word document path
	 * @return the map section containing an ARG section with the inlined document
	 */
	Map<String, Object> generateInlining(Map<String, Object> section, String path);

}
