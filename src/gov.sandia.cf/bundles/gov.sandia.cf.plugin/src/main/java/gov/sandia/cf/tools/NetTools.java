/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.preferences.PrefTools;

/**
 * This class gives methods to interact with execution OS
 * 
 * @author Didier Verstraete
 *
 */
public class NetTools {

	/**
	 * Charset UTF-8 encoding for URL specific characters.
	 * 
	 * @author Didier Verstraete
	 *
	 */
	@SuppressWarnings("javadoc")
	public enum URLEncodingCharset {

		BLANK_SPACE(" ", "%20"); //$NON-NLS-1$ //$NON-NLS-2$

		/**
		 * the original charset
		 */
		private String charset;
		/**
		 * the encoded charset
		 */
		private String encodedCharset;

		/**
		 * The constructor
		 * 
		 * @param charset       the charset
		 * @param encodeCharset the encoded charset
		 */
		URLEncodingCharset(String charset, String encodeCharset) {
			this.charset = charset;
			this.encodedCharset = encodeCharset;
		}

		public String charset() {
			return charset;
		}

		public String encodedCharset() {
			return encodedCharset;
		}
	}

	/**
	 * Private constructor to not allow instantiation.
	 */
	private NetTools() {
	}

	/**
	 * @param url the url to check
	 * @return true if the url is valid, otherwise false.
	 */
	public static boolean isValidURL(String url) {
		return new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS | UrlValidator.ALLOW_2_SLASHES).isValid(url);
	}

	/**
	 * Open the URL depending of the preferences
	 * 
	 * @param url    the url to reach
	 * @param option the open link browser option
	 * @throws PartInitException     if an error occurs during url opening
	 * @throws MalformedURLException if the url is malformed
	 */
	public static void openURL(String url, OpenLinkBrowserOption option)
			throws PartInitException, MalformedURLException {

		OpenLinkBrowserOption finalOption = option;

		if (finalOption == null || finalOption.equals(OpenLinkBrowserOption.CF_PREFERENCE)) {
			try {
				finalOption = OpenLinkBrowserOption
						.valueOf(PrefTools.getPreference(PrefTools.GLOBAL_OPEN_LINK_BROWSER_OPTION_KEY));
			} catch (IllegalArgumentException e) {
				finalOption = OpenLinkBrowserOption.ECLIPSE_PREFERENCE;
			}
		}

		switch (finalOption) {
		case EXTERNAL_BROWSER:
			openURLExternally(url);
			break;
		case INTERNAL_BROWSER:
			openURLInternally(url);
			break;
		default:
			openURLViaEclipsePref(url);
			break;
		}
	}

	/**
	 * Open the URL depending of the Eclipse preferences
	 * 
	 * @param url the url to reach
	 * @throws PartInitException     if an error occurs during url opening
	 * @throws MalformedURLException if the url is malformed
	 */
	public static void openURLViaEclipsePref(String url) throws PartInitException, MalformedURLException {
		if (org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
			PlatformUI.getWorkbench().getBrowserSupport().createBrowser(url).openURL(new URL(url));
		}
	}

	/**
	 * Open the URL in a new internal browser
	 * 
	 * @param url the url to reach
	 * @throws PartInitException     if an error occurs during url opening
	 * @throws MalformedURLException if the url is malformed
	 */
	public static void openURLInternally(String url) throws PartInitException, MalformedURLException {
		if (org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
			PlatformUI.getWorkbench().getBrowserSupport()
					.createBrowser(IWorkbenchBrowserSupport.AS_EDITOR, url, null, null).openURL(new URL(url));
		}
	}

	/**
	 * Open the URL in a new external browser
	 * 
	 * @param url the url to reach
	 * @throws PartInitException     if an error occurs during url opening
	 * @throws MalformedURLException if the url is malformed
	 */
	public static void openURLExternally(String url) throws PartInitException, MalformedURLException {
		if (StringUtils.isNotBlank(url)) {
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(url));
		}
	}
}
