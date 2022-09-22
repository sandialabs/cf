/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages resources like icons, images,...
 * 
 * @author Didier Verstraete
 *
 */
public class RscTools {

	/*
	 * WARNING: the non-key constants should start with _ to be ignored of the
	 * tests. Every message key is tested.
	 */

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(RscTools.class);

	/**
	 * The Resource Bundle
	 */
	private static ResourceBundle rscBundle = null;
	/**
	 * The Locale
	 */
	private static Locale locale = null;
	/**
	 * The message bundle prefix name
	 */
	public static final String RESOURCE_BUNDLE_PREFIX = "MessagesBundle"; //$NON-NLS-1$
	/**
	 * The default language
	 */
	public static final String LOCALE_DEFAULT_LANGUAGE = "en"; //$NON-NLS-1$
	/**
	 * The default country
	 */
	public static final String LOCALE_DEFAULT_COUNTRY = "US"; //$NON-NLS-1$
	/**
	 * The error pattern
	 */
	public static final String MSG_ERROR_PATTERN = "??{0}??"; //$NON-NLS-1$

	/**
	 * String constants
	 */
	/** ASTERISK character */
	public static final String ASTERISK = "*"; //$NON-NLS-1$
	/** COMMA character + SPACE */
	public static final String COMMA = ", "; //$NON-NLS-1$
	/** PERCENT character */
	public static final String PERCENT = "%"; //$NON-NLS-1$
	/** HYPHEN character */
	public static final String HYPHEN = "-"; //$NON-NLS-1$
	/** PLUS character */
	public static final String PLUS = "+"; //$NON-NLS-1$
	/** UNDERSCORE character */
	public static final String UNDERSCORE = "_"; //$NON-NLS-1$
	/** COLON character + SPACE */
	public static final String COLON = ": "; //$NON-NLS-1$
	/** SEMICOLON character + SPACE */
	public static final String SEMICOLON = "; "; //$NON-NLS-1$
	/** SPACE character */
	public static final String SPACE = " "; //$NON-NLS-1$
	/** DOT character */
	public static final String DOT = "."; //$NON-NLS-1$
	/** THREE_DOTS characters */
	public static final String THREE_DOTS = "..."; //$NON-NLS-1$
	/** CARRIAGE_RETURN character */
	public static final String CARRIAGE_RETURN = "\n"; //$NON-NLS-1$
	/**  TAB character. */
	public static final String TAB = "\t"; //$NON-NLS-1$

	/**
	 * Private constructor to not allow instantiation.
	 */
	private RscTools() {
	}

	/**
	 * @return Locale defined in the plugin
	 */
	public static Locale getLocale() {
		if (locale == null) {
			locale = new Locale(LOCALE_DEFAULT_LANGUAGE, LOCALE_DEFAULT_COUNTRY);
		}
		return locale;
	}

	/**
	 * Sets the locale to change language and/or country preferences
	 * 
	 * @param language the language to translate
	 * @param country  the country code
	 */
	public static void setLocale(String language, String country) {
		if (language != null && !language.isEmpty() && country != null && !country.isEmpty()) {
			locale = new Locale(language, country);
		}
	}

	/**
	 * @return the resource bundle
	 */
	public static ResourceBundle getBundle() {
		if (rscBundle == null) {
			rscBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_PREFIX, getLocale());
		}
		return rscBundle;
	}

	/**
	 * @param key the string key
	 * @return the value of @param key in resource file
	 */
	public static String getString(String key) {
		try {
			return getBundle().getString(key);
		} catch (MissingResourceException e) {
			logger.error(e.getMessage(), e);
			return MessageFormat.format(MSG_ERROR_PATTERN, key);
		}
	}

	/**
	 * @param key       the string key
	 * @param arguments the bundle arguments to convert string
	 * @return the value of @param key in resource file formatted with @param
	 *         arguments
	 */
	public static String getString(String key, Object... arguments) {
		try {
			return MessageFormat.format(getBundle().getString(key), arguments);
		} catch (MissingResourceException e) {
			logger.error(e.getMessage(), e);
			return MessageFormat.format(MSG_ERROR_PATTERN, key);
		}
	}

	/**
	 * @return an empty string
	 */
	public static String empty() {
		return getBundle().getString(RscConst.EMPTY_STRING);
	}

	/**
	 * @return the carriage return char
	 */
	public static String carriageReturn() {
		return getBundle().getString(RscConst.CARRIAGE_RETURN);
	}

}
