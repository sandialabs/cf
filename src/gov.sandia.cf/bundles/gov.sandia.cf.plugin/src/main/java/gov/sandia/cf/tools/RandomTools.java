/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.security.SecureRandom;
import java.util.Map;

/**
 * A tool class for random actions
 * 
 * @author Didier Verstraete
 *
 */
public class RandomTools {

	private static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * Private constructor to not allow instantiation.
	 */
	private RandomTools() {
	}

	/**
	 * @param map the map to get a value from
	 * @return a random value from a map
	 */
	public static Object getRandomValue(Map<?, ?> map) {
		if (map != null) {
			Object[] values = map.values().toArray();
			return values[RANDOM.nextInt(values.length)];
		}
		return null;
	}

	/**
	 * @return a random int value {@literal >} 0
	 */
	public static int getInt() {
		return RANDOM.nextInt();
	}

	/**
	 * @param bound the max value exclusive
	 * @return a random int value between 0 and bound
	 */
	public static int getInt(int bound) {
		return RANDOM.nextInt(bound);
	}
}
