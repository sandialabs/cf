/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.tools.RscTools;

/**
 * Test the string and number comparator
 * 
 * @author Didier Verstraete
 *
 */
class StringWithNumberAndNullableComparatorTest {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(StringWithNumberAndNullableComparatorTest.class);

	@SuppressWarnings("nls")
	@Test
	void test_sort_numerical_string() {

		List<String> toSort = Arrays.asList("2", "3", null, "10", "1", "d2", "8", "d1", "11", "22", "d", "6");
		List<String> sorted = Arrays.asList("1", "2", "3", "6", "8", "10", "11", "22", "d", "d1", "d2", null);

		toSort.sort(new StringWithNumberAndNullableComparator());

		int cpt = 0;
		while (cpt < toSort.size()) {
			assertEquals("Not the same at index " + cpt + " toSort:" + toSort.get(cpt) + " - sorted:" + sorted.get(cpt),
					sorted.get(cpt), toSort.get(cpt));
			cpt++;
		}
	}

	@SuppressWarnings("nls")
	@Test
	void test_sort_string_number() {

		List<String> toSort = Arrays.asList("C1A1", "A", "C2", "A1", null, "A2A", "A2A1", "C1", "A2B", "A3", "B", "C",
				"A2", "C1A");
		List<String> sorted = Arrays.asList("A", "A1", "A2", "A2A", "A2A1", "A2B", "A3", "B", "C", "C1", "C1A", "C1A1",
				"C2", null);

		toSort.sort(new StringWithNumberAndNullableComparator());

		int cpt = 0;
		while (cpt < toSort.size()) {
			assertEquals("Not the same at index " + cpt + " toSort:" + toSort.get(cpt) + " - sorted:" + sorted.get(cpt),
					sorted.get(cpt), toSort.get(cpt));
			cpt++;
		}
	}

	@Test
	void test_compare_CompareWithNullVersions() {
		assertEquals(0, new StringWithNumberAndNullableComparator().compare(null, null));
	}

	@Test
	void test_compare_CompareWithEmptyVersions() {
		assertEquals(0, new StringWithNumberAndNullableComparator().compare(RscTools.empty(), RscTools.empty()));
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_Compare_nullvs1() {
		assertTrue(new StringWithNumberAndNullableComparator().compare(null, "1") > 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_Compare_nullvsA() {
		assertTrue(new StringWithNumberAndNullableComparator().compare(null, "A") > 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_Compare_1vsnull() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("1", null) < 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_Compare_Avsnull() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("A", null) < 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareAvsA() {
		assertEquals(0, new StringWithNumberAndNullableComparator().compare("A", "A"));
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_Compare2vs2() {
		assertEquals(0, new StringWithNumberAndNullableComparator().compare("2", "2"));
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_Compare154vs154() {
		assertEquals(0, new StringWithNumberAndNullableComparator().compare("154", "154"));
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareAvsB() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("A", "B") < 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareBvsA12() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("B", "A12") > 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareA2vsB1() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("A2", "B1") < 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareA2vsA1() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("A2", "A1") > 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareA1vsA1() {
		assertEquals(0, new StringWithNumberAndNullableComparator().compare("A1", "A1"));
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareA2vsA12() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("A2", "A12") < 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareAvsA12() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("A", "A12") < 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareA12vsA() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("A12", "A") > 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_Compare1vsA() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("1", "A") < 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareA2AvsA12() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("A2A", "A12") < 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareA2AvsA2B() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("A2A", "A2B") < 0);
	}

	@SuppressWarnings("nls")
	@Test
	void test_compare_CompareA12AvsA2B() {
		assertTrue(new StringWithNumberAndNullableComparator().compare("A12A", "A2B") > 0);
	}
}
