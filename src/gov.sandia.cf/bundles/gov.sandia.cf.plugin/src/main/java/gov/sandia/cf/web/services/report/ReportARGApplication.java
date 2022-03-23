/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.report;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.report.IReportARGApplication;
import gov.sandia.cf.constants.arg.ARGOrientation;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.IGenericTableItem;
import gov.sandia.cf.model.IGenericTableValue;

/**
 * Manage ARG Report Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class ReportARGApplication extends AApplication implements IReportARGApplication {

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

	@Override
	public File createReportStructureFile(ARGParameters argParameters) throws IOException, CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void copyReportStructureContentIntoFile(File structureFile, Map<String, Object> structure)
			throws CredibilityException, IOException {
		// TODO to implement

	}

	@Override
	public File createReportParametersFile(ARGParameters argParameters) throws IOException, CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void generateReportParametersFile(File parametersFile, ARGParameters argParameters)
			throws IOException, CredibilityException {
		// TODO to implement

	}

	@Override
	public Map<String, Object> generateStructure(Map<ExportOptions, Object> options)
			throws CredibilityException, IOException {
		// TODO to implement
		return null;
	}

	@Override
	public String getSectionTypeByGenericLevel(Integer level) {
		// TODO to implement
		return null;
	}

	@Override
	public void generateGenericValues(List<Map<String, Object>> parentSections, IGenericTableItem item,
			List<IGenericTableValue> values, ARGParameters argParameters) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public String getLinkPathRelativeToOutputDir(ARGParameters parameters, String link) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public Map<String, Object> generateHyperlink(String prefix, String suffix, String path, String value) {
		// TODO to implement
		return null;
	}

	@Override
	public String generateLabelValue(String label, String value) {
		// TODO to implement
		return null;
	}

	@Override
	public Map<String, Object> generateParagraph(String text) {
		// TODO to implement
		return null;
	}

	@Override
	public void prefixOrCreateParagraph(String text, Map<String, Object> paragraph) {
		// TODO to implement

	}

	@Override
	public void suffixOrCreateParagraph(String text, Map<String, Object> paragraph) {
		// TODO to implement

	}

	@Override
	public Map<String, Object> generateInlining(Map<String, Object> section, String path) {
		// TODO to implement
		return null;
	}

	@Override
	public Map<String, Object> generateImage(Map<String, Object> section, String path, String caption, String label) {
		// TODO to implement
		return null;
	}

	@Override
	public Map<String, Object> generateHtmlParagraph(String htmlValue) {
		// TODO to implement
		return null;
	}

	@Override
	public Map<String, Object> generateSection(String title, String text, List<Map<String, Object>> subsections,
			ARGOrientation orientation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> generateSubSection(String title, String text, List<Map<String, Object>> subsections,
			ARGOrientation orientation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> generateSubsubSection(String title, String text, List<Map<String, Object>> subsections,
			ARGOrientation orientation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> generateSection(String title, String text, List<Map<String, Object>> subsections,
			String sectionType, ARGOrientation orientation) {
		// TODO Auto-generated method stub
		return null;
	}

}
