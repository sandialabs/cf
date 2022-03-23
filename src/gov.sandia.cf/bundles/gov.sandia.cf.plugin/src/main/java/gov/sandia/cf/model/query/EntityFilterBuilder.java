/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.query;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder to create filters for the entities with parameters
 * 
 * @author Didier Verstraete
 *
 */
public class EntityFilterBuilder {

	private EntityFilterBuilder() {
		// DO NOT IMPLEMENT
	}

	/**
	 * @return a new filter map
	 */
	public static Map<EntityFilter, Object> get() {
		return new HashMap<>();
	}

	/**
	 * @param filter the entity filter
	 * @param value  the value associaetd
	 * @return a new filter map with the parameters included
	 */
	public static Map<EntityFilter, Object> get(EntityFilter filter, Object value) {
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(filter, value);
		return filters;
	}
}
