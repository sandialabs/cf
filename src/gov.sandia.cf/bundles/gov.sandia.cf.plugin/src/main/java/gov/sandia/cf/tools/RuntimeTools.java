/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.preferences.PrefTools;

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

	/**
	 * Private constructor to not allow instantiation.
	 */
	private RuntimeTools() {
	}

	/**
	 * Execute the commands in parameters and log.
	 * 
	 * @param errorLog    the error log string builder
	 * @param infoLog     the info log string builder
	 * @param cmdExec     the commands to execute
	 * @param withEnvFile execute with environment file
	 * @return the return code of the process
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	public static int executeSync(StringBuilder errorLog, StringBuilder infoLog, String cmdExec, boolean withEnvFile)
			throws CredibilityException {

		int returnCode = OK_CODE;

		if (cmdExec != null && !cmdExec.isEmpty()) {

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

			returnCode = executeSync(errorLog, infoLog, builder);
		}

		return returnCode;
	}

	/**
	 * Execute the python script in parameters and log.
	 * 
	 * @param errorLog     the error log string builder
	 * @param infoLog      the info log string builder
	 * @param pythonScript the python script to execute
	 * @param args         the python script arguments
	 * @return the return code of the process
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	public static int executeSyncPythonScript(StringBuilder errorLog, StringBuilder infoLog, String pythonScript,
			String... args) throws CredibilityException {

		int returnCode = OK_CODE;

		if (pythonScript != null) {
			List<String> cmd = new ArrayList<>();
			cmd.add(PrefTools.getPythonExecutablePath());
			cmd.add(MessageFormat.format(CMD_SCRIPTFILE, pythonScript));
			if (args != null && args.length > 0) {
				cmd.addAll(Arrays.asList(args));
			}

			ProcessBuilder builder = new ProcessBuilder(cmd);
			returnCode = executeSync(errorLog, infoLog, builder);
		}

		return returnCode;
	}

	/**
	 * Execute the commands in parameters and log.
	 * 
	 * @param errorLog the error log string builder
	 * @param infoLog  the info log string builder
	 * @param builder  the builder to execute
	 * @return the return code of the process
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	public static int executeSync(StringBuilder errorLog, StringBuilder infoLog, ProcessBuilder builder)
			throws CredibilityException {

		int returnCode = OK_CODE;

		if (builder != null && builder.command() != null) {

			// log before
			if (infoLog != null) {
				infoLog.append(RscTools.CARRIAGE_RETURN)
						.append("################### Start CF Execution ##################") //$NON-NLS-1$
						.append(RscTools.CARRIAGE_RETURN);
				infoLog.append(DateTools.getDateFormattedDateTime()).append(" [CF] Executing ") //$NON-NLS-1$
						.append(String.join(" ", builder.command())).append(RscTools.CARRIAGE_RETURN); //$NON-NLS-1$
			}

			// start process
			builder.directory(new File(CFVariableResolver.resolve(CFVariable.USER_HOME)));

			AtomicInteger returnCodeRef = new AtomicInteger(OK_CODE);
			AtomicReference<StringBuilder> infoLogRef = new AtomicReference<>();
			AtomicReference<StringBuilder> errorLogRef = new AtomicReference<>();

			// put in another thread
			Display.getDefault().syncExec(() -> {
				StringBuilder logThread = new StringBuilder();
				StringBuilder errorLogThread = new StringBuilder();
				Process process;
				try {
					process = builder.start();
					returnCodeRef.set(process.waitFor());
					logInfo(process, logThread);
					logError(process, errorLogThread);
					infoLogRef.set(logThread);
					errorLogRef.set(errorLogThread);

				} catch (IOException | InterruptedException e) {
					logger.error(e.getMessage());
					errorLogThread.append(RscTools.CARRIAGE_RETURN).append(e.getMessage());
					Thread.currentThread().interrupt();
				}
				assert returnCodeRef.get() == 0;
			});

			// log end
			if (infoLog != null) {
				infoLog.append(infoLogRef.get());
				infoLog.append(errorLogRef.get());
			}
			if (errorLog != null) {
				errorLog.append(errorLogRef.get());
			}

			returnCode = returnCodeRef.get();

			if (infoLog != null) {
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
	 * Write the logs to string builders.
	 * 
	 * @param process the process to log
	 * @param infoLog the info log string builder
	 * @throws IOException if an error occured while reading the process log
	 */
	private static void logInfo(Process process, StringBuilder infoLog) throws IOException {

		if (process != null) {
			try (BufferedReader stdInput = new BufferedReader(
					new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

				// Read the output
				String infoOutput = null;
				while ((infoOutput = stdInput.readLine()) != null) {
					// Display debug
					logger.debug(infoOutput);
					infoLog.append(infoOutput).append(RscTools.CARRIAGE_RETURN);
				}
			}
		}
	}

	/**
	 * Write the logs to string builders.
	 * 
	 * @param process  the process to log
	 * @param errorLog the error log string builder
	 * @throws IOException if an error occured while reading the process log
	 */
	private static void logError(Process process, StringBuilder errorLog) throws IOException {

		if (process != null) {
			try (BufferedReader stdError = new BufferedReader(
					new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {

				// Read any error
				String errorOutput = null;
				while ((errorOutput = stdError.readLine()) != null) {
					// Display error
					logger.error(errorOutput);
					errorLog.append(errorOutput).append(RscTools.CARRIAGE_RETURN);
				}
			}
		}
	}
}
