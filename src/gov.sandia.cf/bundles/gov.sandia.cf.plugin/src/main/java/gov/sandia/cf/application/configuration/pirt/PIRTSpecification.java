/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.pirt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.swt.graphics.RGB;

import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.tools.ColorTools;

/**
 * Contains all PIRT configuration variables. This class is loaded by a
 * configuration file.
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTSpecification {

	/**
	 * DEFAULT_CELL_COLOR constant
	 */
	public static final RGB DEFAULT_CELL_COLOR = new RGB(255, 255, 255);

	/**
	 * List of quantity of interest headers. This list is used to set pirt qoi
	 * description table attribute label
	 */
	private List<PIRTDescriptionHeader> headers;

	/**
	 * List of additional column properties of pirt main table
	 */
	private List<PIRTAdequacyColumn> columns;

	/**
	 * Map of pirt importance levels to set value of adequacy columns
	 */
	private Map<String, PIRTLevelImportance> levels;

	/**
	 * Colors used to show differences between expected phenomena importance and
	 * reality
	 */
	private List<PIRTLevelDifferenceColor> colors;

	/**
	 * List of adequacy columns guidelines
	 */
	private List<PIRTAdequacyColumnGuideline> pirtAdequacyGuidelines;

	/**
	 * @return headers list
	 */
	public List<PIRTDescriptionHeader> getHeaders() {
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(List<PIRTDescriptionHeader> headers) {
		this.headers = headers;
	}

	/**
	 * @return pirt table adequacy column properties list
	 */
	public List<PIRTAdequacyColumn> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setAdequacyColumns(List<PIRTAdequacyColumn> columns) {
		this.columns = columns;
	}

	/**
	 * @return importance levels list
	 */
	public Map<String, PIRTLevelImportance> getLevels() {
		return levels;
	}

	/**
	 * @param levels the levels to set
	 */
	public void setLevels(Map<String, PIRTLevelImportance> levels) {
		this.levels = levels;
	}

	/**
	 * @return list of pirt Adequacy columns Guidelines
	 */
	public List<PIRTAdequacyColumnGuideline> getPirtAdequacyGuidelines() {
		return pirtAdequacyGuidelines;
	}

	/**
	 * Set list of pirt Adequacy columns Guidelines
	 * 
	 * @param pirtAdequacyGuidelines the pirtAdequacyGuidelines to set
	 */
	public void setPirtAdequacyGuidelines(List<PIRTAdequacyColumnGuideline> pirtAdequacyGuidelines) {
		this.pirtAdequacyGuidelines = pirtAdequacyGuidelines;
	}

	/**
	 * @param currValue the value to get color associated
	 * @return color corresponding to specified level difference value
	 */
	public RGB getColor(int currValue) {
		for (PIRTLevelDifferenceColor color : colors) {
			if (color != null && color.isInRange(currValue)) {
				return ColorTools.stringRGBToColor(color.getColor());
			}
		}
		return DEFAULT_CELL_COLOR;
	}

	/**
	 * @return the pirt level difference colors
	 */
	public List<PIRTLevelDifferenceColor> getColors() {
		return colors;
	}

	/**
	 * Set the pirt level difference colors
	 * 
	 * @param list the pirt level colors to set
	 */
	public void setColors(List<PIRTLevelDifferenceColor> list) {
		this.colors = list;
	}

	/**
	 * @return the importance levels as a list sorted by Level descending
	 */
	public List<PIRTLevelImportance> getLevelsListSortedByLevelDescending() {

		List<PIRTLevelImportance> listLevelImportance = new ArrayList<>();

		if (levels != null) {
			listLevelImportance = levels.values().stream()
					.sorted(Comparator.comparingInt(PIRTLevelImportance::getLevel).reversed())
					.collect(Collectors.toList());
		}

		return listLevelImportance;
	}

}
