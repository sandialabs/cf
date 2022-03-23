/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class RestUrlBuilder.
 * 
 * @author Didier Verstraete
 */
public class RestUrlBuilder {

	private StringBuilder urlBuilder = new StringBuilder();

	/**
	 * Append.
	 *
	 * @param url the url
	 * @return the rest url builder
	 */
	public RestUrlBuilder append(String url) {
		if (StringUtils.isBlank(url)) {
			return this;
		}

		if (urlBuilder.length() > 0 && !"/".equals(urlBuilder.substring(urlBuilder.length() - 1)) //$NON-NLS-1$
				&& !url.startsWith("/")) { //$NON-NLS-1$
			urlBuilder.append("/"); //$NON-NLS-1$
		}
		urlBuilder.append(url);
		return this;
	}

	/**
	 * Gets the url.
	 *
	 * @return the string
	 */
	public String get() {
		return urlBuilder.toString();
	}

	/**
	 * Builds the url.
	 *
	 * @return the rest url builder
	 */
	public static RestUrlBuilder build() {
		return new RestUrlBuilder();
	}
}
