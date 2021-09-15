/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

/**
 * The ID Tools class
 * 
 * @author Didier Verstraete
 *
 */
public class IDTools {

	private static final String CHAR_A = "A"; //$NON-NLS-1$
	private static final String CHAR_B = "B"; //$NON-NLS-1$
	private static final String CHAR_C = "C"; //$NON-NLS-1$
	private static final String CHAR_D = "D"; //$NON-NLS-1$
	private static final String CHAR_E = "E"; //$NON-NLS-1$
	private static final String CHAR_F = "F"; //$NON-NLS-1$
	private static final String CHAR_G = "G"; //$NON-NLS-1$
	private static final String CHAR_H = "H"; //$NON-NLS-1$
	private static final String CHAR_I = "I"; //$NON-NLS-1$
	private static final String CHAR_J = "J"; //$NON-NLS-1$
	private static final String CHAR_K = "K"; //$NON-NLS-1$
	private static final String CHAR_L = "L"; //$NON-NLS-1$
	private static final String CHAR_M = "M"; //$NON-NLS-1$
	private static final String CHAR_N = "N"; //$NON-NLS-1$
	private static final String CHAR_O = "O"; //$NON-NLS-1$
	private static final String CHAR_P = "P"; //$NON-NLS-1$
	private static final String CHAR_Q = "Q"; //$NON-NLS-1$
	private static final String CHAR_R = "R"; //$NON-NLS-1$
	private static final String CHAR_S = "S"; //$NON-NLS-1$
	private static final String CHAR_T = "T"; //$NON-NLS-1$
	private static final String CHAR_U = "U"; //$NON-NLS-1$
	private static final String CHAR_V = "V"; //$NON-NLS-1$
	private static final String CHAR_W = "W"; //$NON-NLS-1$
	private static final String CHAR_X = "X"; //$NON-NLS-1$
	private static final String CHAR_Y = "Y"; //$NON-NLS-1$
	private static final String CHAR_Z = "Z"; //$NON-NLS-1$

	/**
	 * alphabet map
	 */
	public static final List<String> ALPHABET = Collections.unmodifiableList(Arrays.asList(CHAR_A, CHAR_B, CHAR_C,
			CHAR_D, CHAR_E, CHAR_F, CHAR_G, CHAR_H, CHAR_I, CHAR_J, CHAR_K, CHAR_L, CHAR_M, CHAR_N, CHAR_O, CHAR_P,
			CHAR_Q, CHAR_R, CHAR_S, CHAR_T, CHAR_U, CHAR_V, CHAR_W, CHAR_X, CHAR_Y, CHAR_Z));

	/**
	 * Private constructor to not allow instantiation.
	 */
	private IDTools() {
	}

	/**
	 * @param number the number to convert
	 * @return number to alphabetic format
	 * 
	 *         Example : 0 = A; 25 = Z; 26 = BA; 675 = ZZ; 676 = BAA;
	 */
	public static String generateAlphabeticId(int number) {
		if (number <= 0)
			return ALPHABET.get(0);
		else
			return generateAlphabeticIdRecursive(number);
	}

	/**
	 * @param number the number to convert
	 * @return number to alphabetic format. If number is under 0, return empty
	 *         string
	 */
	private static String generateAlphabeticIdRecursive(int number) {
		if (number <= 0) {
			return RscTools.empty();
		} else {
			int modulo = number % ALPHABET.size();
			int result = number / ALPHABET.size();
			return generateAlphabeticIdRecursive(result) + ALPHABET.get(modulo);
		}
	}

	/**
	 * @param id the id to convert
	 * @return the int value of the @param id string value
	 */
	public static int reverseGenerateAlphabeticIdRecursive(String id) {
		int val = 0;
		for (int i = 0; i < id.length(); i++) {
			String c = String.valueOf(id.charAt(i));
			int d = ALPHABET.indexOf(c);
			val = ALPHABET.size() * val + d;
		}
		return val;
	}

	/**
	 * Return the position of the string in the subset for an id alterning string
	 * and integer:
	 * 
	 * for id label = B set (B) position = 2
	 * 
	 * for id label = B5 set (5) position = 5
	 * 
	 * for id label = B5C set (C) position = 3
	 * 
	 * for id label = B5C2 set (2) position = 2
	 * 
	 * @param id the id to find position for
	 * @return the position of the idlabel in the set. If null or blank return -1.
	 */
	public static int getPositionInSet(String id) {

		if (StringUtils.isBlank(id)) {
			return -1;
		}

		String last = String.valueOf(id.charAt(id.length() - 1));
		boolean integer = false;
		if (MathTools.isInteger(last)) {
			integer = true;
		}

		int firstIndex = 0;
		for (int i = id.length() - 1; i >= 0; i--) {
			String charTmp = String.valueOf(id.charAt(i));
			if ((integer && !MathTools.isInteger(charTmp)) || (!integer && MathTools.isInteger(charTmp))) {
				firstIndex = i + 1;
				break;
			}
		}

		String subId = id.substring(firstIndex, id.length());

		if (integer) {
			return Integer.parseInt(subId) - 1;
		} else {
			return reverseGenerateAlphabeticIdRecursive(subId);
		}
	}

	/**
	 * @param obj the java object to get unique id identifier
	 * @return if obj is not null a unique id identifier for the obj instance,
	 *         otherwise null
	 */
	public static String getObjInstanceUniqueId(Object obj) {
		String identityHashCode = null;
		if (obj != null) {
			identityHashCode = Integer.toHexString(System.identityHashCode(obj));
		}
		return identityHashCode;
	}

	/**
	 * @param <M>           the value class
	 * @param listToReorder the list to reorder
	 * @param element       the element to change
	 * @param newIndex      the new index of the element
	 * @return a new list reordered
	 */
	public static <M> List<M> reorderList(List<M> listToReorder, M element, int newIndex) {

		if (listToReorder == null || element == null || !listToReorder.contains(element) || newIndex < 0
				|| newIndex >= listToReorder.size()) {
			return listToReorder;
		}

		// construct data
		final int startPosition = 0;
		Map<Integer, M> groupMapTmp = new TreeMap<>();
		int indexTmp = startPosition;
		for (M value : listToReorder) {
			groupMapTmp.put(indexTmp, value);
			indexTmp++;
		}

		// map used to sort
		Map<Integer, M> groupMap = new TreeMap<>();

		// search for the old index
		int oldIndex = keyForValueInMap(groupMapTmp, element);

		// reorder
		for (Entry<Integer, M> entry : groupMapTmp.entrySet()) {
			Integer key = entry.getKey();

			if (key < oldIndex && key < newIndex) {
				groupMap.put(key, groupMapTmp.get(key));
			} else if (key > oldIndex && key > newIndex) {
				groupMap.put(key, groupMapTmp.get(key));
			} else {
				if (key == newIndex) {
					groupMap.put(key, element);
				} else if (oldIndex < newIndex) {
					groupMap.put(key, groupMapTmp.get(key + 1));
				} else if (oldIndex > newIndex) {
					groupMap.put(key, groupMapTmp.get(key - 1));
				} else {
					groupMap.put(key, groupMapTmp.get(key));
				}
			}
		}

		return new ArrayList<>(groupMap.values());
	}

	/**
	 * @param <M>
	 * @param map     the map to browse
	 * @param element the element to search
	 * @return the key for the first value searched
	 */
	private static <M> int keyForValueInMap(Map<Integer, M> map, M element) {

		if (map == null || element == null || !map.containsValue(element)) {
			return -1;
		}

		int index = 0;
		for (Entry<Integer, M> entry : map.entrySet()) {
			if (element.equals(entry.getValue())) {
				index = entry.getKey();
				break;
			}
		}

		return index;
	}
}
