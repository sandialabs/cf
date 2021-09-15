/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.arg;

import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.tools.ColorTools;

/**
 * Manage report generation file
 * 
 * @author Maxime N
 */
@SuppressWarnings("javadoc")
public class YmlARGStructure {

	/**
	 * Constants
	 */
	public static final String ARG_VERSION = "1.1.7"; //$NON-NLS-1$
	public static final String DATE_FORMAT = "dd MMMMM yyyy"; //$NON-NLS-1$

	/**
	 * ARG structure Keys
	 */
	public static final String ARG_STRUCTURE_VERSION_KEY = "ARG version"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_CHAPTERS_KEY = "chapters"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_N_KEY = "n"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TITLE_KEY = "title"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_STRING_KEY = "string"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_STRING_SUFFIX_KEY = "string_suffix"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_SECTIONS_KEY = "sections"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_ITEMS_KEY = "items"; //$NON-NLS-1$

	/**
	 * ARG structure values
	 */
	public static final String ARG_STRUCTURE_N_CHAPTER = "chapter"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_N_SECTION = "section"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_N_SUBSECTION = "subsection"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_N_SUBSUBSECTION = "subsubsection"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_N_PARAGRAPH = "paragraph"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_N_TABLE = "color-table"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_N_ITEMIZE = "itemize"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_N_INLINEDOCX = "inline-docx"; //$NON-NLS-1$

	/**
	 * ARG structure table Keys
	 */
	public static final String ARG_STRUCTURE_TABLE_HEADERS_KEY = "headers"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_ROWS_KEY = "rows"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CAPTION_KEY = "caption"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CELL_VALUE_KEY = "value"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CELL_BACKGROUNDCOLOR_KEY = "background-color"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CELL_FOREGROUNDCOLOR_KEY = "foreground-color"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_KEY = "horizontal-alignment"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_CENTER_KEY = "c"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_RIGHT_KEY = "r"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CELL_ALIGNMENTHORIZONTAL_LEFT_KEY = "l"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CELL_ALIGNMENTVERTICAL_KEY = "vertical-alignment"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CELL_ALIGNMENTVERTICAL_CENTER_KEY = "c"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CELL_ALIGNMENTVERTICAL_TOP_KEY = "t"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_TABLE_CELL_ALIGNMENTVERTICAL_BOTTOM_KEY = "b"; //$NON-NLS-1$

	public static final String ARG_STRUCTURE_TABLE_HEADER_BACKGROUNDCOLOR = ColorTools
			.colorToStringRGB(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY));
	public static final String ARG_STRUCTURE_TABLE_HEADER_FOREGROUNDCOLOR = ColorTools
			.colorToStringRGB(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE));

	/**
	 * ARG structure paragraph hyperlink Keys
	 */
	public static final String ARG_STRUCTURE_HYPERLINK_PATH_KEY = "hyperlink_path"; //$NON-NLS-1$
	public static final String ARG_STRUCTURE_HYPERLINK_STRING_KEY = "hyperlink_string"; //$NON-NLS-1$

	/**
	 * ARG structure document inlining docx Keys
	 */
	public static final String ARG_STRUCTURE_INLINEDOCX_PATH_KEY = "docx_path"; //$NON-NLS-1$

	/**
	 * Construct
	 */
	private YmlARGStructure() {
	}

}
