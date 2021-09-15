/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.exceptions.CredibilityException;

/**
 * Resolves the CF Variable locally
 * 
 * @author Didier Verstraete
 *
 */
public class CFVariableResolver {

	/**
	 * Search for a string between '${' and '}' characters
	 */
	public static final String CFVARIABLE_PARSE_REGEX = "\\$\\{([^\\}]*)\\}"; //$NON-NLS-1$

	/**
	 * Do not instantiate
	 */
	private CFVariableResolver() {
	}

	/**
	 * @param toResolve the string to resolve
	 * @return the string with variable resolved if not null
	 * @throws CredibilityException if the string toResolve contains an unrecognized
	 *                              variable
	 */
	public static String resolveAll(final String toResolve) throws CredibilityException {
		if (toResolve == null) {
			return null;
		}

		// check variable
		Pattern pattern = Pattern.compile(CFVARIABLE_PARSE_REGEX);
		Matcher matcher = pattern.matcher(toResolve);
		while (matcher.find()) {
			if (!CFVariable.getAllAsString().contains(matcher.group(0))) {
				throw new CredibilityException(
						RscTools.getString(RscConst.EX_CFVARRESOLVER_VAR_NOTRECOGNIZED, matcher.group(0)));
			}
		}

		String toReturn = String.valueOf(toResolve);

		// resolve variables
		for (CFVariable var : CFVariable.values()) {
			if (toReturn.contains(var.get())) {
				toReturn = toReturn.replace(var.get(), resolve(var));
			}
		}

		return toReturn;
	}

	/**
	 * @param toResolve the string to remove the variables from
	 * @return the string without variable
	 */
	public static String removeAll(final String toResolve) {
		if (toResolve == null) {
			return null;
		}

		String toReturn = String.valueOf(toResolve);

		// resolve variables
		for (CFVariable var : CFVariable.values()) {
			if (toReturn.contains(var.get())) {
				toReturn = toReturn.replace(var.get(), RscTools.empty());
			}
		}

		return toReturn;
	}

	/**
	 * Resolve the variable in parameter.
	 * 
	 * //!\\ Be careful, this method has to be called from a running Eclipse Window
	 * to get some variable depending of the currently opened editor (e.g.
	 * CF_FILENAME, CF_HOMEDIR...)
	 * 
	 * @param variable the variable to resolve
	 * @return the variable resolved or an empty string
	 * @throws CredibilityException if the variable is not recognized
	 */
	public static String resolve(final CFVariable variable) throws CredibilityException {

		if (variable == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CFVARRESOLVER_VAR_NULL));
		}

		String toReturn = null;

		// Path variables
		if (variable.equals(CFVariable.CF_FILEDIR)) {
			toReturn = WorkspaceTools.getActiveHomeDirPath();
		} else if (variable.equals(CFVariable.CF_FILENAME)) {
			toReturn = WorkspaceTools.getActiveFilenameWithoutExtension();
		} else if (variable.equals(CFVariable.CF_HOMEDIR)) {
			toReturn = WorkspaceTools.getActiveHomeDirPath();
		} else if (variable.equals(CFVariable.CF_WORKDIR)) {
			toReturn = WorkspaceTools.getActiveWorkingDirPath();
		} else if (variable.equals(CFVariable.PROJECT)) {
			toReturn = WorkspaceTools.getActiveProjectPathToString();
		} else if (variable.equals(CFVariable.WORKSPACE)) {
			toReturn = WorkspaceTools.getWorkspacePathToString();
		}
		// system variables
		else if (variable.equals(CFVariable.HOSTNAME)) {
			toReturn = SystemTools.getHostName();
		} else if (variable.equals(CFVariable.JAVA_VERSION)) {
			toReturn = SystemTools.get(CFVariable.JAVA_VERSION);
		} else if (variable.equals(CFVariable.OS_NAME)) {
			toReturn = SystemTools.get(CFVariable.OS_NAME);
		} else if (variable.equals(CFVariable.USER_NAME)) {
			toReturn = SystemTools.get(CFVariable.USER_NAME);
		} else if (variable.equals(CFVariable.USER_HOME)) {
			toReturn = SystemTools.get(CFVariable.USER_HOME);
		}
		// variable not recognized -> throw exception
		else {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_CFVARRESOLVER_VAR_NOTRECOGNIZED, variable.get()));
		}

		return toReturn;
	}

}
