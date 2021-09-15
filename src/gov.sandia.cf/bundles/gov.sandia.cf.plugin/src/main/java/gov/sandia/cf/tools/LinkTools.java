/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.net.MalformedURLException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.ParameterLinkGson;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.OpenLinkBrowserOption;

/**
 * This class gives methods for links
 * 
 * @author Didier Verstraete
 *
 */
public class LinkTools {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(LinkTools.class);

	private LinkTools() {
		// do not instantiate
	}

	/**
	 * Open a link stored in GSON
	 * 
	 * @param gsonValue the gson link
	 * @param option    the open link browser option
	 */
	public static void openLinkValue(String gsonValue, OpenLinkBrowserOption option) {

		// Get value
		ParameterLinkGson linkData = GsonTools.getFromGson(gsonValue, ParameterLinkGson.class);

		if (linkData != null && linkData.type != null && linkData.value != null) {
			// Check type - Local File
			if (FormFieldType.LINK_FILE.equals(linkData.type)) {
				WorkspaceTools.openFileInWorkspace(linkData.value);
			}
			// Check type - URL
			else if (FormFieldType.LINK_URL.equals(linkData.type)) {
				try {
					NetTools.openURL(linkData.value, option);
				} catch (PartInitException | MalformedURLException e) {
					logger.error("An error occurred while opening the link {}:\n{}", //$NON-NLS-1$
							gsonValue, e.getMessage(), e);
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							RscTools.getString(RscConst.ERROR_TITLE), e.getMessage());
				}
			}
		}
	}

	/**
	 * @param value the gson value to extract
	 * @return the type
	 */
	public static FormFieldType getType(String value) {
		FormFieldType type = null;
		if (value != null) {
			ParameterLinkGson linkGson = GsonTools.getFromGson(value, ParameterLinkGson.class);
			if (linkGson != null) {
				type = linkGson.type;
			}
		}
		return type;
	}

	/**
	 * @param value the gson value to extract
	 * @return the path
	 */
	public static String getPath(String value) {
		String path = null;
		if (value != null) {
			ParameterLinkGson linkGson = GsonTools.getFromGson(value, ParameterLinkGson.class);
			if (linkGson != null) {
				path = linkGson.value;
			}
		}
		return path;
	}
}
