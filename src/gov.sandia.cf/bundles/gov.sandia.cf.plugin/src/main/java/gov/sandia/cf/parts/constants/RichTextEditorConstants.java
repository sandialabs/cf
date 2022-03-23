/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.constants;

import gov.sandia.cf.tools.RscTools;

/**
 * Eclipse RichText Editor Resource Constants
 * 
 * @author Didier Verstraete
 *
 */
public class RichTextEditorConstants {

	/**
	 * Key for native spell checker configuration.
	 */
	public static final String DISABLE_NATIVE_SPELL_CHECKER = "disableNativeSpellChecker"; //$NON-NLS-1$

	/** The Constant PLUGIN_ELEMENTSPATH. */
	public static final String PLUGIN_ELEMENTSPATH = "elementspath"; //$NON-NLS-1$

	/**
	 * The Constant PLUGIN_SCAYT to reference CKEditor SCAYT spell checker plugin.
	 */
	public static final String PLUGIN_SCAYT = "scayt"; //$NON-NLS-1$

	/** The Constant PLUGIN_CONTEXTMENU. */
	public static final String PLUGIN_CONTEXTMENU = "contextmenu"; //$NON-NLS-1$

	/** The Constant PLUGINS_TO_REMOVE. */
	public static final String PLUGINS_TO_REMOVE = String.join(RscTools.COMMA, PLUGIN_ELEMENTSPATH, PLUGIN_SCAYT,
			PLUGIN_CONTEXTMENU);

	/**
	 * Rich Text editor default toolbar
	 */
	public static final String DEFAULT_TOOLBAR = "[" //$NON-NLS-1$
			+ "{ name: 'clipboard', groups: [ 'undo', 'clipboard' ] }," //$NON-NLS-1$
			+ "{ name: 'other' }," //$NON-NLS-1$
			+ "{ name: 'styles' }," //$NON-NLS-1$
			+ "{ name: 'editing', groups: [ 'find', 'selection', 'editing' ] }," //$NON-NLS-1$ // 'spellchecker',
			+ "'/'," //$NON-NLS-1$
			+ "{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] }," //$NON-NLS-1$
			+ "{ name: 'colors' }," //$NON-NLS-1$
			+ "{ name: 'paragraph', groups: [ 'list', 'indent', 'align' ] }," //$NON-NLS-1$
			+ "]"; //$NON-NLS-1$
	/**
	 * Rich Text editor empty toolbar
	 */
	public static final String EMPTY_TOOLBAR = "[]"; //$NON-NLS-1$

	/**
	 * Do not instantiate.
	 */
	private RichTextEditorConstants() {

	}
}
