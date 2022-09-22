/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.constants;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.ISharedImages;

/**
 * Eclipse Parts Resource Constants
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public class PartsResourceConstants {

	/**
	 * Global
	 */
	/* Views */
	public static final int DEFAULT_TITLE_ADD_PIXEL_TO_FONT = 10;
	public static final int DEFAULT_SUBTITLE_ADD_PIXEL_TO_FONT = 5;
	public static final int DEFAULT_BUTTON_ADD_PIXEL_TO_FONT = 2;
	public static final int DEFAULT_IMPORTANTTEXT_ADD_PIXEL_TO_FONT = 2;
	public static final int DEFAULT_GRIDDATA_V_INDENT = 10;
	/* Dialogs */
	public static final int DESCRIPTIVE_DIALOG_SIZE_X = 900;
	public static final int DESCRIPTIVE_DIALOG_SIZE_Y = 700;
	public static final int DIALOG_TXT_INPUT_HEIGHT = 20;
	public static final int RICHTEXTEDITOR_MINHEIGHT = 200;
	public static final int DESCRIPTIVE_DIALOG_MIN_SIZE_X = 200;
	public static final int DESCRIPTIVE_DIALOG_MIN_SIZE_Y = 100;
	/* Tables */
	public static final int TABLE_VIEWER_SUPPORT_LISTENER_EVENTYPE = SWT.MouseDoubleClick;
	public static final RGB TABLE_NON_EDITABLE_CELL_COLOR = new RGB(250, 250, 250);
	public static final int TABLE_NON_EDITABLE_TEXT_COLOR = SWT.COLOR_DARK_GRAY;
	public static final int TABLE_CELL_BCKGND_COLOR = SWT.COLOR_WHITE;
	public static final int TABLE_ROW_HEIGHT = 25;
	public static final int TREE_IDCOLUMN_WIDTH = 85;
	public static final int TABLE_ACTION_ICON_SIZE = 16;
	public static final int TABLE_ACTIONCOLUMN_WIDTH = 45;
	/* Form factory */
	public static final int FORM_FACTORY_DIALOG_TXT_INPUT_HEIGHT = 60;

	/**
	 * Home View
	 */
	public static final int HOME_VIEW_CARD_TITLE_MARGIN = 15;
	public static final int HOME_VIEW_CARD_ICON_SIZE = 25;
	public static final int HOME_VIEW_CARD_COMPOSITE_MIN_WIDTH = 235;
	/* Cards */
	/* PIRT Card */
	public static final int HOME_VIEW_CARD_PIRT_CHART_MARGIN = 30;
	/* PCMM Card */
	public static final int HOME_VIEW_CARD_PCMM_CHART_MARGIN = 20;
	public static final int HOME_VIEW_NUM_COLUMNS = 3;
	public static final int HOME_VIEW_PCMM_WARNERROR_HEIGHT = 35;
	/* Colors */
	public static final String ACTIVE_BADGET_COLOR = "123,239,178"; //$NON-NLS-1$
	public static final String INACTIVE_BADGET_COLOR = "228,233,237"; //$NON-NLS-1$

	/**
	 * Planning Views
	 */

	/* Generic Parameter table columns */
	public static final int GENPARAM_TABLE_TEXT_COLUMN_COEFF = 3;
	public static final int GENPARAM_TABLE_FLOAT_COLUMN_COEFF = 1;
	public static final int GENPARAM_TABLE_SELECT_COLUMN_COEFF = 2;
	public static final int GENPARAM_TABLE_DATE_COLUMN_COEFF = 2;

	/* QoI Planning */
	public static final int QOIPLANNING_VIEW_GROUP_COLUMN_COEFF = 4;
	public static final int QOIPLANNING_DIALOG_TXT_INPUT_HEIGHT = 20;

	/* System Requirement */
	public static final int SYSREQUIREMENTVIEW_GROUP_COLUMN_COEFF = 4;
	public static final int SYSREQUIREMENTVIEW_DIALOG_TXT_INPUT_HEIGHT = 20;

	/* Uncertainty */
	public static final int UNCERTAINTYVIEW_GROUP_COLUMN_COEFF = 4;
	public static final int UNCERTAINTYVIEW_DIALOG_TXT_INPUT_HEIGHT = 20;

	/* Decision */
	public static final int DECISIONVIEW_GROUP_COLUMN_COEFF = 4;
	public static final int DECISIONVIEW_DIALOG_TXT_INPUT_HEIGHT = 20;
	public static final int DECISIONVIEW_ACTIONCOLUMN_WIDTH = 45;

	/**
	 * PIRT
	 */
	public static final int TABLETREE_CHECKCOLUMN_WIDTH = 25;
	/*
	 * QoI Home View
	 */
	/* Table */
	public static final int QOIHOME_VIEW_TABLEPHEN_CREATIONDATECOLUMN_WIDTH = 150;
	public static final int QOIHOME_VIEW_TABLEPHEN_NAMECOLUMN_WEIGHT = 200;
	public static final int QOIHOME_VIEW_TABLEPHEN_TAGGEDCOLUMN_WIDTH = 60;
	public static final int QOIHOME_VIEW_TABLEPHEN_TAGDATECOLUMN_WIDTH = 300;
	public static final int QOIHOME_VIEW_TABLEPHEN_TAGDESCRIPTION_COLUMN_WEIGHT = 2;
	public static final int QOIHOME_VIEW_TABLEPHEN_ACTION_COLUMN_WIDTH = 40;

	/*
	 * Phenomena View
	 */
	/* Table Header */
	public static final int PHEN_VIEW_TABLEHEADER_WIDTH = 600;
	public static final int PHEN_VIEW_TABLEHEADER_KEYCOLUMN_WIDTH = 200;
	public static final int PHEN_VIEW_TABLEHEADER_VALCOLUMN_WIDTH = 400;
	public static final int PHEN_VIEW_TABLEHEADER_BTNCOLUMN_WIDTH = 30;
	public static final int PIRT_PHEN_TABLEHEADER_FONT_STYLE = SWT.BOLD;
	public static final int PIRT_PHEN_TABLEHEADER_FONT_HEIGHT = 9;

	/* Table Phenomena */
	public static final int PHEN_VIEW_TREEPHEN_TXT_COLUMN_COEFF = 2;
	public static final int PHEN_VIEW_TREEPHEN_LVL_COLUMN_COEFF = 1;
	public static final int PIRT_PHEN_TABLEPHEN_FONT_STYLE = SWT.BOLD;
	public static final int PIRT_PHEN_TABLEPHEN_FONT_HEIGHT = 9;
	public static final int PIRT_PHEN_TABLEPHEN_ACTION_COLUMN_WIDTH = 40;

	/**
	 * PCMM
	 */
	/*
	 * Assess View
	 */
	/* Table Assessment */
	public static final int PCMM_VIEW_TABLEASSESS_IDCOLUMN_PIXEL = 80;
	public static final int PCMM_VIEW_TABLEASSESS_NAMECOLUMN_WEIGHT = 3;
	public static final int PCMM_VIEW_TABLEASSESS_LVLCOLUMN_WEIGHT = 1;
	public static final int PCMM_VIEW_TABLEASSESS_EVIDCOLUMN_WEIGHT = 1;
	public static final int PCMM_VIEW_TABLEASSESS_COMMENTSCOLUMN_WEIGHT = 2;
	public static final int PCMM_VIEW_TABLEASSESS_ACTIONCOLUMN_WIDTH = 45;
	/*
	 * Evidence View
	 */
	/* Table Evidence */
	public static final int PCMM_VIEW_TABLEEVIDENCE_NAMECOLUMN_WEIGHT = 3;
	public static final int PCMM_VIEW_TABLEEVIDENCE_PATHCOLUMN_WEIGHT = 3;
	public static final int PCMM_VIEW_TABLEEVIDENCE_SECTIONCOLUMN_WEIGHT = 2;
	public static final int PCMM_VIEW_TABLEEVIDENCE_DESCCOLUMN_WEIGHT = 2;
	public static final int PCMM_VIEW_TABLEEVIDENCE_USERCOLUMN_WEIGHT = 2;
	public static final int PCMM_VIEW_TABLEEVIDENCE_ROLECOLUMN_WEIGHT = 2;
	public static final int PCMM_VIEW_TABLEEVIDENCE_ACTIONCOLUMN_WIDTH = 45;

	/*
	 * Planning View
	 */
	/* Table planning */
	public static final int PCMM_PLANNINGVIEW_GROUP_COLUMN_COEFF = 4;
	public static final int PCMM_PLANNINGVIEW_DIALOG_TXT_INPUT_HEIGHT = 20;

	/*
	 * Evidence Dialog
	 */
	/* Table Evidence */
	public static final int PCMM_EVID_DIALOG_LBL_WARNING_HEIGHT = 60;
	public static final int PCMM_EVID_DIALOG_BUTTON_ICON_SIZE = 16;
	public static final int PCMM_EVID_DIALOG_BUTTON_HEIGHT = 25;

	/*
	 * PIRT Guidance Level View
	 */
	public static final int PIRT_GUIDANCEVIEW_ID_COLUMN_WIDTH = 200;
	public static final int PIRT_GUIDANCEVIEW_COLOR_COLUMN_WIDTH = 100;
	public static final int PIRT_GUIDANCEVIEW_DEFAULT_COLUMN_WIDTH = 300;
	public static final int PIRT_GUIDANCEVIEW_COLUMN_WEIGHT = 100;
	public static final int PIRT_GUIDANCEVIEW_TABLE_MIN_HEIGHT = 150;

	/*
	 * PCMM Guidance Level View
	 */
	public static final int PCMM_GUIDANCEVIEW_BREAK_RESIZE = 1000;
	public static final int PCMM_GUIDANCEVIEW_ID_COLUMN_WIDTH = 250;
	public static final int PCMM_GUIDANCEVIEW_ID_COLUMN_ROW_WIDTH = 360;
	public static final int PCMM_GUIDANCEVIEW_COLUMN_WEIGHT = 100;
	public static final int PCMM_GUIDANCEVIEW_DEFAULT_PADDING = 15;

	/**
	 * Report View
	 */
	public static final int REPORT_PIRT_TABLE_SELECT_COLUMN_COEFF = 1;
	public static final int REPORT_PIRT_TABLE_NAME_COLUMN_COEFF = 10;
	public static final int REPORT_PIRT_TABLE_TAG_COLUMN_COEFF = 3;

	/**
	 * Parts Wizards
	 */
	/*
	 * Default
	 */
	public static final int CREDCONFWIZARD_NUM_COLUMNS = 2;
	public static final int CREDCONFWIZARD_BTN_WIDTH = 75;

	/**
	 * Parts File browser
	 */
	public static final String PATH_SEP = "\\"; //$NON-NLS-1$
	public static final Styler FILE_BROWSER_COUNTER_STYLER = StyledString.COUNTER_STYLER;
	public static final String DIR_IMG_DESCRIPTOR = ISharedImages.IMG_OBJ_FOLDER;

	/**
	 * Do not instantiate.
	 */
	private PartsResourceConstants() {

	}
}
