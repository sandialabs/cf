/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class DateToolsTest {

	@Test
	void testDateFormat() {
		assertEquals(RscTools.getString(RscConst.DATE_FORMAT), DateTools.getDateFormat());
		assertEquals(RscTools.getString(RscConst.DATE_FORMAT_SHORT), DateTools.getDateFormatShort());
		assertEquals(RscTools.getString(RscConst.DATETIME_FORMAT), DateTools.getDateTimeFormat());
	}

	@Test
	void testGetDateFormatter() {
		assertNotNull(DateTools.getDateFormatter(DateTools.getDateFormat()));
	}

	@Test
	void testFormatDate() {

		Date date = new GregorianCalendar(2014, Calendar.FEBRUARY, 11, 12, 5, 8).getTime();
		SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM"); //$NON-NLS-1$
		String month = monthFormatter.format(date);

		// default date formatter
		assertEquals(month + " 11, 2014", DateTools.formatDate(date)); //$NON-NLS-1$
		assertNotNull(DateTools.getDateFormatted());
		assertFalse(DateTools.getDateFormatted().isEmpty());

		// short date formatter
		assertEquals("02/11/14", DateTools.formatDate(date, DateTools.getDateFormatShort())); //$NON-NLS-1$
		assertNotNull(DateTools.getDateFormattedShort());
		assertFalse(DateTools.getDateFormattedShort().isEmpty());

		// datetime formatter
		assertEquals(month + " 11, 2014 12:05:08", DateTools.formatDate(date, DateTools.getDateTimeFormat())); //$NON-NLS-1$
		assertNotNull(DateTools.getDateFormattedDateTime());
		assertFalse(DateTools.getDateFormattedDateTime().isEmpty());

		// hash formatter
		assertEquals("20140211120508", DateTools.formatDate(date, DateTools.DATE_TIME_FORMAT_HASH)); //$NON-NLS-1$
		assertNotNull(DateTools.getDateFormattedDateTimeHash());
		assertFalse(DateTools.getDateFormattedDateTimeHash().isEmpty());

		assertEquals("20140211120508", DateTools.formatDate(date, DateTools.DATE_TIME_FORMAT_HASH)); //$NON-NLS-1$

		// Pattern null
		assertNotNull(DateTools.formatDate(date, null));

		// Pattern not null but Date null
		assertEquals(DateTools.formatDate(null, DateTools.DATE_TIME_FORMAT_HASH), RscTools.empty());
	}

	@Test
	void testFormatDateFail() {
		assertEquals(RscTools.empty(), DateTools.formatDate(null));
		assertEquals(RscTools.empty(), DateTools.formatDate(null, RscTools.empty()));
		Date date = new Date();
		assertEquals(DateTools.formatDate(date), DateTools.formatDate(date, RscTools.empty()));
	}
}
