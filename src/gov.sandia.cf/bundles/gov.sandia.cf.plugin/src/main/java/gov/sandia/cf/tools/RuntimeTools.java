/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.exceptions.CredibilityException;

/**
 * Runtime Tools to execute processes on a local machine.
 * 
 * @author Didier Verstraete
 *
 */
public class RuntimeTools {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(RuntimeTools.class);

	/** SHELL command */
	public static final String CMD_SCRIPT_SHELL = "sh"; //$NON-NLS-1$
	/** SHELL execute command parameter */
	public static final String CMD_SCRIPT_SHELL_ARG = "-c"; //$NON-NLS-1$
	/** SHELL execute script file in current shell command */
	public static final String CMD_SCRIPTFILE = "\"{0}\""; //$NON-NLS-1$
	/** SHELL execute script file in new shell command */
	public static final String CMD_SCRIPTFILE_SHELL = ". \"{0}\""; //$NON-NLS-1$
	/** SHELL append command characters */
	public static final String CMD_APPEND = "&&"; //$NON-NLS-1$
	/** SHELL success return code */
	public static final int OK_CODE = 0;
	/** SHELL error return code */
	public static final int ERROR_CODE = 1;

	/** The Constant SCRIPT_EXTENSION_LINUX. */
	public static final String SCRIPT_EXTENSION_LINUX = ".sh"; //$NON-NLS-1$

	/** The Constant SCRIPT_EXTENSION_WINDOWS. */
	public static final String SCRIPT_EXTENSION_WINDOWS = ".bat"; //$NON-NLS-1$

	/**
	 * Private constructor to not allow instantiation.
	 */
	private RuntimeTools() {
	}

	/**
	 * Execute the commands in the current thread.
	 *
	 * @param errorLog        the error log string builder
	 * @param infoLog         the info log string builder
	 * @param cmdExec         the commands to execute
	 * @param withEnvFile     execute with environment file
	 * @param progressMonitor the progress monitor
	 * @return the return code of the process
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	public static int execute(StringBuilder errorLog, StringBuilder infoLog, String cmdExec, boolean withEnvFile,
			IProgressMonitor progressMonitor) throws CredibilityException {

		if (cmdExec == null || cmdExec.isEmpty()) {
			return OK_CODE;
		}

		return execute(errorLog, infoLog, getBuilder(cmdExec, withEnvFile), progressMonitor);
	}

	/**
	 * Execute the commands in the current thread.
	 *
	 * @param errorLog        the error log string builder
	 * @param infoLog         the info log string builder
	 * @param builder         the builder to execute
	 * @param progressMonitor the progress monitor
	 * @return the return code of the process
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	public static int execute(StringBuilder errorLog, StringBuilder infoLog, ProcessBuilder builder,
			IProgressMonitor progressMonitor) throws CredibilityException {

		if (builder == null || builder.command() == null) {
			return OK_CODE;
		}

		logInfoBefore(infoLog, builder);

		beforeProcess(builder);

		AtomicInteger returnCodeRef = new AtomicInteger(OK_CODE);

		process(errorLog, infoLog, builder, returnCodeRef, progressMonitor);

		int returnCode = returnCodeRef.get();

		logInfoAfter(infoLog, returnCode, builder);

		return returnCode;
	}

	/**
	 * @param filePath the script file path
	 * @return the script execution cmd
	 */
	public static String getScriptFileCmd(String filePath) {
		String cmd = RscTools.empty();
		if (filePath != null && !filePath.isEmpty()) {
			if (SystemTools.isWindows()) {
				cmd = MessageFormat.format(CMD_SCRIPTFILE, filePath);
			} else {
				cmd = MessageFormat.format(CMD_SCRIPTFILE_SHELL, filePath);
			}
		}
		return cmd;
	}

	/**
	 * Gets the script file extension.
	 *
	 * @return the script file extension
	 */
	public static String getScriptFileExtension() {
		return SystemTools.isWindows() ? SCRIPT_EXTENSION_WINDOWS : SCRIPT_EXTENSION_LINUX;
	}

	/**
	 * Before process.
	 *
	 * @param builder the builder
	 * @throws CredibilityException the credibility exception
	 */
	private static void beforeProcess(ProcessBuilder builder) throws CredibilityException {
		if (builder != null) {
			builder.directory(new File(CFVariableResolver.resolve(CFVariable.USER_HOME)));
		}
	}

	/**
	 * Process.
	 *
	 * @param errorLog        the error log
	 * @param infoLog         the info log
	 * @param builder         the builder
	 * @param returnCodeRef   the return code ref
	 * @param progressMonitor the progress monitor
	 */
	private static void process(StringBuilder errorLog, StringBuilder infoLog, ProcessBuilder builder,
			AtomicInteger returnCodeRef, IProgressMonitor progressMonitor) {
		try {
			Process process = builder.start();

			// log info
			StreamGobbler streamGobblerInfo = new StreamGobbler(process.getInputStream(),
					t -> infoLog.append(t).append(RscTools.CARRIAGE_RETURN));
			StreamGobbler streamGobblerInfoProgress = new StreamGobbler(process.getInputStream(),
					progressMonitor::subTask);

			Future<?> futureInfo = Executors.newSingleThreadExecutor().submit(streamGobblerInfo,
					streamGobblerInfoProgress);
			// log error
			StreamGobbler streamGobblerError = new StreamGobbler(process.getErrorStream(),
					t -> errorLog.append(t).append(RscTools.CARRIAGE_RETURN));
			StreamGobbler streamGobblerErrorProgress = new StreamGobbler(process.getErrorStream(),
					progressMonitor::subTask);
			Future<?> futureError = Executors.newSingleThreadExecutor().submit(streamGobblerError,
					streamGobblerErrorProgress);

			// get logs
			futureInfo.get();
			futureError.get();

			returnCodeRef.set(process.waitFor());

			assert returnCodeRef.get() == 0;

		} catch (IOException | InterruptedException | ExecutionException e) {
			logger.error(e.getMessage());
			errorLog.append(RscTools.CARRIAGE_RETURN).append(e.getMessage());
			Thread.currentThread().interrupt();
			assert returnCodeRef.get() == 0;
		}
	}

	/**
	 * Log info before.
	 *
	 * @param infoLog the info log
	 * @param builder the builder
	 */
	private static void logInfoBefore(StringBuilder infoLog, ProcessBuilder builder) {
		if (infoLog != null) {
			infoLog.append(RscTools.CARRIAGE_RETURN).append("################### Start CF Execution ##################") //$NON-NLS-1$
					.append(RscTools.CARRIAGE_RETURN);
			infoLog.append(DateTools.getDateFormattedDateTime()).append(" [CF] Executing ") //$NON-NLS-1$
					.append(String.join(" ", builder.command())).append(RscTools.CARRIAGE_RETURN); //$NON-NLS-1$
		}
	}

	/**
	 * Log info.
	 *
	 * @param infoLog    the info log
	 * @param returnCode the return code
	 * @param builder    the builder
	 */
	private static void logInfoAfter(StringBuilder infoLog, int returnCode, ProcessBuilder builder) {
		if (infoLog != null && builder != null) {
			if (returnCode == OK_CODE) {
				infoLog.append(DateTools.getDateFormattedDateTime()).append(" [CF] Execution of ") //$NON-NLS-1$
						.append(String.join(" ", builder.command())).append(" stopped.") //$NON-NLS-1$ //$NON-NLS-2$
						.append(RscTools.CARRIAGE_RETURN);
			} else {
				infoLog.append(DateTools.getDateFormattedDateTime()).append(" /!\\/!\\/!\\[CF] Execution of ") //$NON-NLS-1$
						.append(String.join(" ", builder.command())).append(" in error!") //$NON-NLS-1$ //$NON-NLS-2$
						.append(RscTools.CARRIAGE_RETURN);
			}
			infoLog.append(RscTools.CARRIAGE_RETURN).append("################### End CF Execution - Exit code: ") //$NON-NLS-1$
					.append(returnCode).append(" #########").append(RscTools.CARRIAGE_RETURN); //$NON-NLS-1$
		}
	}

	/**
	 * Gets the builder.
	 *
	 * @param cmdExec     the cmd exec
	 * @param withEnvFile the with env file
	 * @return the builder
	 */
	private static ProcessBuilder getBuilder(String cmdExec, boolean withEnvFile) {

		if (cmdExec == null || cmdExec.isEmpty()) {
			return null;
		}

		// start process
		ProcessBuilder builder = new ProcessBuilder();
		if (SystemTools.isWindows()) {
			if (withEnvFile) {
				builder.command(cmdExec);
			} else {
				builder.command(cmdExec.split(" ")); //$NON-NLS-1$
			}
		} else {
			builder.command(CMD_SCRIPT_SHELL, CMD_SCRIPT_SHELL_ARG, cmdExec);
		}

		return builder;
	}
}
