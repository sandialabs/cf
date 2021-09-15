/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.el.ELProcessor;

/**
 * Expression language evaluation tool class
 * 
 * @author Didier Verstraete
 *
 */
public class ELTools {

	/**
	 * Private constructor to not allow instantiation.
	 */
	private ELTools() {
	}

	/**
	 * @param <A>        the class to return
	 * @param classToGet the class to return
	 * @param eval       the expression to evaluate
	 * @param attributes the attributes
	 * @return the result of the EL evaluation. If the return class does not match
	 *         the result class, return null.
	 */
	public static <A> A eval(Class<A> classToGet, final String eval, final Map<String, ?> attributes) {

		if (eval == null) {
			return null;
		}

		String evalTemp = eval;
		ELProcessor elp = new ELProcessor();

		if (attributes != null && !attributes.isEmpty()) {

			// change whitespaces by underscores
			evalTemp = mapEvalVar(eval, attributes.keySet());

			attributes.forEach((key, value) -> elp.defineBean(key != null ? key.replace(" ", "_") : null, value)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		Object toReturn = elp.eval(evalTemp);
		return classToGet.isInstance(toReturn) ? classToGet.cast(toReturn) : null;
	}

	/**
	 * @param eval       the eval to change
	 * @param attributes the attributes to manage
	 * @return the eval with attributes containing whitespaces replaced by
	 *         underscores.
	 */
	private static String mapEvalVar(final String eval, final Set<String> attributes) {

		if (eval == null) {
			return null;
		}

		if (attributes == null) {
			return eval;
		}

		String evalTemp = eval;

		for (String var : attributes) {
			evalTemp = evalTemp.replace(var, var.replace(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return evalTemp;
	}

	/**
	 * @param eval       the eval string
	 * @param attributes the attributes to find
	 * @return the list of attributes found in the eval
	 */
	public static Set<String> getVariableSet(String eval, List<String> attributes) {
		if (eval == null || attributes == null) {
			return new HashSet<>();
		}

		return attributes.stream().filter(Objects::nonNull).filter(eval::contains).collect(Collectors.toSet());
	}

}
