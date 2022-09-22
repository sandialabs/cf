/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * The ID tools unit test class
 * 
 * @author Didier Verstraete
 *
 */
class IDToolsTest {

	@Test
	void test_generateAlphabeticID() {
		assertEquals("A", IDTools.generateAlphabeticId(0)); //$NON-NLS-1$
		assertEquals("BA", IDTools.generateAlphabeticId(26)); //$NON-NLS-1$
		assertEquals("CD", IDTools.generateAlphabeticId(55)); //$NON-NLS-1$
		assertEquals("ZZ", IDTools.generateAlphabeticId(675)); //$NON-NLS-1$
		assertEquals("BAA", IDTools.generateAlphabeticId(676)); //$NON-NLS-1$
		assertEquals("BAY", IDTools.generateAlphabeticId(700)); //$NON-NLS-1$
		assertEquals("BAZ", IDTools.generateAlphabeticId(701)); //$NON-NLS-1$
		assertEquals("BZZ", IDTools.generateAlphabeticId(1351)); //$NON-NLS-1$
		assertEquals("CAA", IDTools.generateAlphabeticId(1352)); //$NON-NLS-1$
		assertEquals("BZZZ", IDTools.generateAlphabeticId(35151)); //$NON-NLS-1$
		assertEquals("CAAA", IDTools.generateAlphabeticId(35152)); //$NON-NLS-1$
	}

	@Test
	void test_reverseGenerateAlphabeticID() {
		assertEquals(0, IDTools.reverseGenerateAlphabeticIdRecursive("A")); //$NON-NLS-1$
		assertEquals(26, IDTools.reverseGenerateAlphabeticIdRecursive("BA")); //$NON-NLS-1$
		assertEquals(55, IDTools.reverseGenerateAlphabeticIdRecursive("CD")); //$NON-NLS-1$
		assertEquals(675, IDTools.reverseGenerateAlphabeticIdRecursive("ZZ")); //$NON-NLS-1$
		assertEquals(676, IDTools.reverseGenerateAlphabeticIdRecursive("BAA")); //$NON-NLS-1$
		assertEquals(700, IDTools.reverseGenerateAlphabeticIdRecursive("BAY")); //$NON-NLS-1$
		assertEquals(701, IDTools.reverseGenerateAlphabeticIdRecursive("BAZ")); //$NON-NLS-1$
		assertEquals(1351, IDTools.reverseGenerateAlphabeticIdRecursive("BZZ")); //$NON-NLS-1$
		assertEquals(1352, IDTools.reverseGenerateAlphabeticIdRecursive("CAA")); //$NON-NLS-1$
		assertEquals(35151, IDTools.reverseGenerateAlphabeticIdRecursive("BZZZ")); //$NON-NLS-1$
		assertEquals(35152, IDTools.reverseGenerateAlphabeticIdRecursive("CAAA")); //$NON-NLS-1$
	}

	@Test
	void test_getObjInstanceUniqueId() {
		String test = "test"; //$NON-NLS-1$

		assertEquals(Integer.toHexString(System.identityHashCode(test)), IDTools.getObjInstanceUniqueId(test));
		assertNull(IDTools.getObjInstanceUniqueId(null));
	}

	@Test
	void test_reorderList_OldIndexBeforeNewIndex() {
		List<String> listToReorder = Arrays.asList("One", "Two", "Three", "Four", "Five", "Six"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		List<String> reorderedList = IDTools.reorderList(listToReorder, "Three", 4); //$NON-NLS-1$
		assertEquals("One", reorderedList.get(0)); //$NON-NLS-1$
		assertEquals("Two", reorderedList.get(1)); //$NON-NLS-1$
		assertEquals("Four", reorderedList.get(2)); //$NON-NLS-1$
		assertEquals("Five", reorderedList.get(3)); //$NON-NLS-1$
		assertEquals("Three", reorderedList.get(4)); //$NON-NLS-1$
		assertEquals("Six", reorderedList.get(5)); //$NON-NLS-1$
	}

	@Test
	void test_reorderList_OldIndexBeforeNewIndex_LastIndex() {
		List<String> listToReorder = Arrays.asList("One", "Two", "Three", "Four", "Five", "Six"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		List<String> reorderedList = IDTools.reorderList(listToReorder, "Three", 5); //$NON-NLS-1$
		assertEquals("One", reorderedList.get(0)); //$NON-NLS-1$
		assertEquals("Two", reorderedList.get(1)); //$NON-NLS-1$
		assertEquals("Four", reorderedList.get(2)); //$NON-NLS-1$
		assertEquals("Five", reorderedList.get(3)); //$NON-NLS-1$
		assertEquals("Six", reorderedList.get(4)); //$NON-NLS-1$
		assertEquals("Three", reorderedList.get(5)); //$NON-NLS-1$
	}

	@Test
	void test_reorderList_OldIndexAfterNewIndex() {
		List<String> listToReorder = Arrays.asList("One", "Two", "Three", "Four", "Five", "Six"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		List<String> reorderedList = IDTools.reorderList(listToReorder, "Four", 1); //$NON-NLS-1$
		assertEquals("One", reorderedList.get(0)); //$NON-NLS-1$
		assertEquals("Four", reorderedList.get(1)); //$NON-NLS-1$
		assertEquals("Two", reorderedList.get(2)); //$NON-NLS-1$
		assertEquals("Three", reorderedList.get(3)); //$NON-NLS-1$
		assertEquals("Five", reorderedList.get(4)); //$NON-NLS-1$
		assertEquals("Six", reorderedList.get(5)); //$NON-NLS-1$
	}

	@Test
	void test_reorderList_OldIndexAfterNewIndex_FirstIndex() {
		List<String> listToReorder = Arrays.asList("One", "Two", "Three", "Four", "Five", "Six"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		List<String> reorderedList = IDTools.reorderList(listToReorder, "Four", 0); //$NON-NLS-1$
		assertEquals("Four", reorderedList.get(0)); //$NON-NLS-1$
		assertEquals("One", reorderedList.get(1)); //$NON-NLS-1$
		assertEquals("Two", reorderedList.get(2)); //$NON-NLS-1$
		assertEquals("Three", reorderedList.get(3)); //$NON-NLS-1$
		assertEquals("Five", reorderedList.get(4)); //$NON-NLS-1$
		assertEquals("Six", reorderedList.get(5)); //$NON-NLS-1$
	}

	@Test
	void test_reorderList_OldIndexEqualsNewIndex() {
		List<String> listToReorder = Arrays.asList("One", "Two", "Three", "Four", "Five", "Six"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		List<String> reorderedList = IDTools.reorderList(listToReorder, "Three", 2); //$NON-NLS-1$
		assertEquals("One", reorderedList.get(0)); //$NON-NLS-1$
		assertEquals("Two", reorderedList.get(1)); //$NON-NLS-1$
		assertEquals("Three", reorderedList.get(2)); //$NON-NLS-1$
		assertEquals("Four", reorderedList.get(3)); //$NON-NLS-1$
		assertEquals("Five", reorderedList.get(4)); //$NON-NLS-1$
		assertEquals("Six", reorderedList.get(5)); //$NON-NLS-1$
	}

	@Test
	void test_reorderList_NewIndexInf0() {
		List<String> listToReorder = Arrays.asList("One", "Two", "Three", "Four", "Five", "Six"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		List<String> reorderedList = IDTools.reorderList(listToReorder, "Three", -1); //$NON-NLS-1$
		assertEquals(listToReorder, reorderedList);
	}

	@Test
	void test_reorderList_NewIndexSup() {
		List<String> listToReorder = Arrays.asList("One", "Two", "Three", "Four", "Five", "Six"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		List<String> reorderedList = IDTools.reorderList(listToReorder, "Three", listToReorder.size()); //$NON-NLS-1$
		assertEquals(listToReorder, reorderedList);
	}

	@Test
	void test_reorderList_ElementNull() {
		List<String> listToReorder = Arrays.asList("One", "Two", "Three", "Four", "Five", "Six"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		List<String> reorderedList = IDTools.reorderList(listToReorder, null, listToReorder.size());
		assertEquals(listToReorder, reorderedList);
	}

	@Test
	void test_reorderList_ElementNotInList() {
		List<String> listToReorder = Arrays.asList("One", "Two", "Three", "Four", "Five", "Six"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		List<String> reorderedList = IDTools.reorderList(listToReorder, "Seven", 2); //$NON-NLS-1$
		assertEquals(listToReorder, reorderedList);
	}

	@Test
	void test_reorderList_ListNull() {
		List<String> reorderedList = IDTools.reorderList(null, "Three", 4); //$NON-NLS-1$
		assertEquals(null, reorderedList);
	}

	@Test
	void test_getPositionInSet_Null() {
		int position = IDTools.getPositionInSet(null); // $NON-NLS-1$
		assertEquals(-1, position);
	}

	@Test
	void test_getPositionInSet_Blank() {
		int position = IDTools.getPositionInSet(""); //$NON-NLS-1$
		assertEquals(-1, position);
	}

	@Test
	void test_getPositionInSet_1Level_Position0() {
		int position = IDTools.getPositionInSet("A"); //$NON-NLS-1$
		assertEquals(0, position);
	}

	@Test
	void test_getPositionInSet_1Level_Position4() {
		int position = IDTools.getPositionInSet("E"); //$NON-NLS-1$
		assertEquals(4, position);
	}

	@Test
	void test_getPositionInSet_2Levels_Position1() {
		int position = IDTools.getPositionInSet("A1"); //$NON-NLS-1$
		assertEquals(0, position);
	}

	@Test
	void test_getPositionInSet_2Levels_Position21() {
		int position = IDTools.getPositionInSet("E21"); //$NON-NLS-1$
		assertEquals(20, position);
	}

	@Test
	void test_getPositionInSet_5Levels_Position3() {
		int position = IDTools.getPositionInSet("B2F4D"); //$NON-NLS-1$
		assertEquals(3, position);
	}

	@Test
	void test_getPositionInSet_5Levels_Position0() {
		int position = IDTools.getPositionInSet("B2F4A"); //$NON-NLS-1$
		assertEquals(0, position);
	}

	@Test
	void test_getPositionInSet_4Levels_Position45() {
		int position = IDTools.getPositionInSet("B2F45"); //$NON-NLS-1$
		assertEquals(44, position);
	}

	@Test
	void test_getPositionInSet_4Levels_Position4558() {
		int position = IDTools.getPositionInSet("B25F4558"); //$NON-NLS-1$
		assertEquals(4557, position);
	}
}
