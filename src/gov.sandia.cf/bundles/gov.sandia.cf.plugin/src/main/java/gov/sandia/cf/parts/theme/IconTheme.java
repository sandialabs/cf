/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.theme;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;

import gov.sandia.cf.tools.SystemTools;

/**
 * Manage Icons
 *
 * @author Maxime N.
 */
public class IconTheme {

	/**
	 * Default color All icon has to be available in this color
	 */
	public static final String ICON_COLOR_DEFAULT = ConstantTheme.COLOR_NAME_BLACK;

	/**
	 * Default icons size
	 */
	/** Default Windows ICON SIZE */
	public static final int ICON_SIZE_DEFAULT_WINDOWS = 20;
	/** Default Unix ICON SIZE */
	public static final int ICON_SIZE_DEFAULT_UNIX = 16;
	/** ICON SIZE small */
	public static final int ICON_SIZE_SMALL = 16;
	/** ICON SIZE for tables */
	public static final int ICON_SIZE_TABLE = 18;

	/**
	 * Icon names
	 */
	/** NOT_FOUND icon */
	public static final String ICON_NAME_NOT_FOUND = "NOT_FOUND"; //$NON-NLS-1$
	/** ADD icon */
	public static final String ICON_NAME_ADD = "ADD"; //$NON-NLS-1$
	/** DUPLICATE icon */
	public static final String ICON_NAME_DUPLICATE = "DUPLICATE"; //$NON-NLS-1$
	/** VIEW icon */
	public static final String ICON_NAME_VIEW = "VIEW"; //$NON-NLS-1$
	/** EDIT icon */
	public static final String ICON_NAME_EDIT = "EDIT"; //$NON-NLS-1$
	/** AGGREGATE icon */
	public static final String ICON_NAME_AGGREGATE = "AGGREGATE"; //$NON-NLS-1$
	/** ASSESS icon */
	public static final String ICON_NAME_ASSESS = "ASSESS"; //$NON-NLS-1$
	/** BACK icon */
	public static final String ICON_NAME_BACK = "BACK"; //$NON-NLS-1$
	/** CHANGE icon */
	public static final String ICON_NAME_CHANGE = "CHANGE"; //$NON-NLS-1$
	/** CLOSE icon */
	public static final String ICON_NAME_CLOSE = "CLOSE"; //$NON-NLS-1$
	/** COLLAPSE icon */
	public static final String ICON_NAME_COLLAPSE = "COLLAPSE"; //$NON-NLS-1$
	/** COMMUNICATE icon */
	public static final String ICON_NAME_COMMUNICATE = "COMMUNICATE"; //$NON-NLS-1$
	/** CONFIG icon */
	public static final String ICON_NAME_CONFIG = "CONFIG"; //$NON-NLS-1$
	/** The Constant ICON_NAME_CONNECT. */
	public static final String ICON_NAME_CONNECT = "CONNECT"; //$NON-NLS-1$
	/** COPY icon */
	public static final String ICON_NAME_COPY = "COPY"; //$NON-NLS-1$
	/** DELETE icon */
	public static final String ICON_NAME_DELETE = "DELETE"; //$NON-NLS-1$
	/** The Constant ICON_NAME_DISCONNECT. */
	public static final String ICON_NAME_DISCONNECT = "DISCONNECT"; //$NON-NLS-1$
	/** EXAMINE icon */
	public static final String ICON_NAME_EXAMINE = "EXAMINE"; //$NON-NLS-1$
	/** EXPAND icon */
	public static final String ICON_NAME_EXPAND = "EXPAND"; //$NON-NLS-1$
	/** EXPORT icon */
	public static final String ICON_NAME_EXPORT = "EXPORT"; //$NON-NLS-1$
	/** The Constant ICON_NAME_FAIL. */
	public static final String ICON_NAME_FAIL = "FAIL"; //$NON-NLS-1$
	/** GEN_CF_REPORT icon */
	public static final String ICON_NAME_GEN_CF_REPORT = "GEN_CF_REPORT"; //$NON-NLS-1$
	/** HELP icon */
	public static final String ICON_NAME_HELP = "HELP"; //$NON-NLS-1$
	/** INFO icon */
	public static final String ICON_NAME_INFO = "INFO"; //$NON-NLS-1$
	/** IMMUTABLE icon */
	public static final String ICON_NAME_IMMUTABLE = "IMMUTABLE"; //$NON-NLS-1$
	/** IMPORT icon */
	public static final String ICON_NAME_IMPORT = "IMPORT"; //$NON-NLS-1$
	/** MUTABLE icon */
	public static final String ICON_NAME_MUTABLE = "MUTABLE"; //$NON-NLS-1$
	/** OPEN icon */
	public static final String ICON_NAME_OPEN = "OPEN"; //$NON-NLS-1$
	/** PCMM icon */
	public static final String ICON_NAME_PCMM = "PCMM"; //$NON-NLS-1$
	/** PCMM_BIS icon */
	public static final String ICON_NAME_PCMM_BIS = "PCMM_BIS"; //$NON-NLS-1$
	/** PIRT icon */
	public static final String ICON_NAME_PIRT = "PIRT"; //$NON-NLS-1$
	/** QUERY icon */
	public static final String ICON_NAME_QUERY = "QUERY"; //$NON-NLS-1$
	/** PLANNING icon */
	public static final String ICON_NAME_PLANNING = "PLANNING"; //$NON-NLS-1$
	/** RESET icon */
	public static final String ICON_NAME_RESET = "RESET"; //$NON-NLS-1$
	/** REFERENCE icon */
	public static final String ICON_NAME_REFERENCE = "REFERENCE"; //$NON-NLS-1$
	/** SAVE icon */
	public static final String ICON_NAME_SAVE = "SAVE"; //$NON-NLS-1$
	/** NOT_SAVE icon */
	public static final String ICON_NAME_NOTSAVE = "NOT_SAVE"; //$NON-NLS-1$
	/** PCMM STAMP icon */
	public static final String ICON_NAME_STAMP = "STAMP"; //$NON-NLS-1$
	/** TAG icon */
	public static final String ICON_NAME_TAG = "TAG"; //$NON-NLS-1$
	/** UP_TO_DATE icon */
	public static final String ICON_NAME_UPTODATE = "UP_TO_DATE"; //$NON-NLS-1$
	/** EMPTY icon */
	public static final String ICON_NAME_EMPTY = "EMPTY"; //$NON-NLS-1$

	/**
	 * Icons By Color List
	 */
	public static final Map<String, Map<String, String>> ICONS;
	static {

		// Initialize
		Map<String, Map<String, String>> iconsMap = new HashMap<>();

		// NOT FOUND ICON
		Map<String, String> iconNotFound = new HashMap<>();
		iconNotFound.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/not_found_icon_128.png"); //$NON-NLS-1$
		iconNotFound.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE),
				"icons/global/not_found_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_NOT_FOUND, iconNotFound);

		// ADD
		Map<String, String> iconAdd = new HashMap<>();
		iconAdd.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/add_icon_128.png"); //$NON-NLS-1$
		iconAdd.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN), "icons/global/add_icon_128_green.png"); //$NON-NLS-1$
		iconAdd.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), "icons/global/add_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_ADD, iconAdd);

		// AGGREGATE
		Map<String, String> iconAggregate = new HashMap<>();
		iconAggregate.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/aggregate_icon_128.png"); //$NON-NLS-1$
		iconAggregate.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLUE),
				"icons/global/aggregate_icon_128_blue.png"); //$NON-NLS-1$
		iconAggregate.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_YELLOW),
				"icons/global/aggregate_icon_128_yellow.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_AGGREGATE, iconAggregate);

		// ASSESS
		Map<String, String> iconAssess = new HashMap<>();
		iconAssess.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/assess_icon_128.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_ASSESS, iconAssess);

		// BACK
		Map<String, String> iconBack = new HashMap<>();
		iconBack.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/back_icon_128.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_BACK, iconBack);

		// CHANGE
		Map<String, String> iconChange = new HashMap<>();
		iconChange.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/change_icon_128.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_CHANGE, iconChange);

		// CLOSE
		Map<String, String> iconClose = new HashMap<>();
		iconClose.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/close_icon_128.png"); //$NON-NLS-1$
		iconClose.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_RED), "icons/global/close_icon_128_red.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_CLOSE, iconClose);

		// COLLAPSE
		Map<String, String> iconCollapse = new HashMap<>();
		iconCollapse.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/collapse_icon_128_black.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_COLLAPSE, iconCollapse);

		// COMMUNICATE
		Map<String, String> iconCommunicate = new HashMap<>();
		iconCommunicate.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/communicate_icon_128.png"); //$NON-NLS-1$
		iconCommunicate.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY),
				"icons/global/communicate_icon_128_primary.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_COMMUNICATE, iconCommunicate);

		// CONFIG
		Map<String, String> iconConfig = new HashMap<>();
		iconConfig.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/config_icon_128.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_CONFIG, iconConfig);

		// CONNECT
		Map<String, String> iconConnect = new HashMap<>();
		iconConnect.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/connect_icon_128.png"); //$NON-NLS-1$
		iconConnect.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE),
				"icons/global/connect_icon_128_white.png"); //$NON-NLS-1$
		iconConnect.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GRAY_DARK),
				"icons/global/connect_icon_128_gray-dark.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_CONNECT, iconConnect);

		// COPY
		Map<String, String> iconCopy = new HashMap<>();
		iconCopy.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/copy_icon_128_black.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_COPY, iconCopy);

		// DELETE
		Map<String, String> iconDelete = new HashMap<>();
		iconDelete.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/delete_icon_128.png"); //$NON-NLS-1$
		iconDelete.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE),
				"icons/global/delete_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_DELETE, iconDelete);

		// DISCONNECT
		Map<String, String> iconDisconnect = new HashMap<>();
		iconDisconnect.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/disconnect_icon_128.png"); //$NON-NLS-1$
		iconDisconnect.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE),
				"icons/global/disconnect_icon_128_white.png"); //$NON-NLS-1$
		iconDisconnect.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GRAY_DARK),
				"icons/global/disconnect_icon_128_gray-dark.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_DISCONNECT, iconDisconnect);

		// DUPLICATE
		Map<String, String> iconDuplicate = new HashMap<>();
		iconDuplicate.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/duplicate_icon_128.png"); //$NON-NLS-1$
		iconDuplicate.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE),
				"icons/global/duplicate_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_DUPLICATE, iconDuplicate);

		// EDIT
		Map<String, String> iconEdit = new HashMap<>();
		iconEdit.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/edit_icon_128.png"); //$NON-NLS-1$
		iconEdit.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN), "icons/global/edit_icon_128_green.png"); //$NON-NLS-1$
		iconEdit.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), "icons/global/edit_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_EDIT, iconEdit);

		// EMPTY
		Map<String, String> iconEmpty = new HashMap<>();
		iconEmpty.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/empty_icon.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_EMPTY, iconEmpty);

		// EXAMINE
		Map<String, String> iconExamine = new HashMap<>();
		iconExamine.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/examine_icon_128.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_EXAMINE, iconExamine);

		// EXPAND
		Map<String, String> iconExpand = new HashMap<>();
		iconExpand.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/expand_icon_128_black.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_EXPAND, iconExpand);

		// EXPORT
		Map<String, String> iconExport = new HashMap<>();
		iconExport.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/export_icon_128_black.png"); //$NON-NLS-1$
		iconExport.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE),
				"icons/global/export_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_EXPORT, iconExport);

		// FAIL
		Map<String, String> iconFail = new HashMap<>();
		iconFail.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/fail_icon_128_red.png"); //$NON-NLS-1$
		iconFail.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_RED), "icons/global/fail_icon_128_red.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_FAIL, iconFail);

		// GEN_CF_REPORT
		Map<String, String> iconGenCfReport = new HashMap<>();
		iconGenCfReport.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/gen_cf_report_icon_128.png"); //$NON-NLS-1$
		iconGenCfReport.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY),
				"icons/global/gen_cf_report_icon_128_primary.png"); //$NON-NLS-1$
		iconGenCfReport.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE),
				"icons/global/gen_cf_report_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_GEN_CF_REPORT, iconGenCfReport);

		// HELP
		Map<String, String> iconHelp = new HashMap<>();
		iconHelp.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/help_icon_128.png"); //$NON-NLS-1$
		iconHelp.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLUE), "icons/global/help_icon_128_blue.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_HELP, iconHelp);

		// INFO
		Map<String, String> iconInfo = new HashMap<>();
		iconInfo.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/info_icon_128.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_INFO, iconInfo);

		// IMMUTABLE
		Map<String, String> iconIock = new HashMap<>();
		iconIock.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/lock_icon_128.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_IMMUTABLE, iconIock);

		// IMPORT
		Map<String, String> iconImport = new HashMap<>();
		iconImport.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/import_icon_128_black.png"); //$NON-NLS-1$
		iconImport.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE),
				"icons/global/import_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_IMPORT, iconImport);

		// MUTABLE
		Map<String, String> iconMencil = new HashMap<>();
		iconMencil.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/pencil_icon_128.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_MUTABLE, iconMencil);

		// NOT SAVE
		Map<String, String> iconNotSave = new HashMap<>();
		iconNotSave.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/not_save_icon_128.png"); //$NON-NLS-1$
		iconNotSave.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE),
				"icons/global/not_save_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_NOTSAVE, iconNotSave);

		// OPEN
		Map<String, String> iconOpen = new HashMap<>();
		iconOpen.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/open_icon_128_black.png"); //$NON-NLS-1$
		iconOpen.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GRAY), "icons/global/open_icon_128_gray.png"); //$NON-NLS-1$
		iconOpen.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), "icons/global/open_icon_128.png"); //$NON-NLS-1$
		iconOpen.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY),
				"icons/global/open_icon_128_primary.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_OPEN, iconOpen);

		// PCMM
		Map<String, String> iconPcmm = new HashMap<>();
		iconPcmm.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/pcmm_icon_128.png"); //$NON-NLS-1$
		iconPcmm.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY),
				"icons/global/pcmm_icon_128_primary.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_PCMM, iconPcmm);

		// PCMM_BIS
		Map<String, String> iconPcmmIco = new HashMap<>();
		iconPcmmIco.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/pcmm_icon_128_bis.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_PCMM_BIS, iconPcmmIco);

		// PIRT
		Map<String, String> iconPirt = new HashMap<>();
		iconPirt.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/pirt_icon_128.png"); //$NON-NLS-1$
		iconPirt.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY),
				"icons/global/pirt_icon_128_primary.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_PIRT, iconPirt);

		// PIRT
		Map<String, String> iconPlanning = new HashMap<>();
		iconPlanning.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/planning_icon_black_128.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_PLANNING, iconPlanning);

		// QUERY
		Map<String, String> iconQuery = new HashMap<>();
		iconQuery.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/query_icon_128.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_QUERY, iconQuery);

		// RESET
		Map<String, String> iconRese = new HashMap<>();
		iconRese.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/reset_icon_128.png"); //$NON-NLS-1$
		iconRese.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_RED), "icons/global/reset_icon_128_red.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_RESET, iconRese);

		// REFERENCE
		Map<String, String> iconRefe = new HashMap<>();
		iconRefe.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/book.png"); //$NON-NLS-1$
		iconRefe.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), "icons/global/book-white.png"); //$NON-NLS-1$
		iconRefe.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY), "icons/global/book-primary.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_REFERENCE, iconRefe);

		// SAVE
		Map<String, String> iconSave = new HashMap<>();
		iconSave.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/save_icon_128.png"); //$NON-NLS-1$
		iconSave.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN), "icons/global/save_icon_128_green.png"); //$NON-NLS-1$
		iconSave.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), "icons/global/save_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_SAVE, iconSave);

		// STAMP
		Map<String, String> iconStamp = new HashMap<>();
		iconStamp.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/stamp_icon_128.png"); //$NON-NLS-1$
		iconStamp.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLUE), "icons/global/stamp_icon_128_blue.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_STAMP, iconStamp);

		// TAG
		Map<String, String> iconTag = new HashMap<>();
		iconTag.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/tag_icon_128.png"); //$NON-NLS-1$
		iconTag.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BROWN), "icons/global/tag_icon_128_brown.png"); //$NON-NLS-1$
		iconTag.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), "icons/global/tag_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_TAG, iconTag);

		// UTD: UP TO DATE
		Map<String, String> iconUpToDate = new HashMap<>();
		iconUpToDate.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/utd_icon_128.png"); //$NON-NLS-1$
		iconUpToDate.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN), "icons/global/utd_icon_128_green.png"); //$NON-NLS-1$
		iconUpToDate.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY),
				"icons/global/utd_icon_128_primary.png"); //$NON-NLS-1$
		iconUpToDate.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), "icons/global/utd_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_UPTODATE, iconUpToDate);

		// VIEW
		Map<String, String> iconView = new HashMap<>();
		iconView.put(ConstantTheme.getColor(ICON_COLOR_DEFAULT), "icons/global/view_icon_128.png"); //$NON-NLS-1$
		iconView.put(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WHITE), "icons/global/view_icon_128_white.png"); //$NON-NLS-1$
		iconsMap.put(ICON_NAME_VIEW, iconView);

		ICONS = Collections.unmodifiableMap(iconsMap);
	}

	/**
	 * Private constructor to not allow instantiation.
	 */
	private IconTheme() {
	}

	/**
	 * Get icon default Color
	 *
	 * @return The default icon color
	 */

	public static String getDefaultColor() {
		return ConstantTheme.getColor(ICON_COLOR_DEFAULT);
	}

	/**
	 * Get Icon Path for icon with color (default color if not found)
	 *
	 * @param iconName the icon name
	 * @param color    the icon color
	 * @return the icon image path
	 */
	public static String getIconPath(String iconName, String color) {

		// Initialize
		String iconPath = null;

		// Found good color
		if (ICONS.containsKey(iconName) && ICONS.get(iconName).containsKey(color)) {
			iconPath = ICONS.get(iconName).get(color);
		}
		// Try to find in default color
		else if (ICONS.containsKey(iconName) && !ICONS.get(iconName).containsKey(color)
				&& ICONS.get(iconName).containsKey(getDefaultColor())) {
			iconPath = ICONS.get(iconName).get(getDefaultColor());
		}
		// icon not found: return a not found icon
		else {
			if (ICONS.get(ICON_NAME_NOT_FOUND).containsKey(color)) {
				iconPath = ICONS.get(ICON_NAME_NOT_FOUND).get(color);
			} else {
				iconPath = ICONS.get(ICON_NAME_NOT_FOUND).get(getDefaultColor());
			}
		}
		return iconPath;
	}

	/**
	 * Get Icon Image with default size
	 * 
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param iconName the icon name
	 * @param color    the icon color
	 * @return the image at @param imagePath in the @param display with
	 *         width= @param width and height= @param height
	 */
	public static Image getIconImage(ResourceManager rscMgr, String iconName, String color) {

		int iconSize = ICON_SIZE_DEFAULT_WINDOWS;
		if (!SystemTools.isWindows()) {
			iconSize = ICON_SIZE_DEFAULT_UNIX;
		}

		return getIconImage(rscMgr, iconName, color, iconSize);
	}

	/**
	 * Get Icon Image
	 *
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param iconName the icon name
	 * @param color    the color
	 * @param size     the size
	 * @return the image at @param imagePath in the @param display with
	 *         width= @param width and height= @param height
	 */
	public static Image getIconImage(ResourceManager rscMgr, String iconName, String color, int size) {

		if (rscMgr == null) {
			return null;
		}

		// Manage Null color
		if (null == color) {
			color = ConstantTheme.getColor(ICON_COLOR_DEFAULT);
		}

		// Get Image Resource
		IconImageDescriptor descriptor = IconImageDescriptor.createFrom(iconName, color, size);

		return rscMgr.createImage(descriptor);
	}

}
