/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.net.URL;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.osgi.framework.Bundle;

import gov.sandia.cf.constants.CredibilityFrameworkConstants;

/**
 * Help tools class used to launch Eclipse help
 * 
 * @author didier
 *
 */
public class HelpTools {

	/**
	 * The enum for contextual help
	 * 
	 * @author Didier Verstraete
	 *
	 */
	@SuppressWarnings("javadoc")
	public enum ContextualHelpId {
		PLANNING("planning"), //$NON-NLS-1$
		INTENDED_PURPOSE("intended_purpose"), //$NON-NLS-1$
		SYSTEM_REQUIREMENT("system_requirement"), //$NON-NLS-1$
		QOIPLANNING("qoi_planning"), //$NON-NLS-1$
		UNCERTAINTY("uncertainty"), //$NON-NLS-1$
		PCMM_PLANNING_ITEM("pcmm_planning_item"), //$NON-NLS-1$
		ANALYST_DECISION("analyst_decision"), //$NON-NLS-1$
		PIRT("pirt"), //$NON-NLS-1$
		PCMM("pcmm"), //$NON-NLS-1$
		PIRT_QOI("pirt_qoi"), //$NON-NLS-1$
		PIRT_PHENOMENA_VIEW("pirt_phenomena"), //$NON-NLS-1$
		PCMM_HOME("pcmm_home"), //$NON-NLS-1$
		PCMM_EVIDENCE("pcmm_evidence"), //$NON-NLS-1$
		PCMM_ASSESS("pcmm_assess"), //$NON-NLS-1$
		PCMM_AGGREGATE("pcmm_aggregate"), //$NON-NLS-1$
		PCMM_STAMP("pcmm_stamp"), //$NON-NLS-1$
		COMMUNICATE("communicate"), //$NON-NLS-1$
		REPORTING("reporting"), //$NON-NLS-1$
		CONFIGURATION("configuration"), //$NON-NLS-1$
		IMPORT("import"), //$NON-NLS-1$
		EXPORT("export"), //$NON-NLS-1$
		CRED_EVID_FOLDER_STRUCT("cred_evid_folder_struct"); //$NON-NLS-1$

		private String value;

		/***
		 * The constructor
		 * 
		 * @param value the value
		 */
		ContextualHelpId(String value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}
	}

	private static final String HELP_NODE_CONTEXTUAL = "gov.sandia.cf.plugin.help"; //$NON-NLS-1$

	private static final String HELP_EXTENSION = ".html"; //$NON-NLS-1$

	private static final String HELP_RESOURCE_PATH = "/src/main/resources/html/"; //$NON-NLS-1$

	/**
	 * Private constructor to not allow instantiation.
	 */
	private HelpTools() {
	}

	/**
	 * Opens a documentation page into Eclipse help
	 * 
	 * @param nodeType the node type
	 */
	public static void openDocumentationWebPage(String nodeType) {
		if (tryOpenDocumentationWebPage(nodeType, HELP_NODE_CONTEXTUAL))
			return;
		for (IExtensionPoint ep : Platform.getExtensionRegistry().getExtensionPoints()) {
			for (IExtension ext : ep.getExtensions()) {
				String bundle = ext.getContributor().getName();
				if (CredibilityFrameworkConstants.CF_PLUGIN_NAME.contentEquals(bundle)
						&& tryOpenDocumentationWebPage(nodeType, bundle)) {
					return;
				}
			}
		}
	}

	/**
	 * Opens the contextual help
	 * 
	 */
	public static void openContextualHelp() {
		IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		helpSystem.displayDynamicHelp();
	}

	/**
	 * @param nodeType the node type
	 * @param bundle   the bundle
	 * @return true if the Eclipse help has been opened
	 */
	private static boolean tryOpenDocumentationWebPage(String nodeType, String bundle) {
		String href = null;
		Bundle bundlePlugin = Platform.getBundle(bundle);
		final URL entry = bundlePlugin.getEntry(HELP_RESOURCE_PATH + nodeType + HELP_EXTENSION);
		if (entry == null)
			return false;
		href = FileTools.PATH_SEPARATOR + bundle + HELP_RESOURCE_PATH + nodeType + HELP_EXTENSION;

		IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		helpSystem.displayHelpResource(href);
		return true;
	}

	/**
	 * Add a contextual help to the composite in parameter
	 * 
	 * @param composite the composite
	 * @param helpId    the help id
	 */
	public static void addContextualHelp(Composite composite, ContextualHelpId helpId) {
		if (helpId != null && helpId.getValue() != null && composite != null) {
			PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
					CredibilityFrameworkConstants.CF_PLUGIN_NAME + "." + helpId.getValue()); //$NON-NLS-1$
		}
	}
}
