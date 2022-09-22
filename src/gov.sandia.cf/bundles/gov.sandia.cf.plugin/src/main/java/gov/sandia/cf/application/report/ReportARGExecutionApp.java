/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.report;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.constants.arg.ARGQueryTypesConstants;
import gov.sandia.cf.constants.arg.ARGQueryVersionConstants;
import gov.sandia.cf.dao.IARGParametersRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.dto.arg.ARGType;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.RuntimeTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * Manage Report Execution Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class ReportARGExecutionApp extends AApplication implements IReportARGExecutionApp {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReportARGExecutionApp.class);

	/**
	 * Default python value
	 */
	private static final String CMD_PYTHON_DEFAULT = "python"; //$NON-NLS-1$

	/**
	 * The constructor
	 */
	public ReportARGExecutionApp() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ReportARGExecutionApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ARGParameters getARGParameters() {
		return getDaoManager().getRepository(IARGParametersRepository.class).getFirst();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ARGParameters addDefaultARGParameters(final IPath cfProjectPath) throws CredibilityException {

		// check if parameters does not already exists
		ARGParameters argParameters = getARGParameters();

		if (argParameters == null) {
			argParameters = ARGParametersFactory.getDefaultParameters(cfProjectPath);
			argParameters = getDaoManager().getRepository(IARGParametersRepository.class).create(argParameters);
		} else {
			logger.warn("ARG parameters already exists in database."); //$NON-NLS-1$
		}

		return argParameters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ARGParameters updateARGParameters(ARGParameters argParameters) throws CredibilityException {
		ARGParameters argParametersDefault = getARGParameters();
		ARGParameters toReturn = null;

		if (argParametersDefault != null && argParameters != null) {

			// set id to merge
			argParameters.setId(argParametersDefault.getId());

			// update
			toReturn = getDaoManager().getRepository(IARGParametersRepository.class).update(argParameters);
		}

		return toReturn;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public ARGType getARGTypes(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog,
			IProgressMonitor monitor) throws URISyntaxException, IOException, CredibilityException {

		ARGType argTypes = new ARGType();

		if (argParameters == null) {
			logger.error("Impossible to get the arg types: the arg parameters are null."); //$NON-NLS-1$
			return argTypes;
		}

		// create log builder to get the current result from log
		StringBuilder logBuilder = new StringBuilder();

		monitor.beginTask(RscTools.getString(RscConst.MSG_REPORTARGEXEC_GETARGTYPES_JOB_INIT), 0);

		// Get paths
		File scriptArg = new File(WorkspaceTools.getStaticFilePath(ARGQueryTypesConstants.SCRIPT_PY_QUERY_ARG_TYPES));
		String argExecutablePath = argParameters.getArgExecPath();

		// arg get version command
		String command = MessageFormat.format("{0} \"{1}\" {2} \"{3}\"", CMD_PYTHON_DEFAULT, //$NON-NLS-1$
				scriptArg.getAbsolutePath(), ARGQueryTypesConstants.ARG_TYPES_SCRIPT_ARGFILE_PARAM, argExecutablePath);

		// Generate commands
		String argSetEnvScriptPath = argParameters.getArgPreScript();
		if (argSetEnvScriptPath != null && !argSetEnvScriptPath.isEmpty()) {
			monitor.subTask(RscTools.getString(RscConst.MSG_REPORTARGEXEC_GETARGTYPES_JOB_EXECWITHPRESCRIPT));
			executeCommandWithPreScript(argParameters, command, errorLog, logBuilder, new NullProgressMonitor());
		} else {
			monitor.subTask(RscTools.getString(RscConst.MSG_REPORTARGEXEC_GETARGTYPES_JOB_EXEC));
			executeCommandWithoutPreScript(command, errorLog, logBuilder, new NullProgressMonitor());
		}

		// get the arg types from python console log
		monitor.subTask(RscTools.getString(RscConst.MSG_REPORTARGEXEC_GETARGTYPES_JOB_RETRIEVERESULT));
		String[] split = logBuilder.toString().split("\n"); //$NON-NLS-1$
		Optional<String> argTypesString = Stream.of(split)
				.filter(s -> s.startsWith(ARGQueryTypesConstants.ARG_TYPES_DICO_KEYWORD)).findFirst();
		if (argTypesString.isPresent()) {
			String ymlTypes = argTypesString.get();
			ymlTypes = ymlTypes.substring(ARGQueryTypesConstants.ARG_TYPES_DICO_KEYWORD.length());
			Yaml yamlParser = new Yaml();
			Object typesAsObject = yamlParser.load(ymlTypes);
			if (typesAsObject instanceof Map) {
				Map<?, ?> mapTypes = (Map<?, ?>) typesAsObject;
				if (mapTypes.get(ARGQueryTypesConstants.ARG_BACKENDTYPE_KEYWORD) instanceof List) {
					argTypes.setBackendTypes(
							(List<String>) mapTypes.get(ARGQueryTypesConstants.ARG_BACKENDTYPE_KEYWORD));
				}
				if (mapTypes.get(ARGQueryTypesConstants.ARG_REPORTTYPE_KEYWORD) instanceof List) {
					argTypes.setReportTypes((List<String>) mapTypes.get(ARGQueryTypesConstants.ARG_REPORTTYPE_KEYWORD));
				}
			}
		}

		// append to main log
		infoLog.append(logBuilder);

		return argTypes;
	}

	/** {@inheritDoc} */
	@Override
	public String getARGVersion(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog,
			IProgressMonitor monitor) throws URISyntaxException, IOException, CredibilityException {

		if (argParameters == null) {
			logger.error("Impossible to get the arg version: the arg parameters are null."); //$NON-NLS-1$
			return RscTools.empty();
		}

		// create log builder to get the current result from log
		StringBuilder logBuilder = new StringBuilder();

		monitor.beginTask(RscTools.getString(RscConst.MSG_REPORTARGEXEC_GETARGVERSION_JOB_INIT), 0);

		// Get paths
		File scriptArg = new File(
				WorkspaceTools.getStaticFilePath(ARGQueryVersionConstants.SCRIPT_PY_QUERY_ARG_VERSION));
		String argExecutablePath = argParameters.getArgExecPath();

		// arg get version command
		String command = MessageFormat.format("{0} \"{1}\" {2} \"{3}\"", CMD_PYTHON_DEFAULT, //$NON-NLS-1$
				scriptArg.getAbsolutePath(), ARGQueryVersionConstants.ARGFILE_PARAM, argExecutablePath);

		// Generate commands
		String argSetEnvScriptPath = argParameters.getArgPreScript();
		if (argSetEnvScriptPath != null && !argSetEnvScriptPath.isEmpty()) {
			monitor.subTask(RscTools.getString(RscConst.MSG_REPORTARGEXEC_GETARGVERSION_JOB_EXECWITHPRESCRIPT));
			executeCommandWithPreScript(argParameters, command, errorLog, logBuilder, monitor);
		} else {
			monitor.subTask(RscTools.getString(RscConst.MSG_REPORTARGEXEC_GETARGVERSION_JOB_EXEC));
			executeCommandWithoutPreScript(command, errorLog, logBuilder, monitor);
		}

		// get the arg version from python console log
		monitor.subTask(RscTools.getString(RscConst.MSG_REPORTARGEXEC_GETARGVERSION_JOB_RETRIEVERESULT));
		String[] split = logBuilder.toString().split("\n"); //$NON-NLS-1$

		Optional<String> argVersionLog = Stream.of(split)
				.filter(s -> s.startsWith(ARGQueryVersionConstants.VERSION_KEYWORD)).findFirst();

		// append log
		infoLog.append(logBuilder);

		return argVersionLog.isPresent()
				? argVersionLog.get().substring(ARGQueryVersionConstants.VERSION_KEYWORD.length())
				: RscTools.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generateReportARG(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog,
			IProgressMonitor progressMonitor) throws CredibilityException {

		if (argParameters == null) {
			logger.error("Report generation error: the arg parameters are null."); //$NON-NLS-1$
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFREPORT_ARG_PARAM_NULL));
		}

		// get argParameters path
		if (argParameters.getParametersFilePath() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFREPORT_YAML_PARAMETERS_FILE_NOTEXISTS,
					argParameters.getParametersFilePath()));
		}
		File parametersFile = new File(argParameters.getParametersFilePath());
		if (!parametersFile.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFREPORT_YAML_PARAMETERS_FILE_NOTEXISTS,
					argParameters.getParametersFilePath()));
		}

		// arg generate report command
		String command = MessageFormat.format("{0} \"{1}\" -A -p \"{2}\"", CMD_PYTHON_DEFAULT, //$NON-NLS-1$
				argParameters.getArgExecPath(), parametersFile.getAbsolutePath());

		// Generate commands
		String argSetEnvScriptPath = argParameters.getArgPreScript();
		if (argSetEnvScriptPath != null && !argSetEnvScriptPath.isEmpty()) {
			executeCommandWithPreScript(argParameters, command, errorLog, infoLog, progressMonitor);
		} else {
			executeCommandWithoutPreScript(command, errorLog, infoLog, progressMonitor);
		}
	}

	/**
	 * Execute command without pre script.
	 *
	 * @param command         the command
	 * @param errorLog        the error log
	 * @param infoLog         the info log
	 * @param progressMonitor the progress monitor
	 * @throws CredibilityException the credibility exception
	 */
	private void executeCommandWithoutPreScript(String command, StringBuilder errorLog, StringBuilder infoLog,
			IProgressMonitor progressMonitor) throws CredibilityException {

		if (StringUtils.isBlank(command)) {
			logger.error("Command execution error: the command is blank."); //$NON-NLS-1$
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFREPORT_COMMAND_NULL));
		}

		// Run process
		int returnCode = RuntimeTools.execute(errorLog, infoLog, command, false, progressMonitor);

		if (returnCode != RuntimeTools.OK_CODE) {
			throw new CredibilityException(errorLog.toString());
		}
	}

	/**
	 * Execute command with pre script.
	 *
	 * @param argParameters   the arg parameters
	 * @param command         the command
	 * @param errorLog        the error log
	 * @param infoLog         the info log
	 * @param progressMonitor the progress monitor
	 * @throws CredibilityException the credibility exception
	 */
	private void executeCommandWithPreScript(ARGParameters argParameters, final String command, StringBuilder errorLog,
			StringBuilder infoLog, IProgressMonitor progressMonitor) throws CredibilityException {

		if (argParameters == null) {
			logger.error("Report generation error: the arg parameters are null."); //$NON-NLS-1$
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFREPORT_ARG_PARAM_NULL));
		}

		// get paths
		String scriptSuffix = "-script"; //$NON-NLS-1$
		File preScriptFile = new File(argParameters.getArgPreScript());
		File scriptFile = new File(argParameters.getOutput(),
				argParameters.getFilename() + scriptSuffix + RuntimeTools.getScriptFileExtension());

		// check pre script file
		// exists?
		if (!preScriptFile.exists()) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_CONFREPORT_PRESCRIPT_FILE_NOTEXISTS, preScriptFile));
		}
		// is readable?
		if (!Files.isReadable(preScriptFile.toPath())) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_CONFREPORT_PRESCRIPT_FILE_NOTREADABLE, preScriptFile));
		}

		try {
			// create script file and copy preScript content
			try {
				Files.copy(preScriptFile.toPath(), scriptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new CredibilityException(
						RscTools.getString(RscConst.EX_CONFREPORT_TMP_SCRIPT_WRITE_ERROR, scriptFile.getPath()));
			}

			// append arg command to script file
			try {
				FileTools.writeStringInFile(scriptFile, command, true);
			} catch (IOException e) {
				throw new CredibilityException(
						RscTools.getString(RscConst.EX_CONFREPORT_TMP_SCRIPT_WRITE_ERROR, scriptFile.getPath()));
			}

			// create tmp error log
			StringBuilder errorLogTmp = new StringBuilder();

			// execute script file
			int returnCode = RuntimeTools.execute(errorLogTmp, infoLog,
					RuntimeTools.getScriptFileCmd(scriptFile.getPath()), false, progressMonitor);

			errorLog.append(errorLogTmp);

			if (returnCode != RuntimeTools.OK_CODE) {
				throw new CredibilityException(errorLogTmp.toString());
			}
		} finally {
			// delete script file
			try {
				Files.delete(scriptFile.toPath());
			} catch (IOException e) {
				logger.error("Impossible to delete previous script file", e); //$NON-NLS-1$
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
