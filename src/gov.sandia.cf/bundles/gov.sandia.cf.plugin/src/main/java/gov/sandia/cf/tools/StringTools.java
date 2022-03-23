/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.text.StringEscapeUtils;

/**
 * The String Resource Constants
 * 
 * @author Didier Verstraete
 *
 */
public class StringTools {

	/**
	 * The PREFIX int value to be used to append a string to an existing one
	 */
	public static final int PREFIX = -1;
	/**
	 * The SUFFIX int value to be used to append a string to an existing one
	 */
	public static final int SUFFIX = 1;

	/**
	 * Private constructor to not allow instantiation.
	 */
	private StringTools() {
	}

	/**
	 * @param string1 the string 1
	 * @param string2 the string 2
	 * @return true if the two string are equals or null, otherwise false
	 */
	public static boolean equals(String string1, String string2) {
		// Initialize
		Boolean isEquals = false;

		// String1 not null
		if (string1 != null) {
			// String 1 equals String2
			if (string1.equals(string2)) {
				isEquals = true;
			}
		} else {
			// Both null
			if (string2 == null) {
				isEquals = true;
			}
		}

		// Result
		return isEquals;
	}

	/**
	 * @param text   the text to build
	 * @param insert the text to insert
	 * @param period the periodicity
	 * @return a string with the insert string inserted periodically (period
	 *         parameter) into text
	 */
	public static String insertPeriodically(String text, String insert, int period) {

		if (period == 0 || insert == null || insert.isEmpty()) {
			return text;
		}

		StringBuilder builder = new StringBuilder(text.length() + insert.length() * (text.length() / period) + 1);

		int index = 0;
		String prefix = RscTools.empty();
		while (index < text.length()) {
			// Don't put the insert in the very first iteration.
			// This is easier than appending it *after* each substring
			builder.append(prefix);
			prefix = insert;
			builder.append(text.substring(index, Math.min(index + period, text.length())));
			index += period;
		}

		return builder.toString();
	}

	/**
	 * Change \n to <br>
	 * 
	 * @param text the text to replace
	 * @return the text replaced
	 */
	public static String nl2br(String text) {
		return text.replace("\n", "<br>"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Clear HTML in a HTML string
	 * 
	 * @param html                  the HTML string source
	 * @param replaceCarriageReturn do remove carriage return?
	 * @return the string without HTML entity
	 */
	public static String clearHtml(String html, boolean replaceCarriageReturn) {
		String text = ""; //$NON-NLS-1$
		if (null != html) {
			// Remove HTML tags
			text = html.replaceAll("\\<[^>]*>", ""); //$NON-NLS-1$ //$NON-NLS-2$
			text = text.trim();

			// Decode HTML entities
			text = StringEscapeUtils.unescapeHtml4(text);
			text = text.trim();

			// Remove multiple space
			text = text.replaceAll("( )+", " "); //$NON-NLS-1$ //$NON-NLS-2$

			// remove carriage return
			if (replaceCarriageReturn) {
				text = text.replaceAll("(\n)+", " "); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return text;
	}

	/**
	 * Clear HTML in a HTML string
	 * 
	 * @param html the HTML string source
	 * @return the string without HTML entity
	 */
	public static String clearHtml(String html) {
		return clearHtml(html, false);
	}

	/**
	 * @param value the value to return
	 * @return the value if not null, otherwise empty string
	 */
	public static String getOrEmpty(Object value) {
		return value != null ? value.toString() : RscTools.empty();
	}

	/**
	 * @param value the value to return
	 * @return the value if not null, otherwise empty string
	 */
	public static String getOrEmpty(String value) {
		return value != null ? value : RscTools.empty();
	}

	/**
	 * @param html the html text to clear
	 * @return a html cleared text with string content
	 */
	public static String htmlToStringText(String html) {
		// disable html tags
		return html != null ? html.replace("<li>", "\n  - ").replace("</li>", "").replace("<ul>", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				.replace("</ul>", "") : null; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @param value         the string value
	 * @param valueToAppend the string value to append
	 * @param appendType    the append type (PREFIX, SUFFIX)
	 * @param addLineBreak  add a line break between the values to append
	 * @return the string with the value appended
	 */
	public static String appendTo(String value, String valueToAppend, int appendType, boolean addLineBreak) {
		StringBuilder str = new StringBuilder();
		if (appendType == PREFIX) {
			str.append(getOrEmpty(valueToAppend));
			if (addLineBreak) {
				str.append(RscTools.CARRIAGE_RETURN);
			}
		}
		str.append(getOrEmpty(value));
		if (appendType == SUFFIX) {
			if (addLineBreak) {
				str.append(RscTools.CARRIAGE_RETURN);
			}
			str.append(getOrEmpty(valueToAppend));
		}

		return str.toString();
	}

	/**
	 * Removes the non printable chars.
	 *
	 * @param <K>     the key type
	 * @param <V>     the value type
	 * @param toParse the to parse
	 * @return the map
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> removeNonPrintableChars(Map<K, V> toParse) {
		if (toParse == null) {
			return null;
		}

		Map<K, V> newMap = new HashMap<>();

		for (Entry<K, V> entry : toParse.entrySet()) {

			K key = entry.getKey();
			V value = entry.getValue();

			if (key instanceof String && StringTools.hasNonPrintableChars((String) key)) {
				key = (K) StringTools.removeNonPrintableChars((String) key);
			}

			if (value instanceof String && StringTools.hasNonPrintableChars((String) value)) {
				value = (V) StringTools.removeNonPrintableChars((String) value);
			} else if (value instanceof Map) {
				value = (V) StringTools.removeNonPrintableChars((Map<?, ?>) value);
			} else if (value instanceof List) {
				value = (V) StringTools.removeNonPrintableChars((List<?>) value);
			}

			newMap.put(key, value);
		}

		return newMap;
	}

	/**
	 * Removes the non printable chars.
	 *
	 * @param <V>     the value type
	 * @param toParse the to parse
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public static <V> List<V> removeNonPrintableChars(List<V> toParse) {
		if (toParse == null) {
			return new ArrayList<>();
		}

		List<V> newList = new ArrayList<>();

		for (V value : toParse) {

			if (value instanceof String && StringTools.hasNonPrintableChars((String) value)) {
				value = (V) StringTools.removeNonPrintableChars((String) value);
			} else if (value instanceof Map) {
				value = (V) StringTools.removeNonPrintableChars((Map<?, ?>) value);
			} else if (value instanceof List) {
				value = (V) StringTools.removeNonPrintableChars((List<?>) value);
			}

			newList.add(value);
		}

		return newList;
	}

	/**
	 * Checks for non printable chars.
	 *
	 * @param string the string
	 * @return true, if successful
	 */
	public static boolean hasNonPrintableChars(final String string) {
		if (string == null) {
			return false;
		}

		return !StringTools.isPrintable(string);
	}

	/**
	 * Checks if is printable.
	 *
	 * @param data the data
	 * @return true, if is printable
	 */
	public static boolean isPrintable(final String data) {
		if (data == null) {
			return false;
		}

		final int length = data.length();
		for (int offset = 0; offset < length;) {
			final int codePoint = data.codePointAt(offset);

			if (!isPrintable(codePoint)) {
				return false;
			}

			offset += Character.charCount(codePoint);
		}

		return true;
	}

	/**
	 * Checks if is printable.
	 *
	 * @param c the c
	 * @return true, if is printable
	 */
	public static boolean isPrintable(final int c) {
		return (c >= 0x20 && c <= 0x7E) || c == 0x9 || c == 0xA || c == 0xD || c == 0x85 || (c >= 0xA0 && c <= 0xD7FF)
				|| (c >= 0xE000 && c <= 0xFFFD) || (c >= 0x10000 && c <= 0x10FFFF);
	}

	/**
	 * Removes the non printable chars.
	 *
	 * @param data the data
	 * @return the string
	 */
	public static String removeNonPrintableChars(final String data) {
		if (data == null) {
			return null;
		}

		String toReturn = data;
		List<String> toDelete = new ArrayList<>();

		final int length = data.length();
		for (int offset = 0; offset < length;) {
			final int codePoint = data.codePointAt(offset);

			if (!isPrintable(codePoint)) {
				toDelete.add(Character.toString(data.charAt(offset)));
			}

			offset += Character.charCount(codePoint);
		}

		for (String charToDelete : toDelete) {
			toReturn = toReturn.replace(charToDelete, ""); //$NON-NLS-1$
		}

		return toReturn;
	}

}