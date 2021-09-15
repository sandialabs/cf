/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public enum CFVariable {

	/**
	 * CF variables for paths
	 */
	CF_FILEDIR("cf.filedir", null), //$NON-NLS-1$
	CF_FILENAME("cf.filename", null), //$NON-NLS-1$
	CF_HOMEDIR("cf.homedir", null), //$NON-NLS-1$
	CF_WORKDIR("cf.workdir", null), //$NON-NLS-1$
	PROJECT("eclipse.project", null), //$NON-NLS-1$
	WORKSPACE("eclipse.workspace", null), //$NON-NLS-1$

	/**
	 * CF variables for system
	 */
	HOSTNAME("hostname", null), //$NON-NLS-1$
	JAVA_VERSION("java.version", "java.version"), //$NON-NLS-1$ //$NON-NLS-2$
	OS_NAME("os.name", "os.name"), //$NON-NLS-1$ //$NON-NLS-2$
	USER_NAME("user.name", "user.name"), //$NON-NLS-1$ //$NON-NLS-2$
	USER_HOME("user.home", "user.home"); //$NON-NLS-1$ //$NON-NLS-2$

	private String variable;
	private String command;
	public static final String VAR_SCHEMA = "${%s}"; //$NON-NLS-1$

	/**
	 * @param variable the variable name
	 * @param command  the associated command
	 */
	private CFVariable(final String variable, final String command) {
		this.variable = variable;
		this.command = command;
	}

	/**
	 * @return the variable
	 */
	public String get() {
		return String.format(VAR_SCHEMA, variable);
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @return the list of CF variable available
	 */
	public static List<CFVariable> getAll() {
		return Arrays.asList(CFVariable.values());
	}

	/**
	 * @return the list of CF variable available as string
	 */
	public static List<String> getAllAsString() {
		return Stream.of(CFVariable.values()).map(CFVariable::get).collect(Collectors.toList());
	}
}
