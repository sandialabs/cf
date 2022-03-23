/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Gson reading, writing tool class
 * 
 * @author Didier Verstraete
 *
 */
public class GsonTools {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(GsonTools.class);

	/**
	 * Private constructor to not allow instantiation.
	 */
	private GsonTools() {
	}

	/**
	 * @param <M>           the class
	 * @param value         the value to parse
	 * @param classToReturn the class to fill and return
	 * @return the class searched in the value in Gson
	 */
	public static <M> M getFromGson(String value, Class<M> classToReturn) {
		M data = null;

		if (value != null) {
			try {
				data = new Gson().fromJson(value, classToReturn);
			} catch (JsonSyntaxException ex) {
				logger.warn(ex.getMessage());
			}
		}

		return data;
	}

	/**
	 * Gets the list from gson.
	 *
	 * @param <M>           the generic type
	 * @param value         the value
	 * @param classToReturn the class to return
	 * @return the list from gson
	 */
	public static <M> List<M> getListFromGson(String value, Class<M> classToReturn) {
		List<M> data = new ArrayList<>();

		if (value != null) {
			try {
				Type listType = new TypeToken<ArrayList<M>>() {
				}.getType();
				data = new Gson().fromJson(value, listType);
			} catch (JsonSyntaxException ex) {
				logger.warn(ex.getMessage());
			}
		}

		return data;
	}

	/**
	 * @param <M>           the class
	 * @param classToReturn the class to convert
	 * @return a new gson value for the class in parameter
	 */
	public static <M> String toGson(M classToReturn) {
		// Encode JSON
		return new Gson().toJson(classToReturn);
	}
}
