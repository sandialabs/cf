/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IReportARGExecutionApp;
import gov.sandia.cf.application.configuration.arg.ARGParametersFactory;
import gov.sandia.cf.application.configuration.arg.ARGQueryTypesConstants;
import gov.sandia.cf.application.configuration.arg.ARGQueryVersionConstants;
import gov.sandia.cf.application.configuration.arg.ARGType;
import gov.sandia.cf.dao.IARGParametersRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
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

	private static final String ARG_CMD = "{0} \"{1}\" -A -p \"{2}\""; //$NON-NLS-1$

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
	public ARGType getARGTypes(ARGParameters argParameters, StringBuilder mainErrorLog, StringBuilder mainInfoLog)
			throws URISyntaxException, IOException, CredibilityException {

		ARGType argTypes = new ARGType();
		boolean withEnvFile = false;

		// Generate commands
		List<String> cmds = new ArrayList<>();
		String argSetEnvScriptPath = argParameters.getArgPreScript();
		if (argSetEnvScriptPath != null && !argSetEnvScriptPath.isEmpty()) {
			cmds.add(RuntimeTools.getScriptFileCmd(argSetEnvScriptPath));
			cmds.add(RuntimeTools.CMD_APPEND);
			withEnvFile = true;
		}

		// Get paths
		File scriptArg = new File(WorkspaceTools.getStaticFilePath(ARGQueryTypesConstants.SCRIPT_PY_QUERY_ARG_TYPES));
		String argExecutablePath = argParameters.getArgExecPath();

		// get python exec path
		String pythonExecPath = !StringUtils.isBlank(argParameters.getPythonExecPath())
				? argParameters.getPythonExecPath()
				: CMD_PYTHON_DEFAULT;

		// Generate command
		cmds.add(pythonExecPath);
		cmds.add(MessageFormat.format(RuntimeTools.CMD_SCRIPTFILE, scriptArg.getAbsolutePath()));
		cmds.add(ARGQueryTypesConstants.ARG_TYPES_SCRIPT_ARGFILE_PARAM);
		cmds.add(MessageFormat.format(RuntimeTools.CMD_SCRIPTFILE, argExecutablePath));

		// start script
		StringBuilder queryLog = new StringBuilder();
		RuntimeTools.executeSync(mainErrorLog, queryLog, String.join(" ", cmds), withEnvFile); //$NON-NLS-1$

		// get the arg types from python console log
		String[] split = queryLog.toString().split("\n"); //$NON-NLS-1$
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
		mainInfoLog.append(queryLog);

		return argTypes;
	}

	/** {@inheritDoc} */
	@Override
	public String getARGVersion(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog)
			throws URISyntaxException, IOException, CredibilityException {

		if (argParameters == null) {
			return RscTools.empty();
		}

		boolean withEnvFile = false;

		// Generate commands
		List<String> cmds = new ArrayList<>();
		String argSetEnvScriptPath = argParameters.getArgPreScript();
		if (argSetEnvScriptPath != null && !argSetEnvScriptPath.isEmpty()) {
			cmds.add(RuntimeTools.getScriptFileCmd(argSetEnvScriptPath));
			cmds.add(RuntimeTools.CMD_APPEND);
			withEnvFile = true;
		}

		// Get paths
		File scriptArg = new File(
				WorkspaceTools.getStaticFilePath(ARGQueryVersionConstants.SCRIPT_PY_QUERY_ARG_VERSION));
		String argExecutablePath = argParameters.getArgExecPath();

		// get python exec path
		String pythonExecPath = !StringUtils.isBlank(argParameters.getPythonExecPath())
				? argParameters.getPythonExecPath()
				: CMD_PYTHON_DEFAULT;

		// Generate command
		cmds.add(pythonExecPath);
		cmds.add(MessageFormat.format(RuntimeTools.CMD_SCRIPTFILE, scriptArg.getAbsolutePath()));
		cmds.add(ARGQueryVersionConstants.ARGFILE_PARAM);
		cmds.add(MessageFormat.format(RuntimeTools.CMD_SCRIPTFILE, argExecutablePath));

		// start script
		StringBuilder logBuilder = new StringBuilder();
		RuntimeTools.executeSync(errorLog, logBuilder, String.join(" ", cmds), withEnvFile); //$NON-NLS-1$

		// get the arg version from python console log
		String[] split = logBuilder.toString().split("\n"); //$NON-NLS-1$
		Optional<String> argVersionString = Stream.of(split)
				.filter(s -> s.startsWith(ARGQueryVersionConstants.VERSION_KEYWORD)).findFirst();

		// append log
		infoLog.append(logBuilder);

		return argVersionString.isPresent()
				? argVersionString.get().substring(ARGQueryVersionConstants.VERSION_KEYWORD.length())
				: RscTools.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generateReportARG(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog)
			throws CredibilityException {

		if (argParameters == null) {
			logger.error("Report generation error: the arg parameters are null."); //$NON-NLS-1$
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFREPORT_ARG_PARAM_NULL));
		}

		List<String> cmds = new ArrayList<>();
		boolean withEnvFile = false;

		// Generate commands
		String argSetEnvScriptPath = argParameters.getArgPreScript();
		if (argSetEnvScriptPath != null && !argSetEnvScriptPath.isEmpty()) {
			cmds.add(RuntimeTools.getScriptFileCmd(argSetEnvScriptPath));
			cmds.add(RuntimeTools.CMD_APPEND);
			withEnvFile = true;
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

		// get python exec path
		String pythonExecPath = !StringUtils.isBlank(argParameters.getPythonExecPath())
				? argParameters.getPythonExecPath()
				: CMD_PYTHON_DEFAULT;

		cmds.add(MessageFormat.format(ARG_CMD, pythonExecPath, argParameters.getArgExecPath(),
				parametersFile.getAbsolutePath()));

		// Run process
		int returnCode = RuntimeTools.executeSync(errorLog, infoLog, String.join(" ", cmds), withEnvFile); //$NON-NLS-1$

		if (returnCode != RuntimeTools.OK_CODE) {
			throw new CredibilityException(errorLog.toString());
		}

	}

}