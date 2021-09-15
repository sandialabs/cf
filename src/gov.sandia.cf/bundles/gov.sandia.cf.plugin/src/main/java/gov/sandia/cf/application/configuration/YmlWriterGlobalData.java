/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * This class write credibility Global data. The actual implementation is stored
 * in a yaml file.
 * 
 * @author Didier Verstraete
 *
 */
public class YmlWriterGlobalData {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(YmlWriterGlobalData.class);

	/**
	 * Write the global data into the file in parameter depending of the options.
	 * 
	 * @param cfDataFile the file to write
	 * @param options    the export options map
	 * @param append     append to the file if it already exists?
	 * @throws CredibilityException if an application error occurs
	 * @throws IOException          if a file exception occurs
	 */
	public void writeGlobalData(final File cfDataFile, final Map<ExportOptions, Object> options, final boolean append)
			throws CredibilityException, IOException {
		if (cfDataFile == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
		}

		// if append is not desired, delete the existing file
		if (cfDataFile.exists() && !append) {
			boolean deleted = Files.deleteIfExists(cfDataFile.toPath());
			if (!deleted || cfDataFile.exists()) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_DELETION_ERROR));
			}
		}

		// create file if it does not exist
		if (!cfDataFile.exists()) {
			boolean created = cfDataFile.createNewFile();
			if (!created || !cfDataFile.exists()) {
				throw new CredibilityException(RscTools.getString(RscConst.EX_CONFLOADER_YAMLCONF_NOTEXISTS));
			}
		}

		if (options == null) {
			logger.warn("Export options are null. Impossible to write into file {}", cfDataFile.getAbsolutePath()); //$NON-NLS-1$
			return;
		}

		logger.debug("Write credibility data to the yml file {}", cfDataFile.getAbsolutePath()); //$NON-NLS-1$

		// load global specs
		Map<String, Object> mapRoot = new LinkedHashMap<>();
		Map<String, Object> mapGlobal = new LinkedHashMap<>();
		Map<String, Object> mapData = new LinkedHashMap<>();

		mapRoot.put(YmlGlobalData.CONF_GLOBAL, mapGlobal);
		mapGlobal.put(YmlGlobalData.CONF_DATA, mapData);

		// Model
		mapData.put(YmlGlobalData.CONF_GLB_MODEL, toMapModel(options));

		// Users
		mapData.put(YmlGlobalData.CONF_GLB_USER, toMapUserList(options));

		// YML reader
		Yaml yaml = new Yaml();

		// Write the specifications to the credibility file
		try (Writer writer = new StringWriter()) {

			// Dump map objects to yml string
			yaml.dump(mapRoot, writer);

			FileTools.writeStringInFile(cfDataFile, writer.toString(), append);
		}
	}

	/**
	 * @param model the model to export
	 * @return a map containing the model data
	 */
	private Map<String, Object> toMapModel(final Map<ExportOptions, Object> options) {

		if (options != null && options.containsKey(ExportOptions.MODEL)
				&& options.get(ExportOptions.MODEL) instanceof Model) {

			return toMapModel((Model) options.get(ExportOptions.MODEL));
		}

		return new LinkedHashMap<>();
	}

	/**
	 * @param model the model to export
	 * @return a map containing the model data
	 */
	private Map<String, Object> toMapModel(final Model model) {

		Map<String, Object> modelMap = new LinkedHashMap<>();

		if (model == null) {
			return modelMap;
		}

		modelMap.put(YmlGlobalData.CONF_GLB_MODEL_ID, model.getId());
		modelMap.put(YmlGlobalData.CONF_GLB_MODEL_APPLICATION, model.getApplication());
		modelMap.put(YmlGlobalData.CONF_GLB_MODEL_CONTACT, model.getContact());
		modelMap.put(YmlGlobalData.CONF_GLB_MODEL_VERSION, model.getVersion());
		modelMap.put(YmlGlobalData.CONF_GLB_MODEL_VERSIONORIGIN, model.getVersionOrigin());
		modelMap.put(YmlGlobalData.CONF_GLB_MODEL_QOIPLANNINGSCHEMAPATH,
				model.getConfFile(CFFeature.QOI_PLANNER) != null ? model.getConfFile(CFFeature.QOI_PLANNER).getPath()
						: RscTools.empty());
		modelMap.put(YmlGlobalData.CONF_GLB_MODEL_PIRTSCHEMAPATH,
				model.getConfFile(CFFeature.PIRT) != null ? model.getConfFile(CFFeature.PIRT).getPath()
						: RscTools.empty());
		modelMap.put(YmlGlobalData.CONF_GLB_MODEL_PCMMSCHEMAPATH,
				model.getConfFile(CFFeature.PCMM) != null ? model.getConfFile(CFFeature.PCMM).getPath()
						: RscTools.empty());
		modelMap.put(YmlGlobalData.CONF_GLB_MODEL_UNCERTAINTYSCHEMAPATH,
				model.getConfFile(CFFeature.UNCERTAINTY) != null ? model.getConfFile(CFFeature.UNCERTAINTY).getPath()
						: RscTools.empty());
		modelMap.put(YmlGlobalData.CONF_GLB_MODEL_SYSREQUIREMENTSCHEMAPATH,
				model.getConfFile(CFFeature.SYSTEM_REQUIREMENTS) != null
						? model.getConfFile(CFFeature.SYSTEM_REQUIREMENTS).getPath()
						: RscTools.empty());

		return modelMap;
	}

	/**
	 * @param userList the user list to export
	 * @return a map of Users
	 */
	private Map<String, Object> toMapUserList(final Map<ExportOptions, Object> options) {

		if (options != null && options.containsKey(ExportOptions.USER_LIST)
				&& options.get(ExportOptions.USER_LIST) instanceof List) {

			return toMapUserList(((List<?>) options.get(ExportOptions.USER_LIST)).stream()
					.filter(User.class::isInstance).map(User.class::cast).collect(Collectors.toList()));
		}

		return new LinkedHashMap<>();
	}

	/**
	 * @param userList the user list to export
	 * @return a map of Users
	 */
	private Map<String, Object> toMapUserList(final List<User> userList) {

		Map<String, Object> userMap = new LinkedHashMap<>();

		if (userList == null) {
			return userMap;
		}

		for (User user : userList) {
			if (user != null && user.getId() != null) {
				userMap.put(MessageFormat.format(YmlGlobalData.MAP_KEY_ID, user.getId(), user.getUserID()),
						toMapUser(user));
			}
		}

		return userMap;
	}

	/**
	 * @param user the user to export
	 * @return a map of user data
	 */
	private Map<String, Object> toMapUser(final User user) {

		Map<String, Object> userMap = new LinkedHashMap<>();

		if (user == null) {
			return userMap;
		}

		userMap.put(YmlGlobalData.CONF_GLB_USER_ID, user.getId());
		userMap.put(YmlGlobalData.CONF_GLB_USER_USERID, user.getUserID());
		userMap.put(YmlGlobalData.CONF_GLB_USER_ROLEPCMM,
				user.getRolePCMM() != null ? user.getRolePCMM().getId() : null);

		return userMap;
	}
}
