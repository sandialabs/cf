/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date Utils class
 * 
 * @author Didier Verstraete
 *
 */
public class DateTools {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(DateTools.class);
	/**
	 * DATE_FORMAT_REPORT constants
	 */
	public static final String DATE_FORMAT_REPORT_US = "MMMM dd, yyyy"; //$NON-NLS-1$
	/**
	 * DATE_TIME_FORMAT_HASH constants
	 */
	public static final String DATE_TIME_FORMAT_HASH = "yyyyMMddHHmmss"; //$NON-NLS-1$
	/**
	 * DATE_TIME_UTC_FORMAT constants
	 */
	public static final String DATE_TIME_UTC_FORMAT = "yyyy-MM-dd HH:mm:ss'Z'"; //$NON-NLS-1$

	/**
	 * Private constructor to not allow instantiation.
	 */
	private DateTools() {
	}

	/**
	 * @return the date format
	 */
	public static String getDateFormat() {
		return RscTools.getString(RscConst.DATE_FORMAT);
	}

	/**
	 * @return the date format short
	 */
	public static String getDateFormatShort() {
		return RscTools.getString(RscConst.DATE_FORMAT_SHORT);
	}

	/**
	 * @return the date format report US
	 */
	public static String getDateFormatReportUS() {
		return DATE_FORMAT_REPORT_US;
	}

	/**
	 * @return the date time format
	 */
	public static String getDateTimeFormat() {
		return RscTools.getString(RscConst.DATETIME_FORMAT);
	}

	/**
	 * @param pattern the date pattern
	 * @return a new simple date format with @param pattern
	 */
	public static DateFormat getDateFormatter(String pattern) {
		return new SimpleDateFormat(pattern);
	}

	/**
	 * @param date the date to convert
	 * @return the date formatted to DATE_FORMAT
	 */
	public static String formatDate(Date date) {
		return date != null ? getDateFormatter(getDateFormat()).format(date) : RscTools.empty();
	}

	/**
	 * @param date the date to convert
	 * @return the date formatted to DATE_TIME_UTC_FORMAT
	 */
	public static String formatDateUTC(Date date) {
		return date != null ? getDateFormatter(DATE_TIME_UTC_FORMAT).format(date) : RscTools.empty();
	}

	/**
	 * @param date    the date to convert
	 * @param pattern the date pattern
	 * @return the date formatted to @param pattern
	 */
	public static String formatDate(Date date, String pattern) {
		if (pattern == null || pattern.isEmpty()) {
			return formatDate(date);
		} else {
			return date != null ? getDateFormatter(pattern).format(date) : RscTools.empty();
		}
	}

	/**
	 * @param date    the date to parse
	 * @param pattern the date pattern
	 * @return the date parsed with @param pattern
	 */
	public static Date parseDate(String date, String pattern) {
		if (date == null || date.isEmpty()) {
			return null;
		}

		// get date formatter
		DateFormat formatter = getDateFormatter(getDateFormat());
		if (pattern != null && !pattern.isEmpty()) {
			formatter = getDateFormatter(getDateFormat());
		}

		// format date
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			logger.error("Parsing date error: {}", e.getMessage(), e); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * @return the current date formatted to default format @see DATE_FORMAT
	 */
	public static String getDateFormatted() {
		return formatDate(getCurrentDate(), getDateFormat());
	}

	/**
	 * @return the current date formatted to format @see DATE_FORMAT_SHORT
	 */
	public static String getDateFormattedShort() {
		return formatDate(getCurrentDate(), getDateFormatShort());
	}

	/**
	 * @return the current date formatted to format @see DATETIME_FORMAT
	 */
	public static String getDateFormattedDateTime() {
		return formatDate(getCurrentDate(), getDateTimeFormat());
	}

	/**
	 * @return the current date formatted to format @see DATE_TIME_FORMAT_HASH
	 */
	public static String getDateFormattedDateTimeHash() {
		return formatDate(getCurrentDate(), DateTools.DATE_TIME_FORMAT_HASH);
	}

	/**
	 * @return the default date
	 */
	public static Date getDefault1900Date() {
		Calendar cal = Calendar.getInstance();
		cal.set(1900, 0, 0);
		return cal.getTime();
	}

	/**
	 * @return the current date
	 */
	public static Date getCurrentDate() {
		return new Date();
	}

}
