/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.constants;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * @author Didier Verstraete
 *
 */
public class CredibilityFrameworkConstants {

	/**
	 * The credibility editor id referenced in plugin.xml
	 */
	public static final String CREDIBILITY_EDITOR_ID = "gov.sandia.cf.credibility-editor"; //$NON-NLS-1$

	/**
	 * The credibility view id referenced in plugin.xml
	 */
	public static final String CREDIBILITY_VIEW_ID = "gov.sandia.cf.credibility-view"; //$NON-NLS-1$

	/**
	 * the guidance view id
	 */
	public static final String GUIDANCE_VIEW_ID = "gov.sandia.cf.cf-guidance-view"; //$NON-NLS-1$

	/**
	 * The plugin name
	 */
	public static final String CF_PLUGIN_NAME = "gov.sandia.cf.plugin"; //$NON-NLS-1$

	/** The Constant CF_PACKAGE_ROOT. */
	public static final String CF_PACKAGE_ROOT = "gov.sandia.cf"; //$NON-NLS-1$

	/** The Constant CF_EXTENSIONPOINT_PREDEFINEDPROPERTIES_ID. */
	public static final String CF_EXTENSIONPOINT_PREDEFINEDPROPERTIES_ID = "cfPropertiesDefinition"; //$NON-NLS-1$

	/** The Constant CF_EXTENSIONPOINT_PREDEFINEDPROPERTIES_CLASS. */
	public static final String CF_EXTENSIONPOINT_PREDEFINEDPROPERTIES_CLASS = "predefinedPropertiesClass"; //$NON-NLS-1$

	/**
	 * Properties to manage the views outside of the current part
	 */
	/** ACTIVEVIEW keyword */
	public static final String PART_PROPERTY_ACTIVEVIEW = "ACTIVEVIEW"; //$NON-NLS-1$
	/** PCMM_SELECTED_ASSESSABLE keyword */
	public static final String PART_PROPERTY_ACTIVEVIEW_PCMM_SELECTED_ASSESSABLE = "PCMM_SELECTED_ASSESSABLE"; //$NON-NLS-1$

	/**
	 * Do not instantiate.
	 */
	private CredibilityFrameworkConstants() {
	}

	/**
	 * @return the cf bundle
	 */
	public static Bundle getBundle() {
		return Platform.getBundle(CredibilityFrameworkConstants.CF_PLUGIN_NAME);
	}

	/**
	 * @return the cf bundle version
	 */
	public static Version getBundleVersion() {
		Version cfVersion = null;
		Bundle cfBundle = getBundle();
		if (cfBundle != null) {
			cfVersion = cfBundle.getVersion();
		}
		return cfVersion;
	}
}
